package net.keyring.bookend.db;

import java.util.ArrayList;

import net.keyring.bookend.Logput;
import net.keyring.bookend.bean.BookBeans;
import net.keyring.bookend.constant.ConstDB;
import net.keyring.bookend.util.StringUtil;
import android.content.ContentValues;
import android.content.Context;
/**
 * update_licenseテーブル処理クラス
 * @author Hamaji
 *
 */
public class UpdateLicenseDao implements ConstDB {
	/** SQLiteEngine */
	private SQLiteEngine db;
	
	public UpdateLicenseDao(Context con){
		db = ContentsDBAccess.getInstance(con);
	}
	
	/**
	 * 指定テーブルレコード数を返す
	 * @param tableName 指定テーブル名
	 * @return レコード数
	 */
	public int getCount(){
		return db.count(UPDATE_LICENSE_TABLE);
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
		public ArrayList<BookBeans> loadList(){
			ArrayList<BookBeans> valuesList = null;
			// SELECT --------------
			// groupBy GROUPBY句
			// having HAVING句
			// orderBy ORDERBY句
			valuesList = db.selectList(UPDATE_LICENSE_TABLE, null, null);
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
			values = db.select(UPDATE_LICENSE_TABLE, null, ID + "=?", new String[] {String.valueOf(rowID)}, null, null, null);
			return values;
		}
		
		/**
		 * update_licenseテーブルに登録されている指定ダウンロードIDコンテンツを返す
		 * @param downloadID
		 * @return Book
		 */
		private BookBeans select(String downloadID){
			return db.select(UPDATE_LICENSE_TABLE, null, DOWNLOAD_ID + "='" + downloadID + "'", null, null, null, null);
		}
		
		// --------------- DELETE --------------------
		/**
		 * update_licenseテーブル削除
		 */
		public void drop(){
			// DB削除
			db.drop(DROP_UP_LIC_TABLE);
		}
		
		/**
		 * 指定レコードDELETE(rowID=-1の場合はテーブル削除)
		 * @param rowID
		 */
		public void delete(long rowID){
			if(rowID == -1){
				db.delete(UPDATE_LICENSE_TABLE, null);
			}else{
				db.delete(UPDATE_LICENSE_TABLE, ID + "=" + rowID);
			}
		}
		
		/**
		 * 指定レコードDELETE(nullの場合はテーブル削除)
		 * @param columnName 指定カラム名
		 * @param key 指定文字列
		 */
		public void delete(String columnName, String key){
			if(StringUtil.isEmpty(key)){
				db.delete(UPDATE_LICENSE_TABLE, null);
			}else{
				db.delete(UPDATE_LICENSE_TABLE, columnName + "=" + key);
			}
		}
		
		// --------------- INSERT・UPDATE共通 --------------------
		
		/**
		 * sharedDeviceをセット("true" or "false")
		 * @param rowID INSERTの場合は-1
		 * @param downloadID ダウンロードID
		 * @param sharedDevice 共有台数変更("true" or "false")
		 * @return 問題なく挿入・更新できた場合はtrue,それ以外はfalseを返す
		 */
		public boolean save(long rowID, String downloadID, String sharedDevice){
			return save(rowID, downloadID, sharedDevice, 0);
		}
		
		/**
		 * borwseをセット...閲覧回数(マイナス値)
		 * @param rowID INSERTの場合は-1
		 * @param downloadID ダウンロードID
		 * @param browse 閲覧回数(マイナス値)
		 * @return 問題なく挿入・更新できた場合はtrue,それ以外はfalseを返す
		 */
		public boolean save(long rowID, String downloadID, int browse){
			return save(rowID, downloadID, null, browse);
		}
		
		/**
		 * updateLicenseテーブルに保存 rowidが-1の場合はinsert、rowidが1以上の場合はupdate<br>
		 * ※ UPDATE時：nullの場合も上書きされる
		 * @param rowID INSERTの場合は-1
		 * @param downloadID ダウンロードID
		 * @param sharedDevice 共有台数("false" or "true")...変更しない場合はNULL指定
		 * @param browse 閲覧回数...変更しない場合は0指定
		 * @return
		 */
		private boolean save(long rowID, String downloadID, String sharedDevice, int browse) {
			boolean check = false;
			
			if(rowID == -1){
				// 念のためダウンロードIDでテーブルに登録されていないかチェック
				BookBeans book = select(downloadID);
				if(book != null){
					rowID = book.getRowId();
					browse = book.getBrowse() + browse;
					// 共有台数が登録済みの場合は
					String sharedDevice_db = book.getSharedDevice();
					if(!StringUtil.isEmpty(sharedDevice) && !StringUtil.isEmpty(sharedDevice_db)){
						if(!sharedDevice_db.equals(sharedDevice)){
							sharedDevice = null;
						}else{
							Logput.i("Registered sharedDevice = [" + rowID + "] " + sharedDevice);
							return check;
						}
					}
				}
			}
			
			// 共有台数・閲覧回数に変更がない場合は更新しなくてOK
			if(StringUtil.isEmpty(sharedDevice) && browse == 0){
				return check;
			}else{
				ContentValues values = new ContentValues();
				if(rowID == -1){
					if(StringUtil.isEmpty(downloadID)){
						// downloadIDは必須
						return false;
					}else{
						// INSERT
						values.put(DOWNLOAD_ID, downloadID);
						if(!StringUtil.isEmpty(sharedDevice)){
							values.put(SHARED_DEVICE, sharedDevice);
						}else{
							values.putNull(SHARED_DEVICE);
						}
						if(browse == 0){
							values.putNull(BROWSE);
						}else{
							values.put(BROWSE, browse);
						}
						// DBアクセス
						int count = db.insert(UPDATE_LICENSE_TABLE, values);
						if(count != -1){
							check = true;
						}
					}
				}else{
					// UPDATE
					if(!StringUtil.isEmpty(downloadID)){
						values.put(DOWNLOAD_ID, downloadID);
					}
					if(!StringUtil.isEmpty(sharedDevice)){
						values.put(SHARED_DEVICE, sharedDevice);
					}
					if(browse != 0){
						values.put(BROWSE, browse);
					}
					int count = db.update(UPDATE_LICENSE_TABLE, rowID, values);
					if(count != -1){
						check = true;
					}
				}
			}
			return check;
		}
}
