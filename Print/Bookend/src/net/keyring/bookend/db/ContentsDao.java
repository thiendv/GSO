package net.keyring.bookend.db;

import java.util.ArrayList;
import java.util.List;

import net.keyring.bookend.bean.BookBeans;
import net.keyring.bookend.constant.Const;
import net.keyring.bookend.constant.ConstDB;
import net.keyring.bookend.constant.ConstQuery;
import net.keyring.bookend.util.DateUtil;
import net.keyring.bookend.util.StringUtil;
import net.keyring.bookend.util.Utils;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteException;
/**
 * contentsテーブル呼び出し処理クラス
 * @author Hamaji
 *
 */
public class ContentsDao implements ConstDB, Const {

	/** SQLiteEngine */
	private SQLiteEngine db;
	
	/**
	 * コンストラクタ
	 * @param con Context
	 */
	public ContentsDao(Context con){
		db = ContentsDBAccess.getInstance(con);
	}
	
	/**
	 * 指定テーブルレコード数を返す
	 * @param tableName 指定テーブル名
	 * @return レコード数
	 */
	public int getCount(){
		return db.count(CONTENTS_TABLE);
	}
	
	// ------------- SELECT ---------------------
	/**
	 * 指定ダウンロードIDのコンテンツが登録されているrowIDを返す
	 * @param downloadID ダウンロードID
	 * @return rowID(未登録の場合は-1)
	 */
	public long getRowID(String downloadID){
		BookBeans book = select(downloadID);
		if(book != null){
			return book.getRowId();
		}else{
			return -1;
		}
	}
	
	/**
	 * リスト情報を全て取得
	 *
	 * @param rowId プライマリキー
	 * @return ロード結果
	 * @throws Exception
	 */
	public ArrayList<BookBeans> loadList(int sort) throws SQLiteException {
		ArrayList<BookBeans> valuesList = null;
		// SELECT --------------
		// groupBy GROUPBY句
		// having HAVING句
		// orderBy ORDERBY句
		valuesList = db.selectList(CONTENTS_TABLE, DELETE_FLAG + " = '" + FALSE + "'" , getOrderBy(sort));
		return valuesList;
	}
	
	/**
	 * Web書庫からDL中のコンテンツ取得
	 * @return Web書庫からダウンロード中コンテンツbean
	 * @throws SQLiteException
	 */
	public ArrayList<BookBeans> loadDownloadingContents_web() throws SQLiteException{
		return db.selectList(CONTENTS_TABLE, DL_STATUS + " BETWEEN " + STATUS_WAIT_FOR_DL_WEB + " AND " + STATUS_DL_ERROR_WEB, null);
	}
	
	/**
	 * idで指定リスト情報を取得
	 *
	 * @param rowId プライマリキー
	 * @return ロード結果
	 * @throws Exception
	 */
	public BookBeans load(long rowID) throws SQLiteException {
		BookBeans values = null;
		// SELECT --------------
		// columns カラム名
		// selection WHERE句
		// selectionArgs selectionの「？」を置き換えする文字列
		// groupBy GROUPBY句
		// having HAVING句
		// orderBy ORDERBY句
		values = db.select(CONTENTS_TABLE, null, ID + "=?", new String[] {String.valueOf(rowID)}, null, null, null);
		return values;
	}
	
	/**
	 * Select - sort方法からOrderBy文字列を取得
	 * @return OrderBy文字列
	 */
	private String getOrderBy(int sort){
		String orderBy = null;
		switch(sort){
		case DESCEND_DATE: // 取得日時：降順...初期表示    (ID順)
			orderBy = DOWNLOAD_DATE + " DESC";
			break;
		case ASCEND_DATE: // 取得日時：昇順   (ID順)
			orderBy = DOWNLOAD_DATE + " ASC";
			break;
		case DESCEND_NAME: // タイトル名：降順
			orderBy = TITLE + " DESC";
			break;
		case ASCEND_NAME: // タイトル名：昇順
			orderBy = TITLE + " ASC";
			break;
		}
		return orderBy;
	}
	
