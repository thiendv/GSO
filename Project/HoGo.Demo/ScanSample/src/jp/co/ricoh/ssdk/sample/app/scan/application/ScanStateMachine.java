/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.app.scan.application;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import jp.co.ricoh.ssdk.sample.app.scan.R;
import jp.co.ricoh.ssdk.sample.app.scan.activity.MainActivity;
import jp.co.ricoh.ssdk.sample.app.scan.activity.PreviewActivity;
import jp.co.ricoh.ssdk.sample.app.scan.activity.ScanActivity;
import jp.co.ricoh.ssdk.sample.function.scan.ScanImage;
import jp.co.ricoh.ssdk.sample.function.scan.ScanPDF;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.HashScanRequestAttributeSet;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.ScanException;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.ScanJobAttributeSet;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.ScanRequestAttributeSet;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.ScanResponseException;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.*;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.FileSetting.FileFormat;

import java.util.Map;

/**
 * Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£â€šÂµÃ£Æ’Â³Ã£Æ’â€”Ã£Æ’Â«Ã£â€šÂ¢Ã£Æ’â€”Ã£Æ’ÂªÃ£ï¿½Â®Ã£â€šÂ¹Ã£Æ’â€ Ã£Æ’Â¼Ã£Æ’Ë†Ã£Æ’Å¾Ã£â€šÂ·Ã£Æ’Â³
 * [Ã¥â€¡Â¦Ã§ï¿½â€ Ã¥â€ â€¦Ã¥Â®Â¹]
 * Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã£ï¿½Â®Ã£Æ’ï¿½Ã£Æ’Â³Ã£Æ’â€°Ã£Æ’ÂªÃ£Æ’Â³Ã£â€šÂ°Ã£â€šâ€™Ã¨Â¡Å’Ã£ï¿½â€žÃ£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
 * Ã£ï¿½Â¾Ã£ï¿½Å¸Ã£â‚¬ï¿½Ã¨ÂµÂ·Ã¥â€¹â€¢Ã¦â„¢â€šÃ¥ï¿½Å Ã£ï¿½Â³Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã§â„¢ÂºÃ¨Â¡Å’Ã¥Â¾Å’Ã£ï¿½Â®Ã¤Â»Â¥Ã¤Â¸â€¹Ã£ï¿½Â®Ã£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°Ã£ï¿½Â®Ã§â€�Å¸Ã¦Ë†ï¿½Ã£Æ’Â»Ã¨Â¡Â¨Ã§Â¤ÂºÃ£Æ’Â»Ã§Â Â´Ã¦Â£â€žÃ£ï¿½Â¯Ã£ï¿½â„¢Ã£ï¿½Â¹Ã£ï¿½Â¦Ã£ï¿½â€œÃ£ï¿½Â®Ã£â€šÂ¹Ã£Æ’â€ Ã£Æ’Â¼Ã£Æ’Ë†Ã£Æ’Å¾Ã£â€šÂ·Ã£Æ’Â³Ã£â€šâ€™Ã©â‚¬Å¡Ã£ï¿½â€”Ã£ï¿½Â¦Ã¥Â®Å¸Ã¨Â¡Å’Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
 *   Ã£Æ’Â»Ã£ï¿½Å Ã¥Â¾â€¦Ã£ï¿½Â¡Ã¤Â¸â€¹Ã£ï¿½â€¢Ã£ï¿½â€žÃ£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°
 *   Ã£Æ’Â»Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã¤Â¸Â­Ã£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°
 *   Ã£Æ’Â»Ã¦Â¬Â¡Ã¥Å½Å¸Ã§Â¨Â¿Ã¥Â¾â€¦Ã£ï¿½Â¡Ã£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°
 *   Ã£Æ’Â»Ã£Æ’â€”Ã£Æ’Â¬Ã£Æ’â€œÃ£Æ’Â¥Ã£Æ’Â¼Ã§â€�Â»Ã©ï¿½Â¢
 *
 * State machine of scan sample application.
 * Handles the Scan job.
 * Display/update/hide processes of the following dialog/screen are always executed by this statemachine.
 *   - Please wait dialog
 *   - Scanning dialog
 *   - Dialog asking for next original(s)
 *   - Preview screen
 */
public class ScanStateMachine {
    private static String TAG = "ScanStateMachine";

    /**
     * Ã£Æ’â€¡Ã£Æ’â€¢Ã£â€šÂ©Ã£Æ’Â«Ã£Æ’Ë†Ã£ï¿½Â®Ã£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°Ã£ï¿½Â®Ã¦Â¨ÂªÃ¥Â¹â€¦
     * Default dialog width
     */
    private static final int DEFAULT_DIALOG_WIDTH = 400;

    /**
     * Ã£Æ’Â¡Ã£â€šÂ¤Ã£Æ’Â³Ã£â€šÂ¢Ã£â€šÂ¯Ã£Æ’â€ Ã£â€šÂ£Ã£Æ’â€œÃ£Æ’â€ Ã£â€šÂ£Ã£ï¿½Â¸Ã£ï¿½Â®Ã¥ï¿½â€šÃ§â€¦Â§
     * Reference to Main activity
     */
    private static Activity mActivity;

    /**
     * Ã£ï¿½Å Ã¥Â¾â€¦Ã£ï¿½Â¡Ã£ï¿½ï¿½Ã£ï¿½Â Ã£ï¿½â€¢Ã£ï¿½â€žÃ£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°
     * "Please wait" dialog
     */
    private ProgressDialog mPleaseWaitDialog;

    /**
     * Ã¥Ë†ï¿½Ã¦Å“Å¸Ã¥Å’â€“Ã¥Â¤Â±Ã¦â€¢â€”Ã£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°
     * Boot failed dialog
     */
    private AlertDialog mBootFailedDialog;

    /**
     * Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã¤Â¸Â­Ã£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°
     * Scanning dialog
     */
    protected ProgressDialog mScanningDialog;

    /**
     * Ã£â€šÂ«Ã£â€šÂ¦Ã£Æ’Â³Ã£Æ’Ë†Ã£Æ’â‚¬Ã£â€šÂ¦Ã£Æ’Â³Ã£ï¿½â€šÃ£â€šÅ Ã¦Â¬Â¡Ã¥Å½Å¸Ã§Â¨Â¿Ã¥Â¾â€¦Ã£ï¿½Â¡Ã£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°
     * "Set next original" dialog : with count down
     */
    protected Dialog mCountDownDialog;

    /**
     * Ã£â€šÂ«Ã£â€šÂ¦Ã£Æ’Â³Ã£Æ’Ë†Ã£Æ’â‚¬Ã£â€šÂ¦Ã£Æ’Â³Ã£ï¿½ÂªÃ£ï¿½â€”Ã¦Â¬Â¡Ã¥Å½Å¸Ã§Â¨Â¿Ã¥Â¾â€¦Ã£ï¿½Â¡Ã£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°
     * "Set next original" dialog: without count down
     */
    protected Dialog mNoCountDownDialog;

    /**
     * Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£â€šÂµÃ£Æ’Â³Ã£Æ’â€”Ã£Æ’Â«Ã£â€šÂ¢Ã£Æ’â€”Ã£Æ’ÂªÃ£â€šÂ±Ã£Æ’Â¼Ã£â€šÂ·Ã£Æ’Â§Ã£Æ’Â³
     * Scan sample application object
     */
    private static ScanSampleApplication mApplication;

    /**
     * UIÃ£â€šÂ¹Ã£Æ’Â¬Ã£Æ’Æ’Ã£Æ’â€°Ã£ï¿½Â®Ã£Æ’ï¿½Ã£Æ’Â³Ã£Æ’â€°Ã£Æ’Â©Ã£Æ’Â¼
     * UI thread handler
     */
    private Handler mHandler;

    ScanStateMachine(ScanSampleApplication app, Handler handler) {
        mApplication = app;
        mHandler = handler;
    }

    /**
     * Ã£Æ’Â¡Ã£â€šÂ¤Ã£Æ’Â³Ã£â€šÂ¢Ã£â€šÂ¯Ã£Æ’â€ Ã£â€šÂ£Ã£Æ’â€œÃ£Æ’â€ Ã£â€šÂ£Ã£â€šâ€™Ã§â„¢Â»Ã©Å’Â²Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
     * Registers the MainActivity.
     *
     * @param act MainActivity
     */
    public void registActivity(Activity act) {
        mActivity = act;
    }

    /**
     * Ã§Å Â¶Ã¦â€¦â€¹Ã©ï¿½Â·Ã§Â§Â»Ã£â€šÂ¤Ã£Æ’â„¢Ã£Æ’Â³Ã£Æ’Ë†
     * State transition event
     */
    public enum ScanEvent {

