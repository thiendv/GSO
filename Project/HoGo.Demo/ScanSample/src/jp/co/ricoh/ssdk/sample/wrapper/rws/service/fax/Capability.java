/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.wrapper.rws.service.fax;

import java.util.List;
import java.util.Map;

import jp.co.ricoh.ssdk.sample.wrapper.common.ArrayElement;
import jp.co.ricoh.ssdk.sample.wrapper.common.CapabilityElement;
import jp.co.ricoh.ssdk.sample.wrapper.common.DateElement;
import jp.co.ricoh.ssdk.sample.wrapper.common.MaxLengthElement;
import jp.co.ricoh.ssdk.sample.wrapper.common.RangeElement;

public class Capability extends CapabilityElement  {

    private static final String KEY_AUTO_CORRECT_JOB_SETTING_LIST   = "autoCorrectJobSettingList";
    private static final String KEY_JOB_MODE_LIST                   = "jobModeList";
    private static final String KEY_ORIGINAL_SIZE_LIST              = "originalSizeList";
    private static final String KEY_ORIGINAL_USER_SIZE_LIST         = "originalUserSizeList";
    private static final String KEY_SCAN_STAMP_LIST                 = "scanStampList";
    private static final String KEY_ORIGINAL_SIDE_LIST              = "originalSideList";
    private static final String KEY_ORIGINAL_ORIENTATION_LIST       = "originalOrientationList";
    private static final String KEY_ORIGINAL_PREVIEW_LIST           = "originalPreviewList";
    private static final String KEY_ORIGINAL_TYPE_LIST              = "originalTypeList";
    private static final String KEY_FAX_RESOLUTION_LIST             = "faxResolutionList";
    private static final String KEY_AUTO_DENSITY_LIST               = "autoDensityList";
    private static final String KEY_MANUAL_DENSITY_RANGE            = "manualDensityRange";
    private static final String KEY_EMAIL_SETTING_CAPABILITY        = "emailSettingCapability";
    private static final String KEY_FAX_SETTING_CAPABILITY          = "faxSettingCapability";
    private static final String KEY_DESTINATION_SETTING_CAPABILITY  = "destinationSettingCapability";
    private static final String KEY_FAX_INFO_CAPABILITY             = "faxInfoCapability";

    Capability(Map<String, Object> values) {
        super(values);
    }

    /*
     * autoCorrectJobSettingList (Array[Boolean])
     */
    public List<Boolean> getAutoCorrectJobSettingList() {
        return getArrayValue(KEY_AUTO_CORRECT_JOB_SETTING_LIST);
    }

    /*
     * jobModeList (Array[String])
     */
    public List<String> getJobModeList() {
        return getArrayValue(KEY_JOB_MODE_LIST);
    }

    /*
     * originalSizeList (Array[String])
     */
    public List<String> getOriginalSizeList() {
        return getArrayValue(KEY_ORIGINAL_SIZE_LIST);
    }

    /*
     * originalUserSizeList (Array[Object])
     */
    public OriginalUserSizeList getOriginalUserSizeList() {
        List<Map<String, Object>> mapArray = getArrayValue(KEY_ORIGINAL_USER_SIZE_LIST);
        if (mapArray == null) {
            return null;
        }
        return new OriginalUserSizeList(mapArray);
    }

    /*
     * scanStampList (Array[Boolean])
     */
    public List<Boolean> getScanStampList() {
        return getArrayValue(KEY_SCAN_STAMP_LIST);
    }

    /*
     * originalSideList (Array[String])
     */
    public List<String> getOriginalSideList() {
        return getArrayValue(KEY_ORIGINAL_SIDE_LIST);
    }

    /*
     * originalOrientationList (Array[String])
     */
    public List<String> getOriginalOrientationList() {
        return getArrayValue(KEY_ORIGINAL_ORIENTATION_LIST);
    }

    /*
     * originalPreviewList (Array[Boolean])
     */
    public List<Boolean> getOriginalPreviewList() {
        return getArrayValue(KEY_ORIGINAL_PREVIEW_LIST);
    }

    /*
     * originalTypeList (Array[String])
     */
    public List<String> getOriginalTypeList() {
        return getArrayValue(KEY_ORIGINAL_TYPE_LIST);
    }