	/**
	 * contentsテーブルに登録されている指定ダウンロードIDコンテンツを返す
	 * @param downloadID
	 * @return Book
	 */
	public BookBeans select(String downloadID){
		return db.select(CONTENTS_TABLE, null, DOWNLOAD_ID + "='" + downloadID + "'", null, null, null, null);
	}
	
	/**
	 * 指定ダウンロードIDコンテンツが登録されているかチェック
	 * @param downloadID ダウンロードID
	 * @return 指定ダウンロードIDのコンテンツが登録されていた場合はtrue,それ以外はfalseを返す
	 */
	public boolean isRegist(String downloadID){
		BookBeans book = select(downloadID);
		if(book == null){
			return true;
		}else{
			return false;
		}
	}
	
	// --------------- DELETE --------------------
	/**
	 * contentsテーブル削除
	 */
	public void drop(){
		// DB削除
		db.drop(DROP_CONTENTS_TABLE);
	}
	
	/**
	 * 指定レコードDELETE(rowID=-1の場合はテーブル削除)
	 * @param rowID
	 */
	public void delete(long rowID){
		if(rowID == -1){
			db.delete(CONTENTS_TABLE, null);
		}else{
			db.delete(CONTENTS_TABLE, ID + "=" + rowID);
		}
	}
	
	/**
	 * 指定レコードDELETE(nullの場合はテーブル削除)
	 * @param columnName 指定カラム名
	 * @param key 指定文字列
	 */
	public void delete(String columnName, String key){
		if(StringUtil.isEmpty(key)){
			db.delete(CONTENTS_TABLE, null);
		}else{
			db.delete(CONTENTS_TABLE, columnName + "=" + key);
		}
	}
	
	// --------------- INSERT・UPDATE共通 --------------------
	
	/**
	 * ファイルタイプチェック
	 * @param type
	 * @return ファイルタイプ
	 */
	private String checkType(String type){
		if(type.equals("1")) type = "pdf";
		else if(type.equals("2")) type = ConstQuery.KRPDF;
		else if(type.equals("3")) type = "xmdf";
		else if(type.equals("4")) type = "book";
		else if(type.equals("5")) type = "krm";
		else if(type.equals("6")) type = ConstQuery.EPUB;
		else if(type.equals("7")) type = ConstQuery.KREPUB;
		else if(type.equals("8")) type = ConstQuery.BEC;
		else if(type.equals("9")) type = ConstQuery.KRBEC;
		else if(type.equals("11")) type = ConstQuery.MCMAGAZINE;
		else if(type.equals("12")) type = ConstQuery.MCBOOK;
		else if(type.equals("13")) type = ConstQuery.MCCOMIC;
		else if(type.equals("14")) type = ConstQuery.KREPA;
		return type;
	}
	
