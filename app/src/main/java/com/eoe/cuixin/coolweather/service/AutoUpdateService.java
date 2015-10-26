package com.eoe.cuixin.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.eoe.cuixin.coolweather.receiver.AutoUpdateReceiver;
import com.eoe.cuixin.coolweather.utli.HttpCallBackListener;
import com.eoe.cuixin.coolweather.utli.HttpUtil;
import com.eoe.cuixin.coolweather.utli.Utility;

/**
 * Created by cuixin on 2015/10/26.
 */
public class AutoUpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager manager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        int circle=8*60*69*1000;
        long trigerTime= SystemClock.elapsedRealtime()+circle;
        Intent intent1=new Intent(AutoUpdateService.this, AutoUpdateReceiver.class);
        PendingIntent pi=PendingIntent.getBroadcast(this,0,intent1,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,trigerTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }
    private void updateWeather(){
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode=preferences.getString("weather_code", "");
        String address = "http://www.weather.com.cn/data/cityinfo/" +
                weatherCode + ".html";
        HttpUtil.sendRequestWithUrlconnection(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String respone) {
                Utility.handleWeatherRespone(AutoUpdateService.this,respone);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });

    }
}
