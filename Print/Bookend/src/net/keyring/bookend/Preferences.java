package net.keyring.bookend;

import java.util.ArrayList;
import java.util.Map;

import net.keyring.bookend.constant.ConstDB;
import net.keyring.bookend.constant.ConstList;
import net.keyring.bookend.constant.ConstPref;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Preferences登録・static変数保持
 * @author Hamaji
 *
 */

public class Preferences implements ConstPref, ConstList{
	// --------- 変数定義 ------------
	/** SharedPrefernce の取得 */
	public static SharedPreferences pref;

	// ----------- static変数定義 ----------------
	/** Bookend専用外部ディレクトリ */
	public static String sExternalStrage;
	/** 必ず行うべき設定チェックを実行したかどうかのフラグ */
	public static boolean sDefaultCheck = false;
	/** ConnectivityManagerがtrueを返したけどCheckActivationリクエストがエラーになった場合にセットされるフラグ
	 * 	Utils.isConnected() で使用される */
	public static boolean sOffline = false;
	
	/** 不正にキャプチャが撮られた場合はtrueに */
	public static boolean sDeleteCapture = false;
	/** 実行環境モード(true=stg) ●static変数でのみ情報保持 */
	public static boolean sMode;
	/** ストアリスト　●static変数でのみ保持 */
	public static ArrayList<Map<String,String>> sStoreList;
	/** InAppBilling public key に問題がないかチェックしたかどうかフラグ 　●static変数でのみ保持 */
	public static boolean isInAppBillingPublicKeyCheck = false;
	
	/** ユーザーID */
	public static String sUserID;
	/** リストソート方法 - メイン画面 */
	public static int sSort_main;
	/** リストソート方法 - Web書庫画面(初期表示は取得日時(降順)) */
	public static int sSort_web = ConstDB.DESCEND_DATE;
	/** Web書庫リストフィルター設定 */
	public static int sListFilter = VIEW_ALL_CONTENTS;
	/** メールアドレス */
	public static String sMailAddress;
	/** GetContents 前回のリクエストのレスポンスで返されたOffsetの値 */
	public static String sOffset;
	
	/** DL時必要なS3Host ●static変数でのみ保持 */
	public static String sS3Host = null;
	/** DL時必要なBucketName ●static変数でのみ保持 */
	public static String sBucketName = null;
	
	/** アプリが起動してから内部領域にあるファイルを削除したかどうかのフラグ **/
	public static boolean sDeletePrivateFiles = false;
	
	/** Purchaseテーブルが初期セットされているかどうかフラグ */
	private boolean mDbInitialized = false;
	
	public Preferences(Context context){
		// SharedPreferncesの取得 - settings.xml
		pref = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
	}
	
	/**
	 * Preferncesクラスで保持しているstatic変数の初期化
	 */
	public void init_pref(){
		pref = null;
		sDefaultCheck = false;
		sDeleteCapture = false;
		sMode = false;
		sStoreList = null;
		sUserID = null;
		sSort_main = ConstDB.DESCEND_DATE;
		sSort_web = ConstDB.DESCEND_DATE;
		sListFilter = VIEW_ALL_CONTENTS;
		sMailAddress = null;
		sOffset = null;
		sS3Host = null;
		sBucketName = null;
		isInAppBillingPublicKeyCheck = false;
		WebBookShelfDlList instance = WebBookShelfDlList.getInstance();
		instance.reset();
		Logput.d("Preferences static : init");
	}
	
	/**
	 * チェックコードを返す
	 * @return チェックコード
	 */
	public String getCheckCode(){
		return pref.getString(PREF_CHECKCODE, null);
	}
	
	/**
	 * チェックコードをセット
	 * @param checkCode チェックコード
	 */
	public void setCheckCode(String checkCode){
		comit(PREF_CHECKCODE, checkCode);
	}
	
	/**
	 * 一時保存しているメールアドレスを返す
	 * @return 一時保存メールアドレス
	 */
	public String getMailAddress_temporary(){
		return pref.getString(PREF_MAILADDRESS_TEMPORARY, null);
	}
	
	/**
	 * メールアドレスを一時保存
	 * @param mailAddress_temporary 一時保存メールアドレス
	 */
	public void setMailAddress_temporary(String mailAddress_temporary){
		comit(PREF_MAILADDRESS_TEMPORARY, mailAddress_temporary);
	}

	/**
	 * バージョン情報最終取得日時を返す(UTC)
	 * @return バージョン情報最終取得日時
	 */
	public String getVersion_lastGetDate() {
		return pref.getString(VERSION, null);
	}

	/**
	 * バージョン情報最終取得日時を保存(UTC)
	 * @param バージョン情報最終取得日時
	 */
	public void setVersion_lastGetDate(String version_lastGetDate) {
		comit(VERSION, version_lastGetDate);
	}

	/**
	 * GetContents 前回のリクエストのレスポンスで返されたOffsetの値を取得
	 * @return GetContents 前回のリクエストのレスポンスで返されたOffsetの値
	 */
	public String getOffset(){
		String offset = pref.getString(PREF_OFFSET, null);
		Preferences.sOffset = offset;
		return offset;
	}

	/**
	 * GetContentsリクエストのレスポンスで返されたOffsetの値をセット
	 * @param GetContentsリクエストのレスポンスで返されたOffsetの値をセット
	 */
	public void setOffset(String offset){
		Preferences.sOffset = offset;
		comit(PREF_OFFSET,offset);
	}

