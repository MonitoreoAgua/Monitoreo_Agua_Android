package com.duran.johan.menu;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import com.duran.johan.menu.R;
import com.facebook.AccessToken;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import static android.content.Context.MODE_PRIVATE;
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



    RelativeLayout generales;
    RelativeLayout obligatorios;
    RelativeLayout opcionales;
    ExpandableLinearLayout content_generales;
    ExpandableLinearLayout content_obligatorios;
    ExpandableLinearLayout content_opcionales;

    Button btnDatePicker, btnTimePicker;
    EditText txtDate;
    private int mYear, mMonth, mDay, mHour, mMinute;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //verificar_session();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar);
        getSupportActionBar().setTitle(R.string.title_activity_agregar);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        generales=(RelativeLayout) findViewById(R.id.generales);
        obligatorios=(RelativeLayout) findViewById(R.id.obligatorios);
        opcionales=(RelativeLayout) findViewById(R.id.opcionales);
        content_generales=(ExpandableLinearLayout) findViewById(R.id.generales_exp);
        content_obligatorios=(ExpandableLinearLayout) findViewById(R.id.obligatorios_exp);
        content_opcionales=(ExpandableLinearLayout) findViewById(R.id.opcionales_exp);

        etPO2 = (EditText) findViewById(R.id.PO2);
        etDBO = (EditText) findViewById(R.id.DBO);
        etNH4 = (EditText) findViewById(R.id.NH4);
        etCF = (EditText) findViewById(R.id.CF);
        etpH = (EditText) findViewById(R.id.pH);

        etNH4Opc = (EditText) findViewById(R.id.NH4Opc);
        etpHOpc = (EditText) findViewById(R.id.pHOpc);
        etCFOpc = (EditText) findViewById(R.id.CFOpc);

        btnDatePicker=(Button)findViewById(R.id.btn_date);
        txtDate=(EditText)findViewById(R.id.in_date);

        btnDatePicker.setOnClickListener(this);


        spinnerKit = (Spinner) findViewById(R.id.spinner_Kit);
        adapterKit = ArrayAdapter.createFromResource(this,R.array.nombre_kits, android.R.layout.simple_spinner_item);
        adapterKit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKit.setAdapter(adapterKit);

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

                            txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
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
            String correo = prefs.getString("correo", "No definido");
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
