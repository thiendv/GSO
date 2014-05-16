/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.wrapper.rws.status;

import java.util.List;
import java.util.Map;

import jp.co.ricoh.ssdk.sample.wrapper.common.ArrayElement;
import jp.co.ricoh.ssdk.sample.wrapper.common.Element;
import jp.co.ricoh.ssdk.sample.wrapper.common.ResponseBody;

public class GetTraysResponseBody extends Element implements ResponseBody {
	
	private static final String KEY_INPUT_TRAYS	= "inputTrays";
	
	GetTraysResponseBody(Map<String, Object> values) {
		super(values);
	}
	
	/*
	 * inputTrays (Array[Object])
	 */
	public InputTraysArray getInputTrays() {
		List<Map<String, Object>> value = getArrayValue(KEY_INPUT_TRAYS);
		if (value == null) {
			return null;
		}
		return new InputTraysArray(value);
	}
	
	
	public static class InputTraysArray extends ArrayElement<InputTrays> {
		
		InputTraysArray(List<Map<String, Object>> list) {
			super(list);
		}
		
		@Override
		protected InputTrays createElement(Map<String, Object> values) {
			return new InputTrays(values);
		}
		
	}
	
	public static class InputTrays extends Element {
		
		private static final String KEY_NAME			= "name";
		private static final String KEY_PAPER_SIZE	= "paperSize";
		private static final String KEY_STATUS		= "status";
		private static final String KEY_PAPER_REMAIN	= "paperRemain";
		private static final String KEY_PAPER_TYPE	= "paperType";
		
		InputTrays(Map<String, Object> values) {
			super(values);
		}
		
		/*
		 * name (String)
		 */
		public String getName() {
			return getStringValue(KEY_NAME);
		}
		
		/*
		 * paperSize (String)
		 */
		public String getPaperSize() {
			return getStringValue(KEY_PAPER_SIZE);
		}
		
		/*
		 * status (String)
		 */
		public String getStatus() {
			return getStringValue(KEY_STATUS);
		}
		
		/*
		 * paperRemain (Number)
		 */
		public Integer getPaperRemain() {
			return getNumberValue(KEY_PAPER_REMAIN);
		}
		
		/*
		 * paperType (String)
		 */
		public String getPaperType() {
			return getStringValue(KEY_PAPER_TYPE);
		}
		
	}
	
}
