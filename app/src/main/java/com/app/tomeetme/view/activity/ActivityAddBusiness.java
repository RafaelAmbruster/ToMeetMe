package com.app.tomeetme.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.app.tomeetme.R;
import com.app.tomeetme.helper.geolocation.Geolocation;
import com.app.tomeetme.helper.geolocation.GeolocationListener;
import com.app.tomeetme.helper.util.BitmapTransform;
import com.app.tomeetme.helper.util.CustomDateFormat;
import com.app.tomeetme.helper.util.DataFormatter;
import com.app.tomeetme.helper.util.FontTypefaceUtils;
import com.app.tomeetme.helper.util.Tools;
import com.app.tomeetme.model.Business;
import com.app.tomeetme.model.CreateResponse;
import com.app.tomeetme.model.User;
import com.app.tomeetme.model.BusinessPictures;
import com.app.tomeetme.model.Response;
import com.app.tomeetme.rest.ApiInterface;
import com.app.tomeetme.rest.ResponseObjectCallBack;
import com.app.tomeetme.rest.ServiceGenerator;
import com.app.tomeetme.rest.task.business.BusinessTask;
import com.app.tomeetme.view.image.Patio;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.apache.commons.validator.routines.UrlValidator;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ActivityAddBusiness extends AbstractActivity implements
        View.OnClickListener, Patio.PatioCallbacks, GeolocationListener, TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener,
        ResponseObjectCallBack {

    public static final String EXTRA_OBJCT_USER = "USER";
    private static final int REQUEST_CODE_TAKE_PICTURE = 1000;
    private static final int REQUEST_CODE_ATTACH_PICTURE = 2000;
    private static final long TIMER_DELAY = 60000l;
    private static final int MAP_ZOOM = 14;
    private static final int PLACE_PICKER_REQUEST = 1;

    private Toolbar toolbar;
    private ActionBar actionBar;
    private EditText tittle, description, direction, phonenumber, website;
    private View parent_view;
    private Patio mPatio;
    private Business business;
    private User us;
    private TextInputLayout tittleWrapper, directionWrapper, descriptionWrapper, phoneWrapper, websiteWrapper, urlWrapper;
    private MaterialDialog progress;
    private Geolocation mGeolocation = null;
    private Location mLocation = null;
    private Handler mTimerHandler;
    private Runnable mTimerRunnable;
    private TextView business_description,
            business_information,
            business_picture,
            business_start_date_dec,
            business_address_description;
    private TextView tvAmPm;
    private TextView tvDay;
    private TextView tvDayOfWeek;
    private TextView tvHour;
    private TextView tvMinute;
    private TextView tvMonthAndYear;
    private TextView business_category;
    private LinearLayout pick_start_date, pick_start_time;
    private Boolean datestart, timestart;
    private int size = (int) Math.ceil(Math.sqrt(BitmapTransform.MAX_WIDTH * BitmapTransform.MAX_HEIGHT));
    private Place place;
    private Calendar startDate = Calendar.getInstance();
    private CharSequence name, address;

    private ArrayList<BusinessPictures> images;
    private ArrayList<String> thumbnails;
    private String url;
    private int count;
    private EditText business_video_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_business_old);

        setupTimer();
        initActionbar();
        Load();
        Tools.systemBarLolipop(this);



    }

    private void Load() {

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            if (bundle.containsKey(EXTRA_OBJCT_USER)) {
                Gson gSon = new Gson();
                us = gSon.fromJson(bundle.getString(EXTRA_OBJCT_USER), new TypeToken<User>() {
                }.getType());
            }
        }

        parent_view = findViewById(android.R.id.content);


        Button pickerButton = (Button) findViewById(R.id.add_address);
        pickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder intentBuilder =
                            new PlacePicker.IntentBuilder();
                    if (mLocation != null)
                        intentBuilder.setLatLngBounds(toBounds(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 10));

                    Intent intent = intentBuilder.build(ActivityAddBusiness.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);

                } catch (GooglePlayServicesRepairableException
                        | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

       /* Button openCategories = (Button) findViewById(R.id.add_categories);
        openCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSingleChoice();
            }
        });*/

    }

    /*public void showSingleChoice() {

        ArrayList<CarBrand> list = new CarBrandDAO(AppDatabaseManager.getInstance().getHelper()).GetList();

        final String[] categories = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            categories[i] = list.get(i).getDescription();
        }

        new MaterialDialog.Builder(this)
                .title(R.string.business_categories)
                .items(categories)
                .itemsCallbackSingleChoice(2, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        filtercategories = new ArrayList<>();
                        filtercategories.add(new CarBrandDAO(AppDatabaseManager.getInstance().getHelper()).LoadBrandDescription(categories[which]));
                        business_category.setText(filtercategories.get(0).getDescription());
                        return true;
                    }
                })
                .autoDismiss(true)
                .positiveText(R.string.chose)
                .show();

    }*/

    public void showAddYoutubeVideoURL() {
        new MaterialDialog.Builder(this)
                .title(R.string.add_video_url)
                .content(R.string.input_add_youtube)
                .inputType(InputType.TYPE_TEXT_VARIATION_URI)
                .positiveText(R.string.add)
                .autoDismiss(true)
                .alwaysCallInputCallback()
                .input(R.string.youtube_video_url, 0, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if (!validateVideo(input.toString())) {
                            dialog.setContent("This is not a valid youtube video url");
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                        } else {
                            dialog.setContent(R.string.input_add_youtube);
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                            url = input.toString();
                        }
                    }
                }).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                business_video_url.setText(url);
            }
        }).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        }).show();
    }

    public LatLngBounds toBounds(LatLng center, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }

    private void SetDefaultStart() {
        tvHour.setText(CustomDateFormat.format(startDate.getTime(), "h"));
        tvMinute.setText(CustomDateFormat.format(startDate.getTime(), "mm"));
        tvAmPm.setText(CustomDateFormat.format(startDate.getTime(), "a"));
        tvDayOfWeek.setText(CustomDateFormat.format(startDate.getTime(), "EEEE"));
        tvDay.setText(CustomDateFormat.format(startDate.getTime(), "d"));
        tvMonthAndYear.setText(CustomDateFormat.format(startDate.getTime(), "MMMMMM yyyy"));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
                mGeolocation = new Geolocation((LocationManager) getSystemService(Context.LOCATION_SERVICE), ActivityAddBusiness.this);
                mTimerHandler.postDelayed(this, TIMER_DELAY);
            }
        };
    }

    private void Create() {
        thumbnails = mPatio.getThumbnailsPaths();
        images = new ArrayList<>();

        if (validate(phonenumber.getText().toString())) {
            showMessage("Please, add a phone number");
            phonenumber.requestFocus();
            phoneWrapper.setError("Incorrect number phone");
            return;
        }

        if (validate(website.getText().toString())) {
            showMessage("Please, add a website to localize your business in internet");
            website.requestFocus();
            websiteWrapper.setError("Invalid website URL");
            return;
        }

        if (validate(direction.getText().toString())) {
            showMessage("Please, pick an address for your business");
            direction.requestFocus();
            return;
        }

        if (validate(tittle.getText().toString())) {
            showMessage("Please, add a tittle");
            tittle.requestFocus();
            tittleWrapper.setError("Tittle should not be empty");
            return;
        }

        if (validate(description.getText().toString())) {
            showMessage("Please, add a description for your business");
            description.requestFocus();
            descriptionWrapper.setError("Description should not be empty");
            return;
        }

       /* if (filtercategories != null) {
            if (filtercategories.size() == 0) {
                showMessage("The business must have a category, please choose one");
                return;
            }
        }*/

        business = new Business();
        business.setDescription(description.getText().toString().trim());
        business.setTitle(description.getText().toString().trim());
        business.setPhoneNumber(phonenumber.getText().toString().trim());
        business.setWebSiteUrl(website.getText().toString().trim());
        business.setApplicationUserId(us.getId());
        //business.setCarBrand(filtercategories.get(0));

        new BusinessTask(this).CallService(3, "", null, business);
        ShowProgress("Please wait");
    }

    public boolean validate(String chain) {
        return chain.trim().length() == 0;
    }

    public boolean validateURL(String chain) {
        String[] schemes = {"http", "https"};
        UrlValidator urlValidator = new UrlValidator(schemes);
        return (urlValidator.isValid(chain));
    }

    public boolean validatePhone(String phoneNo) {
        if (phoneNo.matches("\\d{10}")) return true;
            //validating phone number with -, . or spaces
        else if (phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;
            //validating phone number with extension length from 3 to 5
        else if (phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
            //validating phone number where area code is in braces ()
        else if (phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) return true;
            //return false if nothing matches the input
        else return false;
    }

    private boolean validateVideo(String URL) {
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher;

        matcher = compiledPattern.matcher(URL);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
    }

    private void showMessage(String message) {
        Snackbar snack = Snackbar.make(parent_view, message, Snackbar.LENGTH_LONG).setActionTextColor(Color.YELLOW);
        View view = snack.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snack.show();
    }

    public void initActionbar() {

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        TextView tittle = (TextView) findViewById(R.id.tvSearchToolBar_title);
        tittle.setText("CREATE NEW BUSINESS");
        toolbar.setBackgroundColor(getResources().getColor(R.color.global_color_red_primary_dark));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startTimer();
        TimePickerDialog tpd = (TimePickerDialog) getFragmentManager().findFragmentByTag("Timepickerdialog");
        DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag("Datepickerdialog");

        if (tpd != null) tpd.setOnTimeSetListener(this);
        if (dpd != null) dpd.setOnDateSetListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
        if (mGeolocation != null) mGeolocation.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public void Close() {
        HideProgress();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        };
        new Timer().schedule(task, 1000);
    }

    @Override
    public void onTakePictureClick() {
        Intent intent = mPatio.getTakePictureIntent();
        startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
    }

    @Override
    public void onAddPictureClick() {
        Intent intent = mPatio.getAttachPictureIntent();
        startActivityForResult(intent, REQUEST_CODE_ATTACH_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_ATTACH_PICTURE) {
            mPatio.handleAttachPictureResult(data);
        }
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_TAKE_PICTURE) {
            mPatio.handleTakePictureResult(data);
        }
        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == Activity.RESULT_OK) {

            place = PlacePicker.getPlace(this, data);
            name = place.getName();
            address = place.getAddress();
            direction.setText(name + " - " + address);
            renderViewMap(place.getLatLng());
        }
    }

    private void renderViewMap(LatLng latLng) {
        final ImageView imageView = (ImageView) parent_view.findViewById(R.id.fragment_poi_detail_map_image);

        String key = getString(R.string.google_maps_key);
        String url = getStaticMapUrl(key, latLng.latitude, latLng.longitude, MAP_ZOOM);
        Picasso.with(ActivityAddBusiness.this)
                .load(url).resize(size, size)
                .placeholder(R.drawable.placeholder_map)
                .error(R.drawable.placeholder_map)
                .transform(new BitmapTransform(BitmapTransform.MAX_WIDTH, BitmapTransform.MAX_HEIGHT)).
                centerInside().into(imageView);
    }

    private String getStaticMapUrl(String key, double lat, double lon, int zoom) {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        if (item.getItemId() == R.id.action_create) {
            hideKeyboard();
            Create();
        }

        return super.onOptionsItemSelected(item);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void ShowProgress(String content) {
        if (progress == null) {
            progress = new MaterialDialog.Builder(this)
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

    @Override
    public void onGeolocationRespond(Geolocation geolocation, final Location location) {
        runTaskCallback(new Runnable() {
            public void run() {
                mLocation = location;
            }
        });
    }

    @Override
    public void onGeolocationFail(Geolocation geolocation) {
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {

        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;

        if (timestart) {

            startDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
            startDate.set(Calendar.MINUTE, minute);
            startDate.set(Calendar.SECOND, second);

            tvHour.setText(hourString);
            tvMinute.setText(minuteString);
            tvAmPm.setText(CustomDateFormat.format(startDate.getTime(), "a"));
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        if (datestart) {

            startDate.set(Calendar.YEAR, year);
            startDate.set(Calendar.MONTH, monthOfYear);
            startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            tvDayOfWeek.setText(CustomDateFormat.format(startDate.getTime(), "EEEE"));
            tvDay.setText(String.valueOf(dayOfMonth));
            tvMonthAndYear.setText(DataFormatter.getMonth(monthOfYear) + " " + year);

        }
    }

    private void getDate(String Tittle) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                ActivityAddBusiness.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setThemeDark(true);
        dpd.vibrate(false);
        dpd.dismissOnPause(false);
        dpd.showYearPickerFirst(false);
        dpd.setTitle(Tittle);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    private void getTime(String tittle) {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                ActivityAddBusiness.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        tpd.setThemeDark(true);
        tpd.vibrate(false);
        tpd.dismissOnPause(false);
        tpd.enableSeconds(false);
        tpd.enableMinutes(true);
        tpd.setTitle(tittle);
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    @Override
    public void onResponseObjectCallBack(Object object) {

        if (object instanceof Response) {
            Response response = (Response) object;
            String businessId = response.getBusiness().getId();
            if (thumbnails.size() > 0) {


                for (String item : thumbnails) {
                    BusinessPictures pi = new BusinessPictures();
                    File picfile = new File(item);
                    String filename = picfile.getName();
                    String pic = Tools.CompressImage(item, ActivityAddBusiness.this);
                    pi.setImageBase64(pic);
                    pi.setImageFileName(filename);
                    pi.setBusinessId(businessId);
                    images.add(pi);
                }

                CreateImages(images);
            } else {
                showMessage(response.getMessage());
                Close();
            }
        }
    }

    private void CreateImages(final ArrayList<BusinessPictures> pictures) {
        ApiInterface apiService =
                ServiceGenerator.createService(ApiInterface.class);
        Map<String, String> params;
        count = 1;
        for (BusinessPictures item : pictures) {

            params = new HashMap<>();
            params.put("businessId", item.getBusinessId());
            params.put("imageBase64", item.getImageBase64());
            params.put("imageFileName", item.getImageFileName());

            apiService.createBusinessPicture(params)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<CreateResponse>() {
                        @Override
                        public final void onCompleted() {
                            if (count == pictures.size()) {
                                showMessage("Business created successfully");
                                Close();
                            } else
                                count++;
                        }

                        @Override
                        public final void onError(Throwable e) {
                            count++;
                        }

                        @Override
                        public final void onNext(CreateResponse response) {
                        }
                    });
        }
    }

    @Override
    public void onError(String message, Integer code) {
        HideProgress();
        showMessage(message);
    }

}
