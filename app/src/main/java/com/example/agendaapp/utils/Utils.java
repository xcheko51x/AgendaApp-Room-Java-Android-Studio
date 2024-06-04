package com.example.agendaapp.utils;

import android.content.Context;

import androidx.room.Room;

import com.example.agendaapp.database.AppDatabase;

public class Utils {

    final private String DB_NAME = "dbAgendaApp";

    AppDatabase db;

    public String getDbName() {
        return DB_NAME;
    }

    public  AppDatabase getAppDatabase(Context context) {
        db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_NAME).build();
        return db;
    }

}
