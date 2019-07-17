package com.monitoreo.agua.aforo.android;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.GridView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;


public class ActivityAritmetica extends AppCompatActivity {
    //Variable para manejo de colas de peticiones
    MySingleton singleton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aritmetica);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        Bundle extras= intent.getExtras();
        String id1=extras.getString("id1"); // objId es el id del elementro dentro de la BD
        String id2=extras.getString("id2"); // objId es el id del elementro dentro de la BD
        populateView(id1,id2);//cargar de datos
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //ActivityLauncher.startActivityB(ActivityAritmetica.this,MainActivity.class,true);
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void populateView(String id1,String id2) {
        String file ="datosArPOI_busqueda.php?id1="+id1+"&id2="+id2;
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
                        ActivityLauncher.startActivityB(ActivityAritmetica.this, MainActivity.class,true);
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
            JSONObject POIOneOb = response.getJSONObject(0).getJSONObject("Muestra").getJSONObject("obligatorios");
            JSONObject POIOneOp = response.getJSONObject(0).getJSONObject("Muestra").getJSONObject("opcionales");

            JSONObject POITwoOb = response.getJSONObject(1).getJSONObject("Muestra").getJSONObject("obligatorios");
            JSONObject POITwoOp = response.getJSONObject(1).getJSONObject("Muestra").getJSONObject("opcionales");

            //titulos y etiquetas a desplegar

            final ArrayList<String> title = new ArrayList<String>(5);
            title.add(getString(R.string.elemento));
            //title.add(getString(R.string.sitio_uno));
            //title.add(getString(R.string.sitio_dos));
            title.add(getString(R.string.dif));
            title.add(getString(R.string.dif_porc));

            GridView gridViewTitlePOI = (GridView) findViewById(R.id.GridViewTitlePOI);
            GridViewAdapter gridAdapterTitle = new GridViewAdapter (ActivityAritmetica.this, title,3);
            gridViewTitlePOI.setAdapter(gridAdapterTitle);


            //completdo de cuadro de datos
            DecimalFormat df = new DecimalFormat("#.##");//utilizar dos decimales
            final ArrayList<String> items= new ArrayList<String>();
            //se sacan todas las llaves de los datos obligatorios del POI1
            Iterator<String> obligatoriosOneK = POIOneOb.keys();
            //mientras existan datos obligatorios, haga->
            while(obligatoriosOneK.hasNext()){
                //se obtiene el valor asociado a esa llave dentro del json
                String llave=String.valueOf(obligatoriosOneK.next());
                //si el punto dos tiene un campo con la misma llave entonces se comparan
                if(!POITwoOb.isNull(llave)){
                    //se saca y se inserta
                    try{
                        // Place the code which you think, will get an Exception
                        double val1=POIOneOb.getDouble(llave);
                        double val2=POITwoOb.getDouble(llave);
                        double diferencia = val1-val2;
                        double percent = (diferencia/val2);
                        items.add(llave);
                        //items.add(df.format(val1));
                        //items.add(df.format(val2));
                        items.add(df.format(diferencia));
                        items.add(df.format(percent));
                    } catch(Exception e) {
                        // show Toast as below:
                        //Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }else{
                    if(!POITwoOp.isNull(llave)){
                        //se saca y se inserta
                        try{
                            // Place the code which you think, will get an Exception
                            double val1=POIOneOb.getDouble(llave);
                            double val2=POITwoOp.getDouble(llave);
                            double diferencia = val1-val2;
                            double percent = (diferencia/val2);
                            items.add(llave);
                            //items.add(df.format(val1));
                            //items.add(df.format(val2));
                            items.add(df.format(diferencia));
                            items.add(df.format(percent));
                        } catch(Exception e) {
                            // show Toast as below:
                            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }//caso contrario no se hace nada
                }
            }


            Iterator<String> opcionalesOneK = POIOneOp.keys();
            while(opcionalesOneK.hasNext()){
                String llave=String.valueOf(opcionalesOneK.next());
                if(!POITwoOb.isNull(llave)){
                    //se saca y se inserta
                    try{
                        // Place the code which you think, will get an Exception
                        double val1=POIOneOb.getDouble(llave);
                        double val2=POITwoOb.getDouble(llave);
                        double diferencia = val1-val2;
                        double percent = (diferencia/val2);
                        items.add(llave);
                        //items.add(df.format(val1));
                        //items.add(df.format(val2));
                        items.add(df.format(diferencia));
                        items.add(df.format(percent));
                    } catch(Exception e) {
                        // show Toast as below:
                        //Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }else{
                    if(!POITwoOp.isNull(llave)){
                        //se saca y se inserta
                        try{
                            // Place the code which you think, will get an Exception
                            double val1=POIOneOp.getDouble(llave);
                            double val2=POITwoOp.getDouble(llave);
                            double diferencia = val1-val2;
                            double percent = (diferencia/val2);
                            items.add(llave);
                            //items.add(df.format(val1));
                            //items.add(df.format(val2));
                            items.add(df.format(diferencia));
                            items.add(df.format(percent));
                        } catch(Exception e) {
                        }

                    }//caso contrario no se hace nada
                }
            }



            GridView gridViewPOI = (GridView) findViewById(R.id.GridViewPOI);
            int cantidadVertical=items.size()/3;
            ViewGroup.LayoutParams layoutParams = gridViewPOI.getLayoutParams();
            layoutParams.height = convertDpToPixels(40*cantidadVertical,ActivityAritmetica.this); //this is in pixels
            gridViewPOI.setLayoutParams(layoutParams);

            GridViewAdapter gridAdapterPO = new GridViewAdapter (ActivityAritmetica.this, items,1);
            gridViewPOI.setAdapter(gridAdapterPO);
        } catch (JSONException e) {
            //en caso de error se vuelve a la actividad anterior
            ActivityLauncher.startActivityB(ActivityAritmetica.this, MainActivity.class,true);
            finish();
            e.printStackTrace();
        }
    }
    public static int convertDpToPixels(float dp, Context context){
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resources.getDisplayMetrics()
        );
    }
}