    /*
     * faxResolutionList (Array[String])
     */
    public List<String> getFaxResolutionList() {
        return getArrayValue(KEY_FAX_RESOLUTION_LIST);
    }

    /*
     * autoDensityList (Array[Boolean])
     */
    public List<Boolean> getAutoDensityList() {
        return getArrayValue(KEY_AUTO_DENSITY_LIST);
    }

    /*
     * manualDensityRange (Range)
     */
    public RangeElement getManualDensityRange() {
        return getRangeValue(KEY_MANUAL_DENSITY_RANGE);
    }

    /*
     * emailSettingCapability (Object)
     */
    public EmailSettingCapability getEmailSettingCapability() {
        Map<String, Object> mapValue = getObjectValue(KEY_EMAIL_SETTING_CAPABILITY);
        if (mapValue == null) {
            return null;
        }
        return new EmailSettingCapability(mapValue);
    }

    /*
     * faxSettingCapability (Object)
     */
    public FaxSettingCapability getFaxSettingCapability() {
        Map<String, Object> mapValue = getObjectValue(KEY_FAX_SETTING_CAPABILITY);
        if (mapValue == null) {
            return null;
        }
        return new FaxSettingCapability(mapValue);
    }

    /*
     * destinationSettingCapability (Object)
     */
    public DestinationSettingCapability getDestinationSettingCapability() {
        Map<String, Object> mapValue = getObjectValue(KEY_DESTINATION_SETTING_CAPABILITY);
        if (mapValue == null) {
            return null;
        }
        return new DestinationSettingCapability(mapValue);
    }

    /*
     * faxInfoCapability (Object)
     */
    public FaxInfoCapability getFaxInfoCapability() {
        Map<String, Object> mapValue = getObjectValue(KEY_FAX_INFO_CAPABILITY);
        if (mapValue == null) {
            return null;
        }
        return new FaxInfoCapability(mapValue);
    }


    public static class OriginalUserSizeList extends ArrayElement<OriginalUserSize>{
        protected OriginalUserSizeList(List<Map<String, Object>> list) {
            super(list);
        }

        @Override
        protected OriginalUserSize createElement(Map<String, Object> values) {
            return new OriginalUserSize(values);
        }
    }

    public static class OriginalUserSize extends CapabilityElement {

        private static final String KEY_ID          = "id";
        private static final String KEY_SIZE_X      = "sizeX";
        private static final String KEY_SIZE_Y      = "sizeY";

        OriginalUserSize(Map<String, Object> values) {
            super(values);
        }

        /*
         * id (String)
         */
        public String getId() {
            return getStringValue(KEY_ID);
        }

        /*
         * sizeX (String)
         */
        public String getSizeX() {
            return getStringValue(KEY_SIZE_X);
        }

        /*
         * sizeY (String)
         */
        public String getSizeY() {
            return getStringValue(KEY_SIZE_Y);
        }

    }

    public static class EmailSettingCapability extends CapabilityElement {

        public static final String KEY_SUBJECT_LENGTH           = "subjectLength";
        public static final String KEY_BODY_LENGTH              = "bodyLength";
        public static final String KEY_ADMIN_ADDRESS_AS_SENDER  = "adminAddresAsSender";
        public static final String KEY_SMIME_SIGNATURE_LIST     = "smimeSignatureList";
        public static final String KEY_SMIME_ENCRYPTION_LIST    = "smimeEncryptionList";

        EmailSettingCapability(Map<String, Object> values) {
            super(values);
        }

        /*
         * subjectLength (MaxLength)
         */
        public MaxLengthElement getSubjectLength() {
            return getMaxLengthValue(KEY_SUBJECT_LENGTH);
        }

        /*
         * bodyLength (MaxLength)
         */
        public MaxLengthElement getBodyLength() {
            return getMaxLengthValue(KEY_BODY_LENGTH);
        }

        /*
         * adminAddresAsSender (Boolean)
         */
        public Boolean getAdminAddresAsSender() {
            return getBooleanValue(KEY_ADMIN_ADDRESS_AS_SENDER);
        }

        /*
         * smimeSignatureList (Array[Boolean])
         */
        public List<Boolean> getSmimeSignatureList() {
            return getArrayValue(KEY_SMIME_SIGNATURE_LIST);
        }