        /**
         * Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã§Å Â¶Ã¦â€¦â€¹Ã£ï¿½Å’PENDINGÃ¯Â¼Ë†Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã¥Â®Å¸Ã¨Â¡Å’Ã¥â€°ï¿½Ã¯Â¼â€°Ã£ï¿½Â«Ã©ï¿½Â·Ã§Â§Â»Ã£ï¿½â€”Ã£ï¿½Å¸Ã£ï¿½â€œÃ£ï¿½Â¨Ã£â€šâ€™Ã§Â¤ÂºÃ£ï¿½â„¢Ã£â€šÂ¤Ã£Æ’â„¢Ã£Æ’Â³Ã£Æ’Ë†
         * The event to indicate that the job state has changed to PENDING (before the job starts)
         */
        CHANGE_JOB_STATE_PENDING,

        /**
         * Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã§Å Â¶Ã¦â€¦â€¹Ã£ï¿½Å’PROCESSINGÃ¯Â¼Ë†Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã¥Â®Å¸Ã¨Â¡Å’Ã¤Â¸Â­Ã¯Â¼â€°Ã£ï¿½Â«Ã©ï¿½Â·Ã§Â§Â»Ã£ï¿½â€”Ã£ï¿½Å¸Ã£ï¿½â€œÃ£ï¿½Â¨Ã£â€šâ€™Ã§Â¤ÂºÃ£ï¿½â„¢Ã£â€šÂ¤Ã£Æ’â„¢Ã£Æ’Â³Ã£Æ’Ë†
         * The event to indicate that the job state has changed to PROCESSING (the job is processing)
         */
        CHANGE_JOB_STATE_PROCESSING,

        /**
         * Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã§Å Â¶Ã¦â€¦â€¹Ã£ï¿½Å’ABORTEDÃ¯Â¼Ë†Ã£â€šÂ·Ã£â€šÂ¹Ã£Æ’â€ Ã£Æ’Â Ã¥ï¿½Â´Ã£ï¿½Â§Ã¨ÂªÂ­Ã¥ï¿½â€“Ã¤Â¸Â­Ã¦Â­Â¢Ã¯Â¼â€°Ã£ï¿½Â«Ã©ï¿½Â·Ã§Â§Â»Ã£ï¿½â€”Ã£ï¿½Å¸Ã£ï¿½â€œÃ£ï¿½Â¨Ã£â€šâ€™Ã§Â¤ÂºÃ£ï¿½â„¢Ã£â€šÂ¤Ã£Æ’â„¢Ã£Æ’Â³Ã£Æ’Ë†
         * The event to indicate that the job state has changed to ABORTED (the job is aborted by system)
         */
        CHANGE_JOB_STATE_ABORTED,

        /**
         * Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã§Å Â¶Ã¦â€¦â€¹Ã£ï¿½Å’CANCELEDÃ¯Â¼Ë†Ã£Æ’Â¦Ã£Æ’Â¼Ã£â€šÂ¶Ã£Æ’Â¼Ã¦â€œï¿½Ã¤Â½Å“Ã£ï¿½Â§Ã¨ÂªÂ­Ã¥ï¿½â€“Ã¤Â¸Â­Ã¦Â­Â¢Ã¯Â¼â€°Ã£ï¿½Â«Ã©ï¿½Â·Ã§Â§Â»Ã£ï¿½â€”Ã£ï¿½Å¸Ã£ï¿½â€œÃ£ï¿½Â¨Ã£â€šâ€™Ã§Â¤ÂºÃ£ï¿½â„¢Ã£â€šÂ¤Ã£Æ’â„¢Ã£Æ’Â³Ã£Æ’Ë†
         * The event to indicate that the job state has changed to CANCELED (the job is canceled by user)
         */
        CHANGE_JOB_STATE_CANCELED,

        /**
         * Ã©â‚¬ï¿½Ã¤Â¿Â¡Ã¥â€°ï¿½Ã£Æ’â€”Ã£Æ’Â¬Ã£Æ’â€œÃ£Æ’Â¥Ã£Æ’Â¼Ã§Å Â¶Ã¦â€¦â€¹Ã£ï¿½Â«Ã©ï¿½Â·Ã§Â§Â»Ã£ï¿½â€”Ã£ï¿½Å¸Ã£ï¿½â€œÃ£ï¿½Â¨Ã£â€šâ€™Ã§Â¤ÂºÃ£ï¿½â„¢Ã£â€šÂ¤Ã£Æ’â„¢Ã£Æ’Â³Ã£Æ’Ë†
         * The event to indicate that the job state has changed to the preview state before the data is sent
         */
        CHANGE_JOB_STATE_STOPPED_PREVIEW,

        /**
         * Ã¤Â¸â‚¬Ã¦â„¢â€šÃ¥ï¿½Å“Ã¦Â­Â¢Ã¤Â¸Â­Ã£ï¿½Â®Ã£â€šÂ«Ã£â€šÂ¦Ã£Æ’Â³Ã£Æ’Ë†Ã£Æ’â‚¬Ã£â€šÂ¦Ã£Æ’Â³Ã§Å Â¶Ã¦â€¦â€¹Ã£ï¿½Â«Ã©ï¿½Â·Ã§Â§Â»Ã£ï¿½â€”Ã£ï¿½Å¸Ã£ï¿½â€œÃ£ï¿½Â¨Ã£â€šâ€™Ã§Â¤ÂºÃ£ï¿½â„¢Ã£â€šÂ¤Ã£Æ’â„¢Ã£Æ’Â³Ã£Æ’Ë†
         * The event to indicate that the job state has changed to the countdown state for pausing
         */
        CHANGE_JOB_STATE_STOPPED_COUNTDOWN,

        /**
         * Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã§Å Â¶Ã¦â€¦â€¹Ã£ï¿½Å’COMPLETEDÃ¯Â¼Ë†Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã¦Â­Â£Ã¥Â¸Â¸Ã§Âµâ€šÃ¤Âºâ€ Ã¯Â¼â€°Ã£ï¿½Â«Ã©ï¿½Â·Ã§Â§Â»Ã£ï¿½â€”Ã£ï¿½Å¸Ã£ï¿½â€œÃ£ï¿½Â¨Ã£â€šâ€™Ã§Â¤ÂºÃ£ï¿½â„¢Ã£â€šÂ¤Ã£Æ’â„¢Ã£Æ’Â³Ã£Æ’Ë†
         * The event to indicate that the job state has changed to COMPLETED (the job ended successfully)
         */
        CHANGE_JOB_STATE_COMPLETED,


        /**
         * Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã¥Â®Å¸Ã¨Â¡Å’Ã£ï¿½Â®Ã£Æ’ÂªÃ£â€šÂ¯Ã£â€šÂ¨Ã£â€šÂ¹Ã£Æ’Ë†Ã£â€šÂ¤Ã£Æ’â„¢Ã£Æ’Â³Ã£Æ’Ë†
         * Job start request event
         */
        REQUEST_JOB_START,

        /**
         * Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£â€šÂ»Ã£Æ’Â«Ã£ï¿½Â®Ã£Æ’ÂªÃ£â€šÂ¯Ã£â€šÂ¨Ã£â€šÂ¹Ã£Æ’Ë†Ã£â€šÂ¤Ã£Æ’â„¢Ã£Æ’Â³Ã£Æ’Ë†
         * Job cancel request event
         */
        REQUEST_JOB_CANCEL,

        /**
         * Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã§Â¶Å¡Ã¨Â¡Å’Ã£ï¿½Â®Ã£Æ’ÂªÃ£â€šÂ¯Ã£â€šÂ¨Ã£â€šÂ¹Ã£Æ’Ë†Ã£â€šÂ¤Ã£Æ’â„¢Ã£Æ’Â³Ã£Æ’Ë†
         * Job continue request event
         */
        REQUEST_JOB_CONTINUE,

        /**
         * Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã§Âµâ€šÃ¤Âºâ€ Ã£ï¿½Â®Ã£Æ’ÂªÃ£â€šÂ¯Ã£â€šÂ¨Ã£â€šÂ¹Ã£Æ’Ë†Ã£â€šÂ¤Ã£Æ’â„¢Ã£Æ’Â³Ã£Æ’Ë†
         * Job end request event
         */
        REQUEST_JOB_END,


        /**
         * Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã¥Â®Å¸Ã¨Â¡Å’Ã¤Â¸Â­Ã£ï¿½Â®Ã¦Æ’â€¦Ã¥Â Â±Ã¥Â¤â€°Ã¦â€ºÂ´Ã£â€šÂ¤Ã£Æ’â„¢Ã£Æ’Â³Ã£Æ’Ë†
         * Information change event for scan job in process
         */
        UPDATE_JOB_STATE_PROCESSING,

        /**
         * Ã£â€šÂ¢Ã£â€šÂ¯Ã£Æ’â€ Ã£â€šÂ£Ã£Æ’â€œÃ£Æ’â€ Ã£â€šÂ£Ã§â€�Å¸Ã¦Ë†ï¿½Ã£â€šÂ¤Ã£Æ’â„¢Ã£Æ’Â³Ã£Æ’Ë†
         * MainActivity created event
         */
        ACTIVITY_CREATED,

