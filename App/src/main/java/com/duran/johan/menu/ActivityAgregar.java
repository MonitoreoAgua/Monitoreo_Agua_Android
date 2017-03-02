package com.duran.johan.menu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.duran.johan.menu.R;
import com.facebook.AccessToken;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;

public class ActivityAgregar extends Navigation {



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        verificar_session();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        RelativeLayout generales=(RelativeLayout) findViewById(R.id.generales);
        RelativeLayout obligatorios=(RelativeLayout) findViewById(R.id.obligatorios);
        RelativeLayout opcionales=(RelativeLayout) findViewById(R.id.opcionales);

//to toggle content
        generales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpandableLinearLayout content=(ExpandableLinearLayout) findViewById(R.id.generales_exp);
                content.toggle();
            }
        });
        obligatorios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpandableLinearLayout content=(ExpandableLinearLayout) findViewById(R.id.obligatorios_exp);
                content.toggle();
            }
        });
        opcionales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpandableLinearLayout content=(ExpandableLinearLayout) findViewById(R.id.opcionales_exp);
                content.toggle();
            }
        });



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
