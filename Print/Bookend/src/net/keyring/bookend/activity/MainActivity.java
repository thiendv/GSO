package net.keyring.bookend.activity;

import java.util.ArrayList;
import java.util.Map;

import net.keyring.bookend.Consts.PurchaseState;
import net.keyring.bookend.Consts.ResponseCode;
import net.keyring.bookend.Logput;
import net.keyring.bookend.NewContentsDlDialog;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.R;
import net.keyring.bookend.action.DialogAction;
import net.keyring.bookend.action.MainListAction;
import net.keyring.bookend.adapter.MainListAdapter;
import net.keyring.bookend.asynctask.SendWebBookShelfTask.RegistConfirmListener;
import net.keyring.bookend.asynctask.StartUpCheckTask;
import net.keyring.bookend.asynctask.StartUpCheckTask.StartUpCheckListener;
import net.keyring.bookend.asynctask.ViewTask;
import net.keyring.bookend.asynctask.ViewTask.ViewListener;
import net.keyring.bookend.asynctask.ViewTaskParam;
import net.keyring.bookend.bean.BookBeans;
import net.keyring.bookend.billing.PurchaseObserver;
import net.keyring.bookend.billing.ResponseHandler;
import net.keyring.bookend.callback.CallBack;
import net.keyring.bookend.callback.NewDownloadCallback;
import net.keyring.bookend.constant.Const;
import net.keyring.bookend.constant.ConstDB;
import net.keyring.bookend.constant.ConstList;
import net.keyring.bookend.constant.ConstRegist;
import net.keyring.bookend.constant.ConstStartUp;
import net.keyring.bookend.constant.ConstViewDefault;
import net.keyring.bookend.service.BillingService;
import net.keyring.bookend.service.BillingService.RequestPurchase;
import net.keyring.bookend.service.BillingService.RestoreTransactions;
import net.keyring.bookend.service.NewDownloadService;
import net.keyring.bookend.service.ProcessCheckService;
import net.keyring.bookend.util.StringUtil;
import net.keyring.bookend.util.Utils;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * 最初に表示されるメイン画面 ※ 必ずCheckProgressActivityを通る
 * 
 * @author Hamaji
 * 
 */
