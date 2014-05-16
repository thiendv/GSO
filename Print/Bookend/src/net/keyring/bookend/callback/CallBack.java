package net.keyring.bookend.callback;

import net.keyring.bookend.Logput;
import net.keyring.bookend.R;
import net.keyring.bookend.activity.MainActivity;
import net.keyring.bookend.asynctask.ViewTask;
import net.keyring.bookend.asynctask.ViewTaskParam;
import net.keyring.bookend.bean.BookBeans;
import net.keyring.bookend.constant.Const;
import net.keyring.bookend.constant.ConstDB;
import net.keyring.bookend.constant.ConstList;
import net.keyring.bookend.constant.ConstViewDefault;
import net.keyring.bookend.db.ContentsDao;
import net.keyring.bookend.service.WebBookShelfDlService;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * コールバック処理 - シングルトン
 * 
 * @author Hamaji
 * 
 */
public class CallBack implements Const,ConstViewDefault,ConstList {
	
	/** 唯一のインスタンス */
	private static CallBack	mInstance = new CallBack();
	/** サービスのインターフェイス宣言 - NewDownload */
	private NewDownloadCallback		mNewDlCallback;
	/** サービスのインターフェイス宣言 - WebBookShelfDlCallback */
	private WebBookShelfDlCallback	mWebBookShelfDlCallback;
	/** コンテキスト */
	private Context				mCon;
	/** 表示中のListView */
	private ListView			mListView;
	private MainActivity		mActivity;
	/** progressDialog */
	private ProgressDialog		mProgress;
	/** ContentsDao */
	private ContentsDao			mDao;
	/** Activity flag (デフォルトMainActivity) */
	private int				mActivityFlag = MAIN_ACTIVITY;
	public boolean				mCallBackFlag = false;
	/** プログレスダイヤログの表示スタイルフラグ */
	private boolean			mIsSpnner = false;

	/**
	 * コネクションを張っているときだけunbindするためのフラグ<br>
	 * (ADLを用いてプロセス間通信をしたときに起こるAndroidのバグがあるため)
	 */
	private boolean mBound_web = false;
	/**
	 * コネクションを張っているときだけunbindするためのフラグ<br>
	 * (ADLを用いてプロセス間通信をしたときに起こるAndroidのバグがあるため)
	 */
	private boolean mBound_main = false;

	/**
	 * privateコンストラクタ
	 */
	private CallBack() {
	}

	/**
	 * CallBackインスタンス取得（sinbleton,synchronized）
	 * 
	 * @return DownloadListインスタンス
	 */
	public static CallBack getInstance() {
		return mInstance;
	}

	/**
	 * サービスへのバインド処理
	 * @param con Context
	 */
	public void bind(Context con, int activityFlag, ListView listView, MainActivity activity) {
		this.mCon = con;
		this.mListView = listView;
		this.mActivity = activity;
		if (!mBound_main && activityFlag == MAIN_ACTIVITY) {
			Intent intent = new Intent(NewDownloadCallback.class.getName());
			intent.addCategory(con.getString(R.string.service_category));
			mBound_main = con.bindService(intent, newDownload_service, Context.BIND_AUTO_CREATE);
			Logput.i("newDownload_service bind------- " + mBound_main);
		}else if(!mBound_web && activityFlag == WEB_BOOK_SHELF_ACTIVITY){
			Intent intent = new Intent(WebBookShelfDlCallback.class.getName());
			intent.addCategory(con.getString(R.string.service_category));
			mBound_web = con.bindService(intent, webBookShelf_service, Context.BIND_AUTO_CREATE);
			Logput.i("webBookShelf_service bind------- " + mBound_web);
		}
		this.mActivityFlag = activityFlag;
	}
	
	
	/**
	 * サービスの接続
	 */
	private ServiceConnection newDownload_service = new ServiceConnection() {
		// サービス接続処理
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Logput.i("ComponentName = " + name);
			// サービスのインターフェイスを取得
			mNewDlCallback = NewDownloadCallback.Stub.asInterface(service);
			try {
				mNewDlCallback.registerListener(callback);
				Logput.i("NewDownloadService Connected");
			} catch (RemoteException e) {
				Logput.e(e.getMessage(), e);
			}
		}

