package com.povio.weatherapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.povio.weatherapp.DataBase.DatabaseConnector;

import java.util.Calendar;
import java.util.Locale;

public class CityQuery extends AppCompatActivity {
    Button btnByCityName;
    ProgressBar progressBar;
    ImageView blurryBg;
    public static GetWeatherInfoAPI api2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_query);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "openSansLight.ttf");

        TextView title = (TextView) findViewById(R.id.app_add_city_title);
        TextView version = (TextView) findViewById(R.id.app_add_city_verion);
        if (title != null & version != null) {
            title.setTypeface(typeface);
            Calendar c = Calendar.getInstance();
            String timeStamp = String.format(Locale.getDefault(), "%d", c.get(Calendar.HOUR_OF_DAY));
            int time = Integer.parseInt(timeStamp);
            if (time > 0 && time <= 4) {
                title.setText("Good Night");
            }
            if (time > 4 && time <= 8) {
                title.setText("Good Morning");
            }
            if (time > 8 && time < 12) {
                title.setText("Good Morning");
            }
            if (time >= 12 && time < 18) {
                title.setText("Good Afternoon");
            }
            if (time >= 18 && time < 24) {
                title.setText("Good Evening");
            }
            version.setTypeface(typeface);
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Search city");
        progressBar = (ProgressBar) findViewById(R.id.app_add_city_progress_bar);
        blurryBg = (ImageView) findViewById(R.id.app_add_city_blurry_background);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        if (blurryBg != null){
            blurryBg.setVisibility(View.GONE);
        }

        LinearLayout searchLayout = (LinearLayout) findViewById(R.id.app_search_layout);
        GradientDrawable gdDefault = new GradientDrawable();
        gdDefault.setColor(ContextCompat.getColor(this, R.color.colorDarkTransparentBackground));
        gdDefault.setCornerRadius(10);
        if (searchLayout != null)
            searchLayout.setBackground(gdDefault);

        btnByCityName = (Button) findViewById(R.id.app_add_city_button_check_if_exists);
        final EditText editText = (EditText) findViewById(R.id.app_add_city_edit_text);
        if (editText != null)
            editText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                            (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        checkIfExists(editText.getText().toString());
                        return true;
                    }
                    return false;
                }
            });
        btnByCityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText != null)
                    checkIfExists(editText.getText().toString());
            }
        });
    }

    public void waitForApi(final GetWeatherInfoAPI api) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (api.success) {
                    api2 = api;
                    addToDb(api);
//                    onFinish(api);
                } else {
                    waitForApi(api);
                }
            }
        }, 100);
    }

    public void addToDb(final GetWeatherInfoAPI api){
        AsyncTask<Object, Object, Object> saveContactTask = new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                saveContact(); // save contact to the database
                return null;
            } // end method doInBackground

            @Override
            protected void onPostExecute(Object result) {

                onFinish(api2); // return to the previous Activity
            } // end method onPostExecute
        }; // end AsyncTask

        // save the contact to the database using a separate thread
        saveContactTask.execute((Object[]) null);
    }

    private void saveContact() {
        // get DatabaseConnector to interact with the SQLite database
        DatabaseConnector databaseConnector = new DatabaseConnector(this);

        if (getIntent().getExtras() == null) {
            // insert the contact information into the database
            databaseConnector.insertContact(api2.getCityName());
        } // end if
        else {
//            databaseConnector.updateContact(rowID, nameEditText.getText().toString(), emailEditText
//                    .getText().toString(), phoneEditText.getText().toString(), streetEditText
//                    .getText().toString(), cityEditText.getText().toString());
        } // end else
    } // end class saveContact

    public void onFinish(final GetWeatherInfoAPI api) {
        progressBar.setVisibility(View.GONE);
        blurryBg.setVisibility(View.GONE);
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
                            Toast.makeText(CityQuery.this, api.getCityName() + " added", Toast.LENGTH_SHORT).show();
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
        MenuItem sortIcon = menu.findItem(R.id.sort_icon);
        MenuItem resfreshIcon = menu.findItem(R.id.refresh_toolbar_icon);
        MenuItem gpsIcon = menu.findItem(R.id.gps_search);
        gpsIcon.setVisible(false);
        sortIcon.setVisible(false);
        resfreshIcon.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_city) {
            Intent intent = new Intent(getBaseContext(), CityQuery.class);
            startActivity(intent);
        }
        if (id == R.id.home) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        }
        if (id == android.R.id.home) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        }

        return super.onOptionsItemSelected(item);
    }

    public void checkIfExists(String city) {
        GetWeatherInfoAPI api = new GetWeatherInfoAPI(city);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.requestFocus();
        blurryBg.setVisibility(View.VISIBLE);
        waitForApi(api);
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("returnString", "Ta string vracamo");
        returnIntent.putExtras(bundle);
        setResult(RESULT_OK, returnIntent);
        finish();
        Log.d("CDA", "onBackPressed Called");
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
