package com.murki.flckrdr.repository;

import com.murki.flckrdr.BuildConfig;
import com.murki.flckrdr.model.RecentPhotosResponse;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;


public class FlickrNetworkRepository {

    private static final String CLASSNAME = FlickrNetworkRepository.class.getCanonicalName();
    private static final String ENDPOINT = "https://api.flickr.com/services/rest/";
    private static IFlickrAPI flickrAPI;

    public FlickrNetworkRepository() {
        if (flickrAPI == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            flickrAPI = retrofit.create(IFlickrAPI.class);
        }
    }

    //    @RxLogObservable
    public Observable<RecentPhotosResponse> getRecentPhotos() {
//        if (true) return Observable.error(new RuntimeException("NETWORK.getRecentPhotos() fake Exception!"));
        return flickrAPI.getRecentPhotos();
    }

    //    @RxLogObservable
    public Flowable<RecentPhotosResponse> getRecentFlowPhotos() {
//        if (true) return Observable.error(new RuntimeException("NETWORK.getRecentPhotos() fake Exception!"));
        return flickrAPI.getRecentFlowPhotos();
    }

    //    @RxLogObservable
    public Maybe<RecentPhotosResponse> getMaybePhotos() {
//        if (true) return Observable.error(new RuntimeException("NETWORK.getRecentPhotos() fake Exception!"));
        return flickrAPI.getRecentMaybePhotos();
    }

    private interface IFlickrAPI {
        @GET("?method=flickr.photos.getRecent&format=json&nojsoncallback=1&extras=url_n&api_key=" + BuildConfig.FLICKR_API_KEY)
        Observable<RecentPhotosResponse> getRecentPhotos();

        @GET("?method=flickr.photos.getRecent&format=json&nojsoncallback=1&extras=url_n&api_key=" + BuildConfig.FLICKR_API_KEY)
        Flowable<RecentPhotosResponse> getRecentFlowPhotos();

        @GET("?method=flickr.photos.getRecent&format=json&nojsoncallback=1&extras=url_n&api_key=" + BuildConfig.FLICKR_API_KEY)
        Maybe<RecentPhotosResponse> getRecentMaybePhotos();
    }
}
