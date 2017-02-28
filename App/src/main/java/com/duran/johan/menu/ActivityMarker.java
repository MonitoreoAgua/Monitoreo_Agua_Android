package com.duran.johan.menu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static com.duran.johan.menu.R.id.textView;
import static com.duran.johan.menu.R.id.visible;

public class ActivityMarker extends AppCompatActivity {
    int clickObligatorios;
    int clickOpcionales;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);
        populateView();
    }

    private void populateView() {
        int idsTxt;
    }
}
