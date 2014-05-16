/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.wrapper.rws.property;

import java.util.Map;

import jp.co.ricoh.ssdk.sample.wrapper.common.Element;
import jp.co.ricoh.ssdk.sample.wrapper.common.ResponseBody;

public class GetDeviceInfoResponseBody extends Element implements ResponseBody {

    private static final String KEY_DEVICE_DESCRIPTION  = "deviceDescription";
    private static final String KEY_PLOTTER             = "plotter";
    private static final String KEY_SCANNER             = "scanner";

    GetDeviceInfoResponseBody(Map<String, Object> values) {
        super(values);
    }

    /*
     * deviceDescription (Object)
     */
    public DeviceDescription getDeviceDescription() {
        Map<String, Object> value = getObjectValue(KEY_DEVICE_DESCRIPTION);
        if (value == null) {
            return null;
        }
        return new DeviceDescription(value);
    }

    /*
     * plotter (Object)
     */
    public Plotter getPlotter() {
        Map<String, Object> value = getObjectValue(KEY_PLOTTER);
        if (value == null) {
            return null;
        }
        return new Plotter(value);
    }

    /*
     * scanner (Object)
     */
    public Scanner getScanner() {
        Map<String, Object> value = getObjectValue(KEY_SCANNER);
        if (value == null) {
            return null;
        }
        return new Scanner(value);
    }


    public static class DeviceDescription extends Element {

        private static final String KEY_SERIAL_NUMBER   = "serialNumber";
        private static final String KEY_DESTINATION     = "destination";
        private static final String KEY_LOCATION        = "location";
        private static final String KEY_DESCRIPTION     = "description";
        private static final String KEY_MODEL_NAME      = "modelName";
        private static final String KEY_VENDOR_NAME     = "vendorName";
        private static final String KEY_DEVICE_UUID     = "deviceUUID";

        DeviceDescription(Map<String, Object> values) {
            super(values);
        }

        /*
         * serialNumber (String)
         */
        public String getSerialNumber() {
            return getStringValue(KEY_SERIAL_NUMBER);
        }

        /*
         * destination (String)
         */
        public String getDestination() {
            return getStringValue(KEY_DESTINATION);
        }

        /*
         * location (String)
         */
        public String getLocation() {
            return getStringValue(KEY_LOCATION);
        }

        /*
         * description (String)
         */
        public String getDescription() {
            return getStringValue(KEY_DESCRIPTION);
        }

        /*
         * modelName (String)
         */
        public String getModelName() {
            return getStringValue(KEY_MODEL_NAME);
        }

        /*
         * vendorName (String)
         */
        public String getVendorName() {
            return getStringValue(KEY_VENDOR_NAME);
        }

        /*
         * deviceUUID (String)
         */
        public String getDeviceUUID() {
            return getStringValue(KEY_DEVICE_UUID);
        }

    }

    public static class Plotter extends Element {

        private static final String KEY_COLOR_SUPPORTED = "colorSupported";

        Plotter(Map<String, Object> values) {
            super(values);
        }

        /*
         * colorSupported (String)
         */
        public String getColorSupported() {
            return getStringValue(KEY_COLOR_SUPPORTED);
        }

    }

    public static class Scanner extends Element {

        private static final String KEY_COLOR_SUPPORTED = "colorSupported";

        Scanner(Map<String, Object> values) {
            super(values);
        }

        /*
         * colorSupported (String)
         */
        public String getColorSupported() {
            return getStringValue(KEY_COLOR_SUPPORTED);
        }

    }

}
