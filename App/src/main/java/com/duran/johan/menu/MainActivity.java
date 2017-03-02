package com.duran.johan.menu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.facebook.AccessToken;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;

import static android.R.attr.id;
import static android.R.id.message;
import static com.duran.johan.menu.R.id.map;
import static com.duran.johan.menu.R.layout.maps;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker;

public class MainActivity extends Navigation
        implements OnMapReadyCallback {

    //variables del mapa
    private GoogleMap mMap;//mapa a mostrar
    private Marker marcadores[] = new Marker[12];

    //variables para peticiones al servidor
    MySingleton singleton;
    String server = "http://192.168.0.101:8081/";
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
        RelativeLayout item = (RelativeLayout) findViewById(R.id.relative_element);
        View child = getLayoutInflater().inflate(maps, null);
        item.addView(child);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FAB Action goes here
                ActivityLauncher.startActivityB(MainActivity.this, ActivityFilter.class);
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng costaRica = new LatLng(10.131581, -84.181927);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(costaRica, 9));
        String file = "getMarkers_busqueda.php"; //temporal solo de ejemplo.
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        getRequest(file, 1);
    }


    //Método utilizado para realizar peticiones asincronas al servidor
    public void getRequest(String file, final int num) {
        dir = server + dir + file;
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

                    }
                });

        int socketTimeout = 3000;//
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsArrRequest.setRetryPolicy(policy);
        // Access the RequestQueue through your singleton class.
        singleton.getInstance(this).addToRequestQueue(jsArrRequest);
    }

    private void cargarMarcadores(JSONArray response) {
        //lo que se desea hacer para la petición 1
        int longitud = 0;
        JSONObject resultados = new JSONObject();
        try {
            longitud = response.getJSONObject(0).getJSONObject("results").length();
            resultados = response.getJSONObject(0).getJSONObject("results");
            marcadores = new Marker[longitud];
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < longitud; i++) {
            try {
                JSONObject location = resultados.getJSONObject(Integer.toString(i)).getJSONObject("value").getJSONObject("location");
                LatLng position = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
                String color = resultados.getJSONObject(Integer.toString(i)).getJSONObject("value").getString("color");
                String title = resultados.getJSONObject(Integer.toString(i)).getJSONObject("value").getString("id");
                BitmapDescriptor iconColor = BitmapDescriptorFactory.defaultMarker(getColor(color));
                mMap.addMarker(new MarkerOptions().position(position).title(title)).setIcon(iconColor);
                agregarEventosMarcadores();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void agregarEventosMarcadores() {
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                View view = getLayoutInflater().inflate(R.layout.map_info_window,null);

                TextView lat = (TextView)view.findViewById(R.id.txtLatitud);
                TextView lng = (TextView)view.findViewById(R.id.txtLongitud);
                TextView est = (TextView)view.findViewById(R.id.txtEstacion);
                lat.setText(String.valueOf(marker.getPosition().latitude));
                lng.setText(String.valueOf(marker.getPosition().longitude));
                est.setText(marker.getTitle());

                return view;
            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(MainActivity.this, ActivityMarker.class);
                intent.putExtra("objId", marker.getTitle());
                startActivity(intent);
            }
        });
    }


    private int getColor(String color) {
        switch (color) {
            case "Azul":
                return 240;
            case "Verde":
                return 120;
            case "Amarillo":
                return 60;
            case "Anaranjado":
                return 30;
            case "Rojo":
                return 0;
            default:
                return 300;
        }
    }
}
