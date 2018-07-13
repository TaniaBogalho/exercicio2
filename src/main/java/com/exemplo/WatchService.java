package com.exemplo;

import com.exemplo.rest.JSONService;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class WatchService {

    private static CSVReader cvs = new CSVReader();
    private JSONService jsonService = new JSONService();
    private File invalid_file = null;
    private StringBuilder builder;
    private String ColumnNamesList = "";
    private FileWriter fileWriter;

    private WatchKey watchKey;
    private java.nio.file.WatchService watcher;

    public static void main(String [] args) {

        new WatchService().readCSVFile();

    }

    public WatchService() {

        try {
            //Create a WatchService in folder that we intend to monitor
            java.nio.file.Path path = Paths.get("/home/tania/input/");
            watcher = path.getFileSystem().newWatchService();

            //Associate watch service at the directory to listen to the event types
            watchKey = path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
        } catch (IOException e) {
            // TODO: deal with this
        }
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
     * Insert data into exemploDB data base.
     *
     * @param op  operation used.
     * @param value1  value1 used.
     * @param value2  value2 used.
     * @param total  total of operation used.
     */
    private void insertIntoDB(String op, double value1, double value2, double total)
    {
        Connection con;
        Statement stmt;
        String sql = "INSERT INTO info (op, value1, value2, total) VALUES ('" + op + "', " + value1 + ", " + value2 + ", " + total + ");";
        String url = "jdbc:postgresql://localhost/exercicio2db";

        try
        {
            con = DriverManager.getConnection(url, "postgres", "123");

            con.setAutoCommit(false);

            stmt = con.createStatement();

            stmt.executeUpdate(sql);

            stmt.close();
            con.commit();
            con.close();
        }
        catch (SQLException e) {
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
            //LOGGER.log( Level.SEVERE, e.toString(), e );

            Log.getInstance().logError(e.toString());
        }
    }


    /**
     * Function to start WatchService in specific folder and processed the created files.
     */
    public void readCSVFile() {

        prepareInvalidFile();

        //Write in .log file
        Log.getInstance().logInfo("START WATCH SERVICE PROCESS\n");

        //thread = new Thread(() -> {


            JSONObject objInput;
            JSONObject objOutput;

            try {

                while (true)
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
                            }
                            else
                            {
                                //Send the read JSONObject objInput into JSONService of Ex. Part 1.
                                objOutput = jsonService.receiveJSON(objInput);

                                //Insert values into DB
                                insertIntoDB(objOutput.getString("op"), objOutput.getDouble("value1"), objOutput.getDouble("value2"), objOutput.getDouble("Total"));

                                //System.out.println(objOutput.toString());
                            }

                            //Move the processed file to folder output
                            moveFile(event.context().toString());
                        }
                    }

                    boolean validKey = watchKey.reset();

                    if (!validKey) {

                        //Write in .log file
                        Log.getInstance().logError("Invalid watch key, close the watch service.\n");
                    }

                    //Write in .log file
                    Log.getInstance().logInfo(" END WATCH SERVICE PROCESS\n\n\n");

                    Thread.sleep(500);
                }
            }
            catch (IOException | JSONException e)
            {
                e.printStackTrace();
                Log.getInstance().logError(e.toString());
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


}
