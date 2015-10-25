package com.eoe.cuixin.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by cuixin on 2015/10/24.
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
    public static final String TABLE_PROVINCE="create table Province("
            +"id integer primary key autoincrement,"
            +"province_name text,"
            +"province_code text)";
    public static  final String TABLE_CITY="create table City("
            +"id integer primary key autoincrement,"
            +"city_name text,"
            +"city_code text,"
            +"province_id integer)";
    public static final String TABLE_COUNTY="create table County("
            +"id integer primary key autoincrement,"
            +"county_name text,"
            +"county_code text,"
            +"city_id integer)";
    public CoolWeatherOpenHelper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_PROVINCE);
        db.execSQL(TABLE_CITY);
        db.execSQL(TABLE_COUNTY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
