package com.example.inventairelol.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Récupération des variables
        TextView username = (TextView) findViewById(R.id.username);
        TextView password = (TextView) findViewById(R.id.password);
        MaterialButton loginbtn = (MaterialButton) findViewById(R.id.loginbtn);

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
        loginbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){



                //a modifier
                Context context = getApplicationContext();
                if(username.getText().toString().equals("test")){
                    CharSequence text = "Connecté!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }else{
                    CharSequence text = "Echec de la connexion!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }

        });

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
    }
}