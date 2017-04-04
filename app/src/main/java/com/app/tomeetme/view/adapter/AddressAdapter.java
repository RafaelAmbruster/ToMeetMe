package com.app.tomeetme.view.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.tomeetme.R;
import com.app.tomeetme.data.AppDatabaseManager;
import com.app.tomeetme.data.dao.UserDAO;
import com.app.tomeetme.helper.log.LogManager;
import com.app.tomeetme.helper.util.BitmapTransform;
import com.app.tomeetme.helper.util.FontTypefaceUtils;
import com.app.tomeetme.helper.util.LocationUtility;
import com.app.tomeetme.model.Business;
import com.app.tomeetme.model.BusinessAddress;
import com.app.tomeetme.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ambruster on 20/07/2016.
 */

public class AddressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    public static OnItemClickListener mListener;
    public OnLoadMoreListener mOnLoadMoreListener;
    public boolean isLoading;
    private ItemFilter mFilter = new ItemFilter();
    private Activity ctx;
    private List<BusinessAddress> original_items = new ArrayList<>();
    private List<BusinessAddress> filtered_items = new ArrayList<>();
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private boolean mAnimationEnabled = true;
    private int mAnimationPosition = -1;
    private ImageView imageView;
    private ViewGroup wrapViewGroup;
    private int size = (int) Math.ceil(Math.sqrt(BitmapTransform.MAX_WIDTH * BitmapTransform.MAX_HEIGHT));
    private static final int MAP_ZOOM = 14;

    public AddressAdapter(Activity activity, RecyclerView recyclerView, OnItemClickListener onItemClickListener) {

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

    public void AddItems(List<BusinessAddress> items) {
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
            View view = LayoutInflater.from(ctx).inflate(R.layout.row_address, parent, false);
            return new AddressViewHolder(view, mListener);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(ctx).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AddressViewHolder) {
            AddressViewHolder viewHolder = (AddressViewHolder) holder;
            viewHolder.bind(filtered_items.get(position), position);
        } else {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

        setAnimation(holder.itemView, position);
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

    public class AddressViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txt_business_address, txt_business_distance;
        public CardView cv_parent;
        public Button btn_address;
        private OnItemClickListener mListener;

        public AddressViewHolder(View v, OnItemClickListener listener) {
            super(v);
            mListener = listener;
            itemView.setOnClickListener(this);

            txt_business_address = (TextView) v.findViewById(R.id.txt_business_address);
            txt_business_address.setTypeface(FontTypefaceUtils.getRobotoCondensedLight(ctx));

            txt_business_distance = (TextView) v.findViewById(R.id.txt_business_distance);
            txt_business_distance.setTypeface(FontTypefaceUtils.getRobotoCondensedRegular(ctx));

            imageView = (ImageView) v.findViewById(R.id.fragment_poi_detail_map_image);
            wrapViewGroup = (ViewGroup) v.findViewById(R.id.fragment_poi_detail_map_image_wrap);

            cv_parent = (CardView) v.findViewById(R.id.cv_parent);

            btn_address  = (Button) v.findViewById(R.id.btn_address);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(getAdapterPosition(), v);
        }

        public void bind(final BusinessAddress c, final int position) {
            txt_business_address.setText(c.getAddress());

            if (c.getDistance() > 0) {
                String distance = LocationUtility.getDistanceString(c.getDistance(), LocationUtility.isMetricSystem());
                txt_business_distance.setText(distance);
                txt_business_distance.setVisibility(View.VISIBLE);
            } else {
                txt_business_distance.setVisibility(View.GONE);
            }

            String key = ctx.getString(R.string.google_maps_key);

            Double lat = Double.parseDouble(c.getLatitude());
            Double lon = Double.parseDouble(c.getLongitude());

            String url = getStaticMapUrl(key, lat, lon, MAP_ZOOM);

            Picasso.with(ctx)
                    .load(url).resize(size, size)
                    .placeholder(R.drawable.placeholder_map)
                    .error(R.drawable.placeholder_map)
                    .transform(new BitmapTransform(BitmapTransform.MAX_WIDTH, BitmapTransform.MAX_HEIGHT)).
                    centerInside().into(imageView);

            wrapViewGroup.setOnClickListener(v -> {
                        Handler mainHandler = new Handler(ctx.getMainLooper());
                        Runnable myRunnable = () -> startNavigateActivity(Double.parseDouble(c.getLatitude()), Double.parseDouble(c.getLongitude()));
                        mainHandler.post(myRunnable);
                    }
            );

            btn_address.setOnClickListener(view -> {
                        Handler mainHandler = new Handler(ctx.getMainLooper());
                        Runnable myRunnable = () -> startNavigateActivity(Double.parseDouble(c.getLatitude()), Double.parseDouble(c.getLongitude()));
                        mainHandler.post(myRunnable);
                    }
            );

        }
    }

    private void startNavigateActivity(double lat, double lon) {
        try {
            String uri = String.format("http://maps.google.com/maps?daddr=%s,%s", Double.toString(lat), Double.toString(lon));
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
            ctx.startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
        }
    }

    private String getStaticMapUrl(String key, double lat, double lon, int zoom) {
        TypedValue typedValue = new TypedValue();
        ctx.getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
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
            final List<BusinessAddress> list = original_items;
            final List<BusinessAddress> result_list = new ArrayList<>(list.size());

            for (int i = 0; i < list.size(); i++) {

                String str_2 = list.get(i).getAddress();
                String str_3 = String.valueOf(list.get(i).getBusiness());

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
            filtered_items = (List<BusinessAddress>) results.values;
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
