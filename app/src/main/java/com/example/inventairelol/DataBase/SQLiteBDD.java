package com.example.inventairelol.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.inventairelol.Service.ServiceSQLite;


public class SQLiteBDD extends SQLiteOpenHelper {


    private ServiceSQLite serviceSQLite;

    private String pseudoUser;

    //Info BDD
    private static final int BASE_VERSION = 1;
    private static final String BASE_NOM = "inventaire.db";

    //Table inventory
    private static final String TABLE_INVENTORY = "user";
    public static final String COLONNE_ID = "id";
    public static final String COLONNE_ID_API = "idAPI";
    public static final String COLONNE_NOM_ITEM = "nomItem";

    private static final String REQUETE_CREATI0N_DB = "CREATE TABLE " + TABLE_INVENTORY + "("
            + COLONNE_ID + " INTEGER PRIMARY KEY," + COLONNE_ID_API + " INTEGER NOT NULL,"
            + COLONNE_NOM_ITEM + " TEXT" + ")";




    public SQLiteBDD(Context context, String pseudoUser) {
        super(context, BASE_NOM, null, BASE_VERSION);
        this.pseudoUser = pseudoUser;

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(REQUETE_CREATI0N_DB);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }



    //Méthode appelée pour ajouter un nouvel item à l'inventaire d'un utilisateur
    public void insertItem(String idUser, String idItem){

    }

    //Méthode appelé pour supprimer un item de l'inventaire d'un utilisateur
    public void deleteItem(String idUser, String idItem){


    }




}
