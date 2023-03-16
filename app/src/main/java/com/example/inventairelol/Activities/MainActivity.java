package com.example.inventairelol.Activities;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.inventairelol.DataBase.SQLiteBDD;
import com.example.inventairelol.Fragments.HomeFragment;
import com.example.inventairelol.Fragments.InventoryFragment;
import com.example.inventairelol.Fragments.ProfileFragment;
import com.example.inventairelol.R;
import com.example.inventairelol.Service.ServiceOnlineMYSQL;
import com.example.inventairelol.Util.ConfigGetter;
import com.example.inventairelol.Util.Preferences.PreferenceUserRiot;
import com.example.inventairelol.Util.Preferences.PreferencesUser;
import com.example.inventairelol.databinding.ActivityMainBinding;

import java.util.Map;


public class MainActivity extends AppCompatActivity {

    static int nbInstance = 0;

    ActivityMainBinding binding;


    SQLiteBDD bddHelper;
    ServiceOnlineMYSQL serviceOnlineMYSQL;
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

        MainActivity.nbInstance++;

        try {

            //Récupération des paramétres de configurations de la base de données

            Map<String, String> config = new ConfigGetter(this).getDatabaseConfig();

            this.hostname = config.get("hostname");
            this.port = config.get("port");
            this.username = config.get("username");
            this.password = config.get("password");
            this.database = config.get("database");
            this.url = "jdbc:mysql://" + hostname + ":" + port + "/" + database;


            //Puis instanciation de la variable de connexion et connexion
            serviceOnlineMYSQL = new ServiceOnlineMYSQL(this);
            serviceOnlineMYSQL.execute("connect");
            serviceOnlineMYSQL.get();

            //On vérifie si il y a un compte enregistré via les préférences
            PreferencesUser preferencesUser = new PreferencesUser(this);
            Map<String, String> userInfo = preferencesUser.getUserInfo();


            //Gestion si préférences sauvegarder

            String save = "false";
            if (userInfo.containsKey("save")){
                save = userInfo.get("save");
            }

            if (save.equals("false")) {
                preferencesUser.setKeysEmpty(new String[]{"pseudo", "password"});
            }

            Log.i("save", save);


            String pseudo = "";
            String pass = "";
            if (userInfo.containsKey("pseudo")) {
                pseudo = userInfo.get("pseudo");
            }
            if (userInfo.containsKey("password")) {
                pass = userInfo.get("password");
            }

            //On vérifie si l'utilisateur est connecté

            String connected = "false";
            if (userInfo.containsKey("connected")){
                connected = userInfo.get("connected");
            }


            Log.i("connected", connected);

            if (connected.equals("false")) {
                //On affiche la page principale
                replaceFragment(new HomeFragment());
            }
            //Si oui
            else {
                //Si pseudo et password ne sont pas vide alors on essaie de connecter
                if (!pseudo.equals("") && !pass.equals("")) {

                    serviceOnlineMYSQL = new ServiceOnlineMYSQL(this);
                    serviceOnlineMYSQL.execute("login", pseudo, pass);

                    //Recupération de la réponse du Service
                    String res = serviceOnlineMYSQL.get();

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
                        //On lance la page d'accueil
                        replaceFragment(new HomeFragment());
                    }
                }
                //Sinon on renvoie vers la page de connexion
                else {
                    //On lance la page de connexion
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();
                }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.nbInstance--;

    }
}