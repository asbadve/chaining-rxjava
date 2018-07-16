package com.murki.flckrdr.repository;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.murki.flckrdr.model.FlickrPhoto;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

@Dao
public interface FlickrRoomRepo {


    @Query("SELECT * FROM FlickrPhoto")
    Flowable<List<FlickrPhoto>> getAllFlickerPhotos();

    @Query("SELECT * FROM FlickrPhoto")
    Maybe<List<FlickrPhoto>> getAll();


    @Query("SELECT * FROM FlickrPhoto")
    List<FlickrPhoto> getAllFlickerPhotosList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<FlickrPhoto> flickrCardVMS);
}
