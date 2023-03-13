package com.example.inventairelol.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.inventairelol.R;
import com.example.inventairelol.Service.GetMethodDemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        TextView textView = (TextView) v.findViewById(R.id.test);

        //Récupération des infos de l'utilisateur
        GetMethodDemo getMethodDemo = (GetMethodDemo) new GetMethodDemo(getContext()).execute("getUserInfo", "RGAPI-d2e39834-878f-4c39-a650-406532246abe", "EUW1", "LLEOXE");

        try {
            String res = getMethodDemo.get();
            if (res.contains("Error :") || res.contains("Fail :")) {

            } else {
                String str = "[" + res + "]";
                JSONArray array = new JSONArray(str);
                Log.v("array", array.toString());

                String id = null, accountId = null, puuid = null, name = null, profileIconId = null, summonerLevel = null;
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);

                    id = object.getString("id");
                    accountId = object.getString("accountId");
                    puuid = object.getString("puuid");
                    name = object.getString("name");
                    profileIconId = object.getString("profileIconId");
                    summonerLevel = object.getString("summonerLevel");


                }

                //Récupération des préférences
                SharedPreferences accountLol = getContext().getSharedPreferences("accountLolRiot", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = accountLol.edit();

                editor.putString("idRiot", id);
                editor.putString("accountIdRiot", accountId);
                editor.putString("puuidRiot", puuid);
                editor.putString("nameRiot", name);
                editor.putString("profileIconIdRiot", profileIconId);
                editor.putString("summonerLevelRiot", summonerLevel);
                editor.apply();
                Map<String, String> map = (Map<String, String>) accountLol.getAll();


                textView.setText(map.get("name"));

            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            throw new RuntimeException(e);
        }
        return v;
    }

    ArrayList<String> jsonStringToArray(String jsonString) throws JSONException {

        ArrayList<String> stringArray = new ArrayList<String>();

        JSONArray jsonArray = new JSONArray(jsonString);

        for (int i = 0; i < jsonArray.length(); i++) {
            stringArray.add(jsonArray.getString(i));
        }

        return stringArray;
    }


}