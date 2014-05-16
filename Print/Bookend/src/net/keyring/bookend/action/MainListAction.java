package net.keyring.bookend.action;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.keyring.bookend.ImageCache;
import net.keyring.bookend.Logput;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.R;
import net.keyring.bookend.ServerGetThumb;
import net.keyring.bookend.adapter.MainListAdapter;
import net.keyring.bookend.bean.BookBeans;
import net.keyring.bookend.constant.ConstDB;
import net.keyring.bookend.constant.ConstList;
import net.keyring.bookend.constant.ConstQuery;
import net.keyring.bookend.db.ContentsDao;
import net.keyring.bookend.request.UpdateLicense;
import net.keyring.bookend.util.DateUtil;
import net.keyring.bookend.util.DecryptUtil;
import net.keyring.bookend.util.FileUtil;
import net.keyring.bookend.util.StringUtil;
import net.keyring.bookend.util.Utils;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.Html;
import android.text.util.Linkify;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Main画面処理
 * 
 * @author Hamaji
 * 
 */
public class MainListAction extends CommonAction implements ConstList {

	/** ContentsDaoクラス */
	private ContentsDao mDao;
	/** Preferencesクラス */
	private Preferences mPref;

	/** クリックされたコンテンツ情報 - 長押し */
	private BookBeans mBook;
	
	/**
	 * クリックされたコンテンツ情報を取得
	 * 
	 * @return クリックされたコンテンツ情報
	 */
	public BookBeans getBook() {
		return this.mBook;
	}

	private void setBook(BookBeans book) {
		this.mBook = book;
	}

	/**
	 * リストからクリックされた場合のアクション処理
	 * 
	 * @param con
	 *            Context
	 * @param list_group
	 *            ListView
	 * @param data
	 *            リストデータ一覧
	 * @param adapter
	 *            アダプタ
	 * @param position
	 *            クリックされたリストポジション
	 * @return status
	 */
	public int listClickAction(Context con, ListView list_group,
			ArrayList<Map<String, Object>> data, MainListAdapter adapter, int position) {
		int status = -1;
		BookBeans book = getBook(con, data, adapter, position);
		if (book != null) {
			setBook(book);
			if (mPref == null) mPref = new Preferences(con);
			String lastStartUpDate = mPref.getLast_startup_date();
			status = check_viewBook(con, book, lastStartUpDate);
		}
		return status;
	}
	
	/**
	 * 閲覧制限に引っかかっていないかチェック
	 * 
	 * @param BookBeans コンテンツ
	 * @return ステータス(問題なければ-1を返す)
	 */
	public int check_viewBook(Context con, BookBeans book, String lastStartUpDate) {
		int status = -1;
		long rowID = book.getRowId();
		int browse = book.getBrowse_M();
		String invalidPlatform = book.getInvalidPlatform();
		String contents_path = book.getFile_path();
		if (!super.check_InvalidPlatform(invalidPlatform)) {
			status = INVALID_PLATFORM_NG;
			Logput.d("[" + rowID + "] ... INVALID_PLATFORM_NG");
		} else if (!super.check_ExpiryDate(book.getExpiry_date())) {
			status = EXPIRYDATE_NG;
			Logput.d("[" + rowID + "] ... EXPIRYDATE_NG");
		} else if (!check_BrowseCount(browse)) {
			status = BROWSE_NG;
			Logput.d("[" + rowID + "] ... BROWSE_NG");
		} else if (!check_Date(lastStartUpDate, book.getLast_access_date())) {
			status = OS_TIME_NG;
			Logput.d("[" + rowID + "] ... OS_TIME_NG");
		} else if (!check_Browse_connected(con, browse)) {
			status = OFFLINE_BROWSE_COUNT;
			Logput.d("[" + rowID
					+ "click book status = OFFLINE_BROWSE_COUNT");
		} else if (!Utils.checkFile(contents_path)) {
			status = FILE_NONE;
			Logput.d("[" + rowID + "] ... FILE_NONE");
		}
		return status;
	}

