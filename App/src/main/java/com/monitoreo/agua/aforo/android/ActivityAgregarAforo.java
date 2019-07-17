package com.monitoreo.agua.aforo.android;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Stream;

public class ActivityAgregarAforo extends AppCompatActivity implements
        View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private ArrayAdapter<CharSequence> adapterMAforo;
    private Button btnDateAforo, btnAddSection,btnCalcularAforo, btnInsertarAforo;
    private EditText et_DateAforo, et_endTimeAforo, et_initialTimeAforo, et_referenceStartAforo, et_referenceEndAforo, et_measurementAforo, et_commentAforo, et_dischargeAforo, et_crossAreaAforo;
    private String StLatitud, StLongitud, StMetodoAforo, usuario;
    private Spinner spinner_methodAforo;
    private final String[] StringPermisos = {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private RecyclerView recyclerView;
    private Aforo_Section_Adapter aforoAdapter;
    private List<Aforo_Section> aforo_sectionList;
    private RelativeLayout loading_page;
    private int mYear, mMonth, mDay;
    private RequestQueue queue;
    //Location de Google
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Boolean flag;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_aforo);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        loading_page = findViewById(R.id.loadingPanel);
        loading_page.setVisibility(View.VISIBLE);

        //hacer el texto de usuario con el correo del usuario
        SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        usuario = prefs.getString("correo", "No definido");
