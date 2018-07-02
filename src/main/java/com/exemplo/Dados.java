package com.exemplo;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.Produces;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


@Produces("application/json")
public class Dados {

    private String op;
    private double value1;
    private double value2;
    private double total;

    private static final DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    Date data = new Date();



    /**
     * Gets the operation.
     */
    public String getOp() {
        return op;
    }


    /**
     * Sets the operation.
     *
     * @param op  the text of the tool tip
     */
    public void setOp(String op) {
        this.op = op;
    }


    /**
     * Gets the value1.
     */
    public double getValue1() {
        return value1;
    }


    /**
     * Sets the value1 to use.
     *
     * @param value1  the text of the tool tip
     */
    public void setValue1(double value1) {
        this.value1 = value1;
    }


    /**
     * Gets the value2.
     */
    public double getValue2() {
        return value2;
    }


    /**
     * Sets the value2 to use.
     *
     * @param value2  the text of the tool tip
     */
    public void setValue2(double value2) {
        this.value2 = value2;
    }




    /**
     * Returns a JSONObject JSONObject with operation, value1, value2, total of operation and date time of operation
     *
     * @return      JSONObject with operation, value1, value2, total of operation and date time of operation
     * @see         JSONObject
     */


    public JSONObject calcula() throws JSONException {

        JSONObject jsonOperacao = new JSONObject();

        switch (this.op) {
            case "sum":
                this.total = this.value1 + this.value2;
                break;

            case "avg":
                this.total = (this.value1 + this.value2) / 2;
                break;

            case "mul":
                this.total = value1 * value2;
                break;

            case "div":
                if (this.value2 != 0)
                    this.total = this.value1 / this.value2;
                else
                    this.total = 0;
                break;

            default:
                this.total = 0;
                this.op = "Operação Inválida";

        }

        jsonOperacao.put("op", this.op);
        jsonOperacao.put("value1", this.value1);
        jsonOperacao.put("value2", this.value2);
        jsonOperacao.put("Total", this.total);
        jsonOperacao.put("Data", sdf.format(data));

        return jsonOperacao;
    }

}
