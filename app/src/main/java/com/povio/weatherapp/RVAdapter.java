package com.povio.weatherapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.InfoViewHolder> {
    private Context mContext;
    private int lastPosition = -1;

    public static class InfoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View.OnClickListener clickListener;
        CardView cv;
        TextView cityName;
        TextView minTemp;
        TextView maxTemp;
        TextView temp;
        TextView weatherDesc;
        TextView buttonTest;
        TextView backGroundTemp;
        ImageView weatherPhoto;
        public Typeface typeface;
        FrameLayout container;

        InfoViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            cv = (CardView) itemView.findViewById(R.id.cv2);
            cityName = (TextView) itemView.findViewById(R.id.cityName);
            weatherDesc = (TextView) itemView.findViewById(R.id.weatherDesc);
            temp = (TextView) itemView.findViewById(R.id.mainDegrees);
            minTemp = (TextView) itemView.findViewById(R.id.minDegrees);
            maxTemp = (TextView) itemView.findViewById(R.id.maxDegrees);
            buttonTest = (TextView) itemView.findViewById(R.id.buttonMore);
            weatherPhoto = (ImageView) itemView.findViewById(R.id.weatherIcon);
            backGroundTemp = (TextView) itemView.findViewById(R.id.card_view_background_temp);
            container = (FrameLayout) itemView.findViewById(R.id.item_layout_container2);
            typeface = Typeface.createFromAsset(itemView.getContext().getAssets(), "openSansLight.ttf");
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

    public RVAdapter(List<Data> datas, Context ctx) {
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
    public void onBindViewHolder(final InfoViewHolder weatherViewHolder, final int i) {
        //weatherViewHolder.cityName.setTypeface(type);
        weatherViewHolder.cityName.setText(datas.get(i).getCityName());
        weatherViewHolder.weatherDesc.setText(datas.get(i).getType());
        weatherViewHolder.temp.setText(datas.get(i).getTemp());
        weatherViewHolder.minTemp.setText("min:  " + datas.get(i).getMinTemp() + "°");
        weatherViewHolder.maxTemp.setText("max: " + datas.get(i).getMaxTemp() + "°");
        weatherViewHolder.weatherPhoto.setImageResource(datas.get(i).weatherIcon);

        weatherViewHolder.cityName.setTypeface(weatherViewHolder.typeface);
        weatherViewHolder.weatherDesc.setTypeface(weatherViewHolder.typeface);
        weatherViewHolder.temp.setTypeface(weatherViewHolder.typeface);
        weatherViewHolder.minTemp.setTypeface(weatherViewHolder.typeface);
        weatherViewHolder.maxTemp.setTypeface(weatherViewHolder.typeface);

        GradientDrawable gdDefault = new GradientDrawable();
        gdDefault.setColor(getRightColor(datas.get(i).getTemp()));
        gdDefault.setCornerRadius(10);
        weatherViewHolder.backGroundTemp.setBackground(gdDefault);
        weatherViewHolder.cv.setCardElevation(0);
        weatherViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MoreInfoActivity.class);
                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra("city", datas.get(i).getCityName());
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });
        weatherViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                remove(i, weatherViewHolder.itemView);
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
        setAnimation(weatherViewHolder.container, i);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }


    public void remove(final int position, View view) {
        final Data deleted = datas.get(position);
        final Snackbar snackbar = Snackbar.make(view, datas.get(position).getCityName() + " has been removed", Snackbar.LENGTH_LONG);
        MainActivity.remove(position);
        if (datas.size() == 0)
            MainActivity.mTxtView.setVisibility(View.VISIBLE);
        notifyItemRemoved(position);
        MainActivity.saveState(datas);
        notifyItemRangeChanged(position, getItemCount());
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datas.add(position, deleted);
                MainActivity.mTxtView.setVisibility(View.GONE);
                MainActivity.saveState(datas);
                notifyDataSetChanged();
                notifyItemRangeChanged(position, getItemCount());
            }
        });


        snackbar.show();
    }

    public int getRightColor(String temp){
        float t = Float.parseFloat(temp.replace(",","."));
        if(t < -20){
            return ContextCompat.getColor(mContext, R.color.colorChart0);
        }else if (t >= -20 && t < -16){
            return ContextCompat.getColor(mContext, R.color.colorChart1);
        }else if (t >= -16 && t < -12){
            return ContextCompat.getColor(mContext, R.color.colorChart2);
        }else if (t >= -12 && t < -8){
            return ContextCompat.getColor(mContext, R.color.colorChart3);
        }else if (t >= -8 && t < -4){
            return ContextCompat.getColor(mContext, R.color.colorChart4);
        }else if (t >= -4 && t < 0){
            return ContextCompat.getColor(mContext, R.color.colorChart5);
        }else if (t >= 0 && t < 4){
            return ContextCompat.getColor(mContext, R.color.colorChart6);
        }else if (t >= 4 && t < 8){
            return ContextCompat.getColor(mContext, R.color.colorChart7);
        }else if (t >= 8 && t < 12){
            return ContextCompat.getColor(mContext, R.color.colorChart8);
        }else if (t >= 12 && t < 16){
            return ContextCompat.getColor(mContext, R.color.colorChart9);
        }else if (t >= 16 && t < 20){
            return ContextCompat.getColor(mContext, R.color.colorChart10);
        }else if (t >= 20 && t < 24){
            return ContextCompat.getColor(mContext, R.color.colorChart11);
        }else if (t >= 24 && t < 28){
            return ContextCompat.getColor(mContext, R.color.colorChart12);
        }else{
            return ContextCompat.getColor(mContext, R.color.colorChart13);
        }
    }
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
        if (position < lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_right_recycler_horizontal);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}


