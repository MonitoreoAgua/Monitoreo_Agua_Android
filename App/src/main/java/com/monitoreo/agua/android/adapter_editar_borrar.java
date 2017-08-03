package com.monitoreo.agua.android;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by kenca on 20/03/2017.
 */


public class adapter_editar_borrar extends RecyclerView.Adapter<adapter_editar_borrar.MyViewHolder>  {


    private List<Lista_items_editar_borrar> listItems;
    private Context context;
    public boolean permiso = false;
    public boolean eliminado = false;

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




    private void requestWritePermission() {

        if(isExternalStorageWritable()){
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                permiso = true;
            }
        }

    }


    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }



    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView TVindice;
        public TextView TVvalor;
        public TextView TVcolor;
        public TextView TVfecha;
        public ImageButton IBEliminar;
        public ImageButton IBEditar;
        public ImageButton IBDescarga;




        public MyViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            TVindice = (TextView) itemView.findViewById(R.id.indice_usado);
            TVvalor = (TextView) itemView.findViewById(R.id.valor_indice);
            TVcolor = (TextView) itemView.findViewById(R.id.color_valor);
            TVfecha = (TextView) itemView.findViewById(R.id.fecha_ingresada);

            IBEditar = (ImageButton) itemView.findViewById(R.id.btn_editar_documento);
            IBEliminar = (ImageButton) itemView.findViewById(R.id.btn_eliminar_documento);
            IBDescarga = (ImageButton) itemView.findViewById(R.id.btn_descargar_documento);
            IBDescarga.setOnClickListener(this);
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

                Intent intent = new Intent(context, ActivityAgregar.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("objId", String.valueOf(listItems.get(getAdapterPosition()).get_id_dato()));
                intent.putExtra("fecha", String.valueOf(listItems.get(getAdapterPosition()).getFecha()));
                context.startActivity(intent);

            }else if(v == IBEliminar){
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(itemView.getContext());
                mBuilder.setIcon(android.R.drawable.ic_menu_delete);
                mBuilder.setTitle(R.string.ad_titulo);
                mBuilder.setMessage(R.string.ad_borrar);
                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //prueba
                        //listItems.remove(getAdapterPosition());
                        //notifyDataSetChanged();

                        //Toast.makeText(getApplicationContext(), R.string.ed_bo_eliminado_exito, Toast.LENGTH_SHORT).show();

                        //Proceso para eliminar la muestra de la base de datos!
                        eliminardato(listItems.get(getAdapterPosition()).get_id_dato());






                    }
                });
                mBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                final AlertDialog alertDialog = mBuilder.create();
                alertDialog.setOnShowListener( new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg0) {
                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#00c0f3"));
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                    }
                });
                alertDialog.show();

            }else if(v == IBDescarga){
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(itemView.getContext());
                mBuilder.setIcon(R.drawable.ic_descarga);
                mBuilder.setTitle(R.string.ad_descarga);
                mBuilder.setMessage(R.string.ad_descarga_m);
                mBuilder.setCancelable(false);
                mBuilder.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                mBuilder.setPositiveButton("CSV", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestWritePermission();
                        descargaMuestra(listItems.get(getAdapterPosition()).get_id_dato(), listItems.get(getAdapterPosition()).getFecha(),  false);

                    }
                });
                final AlertDialog alertDialog = mBuilder.create();
                alertDialog.setOnShowListener( new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg0) {
                        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#00c0f3"));
                    }
                });
                alertDialog.show();


            }else{
                //selecciona el card, entonces lo manda a activityMarker para mostrarle los datos que tiene!

                Intent intent = new Intent(context, ActivityMarker.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("objId", String.valueOf(listItems.get(getAdapterPosition()).get_id_dato()));
                context.startActivity(intent);

            }
        }

        private void DialogPermisos() {
            AlertDialog.Builder mBuilderPermisos = new AlertDialog.Builder(itemView.getContext());
            mBuilderPermisos.setIcon(android.R.drawable.ic_delete);
            mBuilderPermisos.setTitle(R.string.ad_falta_permiso);
            mBuilderPermisos.setMessage(R.string.ad_error_escritura);
            mBuilderPermisos.setCancelable(false);
            mBuilderPermisos.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            final AlertDialog alertDialogPermisos = mBuilderPermisos.create();
            alertDialogPermisos.setOnShowListener( new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    alertDialogPermisos.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#00c0f3"));
                }
            });
            alertDialogPermisos.show();
        }

        /**
         * Descarga la muestra del id por parametro y lo envia a escritura.
         *
         * @param id_dato
         */
        private void descargaMuestra(String id_dato, final String fecha , final boolean pdf) {

            Response.Listener<String> responseListener = new Response.Listener<String>() { //Respuesta del servidor
                @Override
                public void onResponse(String response) {
                    try {
                        Log.i("tagconvertstr", "[" + response + "]");
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success) { //Si salió bien descarga la muestra en formato pdf
                            JSONObject jsonObject = jsonResponse.getJSONObject("documentos");
                            if(pdf){
                                createPDF(jsonObject, fecha);
                            }else{
                                createCSV(jsonObject, fecha);
                            }


                            Toast.makeText(getApplicationContext(), R.string.ed_si_descarga, Toast.LENGTH_SHORT).show();

                        } else { // Si salio mal, le indica al usuario que salio mal y le deja volver a intentarlo
                            Toast.makeText(getApplicationContext(), R.string.ed_no_descarga, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            //inserta los datos a un Map para que se envien como parametros a la función que envia al servidor.
            Map<String, String> params;
            params = new HashMap<>();
            params.put("_id", id_dato);

            //Viejo = "http://192.168.138.1:8081/proyectoJavier/android/buscarID.php"
            //Servidor = getString(R.string.server)+"buscarID.php"
            String direccion = context.getString(R.string.server)+"buscarID.php";

            //Envia los datos al servidor
            MongoRequest loginMongoRequest = new MongoRequest(params, direccion, responseListener);
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(loginMongoRequest);


        }

        private void createCSV(JSONObject jsonObject, String fecha) {


            try {
                JSONObject jsonMuestra = jsonObject.getJSONObject("Muestra");
                String indiceEscogido = jsonMuestra.getString("indice_usado");
                String obj_id = jsonObject.getString("_id");
                obj_id = obj_id.substring(obj_id.length()-6, obj_id.length()-2);

                String fileName = indiceEscogido + "-" + fecha+ "-" + obj_id + ".csv";

                File file;

                if(permiso){
                    file = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS), fileName); //
                }else{
                    file = new File(context.getFilesDir(), fileName); //
                }



                file.createNewFile();
                FileOutputStream fOut = new FileOutputStream(file);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

                String llaves = "";
                String valores = "";



                JSONObject jsonObligatorios = jsonMuestra.getJSONObject("obligatorios");
                JSONObject jsonOpcionales = jsonMuestra.getJSONObject("opcionales");
                JSONObject jsonPOI = jsonObject.getJSONObject("POI");
                JSONObject jsonLocation = jsonPOI.getJSONObject("location");
                JSONObject jsonGeo = jsonPOI.getJSONObject("datos_geograficos");

                //MUESTRA
                Iterator<String> MuestraI = jsonMuestra.keys();
                //Mientras existan elementos que leer se insertan en la vista
                while(MuestraI.hasNext()){
                    String llave = String.valueOf(MuestraI.next());
                    if(!llave.equals("obligatorios") && !llave.equals("opcionales")){
                        llaves = llaves + llave + ",";
                        if(!llave.equals("fecha")){
                            valores = valores +  jsonMuestra.getString(llave) + ",";
                        }else{
                            valores = valores +  fecha + ",";
                        }

                    }
                }

                //OBLIGATORIOS
                Iterator<String> ObligatoriosI = jsonObligatorios.keys();
                //Mientras existan elementos que leer se insertan en la vista
                while(ObligatoriosI.hasNext()){
                    String llave = String.valueOf(ObligatoriosI.next());
                    llaves = llaves + llave + ",";
                    valores = valores +  jsonObligatorios.getString(llave) + ",";

                }

                //OPCIONALES
                Iterator<String> OpcionalesI = jsonOpcionales.keys();
                //Mientras existan elementos que leer se insertan en la vista
                while(OpcionalesI.hasNext()){
                    String llave = String.valueOf(OpcionalesI.next());
                    llaves = llaves + llave + ",";
                    valores = valores +  jsonOpcionales.getString(llave) + ",";

                }

                //POI
                Iterator<String> POII = jsonPOI.keys();
                //Mientras existan elementos que leer se insertan en la vista
                while(POII.hasNext()){
                    String llave = String.valueOf(POII.next());
                    if(!llave.equals("location") && !llave.equals("datos_geograficos")){
                        llaves = llaves + llave + ",";
                        valores = valores +  jsonPOI.getString(llave) + ",";
                    }
                }


                //LOCATION
                Iterator<String> LocationI = jsonLocation.keys();
                //Mientras existan elementos que leer se insertan en la vista
                while(LocationI.hasNext()){
                    String llave = String.valueOf(LocationI.next());
                    llaves = llaves + llave + ",";
                    valores = valores +  jsonLocation.getString(llave) + ",";

                }

                //DATOS GEOGRAFICOS
                Iterator<String> GeograficosI = jsonGeo.keys();
                //Mientras existan elementos que leer se insertan en la vista
                while(GeograficosI.hasNext()){
                    String llave = String.valueOf(GeograficosI.next());
                    llaves = llaves + llave + ",";
                    valores = valores +  jsonGeo.getString(llave) + ",";

                }

                myOutWriter.append(llaves);
                myOutWriter.append("\n");
                myOutWriter.append(valores);
                myOutWriter.append("\n");



                //But Above Code is not working for me
                myOutWriter.close();
                fOut.close();

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

        }

        private void createPDF(JSONObject jsonObject, String fecha) {

        }

        /**
         * Elimina el documento con el _id == id_dato del recyclerView.
         *
         * @param id_dato
         */
        private void eliminardato(String id_dato) {

            Response.Listener<String> responseListener = new Response.Listener<String>() { //Respuesta del servidor
                @Override
                public void onResponse(String response) {
                    try {
                        Log.i("tagconvertstr", "[" + response + "]");
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success) { //Si salió bien le enseña al usuario el valor calculado del indice y el color y vuelve a crear el activity para que pueda ingresar otro dato

                            Toast.makeText(getApplicationContext(), R.string.ed_bo_eliminado_exito, Toast.LENGTH_SHORT).show();
                            listItems.remove(getAdapterPosition());
                            notifyDataSetChanged();

                        } else { // Si salio mal, le indica al usuario que salio mal y le deja volver a intentarlo
                            Toast.makeText(getApplicationContext(), R.string.ed_bo_eliminado_error, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            //inserta los datos a un Map para que se envien como parametros a la función que envia al servidor.
            Map<String, String> params;
            params = new HashMap<>();
            params.put("_id", id_dato);

            //Viejo = "http://192.168.138.1:8081/proyectoJavier/android/eliminarDocumento.php"
            //Servidor = getString(R.string.server)+"eliminarDocumento.php"

            String direccion = context.getString(R.string.server)+"eliminarDocumento.php";

            //Envia los datos al servidor
            MongoRequest loginMongoRequest = new MongoRequest(params, direccion, responseListener);
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(loginMongoRequest);


        }


    }


}
