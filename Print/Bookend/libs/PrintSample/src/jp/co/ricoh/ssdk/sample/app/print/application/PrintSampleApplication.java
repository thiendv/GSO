/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.app.print.application;

import android.os.Handler;
import android.util.Log;

import jp.co.ricoh.ssdk.sample.function.common.SmartSDKApplication;
import jp.co.ricoh.ssdk.sample.function.print.PrintFile;
import jp.co.ricoh.ssdk.sample.function.print.PrintJob;
import jp.co.ricoh.ssdk.sample.function.print.PrintService;
import jp.co.ricoh.ssdk.sample.function.print.attribute.PrintJobAttributeSet;
import jp.co.ricoh.ssdk.sample.function.print.attribute.PrintServiceAttributeSet;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.PrintJobPrintingInfo;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.PrintJobState;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.PrinterStateReasons;
import jp.co.ricoh.ssdk.sample.function.print.event.PrintJobAttributeEvent;
import jp.co.ricoh.ssdk.sample.function.print.event.PrintJobAttributeListener;

import java.util.HashMap;
import java.util.Map;

/**
 * プリントサンプルアプリのアプリケーションクラスです。
 * ジョブの発行、プリントサービスの監視を役割とするクラスです。
 * ジョブの監視はステートマシンにて行います。
 * Application class of print sample application.
 * Creates print job and monitors the print service.
 * The state machine is responsible for the job monitoring.
 */
public class PrintSampleApplication extends SmartSDKApplication {

    private PrintService mPrintService;
    private PrintJob mPrintJob;
    private PrintJobAttributeListener mJobAttributeListener;

    private PrintStateMachine mStateMachine;

    /**
     * 機器に設定可能な印刷プロパティの一覧
     * Map of the supported print properties
     */
    private Map<PrintFile.PDL, PrintSettingSupportedHolder> mSettingDataHolders;

    @Override
    public void onCreate() {
        super.onCreate();

        mStateMachine = new PrintStateMachine(this, new Handler());

        mSettingDataHolders = new HashMap<PrintFile.PDL, PrintSettingSupportedHolder>();

        mPrintService = PrintService.getService();
    }


    /***********************************************************
     * 公開メソッド
     * public methods
     ***********************************************************/

    /**
     * プリントサービスを取得します。
     * obtains the print service
     */
    public PrintService getPrintService() {
        return mPrintService;
    }

    /**
     * プリント情報をセットします。
     * Sets the print setting object.
     * @param pdl
     * @param holder
     */
    public void putPrintSettingSupportedHolder(PrintFile.PDL pdl, PrintSettingSupportedHolder holder) {
        mSettingDataHolders.put(pdl, holder);
    }

    /**
     * 印刷を開始します。
     * Start print.
     * @param holder
     */
    public void startPrint(PrintSettingDataHolder holder) {
        if(holder == null) return;

        mStateMachine.procPrintEvent(PrintStateMachine.PrintEvent.CHANGE_JOB_STATE_INITIAL, holder);
        mStateMachine.procPrintEvent(PrintStateMachine.PrintEvent.CHANGE_JOB_STATE_PRE_PROCESS, holder);
    }

    /****************************************************************
     * 内部メソッド
     * private methods
     ****************************************************************/

    /**
     * プリントジョブを初期化します。
     * Initializes the print job.
     */
    private void initialJob() {
        if( mPrintJob != null) {
            if(mJobAttributeListener != null) {
                mPrintJob.removePrintJobAttributeListener(mJobAttributeListener);
            }
        }

        mPrintJob = new PrintJob();
        mJobAttributeListener = new PrintJobAttributeListenerImpl();

        mPrintJob.addPrintJobAttributeListener(mJobAttributeListener);
    }

    /**
     * プリントジョブを取得します。
     * Obtains the print job.
     */
    PrintJob getPrintJob() {
        initialJob();
        return mPrintJob;
    }

    /****************************************************************
     * メンバのセッター/ゲッター
     * Setter/Getter method
     ****************************************************************/

    /**
     * このアプリのステートマシンを取得します。
     * Obtains the statemachine.
     */
    public PrintStateMachine getStateMachine() {
        return this.mStateMachine;
    }

    /**
     * プリントジョブの設定データ保持クラスのインスタンスを取得します。
     * Obtains the instance for the class to save print setting data.
     */
    public Map<PrintFile.PDL, PrintSettingSupportedHolder> getSettingSupportedDataHolders() {
        return this.mSettingDataHolders;
    }

     /**
     * ジョブ状態を監視するリスナークラスです。
     * The listener class to monitor scan job state changes.
     */
    class PrintJobAttributeListenerImpl implements PrintJobAttributeListener {
        /**
         * ジョブ状態が変化すると呼び出されるメソッドです。
         * ジョブ状態を示すイベントを受信し、受信したイベントに応じてステートマシンにイベントをポストします。
         * Called when the job state changes.
         * Receives the job state event and posts the appropriate event to the statemachine.
         */
        @Override
        public void updateAttributes(PrintJobAttributeEvent attributesEvent) {
            PrintJobAttributeSet attributeSet = attributesEvent.getUpdateAttributes();

            PrintJobState jobState = (PrintJobState)attributeSet.get(PrintJobState.class);
            Log.d(getClass().getSimpleName(), "JobState[" + jobState + "]");
            if(jobState == null) return;

            switch (jobState) {
                case PENDING:
                    mStateMachine.procPrintEvent(
                            PrintStateMachine.PrintEvent.CHANGE_JOB_STATE_PENDING, null);
                    break;
                case PROCESSING:
                    PrintJobPrintingInfo printingInfo = (PrintJobPrintingInfo)attributeSet.get(
                            PrintJobPrintingInfo.class);

                    mStateMachine.procPrintEvent(
                            PrintStateMachine.PrintEvent.CHANGE_JOB_STATE_PROCESSING, printingInfo);
                    break;
                case PROCESSING_STOPPED:
                    PrintServiceAttributeSet serviceAttributeSet = mPrintService.getAttributes();
                    PrinterStateReasons reasons = (PrinterStateReasons) serviceAttributeSet.get((PrinterStateReasons.class));

                    mStateMachine.procPrintEvent(
                            PrintStateMachine.PrintEvent.CHANGE_JOB_STATE_PROCESSING_STOPPED, reasons);
                    break;
                case ABORTED:
                    serviceAttributeSet = mPrintService.getAttributes();
                    reasons = (PrinterStateReasons) serviceAttributeSet.get((PrinterStateReasons.class));

                    mStateMachine.procPrintEvent(
                            PrintStateMachine.PrintEvent.CHANGE_JOB_STATE_ABORTED, reasons);
                    break;
                case CANCELED:
                    mStateMachine.procPrintEvent(
                            PrintStateMachine.PrintEvent.CHANGE_JOB_STATE_CANCELED, null);
                    break;
                case COMPLETED:
                    mStateMachine.procPrintEvent(
                            PrintStateMachine.PrintEvent.CHANGE_JOB_STATE_COMPLETED, null);
                    break;
                default:
                    break;
            }
        }
    }
}
