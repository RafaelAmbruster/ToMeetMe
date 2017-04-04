package com.app.tomeetme.view.activity.createBusiness;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.app.tomeetme.R;
import com.app.tomeetme.helper.geolocation.Geolocation;
import com.app.tomeetme.helper.geolocation.GeolocationListener;
import com.app.tomeetme.helper.util.BitmapTransform;
import com.app.tomeetme.helper.util.FontTypefaceUtils;
import com.app.tomeetme.view.fragment.BaseFragment;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;


public class CreateBusinessFirstStepFragment extends BaseFragment implements Step, GeolocationListener {

    private static final String CLICKS_KEY = "clicks";
    private static final int TAP_THRESHOLD = 2;
    private static final String LAYOUT_RESOURCE_ID_ARG_KEY = "messageResourceId";
    private int i = 0;
    private Place place;
    private static final int PLACE_PICKER_REQUEST = 1;
    private Location mLocation = null;
    private int size = (int) Math.ceil(Math.sqrt(BitmapTransform.MAX_WIDTH * BitmapTransform.MAX_HEIGHT));
    private static final int MAP_ZOOM = 14;
    private View parent_view;
    private Geolocation mGeolocation = null;
    private Handler mTimerHandler;
    private Runnable mTimerRunnable;
    private static final long TIMER_DELAY = 60000l;
    private EditText tittle, description, direction;
    private CharSequence name, address;

    @Nullable
    private OnNavigationBarListener onNavigationBarListener;

    public static CreateBusinessFirstStepFragment newInstance(@LayoutRes int layoutResId) {
        Bundle args = new Bundle();
        args.putInt(LAYOUT_RESOURCE_ID_ARG_KEY, layoutResId);
        CreateBusinessFirstStepFragment fragment = new CreateBusinessFirstStepFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNavigationBarListener) {
            onNavigationBarListener = (OnNavigationBarListener) context;
        }
    }

    @Override
    public void onGeolocationRespond(Geolocation geolocation, final Location location) {
        runTaskCallback(() -> mLocation = location);
    }

    @Override
    public void onGeolocationFail(Geolocation geolocation) {
    }

    private void renderViewMap(LatLng latLng) {
        final ImageView imageView = (ImageView) parent_view.findViewById(R.id.fragment_poi_detail_map_image);

        String key = getString(R.string.google_maps_key);
        String url = getStaticMapUrl(key, latLng.latitude, latLng.longitude, MAP_ZOOM);
        Picasso.with(getActivity())
                .load(url).resize(size, size)
                .placeholder(R.drawable.placeholder_map)
                .error(R.drawable.placeholder_map)
                .transform(new BitmapTransform(BitmapTransform.MAX_WIDTH, BitmapTransform.MAX_HEIGHT)).
                centerInside().into(imageView);
    }

    private String getStaticMapUrl(String key, double lat, double lon, int zoom) {
        TypedValue typedValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        int markerColor = typedValue.data;
        String markerColorHex = String.format("0x%06x", (0xffffff & markerColor));

        StringBuilder builder = new StringBuilder();
        builder.append("https://maps.googleapis.com/maps/api/staticmap");
        builder.append("?key=");
        builder.append(key);
        builder.append("&size=320x320");
        builder.append("&scale=2");
        builder.append("&maptype=roadmap");
        builder.append("&zoom=");
        builder.append(zoom);
        builder.append("&center=");
        builder.append(lat);
        builder.append(",");
        builder.append(lon);
        builder.append("&markers=color:");
        builder.append(markerColorHex);
        builder.append("%7C");
        builder.append(lat);
        builder.append(",");
        builder.append(lon);
        return builder.toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parent_view = inflater.inflate(getArguments().getInt(LAYOUT_RESOURCE_ID_ARG_KEY), container, false);
        setupTimer();
        if (savedInstanceState != null) {
            i = savedInstanceState.getInt(CLICKS_KEY);
        }

        updateNavigationBar();

        direction = (EditText) parent_view.findViewById(R.id.business_direction);
        direction.setTypeface(FontTypefaceUtils.getRobotoCondensedLight(getActivity()));

        tittle = (EditText) parent_view.findViewById(R.id.tittle);
        tittle.setTypeface(FontTypefaceUtils.getRobotoCondensedLight(getActivity()));

        description = (EditText) parent_view.findViewById(R.id.description);
        description.setTypeface(FontTypefaceUtils.getRobotoCondensedLight(getActivity()));

        Button pickerButton = (Button) parent_view.findViewById(R.id.add_address);
        pickerButton.setOnClickListener(v1 -> {
            try {
                PlacePicker.IntentBuilder intentBuilder =
                        new PlacePicker.IntentBuilder();
                if (mLocation != null)
                    intentBuilder.setLatLngBounds(toBounds(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 10));

                Intent intent = intentBuilder.build(getActivity());
                startActivityForResult(intent, PLACE_PICKER_REQUEST);

            } catch (GooglePlayServicesRepairableException
                    | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        });


        /*button = (Button) v.findViewById(R.id.button);
        button.setText(Html.fromHtml("Taps: <b>" + i + "</b>"));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setText(Html.fromHtml("Taps: <b>" + (++i) + "</b>"));
                updateNavigationBar();
            }
        });*/

        return parent_view;
    }

    private void startTimer() {
        mTimerHandler.postDelayed(mTimerRunnable, 0);
    }

    private void stopTimer() {
        mTimerHandler.removeCallbacks(mTimerRunnable);
    }

    private void setupTimer() {
        mTimerHandler = new Handler();
        mTimerRunnable = new Runnable() {
            @Override
            public void run() {

                mGeolocation = null;
                mGeolocation = new Geolocation((LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE), CreateBusinessFirstStepFragment.this);
                mTimerHandler.postDelayed(this, TIMER_DELAY);
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
        if (mGeolocation != null) mGeolocation.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        startTimer();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == Activity.RESULT_OK) {

            place = PlacePicker.getPlace(getActivity(), data);
            name = place.getName();
            address = place.getAddress();
            direction.setText(name + " - " + address);
            renderViewMap(place.getLatLng());
        }
    }

    public LatLngBounds toBounds(LatLng center, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }

    @Override
    public VerificationError verifyStep() {
        //return isAboveThreshold() ? null : new VerificationError("Click " + (TAP_THRESHOLD - i) + " more times!");
        return null;
    }

    private boolean isAboveThreshold() {
        return i >= TAP_THRESHOLD;
    }

    @Override
    public void onSelected() {
        updateNavigationBar();
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    private void updateNavigationBar() {
        if (onNavigationBarListener != null) {
            onNavigationBarListener.onChangeEndButtonsEnabled(isAboveThreshold());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(CLICKS_KEY, i);
        super.onSaveInstanceState(outState);
    }

}
