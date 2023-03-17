package com.example.inventairelol.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.ArrayRes;

import com.example.inventairelol.Service.ServiceOnlineMYSQL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SQLiteBDD extends SQLiteOpenHelper {


    private ServiceOnlineMYSQL serviceOnlineMYSQL;
    private Context context;

    //Info BDD
    public static final int BASE_VERSION = 1;
    public static final String BASE_NOM = "inventaire.db";

    //Table inventory
    public static final String TABLE_INVENTORY = "inventory";
    public static final String COLONNE_ID = "id";
    public static final String COLONNE_ID_API = "idAPI";

    private static final String REQUETE_CREATI0N_DB = "CREATE TABLE " + TABLE_INVENTORY + "("
            + COLONNE_ID + " INTEGER PRIMARY KEY," + COLONNE_ID_API + " INTEGER NOT NULL" + ")";


    public SQLiteBDD(Context context) {
        super(context, BASE_NOM, null, BASE_VERSION);
        this.context = context;


    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(REQUETE_CREATI0N_DB);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {


    }


    //Méthode appelée pour ajouter un nouvel item à l'inventaire d'un utilisateur
    public void insertItem(String idUser, String idItem) {

    }

    //Méthode appelé pour supprimer un item de l'inventaire d'un utilisateur
    public void deleteItem(String idUser, String idItem) {

    }

    public void resetInventory(SQLiteDatabase db){
        db.delete(TABLE_INVENTORY,null, null);
    }

    public ArrayList<String> getInventory(SQLiteDatabase db){
        //Read
        String[] projection = {
                COLONNE_ID_API,
                COLONNE_ID
        };

        Cursor cursor = db.rawQuery("SELECT " + COLONNE_ID_API + " FROM " + TABLE_INVENTORY, null);

        ArrayList itemIds = new ArrayList<>();

        if(cursor.moveToFirst()){
            do {
                itemIds.add(cursor.getString(cursor.getColumnIndexOrThrow(COLONNE_ID_API)));
            }while (cursor.moveToNext());
        }

        cursor.close();
        return itemIds;
    }


}
