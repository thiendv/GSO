package net.keyring.bookend.constant;
/**
 * Web書庫を開くためのメールアドレスチェック・登録処理用定義
 * @author Hamaji
 *
 */
public interface ConstRegist {
	
	/** 画面遷移しないフラグ */
	public static final int NO_SEND = 0;
	
	/** メールアドレス登録画面へ遷移フラグ */
	public static final int SEND_REGIST_MAILADDRESS = 1;
	
	/** Web書庫画面遷移フラグ */
	public static final int SEND_WEB_BOOK_SHELF = 2;
	
	
	/** 他のクライアントでリセットされた場合 */
	public static final int RESET_FLAG = 3;
	
	/** DB登録エラー */
	public static final int DB_ERROR = 4;
	
	/** GetContentsリクエストエラー */
	public static final int GET_CONTENTS_ERROR = 5;
	
	/** GetAwsInfoリクエストエラー */
	public static final int GET_AWS_INFO_NG = 6;
	
	/** GetAwsInfo - パラメーターエラー */
	public static final int GET_AWS_INFO_50010 = 7;
	
	/** GetAwsInfo - サービス停止中 */
	public static final int GET_AWS_INFO_50011 = 8;
	
	/** GetAwsInfo - メンテナンス中 */
	public static final int GET_AWS_INFO_50012 = 9;
	
	/** GetAwsInfo - サーバ内部エラー */
	public static final int GET_AWS_INFO_50099 = 10;
}
