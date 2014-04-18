/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.function.scan.impl.service;

import android.util.Log;

import jp.co.ricoh.ssdk.sample.function.common.SmartSDKApplication;
import jp.co.ricoh.ssdk.sample.function.common.impl.AbstractEventReceiver;
import jp.co.ricoh.ssdk.sample.function.common.impl.AsyncConnectState;
import jp.co.ricoh.ssdk.sample.function.common.impl.AsyncEventListener;
import jp.co.ricoh.ssdk.sample.function.common.impl.ScanEventReceiver;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.HashScanServiceAttributeSet;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.ScanServiceAttributeSet;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.OccuredErrorLevel;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.ScannerRemainingMemory;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.ScannerState;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.ScannerStateReasons;
import jp.co.ricoh.ssdk.sample.wrapper.common.GenericJsonDecoder;
import jp.co.ricoh.ssdk.sample.wrapper.common.InvalidResponseException;
import jp.co.ricoh.ssdk.sample.wrapper.common.Request;
import jp.co.ricoh.ssdk.sample.wrapper.common.RequestHeader;
import jp.co.ricoh.ssdk.sample.wrapper.common.Response;
import jp.co.ricoh.ssdk.sample.wrapper.rws.service.scanner.Capability;
import jp.co.ricoh.ssdk.sample.wrapper.rws.service.scanner.GetCapabilityResponseBody;
import jp.co.ricoh.ssdk.sample.wrapper.rws.service.scanner.GetJobStatusResponseBody;
import jp.co.ricoh.ssdk.sample.wrapper.rws.service.scanner.GetScannerStatusResponseBody;
import jp.co.ricoh.ssdk.sample.wrapper.rws.service.scanner.Scanner;

import org.apache.http.HttpStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SDKServiceからの非同期イベントを受け取るリスナークラスです。
 * The listener class to receive asynchronous events from SDKService.
 */
public class FunctionMessageDispatcher implements AsyncEventListener {

	/* single instance */
	private static FunctionMessageDispatcher mInstance = null;

	private ServiceListener mServiceListener;
    private final Object mLockServiceListener = new Object();

	/**
	 *  ジョブに非同期を通知するためのリスナー
	 *  The listener to notify asynchronous to the job
	 */
	private final List<AsyncJobEventHandler> mAsyncEvHandlers = new ArrayList<AsyncJobEventHandler>();
    private String mSubscribedId = null;

    /**
     *  内部管理するスキャナクラス
     *  The scanner class managed internally
     */
    private Scanner mScanner = null;

    /**
     * 内部管理する非同期イベント受信クラス
     * The class to receive asynchronous events managed internally
     */
    private AbstractEventReceiver mScanAsyncEventReceiver;

	/*
	 * クラスロード時にインスタンスを生成
	 * Creates instance at the time of loading class
	 */
	static {
		mInstance = new FunctionMessageDispatcher();
	}

	/*
	 * デフォルトコンストラクタは不可視です。
	 * Default constructor is invisible
	 */
	private FunctionMessageDispatcher(){
	    // Registers an Internal listener to receive asynchronous events from SDKService
        mScanAsyncEventReceiver = new ScanEventReceiver();
        mScanAsyncEventReceiver.addAsyncEventListener(this);

        mScanner = new Scanner();
	}


	/**
	 * インスタンスを返します。
	 * Returns the instance.
	 *
	 * @return シングルトンインスタンス
	 *         Singleton instance
	 */
	public static FunctionMessageDispatcher getInstance(){
		return mInstance;
	}

	/**
	 * サービスリスナーを登録します。
     * 登録の結果、失敗する可能性があります。
     * その場合は、再登録することはせず、リスナー登録失敗としてUI層へ通知します。
     * Registers service listener
     * Registration may fail.
     * If failed, the listener is not registered again; registration failure is notified to the UI layer.
     *
	 * @param listener
     * @return 非同期接続状態の結果
     *         asynchronous connection state result
	 */
	public AsyncConnectState addServiceListener(ServiceListener listener){
       AsyncConnectState asyncConnectState = null;

        synchronized (this.mLockServiceListener) {
            if( this.mServiceListener == null ) {
                asyncConnectState = mScanAsyncEventReceiver.startReceiveAppStateEvent();
                if (asyncConnectState!=null && asyncConnectState.getState() == AsyncConnectState.STATE.CONNECTED) {
                    this.mServiceListener = listener;
                }
            } else {
                asyncConnectState = AsyncConnectState.valueOf(AsyncConnectState.STATE.CONNECTED, AsyncConnectState.ERROR_CODE.NO_ERROR);
            }
        }

        return asyncConnectState;
	}

