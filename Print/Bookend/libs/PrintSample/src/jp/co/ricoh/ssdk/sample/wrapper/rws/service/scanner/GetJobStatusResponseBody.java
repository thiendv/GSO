/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.wrapper.rws.service.scanner;

import java.util.List;
import java.util.Map;

import jp.co.ricoh.ssdk.sample.wrapper.common.Element;
import jp.co.ricoh.ssdk.sample.wrapper.common.ResponseBody;

public class GetJobStatusResponseBody extends Element implements ResponseBody {
	
	private static final String KEY_EVENT_DETAIL		= "eventDetail";
	private static final String KEY_JOB_ID				= "jobId";
	private static final String KEY_JOB_STATUS			= "jobStatus";
	private static final String KEY_JOB_STATUS_REASONS	= "jobStatusReasons";
	private static final String KEY_SCANNING_INFO		= "scanningInfo";
	private static final String KEY_FILING_INFO			= "filingInfo";
	private static final String KEY_OCRING_INFO			= "ocringInfo";
	private static final String KEY_SENDING_INFO		= "sendingInfo";
	private static final String KEY_VALIDATE_ONLY		= "validateOnly";
	private static final String KEY_JOB_SETTING			= "jobSetting";
	
	public GetJobStatusResponseBody(Map<String, Object> values) {
		super(values);
	}
	
	/*
	 * eventDetail (String) Event only
	 */
	public String getEventDetail() {
		return getStringValue(KEY_EVENT_DETAIL);
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
	 * filingInfo (Object)
	 */
	public FilingInfo getFilingInfo() {
		Map<String, Object> value = getObjectValue(KEY_FILING_INFO);
		if (value == null) {
			return null;
		}
		return new FilingInfo(value);
	}
	
	/*
	 * ocringInfo (Object)
	 */
	public OcringInfo getOcringInfo() {
		Map<String, Object> value = getObjectValue(KEY_OCRING_INFO);
		if (value == null) {
			return null;
		}
		return new OcringInfo(value);
	}
	
	/*
	 * sendingInfo (Object)
	 */
	public SendingInfo getSendingInfo() {
		Map<String, Object> value = getObjectValue(KEY_SENDING_INFO);
		if (value == null) {
			return null;
		}
		return new SendingInfo(value);
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
	
	
	public static class ScanningInfo extends Element {
		
		private static final String KEY_JOB_STATUS				= "jobStatus";
		private static final String KEY_JOB_STATUS_REASONS		= "jobStatusReasons";
		private static final String KEY_SCANNED_COUNT			= "scannedCount";
		private static final String KEY_RESET_ORIGINALCOUNT		= "resetOriginalCount";
		private static final String KEY_REMAINING_TIME_OF_WAITING_NEXT_ORIGINAL = "remainingTimeOfWaitingNextOriginal";
		private static final String KEY_SCANNED_THUMBNAIL_URI	= "scannedThumbnailUri";
		
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
			return getNumberValue(KEY_RESET_ORIGINALCOUNT);
		}
		
        /*
         * remainingTimeOfWaitingNextOriginal (Number)
         */
        public Integer getRemainingTimeOfWaitingNextOriginal() {
            return getNumberValue(KEY_REMAINING_TIME_OF_WAITING_NEXT_ORIGINAL);
        }
        
		/*
		 * scannedThumbnailUri (String)
		 */
		public String getScannedThumbnailUri() {
			return getStringValue(KEY_SCANNED_THUMBNAIL_URI);
		}
		
	}
	
	public static class FilingInfo extends Element {
		
		private static final String KEY_JOB_STATUS					= "jobStatus";
		private static final String KEY_JOB_STATUS_REASONS			= "jobStatusReasons";
		private static final String KEY_FILED_PAGE_COUNT			= "filedPageCount";
		private static final String KEY_FILE_URI					= "fileUri";
		
		FilingInfo(Map<String, Object> values) {
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
		 * filedPageCount (Number)
		 */
		public Integer getFiledPageCount() {
		    return getNumberValue(KEY_FILED_PAGE_COUNT);
		}
		
		/*
		 * fileUri (String)
		 */
		public String getFileUri() {
			return getStringValue(KEY_FILE_URI);
		}
		
	}
	
	public static class OcringInfo extends Element {
		
		private static final String KEY_JOB_STATUS					= "jobStatus";
		private static final String KEY_JOB_STATUS_REASONS			= "jobStatusReasons";
		private static final String KEY_OCRED_PAGE_COUNT            = "ocredPageCount";
		private static final String KEY_OMITTED_BLANK_PAGE_COUNT	= "omittedBlankPageCount";
		private static final String KEY_OCRDATA_URI					= "ocrdataUri";
		
		OcringInfo(Map<String, Object> values) {
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
		 * ocredPageCount (Number)
		 */
		public Integer getOcredPageCount() {
		    return getNumberValue(KEY_OCRED_PAGE_COUNT);
		}
		
		/*
		 * omittedBlankPageCount (Number)
		 */
		public Integer getOmittedBlankPageCount() {
			return getNumberValue(KEY_OMITTED_BLANK_PAGE_COUNT);
		}
		
		/*
		 * ocrdataUri (String)
		 */
		public String getOcrdataUri() {
			return getStringValue(KEY_OCRDATA_URI);
		}
		
	}
	
	public static class SendingInfo extends Element {
		
		private static final String KEY_JOB_STATUS					= "jobStatus";
		private static final String KEY_JOB_STATUS_REASONS			= "jobStatusReasons";
		private static final String KEY_SENT_DESTINATION_COUNT		= "sentDestinationCount";
		private static final String KEY_TOTAL_DESTINATION_COUNT		= "totalDestinationCount";
		private static final String KEY_OMITTED_BLANK_PAGE_COUNT	= "omittedBlankPageCount";
		
		SendingInfo(Map<String, Object> values) {
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
		 * sentDestinationCount (Number)
		 */
		public Integer getSentDestinationCount() {
			return getNumberValue(KEY_SENT_DESTINATION_COUNT);
		}
		
		/*
		 * totalDestinationCount (Number)
		 */
		public Integer getTotalDestinationCount() {
			return getNumberValue(KEY_TOTAL_DESTINATION_COUNT);
		}
		
		/*
		 * omittedBlankPageCount (Number)
		 */
		public Integer getOmittedBlankPageCount() {
			return getNumberValue(KEY_OMITTED_BLANK_PAGE_COUNT);
		}
		
	}
	
}
