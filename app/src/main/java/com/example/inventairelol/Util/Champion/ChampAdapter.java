package com.example.inventairelol.Util.Champion;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.inventairelol.R;
import com.example.inventairelol.Service.ApiLoL;
import com.example.inventairelol.Util.ApiKeyGetter;
import com.example.inventairelol.Util.Champion.Champion;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;


public class ChampAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Champion> champs;
    private TextView nameChamp;
    private ImageView imgChamp;

    private String version;

    private String lore;

    public ChampAdapter(Context context, ArrayList<Champion> champs) throws ExecutionException, InterruptedException {
        this.context = context;
        this.champs = champs;
        //Récupération de la version actuelle de LoL
        ApiLoL apiLoL = new ApiLoL(context);
        ApiKeyGetter keyGetter = new ApiKeyGetter(context);
        apiLoL.execute("getVersion", keyGetter.getApiKey());
        this.version = apiLoL.get();

    }

    @Override
    public int getCount() {
        return champs.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        try {


            //récupération du champion
            Champion champion = this.champs.get(i);
            view = LayoutInflater.from(context).inflate(R.layout.champlist, viewGroup, false);
            nameChamp = view.findViewById(R.id.nameChamp);
            nameChamp.setText(champion.getName());
            imgChamp = view.findViewById(R.id.imgChamp);
            String urlImg = "https://ddragon.leagueoflegends.com/cdn/" + this.version + "/img/champion/" + champion.getUrlImg();
            Glide.with(context).load(urlImg).into(imgChamp);


        } catch (Exception e) {
            e.printStackTrace();

        }

        return view;
    }


    public Champion getChamp(int position) {
        return champs.get(position);
    }




}
