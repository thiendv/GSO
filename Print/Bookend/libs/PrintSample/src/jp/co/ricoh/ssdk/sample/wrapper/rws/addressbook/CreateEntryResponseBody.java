/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.wrapper.rws.addressbook;

import java.util.Map;

import jp.co.ricoh.ssdk.sample.wrapper.common.ResponseBody;

public class CreateEntryResponseBody extends Entry implements ResponseBody {

	CreateEntryResponseBody(Map<String, Object> values) {
		super(values);
	}
	
}
