package com.exemplo.rest;

import com.exemplo.Dados;
import com.exemplo.Log;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


@Path("/json")
public class JSONService {

	//private static Logger LOGGER = Logger.getLogger(JSONService.class.getName());

	/*
	/**
	 * Prepare the Log File. Create FileHandler, and use it in Logger.
	 */
	/*private void prepareLogFile()
	{
		try {

			FileHandler fileHandler = new FileHandler("/home/tania/logger.log", true);
			LOGGER.addHandler(fileHandler);
			SimpleFormatter formatter = new SimpleFormatter();
			fileHandler.setFormatter(formatter);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}*/


	/**
	 * Returns a JSONObject with operation, value1, value2, total of operation and date time of operation
	 *
	 * @param  		inputJsonObj  an JSONObject to treat
	 * @return      JSONObject with operation, value1, value2, total of operation and date time of operation
	 */
	@POST
	@Path("/receiveJSON")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject receiveJSON(JSONObject inputJsonObj) {

		JSONObject jsonObjectOutput = new JSONObject();

		//prepareLogFile();


		//LOGGER.info("START PROCESS\n");

		Log.getInstance().logInfo("START JSON SERVICE PROCESS\n");

		Dados dados = new Dados();


		try {
			dados.setOp(inputJsonObj.getString("op"));
			dados.setValue1(inputJsonObj.getDouble("value1"));
			dados.setValue2(inputJsonObj.getDouble("value2"));

			jsonObjectOutput = dados.calcula();

		} catch (JSONException e) {
			e.printStackTrace();
		}


		//LOGGER.info("READING THE DATA \nOP: " + dados.getOp() + "  Value1: " + dados.getValue1() + "  Value2: " + dasdos.getValue2() + "\n");

		Log.getInstance().logInfo("READING THE DATA \nOP: " + dados.getOp() + "  Value1: " + dados.getValue1() + "  Value2: " +
				dados.getValue2() + "\n");


		//LOGGER.info(" END PROCESS\n\n\n");

		Log.getInstance().logInfo(" END JSON SERVICE PROCESS\n\n\n");


		//clean(LOGGER);


		return jsonObjectOutput;

	}




	/**
	 * Cleans the handlers of logger.
	 *
	 * @param logger  the used logger
	 */
	private void clean(Logger logger) {
		if (logger != null) {
			for (Handler handler : logger.getHandlers()) {
				handler.close();
			}
			clean(logger.getParent());
		}
	}

}




