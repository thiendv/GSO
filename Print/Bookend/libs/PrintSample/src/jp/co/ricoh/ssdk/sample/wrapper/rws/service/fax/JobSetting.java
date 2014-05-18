/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.wrapper.rws.service.fax;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.ricoh.ssdk.sample.wrapper.common.ArrayElement;
import jp.co.ricoh.ssdk.sample.wrapper.common.Utils;
import jp.co.ricoh.ssdk.sample.wrapper.common.WritableElement;

public class JobSetting extends WritableElement {

    public static final String KEY_AUTO_CORRECT_JOB_SETTING = "autoCorrectJobSetting";
    public static final String KEY_JOB_MODE                 = "jobMode";
    public static final String KEY_ORIGINAL_SIZE            = "originalSize";
    public static final String KEY_SCAN_STAMP               = "scanStamp";
    public static final String KEY_ORIGINAL_SIDE            = "originalSide";
    public static final String KEY_ORIGINAL_ORIENTATION     = "originalOrientation";
    public static final String KEY_ORIGINAL_PREVIEW         = "originalPreview";
    public static final String KEY_ORIGINAL_TYPE            = "originalType";
    public static final String KEY_FAX_RESOLUTION           = "faxResolution";
    public static final String KEY_AUTO_DENSITY             = "autoDensity";
    public static final String KEY_MANUAL_DENSITY           = "manualDensity";
    public static final String KEY_EMAIL_SETTING            = "emailSetting";
    public static final String KEY_FAX_SETTING              = "faxSetting";
    public static final String KEY_DESTINATION_SETTING      = "destinationSetting";

    JobSetting(Map<String, Object> values) {
        super(values);
    }

    /*
     * autoCorrectJobSetting (Boolean)
     */
    public Boolean getAutoCorrectJobSetting() {
        return getBooleanValue(KEY_AUTO_CORRECT_JOB_SETTING);
    }
    public void setAutoCorrectJobSetting(Boolean value) {
        setBooleanValue(KEY_AUTO_CORRECT_JOB_SETTING, value);
    }
    public Boolean removeAutoCorrectJobSetting() {
        return removeBooleanValue(KEY_AUTO_CORRECT_JOB_SETTING);
    }

    /*
     * jobMode (String)
     */
    public String getJobMode() {
        return getStringValue(KEY_JOB_MODE);
    }
    public void setJobMode(String value) {
        setStringValue(KEY_JOB_MODE, value);
    }
    public String removeJobMode() {
        return removeStringValue(KEY_JOB_MODE);
    }

    /*
     * originalSize (String)
     */
    public String getOriginalSize() {
        return getStringValue(KEY_ORIGINAL_SIZE);
    }
    public void setOriginalSize(String value) {
        setStringValue(KEY_ORIGINAL_SIZE, value);
    }
    public String removeOriginalSize() {
        return removeStringValue(KEY_ORIGINAL_SIZE);
    }

    /*
     * scanStamp (Boolean)
     */
    public Boolean getScanStamp() {
        return getBooleanValue(KEY_SCAN_STAMP);
    }
    public void setScanStamp(Boolean value) {
        setBooleanValue(KEY_SCAN_STAMP, value);
    }
    public Boolean removeScanStamp() {
        return removeBooleanValue(KEY_SCAN_STAMP);
    }

    /*
     * originalSide (String)
     */
    public String getOriginalSide() {
        return getStringValue(KEY_ORIGINAL_SIDE);
    }
    public void setOriginalSide(String value) {
        setStringValue(KEY_ORIGINAL_SIDE, value);
    }
    public String removeOriginalSide() {
        return removeStringValue(KEY_ORIGINAL_SIDE);
    }

