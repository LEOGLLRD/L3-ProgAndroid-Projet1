package com.example.inventairelol.Fragments;

import static com.example.inventairelol.DataBase.SQLiteBDD.COLONNE_ID_API;
import static com.example.inventairelol.DataBase.SQLiteBDD.TABLE_INVENTORY;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.inventairelol.DataBase.SQLiteBDD;
import com.example.inventairelol.R;
import com.example.inventairelol.Service.ApiLoL;
import com.example.inventairelol.Service.ServiceOnlineMYSQL;
import com.example.inventairelol.Util.Item.Item;
import com.example.inventairelol.Util.Item.ItemAdapter;
import com.example.inventairelol.Util.Preferences.PreferencesUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InventoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InventoryFragment extends Fragment {

    SQLiteBDD localSQL;
    private ServiceOnlineMYSQL serviceOnlineMYSQL;
    private ApiLoL apiLoL;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public InventoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InventoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InventoryFragment newInstance(String param1, String param2) {
        InventoryFragment fragment = new InventoryFragment();
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
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);


        try {


            PreferencesUser preferencesUser = new PreferencesUser(getActivity());
            Map<String, String> infosUser = preferencesUser.getUserInfo();
            String connected = "false";
            if (infosUser.containsKey("connected")) {
                connected = infosUser.get("connected");
            }

            //Connecté
            if (connected.equals("true")) {
                String pseudo = infosUser.get("pseudo");
                //Instanciation de la base de données Local.
                localSQL = new SQLiteBDD(getActivity());
                //Recuperation du Writable
                SQLiteDatabase dbW = localSQL.getWritableDatabase();
                //On vide la base de données,
                //On veut que la base de données soit à jour avec celle en ligne
                localSQL.resetInventory(dbW);
                //On récupère les infos de celle en ligne et on insère dans la locale
                initializeFromOnlineMYSQL(dbW, pseudo);
                SQLiteDatabase dbR = localSQL.getReadableDatabase();
                ArrayList<String> inventory = localSQL.getInventory(dbR);

                //Gestion de l'affichage de l'inventaire
                JSONObject allItem = getJSONAllItem();
                ArrayList<Item> items = new ArrayList<>();
                //On récupère les infos correspondants aux items de l'utilisateur
                for (String s : inventory
                ) {
                    String name = allItem.getJSONObject(s).getString("name");
                    items.add(new Item(name, s+".png"));
                }

                Log.i("items", items.toString());

                ListView itemsInventory = view.findViewById(R.id.itemsInventory);

                //Génération de l'affichage des items
                ItemAdapter itemAdapter = new ItemAdapter(getActivity(), items);
                itemsInventory.setAdapter(itemAdapter);

            }

            //Pas connecté
            else {



            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    public void initializeFromOnlineMYSQL(SQLiteDatabase db, String pseudo) {
        ArrayList<String> inventory = getOnlineInventory(pseudo);
        Log.i("inventory", inventory.toString());
        for (String s : inventory) {
            ContentValues values = new ContentValues();
            values.put(COLONNE_ID_API, s);
            db.insert(TABLE_INVENTORY, null, values);
        }


    }

    //méthode retournant les id des items d'un utilisateur sous forme d'une List d'id en string
    public ArrayList<String> getOnlineInventory(String pseudoUser) {
        ArrayList<String> inventory = new ArrayList<>();
        try {

            serviceOnlineMYSQL = new ServiceOnlineMYSQL(getActivity());
            serviceOnlineMYSQL.execute("getUserInventory", pseudoUser);
            String res = serviceOnlineMYSQL.get();
            res = res.replace("[", "");
            res = res.replace("]", "");
            res = res.replace(" ", "");
            //Conversion tableau de string en ArrayList
            Log.i("res", res);
            Log.i("split", Arrays.toString(res.split(",")));
            inventory = new ArrayList<>(Arrays.asList(res.split(",")));

            return inventory;

        } catch (Exception e) {
            e.printStackTrace();
            return inventory;

        }
    }

    @Override
    public void onDestroy() {
        if (localSQL != null) {
            localSQL.close();
        }
        super.onDestroy();
    }

    public String getNameOfItemFromId(String id) {
        try {
            JSONObject allItem = getJSONAllItem();
            Log.i("testAff", allItem.get("1000").toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public JSONObject getJSONAllItem() {
        try {

            apiLoL = new ApiLoL(getActivity());
            apiLoL.execute("getAllItemInfo");
            ArrayList<Item> items = new ArrayList<Item>();
            //On récupère le json des items
            String res3 = apiLoL.get();

            //On convertit le string en json
            JSONObject jsonObject = new JSONObject(res3);
            //On récupère les infos sur les items
            JSONObject data = jsonObject.getJSONObject("data");

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}