	/**
	 * contentsテーブルに保存 rowidが-1の場合はinsert、rowidが1以上の場合はupdate<br>
	 * ※ UPDATE時：nullの場合も上書きされる
	 *
	 * @param book 保存対象のオブジェクト
	 * @return 保存結果
	 */
	public BookBeans save(BookBeans book) {
		if(book == null) return null;
		BookBeans result = null;
		String lastAccessDate = book.getLast_access_date();
		String downloadID = book.getDownload_id();
		String lastModify = book.getLastModify();
		String deleteFlag = book.getDeleteFlag();
		ContentValues values = new ContentValues();
		// UPDATE時：nullの場合も上書きされる
		values.put(LAST_ACCESS_DATE, lastAccessDate);
		values.put(KEYWORDS, book.getKeywords());
		values.put(FILE_PATH, book.getFile_path());
		values.put(CONTENTS_DL_URL, book.getContents_DL_URL());
		values.put(DISTRIBUTOR_NAME, book.getDistributor_name());
		values.put(DOWNLOAD_DATE, book.getDownload_date());
		values.put(CONTENTS_ID, book.getContents_id());
		values.put(DISTRIBUTOR_URL, book.getDistributor_url());
		values.put(TYPE, book.getType());
		values.put(AUTHOR, book.getAuthor());
		values.put(TITLE, book.getTitle());
		values.put(DOWNLOAD_ID, downloadID);
		values.put(DELETE_FLAG, deleteFlag);
		values.put(THUMB_PATH, book.getThumb_path());
		values.put(ENCRYPTION_KEY, book.getEncryptionKey());
		// KRPDF暗号化復号化時のみ読み書きする
		//values.put(ConstDB.KRPDF_FORMAT_VER, book.getKrpdfFormatVer());
		values.put(THUMB_DL_URL, book.getThumb_DL_URL());
		values.put(EXPIRY_DATE, book.getExpiry_date());
		values.put(INVALID_PLATFORM, book.getInvalidPlatform());
		values.put(LAST_MODIFY, lastModify);
		values.put(LABEL_ID, book.getLabelID());
		values.put(SALES_ID, book.getSalesID());
		values.put(ORIGINAL_FILE_NAME, book.getOriginalFileName());
		values.put(PAGE_COUNT, book.getPageCount());
		values.put(SHARED_DEVICE_D, book.getSharedDevice_D());
		values.put(SHARED_DEVICE_M, book.getSharedDevice_M());
		values.put(BROWSE_D, book.getBrowse_D());
		values.put(BROWSE_M, book.getBrowse_M());
		values.put(PRINT_D, book.getPrint_D());
		values.put(PRINT_M, book.getPrint_M());
		values.put(CRC32, book.getCRC32());
		values.put(MAIN_VIEW, book.getMainView());
		values.put(OWNER_PASSWORD, book.getKey());

		Long rowID = book.getRowId();
		// IDが-1の場合はinsert
		if (rowID == -1) {
			int count = db.insert(CONTENTS_TABLE, values);
			if(count != -1){
				result = db.select(CONTENTS_TABLE, null, DOWNLOAD_ID + "='" + downloadID + "'", null, null, null, null);
			}
		} else {
			db.update(CONTENTS_TABLE, rowID, values);
			result = load(rowID);
		}
		return result;
	}
	
	/**
	 * contentsテーブルに保存 rowidが-1の場合はinsert、rowidが1以上の場合はupdate<br>
	 * ※ UPDATE時：nullの場合も上書きされる
	 *
	 * @param bookList 保存対象のオブジェクトリスト
	 * @return 保存件数
	 */
	public int save(List<BookBeans> bookList) {
		int insert_count = -1;
		int update_count = -1;
		if(bookList == null) return -1;
		
		List<ContentValues> insertList = null;
		List<ContentValues> updateList = null;
		for(BookBeans book : bookList){
			Long rowID = book.getRowId();
			String lastAccessDate = book.getLast_access_date();
			String downloadID = book.getDownload_id();
			String lastModify = book.getLastModify();
			String deleteFlag = book.getDeleteFlag();
			ContentValues values = new ContentValues();
			// UPDATE時：nullの場合も上書きされる
			values.put(LAST_ACCESS_DATE, lastAccessDate);
			values.put(KEYWORDS, book.getKeywords());
			values.put(FILE_PATH, book.getFile_path());
			values.put(CONTENTS_DL_URL, book.getContents_DL_URL());
			values.put(DISTRIBUTOR_NAME, book.getDistributor_name());
			values.put(DOWNLOAD_DATE, book.getDownload_date());
			values.put(CONTENTS_ID, book.getContents_id());
			values.put(DISTRIBUTOR_URL, book.getDistributor_url());
			values.put(TYPE, book.getType());
			values.put(AUTHOR, book.getAuthor());
			values.put(TITLE, book.getTitle());
			values.put(DOWNLOAD_ID, downloadID);
			values.put(DELETE_FLAG, deleteFlag);
			values.put(THUMB_PATH, book.getThumb_path());
			values.put(ENCRYPTION_KEY, book.getEncryptionKey());
			values.put(THUMB_DL_URL, book.getThumb_DL_URL());
			values.put(EXPIRY_DATE, book.getExpiry_date());
			values.put(INVALID_PLATFORM, book.getInvalidPlatform());
			values.put(LAST_MODIFY, lastModify);
			values.put(LABEL_ID, book.getLabelID());
			values.put(SALES_ID, book.getSalesID());
			values.put(ORIGINAL_FILE_NAME, book.getOriginalFileName());
			values.put(PAGE_COUNT, book.getPageCount());
			values.put(SHARED_DEVICE_D, book.getSharedDevice_D());
			values.put(SHARED_DEVICE_M, book.getSharedDevice_M());
			values.put(BROWSE_D, book.getBrowse_D());
			values.put(BROWSE_M, book.getBrowse_M());
			values.put(PRINT_D, book.getPrint_D());
			values.put(PRINT_M, book.getPrint_M());
			values.put(CRC32, book.getCRC32());
			values.put(MAIN_VIEW, book.getMainView());
			values.put(OWNER_PASSWORD, book.getKey());
			
			if(rowID == -1){
				if(insertList == null) insertList = new ArrayList<ContentValues>();
				insertList.add(values);
			}else{
				values.put(ID, rowID);
				if(updateList == null) updateList = new ArrayList<ContentValues>();
				updateList.add(values);
			}
		}
		
		if(insertList != null){
			insert_count = db.insert(CONTENTS_TABLE, insertList);
		}
		if(updateList != null){
			update_count = db.update(CONTENTS_TABLE, updateList);
		}
		return insert_count + update_count;
	}
	
	
	// --------------- INSERT --------------------
	
