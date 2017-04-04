package com.app.tomeetme.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.app.tomeetme.R;
import com.app.tomeetme.helper.geolocation.Geolocation;
import com.app.tomeetme.helper.geolocation.GeolocationListener;
import com.app.tomeetme.helper.log.LogManager;
import com.app.tomeetme.helper.util.FontTypefaceUtils;
import com.app.tomeetme.helper.util.LocationUtility;
import com.app.tomeetme.model.Business;
import com.app.tomeetme.model.BusinessAddress;
import com.app.tomeetme.model.BusinessReview;
import com.app.tomeetme.model.User;
import com.app.tomeetme.rest.ResponseObjectCallBack;
import com.app.tomeetme.rest.task.business.BusinessReviewTask;
import com.app.tomeetme.view.adapter.AddressAdapter;
import com.app.tomeetme.view.adapter.ReviewAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Ambruster on 29/06/2016.
 */

public class BusinessAddressFragment extends BaseFragment implements View.OnClickListener,
        ResponseObjectCallBack,GeolocationListener {

    private AddressAdapter adapter;
    private ArrayList<BusinessAddress> businessAddress;
    public static final String EXTRA_OBJCT_BUSINESS = "BUSINESS";
    public static final String EXTRA_OBJCT_USER = "USER";
    public Business business;
    public User user;
    private RecyclerView recycler;
    private ProgressBar progressbar;
    private RelativeLayout ryt_empty, ryt_error;
    private Button error_retry;
    private View parent_view;
    private MaterialDialog progress;
    private static final long TIMER_DELAY = 60000l;
    private Geolocation mGeolocation = null;
    private Location mLocation = null;
    private Handler mTimerHandler;
    private Runnable mTimerRunnable;

    public static BusinessAddressFragment newInstance(Business business, User user) {
        BusinessAddressFragment fragment = new BusinessAddressFragment();
        Bundle args = new Bundle();
        Gson gSon = new Gson();
        args.putString(EXTRA_OBJCT_BUSINESS, gSon.toJson(business));
        args.putString(EXTRA_OBJCT_USER, gSon.toJson(user));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        setupTimer();
    }

    private void startTimer() {
        mTimerHandler.postDelayed(mTimerRunnable, 0);
    }

    private void setupTimer() {
        mTimerHandler = new Handler();
        mTimerRunnable = new Runnable() {
            @Override
            public void run() {
                initGeolocation();
                mTimerHandler.postDelayed(this, TIMER_DELAY);
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        startTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    private void initGeolocation() {
        mGeolocation = null;
        mGeolocation = new Geolocation((LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE), this);
    }

    private void stopTimer() {
        mTimerHandler.removeCallbacks(mTimerRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
        if (mGeolocation != null) mGeolocation.stop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parent_view = inflater.inflate(R.layout.activity_business_address, container, false);

        getActivity().invalidateOptionsMenu();
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            if (bundle.containsKey(EXTRA_OBJCT_BUSINESS)) {
                Gson gSon = new Gson();
                business = gSon.fromJson(bundle.getString(EXTRA_OBJCT_BUSINESS), new TypeToken<Business>() {
                }.getType());
            }

            if (bundle.containsKey(EXTRA_OBJCT_USER)) {
                Gson gSon = new Gson();
                user = gSon.fromJson(bundle.getString(EXTRA_OBJCT_USER), new TypeToken<User>() {
                }.getType());
            }
        }

        CheckGPS();
        Load();
        return parent_view;
    }

    private void CheckGPS() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        final AlertDialog alert = builder.create();
        alert.show();
    }

    protected void Load() {

        getActivity().invalidateOptionsMenu();
        progressbar = (ProgressBar) parent_view.findViewById(R.id.progressbar);
        ryt_empty = (RelativeLayout) parent_view.findViewById(R.id.layout_empty);
        ryt_error = (RelativeLayout) parent_view.findViewById(R.id.layout_error);
        error_retry = (Button) parent_view.findViewById(R.id.action_error_retry);
        error_retry.setOnClickListener(this);

        recycler = (RecyclerView) parent_view.findViewById(R.id.recyclerView);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        FloatingActionButton myFab = (FloatingActionButton) parent_view.findViewById(R.id.fab);
        myFab.setOnClickListener(v -> AddAddress());

        businessAddress = new ArrayList<>();
        adapter = new AddressAdapter(getActivity(), recycler, (position, v) -> {

        });
        recycler.setAdapter(adapter);
        Execute();
    }

    private void AddAddress() {

    }

    protected void Execute() {
        ryt_error.setVisibility(View.GONE);
        progressbar.setVisibility(View.GONE);
        try {

            runTaskCallback(() -> {

                businessAddress.addAll(business.getBusinessAddress());
                calculatePoiDistances();
                sortPoiByDistance();
                adapter.AddItems(businessAddress);
                adapter.setLoaded();

                if (businessAddress.size() > 0) {
                    ryt_empty.setVisibility(View.GONE);
                    recycler.setVisibility(View.VISIBLE);
                } else {
                    recycler.setVisibility(View.GONE);
                    ryt_empty.setVisibility(View.VISIBLE);
                }
            });
        } catch (Exception ex) {
        }
    }

    @Override
    public void onGeolocationRespond(Geolocation geolocation, final Location location) {
        runTaskCallback(() -> {
            mLocation = location;
            calculatePoiDistances();
            sortPoiByDistance();
            if (adapter != null && mLocation != null && businessAddress != null && businessAddress.size() > 0)
                adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onGeolocationFail(Geolocation geolocation) {
    }

    private void calculatePoiDistances() {

        if (mLocation != null && businessAddress != null && businessAddress.size() > 0) {
            for (int i = 0; i < businessAddress.size(); i++) {
                BusinessAddress business = businessAddress.get(i);
                LatLng myLocation = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                LatLng poiLocation = new LatLng(Double.parseDouble(business.getLatitude()), Double.parseDouble(business.getLongitude()));
                int distance = LocationUtility.getDistance(myLocation, poiLocation);
                LogManager.getInstance().info("Distance", " " + distance);
                business.setDistance(distance);
            }
        }
    }

    private void sortPoiByDistance() {
        if (mLocation != null && businessAddress != null && businessAddress.size() > 0) {
            Collections.sort(businessAddress);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.action_retry) {
            Execute();
        } else if (v.getId() == R.id.action_error_retry) {
            Execute();
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponseObjectCallBack(Object object) {
        HideProgress();
        ShowMessage("BusinessReview created");
    }

    @Override
    public void onError(String message, Integer code) {
        HideProgress();
        LogManager.getInstance().info("Error", message + " : " + code);
        ShowMessage(message);
    }

    private void ShowMessage(String message) {
        Snackbar snack = Snackbar.make(parent_view, message, Snackbar.LENGTH_LONG).setActionTextColor(Color.YELLOW);
        View view = snack.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snack.show();
    }

    private void ShowProgress(String content) {
        if (progress == null) {
            progress = new MaterialDialog.Builder(getActivity())
                    .content(content)
                    .cancelable(false)
                    .progress(true, 0)
                    .progressIndeterminateStyle(false)
                    .show();
        }
    }

    private void HideProgress() {
        if (progress != null) {
            if (progress.isShowing()) {
                progress.dismiss();
            }
        }
    }
}