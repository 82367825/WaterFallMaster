package com.zero.waterfalllib.widget;

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
    public void onBindViewHolder(WaterFallHolder holder, int position) {
        ImageLoader.getInstance().loadBitmap(
                mWaterFallBeenList.get(position).getUrl(),
                holder.getImageView(), R.mipmap.ic_launcher
                );
        
    }

    @Override
    public int getItemCount() {
        return mWaterFallBeenList.size();
    }
    
    public void addData(WaterFallBean data, int position) {
        mWaterFallBeenList.add(position, data);
        notifyItemChanged(position);
    }
    
    public void removeData(int position) {
        mWaterFallBeenList.remove(position);
        notifyItemChanged(position);
    }
}
