package com.monitoreo.agua.android;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

public class Aforo_Section_Adapter extends RecyclerView.Adapter<Aforo_Section_Adapter.MyViewHolder>{
    private Context mContext;
    private List<Aforo_Section> sectionList;
    private Button btnCalcularAforo;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public EditText distance_aforo, depth_aforo, speed_aforo, area_section_aforo, discharge_section_aforo, comment_section_aforo;
        public Button btnDelete;

        public MyViewHolder(View view) {
            super(view);
            distance_aforo = (EditText) view.findViewById(R.id.distance_aforo);
            depth_aforo = (EditText) view.findViewById(R.id.depth_aforo);
            speed_aforo = (EditText) view.findViewById(R.id.speed_aforo);
            area_section_aforo = (EditText) view.findViewById(R.id.area_section_aforo);
            discharge_section_aforo = (EditText) view.findViewById(R.id.discharge_section_aforo);
            comment_section_aforo = (EditText) view.findViewById(R.id.comment_section_aforo);
            btnDelete = (Button) view.findViewById(R.id.edit_section_btn);
        }
    }


    public Aforo_Section_Adapter(Context mContext, List<Aforo_Section> sectionList, Button btnCalcularAforo) {
        this.mContext = mContext;
        this.sectionList = sectionList;
        this.btnCalcularAforo = btnCalcularAforo;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_section_aforo, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Aforo_Section section = sectionList.get(position);
        holder.distance_aforo.setText(Double.toString(section.getDistance()));
        holder.depth_aforo.setText(Double.toString(section.getDepth()));
        holder.speed_aforo.setText(Double.toString(section.getSpeed()));
        holder.area_section_aforo.setEnabled(false);
        holder.discharge_section_aforo.setEnabled(false);
        holder.comment_section_aforo.setText(section.getComment());


        // Listener para el botón de eliminar
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Logic extracted from https://stackoverflow.com/a/42040855/4955145 on 2019-07-04
                if (position == sectionList.size() - 1 || position == 0) { // if last or first element are deleted, no need to shift
                    sectionList.remove(position);
                    notifyItemRemoved(position);
                    if (sectionList.size() == 0) {
                        btnCalcularAforo.setEnabled(false);
                    }
                } else { // if the element deleted is not the last one
                    int shift=1; // not zero, shift=0 is the case where position == dataList.size() - 1, which is already checked above
                    while (true) {
                        try {
                            sectionList.remove(position-shift);
                            notifyItemRemoved(position);
                            break;
                        } catch (IndexOutOfBoundsException e) { // if fails, increment the shift and try again
                            shift++;
                        }
                    }
                }
                // Logic extracted from https://stackoverflow.com/a/42040855/4955145 on 2019-07-04
            }
        });
    }

    @Override
    public int getItemCount() {
        return sectionList.size();
    }
}
