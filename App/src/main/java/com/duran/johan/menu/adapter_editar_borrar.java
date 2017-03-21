package com.duran.johan.menu;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by kenca on 20/03/2017.
 */


public class adapter_editar_borrar extends RecyclerView.Adapter<adapter_editar_borrar.MyViewHolder> {


    private List<Lista_items_editar_borrar> listItems;
    private Context context;

    public adapter_editar_borrar(List<Lista_items_editar_borrar> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.datos_editar_borrar, parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Lista_items_editar_borrar listItem = listItems.get(position);

        holder.TVindice.setText( R.string.indice_usado + listItem.getIndice_usado() );
        holder.TVvalor.setText( R.string.valor_indice + listItem.getValor_calculado() );
        holder.TVcolor.setText( R.string.color_indice + listItem.getColor_mostrado() );
        holder.TVfecha.setText( listItem.getFecha() );

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView TVindice;
        public TextView TVvalor;
        public TextView TVcolor;
        public TextView TVfecha;



        public MyViewHolder(View itemView) {
            super(itemView);

            TVindice = (TextView) itemView.findViewById(R.id.indice_usado);
            TVvalor = (TextView) itemView.findViewById(R.id.valor_indice);
            TVcolor = (TextView) itemView.findViewById(R.id.color_valor);
            TVfecha = (TextView) itemView.findViewById(R.id.fecha_ingresada);



        }
    }
}
