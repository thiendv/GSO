package net.keyring.bookend.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;

import net.keyring.bookend.Logput;
import net.keyring.bookend.R;
import net.keyring.bookend.action.MainListAction;
import net.keyring.bookend.action.NewDownloadAction;
import net.keyring.bookend.bean.BookBeans;
import net.keyring.bookend.callback.DownloadStatus;
import net.keyring.bookend.callback.ICallbackListener;
import net.keyring.bookend.callback.NewDownloadCallback;
import net.keyring.bookend.constant.Const;
import net.keyring.bookend.constant.ConstDB;
import net.keyring.bookend.constant.ConstQuery;
import net.keyring.bookend.db.ContentsDao;
import net.keyring.bookend.request.GetContentsInfo;
import net.keyring.bookend.request.SetContents;
import net.keyring.bookend.util.FileUtil;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
/**
 * 新規ダウンロードサービス
 * @author Hamaji
 *
 */
public class NewDownloadService extends Service implements ConstDB, Const {
	
	/** DownloadURLから取得したQueryリスト */
	public static Map<String,String>	sQueryList;
	/** ダウンロード中かどうかフラグ */
	public static boolean				sDlFlag = false;
	
	/** Daoクラス */
	private ContentsDao				mDao;
	/** NewDownloadActionクラス */
	private NewDownloadAction		mNewDlAction = null;
	/** ダウンロード中コンテンツステータス */
	private DownloadStatus			mDL_contents_status = null;
	/** DLしたコンテンツパス */
	private String					mFilePath = null;
	/** DLしたサムネイルパス */
	private String					mThumbPath = null;
	/** MainListAction */
	private MainListAction			mMainListAction = null;
	/** ダウンロード進捗度 */
	private int					mBeforeSabeProgress = 0;
	/** DL後そのままビューワで閲覧するかどうかフラグ ※省略はfalse */
	private boolean				mIsOpen = false;
	/** DLコンテンツを閲覧後削除し、書庫にも残らないようにするフラグ */
	private boolean				mTemporary = false;
	/** コールバックインターフェイス設定 */
	private RemoteCallbackList<ICallbackListener> callbackList = new RemoteCallbackList<ICallbackListener>();
	
	@Override
	public void onCreate() {
		super.onCreate();
		Logput.d("------- NewDownloadService onCreate -------");
		// ダウンロードスレッド開始
		downloadThread.start();
		Logput.d(">>DownloadThread Start");
	}

