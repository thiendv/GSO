/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.function.scan.impl.service;


import jp.co.ricoh.ssdk.sample.wrapper.rws.service.scanner.GetJobStatusResponseBody;

/**
 * ジョブごとの非同期通信用リスナーです。
 * 通知を受けてジョブごとのステートマシンへイベントを発行する役割を持ちます。
 * Listener for asynchronous communication by job
 * Issues events to the state machine for each job after receiving notification
 */
public interface AsyncJobEventHandler {

	/**
	 * 非同期通信の受信部分です。
	 * Receive part of the asynchronous communication
	 * @param event 非同期通信の1つずつのデータ(ジョブイベント)
	 *              one job event in asynchronous communication
	 */
	public abstract void onReceiveJobEvent(GetJobStatusResponseBody event);

	public abstract String getJobId();

}
