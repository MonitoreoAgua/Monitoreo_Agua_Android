package com.monitoreo.agua.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.monitoreo.agua.android.R.id.map;

public class MainActivity extends Navigation
        implements OnMapReadyCallback, LocationListener {

    //variables del mapa
    private GoogleMap mMap;//mapa a mostrar
    Map<String, String> idColor;
    HashMap<String, Integer> colorsRes;


    //Marcadores para aritmetica de puntos
    Marker first;
    Marker second;
    int contadorClics;

    //variables para peticiones al servidor
    MySingleton singleton;
    boolean isMapReady;//indica al GPS que el mapa está listo
    private LocationManager locationManager;

    //sección de filtrado
    JSONArray filtros;
    HashMap<String, String> parametros_filtro;
    boolean filtros_b;

    //dialogo de carga de los elementos
    ProgressDialog loadingDialog;

    //control de multidex.
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        atachLocationListener();
        //Loading dialig
        loadingDialog = new ProgressDialog(MainActivity.this);
        loadingDialog.setTitle(getString(R.string.cargando_titulo));
        loadingDialog.setMessage(getString(R.string.cargando_main));
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        //Inicialización de variables
        contadorClics = 0;
        idColor = new HashMap<String, String>();
        isMapReady = false;
        filtros_b = false;
        colorsRes = new HashMap<>();//dado un color retorna un id de Resourse
        agregarColores();//se agregan los colores al hashmap

        //evento asociado al fab button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Se inicia la actividad para realizar filtros
                if (filtros_b) {
                    Intent intent = new Intent(MainActivity.this, ActivityFilter.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("filtros", parametros_filtro);
                    MainActivity.this.startActivity(intent);
                } else {
                    ActivityLauncher.startActivityB(MainActivity.this, ActivityFilter.class, false);
                }
            }
        });

        if (isOnline()) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(map);
            mapFragment.getMapAsync(this);
        } else {
            //Toast.makeText(this,getString(R.string.sin_internet),Toast.LENGTH_LONG).show();
            makeAndShowDialogBox().show();
        }
    }


    //Funcion que completa el hash para utilizarlo con los valores de color que vienen de la BD.
    private void agregarColores() {
        colorsRes.put("Gris", R.mipmap.gris);
        colorsRes.put("Azul", R.mipmap.azul);
        colorsRes.put("Verde", R.mipmap.verde);
        colorsRes.put("Anaranjado", R.mipmap.anaranjado);
        colorsRes.put("Amarillo", R.mipmap.amarillo);
        colorsRes.put("Rojo", R.mipmap.rojo);
    }


    //Metodo utilizado para iniciar el proceso de solicitud de ubicación
    private void atachLocationListener() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (askPermissions()) {
            String Permiso[] = {"android.permission.ACCESS_FINE_LOCATION"};
            ActivityCompat.requestPermissions(this, Permiso, 1);
        } else {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
                }
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Toast.makeText(this,"READY",Toast.LENGTH_LONG).show();
        mMap = googleMap;
        LatLng centerCR = new LatLng(9.875371, -84.128913);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centerCR, 10));
        //mMap.animateCamera( CameraUpdateFactory.zoomTo(10));
        // Add a marker in CR and move the camera
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(costaRica, 13));
        String file = "getMarkers_busqueda.php"; //temporal solo de ejemplo.
        mMap.getUiSettings().setMapToolbarEnabled(false); //se desabilita redirección a google maps
        //al momento de llegar aquí puede ser la creación normal de la actividad o puede venirse de activity filter en cuyo caso trae parametros.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
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
                        loadingDialog.hide();
                        Toast.makeText(MainActivity.this, getString(R.string.no_markers), Toast.LENGTH_LONG).show();
                        //Esto se necesita para que si falla se tenga la posibilidad de recargar
                        /*new AlertDialog.Builder(MainActivity.this)
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
            Toast.makeText(MainActivity.this, getString(R.string.no_markers), Toast.LENGTH_LONG).show();
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
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(colorsRes.get(color));
                marker.setIcon(icon);
                marker.setTag(id);
                idColor.put(id,color);
            }catch (Exception e){

            }
        }
        crearEventosMapa();// Si se insertaron se crean los eventos del click.

        //en este punto los marcadores fueron cargados y la aplicación deja de cargar
        loadingDialog.hide();
    }

    //Método para eventos de los marcadores y las ventanas que aparecen al darle clic
    private void crearEventosMapa() {

        //evento asociado al clic sobre el mapa, actualmente para borrar arpoi
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
                // TODO Auto-generated method stub
                if(contadorClics>0){
                    //El getTag obtiene un identificador del marcador y BD
                    //se agregó el cambio de marcador para el caso de gris que es el único que es un recurso externo
                    BitmapDescriptor icon1 = BitmapDescriptorFactory.fromResource(colorsRes.get(idColor.get(first.getTag())));
                    first.setIcon(icon1);
                    if(contadorClics==2){
                        //se agregó el cambio de marcador para el caso de gris que es el único que es un recurso externo
                        BitmapDescriptor icon2 = BitmapDescriptorFactory.fromResource(colorsRes.get(idColor.get(second.getTag())));

                        second.setIcon(icon2);
                    }
                    contadorClics=0;
                }
            }
        });

        //evento asociado a la ventana de información mostrada al presionar un marcador.
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
                    btnWindows.setText(String.valueOf(getString(R.string.ver_mas)));
                    return view;
                }else{//si estamos en aritmetica. Si la cantidad de clics es 1 (0,1) indica que se han seleccionado dos marcadores.
                    if(contadorClics<2){
                        BitmapDescriptor iconColor = BitmapDescriptorFactory.fromResource(R.mipmap.aritmetica);
                        if(contadorClics==0){//es el primer marcador en ser seleccionado.
                            btnWindows.setText(String.valueOf(getString(R.string.seleccionar_otro)));
                            first=marker;
                            marker.setIcon(iconColor);
                            contadorClics++;
                        }else{//==1
                            if(!marker.getTag().equals(first.getTag())){//se debe dar clic sobre uno distinto.
                                btnWindows.setText(String.valueOf(getString(R.string.calcular_diferencia)));
                                second=marker;
                                marker.setIcon(iconColor);
                                contadorClics++;
                            }else{//se retorna seleccionar otro ya que se dio clic sonbre el mismo, además no se aumenta el contador
                                btnWindows.setText(String.valueOf(getString(R.string.seleccionar_otro)));
                            }
                        }
                        return view;
                    }else{
                        contadorClics=0;
                        //se agregó el cambio de marcador para el caso de gris que es el único que es un recurso externo
                        BitmapDescriptor icon1 = BitmapDescriptorFactory.fromResource(colorsRes.get(idColor.get(first.getTag())));
                        BitmapDescriptor icon2 = BitmapDescriptorFactory.fromResource(colorsRes.get(idColor.get(second.getTag())));

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

/*    //Colores asociados a cada tipo de indice
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
    }*/

    //retorna verdadero en caso de existir conexión a internet
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
                        .setMessage(getString(R.string.no_internet_message))
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
        //hasta que el mapa no esté listo, no se utiliza la ubicación
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