		// サービス切断処理
		@Override
		public void onServiceDisconnected(ComponentName name) {
			removeListener();
		}
	};
	
	/**
	 * サービスの接続
	 */
	private ServiceConnection webBookShelf_service = new ServiceConnection() {
		// サービス接続処理
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Logput.i("ComponentName = " + name);
			// サービスのインターフェイスを取得
			mWebBookShelfDlCallback = WebBookShelfDlCallback.Stub.asInterface(service);
			try {
				mWebBookShelfDlCallback.registerListener(callback);
				Logput.i("WebBookShelfDlService Connected");
			} catch (RemoteException e) {
				Logput.e(e.getMessage(), e);
			}
		}

		// サービス切断処理
		@Override
		public void onServiceDisconnected(ComponentName name) {
			removeListener();
		}
	};

	/**
	 * コールバックインターフェイスの実装
	 */
	private ICallbackListener callback = new ICallbackListener.Stub() {
		@Override
		public void updateDownloadStatus(DownloadStatus status)
				throws RemoteException {
			handler.sendMessage(Message.obtain(handler, 0, status));
		}
	};

	/**
	 * 画面更新用のHandler.
	 */
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// ダウンロードステータスを受取る
			DownloadStatus status = (DownloadStatus) msg.obj;
			updateView(status);
		}
	};

	/**
	 * コールバック登録解除処理
	 */
	public void removeListener() {
		if (mWebBookShelfDlCallback != null) {
			try {
				mWebBookShelfDlCallback.removeListener(callback);
			} catch (RemoteException e) {
				Logput.e(e.getMessage(), e);
			}
			// サービスのアンバインド処理
			if (mBound_web) {
				mCon.unbindService(webBookShelf_service);
				Logput.i("webBookShelf_service unbind-------");
				mBound_web = false;
			}
		}
		if(mNewDlCallback != null){
			try {
				mNewDlCallback.removeListener(callback);
			} catch (RemoteException e) {
				Logput.e(e.getMessage(), e);
			}
			if (mBound_main) {
				mCon.unbindService(newDownload_service);
				Logput.i("newDownload_service unbind-------");
				mBound_main = false;
			}
		}
	}
	
	/**
	 * NewDownloadServiceインスタンスの取得
	 * @return NewDownloadServiceインスタンス
	 */
	public synchronized NewDownloadCallback getNewDlServiceInstance(){
		return mNewDlCallback;
	}

	/**
	 * リストのダウンロード状態を更新
	 * @param status		DownloadStatus
	 * @param mListView		リスト
	 */
	public void updateView(DownloadStatus status) {
		if (this.mListView != null) {
			switch(mActivityFlag){
			case MAIN_ACTIVITY:				// MainActivity
				if(status.mStatus <= STATUS_DL_ERROR_WEB){
					callBack_webBookShelf(status, mActivity);
				}else{
					callback_main(status);
				}
				break;
			case WEB_BOOK_SHELF_ACTIVITY:	// WebBookShelfActivity 
				callBack_webBookShelf(status);
				break;
			}
		}
	}
	
	/**
	 * 画面がリストの場合のCallBack処理...MainActivity(新規DL)
	 * @param status DownloadStatus
	 */
	private void callback_main(DownloadStatus status){
		// プログレスバーに表示するメッセージ
		long rowID = status.mRowID;
		String message = status.mError;
		int statusFlag = status.mStatus;
		
		try{
			if(mActivityFlag == MAIN_ACTIVITY && mActivity != null){
				switch(statusFlag){
				case STATUS_COMPLETE_MAIN:
					// 処理終了プログレスバー削除
					stopProgress(false);
					mActivity.main();
					break;
				case STATUS_ERROR_MAIN:
					// ERROR
					stopProgress(false);
					mActivity.dialog(DIALOG_ID_VIEW, message);
					try {
						mNewDlCallback.initDlStatus();
					} catch (RemoteException e) {
						Logput.w("Callback error : initDlStatus()");
					}
					break;
				case STATUS_DOWNLOADING_MAIN:
					// 進捗度表示
					int progress = status.mProgress;
					setProgress_horizontal(message, progress);
					break;
				case STATUS_INVALID_PLATFORM_MAIN:
					// InvalidPlatform=Android
					stopProgress(false);
					TextView statusView = (TextView)mListView.findViewWithTag(mCon.getString(R.string.status_main_tag) + rowID);
					if(statusView != null){
						statusView.setText(mCon.getString(R.string.invalid_platform_error));
					}
					mActivity.dialog(DIALOG_ID_VIEW, message);
					if(rowID != -1){
						if(mDao == null) mDao = new ContentsDao(mCon);
						mDao.updateColmun(rowID, ConstDB.DL_STATUS, STATUS_INIT);
					}
					break;
				case EPUBVIEWER_UPDATE:
					// EpubViewerアップデート
					stopProgress(false);
					sendMarketDialog(mActivity, rowID, message, EPUB_VIEWER);
					break;
				case EPUBVIEWER_MARKET:
					stopProgress(false);
					sendMarketDialog(mActivity, rowID, message, EPUB_VIEWER);
					break;
				case ADOBEREADER_MARKET:
					stopProgress(false);
					sendMarketDialog(mActivity, rowID, message, ADOBE_READER);
					break;
				case STATUS_INIT:
					break;
				case OPEN_CONTENT:
					stopProgress(false);
					openContent(rowID, false);
					break;
				case OPEN_TEMPORARY_CONTENT:
					stopProgress(false);
					openContent(rowID, true);
					break;
				default:
					setProgress_spinner(message);
					break;
				}
			}
		}catch(NullPointerException e){
			Logput.w("Callback main view : NullPointerException.");
		}
	}
	
	/**
	 * コンテンツの閲覧を行う
	 * @param rowID
	 * @param isTemporary
	 */
	private void openContent(long rowID, boolean isTemporary) {
		BookBeans book = new ContentsDao(mCon).load(rowID);
	/*	
		//	MCComicViewerの初期化処理
		//	*これを行わないと、ビューアが起動しない
		if(Utils.isMccomic(book.getType())) {
			MCComicViewerUtil mccomicViewerUtil = MCComicViewerUtil.getInstance();
			mccomicViewerUtil.initialize(mActivity);
		}
	*/	
		//	コンテンツ閲覧処理を開始
		ViewTask viewTask = new ViewTask(mActivity, mActivity);
		viewTask.execute(new ViewTaskParam(book, isTemporary));
	}
	
	/**
	 * 画面がリストの場合のCallBack処理...MainActivity(Web書庫からのDL)
	 * @param status DownloadStatus
	 */
	private void callBack_webBookShelf(DownloadStatus status, MainActivity activity) {
		if(mActivityFlag == MAIN_ACTIVITY && status.mStatus == STATUS_DL_COMPLETE_WEB){
			activity.main();
		}
	}

	/**
	 * 画面がリストの場合のCallBack処理...WebBookShelfActivity
	 * @param status DownloadStatus
	 */
	private void callBack_webBookShelf(DownloadStatus status) {
		BookBeans book = null;
		long rowID = status.mRowID;
		LinearLayout initSet = (LinearLayout) mListView.findViewWithTag(mCon.getString(R.string.dl_init_set_tag) + rowID);
		LinearLayout ingSet = (LinearLayout) mListView.findViewWithTag(mCon.getString(R.string.dl_ing_set_tag) + rowID);
		LinearLayout errorSet = (LinearLayout) mListView.findViewWithTag(mCon.getString(R.string.dl_error_set_tag) + rowID);
		
		Button btn = (Button) mListView.findViewWithTag(rowID);
		TextView statusView = (TextView) mListView.findViewWithTag(mCon.getString(R.string.status_web_tag) + rowID);
		ProgressBar progressBar = (ProgressBar) mListView.findViewWithTag(mCon.getString(R.string.progress_tag) + rowID);
		TextView errorMessage = (TextView) mListView.findViewWithTag((mCon.getString(R.string.dl_error_tag) + rowID));
		TextView sharedDevice = (TextView) mListView.findViewWithTag((mCon.getString(R.string.shareddevice_tag) + rowID));
		
		if (initSet != null && ingSet != null && errorSet != null) {
			switch (status.mStatus) {
			case STATUS_WAIT_FOR_DL_WEB: // ● ダウンロード待ち
				initSet.setVisibility(View.GONE);
				ingSet.setVisibility(View.VISIBLE);
				errorSet.setVisibility(View.GONE);
				break;
			case STATUS_DOWNLOADING_WEB: // ● ダウンロード中
				initSet.setVisibility(View.GONE);
				ingSet.setVisibility(View.VISIBLE);
				errorSet.setVisibility(View.GONE);
				progressBar.setProgress(status.mProgress);
				break;
			case STATUS_DL_COMPLETE_WEB: // ● ダウンロード完了
				initSet.setVisibility(View.VISIBLE);
				ingSet.setVisibility(View.GONE);
				errorSet.setVisibility(View.GONE);
				btn.setVisibility(View.GONE);
				statusView.setVisibility(View.VISIBLE);
				statusView.setText(mCon.getString(R.string.download_finish));
				if(sharedDevice != null){
					if(mDao == null) mDao = new ContentsDao(mCon);
					book = mDao.load(rowID);
					sharedDevice.setText(book.getSharedDevice_M() + "/" + book.getSharedDevice_D());
				}
				break;
			case STATUS_DL_ERROR_WEB: // ● ダウンロードエラー
				initSet.setVisibility(View.GONE);
				ingSet.setVisibility(View.GONE);
				errorSet.setVisibility(View.VISIBLE);
				errorMessage.setText(mCon.getString(R.string.download_fail));
				// エラーメッセージをトースト表示
				String errorText = status.mError;
				if(errorText == null){
					errorText = mCon.getString(R.string.download_fail);
				}
				Toast.makeText(mCon, errorText, Toast.LENGTH_SHORT).show();
				break;
			default:
				initSet.setVisibility(View.VISIBLE);
				ingSet.setVisibility(View.GONE);
				errorSet.setVisibility(View.GONE);
				btn.setVisibility(View.VISIBLE);
				break;
			}
			// キャンセルフラグが立っている場合
			if(WebBookShelfDlService.sStopRequested){
				initSet.setVisibility(View.VISIBLE);
				ingSet.setVisibility(View.GONE);
				errorSet.setVisibility(View.GONE);
				btn.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * Web書庫ダウンロードリクエスト
	 * 
	 * @param movieID
	 *            動画ID
	 * @throws RemoteException
	 */
	public void downloadRequest_web(long movieID) throws RemoteException {
		mWebBookShelfDlCallback.startDownload_web(callback, movieID);
	}
	
	/**
	 * GooglePlayerへ遷移
	 */
	public void sendMarketDialog(MainActivity activity, long rowID, String message, final String marketID){
		activity.progress_stop();
		activity.main();
		AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
		alert.setTitle("Please install");
		alert.setMessage(message);
		alert.setPositiveButton(mCon.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	Logput.v("SEND APP MARKET : ID = " + marketID);
            	//Preferences.sDefaultCheck = false;
            	Intent market = new Intent(Intent.ACTION_VIEW);
            	market.setData(Uri.parse("market://details?id=" + marketID));
            	try{
            		mActivity.startActivity(market);
            	}catch(ActivityNotFoundException e){
            		Logput.w(e.getMessage(), e);
            		mActivity.dialog(DIALOG_ID_VIEW, mCon.getString(R.string.error_send_market));
            	}
            }
        });
		alert.setNegativeButton(mCon.getString(R.string.cancel), null);
		alert.show();
	}
	
	/**
	 * プログレスバー表示...STYLE_HORIZONTAL(進捗表示)
	 * @param message
	 * @param progress
	 */
	public void setProgress_horizontal(String message, int progress){
		mIsSpnner = false;
		if(mProgress == null || !mProgress.isShowing()){
			mProgress = new ProgressDialog(this.mActivity);
			mProgress.setIcon(R.drawable.icon);
			// ProgressDialog の確定（false）／不確定（true）を設定
			mProgress.setIndeterminate(false);
			// スタイルを設定
			mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgress.setMax(100);
			mProgress.setMessage(message);
			mProgress.setProgress(progress);
			mProgress.setCancelable(false);
			mProgress.show();
		}else{
			mProgress.setProgress(progress);
		}
	}
	
	/**
	 * プログレスバー表示...STYLE_SPINNER
	 * @param isSpinner
	 * @param message
	 * @param progress
	 */
	public void setProgress_spinner(String message){
		if(!mIsSpnner){
			mIsSpnner = true;
			stopProgress(false);
		}
		if(mProgress == null || !mProgress.isShowing()){
			mProgress = new ProgressDialog(this.mActivity);
			mProgress.setIcon(R.drawable.icon);
			// ProgressDialog の確定（false）／不確定（true）を設定
			mProgress.setIndeterminate(true);
			// スタイルを設定
			mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgress.setMessage(message);
			mProgress.setCancelable(false);
			mProgress.show();
		}else{
			mProgress.setMessage(message);
		}
	}
	
	public void stopProgress(boolean isActivityInit) {
		if (mProgress != null) {
			mProgress.cancel();
			mProgress.dismiss();
			mProgress = null;
		}
		if(isActivityInit){
			mActivity = null;
		}
	}
	
	public void deleteDB(long rowID){
		if(rowID == -1){
			return;
		}
		if(mDao == null){
			mDao = new ContentsDao(mCon);
		}
		mDao.delete(rowID);
	}
}
