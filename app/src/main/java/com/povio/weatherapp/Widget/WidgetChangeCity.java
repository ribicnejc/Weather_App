package com.povio.weatherapp.Widget;

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.povio.weatherapp.R;

public class WidgetChangeCity extends AppCompatActivity {
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

        final EditText editText = (EditText) findViewById(R.id.widget_add_city_edit_text);


        Button searchButton = (Button) findViewById(R.id.widget_add_city_button_check_if_exists);
        if (searchButton != null)
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(editText != null)
                        checkExistanceOfCity(editText.getText().toString());
                }
            });

    }

    private void checkExistanceOfCity(String city){

    }
}
