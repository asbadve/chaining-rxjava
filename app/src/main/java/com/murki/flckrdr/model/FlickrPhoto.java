package com.murki.flckrdr.model;

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
    public String url_n;

    public boolean isFav;

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

    public String getUrl_n() {
        return url_n;
    }

    public void setUrl_n(String url_n) {
        this.url_n = url_n;
    }

    @BindingAdapter({"bind:url_n"})
    public static void loadImage(ImageView view, String url) {
        Picasso pic = Picasso.with(view.getContext());
//        if (BuildConfig.DEBUG) {
//            pic.setLoggingEnabled(true);
//        }
        pic.load(url).into(view);
    }

}
