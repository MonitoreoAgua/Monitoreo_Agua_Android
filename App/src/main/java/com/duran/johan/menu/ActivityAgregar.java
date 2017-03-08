package com.duran.johan.menu;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;
import static com.duran.johan.menu.R.id.CFOpc;
import static com.duran.johan.menu.R.id.NH4Opc;
import static com.duran.johan.menu.R.id.boton_agregar;
import static com.duran.johan.menu.R.id.pHOpc;
import static com.duran.johan.menu.R.string.indice;
import static java.util.TimeZone.getDefault;

public class ActivityAgregar extends Navigation implements
        View.OnClickListener {

    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    Spinner spinnerKit;
    ArrayAdapter<CharSequence> adapterKit;
    EditText etPO2;
    EditText etDBO;
    EditText etNH4;
    EditText etCF;
    EditText etpH;

    //Opcionales
    EditText etNH4Opc;
    EditText etpHOpc;
    EditText etCFOpc;
    EditText  DQO;
    EditText  EC;
    EditText  PO4;
    EditText  GYA;
    EditText  SD;
    EditText  Ssed;
    EditText  STT;
    EditText  ST;
    EditText  SAMM;
    EditText  Aforo;
    EditText  Fosfato;
    EditText  Nitrato;
    EditText  T;
    EditText  Turbidez;
    EditText  Sol_totales;




    EditText Nomb_Institucion ;
    EditText  Nomb_estacion;
    EditText  fecha;
    EditText  editLatitud;
    EditText  editLongitud;
    EditText  editAltitud;
    EditText  editCod_Prov;
    EditText  editCod_Cant;
    EditText  editCod_Dist;
    EditText  editCod_Rio;
    EditText  editTemperatura;
    EditText  editAreaCauce;
    EditText  editVelocidad;

    RelativeLayout generales;
    RelativeLayout obligatorios;
    RelativeLayout opcionales;
    ExpandableLinearLayout content_generales;
    ExpandableLinearLayout content_obligatorios;
    ExpandableLinearLayout content_opcionales;

    Button btnDatePicker, boton_agregar;
    EditText txtDate;
    private int mYear, mMonth, mDay;


    //Strings
    String StPO2;
    String StDBO;
    String StCF;
    String StpH;
    String StNH4;
    String correo;
    String StNombInstitucion;
    String StNombEstacion;
    String StEditLatitud;
    String StEditLongitud;
    String indice;
    String SteditAltitud;
    String  SteditCod_Prov;
    String  SteditCod_Cant;
    String  SteditCod_Dist;
    String  SteditCod_Rio;
    String StFecha;
    String Stkit;
    String  SteditTemperatura;
    String  SteditAreaCauce;
    String SteditVelocidad;


    //Opcionales
    String StNH4Opc;
    String StpHOpc;
    String StCFOpc;
    String  StDQO;
    String  StEC;
    String  StPO4;
    String  StGYA;
    String  StSD;
    String  StSsed;
    String  StSTT;
    String  StST;
    String  StSAMM;
    String  StAforo;
    String  StFosfato;
    String  StNitrato;
    String  StT;
    String  StTurbidez;
    String  StSol_totales;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        verificar_session();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar);
        getSupportActionBar().setTitle(R.string.title_activity_agregar);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //Expandable Layout
        generales=(RelativeLayout) findViewById(R.id.generales);
        obligatorios=(RelativeLayout) findViewById(R.id.obligatorios);
        opcionales=(RelativeLayout) findViewById(R.id.opcionales);
        content_generales=(ExpandableLinearLayout) findViewById(R.id.generales_exp);
        content_obligatorios=(ExpandableLinearLayout) findViewById(R.id.obligatorios_exp);
        content_opcionales=(ExpandableLinearLayout) findViewById(R.id.opcionales_exp);
        //Obligatorios
        etPO2 = (EditText) findViewById(R.id.PO2);
        etDBO = (EditText) findViewById(R.id.DBO);
        etNH4 = (EditText) findViewById(R.id.NH4);
        etCF = (EditText) findViewById(R.id.CF);
        etpH = (EditText) findViewById(R.id.pH);
        //Opcionales
        etNH4Opc = (EditText) findViewById(NH4Opc);
        etpHOpc = (EditText) findViewById(pHOpc);
        etCFOpc = (EditText) findViewById(CFOpc);

        spinnerKit = (Spinner) findViewById(R.id.spinner_Kit);
        adapterKit = ArrayAdapter.createFromResource(this,R.array.nombre_kits, android.R.layout.simple_spinner_item);
        adapterKit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKit.setAdapter(adapterKit);


        btnDatePicker=(Button)findViewById(R.id.btn_date);
        txtDate=(EditText)findViewById(R.id.in_date);
        btnDatePicker.setOnClickListener(this);

        spinner = (Spinner) findViewById(R.id.spinner_indice);
        adapter = ArrayAdapter.createFromResource(this,R.array.nombre_indices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Cosas que necesito meter.
                if(position == 0){
                    sin_campos();
                    if(content_obligatorios.isExpanded()){
                        content_obligatorios.initLayout();
                        content_obligatorios.expand();
                    }else{
                        content_obligatorios.initLayout();
                    }
                }
                else if(position == 1){
                    campos_Holandes();
                    content_opcionales.initLayout();
                    content_obligatorios.initLayout();
                    content_obligatorios.expand();
                }else if(position == 2){
                    campos_NSF();
                    content_opcionales.initLayout();
                    content_obligatorios.initLayout();
                    content_obligatorios.expand();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //to toggle content
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


        boton_agregar = (Button) findViewById(R.id.boton_agregar);

        boton_agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(verificar_datosObligatorios()){
                    indice = spinner.getSelectedItem().toString();
                    if(indice.equals("Índice Holandés")){
                        if(verificar_Holandes()){
                            valores_opcionales();
                            enviar_Holandes();
                            StpHOpc = etpHOpc.getText().toString();
                            StCFOpc = etCFOpc.getText().toString();

                        }else{
                            Toast.makeText(getApplicationContext(), R.string.mensaje_error_Holandes, Toast.LENGTH_SHORT).show();
                        }

                    }else if(indice.equals("Índice NSF")){
                        if(verificar_NSF()){
                            valores_opcionales();
                            enviar_NSF();
                            StNH4Opc = etNH4Opc.getText().toString();
                        }else{
                            Toast.makeText(getApplicationContext(), R.string.mensaje_error_NSF, Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), R.string.mensaje_error_indice, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), R.string.mensaje_error_requeridos, Toast.LENGTH_SHORT).show();
                }


            }
        });

    }




    private boolean verificar_datosObligatorios() {
        Nomb_Institucion  = (EditText) findViewById(R.id.Nomb_Institucion);
        Nomb_estacion = (EditText) findViewById(R.id.Nomb_estacion);
        editLatitud = (EditText) findViewById(R.id.editLatitud);
        editLongitud = (EditText) findViewById(R.id.editLongitud);
        editAltitud = (EditText) findViewById(R.id.editAltitud);
        editCod_Prov = (EditText) findViewById(R.id.editCod_Prov);
        editCod_Cant = (EditText) findViewById(R.id.editCod_Cant);
        editCod_Dist = (EditText) findViewById(R.id.editCod_Dist);
        editCod_Rio = (EditText) findViewById(R.id.editCod_Rio);

        StNombInstitucion = Nomb_Institucion.getText().toString();
        StNombEstacion = Nomb_estacion.getText().toString();
        StEditLatitud = editLatitud.getText().toString();
        StEditLongitud = editLongitud.getText().toString();
        SteditAltitud = editAltitud.getText().toString();
        SteditCod_Prov = editCod_Prov.getText().toString();
        SteditCod_Cant = editCod_Cant.getText().toString();
        SteditCod_Dist = editCod_Dist.getText().toString();
        SteditCod_Rio = editCod_Rio.getText().toString();
            StFecha = txtDate.getText().toString();

        if(!StNombInstitucion.equals("") && !StNombEstacion.equals("") &&  !StEditLatitud.equals("") &&  !StEditLongitud.equals("") &&  !SteditAltitud.equals("") &&
                !SteditCod_Prov.equals("") &&  !SteditCod_Cant.equals("") && !SteditCod_Dist.equals("") &&  !SteditCod_Rio.equals("") && !StFecha.equals("")){
           return true;
        }else{
            return false;
        }


    }

    private boolean verificar_NSF() {
        StPO2 = etPO2.getText().toString();
        StDBO = etDBO.getText().toString();
        StCF = etCF.getText().toString();
        StpH = etpH.getText().toString();

        if(!StPO2.equals("") && !StDBO.equals("") && !StCF.equals("") && !StpH.equals("")) {
            return true;
        }else{
            return false;
        }

    }

    private boolean verificar_Holandes() {
        StPO2 = etPO2.getText().toString();
        StDBO = etDBO.getText().toString();
        StNH4 = etNH4.getText().toString();

        if(!StPO2.equals("") && !StDBO.equals("") && !StNH4.equals("")) {
            return true;
        }else{
            return false;
        }
    }

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

                            txtDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
    }


    private void valores_opcionales() {
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
        Fosfato = (EditText) findViewById(R.id.Fosfato);
        Nitrato = (EditText) findViewById(R.id.Nitrato);
        T = (EditText) findViewById(R.id.T);
        Turbidez = (EditText) findViewById(R.id.Turbidez);
        Sol_totales = (EditText) findViewById(R.id.Sol_totales);
        //Datos Generales
        editTemperatura = (EditText) findViewById(R.id.editTemperatura);
        editAreaCauce = (EditText) findViewById(R.id.editAreaCauce);
        editVelocidad = (EditText) findViewById(R.id.editVelocidad);

        Stkit = spinnerKit.getSelectedItem().toString();
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
        StFosfato = Fosfato.getText().toString();
        StNitrato = Nitrato.getText().toString();
        StT = T.getText().toString();
        StTurbidez = Turbidez.getText().toString();
        StSol_totales = Sol_totales.getText().toString();



    }


    private void enviar_NSF() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("tagconvertstr", "["+response+"]");
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success){
                        String texto = R.string.documento_exito + "\nÍndice utilizado= " + jsonResponse.getString("indice") + "\nResultado del índice= " + jsonResponse.getDouble("valor") +
                                "\nColor= " + jsonResponse.getString("color");
                        Toast.makeText(getApplicationContext(), texto, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ActivityAgregar.this, ActivityAgregar.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        ActivityAgregar.this.startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(), R.string.documento_fallido, Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Map<String, String> params;
        params = new HashMap<>();
        params.put("usuario", correo);
        params.put("Indice", "NSF");
        params.put("temp_agua", SteditTemperatura);
        params.put("velocidad_agua", SteditVelocidad);
        params.put("area_cauce_rio", SteditAreaCauce);
        params.put("PO2", StPO2);
        params.put("DBO", StDBO);
        params.put("CF", StCF);
        params.put("pH", StpH);
        params.put("DQO", StDQO);
        params.put("EC", StEC);
        params.put("PO4", StPO4);
        params.put("GYA", StGYA);
        params.put("SD", StSD);
        params.put("Ssed", StSsed);
        params.put("SST", StSTT);
        params.put("SAAM", StSAMM);
        params.put("T", StT);
        params.put("Aforo", StAforo);
        params.put("ST", StST);
        params.put("NH4", StNH4Opc);
        params.put("Fosfato", StFosfato);
        params.put("Nitrato", StNitrato);
        params.put("Turbidez", StTurbidez);
        params.put("Sol_totales", StSol_totales);
        params.put("nombre_institucion", StNombInstitucion);
        params.put("nombre_estacion", StNombEstacion);
        params.put("fecha", StFecha);
        params.put("kit_desc", Stkit);
        params.put("lat", StEditLatitud);
        params.put("lng", StEditLongitud);
        params.put("alt", SteditAltitud);
        params.put("cod_prov", SteditCod_Prov);
        params.put("cod_cant", SteditCod_Cant);
        params.put("cod_dist", SteditCod_Dist);
        params.put("cod_rio", SteditCod_Rio);

        MongoRequest loginMongoRequest = new MongoRequest(params,"http://192.168.138.1:8081/proyectoJavier/android/insertarNSF.php", responseListener);
        RequestQueue queue = Volley.newRequestQueue(ActivityAgregar.this);
        queue.add(loginMongoRequest);


    }



    private void enviar_Holandes() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("tagconvertstr", "["+response+"]");
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success){
                        Toast.makeText(getApplicationContext(), R.string.documento_exito, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ActivityAgregar.this, ActivityAgregar.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        ActivityAgregar.this.startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(), R.string.documento_fallido, Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Map<String, String> params;
        params = new HashMap<>();
        params.put("usuario", correo);
        params.put("Indice", "Holandés");
        params.put("temp_agua", SteditTemperatura);
        params.put("velocidad_agua", SteditVelocidad);
        params.put("area_cauce_rio", SteditAreaCauce);
        params.put("% O2", StPO2);
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
        params.put("T", StT);
        params.put("Aforo", StAforo);
        params.put("ST", StST);
        params.put("CF", StCFOpc);
        params.put("pH", StpHOpc);
        params.put("Fosfato", StFosfato);
        params.put("Nitrato", StNitrato);
        params.put("Turbidez", StTurbidez);
        params.put("Sol_totales", StSol_totales);
        params.put("nombre_institucion", StNombInstitucion);
        params.put("nombre_estacion", StNombEstacion);
        params.put("fecha", StFecha);
        params.put("kit_desc", Stkit);
        params.put("lat", StEditLatitud);
        params.put("lng", StEditLongitud);
        params.put("alt", SteditAltitud);
        params.put("cod_prov", SteditCod_Prov);
        params.put("cod_cant", SteditCod_Cant);
        params.put("cod_dist", SteditCod_Dist);
        params.put("cod_rio", SteditCod_Rio);

        MongoRequest loginMongoRequest = new MongoRequest(params,"http://192.168.138.1:8081/proyectoJavier/android/insertarHolandes.php", responseListener);
        RequestQueue queue = Volley.newRequestQueue(ActivityAgregar.this);
        queue.add(loginMongoRequest);
    }




    private void campos_NSF() {
        etPO2.setVisibility(View.VISIBLE);
        etDBO.setVisibility(View.VISIBLE);
        etCF.setVisibility(View.VISIBLE);
        etpH.setVisibility(View.VISIBLE);
        etNH4.setVisibility(View.GONE);

        //Opcionales
        etNH4Opc.setVisibility(View.VISIBLE);
        etCFOpc.setVisibility(View.GONE);
        etpHOpc.setVisibility(View.GONE);
    }

    private void sin_campos() {
        etPO2.setText("");
        etDBO.setText("");
        etNH4.setText("");
        etCF.setText("");
        etpH.setText("");
        etPO2.setVisibility(View.GONE);
        etDBO.setVisibility(View.GONE);
        etNH4.setVisibility(View.GONE);
        etCF.setVisibility(View.GONE);
        etpH.setVisibility(View.GONE);

        //opcionales
        etCFOpc.setVisibility(View.GONE);
        etpHOpc.setVisibility(View.GONE);
        etNH4Opc.setVisibility(View.GONE);

        content_opcionales.initLayout();
    }

    private void campos_Holandes() {
        etPO2.setVisibility(View.VISIBLE);
        etDBO.setVisibility(View.VISIBLE);
        etNH4.setVisibility(View.VISIBLE);
        etCF.setVisibility(View.GONE);
        etpH.setVisibility(View.GONE);

        //Opcionales
        etCFOpc.setVisibility(View.VISIBLE);
        etpHOpc.setVisibility(View.VISIBLE);
        etNH4Opc.setVisibility(View.GONE);

    }


    //Ver si hay una sesion activa
    private void verificar_session(){
        if(AccessToken.getCurrentAccessToken() == null){
            SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
            correo = prefs.getString("correo", "No definido");
            Toast.makeText(getApplicationContext(), correo, Toast.LENGTH_SHORT).show();
            if (correo != "No definido") {

            }else{
                goLoginScreen();
            }
        }
    }

    private void goLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
