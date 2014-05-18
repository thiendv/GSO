package net.keyring.bookend.db;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.keyring.bookend.Logput;
import net.keyring.bookend.bean.BookBeans;
import net.keyring.bookend.bean.NaviSalesBeans;
import net.keyring.bookend.bean.PurchaseBeans;
import net.keyring.bookend.constant.ConstDB;
import net.keyring.bookend.util.FileUtil;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteAbortException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.database.sqlite.SQLiteDiskIOException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 
 * @author Hamaji
 *
 */
public abstract class SQLiteEngine extends SQLiteOpenHelper implements ConstDB {

	/** SQLエンジン */
	protected static SQLiteEngine sEngine;
	/** DB */
	private SQLiteDatabase mDB;
	
	/**
	 * コンストラクタ
	 *
	 * @param データベースのバージョン情報
	 */
	public SQLiteEngine(Context context) {
		// 与えられたバージョン情報と実際に存在するデータベースのバージョンが異なる場合はonUpgradeメソッドが呼び出される
		super(context, DB, null, DB_VERSION);
	}
	
	//***********************************
	// DROP
	//***********************************
	
	/**
	 * テーブル削除
	 * @param dropSQL 削除SQL文字列
	 * @throws SQLException
	 */
	public void drop(String dropSQL) throws SQLException{
		mDB.beginTransaction();
		try{
			// DB削除
			mDB.execSQL(dropSQL);
			mDB.setTransactionSuccessful();
		}finally{
			mDB.endTransaction();
		}
	}
	
	//***********************************
	// Insert
	//***********************************
	
	/**
	 * データを挿入する
	 * @param table 挿入するテーブル名
	 * @return 挿入したレコード数
	 */
	public int insert(String table, ContentValues values){
		int count = -1;
		try{
			// DBオープン(書き込み)
			mDB = sEngine.getWritableDatabase();
		}catch(SQLiteException e){
			Logput.e("DB OPEN : INSERT ... " + e.getMessage(), e);
			return -1;
		}
		
		// トランザクション開始
		mDB.beginTransaction();
		long rowID = -1;
		try{
			// Insert
			if(values != null){
				rowID = mDB.insert(table, null, values);
				if(rowID != -1){
					count = 1;
					Logput.v(">INSERT [" + table + "] " + values.toString());
					// トランザクション - 成功フラグ
					mDB.setTransactionSuccessful();
				}else{
					Logput.v(">INSERT ERROR [" + table + "] " + values.toString());
				}
			}
		}catch(SQLiteConstraintException e){
			// 一意制約や外部キーなど制約条件に反したデータ操作を行おうとした場合に発生
			Logput.e(e.getMessage(), e);
		}catch(SQLiteDiskIOException e){
			// ディスク容量不足などのOSレベルでのファイルI/Oエラーが発生した場合...DBファイルが壊れている?
			Logput.e(e.getMessage(), e);
		}catch(SQLiteAbortException e){
			// なんらかの原因でSQLiteの処理が中断された場合
			Logput.e(e.getMessage(), e);
		}catch(SQLiteDatabaseCorruptException e){
			// DBファイルが壊れた場合
			Logput.e(e.getMessage(), e);
		}catch(SQLiteFullException e){
			// データ挿入時にDBファイルサイズの上限を超える（2GBがSQLiteの上限?)
			Logput.e(e.getMessage(), e);
		}catch(IllegalStateException e){
			// おかしな状態遷移が発生
			Logput.e(e.getMessage(), e);
		}catch(UnsupportedOperationException e){
			// サポートされていない操作が行われたときに発生
			Logput.e(e.getMessage(), e);
		}catch(SQLiteException e){
			// SQL構文エラー
			Logput.e(e.getMessage(), e);
		}finally{
			// トランザクション終了
			mDB.endTransaction();
			// DB閉じる
			//db.close();
		}
		return count;
	}
	
