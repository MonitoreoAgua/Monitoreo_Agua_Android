package com.duran.johan.menu;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.duran.johan.menu.R;
import com.facebook.AccessToken;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.key;
import static android.content.Context.MODE_PRIVATE;
import static com.duran.johan.menu.R.id.CFOpc;
import static com.duran.johan.menu.R.id.NH4Opc;
import static com.duran.johan.menu.R.id.Sol_totales;
import static com.duran.johan.menu.R.id.edit_area_adminis_1;
import static com.duran.johan.menu.R.id.edit_area_adminis_2;
import static com.duran.johan.menu.R.id.edit_area_adminis_3;
import static com.duran.johan.menu.R.id.edit_pais;
import static com.duran.johan.menu.R.id.pHOpc;

public class ActivityAgregar extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Boolean flag;

    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    Spinner spinnerKit;
    ArrayAdapter<CharSequence> adapterKit;
    EditText usuario;
    EditText etPO2;
    EditText etDBO;
    EditText etNH4;
    EditText etCF;
    EditText etpH;
    EditText etFosfato;
    EditText etNitrato;
    EditText etT;
    EditText etTurbidez;
    EditText etSol_totales;
    EditText etBiodiversidad;

    //Opcionales
    EditText etNH4Opc;
    EditText etpHOpc;
    EditText etCFOpc;
    EditText DQO;
    EditText EC;
    EditText PO4;
    EditText GYA;
    EditText SD;
    EditText Ssed;
    EditText STT;
    EditText ST;
    EditText SAMM;
    EditText Aforo;
    EditText FosfatoOpc;
    EditText NitratoOpc;
    EditText TOpc;
    EditText TurbidezOpc;
    EditText Sol_totalesOpc;
    EditText BiodiversidadOpc;


    EditText Nomb_Institucion;
    EditText Nomb_estacion;
    EditText editLatitud;
    EditText editLongitud;
    EditText editAltitud;
    EditText edit_country;
    EditText edit_area_admin_1;
    EditText edit_area_admin_2;
    EditText edit_area_admin_3;

    EditText editTemperatura;
    EditText editAreaCauce;
    EditText editVelocidad;

    RelativeLayout generales;
    RelativeLayout obligatorios;
    RelativeLayout opcionales;
    ExpandableLinearLayout content_generales;
    ExpandableLinearLayout content_obligatorios;
    ExpandableLinearLayout content_opcionales;
    private final String[] StringPermisos = {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.INTERNET};

    Button btnDatePicker, boton_agregar;
    EditText txtDate;
    private int mYear, mMonth, mDay;


    //Strings
    String StPO2;
    String StDBO;
    String StCF;
    String StpH;
    String StNH4;
    String StFosfato;
    String StNitrato;
    String StT;
    String StTurbidez;
    String correo;
    String StNombInstitucion;
    String StNombEstacion;
    String StEditLatitud;
    String StEditLongitud;
    String indice;
    String SteditAltitud;
    String StFecha;
    String Stkit;
    String SteditTemperatura;
    String SteditAreaCauce;
    String SteditVelocidad;
    String StSol_totales;
    String StBiodiversidad;


    String country;
    String area_administrativa_1;
    String area_administrativa_2;
    String area_administrativa_3;


    //Opcionales
    String StNH4Opc;
    String StpHOpc;
    String StCFOpc;
    String StDQO;
    String StEC;
    String StPO4;
    String StGYA;
    String StSD;
    String StSsed;
    String StSTT;
    String StST;
    String StSAMM;
    String StAforo;
    String StFosfatoOpc;
    String StNitratoOpc;
    String StTOpc;
    String StTurbidezOpc;
    String StSol_totalesOpc;
    String StBiodiversidadOPc;


    RelativeLayout loading_page;

    String fecha;
    String objId;

    //Location de Google
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    String LatitudGoogle;
    String LongitudGoogle;
    String AltitudGoogle;

    RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar);
        //getSupportActionBar().setTitle(R.string.title_activity_agregar);
        //se agrega el boton de ir atras
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        loading_page = (RelativeLayout) findViewById(R.id.loadingPanel);
        loading_page.setVisibility(View.VISIBLE);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        cargarDatos();


        //hacer el texto de usuario con el correo del usuario
        SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        correo = prefs.getString("correo", "No definido");
        usuario.setText(correo);
        usuario.setFocusable(false);


        //inicialización del spinner para la eleccion del Kit utilizado
        adapterKit = ArrayAdapter.createFromResource(this, R.array.nombre_kits, android.R.layout.simple_spinner_item);
        adapterKit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKit.setAdapter(adapterKit);

        // se pide permisos para trabajar
        requestPermission();
        //se inicializa el GoogleApiClient para pedir latitud y longitud
        buildGoogleApiClient();

        //Inicializa el boton para escoger la fecha con el calendario
        btnDatePicker.setOnClickListener(this);

        //inicialización del spinner para la eleccion del índice utilizado
        adapter = ArrayAdapter.createFromResource(this, R.array.nombre_indices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        //Controles sobre la elección del indice
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Cosas que necesito meter.
                if (position == 0) {
                    sin_campos();
                    if (content_obligatorios.isExpanded()) {
                        content_obligatorios.initLayout();
                        content_obligatorios.expand();
                    } else {
                        content_obligatorios.initLayout();
                    }
                } else if (position == 1) {
                    campos_Holandes();
                    content_opcionales.initLayout();
                    content_obligatorios.initLayout();
                    content_obligatorios.expand();
                } else if (position == 2) {
                    campos_NSF();
                    content_opcionales.initLayout();
                    content_obligatorios.initLayout();
                    content_obligatorios.expand();
                } else if (position == 3) {
                    campos_GLOBAL();
                    content_opcionales.initLayout();
                    content_obligatorios.initLayout();
                    content_obligatorios.expand();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        flag = false;
        Intent intent = getIntent();
        Bundle extras= intent.getExtras();
        if(extras != null){
            objId = extras.getString("objId"); // objId es el id del elementro dentro de la BD
            fecha = extras.getString("fecha");
            flag = true;
            populateView(objId);//cargar de datos
        }

        //control de los expandables Layout
        generales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content_generales.toggle();
            }
        });
        obligatorios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content_obligatorios.toggle();
            }
        });
        opcionales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content_opcionales.toggle();
            }
        });

        boton_agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (verificar_datosObligatorios()) {
                    indice = spinner.getSelectedItem().toString();
                    switch (indice) {
                        case "Índice Holandés":
                            if (verificar_Holandes()) {
                                if (verificar_GeoLocation()) {
                                    valores_opcionales();
                                    StpHOpc = etpHOpc.getText().toString();
                                    StCFOpc = etCFOpc.getText().toString();
                                    StFosfatoOpc = FosfatoOpc.getText().toString();
                                    StNitratoOpc = NitratoOpc.getText().toString();
                                    StTOpc = TOpc.getText().toString();
                                    StTurbidezOpc = TurbidezOpc.getText().toString();
                                    StSol_totalesOpc = Sol_totalesOpc.getText().toString();
                                    StBiodiversidadOPc = BiodiversidadOpc.getText().toString();
                                    enviar_Holandes();
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.mensaje_error_GeoLocation, Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), R.string.mensaje_error_Holandes, Toast.LENGTH_SHORT).show();
                            }

                            break;
                        case "Índice NSF":
                            if (verificar_NSF()) {
                                if (verificar_GeoLocation()) {
                                    valores_opcionales();
                                    StNH4Opc = etNH4Opc.getText().toString();
                                    StBiodiversidadOPc = BiodiversidadOpc.getText().toString();
                                    enviar_NSF();
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.mensaje_error_GeoLocation, Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), R.string.mensaje_error_NSF, Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case "Índice BMWP-CR":
                            if (verificar_GLOBAL()) {
                                if (verificar_GeoLocation()) {
                                    valores_opcionales();
                                    StNH4Opc = etNH4Opc.getText().toString();
                                    enviar_GLOBAL();
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.mensaje_error_GeoLocation, Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), R.string.mensaje_error_GLOBAL, Toast.LENGTH_SHORT).show();
                            }
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), R.string.mensaje_error_indice, Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.mensaje_error_requeridos, Toast.LENGTH_SHORT).show();
                }


            }
        });

        loading_page.setVisibility(View.GONE);

    }



    private boolean verificar_GeoLocation() {
        country = edit_country.getText().toString();
        area_administrativa_3 = edit_area_admin_3.getText().toString();
        area_administrativa_2 = edit_area_admin_2.getText().toString();
        area_administrativa_1 = edit_area_admin_1.getText().toString();

        if(country.equals("")  && area_administrativa_3.equals("") && area_administrativa_2.equals("") && area_administrativa_1.equals("") ){
            return false;
        }else {
            if(country.equals("")){
                country = "";
            }else if(area_administrativa_3.equals("")){
                area_administrativa_3 = "";
            }
            else if(area_administrativa_2.equals("")){
                area_administrativa_2 = "";
            }
            else if(area_administrativa_1.equals("")){
                area_administrativa_1 ="";
            }
            return true;
        }
    }

    private void goMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void populateView(String objId) {
        Response.Listener<String> responseListener = new Response.Listener<String>() { //Respuesta del servidor
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("tagconvertstr", "[" + response + "]");
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) { //Si salió bien pone los valores en los editText
                        JSONObject jsonObject = jsonResponse.getJSONObject("documentos");
                        setData(jsonObject);
                    } else { // Si salio mal, le indica al usuario que salio mal y lo manda a la activityMain
                        loading_page.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), getString(R.string.No_ID), Toast.LENGTH_LONG).show();
                        goMainScreen();
                    }
                } catch (JSONException e) {
                    loading_page.setVisibility(View.GONE);
                    e.printStackTrace();
                    goMainScreen();
                }
            }
        };


        //inserta los datos a un Map para que se envien como parametros a la función que envia al servidor.
        Map<String, String> params;
        params = new HashMap<>();
        params.put("_id", objId);
        //Viejo = "http://192.168.138.1:8081/proyectoJavier/android/buscarID.php"
        //Servidor = getString(R.string.server)+"buscarID.php"

        String direccion = getString(R.string.server)+"buscarID.php";

        //Envia los datos al servidor
        MongoRequest loginMongoRequest = new MongoRequest(params, direccion, responseListener);
        queue = Volley.newRequestQueue(ActivityAgregar.this);
        queue.add(loginMongoRequest);


    }

    private void setData(JSONObject jsonResponse) {
        try {
            JSONObject jsonMuestra = jsonResponse.getJSONObject("Muestra");
            JSONObject jsonObligatorios = jsonMuestra.getJSONObject("obligatorios");
            JSONObject jsonOpcionales = jsonMuestra.getJSONObject("opcionales");
            JSONObject jsonPOI = jsonResponse.getJSONObject("POI");
            JSONObject jsonLocation = jsonPOI.getJSONObject("location");
            JSONObject jsonGeo = jsonPOI.getJSONObject("datos_geograficos");
            String indiceEscogido = jsonMuestra.getString("indice_usado");
            if(indiceEscogido.equals("NSF")){
                spinner.setSelection(2, true);
                //OBligatorios
                etPO2.setText(jsonObligatorios.getString("% O2"), TextView.BufferType.EDITABLE);
                etDBO.setText(jsonObligatorios.getString("DBO"), TextView.BufferType.EDITABLE);
                etCF.setText(jsonObligatorios.getString("CF"), TextView.BufferType.EDITABLE);
                etpH.setText(jsonObligatorios.getString("pH"), TextView.BufferType.EDITABLE);
                etFosfato.setText(jsonObligatorios.getString("Fosfato"), TextView.BufferType.EDITABLE);
                etNitrato.setText(jsonObligatorios.getString("Nitrato"), TextView.BufferType.EDITABLE);
                etT.setText(jsonObligatorios.getString("T"), TextView.BufferType.EDITABLE);
                etTurbidez.setText(jsonObligatorios.getString("Turbidez"), TextView.BufferType.EDITABLE);
                etSol_totales.setText(jsonOpcionales.getString("Sol_totales"), TextView.BufferType.EDITABLE);
                //opcionales
                if(!jsonOpcionales.getString("NH4").equals("ND")){
                    etNH4Opc.setText(jsonOpcionales.getString("NH4"), TextView.BufferType.EDITABLE);
                }
                if(!jsonOpcionales.getString("Biodiversidad").equals("ND")){
                    BiodiversidadOpc.setText(jsonOpcionales.getString("Biodiversidad"), TextView.BufferType.EDITABLE);
                }
            }else if(indiceEscogido.equals("BMWP-CR")){
                spinner.setSelection(3, true);
                //OBligatorios
                etPO2.setText(jsonObligatorios.getString("% O2"), TextView.BufferType.EDITABLE);
                etDBO.setText(jsonObligatorios.getString("DBO"), TextView.BufferType.EDITABLE);
                etCF.setText(jsonObligatorios.getString("CF"), TextView.BufferType.EDITABLE);
                etpH.setText(jsonObligatorios.getString("pH"), TextView.BufferType.EDITABLE);
                etFosfato.setText(jsonObligatorios.getString("Fosfato"), TextView.BufferType.EDITABLE);
                etNitrato.setText(jsonObligatorios.getString("Nitrato"), TextView.BufferType.EDITABLE);
                etT.setText(jsonObligatorios.getString("T"), TextView.BufferType.EDITABLE);
                etTurbidez.setText(jsonObligatorios.getString("Turbidez"), TextView.BufferType.EDITABLE);
                etSol_totales.setText(jsonOpcionales.getString("Sol_totales"), TextView.BufferType.EDITABLE);
                etBiodiversidad.setText(jsonOpcionales.getString("Biodiversidad"), TextView.BufferType.EDITABLE);
                //opcionales
                if(!jsonOpcionales.getString("NH4").equals("ND")){
                    etNH4Opc.setText(jsonOpcionales.getString("NH4"), TextView.BufferType.EDITABLE);
                }
            }else if(indiceEscogido.equals("Holandés")){
                spinner.setSelection(1, true);
                //OBligatorios
                etPO2.setText(jsonObligatorios.getString("% O2"), TextView.BufferType.EDITABLE);
                etDBO.setText(jsonObligatorios.getString("DBO"), TextView.BufferType.EDITABLE);
                etNH4.setText(jsonObligatorios.getString("NH4"), TextView.BufferType.EDITABLE);
                //opcionales
                if(!jsonOpcionales.getString("CF").equals("ND")){
                    etCFOpc.setText(jsonOpcionales.getString("CF"), TextView.BufferType.EDITABLE);
                }
                if(!jsonOpcionales.getString("pH").equals("ND")){
                    etpHOpc.setText(jsonOpcionales.getString("pH"), TextView.BufferType.EDITABLE);
                }
                if(!jsonOpcionales.getString("Fosfato").equals("ND")){
                    FosfatoOpc.setText(jsonOpcionales.getString("Fosfato"), TextView.BufferType.EDITABLE);
                }
                if(!jsonOpcionales.getString("Nitrato").equals("ND")){
                    NitratoOpc.setText(jsonOpcionales.getString("Nitrato"), TextView.BufferType.EDITABLE);
                }
                if(!jsonOpcionales.getString("T").equals("ND")){
                    TOpc.setText(jsonOpcionales.getString("T"), TextView.BufferType.EDITABLE);
                }
                if(!jsonOpcionales.getString("Turbidez").equals("ND")){
                    TurbidezOpc.setText(jsonOpcionales.getString("Turbidez"), TextView.BufferType.EDITABLE);
                }
                if(!jsonOpcionales.getString("Sol_totales").equals("ND")){
                    Sol_totalesOpc.setText(jsonOpcionales.getString("Sol_totales"), TextView.BufferType.EDITABLE);
                }
                if(!jsonOpcionales.getString("Biodiversidad").equals("ND")){
                    BiodiversidadOpc.setText(jsonOpcionales.getString("Biodiversidad"), TextView.BufferType.EDITABLE);
                }
            }

            //Datos en muestra.
            Nomb_Institucion.setText(jsonPOI.getString("nombre_institucion"), TextView.BufferType.EDITABLE);
            Nomb_estacion.setText(jsonPOI.getString("nombre_estacion"), TextView.BufferType.EDITABLE);
            txtDate.setText(fecha, TextView.BufferType.EDITABLE);
            String kitEscogido = jsonPOI.getString("kit_desc");
            if(kitEscogido.equals("LMRHI-UNA")){
                spinnerKit.setSelection(1, true);
            }else if(kitEscogido.equals("LaMotte Deluxe")){
                spinnerKit.setSelection(2, true);
            }else if(kitEscogido.equals("LaMotte Complete")){
                spinnerKit.setSelection(3, true);
            }else if(kitEscogido.equals("LaMotte Earth Force")){
                spinnerKit.setSelection(3, true);
            }else if(kitEscogido.equals("LaMotte Kit de Aula")){
                spinnerKit.setSelection(4, true);
            }else if(kitEscogido.equals("Otro")){
                spinnerKit.setSelection(5, true);
            }
            editLatitud.setText(jsonLocation.getString("lat"), TextView.BufferType.EDITABLE);
            editLongitud.setText(jsonLocation.getString("lng"), TextView.BufferType.EDITABLE);
            editAltitud.setText(jsonGeo.getString("alt"), TextView.BufferType.EDITABLE);
            edit_country.setText(jsonGeo.getString("pais"), TextView.BufferType.EDITABLE);
            edit_area_admin_1.setText(jsonGeo.getString("area_administrativa_1"), TextView.BufferType.EDITABLE);
            edit_area_admin_2.setText(jsonGeo.getString("area_administrativa_2"), TextView.BufferType.EDITABLE);
            edit_area_admin_3.setText(jsonGeo.getString("area_administrativa_3"), TextView.BufferType.EDITABLE);
            if(!jsonMuestra.getString("temp_agua").equals("ND")){
                editTemperatura.setText(jsonMuestra.getString("temp_agua"), TextView.BufferType.EDITABLE);
            }
            if(!jsonMuestra.getString("area_cauce_rio").equals("ND")){
                editAreaCauce.setText(jsonMuestra.getString("area_cauce_rio"), TextView.BufferType.EDITABLE);
            }
            if(!jsonMuestra.getString("velocidad_agua").equals("ND")){
                editVelocidad.setText(jsonMuestra.getString("velocidad_agua"), TextView.BufferType.EDITABLE);
            }
            if(!jsonOpcionales.getString("DQO").equals("ND")){
                DQO.setText(jsonOpcionales.getString("DQO"), TextView.BufferType.EDITABLE);
            }
            if(!jsonOpcionales.getString("EC").equals("ND")){
                EC.setText(jsonOpcionales.getString("EC"), TextView.BufferType.EDITABLE);
            }
            if(!jsonOpcionales.getString("PO4").equals("ND")){
                PO4.setText(jsonOpcionales.getString("PO4"), TextView.BufferType.EDITABLE);
            }
            if(!jsonOpcionales.getString("GYA").equals("ND")){
                GYA.setText(jsonOpcionales.getString("GYA"), TextView.BufferType.EDITABLE);
            }
            if(!jsonOpcionales.getString("SD").equals("ND")){
                SD.setText(jsonOpcionales.getString("SD"), TextView.BufferType.EDITABLE);
            }
            if(!jsonOpcionales.getString("Ssed").equals("ND")){
                Ssed.setText(jsonOpcionales.getString("Ssed"), TextView.BufferType.EDITABLE);
            }
            if(!jsonOpcionales.getString("SST").equals("ND")){
                STT.setText(jsonOpcionales.getString("SST"), TextView.BufferType.EDITABLE);
            }
            if(!jsonOpcionales.getString("SAAM").equals("ND")){
                SAMM.setText(jsonOpcionales.getString("SAAM"), TextView.BufferType.EDITABLE);
            }
            if(!jsonOpcionales.getString("Aforo").equals("ND")){
                Aforo.setText(jsonOpcionales.getString("Aforo"), TextView.BufferType.EDITABLE);
            }
            if(!jsonOpcionales.getString("ST").equals("ND")){
                ST.setText(jsonOpcionales.getString("ST"), TextView.BufferType.EDITABLE);
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * @return
     * Verifica todos los campos que son necesarios poder agregar el documento
     * si está bien devuelve true, sino false para que no se deje agregar.
     */
    private boolean verificar_datosObligatorios() {
        StNombInstitucion = Nomb_Institucion.getText().toString();
        StNombEstacion = Nomb_estacion.getText().toString();
        StEditLatitud = editLatitud.getText().toString();
        StEditLongitud = editLongitud.getText().toString();
        SteditAltitud = editAltitud.getText().toString();
        //SteditCod_Prov = editCod_Prov.getText().toString();
        //SteditCod_Cant = editCod_Cant.getText().toString();
        //SteditCod_Dist = editCod_Dist.getText().toString();
        //SteditCod_Rio = editCod_Rio.getText().toString();
        StFecha = txtDate.getText().toString();

        Stkit = spinnerKit.getSelectedItem().toString();

        if (!Stkit.equals("Kit") && !StNombInstitucion.equals("") && !StNombEstacion.equals("") && !StEditLatitud.equals("") && !StEditLongitud.equals("") && !SteditAltitud.equals("") &&
                !StFecha.equals("")){
                // && !SteditCod_Prov.equals("") && !SteditCod_Cant.equals("") && !SteditCod_Dist.equals("") && !SteditCod_Rio.equals("") ) {
            return true;
        } else {
            return false;
        }


    }

    /**
     * @return
     * Verifica que los campos obligatorios del índice NSF estén con datos.
     * Si no devuelve false para que no se pueda agregar el documento
     */
    private boolean verificar_NSF() {
        StPO2 = etPO2.getText().toString();
        StDBO = etDBO.getText().toString();
        StCF = etCF.getText().toString();
        StpH = etpH.getText().toString();
        StFosfato = etFosfato.getText().toString();
        StNitrato = etNitrato.getText().toString();
        StT = etT.getText().toString();
        StTurbidez = etTurbidez.getText().toString();
        StSol_totales = etSol_totales.getText().toString();

        if (!StPO2.equals("") && !StDBO.equals("") && !StCF.equals("") && !StpH.equals("") && !StFosfato.equals("") &&
                !StNitrato.equals("") && !StT.equals("") && !StTurbidez.equals("") && !StSol_totales.equals("")) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * @return
     * Verifica que los campos obligatorios del índice Holandés estén con datos.
     * Si no devuelve false para que no se pueda agregar el documento
     */
    private boolean verificar_Holandes() {
        StPO2 = etPO2.getText().toString();
        StDBO = etDBO.getText().toString();
        StNH4 = etNH4.getText().toString();

        if (!StPO2.equals("") && !StDBO.equals("") && !StNH4.equals("")) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * @return
     * Verifica que los campos obligatorios del índice GLOBAL estén con datos.
     * Si no devuelve false para que no se pueda agregar el documento
     */
    private boolean verificar_GLOBAL() {
        StPO2 = etPO2.getText().toString();
        StDBO = etDBO.getText().toString();
        StCF = etCF.getText().toString();
        StpH = etpH.getText().toString();
        StFosfato = etFosfato.getText().toString();
        StNitrato = etNitrato.getText().toString();
        StT = etT.getText().toString();
        StTurbidez = etTurbidez.getText().toString();
        StSol_totales = etSol_totales.getText().toString();
        StBiodiversidad = etBiodiversidad.getText().toString();

        if (!StPO2.equals("") && !StDBO.equals("") && !StCF.equals("") && !StpH.equals("") && !StFosfato.equals("") &&
                !StNitrato.equals("") && !StT.equals("") && !StTurbidez.equals("") && !StSol_totales.equals("") && !StBiodiversidad.equals("")) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * @param v
     * hace el proceso para escoger la fecha desde el calendario y la escribe en el campo de fecha
     */
    @Override
    public void onClick(View v) {

        if (v == btnDatePicker) {

            // Get Current Date
            final Calendar c = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" +  year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        }
    }


    /**
     * Toma todos los datos opcionales para ser agregados al documento.
     */
    private void valores_opcionales() {
        SteditTemperatura = editTemperatura.getText().toString();
        SteditAreaCauce = editAreaCauce.getText().toString();
        SteditVelocidad = editVelocidad.getText().toString();

        //Opcionales
        StDQO = DQO.getText().toString();
        StEC = EC.getText().toString();
        StPO4 = PO4.getText().toString();
        StGYA = GYA.getText().toString();
        StSD = SD.getText().toString();
        StSsed = Ssed.getText().toString();
        StSTT = STT.getText().toString();
        StST = ST.getText().toString();
        StSAMM = SAMM.getText().toString();
        StAforo = Aforo.getText().toString();


    }

    private void cargarDatos() {
        DQO = (EditText) findViewById(R.id.DQO);
        EC = (EditText) findViewById(R.id.EC);
        PO4 = (EditText) findViewById(R.id.PO4);
        GYA = (EditText) findViewById(R.id.GYA);
        SD = (EditText) findViewById(R.id.SD);
        Ssed = (EditText) findViewById(R.id.Ssed);
        STT = (EditText) findViewById(R.id.STT);
        ST = (EditText) findViewById(R.id.ST);
        SAMM = (EditText) findViewById(R.id.SAMM);
        Aforo = (EditText) findViewById(R.id.Aforo);



        //Expandable Layout
        usuario = (EditText) findViewById(R.id.Usuario);
        generales = (RelativeLayout) findViewById(R.id.generales);
        obligatorios = (RelativeLayout) findViewById(R.id.obligatorios);
        opcionales = (RelativeLayout) findViewById(R.id.opcionales);
        content_generales = (ExpandableLinearLayout) findViewById(R.id.generales_exp);
        content_obligatorios = (ExpandableLinearLayout) findViewById(R.id.obligatorios_exp);
        content_opcionales = (ExpandableLinearLayout) findViewById(R.id.opcionales_exp);
        //Obligatorios
        etPO2 = (EditText) findViewById(R.id.PO2);
        etDBO = (EditText) findViewById(R.id.DBO);
        etNH4 = (EditText) findViewById(R.id.NH4);
        etCF = (EditText) findViewById(R.id.CF);
        etpH = (EditText) findViewById(R.id.pH);
        etFosfato = (EditText) findViewById(R.id.Fosfato);
        etNitrato = (EditText) findViewById(R.id.Nitrato);
        etT = (EditText) findViewById(R.id.T);
        etTurbidez = (EditText) findViewById(R.id.Turbidez);
        etSol_totales = (EditText) findViewById(R.id.Sol_totales);
        etBiodiversidad = (EditText) findViewById(R.id.Biodiversidad);

        //Datos Generales
        Nomb_Institucion = (EditText) findViewById(R.id.Nomb_Institucion);
        Nomb_estacion = (EditText) findViewById(R.id.Nomb_estacion);
        editLatitud = (EditText) findViewById(R.id.editLatitud);
        editLongitud = (EditText) findViewById(R.id.editLongitud);
        editAltitud = (EditText) findViewById(R.id.editAltitud);
        edit_country = (EditText) findViewById(edit_pais);
        edit_area_admin_1 = (EditText) findViewById(edit_area_adminis_1);
        edit_area_admin_2 = (EditText) findViewById(edit_area_adminis_2);
        edit_area_admin_3 = (EditText) findViewById(edit_area_adminis_3);
        editTemperatura = (EditText) findViewById(R.id.editTemperatura);
        editAreaCauce = (EditText) findViewById(R.id.editAreaCauce);
        editVelocidad = (EditText) findViewById(R.id.editVelocidad);
        txtDate = (EditText) findViewById(R.id.in_date);

        //Opcionales
        etNH4Opc = (EditText) findViewById(NH4Opc);
        etpHOpc = (EditText) findViewById(pHOpc);
        etCFOpc = (EditText) findViewById(CFOpc);
        FosfatoOpc = (EditText) findViewById(R.id.FosfatoOpc);
        NitratoOpc = (EditText) findViewById(R.id.NitratoOpc);
        TOpc = (EditText) findViewById(R.id.TOpc);
        TurbidezOpc = (EditText) findViewById(R.id.TurbidezOpc);
        Sol_totalesOpc = (EditText) findViewById(R.id.Sol_totalesOpc);
        BiodiversidadOpc = (EditText) findViewById(R.id.BiodiversidadOpc);


        // control sobre el boton agregar.
        boton_agregar = (Button) findViewById(R.id.boton_agregar);
        //inicialización del spinner para la eleccion del Kit utilizado
        spinnerKit = (Spinner) findViewById(R.id.spinner_Kit);
        //Inicializa el boton para escoger la fecha con el calendario
        btnDatePicker = (Button) findViewById(R.id.btn_date);
        //inicialización del spinner para la eleccion del índice utilizado
        spinner = (Spinner) findViewById(R.id.spinner_indice);

    }


    /**
     * Método que toma todos los datos y los envia al servidor para ingresar el documento a la base de datos para el índice NSF
     */
    private void enviar_NSF() {
        loading_page = (RelativeLayout) findViewById(R.id.loadingPanel);
        loading_page.setVisibility(View.VISIBLE);

        Response.Listener<String> responseListener = new Response.Listener<String>() { //Respuesta del servidor
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("tagconvertstr", "[" + response + "]");
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) { //Si salió bien le enseña al usuario el valor calculado del indice y el color y vuelve a crear el activity para que pueda ingresar otro dato
                        String texto = getString(R.string.documento_exito) + "\nÍndice utilizado= " + jsonResponse.getString("indice") + "\nResultado del índice= " + jsonResponse.getDouble("valor") +
                                "\nColor= " + jsonResponse.getString("color");
                        Intent intent = new Intent(ActivityAgregar.this, ActivityAgregar.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        loading_page.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), texto, Toast.LENGTH_SHORT).show();
                        ActivityAgregar.this.startActivity(intent);
                    } else { // Si salio mal, le indica al usuario que salio mal y le deja volver a intentarlo
                        loading_page.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), getString(R.string.documento_fallido), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    loading_page.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        };


        //inserta los datos a un Map para que se envien como parametros a la función que envia al servidor.
        Map<String, String> params;
        params = new HashMap<>();
        String flagS = flag.toString();
        params.put("flag", flagS);
        if(flagS.equals("true")){
            params.put("obj_id", objId);
        }
        params.put("usuario", correo);
        params.put("Indice", "NSF");
        params.put("temp_agua", SteditTemperatura);
        params.put("velocidad_agua", SteditVelocidad);
        params.put("area_cauce_rio", SteditAreaCauce);
        params.put("PO2", StPO2);
        params.put("DBO", StDBO);
        params.put("CF", StCF);
        params.put("pH", StpH);
        params.put("Fosfato", StFosfato);
        params.put("Nitrato", StNitrato);
        params.put("T", StT);
        params.put("Turbidez", StTurbidez);
        params.put("Sol_totales", StSol_totales);
        params.put("DQO", StDQO);
        params.put("EC", StEC);
        params.put("PO4", StPO4);
        params.put("GYA", StGYA);
        params.put("SD", StSD);
        params.put("Ssed", StSsed);
        params.put("SST", StSTT);
        params.put("SAAM", StSAMM);
        params.put("Aforo", StAforo);
        params.put("Biodiversidad", StBiodiversidadOPc);
        params.put("ST", StST);
        if (StNH4Opc == null) {
            params.put("NH4", "");
        } else {
            params.put("NH4", StNH4Opc);
        }

        params.put("nombre_institucion", StNombInstitucion);
        params.put("nombre_estacion", StNombEstacion);
        params.put("fecha", StFecha);
        params.put("kit_desc", Stkit);
        params.put("lat", StEditLatitud);
        params.put("lng", StEditLongitud);
        params.put("alt", SteditAltitud);
        params.put("country", country);
        params.put("area_admin_1", area_administrativa_1);
        params.put("area_admin_2", area_administrativa_2);
        params.put("area_admin_3", area_administrativa_3);


        //Viejo = "http://192.168.138.1:8081/proyectoJavier/android/insertarNSF.php"
        //Servidor = getString(R.string.server)+"insertarNSF.php"
        String direccion;
        direccion = getString(R.string.server)+"insertarNSF.php";


        //Envia los datos al servidor
        MongoRequest loginMongoRequest = new MongoRequest(params, direccion, responseListener);
        queue = Volley.newRequestQueue(ActivityAgregar.this);
        queue.add(loginMongoRequest);


    }

    /**
     * Método que toma todos los datos y los envia al servidor para ingresar el documento a la base de datos para el índice Global
     */
    private void enviar_GLOBAL() {
        loading_page = (RelativeLayout) findViewById(R.id.loadingPanel);
        loading_page.setVisibility(View.VISIBLE);

        Response.Listener<String> responseListener = new Response.Listener<String>() { //Respuesta del servidor
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("tagconvertstr", "[" + response + "]");
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) { //Si salió bien le enseña al usuario el valor calculado del indice y el color y vuelve a crear el activity para que pueda ingresar otro dato
                        String texto = getString(R.string.documento_exito) + "\nÍndice utilizado= " + jsonResponse.getString("indice") + "\nResultado del índice= " + jsonResponse.getDouble("valor") +
                                "\nColor= " + jsonResponse.getString("color");
                        Intent intent = new Intent(ActivityAgregar.this, ActivityAgregar.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        loading_page.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), texto, Toast.LENGTH_SHORT).show();
                        ActivityAgregar.this.startActivity(intent);
                    } else { // Si salio mal, le indica al usuario que salio mal y le deja volver a intentarlo
                        loading_page.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), getString(R.string.documento_fallido), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    loading_page.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        };


        //inserta los datos a un Map para que se envien como parametros a la función que envia al servidor.
        Map<String, String> params;
        params = new HashMap<>();
        String flagS = flag.toString();
        params.put("flag", flagS);
        params.put("usuario", correo);
        if(flagS.equals("true")){
            params.put("obj_id", objId);
        }
        params.put("Indice", "BMWP-CR");
        params.put("temp_agua", SteditTemperatura);
        params.put("velocidad_agua", SteditVelocidad);
        params.put("area_cauce_rio", SteditAreaCauce);
        params.put("PO2", StPO2);
        params.put("DBO", StDBO);
        params.put("CF", StCF);
        params.put("pH", StpH);
        params.put("Fosfato", StFosfato);
        params.put("Nitrato", StNitrato);
        params.put("T", StT);
        params.put("Turbidez", StTurbidez);
        params.put("Sol_totales", StSol_totales);
        params.put("Biodiversidad", StBiodiversidad);
        params.put("DQO", StDQO);
        params.put("EC", StEC);
        params.put("PO4", StPO4);
        params.put("GYA", StGYA);
        params.put("SD", StSD);
        params.put("Ssed", StSsed);
        params.put("SST", StSTT);
        params.put("SAAM", StSAMM);
        params.put("Aforo", StAforo);
        params.put("ST", StST);
        if (StNH4Opc == null) {
            params.put("NH4", "");
        } else {
            params.put("NH4", StNH4Opc);
        }

        params.put("nombre_institucion", StNombInstitucion);
        params.put("nombre_estacion", StNombEstacion);
        params.put("fecha", StFecha);
        params.put("kit_desc", Stkit);
        params.put("lat", StEditLatitud);
        params.put("lng", StEditLongitud);
        params.put("alt", SteditAltitud);
        params.put("country", country);
        params.put("area_admin_1", area_administrativa_1);
        params.put("area_admin_2", area_administrativa_2);
        params.put("area_admin_3", area_administrativa_3);


        //Viejo = "http://192.168.138.1:8081/proyectoJavier/android/insertarNSF.php"
        //Servidor = getString(R.string.server)+"insertarNSF.php"
        String direccion;
        direccion = getString(R.string.server)+"insertarGLOBAL.php";


        //Envia los datos al servidor
        MongoRequest loginMongoRequest = new MongoRequest(params, direccion, responseListener);
        queue = Volley.newRequestQueue(ActivityAgregar.this);
        queue.add(loginMongoRequest);


    }


    /**
     * Método que toma todos los datos y los envia al servidor para ingresar el documento a la base de datos para el índice Holandés
     */
    private void enviar_Holandes() {
        loading_page = (RelativeLayout) findViewById(R.id.loadingPanel);
        loading_page.setVisibility(View.VISIBLE);
        Response.Listener<String> responseListener = new Response.Listener<String>() {//Respuesta del servidor
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("tagconvertstr", "[" + response + "]");
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {//Si salió bien le enseña al usuario el valor calculado del indice y el color y vuelve a crear el activity para que pueda ingresar otro dato
                        String texto = getString(R.string.documento_exito) + "\nÍndice utilizado= " + jsonResponse.getString("indice") + "\nResultado del índice= " + jsonResponse.getDouble("valor") +
                                "\nColor= " + jsonResponse.getString("color");
                        Intent intent = new Intent(ActivityAgregar.this, ActivityAgregar.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        loading_page.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), texto, Toast.LENGTH_SHORT).show();
                        ActivityAgregar.this.startActivity(intent);
                    } else { // Si salio mal, le indica al usuario que salio mal y le deja volver a intentarlo
                        loading_page.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), getString(R.string.documento_fallido), Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    loading_page.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        };

        //inserta los datos a un Map para que se envien como parametros a la función que envia al servidor.
        Map<String, String> params;
        params = new HashMap<>();
        String flagS = flag.toString();
        params.put("flag", flagS);
        if(flagS.equals("true")){
            params.put("obj_id", objId);
        }
        params.put("usuario", correo);
        params.put("Indice", "Holandés");
        params.put("temp_agua", SteditTemperatura);
        params.put("velocidad_agua", SteditVelocidad);
        params.put("area_cauce_rio", SteditAreaCauce);
        params.put("PO2", StPO2);
        params.put("DBO", StDBO);
        params.put("NH4", StNH4);
        params.put("DQO", StDQO);
        params.put("EC", StEC);
        params.put("PO4", StPO4);
        params.put("GYA", StGYA);
        params.put("SD", StSD);
        params.put("Ssed", StSsed);
        params.put("SST", StSTT);
        params.put("SAAM", StSAMM);
        params.put("T", StTOpc);
        params.put("Aforo", StAforo);
        params.put("Biodiversidad", StBiodiversidadOPc);
        params.put("ST", StST);
        if (StCFOpc == null) {
            params.put("CF", "");
        } else {
            params.put("CF", StCFOpc);
        }
        if (StpHOpc == null) {
            params.put("pH", "");
        } else {
            params.put("pH", StpHOpc);
        }
        params.put("Fosfato", StFosfatoOpc);
        params.put("Nitrato", StNitratoOpc);
        params.put("Turbidez", StTurbidezOpc);
        params.put("Sol_totales", StSol_totalesOpc);
        params.put("nombre_institucion", StNombInstitucion);
        params.put("nombre_estacion", StNombEstacion);
        params.put("fecha", StFecha);
        params.put("kit_desc", Stkit);
        params.put("lat", StEditLatitud);
        params.put("lng", StEditLongitud);
        params.put("alt", SteditAltitud);
        params.put("country", country);
        params.put("area_admin_1", area_administrativa_1);
        params.put("area_admin_2", area_administrativa_2);
        params.put("area_admin_3", area_administrativa_3);

        //Viejo = http://192.168.138.1:8081/proyectoJavier/android/insertarHolandes.php
        //Servidor = getString(R.string.server)+"insertarHolandes.php"

        String direccion;

        direccion = getString(R.string.server)+"insertarHolandes.php";


        //Envia los datos al servidor
        MongoRequest loginMongoRequest = new MongoRequest(params, direccion, responseListener);
        queue = Volley.newRequestQueue(ActivityAgregar.this);
        queue.add(loginMongoRequest);
    }


    /**
     * Si se escoge el índice NSF en el spinner enseña los datos requeridos y oculta el de los demás índices
     */
    private void campos_NSF() {
        etPO2.setVisibility(View.VISIBLE);
        etDBO.setVisibility(View.VISIBLE);
        etCF.setVisibility(View.VISIBLE);
        etpH.setVisibility(View.VISIBLE);
        etNH4.setVisibility(View.GONE);
        etFosfato.setVisibility(View.VISIBLE);
        etNitrato.setVisibility(View.VISIBLE);
        etT.setVisibility(View.VISIBLE);
        etTurbidez.setVisibility(View.VISIBLE);
        etSol_totales.setVisibility(View.VISIBLE);
        etBiodiversidad.setVisibility(View.GONE);

        //Opcionales
        etNH4Opc.setVisibility(View.VISIBLE);
        BiodiversidadOpc.setVisibility(View.VISIBLE);
        etCFOpc.setVisibility(View.GONE);
        etpHOpc.setVisibility(View.GONE);
        FosfatoOpc.setVisibility(View.GONE);
        NitratoOpc.setVisibility(View.GONE);
        TOpc.setVisibility(View.GONE);
        TurbidezOpc.setVisibility(View.GONE);
        Sol_totalesOpc.setVisibility(View.GONE);
    }

    private void campos_GLOBAL() {
        etPO2.setVisibility(View.VISIBLE);
        etDBO.setVisibility(View.VISIBLE);
        etCF.setVisibility(View.VISIBLE);
        etpH.setVisibility(View.VISIBLE);
        etNH4.setVisibility(View.GONE);
        etFosfato.setVisibility(View.VISIBLE);
        etNitrato.setVisibility(View.VISIBLE);
        etT.setVisibility(View.VISIBLE);
        etTurbidez.setVisibility(View.VISIBLE);
        etSol_totales.setVisibility(View.VISIBLE);
        etBiodiversidad.setVisibility(View.VISIBLE);

        //Opcionales
        etNH4Opc.setVisibility(View.VISIBLE);
        etCFOpc.setVisibility(View.GONE);
        etpHOpc.setVisibility(View.GONE);
        FosfatoOpc.setVisibility(View.GONE);
        NitratoOpc.setVisibility(View.GONE);
        TOpc.setVisibility(View.GONE);
        TurbidezOpc.setVisibility(View.GONE);
        Sol_totalesOpc.setVisibility(View.GONE);
        BiodiversidadOpc.setVisibility(View.GONE);
    }

    /**
     * Mientras que no haya escogido un índice, no se enseña los datos.
     */
    private void sin_campos() {
        etPO2.setText("");
        etDBO.setText("");
        etNH4.setText("");
        etCF.setText("");
        etpH.setText("");
        etFosfato.setText("");
        etNitrato.setText("");
        etT.setText("");
        etTurbidez.setText("");
        etSol_totales.setText("");
        etBiodiversidad.setText("");

        etPO2.setVisibility(View.GONE);
        etDBO.setVisibility(View.GONE);
        etNH4.setVisibility(View.GONE);
        etCF.setVisibility(View.GONE);
        etpH.setVisibility(View.GONE);
        etFosfato.setVisibility(View.GONE);
        etNitrato.setVisibility(View.GONE);
        etT.setVisibility(View.GONE);
        etTurbidez.setVisibility(View.GONE);
        etSol_totales.setVisibility(View.GONE);
        etBiodiversidad.setVisibility(View.GONE);

        //opcionales
        etCFOpc.setVisibility(View.GONE);
        etpHOpc.setVisibility(View.GONE);
        etNH4Opc.setVisibility(View.GONE);
        FosfatoOpc.setVisibility(View.GONE);
        NitratoOpc.setVisibility(View.GONE);
        TOpc.setVisibility(View.GONE);
        TurbidezOpc.setVisibility(View.GONE);
        Sol_totalesOpc.setVisibility(View.GONE);
        BiodiversidadOpc.setVisibility(View.GONE);

        content_opcionales.initLayout();
    }

    /**
     * Si se escoge el índice Holandés en el spinner enseña los datos requeridos y oculta el de los demás índices
     */
    private void campos_Holandes() {
        etPO2.setVisibility(View.VISIBLE);
        etDBO.setVisibility(View.VISIBLE);
        etNH4.setVisibility(View.VISIBLE);
        etCF.setVisibility(View.GONE);
        etpH.setVisibility(View.GONE);
        etFosfato.setVisibility(View.GONE);
        etNitrato.setVisibility(View.GONE);
        etT.setVisibility(View.GONE);
        etTurbidez.setVisibility(View.GONE);
        etSol_totales.setVisibility(View.GONE);
        etBiodiversidad.setVisibility(View.GONE);

        //Opcionales
        etCFOpc.setVisibility(View.VISIBLE);
        etpHOpc.setVisibility(View.VISIBLE);
        FosfatoOpc.setVisibility(View.VISIBLE);
        NitratoOpc.setVisibility(View.VISIBLE);
        TOpc.setVisibility(View.VISIBLE);
        TurbidezOpc.setVisibility(View.VISIBLE);
        Sol_totalesOpc.setVisibility(View.VISIBLE);
        BiodiversidadOpc.setVisibility(View.VISIBLE);
        etNH4Opc.setVisibility(View.GONE);

    }


    /**
     * @param bundle
     * Cuando conecta con el GoogleApiClient pide la latitud y longitud y los pone en los campos para ingresar.
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = ActivityAgregar.this.checkCallingOrSelfPermission(permission);
        if (res == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                LatitudGoogle = String.valueOf(mLastLocation.getLatitude());
                LongitudGoogle = String.valueOf(mLastLocation.getLongitude());
                editLatitud.setText(LatitudGoogle);
                editLongitud.setText(LongitudGoogle);
                requestAltitude(LatitudGoogle,LongitudGoogle);
                requestGeoLocation(LatitudGoogle,LongitudGoogle);
            }
        }
    }

    private void requestGeoLocation(String latitudGoogle, String longitudGoogle) {
        final Response.Listener<String> responseListener = new Response.Listener<String>() {//Respuesta del servidor
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("tagconvertstr", "[" + response + "]");
                    JSONObject jsonResponse = new JSONObject(response);
                    String success = jsonResponse.getString("status");
                    if (success.equals("OK")) {//Si salió bien le enseña al usuario el valor calculado del indice y el color y vuelve a crear el activity para que pueda ingresar otro dato
                        JSONArray jsonArray = jsonResponse.getJSONArray("results");
                        JSONObject obj = jsonArray.getJSONObject(0);
                        JSONArray jsonArrayGeo = obj.getJSONArray("address_components");

                        for (int i = 0; i < jsonArrayGeo.length(); i++) {
                            JSONObject objGeo = jsonArrayGeo.getJSONObject(i);
                            JSONArray jsonArrayType = objGeo.getJSONArray("types");
                            String tipo = jsonArrayType.getString(0);
                            if(tipo.equals("country")){
                                country = objGeo.getString("long_name");
                                edit_country.setText(country);
                            }else if(tipo.equals("administrative_area_level_1")){
                                area_administrativa_1 = objGeo.getString("long_name");
                                edit_area_admin_1.setText(area_administrativa_1);
                            }else if(tipo.equals("administrative_area_level_2")){
                                area_administrativa_2 = objGeo.getString("long_name");
                                edit_area_admin_2.setText(area_administrativa_2);
                                area_administrativa_3 = jsonArrayGeo.getJSONObject(i-1).getString("long_name");
                                edit_area_admin_3.setText(area_administrativa_3);
                            }
                        }


                    }
                    //response = null;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        //inserta los datos a un Map para que se envien como parametros a la función que envia al servidor.
        Map<String, String> params;
        params = new HashMap<>();

        String direccion = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitudGoogle + "," + longitudGoogle + "&key=" + getString(R.string.Google_Key);


        //Envia los datos al servidor
        MongoRequest loginMongoRequest = new MongoRequest(params, direccion, responseListener);
        queue = Volley.newRequestQueue(ActivityAgregar.this);
        queue.add(loginMongoRequest);
    }

    private void requestAltitude(String latitudGoogle, String longitudGoogle) {
        final Response.Listener<String> responseListener = new Response.Listener<String>() {//Respuesta del servidor
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("tagconvertstr", "[" + response + "]");
                    JSONObject jsonResponse = new JSONObject(response);
                    String success = jsonResponse.getString("status");
                    if (success.equals("OK")) {//Si salió bien le enseña al usuario el valor calculado del indice y el color y vuelve a crear el activity para que pueda ingresar otro dato

                        JSONArray jsonArray = jsonResponse.getJSONArray("results");

                            JSONObject obj = jsonArray.getJSONObject(0);


                            AltitudGoogle = obj.getString("elevation");
                            editAltitud.setText(AltitudGoogle);





                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //response = null;
            }
        };

        //inserta los datos a un Map para que se envien como parametros a la función que envia al servidor.
        Map<String, String> params;
        params = new HashMap<>();

        String direccion = "https://maps.googleapis.com/maps/api/elevation/json?locations=" + latitudGoogle + "," + longitudGoogle + "&key=" + getString(R.string.Google_Key);


        //Envia los datos al servidor
        MongoRequest loginMongoRequest = new MongoRequest(params, direccion, responseListener);
        //loginMongoRequest.setTag(12345);
        queue = Volley.newRequestQueue(ActivityAgregar.this);
        queue.add(loginMongoRequest);


    }


    /**
     * inicializa el GoogleApiClient.
     */
    private void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(AppIndex.API).build();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getApplicationContext(), "Conexión suspendida", Toast.LENGTH_SHORT).show();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "Conexión fallida = " + connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();

    }

    /**
     * Metodo encargado de pedir que se le conceda a la aplicacion ciertos permisos.
     */
    private void requestPermission() {
        //Preguntar por permiso
        ActivityCompat.requestPermissions(this, StringPermisos, 0);
    }


    /**
     * Verifica que tenga los permisos apropiados para acceder a la ubicación de usuario.
     *
     * @param  requestCode  codigo del permiso
     * @param  permissions  los permisos que se solicitan
     * @param  grantResults  indica si permiso es concedido o no
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //se crea bien
                } else {
                    Toast.makeText(this, "Need your location", Toast.LENGTH_SHORT).show();
                }
                break;

        }

    }

    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    protected void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

}
