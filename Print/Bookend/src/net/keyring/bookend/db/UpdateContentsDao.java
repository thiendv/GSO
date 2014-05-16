package net.keyring.bookend.db;

import java.util.ArrayList;

import net.keyring.bookend.bean.BookBeans;
import net.keyring.bookend.constant.ConstDB;
import net.keyring.bookend.util.StringUtil;
import android.content.ContentValues;
import android.content.Context;
/**
 * update_contentsテーブル処理クラス
 * @author Hamaji
 *
 */
public class UpdateContentsDao implements ConstDB {

	/** SQLiteEngine */
	private SQLiteEngine db;
	
	/**
	 * コンストラクタ
	 * @param con Context
	 */
	public UpdateContentsDao(Context con){
		db = ContentsDBAccess.getInstance(con);
	}
	
	/**
	 * 指定テーブルレコード数を返す
	 * @param tableName 指定テーブル名
	 * @return レコード数
	 */
	public int getCount(){
		return db.count(UPDATE_CONTENTS_TABLE);
	}
	
	// ------------- SELECT ---------------------
	
	/**
	 * 指定ダウンロードIDのコンテンツが登録されているrowIDを返す
	 * @param downloadID ダウンロードID
	 * @return rowID(未登録の場合は-1)
	 */
	public long getRowID(String downloadID){
		long rowID = -1;
		BookBeans book = select(downloadID);
		if(book != null){
			rowID = book.getRowId();
		}
		return rowID;
	}
	
	/**
	 * リスト情報を全て取得
	 *
	 * @param rowId プライマリキー
	 * @return ロード結果
	 * @throws Exception
	 */
	public ArrayList<BookBeans> loadList(){
		ArrayList<BookBeans> valuesList = null;
		// SELECT --------------
		// groupBy GROUPBY句
		// having HAVING句
		// orderBy ORDERBY句
		valuesList = db.selectList(UPDATE_CONTENTS_TABLE, null , null);
		return valuesList;
	}
	
	/**
	 * idで指定リスト情報を取得
	 *
	 * @param rowId プライマリキー
	 * @return ロード結果
	 * @throws Exception
	 */
	public BookBeans load(long rowID){
		BookBeans values = null;
		// SELECT --------------
		// columns カラム名
		// selection WHERE句
		// selectionArgs selectionの「？」を置き換えする文字列
		// groupBy GROUPBY句
		// having HAVING句
		// orderBy ORDERBY句
		values = db.select(UPDATE_CONTENTS_TABLE, null, ID + "=?", new String[] {String.valueOf(rowID)}, null, null, null);
		return values;
	}
	
	/**
	 * update_contentsテーブルに登録されている指定ダウンロードIDコンテンツを返す
	 * @param downloadID
	 * @return Book
	 */
	private BookBeans select(String downloadID){
		return db.select(UPDATE_CONTENTS_TABLE, null, DOWNLOAD_ID + "='" + downloadID + "'", null, null, null, null);
	}
	
	// --------------- DELETE --------------------
	/**
	 * update_contentsテーブル削除
	 */
	public void drop(){
		// DB削除
		db.drop(DROP_UP_CON_TABLE);
	}
	
	/**
	 * 指定レコードDELETE(rowID=-1の場合はテーブル削除)
	 * @param rowID
	 */
	public void delete(long rowID){
		if(rowID == -1){
			db.delete(UPDATE_CONTENTS_TABLE, null);
		}else{
			db.delete(UPDATE_CONTENTS_TABLE, ID + "=" + rowID);
		}
	}
	
	// ----------------- Insert・Update共通 --------------------
	/**
	 * update_contentsテーブルに追加・更新(TODO deleteFlag未使用のためスルー)
	 * @param rowID
	 * @param downloadID ダウンロードID
	 * @param lastAccessDate 最終閲覧日時
	 * @return
	 */
	public boolean save(long rowID, String downloadID, String lastAccessDate){
		if(StringUtil.isEmpty(lastAccessDate)){
			return false;
		}
		
		// 念のため同じダウンロードIDのコンテンツが登録されていないかチェック、されていた場合はrowIDをセットし、UPDATEする
		if(rowID == -1){
			if(StringUtil.isEmpty(downloadID)){
				return false;
			}else{
				BookBeans book = select(downloadID);
				if(book != null){
					rowID = book.getRowId();
				}
			}
		}
		
		int count = -1;
		ContentValues values = new ContentValues();
		values.put(LAST_MODIFY, lastAccessDate);
		values.put(LAST_ACCESS_DATE, lastAccessDate);
		if(!StringUtil.isEmpty(downloadID)){
			values.put(DOWNLOAD_ID, downloadID);
		}
		if(rowID == -1){
			// INSERT
			count = db.insert(UPDATE_CONTENTS_TABLE, values);
		}else{
			//UPDATE
			count = db.update(UPDATE_CONTENTS_TABLE, rowID, values);
		}
		
		if(count == -1){
			return false;
		}else{
			return true;
		}
	}
	
}
