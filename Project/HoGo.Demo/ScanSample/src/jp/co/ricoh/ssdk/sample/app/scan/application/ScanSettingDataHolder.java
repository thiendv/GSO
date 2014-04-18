/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.app.scan.application;

import jp.co.ricoh.ssdk.sample.app.scan.R;
import jp.co.ricoh.ssdk.sample.function.scan.ScanService;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.FileSetting;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.FileSetting.FileFormat;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.OriginalPreview;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.OriginalSide;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.ScanColor;
import jp.co.ricoh.ssdk.sample.function.scan.supported.FileSettingSupported;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * スキャン設定情報クラスです。
 * Scan setting data class.
 */
public class ScanSettingDataHolder {
	public static String TAG = ScanSettingDataHolder.class.getSimpleName();

    /**
     * 読取カラーの表示文字列IDと設定値のマップです。
     * Map of scan color display string ID and setting values.
     */
	private final LinkedHashMap<Integer, ScanColor> mAllColorMap;

    /**
     * ファイル形式の表示文字列IDと設定値のマップです。
     * Map of file format display string ID and setting values.
     */
	private final LinkedHashMap<Integer, FileFormat> mAllFileFormatMap;

    /**
     * マルチページ設定の表示文字列IDと設定値のマップです。
     * Map of multipage setting display string ID and setting values.
     */
	private final LinkedHashMap<Integer, Boolean> mAllMultiPageMap;

    /**
     * 原稿面の表示文字列IDと設定値のマップです。
     * Map of scan side display string ID and setting values.
     */
	private final LinkedHashMap<Integer, OriginalSide> mAllSideMap;

    /**
     * プレビュー表示設定の表示文字列IDと設定値のマップです。
     * Map of preview setting display string ID and setting values.
     */
	private final LinkedHashMap<Integer, OriginalPreview> mAllPreviewMap;



    /**
     * 読取カラー設定可能値の表示文字列IDのリストです。
     * List of display string ID for the available scan color setting values.
     */
	private List<Integer> mSupportedColorLabelList;

    /**
     * ファイル設定の設定可能値の表示文字列IDのリストです。
     * List of display string ID for the available file setting values.
     */
	private List<Integer> mSupportedFileSettingLabelList;

    /**
     *原稿面設定可能値の表示文字列IDのリストです。
     * List of display string ID for the available scan side setting values.
     */
	private List<Integer> mSupportedSideLabelList;

    /**
     * プレビュー表示設定可能値の表示文字列IDのリストです。
     * List of display string ID for the available preview setting values.
     */
	private List<Integer> mSupportedPreviewLabelList;



    /**
     * 選択中の読取カラー設定値の表示文字列IDです。
     * Display string ID of the selected scan color setting value.
     */
	private int mSelectedColorLabel;

    /**
     * 選択中のファイル設定値の表示文字列IDです。
     * Display string ID of the selected file setting value.
     */
	private int mSelectedFileSettingLabel;

    /**
     * 選択中の原稿面設定値の表示文字列IDです。
     * Display string ID of the selected scan side setting value.
     */
	private int mSelectedSideLabel;

    /**
     * 選択中のプレビュー表示設定値の表示文字列IDです。
     * Display string ID of the selected preview setting value.
     */
	private int mSelectedPreviewLabel;

