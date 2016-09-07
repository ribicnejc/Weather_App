package com.povio.weatherapp.Adapters;
import android.content.Context;
import android.graphics.Typeface;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.povio.weatherapp.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HorizontalRVAdapter extends RecyclerView.Adapter<HorizontalRVAdapter.MyViewHolder> {
    Context mContext;
    private List<String> time;
    private List<Integer> picture;
    private List<String> temperature;
    private List<String> dateTime;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView timeTxtView;
        public ImageView imgView;
        public TextView tempTxtView;
        public Typeface type;
        public TextView rail;
        public TextView day1, day2;

        public MyViewHolder(View view) {
            super(view);
            timeTxtView = (TextView) view.findViewById(R.id.horTime);
            imgView = (ImageView) view.findViewById(R.id.horIcon);
            tempTxtView = (TextView) view.findViewById(R.id.horTemp);
            type = Typeface.createFromAsset(view.getContext().getAssets(), "openSansLight.ttf");
            rail = (TextView) view.findViewById(R.id.rail);
//            day1 = (TextView) view.findViewById(R.id.horDay1);
//            day2 = (TextView) view.findViewById(R.id.horDay2);
        }
    }


    public HorizontalRVAdapter(ArrayList<String> time, ArrayList<Integer> picture, ArrayList<String> temperature, ArrayList<String> dateTime) {
        this.time = time;
        this.picture = picture;
        this.temperature = temperature;
        this.dateTime = dateTime;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.horizontal_recycler_view3, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.timeTxtView.setText(this.time.get(position));
        holder.imgView.setImageResource(this.picture.get(position));
        holder.tempTxtView.setText(Math.round(Double.parseDouble(this.temperature.get(position).replace(',','.')))+"°");
        holder.timeTxtView.setTypeface(holder.type);
        holder.tempTxtView.setTypeface(holder.type);
        holder.rail.setTypeface(holder.type);



        if(this.time.get(position).equals("21:00")){
            holder.rail.setVisibility(View.VISIBLE);
            SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
            try{
                Date MyDate = newDateFormat.parse(this.dateTime.get(position));
                newDateFormat.applyPattern("EEEE");
                String day = newDateFormat.format(MyDate).substring(0, 3);
                day = getPlusDay(day);
                holder.rail.setText(day);
            }catch (Exception e){
                Log.d("Setting day","went wrong");
            }
        }else{
            holder.rail.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return time.size();
    }

    private String getPlusDay(String day){
        if (day.equals("Sun"))
            return "Mon";
        if (day.equals("Mon"))
            return "Tue";
        if (day.equals("Tue"))
            return "Wed";
        if (day.equals("Wed"))
            return "Thu";
        if (day.equals("Thu"))
            return "Fri";
        if (day.equals("Fri"))
            return "Sat";
        else return "Sun";
    }
}



