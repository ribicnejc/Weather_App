package com.povio.weatherapp.Adapters;
import android.content.Context;
import android.graphics.Typeface;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.povio.weatherapp.*;

import java.util.ArrayList;
import java.util.List;

public class HorizontalRVAdapter extends RecyclerView.Adapter<HorizontalRVAdapter.MyViewHolder> {
    Context mContext;
    private List<String> time;
    private List<Integer> picture;
    private List<String> temperature;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView timeTxtView;
        public ImageView imgView;
        public TextView tempTxtView;
        public Typeface type;
        public TextView rail;

        public MyViewHolder(View view) {
            super(view);
            timeTxtView = (TextView) view.findViewById(R.id.horTime);
            imgView = (ImageView) view.findViewById(R.id.horIcon);
            tempTxtView = (TextView) view.findViewById(R.id.horTemp);
            type = Typeface.createFromAsset(view.getContext().getAssets(), "openSansLight.ttf");
            rail = (TextView) view.findViewById(R.id.rail);
        }
    }


    public HorizontalRVAdapter(ArrayList<String> time, ArrayList<Integer> picture, ArrayList<String> temperature) {
        this.time = time;
        this.picture = picture;
        this.temperature = temperature;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.horizontal_rec_view_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.timeTxtView.setText(this.time.get(position));
        holder.imgView.setImageResource(this.picture.get(position));
        holder.tempTxtView.setText(Math.round(Double.parseDouble(this.temperature.get(position).replace(',','.')))+"°");
        holder.timeTxtView.setTypeface(holder.type);
        holder.tempTxtView.setTypeface(holder.type);
        if(this.time.get(position).equals("21:00")){
            holder.rail.setVisibility(View.VISIBLE);
        }else{
            holder.rail.setVisibility(View.GONE);
        }
        /*holder.txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, holder.txtView.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return time.size();
    }
}