    /**
     * コンストラクタです。
     * 各マップの初期化を行います。
     * [処理内容]
     *   (1)読取カラー設定のマップの初期化
     *   (2)ファイル形式設定のマップの初期化
     *   (3)マルチページ設定のマップの初期化
     *   (4)原稿面設定のマップの初期化
     *   (5)プレビュー表示設定のマップの初期化
     *   (6)各設定値の文字列を初期化します。
     *
     *   Constructor.
     *   Initializes maps.
     * [Processes]
     *   (1) Initializes the map for scan color setting
     *   (2) Initializez the map for file setting
     *   (3) Initializez the map for multipage setting
     *   (4) Initializez the map for scan side setting
     *   (5) Initializez the map for preview setting
     *   (6) Initializes the display string of the setting values.
     */
	public ScanSettingDataHolder() {

		//(1)
		mAllColorMap = new LinkedHashMap<Integer, ScanColor>(){
			{
				put(R.string.txid_scan_b_top_mono_text,                 ScanColor.MONOCHROME_TEXT);
				put(R.string.txid_scan_b_top_mono_text_photo,           ScanColor.MONOCHROME_TEXT_PHOTO);
				put(R.string.txid_scan_b_top_mono_text_lineart,         ScanColor.MONOCHROME_TEXT_LINEART);
				put(R.string.txid_scan_b_top_mono_photo,				ScanColor.MONOCHROME_PHOTO);
				put(R.string.txid_scan_b_top_gray_scale,				ScanColor.GRAYSCALE);
				put(R.string.txid_scan_b_top_full_color_text_photo,	    ScanColor.COLOR_TEXT_PHOTO);
				put(R.string.txid_scan_b_top_full_color_glossy_photo,	ScanColor.COLOR_GLOSSY_PHOTO);
				put(R.string.txid_scan_b_top_auto_color_select,         ScanColor.AUTO_COLOR);
			}
		};

		//(2)
		mAllFileFormatMap = new LinkedHashMap<Integer, FileFormat>(){
			{
				put(R.string.txid_scan_b_top_file_stiff_jpg, 	FileFormat.TIFF_JPEG);
				put(R.string.txid_scan_b_top_file_mtiff, 		FileFormat.TIFF_JPEG);
				put(R.string.txid_scan_b_top_file_spdf, 		FileFormat.PDF);
				put(R.string.txid_scan_b_top_file_mpdf, 		FileFormat.PDF);
			}
		};

		//(3)
		mAllMultiPageMap = new LinkedHashMap<Integer, Boolean>(){
			{
				put(R.string.txid_scan_b_top_file_stiff_jpg, 	false);
				put(R.string.txid_scan_b_top_file_mtiff, 		true);
				put(R.string.txid_scan_b_top_file_spdf, 		false);
				put(R.string.txid_scan_b_top_file_mpdf, 		true);
			}
		};

		//(4)
		mAllSideMap = new LinkedHashMap<Integer, OriginalSide>(){
			{
				put(R.string.txid_scan_b_top_one_sided, 		OriginalSide.ONE_SIDE);
				put(R.string.txid_scan_b_top_top_to_top,		OriginalSide.TOP_TO_TOP);
				put(R.string.txid_scan_b_top_top_to_bottom,	OriginalSide.TOP_TO_BOTTOM);
				put(R.string.txid_scan_b_top_spread,			OriginalSide.SPREAD);
			}
		};

		//(5)
		mAllPreviewMap = new LinkedHashMap<Integer, OriginalPreview>() {
		    {
		        put(R.string.txid_scan_b_other_preview_on,     OriginalPreview.ON);
                put(R.string.txid_scan_b_other_preview_off,    OriginalPreview.OFF);
		    }
		};

		//(6)
		mSelectedColorLabel = R.string.txid_scan_b_top_mono_text;
		mSelectedFileSettingLabel = R.string.txid_scan_b_top_file_mpdf;
		mSelectedSideLabel = R.string.txid_scan_b_top_one_sided;
		mSelectedPreviewLabel = R.string.txid_scan_b_other_preview_on;

		mSupportedColorLabelList = new ArrayList<Integer>();
		mSupportedFileSettingLabelList = new ArrayList<Integer>();
		mSupportedSideLabelList = new ArrayList<Integer>();
		mSupportedPreviewLabelList = new ArrayList<Integer>();
	}

    /**
     * ScanServiceから各設定の設定可能値一覧を取得します。
     * [処理内容]
     *   (1)読取カラー設定可能値を取得する
     *   (2)ファイル形式設定可能値とマルチページ設定可能値を取得する
     *   (3)原稿面設定可能値を取得する
     *   (4)プレビュー設定可能値を取得する
     *
     * Obtains the list of available setting values from ScanService.
     * [Processes]
     *   (1) Obtains the available setting values for scan color setting.
     *   (1) Obtains the available setting values for file setting and multipage setting.
     *   (1) Obtains the available setting values for scan side setting.
     *   (1) Obtains the available setting values for preview setting.
     */
	public void init(ScanService scanService) {

        //(1)
        @SuppressWarnings("unchecked")
        List<ScanColor> colorList = (List<ScanColor>)scanService.getSupportedAttributeValues(ScanColor.class);
        setSupportedColorList(colorList);

        //(2)
        FileSettingSupported fileSettingSupported = (FileSettingSupported)scanService.getSupportedAttributeValues(FileSetting.class);
        if (fileSettingSupported != null) {
            List<FileFormat> fileFormatList = fileSettingSupported.getFileFormatList();
            List<Boolean> multipageFormatList = fileSettingSupported.getMultipageFormat();
            setSupportedFileSettingList(fileFormatList, multipageFormatList);
        }

        //(3)
        @SuppressWarnings("unchecked")
        List<OriginalSide> originalSideList = (List<OriginalSide>)scanService.getSupportedAttributeValues(OriginalSide.class);
        setSupportedSideList(originalSideList);

        //(4)
        @SuppressWarnings("unchecked")
        List<OriginalPreview> originalPreviewList = (List<OriginalPreview>)scanService.getSupportedAttributeValues(OriginalPreview.class);
        setSupportedPreviewList(originalPreviewList);

	}


