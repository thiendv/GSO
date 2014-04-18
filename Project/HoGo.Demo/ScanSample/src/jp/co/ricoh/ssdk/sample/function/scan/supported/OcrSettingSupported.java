/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.function.scan.supported;

import java.util.List;

import jp.co.ricoh.ssdk.sample.function.common.Conversions;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.OcrSetting;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.OcrSetting.BlankPageSensitivityLevel;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.OcrSetting.OcrLanguage;
import jp.co.ricoh.ssdk.sample.wrapper.rws.service.scanner.Capability;

public final class OcrSettingSupported {

    private final List<OcrSetting.OcrLanguage> supportedOcrLanguages;
    private final List<Boolean> supportedOmitBlankPages;
    private final List<OcrSetting.BlankPageSensitivityLevel> supportedBlankPageSensitivityLevels;
    private final List<Boolean> supportedAutoFileNames;

    public OcrSettingSupported(Capability.OcrSettingCapability capability) {
    	supportedOcrLanguages = OcrLanguage.getSupportedValue(capability.getOcrLanguageList());
        supportedOmitBlankPages = Conversions.getList(capability.getOmitBlankPageList());
        supportedBlankPageSensitivityLevels = BlankPageSensitivityLevel.getSupportedValue(capability.getBlankPageSensitivityLevelList());
        supportedAutoFileNames = Conversions.getList(capability.getAutoFileNameList());
    }

    public List<OcrSetting.OcrLanguage> getSupportedOcrLanguages() {
        return supportedOcrLanguages;
    }

    public List<Boolean> getSupportedOmitBlankPages() {
        return supportedOmitBlankPages;
    }

    public List<OcrSetting.BlankPageSensitivityLevel> getSupportedBlankPageSensitivityLevels() {
        return supportedBlankPageSensitivityLevels;
    }

    public List<Boolean> getSupportedAutoFileNames() {
        return supportedAutoFileNames;
    }

}