	/**
	 * データを挿入する
	 * @param table 挿入するテーブル名
	 * @return 挿入したレコード数
	 */
	public int insert(String table, List<ContentValues> booksList){
		int count = -1;
		try{
			// DBオープン(書き込み)
			mDB = sEngine.getWritableDatabase();
		}catch(SQLiteException e){
			Logput.e("DB OPEN : INSERT ... " + e.getMessage(), e);
			return -1;
		}
		
		// トランザクション開始
		mDB.beginTransaction();
		long rowID = -1;
		try{
			// Insert
			if(booksList != null){
				for(ContentValues book : booksList){
					rowID = mDB.insert(table, null, book);
					if(rowID != -1){
						Logput.v(">INSERT [" + table + "] " + book.toString());
						if(count == -1){
							count = 1;
						}else{
							count = count + 1;
						}
					}else{
						Logput.w(">INSERT ERROR [" + table + "] " + book.toString());
					}
				}
				if(booksList.size() == count){
					// トランザクション - 成功フラグ
					mDB.setTransactionSuccessful();
				}
			}
		}catch(SQLiteConstraintException e){
			// 一意制約や外部キーなど制約条件に反したデータ操作を行おうとした場合に発生
			Logput.e(e.getMessage(), e);
		}catch(SQLiteDiskIOException e){
			// ディスク容量不足などのOSレベルでのファイルI/Oエラーが発生した場合...DBファイルが壊れている?
			Logput.e(e.getMessage(), e);
		}catch(SQLiteAbortException e){
			// なんらかの原因でSQLiteの処理が中断された場合
			Logput.e(e.getMessage(), e);
		}catch(SQLiteDatabaseCorruptException e){
			// DBファイルが壊れた場合
			Logput.e(e.getMessage(), e);
		}catch(SQLiteFullException e){
			// データ挿入時にDBファイルサイズの上限を超える（2GBがSQLiteの上限?)
			Logput.e(e.getMessage(), e);
		}catch(IllegalStateException e){
			// おかしな状態遷移が発生
			Logput.e(e.getMessage(), e);
		}catch(UnsupportedOperationException e){
			// サポートされていない操作が行われたときに発生
			Logput.e(e.getMessage(), e);
		}catch(SQLiteException e){
			// SQL構文エラー
			Logput.e(e.getMessage(), e);
		}finally{
			// トランザクション終了
			mDB.endTransaction();
			// DB閉じる
			//db.close();
		}
		return count;
	}
	
	//***********************************
	// DELETE
	//***********************************
	
	/**
	 * テーブルのデータを削除
	 * @param table 削除するテーブル名
	 * @param whereClause 削除条件(WHERE句)
	 */
	public void delete(String table, String whereClause){
		try{
			// DBオープン(書き込み)
			mDB = sEngine.getWritableDatabase();
		}catch(SQLiteException e){
			Logput.e(e.getMessage(), e);
			return;
		}
		
		// トランザクション開始
		mDB.beginTransaction();
		try{
			// Delete
			mDB.delete(table, whereClause, null);
			mDB.setTransactionSuccessful();
		}catch(SQLiteConstraintException e){
			// 一意制約や外部キーなど制約条件に反したデータ操作を行おうとした場合に発生
			Logput.e(e.getMessage(), e);
		}catch(SQLiteDiskIOException e){
			// ディスク容量不足などのOSレベルでのファイルI/Oエラーが発生した場合...DBファイルが壊れている?
			Logput.e(e.getMessage(), e);
		}catch(SQLiteAbortException e){
			// なんらかの原因でSQLiteの処理が中断された場合
			Logput.e(e.getMessage(), e);
		}catch(SQLiteDatabaseCorruptException e){
			// DBファイルが壊れた場合
			Logput.e(e.getMessage(), e);
		}catch(SQLiteFullException e){
			// データ挿入時にDBファイルサイズの上限を超える（2GBがSQLiteの上限?)
			Logput.e(e.getMessage(), e);
		}catch(IllegalStateException e){
			// おかしな状態遷移が発生
			Logput.e(e.getMessage(), e);
		}catch(UnsupportedOperationException e){
			// サポートされていない操作が行われたときに発生
			Logput.e(e.getMessage(), e);
		}catch(SQLiteException e){
			// SQL構文エラー
			Logput.e(e.getMessage(), e);
		}finally{
			// トランザクション終了
			mDB.endTransaction();
			// DB閉じる
			//db.close();
			Logput.d(">DELETE " + table + "Table : " + whereClause);
		}
	}
	