    /*
     * originalOrientation (String)
     */
    public String getOriginalOrientation() {
        return getStringValue(KEY_ORIGINAL_ORIENTATION);
    }
    public void setOriginalOrientation(String value) {
        setStringValue(KEY_ORIGINAL_ORIENTATION, value);
    }
    public String removeOriginalOrientation() {
        return removeStringValue(KEY_ORIGINAL_ORIENTATION);
    }

    /*
     * originalPreview (Boolean)
     */
    public Boolean getOriginalPreview() {
        return getBooleanValue(KEY_ORIGINAL_PREVIEW);
    }
    public void setOriginalPreview(Boolean value) {
        setBooleanValue(KEY_ORIGINAL_PREVIEW, value);
    }
    public Boolean removeOriginalPreview() {
        return removeBooleanValue(KEY_ORIGINAL_PREVIEW);
    }

    /*
     * originalType (String)
     */
    public String getOriginalType() {
        return getStringValue(KEY_ORIGINAL_TYPE);
    }
    public void setOriginalType(String value) {
        setStringValue(KEY_ORIGINAL_TYPE, value);
    }
    public String removeOriginalType() {
        return removeStringValue(KEY_ORIGINAL_TYPE);
    }

    /*
     * faxResolution (String)
     */
    public String getFaxResolution() {
        return getStringValue(KEY_FAX_RESOLUTION);
    }
    public void setFaxResolution(String value) {
        setStringValue(KEY_FAX_RESOLUTION, value);
    }
    public String removeFaxResolution() {
        return removeStringValue(KEY_FAX_RESOLUTION);
    }

    /*
     * autoDensity (Boolean)
     */
    public Boolean getAutoDensity() {
        return getBooleanValue(KEY_AUTO_DENSITY);
    }
    public void setAutoDensity(Boolean value) {
        setBooleanValue(KEY_AUTO_DENSITY, value);
    }
    public Boolean removeAutoDensity() {
        return removeBooleanValue(KEY_AUTO_DENSITY);
    }

    /*
     * manualDensity (Number)
     */
    public Integer getManualDensity() {
        return getNumberValue(KEY_MANUAL_DENSITY);
    }
    public void setManualDensity(Integer value) {
        setNumberValue(KEY_MANUAL_DENSITY, value);
    }
    public Integer removeManualDensity() {
        return removeNumberValue(KEY_MANUAL_DENSITY);
    }

    /*
     * emailSetting (Object)
     */
    public EmailSetting getEmailSetting() {
        Map<String, Object> value = getObjectValue(KEY_EMAIL_SETTING);
        if (value == null) {
            value = Utils.createElementMap();
            setObjectValue(KEY_EMAIL_SETTING, value);
        }
        return new EmailSetting(value);
    }
//  public void setEmailSetting(EmailSetting value) {
//      throw new UnsupportedOperationException();
//      }
    public EmailSetting removeEmailSetting() {
        Map<String, Object> value = removeObjectValue(KEY_EMAIL_SETTING);
            if (value == null) {
            return null;
        }
        return new EmailSetting(value);
    }

    /*
     * faxSetting (Object)
     */
    public FaxSetting getFaxSetting() {
        Map<String, Object> value = getObjectValue(KEY_FAX_SETTING);
        if (value == null) {
            value = Utils.createElementMap();
            setObjectValue(KEY_FAX_SETTING, value);
        }
        return new FaxSetting(value);
    }
//  public void setFaxSetting(FaxSetting value) {
//      throw new UnsupportedOperationException();
//      }
    public FaxSetting removeFaxSetting() {
        Map<String, Object> value = removeObjectValue(KEY_FAX_SETTING);
            if (value == null) {
            return null;
        }
        return new FaxSetting(value);
    }

