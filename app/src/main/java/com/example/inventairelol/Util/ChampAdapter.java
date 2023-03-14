package com.example.inventairelol.Util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.inventairelol.R;
import com.example.inventairelol.Service.GetMethodDemo;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class ChampAdapter extends ArrayAdapter {
    Context context;

    public ChampAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.champlist, parent, false);
        }

        try {
            GetMethodDemo getMethodDemo = new GetMethodDemo(this.getContext());
            getMethodDemo.execute("getAllChampInfo", "RGAPI-d2e39834-878f-4c39-a650-406532246abe", "EUW1");

            ArrayList<ArrayList<String>> champs = new ArrayList<>();
            //On récupère le json des champion
            String res = getMethodDemo.get();

            //On convertit le String en tableau JSON
            JSONArray array = new JSONArray("["+res+"]");
            ArrayList<String> list = new ArrayList<String>();
            //Et on le convertit en tableau de string
            int len = array.length();
            for (int i = 0; i < len; i++) {
                list.add(array.get(i).toString());
            }
            String data = array.getString(3);
            Log.v("data", data);

            Log.i("res json", res);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
