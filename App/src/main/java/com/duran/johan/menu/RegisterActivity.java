package com.duran.johan.menu;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText etNombre = (EditText)findViewById(R.id.etNombre);
        final EditText etEmail = (EditText)findViewById(R.id.etEmail);
        final EditText etPassword = (EditText)findViewById(R.id.etPassword);

        final Button bRegistrar = (Button)findViewById(R.id.bRegistrar);

        bRegistrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String nombre = etNombre.getText().toString();
                final String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("tagconvertstr", "["+response+"]");
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                RegisterActivity.this.startActivity(intent);
                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage(jsonResponse.getString("mensaje")).setNegativeButton("Retry", null).create().show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                RegisterRequest registerRequest = new RegisterRequest(nombre, email, password, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
            }
        });


    }

    /*public void getRequest() {
        String dir = "http://192.168.100.12:8081/proyectoJavier/android/registro.php";
        JsonArrayRequest jsArrRequest = new JsonArrayRequest
                (Request.Method.GET, dir, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        switch (num){//Incluir los casos dependiendo de la cantidad de llamados distintos que se puedan hacer.
                            case 1:
                                //lo que se desea hacer para la petición 1
                                int longitud =0;
                                JSONObject resultados=new JSONObject();
                                try {
                                    longitud = response.getJSONObject(0).getJSONObject("results").length();
                                    resultados=response.getJSONObject(0).getJSONObject("results");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                for (int i = 0;i<longitud;i++){
                                    try {
                                        JSONObject location=resultados.getJSONObject(Integer.toString(i)).getJSONObject("value").getJSONObject("location");
                                        LatLng position= new LatLng(location.getDouble("lat"),location.getDouble("lng"));
                                        mMap.addMarker(new MarkerOptions().position(position).title("Centro de Costa Rica"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                break;
                            case 2:
                                //lo que se desea hacer para la petición 2

                                break;
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        //lo que se desea hacer en caso de error

                    }
                });

        int socketTimeout = 3000;//
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsArrRequest.setRetryPolicy(policy);
        // Access the RequestQueue through your singleton class.
        singleton.getInstance(this).addToRequestQueue(jsArrRequest);
    }*/

}
