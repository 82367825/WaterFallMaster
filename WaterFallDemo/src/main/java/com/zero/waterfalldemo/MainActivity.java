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
        "http://img.taopic.com/uploads/allimg/130710/267873-130G011000550.jpg",
            "http://img5.imgtn.bdimg.com/it/u=1104713303,1290055170&fm=21&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=761080875,1194258054&fm=21&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=2614807623,3042537006&fm=21&gp=0.jpg",
            "http://img0.imgtn.bdimg.com/it/u=1906189186,1400341202&fm=21&gp=0.jpg",
            "http://img0.imgtn.bdimg.com/it/u=1953294160,3802419744&fm=11&gp=0.jpg"
    };
}
