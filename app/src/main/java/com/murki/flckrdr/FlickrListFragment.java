package com.murki.flckrdr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.murki.flckrdr.model.FlickrPhoto;
import com.murki.flckrdr.repository.FlickrDomainService;

import org.reactivestreams.Subscription;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FlickrListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String CLASSNAME = FlickrListFragment.class.getCanonicalName();
    private FlickrDomainService flickrDomainService;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FlickrListAdapter flickrListAdapter;
    private CompositeDisposable compositeDisposable;
    private Subscription subscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(CLASSNAME, "onCreate()");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(CLASSNAME, "onCreateView()");

        View view = inflater.inflate(R.layout.fragment_flickr_list, container, false);
        setupView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(CLASSNAME, "onActivityCreated()");
        compositeDisposable = new CompositeDisposable();
        flickrDomainService = new FlickrDomainService(getContext()); // TODO: Inject Singleton
        fetchFlickrItems();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(CLASSNAME, "onDestroy()");

        unsubscribe();
    }

    @Override
    public void onRefresh() {
        Log.d(CLASSNAME, "onRefresh()");
        if (compositeDisposable != null) {

        } else {
            compositeDisposable = new CompositeDisposable();
        }
        fetchFlickrItems();
    }

    private void setupView(View view) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.flickr_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_dark);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        // specify an adapter
        recyclerView.setAdapter(flickrListAdapter = new FlickrListAdapter(Collections.<FlickrPhoto>emptyList()));
    }

    private void fetchFlickrItems() {
        isRefreshing(true);
//        unsubscribe();
//        Observable<List<FlickrPhoto>> recentPhotosObservable = flickrDomainService
//                .getRecentPhotos()
//                .observeOn(AndroidSchedulers.mainThread(), true); // delayError = true
//
//        recentPhotosObservable.subscribe(new Observer<List<FlickrPhoto>>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//                if (compositeDisposable == null) {
//                    compositeDisposable = new CompositeDisposable();
//                }
//                compositeDisposable.add(d);
//            }
//
//            @Override
//            public void onNext(List<FlickrPhoto> flickrCardVMS) {
//                Log.d(CLASSNAME, "flickrRecentPhotosOnNext.call() - Displaying card VMs in Adapter");
////            // refresh the list adapter
//                recyclerView.swapAdapter(flickrListAdapter = new FlickrListAdapter(flickrCardVMS), false);
//            }
//
//            @Override
//            public void onError(Throwable throwable) {
//                Log.e(CLASSNAME, "flickrRecentPhotosOnError.call() - ERROR", throwable);
//                isRefreshing(false);
//                Toast.makeText(getActivity(), "OnError=" + throwable.getMessage(), Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onComplete() {
//                Log.d(CLASSNAME, "flickrRecenPhotosOnComplete.call() - Data completed. Loading done.");
//                isRefreshing(false);
//            }
//        });


//        next(flickrDomainService.getFlickFromDb());
        flickrDomainService.getMergedObsPhotos()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<FlickrPhoto>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(List<FlickrPhoto> flickrPhotos) {
                        next(flickrPhotos);
                    }

                    @Override
                    public void onError(Throwable e) {
                        error(e);
                    }

                    @Override
                    public void onComplete() {
                        complete();
                    }
                });


//        complete();
    }

    private void complete() {
        Log.d(CLASSNAME, "flickrRecenPhotosOnComplete.call() - Data completed. Loading done.");
        isRefreshing(false);
    }

    private void error(Throwable throwable) {
        Log.e(CLASSNAME, "flickrRecentPhotosOnError.call() - ERROR", throwable);
        isRefreshing(false);
        Toast.makeText(getActivity(), "OnError=" + throwable.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void next(List<FlickrPhoto> flickrPhotos) {
        Log.d(CLASSNAME, "flickrRecentPhotosOnNext.call() - Displaying card VMs in Adapter");
//            // refresh the list adapter
        recyclerView.swapAdapter(flickrListAdapter = new FlickrListAdapter(flickrPhotos), false);
        isRefreshing(false);
    }

    private void isRefreshing(final boolean isRefreshing) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(isRefreshing);
            }
        });
    }

    private void unsubscribe() {
        if (compositeDisposable != null) {
            compositeDisposable.clear();
            compositeDisposable = null;
        }

        subscription.cancel();
    }
}
