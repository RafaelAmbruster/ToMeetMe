package com.app.tomeetme.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.tomeetme.R;
import com.app.tomeetme.helper.util.FontTypefaceUtils;
import com.app.tomeetme.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by Ambruster on 29/06/2016.
 */
public class ProfileInformationFragment extends Fragment {

    private static final String EXTRA_OBJCT_PROFILE = "USER";
    public User user;

    public static ProfileInformationFragment newInstance(User us) {
        ProfileInformationFragment fragment = new ProfileInformationFragment();
        Bundle args = new Bundle();
        Gson gSon = new Gson();
        args.putString(EXTRA_OBJCT_PROFILE, gSon.toJson(us));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        getActivity().invalidateOptionsMenu();
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_account_information, container, false);
        setHasOptionsMenu(true);

        TextView phone = (TextView) view.findViewById(R.id.fragment_user_detail_info_phone);
        phone.setTypeface(FontTypefaceUtils.getRobotoCondensedLight(getActivity()));
        TextView address = (TextView) view.findViewById(R.id.fragment_user_detail_info_address);
        address.setTypeface(FontTypefaceUtils.getRobotoCondensedLight(getActivity()));

        getActivity().invalidateOptionsMenu();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.containsKey(EXTRA_OBJCT_PROFILE)) {
                Gson gSon = new Gson();
                user = gSon.fromJson(bundle.getString(EXTRA_OBJCT_PROFILE), new TypeToken<User>() {
                }.getType());
            }
        }

        if (user != null) {
            phone.setText(String.valueOf(user.getPhoneNumber() == null ? "-" : user.getPhoneNumber()));
            address.setText(String.valueOf(user.getFormattedAddress() == null ? "-" : user.getFormattedAddress()));

        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