        /**
         * Ã£â€šÂ¢Ã£â€šÂ¯Ã£Æ’â€ Ã£â€šÂ£Ã£Æ’â€œÃ£Æ’â€ Ã£â€šÂ£Ã§Âµâ€šÃ¤Âºâ€ Ã£â€šÂ¤Ã£Æ’â„¢Ã£Æ’Â³Ã£Æ’Ë†
         * MainActivity destroyed event
         */
        ACTIVITY_DESTROYED,


        /**
         * Ã¥Ë†ï¿½Ã¦Å“Å¸Ã¥Å’â€“Ã¥Â®Å’Ã¤Âºâ€ Ã£â€šÂ¤Ã£Æ’â„¢Ã£Æ’Â³Ã£Æ’Ë†
         * Initialization completed event
         */
        ACTIVITY_BOOT_COMPLETED,

        /**
         * Ã¥Ë†ï¿½Ã¦Å“Å¸Ã¥Å’â€“Ã¥Â¤Â±Ã¦â€¢â€”Ã£â€šÂ¤Ã£Æ’â„¢Ã£Æ’Â³Ã£Æ’Ë†
         * Initialization failed event
         */
        ACTIVITY_BOOT_FAILED,

        /**
         * Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã£Æ’ÂªÃ£â€šÂ»Ã£Æ’Æ’Ã£Æ’Ë†Ã¥Â®Å’Ã¤Âºâ€ Ã£â€šÂ¤Ã£Æ’â„¢Ã£Æ’Â³Ã£Æ’Ë†
         * Job reset completed event
         */
        REBOOT_COMPLETED,
    }

    /**
     * Ã£â€šÂ¹Ã£Æ’â€ Ã£Æ’Â¼Ã£Æ’Ë†Ã£Æ’Å¾Ã£â€šÂ·Ã£Æ’Â³Ã£ï¿½Â®Ã¥Ë†ï¿½Ã¦Å“Å¸Ã§Å Â¶Ã¦â€¦â€¹
     * Statemachine initial state
     */
    private State mState = State.INITIAL;

    /**
     * Ã§Å Â¶Ã¦â€¦â€¹Ã¥Â®Å¡Ã§Â¾Â©
     * State definition
     */
    public enum State {
        /**
         * Ã¥Ë†ï¿½Ã¦Å“Å¸Ã§Å Â¶Ã¦â€¦â€¹
         * Initial state
         */
        INITIAL {
            @Override
            public State getNextState(final ScanStateMachine sm, final ScanEvent event, final Object prm) {
                switch (event) {
                case ACTIVITY_CREATED:
                    actShowPleaseWait(sm, prm);
                    return INITIAL;
                case ACTIVITY_BOOT_COMPLETED:
                    actClosePleaseWait(sm, prm);
                    return IDLE;
                case ACTIVITY_BOOT_FAILED:
                    actClosePleaseWait(sm, prm);
                    actShowBootFailedDialog(sm, prm);
                    return INITIAL;
                default:
                    return super.getNextState(sm, event, prm);
                }
            }
        },
        /**
         * Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã§â€�Å¸Ã¦Ë†ï¿½Ã¥Â¾Å’Ã£ï¿½Â®Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã©â€“â€¹Ã¥Â§â€¹Ã¥â€°ï¿½Ã§Å Â¶Ã¦â€¦â€¹
         * The state before the job is started after the job has been created
         */
        IDLE {
            @Override
            public State getNextState(final ScanStateMachine sm, final ScanEvent event, final Object prm) {
                switch (event) {
                    case CHANGE_JOB_STATE_PENDING :
                        return JOB_PENDING;
                    case REQUEST_JOB_START :
                        return WAITING_JOB_START;
                default:
                    return super.getNextState(sm, event, prm);
                }
            }
        },
        /**
         * Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã©â€“â€¹Ã¥Â§â€¹Ã¥Â¾â€¦Ã£ï¿½Â¡Ã§Å Â¶Ã¦â€¦â€¹
         * Job start waiting state
         */
        WAITING_JOB_START {
            @Override
            public State getNextState(final ScanStateMachine sm, final ScanEvent event, final Object prm) {
                switch (event) {
                case REQUEST_JOB_CANCEL:
                    return WAITING_JOB_CANCEL;

                case CHANGE_JOB_STATE_PENDING :
                    return JOB_PENDING;
                case CHANGE_JOB_STATE_PROCESSING :
                    return JOB_PROCESSING;

                case CHANGE_JOB_STATE_ABORTED :
                    return JOB_ABORTED;
                case CHANGE_JOB_STATE_CANCELED :
                    return JOB_CANCELED;

                case ACTIVITY_DESTROYED:
                    return WAITING_JOB_CANCEL;
                default:
                    return super.getNextState(sm, event, prm);
                }
            }
            @Override
            public void entry(final ScanStateMachine sm, final Object prm) {
                super.entry(sm, prm);
                actShowScanningDialog(sm, prm);
                sm.new StartScanJobTask().execute();
            }
        },
        /**
         * Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã©â€“â€¹Ã¥Â§â€¹Ã¥Â¾Å’Ã£ï¿½Â®Ã¥Â®Å¸Ã¨Â¡Å’Ã¥â€°ï¿½Ã§Å Â¶Ã¦â€¦â€¹
         * The state before the job is started after the job has been started
         */
        JOB_PENDING {
            @Override
            public State getNextState(final ScanStateMachine sm, final ScanEvent event, final Object prm) {
                switch (event) {
                    case CHANGE_JOB_STATE_PROCESSING :
                        return JOB_PROCESSING;
                    case REQUEST_JOB_CANCEL:
                        return WAITING_JOB_CANCEL;

                    case CHANGE_JOB_STATE_ABORTED :
                        return JOB_ABORTED;
                    case CHANGE_JOB_STATE_CANCELED :
                        return JOB_CANCELED;

                    case ACTIVITY_DESTROYED:
                        return WAITING_JOB_CANCEL;
                default:
                    return super.getNextState(sm, event, prm);
                }
            }
        },
        /**
         * Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã¥Â®Å¸Ã¨Â¡Å’Ã¤Â¸Â­
         * Job processing
         */
        JOB_PROCESSING {
            @Override
            public State getNextState(final ScanStateMachine sm, final ScanEvent event, final Object prm) {
                switch (event) {
                    case CHANGE_JOB_STATE_ABORTED :
                        return JOB_ABORTED;
                    case CHANGE_JOB_STATE_CANCELED :
                        return JOB_CANCELED;
                    case CHANGE_JOB_STATE_STOPPED_PREVIEW :
                        return JOB_STOPPED_PREVIEW;
                    case CHANGE_JOB_STATE_STOPPED_COUNTDOWN :
                        return JOB_STOPPED_COUNTDOWN;
                    case CHANGE_JOB_STATE_COMPLETED :
                        return JOB_COMPLETED;
                    case REQUEST_JOB_CANCEL:
                        return WAITING_JOB_CANCEL;
                    case UPDATE_JOB_STATE_PROCESSING :
                        return JOB_PROCESSING;

                    case ACTIVITY_DESTROYED:
                        return WAITING_JOB_CANCEL;
                    default:
                        return super.getNextState(sm, event, prm);
                }
            }
            @Override
            public void entry(final ScanStateMachine sm, final Object prm) {
                super.entry(sm, prm);
                actUpdateScanningDialog(sm, prm);
            }
        },
        /**
         * Ã£â€šÂ·Ã£â€šÂ¹Ã£Æ’â€ Ã£Æ’Â Ã¥ï¿½Â´Ã£ï¿½Â«Ã£â€šË†Ã£â€šÅ Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£â€šÂ»Ã£Æ’Â«
         * Job cancelling by system
         */
        JOB_ABORTED {
            public State getNextState(final ScanStateMachine sm, final ScanEvent event, final Object prm) {
                switch (event) {
                    case REBOOT_COMPLETED :
                        return IDLE;
                    default:
                        return super.getNextState(sm, event, prm);
                }
            }
            @Override
            public void entry(final ScanStateMachine sm, final Object prm) {
                super.entry(sm, prm);
                actCloseScanningDialog(sm,prm);
                actCloseWaitForNextOriginal(sm,prm);
                actClosePreview(sm,prm);

                // show toast message with aborted reason
                String message = "job aborted.";
                if (prm instanceof ScanJobAttributeSet) {
                    ScanJobAttributeSet attributes = (ScanJobAttributeSet) prm;
                    ScanJobStateReasons reasons = (ScanJobStateReasons) attributes.get(ScanJobStateReasons.class);
                    if (reasons != null) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(message);
                        sb.append(System.getProperty("line.separator"));
                        sb.append(reasons.getReasons().toString());
                        message = sb.toString();
                    }
                }
                actShowToastMessage(sm, message);

                actInitJobSetting(sm, prm);
            }
        },
        /**
         * Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£â€šÂ»Ã£Æ’Â«Ã¤Â¸Â­
         * Job cancelling
         */
        JOB_CANCELED {
            public State getNextState(final ScanStateMachine sm, final ScanEvent event, final Object prm) {
                switch (event) {
                    case CHANGE_JOB_STATE_PENDING:
                        return JOB_PENDING;
                    case REBOOT_COMPLETED :
                        return IDLE;
                    default:
                        return super.getNextState(sm, event, prm);
                }
            }
            @Override
            public void entry(final ScanStateMachine sm, final Object prm) {
                super.entry(sm, prm);
                actCloseScanningDialog(sm,prm);
                actCloseWaitForNextOriginal(sm,prm);
                actClosePreview(sm,prm);
                actInitJobSetting(sm, prm);
            }
        },
        /**
         * Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã¤Â¸â‚¬Ã¦â„¢â€šÃ¥ï¿½Å“Ã¦Â­Â¢Ã¤Â¸Â­(Ã¦Â¬Â¡Ã¥Å½Å¸Ã§Â¨Â¿Ã¥Â¾â€¦Ã£ï¿½Â¡Ã£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°Ã¨Â¡Â¨Ã§Â¤Âº)
         * Job pausing (display "set next original" dialog)
         */
        JOB_STOPPED_COUNTDOWN {
            @Override
            public State getNextState(final ScanStateMachine sm, final ScanEvent event, final Object prm) {
                switch (event) {
                    case REQUEST_JOB_CONTINUE:
                        return WAITING_JOB_CONTINUE;
                    case REQUEST_JOB_CANCEL:
                        return WAITING_JOB_CANCEL;
                    case REQUEST_JOB_END:
                        return WAITING_JOB_END;

                    case CHANGE_JOB_STATE_PROCESSING :
                        return JOB_PROCESSING;
                    case CHANGE_JOB_STATE_COMPLETED :
                        return JOB_COMPLETED;
                    case CHANGE_JOB_STATE_ABORTED :
                        return JOB_ABORTED;
                    case CHANGE_JOB_STATE_CANCELED :
                        return JOB_CANCELED;

                    case CHANGE_JOB_STATE_STOPPED_PREVIEW :
                        return JOB_STOPPED_PREVIEW;

                    case ACTIVITY_DESTROYED:
                        return WAITING_JOB_CANCEL;
                    default:
                        return super.getNextState(sm, event, prm);
                }
            }
            @Override
            public void entry(final ScanStateMachine sm, final Object prm) {
                super.entry(sm, prm);
                actWaitForNextOriginal(sm, prm);
            }
        },
        /**
         * Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã¤Â¸â‚¬Ã¦â„¢â€šÃ¥ï¿½Å“Ã¦Â­Â¢Ã¤Â¸Â­Ã¯Â¼Ë†Ã£Æ’â€”Ã£Æ’Â¬Ã£Æ’â€œÃ£Æ’Â¥Ã£Æ’Â¼Ã¨Â¡Â¨Ã§Â¤ÂºÃ¯Â¼â€°
         * Job pausing (display preview)
         */
        JOB_STOPPED_PREVIEW {
            @Override
            public State getNextState(final ScanStateMachine sm, final ScanEvent event, final Object prm) {
                switch (event) {
                case REQUEST_JOB_END:
                    return WAITING_JOB_END;
                case REQUEST_JOB_CANCEL:
                    return WAITING_JOB_CANCEL;

                case CHANGE_JOB_STATE_PROCESSING :
                    return JOB_PROCESSING;
                case CHANGE_JOB_STATE_COMPLETED :
                    return JOB_COMPLETED;
                case CHANGE_JOB_STATE_ABORTED :
                    return JOB_ABORTED;
                case CHANGE_JOB_STATE_CANCELED :
                    return JOB_CANCELED;

                case ACTIVITY_DESTROYED:
                    return WAITING_JOB_CANCEL;
                default:
                    return super.getNextState(sm, event, prm);
                }
            }
            @Override
            public void entry(final ScanStateMachine sm, final Object prm) {
                super.entry(sm, prm);
                actShowPreview(sm, prm);
            }
        },
        /**
         * Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã¥â€ ï¿½Ã©â€“â€¹Ã¥Â¾â€¦Ã£ï¿½Â¡
         * Job waiting to be resumed
         */
        WAITING_JOB_CONTINUE {
            @Override
            public State getNextState(final ScanStateMachine sm, final ScanEvent event, final Object prm) {
                switch (event) {
                    case CHANGE_JOB_STATE_PROCESSING :
                        return JOB_PROCESSING;

                    case CHANGE_JOB_STATE_COMPLETED :
                        return JOB_COMPLETED;
                    case CHANGE_JOB_STATE_ABORTED :
                        return JOB_ABORTED;
                    case CHANGE_JOB_STATE_CANCELED :
                        return JOB_CANCELED;

                    case ACTIVITY_DESTROYED:
                        return WAITING_JOB_CANCEL;
                default:
                    return super.getNextState(sm, event, prm);
                }
            }
            @Override
            public void entry(final ScanStateMachine sm, final Object prm) {
                super.entry(sm, prm);
                sm.new ContinueScanJobTask().execute();
            }
        },
        /**
         * Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£â€šÂ»Ã£Æ’Â«Ã¥Â¾â€¦Ã£ï¿½Â¡
         * Job waiting to be cancelled
         */
        WAITING_JOB_CANCEL {
            @Override
            public State getNextState(final ScanStateMachine sm, final ScanEvent event, final Object prm) {
                switch (event) {
                    case CHANGE_JOB_STATE_CANCELED:
                        return JOB_CANCELED;

                    case CHANGE_JOB_STATE_PROCESSING :
                        return JOB_PROCESSING;
                    case CHANGE_JOB_STATE_COMPLETED :
                        return JOB_COMPLETED;
                    case CHANGE_JOB_STATE_ABORTED :
                        return JOB_ABORTED;
                default:
                    return super.getNextState(sm, event, prm);
                }
            }
            @Override
            public void entry(final ScanStateMachine sm, final Object prm) {
                super.entry(sm, prm);
                sm.new CancelScanJobTask().execute();
            }
        },
        /**
         * Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã§Âµâ€šÃ¤Âºâ€ Ã¥Â¾â€¦Ã£ï¿½Â¡
         * Job waiting to be finished
         */
        WAITING_JOB_END {
            @Override
            public State getNextState(final ScanStateMachine sm, final ScanEvent event, final Object prm) {
                switch (event) {
                    case CHANGE_JOB_STATE_COMPLETED :
                        return JOB_COMPLETED;
                    case CHANGE_JOB_STATE_STOPPED_PREVIEW :
                        return JOB_STOPPED_PREVIEW;

                    case CHANGE_JOB_STATE_ABORTED :
                        return JOB_ABORTED;
                    case CHANGE_JOB_STATE_CANCELED :
                        return JOB_CANCELED;
                default:
                    return super.getNextState(sm, event, prm);
                }
            }
            @Override
            public void entry(final ScanStateMachine sm, final Object prm) {
                super.entry(sm, prm);
                sm.new EndScanJobTask().execute();
            }
        },

