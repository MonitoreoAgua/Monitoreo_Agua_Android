package com.monitoreo.agua.aforo.android;

/**
 * Created by kenca on 20/03/2017.
 */

public class Lista_items_editar_borrar {

    private String _id_dato;
    private String indice_usado;
    private String valor_calculado;
    private String color_mostrado;
    private String fecha;
    private String tipo;


    public Lista_items_editar_borrar(String _id_dato, String indice_usado, String valor_calculado, String color_mostrado, String fecha, String tipo) {
        this._id_dato = _id_dato;
        this.indice_usado = indice_usado;
        this.valor_calculado = valor_calculado;
        this.color_mostrado = color_mostrado;
        this.fecha = fecha;
        this.tipo = tipo;
    }

    public String get_id_dato() {
        return _id_dato;
    }

    public String getIndice_usado() {
        return indice_usado;
    }

    public String getValor_calculado() {
        return valor_calculado;
    }

    public String getColor_mostrado() {
        return color_mostrado;
    }

    public String getFecha() {
        return fecha;
    }

    public String getTipo() {
        return tipo.substring(0,1).toUpperCase() + tipo.substring(1);
    }
}
