package com.example.inventairelol.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventairelol.R;
import com.example.inventairelol.Service.OnlineMYSQL;
import com.google.android.material.button.MaterialButton;

import java.io.InputStream;
import java.util.Properties;

public class Login extends AppCompatActivity {

    OnlineMYSQL onlineMYSQL;
    String username, url, password;
    Login login = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Récupération des variables
        TextView tPseudo = (TextView) findViewById(R.id.username);
        TextView tPassword = (TextView) findViewById(R.id.password);
        MaterialButton loginbtn = (MaterialButton) findViewById(R.id.loginbtn);

        //Récupération du bouton pour aller à l'inscription
        MaterialButton goRegisterbtn = (MaterialButton) findViewById(R.id.goRegister);

        //Appelé quand le bouton aller à l'inscription est appelé
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


        //Appelé quand le bouton de connexion est pressé
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    //Instanciation de la variable de connexion à la base de données
                    onlineMYSQL = new OnlineMYSQL(login, url, username, password);

                    //Appel de la méthode de login
                    onlineMYSQL.execute("login", tPseudo.getText().toString(), tPassword.getText().toString());

                    //On créer une Alerte pour informer l'utilisateur
                    AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                    builder.setTitle(R.string.errorLoginTitle)
                            .setCancelable(false)
                            .setPositiveButton(R.string.understood, null);

                    String res = onlineMYSQL.get();
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
                    //Pas d'erreurs / d'echecs dans la connexion
                    else {
                        //Message indiquant à l'utilisateur qu'il est connecté
                        Toast.makeText(getApplicationContext(), R.string.connected, Toast.LENGTH_SHORT).show();
                        //On lance la page d'accueil
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
}