    /*
     * destinationSetting (Array[Object])
     */
    public DestinationSettingArray getDestinationSetting() {
        List<Map<String, Object>> value = getArrayValue(KEY_DESTINATION_SETTING);
        if (value == null) {
            value = Utils.createElementList();
            setArrayValue(KEY_DESTINATION_SETTING, value);
        }
        return new DestinationSettingArray(value);
    }
//  public void setDestinationSetting(DestinationSetting value) {
//      throw new UnsupportedOperationException();
//      }
    public DestinationSettingArray removeDestinationSetting() {
        List<Map<String, Object>> value = removeArrayValue(KEY_DESTINATION_SETTING);
            if (value == null) {
            return null;
        }
        return new DestinationSettingArray(value);
    }


    public static class EmailSetting extends WritableElement {

        public static final String KEY_SUBJECT          = "subject";
        public static final String KEY_BODY             = "body";
        public static final String KEY_SMIME_SIGNATURE  = "smimeSignature";
        public static final String KEY_SMIME_ENCRYPTION = "smimeEncryption";

        EmailSetting(Map<String, Object> values) {
            super(values);
        }

        /*
         * subject (String)
         */
        public String getSubject() {
            return getStringValue(KEY_SUBJECT);
        }
        public void setSubject(String value) {
            setStringValue(KEY_SUBJECT, value);
        }
        public String removeSubject() {
            return removeStringValue(KEY_SUBJECT);
        }

        /*
         * body (String)
         */
        public String getBody() {
            return getStringValue(KEY_BODY);
        }
        public void setBody(String value) {
            setStringValue(KEY_BODY, value);
        }
        public String removeBody() {
            return removeStringValue(KEY_BODY);
        }

        /*
         * smimeSignature (Boolean)
         */
        public Boolean getSmimeSignature() {
            return getBooleanValue(KEY_SMIME_SIGNATURE);
        }
        public void setSmimeSignature(Boolean value) {
            setBooleanValue(KEY_SMIME_SIGNATURE, value);
        }
        public Boolean removeSmimeSignature() {
            return removeBooleanValue(KEY_SMIME_SIGNATURE);
        }

        /*
         * smimeEncryption (Boolean)
         */
        public Boolean getSmimeEncryption() {
            return getBooleanValue(KEY_SMIME_ENCRYPTION);
        }
        public void setSmimeEncryption(Boolean value) {
            setBooleanValue(KEY_SMIME_ENCRYPTION, value);
        }
        public Boolean removeSmimeEncryption() {
            return removeBooleanValue(KEY_SMIME_ENCRYPTION);
        }
    }

    public static class FaxSetting extends WritableElement {

        public static final String KEY_SEND_LATER               = "sendLater";
        public static final String KEY_SEND_LATER_TIME          = "sendLaterTime";
        public static final String KEY_STANDARD_MESSAGE         = "standardMessage";
        public static final String KEY_AUTO_REDUCE              = "autoReduce";
        public static final String KEY_LABEL_INSERTION          = "labelInsertion";
        public static final String KEY_CLOSED_NETWORK           = "closedNetwork";
        public static final String KEY_FAX_HEADER_PRINT         = "faxHeaderPrint";
        public static final String KEY_SENDER_ENTRY_ID          = "senderEntryId";
        public static final String KEY_STAMP_SENDER_NAME        = "stampSenderName";
        public static final String KEY_EMAIL_SEND_RESULT        = "emailSendResult";
        public static final String KEY_SUB_CODE_TRANSMISSION    = "subCodeTransmission";

        FaxSetting(Map<String, Object> values) {
            super(values);
        }

        /*
         * sendLater (Boolean)
         */
        public Boolean getSendLater() {
            return getBooleanValue(KEY_SEND_LATER);
        }
        public void setSendLater(Boolean value) {
            setBooleanValue(KEY_SEND_LATER, value);
        }
        public Boolean removeSendLater() {
            return removeBooleanValue(KEY_SEND_LATER);
        }

        /*
         * sendLaterTime (String)
         */
        public String getSendLaterTime() {
            return getStringValue(KEY_SEND_LATER_TIME);
        }
        public void setSendLaterTime(String value) {
            setStringValue(KEY_SEND_LATER_TIME, value);
        }
        public String removeSendLaterTime() {
            return removeStringValue(KEY_SEND_LATER_TIME);
        }

