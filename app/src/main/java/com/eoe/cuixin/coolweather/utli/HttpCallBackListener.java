package com.eoe.cuixin.coolweather.utli;

/**
 * Created by cuixin on 2015/10/25.
 */
public interface HttpCallBackListener {
    public void onFinish(String respone);
    public void onError(Exception e);
}
