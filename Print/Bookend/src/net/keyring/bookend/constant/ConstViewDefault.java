package net.keyring.bookend.constant;


/**
 * Activity共通定数定義 - ダイアログＩＤ,環境チェックフラグ
 * @author Hamaji
 *
 */
public interface ConstViewDefault {
	
	// ------------- ダイアログID ------------------
	
	/** エラーダイアログ...アラートを閉じてアプリ終了 */
	public static final int DIALOG_ID_ERROR = 0;
	
	/** アラート表示を閉じたらそのまま元画面表示 */
	public static final int DIALOG_ID_VIEW = 1;
	
	/** プログレスダイアログ */
	public static final int DIALOG_ID_PROGRESS = 3;
	
	/** アップデートダイアログ - 通常アップデート **/
	public static final int DIALOG_ID_UPDATE_UTILL = 4;
	
	/** アップデートダイアログ - 強制アップデート */
	public static final int DIALOG_ID_UPDATE_FORCE = 5;
	
	/** 再Activationチェック確認ダイアログ */
	public static final int DIALOG_ID_REACTIVATION = 7;
	
	/** メイン画面に戻る */
	public static final int DIALOG_ID_SEND_MAIN = 8;
	
	/** アクティベーションリセットダイアログ */
	public static final int DIALOG_ID_RESET = 9;
	
	/** Webサイト誘導ダイヤログ:コンテンツ閲覧後 */
	public static final int DIALOG_ID_NAVI = 10;
	
	/** InAppBilling サポートヘルプサイトに遷移 */
	public static final int DIALOG_BILLING_NOT_SUPPORTED_ID = 11;
	
	// -------------- 環境チェックフラグ(EnvironmentCheck) ---------------
	
	/** 環境チェック - OK */
	public static final int CHECK_OK = 10;

	/** 環境チェック - USBデバックモードが有効になっている */
	public static final int CHECK_ERROR_DEBUGMODE = 12;
	
}