        /*
         * smimeEncryptionList (Array[Boolean])
         */
        public List<Boolean> getSmimeEncryptionList() {
            return getArrayValue(KEY_SMIME_ENCRYPTION_LIST);
        }

    }

    public static class FaxSettingCapability extends CapabilityElement {

        public static final String KEY_SEND_LATER_LIST              = "sendLaterList";
        public static final String KEY_SEND_LATER_TIME_FORMAT       = "sendLaterTimeFormat";
        public static final String KEY_STANDARD_MESSAGE_LIST        = "standardMessageList";
        public static final String KEY_STANDARD_MESSAGE_TEXT_LIST   = "standardMessageTextList";
        public static final String KEY_AUTO_REDUCE_LIST             = "autoReduceList";
        public static final String KEY_LABEL_INSERTION_LIST         = "labelInsertionList";
        public static final String KEY_CLOSED_NETWORK_LIST          = "closedNetworkList";
        public static final String KEY_FAX_HEADER_PRINT_LIST        = "faxHeaderPrintList";
        public static final String KEY_FAX_HEADER_PRINT_TEXT_LIST   = "faxHeaderPrintTextList";
        public static final String KEY_SENDER_ENTRY_ID_LENGTH       = "senderEntryIdLength";
        public static final String KEY_STAMP_SENDER_NAME_LIST       = "stampSenderNameList";
        public static final String KEY_EMAIL_SEND_RESULT_LIST       = "emailSendResultList";
        public static final String KEY_SUB_CODE_TRANSMISSION_LIST   = "subCodeTransmissionList";

        FaxSettingCapability(Map<String, Object> values) {
            super(values);
        }

        /*
         * sendLaterList (Array[Boolean])
         */
        public List<Boolean> getSendLaterList() {
            return getArrayValue(KEY_SEND_LATER_LIST);
        }

        /*
         * sendLaterTimeFormat (Date)
         */
        public DateElement getSendLaterTimeFormat() {
            return getDateValue(KEY_SEND_LATER_TIME_FORMAT);
        }

        /*
         * standardMessageList (Array[String])
         */
        public List<String> getStandardMessageList() {
            return getArrayValue(KEY_STANDARD_MESSAGE_LIST);
        }

        /*
         * standardMessageTextList (Array[Object])
         */
        public StandardMessageTextList getStandardMessageTextList() {
            List<Map<String, Object>> mapArray = getArrayValue(KEY_STANDARD_MESSAGE_TEXT_LIST);
            if (mapArray == null) {
                return null;
            }
            return new StandardMessageTextList(mapArray);
        }

        /*
         * autoReduceList (Array[Boolean])
         */
        public List<Boolean> getAutoReduceList() {
            return getArrayValue(KEY_AUTO_REDUCE_LIST);
        }

        /*
         * labelInsertionList (Array[Boolean])
         */
        public List<Boolean> getLabelInsertionList() {
            return getArrayValue(KEY_LABEL_INSERTION_LIST);
        }

        /*
         * closedNetworkList (Array[Boolean])
         */
        public List<Boolean> getClosedNetworkList() {
            return getArrayValue(KEY_CLOSED_NETWORK_LIST);
        }

        /*
         * faxHeaderPrintList (Array[String])
         */
        public List<String> getFaxHeaderPrintList() {
            return getArrayValue(KEY_FAX_HEADER_PRINT_LIST);
        }

        /*
         * faxHeaderPrintTextList (Array[Object])
         */
        public FaxHeaderPrintTextList getFaxHeaderPrintTextList() {
            List<Map<String, Object>> mapArray = getArrayValue(KEY_FAX_HEADER_PRINT_TEXT_LIST);
            if (mapArray == null) {
                return null;
            }
            return new FaxHeaderPrintTextList(mapArray);
        }

        /*
         * senderEntryIdLength (MaxLength)
         */
        public MaxLengthElement getSenderEntryIdLength() {
            return getMaxLengthValue(KEY_SENDER_ENTRY_ID_LENGTH);
        }

        /*
         * stampSenderNameList (Array[Boolean])
         */
        public List<Boolean> getStampSenderNameList() {
            return getArrayValue(KEY_STAMP_SENDER_NAME_LIST);
        }

        /*
         * emailSendResultList (Array[Boolean])
         */
        public List<Boolean> getEmailSendResultList() {
            return getArrayValue(KEY_EMAIL_SEND_RESULT_LIST);
        }

