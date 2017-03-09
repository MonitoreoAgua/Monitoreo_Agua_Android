package com.duran.johan.menu;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.github.aakira.expandablelayout.ExpandableLinearLayout;

import static com.duran.johan.menu.R.id.generales;
import static com.duran.johan.menu.R.layout.activity_aritmetica;
import static com.duran.johan.menu.R.layout.maps;

public class ActivityAritmetica extends AppCompatActivity{
    RelativeLayout generales;
    ExpandableLinearLayout content_generales;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aritmetica);
        generales=(RelativeLayout) findViewById(R.id.generales1);
        content_generales=(ExpandableLinearLayout) findViewById(R.id.generales_exp11);
        generales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content_generales.toggle();
            }
        });
    }
}
