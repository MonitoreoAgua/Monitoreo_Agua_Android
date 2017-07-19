package com.duran.johan.menu;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class ActivityFilter extends AppCompatActivity implements View.OnClickListener {
    Button btnDatePickerIni;
    Button btnDatePickerFin;
    Button filtrar;
    EditText txtDateIni;
    EditText txtDateFin;
    EditText usuarioET, nombInstitucionET, nombEstacionET, direccionET;
    Spinner spinnerInd;
    ArrayAdapter<CharSequence> adapter;
    Spinner spinnerKit;
    ArrayAdapter<CharSequence> adapterKit;
    RequestQueue queue;


    String fechaIniStr, fechaFinStr, indiceStr, kitStr, usuarioStr, nombEstacionStr, nombInstitucionStr, direccionStr;


    private int mYear, mMonth, mDay;

    Map<String, String> params;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        //se crea boton de ir atras
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        inicializar(); //inicializa los diferentes campos para el filtro

        btnDatePickerIni.setOnClickListener(this);
        btnDatePickerFin.setOnClickListener(this);
        filtrar.setOnClickListener(this);

        //Spiners de Kit e Indice
        adapter = ArrayAdapter.createFromResource(this, R.array.nombre_indices_f, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInd.setAdapter(adapter);

        adapterKit = ArrayAdapter.createFromResource(this, R.array.nombre_kits_f, android.R.layout.simple_spinner_item);
        adapterKit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKit.setAdapter(adapterKit);


    }


    //Metodo que indica que hacer en caso de presionarse el boton atras
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

    @Override
    public void onClick(View v) {
        if (v == btnDatePickerIni) {
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

                            txtDateIni.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year );

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        }else if (v == btnDatePickerFin) {
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

                            txtDateFin.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year );

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        }else if(v == filtrar){
            enviarConsulta();
        }
    }

    private void enviarConsulta() {

        Response.Listener<String> responseListener = new Response.Listener<String>() { //Respuesta del servidor
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("tagconvertstr", "[" + response + "]");
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) { //Si salió bien le enseña al usuario el valor calculado del indice y el color y vuelve a crear el activity para que pueda ingresar otro dato


                        //Aquí va lo que devuelve la base de datos.

                        //Hay que enviarle a Main Activity el Map params


                    } else { // Si salio mal, le indica al usuario que salio mal y le deja volver a intentarlo
                        Toast.makeText(getApplicationContext(), getString(R.string.documento_fallido), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };


        //inserta los datos a un Map para que se envien como parametros a la función que envia al servidor.

        params = new HashMap<>();

        String prueba = "";


        fechaIniStr = txtDateIni.getText().toString();
        if(!Objects.equals(fechaIniStr, "")){
            prueba += fechaIniStr + "  --  ";
            params.put("fecha_inicial", fechaIniStr);
        }
        fechaFinStr = txtDateFin.getText().toString();
        if(!Objects.equals(fechaFinStr, "")){
            prueba += fechaFinStr + "  --  ";
            params.put("fecha_final", fechaFinStr);
        }
        indiceStr = spinnerInd.getSelectedItem().toString();
        if(!Objects.equals(indiceStr, "Índice")){
            prueba += indiceStr + "  --  ";
            params.put("indice_usado", indiceStr);
        }
        kitStr = spinnerKit.getSelectedItem().toString();
        if(!Objects.equals(kitStr, "Kit")){
            prueba += kitStr + "  --  ";
            params.put("kit_desc", kitStr);
        }
        usuarioStr = usuarioET.getText().toString();
        if(!Objects.equals(usuarioStr, "")){
            prueba += usuarioStr + "  --  ";
            params.put("usuario", usuarioStr);
        }
        nombEstacionStr = nombEstacionET.getText().toString();
        if(!Objects.equals(nombEstacionStr, "")){
            prueba += nombEstacionStr + "  --  ";
            params.put("nombre_estacion", nombEstacionStr);
        }
        nombInstitucionStr = nombInstitucionET.getText().toString();
        if(!Objects.equals(nombInstitucionStr, "")){
            prueba += nombInstitucionStr + "  --  ";
            params.put("nombre_institucion", nombInstitucionStr);
        }
        direccionStr = direccionET.getText().toString();
        if(!Objects.equals(direccionStr, "")){
            prueba += direccionStr + "  --  ";
            params.put("direccion", direccionStr);
        }

        Toast.makeText(getApplicationContext(), prueba, Toast.LENGTH_LONG).show();

        String direccion;
        direccion = getString(R.string.server)+"filtros.php";


        //Envia los datos al servidor
        MongoRequest loginMongoRequest = new MongoRequest(params, direccion, responseListener);
        queue = Volley.newRequestQueue(ActivityFilter.this);
        queue.add(loginMongoRequest);

    }


    public void inicializar(){
        //Eventos asociados a la elección de fecha
        btnDatePickerIni = (Button) findViewById(R.id.btnDateIniFilter);
        btnDatePickerFin = (Button) findViewById(R.id.btnDateFinFilter);
        txtDateIni = (EditText) findViewById(R.id.etDateIniFilter);
        txtDateFin = (EditText) findViewById(R.id.etDateFinFilter);
        usuarioET = (EditText) findViewById(R.id.UsuarioFilter);
        nombInstitucionET = (EditText) findViewById(R.id.nombreInstitucionFilter);
        nombEstacionET = (EditText) findViewById(R.id.nombreEstacionFilter);
        direccionET = (EditText) findViewById(R.id.editDireccion);
        spinnerKit = (Spinner) findViewById(R.id.spinner_Kit_f);
        spinnerInd = (Spinner) findViewById(R.id.spinner_indice_f);
        filtrar = (Button) findViewById(R.id.btnFiltrar);
    }
}
