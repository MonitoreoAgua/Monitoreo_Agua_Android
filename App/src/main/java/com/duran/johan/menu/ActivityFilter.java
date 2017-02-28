package com.duran.johan.menu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RelativeLayout;

import static com.duran.johan.menu.R.layout.maps;
import static com.duran.johan.menu.R.layout.sub_consulta;
import static com.duran.johan.menu.R.layout.sub_muestra;
import static com.duran.johan.menu.R.layout.sub_resultado;

public class ActivityFilter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        final Button botonMuestra = (Button) findViewById(R.id.id_muestra);
        final Button botonConsulta = (Button) findViewById(R.id.id_consulta);
        final Button botonResultado = (Button) findViewById(R.id.id_resultado);

        botonMuestra.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                RelativeLayout item = (RelativeLayout)findViewById(R.id.dynamicElement);
                View child = getLayoutInflater().inflate(sub_muestra, null);
                item.removeAllViews();
                item.addView(child);
            }
        });

        botonConsulta.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                RelativeLayout item = (RelativeLayout)findViewById(R.id.dynamicElement);
                View child = getLayoutInflater().inflate(sub_consulta, null);
                item.removeAllViews();
                item.addView(child);
            }
        });

        botonResultado.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                RelativeLayout item = (RelativeLayout)findViewById(R.id.dynamicElement);
                View child = getLayoutInflater().inflate(sub_resultado, null);
                item.removeAllViews();
                item.addView(child);
            }
        });

    }
}
