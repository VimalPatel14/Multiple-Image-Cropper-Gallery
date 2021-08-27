package com.vimal.cusromgallerywithcropper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.vimal.cusromgallerywithcropper.R;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ThumbnailsViewHolder> {

    Context context;
    private List<ThumbnailItem> thumbnailsList;

    public ThumbnailAdapter(Context context, List<ThumbnailItem> thumbnailsList) {
        this.context = context;
        this.thumbnailsList = thumbnailsList;
    }

    @NonNull
    @Override
    public ThumbnailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.thumbnails_list_item, parent, false);
        return new ThumbnailsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbnailsViewHolder holder, int position) {
        Glide.with(context).load(thumbnailsList.get(position).image)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.mipmap.ic_launcher).into(holder.thumbNail);
    }

    @Override
    public int getItemCount() {
        return thumbnailsList.size();
    }

    public class ThumbnailsViewHolder extends RecyclerView.ViewHolder {

        private ImageView thumbNail;

        public ThumbnailsViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbNail = itemView.findViewById(R.id.thumbnailImage);
        }
    }

    public void updatepath(String path) {
//        imagepath = path;
//        notifyDataSetChanged();
    }
}