        /*
         * subCodeTransmissionList (Array[Boolean])
         */
        public List<Boolean> getSubCodeTransmissionList() {
            return getArrayValue(KEY_SUB_CODE_TRANSMISSION_LIST);
        }

    }

    public static class StandardMessageTextList extends ArrayElement<StandardMessageText>{

        StandardMessageTextList(List<Map<String, Object>> list) {
            super(list);
        }

        @Override
        protected StandardMessageText createElement(Map<String, Object> values) {
            return new StandardMessageText(values);
        }

    }

    public static class StandardMessageText extends CapabilityElement {

        private static final String KEY_ID          = "id";
        private static final String KEY_TEXT        = "text";

        StandardMessageText(Map<String, Object> values) {
            super(values);
        }

        /*
         * id (String)
         */
        public String getId() {
            return getStringValue(KEY_ID);
        }

        /*
         * text (String)
         */
        public String getText() {
            return getStringValue(KEY_TEXT);
        }

    }

    public static class FaxHeaderPrintTextList extends ArrayElement<FaxHeaderPrint> {

        FaxHeaderPrintTextList(List<Map<String, Object>> list) {
            super(list);
        }

        @Override
        protected FaxHeaderPrint createElement(Map<String, Object> values) {
            return new FaxHeaderPrint(values);
        }

    }

    public static class FaxHeaderPrint extends CapabilityElement {

        private static final String KEY_ID          = "id";
        private static final String KEY_NAME        = "name";

        public FaxHeaderPrint(Map<String, Object> values) {
            super(values);
        }

        /*
         * id (String)
         */
        public String getId() {
            return getStringValue(KEY_ID);
        }

        /*
         * name (String)
         */
        public String getName() {
            return getStringValue(KEY_NAME);
        }

    }

    public static class DestinationSettingCapability extends CapabilityElement {

        public static final String KEY_DESTINATION_TYPE_LIST                        = "destinationTypeList";
        public static final String KEY_ADDRESSBOOK_DESTINATION_SETTING_CAPABILITY   = "addressbookDestinationSettingCapability";
        public static final String KEY_MANUAL_DESTINATION_SETTING_CAPABILITY        = "manualDestinationSettingCapability";
        public static final String KEY_MAX_BROADCAST_NUMBER                         = "maxBroadcastNumber";

        DestinationSettingCapability(Map<String, Object> values) {
            super(values);
        }

        /*
         * destinationTypeList (Array[String])
         */
        public List<String> getDestinationTypeList() {
            return getArrayValue(KEY_DESTINATION_TYPE_LIST);
        }

        /*
         * addressbookDestinationSettingCapability (Object)
         */
        public AddressbookDestinationSettingCapability getAddressbookDestinationSettingCapability() {
            Map<String, Object> mapValue = getObjectValue(KEY_ADDRESSBOOK_DESTINATION_SETTING_CAPABILITY);
            if (mapValue == null) {
                return null;
            }
            return new AddressbookDestinationSettingCapability(mapValue);
        }

        /*
         * manualDestinationSettingCapability (Object)
         */
        public ManualDestinationSettingCapability getManualDestinationSettingCapability() {
            Map<String, Object> mapValue = getObjectValue(KEY_MANUAL_DESTINATION_SETTING_CAPABILITY);
            if (mapValue == null) {
                return null;
            }
            return new ManualDestinationSettingCapability(mapValue);
        }

        /*
         * maxBroadcastNumber (Object)
         */
        public MaxBroadcastNumber getMaxBroadcastNumber() {
            Map<String, Object> mapValue = getObjectValue(KEY_MAX_BROADCAST_NUMBER);
            if (mapValue == null) {
                return null;
            }
            return new MaxBroadcastNumber(mapValue);
        }

    }

    public static class AddressbookDestinationSettingCapability extends CapabilityElement {

        public static final String KEY_DESTINATION_KIND_LIST    = "destinationKindList";
        public static final String KEY_ENTRY_ID_LENGTH          = "entryIdLength";
        public static final String KEY_REGISTRATION_NO_RANGE    = "registrationNoRange";
        public static final String KEY_MAIL_TO_CC_BCC_LIST      = "mailToCcBccList";

        AddressbookDestinationSettingCapability(Map<String, Object> values) {
            super(values);
        }

