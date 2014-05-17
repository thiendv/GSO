package com.gso.hogoapi.model;

import java.io.Serializable;
import java.util.List;

public class SendData implements Serializable {

	private List<FileData> dataList;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	/**
	 * @return the dataList
	 */
	public List<FileData> getDataList() {
		return dataList;
	}
	/**
	 * @param dataList the dataList to set
	 */
	public void setDataList(List<FileData> dataList) {
		this.dataList = dataList;
	}

}
