package net.keyring.bookend.bean;
/**
 * navi_salesテーブル用Bean
 * @author Hamaji
 *
 */
public class NaviSalesBeans {

	private long mRowID;
	private String mDownloadID;
	private String mNaviURL;
	private String mNaviMessage;
	
	
	/**
	 * navi_salesテーブルIDを取得
	 * @return rowID navi_salesテーブルID
	 */
	public long getRowID() {
		return mRowID;
	}
	/**
	 * navi_salesテーブルIDをセット
	 * @param rowID navi_salesテーブルID
	 */
	public void setRowID(long rowID) {
		this.mRowID = rowID;
	}
	/**
	 * ダウンロードIDを取得
	 * @return ダウンロードID
	 */
	public String getDownloadID() {
		return mDownloadID;
	}
	/**
	 * ダウンロードIDをセット
	 * @param ダウンロードID
	 */
	public void setDownloadID(String downloadID) {
		this.mDownloadID = downloadID;
	}
	/**
	 * WEBサイト誘導URLを取得
	 * @return naviURL WEBサイト誘導URL
	 */
	public String getNaviURL() {
		return mNaviURL;
	}
	/**
	 * WEBサイト誘導URL
	 * @param naviURL WEBサイト誘導URLをセットする
	 */
	public void setNaviURL(String naviURL) {
		this.mNaviURL = naviURL;
	}
	/**
	 * WEBサイト誘導メッセージ([言語タイプ1];[メッセージ1] ;[言語タイプ2];[メッセージ2] ....)を取得
	 * @return mNaviMessage WEBサイト誘導メッセージ
	 */
	public String getNaviMessage() {
		return mNaviMessage;
	}
	/**
	 * WEBサイト誘導メッセージ([言語タイプ1];[メッセージ1] ;[言語タイプ2];[メッセージ2] ....)をセット
	 * @param mNaviMessage WEBサイト誘導メッセージ
	 */
	public void setNaviMessage(String mNaviMessage) {
		this.mNaviMessage = mNaviMessage;
	}
	
	
}
