package com.example.inventairelol.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.inventairelol.Activities.Login;
import com.example.inventairelol.Activities.Register;
import com.example.inventairelol.R;
import com.example.inventairelol.Service.ApiLoL;
import com.example.inventairelol.Service.ServiceOnlineMYSQL;
import com.example.inventairelol.Util.Champion.ChampAdapter;
import com.example.inventairelol.Util.Champion.Champion;
import com.example.inventairelol.Util.ConfigGetter;
import com.example.inventairelol.Util.Item.Item;
import com.example.inventairelol.Util.Item.ItemAdapter;
import com.example.inventairelol.Util.Preferences.PreferenceUserRiot;
import com.example.inventairelol.Util.Preferences.PreferencesUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
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
            Map<String, String> config = new ConfigGetter(getActivity()).getDatabaseConfig();

            //Récupération des paramétres de configurations de la base de données via le fichier config
            this.hostname = config.get("hostname");
            this.port = config.get("port");
            this.database = config.get("database");
            this.username = config.get("username");
            this.password = config.get("password");
            this.url = "jdbc:mysql://" + hostname + ":" + port + "/" + database;


            ApiLoL apiLoL;
            PreferencesUser preferencesUser = new PreferencesUser(getActivity());

            Map<String, String> infos = preferencesUser.getUserInfo();

            //Vérification si connecté
            String connected = "false";
            if (infos.containsKey("connected")) {
                connected = infos.get("connected");
            }

            if (connected.equals("true")) {

                String pseudo = "";
                if (infos.containsKey("pseudo")) {
                    pseudo = infos.get("pseudo");
                }

                //Vérification que le pseudo n'est pas vide
                if (!pseudo.equals("")) {
                    String usernameRiot = "", region = "";
                    ServiceOnlineMYSQL serviceOnlineMYSQL = new ServiceOnlineMYSQL(getActivity());
                    serviceOnlineMYSQL.execute("getRiotUsernameAndRegion", pseudo);
                    String res = serviceOnlineMYSQL.get();

                    //On créer une Alerte pour informer l'utilisateur en cas de problème
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.errorLoginTitle)
                            .setCancelable(false)
                            .setPositiveButton(R.string.understood, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getActivity(), R.string.understood, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getActivity(), Login.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            });

                    if (res == null) {
                        builder.setMessage(R.string.errorMessage);
                        builder.show();
                    } else {
                        String[] split = res.split(",");
                        usernameRiot = split[0];
                        region = split[1];
                    }
                    //Récupération des infos de l'utilisateur
                    apiLoL = (ApiLoL) new ApiLoL(getContext()).execute("getUserInfo", region, usernameRiot);

                    String res2 = apiLoL.get();


                    if (res2.contains("Error :") || res2.contains("Fail :")) {

                    } else {
                        String str = "[" + res2 + "]";
                        JSONArray array = new JSONArray(str);

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

                        PreferenceUserRiot preferenceUserRiot = new PreferenceUserRiot(getActivity());
                        preferenceUserRiot.setUserInfo("id", id);
                        preferenceUserRiot.setUserInfo("accountId", accountId);
                        preferenceUserRiot.setUserInfo("puuid", puuid);
                        preferenceUserRiot.setUserInfo("name", name);
                        preferenceUserRiot.setUserInfo("profileIconId", profileIconId);
                        preferenceUserRiot.setUserInfo("summonerLevel", summonerLevel);

                    }

                }
            }
            //Création du tableau de tous les champions

            apiLoL = new ApiLoL(this.getContext());
            apiLoL.execute("getAllChampInfo", "EUW1");

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
            apiLoL.execute("getAllItemInfo", "EUW1");

            ArrayList<Item> items = new ArrayList<Item>();
            //On récupère le json des items
            String res3 = apiLoL.get();


            //On convertit le string en json
            jsonObject = new JSONObject(res3);
            //On récupère les infos sur les items
            data = jsonObject.getJSONObject("data");
            //On itère et on créer un objet Item par item
            for (Iterator<String> it = data.keys(); it.hasNext(); ) {
                String key = it.next();
                JSONObject infoItem = data.getJSONObject(key);
                items.add(new Item(infoItem.getString("name"), key.toString() + ".png"));

            }

            //Génération de l'affichage des champions
            ItemAdapter itemAdapter = new ItemAdapter(getActivity(), items);
            listViewItem.setAdapter(itemAdapter);


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