	/**
	 * コンテンツが長押しされた時表示されるダイアログセット
	 * 
	 * @param con Context
	 * @param list_group ListView
	 * @param data コンテンツデータ
	 * @param adapter アダプター
	 * @param position クリックされたコンテンツポジション
	 * @return ダイヤログ
	 */
	public AlertDialog.Builder setListLongClickDialog(Context con,
			ArrayList<Map<String, Object>> data, MainListAdapter adapter,
			int position) {
		AlertDialog.Builder dialog = null;
		// 長押し選択したアイテムのコンテンツ詳細ダイアログ表示（削除ボタン有）
		BookBeans book = getBook(con, data, adapter, position);
		if (book != null) {
			setBook(book);
			// 詳細情報文字列取得
			String detailStr = getDetail_main(con, book);
			// TextViewにセット
			TextView detail = new TextView(con);
			// HTMLのLinkなので戻るボタンを押してもアプリに戻ってこない
			detail.setAutoLinkMask(Linkify.WEB_URLS);
			detail.setText(Html.fromHtml(detailStr));

			dialog = new AlertDialog.Builder(con);
			dialog.setIcon(R.drawable.icon);
			dialog.setTitle(con.getString(R.string.detail));
			dialog.setView(detail);
		}
		return dialog;
	}

	/**
	 * 選択されたコンテンツ情報取得
	 * 
	 * @param con Context
	 * @param data コンテンツ情報一覧
	 * @param adapter アダプター
	 * @param position 選択されたコンテンツのリストポジション
	 * @return 選択されたコンテンツ情報
	 */
	public BookBeans getBook(Context con, ArrayList<Map<String, Object>> data, MainListAdapter adapter, int position) {
		BookBeans book = null;
		if (data == null) {
			data = getBooksData(con);
		}
		//Logput.i("getBook data = " + data.toString());
		if (data != null) {
			// コンテンツ情報取得
			Map<String, Object> bookMap = data.get(position);
			final Long rowID = (Long) bookMap.get(ID);
			if (rowID != -1) {
				if (mDao == null) mDao = new ContentsDao(con);
				book = mDao.load(rowID);
			}
		}
		if (book == null) {
			Logput.v(">Click Contents is null.");
		}
		return book;
	}

	/**
	 * コンテンツ削除処理
	 * 
	 * @param con Context
	 * @param mBook Book
	 * @return 問題なく削除できた場合はtrue,それ以外はfalseを返す
	 */
	public boolean delete(Context con) {
		BookBeans book = this.mBook;
		if (book != null) {
			long rowID = book.getRowId();
			// UpdateLicenseの必要がある場合は行う：sharedDevice:false
			if (book.getSharedDevice_D() != -1) {
				// 共有台数が無制限ではない場合はUpdateLicenseを行う
				// UpdateLicenseリクエスト(sharedDevice:削除)
				UpdateLicense updateLicense = new UpdateLicense();
				updateLicense.execute(con, rowID, book.getDownload_id(),
						book.getSharedDevice_M(), false);
			}
			// コンテンツ削除処理
			String path = book.getFile_path();
			if (!StringUtil.isEmpty(path)) {
				FileUtil.deleteFile(new File(path));
			}
			// サムネイル削除
			path = book.getThumb_path();
			if (!StringUtil.isEmpty(path)) {
				FileUtil.deleteFile(new File(path));
			}
			// DB更新
			mDao.updateDelete(rowID);
			return true;
		} else {
			Logput.v(">Delete contents is null.");
			return false;
		}
	}

	/**
	 * DBからコンテンツ情報取得
	 */
	public ArrayList<Map<String, Object>> getBooksData(Context con) {
		// テンプレートのリスト取得
		ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		if (mPref == null) mPref = new Preferences(con);
		int sort = mPref.getSort();
		if (mDao == null) mDao = new ContentsDao(con);
		ArrayList<BookBeans> BooksList = mDao.loadList(sort);

		if (BooksList != null) {
			for (BookBeans book : BooksList) {
				if (book.getMainView() == 0) {
					map = new HashMap<String, Object>();
					map.put(ID, book.getRowId());
					String thumbPath = book.getThumb_path();
					// サムネイルダウンロードURLがNULLの場合はセットする
					thumbPath = checkThumbnail(con, book);
					thumbPath = "file://" + thumbPath;
					map.put(THUMB_URL, thumbPath);
					// webサイト誘導用
					map.put(DOWNLOAD_ID, book.getDownload_id());
					//Logput.v(map.toString());
					data.add(map);
				}
			}
		}
		return data;
	}

