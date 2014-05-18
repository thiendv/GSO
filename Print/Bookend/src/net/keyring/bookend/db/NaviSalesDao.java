package net.keyring.bookend.db;

import net.keyring.bookend.bean.NaviSalesBeans;
import net.keyring.bookend.constant.ConstDB;
import net.keyring.bookend.util.StringUtil;
import android.content.ContentValues;
import android.content.Context;
/**
 * navi_salesテーブル用DAO
 * @author Hamaji
 *
 */
public class NaviSalesDao implements ConstDB {

	/** SQLiteEngine */
	private SQLiteEngine mDB;
	
	/**
	 * コンストラクタ
	 * @param con Context
	 */
	public NaviSalesDao(Context con){
		mDB = ContentsDBAccess.getInstance(con);
	}
	
	/**
	 * 指定コンテンツテーブルIDのWEBサイト誘導情報を取得
	 * @param downloadID ダウンロードID
	 * @return NaviSalesBeans
	 */
	public NaviSalesBeans load(String downloadID){
		if(StringUtil.isEmpty(downloadID)){
			return null;
		}
		return mDB.select(downloadID);
	}
	
	/**
	 * DB更新
	 * @param rowID テーブルID（新規の場合は-1）
	 * @param downloadID ダウンロードID
	 * @param url WEBサイト誘導URL
	 * @param mes メッセージ([言語タイプ1];[メッセージ1] ;[言語タイプ2];[メッセージ2] ....)
	 * @return NaviSalesBeans
	 */
	public NaviSalesBeans save(long rowID, String downloadID, String url, String mes){
		if(StringUtil.isEmpty(downloadID)){
			// ダウンロードIDは必須
			return null;
		}
		
		// 既に同じcontentsテーブルIDが登録されていた場合はrowIDをセット
		long id = checkRegist(downloadID);
		if(id != -1){
			rowID = id;
		}
		ContentValues values = new ContentValues();
		NaviSalesBeans resultBean = null;
		values.put(DOWNLOAD_ID, downloadID);
		values.put(NAVIGATE_URL, url);
		values.put(NAVIGATE_MESSAGE, mes);
		
		if(rowID <= -1){	// Insert
			mDB.insert(NAVI_SALES_TABLE, values);
		}else{	// Update
			mDB.update(NAVI_SALES_TABLE, rowID, values);
		}
		resultBean = mDB.select(downloadID);
		return resultBean;
	}
	
	/**
	 * 既に同じダウンロードIDの情報が登録されていないかチェック
	 * @param downloadID ダウンロードID
	 * @return navi_salesテーブルID
	 */
	public long checkRegist(String downloadID){
		long rowID = -1;
		NaviSalesBeans bean = mDB.select(downloadID);
		if(bean != null){
			rowID = bean.getRowID();
		}
		return rowID;
	}
	
	/**
	 * navi_salesテーブル指定ID列削除（ID=-1の場合は全データ削除）
	 * @param rowID
	 */
	public void delete(long rowID){
		if(rowID == -1){
			mDB.delete(NAVI_SALES_TABLE, null);
		}else{
			mDB.delete(NAVI_SALES_TABLE, ID + "=" + rowID);
		}
	}
}
