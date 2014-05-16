package net.keyring.bookend.bean;


/**
 * InAppBilling - Purchaseテーブル Bean
 * @author Hamaji
 *
 */
public class PurchaseBeans {

	private long		mRowID;
	private String		mOrderID;
	private String		mProductID;
	private String		mDownloadID;

	/**
	 * RowIDを取得
	 * @return RowID
	 */
	public long getRowID() {
		return mRowID;
	}

	/**
	 * RowIDをセット
	 * @param RowID
	 */
	public void setRowID(long rowID) {
		this.mRowID = rowID;
	}
	
	/**
	 * OrderIDを取得
	 * @return OrderID
	 */
	public String getOrderID() {
		return mOrderID;
	}

	/**
	 * OrderIDをセット
	 * @param OrderID
	 */
	public void setOrderID(String orderID) {
		this.mOrderID = orderID;
	}

	/**
	 * InAppBilling - productIDを返す
	 * @return productID
	 */
	public String getProductID() {
		return mProductID;
	}

	/**
	 * InAppBilling - productIDをセット
	 * @param productID
	 */
	public void setProductID(String productID) {
		this.mProductID = productID;
	}
	
	/**
	 * DownloadIDを取得
	 * @return DownloadID
	 */
	public String getDownloadID() {
		return mDownloadID;
	}

	/**
	 * DownloadIDをセット
	 * @param DownloadID
	 */
	public void setDownloadID(String downloadID) {
		this.mDownloadID = downloadID;
	}
}
