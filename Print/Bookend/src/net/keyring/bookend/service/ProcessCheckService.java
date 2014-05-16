package net.keyring.bookend.service;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import net.keyring.bookend.Logput;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.constant.Const;
import net.keyring.bookend.util.Utils;
import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;


public class ProcessCheckService extends IntentService implements Const {
	/** ActivityのstartService(intent);で呼び出されるコンストラクタ */
	public ProcessCheckService() {
		super("ProcessCheckService");
		Logput.d(">>ProcessCheckService START");
	}

	/** [ProcessCheckService] AdobeReaderが起動したかチェックするフラグ */
	public final int VIEWER_START_CHECK = 1;
	/** [ProcessCheckService] AdobeReaderがアクティブかチェックするフラグ */
	public final int VIEWER_ACTIVE_CHECK = 2;
	/** [ProcessCheckService] AdobeReaderが非アクティブになったフラグ */
	public final int VIEWER_CHECK_FINISH = 3;
	/** チェックを行うサイクル(msec) */
	public final int SLEEP_MILLISEC_FOR_VIEWER_START = 500;
	public final int SLEEP_MILLISEC_FOR_VIEWER_STOP = 250;

	// ----- ビューアとして認識するアプリ ----------------------------
	/** Pattern "AdobeReader" */
	private final Pattern PATTERN_ADOBE_READER = Pattern.compile("com.adobe.reader");
	/** Pattern "ePubViewer" */
	private final Pattern PATTERN_EPUB_VIEWER = Pattern.compile("net.keyring.bookend.viewer");
	/** Pattern "MCComicViewer" */
	private Pattern PATTERN_MCCOMIC_VIEWER = null;
	/** Pattern "MCBookViewer" */
	private Pattern PATTERN_MCBOOK_VIEWER = null;
	/** Pattern "AudioViewer" */
	private Pattern PATTERN_AUDIO_VIEWER = null;
	
	//	viewerCheck()の返り値
	private final int APP_OTHER = 0;
	private final int APP_ADOBE_READER = 1;
	private final int APP_EPUB_VIEWER = 2;
	private final int APP_MCCOMIC_VIEWER = 3;
	private final int APP_MCBOOK_VIEWER = 4;
	private final int APP_AUDIO_VIEWER = 5;
	
	//	Adobe ReaderのAcrobat.comへのアップロードを行うアクティビティ(ver 10以下)
	private final String ACTIVITY_ADOBE_COULD_UI = "com.adobe.reader.cloud.ui.FileTransferActivity";
	//	Adobe ReaderのAcrobat.comへのアップロードを行うアクティビティ(ver 11以上)
	private final String ACTIVITY_ADOBE_COULD_UI2 = "com.adobe.reader.misc.ARFileTransferActivity";
	

	/** Reader起動時刻 */
	private Date mViewerStartDate = null;
	/** 閲覧するファイル名(krpdf) */
	private String mFileName = null;
	
	/** スレッドを実行し続けるかどうかのフラグ */
	private boolean enableThread = true;
	/** サービスで使用する設定の一時保存用XMLファイル */
	private Preferences pref = null; 
	
	@Override
	public void onCreate() {
		Logput.v("----< ProcessCheckService:onCreate >----");
		super.onCreate();
		
		pref = new Preferences(this);
		
		//	パッケージ名を取得する必要があるためここで初期化する
		PATTERN_MCCOMIC_VIEWER = Pattern.compile(getPackageName() + ":MCComicViewer");
		PATTERN_MCBOOK_VIEWER = Pattern.compile(getPackageName() + ":MCBook");
		PATTERN_AUDIO_VIEWER = Pattern.compile(getPackageName() + ":AudioViewer");
	}

