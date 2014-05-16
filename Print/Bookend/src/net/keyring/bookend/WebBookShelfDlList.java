package net.keyring.bookend;

import java.util.LinkedList;
/**
 * WEB書庫ダウンロード待ちリスト
 * @author Hamaji
 *
 */
public class WebBookShelfDlList {
	/** 唯一のインスタンス */
	private static WebBookShelfDlList instance = new WebBookShelfDlList();
	/** ダウンロード待ちリスト */
	private LinkedList<Long> downloadList;
	
	/**
	 * privateコンストラクタ
	 */
	private WebBookShelfDlList(){
		if(downloadList == null){
			// ダウンロード待ちリスト生成
			downloadList = new LinkedList<Long>();
		}
	}
	
	/**
	 * DownloadListインスタンス取得（sinbleton,synchronized）
	 * @return WebBookShelfDlListインスタンス
	 */
	public static WebBookShelfDlList getInstance(){
		return instance;
	}
	
	/**
	 * ダウンロード待ちコンテンツListを返す
	 * @return ダウンロード待ちコンテンツList
	 */
	public LinkedList<Long> getDownloadList(){
		return downloadList;
	}
	
	/**
	 * ダウンロード待ちリストリセット
	 */
	public void reset(){
		downloadList = null;
		downloadList = new LinkedList<Long>();
	}
	
	/**
	 * ダウンロードリストクリア
	 */
	public void clear(){
		downloadList = null;
	}
	
	/**
	 * ダウンロードリクエストリストから指定ID削除
	 * @param rowID
	 * 
	 */
	public void dl_finish(Long rowID){
		long id = -1;
		if(!downloadList.isEmpty()){
			for(int i=0; i<downloadList.size(); i++){
				id = downloadList.get(i);
				if(id == rowID){
					downloadList.remove(i);
					Logput.v("Remove downloadList : ID = " + id);
				}
			}
		}
	}
	
	/**
	 * ダウンロード待ちリストに追加
	 * @param rowID
	 * @return 最大ダウンロードリクエスト数を超えていた場合はfalseを返す
	 */
	public boolean setDownloadList(Long rowID){
		downloadList.addFirst(rowID);
		Logput.v("Add downloadList : ID = " + rowID);
		return true;
	}
	
	/**
	 * ダウンロード待機中リストに入っているかチェック
	 * @param movieID
	 */
	public boolean checkID(long rowID){
		long id = -1;
		if(!downloadList.isEmpty()){
			for(int i=0; i<downloadList.size(); i++){
				id = downloadList.get(i);
				if(id == rowID){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * ダウンロード待機中リストから削除
	 * @param rowID
	 */
	public void cancel(long rowID){
		long id = -1;
		if(!downloadList.isEmpty()){
			for(int i=0; i<downloadList.size(); i++){
				id = downloadList.get(i);
				if(id == rowID){
					// リストから削除
					downloadList.remove(i);
					return;
				}
			}
		}
	}
	
	/**
	 * ダウンロードコンテンツRowIDを返す
	 * @return ダウンロードコンテンツMAP
	 */
	public Long getDL_rowID(){
		long rowID = -1;
		if(!downloadList.isEmpty()){
			// リストの最後尾から取得
			rowID =  downloadList.getLast();
			// リスト最後尾削除
			downloadList.removeLast();
		}
		return rowID;
	}
}
