/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.wrapper.common;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import jp.co.ricoh.ssdk.sample.wrapper.json.DecodedException;
import jp.co.ricoh.ssdk.sample.wrapper.json.Decoder;
import jp.co.ricoh.ssdk.sample.wrapper.json.JsonUtils;

/**
 * JSON形式のデータを解析する機能を提供するクラス。
 */
public class GenericJsonDecoder {
	
	private static <T> T decode(String json, Class<T> clazz) throws DecodedException {
		Decoder decoder = JsonUtils.getDecoder();
		return decoder.decode(json, clazz);
	}
	
	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> decodeToMap(String json) {
		try {
			return decode(json, Map.class);
		} catch (DecodedException e) {
			e.printStackTrace();
		}
		return Collections.emptyMap();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> decodeToList(String json) {
		try {
			return decode(json, List.class);
		} catch (DecodedException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}
	
	private GenericJsonDecoder() {}

}
