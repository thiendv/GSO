/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.wrapper.rws.service.scanner;

import java.util.List;
import java.util.Map;

import jp.co.ricoh.ssdk.sample.wrapper.common.CapabilityElement;
import jp.co.ricoh.ssdk.sample.wrapper.common.MagnificationElement;
import jp.co.ricoh.ssdk.sample.wrapper.common.MaxLengthElement;
import jp.co.ricoh.ssdk.sample.wrapper.common.RangeElement;

public class Capability extends CapabilityElement {

	private static final String KEY_AUTO_CORRECT_JOB_SETTING_LIST	= "autoCorrectJobSettingList";
	private static final String KEY_JOB_MODE_LIST					= "jobModeList";
	private static final String KEY_ORIGINAL_SIZE_LIST				= "originalSizeList";
	private static final String KEY_ORIGINAL_SIZE_CUSTOM_X_RANGE	= "originalSizeCustomXRange";
	private static final String KEY_ORIGINAL_SIZE_CUSTOM_Y_RANGE	= "originalSizeCustomYRange";
	private static final String KEY_SCAN_DEVICE_LIST				= "scanDeviceList";
	private static final String KEY_SCAN_METHOD_LIST				= "scanMethodList";
	private static final String KEY_ORIGINAL_OUTPUT_EXIT_LIST		= "originalOutputExitList";
	private static final String KEY_ORIGINAL_SIDE_LIST				= "originalSideList";
	private static final String KEY_ORIGINAL_ORIENTATION_LIST		= "originalOrientationList";
	private static final String KEY_ORIGINAL_PREVIEW_LIST			= "originalPreviewList";
	private static final String KEY_SCAN_COLOR_LIST					= "scanColorList";
	private static final String KEY_MAGNIFICATION_RANGE				= "magnificationRange";
	private static final String KEY_MAGNIFICATION_SIZE_CAPABILITY	= "magnificationSizeCapability";
	private static final String KEY_SCAN_RESOLUTION_LIST			= "scanResolutionList";
	private static final String KEY_AUTO_DENSITY_LIST				= "autoDensityList";
	private static final String KEY_MANUAL_DENSITY_RANGE			= "manualDensityRange";
	private static final String KEY_FILE_SETTING_CAPABILITY			= "fileSettingCapability";
	private static final String KEY_PDF_SETTING_CAPABILITY			= "pdfSettingCapability";
	private static final String KEY_OCR_LIST						= "ocrList";
	private static final String KEY_OCR_SETTING_CAPABILITY			= "ocrSettingCapability";
	private static final String KEY_SECURED_PDF_SETTING_CAPABILITY	= "securedPdfSettingCapability";
	private static final String KEY_EMAIL_SETTING_CAPABILITY		= "emailSettingCapability";
	private static final String KEY_DESTINATION_SETTING_CAPABILITY	= "destinationSettingCapability";
	private static final String KEY_MAX_BROADCAST_NUMBER_CAPABILITY	= "maxBroadcastNumberCapability";
	
	
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
	 * originalSizeCustomXRange (Range)
	 */
	public RangeElement getOriginalSizeCustomXRange() {
		return getRangeValue(KEY_ORIGINAL_SIZE_CUSTOM_X_RANGE);
	}
	
	/*
	 * originalSizeCustomYRange (Range)
	 */
	public RangeElement getOriginalSizeCustomYRange() {
		return getRangeValue(KEY_ORIGINAL_SIZE_CUSTOM_Y_RANGE);
	}
	
	/*
	 * scanDeviceList (Array[String])
	 */
	public List<String> getScanDeviceList() {
		return getArrayValue(KEY_SCAN_DEVICE_LIST);
	}
	
	/*
	 * scanMethodList (Array[String])
	 */
	public List<String> getScanMethodList() {
		return getArrayValue(KEY_SCAN_METHOD_LIST);
	}
	
