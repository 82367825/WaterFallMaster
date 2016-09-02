package com.zero.waterfalllib.util.sp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author linzewu
 * @date 16-9-2
 */
public class SpManager {
    
    private static SpManager sInstance;
    
    private synchronized static SpManager getsInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SpManager(context.getApplicationContext());
        }
        return sInstance;
    }
    
    private SharedPreferences mSharedPreferences;
    private SpManager(Context context) {
        mSharedPreferences = context.getSharedPreferences(SpConstant.SP_NAME, Context.MODE_PRIVATE);
    }
    
}
