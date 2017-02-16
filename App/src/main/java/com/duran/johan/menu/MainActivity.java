package com.duran.johan.menu;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewStub;
import android.widget.RelativeLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;

import static android.R.attr.id;
import static com.duran.johan.menu.R.id.map;
import static com.duran.johan.menu.R.layout.maps;

public class MainActivity extends Navigation
        implements OnMapReadyCallback {

    //variables del mapa
    private GoogleMap mMap;//mapa a mostrar

    //variables para peticiones al servidor
    MySingleton singleton;
    String server="http://192.168.0.106:8081/";
    String dir = "Proyectos/Monitoreo_Agua_Web/php/";

    //control de multidex.
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout item = (RelativeLayout)findViewById(R.id.relative_element);
        View child = getLayoutInflater().inflate(maps, null);
        item.addView(child);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng costaRica = new LatLng(10.131581, -84.181927);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(costaRica));
        String file = "getMarkers_busqueda.php"; //temporal solo de ejemplo.
        getRequest(file,1);
    }


    //Método utilizado para realizar peticiones asincronas al servidor
    public void getRequest(String file,final int num) {
        dir = server+dir+file;
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
    }

}
