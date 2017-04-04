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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.tomeetme.R;
import com.app.tomeetme.helper.geolocation.Geolocation;
import com.app.tomeetme.helper.geolocation.GeolocationListener;
import com.app.tomeetme.helper.log.LogManager;
import com.app.tomeetme.helper.util.LocationUtility;
import com.app.tomeetme.helper.util.Tools;
import com.app.tomeetme.model.Business;
import com.app.tomeetme.model.User;
import com.app.tomeetme.model.BusinessFavorites;
import com.app.tomeetme.rest.ApiInterface;
import com.app.tomeetme.rest.ResponseLoadCallBack;
import com.app.tomeetme.rest.ResponseObjectCallBack;
import com.app.tomeetme.rest.ServiceGenerator;
import com.app.tomeetme.rest.task.business.BusinessTask;
import com.app.tomeetme.view.adapter.BusinessAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ActivityBusinessFavorites extends AbstractActivity implements ResponseObjectCallBack,
        ResponseLoadCallBack,
        SwipeRefreshLayout.OnRefreshListener,
        GeolocationListener, View.OnClickListener {

    public static final String EXTRA_OBJCT_USER = "User";
    private static final long TIMER_DELAY = 60000l;
    private View parent_view;
    private RecyclerView recyclerView;
    private BusinessAdapter adapter;
    private User us;
    public static ArrayList<Business> businesses;
    private RelativeLayout ryt_empty, ryt_error, ryt_connection, ryt_progressbar;
    private SearchView searchView;
    private SwipeRefreshLayout refresh;
    private static View view;
    private Geolocation mGeolocation = null;
    private Location mLocation = null;
    private Handler mTimerHandler;
    private Runnable mTimerRunnable;
    private Button action_retry, error_retry;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        parent_view = findViewById(android.R.id.content);

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
        tittle.setText("FAVORITE CARS");
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

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(ActivityBusinessFavorites.this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        SetupAdapter();

        //Run(false);

    }

    private void calculatePoiDistances() {
      /*  if (mLocation != null && businesses != null && businesses.size() > 0) {
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
       /* if (mLocation != null && businesses != null && businesses.size() > 0) {
            Collections.sort(businesses);
        }*/
    }

    private void SetupAdapter() {
        businesses = new ArrayList<>();
        adapter = new BusinessAdapter(ActivityBusinessFavorites.this, recyclerView, new BusinessAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(int position, View v) {
                int pos = adapter.getPosition(position);
                Business business = businesses.get(pos);
                view = v;
                ryt_progressbar.setVisibility(View.VISIBLE);
                OpenDetail(business);
            }
        }, false);
        recyclerView.setAdapter(adapter);
    }

    private void OpenDetail(Business business) {
        new BusinessTask((ResponseObjectCallBack) this).CallService(2, business.getId(), null, null);
    }

    protected void Run(boolean process) {
        if (Tools.isOnline(ActivityBusinessFavorites.this, true, false)) {
            ryt_connection.setVisibility(View.GONE);
            Execute(process);
        } else {
            ryt_connection.setVisibility(View.VISIBLE);
        }
    }

    protected void Execute(Boolean manual) {

        if (!manual)
            ryt_progressbar.setVisibility(View.VISIBLE);

        LoadFavoriteList();
    }

    private void LoadFavoriteList() {
        ApiInterface apiService =
                ServiceGenerator.createService(ApiInterface.class);
        Call<List<BusinessFavorites>> call = apiService.getBusinessFavorites(us.getId());

        call.enqueue(new Callback<List<BusinessFavorites>>() {
            @Override
            public void onResponse(Call<List<BusinessFavorites>> call, Response<List<BusinessFavorites>> response) {
                try {
                    switch (response.code()) {
                        case 200:
                            List<BusinessFavorites> events = response.body();
                            LogManager.getInstance().info("events size", events.size() + "");
                            LoadFavoriteBusiness(events);
                            break;
                        default:
                            refresh.setRefreshing(false);
                            ryt_progressbar.setVisibility(View.GONE);
                            ryt_error.setVisibility(View.VISIBLE);
                            businesses = new ArrayList<>();
                            recyclerView.setVisibility(View.GONE);
                            break;
                    }
                } catch (Exception ex) {
                    refresh.setRefreshing(false);
                    ryt_progressbar.setVisibility(View.GONE);
                    ryt_error.setVisibility(View.VISIBLE);
                    businesses = new ArrayList<>();
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<BusinessFavorites>> call, Throwable t) {
                refresh.setRefreshing(false);
                ryt_progressbar.setVisibility(View.GONE);
                ryt_error.setVisibility(View.VISIBLE);
                businesses = new ArrayList<>();
                recyclerView.setVisibility(View.GONE);
            }
        });

    }

    private void LoadFavoriteBusiness(final List<BusinessFavorites> events) {
        ApiInterface apiService =
                ServiceGenerator.createService(ApiInterface.class);
        count = 1;
        for (BusinessFavorites item : events) {
            apiService.getBusinessFavorite(item.getBusinessId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Business>() {
                        @Override
                        public final void onCompleted() {
                            if (count == events.size()) {
                                ryt_progressbar.setVisibility(View.GONE);
                                refresh.setRefreshing(false);
                            } else
                                count++;
                        }

                        @Override
                        public final void onError(Throwable e) {
                            count++;
                        }

                        @Override
                        public final void onNext(Business response) {
                            businesses.add(response);
                            adapter.addData(response);
                        }
                    });
        }

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
                mGeolocation = new Geolocation((LocationManager) getSystemService(Context.LOCATION_SERVICE), ActivityBusinessFavorites.this);

                mTimerHandler.postDelayed(this, TIMER_DELAY);
            }
        };
    }

    private void stopTimer() {
        mTimerHandler.removeCallbacks(mTimerRunnable);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        if (us != null) {
            getMenuInflater().inflate(R.menu.menu_search, menu);
            final MenuItem searchItem = menu.findItem(R.id.action_search);
            searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            searchView.setIconified(false);
            searchView.setQueryHint("Search...");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    try {
                        adapter.getFilter().filter(s);
                    } catch (Exception e) {
                    }
                    return true;
                }
            });

            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setItemsVisibility(menu, searchItem, false);
                }
            });


            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    setItemsVisibility(menu, searchItem, true);
                    adapter.notifyDataSetChanged();
                    return false;
                }
            });
            searchView.onActionViewCollapsed();

        } else {
            getMenuInflater().inflate(R.menu.menu_search_offline, menu);
            final MenuItem searchItem = menu.findItem(R.id.action_search);
            searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            searchView.setIconified(false);
            searchView.setQueryHint("Search...");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    try {
                        adapter.getFilter().filter(s);
                    } catch (Exception e) {
                    }
                    return true;
                }
            });

            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setItemsVisibility(menu, searchItem, false);
                }
            });

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    setItemsVisibility(menu, searchItem, true);
                    adapter.notifyDataSetChanged();
                    return false;
                }
            });
            searchView.onActionViewCollapsed();
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void setItemsVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception) item.setVisible(visible);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:
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
        runTaskCallback(new Runnable() {
            public void run() {
                mLocation = location;
                calculatePoiDistances();
                sortPoiByDistance();
                if (adapter != null && mLocation != null && businesses != null && businesses.size() > 0)
                    adapter.notifyDataSetChanged();
            }
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
        if (Tools.isOnline(ActivityBusinessFavorites.this, true, false)) {
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
        Intent intent = new Intent(ActivityBusinessFavorites.this, ActivityBusinessDetails.class);
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

}