    /**
     * サービスリスナーを解除します。
     * SDKServiceに問い合わせた結果、失敗する可能性があります。
     * Removes the service listener
     * Inquiry to SDKService may fail.
     *
     * @return 解除の可否
     *         Returns true if unregistration succeeded
     */
    public boolean removeServiceListener() {
        synchronized ( this.mLockServiceListener) {
            this.mServiceListener = null;
            AsyncConnectState async = mScanAsyncEventReceiver.endReceiveAppStateEvent();
            if (async==null) {
                return false;
            }
            return (async.getState()==AsyncConnectState.STATE.CONNECTED);
        }
    }

	/**
	 * ジョブの非同期イベントリスナーを登録します。
	 * Registers the asynchronous event listener of the job
     *
     * @param handler
     * @return subscribedId
     */
	public String addAsyncJobEventListener(AsyncJobEventHandler handler){
		if(handler == null) {
			throw new NullPointerException("handler is null");
		}

        synchronized (mAsyncEvHandlers) {
		    mAsyncEvHandlers.add(handler);

            if(mAsyncEvHandlers.size() == 1) {
                this.mSubscribedId = mScanAsyncEventReceiver.startReceiveJobEvent();
            }

            return this.mSubscribedId;
        }
	}

	/**
	 * ジョブの非同期イベントリスナー除去します。
	 * Removes the asynchronous event listener of the job
	 *
	 * @param handler
	 */
	public void removeAsyncJobEventListener(AsyncJobEventHandler handler){
		if(handler == null){
			throw new NullPointerException("handler is null");
		}
        synchronized (mAsyncEvHandlers) {
		    mAsyncEvHandlers.remove(handler);

            if(mAsyncEvHandlers.size() <= 0 && this.mSubscribedId != null) {
                this.mSubscribedId = mScanAsyncEventReceiver.endReceiveJobEvent();
            }
        }
	}


