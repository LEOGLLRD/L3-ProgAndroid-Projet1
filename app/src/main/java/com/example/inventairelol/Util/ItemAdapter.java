package com.example.inventairelol.Util;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.inventairelol.R;
import com.example.inventairelol.Service.ApiLoL;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ItemAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Item> items;
    private TextView nameItem;
    private ImageView imgItem;

    private String version;

    public ItemAdapter(Context context, ArrayList<Item> items) throws ExecutionException, InterruptedException {
        this.context = context;
        this.items = items;
        //Récupération de la version actuelle de LoL
        ApiLoL apiLoL = new ApiLoL(context);
        apiLoL.execute("getVersion", "RGAPI-d2e39834-878f-4c39-a650-406532246abe");
        this.version = apiLoL.get();
    }

    @Override
    public int getCount() {
        return items.size();
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


            //récupération des items
            Item item = this.items.get(i);
            view = LayoutInflater.from(context).inflate(R.layout.itemlist, viewGroup, false);
            nameItem = view.findViewById(R.id.nameItem);
            nameItem.setText(item.getName());
            imgItem = view.findViewById(R.id.imgItem);
            String urlImg = "https://ddragon.leagueoflegends.com/cdn/" + version + "/img/item/" + item.getUrlImg();
            Log.v("urlImg", urlImg);
            Glide.with(context).load(urlImg).into(imgItem);

        } catch (Exception e) {
            e.printStackTrace();

        }

        return view;
    }
}
