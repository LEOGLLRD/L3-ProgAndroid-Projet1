package com.example.inventairelol.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.inventairelol.R;
import com.example.inventairelol.Service.OnlineMYSQL;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    OnlineMYSQL onlineMYSQL;
    EditText mail, pseudo, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        try {
            //Récupération des EditTexts
            this.mail = findViewById(R.id.mail);
            this.pseudo = findViewById(R.id.pseudo);
            this.password = findViewById(R.id.password);

            //Récupération du fichier de configuration de la base de données
            Properties p = new Properties();
            AssetManager assetManager = getApplicationContext().getAssets();
            InputStream inputStream = assetManager.open("config.properties");
            p.load(inputStream);

            //Récupération des paramétres de configurations de la base de données via le fichier config
            String hostname = p.getProperty("hostname");
            String port = p.getProperty("port");
            String database = p.getProperty("database");
            String username = p.getProperty("username");
            String password = p.getProperty("password");
            String url = "jdbc:mysql://" + hostname + ":" + port + "/" + database;

            //Puis instanciation de la variable de connexion et connexion
            this.onlineMYSQL = new OnlineMYSQL(this, url, username, password);

        } catch (Exception e) {
            e.printStackTrace();
        }


        Button buttonLogin = (Button) findViewById(R.id.goLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        Button register = (Button) findViewById(R.id.registerbtn);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onlineMYSQL.execute("register", mail.getText().toString(), pseudo.getText().toString(), password.getText().toString());
                try {
                    String res = onlineMYSQL.get();
                    //On voit avec un pattern Regex si on le String de retour contient "Fail"
                    Pattern pattern;
                    Matcher matcher;
                    pattern = Pattern.compile("Fail");
                    matcher = pattern.matcher(res);

                    //Si on trouve le message Fail
                    if (matcher.find()) {

                        //On vérifie quel est la cause du Fail
                        pattern = Pattern.compile("Password Empty");
                        matcher = pattern.matcher(res);
                        //Si c'est à cause d'un password vide
                        if (matcher.find()) {
                            //On créer une Alerte pour informer l'utilisateur
                            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                            builder.setTitle(R.string.errorRegisterTitle)
                                    .setMessage("Vous devez avoir un mot de passe qui n'est pas vide !")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(getApplicationContext(), "Selected Option: YES", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(Register.this, MainActivity.class);
                startActivity(intent);
                finish();
            }


        }
    }