    /**
     * 読取カラー設定可能値の表示文字列IDリストを作成します。
     * Creates the list of display string ID for the available scan color setting values.
     *
     * @param colorList 読取カラー設定可能値のリスト
     *                  List of available scan color setting values
     */
	private void setSupportedColorList(List<ScanColor> colorList) {
	    mSupportedColorLabelList.clear();
	    if (colorList != null) {
	        Set<Map.Entry<Integer, ScanColor>> entrySet = mAllColorMap.entrySet();
	        Iterator<Map.Entry<Integer, ScanColor>> it = entrySet.iterator();
	        while(it.hasNext())
	        {
	            Map.Entry<Integer, ScanColor> entry = it.next();
	            if(colorList.contains(entry.getValue())) {
	                mSupportedColorLabelList.add(entry.getKey());
	            }
	        }
	    }
	}

    /**
     * ファイル設定可能値の表示文字列IDリストを作成します。
     * Creates the list of display string ID for the available file setting values.
     *
     * @param fileFormatList ファイル形式設定可能値のリスト
     *                  List of available scan file format setting values
     * @param multiPageFormatList マルチページ設定可能値のリスト
     *                  List of available scan multipage setting values
     */
	private void setSupportedFileSettingList(List<FileFormat> fileFormatList, List<Boolean> multiPageFormatList) {
	    mSupportedFileSettingLabelList.clear();
	    mSupportedFileSettingLabelList.clear();
	    if (fileFormatList != null) {
	        Set<Map.Entry<Integer, FileFormat>> entrySet1 = mAllFileFormatMap.entrySet();
	        Iterator<Map.Entry<Integer, FileFormat>> it1 = entrySet1.iterator();
	        while(it1.hasNext())
	        {
	            Map.Entry<Integer, FileFormat> entry = it1.next();
	            if(fileFormatList.contains(entry.getValue())) {
	                mSupportedFileSettingLabelList.add(entry.getKey());
	            }
	        }
	    }
	    if (multiPageFormatList != null) {
	        Set<Map.Entry<Integer, Boolean>> entrySet2 = mAllMultiPageMap.entrySet();
	        Iterator<Map.Entry<Integer, Boolean>> it2 = entrySet2.iterator();
	        while(it2.hasNext())
	        {
	            Map.Entry<Integer, Boolean> entry = it2.next();
	            if(!multiPageFormatList.contains(entry.getValue())) {
	                mSupportedFileSettingLabelList.remove(entry.getKey());
	            }
	        }
	    }
	}

    /**
     * 原稿面設定可能値の表示文字列IDリストを作成します。
     * Creates the list of display string ID for the available scan side setting values.
     *
     * @param sideList 原稿面設定可能値のリスト
     *                  List of available scan side setting values
     */
	private void setSupportedSideList(List<OriginalSide> sideList) {
	    mSupportedSideLabelList.clear();
	    if (sideList != null) {
	        Set<Map.Entry<Integer, OriginalSide>> entrySet = mAllSideMap.entrySet();
	        Iterator<Map.Entry<Integer, OriginalSide>> it = entrySet.iterator();
	        while(it.hasNext())
	        {
	            Map.Entry<Integer, OriginalSide> entry = it.next();
	            if(sideList.contains(entry.getValue())) {
	                mSupportedSideLabelList.add(entry.getKey());
	            }
	        }
	    }
	}

    /**
     * プレビュー設定可能値の表示文字列IDリストを作成します。
     * Creates the list of display string ID for the available preview setting values.
     *
     * @param previewList プレビュー設定可能値のリスト
     *                  List of available preview setting values
     */
    private void setSupportedPreviewList(List<OriginalPreview> previewList) {
        mSupportedPreviewLabelList.clear();
        if (previewList != null) {
            Set<Map.Entry<Integer, OriginalPreview>> entrySet = mAllPreviewMap.entrySet();
            Iterator<Map.Entry<Integer, OriginalPreview>> it = entrySet.iterator();
            while(it.hasNext())
            {
                Map.Entry<Integer, OriginalPreview> entry = it.next();
                if(previewList.contains(entry.getValue())) {
                    mSupportedPreviewLabelList.add(entry.getKey());
                }
            }
        }
    }


