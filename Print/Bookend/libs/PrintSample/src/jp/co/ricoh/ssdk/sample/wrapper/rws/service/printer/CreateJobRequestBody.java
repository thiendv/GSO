/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.wrapper.rws.service.printer;

import java.util.HashMap;
import java.util.Map;

import jp.co.ricoh.ssdk.sample.wrapper.common.RequestBody;
import jp.co.ricoh.ssdk.sample.wrapper.common.Utils;
import jp.co.ricoh.ssdk.sample.wrapper.common.WritableElement;
import jp.co.ricoh.ssdk.sample.wrapper.json.EncodedException;
import jp.co.ricoh.ssdk.sample.wrapper.json.JsonUtils;
import jp.co.ricoh.ssdk.sample.wrapper.rws.service.printer.JobSetting;

public class CreateJobRequestBody extends WritableElement implements RequestBody {

	private static final String CONTENT_TYPE_JSON = "application/json; charset=utf-8";

	private static final String KEY_FILE_ID			= "fileId";
	private static final String KEY_FILE_PATH		= "filePath";
	private static final String KEY_PDL 			= "pdl";
	private static final String KEY_USER_CODE		= "userCode";
	private static final String KEY_VALIDATE_ONLY	= "validateOnly";
	private static final String KEY_JOB_SETTING		= "jobSetting";

	public CreateJobRequestBody() {
		super(new HashMap<String, Object>());
	}

	@Override
	public String getContentType() {
		return CreateJobRequestBody.CONTENT_TYPE_JSON;
	}

	@Override
	public String toEntityString() {
		try {
			return JsonUtils.getEncoder().encode(values);
		} catch (EncodedException e) {
			e.printStackTrace();
			return "{}";
		}
	}

	/*
	 * fileId (String)
	 */
	public String getFileId() {
		return getStringValue(KEY_FILE_ID);
	}
	public void setFileId(String value) {
		setStringValue(KEY_FILE_ID, value);
	}
	public String removeFileId() {
		return removeStringValue(KEY_FILE_ID);
	}

	/*
	 * filePath (String)
	 */
	public String getFilePath() {
		return getStringValue(KEY_FILE_PATH);
	}
	public void setFilePath(String value) {
		setStringValue(KEY_FILE_PATH, value);
	}
	public String removeFilePath() {
		return removeStringValue(KEY_FILE_PATH);
	}

	/*
	 * pdl (String)
	 */
	public String getPdl() {
		return getStringValue(KEY_PDL);
	}
	public void setPdl(String value) {
		setStringValue(KEY_PDL, value);
	}
	public String removePdl() {
		return removeStringValue(KEY_PDL);
	}

	/*
	 * userCode (String)
	 */
	public String getUserCode() {
		return getStringValue(KEY_USER_CODE);
	}
	public void setUserCode(String value) {
		setStringValue(KEY_USER_CODE, value);
	}
	public String removeUserCode() {
		return removeStringValue(KEY_USER_CODE);
	}

	/*
	 * validateOnly (Boolean)
	 */
	public Boolean getValidateOnly() {
		return getBooleanValue(KEY_VALIDATE_ONLY);
	}
	public void setValidateOnly(Boolean value) {
		setBooleanValue(KEY_VALIDATE_ONLY, value);
	}
	public Boolean removeValidateOnly() {
		return removeBooleanValue(KEY_VALIDATE_ONLY);
	}

	/*
	 * jobSetting (Object)
	 */
	public JobSetting getJobSetting() {
		Map<String, Object> value = getObjectValue(KEY_JOB_SETTING);
		if (value == null) {
			value = Utils.createElementMap();
			setObjectValue(KEY_JOB_SETTING, value);
		}
		return new JobSetting(value);
	}
//	public void setJobSetting(JobSetting value) {
//		throw new UnsupportedOperationException();
//	}
	public JobSetting removeJobSetting() {
		Map<String, Object> value = removeObjectValue(KEY_JOB_SETTING);
		if (value == null) {
			return null;
		}
		return new JobSetting(value);
	}

}