	//***********************************
	// UPDATE
	//***********************************
	
	/**
	 * テーブルのデータ更新
	 * @param table 更新したいテーブル名
	 * @param whereClause 更新条件(WHERE句)
	 * @return 更新したレコード数
	 */
	public int update(String table, String whereClause, ContentValues values){
		int updateCount = -1;
		try{
			// DBオープン(書き込み)
			mDB = sEngine.getWritableDatabase();
		}catch(SQLiteException e){
			Logput.e("DB OPEN : UPDATE ... " + e.getMessage(), e);
			return updateCount;
		}
		
		// トランザクション開始
		mDB.beginTransaction();
		try{
			updateCount = mDB.update(table, values, whereClause, null);
			if(updateCount != -1){
				mDB.setTransactionSuccessful();
			}
		}catch(SQLiteConstraintException e){
			// 一意制約や外部キーなど制約条件に反したデータ操作を行おうとした場合に発生
			Logput.e(e.getMessage(), e);
		}catch(SQLiteDiskIOException e){
			// ディスク容量不足などのOSレベルでのファイルI/Oエラーが発生した場合...DBファイルが壊れている?
			Logput.e(e.getMessage(), e);
		}catch(SQLiteAbortException e){
			// なんらかの原因でSQLiteの処理が中断された場合
			Logput.e(e.getMessage(), e);
		}catch(SQLiteDatabaseCorruptException e){
			// DBファイルが壊れた場合
			Logput.e(e.getMessage(), e);
		}catch(SQLiteFullException e){
			// データ挿入時にDBファイルサイズの上限を超える（2GBがSQLiteの上限?)
			Logput.e(e.getMessage(), e);
		}catch(IllegalStateException e){
			// おかしな状態遷移が発生
			Logput.e(e.getMessage(), e);
		}catch(UnsupportedOperationException e){
			// サポートされていない操作が行われたときに発生
			Logput.e(e.getMessage(), e);
		}catch(SQLiteException e){
			// SQL構文エラー
			Logput.e(e.getMessage(), e);
		}finally{
			// トランザクション終了
			mDB.endTransaction();
			// DB閉じる
			//db.close();
			Logput.v(">UPDATE [" + table + "] " + values.toString());
		}
		return updateCount;
	}
	
	/**
	 * テーブルのデータ更新
	 * @param table 更新したいテーブル名
	 * @return Updateした数
	 */
	public int update(String table, long rowID, ContentValues values){
		int count = -1;
		try{
			// DBオープン(書き込み)
			mDB = sEngine.getWritableDatabase();
		}catch(SQLiteException e){
			Logput.e("DB OPEN : UPDATE ... " + e.getMessage(), e);
			return count;
		}
		
		// トランザクション開始
		mDB.beginTransaction();
		try{
			if(values != null){
				//Long rowID = (Long) values.get(ID);
				int isUpdate = mDB.update(table, values, ID + "=" + rowID, null);
				if(isUpdate != -1){
					count = 1;
					mDB.setTransactionSuccessful();
					Logput.v(">UPDATE SUCCESS [" + table + "] " + values.toString());
				}else{
					Logput.w(">UPDATE ERROR [" + table + "] " + values.toString());
				}
			}
		}catch(SQLiteConstraintException e){
			// 一意制約や外部キーなど制約条件に反したデータ操作を行おうとした場合に発生
			Logput.e(e.getMessage(), e);
		}catch(SQLiteDiskIOException e){
			// ディスク容量不足などのOSレベルでのファイルI/Oエラーが発生した場合...DBファイルが壊れている?
			Logput.e(e.getMessage(), e);
		}catch(SQLiteAbortException e){
			// なんらかの原因でSQLiteの処理が中断された場合
			Logput.e(e.getMessage(), e);
		}catch(SQLiteDatabaseCorruptException e){
			// DBファイルが壊れた場合
			Logput.e(e.getMessage(), e);
		}catch(SQLiteFullException e){
			// データ挿入時にDBファイルサイズの上限を超える（2GBがSQLiteの上限?)
			Logput.e(e.getMessage(), e);
		}catch(IllegalStateException e){
			// おかしな状態遷移が発生
			Logput.e(e.getMessage(), e);
		}catch(UnsupportedOperationException e){
			// サポートされていない操作が行われたときに発生
			Logput.e(e.getMessage(), e);
		}catch(SQLiteException e){
			// SQL構文エラー
			Logput.e(e.getMessage(), e);
		}finally{
			// トランザクション終了
			mDB.endTransaction();
			// DB閉じる
			//db.close();
		}
		return count;
	}
	
