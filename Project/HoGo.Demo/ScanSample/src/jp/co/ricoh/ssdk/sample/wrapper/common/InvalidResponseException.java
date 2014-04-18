/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.wrapper.common;

import jp.co.ricoh.ssdk.sample.wrapper.client.RestResponse;

/**
 * レスポンスが不正であることを示す例外クラス
 */
public class InvalidResponseException extends Exception {
	
	private final RestResponse response;
	private final ErrorResponseBody body;
	
	public InvalidResponseException(RestResponse response, ErrorResponseBody body) {
		super(response.getStatusLine());
		this.response = response;
		this.body = body;
	}
	
	public RestResponse getResponse() {
		return response;
	}
	
	public int getStatusCode() {
		return response.getStatusCode();
	}
	
	public boolean hasBody() {
		return (body != null);
	}
	
	public ErrorResponseBody getBody() {
		return body;
	}
	
}
