package com.duran.johan.menu;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class ActivityFilter extends AppCompatActivity implements View.OnClickListener {
    Button btnDatePickerIni;
    Button btnDatePickerFin;
    EditText txtDateIni;
    EditText txtDateFin;

    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        //se crea boton de ir atras
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        //Eventos asociados a la elección de fecha
        btnDatePickerIni = (Button) findViewById(R.id.btnDateIniFilter);
        btnDatePickerFin = (Button) findViewById(R.id.btnDateFinFilter);
        txtDateIni = (EditText) findViewById(R.id.etDateIniFilter);
        txtDateFin = (EditText) findViewById(R.id.etDateFinFilter);

        btnDatePickerIni.setOnClickListener(this);
        btnDatePickerFin.setOnClickListener(this);

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

                            txtDateIni.setText(year + "-" + (monthOfYear + 1) + "-" +  dayOfMonth);

                        }
                    }, mYear, mMonth, mDay);
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

                            txtDateFin.setText(year + "-" + (monthOfYear + 1) + "-" +  dayOfMonth);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
    }
}
