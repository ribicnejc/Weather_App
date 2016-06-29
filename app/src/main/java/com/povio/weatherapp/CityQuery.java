package com.povio.weatherapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class CityQuery extends AppCompatActivity {

    final Shimmer shimmer = new Shimmer();
    EditText editTextCityName;
    TextView add;
    ShimmerButton btnByCityName;
    TextView textViewResult, textViewInfo;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_query);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Search city");
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        editTextCityName = (EditText)findViewById(R.id.cityname);
        btnByCityName = (ShimmerButton)findViewById(R.id.bycityname);
        shimmer.setDuration(2000);
        shimmer.start(btnByCityName);
        add = (TextView)findViewById(R.id.add);
        //textViewResult = (TextView)findViewById(R.id.result);
        //textViewResult.setText("");
        textViewInfo = (TextView)findViewById(R.id.info);
        btnByCityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add.setText("Searching...");
                progressBar.setVisibility(View.VISIBLE);
                GetWeatherInfoAPI api = new GetWeatherInfoAPI(editTextCityName.getText().toString());
                waitForApi(api);
            }
        });
    }

    public void waitForApi(final GetWeatherInfoAPI api){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (api.success) {
                    onFinish(api);
                } else {
                    waitForApi(api);
                }
            }
        }, 100);
    }
    public void onFinish(final GetWeatherInfoAPI api) {
        add.setText("Search city");
        progressBar.setVisibility(View.GONE);
        if (api.getMainDesc().equals(":(")) {
            Toast.makeText(CityQuery.this, "That place does not exist", Toast.LENGTH_SHORT).show();
        } else {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            MainActivity.addCity(api.getCityName(), api.getIconDesc(), api.getMainDesc(), api.getIcon(), api.getMainTemp(), api.getMinTemp(), api.getMaxTemp());
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                            Toast.makeText(CityQuery.this, api.getCityName() +" added", Toast.LENGTH_SHORT).show();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(CityQuery.this);
            builder.setMessage("Found as: " + api.getCityName() + ", " + api.getCountry()).setPositiveButton("Add", dialogClickListener)
                    .setNegativeButton("Cancel", dialogClickListener).show();
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
        if (id == R.id.add_city){
            Intent intent = new Intent(getBaseContext(), CityQuery.class);
            startActivity(intent);
        }
        if (id == R.id.home){
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        }
        if (id == android.R.id.home){
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