	/**
	 * InvalidPlatform,閲覧期限、閲覧回数エラーがあった場合はエラー表示文字列を返す
	 * 
	 * @param con Context
	 * @param book Book
	 * @return エラー表示文字列
	 */
	public String getStatus(Context con, BookBeans book) {
		String status = null;
		if (book != null) {
			if (!check_InvalidPlatform(book.getInvalidPlatform())) {
				// InvalidPlatform = Android
				status = con.getString(R.string.invalid_platform_error);
			} else if (!check_ExpiryDate(book.getExpiry_date())) {
				// 閲覧期限NG
				status = con.getString(R.string.expiry_date_error);
			} else if (!check_BrowseCount(book.getBrowse_M())) {
				// 閲覧回数NG
				status = con.getString(R.string.browse_count_error);
			}
		}
		return status;
	}

	/**
	 * サムネイルの確認をして、画像がなければ取得
	 * 
	 * @param con Context
	 * @param book Book
	 * @return ローカルサムネイルファイルパス
	 */
	private String checkThumbnail(Context con, BookBeans book) {
		String thumbPath = book.getThumb_path();
		// サムネイルが存在すればそのままパスを返す
		if (Utils.checkFile(thumbPath)) {
			return thumbPath;
		} else {
			// cacheがあるかチェック
			Bitmap image = ImageCache.getImage("file://" + thumbPath);
			if (image == null) {
				// オンライン時のみ
				if (Utils.isConnected(con)) {
					// サムネイルダウンロード,登録
					return thumbnailDL(con, book);
				} else {
					return thumbPath;
				}
			} else {
				return thumbPath;
			}
		}
	}

	/**
	 * サムネイルダウンロード・登録処理
	 * 
	 * @param con Cntext
	 * @param book Book
	 * @return 保存先サムネイルパス
	 */
	private String thumbnailDL(Context con, BookBeans book) {
		String thumbPath = book.getThumb_path();
		// サムネイルDLURLをDBから取得
		String thumbDlUrl = book.getThumb_DL_URL();
		// サムネイルが存在しない場合はダウンロード
		ServerGetThumb getThumb = new ServerGetThumb(con);
		if (StringUtil.isEmpty(thumbDlUrl)) {
			// DLURLがない場合はサーバから取得
			String contentsID = book.getContents_id();
			thumbDlUrl = getThumb.getDL_URL(contentsID);
		}
		// DLURLが取得できない場合はそのままパスを返す
		if (StringUtil.isEmpty(thumbDlUrl)) {
			return thumbPath;
		} else {
			// DL
			Logput.v("[Thumb DL URL] " + thumbDlUrl);
			String thumb_path = getThumb.getThumbnail(thumbDlUrl);
			// ローカルパスをDB更新
			if (mDao == null)
				mDao = new ContentsDao(con);
			if (mDao.updateColmun(book.getRowId(), ConstDB.THUMB_PATH,
					thumb_path)) {
				return thumb_path;
			} else {
				// DB保存に失敗した場合
				return thumbPath;
			}
		}
	}

	/**
	 * リストにデータをセット
	 * 
	 * @param list_group LstView
	 * @param data リスト表示データ
	 * @param adapter アダプター
	 * @return データセットされたリストビュー
	 */
	public ListView setMainList(ListView list_group, ArrayList<Map<String, Object>> data, MainListAdapter adapter) {
		if (data != null && data.size() > 0) {
			adapter.setListData(data); // テンプレートにデータ内容を保持
			list_group.setFocusable(false);
			list_group.setAdapter(adapter);
			list_group.setScrollingCacheEnabled(false);
			list_group.setTextFilterEnabled(true);
		}
		return list_group;
	}

