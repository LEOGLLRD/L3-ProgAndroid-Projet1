package com.example.inventairelol.Util.Champion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.inventairelol.R;
import com.example.inventairelol.Service.ApiLoL;
import com.example.inventairelol.Util.Champion.Champion;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class ChampAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Champion> champs;
    private TextView nameChamp;
    private ImageView imgChamp;

    private String version;

    public ChampAdapter(Context context, ArrayList<Champion> champs) throws ExecutionException, InterruptedException {
        this.context = context;
        this.champs = champs;
        //Récupération de la version actuelle de LoL
        ApiLoL apiLoL = new ApiLoL(context);
        apiLoL.execute("getVersion", "RGAPI-d2e39834-878f-4c39-a650-406532246abe");
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
            String urlImg = "https://ddragon.leagueoflegends.com/cdn/" + this.version + "/img/champion/"+champion.getUrlImg();
            Glide.with(context).load(urlImg).into(imgChamp);

        } catch (Exception e) {
            e.printStackTrace();

        }

        return view;
    }
}