    /**
     * 選択中の読取カラー設定値の表示文字列IDを取得します。
     * Obtains the display string ID of the selected scan color setting value.
     */
	public Integer getSelectedColorLabel() {
		return mSelectedColorLabel;
	}

    /**
     * 選択中の読取カラー設定値を取得します。
     * Obtains the selected scan color setting value.
     */
	public ScanColor getSelectedColorValue() {
		return mAllColorMap.get(mSelectedColorLabel);
	}

    /**
     * 指定された読取カラー設定値を選択状態にします。
     * Changes the selection state of the specified scan color setting value to "selected."
     * @param id
     */
	public void setSelectedColor(Integer id) {
		if(mSupportedColorLabelList.contains(id)) {
			mSelectedColorLabel = id;
		}
	}


    /**
     * 選択中のファイル設定値の表示文字列IDを取得します。
     * Obtains the display string ID of the selected scan color setting value.
     */
	public Integer getSelectedFileSettingLabel() {
		return mSelectedFileSettingLabel;
	}

    /**
     * 選択中のファイル形式設定値を取得します。
     * Obtains the selected scan color setting value.
     */
	public FileFormat getSelectedFileFormatValue() {
		return mAllFileFormatMap.get(mSelectedFileSettingLabel);
	}

    /**
     * 選択中のマルチページ形式設定値を取得します。
     * Obtains the selected multipage setting value.
     */
	public Boolean getSelectedMultiPageValue() {
		return mAllMultiPageMap.get(mSelectedFileSettingLabel);
	}

    /**
     * 指定されたファイル設定値を選択状態にします。
     * Changes the selection state of the specified file setting value to "selected."
     * @param id
     */
	public void setSelectedFileSetting(Integer id) {
		if(mSupportedFileSettingLabelList.contains(id)) {
			mSelectedFileSettingLabel = id;
		}
	}


    /**
     * 選択中の原稿面設定値の表示文字列IDを取得します。
     * Obtains the display string ID of the selected scan side setting value.
     */
	public Integer getSelectedSideLabel() {
		return mSelectedSideLabel;
	}

    /**
     * 選択中の原稿面設定値を取得します。
     * Obtains the selected scan side setting value.
     */
	public OriginalSide getSelectedSideValue() {
		return mAllSideMap.get(mSelectedSideLabel);
	}

    /**
     * 指定された原稿面設定値を選択状態にします。
     * Changes the selection state of the specified scan side setting value to "selected."
     * @param id
     */
	public void setSelectedSide(Integer id) {
		if(mSupportedSideLabelList.contains(id)) {
			mSelectedSideLabel = id;
		}
	}


    /**
     * 選択中のプレビュー表示設定値の表示文字列IDを取得します。
     * Obtains the display string ID of the selected preview setting value.
     */
    public Integer getSelectedPreviewLabel() {
        return mSelectedPreviewLabel;
    }

    /**
     * 選択中のプレビュー表示設定値を取得します。
     * Obtains the selected preview setting value.
     */
    public OriginalPreview getSelectedPreviewValue() {
        return mAllPreviewMap.get(mSelectedPreviewLabel);
    }

    /**
     * 指定されたプレビュー表示設定値を選択状態にします。
     * Changes the selection state of the specified preview setting value to "selected."
     * @param id
     */
    public void setSelectedPreview(Integer id) {
        if(mSupportedPreviewLabelList.contains(id)) {
            mSelectedPreviewLabel = id;
        }
    }


    /**
     * 読取カラー設定可能値のリストを取得します。
     * Display string ID of the scan color setting value to select
     */
	public List<Integer> getColorLabelList() {
		return mSupportedColorLabelList;
	}

    /**
     * ファイル設定可能値のリストを取得します。
     * Display string ID of the file setting value to select
     */
	public List<Integer> getFileSettingLabelList() {
		return mSupportedFileSettingLabelList;
	}

    /**
     * 原稿面設定可能値のリストを取得します。
     * Display string ID of the scan side setting value to select
     */
	public List<Integer> getSideLabelList() {
		return mSupportedSideLabelList;
	}

    /**
     * プレビュー設定可能値のリストを取得します。
     * Display string ID of the preview setting value to select
     */
    public List<Integer> getPreviewLabelList() {
        return mSupportedPreviewLabelList;
    }

}
