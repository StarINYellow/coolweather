package com.eoe.cuixin.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eoe.cuixin.coolweather.R;
import com.eoe.cuixin.coolweather.model.City;
import com.eoe.cuixin.coolweather.model.CoolWeatherDB;
import com.eoe.cuixin.coolweather.model.County;
import com.eoe.cuixin.coolweather.model.Province;
import com.eoe.cuixin.coolweather.utli.HttpCallBackListener;
import com.eoe.cuixin.coolweather.utli.HttpUtil;
import com.eoe.cuixin.coolweather.utli.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cuixin on 2015/10/25.
 */
public class ChooseAreaActivity extends Activity {
    public static final int Level_Province=0;
    public static final int Level_City=1;
    public static final int Level_County=2;
    private int Level_Current;

    private TextView textTitle;
    private ListView listView;

    private ProgressDialog progressDialog;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> listData=new ArrayList<>();

    private List<Province> listProvinces;
    private List<City> listCities;
    private List<County> listCounty;

    private Province selectedProvince;
    private City selectedCity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.getBoolean("city_selected",false)){
            Intent intent=new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        textTitle=(TextView)findViewById(R.id.text_title);
        listView=(ListView)findViewById(R.id.list_view);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listData);
        listView.setAdapter(adapter);
        coolWeatherDB=CoolWeatherDB.getInstance(this);
        queryProvinces();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(Level_Current==Level_Province)
                {
                    selectedProvince=listProvinces.get(position);
                    queryCities();
                }
                else if(Level_Current==Level_City){
                    selectedCity=listCities.get(position);
                    queryCounties();
                }
                else if(Level_Current==Level_County){
                    String countyCode=listCounty.get(position).getCountyCode();
                    Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("county_code",countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
    private void queryProvinces(){
        listProvinces=coolWeatherDB.loadProvinces();
        if(listProvinces.size()>0){
            listData.clear();
            for(Province p:listProvinces){
                listData.add(p.getProvinceName());

            }
            adapter.notifyDataSetChanged();
            textTitle.setText("中国");
            listView.setSelection(0);
            Level_Current=Level_Province;
        }
        else{
            queryFromServer(null,"province");
        }
    }
    private void queryCities(){
        listCities=coolWeatherDB.loadCities(selectedProvince.getId());
        if(listCities.size()>0){
            listData.clear();
            for(City c:listCities){
                listData.add(c.getCityName());
            }
            adapter.notifyDataSetChanged();
            textTitle.setText(selectedProvince.getProvinceName());
            listView.setSelection(0);
            Level_Current=Level_City;

        }
        else{
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }


    }
    private void queryCounties(){
        listCounty=coolWeatherDB.loadCounties(selectedCity.getId());
        if(listCounty.size()>0){
            listData.clear();
            for(County c:listCounty){
                listData.add(c.getCountyName());
            }
            adapter.notifyDataSetChanged();
            textTitle.setText(selectedCity.getCityName());
            listView.setSelection(0);
            Level_Current=Level_County;
        }
        else{
            queryFromServer(selectedCity.getCityCode(),"county");
        }

    }
    private void queryFromServer(final String code,final String type){
        String address;
        if(!TextUtils.isEmpty(code)){
            address="http://www.weather.com.cn/data/list3/city" +code+".xml";
        }
        else{
            address="http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendRequestWithUrlconnection(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String respone) {
                boolean result=false;
                if("province".equals(type)){
                    result= Utility.handleProvincesRespone(coolWeatherDB, respone);

                }
                else if("city".equals(type)){
                    result= Utility.handleCitiesRespone(coolWeatherDB, respone, selectedProvince.getId());
                }
                else if("county".equals(type)){
                    result= Utility.handleCountiesRespone(coolWeatherDB, respone, selectedCity.getId());
                }
                if(result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }
                            else if("city".equals(type)){
                                queryCities();
                            }
                            else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });

                }
            }

            @Override
            public void onError(Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Toast.makeText(ChooseAreaActivity.this,
                                    "加载失败",Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });
    }
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
           progressDialog.setMessage("加载中");
           progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if(Level_Current==Level_County){
            queryCities();
        }
        else if(Level_Current==Level_City){
            queryProvinces();
        }
        else{
            finish();
        }
    }
}
