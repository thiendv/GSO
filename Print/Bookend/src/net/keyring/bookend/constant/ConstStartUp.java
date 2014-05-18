package net.keyring.bookend.constant;

/**
 * 初期起動時環境チェックフラグ 定数定義
 * @author Hamaji
 *
 */
public interface ConstStartUp {
	
	//--------------- 初期起動時チェックフラグ ------------------
	
	/** 初回チェック - OK */
	public static final int CHECK_OK = 50;
	
	/** 初回チェック - SDカードがセットされていない(起動しない) */
	public static final int CHECK_ERROR_SD = 51;
	
	/** 初回チェック - ActivationチェックNG(起動しない) */
	public static final int CHECK_ERROR_ACTIVATION = 52;
	
	/** 初回チェック - 初回Activationチェック時にオフライン状態(起動しない) */
	public static final int CHECK_OFFLINE_ACTIVATION = 53;
	
	/** 初回チェック - CheckActivationチェックNG(起動しない) */
	public static final int CHECK_ERROR_CHECKACTIVATION = 54;
	
	/** 初回チェック - 再Activationチェックが必要 */
	public static final int CHECK_REQUEST_ACTIVATION = 55;
	
	/** 初回チェック - バージョンアップリクエストが必要 */
	public static final int CHECK_VERSIONUP_REQUEST = 56;
	
	/** 初回チェック - バージョンアップリクエスト ... 強制アップデート  */
	public static final int CHECK_VERSIONUP_FORCE = 57;
	
	/** 初回チェック - バージョンアップリクエスト ... 通常アップデート */
	public static final int CHECK_VERSIONUP_USUALLY = 58;
	
	/** 環境チェック - OS時計がずれている */
	public static final int CHECK_ERROR_CLOCK = 59;
	
	/** 初回チェック - バージョンチェックエラー ... 通常アップデート */
	public static final int CHECK_ERROR_VERSION_CHECK = 60;
	
	// ------------ 初期起動チェック結果を渡すMAPキー ----------------
	
	/** ステータスキーを渡すMAPキー */
	public static final String STATUS_KEY = "status_key";
	
	/** エラー時にダイアログ表示するメッセージを渡すMAPキー */
	public static final String DIALOG_MESSAGE = "dialog_message";
	
	/** バージョンアップクライアントダウンロードURLを渡すMAPキー */
	public static final String UPDATE_URL = "update_url";
	
}