	/**
	 * 指定コンテンツ詳細情報文字列を返す
	 * 
	 * @param BookBeans 指定コンテンツ
	 * @return 詳細情報文字列
	 */
	public String getDetail_main(Context con, BookBeans book) {
		String title = super.setDetailValue(
				con.getString(R.string.detail_title), book.getTitle());
		String author = super.setDetailValue(
				con.getString(R.string.detail_auther), book.getAuthor());
		String distributor_name = super.setDetailValue(
				con.getString(R.string.detail_distributor_name),
				book.getDistributor_name());
		String distributor_url = super.setDetailValue(
				con.getString(R.string.detail_distributor_url),
				book.getDistributor_url());
		String downloadDate = DateUtil.change_viewDate(" - ",
				con.getString(R.string.detail_downloadDate),
				book.getDownload_date());
		String lastAccessDate = DateUtil.change_viewDate(" - ",
				con.getString(R.string.detail_lastAccessDate),
				book.getLast_access_date());
		String expiryDate = DateUtil.change_viewDate(
				con.getString(R.string.indefinitely),
				con.getString(R.string.detail_expiry_date),
				book.getExpiry_date());

		String detail = title + "<br>" + author + "<br>" + distributor_name
				+ "<br>" + distributor_url + "<br>" + downloadDate + "<br>"
				+ lastAccessDate + "<br>" + expiryDate;

		return detail;
	}

	// ------------------ Queryチェック ------------------

	/**
	 * scheme=beinfo Webページに遷移
	 * 
	 * @param con
	 *            Context
	 * @param uri
	 *            getIntentしたURI
	 * @return うまく遷移で着なかった場合はfalseを返す(念のため)
	 */
	public boolean toWebInfo(Context con, Uri uri) {
		boolean check = false;
		String query = uri.getQuery();
		query = query.trim();
		Logput.v("Query [" + query + "]");
		if (mPref == null)
			mPref = new Preferences(con);
		String userID = mPref.getUserID();
		String version = Utils.getBookendVer(con);

		String value = null;
		String retURL = null;
		if (!StringUtil.isEmpty(userID) && !StringUtil.isEmpty(version)) {
			if (query.indexOf("?") == -1) {
				// RetURL以外のクエリ情報なし
				value = "?";
				retURL = uri.getQueryParameter("returl");
			} else {
				value = "&";
				retURL = query.replace("returl=", "");
			}
			if (!StringUtil.isEmpty(retURL)) {
				String url = retURL + value + "be_user_id=" + userID + "&be_version=" + version;
				Logput.i("BEINFO URL : " + url);
				Intent webInfo = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				webInfo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				con.startActivity(webInfo);
				check = true;
			} else {
				Logput.i("BEINFO ERROR : " + retURL);
			}
		} else {
			String url = retURL + " / be_user_id=" + userID + " / be_version=" + version;
			Logput.i("BEINFO ERROR : " + url);
		}
		return check;
	}

	/**
	 * ダウンロードリンクからコンテンツ情報を取得
	 * 
	 * @param クエリー
	 * @return クエリー情報Map
	 */
	public Map<String, String> getContentsDetail(Context con, Uri uri) {
		Logput.d("query = " + uri.getQuery());
		return setDetails(con, uri);
	}

	/**
	 * Queryをリストに入れる
	 * 
	 * @param uri
	 * @param keys
	 * @return map
	 */
	private Map<String, String> getQueryList(Uri uri, Map<String, String> map,
			String[] keys) {
		for (String key : keys) {
			String value = uri.getQueryParameter(key);
			if (!StringUtil.isEmpty(value)) {
				Logput.v("Query [" + key + "]" + value);
				map.put(key, value);
			}
		}
		return map;
	}

	/**
	 * DownloadIDをセットする
	 * 
	 * @param uri
	 * @param map
	 * @return map
	 */
	private Map<String, String> check_downloadID(Context con, Uri uri) {
		Map<String, String> map = null;
		String global_DownloadID = uri.getQueryParameter("GlobalDownloadID");
		if (!StringUtil.isEmpty(global_DownloadID)) {
			// GlobalDownloadIDが存在する場合は、コンテンツのDownloadIDとしてGlobalDownloadIDの値を使用する
			if (checkDLID(con, global_DownloadID)) {
				Logput.d("Query [DownloadID]" + global_DownloadID);
				map = new HashMap<String, String>();
				map.put(ConstQuery.DOWNLOAD_ID, global_DownloadID);
			}
		} else {
			String downloadID = uri.getQueryParameter(ConstQuery.DOWNLOAD_ID);
			if (!StringUtil.isEmpty(downloadID)) {
				try {
					MessageDigest md5 = MessageDigest.getInstance("MD5");
					String dl_id = new String(downloadID + Preferences.sUserID);
					md5.update(dl_id.getBytes());
					downloadID = DecryptUtil.base16enc(md5.digest());
					if (checkDLID(con, downloadID)) {
						Logput.d("Query [DownloadID]" + downloadID);
						map = new HashMap<String, String>();
						map.put(ConstQuery.DOWNLOAD_ID, downloadID);
					}
				} catch (NoSuchAlgorithmException e) {
					Logput.e(e.getMessage(), e);
				}
			}
		}
		return map;
	}

