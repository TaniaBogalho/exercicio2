package com.exemplo;

import com.exemplo.rest.JSONService;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.Path;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.List;
import java.util.logging.*;

@Path("")
public class WatchService {


    private static Logger LOGGER = Logger.getLogger(WatchService.class.getName());

    private static CSVReader cvs = new CSVReader();

    private JSONService jsonService = new JSONService();

    private File invalid_file = null;

    private StringBuilder builder;

    private String ColumnNamesList = "";

    private java.nio.file.Path path = Paths.get("/home/tania/input/");

    private FileWriter fileWriter;

    private FileHandler fileHandler;

    //private Thread thread;


    public static void main(String [] args) {

        new WatchService().readCSVFile();

        clean(LOGGER);
    }

    /**
     * Prepare the InvalidFile.
     */
    private void prepareInvalidFile()
    {
        invalid_file = new File("/home/tania/invalid/test_file_invalidos.csv");

        builder = new StringBuilder();
        ColumnNamesList = "filename, op, value1, value2";
    }


    /**
     * Prepare the Log File. Create FileHandler, and use it in Logger.
     */
    private void prepareLogFile()
    {
        try {

            fileHandler = new FileHandler("/home/tania/logger.log", true);

            LOGGER.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Write into invalid file the processed filename and the JSONObject with operation, value1 and value2.
     *
     *  @param  fileName  file name processed and with invalid lines
     *  @param  jsonObjectToUse the JSONObject to use to write in invalid file
     */
    private void writeInInvalidFile(String fileName, JSONObject jsonObjectToUse)
    {
        try {
        //Write the incorrect line of file read in invalid_lines file
        if (invalid_file.length() <= 1) {
            builder.append(ColumnNamesList);
            builder.append("\n");
            builder.append(fileName);
            builder.append(",");
            builder.append(jsonObjectToUse.get("op"));
            builder.append(",");
            builder.append(jsonObjectToUse.get("value1"));
            builder.append(",");
            builder.append(jsonObjectToUse.get("value2"));

            builder.append('\n');
            fileWriter.write(builder.toString());

            fileWriter.close();


            //Remove the first line in CSV file
            removeFirstLine(invalid_file.toString());

        } else {
            builder = new StringBuilder();
            builder.append(fileName);
            builder.append(",");
            builder.append(jsonObjectToUse.get("op"));
            builder.append(",");
            builder.append(jsonObjectToUse.get("value1"));
            builder.append(",");
            builder.append(jsonObjectToUse.get("value2"));

            builder.append('\n');
            fileWriter.write(builder.toString());
            fileWriter.close();
        }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Move the file from input folder to output folder.
     *
     * @param fileName  file name that we intend to use.
     */
    private void moveFile(String fileName)
    {
        try {
            File source = new File("/home/tania/input/" + fileName);
            File dest = new File("/home/tania/output/" + fileName);

            source.renameTo(dest);


        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.log( Level.SEVERE, e.toString(), e );
        }
    }

    /**
     * Function to start WatchService in specific folder and processed the created files.
     */
    public void readCSVFile() {

        prepareInvalidFile();

        prepareLogFile();


        //Write in .log file
        LOGGER.info("START PROCESS\n");

        //thread = new Thread(() -> {


            JSONObject objInput;
            //JSONObject objOutput;

            try {

                //Create a WatchService in folder that we intend to monitor

                java.nio.file.WatchService watcher = path.getFileSystem().newWatchService();

                //Associate watch service at the directory to listen to the event types
                WatchKey watchKey = path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);


                while (true)  //should have a sleep
                {

                    fileWriter = new FileWriter(invalid_file, true);


                    try {
                        // listen to events
                        watchKey = watcher.take();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }



                    // get list of events as they occur
                    List<WatchEvent<?>> events = watchKey.pollEvents();


                    //iterate over events
                    for (WatchEvent event : events) {
                        //check if the event refers to a new file created
                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                            //Fill the JSONObject with the function readCsvFile return (read in file)
                            objInput = cvs.readCsvFile("/home/tania/input/" + event.context().toString());


                            //Verify the operation read in file
                            if (!objInput.get("op").equals("sum") & !objInput.get("op").equals("avg") & !objInput.get("op").equals("mul") & !objInput.get("op").equals("div")) {
                                writeInInvalidFile(event.context().toString(), objInput);
                            } else {
                                //Send the read JSONObject objInput into JSONService of Ex. Part 1.
                                jsonService.receiveJSON(objInput);

                                //System.out.println(objOutput.toString());


                            }

                            //Move the processed file to folder output
                            moveFile(event.context().toString());

                        }
                    }

                    boolean validKey = watchKey.reset();

                    if (!validKey) {
                        //Write in .log file
                        LOGGER.log(Level.SEVERE, "Invalid watch key, close the watch service.\n");
                    }

                    //Write in .log file
                    LOGGER.info(" END PROCESS\n\n\n");

                    Thread.sleep(500);

                    clean(LOGGER);
                }

            }
            catch (IOException | JSONException e)
            {
                e.printStackTrace();
                LOGGER.log(Level.SEVERE, e.toString(), e);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        //});

        //thread.start();
    }

    /**
     * Remove the first line (empty line) of fileName.
     *
     * @param fileName  file name that i want to use.
     */
    //Function to remove first line of csv file
    private static void removeFirstLine(String fileName)  {

        RandomAccessFile RandomAccessFile;

        try {
            RandomAccessFile = new RandomAccessFile(fileName, "rw");

        //Initial write position
        long writePosition = RandomAccessFile.getFilePointer();
        RandomAccessFile.readLine();
        // Shift the next lines upwards.
        long readPosition = RandomAccessFile.getFilePointer();

        byte[] buff = new byte[1024];
        int n;

        while (-1 != (n = RandomAccessFile.read(buff))) {
            RandomAccessFile.seek(writePosition);
            RandomAccessFile.write(buff, 0, n);
            readPosition += n;
            writePosition += n;
            RandomAccessFile.seek(readPosition);
        }

        RandomAccessFile.setLength(writePosition);
        RandomAccessFile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Cleans the handlers of logger. Cleans .log.lck, .log0...x files.
     *
     * @param logger  the used logger
     */
    private static void clean(Logger logger) {
        if (logger != null) {
            for (Handler handler : logger.getHandlers()) {
                handler.close();
            }
            clean(logger.getParent());
        }
    }


}
