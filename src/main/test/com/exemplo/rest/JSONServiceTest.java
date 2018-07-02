package com.exemplo.rest;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;


class JSONServiceTest {

    private JSONService jsonService = new JSONService();

    private Date data = new Date();

    private Calendar calendar;


    @Test
    void testReceiveJSON ()
    {

        calendar = Calendar.getInstance();
        calendar.setTime(data);

        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH)+1;


        JSONObject obj_input = null;

        try {
            obj_input = new JSONObject()
                    .put("op", "sum")
                    .put("value1", 10)
                    .put("value2", 5);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JSONObject obj_output = jsonService.receiveJSON(obj_input);

        String atual = null;
        String expected = null;

        try {

            if (obj_input != null)
            {
                atual = "{\"op\":\"" + obj_input.getString("op") + "\",\"value1\":" + obj_input.getDouble("value1") +  ",\"value2\":" + obj_input.getDouble("value2") + ",\"Total\":" + obj_output.getString("Total") + ",\"Data\":\"" + day+ "\\/" + (month<10?("0"+month):(month)) + "\\/" + year + "\"}";
            }

            expected = "{\"op\":\"" + obj_output.getString("op") + "\",\"value1\":" + obj_output.getDouble("value1") +  ",\"value2\":" + obj_output.getDouble("value2") + ",\"Total\":" + obj_output.getString("Total") + ",\"Data\":\"" + day+ "\\/" + (month<10?("0"+month):(month)) + "\\/" + year + "\"}";

        } catch (JSONException e) {
            e.printStackTrace();
        }


        assertEquals(expected, atual);
    }
}