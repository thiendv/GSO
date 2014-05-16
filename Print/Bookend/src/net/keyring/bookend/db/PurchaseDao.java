package net.keyring.bookend.db;

import java.util.Set;

import net.keyring.bookend.constant.ConstDB;
import net.keyring.bookend.util.StringUtil;
import android.content.ContentValues;
import android.content.Context;

public class PurchaseDao implements ConstDB {
	
	/** SQLiteEngine */
	private SQLiteEngine mDB;
	
	/**
	 * コンストラクタ
	 * @param con Context
	 */
	public PurchaseDao(Context con){
		mDB = ContentsDBAccess.getInstance(con);
	}
	
	/**
	 * InAppBilling(managed)購入履歴をDBにInsert
	 * @param orderId オーダーID
	 * @param productId プロダクトID
	 * @param developerPayload コンテンツのダウンロードID
	 * @return 挿入したレコード数
	 */
	public int insert(String orderId, String productId, String developerPayload) {
        ContentValues values = new ContentValues();
        values.put(PURCHASED_ORDER_ID, orderId);
        values.put(PURCHASED_PRODUCT_ID, productId);
        if(!StringUtil.isEmpty(developerPayload)){
        	values.put(PURCHASED_DOWNLOAD_ID, developerPayload);
        }
        return mDB.insert(PURCHASED_TABLE, values);
    }
	
	/**
	 * PurchaseテーブルからProductID一覧を取得
	 * @return ProductIDリスト
	 */
	public Set<String> queryAllPurchasedItems() {
        return mDB.selectProductID();
    }
	
	/**
	 * 指定ID行削除（-1の場合は全て削除）
	 * @param rowID
	 */
	public void delete(long rowID){
		if(rowID == -1){
			mDB.delete(PURCHASED_TABLE, null);
		}else{
			mDB.delete(PURCHASED_TABLE, ID + "=" + rowID);
		}
	}
}
