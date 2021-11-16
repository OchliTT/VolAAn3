package com.example.volaan.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.volaan.Main;
import com.example.volaan.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link New_PageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class New_PageFragment  extends Fragment {
    Context context;
    String userName;
    ImageView bad;
    ImageView well;
    ImageView good;
    ImageView excellent;
    LinearLayout insideLayout;

    SharedPreferences sPref;
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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment New_PageFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        context = getActivity().getApplicationContext();
        sPref = context.getSharedPreferences("Auth", context.MODE_PRIVATE);
        userName = loadText("userName");
        View v = inflater.inflate(R.layout.fragment_new__page, null);
        bad = v.findViewById(R.id.bad);
        well = v.findViewById(R.id.well);
        good = v.findViewById(R.id.good);
        excellent = v.findViewById(R.id.excellent);
        insideLayout = v.findViewById(R.id.insideLayout);

        Main ma = (Main) getActivity() ;
        ma.new_page(bad, well, good, excellent, insideLayout);
        return v;
    }

    private void saveText(String name, String value)
    {
        SharedPreferences.Editor ed =sPref.edit();
        ed.putString(name, value);
        ed.commit();
    }
    private String loadText(String name)
    {
        if(!sPref.contains(name)) return "";
        return sPref.getString(name, "");
    }

}