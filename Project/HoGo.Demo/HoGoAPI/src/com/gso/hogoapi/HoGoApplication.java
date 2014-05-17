package com.gso.hogoapi;

import com.gso.serviceapilib.API;
import com.gso.serviceapilib.ServiceAPILibApplication;
import com.gso.serviceapilib.ServiceAction;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import jp.co.ricoh.ssdk.sample.app.scan.application.ScanSampleApplication;

public class HoGoApplication extends ScanSampleApplication {

	private static final String PREFS_NAME = "user_data";

	/**
	 * @param args
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		API.hostURL = "http://avalanche.hogodoc.com/HoGo/api";

	}

	private static HoGoApplication sInstance;

	public static HoGoApplication instace() {
		if (sInstance == null) {
			sInstance = new HoGoApplication();
		}
		return sInstance;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public String getToken(Context context) {
		// TODO Auto-generated method stub
		String token = null;
		SharedPreferences account = context.getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
		if(account !=null){
			token = account.getString("token", null);
			ServiceAPILibApplication.token = token;
		}
		return token;
	}
	public void setToken(Context context, String token){
		SharedPreferences account = context.getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = account.edit();
		if(token!=null){
			editor.putString("token", token);
		}else{
			editor.remove("token");
		}
		editor.commit();
	}

}
