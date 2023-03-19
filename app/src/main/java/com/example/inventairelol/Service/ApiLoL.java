package com.example.inventairelol.Service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.inventairelol.R;
import com.example.inventairelol.Util.ApiKeyGetter;
import com.example.inventairelol.Util.ConfigGetter;

import org.json.JSONArray;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

public class ApiLoL extends AsyncTask<String, Void, String> {

    String apiKey;
    ProgressDialog progressDialog;
    Context context;

    public ApiLoL(Context context) {
        this.context = context;

        try {
            //Récupération clé API
            ApiKeyGetter keyGetter = new ApiKeyGetter(context);
            //Récupération des paramétres de configurations
            this.apiKey = keyGetter.getApiKey();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
        //Affichage d'un écran de chargement avant exécution du processus long
        progressDialog = ProgressDialog.show(context, "Chargement", "En attente de connexion avec le serveur");
        progressDialog.show();
    }


    @Override
    protected String doInBackground(String... strings) {

        /*

        1er : Directive
        2ème : clé API
        3ème : région
        4ème et + : reste des paramètres

         */

        String server_response;

        HttpURLConnection urlConnection = null;

        try {

            //Récupération de la version actuelle
            String version = getVersion();


            if (version.equals("Fail") || version.equals("Error")) {
                //Suppression du chargement
                progressDialog.dismiss();
                return "Fail : Impossible to contact API";
            }

            //Récupération de la directiv
            String directiv = strings[0];

            switch (directiv) {
                //Récupération des informations d'un joueur
                case "getUserInfo": {
                    String region = strings[1];

                    //Répération de l'username
                    String username = strings[2];
                    //On vérifie que l'username Riot n'est pas vide
                    if (username.isEmpty()) {
                        //Suppression du chargement
                        progressDialog.dismiss();
                        return "Fail : Riot Username Empty";
                    }
                    //Montage de l'url
                    URL url = new URL("https://" + region + ".api.riotgames.com/lol/summoner/v4/summoners/by-name/" + username + "?api_key=" + apiKey);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    int responseCode = urlConnection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        server_response = readStream(urlConnection.getInputStream());
                        //Suppression du chargement
                        progressDialog.dismiss();
                        return server_response;
                    } else {
                        //Suppression du chargement
                        progressDialog.dismiss();
                        return "Fail : No respons from server";
                    }

                }

                case "getAllItemInfo": {
                    URL url = new URL("http://ddragon.leagueoflegends.com/cdn/" + version + "/data/" + context.getResources().getString(R.string.lang) + "/item.json");

                    urlConnection = (HttpURLConnection) url.openConnection();
                    int responseCode = urlConnection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        server_response = readStream(urlConnection.getInputStream());
                        //Suppression du chargement
                        progressDialog.dismiss();
                        return server_response;
                    } else {
                        //Suppression du chargement
                        progressDialog.dismiss();
                        return "Fail : No respons from server";
                    }
                }
                case "getAllChampInfo": {
                    URL url = new URL("https://ddragon.leagueoflegends.com/cdn/" + version + "/data/" + context.getResources().getString(R.string.lang) + "/champion.json");

                    urlConnection = (HttpURLConnection) url.openConnection();
                    int responseCode = urlConnection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        server_response = readStream(urlConnection.getInputStream());
                        //Suppression du chargement
                        progressDialog.dismiss();
                        return server_response;
                    } else {
                        //Suppression du chargement
                        progressDialog.dismiss();
                        return "Fail : No respons from server";
                    }
                }
                case "checkIfUserExists": {
                    String region = strings[1];
                    //Répération de l'username
                    String username = strings[2];

                    String doesExists = doesUserExists(username, region, apiKey);

                    if (doesExists.contains("Fail :")) {
                        //Suppression du chargement
                        progressDialog.dismiss();
                        return "false";
                    } else {
                        //Suppression du chargement
                        progressDialog.dismiss();
                        return "true";
                    }
                }
                case "getVersion": {
                    progressDialog.dismiss();
                    return getVersion();
                }

                case "getChampDescription": {

                    String idChamp = strings[1];

                    URL url = new URL("https://ddragon.leagueoflegends.com/cdn/" + version + "/data/" + context.getResources().getString(R.string.lang) + "/champion/" + idChamp + ".json");

                    urlConnection = (HttpURLConnection) url.openConnection();
                    int responseCode = urlConnection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        server_response = readStream(urlConnection.getInputStream());
                        //Suppression du chargement
                        progressDialog.dismiss();
                        return server_response;
                    } else {
                        //Suppression du chargement
                        progressDialog.dismiss();
                        return "Fail : No respons from server";
                    }
                }


            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
            //Suppression du chargement
            progressDialog.dismiss();
            return "Error : An Error Occured";
        } catch (IOException e) {
            //Suppression du chargement
            progressDialog.dismiss();
            e.printStackTrace();
            return "Error : An Error Occured";
        }


        //Suppression du chargement
        progressDialog.dismiss();
        return "Error : An Error Occured";
    }

    @Override
    protected void onPostExecute(String s) {


    }


// Converting InputStream to String

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();

    }

    //Méthode retournant la version acutelle de LoL
    private String getVersion() {
        try {
            String server_response;
            HttpURLConnection urlConnection = null;
            URL url = new URL("https://ddragon.leagueoflegends.com/api/versions.json");
            urlConnection = (HttpURLConnection) url.openConnection();
            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                server_response = readStream(urlConnection.getInputStream());
                String res = server_response;
                //On convertit le String en tableau JSON
                JSONArray array = new JSONArray(res);
                ArrayList<String> list = new ArrayList<String>();
                //Et on le convertit en tableau de string
                int len = array.length();
                for (int i = 0; i < len; i++) {
                    list.add(array.get(i).toString());
                }
                //On retourne la première valuer, qui correspond à la version actuelle de LoL
                return list.get(0);

            } else {
                return "Fail";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }

    public String doesUserExists(String usernameRiot, String region, String key) {
        try {
            String server_response;
            HttpURLConnection urlConnection = null;
            //Montage de l'url
            URL url = new URL("https://" + region + ".api.riotgames.com/lol/summoner/v4/summoners/by-name/" + usernameRiot + "?api_key=" + key);

            urlConnection = (HttpURLConnection) url.openConnection();
            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                server_response = readStream(urlConnection.getInputStream());
                String res = server_response;
                String str = "[" + res + "]";
                //On converti le String en tableau JSON
                JSONArray array = new JSONArray(str);
                ArrayList<String> list = new ArrayList<String>();
                //Et on le convertit en tableau de string
                int len = array.length();
                for (int i = 0; i < len; i++) {
                    list.add(array.get(i).toString());
                }

                return list.get(0);

            } else {
                return "Fail : Fail to reach server";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "false";
        }

    }


}