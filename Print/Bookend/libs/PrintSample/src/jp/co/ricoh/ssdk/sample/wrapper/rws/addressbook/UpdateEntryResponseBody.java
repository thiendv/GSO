/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.wrapper.rws.addressbook;

import java.util.Map;

import jp.co.ricoh.ssdk.sample.wrapper.common.ResponseBody;

public class UpdateEntryResponseBody extends Entry implements ResponseBody {

	UpdateEntryResponseBody(Map<String, Object> values) {
		super(values);
	}
	
}
