package com.zero.waterfalllib.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * @author linzewu
 * @date 16-8-29
 */
public abstract class BaseWaterFall extends RecyclerView {

    public BaseWaterFall(Context context) {
        super(context);
    }

    public BaseWaterFall(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseWaterFall(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected abstract void initRecyclerLayoutManager();

    protected abstract void initRecyclerAdapter();
    
    protected abstract void initDecoration();

    protected abstract void initAnimation();
}
