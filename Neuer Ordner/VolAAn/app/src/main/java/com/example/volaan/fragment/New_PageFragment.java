package com.example.volaan.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.volaan.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link New_PageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class New_PageFragment extends Fragment {

TextView textview;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public New_PageFragment() {
        // Required empty public constructor
    }

    public static New_PageFragment newInstance(String param1, String param2) {
        New_PageFragment fragment = new New_PageFragment();
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
        View v = inflater.inflate(R.layout.fragment_new__page, null);
        TextView textView = v.findViewById(R.id.HelloUser);
        return inflater.inflate(R.layout.fragment_new__page, container, false);

    }
}