/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.app.scan.application;

import android.os.Handler;

import jp.co.ricoh.ssdk.sample.app.scan.R;
import jp.co.ricoh.ssdk.sample.app.scan.application.ScanStateMachine.ScanEvent;
import jp.co.ricoh.ssdk.sample.function.common.SmartSDKApplication;
import jp.co.ricoh.ssdk.sample.function.scan.ScanJob;
import jp.co.ricoh.ssdk.sample.function.scan.ScanPDF;
import jp.co.ricoh.ssdk.sample.function.scan.ScanService;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.ScanJobAttributeSet;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.ScanJobScanningInfo;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.ScanJobSendingInfo;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.ScanJobState;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.ScanJobStateReason;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.ScanJobStateReasons;
import jp.co.ricoh.ssdk.sample.function.scan.event.ScanJobAttributeEvent;
import jp.co.ricoh.ssdk.sample.function.scan.event.ScanJobAttributeListener;
import jp.co.ricoh.ssdk.sample.function.scan.event.ScanJobEvent;
import jp.co.ricoh.ssdk.sample.function.scan.event.ScanJobListener;

/**
 * スキャンサンプルアプリのアプリケーションクラスです。
 * 設定情報を保持し、スキャンサービスとジョブの管理を行います。
 * Application class of Scan sample application.
 * Saves the setting information and manages scan service and job.
 */
public class ScanSampleApplication extends SmartSDKApplication {

    /**
     * 宛先設定
     * Destination setting
     */
	private DestinationSettingDataHolder mDestinationSettingDataHolder;

	/**
	 * スキャン設定
	 * Scan setting
	 */
	private ScanSettingDataHolder mScanSettingDataHolder;

	/**
	 * スキャンサービス
	 * Scan service
	 */
	private ScanService mScanService;

	/**
	 * スキャンジョブ
	 * Scan job
	 */
	private ScanJob mScanJob;

	/**
	 * スキャンジョブ状態変化監視リスナー
	 * Scan job listener
	 */
	private ScanJobListener mScanJobListener;

	/**
	 * スキャンジョブ属性変化監視リスナー
	 * Scan job attribute listener
	 */
	private ScanJobAttributeListener mScanJobAttrListener;

	/**
	 * ステートマシン
	 * Statemachine
	 */
	private ScanStateMachine mStateMachine;

    /**
     * 読み取ったページ数
     * Number of pages scanned
     */
    protected int scannedPages;

    /**
     * 次原稿受付までの最大待ち時間です。
     * 0を指定した場合は、待ち続けます。
     * Maximum waiting time for accept the next page.
     * This timeout value supports "0" which means "keep waiting forever".
     */
    protected int timeOfWaitingNextOriginal = 0;

	@Override
	public void onCreate() {
		super.onCreate();
		init();
	}

	@Override
    public void onTerminate() {
	    super.onTerminate();
	    mDestinationSettingDataHolder = null;
	    mScanSettingDataHolder = null;
	    mScanService = null;
	    mScanJob = null;
	    mScanJobListener = null;
	    mScanJobAttrListener = null;
	    mStateMachine = null;
	}

	/**
	 * リソースを初期化します。
	 * Initializes the resources.
	 */
	public void init() {
        mDestinationSettingDataHolder = new DestinationSettingDataHolder();
        mScanSettingDataHolder = new ScanSettingDataHolder();
        mStateMachine = new ScanStateMachine(this, new Handler());
        mScanService = ScanService.getService();
        initJobSetting();
	}

	/**
	 * スキャンジョブを初期化します。
	 * Initializes the scan job.
	 */
	public void initJobSetting() {
	    //If a state change listener is registered to the current scan job, the listener is removed.
	    if(mScanJob!=null) {
    	    if(mScanJobListener!=null) {
    	        mScanJob.removeScanJobListener(mScanJobListener);
    	    }
    	    if(mScanJobAttrListener!=null) {
    	        mScanJob.removeScanJobAttributeListener(mScanJobAttrListener);
    	    }
	    }

        mScanJob = new ScanJob();
        mScanJobListener = new ScanJobListenerImpl();
        mScanJobAttrListener = new ScanJobAttributeListenerImpl();

        //Registers a new listener to the new scan job.
        mScanJob.addScanJobListener(mScanJobListener);
        mScanJob.addScanJobAttributeListener(mScanJobAttrListener);
	}

