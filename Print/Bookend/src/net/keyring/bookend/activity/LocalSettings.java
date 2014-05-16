
/**
 * Copyright (C) 2012 Morisawa Inc. All rights reserved.
 * Created by Morisawa Development Department, Tokyo.
 */

package net.keyring.bookend.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class LocalSettings {
	// デバッグログ 出力フラグ true:出力あり, false:出力なし
	private static final boolean DEBUG = false;
	// デバッグログ 出力タグ名
	private static final String TAG = "LocalSettings";


	/** Singleton Instance */
	private static LocalSettings mInstance = null;

	//SharedPreferences xmlファイル名 (null時はシステム自動付与)
	private static final String SHARED_PREFS_NAME = "MCComicPrefsFile";

	protected static final String CONTENT_ID = "ContentID";
	protected static final String DOCUMENTROOT_URL = "DocumentRootURL";
	protected static final String DOCUMENTROOT_PORT_NO = "DocumentRootPortNo";
	protected static final String DOCUMENTROOT_ACCOUNT = "DocumentRootAccount";
	protected static final String DOCUMENTROOT_PASSWORD = "DocumentRootPassword";

	private static final String USER_ID = "UserID";
	private static final String SELECT_FILE_PATH = "SelectFilePath";
	private static final String SEARCH_SERVICE = "SerachService";
	private static final String LOADING_TRIAL = "LoadingTrial";


// アプリケーション永続情報
	/** uid */
	private String mUserID = null;
	/** 選択MCMファイルパス */
	private String mSelectFilePath = null;
	/** 検索サービス */
	private int mSearchService = 0;
	/** 立ち読み版で開くかどうか */
	private boolean mLoadingTrial = false;

	// SharedPreferencesインスタンス 設定値読み書き用
	private SharedPreferences mSharedPreferences = null;
	// Editorインスタンス 設定値読み書き用
	private Editor mEditor = null;

	/**
	 * getInstance() インスタンス取得.
	 * @param	context	Contextオブジェクト
	 */
	public static synchronized LocalSettings getInstance(Context context) {
		if(mInstance == null){
			mInstance = new LocalSettings(context);
		}
		return mInstance;
	}

	/**
	 * LocalSettings() コンストラクタ.
	 * @param	context	Contextオブジェクト
	 */
	public LocalSettings(Context context) {
		if (DEBUG) Log.d(TAG, "[LocalSettings]<I>");

		// preference.xmlの呼び出し (xmlが存在しない場合は初期値を取得)
		mSharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		mEditor = mSharedPreferences.edit();

		// 設定値の取得（取得できない場合はデフォルト値）
		mUserID = mSharedPreferences.getString(USER_ID, null);
		mSelectFilePath = mSharedPreferences.getString(SELECT_FILE_PATH, null);
		mSearchService = mSharedPreferences.getInt(SEARCH_SERVICE, 0);
		mLoadingTrial = mSharedPreferences.getBoolean(LOADING_TRIAL, false);

		// mEditor.commitはメソッド内でコールされるため不要。
		if (DEBUG) Log.d(TAG, "[LocalSettings]<O>");
	}


	/**
	 * uidを取得
	 * @return uid
	 */
	public String getUserID() {
		if (DEBUG) Log.d(TAG, "[getUserID]"+":mUserID = "+mUserID);
		return mUserID;
	}
	/**
	 * uidを設定
	 * @param uid
	 */
	public void setUserID(String userID) {
		mUserID = userID;
		mEditor.putString(USER_ID, mUserID);
		mEditor.commit();
		if (DEBUG) Log.d(TAG, "[setUserID]"+":mUserID = "+userID);
	}

	/**
	 * 選択MCMファイルパスを取得
	 * @return 選択MCMファイルパス
	 */
	public String getSelectFilePath() {
		if (DEBUG) Log.d(TAG, "[getSelectFilePath]"+":mSelectFilePath = "+mSelectFilePath);
		return mSelectFilePath;
	}
	/**
	 * 選択MCMファイルパスを設定
	 * @param 選択MCMファイルパス
	 */
	public void setSelectFilePath(String selectFilePath) {
		mSelectFilePath = selectFilePath;
		mEditor.putString(SELECT_FILE_PATH, mSelectFilePath);
		mEditor.commit();
		if (DEBUG) Log.d(TAG, "[setSelectFilePath]"+":mSelectFilePath = "+selectFilePath);
	}

	/**
	 * 検索サービスを取得
	 * @return 検索サービス
	 */
	public int getSearchService() {
		if (DEBUG) Log.d(TAG, "[getSearchService]"+":mSearchService = "+mSearchService);
		return mSearchService;
	}
	/**
	 * 検索サービスを設定
	 * @param 検索サービス
	 */
	public void setSearchWebService(int searchService) {
		mSearchService = searchService;
		mEditor.putInt(SEARCH_SERVICE, mSearchService);
		mEditor.commit();
		if (DEBUG) Log.d(TAG, "[setSearchWebService]"+":mSearchService = "+searchService);
	}

	/**
	 * 立ち読み版で開くかどうかを取得
	 * @return true: 立ち読み版, false: 正規版
	 */
	public boolean isLoadingTrial() {
		if (DEBUG) Log.d(TAG, "[isLoadingTrial] mLoadingTrial="+mLoadingTrial);
		return mLoadingTrial;
	}
	/**
	 * 立ち読み版で開くかどうかを設定
	 * @param true: 立ち読み版, false: 正規版
	 */
	public void setLoadingTrial(boolean loadingTrial) {
		mLoadingTrial = loadingTrial;
		mEditor.putBoolean(LOADING_TRIAL, mLoadingTrial);
		mEditor.commit();
		if (DEBUG) Log.d(TAG, "[setLoadingTrial] mLoadingTrial="+mLoadingTrial);
	}

}

