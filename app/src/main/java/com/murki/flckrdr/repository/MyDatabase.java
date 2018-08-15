package com.murki.flckrdr.repository;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.murki.flckrdr.model.FlickrPhoto;

@Database(entities = {FlickrPhoto.class}, version = 3)
public abstract class MyDatabase extends RoomDatabase {
    public abstract FlickrRoomRepo flickrDao();

    private static MyDatabase INSTANCE;

    public static MyDatabase instance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    MyDatabase.class, "FlickrDb.db")
                    .fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_1_3)
                    .build();
        }
        return INSTANCE;
    }


    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.

            database.execSQL("ALTER TABLE FlickrPhoto "
                    + "ADD COLUMN isFav boolean DEFAULT 1");


        }
    };

    static final Migration MIGRATION_1_3 = new Migration(1, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.

            database.execSQL("ALTER TABLE FlickrPhoto "
                    + "ADD COLUMN isFav boolean DEFAULT 1");


        }
    };

}
