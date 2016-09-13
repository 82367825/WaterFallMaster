package com.zero.waterfalllib.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

import com.zero.waterfalllib.widget.bean.WaterFallBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author linzewu
 * @date 16/9/10
 */
public class WaterFallView extends BaseWaterFall {
    
    private static final int DEFAULT_COLUMNS_NUM = 2;
    
    private int mColumnsNum = DEFAULT_COLUMNS_NUM;
    
    private List<WaterFallBean> mWaterFallBeenList = new ArrayList<>();
    
    private WaterFallAdapter mWaterFallAdapter;
    
    public WaterFallView(Context context) {
        super(context);
        init();
    }

    public WaterFallView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaterFallView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        initRecyclerLayoutManager();
        initRecyclerAdapter();
        initDecoration();
        initAnimation();
    }
    
    public void addData(WaterFallBean waterFallBean) {
        mWaterFallBeenList.add(waterFallBean);
        mWaterFallAdapter.notifyDataSetChanged();
    }
    
    public void addData(WaterFallBean waterFallBean, int position) {
        mWaterFallBeenList.add(position, waterFallBean);
        mWaterFallAdapter.notifyItemChanged(position);
    }
    
    public void remove(WaterFallBean waterFallBean) {
        mWaterFallBeenList.remove(waterFallBean);
        mWaterFallAdapter.notifyDataSetChanged();
    }
    
    public void remove(int position) {
        mWaterFallBeenList.remove(position);
        mWaterFallAdapter.notifyDataSetChanged();
    }
    
    public List<WaterFallBean> getWaterFallBeenList() {
        return this.mWaterFallBeenList;
    }
    
    @Override
    protected void initRecyclerLayoutManager() {
        /* 设置瀑布流布局 */
        setLayoutManager(new StaggeredGridLayoutManager(mColumnsNum,
                StaggeredGridLayoutManager.VERTICAL));
    }

    @Override
    protected void initRecyclerAdapter() {
        /* 初始化适配器 */
        mWaterFallAdapter = new WaterFallAdapter(mWaterFallBeenList);
        setAdapter(mWaterFallAdapter);
    }

    @Override
    protected void initDecoration() {
        /* 设置分割线 */
        addItemDecoration(new WaterFallDecoration(getContext()));
    }

    @Override
    protected void initAnimation() {
        /* 设置动画 */
        setItemAnimator(new DefaultItemAnimator());
    }


}
