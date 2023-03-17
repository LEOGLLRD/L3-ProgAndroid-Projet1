package com.example.inventairelol.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.inventairelol.R;
import com.example.inventairelol.Service.ApiLoL;
import com.example.inventairelol.Service.ServiceOnlineMYSQL;
import com.example.inventairelol.Util.ConfigGetter;
import com.example.inventairelol.Util.Preferences.PreferenceUserRiot;
import com.example.inventairelol.Util.Preferences.PreferencesUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Register extends AppCompatActivity {

    static int nbInstance = 0;

    ServiceOnlineMYSQL serviceOnlineMYSQL;
    ApiLoL apiLoL;
    EditText eMail, ePseudo, ePassword;
    String username, url, password;
    private Register register = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Register.nbInstance++;

        CheckBox remember = (CheckBox) findViewById(R.id.remember2);
        EditText usernameRiot = (EditText) findViewById(R.id.riotUsername);
        Spinner spinnerRegion = (Spinner) findViewById(R.id.region);


        try {
            //Récupération des EditTexts
            this.eMail = findViewById(R.id.mail);
            this.ePseudo = findViewById(R.id.pseudo);
            this.ePassword = findViewById(R.id.password);

            //Récupération du fichier de configuration de la base de données
            Map<String, String> config = new ConfigGetter(this).getDatabaseConfig();
            //Récupération des paramétres de configurations de la base de données via le fichier config
            String hostname = config.get("hostname");
            String port = config.get("port");
            String database = config.get("database");
            this.username = config.get("username");
            this.password = config.get("password");
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
                        apiLoL.execute("checkIfUserExists", spinnerRegion.getSelectedItem().toString(), usernameRiot.getText().toString());
                        String res2 = apiLoL.get();

                        //Vérification si les compte Riot existe
                        if (res2.equals("false")) {

                            builder.setMessage(R.string.riotAccountDoesntExist);
                            builder.show();
                        } else {

                            //Si oui on récupère les infos du compte
                            //Récupération des info riot

                            apiLoL = new ApiLoL(register);
                            apiLoL.execute("getUserInfo", spinnerRegion.getSelectedItem().toString(), usernameRiot.getText().toString());
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
                                serviceOnlineMYSQL = new ServiceOnlineMYSQL(register);


                                serviceOnlineMYSQL.execute("register", eMail.getText().toString(), ePseudo.getText().toString(), ePassword.getText().toString(), usernameRiot.getText().toString(), spinnerRegion.getSelectedItem().toString());

                                String res = serviceOnlineMYSQL.get();

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
                                    else if (res.contains("Riot account already linked to another user")) {
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

                                    PreferencesUser preferencesUser = new PreferencesUser(Register.this);
                                    preferencesUser.setUserInfo("pseudo", ePseudo.getText().toString());
                                    preferencesUser.setUserInfo("password", ePassword.getText().toString());
                                    preferencesUser.setUserInfo("connected", "true");

                                    if (remember.isChecked()) {
                                        //Si oui, on ajoute les informations de connexion aux préférences User
                                        preferencesUser.setUserInfo("save", "true");

                                    } else {
                                        //Si non, on met save a false
                                        preferencesUser.setUserInfo("save", "false");
                                    }

                                    //Message indiquant à l'utilisateur qu'il est connecté
                                    Toast.makeText(getApplicationContext(), R.string.connected, Toast.LENGTH_SHORT).show();
                                    //Enfin on affiche la page d'accueil
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Register.nbInstance--;

    }

}