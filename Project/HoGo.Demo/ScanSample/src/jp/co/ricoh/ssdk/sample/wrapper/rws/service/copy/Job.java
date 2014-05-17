/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.wrapper.rws.service.copy;

import java.io.IOException;
import java.util.Map;

import jp.co.ricoh.ssdk.sample.wrapper.client.RestContext;
import jp.co.ricoh.ssdk.sample.wrapper.client.RestRequest;
import jp.co.ricoh.ssdk.sample.wrapper.client.RestResponse;
import jp.co.ricoh.ssdk.sample.wrapper.common.ApiClient;
import jp.co.ricoh.ssdk.sample.wrapper.common.EmptyResponseBody;
import jp.co.ricoh.ssdk.sample.wrapper.common.GenericJsonDecoder;
import jp.co.ricoh.ssdk.sample.wrapper.common.InvalidResponseException;
import jp.co.ricoh.ssdk.sample.wrapper.common.Request;
import jp.co.ricoh.ssdk.sample.wrapper.common.Response;
import jp.co.ricoh.ssdk.sample.wrapper.common.Utils;
import jp.co.ricoh.ssdk.sample.wrapper.log.Logger;

public class Job extends ApiClient {

	private static final String REST_PATH_JOBS_ID	= "/rws/service/copy/jobs/%s";

	private final String jobId;

	public Job(String jobId) {
		super();
		if (jobId == null) {
			throw new NullPointerException("jobId must not be null.");
		}
		this.jobId = jobId;
	}

	public Job(RestContext context, String jobId) {
		super(context);
		if (jobId == null) {
			throw new NullPointerException("jobId must not be null.");
		}
		this.jobId = jobId;
	}

	/*
	 * GET: /rws/service/copy/jobs/{jobId}
	 * 
	 * RequestBody:  non
	 * ResponseBody: GetJobStatusResponseBody
	 */
	public Response<GetJobStatusResponseBody> getJobStatus(Request request) throws IOException, InvalidResponseException {
		RestResponse restResponse = execute(
				build(RestRequest.METHOD_GET, String.format(REST_PATH_JOBS_ID, jobId), request));
		Map<String, Object> body = GenericJsonDecoder.decodeToMap(restResponse.makeContentString("UTF-8"));

		switch (restResponse.getStatusCode()) {
			case 200:
				return new Response<GetJobStatusResponseBody>(restResponse, new GetJobStatusResponseBody(body));
			default:
				throw Utils.createInvalidResponseException(restResponse, body);
		}
	}

	/*
	 * PUT: /rws/service/copy/jobs/{jobId}
	 * 
	 * RequestBody:  UpdateJobStatusRequestBody
	 * ResponseBody: non (EmptyResponseBody)
	 */
	public Response<EmptyResponseBody> updateJobStatus(Request request) throws IOException, InvalidResponseException {
		if (Logger.isDebugEnabled()) {
			if (request.hasBody()) {
				Logger.debug("copy updateJobStatus json: " + request.getBody().toEntityString());
			}
		}

		RestResponse restResponse = execute(
				build(RestRequest.METHOD_PUT, String.format(REST_PATH_JOBS_ID, jobId), request));
		Map<String, Object> body = GenericJsonDecoder.decodeToMap(restResponse.makeContentString("UTF-8"));

		switch (restResponse.getStatusCode()) {
			case 200:	// validateOnly=true
				return new Response<EmptyResponseBody>(restResponse, new EmptyResponseBody(body));
			case 202:	// validateOnly=false, status change Accepted
				return new Response<EmptyResponseBody>(restResponse, new EmptyResponseBody(body));
			default:
				throw Utils.createInvalidResponseException(restResponse, body);
		}
	}

}
