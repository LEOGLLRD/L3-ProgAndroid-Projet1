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

            //On vérifie si il y a un compte enregistré via les préférences
            SharedPreferences sharedPreferences = this.getSharedPreferences("user", MODE_PRIVATE);
            String pseudo = sharedPreferences.getString("pseudo", "");
            String pass = sharedPreferences.getString("password", "");

            //Récupération des valeurs via intent
            //Utilisé quand l'utilisateur ne veut pas que ses identifiants soient enregistrés
            Bundle extra = this.getIntent().getExtras();

            //Si pseudo et password ne sont pas vide alors on essaie de connecter
            if (!pseudo.equals("") && !pass.equals("")) {
                onlineMYSQL = new OnlineMYSQL(this, url, username, password);
                onlineMYSQL.execute("login", pseudo, pass);

                //Recupération de la réponse du Service
                String res = onlineMYSQL.get();

                //Si fail ou error dans la connexion on renvoie vers la page de connexion
                if (res.contains("Fail") || res.contains("Error")) {

                    //On créer une Alerte pour informer l'utilisateur
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(R.string.errorLoginTitle)
                            .setCancelable(false)
                            .setPositiveButton(R.string.understood, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Toast.makeText(getApplicationContext(), R.string.understood, Toast.LENGTH_SHORT).show();
                                    //On lance la page de connexion
                                    Intent intent = new Intent(getApplicationContext(), Login.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    builder.show();
                }

                //Sinon succes, on affiche la page d'accueil
                else {
                    //On ajoute une variable qui précise que l'utilisateur est connecté
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("connected", "true");
                    editor.apply();
                    //On lance la page d'accueil
                    replaceFragment(new HomeFragment());
                }
            }
            //Sinon on vérifie si on a des valeurs à récupérer via extra
            else if (extra != null) {
                String pseudoViaIntent = extra.getString("pseudo");
                String passwordViaIntent = extra.getString("password");
                //Si oui on vérifie qu'elles sont bien existantes
                if (pseudoViaIntent != null && passwordViaIntent != null) {
                    Log.i("in", "in");
                    //Si oui on tente la connexion
                    onlineMYSQL = new OnlineMYSQL(this, url, username, password);
                    onlineMYSQL.execute("login", pseudoViaIntent, passwordViaIntent);

                    //Recupération de la réponse du Service
                    String res = onlineMYSQL.get();

                    //Si fail ou error dans la connexion on renvoie vers la page de connexion
                    if (res.contains("Fail") || res.contains("Error")) {

                        //On créer une Alerte pour informer l'utilisateur
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(R.string.errorLoginTitle)
                                .setCancelable(false)
                                .setPositiveButton(R.string.understood, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Toast.makeText(getApplicationContext(), R.string.understood, Toast.LENGTH_SHORT).show();
                                        //On lance la page de connexion
                                        Intent intent = new Intent(getApplicationContext(), Login.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                        builder.show();
                    }

                    //Sinon succes, on affiche la page d'accueil
                    else {
                        //On ajoute une variable qui précise que l'utilisateur est connecté
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("connected", "true");
                        editor.apply();
                        //On lance la page d'accueil
                        replaceFragment(new HomeFragment());
                    }
                }
                //Si pas de valeurs existantes
                else {
                    //On lance la page de connexion
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();
                }
            }


            //Sinon on renvoie vers la page de connexion
            else {
                //On lance la page de connexion
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }


            //Pour la barre de naviguation

            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            replaceFragment(new HomeFragment());


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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //methode pour remplacer un fragment, avec en parametre le fragment de destination
    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}