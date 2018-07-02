package com.exemplo;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CSVReader {

    //Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ",";


    public JSONObject readCsvFile(String fileName) {

        BufferedReader fileReader = null;

        JSONObject json_obj_file = new JSONObject();

        String line = "";


        try
        {
            //Create the file reader
            fileReader = new BufferedReader(new FileReader(fileName));

            //Read the CSV file header to skip it
            fileReader.readLine();

            //Read the file line by line starting from the second line
            while ((line = fileReader.readLine()) != null) {

                //Get all tokens available in line
                String[] tokens = line.split(COMMA_DELIMITER);

                if (tokens.length > 0) {

                    /*Dados dados = new Dados();
                    dados.setOp(tokens[0]);
                    dados.setValue1(Double.parseDouble(tokens[1]));
                    dados.setValue2(Double.parseDouble(tokens[2]));
                    */

                    json_obj_file.put("op", tokens[0]);
                    json_obj_file.put("value1", tokens[1]);
                    json_obj_file.put("value2", tokens[2]);

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File doesn't found!!");

        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Error reading file data!!");

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally
        {
            try {
                fileReader.close();
            } catch (IOException e) {
                System.out.println("Error while closing the fileReader!!!");

                e.printStackTrace();
            }
        }


        return json_obj_file;
    }
}