	@Override
	public IBinder onBind(Intent intent) {
		Logput.d("NewDownloadService onBind : intent.action = " + intent.getAction());
		if (NewDownloadCallback.class.getName().equals(intent.getAction())) {
			Logput.v("--- NewDownloadService onBind ---");
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
		Logput.d("--- NewDownloadService onRebind ---");
	}

	/**
	 * バインドが解除されたときに実行される処理
	 * 
	 * @param intent unbindService()渡されたIntentオブジェクト
	 */
	@Override
	public boolean onUnbind(Intent intent) {
		Logput.d("--- NewDownloadService onUnbind ---");
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Logput.d("------- NewDownloadService onDestroy -------");
		callbackList.kill();
	}
	
	// ----------------------------------------
	// setter/getter （synchronized）
	// ----------------------------------------
	
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
	 */
	public synchronized void setDownlodStatus(long rowID, int status, int progress, String message) {
		this.mDL_contents_status = new DownloadStatus(rowID, status, progress, message);
		callback_task(getDownloadStatus());
		if(rowID != -1){
			updateDB(getDownloadStatus());
		}
	}
	
	// -------------------------------------
	// CallBack
	// -------------------------------------
	
	/**
	 * ICallbackServiceの実装
	 */
	private final NewDownloadCallback.Stub serciceIf = new NewDownloadCallback.Stub() {
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
		public void callback() throws RemoteException{
			DownloadStatus status = getDownloadStatus();
			if(status != null){
				Logput.v("callback > [" + status.mRowID + "] status = " + status.mStatus);
				callback_task(status);
			}
		}
		@Override
		public void setProgress(int progress){
			if (mBeforeSabeProgress != progress) {
				setDownlodStatus(-1, STATUS_DOWNLOADING_MAIN, progress, getString(R.string.downloading));
				mBeforeSabeProgress = progress;
			}
		}
		/**
		* DownloadStatusをnullに,ダウンロードフラグにfalseをセット
		*/
		@Override
		public void initDlStatus(){
			mDL_contents_status = null;
			sDlFlag = false;
		}
	};
	
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
	

	//-------------------------
	// Download thread
	//-------------------------
	
	/**
	 * ダウンロード管理メインスレッド(CallBackは全てこのスレッドで行う)
	 */
	Thread downloadThread = new Thread(new Runnable() {
		@Override
		public void run() {
			Logput.d("DownloadService Main Thread Run!");
			DownloadStatus status = null;
			while (true) {
				if (sQueryList != null && !sDlFlag) {
					sDlFlag = true;
					mIsOpen = false;
					mTemporary = false;
					// ダウンロード中のコンテンツがない場合はダウンロード開始
					download(sQueryList);
					// DL中フラグを初期化
					sQueryList = null;
					status = getDownloadStatus();
					if(status != null){
						if(status.mStatus != STATUS_ERROR_MAIN){
							mDL_contents_status = null;
							sDlFlag = false;
						}
					}
				}
				try {
					// 0.5秒スリープ
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
			}
		}
	});
	
	//-------------------------
	// Download実行
	//-------------------------
	/**
	 * 新規ダウンロード実行
	 */
	public void download(Map<String,String> queryList){
		Logput.v("New Contents DL Start --------------");
		BookBeans book = null;
		setDownlodStatus(-1, STATUS_DOWNLOADING_MAIN, 0, getString(R.string.downloading));
		
		mNewDlAction = new NewDownloadAction(getApplicationContext());
		
		// 【 ダウンロード 】
		try{
			if(dlExecute(queryList)){
				return;
			}
		}catch (Exception e) {
			Logput.e(e.getMessage(), e);
			setDownlodStatus(-1, STATUS_ERROR_MAIN, 0, getString(R.string.download_error) + "\n\n" + e.getMessage());
			return;
		}
		
		// 【 登録処理 】
		setDownlodStatus(-1, STATUS_REGIST_MAIN, 0, getString(R.string.registration));
		book = mNewDlAction.setDB();
		
		if(book == null){
			setDownlodStatus(-1, STATUS_ERROR_MAIN, 0, getString(R.string.db_error));
			return;
		}
		SetContents setContents = setContentsReq();
		if(setContents != null){
			long rowID = book.getRowId();
			if(rowID != -1){
				deleteDB(rowID);
			}
			setDownlodStatus(rowID, STATUS_ERROR_MAIN, 0, setContents.getmDescription());
			return;
		}
		GetContentsInfo getContentsInfo = mNewDlAction.getContentsInfo(book.getRowId(), book.getContents_id());
		if(getContentsInfo.getStatus().equals("11100")){
			// GetContentsInfoリクエスト = OK
			book = getContentsInfo.getBookBeans();
		}else{
			// エラー内容詳細を取得表示:エラーでもそのまま処理を続ける
			Logput.i("New DL error : " + getContentsInfo.getErrorMessage());
		}
		
		// 【 InvalidPlatform = Android ? 】
		if(!mNewDlAction.check_InvalidPlatform(book.getInvalidPlatform())){
			// GetLicense
			mNewDlAction.getLicense(book.getDownload_id());
			Logput.i("INVALID_PLATFORM_NG");
			setDownlodStatus(book.getRowId(), STATUS_INVALID_PLATFORM_MAIN, 0, getString(R.string.invalid_platform_error));
		}
		
		Logput.v("--------- New Contents DL : Success ---------");
		
		if(mIsOpen || mTemporary){
			Logput.d(">Viewer open");
			// Openフラグがtrueの場合,又はオンライン閲覧の場合は続けてビューワ起動
			if(mMainListAction == null) mMainListAction = new MainListAction();
			
			//	## TODO: パラメータチェックがなぜかここで行われているのでViewTask内で行うよう修正が必要
			if(mMainListAction.check_InvalidPlatform(book.getInvalidPlatform())){
				// Viewerで閲覧開始(krpdfの場合はレイヤー削除も)
				int status = mTemporary ? OPEN_TEMPORARY_CONTENT : OPEN_CONTENT;
				setDownlodStatus(book.getRowId(), status, 0, "");
			}
		}else{
			setDownlodStatus(book.getRowId(), STATUS_COMPLETE_MAIN, 0, "");
		}
	}
	
	/**
	 * ダウンロード処理
	 * @return ダウンロード処理ステータス
	 * @throws FileNotFoundException 
	 * @throws NullPointerException 
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * @throws ClientProtocolException 
	 */
	private boolean dlExecute(Map<String, String> queryList) throws FileNotFoundException, ClientProtocolException, NullPointerException, URISyntaxException, IOException{
		// 新規ダウンロード前セット・チェック
		if(mNewDlAction.preparation(queryList)){
			// そのまま閲覧するかどうかフラグをチェック
			mIsOpen = mNewDlAction.getFlag(queryList, ConstQuery.OPEN);
			// Temporaryフラグチェック
			mTemporary = mNewDlAction.getFlag(queryList, ConstQuery.TEMPORARY);
			
			// コンテンツDL
			mFilePath = mNewDlAction.contentsDL(true);
				if(mFilePath == null){
					return true;
				}
			String errorMessage = mNewDlAction.authkey_check(mFilePath);
			if(errorMessage == null){
				// サムネイルDL
				mThumbPath = mNewDlAction.contentsDL(false);
				// 閲覧有効期限が設定されている場合は期限時刻を計算
				if(!mNewDlAction.setExpiryDate()){
					error(-1, getString(R.string.expiry_date_error));
				}
			}else{
				error(-1, errorMessage);
				return true;
			}
		}else{
			// ファイルタイプNG
			error(-1, getString(R.string.filetype_error));
			return true;
		}
		return false;
	}
	
	/**
	 * SetContentsリクエスト処理
	 * @return SetContents
	 */
	private SetContents setContentsReq(){
		SetContents setContents = null;
		ArrayList<NameValuePair> setParams = mNewDlAction.setParams();
		if(setParams != null){
			// SetContentsリクエスト
			setContents = new SetContents();
			if(setContents.execute(getApplicationContext(), setParams)){
				return null;
			}
		}
		return setContents;
	}
	
	//--------------------------
	// DL ERROR 処理
	//--------------------------
	
	/**
	 * 新規ダウンロード中のエラーをダイヤログで通知,コンテンツ・DB情報も削除
	 * @param errorMessage 表示エラーメッセージ
	 */
	private void error(long rowID, String errorMessage){
		Logput.i("New DL error : " + errorMessage);
		// 登録エラー:DL済だった場合は削除
		deleteContents(mFilePath, mThumbPath);
		deleteDB(rowID);
		setDownlodStatus(rowID, STATUS_ERROR_MAIN, 0, errorMessage);
	}
	
	/**
	 * コンテンツとサムネイル削除
	 * @param contentsPath
	 * @param thumbPath
	 */
	private void deleteContents(String filePath, String thumbPath){
		if(filePath != null){
			FileUtil.deleteFile(new File(filePath));
		}
		if(thumbPath != null){
			FileUtil.deleteFile(new File(thumbPath));
		}
	}
	/**
	 * contentsテーブルから登録削除
	 */
	private void deleteDB(long rowID){
		if(rowID == -1) return;
		ContentsDao dao = new ContentsDao(getApplicationContext());
		dao.delete(rowID);
	}
	
	//----------------------------
	// DL STATUS - DB UPDATE
	//------------------------------
	/**
	 * ダウンロードステータス - DBアップデート
	 * @param mStatus		DownloadStatus
	 * @param filePath		コンテンツ保存先ファイルパス
	 * @param thumbPath		サムネイル保存先ファイルパス
	 */
	private void updateDB(DownloadStatus dlStatus) {
		long rowID = dlStatus.mRowID;
		int status = dlStatus.mStatus;

		if(rowID != -1){
			return;
		}else if(status == STATUS_COMPLETE_MAIN){
			if (mDao == null) mDao = new ContentsDao(getApplicationContext());
			mDao.updateColmun(rowID, DL_STATUS, STATUS_INIT);
			mDao.updateColmun(rowID, DL_PROGRESS, 0);
//		}else if(status == STATUS_DOWNLOADING_WEB){
//			// TODO:DB未登録の為今現在ここにはこない
//			// 1%～100%の間
//			int progress = dlStatus.mProgress;
//			if (progress > 0 && progress <= 100) {
//				// ダウンロード中の進捗状況をDBに保存
//				if (mDao == null) mDao = new ContentsDao(getApplicationContext());
//				mDao.updateColmun(rowID, DL_PROGRESS, progress);
//			}
		}else{
			if (mDao == null) mDao = new ContentsDao(getApplicationContext());
			mDao.updateColmun(rowID, DL_STATUS, status);
		}
	}
	
}
