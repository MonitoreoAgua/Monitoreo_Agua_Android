package com.duran.johan.menu;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.id;
import static android.os.Build.VERSION_CODES.M;
import static com.duran.johan.menu.R.id.textView;
import static com.duran.johan.menu.R.id.visible;

public class ActivityMarker extends AppCompatActivity {
    MySingleton singleton;
    //String server = "http://10.1.130.48:8081/";
    //String dir = "Proyectos/Monitoreo_Agua_Web/php/";
    final int [] oblogatoriosInt={R.id.txtO2,R.id.txtDbo,R.id.txtNh4};
    final int [] opcionalesInt = {R.id.txtCf,R.id.txtDqo,R.id.txtEc,R.id.txtPo4,R.id.txtGya,R.id.txtPh,R.id.txtSd,R.id.txtSsed,R.id.txtSst,R.id.txtSaam,R.id.txtT,R.id.txtAforo,R.id.txtPtsPso,R.id.txtSam};
    final String [] obligatoriosTxt={"% O2","DBO","NH4"};
    final String [] opcionalesTxt={"CF","DQO","EC","PO4","GYA","Ph","SD", "Ssed", "SST","SAAM","T","Aforo","ST","pts PSO"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);
        Intent intent = getIntent();
        String objId = intent.getStringExtra("objId");
        populateView(objId);
    }

    private void populateView(String objId) {
        getRequest("datosMarker_busqueda.php?id1=",1,objId);
    }


    //Método utilizado para realizar peticiones asincronas al servidor
    public void getRequest(String file, final int num,String objId) {
        String dir = getString(R.string.server)+file+objId;
        Log.d("url",dir);
        JsonArrayRequest jsArrRequest = new JsonArrayRequest
                (Request.Method.GET, dir, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        switch (num) {//Incluir los casos dependiendo de la cantidad de llamados distintos que se puedan hacer.
                            case 1://petición 1 cargar todos los marcadores
                                Log.d("response",response.toString());
                                try {
                                    JSONObject obligatorios = response.getJSONObject(Integer.parseInt("0")).getJSONObject("Muestra").getJSONObject("obligatorios");
                                    JSONObject opcionales = response.getJSONObject(Integer.parseInt("0")).getJSONObject("Muestra").getJSONObject("opcionales");
                                    for (int i = 0;i<oblogatoriosInt.length;i++){
                                        if(!obligatorios.isNull(obligatoriosTxt[i])){
                                            String texto = obligatorios.getString(obligatoriosTxt[i]);
                                            TextView field = (TextView)findViewById(oblogatoriosInt[i]);
                                            field.setText(texto);
                                        }else{
                                            TextView field = (TextView)findViewById(oblogatoriosInt[i]);
                                            field.setText("Campo no ingresado");
                                        }
                                    }


                                    for (int i = 0;i<opcionalesInt.length;i++){
                                        if(!opcionales.isNull(opcionalesTxt[i])){
                                            String texto = opcionales.getString(opcionalesTxt[i]);
                                            TextView field = (TextView)findViewById(opcionalesInt[i]);
                                            field.setText(texto);
                                        }else{
                                            TextView field = (TextView)findViewById(opcionalesInt[i]);
                                            field.setText("Campo no ingresado");
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
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
                        Log.d("response",error.toString());

                        TextView field = (TextView)findViewById(R.id.txtO2);
                        field.setText(error.toString());
                    }
                });

        int socketTimeout = 3000;//
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsArrRequest.setRetryPolicy(policy);
        // Access the RequestQueue through your singleton class.
        singleton.getInstance(this).addToRequestQueue(jsArrRequest);
    }

}
