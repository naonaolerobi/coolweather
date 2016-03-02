package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

public class ChooseAreaActivty extends Activity {
	private static final int LEVEL_PROVINCE = 0;
	private static final int LEVEL_CITY = 1;
	private static final int LEVEL_COUNTY = 2;

	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	/**
	 * �ж��Ƿ��Ǵ�WeatherActivity����ת������
	 */
	private boolean isFromWeatherActivity;

	/**
	 * ʡ�б�
	 */
	private List<Province> provinceList;
	/**
	 * ���б�
	 */
	private List<City> cityList;
	/**
	 * ���б�
	 */
	private List<County> countyList;
	/**
	 * ѡ�е�ʡ��
	 */
	private Province selectedProvince;
	/**
	 * ѡ�е���
	 */
	private City selectedCity;
	/**
	 * ��ǰѡ�еļ���
	 */
	private int currentLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		isFromWeatherActivity = getIntent().getBooleanExtra(
				"form_weather_activity", false);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		//�Ѿ�ѡ���˳��ж��Ҳ��Ǵ�weatherActivity��ת�����ĲŻ�ֱ����ת��weatherActivity
		if (prefs.getBoolean("city_selected", false)&&isFromWeatherActivity) {
			Intent intent = new Intent(ChooseAreaActivty.this,
					WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);

		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(position);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(position);
					queryCounties();
				} else if (currentLevel == LEVEL_COUNTY) {
					String countyCode = countyList.get(position)
							.getCounty_code();
					Intent intent = new Intent(ChooseAreaActivty.this,
							WeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					finish();
				}
			}
		});
		queryProvince();
	}

	/**
	 * ��ѯȫ�����е�ʡ�����ȴ����ݿ�飬��û�дӷ������ϲ���
	 */
	private void queryProvince() {
		provinceList = coolWeatherDB.loadProvince();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvince_name());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");
		}
	}

	/**
	 * ��ѯѡ�е�ʡ�����е���
	 */
	private void queryCities() {
		cityList = coolWeatherDB.loadCity(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCity_name());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvince_name());
			currentLevel = LEVEL_CITY;

		} else {
			queryFromServer(selectedProvince.getProvince_code(), "city");
		}
	}

	/**
	 * ��ѯѡ�е���Ͻ���е��ؼ�
	 */
	private void queryCounties() {
		countyList = coolWeatherDB.loadCounty(selectedCity.getId());
		if (countyList.size() > 0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCounty_name());

			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCity_name());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getCity_code(), "county");
		}
	}

	/**
	 * ���ݴ���Ĵ��ź����ʹӷ������ϲ�ѯʡ�м�������
	 */
	private void queryFromServer(final String code, final String type) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml ";
		}
		// ��ʾ���ȶԻ���
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(coolWeatherDB,
							response);

				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(coolWeatherDB,
							response, selectedProvince.getId());

				} else if ("county".equals(type)) {
					result = Utility.handleCountiesResponse(coolWeatherDB,
							response, selectedCity.getId());
				}

				if (result) {
					// ͨ��runOnUiThread()�����ص����̴߳����߼�
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvince();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("county".equals(type)) {
								queryCounties();
							}
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				// ͨ��runOnUiThread�ص����̴߳����߼�
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivty.this, "����ʧ��",
								Toast.LENGTH_SHORT).show();
					}
				});

			}
		});
	}

	/**
	 * ��ʾ���ȶԻ���
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	/**
	 * �رս��ȶԻ���
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	/**
	 * ����back���������ݵ�ǰ�ļ������жϣ���ʱӦ�÷������б�ʡ�б�����ֱ���˳�
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvince();
		} else {
			if(isFromWeatherActivity){
				Intent intent = new Intent(ChooseAreaActivty.this, WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}

}
