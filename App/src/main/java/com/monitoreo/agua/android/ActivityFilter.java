package com.monitoreo.agua.android;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

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

    int indiceStrPos;
    String fechaIniStr,indiceStr, fechaFinStr, kitStr, usuarioStr, nombEstacionStr, nombInstitucionStr, direccionStr;


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

        Intent intent = getIntent();
        Bundle extras= intent.getExtras();
        if(extras != null){
                HashMap<String, String> parametros_filtro = (HashMap<String, String>) intent.getSerializableExtra("filtros");
                llenarDatos(parametros_filtro);
        }


    }

    private void llenarDatos(HashMap<String, String> parametros_filtro) {
        for (Object o : parametros_filtro.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            switch (pair.getKey().toString()) {
                case "Muestra,usuario" :
                    usuarioET.setText(pair.getValue().toString());
                    break;
                case "Poi,nombre_institucion" :
                    nombInstitucionET.setText(pair.getValue().toString());
                    break;
                case "POI,nombre_estacion" :
                    nombEstacionET.setText(pair.getValue().toString());
                    break;
                case "POI,kit_desc" :
                    switch (pair.getValue().toString()) {
                        case "LMRHI-UNA":
                            spinnerKit.setSelection(1, true);
                            break;
                        case "LaMotte Deluxe":
                            spinnerKit.setSelection(2, true);
                            break;
                        case "LaMotte Complete":
                            spinnerKit.setSelection(3, true);
                            break;
                        case "LaMotte Earth Force":
                            spinnerKit.setSelection(4, true);
                            break;
                        case "LaMotte Kit de Aula":
                            spinnerKit.setSelection(5, true);
                            break;
                        case "Otro":
                            spinnerKit.setSelection(6, true);
                            break;
                    }
                    break;
                case "Muestra,indice_usado" :
                    switch (pair.getValue().toString()) {
                        case "Holandés":
                            spinnerInd.setSelection(1, true);
                            break;
                        case "NSF":
                            spinnerInd.setSelection(2, true);
                            break;
                        case "BMWP-CR":
                            spinnerInd.setSelection(3, true);
                            break;
                        case "Sin Índice":
                            spinnerInd.setSelection(4, true);
                            break;
                    }
                    break;
                case "fecha_inicial":
                    txtDateIni.setText(pair.getValue().toString());
                    break;
                case "fecha_final":
                    txtDateFin.setText(pair.getValue().toString());
                    break;
            }
        }
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
                    JSONArray jsonResponse = new JSONArray(response);

                    boolean success = jsonResponse.getJSONObject(jsonResponse.length()-1).getBoolean("success");
                    if (success) { //Si salió bien le enseña al usuario el valor calculado del indice y el color y vuelve a crear el activity para que pueda ingresar otro dato

                        jsonResponse.remove(jsonResponse.length()-1);
                        if(jsonResponse.length() > 0){
                            Intent intent = new Intent(ActivityFilter.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("response", jsonResponse.toString() );
                            HashMap<String,String> hMap = (HashMap<String, String>) params;
                            intent.putExtra("filtros", hMap);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            ActivityFilter.this.startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(), getString(R.string.filtros_sin_datos), Toast.LENGTH_SHORT).show();
                        }


                        //Aquí va lo que devuelve la base de datos.

                        //Hay que enviarle a Main Activity el Map params


                    } else { // Si salio mal, le indica al usuario que salio mal y le deja volver a intentarlo
                        Toast.makeText(getApplicationContext(), getString(R.string.error_filtros), Toast.LENGTH_SHORT).show();
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
        indiceStrPos=spinnerInd.getSelectedItemPosition();

        if(indiceStrPos!=0){
            prueba += indiceStr + "  --  ";

            switch (indiceStrPos){
                case 1:
                    params.put("Muestra,indice_usado", "Holandés");
                    break;
                case 2:
                    params.put("Muestra,indice_usado", "NSF");
                    break;
                case 3:
                    params.put("Muestra,indice_usado", "BMWP-CR");
                    break;
                case 4:
                    params.put("Muestra,indice_usado", "Sin Índice");
                    break;
            }
        }
        kitStr = spinnerKit.getSelectedItem().toString();
        if(!Objects.equals(kitStr, "Kit")){
            prueba += kitStr + "  --  ";
            params.put("POI,kit_desc", kitStr);
        }
        usuarioStr = usuarioET.getText().toString();
        if(!Objects.equals(usuarioStr, "")){
            prueba += usuarioStr + "  --  ";
            params.put("Muestra,usuario", usuarioStr);
        }
        nombEstacionStr = nombEstacionET.getText().toString();
        if(!Objects.equals(nombEstacionStr, "")){
            prueba += nombEstacionStr + "  --  ";
            params.put("POI,nombre_estacion", nombEstacionStr);
        }
        nombInstitucionStr = nombInstitucionET.getText().toString();
        if(!Objects.equals(nombInstitucionStr, "")){
            prueba += nombInstitucionStr + "  --  ";
            params.put("POI,nombre_institucion", nombInstitucionStr);
        }
        direccionStr = direccionET.getText().toString();
        if(!Objects.equals(direccionStr, "")){
            prueba += direccionStr + "  --  ";
            params.put("direccion", direccionStr);
        }

        //Toast.makeText(getApplicationContext(), prueba, Toast.LENGTH_LONG).show();

        String direccion;
        direccion = getString(R.string.server)+"filtro.php";


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
