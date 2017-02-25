package com.duran.johan.menu;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kenca on 16/02/2017.
 */

public class RegisterRequest extends StringRequest {
    private static final String REGISTER_REQUEST_URLJohan = "http://192.168.0.100:8081/Proyectos/Monitoreo_Agua_Web/android/registro.php";
    private static final String REGISTER_REQUEST_URL = "http://192.168.100.12:8081/proyectoJavier/android/registro.php";
    private Map<String, String> params;

    public RegisterRequest(String nombre, String email, String password, Response.Listener<String> listener){
        super(Request.Method.POST, REGISTER_REQUEST_URLJohan, listener, null);
        params = new HashMap<>();
        params.put("nombre", nombre);
        params.put("correo", email);
        params.put("contraseña", password);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
