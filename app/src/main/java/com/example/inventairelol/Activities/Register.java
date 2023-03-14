package com.example.inventairelol.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.inventairelol.R;
import com.example.inventairelol.Service.ApiLoL;
import com.example.inventairelol.Service.OnlineMYSQL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Register extends AppCompatActivity {

    OnlineMYSQL onlineMYSQL;
    ApiLoL apiLoL;
    EditText eMail, ePseudo, ePassword;
    String username, url, password;
    private Register register = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        CheckBox remember = (CheckBox) findViewById(R.id.remember2);
        EditText usernameRiot = (EditText) findViewById(R.id.riotUsername);
        Spinner spinnerRegion = (Spinner) findViewById(R.id.region);


        try {
            //Récupération des EditTexts
            this.eMail = findViewById(R.id.mail);
            this.ePseudo = findViewById(R.id.pseudo);
            this.ePassword = findViewById(R.id.password);

            //Récupération du fichier de configuration de la base de données
            Properties p = new Properties();
            AssetManager assetManager = getApplicationContext().getAssets();
            InputStream inputStream = assetManager.open("config.properties");
            p.load(inputStream);

            //Récupération des paramétres de configurations de la base de données via le fichier config
            String hostname = p.getProperty("hostname");
            String port = p.getProperty("port");
            String database = p.getProperty("database");
            this.username = p.getProperty("username");
            this.password = p.getProperty("password");
            this.url = "jdbc:mysql://" + hostname + ":" + port + "/" + database;


        } catch (Exception e) {
            e.printStackTrace();
        }


        //Retour à la page de connexion
        Button buttonLogin = (Button) findViewById(R.id.goLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }
        });


        //Gestion Enregistrement
        Button registerBtn = (Button) findViewById(R.id.registerbtn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    //On créer une Alerte pour informer l'utilisateur
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                    builder.setTitle(R.string.errorRegisterTitle)
                            .setCancelable(false)
                            .setPositiveButton(R.string.understood, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), R.string.understood, Toast.LENGTH_SHORT).show();
                                }
                            });

                    //Gestion vérification info Riot
                    //Vérification que le compte Riot a été renseigné
                    if (usernameRiot.getText().toString().isEmpty()) {
                        builder.setMessage(R.string.riotAccountEmpty);
                        builder.show();
                    } else {

                        apiLoL = new ApiLoL(register);
                        apiLoL.execute("checkIfUserExists", "RGAPI-d2e39834-878f-4c39-a650-406532246abe", spinnerRegion.getSelectedItem().toString(), usernameRiot.getText().toString());
                        String res2 = apiLoL.get();

                        //Vérification si les compte Riot existe
                        if (res2.equals("false")) {

                            builder.setMessage(R.string.riotAccountDoesntExist);
                            builder.show();
                        } else {

                            //Si oui on récupère les infos du compte
                            //Récupération des info riot

                            apiLoL = new ApiLoL(register);
                            apiLoL.execute("getUserInfo", "RGAPI-d2e39834-878f-4c39-a650-406532246abe", spinnerRegion.getSelectedItem().toString(), usernameRiot.getText().toString());
                            String res3 = apiLoL.get();

                            //Vérification pas de fail ou d'erreur
                            if (res3.contains("Fail :") || res3.contains("Error :")) {
                                builder.setMessage(R.string.errorMessage);
                                builder.show();

                            } else {

                                //Mise sous format JSON
                                String str = "[" + res3 + "]";
                                JSONArray array = new JSONArray(str);
                                //Création des variables Riot
                                String id = null, accountId = null, puuid = null, name = null, profileIconId = null, summonerLevel = null;
                                //Parcours du tableau JSON et récupération des différentes variables
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = array.getJSONObject(i);

                                    id = object.getString("id");
                                    accountId = object.getString("accountId");
                                    puuid = object.getString("puuid");
                                    name = object.getString("name");
                                    profileIconId = object.getString("profileIconId");
                                    summonerLevel = object.getString("summonerLevel");
                                }

                                //Gestion enregistrement de l'utilisateur
                                onlineMYSQL = new OnlineMYSQL(register, url, username, password);


                                onlineMYSQL.execute("register", eMail.getText().toString(), ePseudo.getText().toString(), ePassword.getText().toString(), usernameRiot.getText().toString(), spinnerRegion.getSelectedItem().toString());

                                String res = onlineMYSQL.get();

                                //Si on trouve le message Fail
                                if (res.contains("Fail")) {

                                    //On vérifie quel est la cause du Fail
                                    //Si c'est à cause d'un mail vide
                                    if (res.contains("Mail Empty")) {
                                        builder.setMessage(R.string.emptyPassword);
                                        builder.show();
                                    }

                                    //Si c'est à cause d'un password vide
                                    else if (res.contains("Password Empty")) {
                                        builder.setMessage(R.string.emptyPassword);
                                        builder.show();
                                    }
                                    //Si c'est à cause d'un password vide
                                    else if (res.contains("Mail or Pseudo already used")) {
                                        builder.setMessage(R.string.alreadyUsedPseudoOrMail);
                                        builder.show();
                                    }
                                    //Si c'est à cause du compte Riot qui est déjà link à un autre utilisateur
                                    else if (res.contains("Riot account already linked to another user")){
                                        builder.setMessage(R.string.riotAccountAlreadyLinked);
                                        builder.show();
                                    }
                                }

                                //On voit si on le String de retour contient "Error"
                                else if (res.contains("Error")) {
                                    builder.setMessage(R.string.errorMessage);
                                    builder.show();
                                }
                                //Pas d'erreurs / echecs dans l'enregistrement
                                else {


                                    //On vérifie si l'utilisateur veut que l'application se rappelle de ses identifiants
                                    //pour le prochain lancement pour se connecter automatiquement
                                    if (remember.isChecked()) {
                                        //Si oui, on ajoute les informations de connexion aux préférences
                                        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("pseudo", ePseudo.getText().toString());
                                        editor.putString("password", ePassword.getText().toString());
                                        editor.apply();

                                        //Et on ajoute les informations du compte Riot
                                        //Récupération des préférences
                                        SharedPreferences accountLol = getSharedPreferences("accountLolRiot", Context.MODE_PRIVATE);
                                        editor = accountLol.edit();

                                        editor.putString("idRiot", id);
                                        editor.putString("accountIdRiot", accountId);
                                        editor.putString("puuidRiot", puuid);
                                        editor.putString("nameRiot", name);
                                        editor.putString("profileIconIdRiot", profileIconId);
                                        editor.putString("summonerLevelRiot", summonerLevel);
                                        editor.apply();
                                        //Message indiquant à l'utilisateur qu'il est connecté
                                        Toast.makeText(getApplicationContext(), R.string.connected, Toast.LENGTH_SHORT).show();
                                        //Enfin on affiche la page d'accueil
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        //Si non, on vide les préférences
                                        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("pseudo", "");
                                        editor.putString("password", "");
                                        editor.apply();
                                        SharedPreferences accountLol = getSharedPreferences("accountLolRiot", Context.MODE_PRIVATE);
                                        editor = accountLol.edit();

                                        editor.putString("idRiot", "");
                                        editor.putString("accountIdRiot", "");
                                        editor.putString("puuidRiot", "");
                                        editor.putString("nameRiot", "");
                                        editor.putString("profileIconIdRiot", "");
                                        editor.putString("summonerLevelRiot", "");
                                        editor.apply();

                                        //Message indiquant à l'utilisateur qu'il est connecté
                                        Toast.makeText(getApplicationContext(), R.string.connected, Toast.LENGTH_SHORT).show();

                                        //L'utilisateur ne veut pas que ses identifiants soient enregistrés,
                                        //donc nous allons les passer via intent, à la fermeture de l'application
                                        //ils ne seront pas conservés

                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.putExtra("pseudo", ePseudo.getText().toString());
                                        intent.putExtra("password", ePassword.getText().toString());
                                        //Idem pour les info Riot
                                        intent.putExtra("idRiot", id);
                                        intent.putExtra("accountIdRiot", accountId);
                                        intent.putExtra("puuidRiot", puuid);
                                        intent.putExtra("nameRiot", name);
                                        intent.putExtra("profileIconIdRiot", profileIconId);
                                        intent.putExtra("summonerLevelRiot", summonerLevel);
                                        //On lance la page d'accueil
                                        startActivity(intent);
                                        finish();


                                    }
                                }

                            }
                        }
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        });

    }
}