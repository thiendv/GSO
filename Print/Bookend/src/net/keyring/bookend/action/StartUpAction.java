package net.keyring.bookend.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import net.keyring.bookend.Logput;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.bean.BookBeans;
import net.keyring.bookend.constant.Const;
import net.keyring.bookend.constant.ConstStartUp;
import net.keyring.bookend.db.ContentsDao;
import net.keyring.bookend.request.Activate2;
import net.keyring.bookend.request.CheckActivation;
import net.keyring.bookend.request.ServerGetTime;
import net.keyring.bookend.util.DateUtil;
import net.keyring.bookend.util.FileUtil;
import net.keyring.bookend.util.StringUtil;
import net.keyring.bookend.util.Utils;
import android.content.Context;
import android.os.Environment;

/**
 * 起動してから1回だけ行う処理(noHistry)<br>
 * - ストレージ上に保存場所を確保する<br>
 * - アクティベーションチェック<br>
 * - バージョンチェック
 *
 * @author Hamaji
 *
 */
public class StartUpAction implements ConstStartUp, Const{
	
	/** ServerGetTimeクラス */
	private static ServerGetTime sGetTime;
	
	/** Context */
	private Context			mCon;
	/** Preferencesクラス */
	private Preferences		mPref;
	/** Activation,CheckActivationエラーメッセージ */
	private String			mErrorMessage;
	
	private String			mLibraryID;
	private String			mAccessCode;
	
	/**
	 * コンストラクタ
	 * @param con Context
	 */
	public StartUpAction(Context con){
		this.mCon = con;
		mPref = new Preferences(con);
	}
	
	/**
	 * ● 起動してから1回だけ行うチェック
	 * @return チェックフラグ
	 */
	public int startUpCheck(){
		int checkFlag = CHECK_OK;
		if(!isExternalStorage()){ // SDカードがセットされているかチェック(エラー：終了)
			checkFlag = CHECK_ERROR_SD;
		}else{
			// 必要な設定を取得・設定・保存
			settingPreferences();
			Logput.d("BookEnd Dir : " + Preferences.sExternalStrage);
			if(StringUtil.isEmpty(Preferences.sUserID)){
				checkFlag = activate();
			}else{
				// ２回目起動からはCheckActivationを行う
				checkFlag = checkActivation();
			}
		}
		
		
		// バージョンアップリクエストが必要かチェック
		if(checkFlag == CHECK_OK){
			// Log出力
			startCheckLog();
			// GetTime
			if(!checkClock()){
				checkFlag = CHECK_ERROR_CLOCK;
			}else if(isVersionUP()){
				checkFlag = CHECK_VERSIONUP_REQUEST;
			}
		}
		return checkFlag;
	}
	
	/**
	 * WEB書庫からＤＬ中、タスクが落ちた場合のDBとコンテンツの整合性
	 */
	public void checkDlFailContents(){
		long rowID = -1;
		String filePath = null;
		String thumbPath = null;
		ContentsDao dao = new ContentsDao(mCon);
		try{
			// DL途中で止まってしまったものはファイル削除後DLstatus初期化
			ArrayList<BookBeans> list = dao.loadDownloadingContents_web();
			for(BookBeans bean : list){
				rowID = bean.getRowId();
				if(rowID != -1){
					Logput.d(">DL status init [ " + rowID + " ]");
					filePath = bean.getFile_path();
					if(filePath != null){
						FileUtil.deleteFile(new File(filePath));
					}
					thumbPath = bean.getThumb_path();
					if(thumbPath != null){
						FileUtil.deleteFile(new File(thumbPath));
					}
					dao.updateDLstatus(rowID, STATUS_INIT, null, null, 0);
				}
			}
		}catch(Exception e){
			Logput.e(e.getMessage(), e);
		}
	}
	
