package com.coolweather.app.db;

import java.util.ArrayList;
import java.util.List;

import android.R;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

public class CoolWeatherDB {
	/**
	 * 数据名
	 */
	public static final String DB_NAME = "cool_weather";
	/**
	 * 数据库版本
	 */
	public static final int VRESION = 1;
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;

	/**
	 * 将构造方法私有化
	 */
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper coolWeatherOpenHelper = new CoolWeatherOpenHelper(
				context, DB_NAME, null, VRESION);
		db = coolWeatherOpenHelper.getWritableDatabase();
	}

	/**
	 * 获取CoolWeather实例
	 */
	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}

	/**
	 * 将Province实例存储到数据库中
	 */
	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("province_name", province.getProvince_name());
			contentValues.put("province_code", province.getProvince_code());
			db.insert("Province", null, contentValues);
		}

	}

	/**
	 * 从数据库中读取全国的省份信息
	 */
	public List<Province> loadProvince() {
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db
				.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvince_name(cursor.getString(cursor
						.getColumnIndex("province_name")));
				province.setProvince_code(cursor.getString(cursor
						.getColumnIndex("province_code")));
				list.add(province);
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}

	/**
	 * 将city实例存储到数据库中
	 */
	public void saveCity(City city) {
		if (city != null) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("city_name", city.getCity_name());
			contentValues.put("city_code", city.getCity_code());
			contentValues.put("province_id", city.getProvince_id());
			db.insert("City", null, contentValues);

		}
	}

	/**
	 * 从数据库中读取某省下的所有城市信息
	 */
	public List<City> loadCity(int province_id) {
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id = ?",
				new String[] { String.valueOf(province_id) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCity_name(cursor.getString(cursor
						.getColumnIndex("city_name")));
				city.setCity_code(cursor.getString(cursor
						.getColumnIndex("city_code")));
				city.setProvince_id(province_id);
				list.add(city);

			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}

		return list;
	}

	/**
	 * 将county实例存储到数据库中
	 */
	public void saveCounty(County county) {
		if (county != null) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("county_name", county.getCounty_name());
			contentValues.put("county_code", county.getCounty_code());
			contentValues.put("city_id", county.getCity_id());
			db.insert("County", null, contentValues);
		}
	}

	/**
	 * 将某市下的所有县从数据库中读取出来
	 */
	public List<County> loadCounty(int city_id) {
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query("County", null, "city_id = ?",
				new String[] { String.valueOf(city_id) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCounty_name(cursor.getString(cursor
						.getColumnIndex("county_name")));
				county.setCounty_code(cursor.getString(cursor
						.getColumnIndex("county_code")));
				county.setCity_id(city_id);
				list.add(county);
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}

}
