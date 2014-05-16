package net.keyring.bookend.callback;

import net.keyring.bookend.callback.DownloadStatus;

/**
* DownloadStatus更新インターフェイス
*/
interface ICallbackListener{
	
	/**
	 * ダウンロードの状態が変更
	 */
	void updateDownloadStatus(inout DownloadStatus status);
}