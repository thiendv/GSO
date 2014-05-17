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
import jp.co.ricoh.ssdk.sample.wrapper.common.GenericJsonDecoder;
import jp.co.ricoh.ssdk.sample.wrapper.common.InvalidResponseException;
import jp.co.ricoh.ssdk.sample.wrapper.common.Request;
import jp.co.ricoh.ssdk.sample.wrapper.common.Response;
import jp.co.ricoh.ssdk.sample.wrapper.common.Utils;
import jp.co.ricoh.ssdk.sample.wrapper.log.Logger;

public class Fax extends ApiClient {

    private static final String REST_PATH_CAPABILITY                = "/rws/service/fax/capability";
    private static final String REST_PATH_JOBS                      = "/rws/service/fax/jobs";
    private static final String REST_PATH_LOG_COMMUNICATION         = "/rws/service/fax/log/communication";
    private static final String REST_PATH_RECENT_DESTINATIONS       = "/rws/service/fax/recentDestinations";
    private static final String REST_PATH_STATUS                    = "/rws/service/fax/status";

    public Fax() {
        super();
    }

    public Fax(RestContext context) {
        super(context);
    }

    /*
     * GET: /rws/service/fax/capability
     * 
     * RequestBody:  non
     * ResponseBody: GetCapabilityResponseBody
     */
    public Response<GetCapabilityResponseBody> getCapability(Request request) throws IOException, InvalidResponseException {
        RestResponse restResponse = execute(
                build(RestRequest.METHOD_GET, REST_PATH_CAPABILITY, request));
        Map<String, Object> body = GenericJsonDecoder.decodeToMap(restResponse.makeContentString("UTF-8"));

        switch (restResponse.getStatusCode()) {
            case 200:
                return new Response<GetCapabilityResponseBody>(restResponse, new GetCapabilityResponseBody(body));
            default:
                throw Utils.createInvalidResponseException(restResponse, body);
        }
    }

    /*
     * POST: /rws/service/fax/jobs
     * 
     * RequestBody:  CreateJobRequestBody
     * ResponseBody: CreateJobResponseBody
     */
    public Response<CreateJobResponseBody> createJob(Request request) throws IOException, InvalidResponseException {
        if (Logger.isDebugEnabled()) {
            if (request.hasBody()) {
                Logger.debug("fax createJob json: " + request.getBody().toEntityString());
            }
        }

        RestResponse restResponse = execute(
                build(RestRequest.METHOD_POST, REST_PATH_JOBS, request));
        Map<String, Object> body = GenericJsonDecoder.decodeToMap(restResponse.makeContentString("UTF-8"));

        switch (restResponse.getStatusCode()) {
            case 200:   // validateOnly=true
                return new Response<CreateJobResponseBody>(restResponse, new CreateJobResponseBody(body));
            case 201:   // validateOnly=false, job created
                return new Response<CreateJobResponseBody>(restResponse, new CreateJobResponseBody(body));

            default:
                throw Utils.createInvalidResponseException(restResponse, body);
        }
    }

    /*
     * GET: /rws/service/fax/log/communication
     * 
     * RequestBody:  non
     * ResponseBody: GetCommunicationLogResponseBody
     */
    public Response<GetCommunicationLogResponseBody> getCommunicationLog(Request request) throws IOException, InvalidResponseException {
        RestResponse restResponse = execute(
                build(RestRequest.METHOD_GET, REST_PATH_LOG_COMMUNICATION, request));
        Map<String, Object> body = GenericJsonDecoder.decodeToMap(restResponse.makeContentString("UTF-8"));

        switch (restResponse.getStatusCode()) {
            case 200:
                return new Response<GetCommunicationLogResponseBody>(restResponse, new GetCommunicationLogResponseBody(body));
            default:
                throw Utils.createInvalidResponseException(restResponse, body);
        }
    }

    /*
     * GET: /rws/service/fax/recentDestinations
     * 
     * RequestBody:  non
     * ResponseBody: GetRecentDestinationsResponseBody
     */
    public Response<GetRecentDestinationsResponseBody> getRecentDestinations(Request request) throws IOException, InvalidResponseException {
        RestResponse restResponse = execute(
                build(RestRequest.METHOD_GET, REST_PATH_RECENT_DESTINATIONS, request));
        Map<String, Object> body = GenericJsonDecoder.decodeToMap(restResponse.makeContentString("UTF-8"));

        switch (restResponse.getStatusCode()) {
            case 200:
                return new Response<GetRecentDestinationsResponseBody>(restResponse, new GetRecentDestinationsResponseBody(body));
            default:
                throw Utils.createInvalidResponseException(restResponse, body);
        }
    }

    /*
     * GET: /rws/service/fax/status
     * 
     * RequestBody:  non
     * ResponseBody: GetFaxStatusResponseBody
     */
    public Response<GetFaxStatusResponseBody> getFaxStatus(Request request) throws IOException, InvalidResponseException {
        RestResponse restResponse = execute(
                build(RestRequest.METHOD_GET, REST_PATH_STATUS, request));
        Map<String, Object> body = GenericJsonDecoder.decodeToMap(restResponse.makeContentString("UTF-8"));

        switch (restResponse.getStatusCode()) {
            case 200:
                return new Response<GetFaxStatusResponseBody>(restResponse, new GetFaxStatusResponseBody(body));
            default:
                throw Utils.createInvalidResponseException(restResponse, body);
        }
    }

}
