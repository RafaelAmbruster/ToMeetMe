package com.app.tomeetme.view.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
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

import com.app.tomeetme.R;
import com.app.tomeetme.helper.util.BitmapTransform;
import com.app.tomeetme.helper.util.FontTypefaceUtils;
import com.app.tomeetme.helper.util.LocationUtility;
import com.app.tomeetme.model.Business;
import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ambruster on 20/07/2016.
 */

public class BusinessAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    public static OnItemClickListener mListener;
    public OnLoadMoreListener mOnLoadMoreListener;
    public boolean isLoading;
    private ItemFilter mFilter = new ItemFilter();
    private Activity ctx;
    private List<Business> original_items = new ArrayList<>();
    private List<Business> filtered_items = new ArrayList<>();
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private boolean mAnimationEnabled = true;
    private int mAnimationPosition = -1;
    private Boolean isOwner;

    public BusinessAdapter(Activity activity, RecyclerView recyclerView, OnItemClickListener onItemClickListener, boolean isOwner) {

        ctx = activity;
        original_items = new ArrayList<>();
        filtered_items = new ArrayList<>();
        mListener = onItemClickListener;
        this.isOwner = isOwner;

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

    public void addData(Business item) {
        original_items.add(item);
        filtered_items.add(item);
        notifyDataSetChanged();
    }

    public void AddItems(List<Business> items) {
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
            View view = LayoutInflater.from(ctx).inflate(R.layout.row_business, parent, false);
            return new BusinessViewHolder(view, mListener);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(ctx).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BusinessViewHolder) {
            BusinessViewHolder viewHolder = (BusinessViewHolder) holder;
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

    public int getPosition(int recyclerPosition) {
        return recyclerPosition;
    }

    public void setLoaded() {
        isLoading = false;
    }

    public Filter getFilter() {
        return mFilter;
    }

    public final class BusinessViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView text_name, text_content, text_distance, text_header;
        private ImageView bt_maps, photo_content, photo_category, bt_add_coupon;
        private OnItemClickListener mListener;
        public View container;
        private CardView cv_parent;
        private MaterialRippleLayout owner, goToAddress;

        public BusinessViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            mListener = listener;
            itemView.setOnClickListener(this);
            container = itemView;
            text_header = (TextView) itemView.findViewById(R.id.text_header);
            text_name = (TextView) itemView.findViewById(R.id.text_name);
            text_name.setTypeface(FontTypefaceUtils.getRobotoCondensedBold(ctx));
            text_content = (TextView) itemView.findViewById(R.id.text_content);
            text_content.setTypeface(FontTypefaceUtils.getRobotoCondensedRegular(ctx));
            photo_content = (ImageView) itemView.findViewById(R.id.photo_content);
            photo_category = (ImageView) itemView.findViewById(R.id.category);
            cv_parent = (CardView) itemView.findViewById(R.id.cv_parent);


        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(getAdapterPosition(), v);
        }

        public void bind(final Business c, final int position) {

            if (c.getBusinessPictures() != null) {
                if (c.getBusinessPictures().size() > 0) {
                    photo_content.setVisibility(View.VISIBLE);
                    String URL = c.getBusinessPictures().get(0).getImagePath();

                    int size = (int) Math.ceil(Math.sqrt(BitmapTransform.MAX_WIDTH * BitmapTransform.MAX_HEIGHT));
                    Picasso.with(ctx).load(URL).resize(size, size).transform(new BitmapTransform(BitmapTransform.MAX_WIDTH, BitmapTransform.MAX_HEIGHT)).centerInside().into(photo_content);
                } else {
                    photo_content.setVisibility(View.GONE);
                }
            }

            if (c.getDescription() != null) {
                text_content.setText(c.getDescription());
                text_content.setVisibility(View.VISIBLE);
            }

            /*if (c.getCarBrand() != null)
                text_header.setText(c.getCarBrand().getName());*/

            text_name.setText(c.getTitle());

            cv_parent.setOnClickListener(v -> mListener.onItemClick(position, v));

            if(isOwner) {
                owner.setVisibility(View.VISIBLE);
                goToAddress.setVisibility(View.GONE);
            }

            /*try {
                if (c.getCarBrand().getId().equals("a8b2b662700a4de0be314a7aa6531059")) {
                    photo_category.setImageResource(R.drawable.ic_auto);
                } else if (c.getCarBrand().getId().equals("901591a0eb164e74a63f3f0d3524b41d")) {
                    photo_category.setImageResource(R.drawable.ic_business);
                } else if (c.getCarBrand().getId().equals("78ccddc0b67443dfa3846844eb6f9023")) {
                    photo_category.setImageResource(R.drawable.ic_computer);
                } else if (c.getCarBrand().getId().equals("ed67db932cd64d3fbc0fd0eb8abbc411")) {
                    photo_category.setImageResource(R.drawable.ic_construction);
                } else if (c.getCarBrand().getId().equals("66a9de88152b41ea9105494dcbbc04bf")) {
                    photo_category.setImageResource(R.drawable.ic_education);
                } else if (c.getCarBrand().getId().equals("d8b6a943a3f04cafb5c0e4ffae8bc516")) {
                    photo_category.setImageResource(R.drawable.ic_entertainment);
                } else if (c.getCarBrand().getId().equals("824082f89e454998870afa9f2cf168f3")) {
                    photo_category.setImageResource(R.drawable.ic_food);
                } else if (c.getCarBrand().getId().equals("6290cf040faf4d0d9b5250864ef10c73")) {
                    photo_category.setImageResource(R.drawable.ic_health);
                } else if (c.getCarBrand().getId().equals("9e44d4ddbe354819b4580007b2114425")) {
                    photo_category.setImageResource(R.drawable.ic_home);
                } else if (c.getCarBrand().getId().equals("346abece27cf4081bd363beb39c79d58")) {
                    photo_category.setImageResource(R.drawable.ic_legal);
                } else if (c.getCarBrand().getId().equals("fc59a27c5ab24212a5e877cf3520f2cf")) {
                    photo_category.setImageResource(R.drawable.ic_manufacturing);
                } else if (c.getCarBrand().getId().equals("bcfec3e2eaef463abcbf7564315abd0b")) {
                    photo_category.setImageResource(R.drawable.ic_merchants);
                } else if (c.getCarBrand().getId().equals("be4ec18b93dc4aec959d8580b8c9947f")) {
                    photo_category.setImageResource(R.drawable.ic_miscellaneous);
                } else if (c.getCarBrand().getId().equals("94b3c7151783465ea3ec572913ada7ff")) {
                    photo_category.setImageResource(R.drawable.ic_personal_care);
                } else if (c.getCarBrand().getId().equals("b38e0253edca4153b59ec752088f5f2b")) {
                    photo_category.setImageResource(R.drawable.ic_realstate);
                } else if (c.getCarBrand().getId().equals("f43ee0abae1a46bbbd8dd0d23d229a0f")) {
                    photo_category.setImageResource(R.drawable.ic_travel);
                }
            } catch (Exception ex) {
            }*/
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View v);
    }

    public static final class LoadingViewHolder extends RecyclerView.ViewHolder {
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
            final List<Business> list = original_items;
            final List<Business> result_list = new ArrayList<>(list.size());

            for (int i = 0; i < list.size(); i++) {
                String str_2 = list.get(i).getDescription();
                String str_3 = String.valueOf(list.get(i).getTitle());
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
            filtered_items = (List<Business>) results.values;
            notifyDataSetChanged();
        }
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
}
