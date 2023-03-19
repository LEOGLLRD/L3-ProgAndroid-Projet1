package com.example.inventairelol.Util.Champion;


import android.content.Context;
import android.util.Log;

import com.example.inventairelol.Service.ApiLoL;
import com.example.inventairelol.Util.ApiKeyGetter;

import org.json.JSONObject;

import java.util.Iterator;

public class Champion {

    String id;


    String name;
    String urlImg;
    String loadingImg;

    String lore;
    String title;

    Context context;


    public Champion(String name, String id, String urlImg, Context context) {
        this.name = name;
        this.id = id;
        this.urlImg = urlImg;
        this.loadingImg = this.id + "_0.jpg";
        this.context = context;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }

    @Override
    public String toString() {
        return this.name + "," + this.urlImg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoadingImg() {
        return loadingImg;
    }

    public void setLoadingImg(String loadingImg) {
        this.loadingImg = loadingImg;
    }

    public String getLore() {
        return lore;
    }

    public void setLore(String lore) {
        this.lore = lore;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    //Met à jour la valeur du lore du champion
    public void initializeLore() {

        try {

            ApiLoL apiLoL = new ApiLoL(context);
            ApiKeyGetter keyGetter = new ApiKeyGetter(context);
            apiLoL.execute("getChampDescription", this.id);
            String res = apiLoL.get();
            //On convertit le string en json
            JSONObject jsonObject = new JSONObject(res);
            //On récupère les infos sur les champions
            JSONObject data = jsonObject.getJSONObject("data");
            //On itère et on créer un objet Champion par champion
            for (Iterator<String> it = data.keys(); it.hasNext(); ) {
                String key = it.next();
                JSONObject infoChamp = data.getJSONObject(key);
                String lore = infoChamp.getString("lore");
                this.lore = lore;
                String title = infoChamp.getString("title");
                this.title = title;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
