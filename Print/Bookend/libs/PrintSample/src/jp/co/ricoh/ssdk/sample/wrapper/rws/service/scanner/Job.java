/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.wrapper.rws.service.scanner;

import java.io.IOException;
import java.util.Map;

import jp.co.ricoh.ssdk.sample.wrapper.client.RestContext;
import jp.co.ricoh.ssdk.sample.wrapper.client.RestRequest;
import jp.co.ricoh.ssdk.sample.wrapper.client.RestResponse;
import jp.co.ricoh.ssdk.sample.wrapper.common.ApiClient;
import jp.co.ricoh.ssdk.sample.wrapper.common.BinaryResponseBody;
import jp.co.ricoh.ssdk.sample.wrapper.common.EmptyResponseBody;
import jp.co.ricoh.ssdk.sample.wrapper.common.GenericJsonDecoder;
import jp.co.ricoh.ssdk.sample.wrapper.common.InvalidResponseException;
import jp.co.ricoh.ssdk.sample.wrapper.common.Request;
import jp.co.ricoh.ssdk.sample.wrapper.common.RequestQuery;
import jp.co.ricoh.ssdk.sample.wrapper.common.Response;
import jp.co.ricoh.ssdk.sample.wrapper.common.Utils;
import jp.co.ricoh.ssdk.sample.wrapper.log.Logger;

public class Job extends ApiClient {
	
	private static final String REST_PATH_JOBS_ID			= "/rws/service/scanner/jobs/%s";
	private static final String REST_PATH_JOBS_ID_FILE		= "/rws/service/scanner/jobs/%s/file";
	private static final String REST_PATH_JOBS_ID_THUMBNAIL	= "/rws/service/scanner/jobs/%s/thumbnail";
	private static final String REST_PATH_JOBS_ID_OCRDATA	= "/rws/service/scanner/jobs/%s/ocrdata";

    private static final String PARAMETER_GET_METHOD_KEY    = "getMethod";
    private static final String PARAMETER_FORMAT_KEY        = "format";

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
	
	private String getQueryValue(RequestQuery query, String key) {
		if (query == null) {
			return null;
		}
		return query.get(key);
	}
	
