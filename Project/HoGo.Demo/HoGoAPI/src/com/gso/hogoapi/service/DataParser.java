package com.gso.hogoapi.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gso.hogoapi.model.AddressBookItem;
import com.gso.hogoapi.model.FileData;
import com.gso.hogoapi.model.LoginData;
import com.gso.hogoapi.model.PackageData;
import com.gso.hogoapi.model.ResponseData;

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

	public ResponseData parseUpdateResult(String input) {
		// TODO Auto-generated method stub
		ResponseData resData = new ResponseData();
		try {
			JSONObject root = new JSONObject(input);
			FileData data = new FileData();
			String status = root.optString("status");
			resData.setStatus(status);
			if(status.equalsIgnoreCase("OK")){
				data.setFileName(root.optString("file_name"));
				data.setQueueId(root.optString("queue_id"));
				resData.setData(data);
			}else{
				
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		
		}
		return resData;
	}

	public ResponseData parseEncodeResult(String input) {
		// TODO Auto-generated method stub
		ResponseData resData = new ResponseData();
		try {
			JSONObject root = new JSONObject(input);
			String status = root.optString("status");
			resData.setStatus(status);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return resData;
	}

	public ResponseData parseCheckEncodeResult(String input) {
		// TODO Auto-generated method stub
		ResponseData data = new ResponseData();
		try {
			
			JSONObject root = new JSONObject(input);
			String status = root.optString("status");
			String documentId = root.optString("document_id");
			FileData item = new FileData();
			item.setDocumentId(documentId);
			data.setStatus(status);
			data.setData(item);
			

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return data;
	}

	public ResponseData parseSendResponse(String input) {
		// TODO Auto-generated method stub
		input = input.replace("package_id ", "package_id");
		ResponseData resData = new ResponseData();
		try {
			
			JSONObject root = new JSONObject(input);
			String status = root.optString("status");

			PackageData item = new PackageData();
			if(status.equals("OK")){
				item.setId(root.optString("package_id"));	
			}
			resData.setData(item);
			resData.setStatus(status);
			

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			
		}
		return resData;
	}

	public ResponseData parseGetDocumentListResult(String input) {
		// TODO Auto-generated method stub
		ResponseData resData = new ResponseData();
		List<FileData> data = new ArrayList<FileData>();
		try {
			JSONObject root = new JSONObject(input);
			String status = root.optString("status");
			resData.setStatus(status);
			if(root.optString("status").equalsIgnoreCase("OK")){
				JSONArray array = root.optJSONArray("document_id");
				if(array !=null){
					int length = array.length();
					for(int i = 0 ; i < length; i++){
						String obj = array.optString(i);
						if(obj !=null){
							FileData item = new FileData();
							item.setDocumentId(obj);
							data.add(item);
						}
						
					}
				}
			}
			resData.setData(data);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return resData;
	}

	public ResponseData parseAddressBookResponse(String input) {
		// TODO Auto-generated method stub
		ResponseData resData = new ResponseData();
		List<AddressBookItem> data = new ArrayList<AddressBookItem>();
		try {
			JSONObject root = new JSONObject(input);
			String status = root.optString("status");
			resData.setStatus(status);
			if(root.optString("status").equalsIgnoreCase("OK")){
				JSONArray array = root.optJSONArray("recipient_detail");
				if(array !=null){
					int length = array.length();
					for(int i = 0 ; i < length; i++){
						JSONObject obj = array.optJSONObject(i);
						if(obj !=null){
							AddressBookItem item = new AddressBookItem();
							item.setEmail(obj.optString("e-mail"));
							item.setFirstName(obj.optString("first_name"));
							item.setLastName(obj.optString("last_name"));
							item.setMiddleName(obj.optString("middle_name"));
							data.add(item);
						}
						
					}
				}
			}
			resData.setData(data);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return resData;
	}

}
