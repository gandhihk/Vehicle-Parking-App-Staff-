package com.example.vehicleparkingappstaff;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
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


public class Home extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ProgressDialog pDialog;
    TabLayout tabs;
    ViewGroup layout;
    ArrayList<String> slot_maps;
    List<TextView> seatViewList = new ArrayList<>();
    int seatVerticalSize = 120;
    int seatHorizontalSize = 180;
    int seatGaping = 10;
    int STATUS_AVAILABLE = 1;
    int STATUS_BOOKED = 2;
    static int level_number,slot_number;
    RequestQueue requestQueue;
    View frameLayout,previous_slot;
    static Activity activity;

    private OnFragmentInteractionListener mListener;

    public Home() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        requestQueue = Volley.newRequestQueue(getActivity());
        //MainActivity.toolbar.setTitle("Home");
        activity = getActivity();
        // Inflate the layout for this fragment
        frameLayout = inflater.inflate(R.layout.home, container, false);
        slot_maps = new ArrayList<>();
        level_number=1;
        tabs = frameLayout.findViewById(R.id.tabs);
        layout = frameLayout.findViewById(R.id.layoutSeat);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();
        getMap();

        return frameLayout;
    }


    void getMap()
    {
        Map<String,String> params = new HashMap<>();
        params.put("operation", "get_map");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, ConfigConstants.GET_MAP_DETAILS, new JSONObject(params), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Toast.makeText(Login.this, response.toString(), Toast.LENGTH_LONG).show();
                            if ((response.getString("message")).equals("success"))
                            {
                                JSONArray map = response.getJSONArray("map");
                                for(int i=0;i<map.length();i++)
                                {
                                    JSONObject j = map.getJSONObject(i);
                                    int level_num = j.getInt("level_number");
                                    tabs.addTab(tabs.newTab().setText("LEVEL "+String.valueOf(level_num)));
                                    int no_of_slots = j.getInt("no_of_slots");
                                    String booked_slots = j.getString("booked_slots");
                                    //Toast.makeText(getActivity(), booked_slots, Toast.LENGTH_LONG).show();
                                    createMapForThisLevel(level_num,no_of_slots,booked_slots);
                                }
                                pDialog.hide();
                                setUpTabs();
                                setMap(slot_maps.get(0));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(),
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

        //Adding the string request to the queue
        requestQueue.add(jsonObjectRequest);
    }

    void setUpTabs()
    {
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                layout.removeAllViews();
                level_number = tab.getPosition()+1;
                setMap(slot_maps.get(level_number-1));
                frameLayout.findViewById(R.id.slot_details).setVisibility(View.GONE);
                frameLayout.findViewById(R.id.details).setVisibility(View.GONE);
                frameLayout.findViewById(R.id.booking_details).setVisibility(View.GONE);
                frameLayout.findViewById(R.id.line).setVisibility(View.GONE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    void createMapForThisLevel(int level_num, int no_of_slots, String booked_slots)
    {
        String[] slots = booked_slots.split(" ");
        String seats = "/";
        boolean flag;
        for(int i=1;i<=no_of_slots;i++)
        {
            //seats=seats+"A";
            flag=false;
            for(String x:slots)
            {
                if(x.equals(String.valueOf(i)))
                    flag=true;
            }
            if(flag)
                seats = seats+"B";
            else
                seats = seats+"A";

            if(i%4==0)
                seats = seats+"/";
            else if(i%2==0)
                seats = seats+"__";
        }
        seats=seats+"/";
        slot_maps.add(seats);
        //Toast.makeText(getActivity(), slot_maps.get(slot_maps.size()-1), Toast.LENGTH_LONG).show();
    }

    void setMap(String seats)
    {
        LinearLayout layoutSeat = new LinearLayout(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutSeat.setOrientation(LinearLayout.VERTICAL);
        layoutSeat.setLayoutParams(params);
        layoutSeat.setPadding(5 * seatGaping, 5 * seatGaping, 5 * seatGaping, 5 * seatGaping);
        layout.removeAllViews();
        layout.addView(layoutSeat);

        LinearLayout layout = null;

        int count = 0;

        for (int index = 0; index < seats.length(); index++)
        {
            if (seats.charAt(index) == '/') {
                layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.HORIZONTAL);
                layoutSeat.addView(layout);
            } else if (seats.charAt(index) == 'B') {
                count++;
                TextView view = new TextView(getActivity());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatHorizontalSize, seatVerticalSize);
                layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
                view.setLayoutParams(layoutParams);
                view.setPadding(0, 0, 0, 2 * seatGaping);
                view.setId(count);
                view.setGravity(Gravity.CENTER);
                view.setBackgroundResource(R.drawable.slot_booked);
                view.setTextColor(Color.BLACK);
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
                view.setTag(STATUS_BOOKED);
                view.setText(count + "");
                layout.addView(view);
                seatViewList.add(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(previous_slot!=null)
                            previous_slot.setBackgroundResource(R.drawable.slot_available);
                        setSlotDetails(v.getId(),"Booked");
                        frameLayout.findViewById(R.id.details).setVisibility(View.GONE);
                        //Toast.makeText(getApplicationContext(), "Slot " + v.getId() + " is Booked", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (seats.charAt(index) == 'A') {
                count++;
                TextView view = new TextView(getActivity());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatHorizontalSize, seatVerticalSize);
                layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
                view.setLayoutParams(layoutParams);
                view.setPadding(0, 0, 0, 2 * seatGaping);
                view.setId(count);
                view.setGravity(Gravity.CENTER);
                view.setBackgroundResource(R.drawable.slot_available);
                view.setText(count + "");
                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                view.setTextColor(Color.BLACK);
                view.setTag(STATUS_AVAILABLE);
                layout.addView(view);
                seatViewList.add(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (previous_slot!=v) {
                            if(previous_slot!=null)
                                previous_slot.setBackgroundResource(R.drawable.slot_available);
                            v.setBackgroundResource(R.drawable.slot_selected);
                            slot_number = v.getId();
                            setSlotDetails(v.getId(),"Available");
                            previous_slot = v;
                        }
                        else{
                            previous_slot=null;
                            v.setBackgroundResource(R.drawable.slot_available);
                            frameLayout.findViewById(R.id.slot_details).setVisibility(View.GONE);
                            frameLayout.findViewById(R.id.details).setVisibility(View.GONE);
                            frameLayout.findViewById(R.id.booking_details).setVisibility(View.GONE);
                            frameLayout.findViewById(R.id.line).setVisibility(View.GONE);
                        }
                    }
                });
            } else if (seats.charAt(index) == '_') {
                TextView view = new TextView(getActivity());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatHorizontalSize/2, seatVerticalSize/2);
                layoutParams.setMargins(seatGaping/2, seatGaping, seatGaping/2, seatGaping);
                view.setLayoutParams(layoutParams);
                view.setBackgroundColor(Color.TRANSPARENT);
                view.setText("");
                layout.addView(view);
            }
        }

        Drawable slot_avail = getResources().getDrawable(R.drawable.slot_available);
        Drawable slot_book = getResources().getDrawable(R.drawable.slot_booked);
        Bitmap bitmap1 = ((BitmapDrawable) slot_avail).getBitmap();
        Bitmap bitmap2 = ((BitmapDrawable) slot_book).getBitmap();
        Drawable avail = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap1, 60, 60, true));
        Drawable book = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap2, 60, 60, true));
        TextView available_txt= frameLayout.findViewById(R.id.available);
        available_txt.setCompoundDrawablesRelativeWithIntrinsicBounds(avail, null, null, null);
        TextView booked_txt = frameLayout.findViewById(R.id.booked);
        booked_txt.setCompoundDrawablesRelativeWithIntrinsicBounds(book, null, null, null);
    }

    void setSlotDetails(int slot_number, String status)
    {
        TextView level_txt = frameLayout.findViewById(R.id.level);
        level_txt.setText("Level Number :  "+level_number);
        TextView slot_txt = frameLayout.findViewById(R.id.slot);
        slot_txt.setText("Slot Number :  "+slot_number);
        TextView status_txt = frameLayout.findViewById(R.id.status);
        status_txt.setText("Status :   "+status);
        frameLayout.findViewById(R.id.details).setVisibility(View.VISIBLE);
        frameLayout.findViewById(R.id.slot_details).setVisibility(View.VISIBLE);
        frameLayout.findViewById(R.id.line).setVisibility(View.GONE);
        frameLayout.findViewById(R.id.booking_details).setVisibility(View.GONE);

        if(status.equals("Booked"))
        {
            frameLayout.findViewById(R.id.line).setVisibility(View.VISIBLE);
            frameLayout.findViewById(R.id.booking_details).setVisibility(View.VISIBLE);

            Map<String,String> params = new HashMap<>();
            params.put("operation", "get_booking_details");
            params.put("slot_number",String.valueOf(slot_number));
            params.put("level_number",String.valueOf(level_number));

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, ConfigConstants.GET_MAP_DETAILS, new JSONObject(params), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //Toast.makeText(Login.this, response.toString(), Toast.LENGTH_LONG).show();
                                if ((response.getString("message")).equals("success"))
                                {
                                    String booking_id = response.getString("booking_id");
                                    String entry_time = response.getString("entry_time");
                                    String category = response.getString("vehicle_category");
                                    String duration = response.getString("parking_time");
                                    String name = response.getString("name");
                                    String contact = response.getString("contact");
                                    String licence = response.getString("licence_number");
                                    ((TextView)frameLayout.findViewById(R.id.booking_id)).setText("Booking ID :  "+booking_id);
                                    ((TextView)frameLayout.findViewById(R.id.entry_time)).setText("Entry Time :  "+entry_time.substring(11));
                                    ((TextView)frameLayout.findViewById(R.id.vehicle_category)).setText("Vehicle Category :    "+category);
                                    ((TextView)frameLayout.findViewById(R.id.name)).setText("Name :   "+name);
                                    ((TextView)frameLayout.findViewById(R.id.contact)).setText("Contact Number :   "+contact);
                                    ((TextView)frameLayout.findViewById(R.id.licence)).setText("Licence Number :   "+licence);
                                    ((TextView)frameLayout.findViewById(R.id.duration)).setText("Duration :  "+duration+" mins");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(),
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


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