    /**
     * スキャンサービスの状態を取得し、属性セットとして値を返します。
     * Obtains the scan service state and returns the value as an attribute set.
     *
     * @return 現在のスキャンサービスの状態属性セット
     *         取得できない場合は空セットが返ります。
     *         The state attribute set of the current scan service.
     *         If the attribute set cannot be obtained, an empty set is returned.
     */
    public ScanServiceAttributeSet getScanStatus() {
        ScanServiceAttributeSet attributes = new HashScanServiceAttributeSet();

        RequestHeader header = new RequestHeader();
        header.put(RequestHeader.KEY_X_APPLICATION_ID, SmartSDKApplication.getProductId());

        Request req = new Request();
        req.setHeader(header);

        Response<GetScannerStatusResponseBody> resp;
        try {
            resp = mScanner.getScannerStatus(req);
            if( resp.getStatusCode() == HttpStatus.SC_OK ){
            	GetScannerStatusResponseBody body = resp.getBody();
            	if (body.getScannerStatus() != null) {
                    attributes.add(ScannerState.fromString(body.getScannerStatus()));
            	}
                if(body.getScannerStatusReasons() != null) {
                   	attributes.add(ScannerStateReasons.convertFrom(body.getScannerStatusReasons()));
                }
                if(body.getRemainingMemory() != null) {
                    attributes.add(new ScannerRemainingMemory(body.getRemainingMemory().intValue()));
                }
                if(body.getRemainingMemoryLocal() != null) {
                    // this version is not supported
                }
                if(body.getOccuredErrorLevel() != null) {
                    attributes.add(OccuredErrorLevel.fromString(body.getOccuredErrorLevel()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return attributes;
    }

    /**
     * 設定可能値一覧をWebAPIから取得します。
     * 取得に失敗した場合は、nullが返ります。
     * Obtains the list of values that can be set from web API.
     * If failing to obtain the list, null is returned.
     *
     * @return
     */
    public Capability getScanCapability() {
        RequestHeader header = new RequestHeader();
        header.put(RequestHeader.KEY_X_APPLICATION_ID, SmartSDKApplication.getProductId());

        Request req = new Request();
        req.setHeader(header);

        Response<GetCapabilityResponseBody> resp;
        try{
            resp = mScanner.getCapability(req);
            if(resp.getStatusCode() == HttpStatus.SC_OK) {
                return resp.getBody().getJobSettingCapability();
            }
        } catch( IOException ex ) {
            ex.printStackTrace();
        } catch( InvalidResponseException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 非同期イベントの接続状態を取得します
     * Obtains the state of asynchronous connection
     *
     * @return 非同期イベントの接続状態
     *         Asynchronous connection state
     */
    public AsyncConnectState getAsyncConnectState() {
        return mScanAsyncEventReceiver.getAsyncConnectState();
    }

    /***************************************************************************
	 *  SDKServiceからの通知
	 *  Notification from SDKService
	 **************************************************************************/
    /**
     * スキャナ状態イベントを受信します。
     * 状態の仕様ならびに、JSONフォーマット仕様はSmartSDK仕様に準拠します。
     * Receives scanner state event.
     * States and JSON format comply with SmartSDK specifications.
     *
     * @param eventData 受信したスキャナ状態を示すJSON文字列
     *                  The JSON string to indicate received scanner event
     */
    @Override
    public void onReceiveAppEvent(String eventData) {
        Log.d(FunctionMessageDispatcher.class.getSimpleName(), "onReceiveStatusEvent[" + eventData + "]");
        if( eventData == null ) return;
        Map<String, Object> decoded = GenericJsonDecoder.decodeToMap(eventData);
        GetScannerStatusResponseBody body = new GetScannerStatusResponseBody((Map<String, Object>) decoded.get("data"));

        ScanServiceAttributeSet notifySet = new HashScanServiceAttributeSet();
        if(body.getScannerStatus() != null) {
            notifySet.add(ScannerState.fromString(body.getScannerStatus()));
        }
        if(body.getScannerStatusReasons() != null) {
            notifySet.add(ScannerStateReasons.convertFrom(body.getScannerStatusReasons()));
        }
        if(body.getRemainingMemory() != null) {
            notifySet.add(new ScannerRemainingMemory(body.getRemainingMemory().intValue()));
        }
        if(body.getRemainingMemoryLocal() != null) {
            // this version is not supported
        }
        if(body.getOccuredErrorLevel() != null) {
            notifySet.add(OccuredErrorLevel.fromString(body.getOccuredErrorLevel()));
        }

        synchronized (this.mLockServiceListener) {
            if(this.mServiceListener != null) {
                this.mServiceListener.onChangeScanServiceAttributes(notifySet);
            }
        }
    }

    /**
     * スキャンジョブ状態イベントを受信します。
     * 状態の仕様ならびに、JSONフォーマット仕様はSmartSDK仕様に準拠します。
     * Receives scan job state event
     * States and JSON format comply with SmartSDK specifications.
     *
     * @param eventData 受信したスキャンジョブ状態を示すJSON文字列
     *                  The JSON string to indicate received scan job event
     */
    @Override
    public void onReceiveJobEvent(String eventData) {
        if(eventData == null) return;

        Map<String, Object> decoded = GenericJsonDecoder.decodeToMap(eventData);
        GetJobStatusResponseBody body = new GetJobStatusResponseBody((Map<String, Object>) decoded.get("data"));

        String jobId = body.getJobId();

        AsyncJobEventHandler[] handlers;
        synchronized (mAsyncEvHandlers) {
            handlers = mAsyncEvHandlers.toArray(new AsyncJobEventHandler[mAsyncEvHandlers.size()]);
        }

        for(AsyncJobEventHandler handler : handlers) {
            if( handler.getJobId() == null || handler.getJobId().equals(jobId)) {
                handler.onReceiveJobEvent(body);
            }
        }
    }

    @Override
    public void onReceiveFaxReceiveEvent(String eventData) {
    }
}