public class MainActivity extends BookendActivity implements Const, ConstList, ConstRegist,
		ConstStartUp, ConstViewDefault, RegistConfirmListener, StartUpCheckListener, ViewListener {
	
	/** アダプター */
	private MainListAdapter				mAdapter;
	/** ListActionクラス */
	private MainListAction				mMainListAction;
	/** getIntent */
	private Intent						mGetIntent;
	/** MarketBillingServiceに接続して(binding)、アプリに代わって Android Market にメッセージを送るサービス */
    private BillingService				mBillingService;
    /** アンドロイドマーケットからコールバックを受取りUIを更新するクラス */
    private DungeonsPurchaseObserver	mDungeonsPurchaseObserver;
    /** ハンドラー */
    private Handler						mHandler;
	/** 表示データリストMap */
	private ArrayList<Map<String, Object>> mData;
	/** リストビュー */
	private ListView					mMainList;
	/** download情報マップ */
	private Map<String, String>			mQueryList;
	/** NewDownloadService */
	private NewDownloadCallback			mNewDlService;
	/** CallBackインスタンス */
	private CallBack					mCallback_instance = CallBack.getInstance();
	/** 画面下部のツールバー */
	private MenuBarSubActivity			mMenuBarSubActivity = new MenuBarSubActivity();
	
	/** 表示項目：サムネイルURL・タイトル・著者・RowID */
	private String[] mFromTemplate = { THUMB_URL, TITLE, AUTHOR, ID, STATUS };
	private int[] mToTemplate = { R.id.thumbnail, R.id.title, R.id.author, R.id.status };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logput.v("----< MainActivity:onCreate >----");
		setContentView(R.layout.start);
		//	アプリケーション領域にあるファイルを削除する
		//	* アプリケーションが起動してから1回だけ行う
		if(Preferences.sDeletePrivateFiles == false) {
			Preferences.sDeletePrivateFiles = true;
			deleteAppDirContents();
		}
	/*	
		//	MCComicViewerの初期化
		MCComicViewerUtil mccomicViewerUtil = MCComicViewerUtil.getInstance();
		mccomicViewerUtil.initialize(this);
	*/
		Logput.v("Environment.getExternalStorageDirectory() = " + Environment.getExternalStorageDirectory());
	}

	@Override
	public void onRestart(){
		super.onRestart();
		Logput.v("----< MainActivity:onRestart >----");
		setContentView(R.layout.start);
	}
	
	@Override
	public void onStart(){
		super.onStart();
		Logput.v("----< MainActivity:onStart >----");
		// Splash画面の背景に白を設定する場合
		try{
			if(getString(R.string.splash_background_white).equals("1")){
				RelativeLayout layout = (RelativeLayout)findViewById(R.id.start);
				layout.setBackgroundColor(Color.WHITE);
			}
		}catch(NullPointerException e){
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Logput.v("----< MainActivity:onResume >----");
		// 購入済みProductID取得
    	//restoreDatabase();
    	
		if (!Preferences.sDefaultCheck) {
			Logput.v("----------- " + getString(R.string.app_name) + " start -----------");
			// 初期設定を行ってない場合はStartUpCheck
			Logput.v("default check : none");
			// 起動時チェック
			// ユーザーID取得
			if (mPref == null) mPref = new Preferences(getApplicationContext());
			mPref.getUserID();
			if (!Utils.isConnected(getApplicationContext()) && StringUtil.isEmpty(Preferences.sUserID)) {
				// 初起動時オフラインの場合はエラーダイアログ表示
				dialog(DIALOG_ID_ERROR, getString(R.string.first_activation_offline));
			} else {
				//dialog(DIALOG_ID_PROGRESS, getString(R.string.starting));
				StartUpCheckTask startUpCheckTask = new StartUpCheckTask(this);
				startUpCheckTask.execute(getApplicationContext());
			}
		} else {
			main();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		Logput.v("----< MainActivity:onStop >----");
		mData = null;
		mAdapter = null;
	}
	
	@Override
	public void onDestroy(){
		try {
			super.onDestroy();
			Logput.v("----< MainActivity:onDestroy >----");
		
			if(mBillingService != null){
				mBillingService.unbind();
			}
		/*	
			//	ProcessCheckServiceに終了通知を送る
			Intent intent = new Intent(this, ProcessCheckService.class);
			intent.putExtra(FINISH_PROCESS, "true");
			startService(intent);
		*/	
		}
		catch(Throwable e) {
			Logput.e(e.getMessage(), e);
		}
	}
	
	/**
	 * Activityがバックグラウンドに行くときに呼ばれる
	 */
	@Override
	protected void onUserLeaveHint(){
		super.onUserLeaveHint();
		Logput.v("----< MainActivity:onUserLeaveHint >----");
		// Activityがバックグラウンドに行くときはプログレスバーを削除する
		mCallback_instance.stopProgress(true);
		Logput.i("onUserLeaveHint progress stop.");
	}
	
	/**
	 * 既にMainActivityが起動している状態で他のタスクからMainActivityが起動された場合に呼び出される
	 * *主にブラウザからURLで起動された場合に使用する
	 * *ここで新しいIntentをセットしない場合、getIntent()で取得できるIntentが初回起動時のままになってしまいます
	 */
	@Override
	protected void onNewIntent (Intent intent){
		Logput.v("----< MainActivity:onNewIntent >----");
		Intent oldIntent = getIntent();
		Logput.v("old intent = " + oldIntent);
		Logput.v("new intent = " + intent);
		setIntent(intent);
	}

	/**
	 * バックグラウンドでの初期起動時チェックからの戻り値
	 * 
	 * @param status
	 */
	@Override
	public void result_startUpCheck(Map<String, Object> startUpCheckList) {
		//progress_stop();
		try{
			int status = (Integer) startUpCheckList.get(STATUS_KEY);
			//	 初期起動時チェックOKまたはバージョンチェックのエラーの場合は通常通り起動する
			if(status == ConstStartUp.CHECK_OK || status == ConstStartUp.CHECK_ERROR_VERSION_CHECK) {
				// デフォルトチェック済
				Preferences.sDefaultCheck = true;
				// メインリスト表示
				main();
			}
			else if(status == ConstStartUp.CHECK_ERROR_CHECKACTIVATION) {
				//	CheckActivationエラーの場合はオフラインとして起動する
				//	*Utils.isConnected()の処理を変更するためPreferences.sOfflineフラグをセットする
				Preferences.sOffline = true;
				Preferences.sDefaultCheck = true;
				main();
			}
			else {
				//	それ以外の場合はエラー
				check_startUp(status, startUpCheckList);			
			}
		} catch(NullPointerException e) {
			Preferences.sDefaultCheck = false;
			Logput.w("StartUp Check : ERROR", e);
			dialog(DIALOG_ID_ERROR, "StartUp Check : ERROR");
		}
	}

	/**
	 * 初期起動時チェックエラーダイアログ処理
	 * 
	 * @param status ステータスコード
	 * @param startUpCheckList
	 */
	private void check_startUp(int status, Map<String, Object> startUpCheckList) {
		String error_message = (String) startUpCheckList.get(DIALOG_MESSAGE);
		if (status == CHECK_VERSIONUP_FORCE) {
			// 強制アップデート
			String url = (String) startUpCheckList.get(UPDATE_URL);
			if (mDialogAction == null) mDialogAction = new DialogAction();
			mDialogAction.setVerUP_URL(url);
			dialog(DIALOG_ID_UPDATE_FORCE, error_message);
		} else if (status == CHECK_VERSIONUP_USUALLY) {
			// 通常アップデート
			String url = (String) startUpCheckList.get(UPDATE_URL);
			if (mDialogAction == null) mDialogAction = new DialogAction();
			mDialogAction.setVerUP_URL(url);
			dialog(DIALOG_ID_UPDATE_UTILL, error_message);
		} else if (status == CHECK_REQUEST_ACTIVATION) {
			dialog(DIALOG_ID_REACTIVATION, error_message);
		} else {
			dialog(DIALOG_ID_ERROR, error_message);
		}
	}
	
	/**
	 * アプリケーション領域にあるファイルを削除
	 * ※残しておきたいファイルができた場合は削除ファイル名にTEMPORARY_FILEを指定する
	 */
	private void deleteAppDirContents(){
		for(String file : fileList()){
			deleteFile(file);
			Logput.d("Delete contents : " + file);
		}
	}
	
	/**
	 * 起動の度チェック・表示する
	 */
	public void main() {
		//	インテントを取得
		mGetIntent = getIntent();
		Logput.v("intent = " + mGetIntent);
		Logput.v("intent flag = " + String.format("0x%08x", mGetIntent.getFlags()));
		
		Logput.v("----< MainActivity:main >----");
		// 不正キャプチャを削除した場合はトーストを表示
		if (Preferences.sDeleteCapture) {
			Preferences.sDeleteCapture = false;
			Toast.makeText(this, this.getString(R.string.delete_capture), Toast.LENGTH_SHORT).show();
		}
		// デバックモードチェック
		if (Utils.isDebugMode(this.getApplicationContext())) {
			// デバックモード有効：NG
			dialog(DIALOG_ID_ERROR, getString(R.string.debug_mode_message));
		} else {
			//	二重に処理しないよう、処理済みフラグを付けておく
			boolean isProcessed = mGetIntent.getBooleanExtra(IS_PROCESSED, false);
			Logput.v("isProcessed = " + isProcessed);
			
			//	FLAG_ACTIVITY_LAUNCHED_FROM_HISTORYが立っている場合はURLを処理しない
			//	*URLを処理するのはブラウザから起動された場合のみなのでHistoryから起動された場合は無視する
			if((mGetIntent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0) {
				Logput.v("Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY found.");
				isProcessed = true;
			}
			
			// ダウンロードURLがあればIntentから取得
			Uri getURL = mGetIntent.getData();
			int schemeFlag = Utils.isDownloadLink(getURL, getString(R.string.custom_name));
			if (!isProcessed && schemeFlag == SCHEME_BEHTTP) {
				download(getURL);
				mGetIntent.putExtra(IS_PROCESSED, true);
			} else if (!isProcessed && schemeFlag == SCHEME_BEINFO) {
				// Webページへ
				if (mMainListAction == null) mMainListAction = new MainListAction();
				if (!mMainListAction.toWebInfo(getApplicationContext(), getURL)) {
					// URL取得エラーの場合はそのままメインページ表示
					listView(setSort());
				}
				mGetIntent.putExtra(IS_PROCESSED, true);
			} else {
				// メインコンテンツリスト表示
				listView(setSort());
			}
			// STGモードの場合は少しの間表示
			if (Preferences.sMode) {
				Toast.makeText(this, "STG MODE", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * ソート方法取得
	 * 
	 * @return
	 */
	private int setSort() {
		mPref = new Preferences(this.getApplicationContext());
		int sort = mPref.getSort();
		if (sort == -1) {
			// ソート方法がセットされていない場合はダウンロード日時降順
			mPref.setSort(net.keyring.bookend.constant.ConstDB.DESCEND_DATE);
			sort = Preferences.sSort_main;
		}
		return sort;
	}

	/**
	 * DLリンクから来た場合の処理
	 * 
	 * @param downloadURL
	 */
	private void download(Uri downloadURL) {
		// Queryチェック
		if (mMainListAction == null) mMainListAction = new MainListAction();
		Map<String, String> queryList = mMainListAction.getContentsDetail(
				this.getApplicationContext(), downloadURL);
		listView(setSort());
		if (queryList == null) {
			dialog(DIALOG_ID_VIEW, getString(R.string.already_downloaded_error));
		} else if (queryList.size() <= 0) {
			dialog(DIALOG_ID_VIEW, getString(R.string.already_downloaded_error));
		} else {
			mHandler = new Handler();
	        mDungeonsPurchaseObserver = new DungeonsPurchaseObserver(mHandler);
	        if(mBillingService == null){
	        	mBillingService = new BillingService();
	        	mBillingService.setContext(this);
	        	ResponseHandler.register(mDungeonsPurchaseObserver);
	        }
	        
			this.mQueryList = queryList;
			// ダウンロード確認ダイアログ mCallback_instance
			new NewContentsDlDialog(MainActivity.this, mBillingService, queryList).show();
		}
	}

	/**
	 * ViewTask戻り値
	 * @param 画面を更新するかどうかフラグ
	 */
	@Override
	public void result_view(String errorMessage){
		removeDialog(DIALOG_ID_PROGRESS);
		if(errorMessage != null){
			dialog(DIALOG_ID_VIEW, errorMessage);
		}
		// list更新
		listView(Preferences.sSort_main);
	}

	/**
	 * リストアクション
	 */
	private void listView(int sort) {
		setContentView(R.layout.main);
		mLayout = (LinearLayout) findViewById(R.id.main_bookshelf);
		mMainList = (ListView) mLayout.findViewById(R.id.main_list);
		
		//	ツールバーの初期化処理を行う
		mMenuBarSubActivity.init(this, MenuBarSubActivity.ACTIVITY_MAIN);
		mMenuBarSubActivity.setupEventListener();
		mMenuBarSubActivity.updateButtonState(this);
		mMenuBarSubActivity.setRegistConfirmListener(this);
		
		// コンテンツ情報取得
		if (mMainListAction == null) mMainListAction = new MainListAction();
		mData = mMainListAction.getBooksData(this.getApplicationContext());
		mAdapter = new MainListAdapter(this, mData, R.layout.main_list, mFromTemplate, mToTemplate);
		mMainList = mMainListAction.setMainList(mMainList, mData, mAdapter);
		// リストビューのアイテムがクリックされた時に呼び出されるコールバックリスナーを登録します
		mMainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int book_status = mMainListAction.listClickAction(
						getApplicationContext(), mMainList, mData, mAdapter, position);
				viewCheck(book_status, mMainListAction.getBook());
			}
		});
		// リストビューのアイテムが長押しクリックされた時のハンドラ
		mMainList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent,
					View view, int position, long id) {
				AlertDialog.Builder dialog = mMainListAction
						.setListLongClickDialog(MainActivity.this, mData, mAdapter, position);
				if (dialog == null) {
					Toast.makeText(getApplicationContext(), "Contents Detail ... None.", Toast.LENGTH_SHORT).show();
				} else {
					dialog.setPositiveButton(
							getString(R.string.delete),
							new OnClickListener() {
								// 削除ボタン
								@Override
								public void onClick(DialogInterface dialog, int which) {
									if (!mMainListAction.delete(getApplicationContext())) {
										Toast.makeText(MainActivity.this, "Delete : fail", Toast.LENGTH_SHORT).show();
									}
									// list更新
									listView(Preferences.sSort_main);
								}
							});
					// 閉じるボタン
					dialog.setNegativeButton(getString(R.string.close), null);
					dialog.show();
				}
				return true;
			}
		});
		// リストがセットできたらバインド
		mCallback_instance.bind(getApplicationContext(), MAIN_ACTIVITY, mMainList, this);
		mNewDlService = mCallback_instance.getNewDlServiceInstance();
		if(mNewDlService != null){
			try {
				mNewDlService.callback();
			} catch (RemoteException e) {
				Logput.w("Get Callback instance is fail.", e);
			}
		}
	}


	/**
	 * クリックされたコンテンツチェック
	 * 
	 * @param status
	 */
	private void viewCheck(int status, BookBeans book) {
		switch (status) {
		case EXPIRYDATE_NG: // 閲覧期限切れ
			// Webサイト誘導情報がセットされている場合はエラーメッセージと一緒に表示して誘導を行う
			expiryDateNavidialog(book.getDownload_id());
			break;
		case BROWSE_NG: // 閲覧回数が過ぎている
			dialog(DIALOG_ID_VIEW, getString(R.string.browse_count_error));
			break;
		case INVALID_PLATFORM_NG: // Androidでの閲覧が許可されていない
			dialog(DIALOG_ID_VIEW, getString(R.string.invalid_platform_error));
			break;
		case FILE_NONE: // ファイルが存在しない
			dialog(DIALOG_ID_VIEW, getString(R.string.contents_no));
			break;
		case OS_TIME_NG: // OSの時計が過去に戻っていた場合
			dialog(DIALOG_ID_VIEW, getString(R.string.clock_check_error));
			break;
		case OFFLINE_BROWSE_COUNT: // 閲覧回数が制限されているコンテンツはオフラインでは閲覧できない
			dialog(DIALOG_ID_VIEW, getString(R.string.offline_browse_count_error));
			break;
		default:	// ファイルチェック後ビューア起動
			ViewTask viewTask = new ViewTask(MainActivity.this, this);
			viewTask.execute(new ViewTaskParam(book, false));
			break;
		}
	}
	
	/**
     * アンドロイドマーケットからコールバックを受取りUIを更新する<br>
     * 
     * A {@link PurchaseObserver} is used to get callbacks when Android Market sends
     * messages to this application so that we can update the UI.
     */
    private class DungeonsPurchaseObserver extends PurchaseObserver implements ViewListener{
    	/**
    	 * コンストラクタ - アンドロイドマーケットからコールバックを受取りUIを更新するクラス
    	 * @param handler
    	 */
        public DungeonsPurchaseObserver(Handler handler) {
            super(MainActivity.this, handler);
        }
        
    	/**
    	 * ViewTask戻り値
    	 * @param 画面を更新するかどうかフラグ
    	 */
    	@Override
    	public void result_view(String errorMessage){
    		removeDialog(DIALOG_ID_PROGRESS);
    		if(errorMessage != null){
    			dialog(DIALOG_ID_VIEW, errorMessage);
    		}
    	}

        /**
         * Androidマーケットが応答するときに呼び出されるコールバック
         */
        @Override
        public void onBillingSupported(boolean supported) {
            Logput.i("supported: " + supported);
            if (!supported) {
            	// アプリ内課金に対応していない場合はダイヤログ表示
                dialog(DIALOG_BILLING_NOT_SUPPORTED_ID, getString(R.string.billing_not_supported_title),getString(R.string.billing_not_supported_message));
            }
        }

        /**
         * アイテムが購入、払い戻し、またはキャンセルされたときに呼び出されるコールバック<br>
		 * 呼び出した{@link BillingService requestPurchase（文字列）}に応じて呼び出される
         */
        @Override
        public void onPurchaseStateChange(PurchaseState purchaseState, String itemId, long purchaseTime, String developerPayload) {
            Logput.i("onPurchaseStateChange() itemId: " + itemId + " " + purchaseState);
            
            if (developerPayload == null) {
            	Logput.d("product:" + itemId + " activity:" + purchaseState);
            } else {
            	Logput.d("product:" + itemId + " activity:" + purchaseState + "\n\t" + developerPayload);
            }
            
            if(purchaseState == PurchaseState.PURCHASED){
        		// [購入]…ダウンロード開始
        		Logput.d("DL : " + mQueryList);
        		// DL中のものがなければダウンロード開始
				if(!NewDownloadService.sDlFlag){
					Logput.d("Set queryList.");
					NewDownloadService.sQueryList = mQueryList;
				}else{
					Logput.i("Other file downloading.");
					Toast.makeText(MainActivity.this, "Other file downloading.", Toast.LENGTH_SHORT);
				}
        	}else{
        		mQueryList = null;
        	}
        }

        /**
         * RequestPurchase要求に対して市場からの応答コードを受信したときに呼び出される
         */
        @Override
        public void onRequestPurchaseResponse(RequestPurchase request,
                ResponseCode responseCode) {
            Logput.d("[product]" + request.mProductId + " [responseCode]" + responseCode);
            
            if (responseCode == ResponseCode.RESULT_OK) {
                Logput.i(">>purchase was successfully sent to server");
            } else if (responseCode == ResponseCode.RESULT_USER_CANCELED) {
                Logput.i(">>user canceled purchase");
            } else {
                Logput.i(">>purchase failed");
            }
        }


        /**
         * {@link RestoreTransactions}要求に対してはAndroidマーケットから取得してレスポンスコードを受信
         */
        @Override
        public void onRestoreTransactionsResponse(RestoreTransactions request,
                ResponseCode responseCode) {
            if (responseCode == ResponseCode.RESULT_OK) {
                Logput.d("completed RestoreTransactions request");
            } else {
                Logput.i("RestoreTransactions error: " + responseCode);
            }
        }
    }
    
    /**
     * データベースが初期化されていない場合、<br>
     * 購入したアイテムリストを取得するためにAndroid MarketにRESTORE_TRANSACTIONS要求を送信<br>
     * ※DBを初期化する必要がある場合のみ使用するように
     */
    private void restoreDatabase() {
    	// Preferencesから
    	if(mPref == null) mPref = new Preferences(getApplicationContext());
        if (!mPref.getDbInitialized()) {
        	if(mBillingService == null){
	        	mBillingService = new BillingService();
	        	mBillingService.setContext(this);
	        	ResponseHandler.register(mDungeonsPurchaseObserver);
	        }
            mBillingService.restoreTransactions();
            Logput.d("Request:RESTORE_TRANSACTIONS");
            mPref.setDbInitialized(true);
            Logput.d("Set : DbInitialized = true");
        }
    }
    
	// ----------------- // MENU ---------------------
	/** ソート方法 */
	private int _sort;

	/**
	 * ソートメニュー
	 */
	private void setSortCheck() {
		// 初期状態はダウンロード日時降順
		CheckBox chkbox = (CheckBox) findViewById(R.id.descend_date);
		mPref = new Preferences(MainActivity.this.getApplicationContext());
		_sort = mPref.getSort();

		if (_sort == ConstDB.DESCEND_DATE) {
			// ソート方法：取得日時(降順)...初期表示
			chkbox = (CheckBox) findViewById(R.id.descend_date);
		} else if (_sort == ConstDB.ASCEND_DATE) {
			// ソート方法：取得日時(昇順)
			chkbox = (CheckBox) findViewById(R.id.ascend_date);
		} else if (_sort == ConstDB.DESCEND_NAME) {
			// ソート方法：取得日時(昇順)
			chkbox = (CheckBox) findViewById(R.id.descend_title);
		} else if (_sort == ConstDB.ASCEND_NAME) {
			// ソート方法：取得日時(昇順)
			chkbox = (CheckBox) findViewById(R.id.ascend_title);
		} else {
			Logput.v("Not sort");
		}
		if (chkbox != null) {
			chkbox.setChecked(true);
		}
	}

	/** オプションメニューが最初に呼び出される時に1度だけ呼び出されます */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean ret = super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		// XMLで定義したmenuを指定する。
		inflater.inflate(R.menu.menu_main, menu);
		return ret;
	}

	/** オプションメニューアイテムが選択された時に呼び出されます */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return main_menu(item);
	}

	/**
	 * メイン画面メニュー
	 * 
	 * @param item
	 */
	private boolean main_menu(MenuItem item) {
		boolean ret = true;

		switch (item.getItemId()) {
		default:
			ret = super.onOptionsItemSelected(item);
			break;
		case R.id.menu_sort: // コンテンツリストソート方法選択
			setSortCheck();
			ret = true;
			break;
		case R.id.descend_date: // ダウンロード日時降順
			if (item.isChecked())
				item.setChecked(false);
			else
				item.setChecked(true);
			// ソート方法登録,リスト更新
			mPref.setSort(ConstDB.DESCEND_DATE);
			listView(ConstDB.DESCEND_DATE);
			break;
		case R.id.ascend_date: // ダウンロード日時昇順
			if (item.isChecked())
				item.setChecked(false);
			else
				item.setChecked(true);
			// ソート方法登録,リスト更新
			mPref.setSort(ConstDB.ASCEND_DATE);
			listView(ConstDB.ASCEND_DATE);
			break;
		case R.id.descend_title: // タイトル降順
			if (item.isChecked())
				item.setChecked(false);
			else
				item.setChecked(true);
			// ソート方法登録,リスト更新
			mPref.setSort(ConstDB.DESCEND_NAME);
			listView(ConstDB.DESCEND_NAME);
			break;
		case R.id.ascend_title: // タイトル昇順
			if (item.isChecked())
				item.setChecked(false);
			else
				item.setChecked(true);
			// ソート方法登録,リスト更新
			mPref.setSort(ConstDB.ASCEND_NAME);
			listView(ConstDB.ASCEND_NAME);
			break;
		}
		return ret;
	}

	/**
	 * RegistConfirmTask - Web書庫遷移処理戻り値処理
	 * @param 処理ステータスコード
	 */
	@Override
	public void result_registConfirm(int status) {
		switch(status){
		case SEND_REGIST_MAILADDRESS:	// メールアドレス登録画面へ遷移
			Intent login = new Intent(this, RegistFormActivity.class);
			startActivity(login);
			break;
		case SEND_WEB_BOOK_SHELF:		// Web書庫画面に遷移
			Intent webBookShelf = new Intent(this, WebBookShelfActivity.class);
			startActivity(webBookShelf);
			break;
		case RESET_FLAG:				// 他のクライアントでリセットされたためアプリ終了ダイアログ表示
			dialog(DIALOG_ID_ERROR, getString(R.string.reset_finish, getString(R.string.app_name)));
			break;
		case DB_ERROR:					//　DB登録エラー発生：メイン画面のまま
			dialog(DIALOG_ID_VIEW, getString(R.string.db_error));
			break;
		case GET_CONTENTS_ERROR:		// GetContentsリクエストエラー:メイン画面のまま
			dialog(DIALOG_ID_VIEW, getString(R.string.getcontents_error));
			break;
		case GET_AWS_INFO_NG:			// GetAwsInfoリクエストエラー 
			dialog(DIALOG_ID_VIEW, getString(R.string.status_false));
			break;
		case GET_AWS_INFO_50010:		// GetAwsInfo - パラメーターエラー
			dialog(DIALOG_ID_VIEW, getString(R.string.status_parameter_error) + 50010);
			break;
		case GET_AWS_INFO_50011:		// GetAwsInfo - サービス停止中
			dialog(DIALOG_ID_VIEW, getString(R.string.getawsinfo_status_50011));
			break;
		case GET_AWS_INFO_50012:		// GetAwsInfo - メンテナンス中
			dialog(DIALOG_ID_VIEW, getString(R.string.getawsinfo_status_50012));
			break;
		case GET_AWS_INFO_50099:		// GetAwsInfo - サーバ内部エラー
			dialog(DIALOG_ID_VIEW, getString(R.string.status_server_internal_error) + 50099);
			break;
		}
	}
	
}
