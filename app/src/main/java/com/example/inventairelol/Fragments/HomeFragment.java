package com.example.inventairelol.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.inventairelol.R;
import com.example.inventairelol.Service.ApiLoL;
import com.example.inventairelol.Service.OnlineMYSQL;
import com.example.inventairelol.Util.ChampAdapter;
import com.example.inventairelol.Util.Champion;
import com.example.inventairelol.Util.Item;
import com.example.inventairelol.Util.ItemAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    String username, url, hostname, password, port, database;

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        ListView listViewChampion = v.findViewById(R.id.champions);
        ListView listViewItem = v.findViewById(R.id.items);


        try {

            //Récupération du fichier de configuration de la base de données
            Properties p = new Properties();
            AssetManager assetManager = getActivity().getAssets();
            InputStream inputStream = assetManager.open("config.properties");
            p.load(inputStream);

            //Récupération des paramétres de configurations de la base de données via le fichier config
            this.hostname = p.getProperty("hostname");
            this.port = p.getProperty("port");
            this.database = p.getProperty("database");
            this.username = p.getProperty("username");
            this.password = p.getProperty("password");
            this.url = "jdbc:mysql://" + hostname + ":" + port + "/" + database;


            //Vérification si le pseudo est passé via intent ou preferences
            ApiLoL apiLoL;
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", MODE_PRIVATE);
            String pseudo = sharedPreferences.getString("pseudo", "");
            if (!pseudo.equals("")) {
                String usernameRiot = "", region = "";
                OnlineMYSQL onlineMYSQL = new OnlineMYSQL(getActivity(), url, username, password);
                onlineMYSQL.execute("getRiotUsernameAndRegion", pseudo);
                String res = onlineMYSQL.get();
                Log.i("res", res);

                if (res == null) {

                } else {
                    String[] split = res.split(",");
                    usernameRiot = split[0];
                    region = split[1];
                }
                //Récupération des infos de l'utilisateur
                apiLoL = (ApiLoL) new ApiLoL(getContext()).execute("getUserInfo", "RGAPI-d2e39834-878f-4c39-a650-406532246abe", region, usernameRiot);
            }
            //Rien via preference, mais via intent
            else {
                Intent intent = getActivity().getIntent();
                Bundle extra = intent.getExtras();
                String usernameRiot = "", region = "";
                if (extra != null) {
                    pseudo = extra.getString("pseudo");
                    OnlineMYSQL onlineMYSQL = new OnlineMYSQL(getActivity(), url, username, password);
                    onlineMYSQL.execute("getRiotUsernameAndRegion", pseudo);
                    String res = onlineMYSQL.get();
                    if (res == null) {

                    } else {
                        String[] split = res.split(",");
                        usernameRiot = split[0];
                        region = split[1];
                    }
                    //Récupération des infos de l'utilisateur
                    apiLoL = (ApiLoL) new ApiLoL(getContext()).execute("getUserInfo", "RGAPI-d2e39834-878f-4c39-a650-406532246abe", region, usernameRiot);
                }
                //Récupération des infos de l'utilisateur
                apiLoL = (ApiLoL) new ApiLoL(getContext()).execute("getUserInfo", "RGAPI-d2e39834-878f-4c39-a650-406532246abe", region, usernameRiot);


            }


            String res = apiLoL.get();
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
                SharedPreferences accountLol = getContext().getSharedPreferences("accountLolRiot", MODE_PRIVATE);
                SharedPreferences.Editor editor = accountLol.edit();

                editor.putString("idRiot", id);
                editor.putString("accountIdRiot", accountId);
                editor.putString("puuidRiot", puuid);
                editor.putString("nameRiot", name);
                editor.putString("profileIconIdRiot", profileIconId);
                editor.putString("summonerLevelRiot", summonerLevel);
                editor.apply();
                Map<String, String> map = (Map<String, String>) accountLol.getAll();


            }

            //Création du tableau de tous les champions

            apiLoL = new ApiLoL(this.getContext());
            apiLoL.execute("getAllChampInfo", "RGAPI-d2e39834-878f-4c39-a650-406532246abe", "EUW1");

            ArrayList<Champion> champs = new ArrayList<Champion>();
            //On récupère le json des champion
            String res2 = apiLoL.get();

            //On convertit le string en json
            JSONObject jsonObject = new JSONObject(res2);
            //On récupère les infos sur les champions
            JSONObject data = jsonObject.getJSONObject("data");
            //On itère et on créer un objet Champion par champion
            for (Iterator<String> it = data.keys(); it.hasNext(); ) {
                String key = it.next();
                JSONObject infoChamp = data.getJSONObject(key);
                champs.add(new Champion(infoChamp.get("id").toString(), infoChamp.get("id").toString() + ".png"));

            }

            //Génération de l'affichage des champions
            ChampAdapter adapter = new ChampAdapter(getActivity(), champs);
            listViewChampion.setAdapter(adapter);

            //Création du tableau de tous les items

            apiLoL = new ApiLoL(this.getContext());
            apiLoL.execute("getAllItemInfo", "RGAPI-d2e39834-878f-4c39-a650-406532246abe", "EUW1");

            ArrayList<Item> items = new ArrayList<Item>();
            //On récupère le json des items
            String res3 = apiLoL.get();


            //On convertit le string en json
            jsonObject = new JSONObject(res3);
            //On récupère les infos sur les items
            data = jsonObject.getJSONObject("data");
            Log.i("data", data.toString());
            //On itère et on créer un objet Item par item
            for (Iterator<String> it = data.keys(); it.hasNext(); ) {
                String key = it.next();
                items.add(new Item(key.toString(), key.toString() + ".png"));

            }

            Log.v("items", items.toString());

            //Génération de l'affichage des champions
            ItemAdapter itemAdapter = new ItemAdapter(getActivity(), items);
            listViewItem.setAdapter(itemAdapter);



        } catch (InterruptedException | ExecutionException | JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
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