	/**
	 * テーブルのデータ更新
	 * @param table 更新したいテーブル名
	 * @return Updateした数
	 */
	public int update(String table, List<ContentValues> booksList){
		int count = -1;
		try{
			// DBオープン(書き込み)
			mDB = sEngine.getWritableDatabase();
		}catch(SQLiteException e){
			Logput.e("DB OPEN : UPDATE ... " + e.getMessage(), e);
			return count;
		}
		
		// トランザクション開始
		mDB.beginTransaction();
		try{
			if(booksList != null){
				for(ContentValues book : booksList){
					int isUpdate = mDB.update(table, book, ID + "=" + book.getAsLong(ID), null);
					if(isUpdate != -1){
						Logput.v(">UPDATE SUCCESS [" + table + "] " + book.toString());
						if(count == -1){
							count = 1;
						}else{
							count = count + 1;
						}
					}else{
						Logput.w(">UPDATE ERROR [" + table + "] " + book.toString());
					}
				}
				if(count == booksList.size()){
					mDB.setTransactionSuccessful();
				}
			}
		}catch(SQLiteConstraintException e){
			// 一意制約や外部キーなど制約条件に反したデータ操作を行おうとした場合に発生
			Logput.e(e.getMessage(), e);
		}catch(SQLiteDiskIOException e){
			// ディスク容量不足などのOSレベルでのファイルI/Oエラーが発生した場合...DBファイルが壊れている?
			Logput.e(e.getMessage(), e);
		}catch(SQLiteAbortException e){
			// なんらかの原因でSQLiteの処理が中断された場合
			Logput.e(e.getMessage(), e);
		}catch(SQLiteDatabaseCorruptException e){
			// DBファイルが壊れた場合
			Logput.e(e.getMessage(), e);
		}catch(SQLiteFullException e){
			// データ挿入時にDBファイルサイズの上限を超える（2GBがSQLiteの上限?)
			Logput.e(e.getMessage(), e);
		}catch(IllegalStateException e){
			// おかしな状態遷移が発生
			Logput.e(e.getMessage(), e);
		}catch(UnsupportedOperationException e){
			// サポートされていない操作が行われたときに発生
			Logput.e(e.getMessage(), e);
		}catch(SQLiteException e){
			// SQL構文エラー
			Logput.e(e.getMessage(), e);
		}finally{
			// トランザクション終了
			mDB.endTransaction();
			// DB閉じる
			//db.close();
		}
		return count;
	}
	
	//***********************************
	// SELECT
	//***********************************
	
	/**
	 * 指定テーブルのレコード数を返す
	 * @param table 指定テーブル名
	 * @return レコード数
	 */
	public int count(String table){
		int count = 0;
		// DBオープン(読み取り)
		mDB = sEngine.getReadableDatabase();
		Cursor cursor = mDB.query(table, null, null, null, null, null, null);
		if(cursor != null){
			if(cursor.moveToFirst()){
				count = cursor.getCount();
			}
			cursor.close();
			//db.close();
		}
		return count;
	}
	
