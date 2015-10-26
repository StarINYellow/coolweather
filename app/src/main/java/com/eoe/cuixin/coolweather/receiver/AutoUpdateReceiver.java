package com.eoe.cuixin.coolweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.eoe.cuixin.coolweather.service.AutoUpdateService;

/**
 * Created by cuixin on 2015/10/26.
 */
public class AutoUpdateReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i=new Intent(context, AutoUpdateService.class);
        context.startActivity(i);
    }
}
