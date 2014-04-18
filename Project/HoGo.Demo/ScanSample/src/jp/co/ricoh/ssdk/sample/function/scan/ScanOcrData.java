/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.function.scan;


import jp.co.ricoh.ssdk.sample.wrapper.common.BinaryResponseBody;
import jp.co.ricoh.ssdk.sample.wrapper.common.InvalidResponseException;
import jp.co.ricoh.ssdk.sample.wrapper.common.Request;
import jp.co.ricoh.ssdk.sample.wrapper.common.RequestQuery;
import jp.co.ricoh.ssdk.sample.wrapper.common.Response;
import jp.co.ricoh.ssdk.sample.wrapper.rws.service.scanner.FilePathResponseBody;
import jp.co.ricoh.ssdk.sample.wrapper.rws.service.scanner.GetOcrTextResponseBody;
import jp.co.ricoh.ssdk.sample.wrapper.rws.service.scanner.Job;

import java.io.IOException;
import java.io.InputStream;


/**
 * OCRデータを操作するための機能を提供します。
 * Provides the function to operate OCR data.
 */
public class ScanOcrData {

    private Job mJob = null;

    /**
     * ジョブに紐付くOCRデータオブジェクトを生成します。
     * ジョブは正常に終了した場合だけ、画像操作を行えます。
     * 正常に終了していない場合(ジョブ実行中/ジョブ異常終了など)は、本オブジェクトメソッドを実行しても
     * 操作に失敗します。
     * Creates the OCR data object associated with the job.
     * Image can be operated only when the job ends successfully.
     * If the job does not end correctly (e.g. job processing, job ended with error), the operation fails
     * even if this object method is executed.
     *
     * @param job
     */
    public ScanOcrData(ScanJob job){
        this.mJob = new Job(job.getCurrentJobId());
    }

    /**
     * OCRデータ(DRFベース)を保存しているパスを取得します。
     * 本APIはMultiLink-Panelでのみ動作します。
     * 取得するパスは絶対パスで取得できます。
     * 取得に失敗した場合は、nullが返ります。
     * Obtains the path which stores the OCR data (DRF base).
     * This API runs only on MultiLink-Panel.
     * The path can be obtained in absolute path.
     * If failing to obtain the path, null is returned.
     *
     * @param pageNo 取得するページ番号
     *               Page number to obtain
     * @return OCRデータ(DRFベース)を保存しているパス
     *         The path to save OCR data (DRF base)
     */
    public String getOcrDataDrfFilePath(int pageNo) {
        RequestQuery query = new RequestQuery();
        query.put("pageNo", Integer.toString(pageNo));
        query.put("format", "drf");
        query.put("getMethod", "filePath");

        Request request = new Request();
        request.setQuery(query);

        try{
            Response<FilePathResponseBody> response = this.mJob.getOcrBinaryPath(request);
            return response.getBody().getFilePath();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * OCRデータのテキストを取得します。
     * 取得に失敗した場合は、nullが返ります。
     * Obtains OCR data text.
     * If failing to obtain the text, null is returned.
     *
     * @param pageNo 取得するページ番号
     *               Page number to obtain
     * @return OCRテキストデータ
     *         Ocr text data
     */
    public GetOcrTextResponseBody getOcrDataText(int pageNo) {
        RequestQuery query = new RequestQuery();
        query.put("pageNo", Integer.toString(pageNo));
        query.put("format", "text");
        query.put("getMethod", "direct");

        Request request = new Request();
        request.setQuery(query);

        try{
            Response<GetOcrTextResponseBody> response = this.mJob.getOcrText(request);
            return response.getBody();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * OCRデータのDRFデータのインプットストリームを取得します。
     * 取得に失敗した場合は、nullが返ります。
     * Obtains the input stream of DRF data of OCR data.
     * If failing to obtain the input stream, null is returned.
     *
     * @param pageNo 取得するページ番号
     *               Page number to obtain
     * @return OCR DRFデータのインプットストリーム
     *         Input stream of DRF data of OCR data
     */
    public InputStream getOcrDataDrfInputStream(int pageNo) {
        RequestQuery query = new RequestQuery();
        query.put("pageNo", Integer.toString(pageNo));
        query.put("format", "drf");
        query.put("getMethod", "direct");

        Request request = new Request();
        request.setQuery(query);

        try{
            Response<BinaryResponseBody> response = this.mJob.getOcrBinary(request);
            return response.getBody().getInputStream();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
