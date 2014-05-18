/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.wrapper.rws.service.fax;

import java.util.List;
import java.util.Map;

import jp.co.ricoh.ssdk.sample.wrapper.common.Element;
import jp.co.ricoh.ssdk.sample.wrapper.common.ResponseBody;

public class GetJobStatusResponseBody extends Element implements ResponseBody {

    private static final String KEY_JOB_ID                  = "jobId";
    private static final String KEY_JOB_STATUS              = "jobStatus";
    private static final String KEY_JOB_STATUS_REASONS      = "jobStatusReasons";
    private static final String KEY_SCANNING_INFO           = "scanningInfo";
    private static final String KEY_VALIDATE_ONLY           = "validateOnly";
    private static final String KEY_JOB_SETTING             = "jobSetting";

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

        private static final String KEY_JOB_STATUS              = "jobStatus";
        private static final String KEY_JOB_STATUS_REASONS      = "jobStatusReasons";
        private static final String KEY_SCANNED_COUNT           = "scannedCount";
        private static final String KEY_SCANNED_THUMBNAIL_URI   = "scannedThumbnailUri";

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
         * scannedThumbnailUri (String)
         */
        public String getScannedThumbnailUri() {
            return getStringValue(KEY_SCANNED_THUMBNAIL_URI);
        }

    }

}
