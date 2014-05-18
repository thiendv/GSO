package net.keyring.bookend.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.keyring.bookend.Logput;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.R;
import net.keyring.bookend.adapter.WebBookShelfAdapter;
import net.keyring.bookend.bean.BookBeans;
import net.keyring.bookend.constant.ConstList;
import net.keyring.bookend.db.ContentsDao;
import net.keyring.bookend.request.GetAwsInfo;
import net.keyring.bookend.util.DateUtil;
import net.keyring.bookend.util.StringUtil;
import net.keyring.bookend.util.Utils;
import android.app.AlertDialog;
import android.content.Context;
import android.text.Html;
import android.text.util.Linkify;
import android.widget.ListView;
import android.widget.TextView;

public class WebBookShelfAction extends CommonAction implements ConstList {
	/** BooksDaoクラス */
	private ContentsDao mDao;

	/**
	 * リストにデータをセット
	 * 
	 * @param list_group
	 *            ListView
	 * @param data
	 *            リスト表示データ
	 * @param adapter
	 *            アダプター
	 * @return データセットされたリストビュー
	 */
	public ListView setMainList(ListView list_group, ArrayList<Map<String, Object>> data, WebBookShelfAdapter adapter) {
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
	 * 選択されたコンテンツ情報取得
	 * 
	 * @param con
	 *            Context
	 * @param data
	 *            コンテンツ情報一覧
	 * @param adapter
	 *            アダプター
	 * @param position
	 *            選択されたコンテンツのリストポジション
	 * @return 選択されたコンテンツ情報
	 */
	public BookBeans getBook(Context con, ArrayList<Map<String, Object>> data,
			WebBookShelfAdapter adapter, int position) {
		BookBeans book = null;
		if (data == null) {
			data = getWebBooksData(con);
		}
		if (data != null) {
			// コンテンツ情報取得
			Map<String, Object> bookMap = data.get(position);
			final Long rowID = (Long) bookMap.get(ID);
			if (rowID != -1) {
				if (mDao == null)
					mDao = new ContentsDao(con);
				book = mDao.load(rowID);
			}
		}
		if (book == null) {
			Logput.v(">Click Contents is null.");
		}
		return book;
	}

	/**
	 * DBからコンテンツ情報取得
	 */
	public ArrayList<Map<String, Object>> getWebBooksData(Context con) {
		// テンプレートのリスト取得
		ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		// storeテーブルから販売サイト一覧を取得
		if (mDao == null) mDao = new ContentsDao(con);
		ArrayList<BookBeans> BooksList = mDao.loadList(Preferences.sSort_web);

		if (BooksList != null) {
			for (BookBeans book : BooksList) {
				if (checkFilter(book)) {
					map = new HashMap<String, Object>();
					map.put(ID, book.getRowId());
					String thumbURL = book.getThumb_DL_URL();
					if (StringUtil.isEmpty(thumbURL)) {
						// サムネイルダウンロードURLがNULLの場合はセットする
						thumbURL = getThumbDL_URL(con, book.getContents_id());
					}
					map.put(THUMB_URL, thumbURL);
//					Logput.v(map.toString());
					data.add(map);
				}
			}
		}
		return data;
	}

	/**
	 * InvalidPlatform,閲覧期限、閲覧回数エラーがあった場合はエラー表示文字列を返す
	 * 
	 * @param con
	 *            Context
	 * @param book
	 *            Book
	 * @return エラー表示文字列
	 */
	public String getStatus(Context con, BookBeans book) {
		String status = "";
		if (book != null) {
			switch (check_WebBookShelf_DL(book)) {
			case FILE_TYPE_NG: // ファイルタイプがkrpdf,epub,krepub,krepa以外
				status = con.getString(R.string.filetype_error);
				break;
			case EXPIRYDATE_NG: // 閲覧期限が切れている
				status = con.getString(R.string.expiry_date_error);
				break;
			case SHARED_DEVICE_NG: // 残り共有台数が0の場合(SharedDevice)
				status = con.getString(R.string.shared_device_error);
				break;
			case INVALID_PLATFORM_NG: // Androidでの閲覧が許可されていない
				status = con.getString(R.string.invalid_platform_error);
				break;
			case DOWNLOADED: // 既にダウンロード済
				status = con.getString(R.string.downloaded);
				break;
			}
		}
		return status;
	}

	/**
	 * リストに表示するかどうかチェック
	 * 
	 * @param book
	 *            コンテンツ情報
	 * @return リストに表示する場合はtrue,その他はfalseを返す
	 */
	private boolean checkFilter(BookBeans book) {
		if (Preferences.sListFilter == VIEW_DELETE_CONTENTS) {
			// 未ダウンロードコンテンツのみ表示する場合
			String path = book.getFile_path();
			if (Utils.checkFile(path)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * ★contentsIDからサムネイルダウンロードURLを取得
	 * 
	 * @param context
	 * @param contentsID
	 * @return サムネイルダウンロードURL
	 */
	public String getThumbDL_URL(Context con, String contentsID) {
		if (StringUtil.isEmpty(contentsID))
			return null;
		String s3host = Preferences.sS3Host;
		String bucketName = Preferences.sBucketName;

		if (StringUtil.isEmpty(s3host) || StringUtil.isEmpty(bucketName)) {
			// GetAwsInfoリクエストを行う(S3Host,BucketNameを取得)
			GetAwsInfo getAwsInfo = new GetAwsInfo();
			getAwsInfo.execute(con);
		}
		// contentsIDの先頭二文字を取得
		String key = contentsID.substring(0, 2);
		String url = "http://" + bucketName + "." + s3host + "/contents/" + key
				+ "/" + contentsID + "_thumb";
		return url;
	}

	/**
	 * コンテンツが長押しされた時表示されるダイアログセット
	 * 
	 * @param con
	 *            Context
	 * @param list_group
	 *            ListView
	 * @param data
	 *            コンテンツデータ
	 * @param adapter
	 *            アダプター
	 * @param position
	 *            クリックされたコンテンツポジション
	 * @return ダイヤログ
	 */
	public AlertDialog.Builder setListClickDialog(Context con,
			ArrayList<Map<String, Object>> data, WebBookShelfAdapter adapter, int position) {
		AlertDialog.Builder dialog = null;
		// 長押し選択したアイテムのコンテンツ詳細ダイアログ表示（削除ボタン有）
		BookBeans book = getBook(con, data, adapter, position);
		if (book != null) {
			// 詳細情報文字列取得
			String detailStr = getDetail_webBookShelf(con, book);
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
	 * 指定コンテンツ詳細情報文字列を返す
	 * 
	 * @param BookBeans
	 *            指定コンテンツ
	 * @return 詳細情報文字列
	 */
	public String getDetail_webBookShelf(Context con, BookBeans book) {
		String title = setDetailValue(con.getString(R.string.detail_title),
				book.getTitle());
		String author = setDetailValue(con.getString(R.string.detail_auther),
				book.getAuthor());
		String distributor_name = setDetailValue(
				con.getString(R.string.detail_distributor_name),
				book.getDistributor_name());
		String distributor_url = setDetailValue(
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
		String sharedDevice = setDetail_SharedDevice(
				con.getString(R.string.unlimited),
				con.getString(R.string.detail_shared_device), book);

		String detail = title + "<br>" + author + "<br>" + distributor_name
				+ "<br>" + distributor_url + "<br>" + downloadDate + "<br>"
				+ lastAccessDate + "<br>" + sharedDevice + "<br>" + expiryDate;
		return detail;
	}

	/**
	 * SharedDeviceを詳細表示用文字列に
	 * 
	 * @param title
	 *            詳細表示見出し
	 * @param book
	 *            コンテンツ情報
	 * @return SharedDevice表示用文字列
	 */
	public String setDetail_SharedDevice(String unlimited, String title,
			BookBeans book) {
		int shared_d = book.getSharedDevice_D();
		String sharedDevice = unlimited;
		if (shared_d != -1) {
			sharedDevice = book.getSharedDevice_M() + "/" + shared_d;
		}
		return title + sharedDevice;
	}

	/**
	 * Web書庫からダウンロードするコンテンツチェック
	 * 
	 * @param BookBeans
	 * @return ステータス(問題なければ-1を返す)
	 */
	public int check_WebBookShelf_DL(BookBeans book) {
		int status = -1;
		String invalidPlatform = book.getInvalidPlatform();
		//long rowID = book.getRowId();
		if (!isDownloaded(book)) {
			status = DOWNLOADED;
			//Logput.d("[" + rowID + "] ... DOWNLOADED");
		} else if (!check_FileType(book.getType())) {
			status = FILE_TYPE_NG;
			//Logput.d("[" + rowID + "] ... FILE_TYPE_NG:" + book.getType());
		} else if (!check_InvalidPlatform(invalidPlatform)) {
			status = INVALID_PLATFORM_NG;
			//Logput.d("[" + rowID + "] ... INVALID_PLATFORM_NG:" + invalidPlatform);
		} else if (!check_ExpiryDate(book.getExpiry_date())) {
			status = EXPIRYDATE_NG;
			//Logput.d("[" + rowID + "] ... EXPIRYDATE_NG");
		} else if (!check_SharedDevice(book)) {
			status = SHARED_DEVICE_NG;
			//Logput.d("[" + rowID + "] ... SHARED_DEVICE_NG:" + book.getSharedDevice_M() + "/" + book.getSharedDevice_D());
		}
		return status;
	}

	/**
	 * ファイルタイプチェック
	 * 
	 * @param type ファイルタイプ
	 * @return 閲覧可能なファイルタイプの場合はtrue,それ以外はfalseを返す
	 */
	private boolean check_FileType(String type) {
		if (!StringUtil.isEmpty(type)) {
			if (Utils.isKrpdf(type) || Utils.isEPub(type) || Utils.isKrEPA(type)
					|| Utils.isKrEPub(type) || Utils.isBec(type)
					|| Utils.isKrbec(type) || Utils.isPdf(type) || 
					Utils.isMcm(type) || Utils.isMccomic(type)|| Utils.isMcbook(type) || Utils.isKrpdfx(type))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * 共有台数チェック
	 * 
	 * @param book
	 * @return 残り共有台数が０の場合はfalse,その他はtrueを返す
	 */
	private boolean check_SharedDevice(BookBeans book) {
		int sharedDevice_m = book.getSharedDevice_M();
		int sharedDevice_d = book.getSharedDevice_D();
		// 共有台数制限がなければtrueを返す
		if (sharedDevice_d == -1)
			return true;
		else if (sharedDevice_m == 0) {
			// 共有台数設定が0の場合はfalseを返す
			return false;
		} else {
			return true;
		}
	}

	/**
	 * ダウンロード済のものではないかチェック
	 * 
	 * @param book
	 * @return ダウンロード済ならfalse,未ならtrueを返す
	 */
	private boolean isDownloaded(BookBeans book) {
		boolean isDownloaded = false;
		String filePath = book.getFile_path();
		if (!Utils.checkFile(filePath)) {
			String contentsID = book.getContents_id();
			if (!StringUtil.isEmpty(contentsID)) {
				isDownloaded = true;
			}
		}
		return isDownloaded;
	}
}
