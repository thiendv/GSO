package com.gso.hogoapi.model;

public class ResponseData {

	/**
	 * @param args
	 */
	private String status;
	private Object data;
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	/**
	 * @return the data
	 */
	public Object getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(Object data) {
		this.data = data;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

}
