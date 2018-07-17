package com.murki.flckrdr.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.databinding.BindingAdapter;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

@Entity
public class FlickrPhoto {
    @PrimaryKey
    @NonNull
    public String id;

    public String title;
    @ColumnInfo(name = "url_n")//
    public String imageUrl;

    boolean isFav;

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, String url) {
        Picasso pic = Picasso.with(view.getContext());
//        if (BuildConfig.DEBUG) {
//            pic.setLoggingEnabled(true);
//        }
        pic.load(url).into(view);
    }

}