	/*
	 * GET: /rws/service/scanner/jobs/{jobId}
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
	 * PUT: /rws/service/scanner/jobs/{jobId}
	 * 
	 * RequestBody:  UpdateJobStatusRequestBody
	 * ResponseBody: non (EmptyResponseBody)
	 */
	public Response<EmptyResponseBody> updateJobStatus(Request request) throws IOException, InvalidResponseException {
        if (Logger.isDebugEnabled()) {
            if (request.hasBody()) {
                Logger.debug("scanner updateJobStatus json: " + request.getBody().toEntityString());
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
	
	/*
	 * GET: /rws/service/scanner/jobs/{jobId}/file?getMethod=direct
	 * 
	 * RequestBody:  non
	 * ResponseBody: BinaryResponseBody
	 */
	public Response<BinaryResponseBody> getFile(Request request) throws IOException, InvalidResponseException {
		String getMethod = getQueryValue(request.getQuery(), PARAMETER_GET_METHOD_KEY);
		if ((getMethod != null) && (! "direct".equals(getMethod))) {
			throw new IllegalArgumentException("Invalid parameter: getMethod.");
		}
		
		RestResponse restResponse = execute(
				build(RestRequest.METHOD_GET, String.format(REST_PATH_JOBS_ID_FILE, jobId), request));
		
		switch (restResponse.getStatusCode()) {
			case 200:
				return new Response<BinaryResponseBody>(restResponse, new BinaryResponseBody(restResponse.getBytes()));
			default:
				Map<String, Object> body = GenericJsonDecoder.decodeToMap(restResponse.makeContentString("UTF-8"));
				throw Utils.createInvalidResponseException(restResponse, body);
		}
	}
	
	/*
	 * GET: /rws/service/scanner/jobs/{jobId}/file?getMethod=filePath
	 * MultiLink-Panel only
	 * 
	 * RequestBody:  non
	 * ResponseBody: FilePathResponseBody
	 */
	public Response<FilePathResponseBody> getFilePath(Request request) throws IOException, InvalidResponseException {
		String getMethod = getQueryValue(request.getQuery(), PARAMETER_GET_METHOD_KEY);
		if (! "filePath".equals(getMethod)) {
			throw new IllegalArgumentException("Invalid parameter: getMethod.");
		}
		
		RestResponse restResponse = execute(
				build(RestRequest.METHOD_GET, String.format(REST_PATH_JOBS_ID_FILE, jobId), request));
		Map<String, Object> body = GenericJsonDecoder.decodeToMap(restResponse.makeContentString("UTF-8"));
		
		switch (restResponse.getStatusCode()) {
			case 200:
				return new Response<FilePathResponseBody>(restResponse, new FilePathResponseBody(body));
			default:
				throw Utils.createInvalidResponseException(restResponse, body);
		}
	}
	
	/*
	 * DELETE: /rws/service/scanner/jobs/{jobId}/file
	 * 
	 * RequestBody:  non
	 * ResponseBody: non (EmptyResponseBody)
	 */
	public Response<EmptyResponseBody> deleteFile(Request request) throws IOException, InvalidResponseException {
		RestResponse restResponse = execute(
				build(RestRequest.METHOD_DELETE, String.format(REST_PATH_JOBS_ID_FILE, jobId), request));
		Map<String, Object> body = GenericJsonDecoder.decodeToMap(restResponse.makeContentString("UTF-8"));
		
		switch (restResponse.getStatusCode()) {
			case 200:
				return new Response<EmptyResponseBody>(restResponse, new EmptyResponseBody(body));
			default:
				throw Utils.createInvalidResponseException(restResponse, body);
		}
	}
	
	/*
	 * GET: /rws/service/scanner/jobs/{jobId}/thumbnail?getMethod=direct
	 * 
	 * RequestBody:  non
	 * ResponseBody: BinaryResponseBody
	 */
	public Response<BinaryResponseBody> getThumbnail(Request request) throws IOException, InvalidResponseException {
		String getMethod = getQueryValue(request.getQuery(), PARAMETER_GET_METHOD_KEY);
		if ((getMethod != null) && (! "direct".equals(getMethod))) {
			throw new IllegalArgumentException("Invalid parameter: getMethod.");
		}
		
		RestResponse restResponse = execute(
				build(RestRequest.METHOD_GET, String.format(REST_PATH_JOBS_ID_THUMBNAIL, jobId), request));
		
		switch (restResponse.getStatusCode()) {
			case 200:
				return new Response<BinaryResponseBody>(restResponse, new BinaryResponseBody(restResponse.getBytes()));
			default:
				Map<String, Object> body = GenericJsonDecoder.decodeToMap(restResponse.makeContentString("UTF-8"));
				throw Utils.createInvalidResponseException(restResponse, body);
		}
	}
	
	/*
	 * GET: /rws/service/scanner/jobs/{jobId}/thumbnail?getMethod=filePath
	 * MultiLink-Panel only
	 * 
	 * RequestBody:  non
	 * ResponseBody: FilePathResponseBody
	 */
	public Response<FilePathResponseBody> getThumbnailPath(Request request) throws IOException, InvalidResponseException {
		String getMethod = getQueryValue(request.getQuery(), PARAMETER_GET_METHOD_KEY);
		if (! "filePath".equals(getMethod)) {
			throw new IllegalArgumentException("Invalid parameter: getMethod.");
		}
		
		RestResponse restResponse = execute(
				build(RestRequest.METHOD_GET, String.format(REST_PATH_JOBS_ID_THUMBNAIL, jobId), request));
		Map<String, Object> body = GenericJsonDecoder.decodeToMap(restResponse.makeContentString("UTF-8"));
		
		switch (restResponse.getStatusCode()) {
			case 200:
				return new Response<FilePathResponseBody>(restResponse, new FilePathResponseBody(body));
			default:
				throw Utils.createInvalidResponseException(restResponse, body);
		}
	}
	
	/*
	 * GET: /rws/service/scanner/jobs/{jobId}/ocrdata?format=text
	 * 
	 * RequestBody:  non
	 * ResponseBody: GetOcrTextResponseBody
	 */
	public Response<GetOcrTextResponseBody> getOcrText(Request request) throws IOException, InvalidResponseException {
        String format = getQueryValue(request.getQuery(), PARAMETER_FORMAT_KEY);
        if((format != null) && (! "text".equals(format))) {
            throw new IllegalArgumentException("Invalid parameter: format.");
        }
		
		RestResponse restResponse = execute(
				build(RestRequest.METHOD_GET, String.format(REST_PATH_JOBS_ID_OCRDATA, jobId), request));
        Map<String, Object> body = GenericJsonDecoder.decodeToMap(restResponse.makeContentString("UTF-8"));
		
		switch (restResponse.getStatusCode()) {
			case 200:
				return new Response<GetOcrTextResponseBody>(restResponse, new GetOcrTextResponseBody(body));
			default:
				throw Utils.createInvalidResponseException(restResponse, body);
		}
	}

    /*
     * GET: /rws/service/scanner/jobs/{jobId}/ocrdata?getMethod=direct&format=drf
	 * 
	 * RequestBody:  non
	 * ResponseBody: BinaryResponseBody
     */
    public Response<BinaryResponseBody> getOcrBinary(Request request) throws IOException, InvalidResponseException {
        String format = getQueryValue(request.getQuery(), PARAMETER_FORMAT_KEY);
        if(! "drf".equals(format)) {
            throw new IllegalArgumentException("Invalid parameter: format.");
        }
        String getMethod = getQueryValue(request.getQuery(), PARAMETER_GET_METHOD_KEY);
        if ((getMethod != null) && (! "direct".equals(getMethod))) {
            throw new IllegalArgumentException("Invalid parameter: getMethod.");
        }

        RestResponse restResponse = execute(
                build(RestRequest.METHOD_GET, String.format(REST_PATH_JOBS_ID_OCRDATA, jobId), request));

        switch (restResponse.getStatusCode()) {
            case 200:
                return new Response<BinaryResponseBody>(restResponse, new BinaryResponseBody(restResponse.getBytes()));
            default:
                Map<String, Object> body = GenericJsonDecoder.decodeToMap(restResponse.makeContentString("UTF-8"));
                throw Utils.createInvalidResponseException(restResponse, body);
        }
    }
	
	/*
	 * GET: /rws/service/scanner/jobs/{jobId}/ocrdata?getMethod=filePath&format=drf
	 * MultiLink-Panel only
	 * 
	 * RequestBody:  non
	 * ResponseBody: FilePathResponseBody
	 */
	public Response<FilePathResponseBody> getOcrBinaryPath(Request request) throws IOException, InvalidResponseException {
        String format = getQueryValue(request.getQuery(), PARAMETER_FORMAT_KEY);
        if(! "drf".equals(format)) {
            throw  new IllegalArgumentException("Invalid parameter: format.");
        }
		String getMethod = getQueryValue(request.getQuery(), PARAMETER_GET_METHOD_KEY);
		if (! "filePath".equals(getMethod)) {
			throw new IllegalArgumentException("Invalid parameter: getMethod.");
		}
		
		RestResponse restResponse = execute(
				build(RestRequest.METHOD_GET, String.format(REST_PATH_JOBS_ID_OCRDATA, jobId), request));
		Map<String, Object> body = GenericJsonDecoder.decodeToMap(restResponse.makeContentString("UTF-8"));
		
		switch (restResponse.getStatusCode()) {
			case 200:
				return new Response<FilePathResponseBody>(restResponse, new FilePathResponseBody(body));
			default:
				throw Utils.createInvalidResponseException(restResponse, body);
		}
	}
}
