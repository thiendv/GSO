/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.wrapper.rws.service.storage;

import java.io.IOException;
import java.util.Map;

import jp.co.ricoh.ssdk.sample.wrapper.client.RestContext;
import jp.co.ricoh.ssdk.sample.wrapper.client.RestRequest;
import jp.co.ricoh.ssdk.sample.wrapper.client.RestResponse;
import jp.co.ricoh.ssdk.sample.wrapper.common.ApiClient;
import jp.co.ricoh.ssdk.sample.wrapper.common.BinaryResponseBody;
import jp.co.ricoh.ssdk.sample.wrapper.common.GenericJsonDecoder;
import jp.co.ricoh.ssdk.sample.wrapper.common.InvalidResponseException;
import jp.co.ricoh.ssdk.sample.wrapper.common.Request;
import jp.co.ricoh.ssdk.sample.wrapper.common.RequestQuery;
import jp.co.ricoh.ssdk.sample.wrapper.common.Response;
import jp.co.ricoh.ssdk.sample.wrapper.common.Utils;

public class Files extends ApiClient {

    private static final String REST_PATH_FIELS_ID_IMAGE        = "/rws/service/storage/files/%s/image";

    public Files() {
        super();
    }

    public Files(RestContext context) {
        super(context);
    }

    private String getQueryValue(RequestQuery query, String key) {
        if (query == null) {
            return null;
        }
        return query.get(key);
    }

    /*
     * GET: /rws/service/storage/files/{fileId}/image?getMethod=direct
     *
     * RequestBody:  non
     * ResponseBody: BinaryResponseBody
     */
    public Response<BinaryResponseBody> getImage(Request request, String fileId) throws IOException, InvalidResponseException {
        if (fileId == null) {
            throw new NullPointerException("fileId must not be null.");
        }

        String getMethod = getQueryValue(request.getQuery(), "getMethod");
        if ((getMethod != null) && (! "direct".equals(getMethod))) {
            throw new IllegalArgumentException("Invalid parameter: getMethod.");
        }

        RestResponse restResponse = execute(
                build(RestRequest.METHOD_GET, String.format(REST_PATH_FIELS_ID_IMAGE, fileId), request));

        switch (restResponse.getStatusCode()) {
            case 200:
                return new Response<BinaryResponseBody>(restResponse, new BinaryResponseBody(restResponse.getBytes()));
            default:
                Map<String, Object> body = GenericJsonDecoder.decodeToMap(restResponse.makeContentString("UTF-8"));
                throw Utils.createInvalidResponseException(restResponse, body);
        }
    }

    /*
     * GET: /rws/service/storage/files/{fileId}/image?getMethod=filePath
     * MultiLink-Panel only
     *
     * RequestBody:  non
     * ResponseBody: FilePathResponseBody
     */
    public Response<FilePathResponseBody> getImagePath(Request request, String fileId) throws IOException, InvalidResponseException {
        if (fileId == null) {
            throw new NullPointerException("fileId must not be null.");
        }

        String getMethod = getQueryValue(request.getQuery(), "getMethod");
        if (getMethod == null) {
            throw new IllegalArgumentException("Required parameter: getMethod.");
        }
        if (! "filePath".equals(getMethod)) {
            throw new IllegalArgumentException("Invalid parameter: getMethod.");
        }

        RestResponse restResponse = execute(
                build(RestRequest.METHOD_GET, String.format(REST_PATH_FIELS_ID_IMAGE, fileId), request));
        Map<String, Object> body = GenericJsonDecoder.decodeToMap(restResponse.makeContentString("UTF-8"));

        switch (restResponse.getStatusCode()) {
            case 200:
                return new Response<FilePathResponseBody>(restResponse, new FilePathResponseBody(body));
            default:
                throw Utils.createInvalidResponseException(restResponse, body);
        }
    }

}