        /*
         * standardMessage (String)
         */
        public String getStandardMessage() {
            return getStringValue(KEY_STANDARD_MESSAGE);
        }
        public void setStandardMessage(String value) {
            setStringValue(KEY_STANDARD_MESSAGE, value);
        }
        public String removeStandardMessage() {
            return removeStringValue(KEY_STANDARD_MESSAGE);
        }

        /*
         * autoReduce (Boolean)
         */
        public Boolean getAutoReduce() {
            return getBooleanValue(KEY_AUTO_REDUCE);
        }
        public void setAutoReduce(Boolean value) {
            setBooleanValue(KEY_AUTO_REDUCE, value);
        }
        public Boolean removeAutoReduce() {
            return removeBooleanValue(KEY_AUTO_REDUCE);
        }

        /*
         * labelInsertion (Boolean)
         */
        public Boolean getLabelInsertion() {
            return getBooleanValue(KEY_LABEL_INSERTION);
        }
        public void setLabelInsertion(Boolean value) {
            setBooleanValue(KEY_LABEL_INSERTION, value);
        }
        public Boolean removeLabelInsertion() {
            return removeBooleanValue(KEY_LABEL_INSERTION);
        }

        /*
         * closedNetwork (Boolean)
         */
        public Boolean getClosedNetwork() {
            return getBooleanValue(KEY_CLOSED_NETWORK);
        }
        public void setClosedNetwork(Boolean value) {
            setBooleanValue(KEY_CLOSED_NETWORK, value);
        }
        public Boolean removeClosedNetwork() {
            return removeBooleanValue(KEY_CLOSED_NETWORK);
        }

        /*
         * faxHeaderPrint (String)
         */
        public String getFaxHeaderPrint() {
            return getStringValue(KEY_FAX_HEADER_PRINT);
        }
        public void setFaxHeaderPrint(String value) {
            setStringValue(KEY_FAX_HEADER_PRINT, value);
        }
        public String removeFaxHeaderPrint() {
            return removeStringValue(KEY_FAX_HEADER_PRINT);
        }

        /*
         * senderEntryId (String)
         */
        public String getSenderEntryId() {
            return getStringValue(KEY_SENDER_ENTRY_ID);
        }
        public void setSenderEntryId(String value) {
            setStringValue(KEY_SENDER_ENTRY_ID, value);
        }
        public String removeSenderEntryId() {
            return removeStringValue(KEY_SENDER_ENTRY_ID);
        }

        /*
         * stampSenderName (Boolean)
         */
        public Boolean getStampSenderName() {
            return getBooleanValue(KEY_STAMP_SENDER_NAME);
        }
        public void setStampSenderName(Boolean value) {
            setBooleanValue(KEY_STAMP_SENDER_NAME, value);
        }
        public Boolean removeStampSenderName() {
            return removeBooleanValue(KEY_STAMP_SENDER_NAME);
        }

        /*
         * emailSendResult (Boolean)
         */
        public Boolean getEmailSendResult() {
            return getBooleanValue(KEY_EMAIL_SEND_RESULT);
        }
        public void setEmailSendResult(Boolean value) {
            setBooleanValue(KEY_EMAIL_SEND_RESULT, value);
        }
        public Boolean removeEmailSendResult() {
            return removeBooleanValue(KEY_EMAIL_SEND_RESULT);
        }

        /*
         * subCodeTransmission (Boolean)
         */
        public Boolean getSubCodeTransmission() {
            return getBooleanValue(KEY_SUB_CODE_TRANSMISSION);
        }
        public void setSubCodeTransmission(Boolean value) {
            setBooleanValue(KEY_SUB_CODE_TRANSMISSION, value);
        }
        public Boolean removeSubCodeTransmission() {
            return removeBooleanValue(KEY_SUB_CODE_TRANSMISSION);
        }

    }

