package com.monitoreo.agua.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
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
import org.w3c.dom.Text;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.monitoreo.agua.android.R.id.map;

public class MainActivity extends Navigation
        implements OnMapReadyCallback, LocationListener {

    //archivo para guardar la selección de indice de la persona.
    public static final String PREFS_NAME = "spinnerSelectionFile";
    int cantidadRecargas;
    //variables del mapa
    private GoogleMap mMap;//mapa a mostrar
    Map<String, String> idColor;
    Map<String, String> idValor;
    Map<String, String> idTipo;
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
    HashMap<String, Boolean> tipos_a_mostrar;
    boolean filtros_b;
    private Spinner spinner_seleccionar_indice;


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
        idValor = new HashMap<String, String>();
        idTipo = new HashMap<String, String>();
        isMapReady = false;
        filtros_b = false;
        cantidadRecargas = 0;
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
                    intent.putExtra("toggleIcons", tipos_a_mostrar);
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
        //Fuente Superficial
        colorsRes.put("fuente_superficial_Gris", R.mipmap.gris);
        colorsRes.put("fuente_superficial_Azul", R.mipmap.azul);
        colorsRes.put("fuente_superficial_Verde", R.mipmap.verde);
        colorsRes.put("fuente_superficial_Anaranjado", R.mipmap.anaranjado);
        colorsRes.put("fuente_superficial_Amarillo", R.mipmap.amarillo);
        colorsRes.put("fuente_superficial_Rojo", R.mipmap.rojo);
        colorsRes.put("Mitigacion", R.mipmap.mitigacion);
        colorsRes.put("QTWQI", R.mipmap.qtwqi);

        //Naciente
        colorsRes.put("naciente_Gris", R.drawable.naciente_gris);
        colorsRes.put("naciente_Azul", R.drawable.naciente_azul);
        colorsRes.put("naciente_Verde", R.drawable.naciente_verde);
        colorsRes.put("naciente_Anaranjado", R.drawable.naciente_anaranjado);
        colorsRes.put("naciente_Amarillo", R.drawable.naciente_amarillo);
        colorsRes.put("naciente_Rojo", R.drawable.naciente_rojo);
        colorsRes.put("naciente_QTWQI", R.drawable.naciente_qtwqi);

        //Agua Termal
        colorsRes.put("agua_termal_Gris", R.drawable.agua_termal_gris);
        colorsRes.put("agua_termal_Azul", R.drawable.agua_termal_azul);
        colorsRes.put("agua_termal_Verde", R.drawable.agua_termal_verde);
        colorsRes.put("agua_termal_Anaranjado", R.drawable.agua_termal_anaranjado);
        colorsRes.put("agua_termal_Amarillo", R.drawable.agua_termal_amarillo);
        colorsRes.put("agua_termal_Rojo", R.drawable.agua_termal_rojo);
        colorsRes.put("agua_termal_QTWQI", R.drawable.agua_termal_qtwqi);

        //Pozo
        colorsRes.put("pozo_Gris", R.drawable.pozo_gris);
        colorsRes.put("pozo_Azul", R.drawable.pozo_azul);
        colorsRes.put("pozo_Verde", R.drawable.pozo_verde);
        colorsRes.put("pozo_Anaranjado", R.drawable.pozo_anaranjado);
        colorsRes.put("pozo_Amarillo", R.drawable.pozo_amarillo);
        colorsRes.put("pozo_Rojo", R.drawable.pozo_rojo);
        colorsRes.put("pozo_QTWQI", R.drawable.pozo_qtwqi);
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
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
        //Se realiza solo la primera vez. Al cargarse el mapa se elige el valor del spinner acorde a lo guardado.
        spinner_seleccionar_indice = (Spinner) findViewById(R.id.spinner_seleccionar_indice);
        /*SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        int indexPosition = settings.getInt("spinnerIndexPosition",0);
        spinner_seleccionar_indice.setSelection(indexPosition);
        cantidadRecargas++;*/
        cargarSpinnerMenu();

        //Toast.makeText(this,"READY",Toast.LENGTH_LONG).show();
        mMap = googleMap;
        LatLng centerCR = new LatLng(9.875371, -84.128913);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centerCR, 10));
        //mMap.animateCamera( CameraUpdateFactory.zoomTo(10));
        // Add a marker in CR and move the camera
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(costaRica, 13));
        mMap.getUiSettings().setMapToolbarEnabled(false); //se desabilita redirección a google maps
        inicializarYCarga();
    }

    public void inicializarYCarga() {
        String file = "getMarkers_old.php?indice_usado=" + spinner_seleccionar_indice.getSelectedItemPosition();

        //al momento de llegar aquí puede ser la creación normal de la actividad o puede venirse de activity filter en cuyo caso trae parametros.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            try {
                if (intent.hasExtra("filtros")) {
                    filtros = new JSONArray(extras.getString("response"));
                    parametros_filtro = (HashMap<String, String>) intent.getSerializableExtra("filtros");
                    tipos_a_mostrar = (HashMap<String, Boolean>) intent.getSerializableExtra("toggleIcons");
                    cargarMarcadores(filtros);

                    filtros_b = true;
                    intent.removeExtra("response");
                    intent.removeExtra("filtros");
                    intent.removeExtra("toggleIcons");
                }
                else if (intent.hasExtra("toggleIcons")) {
                    tipos_a_mostrar = (HashMap<String, Boolean>) intent.getSerializableExtra("toggleIcons");

                    filtros_b = true;
                    intent.removeExtra("toggleIcons");
                    getRequest(file, 1); //peticion para cargar los marcadores
                }
            } catch (JSONException e) {
                //e.printStackTrace();
            }
        } else {
            getRequest(file, 1); //peticion para cargar los marcadores
        }
    }

    //metodo para controlar el elemento seleccionado del menú con respecto al indice y los datos que se deben mostrar.
    private void cargarSpinnerMenu() {
        spinner_seleccionar_indice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("spinnerIndexPosition", position);
                editor.commit();
                recargarDatos(2);//dos indica que es recarga de nuevo índice para marcadores.
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    //Método utilizado para realizar peticiones asincronas al servidor
    public void getRequest(String file, final int num) {
        String dir = getString(R.string.server) + file;//se crea la dirección completa al servidor
        Log.e("aiuda",dir);

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
                        Log.e("aiuda",error.toString());
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
//        Toast.makeText(this,response.toString(),Toast.LENGTH_LONG).show();

        //lo que se desea hacer para la petición 1
        int cantidadMarcadores = 0;

        cantidadMarcadores = response.length();
        if (cantidadMarcadores == 0) {
            Toast.makeText(MainActivity.this, getString(R.string.no_markers), Toast.LENGTH_LONG).show();
        }
        //se insertan los marcadores en el mapa
        for (int i = 0; i < cantidadMarcadores; i++) {
            try {
                JSONObject location = response.getJSONObject(i).getJSONObject("location");
                LatLng position = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
                String color = response.getJSONObject(i).getString("color");
                String tipo = color.equalsIgnoreCase("Mitigacion")?"mitigacion":response.getJSONObject(i).getString("tipo_de_POI");
                String valorTmp = "";
                if (response.getJSONObject(i).has("valor")) {
                    valorTmp = response.getJSONObject(i).getString("valor");
                }
                else if (response.getJSONObject(i).has("val_indice")) {
                    valorTmp = response.getJSONObject(i).getString("val_indice");
                }
                String valor = !color.equalsIgnoreCase("Mitigacion")?valorTmp:"";
                String id = response.getJSONObject(i).getString("id");
                String title = response.getJSONObject(i).getString("_id");

                if (filtros_b) {
                    if (tipos_a_mostrar.get(tipo)) {
                        Marker marker = mMap.addMarker(new MarkerOptions().position(position).title(title));
                        if (!color.equalsIgnoreCase("Mitigacion") && !color.equalsIgnoreCase("QTWQI")) {
                            color = tipo + "_" + color;
                        }
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(colorsRes.get(color));
                        marker.setIcon(color.equalsIgnoreCase("QTWQI") ? BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(colorsRes.get("QTWQI"), valor)) : icon);
                        marker.setTag(color.equalsIgnoreCase("Mitigacion") ? "Mitigacion" : id);
                        tipo = tipo.replace('_', ' ');
                        idColor.put(id, color);
                        idValor.put(id, valor);
                        idTipo.put(id, tipo.substring(0, 1).toUpperCase() + tipo.substring(1));
                    }
                }
                else {
                    Marker marker = mMap.addMarker(new MarkerOptions().position(position).title(title));
                    if (!color.equalsIgnoreCase("Mitigacion") && !color.equalsIgnoreCase("QTWQI")) {
                        color = tipo + "_" + color;
                    }
                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(colorsRes.get(color));

                    marker.setIcon(color.equalsIgnoreCase("QTWQI") ? BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(colorsRes.get("QTWQI"), valor)) : icon);
                    marker.setTag(color.equalsIgnoreCase("Mitigacion") ? "Mitigacion" : id);
                    tipo = tipo.replace('_',' ');
                    idColor.put(id, color);
                    idValor.put(id, valor);
                    idTipo.put(id, tipo.substring(0,1).toUpperCase() + tipo.substring(1));
                }
            } catch (Exception e) {
                Log.e("JSONParsing",e.getMessage());
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
                if (contadorClics > 0) {
                    //El getTag obtiene un identificador del marcador y BD
                    //se agregó el cambio de marcador para el caso de gris que es el único que es un recurso externo
                    BitmapDescriptor icon1 = BitmapDescriptorFactory.fromResource(colorsRes.get(idColor.get(first.getTag())));
                    first.setIcon(idColor.get(first.getTag()).equalsIgnoreCase("QTWQI") ? BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(colorsRes.get(idColor.get(first.getTag())), idValor.get(first.getTag()))) : icon1);
                    //first.setIcon(icon1);
                    if (contadorClics == 2) {
                        //se agregó el cambio de marcador para el caso de gris que es el único que es un recurso externo
                        BitmapDescriptor icon2 = BitmapDescriptorFactory.fromResource(colorsRes.get(idColor.get(second.getTag())));
                        second.setIcon(idColor.get(second.getTag()).equalsIgnoreCase("QTWQI") ? BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(colorsRes.get(idColor.get(second.getTag())), idValor.get(second.getTag()))) : icon2);

                        //second.setIcon(icon2);
                    }
                    contadorClics = 0;
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

                View view = getLayoutInflater().inflate(R.layout.map_info_window, null);

                TextView lat = (TextView) view.findViewById(R.id.txtLatitud);
                TextView lng = (TextView) view.findViewById(R.id.txtLongitud);
                TextView est = (TextView) view.findViewById(R.id.txtEstacion);
                TextView pType = (TextView) view.findViewById(R.id.txtPOIType);
                lat.setText(String.valueOf(marker.getPosition().latitude));
                lng.setText(String.valueOf(marker.getPosition().longitude));
                est.setText(marker.getTitle());
                pType.setText(idTipo.get(marker.getTag()));
                TextView btnWindows = (TextView) view.findViewById(R.id.verMas);
                btnWindows.setVisibility(View.VISIBLE);
                btnWindows.setEnabled(true);

                if (marker.getTag().toString().equalsIgnoreCase("Mitigacion")) {
                    btnWindows.setEnabled(false);
                    btnWindows.setVisibility(View.GONE);
                    return view;
                } else if (!arPOIFlag) {//si no estamos en aritmetica, al dar clic se muestra ver más.
                    btnWindows.setText(String.valueOf(getString(R.string.ver_mas)));
                    return view;
                } else {//si estamos en aritmetica. Si la cantidad de clics es 1 (0,1) indica que se han seleccionado dos marcadores.
                    if (contadorClics < 2) {
                        BitmapDescriptor iconColor = BitmapDescriptorFactory.fromResource(R.mipmap.aritmetica);
                        if (contadorClics == 0) {//es el primer marcador en ser seleccionado.
                            btnWindows.setText(String.valueOf(getString(R.string.seleccionar_otro)));
                            first = marker;
                            marker.setIcon(iconColor);
                            contadorClics++;
                        } else {//==1
                            if (!marker.getTag().equals(first.getTag())) {//se debe dar clic sobre uno distinto.
                                btnWindows.setText(String.valueOf(getString(R.string.calcular_diferencia)));
                                second = marker;
                                marker.setIcon(iconColor);
                                contadorClics++;
                            } else {//se retorna seleccionar otro ya que se dio clic sonbre el mismo, además no se aumenta el contador
                                btnWindows.setText(String.valueOf(getString(R.string.seleccionar_otro)));
                            }
                        }
                        return view;
                    } else {
                        contadorClics = 0;
                        //se agregó el cambio de marcador para el caso de gris que es el único que es un recurso externo
                        BitmapDescriptor icon1 = BitmapDescriptorFactory.fromResource(colorsRes.get(idColor.get(first.getTag())));
                        BitmapDescriptor icon2 = BitmapDescriptorFactory.fromResource(colorsRes.get(idColor.get(second.getTag())));
                        first.setIcon(idColor.get(first.getTag()).equalsIgnoreCase("QTWQI") ? BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(colorsRes.get(idColor.get(first.getTag())), idValor.get(first.getTag()))) : icon1);
                        second.setIcon(idColor.get(second.getTag()).equalsIgnoreCase("QTWQI") ? BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(colorsRes.get(idColor.get(second.getTag())), idValor.get(second.getTag()))) : icon2);

                        //first.setIcon(icon1);
                        //second.setIcon(icon2);
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
                if (arPOIFlag) {
                    if (contadorClics == 2) {
                        Intent intent = new Intent(MainActivity.this, ActivityAritmetica.class);
                        intent.putExtra("id1", String.valueOf(first.getTag()));
                        intent.putExtra("id2", String.valueOf(second.getTag()));
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(MainActivity.this, ActivityMarker.class);
                    intent.putExtra("objId", String.valueOf(marker.getTag()));
                    startActivity(intent);
                }
            }
        });
        //se indica que el mapa está listo al location change para que cargue la posición actual
        isMapReady = true;
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


    private AlertDialog makeAndShowDialogBox() {
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
                                if (!isOnline()) {
                                    makeAndShowDialogBox().show();
                                } else {
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
        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
        if (isMapReady) {
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
    private boolean askPermissions() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            return true;
        }
        return false;
    }

    private void recargarDatos(int tipoRecarga) {
        //reinicialización de variables
        mMap.clear();
        contadorClics = 0;
        idColor.clear();
        isMapReady = false;
        filtros_b = false;

        //Marcadores para aritmetica de puntos
        contadorClics = 0;

        //sección de filtrado
        if (parametros_filtro != null) {
            parametros_filtro.clear();
        }
        if (tipos_a_mostrar != null) {
            tipos_a_mostrar.clear();
        }
        //Loading dialog
        loadingDialog = new ProgressDialog(MainActivity.this);
        loadingDialog.setTitle(getString(R.string.cargando_titulo));
        loadingDialog.setMessage(getString(R.string.cargando_main));
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        inicializarYCarga();
    }

    private Bitmap writeTextOnDrawable(int drawableId, String text) {

        Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId)
                .copy(Bitmap.Config.ARGB_8888, true);

        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(convertToPixels(this.getApplicationContext(), 11));

        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(bm);

        //If the text is bigger than the canvas , reduce the font size
        if (textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
            paint.setTextSize(convertToPixels(this.getApplicationContext(), 7));        //Scaling needs to be used for different dpi's

        //Calculate the positions
        int xPos = (canvas.getWidth() / 2);     //-2 is for regulating the x position offset

        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) - (canvas.getHeight() / 6);

        canvas.drawText(text, xPos, yPos, paint);

        return bm;
    }


    public static int convertToPixels(Context context, int nDP) {
        final float conversionScale = context.getResources().getDisplayMetrics().density;

        return (int) ((nDP * conversionScale) + 0.5f);

    }
}
