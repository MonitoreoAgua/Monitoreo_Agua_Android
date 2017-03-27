package com.duran.johan.menu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.duran.johan.menu.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class editar_borrar extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private List<Lista_items_editar_borrar> listItems;

    RelativeLayout loading_page;
    String correo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_borrar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        //recyclerView.setHasFixedSize(true);

        loading_page = (RelativeLayout) findViewById(R.id.loadingPanelEditar);
        loading_page.setVisibility(View.VISIBLE);

        String texto = getString(R.string.mensaje_editar_borrar);
        Toast.makeText(getApplicationContext(), texto, Toast.LENGTH_LONG).show();

        listItems = new ArrayList<>();

        //loadRecyclerViewData();
        loadRecyclerViewDataPrueba();


    }

    private void loadRecyclerViewDataPrueba() {

        for(int i = 0; i<=10; i++) {
            Lista_items_editar_borrar listItem = new Lista_items_editar_borrar(
                    "" + (i), "NSF", ""+14, "Rojo", i+"/03/2017"
            );

            listItems.add(listItem);

        }

        adapter = new adapter_editar_borrar(listItems, this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loading_page.setVisibility(View.GONE);

    }


    private void loadRecyclerViewData() {
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

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            Lista_items_editar_borrar item = new Lista_items_editar_borrar(
                                    obj.getString("_id"),
                                    obj.getString("Muestra.indice_usado"),
                                    obj.getString("Muestra.val_indice"),
                                    obj.getString("Muestra.color"),
                                    obj.getString("Muestra.fecha")
                            );
                            listItems.add(item);
                        }

                        adapter = new adapter_editar_borrar(listItems, getApplicationContext());

                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        loading_page.setVisibility(View.GONE);

                    }else{

                        Intent intent = new Intent(editar_borrar.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        loading_page.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), getString(R.string.no_hay_documentos), Toast.LENGTH_SHORT).show();
                        editar_borrar.this.startActivity(intent);

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

}
