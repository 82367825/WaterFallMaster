package com.zero.waterfalllib.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zero.waterfalllib.R;

/**
 * @author linzewu
 * @date 16/9/10
 */
public class WaterFallHolder extends RecyclerView.ViewHolder {
    
    private ImageView mImageView;
    private TextView mTextView;
    
    
    public WaterFallHolder(View view) {
        super(view);
        this.mImageView = (ImageView) view.findViewById(R.id.image);
        this.mTextView = (TextView) view.findViewById(R.id.text);
    }
    
    public void setImageView(ImageView imageView) {
        this.mImageView = imageView;
    }
    
    public void setTextView(TextView textView) {
        this.mTextView = textView;
    }
    
    public ImageView getImageView() {
        return mImageView;
    }
    
    public TextView getTextView() {
        return mTextView;
    }
    
}