    /**
     * スキャンジョブの属性変化監視リスナー実装クラスです。
     * [処理内容]
     *    (1)読み取り情報があれば、ステートマシンに通知します。
     *    (2)送信情報があれば、ステートマシンに通知します。
     *
     * The class to implement the listener to monitor scan job state changes.
     * [Processes]
     *    (1) If scan information exists, the information is notified to the state machine.
     *    (2) If data transfer information exists, the information is notified to the state machine.
     */
    class ScanJobAttributeListenerImpl implements ScanJobAttributeListener {

        @Override
        public void updateAttributes(ScanJobAttributeEvent attributesEvent) {
            ScanJobAttributeSet attributes = attributesEvent.getUpdateAttributes();

            //(1)
            ScanJobScanningInfo scanningInfo = (ScanJobScanningInfo) attributes.get(ScanJobScanningInfo.class);
            if (scanningInfo != null && scanningInfo.getScanningState()==ScanJobState.PROCESSING) {
                String status = getString(R.string.txid_scan_d_scanning) + " "
                        + String.format(getString(R.string.txid_scan_d_count), scanningInfo.getScannedCount());
                scannedPages = Integer.valueOf(scanningInfo.getScannedCount());
                mStateMachine.procScanEvent(ScanEvent.UPDATE_JOB_STATE_PROCESSING, status);
            }
            if (scanningInfo != null && scanningInfo.getScanningState() == ScanJobState.PROCESSING_STOPPED) {
                timeOfWaitingNextOriginal = Integer.valueOf(scanningInfo.getRemainingTimeOfWaitingNextOriginal());
            }

            //(2)
            ScanJobSendingInfo sendingInfo = (ScanJobSendingInfo) attributes.get(ScanJobSendingInfo.class);
            if (sendingInfo != null && sendingInfo.getSendingState()==ScanJobState.PROCESSING) {
                String status = getString(R.string.txid_scan_d_sending);
                mStateMachine.procScanEvent(ScanEvent.UPDATE_JOB_STATE_PROCESSING, status);
            }
        }

    }

    /**
     * スキャンジョブの状態監視リスナーです。
     * The listener to monitor scan job state changes.
     */
    class ScanJobListenerImpl  implements ScanJobListener {

		@Override
		public void jobCanceled(ScanJobEvent event) {
			mStateMachine.procScanEvent(ScanEvent.CHANGE_JOB_STATE_CANCELED);
		}

		@Override
		public void jobCompleted(ScanJobEvent event) {
            mStateMachine.procScanEvent(ScanEvent.CHANGE_JOB_STATE_COMPLETED);
        }

		@Override
		public void jobAborted(ScanJobEvent event) {
            ScanJobAttributeSet attributes = event.getAttributeSet();
			mStateMachine.procScanEvent(ScanEvent.CHANGE_JOB_STATE_ABORTED, attributes);
		}

		@Override
		public void jobProcessing(ScanJobEvent event) {
            ScanJobAttributeSet attributes = event.getAttributeSet();
			mStateMachine.procScanEvent(ScanEvent.CHANGE_JOB_STATE_PROCESSING, attributes);
		}

		@Override
		public void jobPending(ScanJobEvent event) {
			mStateMachine.procScanEvent(ScanEvent.CHANGE_JOB_STATE_PENDING);
		}

