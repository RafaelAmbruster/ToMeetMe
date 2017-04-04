package com.app.tomeetme.view.adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.tomeetme.helper.util.FontTypefaceUtils;
import com.app.tomeetme.R;
import com.app.tomeetme.data.AppDatabaseManager;
import com.app.tomeetme.data.dao.UserDAO;
import com.app.tomeetme.model.BusinessCoupon;
import com.app.tomeetme.model.User;
import com.app.tomeetme.view.tagview.TagView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ambruster on 20/07/2016.
 */

public class CouponAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    public static OnItemClickListener mListener;
    public OnLoadMoreListener mOnLoadMoreListener;
    public boolean isLoading;
    private ItemFilter mFilter = new ItemFilter();
    private Activity ctx;
    private List<BusinessCoupon> original_items = new ArrayList<>();
    private List<BusinessCoupon> filtered_items = new ArrayList<>();
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private boolean mAnimationEnabled = true;
    private int mAnimationPosition = -1;

    public CouponAdapter(Activity activity, RecyclerView recyclerView, OnItemClickListener onItemClickListener) {

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

    public void AddItems(List<BusinessCoupon> items) {
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
            View view = LayoutInflater.from(ctx).inflate(R.layout.row_coupon, parent, false);
            return new CouponViewHolder(view, mListener);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(ctx).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CouponViewHolder) {
            CouponViewHolder viewHolder = (CouponViewHolder) holder;
            viewHolder.bind(filtered_items.get(position), position);
        } else {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

        //setAnimation(holder.itemView, position);
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

    public class CouponViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView text_name, text_content, text_date, text_date_end, text_address;
        public TagView  text_discount, text_active;
        public ImageView photo_content;
        public CardView cv_parent;
        private OnItemClickListener mListener;

        public CouponViewHolder(View v, OnItemClickListener listener) {
            super(v);
            mListener = listener;
            itemView.setOnClickListener(this);
            text_name = (TextView) v.findViewById(R.id.text_name);
            text_name.setTypeface(FontTypefaceUtils.getRobotoCondensedBold(ctx));
            text_date = (TextView) v.findViewById(R.id.text_date);
            text_date.setTypeface(FontTypefaceUtils.getRobotoCondensedLight(ctx));
            text_date_end = (TextView) v.findViewById(R.id.text_date_end);
            text_date_end.setTypeface(FontTypefaceUtils.getRobotoCondensedLight(ctx));
            text_address = (TextView) v.findViewById(R.id.text_address);
            text_address.setTypeface(FontTypefaceUtils.getRobotoCondensedRegular(ctx));
            text_discount = (TagView) itemView.findViewById(R.id.text_discount);
            text_active = (TagView) itemView.findViewById(R.id.text_active);
            text_content = (TextView) v.findViewById(R.id.text_content);
            cv_parent = (CardView) v.findViewById(R.id.cv_parent);
            photo_content = (ImageView) v.findViewById(R.id.photo_content);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(getAdapterPosition(), v);
        }

        public void bind(final BusinessCoupon c, final int position) {

            DateTime dateTime = new DateTime(c.getStartDate(), DateTimeZone.UTC);
            String formattedDate = DateUtils.formatDateTime(ctx, dateTime.toDate().getTime(),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR);
            text_date.setText(formattedDate.toUpperCase(Locale.getDefault()));

            DateTime dateTimeEnd = new DateTime(c.getEndDate(), DateTimeZone.UTC);
            String formattedDateEnd = DateUtils.formatDateTime(ctx, dateTimeEnd.toDate().getTime(),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR);
            text_date_end.setText(formattedDateEnd.toUpperCase(Locale.getDefault()));

            /*if (c.getImagePath() != null) {
                photo_content.setVisibility(View.VISIBLE);
                String URL = c.getImagePath();
                int size = (int) Math.ceil(Math.sqrt(BitmapTransform.MAX_WIDTH * BitmapTransform.MAX_HEIGHT));
                Picasso.with(ctx).load(URL).resize(size, size).transform(new BitmapTransform(BitmapTransform.MAX_WIDTH, BitmapTransform.MAX_HEIGHT)).centerInside().into(photo_content);
            } else {
                photo_content.setVisibility(View.GONE);
            }*/

            photo_content.setVisibility(View.GONE);

            if (c.getDescription() != null) {
                text_content.setText(c.getDescription());
                text_content.setVisibility(View.VISIBLE);
            }

            text_name.setText(c.getPlaceOrBusiness());

            if (c.getDiscount() > 0) {
                text_discount.setText(c.getDiscount() + (c.getInPercent() ? "%" : ""));
                text_discount.setVisibility(View.VISIBLE);
            } else {
                text_discount.setVisibility(View.GONE);
            }

            if (!c.getActive()) {
                text_active.setText("Inactive");
                text_active.setTagColor(0xFFFF0000);
            } else {
                text_active.setText("Active");
                text_active.setTagColor(0xFFFFBB33);
            }

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
            final List<BusinessCoupon> list = original_items;
            final List<BusinessCoupon> result_list = new ArrayList<>(list.size());

            for (int i = 0; i < list.size(); i++) {

                String str_2 = list.get(i).getDescription();
                String str_3 = String.valueOf(list.get(i).getPlaceOrBusiness());

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
            filtered_items = (List<BusinessCoupon>) results.values;
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