        /*
         * destinationKindList (Array[String])
         */
        public List<String> getDestinationKindList() {
            return getArrayValue(KEY_DESTINATION_KIND_LIST);
        }

        /*
         * entryIdLength (MaxLength)
         */
        public MaxLengthElement getEntryIdLength() {
            return getMaxLengthValue(KEY_ENTRY_ID_LENGTH);
        }

        /*
         * registrationNoRange (Range)
         */
        public RangeElement getRegistrationNoRange() {
            return getRangeValue(KEY_REGISTRATION_NO_RANGE);
        }

        /*
         * mailToCcBccList (Array[String])
         */
        public List<String> getMailToCcBccList() {
            return getArrayValue(KEY_MAIL_TO_CC_BCC_LIST);
        }

    }
    
    public static class ManualDestinationSettingCapability extends CapabilityElement {

        public static final String KEY_DESTINATION_KIND_LIST        = "destinationKindList";
        public static final String KEY_FAX_ADDRESS_INFO_CAPABILITY  = "faxAddressInfoCapability";
        public static final String KEY_MAIL_ADDRESS_INFO_CAPABILITY = "mailAddressInfoCapability";

        ManualDestinationSettingCapability(Map<String, Object> values) {
            super(values);
        }

        /*
         * destinationKindList (Array[String])
         */
        public List<String> getDestinationKindList() {
            return getArrayValue(KEY_DESTINATION_KIND_LIST);
        }

        /*
         * faxAddressInfoCapability (Object)
         */
        public FaxAddressInfoCapability getFaxAddressInfoCapability() {
            Map<String, Object> mapValue = getObjectValue(KEY_FAX_ADDRESS_INFO_CAPABILITY);
            if (mapValue == null) {
                return null;
            }
            return new FaxAddressInfoCapability(mapValue);
        }

        /*
         * mailAddressInfoCapability (Object)
         */
        public MailAddressInfoCapability getMailAddressInfoCapability() {
            Map<String, Object> mapValue = getObjectValue(KEY_MAIL_ADDRESS_INFO_CAPABILITY);
            if (mapValue == null) {
                return null;
            }
            return new MailAddressInfoCapability(mapValue);
        }

    }

    public static class FaxAddressInfoCapability extends CapabilityElement {

        public static final String KEY_FAX_NUMBER_LENGTH        = "faxNumberLength";
        public static final String KEY_SUB_CODE_LENGTH          = "subCodeLength";
        public static final String KEY_SID_PASSWORD_LENGTH      = "sidPasswordLength";
        public static final String KEY_SEP_CODE_LENGTH          = "sepCodeLength";
        public static final String KEY_PWD_PASSWORD_LENGTH      = "pwdPasswordLength";
        public static final String KEY_LINE_LIST                = "lineList";
        public static final String KEY_RE_ENTER_COUNT           = "reEnterCount";
        public static final String KEY_USABLE_CHARACTER_LIST    = "usableCharacterList";

        FaxAddressInfoCapability(Map<String, Object> values) {
            super(values);
        }

        /*
         * faxNumberLength (MaxLength)
         */
        public MaxLengthElement getFaxNumberLength() {
            return getMaxLengthValue(KEY_FAX_NUMBER_LENGTH);
        }

        /*
         * subCodeLength (MaxLength)
         */
        public MaxLengthElement getSubCodeLength() {
            return getMaxLengthValue(KEY_SUB_CODE_LENGTH);
        }

        /*
         * sidPasswordLength (MaxLength)
         */
        public MaxLengthElement getSidPasswordLength() {
            return getMaxLengthValue(KEY_SID_PASSWORD_LENGTH);
        }

        /*
         * sepCodeLength (MaxLength)
         */
        public MaxLengthElement getSepCodeLength() {
            return getMaxLengthValue(KEY_SEP_CODE_LENGTH);
        }

        /*
         * pwdPasswordLength (MaxLength)
         */
        public MaxLengthElement getPwdPasswordLength() {
            return getMaxLengthValue(KEY_PWD_PASSWORD_LENGTH);
        }

        /*
         * lineList (Array[String])
         */
        public List<String> getLineList() {
            return getArrayValue(KEY_LINE_LIST);
        }

        /*
         * reEnterCount (Number)
         */
        public Integer getReEnterCount() {
            return getNumberValue(KEY_RE_ENTER_COUNT);
        }

