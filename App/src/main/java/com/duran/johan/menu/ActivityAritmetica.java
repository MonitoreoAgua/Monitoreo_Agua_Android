package com.duran.johan.menu;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.github.aakira.expandablelayout.ExpandableLinearLayout;

import static com.duran.johan.menu.R.id.generales;
import static com.duran.johan.menu.R.id.obligatorios;
import static com.duran.johan.menu.R.layout.activity_aritmetica;
import static com.duran.johan.menu.R.layout.maps;

public class ActivityAritmetica extends AppCompatActivity{
    RelativeLayout obligatorios;
    ExpandableLinearLayout content_obligatorios;
    RelativeLayout opcionales;
    ExpandableLinearLayout content_opcionales;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aritmetica);
        obligatorios=(RelativeLayout) findViewById(R.id.obligatoriosPOI);
        content_obligatorios=(ExpandableLinearLayout) findViewById(R.id.obligatorios_expPOI);
        opcionales=(RelativeLayout) findViewById(R.id.opcionalesPOI);
        content_opcionales=(ExpandableLinearLayout) findViewById(R.id.opcionales_expPOI);
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
}
