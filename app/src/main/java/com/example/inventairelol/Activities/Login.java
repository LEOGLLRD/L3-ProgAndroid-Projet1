package com.example.inventairelol.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventairelol.R;
import com.example.inventairelol.Service.ServiceOnlineMYSQL;
import com.example.inventairelol.Util.ConfigGetter;
import com.example.inventairelol.Util.Preferences.PreferenceUserRiot;
import com.example.inventairelol.Util.Preferences.PreferencesUser;
import com.google.android.material.button.MaterialButton;

import java.util.Map;

public class Login extends AppCompatActivity {

    static int nbInstance = 0;

    ServiceOnlineMYSQL serviceOnlineMYSQL;
    String username, url, password;
    Login login = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Login.nbInstance++;

        //Récupération des variables
        TextView tPseudo = (TextView) findViewById(R.id.username);
        TextView tPassword = (TextView) findViewById(R.id.password);
        MaterialButton loginbtn = (MaterialButton) findViewById(R.id.loginbtn);
        CheckBox remember = (CheckBox) findViewById(R.id.remember);


        MaterialButton goRegisterbtn = (MaterialButton) findViewById(R.id.goRegister);
        goRegisterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                finish();
            }
        });

        try {

            //Récupération du fichier de configuration de la base de données
            Map<String, String> config = new ConfigGetter(this).getDatabaseConfig();

            //Récupération des paramétres de configurations de la base de données via le fichier config
            String hostname = config.get("hostname");
            String port = config.get("port");
            String database = config.get("databse");
            this.username = config.get("username");
            this.password = config.get("password");
            this.url = "jdbc:mysql://" + hostname + ":" + port + "/" + database;


        } catch (Exception e) {
            e.printStackTrace();
        }


        //Appelé quand le bouton de connexion est pressé
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    //Instanciation de la variable de connexion à la base de données
                    serviceOnlineMYSQL = new ServiceOnlineMYSQL(login);

                    //Appel de la méthode de login
                    serviceOnlineMYSQL.execute("login", tPseudo.getText().toString(), tPassword.getText().toString());

                    //On créer une Alerte pour informer l'utilisateur
                    AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                    builder.setTitle(R.string.errorLoginTitle)
                            .setCancelable(false)
                            .setPositiveButton(R.string.understood, null);

                    String res = serviceOnlineMYSQL.get();
                    Log.i("res", res);

                    //Vérification echec
                    if (res.contains("Fail")) {

                        //Vérification si pseudo vide
                        if (res.contains("Pseudo Empty")) {
                            builder.setMessage(R.string.emptyPseudo);
                            builder.show();
                        }
                        //Vérification si Password vide
                        else if (res.contains("Password Empty")) {
                            builder.setMessage(R.string.emptyPassword);
                            builder.show();
                        }
                        //Vérification si la connexion échou dû à un mauvais mot de passe ou pseudo
                        else if (res.contains("Login Failed")) {
                            builder.setMessage(R.string.wrongPasswordOrPseudo);
                            builder.show();
                        }


                    }
                    //Vérification erreur
                    else if (res.contains("Error")) {
                        builder.setMessage(R.string.errorMessage);
                        builder.show();
                    }
                    //Pas d'erreurs / echecs dans l'enregistrement
                    else {


                        //On vérifie si l'utilisateur veut que l'application se rappelle de ses identifiants
                        //pour le prochain lancement pour se connecter automatiquement

                        PreferencesUser preferencesUser = new PreferencesUser(Login.this);
                        preferencesUser.setUserInfo("pseudo", tPseudo.getText().toString());
                        preferencesUser.setUserInfo("password", tPassword.getText().toString());
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

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Login.nbInstance--;




    }
}