	/**
	 * 最後にサーバと後期した日時(UTC)を返す - Web書庫
	 * @return 最後にサーバと後期した日時(UTC)
	 */
	public String getLastSyncDate_book(){
		return pref.getString(PREF_LAST_SYNC_DATE_BOOK, null);
	}

	/**
	 * 最後にサーバと後期した日時(UTC)を保存する - 販売サイト一覧
	 * @param 最後にサーバと後期した日時(UTC)
	 */
	public void setLastSyncDate_book(String last_sync_date){
		comit(PREF_LAST_SYNC_DATE_BOOK,last_sync_date);
	}

	/**
	 * メールアドレスを返す
	 * @return メールアドレス
	 */
	public String getMailAddress(){
		String address = pref.getString(PREF_MAIL_ADDRESS, null);
		Preferences.sMailAddress = address;
		return address;
	}

	/**
	 * メールアドレスを保存する
	 * @param メールアドレス
	 */
	public void setMailAddress(String address){
		Preferences.sMailAddress = address;
		comit(PREF_MAIL_ADDRESS, address);
	}

	/**
	 * コンテンツリストのソート方法を取得(メイン画面)
	 * @return コンテンツリストのソート方法
	 */
	public int getSort(){
		String listSort = pref.getString(PREF_SORT, null);
		if(listSort == null){
			// 設定されていない場合はダウンロード日時降順
			setSort(ConstDB.DESCEND_DATE);
		}else{
			Preferences.sSort_main = Integer.parseInt(listSort);
		}
		return Preferences.sSort_main;
	}

	/**
	 * コンテンツリストのソート方法をセット(メイン画面)
	 * @param ソート方法
	 */
	public void setSort(int sort){
		Preferences.sSort_main = sort;
		String listSort = String.valueOf(sort);
		comit(PREF_SORT, listSort);
	}

	/**
	 * 最終起動日時刻を取得
	 * @return 最終起動時刻
	 */
	public String getLast_startup_date() {
		// settings.xmlから読み取り
		return pref.getString(PREF_LAST_STARTUP_DATE_KEY, null);
	}

	/**
	 * 最終起動日時を保存
	 * @param last_startup_date
	 */
	public void setLast_startup_date(String last_startup_date) {
		// 日時をパターン文字列に変換し
		Logput.v("[LAST STARTUP DATE] " + last_startup_date);
		comit(PREF_LAST_STARTUP_DATE_KEY, last_startup_date);
	}

	/**
	 * ライブラリIDを取得
	 * @return ライブラリID
	 */
	public String getLibraryID() {
		// settings.xmlから読み取り
		return pref.getString(PREF_LIBRARYID_KEY, null);
	}

	/**
	 * ライブラリIDを保存
	 * @param ライブラリID
	 */
	public void setLibraryID(String libraryID) {
		comit(PREF_LIBRARYID_KEY, libraryID);
	}

	/**
	 * ユーザーIDを取得
	 * @return ユーザーID
	 */
	public String getUserID() {
		// settings.xmlから読み取り,static変数にセット
		Preferences.sUserID = pref.getString(PREF_USERID_KEY,null);
		return sUserID;
	}

	/**
	 * ユーザーIDを保存
	 * @param ユーザーID
	 */
	public void setUserID(String userID) {
		Preferences.sUserID = userID;
		comit(PREF_USERID_KEY, userID);
	}

	/**
	 * アクセスコードを取得
	 * @return アクセスコード
	 */
	public String getAccessCode() {
		// settings.xmlから読み取り
		return pref.getString(PREF_ACCESSCODE_KEY, null);
	}

	/**
	 * アクセスコードを保存
	 * @param アクセスコード
	 */
	public void setAccessCode(String accessCode) {
		comit(PREF_ACCESSCODE_KEY, accessCode);
	}
	
	/**
	 * WEBサイト誘導用：閲覧コンテンツダウンロードIDをセット
	 * @param downloadID ダウンロードID
	 */
	public void setNaviDlId(String downloadID){
		comit(PREF_NAVI_DL_ID, downloadID);
	}
	
	
	/**
	 * WEBサイト誘導用：閲覧コンテンツダウンロードIDを取得
	 * @return downloadID ダウンロードID
	 */
	public String getNaviDlId(){
		return pref.getString(PREF_NAVI_DL_ID, null);
	}
	
	/**
	 * Purchaseテーブルが初期セットされているかどうかフラグを取得
	 * @return Purchaseテーブルが初期セットされているかどうかフラグ
	 */
	public boolean getDbInitialized(){
		mDbInitialized = pref.getBoolean(PREF_DB_INITIALIZED, false);
		return mDbInitialized;
	}
	/**
	 * Purchaseテーブルが初期セットされているかどうかフラグをセット
	 * @param Purchaseテーブルが初期セットされているかどうかフラグ
	 */
	public void setDbInitialized(boolean initialized){
		mDbInitialized = initialized;
		comit(PREF_DB_INITIALIZED, initialized);
	}
	
	public void setViewingFileName(String fileName) {
		comit("viewing_filename", fileName);
	}
	public String getViewingFileName() {
		return pref.getString("viewing_filename", null);
	}
	
	//********************************
	// COMIT
	//********************************
	
	private void comit(String key, String value){
		Editor e = pref.edit();
		e.putString(key, value);
		e.commit();
	}
	
	private void comit(String key, boolean value){
		Editor e = pref.edit();
		e.putBoolean(key, value);
		e.commit();
	}
	
	private void comit(String key, long value){
		Editor e = pref.edit();
		e.putLong(key, value);
		e.commit();
	}
	
}
