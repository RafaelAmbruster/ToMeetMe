package com.app.tomeetme.view.adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.app.tomeetme.R;
import com.app.tomeetme.data.AppDatabaseManager;
import com.app.tomeetme.data.dao.UserDAO;
import com.app.tomeetme.helper.util.FontTypefaceUtils;
import com.app.tomeetme.model.User;
import com.app.tomeetme.model.Notification;
import com.app.tomeetme.view.tagview.TagView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ambruster on 20/07/2016.
 */

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    public static OnItemClickListener mListener;
    public OnLoadMoreListener mOnLoadMoreListener;
    public boolean isLoading;
    private ItemFilter mFilter = new ItemFilter();
    private Activity ctx;
    private List<Notification> original_items = new ArrayList<>();
    private List<Notification> filtered_items = new ArrayList<>();
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private boolean mAnimationEnabled = true;
    private int mAnimationPosition = -1;

    public NotificationAdapter(Activity activity, RecyclerView recyclerView, OnItemClickListener onItemClickListener) {

        ctx = activity;
        original_items = new ArrayList<>();
        filtered_items = new ArrayList<>();
        mListener = onItemClickListener;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    public int getPosition(int recyclerPosition) {
        return recyclerPosition;
    }

    public void AddItems(List<Notification> items) {
        original_items.clear();
        filtered_items.clear();
        original_items.addAll(items);
        filtered_items.addAll(items);
        notifyDataSetChanged();
    }

    public void clearList() {
        original_items.clear();
        filtered_items.clear();
        notifyDataSetChanged();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return filtered_items.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(ctx).inflate(R.layout.row_notification, parent, false);
            return new NotificationViewHolder(view, mListener);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(ctx).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NotificationViewHolder) {
            NotificationViewHolder viewHolder = (NotificationViewHolder) holder;
            viewHolder.bind(filtered_items.get(position), position);
        } else {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return (null != filtered_items ? filtered_items.size() : 0);
    }

    public void setLoaded() {
        isLoading = false;
    }

    public Filter getFilter() {
        return mFilter;
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tittle, content, date;
        public RatingBar ratingBar;
        public CardView cv_parent;
        public TagView text_readed;
        private OnItemClickListener mListener;

        public NotificationViewHolder(View v, OnItemClickListener listener) {
            super(v);
            mListener = listener;
            itemView.setOnClickListener(this);

            text_readed = (TagView) v.findViewById(R.id.text_readed);


            tittle = (TextView) v.findViewById(R.id.text_tittle);
            tittle.setTypeface(FontTypefaceUtils.getRobotoCondensedBold(ctx));

            date = (TextView) v.findViewById(R.id.text_date);
            date.setTypeface(FontTypefaceUtils.getRobotoCondensedLight(ctx));

            content = (TextView) v.findViewById(R.id.text_content);
            content.setTypeface(FontTypefaceUtils.getRobotoCondensedRegular(ctx));

            cv_parent = (CardView) v.findViewById(R.id.cv_parent);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(getAdapterPosition(), v);
        }

        public void bind(final Notification c, final int position) {

            DateTime dateTime = new DateTime(new Date(), DateTimeZone.UTC);
            String formattedDate = DateUtils.formatDateTime(ctx, dateTime.toDate().getTime(),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR);
            date.setText(formattedDate.toUpperCase(Locale.getDefault()));

            tittle.setText(c.getSubject());
            content.setText(Html.fromHtml(c.getBody()));
            text_readed.setText(c.getRead() ? "Read" : "New");
            cv_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(position, v);
                }
            });
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String query = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();
            final List<Notification> list = original_items;
            final List<Notification> result_list = new ArrayList<>(list.size());

            for (int i = 0; i < list.size(); i++) {

                String str_2 = list.get(i).getBody();
                String str_3 = String.valueOf(list.get(i).getSubject());

                if (str_2.toLowerCase().contains(query) || str_3.toLowerCase().contains(query)) {
                    result_list.add(list.get(i));
                }
            }

            results.values = result_list;
            results.count = result_list.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filtered_items = (List<Notification>) results.values;
            notifyDataSetChanged();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View v);
    }

    private void setAnimation(final View view, int position) {
        if (mAnimationEnabled && position > mAnimationPosition) {
            view.setScaleX(0f);
            view.setScaleY(0f);
            view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(300)
                    .setInterpolator(new DecelerateInterpolator());

            mAnimationPosition = position;
        }
    }

    public User getUser() {
        return new UserDAO(AppDatabaseManager.getInstance().getHelper()).getUser();
    }

}
