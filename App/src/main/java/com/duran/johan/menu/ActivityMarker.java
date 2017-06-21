package com.duran.johan.menu;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Struct;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static android.R.attr.id;
import static android.os.Build.VERSION_CODES.M;
import static com.duran.johan.menu.R.id.fecha_ingresada;
import static com.duran.johan.menu.R.id.generales_exp;
import static com.duran.johan.menu.R.id.visible;


public class ActivityMarker extends AppCompatActivity {
    CarouselView carouselView;

    //Variable para manejo de colas de peticiones
    MySingleton singleton;
    RelativeLayout obligatorios;
    ExpandableLinearLayout content_obligatorios;
    RelativeLayout generales;
    ExpandableLinearLayout content_generales;
    RelativeLayout opcionales;
    ExpandableLinearLayout content_opcionales;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);
        //se agrega el boton de ir atras
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);



        //Elementos utilizados para hacer el efecto de toggle, que expande.
        generales=(RelativeLayout) findViewById(R.id.generalesMarker);
        content_generales=(ExpandableLinearLayout) findViewById(R.id.generales_expMarker);
        obligatorios=(RelativeLayout) findViewById(R.id.obligatoriosMarker);
        content_obligatorios=(ExpandableLinearLayout) findViewById(R.id.obligatorios_expMarker);
        opcionales=(RelativeLayout) findViewById(R.id.opcionalesMarker);
        content_opcionales=(ExpandableLinearLayout) findViewById(R.id.opcionales_expMarker);

        generales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content_generales.toggle();
            }
        });

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


        //se leen los valores que entran por parámetro
        Intent intent = getIntent();
        Bundle extras= intent.getExtras();
        String objId=extras.getString("objId"); // objId es el id del elementro dentro de la BD
        populateView(objId);//cargar de datos
    }

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

    private void populateView(String objId) {
        String file ="datosMarker_busqueda.php?id1="+objId;
        getRequest(file,1);
    }


    /*Método utilizado para realizar peticiones asincronas al servidor
    Parametro 1: nombre del archivo php de tener get debe ir incluido en el string
    Parametro 2: entero para control de opciones de peticiones
    Opciones parametro 2:
    1-> traer información del marcador con id = objID
    */

    public void getRequest(String file, final int num) {
        String dir = getString(R.string.server)+file; //se crea el directorio completo
        //inicio de la petición al servidor GET
        JsonArrayRequest jsArrRequest = new JsonArrayRequest
                (Request.Method.GET, dir, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        switch (num) {//Incluir los casos dependiendo de la cantidad de llamados distintos que se puedan hacer.
                            case 1://petición 1 cargar todos los marcadores
                                cargarMarcador(response);
                                break;
                            case 2:
                                //lo que se desea hacer para la petición 2

                                break;
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        //lo que se desea hacer en caso de error
                        Log.d("response",error.toString());
                        //en caso de error volvemos a la ventana principal
                        ActivityLauncher.startActivityB(ActivityMarker.this, MainActivity.class,true);
                    }
                });

        int socketTimeout = 3000;//tiempo de espera a la peticion
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsArrRequest.setRetryPolicy(policy);
        // Access the RequestQueue through your singleton class.
        singleton.getInstance(this).addToRequestQueue(jsArrRequest);
    }

    //Método llamado al obtenerse respuesta con datos de un marcador
    private void cargarMarcador(JSONArray response) {
        try {
            //se obtiene el documento que contiene los datos obligatorios, opcionales del punto
            JSONObject muestra= response.getJSONObject(Integer.parseInt("0")).getJSONObject("Muestra");
            JSONObject obligatorios = muestra.getJSONObject("obligatorios");
            JSONObject opcionales = muestra.getJSONObject("opcionales");

            //carousel
            boolean hasImages = !muestra.isNull("fotos");
            final JSONArray fotos = hasImages?muestra.getJSONArray("fotos"):null;
            boolean hayFotos=false;
            int cantidadFotos=1;
            if(fotos!=null){
                hayFotos=fotos.length()>0?true:false;
                cantidadFotos=fotos.length();
            }
            cargarCarousel(fotos,hayFotos,cantidadFotos);


            //Retroalimentación
            boolean existColor = !muestra.isNull("color");
            String color = existColor?muestra.getString("color"):"ND";
            retroalimentar(color,obligatorios,opcionales);

            //inserción de datos generales
            insertarGenerales(muestra);

            //complatado de datos obligatorios
            insertarDatosDropDown(obligatorios,(GridView)findViewById(R.id.GridViewObligatoriosMarker));
            insertarDatosDropDown(opcionales,(GridView)findViewById(R.id.GridViewOpcionalesMarker));

            //reinicio de los contenidos para que reconozca que se han insertado de forma dinámica
            content_generales.initLayout();
            content_obligatorios.initLayout();
            content_opcionales.initLayout();




        } catch (JSONException e) {
            //en caso de error se vuelve a la actividad anterior
            ActivityLauncher.startActivityB(ActivityMarker.this, MainActivity.class,true);
            finish();
            e.printStackTrace();
        }

    }


    public void insertarGenerales(JSONObject muestra) throws JSONException {
        //obtención de campos
        boolean hasDate=true;
        String formattedDate="";
        try {
            String sUnixSeconds = muestra.getJSONObject("fecha").getJSONObject("$date").getString("$numberLong");
            sUnixSeconds = sUnixSeconds.substring(0,10);
            Long unixSeconds = Long.parseLong(sUnixSeconds);
            Date date = new Date(unixSeconds*1000L); // *1000 is to convert seconds to milliseconds
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
            sdf.setTimeZone(TimeZone.getTimeZone("GMT-6")); // give a timezone reference for formating (see comment at the bottom
            formattedDate = sdf.format(date);
            formattedDate=formattedDate.substring(0,10);
        }catch(Exception e){
            hasDate=false;
        }

        String usuario = !muestra.isNull("usuario")?muestra.getString("usuario"):null;
        String indice_usado = !muestra.isNull("indice_usado")?muestra.getString("indice_usado"):null;
        String val_indice = !muestra.isNull("val_indice")?muestra.getString("val_indice"):null;

        final ArrayList<String> items= new ArrayList<String>();
        if(usuario!=null){
            items.add("Usuario");
            items.add(usuario);
        }

        if(indice_usado!=null){
            items.add("Índice");
            items.add(indice_usado);
        }

        if(val_indice!=null){
            items.add("Valor del índice");
            items.add(val_indice);
        }

        if(hasDate){
            items.add("Fecha");
            items.add(formattedDate);
        }

        //se adaptan los datos al gridview de obligatorios
        GridView gridView = (GridView)findViewById(R.id.GridViewGeneralesMarker);
        int cantidadVertical=items.size()/2;//cantidad de filas
        ViewGroup.LayoutParams layoutParams = gridView.getLayoutParams();
        layoutParams.height = convertDpToPixels(40*cantidadVertical,ActivityMarker.this); //se modifica la altura de los elementos a mostrar
        gridView.setLayoutParams(layoutParams);

        GridViewAdapter gridAdapter = new GridViewAdapter (ActivityMarker.this, items,0);//0 means Marker style
        gridView.setAdapter(gridAdapter);
    }

    public void cargarCarousel(final JSONArray fotos, final boolean hayFotos,int cantidadFotos){

        carouselView = (CarouselView) findViewById(R.id.carouselView);
        //carousel
        ImageListener imageListener = new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                if(hayFotos){
                    try {
                        Picasso.with(getApplicationContext()).load(fotos.getString(position)).fit().centerCrop().into(imageView);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    String urlImage = "http://wikicode.xyz/monitoreo.png";
                    Picasso.with(getApplicationContext()).load(urlImage).fit().into(imageView);
                }

            }
        };

        carouselView.setImageListener(imageListener);
        carouselView.setPageCount(cantidadFotos);

    }

    /*
    * complatado de datos obligatorios u opcionales
    * los datos son el objecto JSON a ser insertado con el adapter
    * gridView es donde los datos son insertados
    * */
    public void insertarDatosDropDown(JSONObject datos,GridView gridView) throws JSONException {
        final ArrayList<String> items= new ArrayList<String>();
        Iterator<String> obligatoriosK = datos.keys();
        //Mientras existan elementos que leer se insertan en la vista
        while(obligatoriosK.hasNext()){
            String llave=String.valueOf(obligatoriosK.next());
            String valor =datos.getString(llave);
            items.add(llave);
            items.add(valor);
        }
        //se adaptan los datos al gridview de obligatorios
        int cantidadVertical=items.size()/2;//cantidad de filas
        ViewGroup.LayoutParams layoutParams = gridView.getLayoutParams();
        layoutParams.height = convertDpToPixels(40*cantidadVertical,ActivityMarker.this); //se modifica la altura de los elementos a mostrar
        gridView.setLayoutParams(layoutParams);

        GridViewAdapter gridAdapter = new GridViewAdapter (ActivityMarker.this, items,0);//0 means Marker style
        gridView.setAdapter(gridAdapter);
    }

    /*
     *
     * Seccion de encabezado:
     * Se le coloca el color acorde al del cargarMarcadores
     * Se inserta el texto para los valores de T asociados
     * si existT = false se muestra el mensaje T no existe
     * Si isTObligatorio = false entonces es opcional
     * T_value = -1 indica que T no existe
     *
     * */
    public void retroalimentar(String color,JSONObject obligatorios,JSONObject opcionales) throws JSONException {
        boolean tObligatorios=!obligatorios.isNull("T");
        boolean tOpcionales=!opcionales.isNull("T");

        TextView datosGenerales=(TextView)findViewById(R.id.txtFeedbackMarker);
        datosGenerales.setBackgroundResource(getColor(color));//se le da color segun color del marcador
        boolean amarillo=color.equals("Amarillo");
        boolean verde=color.equals("Verde");
        boolean isBlack=amarillo||verde;// si es verde o amarillo la letra es negra
        if(!isBlack){
            datosGenerales.setTextColor(0xFFFFFFFF);
        }

        //insercion del texto que brinda retroalimentacion

        if(tObligatorios){//si la temeperatura es parte de los datos obligatorios
            String T=obligatorios.getString("T");//getDouble("T");
            if(T.equals("ND")){

                datosGenerales.setText(getString(R.string.no_hay_T));
            }else{
                double DatoT =obligatorios.getDouble("T");
                datosGenerales.setText(getFeedBack(DatoT));
            }
        }else if(tOpcionales){//si es parte de los opcionales
            String T=opcionales.getString("T");//getDouble("T");
            if(T.equals("ND")){

                datosGenerales.setText(getString(R.string.no_hay_T));
            }else{
                double DatoT =opcionales.getDouble("T");
                datosGenerales.setText(getFeedBack(DatoT));
            }
        }
    }


    /*
    *
    * Retorna un valor en pixeles asociado a unDP
    *
    * */
    public static int convertDpToPixels(float dp, Context context){
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resources.getDisplayMetrics()
        );
    }



    /*
    * Colores asociados a cada tipo de indice
    * En caso de no ser un color valido se retorna gris
    * */
    private int getColor(String color) {
        switch (color) {
            case "Azul":
                return R.color.material_blue_500;
            case "Verde":
                return R.color.colorAccent;
            case "Amarillo":
                return R.color.material_yellow_A200;
            case "Anaranjado":
                return R.color.material_orange_A400;

            case "Rojo":
                return R.color.material_red_600;
            default:
                return R.color.gray;
        }
    }


    /*
    * Retorna el feedback para una temperatura dada
    *
    */
    private  String getFeedBack(double temperatura){
        String resultado="Para los niveles de OD presentes en este POI se preveé:\n";
        if(temperatura>=26){//OD 7
            return resultado+"producción de truchas severamente deteriorada";
        }else if(temperatura>=20){//OD 8
            return resultado+"producción no deteriorada de peces no salmónidos (e.g. truchas) ";
        }else if(temperatura>=14){//OD 9
            return resultado+"producción no deteriorada de peces salmónidos, e.g. truchas";
        }else if(temperatura>=10){//OD 10
            return resultado+"Dato no definido";
        }else if(temperatura>=7){//OD 11
            return resultado+"Dato no definido";
        }else if(temperatura>=4){//OD 12
            return resultado+"Dato no definido";
        }else{
            return "No definido";
        }
    }
}
