package net.keyring.bookend.callback;

import net.keyring.bookend.callback.ICallbackListener;
import net.keyring.bookend.callback.DownloadStatus;

/**
* Callback登録用インターフェイス
*/
oneway interface NewDownloadCallback{

	/*
	* コールバックインターフェイス登録処理
	*/
	void registerListener(ICallbackListener listener);
	
	/**
	* コールバックインターフェイス登録解除処理
	*/
	void removeListener(ICallbackListener listener);
	
	/**
	* ダウンロード中のものがある場合はcallback処理を行う
	*/
	void callback();
	
	/**
	* ダウンロード進捗をセット
	*/
	void setProgress(int progress);
	
	/**
	* DownloadStatusをnullに,ダウンロードフラグにfalseをセット
	*/
	void initDlStatus();
}