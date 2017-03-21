package com.duran.johan.menu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.duran.johan.menu.R;

import java.util.ArrayList;
import java.util.List;

public class editar_borrar extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private List<Lista_items_editar_borrar> listItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_borrar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        //recyclerView.setHasFixedSize(true);



        listItems = new ArrayList<>();

        for(int i = 0; i<=10; i++) {
            Lista_items_editar_borrar listItem = new Lista_items_editar_borrar(
                    "" + (i), "NSF", ""+14, "Rojo", i+"/03/2017"
            );

            listItems.add(listItem);

        }

        adapter = new adapter_editar_borrar(listItems, this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



    }
}
