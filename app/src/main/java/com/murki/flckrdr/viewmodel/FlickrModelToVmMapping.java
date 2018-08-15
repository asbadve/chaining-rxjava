package com.murki.flckrdr.viewmodel;


import android.util.Log;

import com.murki.flckrdr.model.FlickrPhoto;
import com.murki.flckrdr.model.RecentPhotosResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Predicate;

public class FlickrModelToVmMapping implements Predicate<RecentPhotosResponse> {
    private static volatile FlickrModelToVmMapping instance;
    private static final String CLASSNAME = FlickrModelToVmMapping.class.getCanonicalName();

    @Override
    public boolean test(RecentPhotosResponse recentPhotosResponse) throws Exception {
        List<FlickrPhoto> photoList = recentPhotosResponse.photos.photo;
        Log.d(CLASSNAME, "FlickrModelToVmMapping.call() - Response list size=" + photoList.size());
        List<FlickrCardVM> flickrCardVMs = new ArrayList<>(photoList.size());
        for (FlickrPhoto photo : photoList) {
            flickrCardVMs.add(new FlickrCardVM(photo.title, photo.getUrl_n()));
        }

        if (flickrCardVMs.size() > 0) {
            return true;
        }
        return false;
    }

    public static FlickrModelToVmMapping instance() {
        if (instance == null) {
            instance = new FlickrModelToVmMapping();
        }
        return instance;
    }

//        implements Predicate<RecentPhotosResponse>, <List<FlickrCardVM>> {

//    private static final String CLASSNAME = FlickrModelToVmMapping.class.getCanonicalName();
//    private static volatile FlickrModelToVmMapping instance;
//
//    public static FlickrModelToVmMapping instance() {
//        if (instance == null) {
//            instance = new FlickrModelToVmMapping();
//        }
//        return instance;
//    }
//
//    @Override
//    public List<FlickrCardVM> call(RecentPhotosResponse recentPhotosResponse) {
//        List<FlickrPhoto> photoList = recentPhotosResponse.getValue().photos.photo;
//        Log.d(CLASSNAME, "FlickrModelToVmMapping.call() - Response list size=" + photoList.size());
//        List<FlickrCardVM> flickrCardVMs = new ArrayList<>(photoList.size());
//        for (FlickrPhoto photo : photoList) {
//            flickrCardVMs.add(new FlickrCardVM(photo.title, photo.url_n));
//        }
//        return flickrCardVMs;
//    }


}
