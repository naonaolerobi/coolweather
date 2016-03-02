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
	 * ������
	 */
	public static final String DB_NAME = "cool_weather";
	/**
	 * ���ݿ�汾
	 */
	public static final int VRESION = 1;
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;

	/**
	 * �����췽��˽�л�
	 */
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper coolWeatherOpenHelper = new CoolWeatherOpenHelper(
				context, DB_NAME, null, VRESION);
		db = coolWeatherOpenHelper.getWritableDatabase();
	}

	/**
	 * ��ȡCoolWeatherʵ��
	 */
	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}

	/**
	 * ��Provinceʵ���洢�����ݿ���
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
	 * �����ݿ��ж�ȡȫ����ʡ����Ϣ
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
	 * ��cityʵ���洢�����ݿ���
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
	 * �����ݿ��ж�ȡĳʡ�µ����г�����Ϣ
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
	 * ��countyʵ���洢�����ݿ���
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
	 * ��ĳ���µ������ش����ݿ��ж�ȡ����
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
