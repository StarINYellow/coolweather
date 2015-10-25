package com.eoe.cuixin.coolweather.utli;

import android.text.TextUtils;

import com.eoe.cuixin.coolweather.model.City;
import com.eoe.cuixin.coolweather.model.CoolWeatherDB;
import com.eoe.cuixin.coolweather.model.County;
import com.eoe.cuixin.coolweather.model.Province;

/**
 * Created by cuixin on 2015/10/25.
 */
public class Utilty {
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

}
