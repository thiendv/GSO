/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.wrapper.rws.service.copy;

import java.util.List;
import java.util.Map;

import jp.co.ricoh.ssdk.sample.wrapper.common.ArrayElement;
import jp.co.ricoh.ssdk.sample.wrapper.common.Element;
import jp.co.ricoh.ssdk.sample.wrapper.common.ResponseBody;

public class GetCopyStatusResponseBody extends Element implements ResponseBody  {

	private static final String KEY_COPY_STATUS				= "copyStatus";
	private static final String KEY_COPY_STATUS_REASONS		= "copyStatusReasons";
	private static final String KEY_OCCURED_ERROR_LEVEL     = "occuredErrorLevel";

	public GetCopyStatusResponseBody(Map<String, Object> values) {
		super(values);
	}

	/*
	 * copyStatus (String)
	 */
	public String getCopyStatus() {
		return getStringValue(KEY_COPY_STATUS);
	}

	/*
	 * copyStatusReasons (Array[Object])
	 */
	public CopyStatusReasonsArray getCopyStatusReason() {
		List<Map<String, Object>> value = getArrayValue(KEY_COPY_STATUS_REASONS);
		if (value == null) {
			return null;
		}
		return new CopyStatusReasonsArray(value);
	}
	
	/*
	 * occuredErrorLevel (String)
	 */
	public String getOccuredErrorLevel() {
	    return getStringValue(KEY_OCCURED_ERROR_LEVEL);
	}


	public static class CopyStatusReasonsArray extends ArrayElement<CopyStatusReasons> {

		CopyStatusReasonsArray(List<Map<String, Object>> list) {
			super(list);
		}

		@Override
		protected CopyStatusReasons createElement(Map<String, Object> values) {
			return new CopyStatusReasons(values);
		}

	}

	public static class CopyStatusReasons extends Element {

		private static final String KEY_COPY_STATUS_REASON	= "copyStatusReason";
		private static final String KEY_SEVERITY			= "severity";

		CopyStatusReasons(Map<String, Object> values) {
			super(values);
		}

		/*
		 * copyStatusReason (String)
		 */
		public String getCopyStatusReason() {
			return getStringValue(KEY_COPY_STATUS_REASON);
		}

		/*
		 * severity (String)
		 */
		public String getSeverity() {
			return getStringValue(KEY_SEVERITY);
		}

	}

}
