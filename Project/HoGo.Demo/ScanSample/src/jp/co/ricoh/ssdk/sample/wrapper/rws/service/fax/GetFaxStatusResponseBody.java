/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.wrapper.rws.service.fax;

import java.util.List;
import java.util.Map;

import jp.co.ricoh.ssdk.sample.wrapper.common.ArrayElement;
import jp.co.ricoh.ssdk.sample.wrapper.common.Element;
import jp.co.ricoh.ssdk.sample.wrapper.common.ResponseBody;

public class GetFaxStatusResponseBody extends Element implements ResponseBody {

    private static final String KEY_FAX_STATUS          = "faxStatus";
    private static final String KEY_FAX_STATUS_REASONS  = "faxStatusReasons";
    private static final String KEY_REMAINING_MEMORY    = "remainingMemory";
    private static final String KEY_OCCURED_ERROR_LEVEL = "occuredErrorLevel";

    public GetFaxStatusResponseBody(Map<String, Object> values) {
        super(values);
    }

    /*
     * faxStatus (String)
     */
    public String getFaxStatus() {
        return getStringValue(KEY_FAX_STATUS);
    }

    /*
     * faxStatusReasons (Array[Object])
     */
    public FaxStatusReasonsArray getFaxStatusReasons() {
        List<Map<String, Object>> value = getArrayValue(KEY_FAX_STATUS_REASONS);
        if (value == null) {
            return null;
        }
        return new FaxStatusReasonsArray(value);
    }

    /*
     * remainingMemory (Number)
     */
    public Integer getRemainingMemory() {
        return getNumberValue(KEY_REMAINING_MEMORY);
    }
    
    /*
     * occuredErrorLevel (String)
     */
    public String getOccuredErrorLevel() {
        return getStringValue(KEY_OCCURED_ERROR_LEVEL);
    }
    
    
    public static class FaxStatusReasonsArray extends ArrayElement<FaxStatusReasons> {

        FaxStatusReasonsArray(List<Map<String, Object>> list) {
            super(list);
        }

        @Override
        protected FaxStatusReasons createElement(Map<String, Object> values) {
            return new FaxStatusReasons(values);
        }
        
    }
    
    public static class FaxStatusReasons extends Element {
        
        private static final String KEY_FAX_STATUS_REASON = "faxStatusReason";
        private static final String KEY_SEVERITY          = "severity";

        FaxStatusReasons(Map<String, Object> values) {
            super(values);
        }
        
        /*
         * faxStatusReason (String)
         */
        public String getFaxStatusReason() {
            return getStringValue(KEY_FAX_STATUS_REASON);
        }
        
        /*
         * severity (String)
         */
        public String getSeverity() {
            return getStringValue(KEY_SEVERITY);
        }

    }

}
