package net.keyring.bookend.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;

import net.keyring.bookend.Logput;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.R;
import net.keyring.bookend.ServerGetThumb;
import net.keyring.bookend.WebBookShelfDlList;
import net.keyring.bookend.bean.BookBeans;
import net.keyring.bookend.callback.DownloadStatus;
import net.keyring.bookend.callback.ICallbackListener;
import net.keyring.bookend.callback.WebBookShelfDlCallback;
import net.keyring.bookend.constant.Const;
import net.keyring.bookend.constant.ConstDB;
import net.keyring.bookend.db.ContentsDao;
import net.keyring.bookend.request.GetAwsInfo;
import net.keyring.bookend.request.UpdateLicense;
import net.keyring.bookend.util.FileUtil;
import net.keyring.bookend.util.StringUtil;
import net.keyring.bookend.util.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
/**
 * Web書庫からのダウンロードサービス
 * @author Hamaji
 *
 */
public class WebBookShelfDlService extends Service implements ConstDB, Const {
	
	/** コールバックインターフェイス設定 */
	private RemoteCallbackList<ICallbackListener> callbackList = new RemoteCallbackList<ICallbackListener>();
	/** Daoクラス */
	private ContentsDao				mDao;
	/** ダウンロード中コンテンツステータス */
	private DownloadStatus			mDL_contents_status = null;
	/** ダウンロードするコンテンツRowID */
	private long					mID = -1;
	/** ダウンロードコンテンツ保存先パス */
	private String					mFilePath;
	/** ダウンロードサムネイル保存先パス */
	private String					mThumbPath;
	
	/** キャンセルフラグ（ダウンロード実行中コンテンツ） */
	public static volatile boolean	sStopRequested = false;
	/** キャンセル可・不可フラグ */
	public static volatile boolean	sIsNotCancel = false;
	/** ダウンロード中フラグ：DL中のコンテンツがある場合はtrue,それ以外はfalse */
	private static boolean			sDlFlag = false;
	
