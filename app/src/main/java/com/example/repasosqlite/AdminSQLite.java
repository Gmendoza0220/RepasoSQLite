package com.example.repasosqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;


/**
 * Creamos la siguiente clase SQLiteOpenHelper que nos permitirá crear una base de datos temporal
 * para la aplicación.
 */

public class AdminSQLite extends SQLiteOpenHelper {

    // Importamos los siguientes elementos:

    // Contructor
    public AdminSQLite(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // Permitirá elementos cuando se cree la instancia de esta clase
    @Override
    public void onCreate(SQLiteDatabase mcdonnals) {
        // Tabla de nuestra base de datos temporal
        mcdonnals.execSQL("CREATE TABLE MCDONNALS(ID_PEDIDO INTEGER PRIMARY KEY AUTOINCREMENT, NOMBRE_CLIENTE TEXT, COMIDA TEXT, PRECIO DOUBLE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}
}
