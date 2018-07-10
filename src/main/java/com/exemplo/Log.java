package com.exemplo;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {

    private static Log INSTANCE = null;

    private static Logger LOGGER = Logger.getLogger(Log.class.getName());

    private Log() {
        try {
            String logFile = "/home/tania/logger.log";
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

    void logError(String error) {
        LOGGER.log(Level.SEVERE, error, error);
    }

    public void logInfo(String info) {
        LOGGER.info(info);
    }
}
