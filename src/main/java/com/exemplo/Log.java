package com.exemplo;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {

    private String logFile = "/home/tania/logger.log";

    private static Log INSTANCE = null;

    private static Logger LOGGER = Logger.getLogger(WatchService.class.getName());

    private Log() {
        try {
            //FileWriter fw = new FileWriter(logFile);
            //PrintWriter writer = new PrintWriter(fw, true);

            FileHandler fileHandler = new FileHandler(logFile, true);

            LOGGER.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static synchronized Log getInstance(){
        if(INSTANCE == null)
            INSTANCE = new Log();
        return INSTANCE;
    }

    public void logError(String error) {

        /*PrintWriter out = null;

        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(logFile)));

        } catch (IOException e) {
            e.printStackTrace();
        }

        out.write("ERROR: " + error );*/

        LOGGER.log(Level.SEVERE, error, error);
    }


    public void logInfo(String info) {

        /*PrintWriter out = null;

        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(logFile)));

        } catch (IOException e) {
            e.printStackTrace();
        }

        out.write("INFO: " + info );*/

        LOGGER.info(info);
    }


}
