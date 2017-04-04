package com.app.tomeetme.view.activity.createBusiness;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.app.tomeetme.R;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

public class CreateBusinessAdapter extends AbstractFragmentStepAdapter {

    StepViewModel step;
    StepViewModel.Builder builder;

    public CreateBusinessAdapter(@NonNull FragmentManager fm, @NonNull Context context) {
        super(fm, context);
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(@IntRange(from = 0) int position) {
        builder = new StepViewModel.Builder(context);
        step = builder
                .create();

        return step;
    }

    @Override
    public Step createStep(int position) {
        switch (position) {
            case 0:
                return CreateBusinessFirstStepFragment.newInstance(R.layout.activity_add_business);
            case 1:
                return CreateBusinessSecondStepFragment.newInstance(R.layout.activity_add_business_step_second);
            case 2:
                return CreateBusinessFinalStepFragment.newInstance(R.layout.activity_add_business_step_final);
            default:
                throw new IllegalArgumentException("Unsupported position: " + position);
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}