	@Override
	public void onDestroy() {
		Logput.v("----< ProcessCheckService:onDestroy >----");
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Logput.v("----< ProcessCheckService:onStartCommand >----");
		Logput.v("intent=" + intent);
		Logput.v("flags=" + flags);
		Logput.v("startId=" + startId);
		
		//	Service.START_STICKYをreturnしているので、intentは以下の2通り
		//	A) intent!=nullの場合: サービス起動時、またはBookendアプリの終了通知
		//	B) intent==nullの場合: サービスが再起動された
		if(intent != null) {
			//	A) FINISH_PROCESS が存在する場合: Bookendアプリが終了した
			//	B) FINISH_PROCESS が存在しない場合: 通常のサービス起動
			String finishProcess = intent.getStringExtra(FINISH_PROCESS);
			if(finishProcess != null) {
				//	復号ファイル、キャプチャ画像を削除
				action(VIEWER_CHECK_FINISH);
				//	暗号化PDFファイルの場合はAdobe Readerを停止
				if(mFileName != null) {
					forceKillAdobeReader();
				}
				//	監視処理を停止
				enableThread = false;
			}
			else {
				//	 閲覧対象のファイル名を取得してXMLに保存しておく
				//	*このExtraはpdf,krpdfの場合のみセットされている
				mFileName = intent.getStringExtra(VIEW_CONTENT_NAME);
				pref.setViewingFileName(mFileName);
				Logput.d("mFileName: " + mFileName);
			}
		}
		else {
			//	閲覧対象のファイル名をXMLファイルから再取得
			String fileName = pref.getViewingFileName();
			if(fileName != null) {
				mFileName = fileName;
			}
			Logput.d("mFileName: " + mFileName);
		}
		
		int ret = super.onStartCommand(intent, flags, startId);
		Logput.d("ret: " + ret);
		return Service.START_STICKY;
	}

	/**
	 * 非同期処理を行うメソッド
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		Logput.v("----< ProcessCheckService:onHandleIntent >----");
		Logput.v("intent=" + intent);
		
		//	プロセス監視処理
		while(enableThread) {
			// ビューアが起動するまで待つ
			action(VIEWER_START_CHECK);
			// ビューアが終了するまで待つ
			action(VIEWER_ACTIVE_CHECK);
			// ファイルを削除して処理終了
			action(VIEWER_CHECK_FINISH);
		}
	}

	/**
	 * 少し待つ
	 */
	private void sleep(long waitMilliSec) {
		try{
			Thread.sleep(waitMilliSec);
		} catch(InterruptedException e) {
		}
	}
	
