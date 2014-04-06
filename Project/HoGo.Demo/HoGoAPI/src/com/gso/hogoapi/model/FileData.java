package com.gso.hogoapi.model;

import java.io.Serializable;

public class FileData implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String fileName;
	private String fileDescription;
	private String fileTitle;
	private String queueId;
	private String coverImageUrl;
	 
	/**
	 * @return the fileTitle
	 */
	public String getFileTitle() {
		return fileTitle;
	}
	/**
	 * @param fileTitle the fileTitle to set
	 */
	public void setFileTitle(String fileTitle) {
		this.fileTitle = fileTitle;
	}
	/**
	 * @return the fileDescription
	 */
	public String getFileDescription() {
		return fileDescription;
	}
	/**
	 * @param fileDescription the fileDescription to set
	 */
	public void setFileDescription(String fileDescription) {
		this.fileDescription = fileDescription;
	}
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return the queueId
	 */
	public String getQueueId() {
		return queueId;
	}
	/**
	 * @param queueId the queueId to set
	 */
	public void setQueueId(String queueId) {
		this.queueId = queueId;
	}
	public String getCoverImageUrl() {
		return coverImageUrl;
	}
	public void setCoverImageUrl(String coverImageUrl) {
		this.coverImageUrl = coverImageUrl;
	}

}
