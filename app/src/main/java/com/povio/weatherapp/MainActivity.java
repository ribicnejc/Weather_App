package com.povio.weatherapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import jp.co.recruit_lifestyle.android.widget.*;

public class MainActivity extends AppCompatActivity {
    public static List<Data> datas = new ArrayList<>();
    //public static List<Data> a;
    private RecyclerView rv;
    public static TextView mTxtView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mTxtView = (TextView) findViewById(R.id.emptyRV);
        final WaveSwipeRefreshLayout swipeView = (WaveSwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeView.setColorSchemeColors(Color.WHITE, Color.WHITE);
        swipeView.setWaveColor(Color.argb(100, 7, 133, 171));
        swipeView.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                refreshItems(swipeView);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), CityQuery.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        // setup data
        datas = readState();

        //check if datas is empty
        emptyRecyclerView(datas);

        // setup recyclerview
        rv = (RecyclerView)findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayoutManager);
        rv.setHasFixedSize(true);

        // setup adapter
        final RVAdapter adapter = new RVAdapter(datas, this);
        rv.setAdapter(adapter);

        //setup ItemTouchHelper
        ItemTouchHelper.Callback callback = new CardTouchHelper(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(rv);
      }
    public void refreshItems(WaveSwipeRefreshLayout swipe){
        try{
            for (int i = 0; i < datas.size(); i++) {
                datas.get(i).refreshData(i);
            }
            rv.getAdapter().notifyDataSetChanged();
            rv.getAdapter().notifyItemRangeChanged(0, rv.getAdapter().getItemCount());
            emptyRecyclerView(datas);
            stillRefreshing(swipe, 0);
        }catch (Exception e){
            swipe.setRefreshing(false);
            Toast.makeText(this, "Nothing to refresh", Toast.LENGTH_SHORT).show();
        }
    }
    public void onItemsLoadComplete(final WaveSwipeRefreshLayout swipe){
        try{
            saveState(datas);
            swipe.setRefreshing(false);
            datas.get(datas.size() - 1).setRefreshingState(true);
            rv.getAdapter().notifyDataSetChanged();
            Toast.makeText(this, "Items refreshed", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this, "Nothing to refresh", Toast.LENGTH_SHORT).show();
        }

    }
    public void stillRefreshing(final WaveSwipeRefreshLayout view, final int count){
        try{
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onItemsLoadComplete(view);
                }
            }, datas.size() * 300);
        }catch (Exception e){
            Toast.makeText(this, "Nothing to refresh", Toast.LENGTH_SHORT).show();
        }

    }

    public static void addCity(String name, String weather, String type, int icon, String temp, String tempMin, String tempMax) {
        datas.add(0, new Data(name, weather, type, icon, temp, tempMax, tempMin));
        saveState(datas);
    }

    public final void emptyRecyclerView(List someList){
        if (someList.isEmpty()){
            TextView mTextView = (TextView) findViewById(R.id.emptyRV);
            mTextView.setText("City list is empty");
        }
        else{
            TextView mTextView = (TextView) findViewById(R.id.emptyRV);
            mTextView.setText("");
        }
    }
    public static void remove(int pos){
        try {
            datas.remove(pos);
            saveState(datas);
            if (datas.isEmpty()) {
                mTxtView.setText("City list is empty");
            } else {
                mTxtView.setText("");
            }
        }catch (Exception e){
            Log.d("Crash swipe", "removing");
        }
    }

    public static String getNameOfCity(int pos){
        String name;
        name = datas.get(pos).getCityName();
        return name;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_city){
            Intent intent = new Intent(getBaseContext(), CityQuery.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        }
        if (id == R.id.home){
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    public static void saveState(List<Data> objects){
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File("/storage/emulated/0/savedData.ser"));
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
            outputStream.writeObject(objects);
            outputStream.close();
            fileOutputStream.close();
        }catch (Exception e){
            // save log
        }


    }
    public List<Data> readState(){
        boolean check = new File("/storage/emulated/0/savedData.ser").exists();
        List<Data> list = new ArrayList<>();
        if (check){
            list = readData();
        }
        return list;
    }
    public List<Data> readData(){
        List<Data> list;
        try{
            FileInputStream fileInputStream = new FileInputStream(new File("/storage/emulated/0/savedData.ser"));
            ObjectInputStream in = new ObjectInputStream(fileInputStream);
            list = (ArrayList) in.readObject();





        }catch(Exception e){
            list = new ArrayList<>();
        }
        return list;
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Log.d("CDA", "onBackPressed Called");
            Intent setIntent = new Intent(Intent.ACTION_MAIN);
            setIntent.addCategory(Intent.CATEGORY_HOME);
            setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(setIntent);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
