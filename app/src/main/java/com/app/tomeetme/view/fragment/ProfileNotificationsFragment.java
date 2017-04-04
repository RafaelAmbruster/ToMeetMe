package com.app.tomeetme.view.fragment;

import android.os.Bundle;
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
import com.app.tomeetme.model.User;
import com.app.tomeetme.model.Notification;
import com.app.tomeetme.view.adapter.NotificationAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by Ambruster on 29/06/2016.
 */

public class ProfileNotificationsFragment extends BaseFragment implements View.OnClickListener{

    private NotificationAdapter adapter;
    private ArrayList<Notification> notifications;
    private static final String EXTRA_OBJCT_PROFILE = "USER";
    private User user;
    private RecyclerView recycler;
    private ProgressBar progressbar;
    private RelativeLayout ryt_empty, ryt_error;
    private Button action_retry, error_retry;
    private View parent_view;

    public static ProfileNotificationsFragment newInstance(User us) {
        ProfileNotificationsFragment fragment = new ProfileNotificationsFragment();
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

        recycler = (RecyclerView) parent_view.findViewById(R.id.recyclerView);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(getActivity(), recycler, new NotificationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                int pos = adapter.getPosition(position);

            }
        });
        recycler.setAdapter(adapter);
        Execute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parent_view = inflater.inflate(R.layout.activity_account_notifications, container, false);
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
            if (user.getNotifications() != null) {
                runTaskCallback(new Runnable() {
                    public void run() {
                        notifications.addAll(user.getNotifications());
                        adapter.AddItems(notifications);
                        adapter.setLoaded();

                        if (notifications.size() > 0) {
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
