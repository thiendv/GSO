/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.wrapper.rws.service.scanner;

import java.util.List;
import java.util.Map;

import jp.co.ricoh.ssdk.sample.wrapper.common.ArrayElement;
import jp.co.ricoh.ssdk.sample.wrapper.common.Element;
import jp.co.ricoh.ssdk.sample.wrapper.common.ResponseBody;

public class GetScannerStatusResponseBody extends Element implements ResponseBody {
	
	private static final String KEY_SCANNER_STATUS			= "scannerStatus";
	private static final String KEY_SCANNER_STATUS_REASONS	= "scannerStatusReasons";
	private static final String KEY_REMAINING_MEMORY		= "remainingMemory";
    private static final String KEY_REMAINING_MEMORY_LOCAL	= "remainingMemoryLocal";
	private static final String KEY_OCCURED_ERROR_LEVEL     = "occuredErrorLevel";
	
	public GetScannerStatusResponseBody(Map<String, Object> values) {
		super(values);
	}
	
	/*
	 * scannerStatus (String)
	 */
	public String getScannerStatus() {
		return getStringValue(KEY_SCANNER_STATUS);
	}
	
	/*
	 * scannerStatusReasons (Array[Object])
	 */
	public ScannerStatusReasonsArray getScannerStatusReasons() {
		List<Map<String, Object>> value = getArrayValue(KEY_SCANNER_STATUS_REASONS);
		if (value == null) {
			return null;
		}
		return new ScannerStatusReasonsArray(value);
	}
	
	/*
	 * remainingMemory (Number)
	 */
	public Integer getRemainingMemory() {
		return getNumberValue(KEY_REMAINING_MEMORY);
	}
	
	/*
	 * remainingMemoryLocal (Number)
	 */
	public Integer getRemainingMemoryLocal() {
		return getNumberValue(KEY_REMAINING_MEMORY_LOCAL);
	}
	
	/*
	 * occuredErrorLevel (String)
	 */
	public String getOccuredErrorLevel() {
	    return getStringValue(KEY_OCCURED_ERROR_LEVEL);
	}


	public static class ScannerStatusReasonsArray extends ArrayElement<ScannerStatusReasons> {
		
		ScannerStatusReasonsArray(List<Map<String, Object>> list) {
			super(list);
		}
		
		@Override
		protected ScannerStatusReasons createElement(Map<String, Object> values) {
			return new ScannerStatusReasons(values);
		}
		
	}
	
	public static class ScannerStatusReasons extends Element {
		
		private static final String KEY_SCANNER_STATUS_REASON	= "scannerStatusReason";
		private static final String KEY_SEVERITY				= "severity";
		
		ScannerStatusReasons(Map<String, Object> values) {
			super(values);
		}
		
		/*
		 * scannerStatusReason (String)
		 */
		public String getScannerStatusReason() {
			return getStringValue(KEY_SCANNER_STATUS_REASON);
		}
		
		/*
		 * severity (String)
		 */
		public String getSeverity() {
			return getStringValue(KEY_SEVERITY);
		}
		
	}
	
}