//        usuario.setText(usuario);

        cargarDatos();

        // se pide permisos para trabajar
        requestPermission();
        //se inicializa el GoogleApiClient para pedir latitud y longitud
        buildGoogleApiClient();
        flag = false;

        btnDateAforo.setOnClickListener(this);
        btnAddSection = findViewById(R.id.add_section_btn);
        btnCalcularAforo = findViewById(R.id.calcular_aforo_btn);
        btnInsertarAforo = findViewById(R.id.insertar_aforo_btn);

        recyclerView = findViewById(R.id.recycler_view_aforo);
        recyclerView.setHasFixedSize(false);
        aforo_sectionList = new ArrayList<>();
        aforoAdapter = new Aforo_Section_Adapter(this,aforo_sectionList, btnCalcularAforo);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(aforoAdapter);

        aforo_sectionList.add(new Aforo_Section(0,0,0,""));
        aforoAdapter.notifyItemChanged(0);
        btnCalcularAforo.setEnabled(true);

        btnAddSection.setOnClickListener(view -> {
            aforo_sectionList.add(new Aforo_Section(0,0,0,""));
            aforoAdapter.notifyItemChanged(aforo_sectionList.size()-1);
        });


        btnCalcularAforo.setOnClickListener(view -> {
            // Crear arreglos con los datos ingresados
            ArrayList<ArrayList<Double>> dataDescarga = new ArrayList<>();
            ArrayList<Double> areaSecciones = new ArrayList<>();
            ArrayList<Double> descargaSecciones = new ArrayList<>();

            for (int i = 0 ; i < aforo_sectionList.size() ; i++) {
                Log.e("childcount",Integer.toString(recyclerView.getLayoutManager().getChildCount()));
                View vista = recyclerView.getLayoutManager().findViewByPosition(i);
                EditText distance = vista.findViewById(R.id.distance_aforo);
                EditText depth = vista.findViewById(R.id.depth_aforo);
                EditText speed = vista.findViewById(R.id.speed_aforo);

                ArrayList<Double> datosEntrada = new ArrayList<>();
                datosEntrada.add(Double.parseDouble(distance.getText().toString()));
                datosEntrada.add(Double.parseDouble(depth.getText().toString()));
                datosEntrada.add(Double.parseDouble(speed.getText().toString()));
                dataDescarga.add(datosEntrada);
            }

            for (int i = 0 ; i < dataDescarga.size() ; i++) {
                double areaSeccion;
                if (i+1 == dataDescarga.size()) {
                    areaSeccion = (dataDescarga.get(i).get(0)-0)*dataDescarga.get(i).get(1);
                    areaSecciones.add(areaSeccion);
                }
                else {
                    areaSeccion = (dataDescarga.get(i).get(0)-dataDescarga.get(i+1).get(0))*dataDescarga.get(i).get(1);
                    areaSecciones.add(areaSeccion);
                }
                descargaSecciones.add(dataDescarga.get(i).get(2)*areaSeccion);
            }

            for (int i = 0 ; i < aforo_sectionList.size() ; i++) {
                View vista = recyclerView.getLayoutManager().findViewByPosition(i);
                EditText area_section = vista.findViewById(R.id.area_section_aforo);
                EditText discharge_section = vista.findViewById(R.id.discharge_section_aforo);

                area_section.setText(Double.toString(areaSecciones.get(i)));
                discharge_section.setText(Double.toString(descargaSecciones.get(i)));
            }
            double crossArea;
            double discharge;
            Stream<Double> areaStream = Arrays.stream(areaSecciones.toArray(new Double[areaSecciones.size()]));
            Stream<Double> dischargeStream = Arrays.stream(descargaSecciones.toArray(new Double[descargaSecciones.size()]));
            crossArea = areaStream.reduce(Double::sum).get();
            discharge = dischargeStream.reduce(Double::sum).get();
            et_crossAreaAforo.setText(Double.toString(crossArea));
            et_dischargeAforo.setText(Double.toString(discharge));
            btnInsertarAforo.setEnabled(true);
        });

        //listener para el botón de insertar aforo
        btnInsertarAforo.setOnClickListener(view -> {
            metodo_aforo();
            if (StMetodoAforo != null) {
                enviar_aforo();
            }
        });


        //inicialización del spinner para el tipo de POI
        adapterMAforo = ArrayAdapter.createFromResource(this, R.array.methods_aforo, android.R.layout.simple_spinner_item);
        adapterMAforo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_methodAforo.setAdapter(adapterMAforo);

        loading_page.setVisibility(View.GONE);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Según la opción que se haya marcado en el spinner respectivo, almacena en una variable
     * el valor que se guardará en la base de datos
     */
    private void metodo_aforo() {
        int indiceMarcado = spinner_methodAforo.getSelectedItemPosition();
        switch (indiceMarcado) {
            case 1:
                StMetodoAforo = "Punto reducido";
                break;
            case 2:
                StMetodoAforo = "Distribución de velocidad";
                break;
            default:
                StMetodoAforo = null;
                Toast.makeText(getApplicationContext(), R.string.error_method_aforo, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void goMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    /**
     * @param v hace el proceso para escoger la fecha desde el calendario y la escribe en el campo de fecha
     */
    @Override
    public void onClick(View v) {
        if (v == btnDateAforo) {
            // Get Current Date
            final Calendar c = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, monthOfYear, dayOfMonth) -> et_DateAforo.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year), mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        }
    }

    private void cargarDatos() {
        //Aforo
        et_DateAforo = findViewById(R.id.fechaAforo);
        Date now = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
        et_DateAforo.setText(formato.format(now));
        et_endTimeAforo = findViewById(R.id.endTime);
        et_initialTimeAforo = findViewById(R.id.initialTime);
        formato = new SimpleDateFormat("hh:mm aa");
        et_initialTimeAforo.setText(formato.format(now));
        et_referenceStartAforo = findViewById(R.id.referenceStart);
        et_referenceEndAforo = findViewById(R.id.referenceEnd);
        spinner_methodAforo = findViewById(R.id.spinner_method_aforo);
        et_measurementAforo = findViewById(R.id.measurementMethod);
        et_commentAforo = findViewById(R.id.commment_aforo);
        et_dischargeAforo = findViewById(R.id.discharge_aforo);
        et_dischargeAforo.setEnabled(false);
        et_crossAreaAforo = findViewById(R.id.cross_area_aforo);
        et_crossAreaAforo.setEnabled(false);
        btnDateAforo = findViewById(R.id.btn_date_aforo);
    }

    /**
     * Método que toma todos los datos y los envia al servidor para ingresar el documento a la base de datos para el aforo calculado
     */
    private void enviar_aforo() {
        loading_page = findViewById(R.id.loadingPanel);
        loading_page.setVisibility(View.VISIBLE);

        //Respuesta del servidor
        Response.Listener<String> responseListener = response -> {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean success = jsonResponse.getBoolean("success");
                if (success) {
                    String texto = getString(R.string.documento_exito);

                    loading_page.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), texto, Toast.LENGTH_SHORT).show();
                    btnInsertarAforo.setEnabled(false);
                    goMainScreen();
                } else { // Si salio mal, le indica al usuario que salio mal y le deja volver a intentarlo
                    loading_page.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), getString(R.string.documento_fallido), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                loading_page.setVisibility(View.GONE);
                e.printStackTrace();
            }
        };


        //inserta los datos a un Map para que se envien como parametros a la función que envia al servidor.
        Map<String, String> params;
        params = new HashMap<>();
        params.put("email", usuario);
        params.put("latitud", StLatitud);
        params.put("longitud", StLongitud);
        params.put("fecha", et_DateAforo.getText().toString());
        params.put("tiempoFinal", et_endTimeAforo.getText().toString());
        params.put("tiempoInicio", et_initialTimeAforo.getText().toString());
        params.put("medicionInicio", et_referenceStartAforo.getText().toString());
        params.put("medicionFinal", et_referenceEndAforo.getText().toString());
        params.put("metodoUsado", StMetodoAforo);
        params.put("metodoMedicion", et_measurementAforo.getText().toString());
        params.put("comments", et_commentAforo.getText().toString());
        params.put("descargaCalculada", et_dischargeAforo.getText().toString());
        params.put("crossDescarga", et_crossAreaAforo.getText().toString());

        String direccion;
        direccion = getString(R.string.server) + "insertarAforo.php";


        //Envia los datos al servidor
        MongoRequest loginMongoRequest = new MongoRequest(params, direccion, responseListener);
        loginMongoRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue = Volley.newRequestQueue(ActivityAgregarAforo.this);
        queue.add(loginMongoRequest);
    }

    /**
     * @param bundle Cuando conecta con el GoogleApiClient pide la latitud y longitud y los pone en los campos para ingresar.
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = ActivityAgregarAforo.this.checkCallingOrSelfPermission(permission);
        if (res == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                if (!flag) {
                    StLatitud = String.valueOf(mLastLocation.getLatitude());
                    StLongitud = String.valueOf(mLastLocation.getLongitude());
                }
            }
        }
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
     * @param requestCode  codigo del permiso
     * @param permissions  los permisos que se solicitan
     * @param grantResults indica si permiso es concedido o no
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