	/**
	 * contentsテーブルINSERTセット...新規ダウンロード時
	 * @param downloadID ダウンロードID 必須
	 * @param contentsID コンテンツID 必須
	 * @param fileType ファイルタイプ
	 * @param title コンテンツタイトル
	 * @param author 著者名
	 * @param keyword キーワード
	 * @param distributor_name 配布者名
	 * @param distributor_url 配布者サイトURL
	 * @param filePath コンテンツ保存先パス
	 * @param thumbPath サムネイル保存先パス
	 * @param contentsDL_URL コンテンツダウンロードURL
	 * @param thumbDL_URL サムネイルダウンロードURL
	 * @param downloadDate ダウンロード日時
	 * @param expiryDate 閲覧期限日時
	 * @param invalidPlatform 閲覧許可しないOS
	 * @param sharedDevice_d 共有台数制限
	 * @param browse_d 閲覧回数制限
	 * @param print_d 印刷回数制限
	 * @return book Book DBinsert用にセットしたコンテンツ情報
	 */
	public BookBeans setBook(String downloadID, String contentsID, String fileType, String title, String author,
			String keyword, String distributor_name, String distributor_url, String filePath, String thumbPath,
			String contentsDL_URL, String thumbDL_URL, String downloadDate, String expiryDate,
			String invalidPlatform, int sharedDevice_d, int browse_d, int print_d){
		BookBeans book = null;
		
		if(StringUtil.isEmpty(downloadID) || StringUtil.isEmpty(contentsID) || StringUtil.isEmpty(fileType)){
			// ダウンロードID,コンテンツID,ファイルタイプは必須
			return null;
		}else{
			// 閲覧期限無制限の場合 "9999-12-31 23:59:59"
			if(StringUtil.isEmpty(expiryDate)){
				expiryDate = DATE_MAX;
			}else if(expiryDate.equals("-1")){
				expiryDate = DATE_MAX;
			}
			// 更新日時取得(新規ダウンロードの場合はダウンロード日時と同じに)
			String lastModify = downloadDate;
			// 閲覧が許可されていないプラットフォームがnullの場合
			if(StringUtil.isEmpty(invalidPlatform)) invalidPlatform = NONE;
			String lastAccessDate = DATE_MAX;
			int sharedDevice_m = -1;
			int browse_m = -1;
			int print_m = -1;
			// 無制限ではない場合は制限数と同じに
			if(sharedDevice_d != -1){
				// sharedDeviceはダウンロードした時点で１つ減る
				sharedDevice_m = sharedDevice_d -1;
			}
			if(browse_d != -1){
				browse_m = browse_d;
			}
			if(print_d != -1){
				print_m = print_d;
			}
			
			book = new BookBeans();
			book.setDownload_id(downloadID);
			book.setContents_id(contentsID);
			book.setType(checkType(fileType));
			book.setTitle(title);
			book.setAuthor(author);
			book.setKeywords(keyword);
			book.setDistributor_name(distributor_name);
			book.setDistributor_url(distributor_url);
			book.setFile_path(filePath);
			book.setThumb_path(thumbPath);
			book.setContents_DL_URL(contentsDL_URL);
			book.setThumb_DL_URL(thumbDL_URL);
			book.setDownload_date(downloadDate);
			book.setLast_access_date(lastAccessDate);
			book.setExpiry_date(expiryDate);
			book.setInvalidPlatform(invalidPlatform);
			book.setLastModify(lastModify);
			book.setSharedDevice_D(sharedDevice_d);
			book.setSharedDevice_M(sharedDevice_m);
			book.setBrowse_D(browse_d);
			book.setBrowse_M(browse_m);
			book.setPrint_D(print_d);
			book.setPrint_M(print_m);
			book.setMainView(MAIN_VIEW_FLAG);
			book.setDeleteFlag(FALSE);
		}
		
		return book;
	}
	
