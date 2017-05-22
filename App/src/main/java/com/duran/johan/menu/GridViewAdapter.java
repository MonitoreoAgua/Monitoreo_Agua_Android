package com.duran.johan.menu;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by johan on 13/3/2017.
 */

public class GridViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> items;
    LayoutInflater inflater;
    int styleType;
    int rowIndex;
    int back;
    public GridViewAdapter(Context context, ArrayList<String> items, int activityNStyle) {
        back=R.color.material_blue_grey_50;
        rowIndex=0;
        this.context = context;
        this.items = items;
        this.styleType=activityNStyle;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.cell, null);
        }
        TextView  field = (TextView) convertView.findViewById(R.id.grid_item);
        if(styleType==0){//estilo para marker activity
            field.setBackgroundResource(back);//se le asigna un borde
            rowIndex=(rowIndex+1)%2;
            if(rowIndex==0){//==4
                if(back==R.color.material_blue_grey_50){
                    back=R.color.material_blue_grey_100;
                }else{
                    back=R.color.material_blue_grey_50;
                }
            }
        }else if(styleType==1){//estilo para aritmetica de POIS
            field.setBackgroundResource(back);//se cambia el color para cada fila
            if(rowIndex==0){//==4
                if(back==R.color.material_blue_grey_50){
                    back=R.color.material_blue_grey_100;
                }else{
                    back=R.color.material_blue_grey_50;
                }
            }
            rowIndex=(rowIndex+1)%5;
        }
        field.setText(items.get(position));

        return convertView;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}