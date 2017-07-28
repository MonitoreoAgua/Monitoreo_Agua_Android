package com.duran.johan.menu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.facebook.AccessToken;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.query.Filter;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.delay;
import static android.R.attr.dial;
import static android.R.attr.id;
import static android.R.id.message;
import static android.graphics.Color.GRAY;
import static android.os.Build.VERSION_CODES.M;
import static com.duran.johan.menu.R.id.longitud;
import static com.duran.johan.menu.R.id.map;
import static com.duran.johan.menu.R.layout.maps;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker;
import static java.sql.DriverManager.println;

public class MainActivity extends Navigation
        implements OnMapReadyCallback, LocationListener {

    //variables del mapa
    private GoogleMap mMap;//mapa a mostrar
    List<Marker> markers = new ArrayList<Marker>();
    JSONObject marcadoresJson;
    Map<String, String> idColor;
    //Marcadores para aritmetica de puntos
    Marker first;
    Marker second;
    int contadorClics;
    //variables para peticiones al servidor
    MySingleton singleton;
    //indica al GPS que el mapa está listo
    boolean isMapReady;
    private LocationManager locationManager;

    JSONArray filtros;
    HashMap<String, String> parametros_filtro;
    boolean filtros_b;

    //dialogo de carga de los elementos
    ProgressDialog dialog;
    //control de multidex.
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setTitle(getString(R.string.cargandoTitulo));
        dialog.setMessage(getString(R.string.cargandoMain));
        dialog.show();
        //se agrega la vista complementaria al menú.
        RelativeLayout item = (RelativeLayout) findViewById(R.id.relative_element);
        View child = getLayoutInflater().inflate(maps, null);
        item.addView(child);
        //inicialización de variable
        contadorClics = 0;
        idColor = new HashMap<String, String>();
        isMapReady = false;
        filtros_b = false;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (askPermissions()) {
            //Toast.makeText(this,"ASK P",Toast.LENGTH_LONG).show();
            String Permiso[] = {"android.permission.ACCESS_FINE_LOCATION"};
            ActivityCompat.requestPermissions(this, Permiso, 1);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        }else{
           // Toast.makeText(this," NO ASK P",Toast.LENGTH_LONG).show();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        }

         //evento asociado al boton sobre el mapa
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Se inicia la actividad para realizar filtros
                if(filtros_b){
                    Intent intent = new Intent(MainActivity.this, ActivityFilter.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("filtros", parametros_filtro);
                    MainActivity.this.startActivity(intent);
                }else{
                    ActivityLauncher.startActivityB(MainActivity.this, ActivityFilter.class, false);
                }


            }
        });

        if(isOnline()){
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(map);
            mapFragment.getMapAsync(this);
        }else{
            Toast.makeText(this,getString(R.string.sinInternet),Toast.LENGTH_LONG).show();
            makeAndShowDialogBox().show();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Toast.makeText(this,"READY",Toast.LENGTH_LONG).show();
        mMap = googleMap;
        LatLng centerCR = new LatLng(9.875371,-84.128913);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centerCR, 10));
        //mMap.animateCamera( CameraUpdateFactory.zoomTo(10));
        // Add a marker in CR and move the camera
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(costaRica, 13));
        String file = "getMarkers_busqueda.php"; //temporal solo de ejemplo.
        mMap.getUiSettings().setMapToolbarEnabled(false); //se desabilita redirección a google maps
        mMap.getUiSettings().setMyLocationButtonEnabled(true); //habilita myLocation buttom

        Intent intent = getIntent();
        Bundle extras= intent.getExtras();
        if(extras != null){
            try {
                filtros = new JSONArray(extras.getString("response"));
                cargarMarcadores(filtros);
                parametros_filtro = (HashMap<String, String>) intent.getSerializableExtra("filtros");
                filtros_b = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            getRequest(file, 1); //peticion para cargar los marcadores
        }
    }


    //Método utilizado para realizar peticiones asincronas al servidor
    public void getRequest(String file, final int num) {
        String dir = getString(R.string.server)+ file;//se crea la dirección completa al servidor

        //inicio de la peticion GET
        JsonArrayRequest jsArrRequest = new JsonArrayRequest
                (Request.Method.GET, dir, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        switch (num) {//Incluir los casos dependiendo de la cantidad de llamados distintos que se puedan hacer.
                            case 1://petición 1 cargar todos los marcadores
                                cargarMarcadores(response);
                                dialog.hide();
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
                        dialog.hide();
                        Toast.makeText(MainActivity.this, getString(R.string.noMarkers), Toast.LENGTH_LONG).show();
/*                        new AlertDialog.Builder(MainActivity.this)
                                .setMessage(getString(R.string.recargarMain))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.recargarMain), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        MainActivity.this.startActivity(intent);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                    }
                                })
                                .show();*/
                    }
                });

        int socketTimeout = 3000;//Tiempo dde espera antes de morir TIMEOUT
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsArrRequest.setRetryPolicy(policy);
        // Access the RequestQueue through your singleton class.
        singleton.getInstance(this).addToRequestQueue(jsArrRequest);
    }

    //Método para cargar los marcadorees sobre le mapa recibe el json de la peticion
    private void cargarMarcadores(JSONArray response) {
        //Toast.makeText(this,response.toString(),Toast.LENGTH_LONG).show();
        //lo que se desea hacer para la petición 1
        int cantidadMarcadores = 0;

        cantidadMarcadores = response.length();
        if(cantidadMarcadores==0){
            Toast.makeText(MainActivity.this, getString(R.string.noMarkers), Toast.LENGTH_LONG).show();
        }
        //se insertan los marcadores en el mapa
        for (int i = 0; i < cantidadMarcadores; i++) {
            try {
                JSONObject location = response.getJSONObject(i).getJSONObject("location");
                LatLng position = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
                String color = response.getJSONObject(i).getString("color");
                String id = response.getJSONObject(i).getString("id");
                String title = response.getJSONObject(i).getString("_id");
                Marker marker= mMap.addMarker(new MarkerOptions().position(position).title(title));
                BitmapDescriptor icon;
                if(color.equals("Gris")){
                    icon = BitmapDescriptorFactory.fromResource(R.mipmap.gray_marker);
                }else{
                    icon = BitmapDescriptorFactory.defaultMarker(getColor(color));
                }
                marker.setIcon(icon);
                marker.setTag(id);
                idColor.put(id,color);
                //markers.add(Integer.getInteger(id),marker);
            }catch (Exception e){

            }
        }
        crearEventosMapa();// Si se insertaron se crean los eventos del click.
    }

    //Método para eventos de los marcadores y las ventanas que aparecen al darle clic
    private void crearEventosMapa() {

        //evento asociado al clic sobre el mapa, actualmente para borrar arpoi
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
                // TODO Auto-generated method stub
                //Log.d("arg0", arg0.latitude + "-" + arg0.longitude);
                if(contadorClics>0){
                    //se agregó el cambio de marcador para el caso de gris que es el único que es un recurso externo
                    BitmapDescriptor icon1;
                    if(idColor.get(first.getTag()).equals("Gris")){
                        icon1 = BitmapDescriptorFactory.fromResource(R.mipmap.gray_marker);
                    }else{
                        icon1 = BitmapDescriptorFactory.defaultMarker(getColor(idColor.get(first.getTag())));
                    }
                    first.setIcon(icon1);
                    if(contadorClics==2){
                        //se agregó el cambio de marcador para el caso de gris que es el único que es un recurso externo
                        BitmapDescriptor icon2;
                        if(idColor.get(second.getTag()).equals("Gris")){
                            icon2 = BitmapDescriptorFactory.fromResource(R.mipmap.gray_marker);
                        }else{
                            icon2 = BitmapDescriptorFactory.defaultMarker(getColor(idColor.get(second.getTag())));
                        }
                        second.setIcon(icon2);
                    }
                    contadorClics=0;
                }
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                // TODO Auto-generated method stub
                return null;
            }

            //diseño de la ventana del marcador
            @Override
            public View getInfoContents(Marker marker) {

                View view = getLayoutInflater().inflate(R.layout.map_info_window,null);

                TextView lat = (TextView)view.findViewById(R.id.txtLatitud);
                TextView lng = (TextView)view.findViewById(R.id.txtLongitud);
                TextView est = (TextView)view.findViewById(R.id.txtEstacion);
                lat.setText(String.valueOf(marker.getPosition().latitude));
                lng.setText(String.valueOf(marker.getPosition().longitude));
                est.setText(marker.getTitle());
                TextView btnWindows = (TextView)view.findViewById(R.id.verMas);
                if(!arPOIFlag){//si no estamos en aritmetica, al dar clic se muestra ver más.
                    btnWindows.setText(String.valueOf(getString(R.string.verMas)));
                    return view;
                }else{//si estamos en aritmetica. Si la cantidad de clics es 1 (0,1) indica que se han seleccionado dos marcadores.
                    if(contadorClics<2){
                        BitmapDescriptor iconColor = BitmapDescriptorFactory.defaultMarker(getColor("rose"));
                        if(contadorClics==0){//es el primer marcador en ser seleccionado.
                            btnWindows.setText(String.valueOf(getString(R.string.seleccionarOtro)));
                            first=marker;
                            marker.setIcon(iconColor);
                            contadorClics++;
                        }else{//==1
                            if(!marker.getTag().equals(first.getTag())){//se debe dar clic sobre uno distinto.
                                btnWindows.setText(String.valueOf(getString(R.string.calcularDiferencia)));
                                second=marker;
                                marker.setIcon(iconColor);
                                contadorClics++;
                            }else{//se retorna seleccionar otro ya que se dio clic sonbre el mismo, además no se aumenta el contador
                                btnWindows.setText(String.valueOf(getString(R.string.seleccionarOtro)));
                            }
                        }
                        return view;
                    }else{
                        contadorClics=0;
                        //se agregó el cambio de marcador para el caso de gris que es el único que es un recurso externo
                        BitmapDescriptor icon1;
                        if(idColor.get(first.getTag()).equals("Gris")){
                            icon1 = BitmapDescriptorFactory.fromResource(R.mipmap.gray_marker);
                        }else{
                            icon1 = BitmapDescriptorFactory.defaultMarker(getColor(idColor.get(first.getTag())));
                        }
                        BitmapDescriptor icon2;
                        if(idColor.get(second.getTag()).equals("Gris")){
                            icon2 = BitmapDescriptorFactory.fromResource(R.mipmap.gray_marker);
                        }else{
                            icon2 = BitmapDescriptorFactory.defaultMarker(getColor(idColor.get(second.getTag())));
                        }
                        first.setIcon(icon1);
                        second.setIcon(icon2);
                        return getInfoContents(marker);
                    }
                }
            }
        });
        //evento para control de clic dentro de la ventana
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //al presionarse clic se lanza la actividad de marcador información.
                if(arPOIFlag){
                    if (contadorClics==2){
                        Intent intent = new Intent(MainActivity.this, ActivityAritmetica.class);
                        intent.putExtra("id1", String.valueOf(first.getTag()));
                        intent.putExtra("id2", String.valueOf(second.getTag()));
                        startActivity(intent);
                    }
                }else{
                    Intent intent = new Intent(MainActivity.this, ActivityMarker.class);
                    intent.putExtra("objId", String.valueOf(marker.getTag()));
                    startActivity(intent);
                }
            }
        });
        //se indica que el mapa está listo al location change para que cargue la posición actual
        isMapReady=true;
    }

    //Colores asociados a cada tipo de indice
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
            case "arpoi":
                return 200;
            default:
                return 300;
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    private AlertDialog makeAndShowDialogBox(){
        AlertDialog myQuittingDialogBox =

                new AlertDialog.Builder(this)
                        //set message, title, and icon
                        .setTitle("Internet")
                        .setMessage(getString(R.string.noInternetMessage))
                        .setIcon(R.drawable.ic_logo_monitoreosvg)
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.reintentar), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //whatever should be done when answering "YES" goes here
                                if(!isOnline()){
                                    makeAndShowDialogBox().show();
                                }else{
                                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                            .findFragmentById(map);
                                    mapFragment.getMapAsync(MainActivity.this);
                                }
                            }
                        })//setPositiveButton
                        .setNegativeButton(getString(R.string.salir), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                finish();
                                System.exit(0);
                                //whatever should be done when answering "NO" goes here
                            }
                        })//setNegativeButton

                        .create();

        return myQuittingDialogBox;
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng current = new LatLng(location.getLatitude(),location.getLongitude());
        if(isMapReady){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, 10));
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /**
     * Metodo encargado de cerciorarse si es o no necesaria la solicitud dinamica de permisos.
     *
     * @return verdadero si android del dispositivo es mayor a Lollipop, en caso contrario falso
     */
    private boolean askPermissions(){

        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1)
        {
            return true;
        }
        return false;
    }
}
