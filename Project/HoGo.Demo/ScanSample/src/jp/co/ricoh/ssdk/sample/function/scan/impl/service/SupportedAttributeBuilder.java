/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.function.scan.impl.service;

import jp.co.ricoh.ssdk.sample.function.scan.attribute.ScanRequestAttribute;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.AutoCorrectJobSetting;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.AutoDensity;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.DestinationSetting;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.EmailSetting;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.FileSetting;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.JobMode;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.Magnification;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.MagnificationSize;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.ManualDensity;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.Ocr;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.OcrSetting;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.OriginalOrientation;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.OriginalOutputExit;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.OriginalPreview;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.OriginalSide;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.OriginalSize;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.OriginalSizeCustomX;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.OriginalSizeCustomY;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.PdfSetting;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.ScanColor;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.ScanDevice;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.ScanMethod;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.ScanResolution;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.SecuredPdfSetting;
import jp.co.ricoh.ssdk.sample.function.scan.supported.DestinationSettingSupported;
import jp.co.ricoh.ssdk.sample.function.scan.supported.EmailSettingSupported;
import jp.co.ricoh.ssdk.sample.function.scan.supported.FileSettingSupported;
import jp.co.ricoh.ssdk.sample.function.scan.supported.MagnificationSizeSupported;
import jp.co.ricoh.ssdk.sample.function.scan.supported.MagnificationSupported;
import jp.co.ricoh.ssdk.sample.function.scan.supported.OcrSettingSupported;
import jp.co.ricoh.ssdk.sample.function.scan.supported.PdfSettingSupported;
import jp.co.ricoh.ssdk.sample.function.scan.supported.SecuredPdfSettingSupported;
import jp.co.ricoh.ssdk.sample.wrapper.rws.service.scanner.Capability;

import java.util.HashMap;
import java.util.Map;

/**
 * 指定したCapabilityオブジェクトから、SupportedAttributeを生成するためのクラスです。
 * The class to create supportedAttribute from specified capability object.
 */
public class SupportedAttributeBuilder {

	private SupportedAttributeBuilder(){
	}

	public static Map<Class<? extends ScanRequestAttribute>,Object> getSupportedAttribute(Capability cap) {

		Map<Class<? extends ScanRequestAttribute>, Object> retList = new HashMap<Class<? extends ScanRequestAttribute>, Object>();

        if( cap == null ) {
            return null;
        }

		/*
		 * Capability#getXXXを取得し、値が格納されている場合のみCapabilityとして登録します。
		 * Obtains Capability#getXXX and registers as capability only if value is stored.
		 */

		if( cap.getAutoCorrectJobSettingList() != null ) {
			retList.put(AutoCorrectJobSetting.class, AutoCorrectJobSetting.getSupportedValue(cap.getAutoCorrectJobSettingList()));
		}
		if ( cap.getJobModeList() != null ) {
			retList.put(JobMode.class, JobMode.getSupportedValue(cap.getJobModeList()));
		}
		if ( cap.getOriginalSizeList() != null ) {
			retList.put(OriginalSize.class, OriginalSize.getSupportedValue(cap.getOriginalSizeList()));
		}
		if( cap.getOriginalSizeCustomXRange() != null ) {
			retList.put(OriginalSizeCustomX.class, OriginalSizeCustomX.getSupportedValue(cap.getOriginalSizeCustomXRange()));
		}
		if( cap.getOriginalSizeCustomYRange() != null ) {
			retList.put(OriginalSizeCustomY.class, OriginalSizeCustomY.getSupportedValue(cap.getOriginalSizeCustomYRange()));
		}
		if( cap.getScanDeviceList() != null ) {
			retList.put(ScanDevice.class, ScanDevice.getSupportedValue(cap.getScanDeviceList()));
		}
		if( cap.getScanMethodList() != null ) {
			retList.put(ScanMethod.class, ScanMethod.getSupportedValue(cap.getScanMethodList()));
		}
		if( cap.getOriginalOutputExitList() != null ) {
			retList.put(OriginalOutputExit.class, OriginalOutputExit.getSupportedValue(cap.getOriginalOutputExitList()));
		}
		if( cap.getOriginalSideList() != null ) {
			retList.put(OriginalSide.class, OriginalSide.getSupportedValue(cap.getOriginalSideList()));
		}
		if( cap.getOriginalOrientationList() != null) {
			retList.put(OriginalOrientation.class, OriginalOrientation.getSupportedValue(cap.getOriginalOrientationList()));
		}
		if( cap.getOriginalPreviewList() != null ) {
			retList.put(OriginalPreview.class, OriginalPreview.getSupportedValue(cap.getOriginalPreviewList()));
		}
		if( cap.getScanColorList() != null ) {
			retList.put(ScanColor.class, ScanColor.getSupportedValue(cap.getScanColorList()));
		}
		if( cap.getMagnificationRange() != null ) {
			retList.put(Magnification.class, new MagnificationSupported(cap.getMagnificationRange()));
		}
		if( cap.getMagnificationSize() != null ) {
			retList.put(MagnificationSize.class, new MagnificationSizeSupported(cap.getMagnificationSize()));
		}
		if( cap.getScanResolutionList() != null ) {
            retList.put(ScanResolution.class, ScanResolution.getSupportedValue(cap.getScanResolutionList()));
		}
		if ( cap.getAutoDensityList() != null ) {
			retList.put(AutoDensity.class, AutoDensity.getSupportedValue(cap.getAutoDensityList()));
		}
		if( cap.getManualDensityRange() != null ) {
            retList.put(ManualDensity.class,ManualDensity.getSupportedValue(cap.getManualDensityRange()));
		}
		if( cap.getFileSettingCapability() != null ) {
			retList.put(FileSetting.class, new FileSettingSupported(cap.getFileSettingCapability()));
		}
		if( cap.getPdfSettingCapability() != null ) {
            retList.put(PdfSetting.class, new PdfSettingSupported(cap.getPdfSettingCapability()));
		}
		if( cap.getOcrList() != null ) {
            retList.put(Ocr.class, Ocr.getSupportedValue(cap.getOcrList()));
		}
		if( cap.getOcrSettingCapability() != null ) {
            retList.put(OcrSetting.class, new OcrSettingSupported(cap.getOcrSettingCapability()));
		}
		if( cap.getSecuredPdfSettingCapability() != null ) {
            retList.put(SecuredPdfSetting.class, new SecuredPdfSettingSupported(cap.getSecuredPdfSettingCapability()));
		}
		if( cap.getEmailSettingCapability() != null ) {
			retList.put(EmailSetting.class, new EmailSettingSupported(cap.getEmailSettingCapability()));
		}
		if( cap.getDestinationSettingCapability() != null ) {
			retList.put(DestinationSetting.class, new DestinationSettingSupported(cap.getDestinationSettingCapability(), cap.getMaxBroadcastNumberCapability()));
		}

		return retList;
	}
}
