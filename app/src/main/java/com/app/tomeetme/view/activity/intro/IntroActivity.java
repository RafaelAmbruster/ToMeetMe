package com.app.tomeetme.view.activity.intro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.app.tomeetme.R;
import com.app.tomeetme.helper.manager.PreferenceManager;
import com.app.tomeetme.view.activity.ActivityMain;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.SlideFragmentBuilder;

public class IntroActivity extends MaterialIntroActivity {
    PreferenceManager prefManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefManager = new PreferenceManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        enableLastSlideAlphaExitTransition(true);

        getNextButtonTranslationWrapper()
                .setEnterTranslation((view, percentage) -> view.setAlpha(percentage));

        addSlide(new IntroSlideOne());
        addSlide(new IntroSlideTwo());
        addSlide(new IntroSlideFive());
        addSlide(new IntroSlideSix());
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.bg_screen4)
                .buttonsColor(R.color.first_slide_buttons)
                .title("That's it")
                .description("Would you join us?")
                .build());

    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(this, ActivityMain.class));
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, IntroActivity.class);
    }

    @Override
    public void onFinish() {
        launchHomeScreen();
        super.onFinish();
    }
}