    public static class DestinationSettingArray extends ArrayElement<DestinationSetting> {

        DestinationSettingArray(List<Map<String, Object>> list) {
            super(list);
        }

        public boolean add(DestinationSetting value) {
            if (value == null) {
                throw new NullPointerException("value must not be null.");
            }
            return list.add(value.cloneValues());
        }

        public DestinationSetting remove(int index) {
            Map<String, Object> value = list.remove(index);
            if (value == null) {
                return null;
            }
            return createElement(value);
        }

        public void clear() {
            list.clear();
        }

        @Override
        protected DestinationSetting createElement(Map<String, Object> values) {
            return new DestinationSetting(values);
        }

    }

    public static class DestinationSetting extends WritableElement {

        public static final String KEY_DESTINATION_TYPE                 = "destinationType";
        public static final String KEY_ADDRESSBOOK_DESTINATION_SETTING  = "addressbookDestinationSetting";
        public static final String KEY_MANUAL_DESTINATION_SETTING       = "manualDestinationSetting";

        public DestinationSetting() {
            super(new HashMap<String, Object>());
        }

        DestinationSetting(Map<String, Object> values) {
            super(values);
        }

        /*
         * destinationType (String)
         */
        public String getDestinationType() {
            return getStringValue(KEY_DESTINATION_TYPE);
        }
        public void setDestinationType(String value) {
            setStringValue(KEY_DESTINATION_TYPE, value);
        }
        public String removeDestinationType() {
            return removeStringValue(KEY_DESTINATION_TYPE);
        }

        /*
         * addressbookDestinationSetting (Object)
         */
        public AddressbookDestinationSetting getAddressbookDestinationSetting() {
            Map<String, Object> value = getObjectValue(KEY_ADDRESSBOOK_DESTINATION_SETTING);
            if (value == null) {
                value = Utils.createElementMap();
                setObjectValue(KEY_ADDRESSBOOK_DESTINATION_SETTING, value);
            }
            return new AddressbookDestinationSetting(value);
        }
//      public void setAddressbookDestinationSetting(AddressbookDestinationSetting value) {
//          throw new UnsupportedOperationException();
//          }
        public AddressbookDestinationSetting removeAddressbookDestinationSetting() {
            Map<String, Object> value = removeObjectValue(KEY_ADDRESSBOOK_DESTINATION_SETTING);
                if (value == null) {
                return null;
            }
            return new AddressbookDestinationSetting(value);
        }

        /*
         * manualDestinationSetting (Object)
         */
        public ManualDestinationSetting getManualDestinationSetting() {
            Map<String, Object> value = getObjectValue(KEY_MANUAL_DESTINATION_SETTING);
            if (value == null) {
                value = Utils.createElementMap();
                setObjectValue(KEY_MANUAL_DESTINATION_SETTING, value);
            }
            return new ManualDestinationSetting(value);
        }
//      public void setManualDestinationSetting(ManualDestinationSetting value) {
//          throw new UnsupportedOperationException();
//          }
        public ManualDestinationSetting removeManualDestinationSetting() {
            Map<String, Object> value = removeObjectValue(KEY_MANUAL_DESTINATION_SETTING);
                if (value == null) {
                return null;
            }
            return new ManualDestinationSetting(value);
        }
    }

    public static class AddressbookDestinationSetting extends WritableElement {

        private static final String KEY_DESTINATION_KIND    = "destinationKind";
        private static final String KEY_ENTRY_ID            = "entryId";
        private static final String KEY_REGISTRATION_NO     = "registrationNo";
        private static final String KEY_MAIL_TO_CC_BCC      = "mailToCcBcc";

        AddressbookDestinationSetting(Map<String, Object> values) {
            super(values);
        }

