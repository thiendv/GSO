package net.keyring.bookend.constant;

public interface ConstPref {
	
	/** プリファレンスファイル名 */
	public static final String SETTINGS = "settings";

	/** Preferencesキー : リストソート方法 */
	public static final String PREF_SORT = "sort";
	
	/** Preferencesキー : メールアドレス */
	public static final String PREF_MAIL_ADDRESS = "mail_address";

	/** Preferencesキー : 最後にサーバと同期した日時(Web書庫) */
	public static final String PREF_LAST_SYNC_DATE_BOOK = "LastSyncDate_book";

	/** Preferencesキー : GetContents 前回のリクエストのレスポンスで返されたOffsetの値 */
	public static final String PREF_OFFSET = "Offset";

	/** Preferencesキー : バージョン情報最終取得日時 */
	public static final String VERSION = "version_LastGetDate";
	
	/** Preferencesキー : メールアドレス一時保存 */
	public static final String PREF_MAILADDRESS_TEMPORARY = "mailAddress_temporary";
	
	/** Preferencesキー : チェックコード */
	public static final String PREF_CHECKCODE = "checkCode";

	/** Preferencesキー : クライアントの最終起動日時 */
	public static final String PREF_LAST_STARTUP_DATE_KEY = "last_startup_date";

	/** Preferencesキー : ライブラリID */
	public static final String PREF_LIBRARYID_KEY = "library_id";

	/** Preferencesキー : ユーザーID */
	public static final String PREF_USERID_KEY = "user_id";

	/** Preferencesキー : アクセスコード */
	public static final String PREF_ACCESSCODE_KEY = "access_code";

	/** Preferencesキー : Navi表示用閲覧中コンテンツダウンロードID */
	public static final String PREF_NAVI_DL_ID = "navi_dl_id";
	
	/** Preferencesキー : InAppBilling 起動時ProductIDを取得したかどうか */
	public static final String PREF_DB_INITIALIZED = "db_initialized";
	
}
