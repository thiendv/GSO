package net.keyring.bookend.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import net.keyring.bookend.Logput;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.R;
import net.keyring.bookend.WebBookShelfDlList;
import net.keyring.bookend.action.WebBookShelfAction;
import net.keyring.bookend.adapter.WebBookShelfAdapter;
import net.keyring.bookend.bean.BookBeans;
import net.keyring.bookend.callback.CallBack;
import net.keyring.bookend.constant.Const;
import net.keyring.bookend.constant.ConstDB;
import net.keyring.bookend.constant.ConstList;
import net.keyring.bookend.constant.ConstViewDefault;
import net.keyring.bookend.db.ContentsDao;
import net.keyring.bookend.request.UpdateContents;
import net.keyring.bookend.service.WebBookShelfDlService;
import net.keyring.bookend.util.FileUtil;
import net.keyring.bookend.util.Utils;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Web書庫画面・メールアドレス画面
 * 
 * @author Hamaji
 * 
 */
public class WebBookShelfActivity extends BookendActivity implements ConstList,
		ConstViewDefault, Const {

	/** 表示データリストMap */
	private ArrayList<Map<String, Object>> mData = null;
	/** アダプタ */
	private WebBookShelfAdapter mAdapter = null;
	/** ListActionクラス */
	private WebBookShelfAction mAction;
	/** ListView */
	private ListView mWebBookShelfList;
	/** 長押しされたコンテンツ情報 */
	private BookBeans mCheckBook;
	/** ダイアログ */
	private AlertDialog mDialog;
	/** CallBackインスタンス */
	private CallBack mCallback_instance = CallBack.getInstance();
	/** ContentsDao */
	private ContentsDao mDao;
	/** 画面下部のツールバー */
	private MenuBarSubActivity			mMenuBarSubActivity = new MenuBarSubActivity();

	/** 表示項目：サムネイルURL・タイトル・著者・RowID */
	private String[] mFromTemplate = { THUMB_URL, TITLE, ID };
	private int[] mToTemplate = { R.id.thumbnail_web, R.id.title_web };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_book_shelf);
	}

	@Override
	public void onRestart() {
		super.onRestart();
		setContentView(R.layout.web_book_shelf);
	}

	@Override
	public void onStop() {
		super.onStop();
		mData = null;
		mAdapter = null;
	}

	/**
	 * 【 メイン 】
	 */
	@Override
	public void onResume() {
		super.onResume();
		// デバックモードチェック
		if (Utils.isDebugMode(this.getApplicationContext())) {
			// デバックモード有効：NG
			dialog(DIALOG_ID_ERROR, getString(R.string.debug_mode_message));
		} else {
			setLayout();
			setList();
		}
	}
	
	/**
	 * レイアウトセット
	 */
	public void setLayout(){
		mLayout = (LinearLayout) findViewById(R.id.web_book_shelf);
		mWebBookShelfList = (ListView) mLayout.findViewById(R.id.web_book_list);
		//	ツールバーの初期化処理を行う
		mMenuBarSubActivity.init(this, MenuBarSubActivity.ACTIVITY_WEBSHELF);
		mMenuBarSubActivity.setupEventListener();
		mMenuBarSubActivity.updateButtonState(this);
	}

	/**
	 * Layout - Web書庫
	 */
	private void setList() {
		// コンテンツ情報取得
		mData = getData();
		// アダプターにセット
		mAdapter = new WebBookShelfAdapter(this, mData, R.layout.web_book, mFromTemplate, mToTemplate);
		mWebBookShelfList = mAction.setMainList(mWebBookShelfList, mData, mAdapter);
		mWebBookShelfList.setAdapter(mAdapter);
		// 押下イベント
		mWebBookShelfList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				detailDialogView(position);
			}
		});
		// リストがセットできたらバインド
		mCallback_instance.bind(getApplicationContext(), WEB_BOOK_SHELF_ACTIVITY, mWebBookShelfList, null);
	}
	
	/**
	 * Web書庫表示データ取得
	 * @return
	 */
	private ArrayList<Map<String, Object>> getData(){
		if (mAction == null) mAction = new WebBookShelfAction();
		return mAction.getWebBooksData(this.getApplicationContext());
	}
	
	
	// --------------------------------------
	//	ボタンクリックアクション
	// --------------------------------------
	/**
	 * ダウンロードボタン
	 * @param view
	 */
	public void downloadBtnAction(View view){
		long rowID = (Long)view.getTag();
		Logput.d("downloadBtnAction rowID = " + rowID);
		
		// リスト選択時の処理
		if(mDao == null) mDao = new ContentsDao(getApplicationContext());
		BookBeans book = mDao.load(rowID);
		if (mAction == null) mAction = new WebBookShelfAction();
		int status = mAction.check_WebBookShelf_DL(book);
		if (status == -1) {
			contentsDL(rowID, book.getTitle());
		} else {
			statusErrorDialog(status);
		}
	}
	/**
	 * キャンセルボタンアクション
	 * @param view
	 */
	public void cancelBtnAction(View view){
		if(WebBookShelfDlService.sIsNotCancel){
			// キャンセル不可フラグが立っている場合は処理しない
			Toast.makeText(this, "Cancel NG.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		long rowID = (Long)view.getTag();
		// DBから最新の状態を取得
		if (mDao == null) mDao = new ContentsDao(getApplicationContext());
		BookBeans book = mDao.load(rowID);
		// キャンセル確認ダイアログ
		String title = book.getTitle();
		Logput.v("Cancel Request [" + rowID + "] " + title);
		switch (book.getDlStatus()) {
		case STATUS_WAIT_FOR_DL_WEB: // ダウンロード待機中の場合
			// ダウンロード待機中リストから削除
			WebBookShelfDlList instance = WebBookShelfDlList.getInstance();
			instance.cancel(rowID);
			// ダウンロードステータス初期化,リスト更新
			status_init(book);
			break;
		case STATUS_DOWNLOADING_WEB: // ダウンロード中
			// キャンセルフラグを立てる
			WebBookShelfDlService.sStopRequested = true;
			break;
		default:
			setStatus(book);
			break;
		}
	}
	
	/**
	 * リトライボタンアクション
	 * @param view
	 */
	public void retryBtnAction(View view){
		long rowID = (Long)view.getTag();
		Logput.d("retryBtnAction rowID = " + rowID);
		// DLステータスを初期化
		if(mDao == null) mDao = new ContentsDao(getApplicationContext());
		mDao.updateDLstatus(rowID, STATUS_INIT, null, null, 0);
		LinearLayout initSet = (LinearLayout) mWebBookShelfList.findViewWithTag(getString(R.string.dl_init_set_tag) + rowID);
		LinearLayout errorSet = (LinearLayout) mWebBookShelfList.findViewWithTag(getString(R.string.dl_error_set_tag) + rowID);
		errorSet.setVisibility(View.GONE);
		initSet.setVisibility(View.VISIBLE);
		downloadBtnAction(view);
	}
	
	
	/**
	 * ダウンロードステータス初期化
	 * 
	 * @param position リストポジション
	 * @param bean ダウンロードテーブル情報
	 */
	private void status_init(BookBeans book) {
		String filePath = book.getFile_path();
		String thumbPath = book.getThumb_path();
		if(filePath != null){
			FileUtil.deleteFile(new File(filePath));
		}
		if(thumbPath != null){
			FileUtil.deleteFile(new File(thumbPath));
		}
		// ダウンロードステータスを初期化
		if (mDao == null) mDao = new ContentsDao(getApplicationContext());
		mDao.updateDLstatus(book.getRowId(), STATUS_INIT, null, null, 0);
		setStatus(book);
	}
	
	private void setStatus(BookBeans book){
		long rowID = book.getRowId();
		int status = book.getDlStatus();
		LinearLayout initSet = (LinearLayout) mWebBookShelfList.findViewWithTag(getString(R.string.dl_init_set_tag) + rowID);
		LinearLayout ingSet = (LinearLayout) mWebBookShelfList.findViewWithTag(getString(R.string.dl_ing_set_tag) + rowID);
		LinearLayout errorSet = (LinearLayout) mWebBookShelfList.findViewWithTag(getString(R.string.dl_error_set_tag) + rowID);
		switch (status) {
		case STATUS_WAIT_FOR_DL_WEB: // DL待機中の場合はボタン非アクティブに
			initSet.setVisibility(View.VISIBLE);
			ingSet.setVisibility(View.GONE);
			errorSet.setVisibility(View.GONE);
			break;
		case STATUS_DOWNLOADING_WEB: // DL中の場合はプログレスバーとキャンセルボタンを表示
			initSet.setVisibility(View.GONE);
			ingSet.setVisibility(View.VISIBLE);
			errorSet.setVisibility(View.GONE);
			break;
		case STATUS_DL_ERROR_WEB: // DLエラーの場合は初期化
			initSet.setVisibility(View.GONE);
			ingSet.setVisibility(View.VISIBLE);
			errorSet.setVisibility(View.GONE);
			TextView errorTextView = (TextView) mWebBookShelfList.findViewWithTag((getString(R.string.dl_error_tag) + rowID));
			errorTextView.setText(getString(R.string.download_fail));
			// 初期化
			FileUtil.deleteFile(new File(book.getFile_path()));
			if (mDao == null) mDao = new ContentsDao(getApplicationContext());
			mDao.updateDLstatus(rowID, STATUS_INIT, null, null, 0);
			book = mDao.load(rowID);
			setStatus(book);
			break;
		default:
			initSet.setVisibility(View.VISIBLE);
			ingSet.setVisibility(View.GONE);
			errorSet.setVisibility(View.GONE);
			TextView statusView = (TextView) mWebBookShelfList.findViewWithTag(getString(R.string.status_web_tag) + rowID);
			statusView.setText("");
			break;
		}
	}
	
	/**
	 * Web書庫からコンテンツダウンロード
	 */
	public void contentsDL(long rowID, String title){
		// DL開始
		LinearLayout initSet = (LinearLayout) mWebBookShelfList.findViewWithTag(getString(R.string.dl_init_set_tag) + rowID);
		LinearLayout ingSet = (LinearLayout) mWebBookShelfList.findViewWithTag(getString(R.string.dl_ing_set_tag) + rowID);
		initSet.setVisibility(View.GONE);
		ingSet.setVisibility(View.VISIBLE);
		try {
			// ダウンロード待ちリストに追加
			mCallback_instance.downloadRequest_web(rowID);
		} catch (RemoteException e) {
			Logput.e(e.getMessage(), e);
			dialog(DIALOG_ID_VIEW, getString(R.string.download_service_error));
		}
	}
	
	/**
	 * リスト選択時表示されるダイアログセット
	 * @param position　リストポジション
	 */
	public void detailDialogView(int position){
		if (mAction == null) mAction = new WebBookShelfAction();
		AlertDialog.Builder alert = mAction.setListClickDialog(this, mData, mAdapter, position);
		mCheckBook = mAction.getBook(getApplicationContext(), mData, mAdapter, position);
		try{
			alert.setPositiveButton(getString(R.string.web_delete), new OnClickListener() {
				// 削除ボタン
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// 削除：updateContents
					deleteDialog();
				}
			});
			// 閉じるボタン
			alert.setNegativeButton(getString(R.string.close), null);
			// ダイアログが二重に開いてしまわないための処理
			if(mDialog == null || !mDialog.isShowing()){
				mDialog = alert.create();
				mDialog.show();
			}
		}catch(Exception e){
		}
	}
	
	/**
	 * コンテンツ削除確認アラート
	 * @param book 削除コンテンツ
	 */
	private void deleteDialog(){
		LayoutInflater inflater = LayoutInflater.from(this.getApplicationContext());
		View view = inflater.inflate(R.layout.alert, null);
		TextView message =(TextView)view.findViewById(R.id.alert_message);
		message.setText(getString(R.string.contents_delete_message));
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setView(view);
		alert.setIcon(R.drawable.icon);
		alert.setTitle(getString(R.string.web_delete));
		alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	delete(mCheckBook);
            }
        });
		alert.setNegativeButton(getString(R.string.cancel), null);
		alert.show();
	}
	
	/**
	 * コンテンツ完全削除
	 * @param book 削除コンテンツ
	 */
	private void delete(BookBeans book){
		long rowID = book.getRowId();
		UpdateContents updateContents = new UpdateContents(getApplicationContext());
		boolean isDelete = updateContents.execute(rowID, book.getDownload_id(), null, "true");
		if(isDelete){
			if(mAction == null) mAction = new WebBookShelfAction();
			mAction.deleteContent(getApplicationContext(), book);
			// リスト更新
			setList();
		}
	}

	/**
	 * 選択コンテンツがＤＬできるものかチェック
	 * 
	 * @param book
	 *            コンテンツ情報
	 * @return DLできるものはtrue,できないものはfalseを返す
	 */
	private void statusErrorDialog(int book_status) {
		switch (book_status) {
		case FILE_TYPE_NG: // ファイルタイプがkrpdf以外
			dialog(DIALOG_ID_VIEW, getString(R.string.filetype_error));
			break;
		case EXPIRYDATE_NG: // 閲覧期限が切れている
			dialog(DIALOG_ID_VIEW, getString(R.string.expiry_date_error));
			break;
		case SHARED_DEVICE_NG: // 残り共有台数が0の場合(SharedDevice)
			dialog(DIALOG_ID_VIEW, getString(R.string.shared_device_error));
			break;
		case INVALID_PLATFORM_NG: // Androidでの閲覧が許可されていない
			dialog(DIALOG_ID_VIEW, getString(R.string.invalid_platform_error));
			break;
		case DOWNLOADED: // 既にダウンロード済
			dialog(DIALOG_ID_VIEW, getString(R.string.downloaded));
			break;
		}
	}

	// ----------------- // MENU ---------------------

	/**
	 * ソートメニュー
	 */
	private void setSortCheck() {
		// 初期状態はダウンロード日時降順
		CheckBox chkbox = (CheckBox) findViewById(R.id.descend_date);
		int sort = Preferences.sSort_web;

		if (sort == ConstDB.DESCEND_DATE) {
			// ソート方法：取得日時(降順)...初期表示
			chkbox = (CheckBox) findViewById(R.id.descend_date);
		} else if (sort == ConstDB.ASCEND_DATE) {
			// ソート方法：取得日時(昇順)
			chkbox = (CheckBox) findViewById(R.id.ascend_date);
		} else if (sort == ConstDB.DESCEND_NAME) {
			// ソート方法：取得日時(昇順)
			chkbox = (CheckBox) findViewById(R.id.descend_title);
		} else if (sort == ConstDB.ASCEND_NAME) {
			// ソート方法：取得日時(昇順)
			chkbox = (CheckBox) findViewById(R.id.ascend_title);
		} else {
			Logput.v("Not sort");
		}
		if (chkbox != null)
			chkbox.setChecked(true);
	}

	/** オプションメニューが最初に呼び出される時に1度だけ呼び出されます */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		// XMLで定義したmenuを指定する。
		inflater.inflate(R.menu.menu_webbookshelf, menu);
		return true;
	}

	/** オプションメニューアイテムが選択された時に呼び出されます */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return webBookShelf_menu(item);
	}

	/**
	 * Web書庫メニュー
	 * 
	 * @param item
	 * @return
	 */
	private boolean webBookShelf_menu(MenuItem item) {
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
			Preferences.sSort_web = ConstDB.DESCEND_DATE;
			setList();
			break;
		case R.id.ascend_date: // ダウンロード日時昇順
			if (item.isChecked())
				item.setChecked(false);
			else
				item.setChecked(true);
			// ソート方法登録,リスト更新
			Preferences.sSort_web = ConstDB.ASCEND_DATE;
			setList();
			break;
		case R.id.descend_title: // タイトル降順
			if (item.isChecked())
				item.setChecked(false);
			else
				item.setChecked(true);
			// ソート方法登録,リスト更新
			Preferences.sSort_web = ConstDB.DESCEND_NAME;
			setList();
			break;
		case R.id.ascend_title: // タイトル昇順
			if (item.isChecked())
				item.setChecked(false);
			else
				item.setChecked(true);
			// ソート方法登録,リスト更新
			Preferences.sSort_web = ConstDB.ASCEND_NAME;
			setList();
			break;
		case R.id.menu_filter: // ●フィルタ設定
			setFilterCheck();
			ret = true;
			break;
		case R.id.filter_yet: // ・未ダウンロードコンテンツ一覧
			Preferences.sListFilter = VIEW_DELETE_CONTENTS;
			if (item.isChecked())
				item.setChecked(false);
			else
				item.setChecked(true);
			setList();
			ret = true;
			break;
		case R.id.filter_all: // ・Allコンテンツ一覧
			Preferences.sListFilter = VIEW_ALL_CONTENTS;
			if (item.isChecked())
				item.setChecked(false);
			else
				item.setChecked(true);
			setList();
			ret = true;
			break;
		}
		return ret;
	}

	private void setFilterCheck() {
		// 初期状態：全てのコンテンツ表示
		CheckBox chkbox = (CheckBox) findViewById(R.id.filter_all);
		// いつも初期状態から(static変数)...ダウンロードした日時降順
		int filter = Preferences.sListFilter;

		if (filter == VIEW_DELETE_CONTENTS) {
			// フィルタ方法：未ダウンロードコンテンツ一覧
			chkbox = (CheckBox) findViewById(R.id.filter_yet);
		} else if (filter == VIEW_ALL_CONTENTS) {
			// フィルタ方法：全てのコンテンツ一覧
			chkbox = (CheckBox) findViewById(R.id.filter_all);
		} else {
			Logput.v("Not filter");
		}
		if (chkbox != null)
			chkbox.setChecked(false);
	}
}
