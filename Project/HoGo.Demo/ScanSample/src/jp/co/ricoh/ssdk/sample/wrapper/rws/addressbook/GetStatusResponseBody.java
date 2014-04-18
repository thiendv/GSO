/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.wrapper.rws.addressbook;

import java.util.Map;

import jp.co.ricoh.ssdk.sample.wrapper.common.Element;
import jp.co.ricoh.ssdk.sample.wrapper.common.ResponseBody;

public class GetStatusResponseBody extends Element implements ResponseBody {
	
	private static final String KEY_ENTRY_NUM		= "entryNum";
	private static final String KEY_TAG_NUM		= "tagNum";
	private static final String KEY_GROUP_NUM		= "groupNum";
	
	GetStatusResponseBody(Map<String, Object> values) {
		super(values);
	}
	
	/*
	 * entryNum (Int)
	 */
	public Integer getEntryNum() {
		return getNumberValue(KEY_ENTRY_NUM);
	}
	
	/*
	 * tagNum (Int)
	 */
	public Integer getTagNum() {
		return getNumberValue(KEY_TAG_NUM);
	}
	
	/*
	 * groupNum (Int)
	 */
	public Integer getGroupNum() {
		return getNumberValue(KEY_GROUP_NUM);
	}
	
}