        /*
         * usableCharacterList (Array[String])
         */
        public List<String> getUsableCharacterList() {
            return getArrayValue(KEY_USABLE_CHARACTER_LIST);
        }

    }

    public static class MailAddressInfoCapability extends CapabilityElement {

        public static final String KEY_MAIL_ADDRESS_LENGTH  = "mailAddressLength";
        public static final String KEY_DIRECT_SMTP_LIST     = "directSmtpList";
        public static final String KEY_MAIL_TO_CC_BCC_LIST  = "mailToCcBccList";

        MailAddressInfoCapability(Map<String, Object> values) {
            super(values);
        }

        /*
         * mailAddressLength (MaxLength)
         */
        public MaxLengthElement getMailAddressLength() {
            return getMaxLengthValue(KEY_MAIL_ADDRESS_LENGTH);
        }

        /*
         * directSmtpList (Array[Boolean])
         */
        public List<Boolean> getDirectSmtpList() {
            return getArrayValue(KEY_DIRECT_SMTP_LIST);
        }

        /*
         * mailToCcBccList (Array[String])
         */
        public List<String> getMailToCcBccList() {
            return getArrayValue(KEY_MAIL_TO_CC_BCC_LIST);
        }

    }

    public static class MaxBroadcastNumber extends CapabilityElement {

        public static final String KEY_MAIL             = "mail";
        public static final String KEY_MANUAL_MAIL      = "manualMail";
        public static final String KEY_FOLDER           = "folder";
        public static final String KEY_MANUAL_FOLDER    = "manualFolder";
        public static final String KEY_FAX              = "fax";
        public static final String KEY_MANUAL_FAX       = "manualFax";
        public static final String KEY_TOTAL            = "total";
        public static final String KEY_MANUAL_TOTAL     = "manualTotal";

        MaxBroadcastNumber(Map<String, Object> values) {
            super(values);
        }

        /*
         * mail (Number)
         */
        public Integer getMail() {
            return getNumberValue(KEY_MAIL);
        }

        /*
         * manualMail (Number)
         */
        public Integer getManualMail() {
            return getNumberValue(KEY_MANUAL_MAIL);
        }

        /*
         * folder (Number)
         */
        public Integer getFolder() {
            return getNumberValue(KEY_FOLDER);
        }

        /*
         * manualFolder (Number)
         */
        public Integer getManualFolder() {
            return getNumberValue(KEY_MANUAL_FOLDER);
        }

        /*
         * fax (Number)
         */
        public Integer getFax() {
            return getNumberValue(KEY_FAX);
        }

        /*
         * manualFax (Number)
         */
        public Integer getManualFax() {
            return getNumberValue(KEY_MANUAL_FAX);
        }

        /*
         * total (Number)
         */
        public Integer getTotal() {
            return getNumberValue(KEY_TOTAL);
        }

        /*
         * manualTotal (Number)
         */
        public Integer getManualTotal() {
            return getNumberValue(KEY_MANUAL_TOTAL);
        }

    }

    public static class FaxInfoCapability extends CapabilityElement {

        public static final String KEY_FAX_MACHINE_TYPE                 = "faxMachineType";
        public static final String KEY_TRANSMISSION_STANDBY_FILE_LIST   = "transmissionStandbyFileList";
        public static final String KEY_COMMUNICATION_LOG_LIST           = "communicationLogList";
        public static final String KEY_RECENT_DESTINATIONS              = "recentDestinations";
        public static final String KEY_REMOTE_LINK_FAX_INFO_LIST        = "remoteLinkFaxInfoList";
        public static final String KEY_RECONFIRM_DESTINATION            = "reconfirmDestination";
        public static final String KEY_CONFIRM_ADD_DESTINATION          = "confirmAddDestination";
        public static final String KEY_OUTSIDE_ACCESS_NUMBER_INFO_LIST  = "outsideAccessNumberInfoList";

        FaxInfoCapability(Map<String, Object> values) {
            super(values);
        }

        /*
         * faxMachineType (String)
         */
        public String getFaxMachineType() {
            return getStringValue(KEY_FAX_MACHINE_TYPE);
        }