	private final int DL_SUCCESS = 0;
	private final int DL_FAIL = 1;
	private final int DL_CANCEL = 2;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Logput.d("------- WebBookShelfDlService onCreate -------");
		// メインスレッド開始
		mainThread.start();
		Logput.d(">>MainThread OK");
		// ダウンロードスレッド開始
		downloadThread.start();
		Logput.d(">>DownloadThread OK");
	}

	@Override
	public IBinder onBind(Intent intent) {
		if (WebBookShelfDlCallback.class.getName().equals(intent.getAction())) {
			Logput.v("--- WebBookShelfDlService onBind ---");
			// 実装クラスのインスタンスを返す
			return serciceIf;
		} else {
			return null;
		}
	}
	
	/**
	 * 再度バインドし直したときに実行される処理
	 * 
	 * @return
	 */
	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
		Logput.d("--- WebBookShelfDlService onRebind ---");
	}

	/**
	 * バインドが解除されたときに実行される処理
	 * 
	 * @param intent unbindService()渡されたIntentオブジェクト
	 */
	@Override
	public boolean onUnbind(Intent intent) {
		Logput.d("--- WebBookShelfDlService onUnbind ---");
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Logput.d("------- WebBookShelfDlService onDestroy -------");
		callbackList.kill();
	}
	
	// ----------------------------------------
	// setter/getter （synchronized）
	// ----------------------------------------
	/**
	 * ダウンロード中コンテンツローカル保存パスを取得<br>
	 * ※複数のスレッドから参照するのでsynchronizedのゲッターを使う
	 * 
	 * @return filePath ダウンロード中コンテンツローカル保存パス
	 */
	private synchronized String getFilePath() {
		return mFilePath;
	}

	/**
	 * ダウンロード中コンテンツローカル保存パスをセット<br>
	 * ※複数のスレッドから参照するのでsynchronizedのゲッターを使う
	 * 
	 * @param filePath ダウンロード中コンテンツローカル保存パス
	 */
	private synchronized void setFilePath(String filePath) {
		mFilePath = filePath;
	}
	/**
	 * ダウンロード中サムネイルローカル保存パスを取得<br>
	 * ※複数のスレッドから参照するのでsynchronizedのゲッターを使う
	 * 
	 * @return thumbPath ダウンロード中サムネイルローカル保存パス
	 */
	private synchronized String getThumbPath() {
		return mThumbPath;
	}

	/**
	 * ダウンロード中サムネイルローカル保存パスをセット<br>
	 * ※複数のスレッドから参照するのでsynchronizedのゲッターを使う
	 * 
	 * @param thumbPath ダウンロード中サムネイルローカル保存パス
	 */
	private synchronized void setThumbPath(String thumbPath) {
		mThumbPath = thumbPath;
	}
	
	/**
	 * ダウンロード中コンテンツステータス取得
	 * 
	 * @return ダウンロード中ステータス
	 */
	public synchronized DownloadStatus getDownloadStatus() {
		return this.mDL_contents_status;
	}

	/**
	 * ダウンロード中コンテンツステータスセット
	 * 
	 * @param rowID			コンテンツID
	 * @param status		ダウンロードステータ ス
	 * @param progress		ダウンロード進捗
	 */
	public synchronized void setDoanlodStatus(long rowID, int status, int progress, String errorMessage) {
		this.mDL_contents_status = new DownloadStatus(rowID, status, progress, errorMessage);
	}
	
	// ----------- Cancel -----------------
	/**
	 * ダウンロードキャンセル
	 * @param rowID コンテンツID
	 */
	private void cancel(long rowID, String filePath, String thumbPath) {
		setDoanlodStatus(rowID, STATUS_INIT, 0, null);
		delete(filePath);
		delete(thumbPath);
		setFilePath(null);
		setThumbPath(null);
		// ダウンロードステータスを初期化
		if (mDao == null) mDao = new ContentsDao(getApplicationContext());
		mDao.updateDLstatus(rowID, STATUS_INIT, null, null, 0);
		WebBookShelfDlService.sStopRequested = false;
		WebBookShelfDlService.sIsNotCancel = false;
		// ダウンロード中フラグをfalseに
		//WebBookShelfDlService.sDlFlag = false;
		Logput.i(">[" + rowID + "] DOWNLOAD CANCEL.");
	}
	
	// -------------------------------------
	// CallBack
	// -------------------------------------
	
	/**
	 * ICallbackServiceの実装
	 */
	private final WebBookShelfDlCallback.Stub serciceIf = new WebBookShelfDlCallback.Stub() {
		@Override
		public void registerListener(ICallbackListener listener)
				throws RemoteException {
			// コールバックリスト登録処理
			callbackList.register(listener);
			Logput.d(">>callbackList.register");
		}

		@Override
		public void removeListener(ICallbackListener listener)
				throws RemoteException {
			// コールバックリスト登録解除処理
			callbackList.unregister(listener);
			Logput.d(">>callbackList.unregister");
		}

		@Override
		public void startDownload_web(ICallbackListener listener, long rowID) throws RemoteException {
			Logput.d("Donwload request ID = " + rowID);
			// ダウンロードコンテンツ情報をセット
			// ダウンロード待ちリストにセット
			WebBookShelfDlList dlList = WebBookShelfDlList.getInstance();
			dlList.setDownloadList(rowID);
			// DB[ダウンロード待ち]更新
			if (mDao == null) mDao = new ContentsDao(getApplicationContext());
			mDao.updateDLstatus(rowID, STATUS_WAIT_FOR_DL_WEB, null, null, 0);
		}
	};
	
	/**
	 * ダウンロード待ちコンテンツ全て「ダウンロード中」 CallBack
	 * 
	 * @param downloadList	ダウンロード待ちコンテンツリスト
	 */
	private void callBack_waitForDL(LinkedList<Long> downloadList) {
		for (Long rowID : downloadList) {
			callback_task(new DownloadStatus(rowID, STATUS_WAIT_FOR_DL_WEB, 0, null));
		}
	}
	
	/**
	 * コールバック処理
	 */
	private void callback_task(DownloadStatus dlStatus) {
		// Callbackリストの処理を開始
		int n = callbackList.beginBroadcast();
		// 全アイテム分ループ
		for (int i = 0; i < n; i++) {
			try {
				// Callback Interfaceを叩く
				callbackList.getBroadcastItem(i).updateDownloadStatus(dlStatus);
			} catch (RemoteException e) {
				Logput.e(e.getMessage(), e);
			}
		}
		// Finish
		callbackList.finishBroadcast();
	}
	
	// ----------------------------
	// MainThread
	// ----------------------------
	
	/**
	 * ダウンロード管理メインスレッド(CallBackは全てこのスレッドで行う)
	 */
	Thread mainThread = new Thread(new Runnable() {
		@Override
		public void run() {
			Logput.d("DownloadService Main Thread Run!");
			while (true) {
				// ダウンロード実行管理
				if (!WebBookShelfDlService.sDlFlag) {
					download();
				}
				try {
					// 0.5秒スリープ
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
			}
		}
	});
	
	// ----------------------------
	// DownloadThread
	// ----------------------------
	
	/**
	 * ダウンロード実行スレッド(CallBackしないこと)
	 */
	Thread downloadThread = new Thread(new Runnable() {
		@Override
		public void run() {
			while (true) {
				long rowID = mID;
				if (rowID != -1) {
					if(Utils.isConnected(getApplicationContext())){
						if (WebBookShelfDlService.sStopRequested) {
							// キャンセルフラグが立っている場合
							cancel(rowID, null, null);
							mID = -1;
							// ダウンロード中のコンテンツがなくなったのでフラグをfalseに変更
							WebBookShelfDlService.sDlFlag = false;
							WebBookShelfDlService.sIsNotCancel = false;
						} else if (WebBookShelfDlService.sDlFlag) {
							setFilePath(null);
							setThumbPath(null);
							// DL開始
							int statusFlag;
							String filePath = null;
							String thumbPath = null;
							BookBeans book = null;
							try {
								statusFlag = download(rowID);
								book = getBook();
								filePath = book.getFile_path();
								thumbPath = book.getThumb_path();
								if(statusFlag == DL_FAIL){
									// DL ERROR
									exception(getString(R.string.download_fail), rowID, filePath, thumbPath);
								}else if(statusFlag == DL_SUCCESS){
									// DL SUCCESS
									setFilePath(filePath);
									setThumbPath(thumbPath);
									setDoanlodStatus(rowID, STATUS_DL_COMPLETE_WEB, 100, null);
								}else if(statusFlag == DL_CANCEL){
									cancel(rowID, getFilePath(), getThumbPath());
								}
							} catch (Exception e) {
								exception(e, rowID, getFilePath(), getThumbPath());
							}
							mID = -1;
							// フラグリセット
							WebBookShelfDlService.sDlFlag = false;
							WebBookShelfDlService.sIsNotCancel = false;
						}
					}else{
						// オフラインエラー
						exception(getString(R.string.dl_offline_error), rowID, null, null);
						// ダウンロード中のコンテンツがなくなったのでフラグをfalseに変更
						WebBookShelfDlService.sDlFlag = false;
						WebBookShelfDlService.sIsNotCancel = false;
					}
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Logput.e("DownloadThread error", e);
					exception(e, rowID, null, null);
				}
			}
		}
	});
	
	//--------------------------------------
	// Download
	//--------------------------------------
	
	/**
	 * ダウンロード,ダウンロードスレッド管理
	 * 
	 * @throws InterruptedException
	 */
	private void download() {
		// ダウンロード待ちリスト管理クラスインスタンス取得
		WebBookShelfDlList dlListInstance = WebBookShelfDlList.getInstance();
		// ダウンロード待ちリスト取得
		LinkedList<Long> downloadList = dlListInstance.getDownloadList();
		// Utils.debug("DownloadList size = " + downloadList.size());
		if (!downloadList.isEmpty()) {
			// ダウンロード待ちコンテンツコールバック「ダウンロード待ち」
			callBack_waitForDL(downloadList);
			// ダウンロードするコンテンツRowID取得
			mID = dlListInstance.getDL_rowID();
			if (mID != -1) {
				long rowID = mID;
				Logput.d(">>[" + rowID + "] DownloadList Start---------");
				WebBookShelfDlService.sDlFlag = true;
				// CallBack = [ダウンロード中]
				setDoanlodStatus(rowID, STATUS_DOWNLOADING_WEB, 0, null);
				callback_task(getDownloadStatus());
				if (mDao == null) mDao = new ContentsDao(getApplicationContext());
				mDao.updateColmun(rowID, DL_STATUS, STATUS_DOWNLOADING_WEB);
				updateDB(getDownloadStatus(), getFilePath(), getThumbPath());

				// ダウンロード進捗状態取得しcallback
				while (getDownloadStatus().mStatus == STATUS_DOWNLOADING_WEB) {
					callback_task(getDownloadStatus());
					updateDB(getDownloadStatus(), getFilePath(), getThumbPath());
					// 0.5秒スリープ
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
				}
				// ダウンロード終了後CallBack
				callback_task(getDownloadStatus());
				updateDB(getDownloadStatus(), getFilePath(), getThumbPath());
				if (getDownloadStatus().mStatus == STATUS_DL_COMPLETE_WEB) {
					Logput.d(">>[" + rowID + "] Download finish = " + getFilePath() + "  ------------");
				} else if (getDownloadStatus().mStatus == STATUS_DL_ERROR_WEB) {
					Logput.w("[" + rowID + "] Download error ------------");
				}
			}
		}
	}
	
	// --------------------
	// DB
	// --------------------
	private int beforeSabeProgress = 0;

	/**
	 * ダウンロードステータス - DBアップデート
	 * @param mStatus		DownloadStatus
	 * @param filePath		コンテンツ保存先ファイルパス
	 * @param thumbPath		サムネイル保存先ファイルパス
	 */
	private void updateDB(DownloadStatus dlStatus, String filePath, String thumbPath) {
		long rowID = dlStatus.mRowID;
		int status = dlStatus.mStatus;

		switch (status) {
		case STATUS_DOWNLOADING_WEB: // ● ダウンロード中
			// 1%～100%の間
			int progress = dlStatus.mProgress;
			if (progress > 0 && progress <= 100) {
				if (beforeSabeProgress != progress) {
					// ダウンロード中の進捗状況をDBに保存
					if (mDao == null) mDao = new ContentsDao(getApplicationContext());
					mDao.updateColmun(rowID, DL_PROGRESS, progress);
					beforeSabeProgress = progress;
				}
			}
			break;
		case STATUS_DL_COMPLETE_WEB: // ● ダウンロード完了
			// DB更新
			if (mDao == null) mDao = new ContentsDao(getApplicationContext());
			mDao.updateDLstatus(rowID, STATUS_DL_COMPLETE_WEB, filePath, thumbPath, 100);
			break;
		case STATUS_DL_ERROR_WEB: // ● ダウンロードエラー
			// ダウンロード途中のファイルが残っている場合は削除
			delete(filePath);
			delete(thumbPath);
			// DB：DLステータス初期化
			if (mDao == null) mDao = new ContentsDao(getApplicationContext());
			mDao.updateDLstatus(rowID, STATUS_DL_ERROR_WEB, null, null, 0);
			break;
		}
	}
	
	
	
	// -----------------
	// 例外処理
	// -----------------
	/**
	 * 例外処理
	 * 
	 * @param e			例外
	 * @param rowID		コンテンツID
	 * @param filePath	コンテンツ保存先パス
	 * @param thumbPath サムネイル保存先パス
	 */
	private void exception(Exception e, long rowID, String filePath, String thumbPath) {
		setDoanlodStatus(rowID, STATUS_DL_ERROR_WEB, 0, e.getMessage());
		Logput.e(e.getMessage(), e);
		// 保存されているコンテンツ・サムネイルを削除
		delete(filePath);
		delete(thumbPath);
		setFilePath(null);
		setThumbPath(null);
		mID = -1;
	}
	private void exception(String errorMessage, long rowID, String filePath, String thumbPath) {
		setDoanlodStatus(rowID, STATUS_DL_ERROR_WEB, 0, errorMessage);
		Logput.e(errorMessage);
		// 保存されているコンテンツ・サムネイルを削除
		delete(filePath);
		delete(thumbPath);
		setFilePath(null);
		setThumbPath(null);
		mID = -1;
	}
	
	private void delete(String path){
		try{
			FileUtil.deleteFile(new File(path));
		}catch(Exception e){
		}
	}
	
	
	/*********************************************************
	 * Web書庫からダウンロード 
	 * @author Hamaji
	 *********************************************************/
	/** ServerGetThumbクラス */
	private ServerGetThumb	mGetThumb = null;
	/** BookBeans */
	private BookBeans		mBook;
	
	/**
	 * DLするBookBeansを取得
	 * @return BookBeans
	 */
	public synchronized BookBeans getBook(){
		return mBook;
	}

	/**
	 * Web書庫からダウンロード
	 * @param rowID
	 * @return statusFlag ダウンロードステータスフラグ
	 */
	public int download(long rowID) throws Exception{
		int statusFlag = -1;
		// ダウンロードIDを受取る
		if(rowID != -1){
			// コンテンツダウンロードURLが設定されている場合はそのURLでダウンロード
			if(mDao == null) mDao = new ContentsDao(getApplicationContext());
			mBook = mDao.load(rowID);
			// downloadID取得
			String downloadID = mBook.getDownload_id();
			if(StringUtil.isEmpty(downloadID)){
				// ダウンロードIDが取得できなかった場合はエラー
				return DL_FAIL;
			}
			// DL URLセット
			String dl_url = mBook.getContents_DL_URL();
			if(StringUtil.isEmpty(dl_url)){
				dl_url = setDL_URL(mBook);
			}
			String thumbnailDL_URL = mBook.getThumb_DL_URL();
			if(StringUtil.isEmpty(thumbnailDL_URL)){
				if(mGetThumb == null) mGetThumb = new ServerGetThumb(getApplicationContext());
				thumbnailDL_URL = mGetThumb.getDL_URL(mBook.getContents_id());
			}
			// コンテンツが存在するかチェック
			HttpResponse dl_Resp = httpGet(dl_url);
			if(dl_Resp == null){
				return DL_FAIL;
			}
			// コンテンツDL
			statusFlag = contentsDL(rowID, dl_Resp);
			if(statusFlag == DL_SUCCESS){
				// コンテンツのDLに成功したらサムネイルをDLする(失敗してもとりあえずスルー)
				getThumbnail(rowID, thumbnailDL_URL);
				// UpdateLicenseリクエスト
				int sharedDevice_d = mBook.getSharedDevice_D();
				if(sharedDevice_d != -1 && !WebBookShelfDlService.sStopRequested){
					UpdateLicense updateLicense = new UpdateLicense();
					if(!updateLicense.execute(getApplicationContext(), rowID, downloadID, mBook.getSharedDevice_M(), true)){
						// UpdateLicenseに失敗した場合はＤＬエラーにし、コンテンツ削除
						mBook = mDao.load(rowID);
						return DL_FAIL;
					}
				}
				mBook = mDao.load(rowID);
			}
		}
		return statusFlag;
	}
	
	/**
	 * コンテンツのDLに成功したらサムネイルをDLする(失敗してもとりあえずスルー)
	 * @param rowID
	 * @param contentsPath	キャンセルされた場合削除するコンテンツパス
	 * @param url			サムネイルダウンロードURL
	 */
	private void getThumbnail(long rowID, String url){
		String thumbnailPath = null;
		try{
			Logput.d("[Thumb DL URL]" + url);
			if(mGetThumb == null) mGetThumb = new ServerGetThumb(getApplicationContext());
			thumbnailPath = mGetThumb.getThumbnail(url);
			if(mDao == null) mDao = new ContentsDao(getApplicationContext());
			mDao.updateColmun(rowID, ConstDB.THUMB_PATH, thumbnailPath);
			Logput.d("Contents Donwload Success : " + thumbnailPath);
			setThumbPath(thumbnailPath);
		}catch(NullPointerException e){
		}
		if(WebBookShelfDlService.sStopRequested){
			cancel(rowID, getFilePath(), thumbnailPath);
		}
	}

	/**
	 * ダウンロードURLをセット
	 * @param book
	 * @return ダウンロードURL
	 */
	private String setDL_URL(BookBeans book){
		String contentsID = book.getContents_id();
		String key = contentsID.substring(0, 2);
		
		String url = null;
		if(!StringUtil.isEmpty(Preferences.sS3Host) && !StringUtil.isEmpty(Preferences.sBucketName)){
			// staticにS3HostとBucketNameが保存されている場合はGetAwsInfoリクエストしなくてOK
			url = "http://" + Preferences.sBucketName + "." + Preferences.sS3Host + "/contents/"+ key + "/" + contentsID;
		}else{
			// GetAwsInfoリクエストを行う(S3Host,BucketNameを取得)
			GetAwsInfo getAwsInfo = new GetAwsInfo();
			if(getAwsInfo.execute(getApplicationContext())){
				url = "http://" + Preferences.sBucketName + "." + Preferences.sS3Host + "/contents/"+ key + "/" + contentsID;
			}
		}
		return url;
	}
	
	/**
	 * コンテンツダウンロード
	 * @param ダウンロードURL
	 */
	private int contentsDL(long rowID, HttpResponse resp){
		// DLしてDBに登録
		int statusFlag = -1;
		try{
			statusFlag = getContents(resp, rowID);
			if(statusFlag == DL_SUCCESS){
				// 成功 - メインに表示フラグ更新 DB
				if(mDao == null) mDao = new ContentsDao(getApplicationContext());
				if(mDao.updateColmun(rowID, ConstDB.MAIN_VIEW, ConstDB.MAIN_VIEW_FLAG)){
					// キャンセル不可フラグを立てる
					WebBookShelfDlService.sIsNotCancel = true;
					return DL_SUCCESS;
				}
			}
		}catch(Exception e){
			exception(e, rowID, null, null);
		}
		return statusFlag;
	}

	/**
	 * エラー文字列登録
	 * 指定URLにアクセスし、結果を受取る
	 * @param ダウンロードURL
	 * @return レスポンスコード
	 */
	private HttpResponse httpGet(String dl_URL) throws Exception{
		if(StringUtil.isEmpty(dl_URL)) return null;
	    int responseCode = -1;
	    DefaultHttpClient hClient = null;
	    HttpResponse hResp = null;
	    HttpGet hGet = null;
	    URI url = null;
	    try{
	    	url = new URI(dl_URL);
	    	hClient = new DefaultHttpClient();
		    // HttpGet オブジェクト
		    hGet = new HttpGet();
		    hClient.getParams().setParameter("http.connection.timeout", new Integer(15000));
		    // HttpRequestの結果を受け取る
		    hGet.setURI(url);
		    hResp = hClient.execute(hGet);
		    responseCode = hResp.getStatusLine().getStatusCode();
		    //hClient.getConnectionManager().shutdown();
	    }catch(IllegalStateException e){
	    	throw new IllegalStateException(e.getMessage(), e);
		}catch(URISyntaxException e){
			// URLエラー = 引数：例外を発生させた文字列、例外が発生した理由、例外が発生した位置
			throw new URISyntaxException(dl_URL, e.getReason(), e.getIndex());
	    }catch(ClientProtocolException e){
	    	// HTTPプロトコルのエラー
	    	throw new ClientProtocolException("HTTP protocol error : " + e.getMessage(), e.getCause());
	    }catch(IOException e){
	    	// 接続に問題があった場合
	    	throw new IOException("Connection error : " + e.getMessage());
	    }catch(NullPointerException e){
	    	throw new NullPointerException("NullPointerException");
	    }
	    // リクエストに成功しなかった場合はnullを返す
	    if (responseCode != HttpStatus.SC_OK){
	    	hResp = null;
	    }
		return hResp;
	}

	/**
	 * コンテンツダウンロード
	 * @param HttpResponse
	 * @param コンテンツID
	 * @return コンテンツ保存パス
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private int getContents(HttpResponse dl_Resp,long rowID) throws FileNotFoundException, IOException{
		if(dl_Resp == null) return DL_FAIL;
		// ランダムなファイル名
		String fileName = Utils.getRandomContentFileName(mBook.getType());
		// コンテンツ保存ディレクトリがなければ作成
		String local_dontentsDirPath = setDir(Const.CONTENTS_DIR_NAME);
		// ファイルパスをDBに保存
		String localmContentsPath = local_dontentsDirPath + "/" + fileName;
		if(mDao == null) mDao = new ContentsDao(getApplicationContext());
		if(mDao.updateColmun(rowID,ConstDB.FILE_PATH, localmContentsPath)){
			// DLが終わったらsettings.xmlに保存したファイル名を削除
			int flag = download(rowID, dl_Resp,localmContentsPath);
			if(flag == DL_SUCCESS){
				Logput.d("Contents Donwload Success : " + localmContentsPath);
				return DL_SUCCESS;
			}else if(flag == DL_CANCEL){	// DLキャンセル
				return DL_CANCEL;
			}else{	// DL失敗
				Logput.w("Contents Donwload Fail.");
				FileUtil.deleteFile(new File(localmContentsPath));
				if(mDao.updateColmun(rowID, ConstDB.FILE_PATH, null)){
					Logput.e("[FAIL] DB UPDATE : download file path is null.");
				}
			}
		}else{
			Logput.e("Contents Path Update DB : false ... " + localmContentsPath);
			FileUtil.deleteFile(new File(localmContentsPath));
		}
		return DL_FAIL;
	}

	/**
	 * ファイル保存ディレクトリがなければ作成
	 * @param ディレクトリ名
	 * @return ディレクトリパス
	 */
	private String setDir(String dirName){
		if(Preferences.sExternalStrage == null) Preferences.sExternalStrage = getExternalFilesDir(Const.BOOKEND).getPath();
		String contentsDir = Preferences.sExternalStrage + "/" + dirName;
		try {
			FileUtil.mkdir_p(contentsDir);
			return contentsDir;
		} catch (IOException e) {
			Logput.e(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * ダウンロード
	 * @param リクエストコード
	 * @param ダウンロードファイル保存パス
	 * @return ダウンロード結果ステータスフラグ
	 */
	private int download(long rowID, HttpResponse hResp, String path) throws FileNotFoundException,IOException{
		int statusFlag = DL_FAIL;
		int BUFFER_SIZE = 10240;
		InputStream is = null;
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		File file = new File(path);
    	int fileSize = (int) hResp.getEntity().getContentLength();
    	Logput.v("File size = " + fileSize);
    	try{
	        // BufferdInputStream オブジェクトのインスタンスからデータがなくなるまで、バッファサイズ分ずつ読み込み
	    	is = hResp.getEntity().getContent();
	    	in = new BufferedInputStream(is, BUFFER_SIZE);	// ※第二引数のバッファサイズを指定しない場合 = 8K
	        out = new BufferedOutputStream(new FileOutputStream(file, false), BUFFER_SIZE);
	        byte buf[] = new byte[BUFFER_SIZE];
	        int size = -1;
	        int progress = 0;
	        while(!WebBookShelfDlService.sStopRequested) {
	        	size = in.read(buf, 0 , buf.length);
	        	if (size <= 0){
	        		break;
	        	}
	            out.write(buf, 0, size);
	            progress = (int) (file.length() * 100 / fileSize);
	            if (progress >= 100){
	            	progress = 100;
	            }
	            //Logput.v("size = " + size + " / progress = " + progress);
	            setDoanlodStatus(rowID, STATUS_DOWNLOADING_WEB, progress, null);
	        }
	        
	        if (WebBookShelfDlService.sStopRequested) {
				// ダウンロードキャンセルフラグが立っている場合
				cancel(rowID, path, null);
				statusFlag = DL_CANCEL;
			} else {
				// ファイルパスセット
				setFilePath(path);
				statusFlag = DL_SUCCESS;
			}
    	} catch(IllegalStateException e){
    		throw new IllegalStateException(e.getMessage(), e);
    	}catch(FileNotFoundException e){
    		// ファイルが書き込み用に開くことができない場合
    		throw new FileNotFoundException("FileNotFoundException:" + e.getMessage());
    	}catch(IOException e){
    		throw new IOException(getString(R.string.download_fail) + " : " + e.getMessage());
    	}catch(NullPointerException e){
    		throw new NullPointerException(getString(R.string.download_fail) + ":" + e.getMessage());
		} finally {
    		try{
	    		out.flush();
		        out.close();
		        in.close();
		        is.close();
    		}catch(NullPointerException e){
    		}
		}
    	return statusFlag;
    }
}
