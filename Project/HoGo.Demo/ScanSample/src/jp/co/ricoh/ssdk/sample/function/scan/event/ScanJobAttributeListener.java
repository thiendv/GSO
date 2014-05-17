/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.function.scan.event;

/**
 * ファクスジョブ属性変化監視リスナーのインターフェースです。
 * The listener interface to monitor fax job attribute changes
 */
public interface ScanJobAttributeListener {

	public abstract void updateAttributes(ScanJobAttributeEvent attributesEvent);

}