	/**
	 * 指定テーブルからデータ取得
	 * @param table テーブル名
	 * @param columns カラム名
	 * @param selection WHERE句
	 * @param selectionArgs selectionの「？」を置き換えする文字列
	 * @param groupBy GROUPBY句
	 * @param having HAVING句
	 * @param orderBy ORDERBY句
	 * @return 取得したデータ
	 */
	public ArrayList<BookBeans> selectList(String tableName, String selection, String orderBy){
		ArrayList<BookBeans>  books = null;
		try{
			// DBオープン(読み取り)
			mDB = sEngine.getReadableDatabase();
			Cursor cursor = mDB.query(tableName, null, selection, null, null, null, orderBy);
			if(cursor == null) return null;
			books = new ArrayList<BookBeans>();
			if(cursor.moveToFirst()){
				if(tableName.equals(CONTENTS_TABLE)){
					while (!cursor.isAfterLast()) {
						books.add(getContents(cursor));
						cursor.moveToNext();
					}
				}else if(tableName.equals(UPDATE_CONTENTS_TABLE)){
					while (!cursor.isAfterLast()) {
						books.add(getUpdateContents(cursor));
						cursor.moveToNext();
					}
				}else if(tableName.equals(UPDATE_LICENSE_TABLE)){
					while (!cursor.isAfterLast()) {
						books.add(getUpdateLicense(cursor));
						cursor.moveToNext();
					}
				}
			}
			cursor.close();
		}catch(SQLiteException e){
			Logput.e(e.getMessage(), e);
		}
		return books;
	}
	
	/**
	 * 指定テーブルからデータ取得
	 * @param table テーブル名
	 * @param columns カラム名
	 * @param selection WHERE句
	 * @param selectionArgs selectionの「？」を置き換えする文字列
	 * @param groupBy GROUPBY句
	 * @param having HAVING句
	 * @param orderBy ORDERBY句
	 * @return 取得したデータ
	 */
	public BookBeans select(String tableName, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having, String orderBy){
		BookBeans book = null;
		try{
			// DBオープン(読み取り)
			mDB = sEngine.getReadableDatabase();
			Cursor cursor = mDB.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
			if(cursor != null){
				if(cursor.moveToFirst()){
					if(tableName.equals(CONTENTS_TABLE)){
						book = getContents(cursor);
					}else if(tableName.equals(UPDATE_CONTENTS_TABLE)){
						book = getUpdateContents(cursor);
					}else if(tableName.equals(UPDATE_LICENSE_TABLE)){
						book = getUpdateLicense(cursor);
					}
				}
			}
			cursor.close();
		}catch(SQLiteException e){
			Logput.e(e.getMessage(), e);
		}
		return book;
	}
	
	/**
	 * 指定コンテンツテーブルIDのWEBサイト誘導情報を取得
	 * @param downloadID ダウンロードID
	 * @return NaviSalesBeans
	 */
	public NaviSalesBeans select(String downloadID){
		NaviSalesBeans bean = null;
		try{
			// DBオープン(読み取り)
			mDB = sEngine.getReadableDatabase();
			Cursor cursor = mDB.query(NAVI_SALES_TABLE, null, DOWNLOAD_ID + "='" + downloadID + "'", null, null, null, null);
			if(cursor != null){
				if(cursor.moveToFirst()){
					bean = getNaviSales(cursor);
				}
			}
			cursor.close();
		}catch(SQLiteException e){
			Logput.e(e.getMessage(), e);
		}
		return bean;
	}
	
	/**
	 * PurchaseテーブルからProductID一覧を取得
	 * @return ProductIDリスト
	 */
	public Set<String> selectProductID(){
		Set<String> productIDList = new HashSet<String>();
		try{
			// DBオープン(読み取り)
			mDB = sEngine.getReadableDatabase();
			Cursor cursor = mDB.query(PURCHASED_TABLE, new String[] {PURCHASED_PRODUCT_ID}, null, null, null, null, null);
			if(cursor != null){
				if(cursor.moveToFirst()){
					while (!cursor.isAfterLast()) {
						productIDList.add(cursor.getString(cursor.getColumnIndexOrThrow(PURCHASED_PRODUCT_ID)));
						cursor.moveToNext();
					}
				}
			}
			cursor.close();
		}catch(SQLiteException e){
			Logput.e(e.getMessage(), e);
		}
		return productIDList;
	}
	
