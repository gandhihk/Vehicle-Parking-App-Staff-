package com.example.vehicleparkingappstaff;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Verify extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Button button;

    private OnFragmentInteractionListener mListener;

    public Verify() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Verify newInstance(String param1, String param2) {
        Verify fragment = new Verify();
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
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.verify, container, false);
        button = rootview.findViewById(R.id.button);
        final EditText otp_txt = rootview.findViewById(R.id.otp);
        final EditText booking_id_txt = rootview.findViewById(R.id.booking_id);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = otp_txt.getText().toString();
                String booking_id = booking_id_txt.getText().toString();
                Intent intent = new Intent(getActivity(), PaymentDetails.class);
                if(!otp.equals("") && !booking_id.equals(""))
                    Toast.makeText(getActivity(), "Enter only booking ID or OTP", Toast.LENGTH_SHORT).show();
                else if(!otp.equals(""))
                {
                    intent.putExtra("otp",otp);
                    intent.putExtra("type","otp");
                    startActivity(intent);
                }
                else if(!booking_id.equals(""))
                {
                    intent.putExtra("booking_id",booking_id);
                    intent.putExtra("type","booking_id");
                    startActivity(intent);
                }
            }
        });

        return rootview;
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
