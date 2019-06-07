package com.monitoreo.agua.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class editar_borrar extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private List<Lista_items_editar_borrar> listItems;

    RelativeLayout loading_page;
    String correo;

    private static final int REQUEST_WRITE = 0;
    public boolean permiso = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_borrar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        requestWritePermission();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        //recyclerView.setHasFixedSize(true);

        listItems = new ArrayList<>();

        loadRecyclerViewData();
        //loadRecyclerViewDataPrueba();


    }

    /*private void loadRecyclerViewDataPrueba() {

        for(int i = 0; i<=10; i++) {
            Lista_items_editar_borrar listItem = new Lista_items_editar_borrar(
                    "58dbfe60650eea158841f583" , "NSF", ""+14, "Rojo", i+"/03/2017"
            );

            listItems.add(listItem);

        }

        adapter = new adapter_editar_borrar(listItems, this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loading_page.setVisibility(View.GONE);

    }*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.descargas_menu, menu);
        return true;
    }

    private void loadRecyclerViewData() {
        loading_page = (RelativeLayout) findViewById(R.id.loadingPanelEditar);
        loading_page.setVisibility(View.VISIBLE);
        SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        correo = prefs.getString("correo", "No definido");

        Response.Listener<String> responseListener = new Response.Listener<String>() {//Respuesta del servidor
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("tagconvertstr", "[" + response + "]");
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        String texto = getString(R.string.mensaje_editar_borrar);
                        Toast.makeText(getApplicationContext(), texto, Toast.LENGTH_LONG).show();
                        JSONArray jsonArray = jsonResponse.getJSONArray("documentos");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            //Toast.makeText(getApplicationContext(), obj.getString("_id") + "   "+ obj.getString("fecha"), Toast.LENGTH_LONG).show();

                            Lista_items_editar_borrar item = new Lista_items_editar_borrar(
                                    obj.getString("_id"),
                                    obj.getString("indice_usado"),
                                    obj.getString("val_indice"),
                                    obj.getString("color"),
                                    obj.getString("fecha"),
                                    obj.getString("tipo_de_POI")
                            );
                            listItems.add(item);
                        }

                        adapter = new adapter_editar_borrar(listItems, getApplicationContext());

                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        loading_page.setVisibility(View.GONE);

                    }else{

                        //Intent intent = new Intent(editar_borrar.this, MainActivity.class);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        loading_page.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), getString(R.string.no_hay_documentos), Toast.LENGTH_SHORT).show();
                        //editar_borrar.this.startActivity(intent);

                    }
                } catch (JSONException e) {
                    loading_page.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        };

        Map<String, String> params;
        params = new HashMap<>();
        params.put("correo", correo);

        //Viejo = http://192.168.138.1:8081/proyectoJavier/android/ultimos10datos.php
        //Servidor = getString(R.string.server)+"ultimos10datos.php"

        String direccion = getString(R.string.server)+"ultimos10datos.php";

        //Envia los datos al servidor
        MongoRequest editar_borrar_request = new MongoRequest(params, direccion, responseListener);
        RequestQueue queue = Volley.newRequestQueue(editar_borrar.this);
        queue.add(editar_borrar_request);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(intent);
                return true;
            case R.id.descarga_holandes:
                Toast.makeText(getApplicationContext(), "Funcionalidad será agregada posteriormente", Toast.LENGTH_SHORT).show();
                //requestWritePermission();
                //descarga_datos("Holandés");
                return true;
            case R.id.descarga_nsf:
                Toast.makeText(getApplicationContext(), "Funcionalidad será agregada posteriormente", Toast.LENGTH_SHORT).show();
                //requestWritePermission();
                //descarga_datos("NSF");
                return true;
            case R.id.descarga_bmwp_cr:
                Toast.makeText(getApplicationContext(), "Funcionalidad será agregada posteriormente", Toast.LENGTH_SHORT).show();
                //requestWritePermission();
                //descarga_datos("BMWP-CR");
                return true;

        }
        return super.onOptionsItemSelected(item);

    }

    private void descarga_datos(final String indice) {
        SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        correo = prefs.getString("correo", "No definido");

        Response.Listener<String> responseListener = new Response.Listener<String>() {//Respuesta del servidor
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("tagconvertstr", "[" + response + "]");
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        JSONArray jsonArray = jsonResponse.getJSONArray("documentos");
                        crearCSVIndice(jsonArray, indice);

                    }else{
                        Toast.makeText(getApplicationContext(), getString(R.string.men_no_hay_indice) + " " + indice, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Map<String, String> params;
        params = new HashMap<>();
        params.put("correo", correo);
        params.put("indice", indice);

        //Viejo = http://192.168.138.1:8081/proyectoJavier/android/ultimos10datos.php
        //Servidor = getString(R.string.server)+"ultimos10datos.php"

        String direccion = getString(R.string.server)+"descarga_indice.php";

        //Envia los datos al servidor
        MongoRequest editar_borrar_request = new MongoRequest(params, direccion, responseListener);
        RequestQueue queue = Volley.newRequestQueue(editar_borrar.this);
        queue.add(editar_borrar_request);
    }

    private void crearCSVIndice(JSONArray jsonArray, String indice) {


            try {

                String hoy = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

                String fileName = "Historial-" + indice + "-" + hoy + ".csv";

                File file;

                if(permiso){
                    file = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS), fileName); //
                }else{
                    file = new File(this.getFilesDir(), fileName); //
                }

                if(file.exists()){
                    file.delete();
                }
                if(file.createNewFile()){
                    FileOutputStream fOut = new FileOutputStream(file);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

                    String llaves = "";
                    String valores = "";

                    //Objetos json para iterar.
                    JSONObject obj, jsonMuestra, jsonObligatorios, jsonOpcionales, jsonPOI, jsonLocation, jsonGeo;

                    //iteradores
                    Iterator<String> MuestraI, ObligatoriosI, OpcionalesI, POII, LocationI, GeograficosI;


                    //Sacar llaves y primera muestra
                    obj = jsonArray.getJSONObject(0);

                    jsonMuestra = obj.getJSONObject("Muestra");
                    jsonObligatorios = jsonMuestra.getJSONObject("obligatorios");
                    jsonOpcionales = jsonMuestra.getJSONObject("opcionales");
                    jsonPOI = obj.getJSONObject("POI");
                    jsonLocation = jsonPOI.getJSONObject("location");
                    jsonGeo = jsonPOI.getJSONObject("datos_geograficos");

                    //MUESTRA
                    MuestraI = jsonMuestra.keys();
                    //Mientras existan elementos que leer se insertan en la vista
                    while(MuestraI.hasNext()){
                        String llave = String.valueOf(MuestraI.next());
                        if(!llave.equals("obligatorios") && !llave.equals("opcionales")){
                            llaves = llaves + llave + ",";
                            valores = valores +  jsonMuestra.getString(llave) + ",";

                        }
                    }

                    //OBLIGATORIOS
                    ObligatoriosI = jsonObligatorios.keys();
                    //Mientras existan elementos que leer se insertan en la vista
                    while(ObligatoriosI.hasNext()){
                        String llave = String.valueOf(ObligatoriosI.next());
                        llaves = llaves + llave + ",";
                        valores = valores +  jsonObligatorios.getString(llave) + ",";

                    }

                    //OPCIONALES
                    OpcionalesI = jsonOpcionales.keys();
                    //Mientras existan elementos que leer se insertan en la vista
                    while(OpcionalesI.hasNext()){
                        String llave = String.valueOf(OpcionalesI.next());
                        llaves = llaves + llave + ",";
                        valores = valores +  jsonOpcionales.getString(llave) + ",";

                    }

                    //POI
                    POII = jsonPOI.keys();
                    //Mientras existan elementos que leer se insertan en la vista
                    while(POII.hasNext()){
                        String llave = String.valueOf(POII.next());
                        if(!llave.equals("location") && !llave.equals("datos_geograficos")){
                            llaves = llaves + llave + ",";
                            valores = valores +  jsonPOI.getString(llave) + ",";
                        }
                    }


                    //LOCATION
                    LocationI = jsonLocation.keys();
                    //Mientras existan elementos que leer se insertan en la vista
                    while(LocationI.hasNext()){
                        String llave = String.valueOf(LocationI.next());
                        llaves = llaves + llave + ",";
                        valores = valores +  jsonLocation.getString(llave) + ",";

                    }

                    //DATOS GEOGRAFICOS
                    GeograficosI = jsonGeo.keys();
                    //Mientras existan elementos que leer se insertan en la vista
                    while(GeograficosI.hasNext()){
                        String llave = String.valueOf(GeograficosI.next());
                        llaves = llaves + llave + ",";
                        valores = valores +  jsonGeo.getString(llave) + ",";

                    }

                    myOutWriter.append(llaves);
                    myOutWriter.append("\n");
                    myOutWriter.append(valores);
                    myOutWriter.append("\n");
                    valores = "";




                    for (int i = 1; i < jsonArray.length(); i++) {
                        obj = jsonArray.getJSONObject(i);

                        jsonMuestra = obj.getJSONObject("Muestra");
                        jsonObligatorios = jsonMuestra.getJSONObject("obligatorios");
                        jsonOpcionales = jsonMuestra.getJSONObject("opcionales");
                        jsonPOI = obj.getJSONObject("POI");
                        jsonLocation = jsonPOI.getJSONObject("location");
                        jsonGeo = jsonPOI.getJSONObject("datos_geograficos");


                        //MUESTRA
                        MuestraI = jsonMuestra.keys();
                        //Mientras existan elementos que leer se insertan en la vista
                        while(MuestraI.hasNext()){
                            String llave = String.valueOf(MuestraI.next());
                            if(!llave.equals("obligatorios") && !llave.equals("opcionales")){
                                valores = valores +  jsonMuestra.getString(llave) + ",";

                            }
                        }

                        //OBLIGATORIOS
                        ObligatoriosI = jsonObligatorios.keys();
                        //Mientras existan elementos que leer se insertan en la vista
                        while(ObligatoriosI.hasNext()){
                            String llave = String.valueOf(ObligatoriosI.next());
                            valores = valores +  jsonObligatorios.getString(llave) + ",";

                        }

                        //OPCIONALES
                       OpcionalesI = jsonOpcionales.keys();
                        //Mientras existan elementos que leer se insertan en la vista
                        while(OpcionalesI.hasNext()){
                            String llave = String.valueOf(OpcionalesI.next());
                            valores = valores +  jsonOpcionales.getString(llave) + ",";

                        }

                        //POI
                        POII = jsonPOI.keys();
                        //Mientras existan elementos que leer se insertan en la vista
                        while(POII.hasNext()){
                            String llave = String.valueOf(POII.next());
                            if(!llave.equals("location") && !llave.equals("datos_geograficos")){
                                valores = valores +  jsonPOI.getString(llave) + ",";
                            }
                        }


                        //LOCATION
                        LocationI = jsonLocation.keys();
                        //Mientras existan elementos que leer se insertan en la vista
                        while(LocationI.hasNext()){
                            String llave = String.valueOf(LocationI.next());
                            valores = valores +  jsonLocation.getString(llave) + ",";

                        }

                        //DATOS GEOGRAFICOS
                        GeograficosI = jsonGeo.keys();
                        //Mientras existan elementos que leer se insertan en la vista
                        while(GeograficosI.hasNext()){
                            String llave = String.valueOf(GeograficosI.next());
                            valores = valores +  jsonGeo.getString(llave) + ",";

                        }

                        myOutWriter.append(valores);
                        myOutWriter.append("\n");
                        valores = "";

                    }

                    //But Above Code is not working for me
                    myOutWriter.close();
                    fOut.close();

                }else{
                    Toast.makeText(getApplicationContext(), getString(R.string.archivo_no_creado), Toast.LENGTH_SHORT).show();
                }


            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }







    }


    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case REQUEST_WRITE: {
                permiso = true;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void requestWritePermission() {

        if(isExternalStorageWritable()){
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions( this,
                        new String[] {
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        }, REQUEST_WRITE);
            }else{
                permiso = true;
            }
        }

    }


    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }



}