	//***********************************
	// カーソルからオブジェクトへの変換
	//***********************************
	
	/**
	 * カーソルからオブジェクトへの変換 - Book
	 * @param cursorカーソル
	 * @return 変換結果
	 */
	private BookBeans getContents(Cursor cursor) {
		BookBeans book = new BookBeans();
		book.setRowId(cursor.getLong(cursor.getColumnIndexOrThrow(ID)));
		book.setContents_id(cursor.getString(cursor.getColumnIndexOrThrow(CONTENTS_ID)));
		book.setDownload_id(cursor.getString(cursor.getColumnIndexOrThrow(DOWNLOAD_ID)));
		book.setType(cursor.getString(cursor.getColumnIndexOrThrow(TYPE)));
		book.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
		book.setAuthor(cursor.getString(cursor.getColumnIndexOrThrow(AUTHOR)));
		book.setKeywords(cursor.getString(cursor.getColumnIndexOrThrow(KEYWORDS)));
		book.setDistributor_name(cursor.getString(cursor.getColumnIndexOrThrow(DISTRIBUTOR_NAME)));
		book.setDistributor_url(cursor.getString(cursor.getColumnIndexOrThrow(DISTRIBUTOR_URL)));
		book.setFile_path(cursor.getString(cursor.getColumnIndexOrThrow(FILE_PATH)));
		book.setEncryptionKey(cursor.getString(cursor.getColumnIndexOrThrow(ENCRYPTION_KEY)));
		book.setKrpdfFormatVer(cursor.getInt(cursor.getColumnIndexOrThrow(KRPDF_FORMAT_VER)));
		book.setContents_DL_URL(cursor.getString(cursor.getColumnIndexOrThrow(CONTENTS_DL_URL)));
		book.setThumb_path(cursor.getString(cursor.getColumnIndexOrThrow(THUMB_PATH)));
		book.setThumb_DL_URL(cursor.getString(cursor.getColumnIndexOrThrow(THUMB_DL_URL)));
		book.setDownload_date(cursor.getString(cursor.getColumnIndexOrThrow(DOWNLOAD_DATE)));
		book.setLast_access_date(cursor.getString(cursor.getColumnIndexOrThrow(LAST_ACCESS_DATE)));
		book.setExpiry_date(cursor.getString(cursor.getColumnIndexOrThrow(EXPIRY_DATE)));
		book.setInvalidPlatform(cursor.getString(cursor.getColumnIndexOrThrow(INVALID_PLATFORM)));
		book.setFileSize(cursor.getString(cursor.getColumnIndexOrThrow(FILESIZE)));
		book.setOriginalFileName(cursor.getString(cursor.getColumnIndexOrThrow(ORIGINAL_FILE_NAME)));
		book.setPageCount(cursor.getInt(cursor.getColumnIndexOrThrow(PAGE_COUNT)));
		book.setLastModify(cursor.getString(cursor.getColumnIndexOrThrow(LAST_MODIFY)));
		book.setLabelID(cursor.getString(cursor.getColumnIndexOrThrow(LABEL_ID)));
		book.setSalesID(cursor.getString(cursor.getColumnIndexOrThrow(SALES_ID)));
		book.setSharedDevice_D(cursor.getInt(cursor.getColumnIndexOrThrow(SHARED_DEVICE_D)));
		book.setSharedDevice_M(cursor.getInt(cursor.getColumnIndexOrThrow(SHARED_DEVICE_M)));
		book.setBrowse_D(cursor.getInt(cursor.getColumnIndexOrThrow(BROWSE_D)));
		book.setBrowse_M(cursor.getInt(cursor.getColumnIndexOrThrow(BROWSE_M)));
		book.setPrint_D(cursor.getInt(cursor.getColumnIndexOrThrow(PRINT_D)));
		book.setPrint_D(cursor.getInt(cursor.getColumnIndexOrThrow(PRINT_M)));
		book.setKey(cursor.getString(cursor.getColumnIndexOrThrow(OWNER_PASSWORD)));
		book.setCRC32(cursor.getString(cursor.getColumnIndexOrThrow(CRC32)));
		book.setMainView(cursor.getInt(cursor.getColumnIndexOrThrow(MAIN_VIEW)));
		book.setDlStatus(cursor.getInt(cursor.getColumnIndexOrThrow(DL_STATUS)));
		book.setDlProgress(cursor.getInt(cursor.getColumnIndexOrThrow(DL_PROGRESS)));
		book.setDeleteFlag(cursor.getString(cursor.getColumnIndexOrThrow(DELETE_FLAG)));
		return book;
	}

