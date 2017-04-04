package com.app.tomeetme.view.fragment;

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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.app.tomeetme.R;
import com.app.tomeetme.helper.geolocation.Geolocation;
import com.app.tomeetme.helper.geolocation.GeolocationListener;
import com.app.tomeetme.helper.util.LocationUtility;
import com.app.tomeetme.helper.util.Tools;
import com.app.tomeetme.model.Business;
import com.app.tomeetme.model.BusinessAddress;
import com.app.tomeetme.model.BusinessFilter;
import com.app.tomeetme.model.BusinessPictures;
import com.app.tomeetme.model.User;
import com.app.tomeetme.rest.ResponseLoadCallBack;
import com.app.tomeetme.rest.ResponseObjectCallBack;
import com.app.tomeetme.rest.task.business.BusinessTask;
import com.app.tomeetme.view.activity.ActivityBusinessDetails;
import com.app.tomeetme.view.adapter.BusinessAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;

public class BusinessListFragment extends BaseFragment implements ResponseObjectCallBack,
        ResponseLoadCallBack,
        SwipeRefreshLayout.OnRefreshListener,
        GeolocationListener, View.OnClickListener {

    public static final String EXTRA_OBJCT_USER = "User";
    private static final long TIMER_DELAY = 60000l;
    private RecyclerView recyclerView;
    private BusinessAdapter adapter;
    private SearchView searchView;
    public static ArrayList<Business> businesses;
    private RelativeLayout ryt_empty, ryt_error, ryt_connection, ryt_progressbar;
    private SwipeRefreshLayout refresh;
    private View view;
    private Geolocation mGeolocation = null;
    private Location mLocation = null;
    private Handler mTimerHandler;
    private Runnable mTimerRunnable;
    private Button action_retry, error_retry;
    private User user;

    public static BusinessListFragment newInstance(User us) {
        BusinessListFragment fragment = new BusinessListFragment();
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
        view = inflater.inflate(R.layout.activity_list, container, false);
        getActivity().invalidateOptionsMenu();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.containsKey(EXTRA_OBJCT_USER)) {
                Gson gSon = new Gson();
                user = gSon.fromJson(bundle.getString(EXTRA_OBJCT_USER), new TypeToken<User>() {
                }.getType());
            }
        }

        CheckGPS();
        Load();
        return view;
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

    @Override
    public void onStop() {
        super.onStop();
    }

    protected void Load() {
        ryt_progressbar = (RelativeLayout) view.findViewById(R.id.layout_progress);
        ryt_empty = (RelativeLayout) view.findViewById(R.id.layout_empty);
        ryt_error = (RelativeLayout) view.findViewById(R.id.layout_error);
        ryt_connection = (RelativeLayout) view.findViewById(R.id.layout_connection);

        error_retry = (Button) view.findViewById(R.id.action_error_retry);
        error_retry.setOnClickListener(this);

        action_retry = (Button) view.findViewById(R.id.action_retry);
        action_retry.setOnClickListener(this);

        refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        refresh.setOnRefreshListener(this);
        refresh.setColorSchemeColors(Color.parseColor("#ffc107"));

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        SetupAdapter();

        //Run(false);
        RunMock();

    }

    private void RunMock() {

        ArrayList<Business> list = new ArrayList<>();
        Business item;
        BusinessPictures pic = new BusinessPictures();
        pic.setImagePath("https://serbiananimalsvoice.files.wordpress.com/2016/10/burger-king-burger-logo.jpg");
        ArrayList<BusinessPictures> listpic = new ArrayList<>();
        listpic.add(pic);


        ArrayList<BusinessAddress> addresses;

        for (int i = 0; i < 10; i++) {
            addresses = new ArrayList<>();

            item = new Business();
            item.setId(String.valueOf(i));
            item.setDescription("Far far away, behind the word mountains, far from the countries Vokalia and Consonantia, there live the blind texts. Separated they live in Bookmarksgrove right at the coast of the Semantics, a large language ocean. A small river named Duden flows by their place and supplies it with the necessary regelialia. It is a paradisematic country, in which roasted parts of sentences fly into your mouth. Even the all-powerful Pointing has no control about the blind texts it is an almost unorthographic life One day however a small line of blind text by the name of Lorem Ipsum decided to leave for the far World of Grammar. The Big Oxmox advised her not to do so, because there were thousands of bad Commas, wild Question Marks and devious Semikoli, but the Little Blind Text didn’t listen. She packed her seven versalia, put her initial into the belt and made herself on the way. When she reached the first hills of the Italic Mountains, she had a last view back on the skyline of her hometown Bookmarksgrove, the headline of Alphabet Village and the subline of her own road, the Line Lane. Pityful a rethoric question ran over her cheek, then");
            item.setWebSiteUrl("https://www.google.com/");
            item.setEmailAddress("email@macdonald.com");
            item.setPhoneNumber("593-0987523921");
            item.setTitle("Mac Donald");

            item.setBusinessPictures(listpic);

            BusinessAddress address;
            for (int j = 0; j < 3; j++) {
                address = new BusinessAddress();
                address.setId(i + j);
                address.setLatitude("25.763214");
                address.setLongitude("-80.263015");
                address.setAddress("4180 SW 9th St, Miami, FL 33134");
                addresses.add(address);
            }

            item.setBusinessAddress(addresses);

            list.add(item);
        }

        ryt_error.setVisibility(View.GONE);
        ryt_empty.setVisibility(View.GONE);
        ryt_connection.setVisibility(View.GONE);
        refresh.setRefreshing(false);
        ryt_progressbar.setVisibility(View.GONE);

        try {
            businesses.addAll(list);
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

    private void SetupAdapter() {
        businesses = new ArrayList<>();
        adapter = new BusinessAdapter(getActivity(), recyclerView, (position, v) -> {
            int pos = adapter.getPosition(position);
            Business business = businesses.get(pos);
            view = v;
            ryt_progressbar.setVisibility(View.VISIBLE);
            //OpenDetail(business);
            OpenMockDetail(business);
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
        if (Tools.isOnline(getActivity(), true, false)) {
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
                initGeolocation();
                mTimerHandler.postDelayed(this, TIMER_DELAY);
            }
        };
    }

    private void initGeolocation() {
        mGeolocation = null;
        mGeolocation = new Geolocation((LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE), this);
    }

    private void stopTimer() {
        mTimerHandler.removeCallbacks(mTimerRunnable);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {

            inflater.inflate(R.menu.menu_search, menu);
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

            searchView.setOnSearchClickListener(v -> setItemsVisibility(menu, searchItem, false));


            searchView.setOnCloseListener(() -> {
                setItemsVisibility(menu, searchItem, true);
                adapter.notifyDataSetChanged();
                return false;
            });
            searchView.onActionViewCollapsed();

        super.onCreateOptionsMenu(menu, inflater);
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
        if (Tools.isOnline(getActivity(), true, false)) {
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
        Intent intent = new Intent(getActivity(), ActivityBusinessDetails.class);
        Bundle b = new Bundle();
        Gson gSon = new Gson();
        b.putString(ActivityBusinessDetails.EXTRA_OBJCT_BUSINESS, gSon.toJson(business));
        b.putString(ActivityBusinessDetails.EXTRA_OBJCT_USER, gSon.toJson(user));
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