        /*
         * destinationKind (String)
         */
        public String getDestinationKind() {
            return getStringValue(KEY_DESTINATION_KIND);
        }
        public void setDestinationKind(String value) {
            setStringValue(KEY_DESTINATION_KIND, value);
        }
        public String removeDestinationKind() {
            return removeStringValue(KEY_DESTINATION_KIND);
        }

        /*
         * entryId (String)
         */
        public String getEntryId() {
            return getStringValue(KEY_ENTRY_ID);
        }
        public void setEntryId(String value) {
            setStringValue(KEY_ENTRY_ID, value);
        }
        public String removeEntryId() {
            return removeStringValue(KEY_ENTRY_ID);
        }

        /*
         * registrationNo (Number)
         */
        public Integer getRegistrationNo() {
            return getNumberValue(KEY_REGISTRATION_NO);
        }
        public void setRegistrationNo(Integer value) {
            setNumberValue(KEY_REGISTRATION_NO, value);
        }
        public Integer removeRegistrationNo() {
            return removeNumberValue(KEY_REGISTRATION_NO);
        }

        /*
         * mailToCcBcc (String)
         */
        public String getMailToCcBcc() {
            return getStringValue(KEY_MAIL_TO_CC_BCC);
        }
        public void setMailToCcBcc(String value) {
            setStringValue(KEY_MAIL_TO_CC_BCC, value);
        }
        public String removeMailToCcBcc() {
            return removeStringValue(KEY_MAIL_TO_CC_BCC);
        }

    }

    public static class ManualDestinationSetting extends WritableElement {

        private static final String KEY_DESTINATION_KIND    = "destinationKind";
        private static final String KEY_FAX_ADDRESS_INFO    = "faxAddressInfo";
        private static final String KEY_MAIL_ADDRESS_INFO   = "mailAddressInfo";

        ManualDestinationSetting(Map<String, Object> values) {
            super(values);
        }

        /*
         * destinationKind (String)
         */
        public String getDestinationKind() {
            return getStringValue(KEY_DESTINATION_KIND);
        }
        public void setDestinationKind(String value) {
            setStringValue(KEY_DESTINATION_KIND, value);
        }
        public String removeDestinationKind() {
            return removeStringValue(KEY_DESTINATION_KIND);
        }

        /*
         * faxAddressInfo (Object)
         */
        public FaxAddressInfo getFaxAddressInfo() {
            Map<String, Object> value = getObjectValue(KEY_FAX_ADDRESS_INFO);
            if (value == null) {
                value = Utils.createElementMap();
                setObjectValue(KEY_FAX_ADDRESS_INFO, value);
            }
            return new FaxAddressInfo(value);
        }
//      public void setFaxAddressInfo(FaxAddressInfo value) {
//          throw new UnsupportedOperationException();
//      }
        public FaxAddressInfo removeFaxAddressInfo() {
            Map<String, Object> value = removeObjectValue(KEY_FAX_ADDRESS_INFO);
            if (value == null) {
                return null;
            }
            return new FaxAddressInfo(value);
        }

        /*
         * mailAddressInfo (Object)
         */
        public MailAddressInfo getMailAddressInfo() {
            Map<String, Object> value = getObjectValue(KEY_MAIL_ADDRESS_INFO);
            if (value == null) {
                value = Utils.createElementMap();
                setObjectValue(KEY_MAIL_ADDRESS_INFO, value);
            }
            return new MailAddressInfo(value);
        }
//      public void setMailAddressInfo(MailAddressInfo value) {
//          throw new UnsupportedOperationException();
//      }
        public MailAddressInfo removeMailAddressInfo() {
            Map<String, Object> value = removeObjectValue(KEY_MAIL_ADDRESS_INFO);
            if (value == null) {
                return null;
            }
            return new MailAddressInfo(value);
        }

    }

    public static class FaxAddressInfo extends WritableElement {

