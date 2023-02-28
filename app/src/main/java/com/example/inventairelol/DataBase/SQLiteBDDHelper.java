package com.example.inventairelol.DataBase;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class SQLiteBDDHelper extends SQLiteOpenHelper {



    //Info BDD
    private static final int BASE_VERSION = 1;
    private static final String BASE_NOM = "inventaire.db";

    //Table user
    private static final String TABLE_USER = "user";
    public static final String COLONNE_ID = "id";
    public static final String COLONNE_PSEUDO = "pseudo";
    public static final String COLONNE_MAIL = "mail";
    public static final String COLONNE_PASSWORD = "password";


    public SQLiteBDDHelper(Context context, String nom,
                           SQLiteDatabase.CursorFactory cursorfactory, int version) {
        super(context, nom, cursorfactory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {



    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
