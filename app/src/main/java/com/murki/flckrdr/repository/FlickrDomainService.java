package com.murki.flckrdr.repository;

import android.content.Context;

import com.murki.flckrdr.model.FlickrPhoto;
import com.murki.flckrdr.model.RecentPhotosResponse;

import org.reactivestreams.Publisher;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.MaybeSource;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class FlickrDomainService {

    private static final String CLASSNAME = FlickrDomainService.class.getCanonicalName();

    private FlickrNetworkRepository flickrNetworkRepository = null;
    private final MyDatabase myDatabase;
    private final FlickrRoomRepo flickrRoomRepo;

    public FlickrDomainService(Context context) {
        flickrNetworkRepository = new FlickrNetworkRepository(); // TODO: Inject Singleton
        myDatabase = MyDatabase.instance(context);
        flickrRoomRepo = myDatabase.flickrDao();
    }


    public Flowable<List<FlickrPhoto>> getFlickerPhotos() {
        return getMergedFloFlickrPhotos();
    }

    public Maybe<List<FlickrPhoto>> getAll() {
        return getMergedMayBePhotos();
    }

    public Flowable<List<FlickrPhoto>> getFlickFromNetwork() {
        return flickrNetworkRepository.getRecentFlowPhotos().concatMap(new Function<RecentPhotosResponse, Publisher<? extends List<FlickrPhoto>>>() {
            @Override
            public Publisher<? extends List<FlickrPhoto>> apply(RecentPhotosResponse recentPhotosResponse) throws Exception {
                List<FlickrPhoto> flickrPhotos = recentPhotosResponse.photos.photo;
                Flowable<List<FlickrPhoto>> asdsd = io.reactivex.Observable.fromIterable(flickrPhotos).toList().toObservable().toFlowable(BackpressureStrategy.DROP);
//                return Flowable.fromIterable(flickrPhotos).toList().toFlowable();
//                Flowable<List<FlickrPhoto>> asd = Flowable.fromIterable(flickrPhotos).toList().toFlowable().cacheWithInitialCapacity(120);
//                return Flowable.fromIterable(flickrPhotos).toList().toFlowable();
                return asdsd;
            }
        });
    }

    public List<FlickrPhoto> getFlickFromDb() {
        return flickrRoomRepo.getAllFlickerPhotosList();
    }


    private Flowable<List<FlickrPhoto>> getMergedFloFlickrPhotos() {
        Flowable<List<FlickrPhoto>> networkListObservable = flickrNetworkRepository.getRecentFlowPhotos()
                .concatMap(new Function<RecentPhotosResponse, Publisher<? extends List<FlickrPhoto>>>() {
                    @Override
                    public Publisher<? extends List<FlickrPhoto>> apply(RecentPhotosResponse recentPhotosResponse) throws Exception {
                        List<FlickrPhoto> flickrPhotos = recentPhotosResponse.photos.photo;
                        return Flowable.fromIterable(flickrPhotos).toList().toFlowable();
                    }
                });

        return Flowable.mergeDelayError(
                flickrRoomRepo.getAllFlickerPhotos(),
                networkListObservable.doOnNext(new Consumer<List<FlickrPhoto>>() {
                    @Override
                    public void accept(List<FlickrPhoto> flickrPhotos) throws Exception {
                        flickrRoomRepo.insert(flickrPhotos);
                    }
                }).subscribeOn(Schedulers.io())
        );
    }


    Maybe<List<FlickrPhoto>> getMergedMayBePhotos() {
        Maybe<List<FlickrPhoto>> networkListObservable = flickrNetworkRepository.getMaybePhotos()
                .observeOn(Schedulers.io())
                .concatMap(new Function<RecentPhotosResponse, MaybeSource<? extends List<FlickrPhoto>>>() {
                    @Override
                    public MaybeSource<? extends List<FlickrPhoto>> apply(RecentPhotosResponse recentPhotosResponse) throws Exception {
                        final List<FlickrPhoto> flickrPhotos = recentPhotosResponse.photos.photo;
                        return Maybe.create(new MaybeOnSubscribe<List<FlickrPhoto>>() {
                            @Override
                            public void subscribe(MaybeEmitter<List<FlickrPhoto>> e) throws Exception {
                                if (flickrPhotos == null) {

                                } else {
                                    if (flickrPhotos.size() > 0)
                                        e.onSuccess(flickrPhotos);
                                }
                            }
                        });
                    }
                });

        final Flowable<List<FlickrPhoto>> listFlowable = Maybe.mergeDelayError(flickrRoomRepo.getAll(), networkListObservable);
        return listFlowable.singleElement();
    }


    public Observable<List<FlickrPhoto>> getMergedObsPhotos() {

        Observable<List<FlickrPhoto>> network = flickrNetworkRepository.getRecentPhotos()
                .concatMap(new Function<RecentPhotosResponse, ObservableSource<? extends List<FlickrPhoto>>>() {
                    @Override
                    public ObservableSource<? extends List<FlickrPhoto>> apply(RecentPhotosResponse recentPhotosResponse) throws Exception {
                        final List<FlickrPhoto> flickrPhotos = recentPhotosResponse.photos.photo;

                        return Observable.create(new ObservableOnSubscribe<List<FlickrPhoto>>() {
                            @Override
                            public void subscribe(ObservableEmitter<List<FlickrPhoto>> e) throws Exception {
                                if (e == null) {
                                    return;
                                }

                                if (flickrPhotos == null) {
                                    e.onError(new Throwable("No response"));
                                } else {
                                    if (flickrPhotos.size() > 0) {
                                        e.onNext(flickrPhotos);
                                        flickrRoomRepo.partialUpdate(flickrPhotos,myDatabase);
                                    }
                                }

                            }
                        });
                    }
                });

        return Observable.mergeDelayError(network, flickrRoomRepo.getAllFlickerPhotos().toObservable());
    }


}
