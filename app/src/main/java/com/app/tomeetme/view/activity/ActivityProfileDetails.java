package com.app.tomeetme.view.activity;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.tomeetme.R;
import com.app.tomeetme.data.AppDatabaseManager;
import com.app.tomeetme.data.dao.UserDAO;
import com.app.tomeetme.helper.util.FontTypefaceUtils;
import com.app.tomeetme.helper.util.Tools;
import com.app.tomeetme.model.User;
import com.app.tomeetme.rest.ResponseObjectCallBack;
import com.app.tomeetme.rest.task.account.UserTask;
import com.app.tomeetme.view.fragment.ProfileCarFragment;
import com.app.tomeetme.view.fragment.ProfileInformationFragment;
import com.app.tomeetme.view.fragment.ProfileNotificationsFragment;
import com.app.tomeetme.view.tagview.TagView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ActivityProfileDetails extends AppCompatActivity implements ResponseObjectCallBack {

    public static final String EXTRA_OBJCT_USER = "User";
    private ViewPager mViewPager;
    private ActionBar actionBar;
    private User profile;
    private View parent_view;
    private TextView username, email;
    private LinearLayout header_content;
    private RelativeLayout ryt_progressbar;
    private ProfileInformationFragment frg_information;
    private ProfileNotificationsFragment frg_notifications;
    private ProfileCarFragment frg_business;
    private TagView owner;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        parent_view = findViewById(R.id.content);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            if (bundle.containsKey(EXTRA_OBJCT_USER)) {
                Gson gSon = new Gson();
                profile = gSon.fromJson(bundle.getString(EXTRA_OBJCT_USER), new TypeToken<User>() {
                }.getType());
            }
        }

        //LogManager.getInstance().info("USER ID", profile.getId());

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        ryt_progressbar = (RelativeLayout) findViewById(R.id.layout_progress);
        username = (TextView) findViewById(R.id.name);
        username.setTypeface(FontTypefaceUtils.getKnockout(this));
        email = (TextView) findViewById(R.id.address);
        email.setTypeface(FontTypefaceUtils.getKnockout(this));
        owner = (TagView) findViewById(R.id.owner);
        header_content = (LinearLayout) findViewById(R.id.header_content);

        ImageView ivImage = (ImageView) findViewById(R.id.ivImage);
        Picasso.with(this).load("file:///android_asset/placeholder.png").into(ivImage);

        //GetUserInfo();

        username.setText(profile.getFirstName());
        email.setText(profile.getEmail());
        owner.setText(profile.isOwner() ? "Owner" : "Customer");

        if (Tools.getAPIVerison() >= 5.0) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    private void GetUserInfo() {
        if (Tools.isOnline(this, false, false)) {
            ryt_progressbar.setVisibility(View.VISIBLE);
            new UserTask(this).CallService(2, profile.getId());
        }
    }

    private void setupViewPager(ViewPager mViewPager) {
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());

        if (frg_information == null) {
            frg_information = new ProfileInformationFragment().newInstance(profile);
        }

        if (frg_notifications == null) {
            frg_notifications = new ProfileNotificationsFragment().newInstance(profile);
        }

        adapter.addFragment(frg_information, "PROFILE");
        adapter.addFragment(frg_notifications, "NOTIFICATIONS");

        if (profile.isOwner()) {
            if (frg_business == null) {
                frg_business = new ProfileCarFragment().newInstance(profile);
            }



            adapter.addFragment(frg_business, "BUSINESS");
        }

        mViewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void showContentScreen() {
        getSupportActionBar().setTitle("Profile");
        supportInvalidateOptionsMenu();
    }

    private void ShowMessage(String message) {
        String msg = message;
        Snackbar snack = Snackbar.make(parent_view, msg != null ? msg : "Generic Error", Snackbar.LENGTH_LONG).setActionTextColor(Color.YELLOW);
        View view = snack.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snack.show();
    }

    @Override
    public void onResponseObjectCallBack(Object object) {
        ryt_progressbar.setVisibility(View.GONE);
        if (object != null) {
            showContentScreen();
            profile = (User) object;
            profile.setActive(true);
            new UserDAO(AppDatabaseManager.getInstance().getHelper()).Update(profile);
        }
    }

    @Override
    public void onError(String message, Integer code) {
        ryt_progressbar.setVisibility(View.GONE);
        ShowMessage(message);
        switch (code) {
            case 401:
                Snackbar.make(parent_view, "Your session has expired, please login again", Snackbar.LENGTH_LONG).setAction("LOGIN", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        profile.setActive(false);
                        profile.setToken("");
                        new UserDAO(AppDatabaseManager.getInstance().getHelper()).Update(profile);
                       /* LoginManager.getInstance().logOut();
                        Intent ic = new Intent(ActivityProfileDetails.this, ActivityLogInPage.class);
                        ic.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        finish();
                        startActivity(ic);*/
                    }
                }).show();
                break;
            default:
                ShowMessage(message);
                break;
        }
    }

    static class MyPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fm) {
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
