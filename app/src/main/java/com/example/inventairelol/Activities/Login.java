package com.example.inventairelol.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventairelol.R;
import com.example.inventairelol.Service.OnlineMYSQL;
import com.google.android.material.button.MaterialButton;

public class Login extends AppCompatActivity {

    OnlineMYSQL onlineMYSQL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Récupération des variables
        TextView username = (TextView) findViewById(R.id.username);
        TextView password = (TextView) findViewById(R.id.password);
        MaterialButton loginbtn = (MaterialButton) findViewById(R.id.loginbtn);

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