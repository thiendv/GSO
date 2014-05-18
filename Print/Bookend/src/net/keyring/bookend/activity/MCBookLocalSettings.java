/**
 * Copyright (C) 2012 Morisawa Inc. All rights reserved.
 * Created by Morisawa Development Department, Tokyo.
 */
package net.keyring.bookend.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * コンテンツパス選択状態保存用クラス
 */
public class MCBookLocalSettings {
	/** Singleton Instance */
	private static MCBookLocalSettings mInstance = null;

	//SharedPreferences xmlファイル名 (null時はシステム自動付与)
	private static final String SHARED_PREFS_NAME = "MCBPrefsFile";

	private static final String SELECT_FILE_PATH = "SelectFilePath";

	/** 選択MCCファイルパス */
	private String mSelectFilePath = null;

	// SharedPreferencesインスタンス 設定値読み書き用
	private SharedPreferences mSharedPreferences = null;
	// Editorインスタンス 設定値読み書き用
	private Editor mEditor = null;

	/**
	 * getInstance() インスタンス取得.
	 * @param	context	Contextオブジェクト
	 */
	public static synchronized MCBookLocalSettings getInstance(Context context) {
		if(mInstance == null){
			mInstance = new MCBookLocalSettings(context);
		}
		return mInstance;
	}

	/**
	 * LocalSettings() コンストラクタ.
	 * @param	context	Contextオブジェクト
	 */
	public MCBookLocalSettings(Context context) {
		// preference.xmlの呼び出し (xmlが存在しない場合は初期値を取得)
		mSharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		mEditor = mSharedPreferences.edit();

		// 設定値の取得（取得できない場合はデフォルト値）
		mSelectFilePath = mSharedPreferences.getString(SELECT_FILE_PATH, null);

		// mEditor.commitはメソッド内でコールされるため不要。
	}

	/**
	 * 選択MCCファイルパスを取得
	 * @return 選択MCCファイルパス
	 */
	public String getSelectFilePath() {
		return mSelectFilePath;
	}

	/**
	 * 選択MCCファイルパスを設定
	 * @param 選択MCCファイルパス
	 */
	public void setSelectFilePath(String selectFilePath) {
		mSelectFilePath = selectFilePath;
		mEditor.putString(SELECT_FILE_PATH, mSelectFilePath);
		mEditor.commit();
	}
}
