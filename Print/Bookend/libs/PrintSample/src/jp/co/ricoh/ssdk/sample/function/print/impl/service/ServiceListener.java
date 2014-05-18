/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.function.print.impl.service;

import jp.co.ricoh.ssdk.sample.function.print.attribute.PrintServiceAttributeSet;

/**
 * SDKServiceからの非同期サービスイベントを通知するリスナーインターフェイスです。
 * The listener interface to notify the asynchronous service events from SDKService.
 */
public interface ServiceListener {

    void onChangePrintServiceAttributes(PrintServiceAttributeSet attributes);

}