        /**
         * Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã¦Â­Â£Ã¥Â¸Â¸Ã§Âµâ€šÃ¤Âºâ€ 
         * Job completed successfully
         */
        JOB_COMPLETED {
            @Override
            public State getNextState(final ScanStateMachine sm, final ScanEvent event, final Object prm) {
                switch (event) {
                case REBOOT_COMPLETED :
                    return IDLE;
                default:
                    return super.getNextState(sm, event, prm);
                }
            }
            @Override
            public void entry(final ScanStateMachine sm, final Object prm) {
                super.entry(sm, prm);
                actCloseScanningDialog(sm,prm);
                actCloseWaitForNextOriginal(sm,prm);
                actClosePreview(sm,prm);
                actInitJobSetting(sm, prm);
                actPostJobCompleted(sm, prm);
            }
        },
        ;

        private static void actPostJobCompleted(ScanStateMachine sm, Object prm) {
            ((ScanActivity) mActivity).onJobCompleted();
        }


        /**
         * Ã¦Â¬Â¡Ã£ï¿½Â®Ã§Å Â¶Ã¦â€¦â€¹Ã£â€šâ€™Ã¥ï¿½â€“Ã¥Â¾â€”Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
         * Ã¥ï¿½â€žÃ§Å Â¶Ã¦â€¦â€¹Ã£ï¿½Å’Ã£â€šÂªÃ£Æ’Â¼Ã£Æ’ï¿½Ã£Æ’Â¼Ã£Æ’Â©Ã£â€šÂ¤Ã£Æ’â€°Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
         * Obtains the next state.
         * Each state should override this method.
         *
         * @param sm
         * @param event
         * @param prm Object for additional information
         * @return
         */
        public State getNextState(final ScanStateMachine sm, final ScanEvent event, final Object prm) {
            switch (event) {
            default:
                return null;
            }
        }

