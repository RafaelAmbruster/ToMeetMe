package com.app.tomeetme.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.app.tomeetme.R;
import com.app.tomeetme.helper.geolocation.Geolocation;
import com.app.tomeetme.helper.geolocation.GeolocationListener;
import com.app.tomeetme.helper.log.LogManager;
import com.app.tomeetme.helper.util.BitmapTransform;
import com.app.tomeetme.helper.util.CustomDateFormat;
import com.app.tomeetme.helper.util.DataFormatter;
import com.app.tomeetme.helper.util.FontTypefaceUtils;
import com.app.tomeetme.helper.util.LocationUtility;
import com.app.tomeetme.model.Business;
import com.app.tomeetme.model.User;
import com.app.tomeetme.view.widget.TextViewExpandableAnimation;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;


public class BusinessDetailFragment extends BaseFragment implements GeolocationListener, DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener{

    private static final long TIMER_DELAY = 60000l;
    public static final String EXTRA_OBJCT_BUSINESS = "BUSINESS";
    public static final String EXTRA_OBJCT_USER = "USER";
    private Geolocation mGeolocation = null;
    private Handler mTimerHandler;
    private Runnable mTimerRunnable;
    private Business business;
    private User user;
    private View parent_view;


    public static BusinessDetailFragment newInstance(Business business, User user) {
        BusinessDetailFragment fragment = new BusinessDetailFragment();
        Bundle args = new Bundle();
        Gson gSon = new Gson();
        args.putString(EXTRA_OBJCT_BUSINESS, gSon.toJson(business));
        args.putString(EXTRA_OBJCT_USER, gSon.toJson(user));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        getActivity().invalidateOptionsMenu();
        startTimer();
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parent_view = inflater.inflate(R.layout.activity_business_information, container, false);
        setHasOptionsMenu(true);
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

        renderView();
        return parent_view;
    }

    private void renderView() {
        renderViewInfo();
        renderViewDescription();
    }

    private void renderViewInfo() {

        TextView emailTextView = (TextView) parent_view.findViewById(R.id.fragment_poi_detail_info_email);
        emailTextView.setTypeface(FontTypefaceUtils.getRobotoCondensedLight(getActivity()));
        TextView linkTextView = (TextView) parent_view.findViewById(R.id.fragment_poi_detail_info_link);
        linkTextView.setTypeface(FontTypefaceUtils.getRobotoCondensedLight(getActivity()));
        TextView phoneTextView = (TextView) parent_view.findViewById(R.id.fragment_poi_detail_info_phone);
        phoneTextView.setTypeface(FontTypefaceUtils.getRobotoCondensedLight(getActivity()));
        Button btn_schedule = (Button) parent_view.findViewById(R.id.btn_schedule);

        if (business.getEmailAddress() != null && !business.getEmailAddress().trim().equals("")) {
            emailTextView.setText(business.getEmailAddress());
            emailTextView.setVisibility(View.VISIBLE);
        } else {
            emailTextView.setVisibility(View.GONE);
        }

        if (business.getWebSiteUrl() != null && !business.getWebSiteUrl().trim().equals("")) {
            linkTextView.setText(business.getWebSiteUrl());
            linkTextView.setVisibility(View.VISIBLE);
            linkTextView.setOnClickListener(v -> startWebActivity(business.getWebSiteUrl()));
        } else {
            linkTextView.setVisibility(View.GONE);
        }

        if (business.getPhoneNumber() != null && !business.getPhoneNumber().trim().equals("")) {
            phoneTextView.setText(business.getPhoneNumber());
            phoneTextView.setVisibility(View.VISIBLE);
            phoneTextView.setOnClickListener(v -> startCallActivity(business.getPhoneNumber()));
        } else {
            phoneTextView.setVisibility(View.GONE);
        }

        btn_schedule.setOnClickListener(v -> showSchedule());
    }

    public void showSchedule() {

        new MaterialDialog.Builder(getActivity())
                .title(R.string.business_schedule)
                .customView(R.layout.dialog_business_schedule, true)
                .titleColorRes(R.color.colorAccent)
                .titleGravity(GravityEnum.CENTER)
                .contentColorRes(android.R.color.white)
                .btnSelector(R.drawable.md_btn_selector_custom_accent, DialogAction.POSITIVE)
                .positiveColor(Color.WHITE)
                .theme(Theme.LIGHT)
                .autoDismiss(true)
                .positiveText(R.string.ok)
                .show();

    }

    private void renderViewDescription() {
        TextViewExpandableAnimation descriptionTextView = (TextViewExpandableAnimation) parent_view.findViewById(R.id.fragment_poi_detail_description_text);
        descriptionTextView.getTextView().setTypeface(FontTypefaceUtils.getRobotoCondensedLight(getActivity()));
        descriptionTextView.resetState(true);
        if (business.getDescription() != null && !business.getDescription().trim().equals("")) {
            descriptionTextView.setText(business.getDescription());
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupTimer();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
        if (mGeolocation != null) mGeolocation.stop();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onGeolocationRespond(Geolocation geolocation, final Location location) {
        runTaskCallback(() -> {
            if (business != null)
                renderViewInfo();
        });
    }

    @Override
    public void onGeolocationFail(Geolocation geolocation) {
    }

    private void setupTimer() {
        mTimerHandler = new Handler();
        mTimerRunnable = new Runnable() {
            @Override
            public void run() {
                mGeolocation = null;
                mGeolocation = new Geolocation((LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE), BusinessDetailFragment.this);

                mTimerHandler.postDelayed(this, TIMER_DELAY);
            }
        };
    }

    private void startTimer() {
        mTimerHandler.postDelayed(mTimerRunnable, 0);
    }

    private void stopTimer() {
        mTimerHandler.removeCallbacks(mTimerRunnable);
    }

    private void startWebActivity(String url) {
        try {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
        }
    }

    private void startCallActivity(String phoneNumber) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("tel:");
            builder.append(phoneNumber);

            Intent intent = new Intent(android.content.Intent.ACTION_DIAL, Uri.parse(builder.toString()));
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
    }
}
