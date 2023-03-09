package com.example.inventairelol.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.inventairelol.R;
import com.example.inventairelol.Service.OnlineMYSQL;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Register extends AppCompatActivity {

    OnlineMYSQL onlineMYSQL;
    EditText eMail, ePseudo, ePassword;
    String username, url, password;
    private Register register = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button button = (Button) findViewById(R.id.goLogin);
        CheckBox remember = (CheckBox) findViewById(R.id.remember2);


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

                onlineMYSQL = new OnlineMYSQL(register, url, username, password);

                onlineMYSQL.execute("register", eMail.getText().toString(), ePseudo.getText().toString(), ePassword.getText().toString());
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


                    String res = onlineMYSQL.get();
                    Log.i("res", res);

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
                    }

                    //On voit avec un pattern Regex si on le String de retour contient "Error"
                    else if (res.contains("Error")) {
                        builder.setMessage(R.string.errorMessage);
                        builder.show();
                    }
                    //Pas d'erreurs / echecs dans l'enregistrement
                    else {
                        //Message indiquant à l'utilisateur qu'il est connecté
                        Toast.makeText(getApplicationContext(), R.string.connected, Toast.LENGTH_SHORT).show();

                        //On vérifie si l'utilisateur veut que l'application se rappelle de ses identifiants
                        //pour le prochain lancement pour se connecter automatiquement
                        if (remember.isChecked()) {
                            //Si oui, on ajoute les informations de connexion aux préférences
                            SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("pseudo", ePseudo.getText().toString());
                            editor.putString("password", ePassword.getText().toString());
                            editor.apply();
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

                            //L'utilisateur ne veut pas que ses identifiants soient enregistrés,
                            //donc nous allons les passer via intent, à la fermeture de l'application
                            //ils ne seront pas conservés

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("pseudo", ePseudo.getText().toString());
                            intent.putExtra("password", ePassword.getText().toString());
                            //On lance la page d'accueil
                            startActivity(intent);
                            finish();


                        }
                    }


                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }


        });

    }
}