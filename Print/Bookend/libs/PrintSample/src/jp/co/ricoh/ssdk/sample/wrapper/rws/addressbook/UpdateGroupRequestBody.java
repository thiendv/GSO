/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.wrapper.rws.addressbook;

import java.util.HashMap;
import java.util.Map;

import jp.co.ricoh.ssdk.sample.wrapper.common.RequestBody;
import jp.co.ricoh.ssdk.sample.wrapper.json.EncodedException;
import jp.co.ricoh.ssdk.sample.wrapper.json.JsonUtils;

public class UpdateGroupRequestBody extends Group implements RequestBody {

	private static final String CONTENT_TYPE_JSON = "application/json; charset=utf-8";
	
	public UpdateGroupRequestBody() {
		super(new HashMap<String, Object>());
	}
	public UpdateGroupRequestBody(Map<String, Object> values) {
		super(values);
	}

	@Override
	public String getContentType() {
		return CONTENT_TYPE_JSON;
	}
	
	@Override
	public String toEntityString() {
		try {
			return JsonUtils.getEncoder().encode(values);
		} catch (EncodedException e) {
			e.printStackTrace();
			return "{}";
		}
	}

}
