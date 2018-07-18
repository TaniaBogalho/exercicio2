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


@Path("/json")
public class JSONService {

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

		Log.getInstance().logInfo("READING THE DATA \nOP: " + dados.getOp() + "  Value1: " + dados.getValue1() + "  Value2: " +
				dados.getValue2() + "\n");

		Log.getInstance().logInfo(" END JSON SERVICE PROCESS\n");

		return jsonObjectOutput;
	}

}




