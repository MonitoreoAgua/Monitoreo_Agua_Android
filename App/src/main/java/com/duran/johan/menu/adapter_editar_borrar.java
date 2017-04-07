package com.duran.johan.menu;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

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

        String indiceTexto = context.getString(R.string.indice_usado) + " " + listItem.getIndice_usado();
        String valorIndiceTexto = context.getString(R.string.valor_indice) + " " + listItem.getValor_calculado();
        String colorIndiceTexto = context.getString(R.string.color_indice) + " " + listItem.getColor_mostrado();


        holder.TVindice.setText( indiceTexto );
        holder.TVvalor.setText( valorIndiceTexto );
        holder.TVcolor.setText( colorIndiceTexto );
        holder.TVfecha.setText( listItem.getFecha() );

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView TVindice;
        public TextView TVvalor;
        public TextView TVcolor;
        public TextView TVfecha;
        public ImageButton IBEliminar;
        public ImageButton IBEditar;




        public MyViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            TVindice = (TextView) itemView.findViewById(R.id.indice_usado);
            TVvalor = (TextView) itemView.findViewById(R.id.valor_indice);
            TVcolor = (TextView) itemView.findViewById(R.id.color_valor);
            TVfecha = (TextView) itemView.findViewById(R.id.fecha_ingresada);

            IBEditar = (ImageButton) itemView.findViewById(R.id.btn_editar_documento);
            IBEliminar = (ImageButton) itemView.findViewById(R.id.btn_eliminar_documento);
            IBEditar.setOnClickListener(this);
            IBEliminar.setOnClickListener(this);

        }


        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            if(v == IBEditar){

            }else if(v == IBEliminar){
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(itemView.getContext());
                mBuilder.setIcon(android.R.drawable.ic_menu_delete);
                mBuilder.setTitle(R.string.ad_titulo);
                mBuilder.setMessage(R.string.ad_borrar);
                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Proceso para eliminar la muestra de la base de datos!

                        listItems.remove(getAdapterPosition());
                        notifyDataSetChanged();

                        Toast.makeText(getApplicationContext(), R.string.ed_bo_eliminado_exito, Toast.LENGTH_SHORT).show();
                    }
                });
                mBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = mBuilder.create();
                alertDialog.show();

            }else{
                Toast.makeText(getApplicationContext(), "Se seleccionó todo el card!" + String.valueOf(listItems.get(getAdapterPosition()).get_id_dato()), Toast.LENGTH_SHORT).show();
                //selecciona el card, entonces lo manda a activityMarker para mostrarle los datos que tiene!

                Intent intent = new Intent(context, ActivityMarker.class);

                intent.putExtra("objId", String.valueOf(listItems.get(getAdapterPosition()).get_id_dato()));
                context.startActivity(intent);

            }
        }
    }
}
