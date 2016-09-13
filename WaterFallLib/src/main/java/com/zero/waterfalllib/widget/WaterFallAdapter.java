package com.zero.waterfalllib.widget;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zero.waterfalllib.R;
import com.zero.waterfalllib.cache.ImageLoader;
import com.zero.waterfalllib.widget.bean.WaterFallBean;

import java.util.List;

/**
 * @author linzewu
 * @date 16/9/10
 */
public class WaterFallAdapter extends RecyclerView.Adapter<WaterFallHolder> {

    private List<WaterFallBean> mWaterFallBeenList;
    
    public WaterFallAdapter(List<WaterFallBean> waterFallBeenList) {
        if (waterFallBeenList == null) {
            throw new IllegalArgumentException("WaterFallBean List can not be null");
        }
        this.mWaterFallBeenList = waterFallBeenList;
    }
    
    @Override
    public WaterFallHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_waterfall, null);
        return new WaterFallHolder(view);
    }

    @Override
    public void onBindViewHolder(final WaterFallHolder holder, int position) {
        //default image
        holder.getImageView().setImageResource(R.mipmap.ic_launcher);
        holder.getImageView().setTag(mWaterFallBeenList.get(position).getUrl());
        ImageLoader.getInstance().loadBitmapWithWidth(mWaterFallBeenList.get(position).getUrl(), 
                300, new ImageLoader.ImageLoadListener() {
            @Override
            public void onSuccess(String url, Bitmap bitmap) {
                String tag = (String) holder.getImageView().getTag();
                if (url.equals(tag)) {
                    holder.getImageView().setImageBitmap(bitmap);
                }
            }

            @Override
            public void onFail(String url) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mWaterFallBeenList.size();
    }
}
