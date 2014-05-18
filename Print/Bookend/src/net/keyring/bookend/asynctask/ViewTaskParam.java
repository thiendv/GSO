package net.keyring.bookend.asynctask;

import net.keyring.bookend.bean.BookBeans;

/**
 * ViewTaskのパラメータ
 */
public class ViewTaskParam {
	public BookBeans	mBook;	//	コンテンツ情報
	public boolean		mIsTemporary;	//	ダウンロード時のパラメータTemporaryが指定されているか
	
	public ViewTaskParam(BookBeans book, boolean isTemporary) {
		mBook = book;
		mIsTemporary = isTemporary;
	}
}
