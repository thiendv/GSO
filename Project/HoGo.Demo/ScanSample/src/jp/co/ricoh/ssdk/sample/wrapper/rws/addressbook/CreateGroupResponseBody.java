/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.wrapper.rws.addressbook;

import java.util.Map;

import jp.co.ricoh.ssdk.sample.wrapper.common.ResponseBody;

public class CreateGroupResponseBody extends Group implements ResponseBody {

	CreateGroupResponseBody(Map<String, Object> value) {
		super(value);
	}

}
