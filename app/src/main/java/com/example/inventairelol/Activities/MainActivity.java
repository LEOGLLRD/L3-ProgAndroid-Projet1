package com.example.inventairelol.Activities;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.inventairelol.DataBase.SQLiteBDDHelper;
import com.example.inventairelol.Fragments.HomeFragment;
import com.example.inventairelol.Fragments.InventoryFragment;
import com.example.inventairelol.Fragments.ProfileFragment;
import com.example.inventairelol.R;
import com.example.inventairelol.Service.OnlineMYSQL;
import com.example.inventairelol.databinding.ActivityMainBinding;

import java.io.InputStream;
import java.util.PrimitiveIterator;
import java.util.Properties;


public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;


    SQLiteBDDHelper bddHelper;
    OnlineMYSQL onlineMYSQL;
    boolean isConnected;
    private static String hostname;
    private static String port;
    private static String database;
    private static String username;
    private static String password;
    private static String url;
    MainActivity mainActivity = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {


            //Récupération du fichier de configuration de la base de données
            Properties p = new Properties();
            AssetManager assetManager = getApplicationContext().getAssets();
            InputStream inputStream = assetManager.open("config.properties");
            p.load(inputStream);

            //Récupération des paramétres de configurations de la base de données via le fichier config
            this.hostname = p.getProperty("hostname");
            this.port = p.getProperty("port");
            this.database = p.getProperty("database");
            this.username = p.getProperty("username");
            this.password = p.getProperty("password");
            this.url = "jdbc:mysql://" + hostname + ":" + port + "/" + database;

            //Puis instanciation de la variable de connexion et connexion
            onlineMYSQL = new OnlineMYSQL(this, url, username, password);
            onlineMYSQL.execute("connect");

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            //On vérifie si il y a un compte enregistré
            SharedPreferences sharedPreferences = getSharedPreferences("user", MainActivity.MODE_PRIVATE);
            String pseudo = sharedPreferences.getString("pseudo", "");
            String password = sharedPreferences.getString("password", "");

            //Pour la barre de naviguation
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            Log.i("prefPseudo", pseudo);

            //Si un pseudo et un password avaient été enregistrés
            if (!pseudo.equals("") && !password.equals("")) {
                //On essaie alors de connecter l'utilisateur
                onlineMYSQL = new OnlineMYSQL(this, this.url, this.username, this.password);
                //Appel de la méthode de login
                onlineMYSQL.execute("login", pseudo, password);
                //En attente de la réponse du Service
                String res = onlineMYSQL.get();
                //Si un Fail ou une Error
                if (res.contains("Fail") || res.contains("Error")) {
                    //On affiche un message que l'auto-login a échoué
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(R.string.errorLoginTitle)
                            .setCancelable(false)
                            .setPositiveButton(R.string.understood, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), R.string.understood, Toast.LENGTH_SHORT).show();
                                    //On affiche la page de connexion
                                    Intent intent = new Intent(mainActivity, Login.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });

                    builder.show();
                }
                //Sinon succès
                else {
                    //On affiche la page d'accueil
                    replaceFragment(new HomeFragment());
                }
            }
            //Sinon
            else {
                //On affiche un message que l'auto-login a échoué
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.errorLoginTitle)
                        .setCancelable(false)
                        .setPositiveButton(R.string.understood, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), R.string.understood, Toast.LENGTH_SHORT).show();
                                //On affiche la page de connexion
                                Intent intent = new Intent(mainActivity, Login.class);
                                startActivity(intent);
                                finish();
                            }
                        });

                builder.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        binding.bottomNavigationView.setOnItemReselectedListener(item -> {

            switch (item.getItemId()) {

                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.inventory:
                    replaceFragment(new InventoryFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    break;

            }

        });
    }

    //methode pour remplacer un fragment, avec en parametre le fragment de destination
    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}