	/**
	 * contentsテーブルINSERTセット...GetContents(NewContentsList)用
	 * @param downloadID ダウンロードID
	 * @param contentsID コンテンツID
	 * @param fileType ファイルタイプ
	 * @param title タイトル
	 * @param author 著者名
	 * @param keywords キーワード
	 * @param distributorName 配布者名
	 * @param distributorURL 配布者サイトURL 
	 * @param fileSize ファイルサイズ
	 * @param originalFileName オリジナルファイル名
	 * @param pageCount ページ数...int
	 * @param downloadDate ダウンロード日時
	 * @param lastAccessDate 最終閲覧日時
	 * @param lastModify クライアントでの最終更新日時
	 * @param crc32 CRC32
	 * @param labelID ラベルID
	 * @param salesID セールスID
	 * @param contents_DL_URL コンテンツダウンロードURL
	 * @param thumb_DL_URL サムネイルダウンロードURL
	 * @return　book Book insertコンテンツ情報
	 */
	public BookBeans setBook(String downloadID, String contentsID, String fileType, String title, String author,
			String keywords, String distributorName, String distributorURL, String fileSize,
			String originalFileName, int pageCount, String downloadDate, String lastAccessDate,
			String lastModify, String crc32, String labelID, String salesID, String contentsDL_URL,
			String thumbDL_URL){
		BookBeans book = null;
		
		if(StringUtil.isEmpty(downloadID) || StringUtil.isEmpty(contentsID) || StringUtil.isEmpty(fileType)){
			// ダウンロードID,コンテンツID,ファイルタイプは必須
			return null;
		}else{
			if(StringUtil.isEmpty(lastModify)) lastModify = DateUtil.getNowUTC();
			// 既に登録されているものかどうかチェック
			book = select(downloadID);
			
			int mainView = MAIN_VIEW_NONE_FLAG;
			String expiryDate = DATE_MAX;
			if(book == null){
				book = new BookBeans();
			}else{
				if(Utils.checkFile(book.getFile_path())){
					mainView = MAIN_VIEW_FLAG;
				}
				expiryDate = book.getExpiry_date();
			}
			// ページ数0以下は全て0に
			if(pageCount<0) pageCount = 0;
			// ダウンロード日時がnullの場合(きっとそんなことはないはず)
			if(StringUtil.isEmpty(downloadDate)) downloadDate = DATE_MAX;
			// 最終アクセス日時がnullの場合
			if(StringUtil.isEmpty(lastAccessDate)) lastAccessDate = DATE_MAX;
			
			
			book.setDownload_id(downloadID);
			book.setContents_id(contentsID);
			book.setType(checkType(fileType));
			book.setTitle(title);
			book.setAuthor(author);
			book.setKeywords(keywords);
			book.setDistributor_name(distributorName);
			book.setDistributor_url(distributorURL);
			book.setFileSize(fileSize);
			book.setOriginalFileName(originalFileName);
			book.setPageCount(pageCount);
			book.setContents_DL_URL(contentsDL_URL);
			book.setThumb_DL_URL(thumbDL_URL);
			book.setDownload_date(downloadDate);
			book.setLast_access_date(lastAccessDate);
			book.setLastModify(lastModify);
			book.setSalesID(salesID);
			book.setLabelID(labelID);
			book.setCRC32(crc32);
			book.setExpiry_date(expiryDate);
			book.setMainView(mainView);
			book.setDeleteFlag(FALSE);
		}
		return book;
	}
	
