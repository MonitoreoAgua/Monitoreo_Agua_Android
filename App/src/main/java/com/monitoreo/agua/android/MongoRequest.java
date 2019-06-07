package com.monitoreo.agua.android;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

/**
 * Created by kenca on 16/02/2017.
 */

public class MongoRequest extends StringRequest {

    //private static final String REGISTER_REQUEST_URL = "http://192.168.100.12:8081/proyectoJavier/android/registro.php";
    private Map<String, String> params;

    public MongoRequest(Map<String, String> parametros, String REQUEST_URL, Response.Listener<String> listener){
        super(Request.Method.POST, REQUEST_URL, listener, null);
        params = parametros;

    }

    public MongoRequest(Map<String, String> parametros, String REQUEST_URL, Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(Request.Method.POST, REQUEST_URL, listener, errorListener);
        params = parametros;

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
