package com.eoe.cuixin.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eoe.cuixin.coolweather.R;
import com.eoe.cuixin.coolweather.service.AutoUpdateService;
import com.eoe.cuixin.coolweather.utli.HttpCallBackListener;
import com.eoe.cuixin.coolweather.utli.HttpUtil;
import com.eoe.cuixin.coolweather.utli.Utility;

import org.w3c.dom.Text;

/**
 * Created by cuixin on 2015/10/25.
 */
public class WeatherActivity extends Activity implements View.OnClickListener{
    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    /**
     * 用于显示气温1
     */
    private TextView temp1Text;
    /**
     * 用于显示气温2
     */
    private TextView temp2Text;
    /**
     * 用于显示当前日期
     */
    private TextView currentDateText;


    private Button btnswitchCity;
    private Button btnrefreshWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        weatherInfoLayout=(LinearLayout)findViewById(R.id.weather_info_layout);
        cityNameText=(TextView)findViewById(R.id.city_name);
        publishText=(TextView)findViewById(R.id.publish_text);
        weatherDespText=(TextView)findViewById(R.id.weather_desp);
        temp1Text=(TextView)findViewById(R.id.temp1);
        temp2Text=(TextView)findViewById(R.id.temp2);
        currentDateText=(TextView)findViewById(R.id.current_date);
        btnswitchCity=(Button)findViewById(R.id.switch_city);
        btnrefreshWeather=(Button)findViewById(R.id.refresh_weather);
        String countyCode=getIntent().getStringExtra("county_code");
        if(!TextUtils.isEmpty(countyCode)){
            publishText.setText("同步中");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        }
        else{
            showWeather();
        }
        btnswitchCity.setOnClickListener(this);
        btnrefreshWeather.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.switch_city:
                Intent intent=new Intent(this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                publishText.setText("同步中");
                SharedPreferences preferences=PreferenceManager
                        .getDefaultSharedPreferences(this);
                String weatherCode=preferences.getString("weather_code","");
                if(!TextUtils.isEmpty(weatherCode)){
                    queryWeatherInfo(weatherCode);
                }
                break;
        }
    }
    private void queryWeatherCode(String countyCode){
        String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
        queryFromServer(address, "countyCode");
    }
    private void queryWeatherInfo(String weatherCode){
        String address = "http://www.weather.com.cn/data/cityinfo/" +
                weatherCode + ".html";
        queryFromServer(address, "weatherCode");

    }
    private void queryFromServer(final String address,final String type){
        HttpUtil.sendRequestWithUrlconnection(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String respone) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(respone)) {
                        String[] array = respone.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    Utility.handleWeatherRespone(WeatherActivity.this,respone);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }
    private void showWeather(){
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(preferences.getString("city_name",""));
        publishText.setText("今天"+preferences.getString("publish_time","")+"发布");
        weatherDespText.setText(preferences.getString("weather_desp",""));
        temp1Text.setText(preferences.getString("temp1",""));
        temp2Text.setText(preferences.getString("temp2", ""));
        currentDateText.setText(preferences.getString("current_date", ""));
       weatherInfoLayout.setVisibility(View.VISIBLE);
       cityNameText.setVisibility(View.VISIBLE);
        Intent intent=new Intent(this, AutoUpdateService.class);
       startService(intent);
    }
}
