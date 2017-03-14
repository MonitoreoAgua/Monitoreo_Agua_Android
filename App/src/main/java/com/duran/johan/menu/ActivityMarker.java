package com.duran.johan.menu;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.R.attr.id;
import static android.os.Build.VERSION_CODES.M;
import static com.duran.johan.menu.R.id.textView;
import static com.duran.johan.menu.R.id.visible;

public class ActivityMarker extends AppCompatActivity {
    //Variable para manejo de colas de peticiones
    MySingleton singleton;
    //arrays para control de valores a ser buscados dentro del json traído del servidor
    //final int [] oblogatoriosInt={R.id.txtO2,R.id.txtDbo,R.id.txtNh4};
    //final int [] opcionalesInt = {R.id.txtCf,R.id.txtDqo,R.id.txtEc,R.id.txtPo4,R.id.txtGya,R.id.txtPh,R.id.txtSd,R.id.txtSsed,R.id.txtSst,R.id.txtSaam,R.id.txtT,R.id.txtAforo,R.id.txtPtsPso,R.id.txtSam};
    //final String [] obligatoriosTxt={"% O2","DBO","NH4"};
    //final String [] opcionalesTxt={"CF","DQO","EC","PO4","GYA","Ph","SD", "Ssed", "SST","SAAM","T","Aforo","ST","pts PSO"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);

        //se leen los valores que entran por parámetro
        Intent intent = getIntent();
        Bundle extras= intent.getExtras();
        String objId=extras.getString("objId"); // objId es el id del elementro dentro de la BD
        populateView(objId);//cargar de datos

    }

    private void populateView(String objId) {
        String file ="datosMarker_busqueda.php?id1="+objId;
        getRequest(file,1);
    }


    /*Método utilizado para realizar peticiones asincronas al servidor
    Parametro 1: nombre del archivo php de tener get debe ir incluido en el string
    Parametro 2: entero para control de opciones de peticiones
    Opciones parametro 2:
    1-> traer información del marcador con id = objID
    */

    public void getRequest(String file, final int num) {
        String dir = getString(R.string.server)+file; //se crea el directorio completo
        Log.d("url",dir);
        //inicio de la petición al servidor GET
        JsonArrayRequest jsArrRequest = new JsonArrayRequest
                (Request.Method.GET, dir, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        switch (num) {//Incluir los casos dependiendo de la cantidad de llamados distintos que se puedan hacer.
                            case 1://petición 1 cargar todos los marcadores
                                cargarMarcadores(response);
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
                        //en caso de error volvemos a la ventana principal
                        ActivityLauncher.startActivityB(ActivityMarker.this, MainActivity.class,true);
                    }
                });

        int socketTimeout = 3000;//tiempo de espera a la peticion
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsArrRequest.setRetryPolicy(policy);
        // Access the RequestQueue through your singleton class.
        singleton.getInstance(this).addToRequestQueue(jsArrRequest);
    }

    //Método llamado al obtenerse respuesta con datos de un marcador
    private void cargarMarcadores(JSONArray response) {
        Log.d("response",response.toString());
        try {
            //se obtiene el documento que contiene los datos obligatorios y opcionales
            JSONObject obligatorios = response.getJSONObject(Integer.parseInt("0")).getJSONObject("Muestra").getJSONObject("obligatorios");
            JSONObject opcionales = response.getJSONObject(Integer.parseInt("0")).getJSONObject("Muestra").getJSONObject("opcionales");
            //complatado de datos obligatorios
            final String[] itemsObligatorios = new String[obligatorios.length()*2];
            Iterator<String> obligatoriosK = obligatorios.keys();
            int contador=0;
            while(obligatoriosK.hasNext()){
                String llave=String.valueOf(obligatoriosK.next());
                String valor =obligatorios.getString(llave);
                itemsObligatorios[contador++]=llave;
                itemsObligatorios[contador++]=valor;
            }

            GridView gridViewOb = (GridView) this.findViewById(R.id.GridViewObligatorios);
            GridViewAdapter gridAdapterOb = new GridViewAdapter (ActivityMarker.this, itemsObligatorios);
            gridViewOb.setAdapter(gridAdapterOb);

            //complatado de datos opcionales
            final String[] itemsOpcionales = new String[opcionales.length()*2];
            Iterator<String> opcionalesK = opcionales.keys();
            contador=0;
            while(opcionalesK.hasNext()){
                String llave=String.valueOf(opcionalesK.next());
                String valor =opcionales.getString(llave);
                itemsOpcionales[contador++]=llave;
                itemsOpcionales[contador++]=valor;
            }
            GridView gridViewOp = (GridView) this.findViewById(R.id.GridViewOpcionales);
            GridViewAdapter gridAdapterOp = new GridViewAdapter (ActivityMarker.this, itemsOpcionales);
            gridViewOp.setAdapter(gridAdapterOp);

        } catch (JSONException e) {
            //en caso de error se vuelve a la actividad anterior
            ActivityLauncher.startActivityB(ActivityMarker.this, MainActivity.class,true);
            finish();
            e.printStackTrace();
        }
    }
}