	/*
	 * originalOutputExitList (Array[String])
	 */
	public List<String> getOriginalOutputExitList() {
		return getArrayValue(KEY_ORIGINAL_OUTPUT_EXIT_LIST);
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
	 * scanColorList (Array[String])
	 */
	public List<String> getScanColorList() {
		return getArrayValue(KEY_SCAN_COLOR_LIST);
	}
	
	/*
	 * magnificationRange (Magnification)
	 */
	public MagnificationElement getMagnificationRange() {
		return getMagnificationValue(KEY_MAGNIFICATION_RANGE);
	}
	
	/*
	 * magnificationSizeCapability (Object)
	 */
	public MagnificationSizeCapability getMagnificationSize() {
		Map<String, Object> mapValue = getObjectValue(KEY_MAGNIFICATION_SIZE_CAPABILITY);
		if (mapValue == null) {
			return null;
		}
		return new MagnificationSizeCapability(mapValue);
	}
	
	/*
	 * scanResolutionList (Array[String])
	 */
	public List<String> getScanResolutionList() {
		return getArrayValue(KEY_SCAN_RESOLUTION_LIST);
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
	 * fileSettingCapability (Object)
	 */
	public FileSettingCapability getFileSettingCapability() {
		Map<String, Object> mapValue = getObjectValue(KEY_FILE_SETTING_CAPABILITY);
		if (mapValue == null) {
			return null;
		}
		return new FileSettingCapability(mapValue);
	}
	
	/*
	 * pdfSettingCapability (Object)
	 */
	public PdfSettingCapability getPdfSettingCapability() {
		Map<String, Object> mapValue = getObjectValue(KEY_PDF_SETTING_CAPABILITY);
		if (mapValue == null) {
			return null;
		}
		return new PdfSettingCapability(mapValue);
	}
	
	/*
	 * ocrList (Array[Boolean])
	 */
	public List<Boolean> getOcrList() {
		return getArrayValue(KEY_OCR_LIST);
	}
	
	/*
	 * ocrSettingCapability (Object)
	 */
	public OcrSettingCapability getOcrSettingCapability() {
		Map<String, Object> mapValue = getObjectValue(KEY_OCR_SETTING_CAPABILITY);
		if (mapValue == null) {
			return null;
		}
		return new OcrSettingCapability(mapValue);
	}
	
	/*
	 * securedPdfSettingCapability (Object)
	 */
	public SecuredPdfSettingCapability getSecuredPdfSettingCapability() {
		Map<String, Object> mapValue = getObjectValue(KEY_SECURED_PDF_SETTING_CAPABILITY);
		if (mapValue == null) {
			return null;
		}
		return new SecuredPdfSettingCapability(mapValue);
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
	 * maxBroadcastNumberCapability (Object)
	 */
	public MaxBroadcastNumberCapability getMaxBroadcastNumberCapability() {
		Map<String, Object> mapValue = getObjectValue(KEY_MAX_BROADCAST_NUMBER_CAPABILITY);
		if (mapValue == null) {
			return null;
		}
		return new MaxBroadcastNumberCapability(mapValue);
	}
	
	
	
	public static class MagnificationSizeCapability extends CapabilityElement {
		
		private static final String KEY_SIZE_LIST		= "sizeList";
		private static final String KEY_CUSTOM_X_RANGE	= "customXRange";
		private static final String KEY_CUSTOM_Y_RANGE	= "customYRange";
		
		MagnificationSizeCapability(Map<String, Object> values) {
			super(values);
		}
		
		/*
		 * sizeList (Array[String])
		 */
		public List<String> getSizeList() {
			return getArrayValue(KEY_SIZE_LIST);
		}
		
		/*
		 * customXRange (Range)
		 */
		public RangeElement getCustomXRange() {
			return getRangeValue(KEY_CUSTOM_X_RANGE);
		}
		
		/*
		 * customYRange (Range)
		 */
		public RangeElement getCustomYRange() {
			return getRangeValue(KEY_CUSTOM_Y_RANGE);
		}
		
	}
	
	public static class FileSettingCapability extends CapabilityElement {
		
		private static final String KEY_COMPRESSION_METHOD_LIST		= "compressionMethodList";
		private static final String KEY_COMPRESSION_LEVEL_LIST		= "compressionLevelList";
		private static final String KEY_FILE_FORMAT_LIST			= "fileFormatList";
		private static final String KEY_MULTI_PAGE_FORMAT_LIST		= "multiPageFormatList";
		private static final String KEY_FILE_NAME_LENGTH			= "fileNameLength";
		private static final String KEY_FILE_NAME_TIME_STAMP_LIST	= "fileNameTimeStampList";
		
		FileSettingCapability(Map<String, Object> values) {
			super(values);
		}
		
		/*
		 * compressionMethodList (Array[String])
		 */
		public List<String> getCompressionMethodList() {
			return getArrayValue(KEY_COMPRESSION_METHOD_LIST);
		}
		
		/*
		 * compressionLevelList (Array[String])
		 */
		public List<String> getCompressionLevelList() {
			return getArrayValue(KEY_COMPRESSION_LEVEL_LIST);
		}
		
		/*
		 * fileFormatList (Array[String])
		 */
		public List<String> getFileFormatList() {
			return getArrayValue(KEY_FILE_FORMAT_LIST);
		}
		
		/*
		 * multiPageFormatList (Array[Boolean])
		 */
		public List<Boolean> getMultiPageFormatList() {
			return getArrayValue(KEY_MULTI_PAGE_FORMAT_LIST);
		}
		
		/*
		 * fileNameLength (MaxLength)
		 */
		public MaxLengthElement getFileNameLength() {
			return getMaxLengthValue(KEY_FILE_NAME_LENGTH);
		}
		
		/*
		 * fileNameTimeStampList (Array[Boolean])
		 */
		public List<Boolean> getFileNameTimeStampList() {
			return getArrayValue(KEY_FILE_NAME_TIME_STAMP_LIST);
		}
		
	}
	
	public static class PdfSettingCapability extends CapabilityElement {
		
		private static final String KEY_PDF_A_LIST					= "pdfAList";
		private static final String KEY_HIGH_COMPRESSED_PDF_LIST	= "highCompressionPdfList";
		private static final String KEY_DIGITAL_SIGNATURE_PDF_LIST	= "digitalSignaturePdfList";
		
		PdfSettingCapability(Map<String, Object> values) {
			super(values);
		}
		
		/*
		 * pdfAList (Array[Boolean])
		 */
		public List<Boolean> getPdfAList() {
			return getArrayValue(KEY_PDF_A_LIST);
		}
		
		/*
		 * highCompressionPdfList (Array[Boolean])
		 */
		public List<Boolean> getHighCompressedPdfList() {
			return getArrayValue(KEY_HIGH_COMPRESSED_PDF_LIST);
		}
		
		/*
		 * digitalSignaturePdfList (Array[Boolean])
		 */
		public List<Boolean> getDigitalSignaturePdfList() {
			return getArrayValue(KEY_DIGITAL_SIGNATURE_PDF_LIST);
		}
		
	}
	
	public static class OcrSettingCapability extends CapabilityElement {
		
		private static final String KEY_OCR_LANGUAGE_LIST					= "ocrLanguageList";
		private static final String KEY_OMIT_BLANK_PAGE_LIST				= "omitBlankPageList";
		private static final String KEY_BLANK_PAGE_SENSITIVITY_LEVEL_LIST	= "blankPageSensitivityLevelList";
		private static final String KEY_AUTO_FILE_NAME_LIST					= "autoFileNameList";
		
		OcrSettingCapability(Map<String, Object> values) {
			super(values);
		}
		
		/*
		 * ocrLanguageList (Array[String])
		 */
		public List<String> getOcrLanguageList() {
			return getArrayValue(KEY_OCR_LANGUAGE_LIST);
		}
		
		/*
		 * omitBlankPageList (Array[Boolean])
		 */
		public List<Boolean> getOmitBlankPageList() {
			return getArrayValue(KEY_OMIT_BLANK_PAGE_LIST);
		}
		
		/*
		 * blankPageSensitivityLevelList (Array[String])
		 */
		public List<String> getBlankPageSensitivityLevelList() {
			return getArrayValue(KEY_BLANK_PAGE_SENSITIVITY_LEVEL_LIST);
		}
		
		/*
		 * autoFileNameList (Array[Boolean])
		 */
		public List<Boolean> getAutoFileNameList() {
			return getArrayValue(KEY_AUTO_FILE_NAME_LIST);
		}
		
	}
	
	public static class SecuredPdfSettingCapability extends CapabilityElement {
		
		private static final String KEY_ENCRYPTION_LIST						= "encryptionList";
		private static final String KEY_ENCRYPTION_PASSWORD_LENGTH			= "encryptionPasswordLength";
		private static final String KEY_ENCRYPTION_LEVEL_LIST				= "encryptionLevelList";
		private static final String KEY_DOCUMENT_SECURITY_LIST				= "documentSecurityList";
		private static final String KEY_DOCUMENT_SECURITY_PASSWORD_LENGTH	= "documentSecurityPasswordLength";
		private static final String KEY_ALLOW_PRINT_LIST					= "allowPrintList";
		private static final String KEY_ALLOW_EDIT_LIST						= "allowEditList";
		private static final String KEY_ALLOW_COPY_AND_EXTRACT_LIST			= "allowCopyAndExtractList";
		
		SecuredPdfSettingCapability(Map<String, Object> values) {
			super(values);
		}
		
		/*
		 * encryptionList (Array[Boolean])
		 */
		public List<Boolean> getEncryptionList() {
			return getArrayValue(KEY_ENCRYPTION_LIST);
		}
		
		/*
		 * encryptionPasswordLength (MaxLength)
		 */
		public MaxLengthElement getEncryptionPasswordLength() {
			return getMaxLengthValue(KEY_ENCRYPTION_PASSWORD_LENGTH);
		}
		
		/*
		 * encryptionLevelList (Array[String])
		 */
		public List<String> getEncryptionLevelList() {
			return getArrayValue(KEY_ENCRYPTION_LEVEL_LIST);
		}
		
		/*
		 * documentSecurityList (Array[Boolean])
		 */
		public List<Boolean> getDocumentSecurityList() {
			return getArrayValue(KEY_DOCUMENT_SECURITY_LIST);
		}
		
		/*
		 * documentSecurityPasswordLength (MaxLength)
		 */
		public MaxLengthElement getDocumentSecurityPasswordLength() {
			return getMaxLengthValue(KEY_DOCUMENT_SECURITY_PASSWORD_LENGTH);
		}
		
		/*
		 * allowPrintList (Array[String])
		 */
		public List<String> getAllowPrintList() {
			return getArrayValue(KEY_ALLOW_PRINT_LIST);
		}
		
		/*
		 * allowEditList (Array[Boolean])
		 */
		public List<Boolean> getAllowEditList() {
			return getArrayValue(KEY_ALLOW_EDIT_LIST);
		}
		
		/*
		 * allowCopyAndExtractList (Array[Boolean])
		 */
		public List<Boolean> getAllowCopyAndExtractList() {
			return getArrayValue(KEY_ALLOW_COPY_AND_EXTRACT_LIST);
		}
		
	}
	
	public static class EmailSettingCapability extends CapabilityElement {
		
		private static final String KEY_SUBJECT_LENGTH				= "subjectLength";
		private static final String KEY_BODY_LENGTH					= "bodyLength";
		private static final String KEY_SENDER_ENTRY_ID_LENGTH		= "senderEntryIdLength";
		private static final String KEY_ADMIN_ADDRES_AS_SENDER		= "adminAddresAsSender";
		private static final String KEY_SMIME_SIGNATURE_LIST		= "smimeSignatureList";
		private static final String KEY_SMIME_ENCRYPTION_LIST = "smimeEncryptionList";
		
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
		 * senderEntryIdLength (MaxLength)
		 */
		public MaxLengthElement getSenderEntryIdLength() {
			return getMaxLengthValue(KEY_SENDER_ENTRY_ID_LENGTH);
		}
		
		/*
		 * adminAddresAsSender (Boolean)
		 */
		public Boolean getAdminAddresAsSender() {
			return getBooleanValue(KEY_ADMIN_ADDRES_AS_SENDER);
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
	
	public static class DestinationSettingCapability extends CapabilityElement {
		
		private static final String KEY_DESTINATION_TYPE_LIST						= "destinationTypeList";
		private static final String KEY_ADDRESSBOOK_DESTINATION_SETTING_CAPABILITY	= "addressbookDestinationSettingCapability";
		private static final String KEY_MANUAL_DESTINATION_SETTING_CAPABILITY		= "manualDestinationSettingCapability";
		
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
		
	}
	
	public static class AddressbookDestinationSettingCapability extends CapabilityElement {
		
		private static final String KEY_DESTINATION_KIND_LIST		= "destinationKindList";
		private static final String KEY_ENTRY_ID_LENGTH				= "entryIdLength";
		private static final String KEY_REGISTRATION_NO_RANGE		= "registrationNoRange";
		private static final String KEY_MAIL_TO_CC_BCC_LIST			= "mailToCcBccList";
		
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
		
		private static final String KEY_DESTINATION_KIND_OBJECT			= "destinationKindObject";
		private static final String KEY_MAIL_ADDRESS_INFO_CAPABILITY	= "mailAddressInfoCapability";
		private static final String KEY_SMB_ADDRESS_INFO_CAPABILITY		= "smbAddressInfoCapability";
		private static final String KEY_FTP_ADDRESS_INFO_CAPABILITY		= "ftpAddressInfoCapability";
		private static final String KEY_NCP_ADDRESS_INFO_CAPABILITY		= "ncpAddressInfoCapability";
		
		ManualDestinationSettingCapability(Map<String, Object> values) {
			super(values);
		}
		
		/*
		 * destinationKindObject (Array[String])
		 */
		public List<String> getDestinationKindObject() {
			return getArrayValue(KEY_DESTINATION_KIND_OBJECT);
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
		
		/*
		 * smbAddressInfoCapability (Object)
		 */
		public SmbAddressInfoCapability getSmbAddressInfoCapability() {
			Map<String, Object> mapValue = getObjectValue(KEY_SMB_ADDRESS_INFO_CAPABILITY);
			if (mapValue == null) {
				return null;
			}
			return new SmbAddressInfoCapability(mapValue);
		}
		
		/*
		 * ftpAddressInfoCapability (Object)
		 */
		public FtpAddressInfoCapability getFtpAddressInfoCapability() {
			Map<String, Object> mapValue = getObjectValue(KEY_FTP_ADDRESS_INFO_CAPABILITY);
			if (mapValue == null) {
				return null;
			}
			return new FtpAddressInfoCapability(mapValue);
		}
		
		/*
		 * ncpAddressInfoCapability (Object)
		 */
		public NcpAddressInfoCapability getNcpAddressInfoCapability() {
			Map<String, Object> mapValue = getObjectValue(KEY_NCP_ADDRESS_INFO_CAPABILITY);
			if (mapValue == null) {
				return null;
			}
			return new NcpAddressInfoCapability(mapValue);
		}
		
	}
	
	public static class MailAddressInfoCapability extends CapabilityElement {
		
		private static final String KEY_MAIL_ADDRESS_LENGTH		= "mailAddressLength";
		private static final String KEY_MAIL_TO_CC_BCC_LIST		= "mailToCcBccList";
		
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
		 * mailToCcBccList (Array[String])
		 */
		public 	List<String> getMailToCcBccList() {
			return getArrayValue(KEY_MAIL_TO_CC_BCC_LIST);
		}
		
	}
	
	public static class SmbAddressInfoCapability extends CapabilityElement {
		
		private static final String KEY_PATH_LENGTH				= "pathLength";
		private static final String KEY_USER_NAME_LENGTH		= "userNameLength";
		private static final String KEY_PASSWORD_LENGTH			= "passwordLength";
		
		SmbAddressInfoCapability(Map<String, Object> values) {
			super(values);
		}
		
		/*
		 * pathLength (MaxLength)
		 */
		public MaxLengthElement getPathLength() {
			return getMaxLengthValue(KEY_PATH_LENGTH);
		}
		
		/*
		 * userNameLength (MaxLength)
		 */
		public MaxLengthElement getUserNameLength() {
			return getMaxLengthValue(KEY_USER_NAME_LENGTH);
		}
		
		/*
		 * passwordLength (MaxLength)
		 */
		public MaxLengthElement getPasswordLength() {
			return getMaxLengthValue(KEY_PASSWORD_LENGTH);
		}
		
	}
	
	public static class FtpAddressInfoCapability extends CapabilityElement {
		
		private static final String KEY_SERVER_NAME_LENGTH		= "serverNameLength";
		private static final String KEY_PATH_LENGTH				= "pathLength";
		private static final String KEY_USER_NAME_LENGTH		= "userNameLength";
		private static final String KEY_PASSWORD_LENGTH			= "passwordLength";
		private static final String KEY_CHARACTER_CODE_LIST		= "characterCodeList";
		private static final String KEY_PORT_RANGE				= "portRange";
		
		FtpAddressInfoCapability(Map<String, Object> values) {
			super(values);
		}
		
		/*
		 * serverNameLength (MaxLength)
		 */
		public MaxLengthElement getServerNameLength() {
			return getMaxLengthValue(KEY_SERVER_NAME_LENGTH);
		}
		
		/*
		 * pathLength (MaxLength)
		 */
		public MaxLengthElement getPathLength() {
			return getMaxLengthValue(KEY_PATH_LENGTH);
		}
		
		/*
		 * userNameLength (MaxLength)
		 */
		public MaxLengthElement getUserNameLength() {
			return getMaxLengthValue(KEY_USER_NAME_LENGTH);
		}
		
		/*
		 * passwordLength (MaxLength)
		 */
		public MaxLengthElement getPasswordLength() {
			return getMaxLengthValue(KEY_PASSWORD_LENGTH);
		}
		
		/*
		 * characterCodeList (Array[String])
		 */
		public List<String> getCharacterCodeList() {
			return getArrayValue(KEY_CHARACTER_CODE_LIST);
		}
		
		/*
		 * portRange (Range)
		 */
		public RangeElement getPortRange() {
			return getRangeValue(KEY_PORT_RANGE);
		}
		
	}
	
	public static class NcpAddressInfoCapability extends CapabilityElement {
		
		private static final String KEY_PATH_LENGTH				= "pathLength";
		private static final String KEY_USER_NAME_LENGTH		= "userNameLength";
		private static final String KEY_PASSWORD_LENGTH			= "passwordLength";
		private static final String KEY_CONNECTION_TYPE_LIST	= "connectionTypeList";
		
		NcpAddressInfoCapability(Map<String, Object> values) {
			super(values);
		}
		
		/*
		 * pathLength (MaxLength)
		 */
		public MaxLengthElement getPathLength() {
			return getMaxLengthValue(KEY_PATH_LENGTH);
		}
		
		/*
		 * userNameLength (MaxLength)
		 */
		public MaxLengthElement getUserNameLength() {
			return getMaxLengthValue(KEY_USER_NAME_LENGTH);
		}
		
		/*
		 * passwordLength (MaxLength)
		 */
		public MaxLengthElement getPasswordLength() {
			return getMaxLengthValue(KEY_PASSWORD_LENGTH);
		}
		
		/*
		 * connectionTypeList (Array[String])
		 */
		public List<String> getConnectionTypeList() {
			return getArrayValue(KEY_CONNECTION_TYPE_LIST);
		}
		
	}
	
	public static class MaxBroadcastNumberCapability extends CapabilityElement {
		
		private static final String KEY_MAIL				= "mail";
		private static final String KEY_MANUAL_MAIL			= "manualMail";
		private static final String KEY_FOLDER				= "folder";
		private static final String KEY_MANUAL_FOLDER		= "manualFolder";
		private static final String KEY_TOTAL				= "total";
		private static final String KEY_MANUAL_TOTAL		= "manualTotal";
		
		MaxBroadcastNumberCapability(Map<String, Object> values) {
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
	

}
