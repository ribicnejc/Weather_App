package com.povio.weatherapp;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.widget.Toast;


public class CardTouchHelper extends ItemTouchHelper.SimpleCallback {
    private RVAdapter mRVAdapter;

    public CardTouchHelper(RVAdapter mRVAdapter){
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.mRVAdapter = mRVAdapter;
    }


    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        try {
            mRVAdapter.remove(viewHolder.getAdapterPosition(), viewHolder.itemView);
        }catch(Exception e){
            Log.d("Swipe crash", "All swiped");
        }

    }
}