        /**
         * Ã§Å Â¶Ã¦â€¦â€¹Ã£ï¿½Â«Ã¥â€¦Â¥Ã£â€šâ€¹Ã£ï¿½Â¨Ã£ï¿½ï¿½Ã£ï¿½Â«Ã¥â€˜Â¼Ã£ï¿½Â°Ã£â€šÅ’Ã£â€šâ€¹Ã£Æ’Â¡Ã£â€šÂ½Ã£Æ’Æ’Ã£Æ’â€°Ã£ï¿½Â§Ã£ï¿½â„¢Ã£â‚¬â€š
         * Ã¥ï¿½â€žÃ§Å Â¶Ã¦â€¦â€¹Ã£ï¿½Å’Ã¥Â¿â€¦Ã¨Â¦ï¿½Ã£ï¿½Â«Ã¥Â¿Å“Ã£ï¿½ËœÃ£ï¿½Â¦Ã£â€šÂªÃ£Æ’Â¼Ã£Æ’ï¿½Ã£Æ’Â¼Ã£Æ’Â©Ã£â€šÂ¤Ã£Æ’â€°Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
         * This method is called when entering a state.
         * Each state should override this method if necessary.
         *
         * @param sm
         * @param prm Object for additional information
         */
        public void entry(final ScanStateMachine sm, final Object prm) {
        }

        /**
         * Ã§Å Â¶Ã¦â€¦â€¹Ã£ï¿½â€¹Ã£â€šâ€°Ã¦Å Å“Ã£ï¿½â€˜Ã£â€šâ€¹Ã£ï¿½Â¨Ã£ï¿½ï¿½Ã£ï¿½Â«Ã¥â€˜Â¼Ã£ï¿½Â°Ã£â€šÅ’Ã£â€šâ€¹Ã£Æ’Â¡Ã£â€šÂ½Ã£Æ’Æ’Ã£Æ’â€°Ã£ï¿½Â§Ã£ï¿½â„¢Ã£â‚¬â€š
         * Ã¥ï¿½â€žÃ§Å Â¶Ã¦â€¦â€¹Ã£ï¿½Å’Ã¥Â¿â€¦Ã¨Â¦ï¿½Ã£ï¿½Â«Ã¥Â¿Å“Ã£ï¿½ËœÃ£ï¿½Â¦Ã£â€šÂªÃ£Æ’Â¼Ã£Æ’ï¿½Ã£Æ’Â¼Ã£Æ’Â©Ã£â€šÂ¤Ã£Æ’â€°Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
         * This method is called when exiting a state.
         * Each state should override this method if necessary.
         *
         * @param sm
         * @param prm Object for additional information
         */
        public void exit(final ScanStateMachine sm, final Object prm) {
        }


        // +++++++++++++++++++++++++++++++++++++++++
        // Ã£â€šÂ¢Ã£â€šÂ¯Ã£â€šÂ·Ã£Æ’Â§Ã£Æ’Â³Ã©â€“Â¢Ã¦â€¢Â°
        // Action method
        // +++++++++++++++++++++++++++++++++++++++++

        /**
         * PleaseWaitÃ§â€�Â»Ã©ï¿½Â¢Ã£ï¿½Â®Ã¨Â¡Â¨Ã§Â¤Âº
         * Displays please wait dialog
         */
        protected void actShowPleaseWait(final ScanStateMachine sm, final Object prm) {
            sm.mHandler.post(new Runnable() {
                @Override
                public void run() {
                    sm.showPleaseWaitDialog();
                }
            });
        }

        /**
         * PleaseWaitÃ§â€�Â»Ã©ï¿½Â¢Ã£ï¿½Â®Ã¦Â¶Ë†Ã¥Å½Â»
         * Hides please wait dialog
         */
        protected void actClosePleaseWait(final ScanStateMachine sm, final Object prm) {
            sm.mHandler.post(new Runnable() {
                @Override
                public void run() {
                    sm.closePleaseWaitDialog();
                }
            });
        }

        /**
         * Ã¥Ë†ï¿½Ã¦Å“Å¸Ã¥Å’â€“Ã¥Â¤Â±Ã¦â€¢â€”Ã§â€�Â»Ã©ï¿½Â¢Ã£ï¿½Â®Ã¨Â¡Â¨Ã§Â¤Âº
         * Displays boot failed dialog
         */
        protected void actShowBootFailedDialog(final ScanStateMachine sm, final Object prm) {
            sm.mHandler.post(new Runnable() {
                @Override
                public void run() {
                    sm.showBootFailedDialog();
                }
            });
        }

        /**
         * Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã¤Â¸Â­Ã§â€�Â»Ã©ï¿½Â¢Ã£ï¿½Â®Ã¨Â¡Â¨Ã§Â¤Âº
         * Diaplays scanning dialog
         */
        protected void actShowScanningDialog(final ScanStateMachine sm, final Object prm) {
            sm.mHandler.post(new Runnable() {
                @Override
                public void run() {
                    sm.showScanningDialog();
                }
            });
        }

        /**
         * Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã¤Â¸Â­Ã§â€�Â»Ã©ï¿½Â¢Ã£ï¿½Â®Ã¦Â¶Ë†Ã¥Å½Â»
         * Hides scanning dialog
         */
        protected void actCloseScanningDialog(final ScanStateMachine sm, final Object prm) {
            sm.mHandler.post(new Runnable() {
                @Override
                public void run() {
                    sm.closeScanningDialog();
                }
            });
        }