        public static final String KEY_FAX_NUMBER       = "faxNumber";
        public static final String KEY_SUB_CODE         = "subCode";
        public static final String KEY_SID_PASSWORD     = "sidPassword";
        public static final String KEY_SEP_CODE         = "sepCode";
        public static final String KEY_PWD_PASSWORD     = "pwdPassword";
        public static final String KEY_LINE             = "line";

        FaxAddressInfo(Map<String, Object> values) {
            super(values);
        }

        /*
         * faxNumber (String)
         */
        public String getFaxNumber() {
            return getStringValue(KEY_FAX_NUMBER);
        }
        public void setFaxNumber(String value) {
            setStringValue(KEY_FAX_NUMBER, value);
        }
        public String removeFaxNumber() {
            return removeStringValue(KEY_FAX_NUMBER);
        }

        /*
         * subCode (String)
         */
        public String getSubCode() {
            return getStringValue(KEY_SUB_CODE);
        }
        public void setSubCode(String value) {
            setStringValue(KEY_SUB_CODE, value);
        }
        public String removeSubCode() {
            return removeStringValue(KEY_SUB_CODE);
        }

        /*
         * sidPassword (String)
         */
        public String getSidPassword() {
            return getStringValue(KEY_SID_PASSWORD);
        }
        public void setSidPassword(String value) {
            setStringValue(KEY_SID_PASSWORD, value);
        }
        public String removeSidPassword() {
            return removeStringValue(KEY_SID_PASSWORD);
        }

        /*
         * sepCode (String)
         */
        public String getSepCode() {
            return getStringValue(KEY_SEP_CODE);
        }
        public void setSepCode(String value) {
            setStringValue(KEY_SEP_CODE, value);
        }
        public String removeSepCode() {
            return removeStringValue(KEY_SEP_CODE);
        }

        /*
         * pwdPassword (String)
         */
        public String getPwdPassword() {
            return getStringValue(KEY_PWD_PASSWORD);
        }
        public void setPwdPassword(String value) {
            setStringValue(KEY_PWD_PASSWORD, value);
        }
        public String removePwdPassword() {
            return removeStringValue(KEY_PWD_PASSWORD);
        }

        /*
         * line (String)
         */
        public String getLine() {
            return getStringValue(KEY_LINE);
        }
        public void setLine(String value) {
            setStringValue(KEY_LINE, value);
        }
        public String removeLine() {
            return removeStringValue(KEY_LINE);
        }
    }

    public static class MailAddressInfo extends WritableElement {

        private static final String KEY_MAIL_ADDRESS        = "mailAddress";
        private static final String KEY_DIRECT_SMTP         = "directSmtp";
        private static final String KEY_MAIL_TO_CC_BCC      = "mailToCcBcc";

        MailAddressInfo(Map<String, Object> values) {
            super(values);
        }

        /*
         * mailAddress (String)
         */
        public String getMailAddress() {
            return getStringValue(KEY_MAIL_ADDRESS);
        }
        public void setMailAddress(String value) {
            setStringValue(KEY_MAIL_ADDRESS, value);
        }
        public String removeMailAddress() {
            return removeStringValue(KEY_MAIL_ADDRESS);
        }

        /*
         * directSmtp (Boolean)
         */
        public Boolean getDirectSmtp() {
            return getBooleanValue(KEY_DIRECT_SMTP  );
        }
        public void setDirectSmtp(Boolean value) {
            setBooleanValue(KEY_DIRECT_SMTP , value);
        }
        public Boolean removeDirectSmtp() {
            return removeBooleanValue(KEY_DIRECT_SMTP);
        }

        /*
         * mailToCcBcc (String)
         */
        public String getMailToCcBcc() {
            return getStringValue(KEY_MAIL_TO_CC_BCC);
        }
        public void setMailToCcBcc(String value) {
            setStringValue(KEY_MAIL_TO_CC_BCC, value);
        }
        public String removeMailToCcBcc() {
            return removeStringValue(KEY_MAIL_TO_CC_BCC);
        }

    }

}
