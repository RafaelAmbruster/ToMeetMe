package com.app.tomeetme.view.activity.intro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.app.tomeetme.R;

import agency.tango.materialintroscreen.SlideFragment;


public class IntroSlideFour extends SlideFragment {
    private CheckBox checkBox;
    private TextView text;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.intro_slide4, container, false);

        //text = (TextView) view.findViewById(R.id.text);
        //text.setTypeface(FontTypefaceUtils.getRobotoCondensedLight(getActivity()));

        return view;
    }

    @Override
    public int backgroundColor() {
        return R.color.white80;
    }

    @Override
    public int buttonsColor() {
        return R.color.colorAccent;
    }

    @Override
    public boolean canMoveFurther() {
        return true;
                //checkBox.isChecked();
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return getString(R.string.error_message);
    }

}