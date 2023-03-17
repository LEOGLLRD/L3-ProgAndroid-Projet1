package com.example.inventairelol.Util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigGetter {

    private Context context;

    public ConfigGetter(Context context){
        this.context = context;
    }

    //Permet de récupérer un map des paramètres de configurations de la base de données
    public Map<String, String> getDatabaseConfig(){
        HashMap<String, String > databaseConfig = new HashMap<>();

        try {

            //Récupération du fichier de configuration de la base de données
            Properties p = new Properties();
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("config.properties");
            p.load(inputStream);

            Log.i("properties", p.toString());

            //Récupération des paramétres de configurations de la base de données via le fichier config
            databaseConfig.put("hostname", p.getProperty("hostname"));
            databaseConfig.put("port", p.getProperty("port"));
            databaseConfig.put("database", p.getProperty("database"));
            databaseConfig.put("username", p.getProperty("username"));
            databaseConfig.put("password", p.getProperty("password"));
            databaseConfig.put("apiKey", p.getProperty("apiKey"));

            return databaseConfig;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<Integer> getStarterItems(){
        try {

            ArrayList<Integer> returnedItems = new ArrayList<>();

            //Récupération du fichier du start d'items
            AssetManager assetManager = context.getAssets();
            BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open("startItems.json")));
            //Récupération en string du contenu du fichier
            int c = 0;

            String line = "" ;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();
            //On convertit le string en json
            JSONObject jsonObject = new JSONObject(json);
            //On récupère les valeurs qui correspondes aux id des items
            JSONArray items = (JSONArray) jsonObject.get("items");


            //On itère et récupère
            for (int i = 0; i < items.length(); i++) {
                //On ajoute les valeurs retrouvées
                returnedItems.add((Integer) items.get(i));
            }
            return returnedItems;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
