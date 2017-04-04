package com.app.tomeetme.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.app.tomeetme.R;
import com.app.tomeetme.data.AppDatabaseManager;
import com.app.tomeetme.data.dao.UserDAO;
import com.app.tomeetme.data.dao.IOperationDAO;
import com.app.tomeetme.helper.log.LogManager;
import com.app.tomeetme.helper.util.CustomTypefaceSpan;
import com.app.tomeetme.helper.util.FontTypefaceUtils;
import com.app.tomeetme.helper.util.Tools;
import com.app.tomeetme.model.User;
import com.app.tomeetme.view.fragment.BusinessListFragment;
import com.app.tomeetme.view.fragment.MapFragment;
import com.app.tomeetme.view.fragment.SettingFragment;
import com.app.tomeetme.view.activity.createBusiness.CreateBusinessActivity;
import com.app.tomeetme.view.widget.CustomFontTextView;
import com.github.florent37.awesomebar.AwesomeBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class ActivityMain extends AppCompatActivity {

    public static final String EXTRA_OBJCT_USER = "USER";
    private static final int REQUEST_LOGIN = 0;
    private Toolbar toolbar;
    //private ActionBar actionBar;
    private NavigationView navigationView;
    private User user;
    private Handler mainHandler;
    private Runnable mRunnable;
    private Fragment fragment;
    public static int currentColor;
    private DrawerLayout drawer;
    private Bundle bundle;

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] permissionsRequired = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;

    AwesomeBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initActionbar();
        initAwesomeActionbar();
        CheckPermission();
    }

    private void initAwesomeActionbar() {
        bar = (AwesomeBar) findViewById(R.id.bar);
        //bar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        bar.setActionItemClickListener((position, actionItem) ->  startActivity(new Intent(this, CreateBusinessActivity.class)));
        bar.setOnMenuClickedListener(v -> drawer.openDrawer(GravityCompat.START));

        //if (user != null)
        bar.addAction(R.drawable.awsb_ic_edit_animated, "Add a Business");
    }

    private void CheckPermission() {
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);

        if (ActivityCompat.checkSelfPermission(ActivityMain.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(ActivityMain.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(ActivityMain.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ActivityMain.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(ActivityMain.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(ActivityMain.this, permissionsRequired[2])) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMain.this);
                builder.setTitle("Need Permissions");
                builder.setMessage("This app needs Camera and Location permissions.");
                builder.setPositiveButton("Grant All", (dialog, which) -> {
                    dialog.cancel();
                    ActivityCompat.requestPermissions(ActivityMain.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                builder.show();
            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMain.this);
                builder.setTitle("Need Permissions");
                builder.setMessage("This app needs Camera and Location permissions.");
                builder.setPositiveButton("Grant All", (dialog, which) -> {
                    dialog.cancel();
                    sentToSettings = true;
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                    Toast.makeText(getBaseContext(), "Go to Permissions to Grant Camera and Location", Toast.LENGTH_LONG).show();
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                builder.show();
            } else {
                ActivityCompat.requestPermissions(ActivityMain.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.commit();
        } else {
            Init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                Init();
                LogManager.getInstance().info("All granted", "true");
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(ActivityMain.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(ActivityMain.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(ActivityMain.this, permissionsRequired[2])) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMain.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs some permissions for correct functioning.");
                builder.setPositiveButton("Grant", (dialog, which) -> {
                    dialog.cancel();
                    ActivityCompat.requestPermissions(ActivityMain.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                builder.show();
            } else {
                Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(ActivityMain.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                Init();
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(ActivityMain.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                Init();
            }
        }
    }

    private void Init() {

        bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(EXTRA_OBJCT_USER)) {
                Gson gSon = new Gson();
                user = gSon.fromJson(bundle.getString(EXTRA_OBJCT_USER), new TypeToken<User>() {
                }.getType());
            }
        }

        initDrawerMenu();

    }

    private void initDrawerMenu() {

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                hideKeyboard();
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            menuItem.setChecked(true);
            drawer.closeDrawers();

            switch (menuItem.getItemId()) {

                case R.id.nav_home:
                    mainHandler = new Handler(getMainLooper());

                    mRunnable = () -> {
                        //actionBar.setTitle(menuItem.getTitle());
                        fragment = new MapFragment().newInstance(user);
                        displayContentView(fragment);
                    };
                    mainHandler.post(mRunnable);
                    break;

                case R.id.nav_setting:
                    //actionBar.setTitle(menuItem.getTitle());
                    fragment = new SettingFragment();
                    displayContentView(fragment);
                    break;

                case R.id.nav_business:
                    mainHandler = new Handler(getMainLooper());

                    mRunnable = () -> {
                        fragment = new BusinessListFragment().newInstance(user);
                        displayContentView(fragment);
                    };
                    mainHandler.post(mRunnable);
                    break;

                case R.id.nav_favorite:
                    mainHandler = new Handler(getMainLooper());

                    mRunnable = () -> {
                        Intent intent;
                        intent = new Intent(ActivityMain.this, ActivityBusinessFavorites.class);
                        Bundle b = new Bundle();
                        Gson gSon = new Gson();
                        b.putString(ActivityBusinessList.EXTRA_OBJCT_USER, gSon.toJson(user));
                        intent.putExtras(b);
                        startActivityForResult(intent, 1);
                    };
                    mainHandler.post(mRunnable);
                    break;

                case R.id.nav_logout:

                    user.setActive(false);
                    new UserDAO(AppDatabaseManager.getInstance().getHelper()).Create(user, IOperationDAO.OPERATION_INSERT_OR_UPDATE);

                    user = null;
                    getIntent().removeExtra(EXTRA_OBJCT_USER);
                    finish();
                    startActivity(getIntent());
                    break;

                case R.id.nav_login:
                    Intent intent = new Intent(getApplicationContext(), ActivityLogin.class);
                    startActivityForResult(intent, REQUEST_LOGIN);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    break;
            }

            return true;
        });

        navigationView.getMenu().clear();

        if (user != null) {
            navigationView.inflateMenu(R.menu.menu_drawer);
            View nav_header = LayoutInflater.from(this).inflate(R.layout.nav_header, null);
            ((TextView) nav_header.findViewById(R.id.name)).setText(user.getFirstName());
            ((TextView) nav_header.findViewById(R.id.name)).setTypeface(FontTypefaceUtils.getKnockout(this));
            ((TextView) nav_header.findViewById(R.id.address)).setText(user.getEmail());
            ((TextView) nav_header.findViewById(R.id.address)).setTypeface(FontTypefaceUtils.getKnockout(this));

            (nav_header.findViewById(R.id.header_content)).setOnClickListener(view -> {
                        Handler mainHandler1 = new Handler(getMainLooper());
                        Runnable myRunnable = () -> {
                            Intent intent;
                            intent = new Intent(ActivityMain.this, ActivityProfileDetails.class);
                            Bundle b = new Bundle();
                            Gson gSon = new Gson();
                            b.putString(ActivityProfileDetails.EXTRA_OBJCT_USER, gSon.toJson(user));
                            intent.putExtras(b);
                            startActivityForResult(intent, 1);
                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        };
                        mainHandler1.post(myRunnable);
                    }
            );
            navigationView.addHeaderView(nav_header);
        } else {
            navigationView.inflateMenu(R.menu.menu_drawer_offline);
        }

        fragment = new MapFragment().newInstance(user);
        displayContentView(fragment);
        applyFontToMenu(navigationView.getMenu());

    }

    private void applyFontToMenu(Menu m) {
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {

                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }
            applyFontToMenuItem(mi);
        }
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Knockout-29.otf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    public void initActionbar() {
        Tools.systemBarLolipop(this);
        currentColor = ContextCompat.getColor(this, R.color.toolbar_bg);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        //actionBar = getSupportActionBar();
        //actionBar.setTitle(getString(R.string.str_home));
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        CustomFontTextView tvSearchToolBar_title = (CustomFontTextView) findViewById(R.id.tvSearchToolBar_title);
        tvSearchToolBar_title.setText(getString(R.string.app_name).toUpperCase());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> drawer.openDrawer(GravityCompat.START));

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            doExitApp();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void displayContentView(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_content, fragment);
            fragmentTransaction.commit();
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private long exitTime = 0;

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.press_again_exit_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            moveTaskToBack(true);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}