        /*
         * transmissionStandbyFileList (Array[String])
         */
        public List<String> getTransmissionStandbyFileList() {
            return getArrayValue(KEY_TRANSMISSION_STANDBY_FILE_LIST);
        }

        /*
         * communicationLogList (Array[String])
         */
        public List<String> getCommunicationLogList() {
            return getArrayValue(KEY_COMMUNICATION_LOG_LIST);
        }

        /*
         * recentDestinations (Boolean)
         */
        public Boolean getRecentDestinations() {
            return getBooleanValue(KEY_RECENT_DESTINATIONS);
        }

        /*
         * remoteLinkFaxInfoList (Array[Object])
         */
        public RemoteLinkFaxInfoList getRemoteLinkFaxInfoList() {
            List<Map<String, Object>> mapArray = getArrayValue(KEY_REMOTE_LINK_FAX_INFO_LIST);
            if (mapArray == null) {
                return null;
            }
            return new RemoteLinkFaxInfoList(mapArray);
        }

        /*
         * reconfirmDestination (Boolean)
         */
        public Boolean getReconfirmDestination() {
            return getBooleanValue(KEY_RECONFIRM_DESTINATION);
        }

        /*
         * confirmAddDestination (Boolean)
         */
        public Boolean getConfirmAddDestination() {
            return getBooleanValue(KEY_CONFIRM_ADD_DESTINATION);
        }

        /*
         * outsideAccessNumberInfoList (Array[Object])
         */
        public OutsideAccessNumberInfoList getOutsideAccessNumberInfoList() {
            List<Map<String, Object>> mapArray = getArrayValue(KEY_OUTSIDE_ACCESS_NUMBER_INFO_LIST);
            if (mapArray == null) {
                return null;
            }
            return new OutsideAccessNumberInfoList(mapArray);
        }

    }

    public static class RemoteLinkFaxInfoList extends ArrayElement<RemoteLinkFaxInfo> {

        RemoteLinkFaxInfoList(List<Map<String, Object>> list) {
            super(list);
        }

        @Override
        protected RemoteLinkFaxInfo createElement(Map<String, Object> values) {
            return new RemoteLinkFaxInfo(values);
        }
    }

    public static class RemoteLinkFaxInfo extends CapabilityElement {

        public static final String KEY_ID               = "id";
        public static final String KEY_NAME             = "name";
        public static final String KEY_LOCATION         = "location";
        public static final String KEY_HOST_ADDRESS     = "hostAddress";
        public static final String KEY_STATUS           = "status";

        RemoteLinkFaxInfo(Map<String, Object> values) {
            super(values);
        }

        /*
         * id (String)
         */
        public String getId() {
            return getStringValue(KEY_ID);
        }

        /*
         * name (String)
         */
        public String getName() {
            return getStringValue(KEY_NAME);
        }

        /*
         * location (String)
         */
        public String getLocation() {
            return getStringValue(KEY_LOCATION);
        }

        /*
         * hostAddress (String)
         */
        public String getHostAddress() {
            return getStringValue(KEY_HOST_ADDRESS);
        }

        /*
         * status (String)
         */
        public String getStatus() {
            return getStringValue(KEY_STATUS);
        }

    }

    public static class OutsideAccessNumberInfoList extends ArrayElement<OutsideAccessNumberInfo> {

        OutsideAccessNumberInfoList(List<Map<String, Object>> list) {
            super(list);
        }

        @Override
        protected OutsideAccessNumberInfo createElement(Map<String, Object> values) {
            return new OutsideAccessNumberInfo(values);
        }

    }

    public static class OutsideAccessNumberInfo extends CapabilityElement {

        public static final String KEY_ID                       = "id";
        public static final String KEY_OUTSIDE_ACCESS_NUMBER    = "outsideAccessNumber";
        public static final String KEY_AUTO_ADD                 = "autoAdd";

        OutsideAccessNumberInfo(Map<String, Object> values) {
            super(values);
        }

        /*
         * id (String)
         */
        public String getId() {
            return getStringValue(KEY_ID);
        }

        /*
         * outsideAccessNumber (String)
         */
        public String getOutsideAccessNumber() {
            return getStringValue(KEY_OUTSIDE_ACCESS_NUMBER);
        }

        /*
         * autoAdd (Boolean)
         */
        public Boolean getAutoAdd() {
            return getBooleanValue(KEY_AUTO_ADD);
        }

    }

}
