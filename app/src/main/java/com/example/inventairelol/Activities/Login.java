package com.example.inventairelol.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventairelol.R;
import com.google.android.material.button.MaterialButton;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView username = (TextView) findViewById(R.id.username);
        TextView password = (TextView) findViewById(R.id.password);

        MaterialButton loginbtn = (MaterialButton) findViewById(R.id.loginbtn);

        //methode pour la connexion

        loginbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //a modifier
                if(username.getText().toString().equals("test")){
                    Toast.makeText(Login.this, "Connexion réussi",Toast.LENGTH_SHORT);
                }else{
                    Toast.makeText(Login.this, "Connexion failed",Toast.LENGTH_SHORT);
                }
            }


        });

        MaterialButton goRegisterbtn = (MaterialButton) findViewById(R.id.goRegister);
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