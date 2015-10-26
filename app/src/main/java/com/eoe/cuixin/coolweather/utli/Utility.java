package com.eoe.cuixin.coolweather.utli;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.eoe.cuixin.coolweather.model.City;
import com.eoe.cuixin.coolweather.model.CoolWeatherDB;
import com.eoe.cuixin.coolweather.model.County;
import com.eoe.cuixin.coolweather.model.Province;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by cuixin on 2015/10/25.
 */
public class Utility {
    public synchronized static boolean handleProvincesRespone
            (CoolWeatherDB coolWeatherDB, String respone){
        if(!TextUtils.isEmpty(respone)){
            String[] provinces=respone.split(",");
            if(provinces!=null&provinces.length>0){
                for(String eachProvince:provinces){
                   String[] array= eachProvince.split("\\|");
                    Province province=new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }
    public  static boolean handleCitiesRespone
            (CoolWeatherDB coolWeatherDB,String respone,int provinceId){
        if(!TextUtils.isEmpty(respone)){
            String[] cities=respone.split(",");
            if(cities.length>0&&cities!=null){
                for(String c:cities){
                    String[] array=c.split("\\|");
                    City city=new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    coolWeatherDB.saveCities(city);
                }
                return true;
            }
        }
        return false;
    }
    public  static boolean handleCountiesRespone
            (CoolWeatherDB coolWeatherDB,String respone,int cityId){
        if(!TextUtils.isEmpty(respone)){
            String[] counties=respone.split(",");
            for(String c:counties){
                String[] array=c.split("\\|");
                County county=new County();
                county.setCountyCode(array[0]);
                county.setCountyName(array[1]);
                county.setCityId(cityId);
                coolWeatherDB.saveCounty(county);
            }
            return true;
        }
        return false;
    }
    public static void handleWeatherRespone(Context context,String responce){
       try{
           JSONObject jsonObject=new JSONObject(responce);
           JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
           String cityName=weatherInfo.getString("city");
           String weatherCode=weatherInfo.getString("cityid");
           String temp1=weatherInfo.getString("temp1");
           String temp2=weatherInfo.getString("temp2");
           String weatherDesp=weatherInfo.getString("weather");
           String publishTime=weatherInfo.getString("ptime");
           saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
       }catch (Exception e){
           e.printStackTrace();
       }

    }
    public static void saveWeatherInfo
            (Context context,String cityName,String weatherCode,String temp1,String temp2,
             String weatherDsp,String publishTime){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDsp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_time",simpleDateFormat.format(new Date()));
        editor.commit();
    }


}