        /**
         * Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã¤Â¸Â­Ã§â€�Â»Ã©ï¿½Â¢Ã£ï¿½Â®Ã¦â€ºÂ´Ã¦â€“Â°
         * Updates scanning dialog
         */
        protected void actUpdateScanningDialog(final ScanStateMachine sm, final Object prm) {
            if(prm instanceof String) {
                sm.mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        sm.updateScanDialogMessage((String)prm);
                    }
                });
            }
        }

        /**
         * Ã¦Â¬Â¡Ã¥Å½Å¸Ã§Â¨Â¿Ã¥Â¾â€¦Ã£ï¿½Â¡Ã£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°Ã£ï¿½Â®Ã¨Â¡Â¨Ã§Â¤Âº
         * Displays "set next original" dialog
         */
        protected void actWaitForNextOriginal(final ScanStateMachine sm, final Object prm) {
            if (mApplication.getTimeOfWaitingNextOriginal() == 0)
                sm.mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        sm.showNoCountDownDialog();
                    }
                });
            else
                sm.mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        sm.showCountDownDialog();
                    }
                });
        }

        /**
         * Ã¦Â¬Â¡Ã¥Å½Å¸Ã§Â¨Â¿Ã¥Â¾â€¦Ã£ï¿½Â¡Ã£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°Ã£ï¿½Â®Ã¦Â¶Ë†Ã¥Å½Â»
         * Hides "set next original" dialog
         */
        protected void actCloseWaitForNextOriginal(final ScanStateMachine sm, final Object prm) {
            sm.mHandler.post(new Runnable() {
                @Override
                public void run() {
                    sm.closeCountDownDialog();
                    sm.closeNoCountDownDialog();
                }
            });
        }

        /**
         * Ã£Æ’â€”Ã£Æ’Â¬Ã£Æ’â€œÃ£Æ’Â¥Ã£Æ’Â¼Ã§â€�Â»Ã©ï¿½Â¢Ã£ï¿½Â®Ã¨Â¡Â¨Ã§Â¤Âº
         * Displays preview screen
         */
        protected void actShowPreview(final ScanStateMachine sm, final Object prm) {
            sm.mHandler.post(new Runnable() {
                @Override
                public void run() {
                    sm.showPreview();
                }
            });
        }

        /**
         * Ã£Æ’â€”Ã£Æ’Â¬Ã£Æ’â€œÃ£Æ’Â¥Ã£Æ’Â¼Ã§â€�Â»Ã©ï¿½Â¢Ã£ï¿½Â®Ã¦Â¶Ë†Ã¥Å½Â»
         * Hides preview screen
         */
        protected void actClosePreview(final ScanStateMachine sm, final Object prm) {
            sm.mHandler.post(new Runnable() {
                @Override
                public void run() {
                    sm.closePreview();
                }
            });
        }

        /**
         * ToastÃ£Æ’Â¡Ã£Æ’Æ’Ã£â€šÂ»Ã£Æ’Â¼Ã£â€šÂ¸Ã¨Â¡Â¨Ã§Â¤Âº
         * Displays toast message
         */
        protected void actShowToastMessage(final ScanStateMachine sm, final Object prm) {
            sm.mHandler.post(new Runnable() {
                @Override
                public void run() {
                    sm.showToastMessage((String)prm);
                }
            });
        }

        /**
         * Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã£ï¿½Â®Ã¥Ë†ï¿½Ã¦Å“Å¸Ã¥Å’â€“
         * Initializes scan job
         */
        protected void actInitJobSetting(final ScanStateMachine sm, final Object prm) {
            sm.mHandler.post(new Runnable() {
                @Override
                public void run() {
                    /* unlock power mode*/
                    mApplication.unlockPowerMode();
                    /* unlock offline */
                    mApplication.unlockOffline();

                    mApplication.initJobSetting();
                    sm.procScanEvent(ScanEvent.REBOOT_COMPLETED);
                }
            });
        }
    }


    /**
     * Ã§Å Â¶Ã¦â€¦â€¹Ã©ï¿½Â·Ã§Â§Â»Ã£â€šâ€™Ã¨Â¡Å’Ã£ï¿½â€žÃ£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
     * Changes states.
     *
     * @param event
     */
    public void procScanEvent(final ScanEvent event) {
        procScanEvent(event, null);
    }

    /**
     * Ã§Å Â¶Ã¦â€¦â€¹Ã©ï¿½Â·Ã§Â§Â»Ã£â€šâ€™Ã¨Â¡Å’Ã£ï¿½â€žÃ£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
     * Changes states.
     *
     * @param event
     * @param prm  Object for additional information
     */
    public void procScanEvent(final ScanEvent event, final Object prm) {
        Log.i(TAG, ">evtp :" + event);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                State newState = mState.getNextState(ScanStateMachine.this, event, prm);
                if (newState != null) {
                    Log.i(TAG, "#evtp :" + event + " state:" + mState + " > " + newState);
                    mState.exit(ScanStateMachine.this, prm);
                    mState = newState;
                    mState.entry(ScanStateMachine.this, prm);
                }
            }
        });
    }

    /*=============================================================
     * Ã£â€šÂ¹Ã£Æ’â€ Ã£Æ’Â¼Ã£Æ’Ë†Ã£Æ’Å¾Ã£â€šÂ·Ã£Æ’Â³Ã£ï¿½â€¹Ã£â€šâ€°Ã¥â€˜Â¼Ã£ï¿½Â°Ã£â€šÅ’Ã£â€šâ€¹publicÃ£Æ’Â¡Ã£â€šÂ½Ã£Æ’Æ’Ã£Æ’â€°
     * public methos called by statemachine
     *=============================================================*/

    /**
     * Ã£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°Ã£â€šâ€™Ã¦Å’â€¡Ã¥Â®Å¡Ã£ï¿½â€¢Ã£â€šÅ’Ã£ï¿½Å¸Ã¥Â¹â€¦Ã£ï¿½Â§Ã¨Â¡Â¨Ã§Â¤ÂºÃ£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
     * Displays the dialog in specified width.
     *
     * @param d dialog
     * @param width dialog width
     */
    private void showDialog(Dialog d, int width) {
        d.show();
        WindowManager.LayoutParams lp = d.getWindow().getAttributes();
        lp.width = width;
        d.getWindow().setAttributes(lp);
    }

    /**
     * Ã£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°Ã£â€šâ€™Ã£Æ’â€¡Ã£Æ’â€¢Ã£â€šÂ©Ã£Æ’Â«Ã£Æ’Ë†Ã£â€šÂµÃ£â€šÂ¤Ã£â€šÂºÃ£ï¿½Â§Ã¨Â¡Â¨Ã§Â¤ÂºÃ£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
     * Displays the dialog in default size.
     * @param d dialog
     */
    private void showDialog(Dialog d) {
        showDialog(d, DEFAULT_DIALOG_WIDTH);
    }

    /**
     * Ã£ï¿½Å Ã¥Â¾â€¦Ã£ï¿½Â¡Ã¤Â¸â€¹Ã£ï¿½â€¢Ã£ï¿½â€žÃ£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°Ã£â€šâ€™Ã¨Â¡Â¨Ã§Â¤ÂºÃ£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
     * Displays please wait dialog.
     */
    public void showPleaseWaitDialog() {
        if(mPleaseWaitDialog==null || mPleaseWaitDialog.isShowing()==false) {
            mPleaseWaitDialog = createPleaseWaitDialog();
            showDialog(mPleaseWaitDialog);
        }
    }

    /**
     * Ã£ï¿½Å Ã¥Â¾â€¦Ã£ï¿½Â¡Ã¤Â¸â€¹Ã£ï¿½â€¢Ã£ï¿½â€žÃ£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°Ã£â€šâ€™Ã©â€“â€°Ã£ï¿½ËœÃ£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
     * Hides please wait dialog.
     */
    public void closePleaseWaitDialog() {
        if(mPleaseWaitDialog!=null && mPleaseWaitDialog.isShowing()) {
            mPleaseWaitDialog.dismiss();
            mPleaseWaitDialog = null;
        }
    }

    /**
     * Ã¥Ë†ï¿½Ã¦Å“Å¸Ã¥Å’â€“Ã¥Â¤Â±Ã¦â€¢â€”Ã£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°Ã£â€šâ€™Ã¨Â¡Â¨Ã§Â¤ÂºÃ£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
     * Displays boot failed dialog.
     */
    public void showBootFailedDialog() {
        if(mBootFailedDialog==null || mBootFailedDialog.isShowing()==false) {
            mBootFailedDialog = createBootFailedDialog();
            showDialog(mBootFailedDialog);
        }
    }

    /**
     * Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã¤Â¸Â­Ã£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°Ã£â€šâ€™Ã¨Â¡Â¨Ã§Â¤ÂºÃ£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
     * Displays scanning dialog.
     */
    public void showScanningDialog() {
        if( mScanningDialog==null || mScanningDialog.isShowing()==false) {
            mScanningDialog = createScanProgressDialog();
            showDialog(mScanningDialog);
        }
    }

    /**
     * Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã¤Â¸Â­Ã£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°Ã£â€šâ€™Ã©â€“â€°Ã£ï¿½ËœÃ£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
     * Hides scanning dialog.
     */
    public void closeScanningDialog() {
        if(mScanningDialog!=null && mScanningDialog.isShowing()) {
            mScanningDialog.cancel();
            mScanningDialog = null;
        }
    }

    /**
     * Ã£â€šÂ«Ã£â€šÂ¦Ã£Æ’Â³Ã£Æ’Ë†Ã£Æ’â‚¬Ã£â€šÂ¦Ã£Æ’Â³Ã£ï¿½â€šÃ£â€šÅ Ã¦Â¬Â¡Ã¥Å½Å¸Ã§Â¨Â¿Ã¥Â¾â€¦Ã£ï¿½Â¡Ã£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°Ã£â€šâ€™Ã¨Â¡Â¨Ã§Â¤ÂºÃ£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
     * Displays "set next original" dialog (with count down)
     */
    public void showCountDownDialog() {
        if(mCountDownDialog==null || mCountDownDialog.isShowing()==false) {
            mCountDownDialog = createCountDownDialog();
            mCountDownDialog.show();
        }
    }

    /**
     * Ã£â€šÂ«Ã£â€šÂ¦Ã£Æ’Â³Ã£Æ’Ë†Ã£Æ’â‚¬Ã£â€šÂ¦Ã£Æ’Â³Ã£ï¿½â€šÃ£â€šÅ Ã¦Â¬Â¡Ã¥Å½Å¸Ã§Â¨Â¿Ã¥Â¾â€¦Ã£ï¿½Â¡Ã£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°Ã£â€šâ€™Ã©â€“â€°Ã£ï¿½ËœÃ£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
     * Hides "set next original" dialog (with count down)
     */
    public void closeCountDownDialog() {
        if(mCountDownDialog!=null && mCountDownDialog.isShowing()) {
            mCountDownDialog.cancel();
            mCountDownDialog = null;
        }
    }

    /**
     * Ã£â€šÂ«Ã£â€šÂ¦Ã£Æ’Â³Ã£Æ’Ë†Ã£Æ’â‚¬Ã£â€šÂ¦Ã£Æ’Â³Ã£ï¿½ÂªÃ£ï¿½â€”Ã¦Â¬Â¡Ã¥Å½Å¸Ã§Â¨Â¿Ã¥Â¾â€¦Ã£ï¿½Â¡Ã£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°Ã£â€šâ€™Ã¨Â¡Â¨Ã§Â¤ÂºÃ£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
     * Displays "set next original" dialog (without count down)
     */
    public void showNoCountDownDialog() {
        if(mNoCountDownDialog==null || mNoCountDownDialog.isShowing()==false) {
            mNoCountDownDialog = createNoCountDownDialog();
            mNoCountDownDialog.show();
        }
    }

    /**
     * Ã£â€šÂ«Ã£â€šÂ¦Ã£Æ’Â³Ã£Æ’Ë†Ã£Æ’â‚¬Ã£â€šÂ¦Ã£Æ’Â³Ã£ï¿½ÂªÃ£ï¿½â€”Ã¦Â¬Â¡Ã¥Å½Å¸Ã§Â¨Â¿Ã¥Â¾â€¦Ã£ï¿½Â¡Ã£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°Ã£â€šâ€™Ã©â€“â€°Ã£ï¿½ËœÃ£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
     * Hides "set next original" dialog (without count down)
     */
    public void closeNoCountDownDialog() {
        if(mNoCountDownDialog!=null && mNoCountDownDialog.isShowing()) {
            mNoCountDownDialog.cancel();
            mNoCountDownDialog = null;
        }
    }

    /**
     * Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°Ã£ï¿½Â®Ã¨Â¡Â¨Ã§Â¤ÂºÃ£â€šâ€™Ã¦â€ºÂ´Ã¦â€“Â°Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
     * Updates scanning dialog.
     */
    public void updateScanDialogMessage(String message) {
        if(mScanningDialog!=null && mScanningDialog.isShowing()) {
            mScanningDialog.setMessage(message);
        }
    }

    /**
     * Ã£Æ’â€”Ã£Æ’Â¬Ã£Æ’â€œÃ£Æ’Â¥Ã£Æ’Â¼Ã§â€�Â»Ã©ï¿½Â¢Ã£ï¿½Â«Ã©ï¿½Â·Ã§Â§Â»Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
     * Changes to the preview screen.
     */
    public void showPreview() {
        Intent intent = new Intent(mActivity, PreviewActivity.class);
        mActivity.startActivityForResult(intent, PreviewActivity.REQUEST_CODE_PREVIEW_ACTIVITY);
    }

    /**
     * Ã£Æ’â€”Ã£Æ’Â¬Ã£Æ’â€œÃ£Æ’Â¥Ã£Æ’Â¼Ã§â€�Â»Ã©ï¿½Â¢Ã£â€šâ€™Ã¦Â¶Ë†Ã¥Å½Â»Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
     * Hides preview screen
     */
    public void closePreview() {
        mActivity.finishActivity(PreviewActivity.REQUEST_CODE_PREVIEW_ACTIVITY);
    }

    /**
     * ToastÃ£Æ’Â¡Ã£Æ’Æ’Ã£â€šÂ»Ã£Æ’Â¼Ã£â€šÂ¸Ã£â€šâ€™Ã¨Â¡Â¨Ã§Â¤ÂºÃ£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
     * Displays a toast message.
     */
    void showToastMessage(String message) {
        Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
    }

    /*=============================================================
     * Ã£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°Ã§â€�Å¸Ã¦Ë†ï¿½
     * Creates dialog
     *=============================================================*/

    /**
     * Ã£ï¿½â€”Ã£ï¿½Â°Ã£â€šâ€°Ã£ï¿½ï¿½Ã£ï¿½Å Ã¥Â¾â€¦Ã£ï¿½Â¡Ã¤Â¸â€¹Ã£ï¿½â€¢Ã£ï¿½â€žÃ£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°Ã£â€šâ€™Ã§â€�Å¸Ã¦Ë†ï¿½Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
     * Creates the "please wait" dialog.
     */
    private ProgressDialog createPleaseWaitDialog() {
        ProgressDialog dialog = new ProgressDialog(mActivity);
        dialog.setMessage(mActivity.getString(R.string.txid_cmn_d_wait));
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, mActivity.getString(R.string.txid_cmn_b_cancel),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mActivity.finish();
                }
            });
        return dialog;
    }

    /**
     * Ã¥Ë†ï¿½Ã¦Å“Å¸Ã¥Å’â€“Ã¥Â¤Â±Ã¦â€¢â€”Ã£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°Ã£â€šâ€™Ã§â€�Å¸Ã¦Ë†ï¿½Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
     * Creates the "boot failed" dialog.
     */
    private AlertDialog createBootFailedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(R.string.txid_cmn_b_error);
        builder.setMessage(R.string.txid_cmn_d_cannot_connect);
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.txid_cmn_b_close,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mActivity.finish();
                }
            });
        return builder.create();
    }

    /**
     * Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã¤Â¸Â­Ã£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°Ã£â€šâ€™Ã§â€�Å¸Ã¦Ë†ï¿½Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
     * Creates the scanning dialog.
     */
    private ProgressDialog createScanProgressDialog() {
        ProgressDialog dialog = new ProgressDialog(mActivity);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(mActivity.getString(R.string.txid_scan_d_scanning));
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, mActivity.getString(R.string.txid_cmn_b_cancel),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    procScanEvent(ScanEvent.REQUEST_JOB_CANCEL);
                }
            });
        return dialog;
    }

    /**
     * Ã¤Â¸â‚¬Ã¦â„¢â€šÃ¥ï¿½Å“Ã¦Â­Â¢Ã£â€šÂ¤Ã£Æ’â„¢Ã£Æ’Â³Ã£Æ’Ë†Ã¤Â¸Â­Ã£ï¿½Â®Ã£â€šÂ«Ã£â€šÂ¦Ã£Æ’Â³Ã£Æ’Ë†Ã£Æ’â‚¬Ã£â€šÂ¦Ã£Æ’Â³Ã£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°Ã£â€šâ€™Ã§â€�Å¸Ã¦Ë†ï¿½Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
     * Creates the "set next original" dialog (with countdown) for the pause event.
     */
   private Dialog createCountDownDialog() {
        final Dialog countDownDialog = new Dialog(mActivity);
        countDownDialog.setTitle("Scanning");
        countDownDialog.setContentView(R.layout.dlg_count_down);
        countDownDialog.show();

        final CountDownTimer timer = new CountDownTimer((mApplication.getTimeOfWaitingNextOriginal()+1)*1000, 1000) {
            @Override
            public void onTick(long millis) {
                ((TextView) countDownDialog.findViewById(R.id.txt_count_down))
                .setText(String.format(mActivity.getString(R.string.txid_scan_t_count_down_text),
                        millis / 1000));
            }

            @Override
            public void onFinish() {
                countDownDialog.dismiss();
                procScanEvent(ScanEvent.REQUEST_JOB_END);
            }
        }.start();

        countDownDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                timer.cancel();
            }
        });

        // continue button
        Button start = (Button) countDownDialog.findViewById(R.id.btn_count_down_start);
        start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                countDownDialog.dismiss();
                procScanEvent(ScanEvent.REQUEST_JOB_CONTINUE);
            }
        });

        // finish button
        Button finish = (Button) countDownDialog.findViewById(R.id.btn_count_down_finish);
        finish.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                countDownDialog.dismiss();
                procScanEvent(ScanEvent.REQUEST_JOB_END);
            }
        });

        return countDownDialog;
    }

   /**
    * Ã¤Â¸â‚¬Ã¦â„¢â€šÃ¥ï¿½Å“Ã¦Â­Â¢Ã£â€šÂ¤Ã£Æ’â„¢Ã£Æ’Â³Ã£Æ’Ë†Ã¤Â¸Â­Ã£ï¿½Â®Ã£â€šÂ«Ã£â€šÂ¦Ã£Æ’Â³Ã£Æ’Ë†Ã£Æ’â‚¬Ã£â€šÂ¦Ã£Æ’Â³Ã£ï¿½ÂªÃ£ï¿½â€”Ã£Æ’â‚¬Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â­Ã£â€šÂ°Ã£â€šâ€™Ã§â€�Å¸Ã¦Ë†ï¿½Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
    * Creates "set next original" dialog (without countdown) for the pause event.
    */
  private Dialog createNoCountDownDialog() {
       final Dialog noCountDownDialog = new Dialog(mActivity);
       noCountDownDialog.setTitle("Scanning");
       noCountDownDialog.setContentView(R.layout.dlg_no_count_down);
       noCountDownDialog.show();

       // continue button
       Button start = (Button) noCountDownDialog.findViewById(R.id.btn_count_down_start);
       start.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               noCountDownDialog.dismiss();
               procScanEvent(ScanEvent.REQUEST_JOB_CONTINUE);
           }
       });

       // finish button
       Button finish = (Button) noCountDownDialog.findViewById(R.id.btn_count_down_finish);
       finish.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               noCountDownDialog.dismiss();
               procScanEvent(ScanEvent.REQUEST_JOB_END);
           }
       });

       return noCountDownDialog;
   }

   /*=============================================================
    * Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã¦â€œï¿½Ã¤Â½Å“Ã£ï¿½Â®Ã©ï¿½Å¾Ã¥ï¿½Å’Ã¦Å“Å¸Ã£â€šÂ¿Ã£â€šÂ¹Ã£â€šÂ¯
    * The asynchronous task to start the scan job.
    *=============================================================*/

   /**
    * ScanResponseExceptionÃ£ï¿½Â®Ã£â€šÂ¨Ã£Æ’Â©Ã£Æ’Â¼Ã¦Æ’â€¦Ã¥Â Â±Ã£â€šâ€™Ã¦â€“â€¡Ã¥Â­â€”Ã¥Ë†â€”Ã¥Å’â€“Ã£ï¿½â€”Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
    * Ã£Æ’â€¢Ã£â€šÂ©Ã£Æ’Â¼Ã£Æ’Å¾Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Â¯Ã¤Â»Â¥Ã¤Â¸â€¹Ã£ï¿½Â®Ã©â‚¬Å¡Ã£â€šÅ Ã£ï¿½Â§Ã£ï¿½â„¢Ã£â‚¬â€š
    * Creates the string of the ScanResponseException error information.
    * The format is as below.
    *
    * base[separator]
    * [separator]
    * message_id: message[separator]
    * message_id: message[separator]
    * message_id: message
    *
    * @param e Ã¦â€“â€¡Ã¥Â­â€”Ã¥Ë†â€”Ã¥Å’â€“Ã¥Â¯Â¾Ã¨Â±Â¡Ã£ï¿½Â®ScanResponseException
    *          ScanResponseException to be converted as a string
    * @param base Ã£Æ’Â¡Ã£Æ’Æ’Ã£â€šÂ»Ã£Æ’Â¼Ã£â€šÂ¸Ã¥â€¦Ë†Ã©Â Â­Ã¦â€“â€¡Ã¥Â­â€”Ã¥Ë†â€”
    *             Starting string of the message
    * @return Ã£Æ’Â¡Ã£Æ’Æ’Ã£â€šÂ»Ã£Æ’Â¼Ã£â€šÂ¸Ã¦â€“â€¡Ã¥Â­â€”Ã¥Ë†â€”
    *         Message string
    */
   private String makeJobErrorResponceMessage(ScanResponseException e, String base) {
       StringBuilder sb = new StringBuilder(base);
       if (e.hasErrors()) {
           Map<String, String> errors = e.getErrors();
           if (errors.size() > 0) {
               String separator = System.getProperty("line.separator");
               sb.append(separator);

               for (Map.Entry<String, String> entry : errors.entrySet()) {
                   sb.append(separator);
                   // message_id
                   sb.append(entry.getKey());
                   // message (if exists)
                   String message = entry.getValue();
                   if ((message != null) && (message.length() > 0)) {
                       sb.append(": ");
                       sb.append(message);
                   }
               }
           }
       }
       return sb.toString();
   }

   /**
    * Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã£â€šâ€™Ã¥Â®Å¸Ã¨Â¡Å’Ã©â€“â€¹Ã¥Â§â€¹Ã£ï¿½â„¢Ã£â€šâ€¹Ã£ï¿½Å¸Ã£â€šï¿½Ã£ï¿½Â®Ã©ï¿½Å¾Ã¥ï¿½Å’Ã¦Å“Å¸Ã£â€šÂ¿Ã£â€šÂ¹Ã£â€šÂ¯Ã£ï¿½Â§Ã£ï¿½â„¢Ã£â‚¬â€š
    * The asynchronous task to start the scan job.
    */
   private class StartScanJobTask extends AsyncTask<Void, Void, Boolean> {

       private String message = null;

       @Override
       protected Boolean doInBackground(Void... param) {
           // lock power mode, lock offline
           if (!mApplication.lockPowerMode() || !mApplication.lockOffline()) {
               mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showToastMessage("Error: cannot start scan job. LockPowerMode() or lockOffline() failed.");
                    }
               });
               return false;
           }

           ScanSettingDataHolder scanSetDataHolder = mApplication.getScanSettingDataHolder();
