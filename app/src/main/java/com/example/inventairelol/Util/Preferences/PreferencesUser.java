package com.example.inventairelol.Util.Preferences;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Map;

public class PreferencesUser {
    Context context;
    SharedPreferences sharedPreferences;

    public PreferencesUser(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE);

    }

    public Map<String, String> getUserInfo(){
        //Si oui, on ajoute les informations de connexion aux préférences

       return (Map<String, String>) sharedPreferences.getAll();

    }

    public void setUserInfo(String key, String value){
        //Si oui, on ajoute les informations de connexion aux préférences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void setKeysEmpty(String[] keys){
        for (String s : keys){
            setUserInfo(s, "");
        }
    }
}
