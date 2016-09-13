package com.zero.waterfalldemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.zero.waterfalllib.WaterFallManager;
import com.zero.waterfalllib.cache.executor.WFThreadPoolProxy;
import com.zero.waterfalllib.widget.WaterFallView;
import com.zero.waterfalllib.widget.bean.WaterFallBean;
import com.zero.waterfallmaster.R;

/**
 * @author linzewu
 * @date 16-9-5
 */
public class MainActivity extends Activity {

    private WaterFallView mWaterFallView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WaterFallManager.getInstance().init(this);
        mWaterFallView = (WaterFallView) findViewById(R.id.waterfall);
        refreshData();
    }
    
    private void refreshData() {
       for (int i = 0 ; i < urls.length ; i++) {
           WaterFallBean waterFallBean = new WaterFallBean(urls[i], "", "");
           mWaterFallView.addData(waterFallBean);
       }
    }
    
    
    private String[] urls = {
         "www.baidu.com"   
    };
}