	/**
	 * 閲覧中にキャプチャされた画像ファイルを削除
	 * *予め決められたディレクトリ内を検索する
	 */
	private void deleteCaptureFiles1() {
		for(int i = 0; i < Const.SCREENCAPTURE_DIR.length; i++) {
			if(Utils.checkFile(Const.SCREENCAPTURE_DIR[i])){
				
				// SDカード内にScreenCaptureがあった場合はディレクトリ内のキャプチャ画像をチェック
				// ScreenCaptureディレクトリ内のファイルを取得
				File[] captureFiles = new File(Const.SCREENCAPTURE_DIR[i]).listFiles();
				if(captureFiles.length >= 1){
					for(File file : captureFiles){
						// ファイルの最終更新日を取得・セット
						long last_update = file.lastModified();
						Date lastUpdate = new Date(last_update);
						
						// Reader起動より後に撮られたpng,jpg形式のファイルを削除
						if(lastUpdate.after(mViewerStartDate)){
							if(file.getName().endsWith(".png") || file.getName().endsWith(".jpg")){
								Logput.d("NG Capture File : " + file.getPath());
								// ＮＧファイルを削除
								file.delete();
								Preferences.sDeleteCapture = true;
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * 閲覧中にキャプチャされた画像ファイルを削除
	 * *ContentProviderを使用してファイルを検索する
	 */
	private void deleteCaptureFiles2() {
		try {
			ContentResolver contentResolver = getContentResolver();
	        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
	        cursor.moveToFirst();
	 
	        //	データが1つもなければ終了
	        if(cursor.getCount() == 0) {
	        	return;
	        }
	        
	        int fieldIndex;
	        do {
	            //	ファイルパスが Screenshots を含んでいる
	            fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
	            if(fieldIndex == -1) {
	            	continue;
	            }
	            String filePath = cursor.getString(fieldIndex);
	            //Logput.d("filePath = " + filePath);
	            if(filePath.indexOf("Screenshots") == -1) {
	            	continue;
	            }
	            
	            //	ファイル拡張子が .png か .jpg 
	            File imageFile = new File(filePath);
	            if(!imageFile.getName().endsWith(".png") && !imageFile.getName().endsWith(".jpg")){
	            	continue;
	            }
	                       
	            //	最終更新日が mViewerStartDate 以降
	            fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED);
	            if(fieldIndex == -1) {
	            	continue;
	            }
	            String dateModified = cursor.getString(fieldIndex);
	            Date modified = new Date(Long.valueOf(dateModified).longValue() * 1000);
	            if(!modified.after(mViewerStartDate)){
	            	continue;
	            }
	            
	            //	ファイルを削除する
	            Logput.d("NG Capture File : " + imageFile.getPath());
	            imageFile.delete();
				Preferences.sDeleteCapture = true;
	        } 
	        while (cursor.moveToNext());
		}
		catch(Throwable e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 処理分け
	 * @param アクション指定
	 */
	private void action(int actionFlag) {
		ActivityManager activityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
		switch (actionFlag) {
		case VIEWER_START_CHECK:	// ビューアが起動するまで待つ
			Logput.v("Viewer start wait...");
			while(!processCheck(activityManager)){
				//Logput.v("wait start.");
				sleep(SLEEP_MILLISEC_FOR_VIEWER_START);
			}
			// ビューア起動時刻を取得する
			mViewerStartDate = new Date();
			Logput.v("Viewer start date = " + mViewerStartDate);
			break;
		case VIEWER_ACTIVE_CHECK:	// ビューアが終了するまで待つ
			while(processCheck(activityManager)){
				//Logput.v("wait stop.");
				sleep(SLEEP_MILLISEC_FOR_VIEWER_STOP);
			}
			Logput.v("Viewer close");
			break;
		case VIEWER_CHECK_FINISH:	// PDFファイルを削除して処理終了
			// Viewer起動中に撮られたキャプチャを削除
			deleteCaptureFile();
			
			// pdf,krpdfの場合は生PDFファイルを削除する
			if(mFileName != null){
				deleteDecryptPdfFile();
			}
			Logput.d("ProcessCheckService FINISH <<");
		}
	}
	
	/**
	 * キャプチャ画像の削除を行う
	 */
	private void deleteCaptureFile() {
		//	bookendアプリが終了した場合は mViewerStartDate が null の場合がある
		//	この場合は削除する必要はないので何もしない
		if(mViewerStartDate == null) {return;}
		
		// 	Viewer起動中に撮られたキャプチャを削除
		deleteCaptureFiles1();
		deleteCaptureFiles2();
		
		//	キャプチャ画像がまだ保存されてない場合があるので、しばらく経ってからもう一度実行する
		new Timer(true).schedule(new TimerTask() {
			@Override
			public void run() {
				deleteCaptureFiles1();
				deleteCaptureFiles2();
			}
		}, 2000);
	}
	
	/**
	 * PDFファイルを削除する
	 * *Adobe Readerがコピーしたファイルを削除する
	 * *bookendアプリが復号化したファイルを削除する
	 */
	private void deleteDecryptPdfFile() {
		// Viewer起動中にコピーされたPDFファイルを削除
		//	*PDFファイルをMODE_WORLD_WRITEABLEにすることでコピーされなくなったのでコメントアウト
		//deleteCopiedFileByAdobeReader();
		
		// 復号化されたファイルを削除
		if(getFileStreamPath(mFileName + ".pdf").exists()) {
			deleteFile(mFileName + ".pdf");
			Logput.v("Delete file name : " + mFileName + ".pdf");
		}
	}

	/**
	 * プロセスリストを取得し、リーダーがフォアグラウンドにいるかどうかをチェックする
	 *
	 * @return AdobeReader,ePubViewerがフォアグラウンドにあったらtrueを返す
	 */
	private boolean processCheck(ActivityManager activityManager){
		//Logput.d("---------- processCheck() -----------");
		
		boolean processCheck = false;
		// プロセスリストを取得する
		List<ActivityManager.RunningAppProcessInfo> processList = activityManager.getRunningAppProcesses();
		if(processList != null){
			for(ActivityManager.RunningAppProcessInfo process :processList){
				//Logput.d("process: " + process.processName + ", " + process.importance);
				
				if(process != null){
					String name = process.processName;
					int importance = process.importance;
					int app = viewerCheck(importance, name);
					// フォアグラウンドがビューアアプリだったらループを抜ける
					if(app != APP_OTHER){
						processCheck = true;
						//	Adobe Readerの場合のみ、さらにアクティビティのチェックを行う
						if(app == APP_ADOBE_READER) {
							//	不正なActivityが最前面にいる場合は復号化ファイルを削除する
							if(!checkAdobeReaderActivity(activityManager)) {
								deleteDecryptPdfFile();
							}
						}
						
						break;
					}
				}
			}
		}else{
			Logput.i("RunningTaskInfo ERROR");
		}
		return processCheck;
	}


	/**
	 * 最前面にあるのがビューアアプリかどうかをチェック
	 * @param プロセスインポータんス
	 * @param プロセス名
	 * @return ビューアアプリの場合は該当する値を返す、それ以外の場合は 0 を返す
	 */
	private int viewerCheck(int process, String name){
		if(process == 100){	// プロセスが100だった場合(IMPORTANCE_FOREGROUND)
			//Utils.debug("ForeGround app name = " + name);
			if(PATTERN_ADOBE_READER.matcher(name).matches()){	// プロセス名がAdobeReaderだった場合
				return APP_ADOBE_READER;
			}else if(PATTERN_EPUB_VIEWER.matcher(name).matches()){	// プロセス名がePubViewerだった場合
				return APP_EPUB_VIEWER;
			}else if(PATTERN_MCCOMIC_VIEWER.matcher(name).matches()){	// プロセス名がMCComicViewerだった場合
				return APP_MCCOMIC_VIEWER;
			}else if(PATTERN_MCBOOK_VIEWER.matcher(name).matches()){	// プロセス名がMCBookViewerだった場合
				return APP_MCBOOK_VIEWER;
			}else if(PATTERN_AUDIO_VIEWER.matcher(name).matches()){	// プロセス名がAudioViewerだった場合
				return APP_AUDIO_VIEWER;
			}
		}
		return APP_OTHER;
	}
	
	/**
	 * Adobe Readerのアクティビティをチェックする
	 * *Acrobat.comへのアップロードを行うアクティビティだった場合は false を返す
	 * @param activityManager
	 * @return
	 */
	private boolean checkAdobeReaderActivity(ActivityManager activityManager) {
		List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
		for(int i = 0; i < tasks.size(); i++) {
			ComponentName topActivity = tasks.get(i).topActivity;
			//Logput.d(String.format("task[%d]: %s, %s", i, topActivity.getPackageName(), topActivity.getClassName()));
			
			String activityClassName = topActivity.getClassName();
			if(activityClassName.compareToIgnoreCase(ACTIVITY_ADOBE_COULD_UI) == 0) {
				return false;
			} else if(activityClassName.compareToIgnoreCase(ACTIVITY_ADOBE_COULD_UI2) == 0) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Adobe Readerでの閲覧を強制停止する
	 * *アプリを強制終了するには権限が必要なので、ダミーPDFを開かせることで現在のPDFの閲覧を強制的に終了させる
	 */
	private void forceKillAdobeReader() {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("file:///dummy.pdf"));
		intent.setClassName("com.adobe.reader", "com.adobe.reader.AdobeReader");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}
}