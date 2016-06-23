package com.iminer.memoryinfodemo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.iminer.memoryinfo.MemoryInfoService;

/**
 * Created by dell on 2016/6/23.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if(getSharedPreferences("sp",Context.MODE_PRIVATE).getBoolean("showMemory",false)){
            startService(new Intent(this, MemoryInfoService.class));
            getSharedPreferences("sp",Context.MODE_PRIVATE).edit().putBoolean("showMemory",true).apply();
        }
    }
}
