package com.app.tomeetme.view.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.app.tomeetme.R;
import com.app.tomeetme.model.Business;
import com.app.tomeetme.model.User;
import com.app.tomeetme.rest.ResponseObjectCallBack;
import com.app.tomeetme.rest.task.business.BusinessTask;
import com.app.tomeetme.view.activity.ActivityAddBusiness;
import com.app.tomeetme.view.activity.ActivityBusinessDetails;
import com.app.tomeetme.view.adapter.BusinessAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by Ambruster on 29/06/2016.
 */

public class ProfileCarFragment extends BaseFragment implements View.OnClickListener, ResponseObjectCallBack {

    private BusinessAdapter adapter;
    private ArrayList<Business> businesses;
    private static final String EXTRA_OBJCT_PROFILE = "USER";
    private User user;
    private RecyclerView recycler;
    private ProgressBar progressbar;
    private RelativeLayout ryt_empty, ryt_error;
    private Button action_retry, error_retry;
    private View parent_view;
    private static View view;
    private FloatingActionButton fab;

    public static ProfileCarFragment newInstance(User us) {
        ProfileCarFragment fragment = new ProfileCarFragment();
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

    protected void Load() {
        getActivity().invalidateOptionsMenu();
        progressbar = (ProgressBar) parent_view.findViewById(R.id.progressbar);
        ryt_empty = (RelativeLayout) parent_view.findViewById(R.id.layout_empty);
        ryt_error = (RelativeLayout) parent_view.findViewById(R.id.layout_error);
        error_retry = (Button) parent_view.findViewById(R.id.action_error_retry);
        error_retry.setOnClickListener(this);

        fab = (FloatingActionButton) parent_view.findViewById(R.id.fab);

        if (user != null) {
            if (!user.isOwner())
                fab.setVisibility(View.GONE);
        }

        fab.setFocusable(false);
        fab.setFocusableInTouchMode(false);

        fab.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), ActivityAddBusiness.class);
            Bundle b = new Bundle();
            Gson gSon = new Gson();
            b.putString(ActivityAddBusiness.EXTRA_OBJCT_USER, gSon.toJson(user));
            intent.putExtras(b);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityOptions options = ActivityOptions.makeScaleUpAnimation(view1, 0, 0, view1.getWidth(), view1.getHeight());
                startActivity(intent, options.toBundle());
            } else {
                startActivity(intent);
            }
        });

        recycler = (RecyclerView) parent_view.findViewById(R.id.recyclerView);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        businesses = new ArrayList<>();
        adapter = new BusinessAdapter(getActivity(), recycler, new BusinessAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                int pos = adapter.getPosition(position);
                Business business = businesses.get(pos);
                view = v;
                progressbar.setVisibility(View.VISIBLE);
                OpenDetail(business);

            }
        }, true);
        recycler.setAdapter(adapter);
        Execute();
    }

    private void OpenDetail(Business business) {
        new BusinessTask((ResponseObjectCallBack) this).CallService(2, business.getId(), null, null);
    }

    @Override
    public void onResponseObjectCallBack(Object object) {
        progressbar.setVisibility(View.GONE);
        startBusinessDetail((Business) object);
    }

    @Override
    public void onError(String message, Integer code) {
        progressbar.setVisibility(View.GONE);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parent_view = inflater.inflate(R.layout.activity_account_list, container, false);
        setHasOptionsMenu(true);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.containsKey(EXTRA_OBJCT_PROFILE)) {
                Gson gSon = new Gson();
                user = gSon.fromJson(bundle.getString(EXTRA_OBJCT_PROFILE), new TypeToken<User>() {
                }.getType());
            }
        }

        getActivity().invalidateOptionsMenu();
        Load();
        return parent_view;
    }

    protected void Execute() {
        ryt_error.setVisibility(View.GONE);
        progressbar.setVisibility(View.GONE);
        try {
            if (user.getBusinesses() != null) {
                runTaskCallback(new Runnable() {
                    public void run() {
                        businesses.addAll(user.getBusinesses());
                        adapter.AddItems(businesses);
                        adapter.setLoaded();

                        if (businesses.size() > 0) {
                            ryt_empty.setVisibility(View.GONE);
                            recycler.setVisibility(View.VISIBLE);
                        } else {
                            recycler.setVisibility(View.GONE);
                            ryt_empty.setVisibility(View.VISIBLE);
                        }
                    }
                });
            } else {
                recycler.setVisibility(View.GONE);
                ryt_empty.setVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.action_retry) {
            Execute();
        } else if (view.getId() == R.id.action_error_retry) {
            Execute();
        }
    }
}
