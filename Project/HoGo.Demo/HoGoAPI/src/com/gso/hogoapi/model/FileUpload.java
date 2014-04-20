package com.gso.hogoapi.model;

import java.io.Serializable;

public class FileUpload implements Serializable{

	private String pdfPath;
	private String jpgPath;
	public String getPdfPath() {
		return pdfPath;
	}
	public void setPdfPath(String pdfPath) {
		this.pdfPath = pdfPath;
	}
	public String getJpgPath() {
		return jpgPath;
	}
	public void setJpgPath(String jpgPath) {
		this.jpgPath = jpgPath;
	}
}
