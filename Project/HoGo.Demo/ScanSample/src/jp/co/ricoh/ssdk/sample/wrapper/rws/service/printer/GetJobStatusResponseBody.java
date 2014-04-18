/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.wrapper.rws.service.printer;

import java.util.Map;

import jp.co.ricoh.ssdk.sample.wrapper.common.ResponseBody;

public class GetJobStatusResponseBody extends JobInfo implements ResponseBody {

	public GetJobStatusResponseBody(Map<String, Object> values) {
		super(values);
	}

}
