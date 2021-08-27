/**
 * Created by Vimal on June-2021.
 */
package com.vimal.cusromgallerywithcropper.adapter;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.vimal.cusromgallerywithcropper.R;
import com.vimal.cusromgallerywithcropper.model.GalleryImage;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


public class CropAdapter extends RecyclerView.Adapter<CropAdapter.MyView> {

    Context context;
    private List<GalleryImage> list;
    OnShareClickedListener mCallback;

    public class MyView extends RecyclerView.ViewHolder {


        LinearLayout mainlay;
        ImageView image;


        public MyView(View view) {
            super(view);
            mainlay = view.findViewById(R.id.mainlay);
            image = view.findViewById(R.id.image);
        }
    }


    public void setOnShareClickedListener(OnShareClickedListener mCallback) {
        this.mCallback = mCallback;
    }

    public interface OnShareClickedListener {
        public void ShareClicked(int pos);
    }

    public CropAdapter(Context context, List<GalleryImage> horizontalList) {
        this.context = context;
        this.list = horizontalList;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_crop, parent, false);
        return new MyView(itemView);
    }


    @Override
    public void onBindViewHolder(final MyView holder, final int position) {

        if (list.get(position).getAutocrop()) {
            Glide.with(context).load(list.get(position).getImagepath())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).placeholder(R.mipmap.ic_launcher).into(holder.image);
        } else {
            Glide.with(context).load(list.get(position).getImagepath())
                    .apply(bitmapTransform(new BlurTransformation(5)))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.mipmap.ic_launcher).into(holder.image);
        }


        holder.mainlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCallback.ShareClicked(position);

            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
