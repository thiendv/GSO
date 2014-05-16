/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.wrapper.rws.service.copy;

import java.util.List;
import java.util.Map;

import jp.co.ricoh.ssdk.sample.wrapper.common.Element;
import jp.co.ricoh.ssdk.sample.wrapper.common.ResponseBody;

public class GetJobStatusResponseBody extends Element implements ResponseBody  {

	private static final String KEY_JOB_ID				= "jobId";
	private static final String KEY_JOB_STATUS			= "jobStatus";
	private static final String KEY_JOB_STATUS_REASONS	= "jobStatusReasons";
	private static final String KEY_SCANNING_INFO		= "scanningInfo";
	private static final String KEY_PRINTING_INFO		= "printingInfo";
	private static final String KEY_VALIDATE_ONLY		= "validateOnly";
	private static final String KEY_JOB_SETTING			= "jobSetting";
	private static final String KEY_OCCURED_ERROR_LEVEL	= "occuredErrorLevel";

	public GetJobStatusResponseBody(Map<String, Object> values) {
		super(values);
	}

	/*
	 * jobId (String)
	 */
	public String getJobId() {
		return getStringValue(KEY_JOB_ID);
	}

	/*
	 * jobStatus (String)
	 */
	public String getJobStatus() {
		return getStringValue(KEY_JOB_STATUS);
	}

	/*
	 * jobStatusReasons (Array[String])
	 */
	public List<String> getJobStatusReasons() {
		return getArrayValue(KEY_JOB_STATUS_REASONS);
	}

	/*
	 * scanningInfo (Object)
	 */
	public ScanningInfo getScanningInfo() {
		Map<String, Object> value = getObjectValue(KEY_SCANNING_INFO);
		if (value == null) {
			return null;
		}
		return new ScanningInfo(value);
	}

	/*
	 * printingInfo (Object)
	 */
	public PrintingInfo getPrintingInfo() {
		Map<String, Object> value = getObjectValue(KEY_PRINTING_INFO);
		if (value == null) {
			return null;
		}
		return new PrintingInfo(value);
	}

	/*
	 * validateOnly (Boolean)
	 */
	public Boolean getValidateOnly() {
		return getBooleanValue(KEY_VALIDATE_ONLY);
	}

	/*
	 * jobSetting (Object)
	 */
	public JobSetting getJobSetting() {
		Map<String, Object> value = getObjectValue(KEY_JOB_SETTING);
		if (value == null) {
			return null;
		}
		return new JobSetting(value);
	}

	/*
	 * occuredErrorLevel (String)
	 */
	public String getOccuredErrorLevel() {
		return getStringValue(KEY_OCCURED_ERROR_LEVEL);
	}


	public static class ScanningInfo extends Element {
		private static final String KEY_JOB_STATUS				= "jobStatus";
		private static final String KEY_JOB_STATUS_REASONS		= "jobStatusReasons";
		private static final String KEY_SCANNED_COUNT			= "scannedCount";
		private static final String KEY_RESET_ORIGINAL_COUNT	= "resetOriginalCount";

		ScanningInfo(Map<String, Object> values) {
			super(values);
		}

		/*
		 * jobStatus (String)
		 */
		public String getJobStatus() {
			return getStringValue(KEY_JOB_STATUS);
		}

		/*
		 * jobStatusReasons (Array[String])
		 */
		public List<String> getJobStatusReasons() {
			return getArrayValue(KEY_JOB_STATUS_REASONS);
		}

		/*
		 * scannedCount (Number)
		 */
		public Integer getScannedCount() {
			return getNumberValue(KEY_SCANNED_COUNT);
		}

		/*
		 * resetOriginalCount (Number)
		 */
		public Integer getResetOriginalCount() {
			return getNumberValue(KEY_RESET_ORIGINAL_COUNT);
		}
	}

	public static class PrintingInfo extends Element {
		private static final String KEY_JOB_STATUS				= "jobStatus";
		private static final String KEY_JOB_STATUS_REASONS		= "jobStatusReasons";
		private static final String KEY_PRINTED_COPIES			= "printedCopies";
		private static final String KEY_TRAY_IN_USE				= "trayInUse";

		PrintingInfo(Map<String, Object> values) {
			super(values);
		}

		/*
		 * jobStatus (String)
		 */
		public String getJobStatus() {
			return getStringValue(KEY_JOB_STATUS);
		}

		/*
		 * jobStatusReasons (Array[String])
		 */
		public List<String> getJobStatusReasons() {
			return getArrayValue(KEY_JOB_STATUS_REASONS);
		}

		/*
		 * printedCopies (Number)
		 */
		public Integer getPrintedCopies() {
			return getNumberValue(KEY_PRINTED_COPIES);
		}

		/*
		 * trayInUse (String)
		 */
		public String getTrayInUse() {
			return getStringValue(KEY_TRAY_IN_USE);
		}
	}

}