	/**
	 * リストからダウンロード情報詳細を取得
	 * 
	 * @param クエリー情報が入ったリストList
	 *            <NameList,valueList>
	 * @return 失敗した場合はfalseを返す
	 */
	private Map<String, String> setDetails(Context con, Uri uri) {
		Map<String, String> map = null;
		if (uri != null) {
			map = check_downloadID(con, uri);
			if (map != null) {
				map = getQueryList(uri, map, ConstQuery.QUERY_KEYS);
			}
		}
		return map;
	}

	/**
	 * 同じコンテンツがダウンロード済ではないかチェックする
	 * 
	 * @return 同じコンテンツがダウンロード済の場合はfalseを返す
	 */
	private boolean checkDLID(Context con, String downloadID) {
		boolean check = false;
		if (!StringUtil.isEmpty(downloadID, true)) {
			if (mDao == null)
				mDao = new ContentsDao(con);
			// 指定ダウンロードIDがＤＢに登録されてないかチェック
			check = mDao.isRegist(downloadID);
			Logput.v("DL : downloadID check = " + check);
		}
		return check;
	}

	/**
	 * 閲覧回数が制限されているコンテンツは、オフラインでは閲覧できない
	 * 
	 * @param con
	 *            Context
	 * @param browse_count_m
	 *            閲覧回数
	 * @return オンライン状態の場合・オフラインでも閲覧制限がない場合はtrue,それ以外はfalseを返す
	 */
	private boolean check_Browse_connected(Context con, int browse_count_m) {
		boolean check = true;
		if (!Utils.isConnected(con)) {
			// 閲覧回数が制限されているコンテンツは、オフラインでは閲覧できない
			if (browse_count_m != -1) {
				check = false;
			}
		}
		return check;
	}

	/**
	 * 閲覧回数チェック(browse_count)
	 * 
	 * @param 閲覧回数
	 *            /閲覧制限回数
	 * @return 閲覧制限回数に問題なければtrue,指定回数を超えていた場合はfalseを返す
	 */
	public boolean check_BrowseCount(int browse_count_m) {
		// 閲覧回数制限がなければtrueを返す
		if (browse_count_m == -1)
			return true;
		else if (browse_count_m == 0) {
			// 閲覧回数制限が0の場合はfalseを返す
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 現在時刻がコンテンツの最終閲覧時刻・起動時刻より過去を指していないかチェック
	 * 
	 * @return　時刻に問題がなければtrue,それ以外はfalseを返す
	 */
	public boolean check_Date(String lastStartUpDate, String lastAccessDate) {
		boolean check = true;
		Date now = DateUtil.getNow();
		if (!StringUtil.isEmpty(lastAccessDate)) {
			if (!DateUtil.isUnlimitedDate(lastAccessDate)) {
				// 現在時刻がコンテンツ最終閲覧日時より過去ではないかチェック
				Date last_access_date = DateUtil.toDate(lastAccessDate, "UTC");
				check = DateUtil.isExpired(last_access_date);
				if (!check) {
					Logput.d("ERROR : [Now]" + now + " [LastAccessDate]"
							+ last_access_date);
				}
			}
		}
		if (check && !StringUtil.isEmpty(lastStartUpDate)) {
			// 現在時刻がクライアント起動日時より過去ではないかチェック
			Date last_startUp_date = DateUtil.toDate(lastStartUpDate, "UTC");
			check = DateUtil.isExpired(last_startUp_date);
			if (!check) {
				Logput.d("ERROR : [Now]" + now + " [LastStartUpDate]"
						+ last_startUp_date);
			}
		}
		return check;
	}

}
