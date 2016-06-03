package com.povio.weatherapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.InfoViewHolder> {
    private Context mContext;
    public static class InfoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private View.OnClickListener clickListener;
        CardView cv;
        TextView cityName;
        TextView minTemp;
        TextView maxTemp;
        TextView temp;
        TextView weatherDesc;
        TextView buttonTest;
        ImageView weatherPhoto;

        InfoViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            cv = (CardView)itemView.findViewById(R.id.cv2);
            cityName = (TextView)itemView.findViewById(R.id.cityName);
            weatherDesc = (TextView)itemView.findViewById(R.id.weatherDesc);
            temp = (TextView)itemView.findViewById(R.id.mainDegrees);
            minTemp = (TextView)itemView.findViewById(R.id.minDegrees);
            maxTemp = (TextView)itemView.findViewById(R.id.maxDegrees);
            buttonTest = (TextView)itemView.findViewById(R.id.buttonMore);
            weatherPhoto = (ImageView)itemView.findViewById(R.id.weatherIcon);
        }

        public void setClickListener(View.OnClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }
        @Override
        public void onClick(View v) {
            clickListener.onClick(v);
        }
    }

    List<Data> datas;

    public RVAdapter(List<Data> datas, Context ctx){
        this.datas = datas;
        this.mContext = ctx;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public InfoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_update, viewGroup, false);
        InfoViewHolder pvh = new InfoViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(InfoViewHolder weatherViewHolder, final int i) {
        //weatherViewHolder.cityName.setTypeface(type);
        weatherViewHolder.cityName.setText(datas.get(i).getCityName());
        weatherViewHolder.weatherDesc.setText(datas.get(i).getType());
        weatherViewHolder.temp.setText(datas.get(i).getTemp());
        weatherViewHolder.minTemp.setText("min:  " + datas.get(i).getMinTemp() + "°");
        weatherViewHolder.maxTemp.setText("max: " + datas.get(i).getMaxTemp() + "°");
        weatherViewHolder.weatherPhoto.setImageResource(datas.get(i).weatherIcon);
        //weatherViewHolder.cv.setBackgroundColor(Color.TRANSPARENT);
        weatherViewHolder.cv.setCardElevation(0);
        weatherViewHolder.buttonTest.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext, "More info", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(mContext, MoreInfoActivity.class);
                //intent.putExtra("city", datas.get(i).getCityName());
                //mContext.startActivity(intent);
                //((Activity) mContext).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });
        weatherViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MoreInfoActivity.class);
                intent.putExtra("city", datas.get(i).getCityName());
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });
        weatherViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                MainActivity.remove(i);
                                notifyDataSetChanged();
                                notifyItemRangeChanged(0, getItemCount());
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Remove " + datas.get(i).getCityName() + " from the list?").setPositiveButton("Remove", dialogClickListener)
                        .setNegativeButton("Cancel", dialogClickListener).show();


                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }


    public void remove(final int position, View view){
        final Data deleted = datas.get(position);
        final Snackbar snackbar = Snackbar.make(view, datas.get(position).getCityName()+" has been removed", Snackbar.LENGTH_LONG);
        MainActivity.remove(position);
        notifyItemRemoved(position);
        MainActivity.saveState(datas);
        notifyItemRangeChanged(position, getItemCount());
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datas.add(position, deleted);
                MainActivity.saveState(datas);
                notifyDataSetChanged();
                notifyItemRangeChanged(position, getItemCount());
            }
        });


        snackbar.show();
        }



    }


