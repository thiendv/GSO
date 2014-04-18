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
import jp.co.ricoh.ssdk.sample.wrapper.common.EmptyResponseBody;
import jp.co.ricoh.ssdk.sample.wrapper.common.GenericJsonDecoder;
import jp.co.ricoh.ssdk.sample.wrapper.common.InvalidResponseException;
import jp.co.ricoh.ssdk.sample.wrapper.common.Request;
import jp.co.ricoh.ssdk.sample.wrapper.common.Response;
import jp.co.ricoh.ssdk.sample.wrapper.common.Utils;

public class TransmissionStandbyFile extends ApiClient {

    private static final String REST_PATH_TRANSMISSION_STANDBY_FILE         = "/rws/service/fax/transmissionStandbyFile";
    private static final String REST_PATH_TRANSMISSION_STANDBY_FILE_NUMBER  = "/rws/service/fax/transmissionStandbyFile/%s";

    public TransmissionStandbyFile() {
        super();
    }

    public TransmissionStandbyFile(RestContext context) {
        super(context);
    }

    /*
     * GET: /rws/service/fax/transmissionStandbyFile
     * 
     * RequestBody:  non
     * ResponseBody: GetStandbyFileListResponseBody
     */
    public Response<GetStandbyFileListResponseBody> getStandbyFileList(Request request) throws IOException, InvalidResponseException {
        RestResponse restResponse = execute(
                build(RestRequest.METHOD_GET, REST_PATH_TRANSMISSION_STANDBY_FILE, request));
        Map<String, Object> body = GenericJsonDecoder.decodeToMap(restResponse.makeContentString("UTF-8"));

        switch (restResponse.getStatusCode()) {
            case 200:
                return new Response<GetStandbyFileListResponseBody>(restResponse, new GetStandbyFileListResponseBody(body));
            default:
                throw Utils.createInvalidResponseException(restResponse, body);
        }
    }

    /*
     * PUT: /rws/service/fax/transmissionStandbyFile/{docNumber}
     * 
     * RequestBody:  UpdateStandbyFileRequestBody
     * ResponseBody: non (EmptyResponseBody)
     */
    public Response<EmptyResponseBody> updateFile(Request request, String docNumber) throws IOException, InvalidResponseException {
        if (docNumber == null) {
            throw new NullPointerException("docNumber must not be null.");
        }

        RestResponse restResponse = execute(
                build(RestRequest.METHOD_PUT, String.format(REST_PATH_TRANSMISSION_STANDBY_FILE_NUMBER, docNumber), request));
        Map<String, Object> body = GenericJsonDecoder.decodeToMap(restResponse.makeContentString("UTF-8"));

        switch (restResponse.getStatusCode()) {
            case 200:
                return new Response<EmptyResponseBody>(restResponse, new EmptyResponseBody(body));
            default:
                throw Utils.createInvalidResponseException(restResponse, body);
        }
    }

    /*
     * POST: /rws/service/fax/transmissionStandbyFile/{docNumber}
     * 
     * RequestBody:  non
     * ResponseBody: non (EmptyResponseBody)
     */
    public Response<EmptyResponseBody> transmitFile(Request request, String docNumber) throws IOException, InvalidResponseException {
        if (docNumber == null) {
            throw new NullPointerException("docNumber must not be null.");
        }

        RestResponse restResponse = execute(
                build(RestRequest.METHOD_POST, String.format(REST_PATH_TRANSMISSION_STANDBY_FILE_NUMBER, docNumber), request));
        Map<String, Object> body = GenericJsonDecoder.decodeToMap(restResponse.makeContentString("UTF-8"));

        switch (restResponse.getStatusCode()) {
            case 200:   // validateOnly=true
                return new Response<EmptyResponseBody>(restResponse, new EmptyResponseBody(body));
            default:
                throw Utils.createInvalidResponseException(restResponse, body);
        }
    }

    /*
     * DELETE: /rws/service/fax/transmissionStandbyFile/{docNumber}
     * 
     * RequestBody:  non
     * ResponseBody: non (EmptyResponseBody)
     */
    public Response<EmptyResponseBody> deleteFile(Request request, String docNumber) throws IOException, InvalidResponseException {
        if (docNumber == null) {
            throw new NullPointerException("docNumber must not be null.");
        }

        RestResponse restResponse = execute(
                build(RestRequest.METHOD_DELETE, String.format(REST_PATH_TRANSMISSION_STANDBY_FILE_NUMBER, docNumber), request));
        Map<String, Object> body = GenericJsonDecoder.decodeToMap(restResponse.makeContentString("UTF-8"));

        switch (restResponse.getStatusCode()) {
            case 200:
                return new Response<EmptyResponseBody>(restResponse, new EmptyResponseBody(body));
            default:
                throw Utils.createInvalidResponseException(restResponse, body);
        }
    }

}
