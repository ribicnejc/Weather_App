package com.povio.weatherapp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

public class NavigationDrawerFragment extends Fragment {

    public static final String PREF_FILE_NAME = "testpref";
    public static final String KEY_LEARNED_DRAWER = "user_learned_drawer";
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    public static SharedPreferences sharedPreferences;
    private RadioButton radioButtonTemp, radioButtonType, radioButtonName;

    private View containerView;
    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    private LinearLayout linearLayoutHomeButton;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = Boolean.valueOf(readFromPreferences(getActivity(), KEY_LEARNED_DRAWER, "false"));
        if (savedInstanceState != null) {
            mFromSavedInstanceState = true;
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);


        radioButtonTemp = (RadioButton) view.findViewById(R.id.navigation_drawer_radio_temp);
        radioButtonType = (RadioButton) view.findViewById(R.id.navigation_drawer_radio_type);
        radioButtonName = (RadioButton) view.findViewById(R.id.navigation_drawer_radio_name);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if(sharedPreferences.contains("radioButtonName")) {
            radioButtonName.setChecked(sharedPreferences.getBoolean("radioButtonName", false));
            radioButtonTemp.setChecked(sharedPreferences.getBoolean("radioButtonTemp", false));
            radioButtonType.setChecked(sharedPreferences.getBoolean("radioButtonType", false));
        }
        linearLayoutHomeButton = (LinearLayout) view.findViewById(R.id.navigation_drawer_home_button);
        linearLayoutHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
            }
        });
        LinearLayout linearLayoutAddCityButton = (LinearLayout) view.findViewById(R.id.navigation_drawer_add_city_button);
        linearLayoutAddCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
                Intent intent = new Intent(getActivity(), CityQuery.class);
                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                (getActivity()).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("radioButtonTemp", radioButtonTemp.isChecked());
                editor.putBoolean("radioButtonType", radioButtonType.isChecked());
                editor.putBoolean("radioButtonName", radioButtonName.isChecked());
                editor.apply();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    saveToPreference(getActivity(), KEY_LEARNED_DRAWER, mUserLearnedDrawer + "");
                }
                getActivity().invalidateOptionsMenu();
                radioButtonName.setChecked(sharedPreferences.getBoolean("radioButtonName", false));
                radioButtonTemp.setChecked(sharedPreferences.getBoolean("radioButtonTemp", false));
                radioButtonType.setChecked(sharedPreferences.getBoolean("radioButtonType", false));
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("radioButtonTemp", radioButtonTemp.isChecked());
                editor.putBoolean("radioButtonType", radioButtonType.isChecked());
                editor.putBoolean("radioButtonName", radioButtonName.isChecked());
                editor.apply();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                //if(slideOffset < 0.6)
                //    toolbar.setAlpha(1-slideOffset);
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    public static void saveToPreference(Context context, String prefreneceName, String preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(prefreneceName, preferenceValue);
        editor.apply();
    }

    public static String readFromPreferences(Context context, String preferenceName, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }

    public static SharedPreferences getSharedPreferences(){
        return sharedPreferences;
    }
}
