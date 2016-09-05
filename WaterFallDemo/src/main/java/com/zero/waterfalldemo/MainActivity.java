package com.zero.waterfalldemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.zero.waterfalllib.WaterFallManager;
import com.zero.waterfalllib.cache.executor.WFThreadPoolProxy;
import com.zero.waterfallmaster.R;

/**
 * @author linzewu
 * @date 16-9-5
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WFThreadPoolProxy.getInstance().pause();
            }
        });
        
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WFThreadPoolProxy.getInstance().restart();
            }
        });
        
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NumRunnable numRunnable = new NumRunnable();
                numRunnable.index = mIndex;
                WFThreadPoolProxy.getInstance().execute(numRunnable);
                mIndex++;
            }
        });

        WFThreadPoolProxy.getInstance();

    }
    
    private static int mIndex = 0;
    
    public class NumRunnable implements Runnable {
        
        public int index;
        
        @Override
        public void run() {
            for (int i = 0 ; i < 50 ; i++) {
                System.out.println(index);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
