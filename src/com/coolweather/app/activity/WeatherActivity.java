package com.coolweather.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolweather.app.R;
import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

public class WeatherActivity extends Activity {
	private LinearLayout weatherInfoLayout;
	public static final String TAG = "WeatherActivity";

	/**
	 * ��ʾ������
	 */
	private TextView cityNameText;
	/**
	 * ��ʾ����ʱ��
	 */
	private TextView publishText;
	/**
	 * ������ʾ������������Ϣ
	 */
	private TextView weatherDespText;
	/**
	 * ��ʾ����1
	 */
	private TextView temp1Text;
	/**
	 * ��ʾ����2
	 */
	private TextView temp2Text;
	/**
	 * ������ʾ��ǰ����
	 */
	private TextView currentDateText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		// ��ʼ�������ؼ�
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		Log.d(TAG, "��ʼ����ϡ�������");
		
		String countyCode = getIntent().getStringExtra("county_code");
		if (!TextUtils.isEmpty(countyCode)) {
			// ���ؼ����ž�ȥ��ѯ����
			publishText.setText("ͬ����...");
			Log.d(TAG, "ͬ���С�����");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.VISIBLE);
			queryWeatherCode(countyCode);
			Log.d(TAG, "ͬ�����");
		} else {
			showWeather();
		}
	}

	/**
	 * ��ѯ�ؼ�����ʵֱ����ʾ��������
	 */
	public void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countyCode + ".xml";
		Log.d(TAG, "���������������");
		queryFormServer(address, "countyCode");
		Log.d(TAG, "���������");
	}

	/**
	 * ��ѯ������������Ӧ������
	 */
	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
		queryFormServer(address, "weatherCode");
	}

	/**
	 * ���ݴ���ĵ�ַ������ȥ���������ѯ�������Ż���������Ϣ
	 */
	private void queryFormServer(final String address, final String type) {
		Log.d(TAG, "server������");
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(final String response) {
				// TODO Auto-generated method stub
				Log.d(TAG, "���������");
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						// �ӷ��������ص������н�������������
						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				} else if ("weatherCode".equals(type)) {
					// ������������ص�������Ϣ
					Utility.handleWeatherResponse(WeatherActivity.this,
							response);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							showWeather();
						}
					});
				}
				Log.d(TAG, "�������������");
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					public void run() {
						publishText.setText("ͬ��ʧ��...");
					}
				});
			}
		});
	}

	/**
	 * ��sharedPreference�ļ��ж�ȡ�洢��������Ϣ������ʾ��������
	 */
	private void showWeather() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp2", ""));
		temp2Text.setText(prefs.getString("temp1", ""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		publishText.setText("����"+prefs.getString("publish_time", "")+"����");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		Intent intent = new Intent(this, AutoUpdateService.class);
		startService(intent);
	}
	/**
	 * button�¼�����
	 */
	public void switchCity(View view){
		Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivty.class);
		intent.putExtra("from_weather_activity", true);
		startActivity(intent);
		finish();
	}
	public void refreshWeather(View view){
		publishText.setText("ͬ����...");
		Log.d(TAG, "������");
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String weatherCode = prefs.getString("weather_code", "");
		if(!TextUtils.isEmpty(weatherCode)){
			queryWeatherInfo(weatherCode);
		}
		Log.d(TAG, "�������");
	}
	

	
}
