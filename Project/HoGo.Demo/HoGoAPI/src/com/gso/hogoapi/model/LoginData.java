package com.gso.hogoapi.model;

public class LoginData {

	private boolean status;
	private String token;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the statues
	 */
	public boolean isStatus() {
		return status;
	}

	/**
	 * @param statues
	 *            the statues to set
	 */
	public void setStatus(boolean statues) {
		this.status = statues;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token
	 *            the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

}
