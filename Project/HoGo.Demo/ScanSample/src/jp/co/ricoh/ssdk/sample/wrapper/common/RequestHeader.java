/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.wrapper.common;

import java.util.HashMap;
import java.util.Map;

/**
 * WebAPI通信でのリクエストヘッダ部分を定義するインターフェース。
 */
public final class RequestHeader {
	
	public static final String KEY_HOST					= "Host";
	public static final String KEY_AUTHORIZATION		= "Authorization";
	public static final String KEY_ACCEPT				= "Accept";
	
	public static final String KEY_X_APPLICATION_ID		= "X-Application-Id";
	public static final String KEY_X_SUBSCRIPTION_ID	= "X-Subscription-Id";
	
	public static final String ACCEPT_APPLICATION_JSON	= "application/json";
	public static final String ACCEPT_APPLICATION_XML	= "application/xml";
	
	
	private final Map<String, String> headers;
	
	public RequestHeader() {
		headers = new HashMap<String, String>();
	}
	
	public Map<String, String> getHeaders() {
		return new HashMap<String, String>(headers);
	}
	
	public String get(String key) {
		return headers.get(key);
	}
	
	public void put(String key, String value) {
		headers.put(key, value);
	}
	
	public void putAll(Map<String, String> values) {
		headers.putAll(values);
	}
	
	public String remove(String key) {
		return headers.remove(key);
	}
	
	public void clear() {
		headers.clear();
	}
	
}