		/**
         * ジョブが一時停止状態になった際に呼び出されます。
         * 状態の理由が複数ある場合は、最初の１つのみを参照します。
         * [処理内容]
         *   (1)原稿ガラススキャン時、次原稿待ちの一時停止イベントだった場合
         *      -ステートマシンに次原稿待ちイベントを送信します。
         *   (2)プレビュー表示のための一時停止イベントだった場合
         *      -ステートマシンにプレビュー表示のイベントを送信します。
         *   (3)その他の理由による一時停止イベントだった場合
         *      -ここでは、ジョブをキャンセルします。
         *
		 * Called when the job is in paused state.
		 * If multiple reasons exist, only the first reason is checked.
		 * [Processes]
		 *   (1) For the pause event for waiting for the next document when using exposure glass
		 *        - Sends the document waiting event to the state machine.
		 *   (2) For the pause event for preview display
		 *        - Sends the preview display event to the state machine.
		 *   (3) For the pause event for other reasons
		 *        - The job is cancelled in this case.
		 */
		@Override
		public void jobProcessingStop(ScanJobEvent event) {
            ScanJobAttributeSet attributes = event.getAttributeSet();
            ScanJobStateReasons reasons = (ScanJobStateReasons)attributes.get(ScanJobStateReasons.class);

            for(ScanJobStateReason reason : reasons) {
                switch(reason) {
                    case SCANNER_JAM:
                    case WAIT_FOR_NEXT_ORIGINAL:
                        mStateMachine.procScanEvent(ScanEvent.REQUEST_JOB_CANCEL);
                        break;

                    case WAIT_FOR_NEXT_ORIGINAL_AND_CONTINUE:
                        mStateMachine.procScanEvent(ScanEvent.CHANGE_JOB_STATE_STOPPED_COUNTDOWN);
                        break;

                    case EXCEEDED_MAX_EMAIL_SIZE:
                    case EXCEEDED_MAX_PAGE_COUNT:
                    case CANNOT_DETECT_ORIGINAL_SIZE:
                    case EXCEEDED_MAX_DATA_CAPACITY:
                    case NOT_SUITABLE_ORIGINAL_ORIENTATION:
                    case TOO_SMALL_SCAN_SIZE:
                    case TOO_LARGE_SCAN_SIZE:
                        mStateMachine.procScanEvent(ScanEvent.REQUEST_JOB_CANCEL);
                        break;

                    case WAIT_FOR_ORIGINAL_PREVIEW_OPERATION:
                        ScanJobScanningInfo scanningInfo = (ScanJobScanningInfo) attributes.get(ScanJobScanningInfo.class);
                        if(scanningInfo!=null && scanningInfo.getScannedThumbnailUri()!=null) {
                          mStateMachine.procScanEvent(ScanEvent.CHANGE_JOB_STATE_STOPPED_PREVIEW, scanningInfo.getScannedThumbnailUri());
                        }
                        break;

                    default:
                        mStateMachine.procScanEvent(ScanEvent.REQUEST_JOB_CANCEL);
                        break;
                }
                break;
            }
		}
    }

    /**
     * スキャンサービスを取得します。
     * obtains the scan service
     */
    public ScanService getScanService() {
        return mScanService;
    }

    /**
     * スキャンジョブを取得します。
     * Obtains the scan job.
     */
    public ScanJob getScanJob() {
        return mScanJob;
    }

	/**
	 * 宛先のデータ保持クラスのインスタンスを取得します。
	 * Obtains the instance for the class to save destination data.
	 */
	public DestinationSettingDataHolder getDestinationSettingDataHolder() {
		return mDestinationSettingDataHolder;
	}

    /**
     * スキャンジョブの設定データ保持クラスのインスタンスを取得します。
     * Obtains the instance for the class to save scan setting data.
     */
	public ScanSettingDataHolder getScanSettingDataHolder() {
	    return mScanSettingDataHolder;
	}

	/**
	 * このアプリのステートマシンを取得します。
	 * Obtains the statemachine.
	 */
	public ScanStateMachine getStateMachine() {
		return mStateMachine;
	}

    /**
     * スキャンページ総数を取得します。
     * Obtains the number of total pages scanned.
     */
	public int getScannedPages() {
	    return scannedPages;
	}

	/**
	 * 次原稿までの最大待ち時間を取得します。
	 * Obtains the maximum waiting time.
	 * @return
	 */
	public int getTimeOfWaitingNextOriginal() {
        return timeOfWaitingNextOriginal;
    }
}
