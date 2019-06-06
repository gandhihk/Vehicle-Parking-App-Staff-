package com.example.vehicleparkingappstaff;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements Home.OnFragmentInteractionListener,Verify.OnFragmentInteractionListener{

    static Toolbar toolbar;
    SessionManager s;
    User u;
    static Handler h;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        toolbar = findViewById(R.id.toolbar);
        getSupportActionBar().setTitle("Home");

        //check if already logged in
        s = new SessionManager(getApplicationContext());
        s.checkLogin();

        if(!s.isLoggedIn())
            finish();

        SharedPreferences sp = s.pref;
        u = new User();
        u.fname = sp.getString(ConfigConstants.KEY_FNAME, "");
        u.lname = sp.getString(ConfigConstants.KEY_LNAME,"");
        u.username = sp.getString(ConfigConstants.KEY_USERNAME,"");
        u.contact = sp.getString(ConfigConstants.KEY_PHONE,"");
        u.licence = sp.getString(ConfigConstants.KEY_LICENCE,"");
        u.address = sp.getString(ConfigConstants.KEY_ADDRESS,"");

        //To finish this activity if log in is successful
        h = new Handler() {

            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch(msg.what) {

                    case 0:
                        finish();
                        break;

                }
            }

        };

        loadFragment(new Home());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getSupportActionBar().setTitle("Home");
                    fragment = new Home();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_verify:
                    getSupportActionBar().setTitle("Verify");
                    fragment = new Verify();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_logout:
                    logout();
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    void logout()
    {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        alertDialog.setTitle("Confirm Logout");
        alertDialog.setMessage("Are you sure you want to logout?");

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                s.logoutUser();
                finish();
                startActivity(getIntent());
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //
    }
}

class User
{
    String fname,lname,username,address,licence,contact;
}
