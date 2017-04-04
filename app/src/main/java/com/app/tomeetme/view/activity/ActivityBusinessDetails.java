package com.app.tomeetme.view.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.app.tomeetme.R;
import com.app.tomeetme.helper.log.LogManager;
import com.app.tomeetme.helper.util.BitmapTransform;
import com.app.tomeetme.helper.util.Tools;
import com.app.tomeetme.model.Business;
import com.app.tomeetme.model.User;
import com.app.tomeetme.model.BusinessFavorites;
import com.app.tomeetme.rest.ResponseLoadCallBack;
import com.app.tomeetme.rest.ResponseObjectCallBack;
import com.app.tomeetme.rest.task.business.BusinessFavoriteTask;
import com.app.tomeetme.view.fragment.BusinessAddressFragment;
import com.app.tomeetme.view.fragment.BusinessCouponsFragment;
import com.app.tomeetme.view.fragment.BusinessDetailFragment;
import com.app.tomeetme.view.fragment.BusinessReviewsFragment;
import com.app.tomeetme.view.widget.CustomFontTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ActivityBusinessDetails extends AppCompatActivity implements ResponseObjectCallBack, ResponseLoadCallBack {

    public static final String EXTRA_OBJCT_BUSINESS = "BUSINESS";
    public static final String EXTRA_OBJCT_USER = "USER";
    private ViewPager mViewPager;
    private BusinessDetailFragment frag_detail;
    private BusinessReviewsFragment frag_reviews;
    private BusinessAddressFragment frag_address;
    private BusinessCouponsFragment frag_promotions;
    private Business business;
    private int size = (int) Math.ceil(Math.sqrt(BitmapTransform.MAX_WIDTH * BitmapTransform.MAX_HEIGHT));
    private FloatingActionButton fab;
    private User user;
    private BusinessFavorites favorite;
    private ViewGroup containerProgress;
    private boolean isRunning = false;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_details);

        Bundle bundle = getIntent().getExtras();

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

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (business != null) {
            collapsingToolbarLayout.setTitle(business.getTitle());
        }

        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setNavigationOnClickListener(v -> finish());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Init();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void Init() {

        ImageView ivImage = (ImageView) findViewById(R.id.ivImage);
        CustomFontTextView tittle = (CustomFontTextView) findViewById(R.id.tittle);
        CustomFontTextView text = (CustomFontTextView) findViewById(R.id.text);

        tittle.setText(business.getTitle().equals("") ? "AUTO" : business.getTitle());
        //text.setText(business.getCarBrand().getDescription().equals("") ? "CAR DESCRIPTION" : business.getCarBrand().getDescription());

        fab = (FloatingActionButton) findViewById(R.id.fab);

        String url = null;
        if (business.getBusinessPictures() != null) {
            if (business.getBusinessPictures().size() > 0) {
                url = business.getBusinessPictures().get(0).getImagePath();
                Picasso.with(ActivityBusinessDetails.this).load(url).resize(size, size).transform(new BitmapTransform(BitmapTransform.MAX_WIDTH, BitmapTransform.MAX_HEIGHT)).centerInside().into(ivImage);
            }
        }

        if (url == null) {
            Picasso.with(this).load("file:///android_asset/placeholder.png").into(ivImage);
        }

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        if (Tools.getAPIVerison() >= 5.0) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }

        containerProgress = (ViewGroup) findViewById(R.id.container_progress);
        containerProgress.setVisibility(View.GONE);

        CheckFavorite();
    }


    private void CheckFavorite() {
        if (user != null) {
            showProgress();
            new BusinessFavoriteTask((ResponseLoadCallBack) this).CallService(1, user.getId(), "");
        } else {
            fab.setVisibility(View.GONE);
        }
    }

    private void renderFavorite() {
        fab.setVisibility(View.VISIBLE);
        fab.setImageDrawable(business.isFavorite() ? getResources().getDrawable(R.drawable.ic_star_white_36dp) : getResources().getDrawable(R.drawable.ic_star_border_white_36dp));

        fab.setOnClickListener(v -> {
            if (!isRunning) {
                isRunning = true;
                if (business.isFavorite()) {
                    showProgress();
                    RemoveFromFavorites();
                } else {
                    showProgress();
                    AddFavorite();
                }
                business.setFavorite(!business.isFavorite());
                fab.setImageDrawable(business.isFavorite() ? getResources().getDrawable(R.drawable.ic_star_white_36dp) : getResources().getDrawable(R.drawable.ic_star_border_white_36dp));
            }
        });
    }

    private void setupViewPager(ViewPager mViewPager) {
        BusinessPagerAdapter adapter = new BusinessPagerAdapter(getSupportFragmentManager());

        if (frag_detail == null) {
            frag_detail = new BusinessDetailFragment().newInstance(business, user);
        }

        if (frag_address == null) {
            frag_address = new BusinessAddressFragment().newInstance(business, user);
        }

        if (frag_promotions == null) {
            frag_promotions = new BusinessCouponsFragment().newInstance(business, user);
        }

        if (frag_reviews == null) {
            frag_reviews = new BusinessReviewsFragment().newInstance(business, user);
        }

        adapter.addFragment(frag_detail, getString(R.string.business_detail_description));
        adapter.addFragment(frag_address, getString(R.string.business_detail_address));
        adapter.addFragment(frag_address, getString(R.string.business_detail_promo));
        adapter.addFragment(frag_reviews, getString(R.string.business_detail_reviews));

        mViewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;
        Bundle b;
        Gson gSon;

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            case R.id.menu_images:
                intent = new Intent(ActivityBusinessDetails.this, ActivityBusinessImages.class);
                b = new Bundle();
                gSon = new Gson();
                b.putString(ActivityBusinessImages.EXTRA_OBJCT_BUSINESS, gSon.toJson(business));
                intent.putExtras(b);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_business, menu);

        if (business.getBusinessPictures().size() > 0)
            menu.findItem(R.id.menu_images).setVisible(true);
        else
            menu.findItem(R.id.menu_images).setVisible(false);
        return true;
    }

    @Override
    public void onResponseObjectCallBack(Object object) {
        isRunning = false;
        CheckFavorite();
    }

    private void AddFavorite() {
        new BusinessFavoriteTask((ResponseObjectCallBack) this).CallService(2, user.getId(), business.getId());
    }

    private void RemoveFromFavorites() {
        if (favorite != null) {
            new BusinessFavoriteTask((ResponseObjectCallBack) this).CallService(3, "", favorite.getId());
        }
    }

    @Override
    public void onResponseLoadCallBack(ArrayList list) {
        hideProgress();
        Boolean isFav = false;
        ArrayList<BusinessFavorites> favorites = list;

        for (BusinessFavorites item : favorites) {
            if (item.getBusinessId().equals(business.getId())) {
                favorite = item;
                isFav = true;
                break;
            }
        }

        business.setFavorite(isFav);
        renderFavorite();
    }

    private void hideProgress() {
        containerProgress.setVisibility(View.GONE);
    }

    private void showProgress() {
        containerProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onError(String message, Integer code) {
        hideProgress();
        fab.setVisibility(View.GONE);
        LogManager.getInstance().info("Error", message + code);
    }

    static class BusinessPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public BusinessPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

}
