/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.wrapper.rws.addressbook;

import java.util.Map;

import jp.co.ricoh.ssdk.sample.wrapper.common.ResponseBody;

public class GetGroupResponseBody extends Group implements ResponseBody {
	
	GetGroupResponseBody(Map<String, Object> values) {
		super(values);
	}
	
}
