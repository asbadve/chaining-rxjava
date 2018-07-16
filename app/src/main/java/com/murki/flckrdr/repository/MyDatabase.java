package com.murki.flckrdr.repository;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.murki.flckrdr.model.FlickrPhoto;

@Database(entities = {FlickrPhoto.class}, version = 1)
public abstract class MyDatabase extends RoomDatabase {
    public abstract FlickrRoomRepo flickrDao();

    private static MyDatabase INSTANCE;

    public static MyDatabase instance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    MyDatabase.class, "FlickrDb.db")
                    .build();
        }
        return INSTANCE;
    }

}
