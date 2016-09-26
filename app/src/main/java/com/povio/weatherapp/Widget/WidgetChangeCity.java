package com.povio.weatherapp.Widget;

import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.povio.weatherapp.GetWeatherInfoAPI;
import com.povio.weatherapp.MainActivity;
import com.povio.weatherapp.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;

public class WidgetChangeCity extends AppCompatActivity {
    GetWeatherInfoAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_change_city);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "openSansLight.ttf");
        TextView title = (TextView) findViewById(R.id.widget_add_city_title);
        TextView version = (TextView) findViewById(R.id.widget_add_city_verion);


        LinearLayout searchLayout = (LinearLayout) findViewById(R.id.widget_search_layout);
        GradientDrawable gdDefault = new GradientDrawable();
        gdDefault.setColor(ContextCompat.getColor(this, R.color.colorDarkTransparentBackground));
        gdDefault.setCornerRadius(10);

        if (searchLayout != null)
            searchLayout.setBackground(gdDefault);
        if (title != null && version != null) {
            title.setTypeface(typeface);
            version.setTypeface(typeface);
        }

        ImageView logoIcon = (ImageView) findViewById(R.id.widget_add_city_icon);
        if (logoIcon != null)
            logoIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getBaseContext(), MainActivity.class));
                }
            });

        final EditText editText = (EditText) findViewById(R.id.widget_add_city_edit_text);
        if (editText != null)
            editText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                            (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        checkExistanceOfCity(editText.getText().toString());
                        return true;
                    }
                    return false;
                }
            });

        Button searchButton = (Button) findViewById(R.id.widget_add_city_button_check_if_exists);
        if (searchButton != null)
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editText != null)
                        checkExistanceOfCity(editText.getText().toString());
                }
            });

    }

    private void checkExistanceOfCity(String city) {
        api = new GetWeatherInfoAPI(city);
        waitForApi(api);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.widget_add_city_progress_bar);
        ImageView imageView = (ImageView) findViewById(R.id.widget_add_city_blurry_background);
        if (progressBar != null && imageView != null) {
            progressBar.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    public void waitForApi(final GetWeatherInfoAPI api) {
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
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.widget_add_city_progress_bar);
        ImageView imageView = (ImageView) findViewById(R.id.widget_add_city_blurry_background);
        if (progressBar != null && imageView != null) {
            progressBar.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
        }
        if (api.getMainDesc().equals(":(")) {
            Toast.makeText(this, "That place does not exist", Toast.LENGTH_SHORT).show();
        } else {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            Toast.makeText(getApplicationContext(), api.getCityName() + " added", Toast.LENGTH_SHORT).show();
                            WidgetProvider.cityNameWidget = api.getCityName();
                            try {
                                PrintWriter printWriter = new PrintWriter("/storage/emulated/0/widget.txt");
                                printWriter.print(api.getCityName());
                                printWriter.close();
                                Intent intent = new Intent(getBaseContext(), WidgetProvider.class);
                                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                                int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds
                                        (new ComponentName(getApplication(), WidgetProvider.class));
// Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
// since it seems the onUpdate() is only fired on that:
//                                int[] ids = {widgetId};
                                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                                sendBroadcast(intent);
                            } catch (Exception e) {
                                Log.d("WRITE TO WIDGETS.TXT", "Something went wrong better look what");
                            }
                            Intent setIntent = new Intent(Intent.ACTION_MAIN);
                            setIntent.addCategory(Intent.CATEGORY_HOME);
                            setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(setIntent);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(WidgetChangeCity.this);
            builder.setMessage("Found as: " + api.getCityName() + ", " + api.getCountry()).setPositiveButton("Add", dialogClickListener)
                    .setNegativeButton("Cancel", dialogClickListener).show();
        }
    }
}
