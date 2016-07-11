package com.povio.weatherapp;

import com.povio.weatherapp.APIs.*;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.os.Handler;

import jp.co.recruit_lifestyle.android.widget.*;

public class MainActivity extends AppCompatActivity {
    public static List<Data> datas = new ArrayList<>();
    //public static List<Data> a;
    private RecyclerView rv;
    public static TextView mTxtView;
    public ImageView background;
    public static Context mContext;

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int hasWritePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int hasReadPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            int hasCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);

            List<String> permissions = new ArrayList<String>();
            if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            }

            if (hasReadPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);

            }

            if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.CAMERA);

            }
            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 111);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 111: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        System.out.println("Permissions --> " + "Permission Granted: " + permissions[i]);


                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        System.out.println("Permissions --> " + "Permission Denied: " + permissions[i]);

                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_appbar);

        background = (ImageView) findViewById(R.id.background);
        background.setImageBitmap(
                decodeSampledBitmapFromResource(getResources(), R.id.background, 100, 100)
        );
        background.setImageResource(R.drawable.bg_main2);
        mTxtView = (TextView) findViewById(R.id.emptyRV);
        Typeface type = Typeface.createFromAsset(getAssets(), "openSansLight.ttf");
        mTxtView.setTypeface(type);
        final WaveSwipeRefreshLayout swipeView = (WaveSwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeView.setColorSchemeColors(Color.WHITE, Color.WHITE);
        swipeView.setWaveColor(Color.argb(100, 7, 133, 171));
        swipeView.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                refreshItems(swipeView, 0);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer ,(DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), CityQuery.class);
                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        // setup data
        datas = readState();

        //check if datas is empty
        emptyRecyclerView(datas);

        // setup recyclerview
        rv = (RecyclerView) findViewById(R.id.recyclerView);
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

    public void refreshItems(final WaveSwipeRefreshLayout swipe, int pos) {
        if (pos < datas.size()) {
            RefreshData refreshData = new RefreshData(datas.get(pos).getCityName(), pos);
            refreshData.start();
            refreshing(pos, refreshData, swipe);
        } else if (pos == 0) {
            Toast.makeText(this, "Nothing to refresh", Toast.LENGTH_SHORT).show();
            onItemsLoadComplete(swipe);
        } else {
//            Toast.makeText(this, "Items refreshed", Toast.LENGTH_SHORT).show();
            onItemsLoadComplete(swipe);
        }
    }

    public void refreshing(final int pos, final RefreshData refreshData, final WaveSwipeRefreshLayout swipe) {
        try {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (refreshData.api.success) {
                        int tmp = pos;
                        tmp += 1;
                        refreshItems(swipe, tmp);
                    } else {
                        refreshing(pos, refreshData, swipe);
                    }
                }
            }, 100);
        } catch (Exception e) {
            Toast.makeText(this, "Nothing to refresh", Toast.LENGTH_SHORT).show();
        }
    }

    public void onItemsLoadComplete(final WaveSwipeRefreshLayout swipe) {
        try {

            saveState(datas);
            swipe.setRefreshing(false);
            datas.get(datas.size() - 1).setRefreshingState(true);
            rv.getAdapter().notifyDataSetChanged();
            Toast.makeText(this, "Items refreshed", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Nothing to refresh", Toast.LENGTH_SHORT).show();
        }

    }

    public static void addCity(String name, String weather, String type, int icon, String temp, String tempMin, String tempMax) {
        datas.add(0, new Data(name, weather, type, icon, temp, tempMax, tempMin));
        saveState(datas);
    }

    public final void emptyRecyclerView(List someList) {
        if (someList.isEmpty()) {
            mTxtView.setVisibility(View.VISIBLE);
            //mTextView.setVisibility(View.GONE);
        } else {
            mTxtView.setVisibility(View.GONE);
            //mTextView.setVisibility(View.INVISIBLE);
        }
    }

    public static void remove(int pos) {
        try {
            datas.remove(pos);
            saveState(datas);
            if (datas.isEmpty()) {
                mTxtView.setVisibility(View.VISIBLE);
            } else {
                mTxtView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.d("Crash swipe", "removing");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_city) {
            Intent intent = new Intent(getBaseContext(), CityQuery.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        }
        if (id == R.id.home) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
        }
        if (id == R.id.sort_icon){
            Collections.sort(datas, new Comparator<Data>() {
                public int compare(Data d1, Data d2) {
                    return d1.getCityName().compareToIgnoreCase(d2.getCityName());
                }
            });
            rv.getAdapter().notifyDataSetChanged();

        }
        return super.onOptionsItemSelected(item);
    }

    public static void saveState(List<Data> objects) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File("/storage/emulated/0/savedData.ser"));
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
            outputStream.writeObject(objects);
            outputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            // save log
        }


    }

    public List<Data> readState() {
        boolean check = new File("/storage/emulated/0/savedData.ser").exists();
        List<Data> list = new ArrayList<>();
        if (check) {
            list = readData();
        }
        return list;
    }

    public List<Data> readData() {
        List<Data> list;
        try {
            FileInputStream fileInputStream = new FileInputStream(new File("/storage/emulated/0/savedData.ser"));
            ObjectInputStream in = new ObjectInputStream(fileInputStream);
            list = (ArrayList) in.readObject();


        } catch (Exception e) {
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
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
