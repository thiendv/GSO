/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.wrapper.rws.service.fax;

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

    private static final String REST_PATH_JOBS_ID                   = "/rws/service/fax/jobs/%s";
    private static final String REST_PATH_JOBS_ID_THUMBNAIL         = "/rws/service/fax/jobs/%s/thumbnail";

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
     * GET: /rws/service/fax/jobs/{jobId}
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
     * PUT: /rws/service/fax/jobs/{jobId}
     * 
     * RequestBody:  UpdateJobStatusRequestBody
     * ResponseBody: non (EmptyResponseBody)
     */
    public Response<EmptyResponseBody> updateJobStatus(Request request) throws IOException, InvalidResponseException {
        if (Logger.isDebugEnabled()) {
            if (request.hasBody()) {
                Logger.debug("fax updateJobStatus json: " + request.getBody().toEntityString());
            }
        }

        RestResponse restResponse = execute(
                build(RestRequest.METHOD_PUT, String.format(REST_PATH_JOBS_ID, jobId), request));
        Map<String, Object> body = GenericJsonDecoder.decodeToMap(restResponse.makeContentString("UTF-8"));

        switch (restResponse.getStatusCode()) {
            case 200:   // validateOnly=true
                return new Response<EmptyResponseBody>(restResponse, new EmptyResponseBody(body));
            case 202:   // validateOnly=false, status change Accepted
                return new Response<EmptyResponseBody>(restResponse, new EmptyResponseBody(body));
            default:
                throw Utils.createInvalidResponseException(restResponse, body);
        }
    }

    /*
     * GET: /rws/service/fax/jobs/{jobId}/thumbnail?getMethod=direct
     * 
     * RequestBody:  non
     * ResponseBody: BinaryResponseBody
     */
    public Response<BinaryResponseBody> getThumbnail(Request request) throws IOException, InvalidResponseException {
        String getMethod = getQueryValue(request.getQuery(), "getMethod");
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
     * GET: /rws/service/fax/jobs/{jobId}/thumbnail?getMethod=filePath
     * MultiLink-Panel only
     * 
     * RequestBody:  non
     * ResponseBody: FilePathResponseBody
     */
    public Response<FilePathResponseBody> getThumbnailPath(Request request) throws IOException, InvalidResponseException {
        String getMethod = getQueryValue(request.getQuery(), "getMethod");
        if (getMethod == null) {
            throw new IllegalArgumentException("Required parameter: getMethod.");
        }
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

}