	/**
	 * ● OS時計のチェック CHECK_ERROR_CLOCK
	 * @return　日時がずれていなければtrue,それ以外はfalseを返す
	 */
	private boolean checkClock(){
		boolean check = false;
		// 最終起動日時を取得
		if(mPref == null) mPref = new Preferences(mCon);
		String lastStartUpDate = mPref.getLast_startup_date();
		
		if(Utils.isConnected(mCon)){
			Logput.d("[NETWORK] ONLINE");
			// オンライン時のチェック - Serverから取得した日時と前後10分以上ずれていないか
    		if(sGetTime == null) sGetTime = new ServerGetTime();
    		if(sGetTime.checkClock()){
    			check = true;
    		}
		}else{
			Logput.d("[NETWORK] OFFLINE");
			try{
				// オフライン時のチェック
				Date last_startup_date = DateUtil.toDate(lastStartUpDate, "UTC");
				Date osNow = DateUtil.toDate(DateUtil.getNowUTC(), "UTC");
	    		// 最終起動日時よりOSの現在日時が後ならOK,起動時刻と現在日時が同じ場合もOK
	    		if(!osNow.after(last_startup_date) || osNow == last_startup_date){
	    			Date checkDate = new Date(osNow.getTime() + (-60000));
	    			// 最終起動日時よりOS時計が1分以上過去の場合はエラーとして起動しない
		          	if(checkDate.before(last_startup_date)){
		          		Logput.e("UTC) [OSNOW-1m]" + checkDate + " [LAST_STARTUP_DATE]" + last_startup_date);
		          	}else{
		          		check = true;
		          	}
	    		}else{
	    			check = true;
	    		}
			}catch(NullPointerException e){
			}
		}
		if(check){
			// 起動日時保存
			if(mPref == null) mPref = new Preferences(mCon);
			mPref.setLast_startup_date(DateUtil.getNowUTC());
			Logput.v("[START_UP_DATE UTC] " + lastStartUpDate);
		}
		return check;
	}
	
	/**
	 * Activation
	 * @return
	 */
	public int activate(){
		int checkFlag = CHECK_OK;
		if(Utils.isConnected(mCon)){
			// オンライン状態の場合は初回アクティベーションチェックを行う
			Activate2 activate = new Activate2(mCon);
			if(!activate.execute()){
				// Activation - NG
				checkFlag = CHECK_ERROR_ACTIVATION;
				this.mErrorMessage = activate.getDescription();
			}
		}else{
			// 初回アクティベーションはオンライン状態でなければならない
			checkFlag = CHECK_OFFLINE_ACTIVATION;
		}
		return checkFlag;
	}
	
	/**
	 * 必要な設定を取得・設定・保存し、アクティベーションクラスへ
	 */
	private void settingPreferences(){
		// ライブラリID取得
		mLibraryID = mPref.getLibraryID();
		// アクセスコード取得
		mAccessCode = mPref.getAccessCode();

		// 実行環境モード確認・static変数に保存
		Preferences.sMode = Utils.checkFile(Environment.getExternalStorageDirectory().getPath() + "/" + Const.STG_FILE);
		// ログキャットを出力するかチェック
		Logput.isLogCat();
		// ログを出力するかどうか
		Logput.isLogput = Utils.checkFile(Preferences.sExternalStrage + "/" + Logput.LOG_DIR);
	}
	
	/**
	 * SDカードがセットされているかチェック、セットされている場合は専用ディレクトリを作成
	 * @return SDカードがセットされていた場合はtrue,それ以外はfalseを返す
	 */
	public boolean isExternalStorage(){
		Logput.v("Environment.getExternalStorageState() = " + Environment.getExternalStorageState());
		Preferences.sExternalStrage = mCon.getDir("download", Context.MODE_WORLD_READABLE).getPath();
		return true;
	/*
	 * // SDカードがマウントされているかどうかチェック
		boolean check = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		if(check && StringUtil.isEmpty(Preferences.sExternalStrage)){
			// マウントされていて、外部アプリ領域が設定されていない場合
			// /mnt/sdcard/external_sd/Android/data/net.keyring.bookend/files/log
    		Preferences.sExternalStrage = mCon.getExternalFilesDir(null).getPath();
		}
		return check;
	*/
	}
	
