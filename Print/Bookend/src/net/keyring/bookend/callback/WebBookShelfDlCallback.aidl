package net.keyring.bookend.callback;

import net.keyring.bookend.callback.ICallbackListener;

/**
* Callback登録用インターフェイス
*/
oneway interface WebBookShelfDlCallback{
	/*
	* コールバックインターフェイス登録処理
	*/
	void registerListener(ICallbackListener listener);
	
	/**
	* コールバックインターフェイス登録解除処理
	*/
	void removeListener(ICallbackListener listener);
	
	/**
	* Web書庫からダウンロード開始
	*/
	void startDownload_web(ICallbackListener listener,long movieID);
	
}