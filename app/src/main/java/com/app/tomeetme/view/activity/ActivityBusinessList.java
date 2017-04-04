package com.app.tomeetme.view.activity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.tomeetme.R;
import com.app.tomeetme.helper.geolocation.Geolocation;
import com.app.tomeetme.helper.geolocation.GeolocationListener;
import com.app.tomeetme.helper.log.LogManager;
import com.app.tomeetme.helper.util.LocationUtility;
import com.app.tomeetme.helper.util.Tools;
import com.app.tomeetme.model.Business;
import com.app.tomeetme.model.BusinessAddress;
import com.app.tomeetme.model.BusinessPictures;
import com.app.tomeetme.model.User;
import com.app.tomeetme.model.BusinessFilter;
import com.app.tomeetme.rest.ApiInterface;
import com.app.tomeetme.rest.ResponseLoadCallBack;
import com.app.tomeetme.rest.ResponseObjectCallBack;
import com.app.tomeetme.rest.ServiceGenerator;
import com.app.tomeetme.rest.task.business.BusinessTask;
import com.app.tomeetme.view.adapter.BusinessAdapter;
import com.app.tomeetme.view.search.SearchBox;
import com.app.tomeetme.view.search.SearchResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ActivityBusinessList extends AbstractActivity implements ResponseObjectCallBack,
        ResponseLoadCallBack,
        SwipeRefreshLayout.OnRefreshListener,
        GeolocationListener, View.OnClickListener {

    public static final String EXTRA_OBJCT_USER = "User";
    private static final long TIMER_DELAY = 60000l;
    private RecyclerView recyclerView;
    private BusinessAdapter adapter;
    private User us;
    public static ArrayList<Business> businesses;
    public static ArrayList<Business> searchcars;
    private RelativeLayout ryt_empty, ryt_error, ryt_connection, ryt_progressbar;
    private SwipeRefreshLayout refresh;
    private static View view;
    private Geolocation mGeolocation = null;
    private Location mLocation = null;
    private Handler mTimerHandler;
    private Runnable mTimerRunnable;
    private Button action_retry, error_retry;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private SearchBox sbSearch;
    private String previous_term = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        initActionbar();

        setupTimer();
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            if (bundle.containsKey(EXTRA_OBJCT_USER)) {
                Gson gSon = new Gson();
                us = gSon.fromJson(bundle.getString(EXTRA_OBJCT_USER), new TypeToken<User>() {
                }.getType());
            }
        }

        Load();
        Tools.systemBarLolipop(this);
    }

    public void initActionbar() {

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        TextView tittle = (TextView) findViewById(R.id.tvSearchToolBar_title);
        tittle.setText("CARS FOR RENT");
        toolbar.setBackgroundColor(getResources().getColor(R.color.toolbar_bg));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    protected void Load() {
        ryt_progressbar = (RelativeLayout) findViewById(R.id.layout_progress);
        ryt_empty = (RelativeLayout) findViewById(R.id.layout_empty);
        ryt_error = (RelativeLayout) findViewById(R.id.layout_error);
        ryt_connection = (RelativeLayout) findViewById(R.id.layout_connection);

        error_retry = (Button) findViewById(R.id.action_error_retry);
        error_retry.setOnClickListener(this);

        action_retry = (Button) findViewById(R.id.action_retry);
        action_retry.setOnClickListener(this);

        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refresh.setOnRefreshListener(this);
        refresh.setColorSchemeColors(Color.parseColor("#ffc107"));

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(ActivityBusinessList.this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        SetupAdapter();

        //Run(false);


    }



    private void calculatePoiDistances() {
        /*if (mLocation != null && businesses != null && businesses.size() > 0) {
            for (int i = 0; i < businesses.size(); i++) {
                Business business = businesses.get(i);
                LatLng myLocation = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                LatLng poiLocation = new LatLng(Double.parseDouble(business.getLatitude()), Double.parseDouble(business.getLongitude()));
                int distance = LocationUtility.getDistance(myLocation, poiLocation);
                business.setDistance(distance);
            }
        }*/
    }

    private void sortPoiByDistance() {
        /*if (mLocation != null && businesses != null && businesses.size() > 0) {
            Collections.sort(businesses);
        }*/
    }

    private void SetupAdapter() {
        businesses = new ArrayList<>();
        adapter = new BusinessAdapter(ActivityBusinessList.this, recyclerView, new BusinessAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                int pos = adapter.getPosition(position);
                Business business = businesses.get(pos);
                view = v;
                ryt_progressbar.setVisibility(View.VISIBLE);
                //OpenDetail(business);
                OpenMockDetail(business);
            }
        }, false);
        recyclerView.setAdapter(adapter);
    }

    private void OpenDetail(Business business) {
        new BusinessTask((ResponseObjectCallBack) this).CallService(2, business.getId(), null, null);
    }

    private void OpenMockDetail(Business business) {
        ryt_progressbar.setVisibility(View.GONE);
        startBusinessDetail(business);
    }

    protected void Run(boolean process) {
        if (Tools.isOnline(ActivityBusinessList.this, true, false)) {
            ryt_connection.setVisibility(View.GONE);
            Execute(process);
        } else {
            ryt_connection.setVisibility(View.VISIBLE);
        }
    }

    protected void Execute(Boolean manual) {

        if (!manual)
            ryt_progressbar.setVisibility(View.VISIBLE);

        BusinessFilter filter = new BusinessFilter();
        new BusinessTask((ResponseLoadCallBack) this).CallService(1, "", filter, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        startTimer();
    }

    private void startTimer() {
        mTimerHandler.postDelayed(mTimerRunnable, 0);
    }

    private void setupTimer() {
        mTimerHandler = new Handler();
        mTimerRunnable = new Runnable() {
            @Override
            public void run() {

                mGeolocation = null;
                mGeolocation = new Geolocation((LocationManager) getSystemService(Context.LOCATION_SERVICE), ActivityBusinessList.this);

                mTimerHandler.postDelayed(this, TIMER_DELAY);
            }
        };
    }

    private void stopTimer() {
        mTimerHandler.removeCallbacks(mTimerRunnable);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_cars_offline, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:
                openSearch();
                sbSearch.revealFromMenuItem(item.getItemId(), this);
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponseLoadCallBack(ArrayList list) {

        ryt_error.setVisibility(View.GONE);
        ryt_empty.setVisibility(View.GONE);
        ryt_connection.setVisibility(View.GONE);
        refresh.setRefreshing(false);
        ryt_progressbar.setVisibility(View.GONE);

        try {
            businesses.addAll(list);
            calculatePoiDistances();
            sortPoiByDistance();
            adapter.AddItems(businesses);
            adapter.setLoaded();

            if (businesses.size() > 0) {

                ryt_empty.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            } else {

                recyclerView.setVisibility(View.GONE);
                ryt_empty.setVisibility(View.VISIBLE);
            }

        } catch (Exception ex) {
        }
    }

    @Override
    public void onGeolocationRespond(Geolocation geolocation, final Location location) {
        runTaskCallback(() -> {
            mLocation = location;
            calculatePoiDistances();
            sortPoiByDistance();
            if (adapter != null && mLocation != null && businesses != null && businesses.size() > 0)
                adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onGeolocationFail(Geolocation geolocation) {
    }

    @Override
    public void onError(String message, Integer code) {
        refresh.setRefreshing(false);
        ryt_progressbar.setVisibility(View.GONE);
        ryt_error.setVisibility(View.VISIBLE);
        businesses = new ArrayList<>();
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onRefresh() {
        if (Tools.isOnline(ActivityBusinessList.this, true, false)) {
            businesses.clear();
            adapter.clearList();
            Run(true);
        } else {
            refresh.setRefreshing(false);
        }
    }

    @Override
    public void onResponseObjectCallBack(Object object) {
        ryt_progressbar.setVisibility(View.GONE);
        startBusinessDetail((Business) object);
    }

    private void startBusinessDetail(Business business) {
        Intent intent = new Intent(ActivityBusinessList.this, ActivityBusinessDetails.class);
        Bundle b = new Bundle();
        Gson gSon = new Gson();
        b.putString(ActivityBusinessDetails.EXTRA_OBJCT_BUSINESS, gSon.toJson(business));
        b.putString(ActivityBusinessDetails.EXTRA_OBJCT_USER, gSon.toJson(us));
        intent.putExtras(b);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityOptions options = ActivityOptions.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight());
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
        if (mGeolocation != null) mGeolocation.stop();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.action_retry) {
            Run(false);
        } else if (view.getId() == R.id.action_error_retry) {
            Run(false);
        }
    }

    public void openSearch() {

        toolbar.setTitle("");
        sbSearch.setHint("search business..");
        sbSearch.revealFromMenuItem(R.id.fab, this);
        sbSearch.setMenuListener(new SearchBox.MenuListener() {
            @Override
            public void onMenuClick() {
            }
        });

        sbSearch.setSearchListener(new SearchBox.SearchListener() {

            @Override
            public void onSearchOpened() {
            }

            @Override
            public void onSearchClosed() {
                ryt_progressbar.setVisibility(ProgressBar.GONE);
                closeSearch();
            }

            @Override
            public void onSearchTermChanged(String term) {
                if (term.isEmpty()) {
                    ryt_progressbar.setVisibility(ProgressBar.GONE);
                }
                ryt_progressbar.setVisibility(ProgressBar.VISIBLE);
                sbSearch.showLoading(true);
                searchBusiness(term);
                LogManager.getInstance().info("Term", term);
            }

            @Override
            public void onSearch(String searchTerm) {
                toolbar.setTitle(searchTerm);
            }

            @Override
            public void onResultClick(final SearchResult result) {
                //Intent intent = new Intent(ActivityBusinessList.this, SearchActivity.class);
                //intent.putExtra("searchTerm", result.title);
                //startActivity(intent);
            }

            @Override
            public void onSearchCleared() {

            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1234 && resultCode == RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            sbSearch.populateEditText(matches.get(0));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void closeSearch() {
        sbSearch.hideCircularly(this);
        if (sbSearch.getSearchText().isEmpty()) toolbar.setTitle("");

    }

    private void searchBusiness(final String searchTerm) {

        ApiInterface apiService =
                ServiceGenerator.createService(ApiInterface.class);

        Observable<List<Business>> businessObservable = apiService.autoSuggestBusiness(searchTerm);

        businessObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<List<Business>>() {
                    @Override
                    public void onCompleted() {
                        LogManager.getInstance().info("onCompleted", "True");
                        ryt_progressbar.setVisibility(ProgressBar.GONE);
                        sbSearch.showLoading(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogManager.getInstance().info("onError", e.getMessage());
                        ryt_progressbar.setVisibility(ProgressBar.GONE);
                        sbSearch.showLoading(false);
                    }

                    @Override
                    public void onNext(List<Business> result) {
                        searchcars = (ArrayList<Business>) result;
                        LogManager.getInstance().info("searchbusinesses", searchcars.size() + "");
                        if (!previous_term.equals(searchTerm)) {
                            for (Business item : searchcars) {
                                SearchResult option = new SearchResult(item.getTitle(), getResources().getDrawable(
                                        R.drawable.ic_history));
                                sbSearch.addSearchable(option);
                            }
                        }
                        previous_term = searchTerm;
                    }
                });
    }

}
