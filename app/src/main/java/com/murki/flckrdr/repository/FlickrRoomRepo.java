package com.murki.flckrdr.repository;

import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;
import android.arch.persistence.room.Transaction;
import android.database.DatabaseUtils;
import android.util.Log;

import com.google.common.base.Optional;
import com.murki.flckrdr.model.FlickrPhoto;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@Dao
public abstract class FlickrRoomRepo {


    @Query("SELECT * FROM FlickrPhoto")
    public abstract Flowable<List<FlickrPhoto>> getAllFlickerPhotos();

    @Query("SELECT * FROM FlickrPhoto")
    public abstract Maybe<List<FlickrPhoto>> getAll();

    @Query("SELECT * FROM FlickrPhoto WHERE id=:id")
    public abstract Optional<FlickrPhoto> getFlickerPhoto(int id);


    @Query("SELECT * FROM FlickrPhoto")
    public abstract List<FlickrPhoto> getAllFlickerPhotosList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(List<FlickrPhoto> flickrCardVMS);

    @Insert()
    public abstract void insertPhoto(FlickrPhoto photo);

//    @Query("INSERT INTO FlickrPhoto VALUES (:id,:title,:url_n)")
//    public abstract void insertWithDetail(String id, String title, String url_n);

    @RawQuery()
    public abstract int insertWithDetail(SupportSQLiteQuery supportSQLiteQuery);

    @Query("SELECT * from FlickrPhoto where id=:id")
    public abstract boolean isPhotoPresentInDb(String id);


    @Transaction
    public void partialUpdate(List<FlickrPhoto> flickrPhotos, final MyDatabase myDatabase) {
        Observable.fromIterable(flickrPhotos)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<FlickrPhoto>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(FlickrPhoto flickrPhoto) {
                        if (flickrPhoto != null) {
                            final String id = flickrPhoto.getId();
                            if (!isPhotoPresentInDb(id)) {
                                insertPhoto(flickrPhoto);
                            } else {
//                                String[] args =new String[]{String.valueOf(flickrPhoto.getTitle()), String.valueOf(flickrPhoto.getUrl_n()), String.valueOf(1), String.valueOf(flickrPhoto.getId())};
//                                myDatabase.getOpenHelper().getWritableDatabase().execSQL("UPDATE FlickrPhoto set title  =':?',url_n='?',isFav='?' WHERE id='?'",args);
                                String replace = flickrPhoto.getTitle().replaceAll("'", "\'");
                                myDatabase.getOpenHelper().getWritableDatabase().execSQL("UPDATE FlickrPhoto set title = '" + replace + "',url_n='" + flickrPhoto.getUrl_n() + "',isFav='" + 1 + "' WHERE id='" + flickrPhoto.getId() + "'");
//                                SupportSQLiteQuery supportSQLiteQuery = new SimpleSQLiteQuery("UPDATE FlickrPhoto set title = '?',url_n='?',isFav='?' WHERE id='?'", args);
//                                SupportSQLiteQuery  supportSQLiteQuery1 = SupportSQLiteQueryBuilder.builder(FlickrPhoto.class.getSimpleName())
//                                insertWithDetail(supportSQLiteQuery);
                            }
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("error", "onError() called with: e = [" + e + "]");
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }
}