	/**
	 * バージョンアップが必要かチェック
	 * @return バージョンアップが必要な場合はtrue,それ以外はfalseを返す
	 */
	private boolean isVersionUP(){
		boolean check = false;
		// バージョンチェック(オンライン時のみ)
		if(Utils.isConnected(mCon)){
			String verLastGetDate = mPref.getVersion_lastGetDate();
			// 現在使用しているBookendのバージョン
			String nowVer = Utils.getBookendVer(mCon);
			Logput.d("[bookend ver." + nowVer + "]");
			check = check_getVersion(verLastGetDate);
		}
		return check;
	}
	
    /**
     * バージョンチェックを行うかどうかチェック
     * @param GetVersion最終確認日時
     * @return GetVersionが必要な場合はtrue,必要ない場合はfalseを返す
     */
    private boolean check_getVersion(String verLastGetDate){
    	boolean check = true;
    	// SDカード直下にConst.IS_VER_CHECKファイルが存在した場合は常にチェック
    	String sd = Environment.getExternalStorageDirectory().getPath();
    	if(!Utils.checkFile(sd + "/" + Const.IS_VER_CHECK)){
	    	if(!StringUtil.isEmpty(verLastGetDate)){
				// １日前の現在時刻を取得
				Date yesterday = DateUtil.getNow(-86400);
				// 最終確認日時のタイムゾーンをOSに合わせる
				Date lastGetVersionDate = DateUtil.toDate(verLastGetDate, "UTC");
				// 前回の最終確認日時から1日経ていなかった場合はチェックしなくてOK
				if(lastGetVersionDate.after(yesterday)){
					check = false;
				}
	    	}else{
	    		// 初めての場合は現在時刻を入力しておく
	    		verLastGetDate = DateUtil.getNowUTC();
				mPref.setVersion_lastGetDate(verLastGetDate);
	    	}
	    	Logput.v("[LastGetDate UTC] " + verLastGetDate);
    	}else{
    		Logput.v(">Always version check.");
    	}
    	return check;
    }
    
 // ------------------ //Activation処理 -------------------

 	/**
 	 * checkActivation - 既に「アクティベーション済み」の場合
 	 */
 	private int checkActivation(){
 		int checkFlag = CHECK_OK;
 		if(Utils.isConnected(mCon)){
 			// CheckActivationリクエスト+ステータスチェック
 			CheckActivation checkActivation = new CheckActivation(mCon);
 			String status = checkActivation.execute();
 			this.mErrorMessage = checkActivation.getDescription();
 			if(Utils.equal_str("23011", status)){
 				// status = 23011 : 再Activate
 				// 指定されたライブラリIDがリセットされている。再アクティベーションの確認をユーザに行い、再度Activateリクエストを行ってアクティベーションする
 				checkFlag = CHECK_REQUEST_ACTIVATION;
 			}else if(!Utils.equal_str("23000", status)){
 				// CheckActivationリクエスト - NG
 				checkFlag = CHECK_ERROR_CHECKACTIVATION;
 			}
 		}else{
 			// オフラインの場合はチェックしない
 			Logput.v("Offline >> None CheckActivate.");
 		}
 		return checkFlag;
 	}

 	// ------------------ Activation処理// -------------------

	/**
	 * static変数初期状態をログに出力
	 */
	private void startCheckLog(){
		String logMode = null;
		String logIsPut;
		// 実行モード
		if(Preferences.sMode)logMode = "STG";
		else logMode = "PUBLIC";
		// LOGを出力するか否か
		if(Logput.isLogput) logIsPut = "LOG OUTPUT";
		else logIsPut = "LOG OFF";

		Logput.d("[START_CHECK] >>>>>> " + logIsPut);
		Logput.d("[MODE] " + logMode);
		
		Logput.d("[USER_ID] " + Preferences.sUserID);
		if(mAccessCode == null){
			mAccessCode = mPref.getAccessCode();
		}
		Logput.d("[ACCESS_CODE] " + mAccessCode);
		if(mLibraryID == null){
			mLibraryID = mPref.getLibraryID();
		}
		Logput.d("[LIBRARY_ID] " + mLibraryID);
	}
    
	/**
	 * Activation,CheckActivationエラーメッセージを取得
	 * @return エラーメッセージ
	 */
	public String getErrorMessage(){
		return this.mErrorMessage;
	}
}
