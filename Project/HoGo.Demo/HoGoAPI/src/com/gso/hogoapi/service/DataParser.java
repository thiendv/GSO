package com.gso.hogoapi.service;

import org.json.JSONObject;

import android.util.Log;

import com.gso.hogoapi.model.FileData;
import com.gso.hogoapi.model.LoginData;

public class DataParser {

	public DataParser(boolean isJson) {

	}

	public Object parseRetaurentList() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object parseLogin(String input) {
		// TODO Auto-generated method stub
		try {
			JSONObject root = new JSONObject(input);
			LoginData data = new LoginData();
			boolean status = root.optString("status").equalsIgnoreCase("OK");
			String token = root.optString("token");
			data.setStatus(status);
			data.setToken(token);
			return data;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}

	}

	public Object parseLogout() {
		// TODO Auto-generated method stub

		return null;
	}

	public FileData parseUpdateResult(String input) {
		// TODO Auto-generated method stub
		try {
			JSONObject root = new JSONObject(input);
			FileData data = new FileData();
			boolean status = root.optString("status").equalsIgnoreCase("OK");
			if(status){
				data.setFileName(root.optString("file_name"));
				data.setQueueId(root.optString("queue_id"));
				return data;
			}else{
				return null;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}

	public boolean parseEncodeResult(String input) {
		// TODO Auto-generated method stub
		try {
			JSONObject root = new JSONObject(input);
			boolean status = root.optString("status").equalsIgnoreCase("OK");
			return status;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

	public String parseCheckEncodeResult(String input) {
		// TODO Auto-generated method stub
		try {
			JSONObject root = new JSONObject(input);
			String status = root.optString("status");
			return status;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return "0";
		}
	}

}
