package com.eoe.cuixin.coolweather.utli;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by cuixin on 2015/10/25.
 */
public class HttpUtil {
    public static void sendRequestWithUrlconnection(final String address,final HttpCallBackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection=null;
                try {
                    URL url = new URL(address);
                   connection=(HttpURLConnection)url.openConnection();
                    connection.setConnectTimeout(10000);
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(10000);
                    InputStream in=connection.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder respone=new StringBuilder();
                    String line;
                    while ((line=reader.readLine())!=null){
                        respone.append(line);
                    }
                    if(listener!=null){
                        listener.onFinish(respone.toString());
                    }

                }
                catch(Exception e){
                    if(listener!=null)
                        listener.onError(e);
                }

            }
        }).start();
    }
}
