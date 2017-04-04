package com.app.tomeetme.view.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.app.tomeetme.R;
import com.app.tomeetme.helper.geolocation.Geolocation;
import com.app.tomeetme.helper.geolocation.GeolocationListener;
import com.app.tomeetme.helper.log.LogManager;
import com.app.tomeetme.helper.util.BitmapTransform;
import com.app.tomeetme.helper.util.FontTypefaceUtils;
import com.app.tomeetme.helper.util.Preferences;
import com.app.tomeetme.helper.util.Tools;
import com.app.tomeetme.model.AbstractMapModel;
import com.app.tomeetme.model.BusinessAddress;
import com.app.tomeetme.model.BusinessPictures;
import com.app.tomeetme.model.User;
import com.app.tomeetme.model.Business;
import com.app.tomeetme.model.BusinessFilter;
import com.app.tomeetme.rest.ResponseLoadCallBack;
import com.app.tomeetme.rest.ResponseObjectCallBack;
import com.app.tomeetme.rest.task.business.BusinessTask;
import com.app.tomeetme.view.activity.ActivityBusinessDetails;
import com.app.tomeetme.view.rangeBar.RangeBar;
import com.app.tomeetme.view.tagview.TagView;
import com.app.tomeetme.view.widget.ViewState;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class MapFragment extends BaseFragment implements ResponseObjectCallBack,
        ResponseLoadCallBack,
        View.OnClickListener,
        GeolocationListener,
        OnMapReadyCallback {

    public static final String EXTRA_OBJCT_USER = "User";
    private static final int MAP_ZOOM = 14;
    private static final long TIMER_DELAY = 60000l;
    private ViewState mViewState = null;
    private View mRootView;
    private List<AbstractMapModel> models = new ArrayList();
    private ClusterManager<AbstractMapModel> ClusterManager;
    private double mPoiLatitude = 0.0;
    private double mPoiLongitude = 0.0;
    private GoogleMap map;
    private FloatingActionButton fab;
    private Button action_retry;
    private Integer range;
    private Handler mTimerHandler;
    private Runnable mTimerRunnable;
    private Location mLocation = null;
    private RangeBar distance;
    private AppCompatCheckBox checkbox;
    private User user;
    private AbstractMapModel clickedClusterItem;
    private int size = (int) Math.ceil(Math.sqrt(BitmapTransform.MAX_WIDTH * BitmapTransform.MAX_HEIGHT));
    ArrayList<Business> business = new ArrayList<>();

    public static MapFragment newInstance(User us) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        Gson gSon = new Gson();
        args.putString(EXTRA_OBJCT_USER, gSon.toJson(us));
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_map, container, false);
        getActivity().invalidateOptionsMenu();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.containsKey(EXTRA_OBJCT_USER)) {
                Gson gSon = new Gson();
                user = gSon.fromJson(bundle.getString(EXTRA_OBJCT_USER), new TypeToken<User>() {
                }.getType());
            }
        }

        initMap();
        iniFilter();
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CheckGPS();
        if (mViewState == null || mViewState == ViewState.OFFLINE) {
            //setupTimer();
            initGeolocation();
        } else if (mViewState == ViewState.CONTENT) {
            if (models != null) {
                renderView();
                showContent();
            }
        } else if (mViewState == ViewState.PROGRESS) {
            showProgress();
        } else if (mViewState == ViewState.EMPTY) {
            showEmpty();
        }
    }

    private void iniFilter() {

        fab = (FloatingActionButton) mRootView.findViewById(R.id.fab);
        fab.setFocusable(false);
        fab.setFocusableInTouchMode(false);

        fab.setOnClickListener(view -> OpenFilter());

    }

    public void OpenFilter() {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.businesses)
                .customView(R.layout.dialog_filter_business, true)
                .cancelable(false)
                .positiveText(R.string.filter)
                .negativeText(R.string.dialog_cancel)
                .titleColorRes(R.color.colorPrimaryDark)
                .titleGravity(GravityEnum.CENTER)
                .contentColorRes(android.R.color.white)
                .btnSelector(R.drawable.md_btn_selector_custom_accent, DialogAction.POSITIVE)
                .positiveColor(Color.WHITE)
                .theme(Theme.LIGHT)
                .onPositive((dialog1, which) -> {
                    BusinessFilter filter = new BusinessFilter();
                    filter.setDistance(range);

                    if (checkbox.isChecked()) {
                        if (mLocation != null) {
                            filter.setLatitude(mLocation.getLatitude());
                            filter.setLongitude(mLocation.getLongitude());
                        } else {
                            Toast.makeText(getActivity(), "Skipping your current location due to an unknown error", Toast.LENGTH_LONG).show();
                        }
                    }

                    //ExecuteBusiness();
                }).build();


        distance = (RangeBar) dialog.getCustomView().findViewById(R.id.distance_filter);
        distance.setRangeBarEnabled(false);
        distance.setTickEnd(50);
        distance.setTickStart(1);
        distance.setTickInterval(1);
        float ini = Float.parseFloat("1");
        float end = 1;
        distance.setRangePinsByValue(ini, end);

        distance.setOnRangeBarChangeListener((rangeBar, leftPinIndex, rightPinIndex, leftPinValue, rightPinValue) -> range = Integer.parseInt(rightPinValue));

        distance.setFormatter(s -> s + " M");

        checkbox = (AppCompatCheckBox) dialog.getCustomView().findViewById(R.id.enabled);
        checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                distance.setVisibility(View.VISIBLE);
            } else
                distance.setVisibility(View.GONE);
        });


        Button openCategories = (Button) dialog.getCustomView().findViewById(R.id.button2);
        openCategories.setOnClickListener(view -> showMultiChoice());
        dialog.show();

    }

    @Override
    public void onResume() {
        startTimer();
        super.onResume();

    }

    @Override
    public void onPause() {
        stopTimer();
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRootView = null;
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
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_map, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_layers_normal:
                setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;

            case R.id.menu_layers_satellite:
                setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;

            case R.id.menu_layers_hybrid:
                setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;

            case R.id.menu_layers_terrain:
                setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showContent() {
        ViewGroup containerContent = (ViewGroup) mRootView.findViewById(R.id.container_content);
        ViewGroup containerProgress = (ViewGroup) mRootView.findViewById(R.id.container_progress);
        ViewGroup containerOffline = (ViewGroup) mRootView.findViewById(R.id.container_offline);
        ViewGroup containerEmpty = (ViewGroup) mRootView.findViewById(R.id.container_empty);
        containerContent.setVisibility(View.VISIBLE);
        containerProgress.setVisibility(View.GONE);
        containerOffline.setVisibility(View.GONE);
        containerEmpty.setVisibility(View.GONE);
        mViewState = ViewState.CONTENT;
    }

    private void showProgress() {
        ViewGroup containerContent = (ViewGroup) mRootView.findViewById(R.id.container_content);
        ViewGroup containerProgress = (ViewGroup) mRootView.findViewById(R.id.container_progress);
        ViewGroup containerOffline = (ViewGroup) mRootView.findViewById(R.id.container_offline);
        ViewGroup containerEmpty = (ViewGroup) mRootView.findViewById(R.id.container_empty);
        containerContent.setVisibility(View.VISIBLE);
        containerProgress.setVisibility(View.VISIBLE);
        containerOffline.setVisibility(View.GONE);
        containerEmpty.setVisibility(View.GONE);
        mViewState = ViewState.PROGRESS;
    }

    private void showEmpty() {
        ViewGroup containerContent = (ViewGroup) mRootView.findViewById(R.id.container_content);
        ViewGroup containerProgress = (ViewGroup) mRootView.findViewById(R.id.container_progress);
        ViewGroup containerOffline = (ViewGroup) mRootView.findViewById(R.id.container_offline);
        ViewGroup containerEmpty = (ViewGroup) mRootView.findViewById(R.id.container_empty);
        containerContent.setVisibility(View.GONE);
        containerProgress.setVisibility(View.GONE);
        containerOffline.setVisibility(View.GONE);
        containerEmpty.setVisibility(View.VISIBLE);
        mViewState = ViewState.EMPTY;
    }

    private void showOffline() {

        try {
            ViewGroup containerContent = (ViewGroup) mRootView.findViewById(R.id.container_content);
            ViewGroup containerProgress = (ViewGroup) mRootView.findViewById(R.id.container_progress);
            ViewGroup containerOffline = (ViewGroup) mRootView.findViewById(R.id.container_offline);
            ViewGroup containerEmpty = (ViewGroup) mRootView.findViewById(R.id.container_empty);
            containerContent.setVisibility(View.GONE);
            containerProgress.setVisibility(View.GONE);
            containerOffline.setVisibility(View.VISIBLE);
            containerEmpty.setVisibility(View.GONE);

            action_retry = (Button) mRootView.findViewById(R.id.action_retry);
            action_retry.setOnClickListener(this);

            mViewState = ViewState.OFFLINE;
        } catch (Exception ex) {
        }
    }

    private void renderView() {

        if (map != null) {
            map.clear();
            ClusterManager.clearItems();
            for (AbstractMapModel items : models) {
                ClusterManager.addItem(items);
            }
            ClusterManager.cluster();
        }
    }

    private void initMap() {
        if (!Tools.isSupportedOpenGlEs2(getActivity())) {
            Toast.makeText(getActivity(), R.string.global_map_fail_toast, Toast.LENGTH_LONG).show();
        }
    }

    public class InfoWindowRefresher implements Callback {
        private Marker markerToRefresh;

        public InfoWindowRefresher(Marker markerToRefresh) {
            this.markerToRefresh = markerToRefresh;
        }

        @Override
        public void onSuccess() {
            markerToRefresh.showInfoWindow();
        }

        @Override
        public void onError() {
        }
    }

    public class BusinessInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        BusinessInfoWindowAdapter() {
            myContentsView = getActivity().getLayoutInflater().inflate(R.layout.row_business_marker, null);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public View getInfoContents(Marker marker) {
            final ImageView mImageView_b;
            final TextView tittle_b;
            final RatingBar rating_b;
            final TextView address_b;
            final CardView cv_parent_b;
            final int mDimensionHe, mDimensionWe;
            final TagView offer;

            mDimensionHe = (int) getResources().getDimension(R.dimen.marker_large);
            mDimensionWe = (int) getResources().getDimension(R.dimen.marker_large_we);

            cv_parent_b = (CardView) myContentsView.findViewById(R.id.cv_parent);
            mImageView_b = (ImageView) myContentsView.findViewById(R.id.image);
            tittle_b = (TextView) myContentsView.findViewById(R.id.amu_text);
            tittle_b.setTypeface(FontTypefaceUtils.getKnockout(getActivity()));
            rating_b = (RatingBar) myContentsView.findViewById(R.id.user_review_list_rating_bar);
            address_b = (TextView) myContentsView.findViewById(R.id.amu_text_address);
            address_b.setTypeface(FontTypefaceUtils.getKnockout(getActivity()));
            TagView amount = (TagView) myContentsView.findViewById(R.id.tag_amount);
            offer = (TagView) myContentsView.findViewById(R.id.tag_offer);

            cv_parent_b.setLayoutParams(new CardView.LayoutParams(
                    mDimensionWe, mDimensionHe));

            if (clickedClusterItem != null) {
                if (clickedClusterItem instanceof BusinessAddress) {

                    final BusinessAddress bus = (BusinessAddress) clickedClusterItem;

                    if (bus.getBusiness().isOffer())
                        offer.setVisibility(View.VISIBLE);
                    else
                        offer.setVisibility(View.GONE);

                    tittle_b.setText(bus.getBusiness().getTitle());
                    DecimalFormat decimalFormat = new DecimalFormat("#");
                    rating_b.setRating(Float.parseFloat(decimalFormat.format(bus.getBusiness().getReviewAverageStars())));
                    address_b.setText(bus.getAddress().toString());
                    cv_parent_b.setLayoutParams(new CardView.LayoutParams(mDimensionWe, mDimensionHe));

                    if (bus.getLastknowpeoplecount() < 10) {
                        amount.setTagColor(getResources().getColor(R.color.global_color_blue_accent));
                        amount.setText(String.valueOf(5));
                    } else if (bus.getLastknowpeoplecount() > 10 && bus.getLastknowpeoplecount() < 50) {
                        amount.setTagColor(getResources().getColor(R.color.global_color_green_primary_dark));
                        amount.setText("10+");
                    } else if (bus.getLastknowpeoplecount() > 50 && bus.getLastknowpeoplecount() < 100) {
                        amount.setTagColor(getResources().getColor(R.color.global_color_carrot_primary));
                        amount.setText("50+");
                    } else {
                        amount.setTagColor(getResources().getColor(R.color.primary_darker));
                        amount.setText("100+");
                    }

                    try {
                        if (bus.getBusiness().getBusinessPictures() != null) {
                            if (bus.getBusiness().getBusinessPictures().size() > 0) {
                                String url = bus.getBusiness().getBusinessPictures().get(0).getImagePath();

                                if (mImageView_b.getDrawable() == null) {
                                    Picasso.with(getActivity()).load(url)
                                            .resize(size, size).
                                            transform(new BitmapTransform(BitmapTransform.MAX_WIDTH, BitmapTransform.MAX_HEIGHT))
                                            .centerInside().into(mImageView_b, new InfoWindowRefresher(marker));
                                } else {
                                    Picasso.with(getActivity()).load(url)
                                            .resize(size, size).
                                            transform(new BitmapTransform(BitmapTransform.MAX_WIDTH, BitmapTransform.MAX_HEIGHT))
                                            .centerInside().into(mImageView_b);
                                }
                            } else {
                                mImageView_b.setImageDrawable(getActivity().getDrawable(R.drawable.placeholder));
                            }
                        }
                    } catch (Exception ex) {
                        LogManager.getInstance().info("Error", ex.getLocalizedMessage());
                    }
                }
            }

            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

    }

    private void SetupClusterManager() {

        final IconGenerator BusinessIconGenerator = new IconGenerator(getActivity());
        final ImageView mImageView_b;
        final TagView amount;
        final ImageView offer;

        View BusinessProfile = getActivity().getLayoutInflater().inflate(R.layout.row_business_marker_v_one, null);
        BusinessIconGenerator.setContentView(BusinessProfile);

        mImageView_b = (ImageView) BusinessProfile.findViewById(R.id.image);
        amount = (TagView) BusinessProfile.findViewById(R.id.tag_amount);
        offer = (ImageView) BusinessProfile.findViewById(R.id.tag_offer);

        if (map != null) {

            ClusterManager = new ClusterManager<>(getActivity(), map);
            map.setInfoWindowAdapter(ClusterManager.getMarkerManager());

            ClusterManager.setRenderer(new DefaultClusterRenderer<AbstractMapModel>(getActivity(), map, ClusterManager) {

                @Override
                protected boolean shouldRenderAsCluster(Cluster cluster) {
                    return cluster.getSize() > 5;
                }

                @Override
                protected void onBeforeClusterItemRendered(AbstractMapModel model, MarkerOptions markerOptions) {

                    if (model instanceof BusinessAddress) {
                        final BusinessAddress businessAddress = (BusinessAddress) model;

                        if (((BusinessAddress) model).getBusiness().isOffer())
                            offer.setVisibility(View.VISIBLE);
                        else
                            offer.setVisibility(View.GONE);

                        ClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new BusinessInfoWindowAdapter());
                        try {

                            if (((BusinessAddress) model).getId() % 2 == 0)
                                mImageView_b.setImageResource(R.drawable.ic_burger);
                            else if (((BusinessAddress) model).getId() % 3 == 1)
                                mImageView_b.setImageResource(R.drawable.ic_car);
                            else if(((BusinessAddress) model).getId() % 2 == 1)
                                mImageView_b.setImageResource(R.drawable.ic_shoes);
                            else
                                mImageView_b.setImageResource(R.drawable.ic_hairdressed);

                                if (businessAddress.getLastknowpeoplecount() < 10) {
                                    amount.setTagColor(getResources().getColor(R.color.global_color_blue_accent));
                                } else if (businessAddress.getLastknowpeoplecount() > 10 && businessAddress.getLastknowpeoplecount() < 50) {
                                    amount.setTagColor(getResources().getColor(R.color.global_color_green_primary_dark));
                                } else if (businessAddress.getLastknowpeoplecount() > 50 && businessAddress.getLastknowpeoplecount() < 100) {
                                    amount.setTagColor(getResources().getColor(R.color.global_color_carrot_primary));
                                } else {
                                    amount.setTagColor(getResources().getColor(R.color.primary_darker));
                                }
                        } catch (Exception ex) {
                        }

                        Bitmap icon = BusinessIconGenerator.makeIcon();
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));

                        map.setOnInfoWindowClickListener(marker -> {
                            if (clickedClusterItem != null)
                                if (clickedClusterItem instanceof BusinessAddress)
                                    startBusinessDetailActivity(((BusinessAddress) clickedClusterItem).getBusiness());
                        });

                    }
                    super.onBeforeClusterItemRendered(model, markerOptions);
                }
            });

            map.setOnCameraChangeListener(ClusterManager);
            map.setOnInfoWindowClickListener(ClusterManager);
            map.setOnMarkerClickListener(ClusterManager);

            ClusterManager.setOnClusterItemClickListener(item -> {
                clickedClusterItem = item;
                return false;
            });
        }
    }

    private Location getLastKnownLocation(LocationManager locationManager) {

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }

        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Location locationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        long timeNet = 0l;
        long timeGps = 0l;

        if (locationNet != null) {
            timeNet = locationNet.getTime();
        }

        if (locationGps != null) {
            timeGps = locationGps.getTime();
        }

        if (timeNet > timeGps) return locationNet;
        else return locationGps;
    }

    private void setMapType(int type) {
        if (map != null) {
            map.setMapType(type);

            Preferences preferences = new Preferences(getActivity());
            preferences.setMapType(type);
        }
    }

    private void startBusinessDetailActivity(Business business) {
        //showProgress();
        //new BusinessTask((ResponseObjectCallBack) this).CallService(2, business.getId(), null, null);
    }

    @Override
    public void onResponseLoadCallBack(final ArrayList list) {

        runTaskCallback(() -> {
            if (mRootView == null) return;

            Iterator<AbstractMapModel> iterator = list.iterator();
            while (iterator.hasNext()) {
                AbstractMapModel bus = iterator.next();
                models.add(bus);
            }
            if (models != null && models.size() > 0) {
                renderView();
                showContent();
            } else showEmpty();
        });
    }

    @Override
    public void onResponseObjectCallBack(Object object) {
        showContent();
        Intent intent;
        if (object instanceof Business) {
            intent = new Intent(getActivity(), ActivityBusinessDetails.class);
            Bundle b = new Bundle();
            Gson gSon = new Gson();
            b.putString(ActivityBusinessDetails.EXTRA_OBJCT_BUSINESS, gSon.toJson(object));
            b.putString(ActivityBusinessDetails.EXTRA_OBJCT_USER, gSon.toJson(user));
            intent.putExtras(b);
            startActivity(intent);
        }
    }

    @Override
    public void onError(String message, Integer code) {
        LogManager.getInstance().info(message, code + "");
        showOffline();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.action_retry) {
            BusinessFilter filter = new BusinessFilter();
            //ExecuteBusiness();
        } else if (view.getId() == R.id.action_error_retry) {

        }
    }

    private void ExecuteBusiness() {

        fab.setVisibility(View.VISIBLE);
        models = new ArrayList<>();
        business = new ArrayList<>();
        Business item;
        BusinessPictures pic;
        BusinessAddress address;

        ArrayList<BusinessPictures> listpic = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            pic = new BusinessPictures();
            pic.setImagePath("https://serbiananimalsvoice.files.wordpress.com/2016/10/burger-king-burger-logo.jpg");
            listpic.add(pic);
        }

        ArrayList<BusinessAddress> addresses;
        LatLng myLocation = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());

        business = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            item = new Business();
            item.setId(String.valueOf(i));
            item.setDescription("Far far away, behind the word mountains, far from the countries Vokalia and Consonantia, there live the blind texts. Separated they live in Bookmarksgrove right at the coast of the Semantics, a large language ocean. A small river named Duden flows by their place and supplies it with the necessary regelialia. It is a paradisematic country, in which roasted parts of sentences fly into your mouth. Even the all-powerful Pointing has no control about the blind texts it is an almost unorthographic life One day however a small line of blind text by the name of Lorem Ipsum decided to leave for the far World of Grammar. The Big Oxmox advised her not to do so, because there were thousands of bad Commas, wild Question Marks and devious Semikoli, but the Little Blind Text didn’t listen. She packed her seven versalia, put her initial into the belt and made herself on the way. When she reached the first hills of the Italic Mountains, she had a last view back on the skyline of her hometown Bookmarksgrove, the headline of Alphabet Village and the subline of her own road, the Line Lane. Pityful a rethoric question ran over her cheek, then");
            item.setWebSiteUrl("https://www.google.com/");
            item.setEmailAddress("email@macdonald.com");
            item.setPhoneNumber("593-0987523921");
            item.setTitle("Mac Donald");
            item.setReviewAverageStars((double) getRandomNumberInRange(1, 5));
            item.setBusinessPictures(listpic);

            if ((i % 2) == 0)
                item.setOffer(true);
            else
                item.setOffer(false);

            addresses = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                address = new BusinessAddress();
                address.setId(new Random().nextInt());
                LatLng tmp = getRandomLocation(myLocation, 4000);
                address.setLatitude(String.valueOf(tmp.latitude));
                address.setLongitude(String.valueOf(tmp.longitude));
                address.setAddress("4180 SW 9th St, Miami, FL 33134");
                address.setBusiness(item);
                address.setLastknowpeoplecount(getRandomNumberInRange(1, 200));
                addresses.add(address);
            }

            item.setBusinessAddress(addresses);
            business.add(item);
        }

        runTaskCallback(() -> {
            if (mRootView == null) return;

            Iterator<Business> buss = business.iterator();
            Iterator<BusinessAddress> addrs;

            while (buss.hasNext()) {
                Business btmp = buss.next();
                addrs = btmp.getBusinessAddress().iterator();

                while (addrs.hasNext()) {
                    BusinessAddress atmp = addrs.next();
                    AbstractMapModel bus = atmp;
                    models.add(bus);
                }
            }

            if (models != null && models.size() > 0) {
                renderView();
                showContent();
            } else showEmpty();
        });
    }

    public LatLng getRandomLocation(LatLng point, int radius) {

        List<LatLng> randomPoints = new ArrayList<>();
        List<Float> randomDistances = new ArrayList<>();
        Location myLocation = new Location("");
        myLocation.setLatitude(point.latitude);
        myLocation.setLongitude(point.longitude);

        //This is to generate 10 random points
        for (int i = 0; i < 10; i++) {
            double x0 = point.latitude;
            double y0 = point.longitude;

            Random random = new Random();

            // Convert radius from meters to degrees
            double radiusInDegrees = radius / 111000f;

            double u = random.nextDouble();
            double v = random.nextDouble();
            double w = radiusInDegrees * Math.sqrt(u);
            double t = 2 * Math.PI * v;
            double x = w * Math.cos(t);
            double y = w * Math.sin(t);

            // Adjust the x-coordinate for the shrinking of the east-west distances
            double new_x = x / Math.cos(y0);

            double foundLatitude = new_x + x0;
            double foundLongitude = y + y0;
            LatLng randomLatLng = new LatLng(foundLatitude, foundLongitude);
            randomPoints.add(randomLatLng);
            Location l1 = new Location("");
            l1.setLatitude(randomLatLng.latitude);
            l1.setLongitude(randomLatLng.longitude);
            randomDistances.add(l1.distanceTo(myLocation));
        }
        //Get nearest point to the centre
        int indexOfNearestPointToCentre = randomDistances.indexOf(Collections.min(randomDistances));
        return randomPoints.get(indexOfNearestPointToCentre);
    }

    private static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    private void Run() {
        ExecuteBusiness();
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

    private void initGeolocation() {
        new Geolocation((LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE), this);
    }

    private void stopTimer() {
        mTimerHandler.removeCallbacks(mTimerRunnable);
    }

    @Override
    public void onGeolocationRespond(Geolocation geolocation, final Location location) {
        runTaskCallback(() -> {
            mLocation = location;
            Run();
        });
    }

    @Override
    public void onGeolocationFail(Geolocation geolocation) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        setUpMap();
    }

    public void setUpMap() {

        if (map != null) {
            Preferences preferences = new Preferences(getActivity());
            map.setMapType(preferences.getMapType());
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            map.setMyLocationEnabled(true);
            UiSettings settings = map.getUiSettings();
            settings.setAllGesturesEnabled(true);
            settings.setMyLocationButtonEnabled(true);
            settings.setZoomControlsEnabled(false);

            LatLng latLng = null;
            if (mPoiLatitude == 0.0 && mPoiLongitude == 0.0) {
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                Location location = getLastKnownLocation(locationManager);
                if (location != null)
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
            } else {
                latLng = new LatLng(mPoiLatitude, mPoiLongitude);
            }

            if (latLng != null) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(MAP_ZOOM)
                        .bearing(0)
                        .tilt(0)
                        .build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }

        SetupClusterManager();
    }

    private void CheckGPS() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
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

    public void showMultiChoice() {

       /* ArrayList<CarBrand> list = new CarBrandDAO(AppDatabaseManager.getInstance().getHelper()).GetList();
        String[] categories = new String[0];
        if (list != null) {
            if (list.size() > 0) {
                 categories = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    categories[i] = list.get(i).getDescription();
                }
            }
        }

        final String[] finalCategories = categories;
        new MaterialDialog.Builder(getActivity())
                .title(R.string.business_categories)
                .items(categories)
                .itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        return true;
                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.clearSelectedIndices();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.getSelectedIndices();
                        filtercategories = new ArrayList<>();
                        for (int i = 0; i < dialog.getSelectedIndices().length; i++) {
                            filtercategories.add(new CarBrandDAO(AppDatabaseManager.getInstance().getHelper()).LoadBrandDescription(finalCategories[dialog.getSelectedIndices()[i]]).getId());
                        }

                        strFilter = new StringBuilder();
                        for (int j = 0; j < filtercategories.size(); j++) {
                            if (j > 0) strFilter.append('\n');
                            strFilter.append(filtercategories.get(j));
                            strFilter.append(": ");
                        }

                        dialog.dismiss();
                    }
                })
                .alwaysCallMultiChoiceCallback()
                .positiveText(R.string.chose)
                .autoDismiss(false)
                .neutralText(R.string.clear)
                .show();*/
    }

}