	/**
	 * カーソルからオブジェクトへの変換
	 *
	 * @param cursor カーソル
	 * @return 変換結果
	 */
	private BookBeans getUpdateContents(Cursor cursor) {
		BookBeans book = new BookBeans();
		book.setRowId(cursor.getLong(cursor.getColumnIndexOrThrow(ID)));
		book.setDownload_id(cursor.getString(cursor.getColumnIndexOrThrow(DOWNLOAD_ID)));
		book.setLast_access_date(cursor.getString(cursor.getColumnIndexOrThrow(LAST_ACCESS_DATE)));
		book.setLastModify(cursor.getString(cursor.getColumnIndexOrThrow(LAST_MODIFY)));
		book.setDeleteFlag(cursor.getString(cursor.getColumnIndexOrThrow(DELETE_FLAG)));
		return book;
	}
	
	/**
	 * カーソルからオブジェクトへの変換
	 *
	 * @param cursor カーソル
	 * @return 変換結果
	 */
	private BookBeans getUpdateLicense(Cursor cursor) {
		BookBeans book = new BookBeans();
		book.setRowId(cursor.getLong(cursor.getColumnIndexOrThrow(ID)));
		book.setDownload_id(cursor.getString(cursor.getColumnIndexOrThrow(DOWNLOAD_ID)));
		book.setSharedDevice(cursor.getString(cursor.getColumnIndexOrThrow(SHARED_DEVICE)));
		book.setBrowse(cursor.getInt(cursor.getColumnIndexOrThrow(BROWSE)));
		return book;
	}
	
	/**
	 * カーソルからオブジェクトへの変換 - NaviSalesBeans
	 * @param cursor
	 * @return 変換結果
	 */
	private NaviSalesBeans getNaviSales(Cursor cursor){
		NaviSalesBeans bean = new NaviSalesBeans();
		bean.setRowID(cursor.getLong(cursor.getColumnIndexOrThrow(ID)));
		bean.setDownloadID(cursor.getString(cursor.getColumnIndexOrThrow(DOWNLOAD_ID)));
		bean.setNaviURL(cursor.getString(cursor.getColumnIndexOrThrow(NAVIGATE_URL)));
		bean.setNaviMessage(cursor.getString(cursor.getColumnIndexOrThrow(NAVIGATE_MESSAGE)));
		return bean;
	}
	
	/**
	 * カーソルからオブジェクトへの変換 - PurchaseBeans
	 * @param cursor
	 * @return 変換結果
	 */
	private PurchaseBeans getPurchases(Cursor cursor){
		PurchaseBeans bean = new PurchaseBeans();
		bean.setRowID(cursor.getLong(cursor.getColumnIndexOrThrow(ID)));
		bean.setOrderID(cursor.getString(cursor.getColumnIndexOrThrow(PURCHASED_ORDER_ID)));
		bean.setProductID(cursor.getString(cursor.getColumnIndexOrThrow(PURCHASED_PRODUCT_ID)));
		bean.setDownloadID(cursor.getString(cursor.getColumnIndexOrThrow(PURCHASED_DOWNLOAD_ID)));
		return bean;
	}
	
	/**
	 * DBファイルを指定の場所にコピーする(debug用)
	 * @param dstFile
	 * @throws IOException
	 */
	static public void copyDB(File dstFile) throws IOException {
		File dbFile = new File(sEngine.getReadableDatabase().getPath());
		FileUtil.copyFile(dbFile, dstFile);
	}
}