//           DestinationSettingDataHolder destSetDataHolder = mApplication.getDestinationSettingDataHolder();

           Log.d(TAG, "before request...");
           Log.d(TAG, "getSelectedColorValue: " + ScanColor.AUTO_COLOR);
           Log.d(TAG, "getSelectedSideValue: " + OriginalSide.ONE_SIDE);
           Log.d(TAG, "getSelectedPreviewValue: " + OriginalPreview.OFF);
           Log.d(TAG, "getSelectedFileFormatValue: " + scanSetDataHolder.getSelectedFileFormatValue());
           Log.d(TAG, "getSelectedMultiPageValue: " + scanSetDataHolder.getSelectedMultiPageValue());

           //HoGo custom
           // Sets scan attributes.
           ScanRequestAttributeSet requestAttributes;
           requestAttributes = new HashScanRequestAttributeSet();
           requestAttributes.add(AutoCorrectJobSetting.AUTO_CORRECT_ON);
           requestAttributes.add(JobMode.SCAN_AND_STORE_TEMPORARY);
           requestAttributes.add(ScanColor.AUTO_COLOR);
           requestAttributes.add(OriginalSide.ONE_SIDE);
           //requestAttributes.add(OriginalPreview.OFF);
           requestAttributes.add(OriginalPreview.OFF);

           FileSetting fileSetting = new FileSetting();
           fileSetting.setFileFormat(FileFormat.PDF);
           fileSetting.setMultiPageFormat(true);
           requestAttributes.add(fileSetting);