	// ------------------ UPDATE --------------------
	
	/**
	 * ダウンロードステータスアップデート
	 * @param rowID
	 * @param status ダウンロードステータス
	 * @param filePath 保存先ファイルパス
	 * @param progress ダウンロード進捗
	 */
	public void updateDLstatus(long rowID, int status, String filePath, String thumbPath, int progress){
		if(status < 0 || status > 4 || rowID <= -1) return;
		// movieテーブルステータス更新
		ContentValues values = new ContentValues();
		values.put(DL_STATUS, status);
		values.put(DL_PROGRESS, progress);
		values.put(FILE_PATH, filePath);
		values.put(THUMB_PATH, thumbPath);
		if(filePath == null){
			values.put(MAIN_VIEW, MAIN_VIEW_NONE_FLAG);
		}
		db.update(CONTENTS_TABLE, ID + "=" + rowID, values);
	}
	
	/**
	 * contentsテーブルUPDATEセット...GetContentsInfo用
	 * @param rowID
	 * @param fileType
	 * @param title
	 * @param author
	 * @param keywords
	 * @param distributorName
	 * @param distributorURL
	 * @param fileSize
	 * @param originalFileName
	 * @param page
	 * @param contentsURL
	 * @param thumbURL
	 * @param crc32
	 * @param salesID
	 * @return
	 */
	public BookBeans setBook(long rowID, String fileType, String title, String author, String keywords,
			 String distributorName, String distributorURL, String fileSize, String originalFileName,
			 int page, String contentsURL, String thumbURL, String crc32, String salesID){
		if(rowID <= -1) return null;
		
		BookBeans book = load(rowID);
		book.setType(checkType(fileType));
		book.setTitle(title);
		book.setAuthor(author);
		book.setKeywords(keywords);
		book.setDistributor_name(distributorName);
		book.setDistributor_url(distributorURL);
		book.setFileSize(fileSize);
		book.setOriginalFileName(originalFileName);
		book.setPageCount(page);
		book.setContents_DL_URL(contentsURL);
		book.setThumb_DL_URL(thumbURL);
		book.setCRC32(crc32);
		book.setSalesID(salesID);
		return book;
	}
	
	
	/**
	 * contentsテーブルUPDATEセット...GetContents(License)用
	 * @param downloadID ダウンロードID
	 * @param key オーナーキー
	 * @param sharedDevice_m 共有台数残
	 * @param sharedDevice_d 共有台数制限
	 * @param browse_m 閲覧回数残
	 * @param browse_d 閲覧回数制限
	 * @param print_m 印刷回数残
	 * @param print_d 印刷回数制限
	 * @param invalidPlatform 閲覧を許可しないプラットフォーム
	 * @param expiryDate 閲覧期限
	 * @return book
	 */
	public BookBeans setBook(String downloadID, String key, int sharedDevice_m, int sharedDevice_d,
			int browse_m, int browse_d, int print_m, int print_d, String invalidPlatform, String expiryDate){
		BookBeans book = null;
		if(StringUtil.isEmpty(downloadID)){
			// ダウンロードIDは必須
			return null;
		}else{
			// 既に登録されているものかどうかチェック
			book = select(downloadID);
			if(book != null){
				book.setKey(key);
				book.setSharedDevice_M(sharedDevice_m);
				book.setSharedDevice_D(sharedDevice_d);
				book.setBrowse_M(browse_m);
				book.setBrowse_D(browse_d);
				book.setPrint_M(print_m);
				book.setPrint_D(print_d);
				if(StringUtil.isEmpty(invalidPlatform)){
					invalidPlatform = NONE;
				}
				book.setInvalidPlatform(invalidPlatform);

				if(StringUtil.isEmpty(expiryDate)){
					expiryDate = DATE_MAX;
				}
				book.setExpiry_date(expiryDate);
			}
		}
		return book;
	}
	
