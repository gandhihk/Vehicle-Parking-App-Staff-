package com.example.vehicleparkingappstaff;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PaymentDetails extends AppCompatActivity {

    private int unique;
    RequestQueue requestQueue;
    String booking_id,transaction_id,payment_mode;
    ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_details);
        toolbar = getSupportActionBar();
        toolbar.setTitle("Verify Payment");
        toolbar.setDisplayHomeAsUpEnabled(true);
        toolbar.setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        String unique1;
        if(intent.getStringExtra("type").equals("booking_id"))
            unique1 = intent.getStringExtra("booking_id");
        else
            unique1 = intent.getStringExtra("otp");
        unique = Integer.valueOf(unique1);

        requestQueue = Volley.newRequestQueue(this);
        displayPaymentDetails(intent.getStringExtra("type"),unique);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void displayPaymentDetails(String type,int unique)
    {
        Map<String,String> params = new HashMap<>();
        params.put("operation", "get_payment_details");
        params.put("type",String.valueOf(type));
        params.put("unique",String.valueOf(unique));
        //Toast.makeText(PaymentDetails.this, type+" "+unique, Toast.LENGTH_LONG).show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, ConfigConstants.GET_VEHICLES, new JSONObject(params), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Toast.makeText(PaymentDetails.this, response.toString(), Toast.LENGTH_LONG).show();
                            if ((response.getString("message")).equals("success"))
                            {
                                findViewById(R.id.table).setVisibility(View.VISIBLE);
                                booking_id = response.getString("booking_id");
                                String entry_time = response.getString("entry_time");
                                String exit_time = response.getString("exit_time");
                                String duration = response.getString("duration");
                                String total_fare = response.getString("total_fare");
                                String level_num = response.getString("level_number");
                                String slot_num = response.getString("slot_number");
                                transaction_id = response.getString("transaction_id");
                                payment_mode = response.getString("payment_mode");
                                String payment_status = response.getString("payment_status");
                                ((TextView)findViewById(R.id.booking_id)).setText(booking_id);
                                ((TextView)findViewById(R.id.entry_time)).setText(entry_time.substring(11));
                                ((TextView)findViewById(R.id.exit_time)).setText(exit_time.substring(11));
                                ((TextView)findViewById(R.id.duration)).setText(duration+" minutes");
                                ((TextView)findViewById(R.id.fare)).setText(total_fare+" Rs.");
                                ((TextView)findViewById(R.id.level_number)).setText(level_num);
                                ((TextView)findViewById(R.id.slot_number)).setText(slot_num);
                                ((TextView)findViewById(R.id.transaction_id)).setText(transaction_id);
                                ((TextView)findViewById(R.id.payment_mode)).setText(payment_mode);
                                ((TextView)findViewById(R.id.payment_status)).setText(payment_status);
                                if(payment_mode.equals("Offline"))
                                    ((Button)findViewById(R.id.button)).setText("Collect Cash & Verify");
                            }
                            else if((response.getString("message")).equals("No booking found"))
                            {
                                findViewById(R.id.table).setVisibility(View.GONE);
                                Toast.makeText(PaymentDetails.this, "No Booking found !",
                                        Toast.LENGTH_LONG).show();
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PaymentDetails.this,
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //You can handle error here if you want
                            }
                        });

        requestQueue.add(jsonObjectRequest);
    }

    public void verify(View view) {
        Map<String,String> params = new HashMap<>();
        params.put("operation", "verify_payment");
        params.put("booking_id",booking_id);
        params.put("transaction_id",transaction_id);
        params.put("payment_mode",payment_mode);
        //Toast.makeText(PaymentDetails.this, type+" "+unique, Toast.LENGTH_LONG).show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, ConfigConstants.GET_VEHICLES, new JSONObject(params), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Toast.makeText(PaymentDetails.this, response.toString(), Toast.LENGTH_LONG).show();
                            if ((response.getString("message")).equals("success"))
                            {
                                findViewById(R.id.payment_details).setVisibility(View.GONE);
                                findViewById(R.id.table).setVisibility(View.GONE);
                                findViewById(R.id.button).setVisibility(View.GONE);
                                findViewById(R.id.verified_img).setVisibility(View.VISIBLE);
                                findViewById(R.id.successful_verify).setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PaymentDetails.this,
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //You can handle error here if you want
                            }
                        });

        requestQueue.add(jsonObjectRequest);
    }
}