//           DestinationSetting destSet = new DestinationSetting();
//           destSet.add(destSetDataHolder.getDestinationSettingItem());
//           requestAttributes.add(destSet);

           // start scan
           boolean result = false;
           try {
               Log.d(TAG, "scan requesting...");
               result = mApplication.getScanJob().scan(requestAttributes);
               Log.d(TAG, "scan requested. result=" + result);
           } catch (ScanException e) {
               message = "job start failed. " + e.getMessage();
               if (e instanceof ScanResponseException) {
                   message = makeJobErrorResponceMessage((ScanResponseException)e, message);
               }
               Log.w(TAG, message, e);
           }
           return result;
       }
       @Override
       protected void onPostExecute(Boolean result) {
           if (message != null) {
               Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
           }
           if (!result) {
               ScanStateMachine.this.procScanEvent(ScanEvent.CHANGE_JOB_STATE_CANCELED);
           }
       }

   }

   /**
    * Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã£â€šâ€™Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£â€šÂ»Ã£Æ’Â«Ã£ï¿½â„¢Ã£â€šâ€¹Ã£ï¿½Å¸Ã£â€šï¿½Ã£ï¿½Â®Ã©ï¿½Å¾Ã¥ï¿½Å’Ã¦Å“Å¸Ã£â€šÂ¿Ã£â€šÂ¹Ã£â€šÂ¯Ã£ï¿½Â§Ã£ï¿½â„¢Ã£â‚¬â€š
    * The asynchronous task to cancel the scan job.
    */
   private class CancelScanJobTask extends AsyncTask<Void, Void, Boolean> {
       @Override
       protected Boolean doInBackground(Void... param) {
           boolean result = false;
           try {
               Log.d(TAG, "scan cancel requesting...");
               result = mApplication.getScanJob().cancelScanJob();
               Log.d(TAG, "scan cancel requested. result=" + result);
           } catch (ScanException e) {
               String message = "job cancel failed. " + e.getMessage();
               if (e instanceof ScanResponseException) {
                   message = makeJobErrorResponceMessage((ScanResponseException)e, message);
               }
               Log.w(TAG, message, e);
           }
           return result;
       }
       @Override
       protected void onPostExecute(Boolean result) {
           if (!result) {
               ScanStateMachine.this.procScanEvent(ScanEvent.CHANGE_JOB_STATE_CANCELED);
           }
       }
   }

   /**
    * Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã£â€šâ€™Ã¥â€ ï¿½Ã©â€“â€¹Ã£ï¿½â„¢Ã£â€šâ€¹Ã£ï¿½Å¸Ã£â€šï¿½Ã£ï¿½Â®Ã©ï¿½Å¾Ã¥ï¿½Å’Ã¦Å“Å¸Ã£â€šÂ¿Ã£â€šÂ¹Ã£â€šÂ¯Ã£ï¿½Â§Ã£ï¿½â„¢Ã£â‚¬â€š
    * The asynchronous task to resume the scan job.
    */
   private class ContinueScanJobTask extends AsyncTask<Void, Void, Boolean> {

       private String message = null;

       @Override
       protected Boolean doInBackground(Void... param) {
           boolean result = false;
           try {
               Log.d(TAG, "scan continue requesting...");
               result = mApplication.getScanJob().continueScanJob(null);
               Log.d(TAG, "scan continue requested. result=" + result);
           } catch (ScanException e) {
               message = "job continue failed. cancel job. " + e.getMessage();
               if (e instanceof ScanResponseException) {
                   message = makeJobErrorResponceMessage((ScanResponseException)e, message);
               }
               Log.w(TAG, message, e);
           }
           return result;
       }
       @Override
       protected void onPostExecute(Boolean result) {
           if (message != null) {
               Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
           }
           if (!result) {
               new CancelScanJobTask().execute();
           }
       }
   }

   /**
    * Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£â€šÂ¸Ã£Æ’Â§Ã£Æ’â€“Ã£â€šâ€™Ã§Âµâ€šÃ¤Âºâ€ Ã£ï¿½â„¢Ã£â€šâ€¹Ã£ï¿½Å¸Ã£â€šï¿½Ã£ï¿½Â®Ã©ï¿½Å¾Ã¥ï¿½Å’Ã¦Å“Å¸Ã£â€šÂ¿Ã£â€šÂ¹Ã£â€šÂ¯
    * The asynchronous task to end the scan job.
    */
   private class EndScanJobTask extends AsyncTask<Void, Void, Boolean> {

       private String message = null;

       @Override
       protected Boolean doInBackground(Void... param) {
           boolean result = false;
           try {
               Log.d(TAG, "scan end requesting...");
               result = mApplication.getScanJob().endScanJob();
               Log.d(TAG, "scan end requested. result=" + result);
           } catch (ScanException e) {
               message = "job end failed. cancel job. " + e.getMessage();
               if (e instanceof ScanResponseException) {
                   message = makeJobErrorResponceMessage((ScanResponseException)e, message);
               }
               Log.w(TAG, message, e);
           }
           return result;
       }
       @Override
       protected void onPostExecute(Boolean result) {
           if (message != null) {
               Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
           }
           if (!result) {
               new CancelScanJobTask().execute();
           }
       }
   }

}