	/**
	 * 指定カラムUPDATE
	 * @param rowID RowID
	 * @param colmunName 更新したいカラム名
	 * @param value 更新情報 ...String
	 * @return 問題なくUPDATEできた場合はtrue,それ以外はfalseを返す
	 */
	public boolean updateColmun(long rowID, String colmunName, String value){
		String lastModify = DateUtil.getNowUTC();
		
		ContentValues values = new ContentValues();
		values.put(colmunName, value);
		values.put(LAST_MODIFY, lastModify);
		
		int updateCount = db.update(CONTENTS_TABLE, ID + " = '"+rowID+"'", values);
		if(updateCount == -1){
			return false;
		}else{
			return true;
		}
	}
	/**
	 * 指定カラムUPDATE
	 * @param rowID RowID
	 * @param colmunName 更新したいカラム名
	 * @param value 更新情報 ...int
	 * @return 問題なくUPDATEできた場合はtrue,それ以外はfalseを返す
	 */
	public boolean updateColmun(long rowID, String colmunName, int value){
		String lastModify = DateUtil.getNowUTC();
		
		ContentValues values = new ContentValues();
		values.put(colmunName, value);
		values.put(LAST_MODIFY, lastModify);
		
		int updateCount = db.update(CONTENTS_TABLE, ID + " = '"+rowID+"'", values);
		if(updateCount == -1){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * コンテンツ削除ボタン押下時の処理
	 * @param rowID 指定コンテンツID
	 * @return 問題なくUPDATEできた場合はtrue,それ以外はfalseを返す
	 */
	public boolean updateDelete(long rowID){
		String lastModify = DateUtil.getNowUTC();
		ContentValues values = new ContentValues();
		values.putNull(FILE_PATH);
		values.putNull(THUMB_PATH);
		values.put(MAIN_VIEW, MAIN_VIEW_NONE_FLAG);
		values.put(LAST_MODIFY, lastModify);
		values.putNull(ENCRYPTION_KEY);
		values.put(DL_STATUS, STATUS_INIT);
		values.put(DL_PROGRESS, 0);
		int updateCount = db.update(CONTENTS_TABLE, ID + " = '"+rowID+"'", values);
		if(updateCount == -1){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * KRPDF暗号化鍵と暗号化フォーマットバージョンを更新
	 * @param rowID コンテンツID
	 * @param key KRPDF暗号化鍵
	 * @return　問題なくUPDATEできた場合はtrue,それ以外はfalseを返す
	 */
	public boolean updateKrpdfKey(long rowID, String key){
		String lastModify = DateUtil.getNowUTC();
		
		ContentValues values = new ContentValues();
		values.put(ENCRYPTION_KEY, key);
		values.put(ConstDB.KRPDF_FORMAT_VER, Const.KRPDF_FORMAT_VER);
		values.put(LAST_MODIFY, lastModify);
		
		int updateCount = db.update(CONTENTS_TABLE, ID + " = '"+rowID+"'", values);
		if(updateCount == -1){
			return false;
		}else{
			return true;
		}
	}
}
