package net.keyring.bookend.constant;
/**
 * DBクラス用定数定義クラス
 * @author Hamaji
 *
 */
public interface ConstDB {
	/** DB名[bookend] */
	public static final String DB = "contents.db";
	
	/** version */
	public static final int DB_VERSION = 5;

	/** メイン画面表示フラグ(DB登録にも使うので値を変えないこと) */
	public static final int MAIN_VIEW_FLAG = 0;
	/** メイン画面非表示フラグ(DB登録にも使うので値を変えないこと) */
	public static final int MAIN_VIEW_NONE_FLAG = 1;

	// ------- テーブル作成SQL -------
	/** コンテンツ：テーブル名 */
	public static final String CONTENTS_TABLE = "contents";
	/** アップデートコンテンツ：テーブル名 */
	public static final String UPDATE_CONTENTS_TABLE = "update_contents";
	/** アップデートライセンス：テーブル名 */
	public static final String UPDATE_LICENSE_TABLE = "update_license";
	/** 列名(_id列がある前提で実装されているクラスがある為) */
	public static final String ID = "_id";
	/** カラム - コンテンツID */
	public static final String CONTENTS_ID = "contents_id";
	/** カラム - ダウンロードID */
	public static final String DOWNLOAD_ID = "download_id";
	/** カラム - コンテンツタイプ */
	public static final String TYPE = "type";
	/** カラム - タイトル(UTF8) */
	public static final String TITLE = "title";
	/** カラム - 著者(UTF8) */
	public static final String AUTHOR = "author";
	/** カラム - キーワード(UTF8) */
	public static final String KEYWORDS = "keywords";
	/** カラム - 配布者の表示名(UTF8) */
	public static final String DISTRIBUTOR_NAME = "distributor_name";
	/** カラム - 配布者のURL */
	public static final String DISTRIBUTOR_URL = "distributor_url";
	/** カラム - コンテンツのファイルパス */
	public static final String FILE_PATH = "file_path";
	/** カラム - コンテンツダウンロードURL */
	public static final String CONTENTS_DL_URL = "contents_dl_url";
	/** カラム - サムネイルのファイルパス */
	public static final String THUMB_PATH = "thumb_path";
	/** カラム - サムネイルダウンロードパス */
	public static final String THUMB_DL_URL = "thumb_dl_url";
	/** カラム - ダウンロード日時(UTC:日本時間-9h) */
	public static final String DOWNLOAD_DATE = "download_date";
	/** カラム - 最終閲覧日時(UTC:日本時間-9h) */
	public static final String LAST_ACCESS_DATE = "last_access_date";
	/** カラム - 閲覧期限(UTC:日本時間-9h) */
	public static final String EXPIRY_DATE = "expiry_date";
	/** カラム - 閲覧を許可しないプラットフォーム */
	public static final String INVALID_PLATFORM = "invalid_platform";
	/** 削除フラグ */
	public static final String DELETE_FLAG = "delete_flag";
	/** メイン画面に表示するかしないかフラグ */
	public static final String MAIN_VIEW = "main_view";
	/** オーナーパスワード(コンテンツタイプがpdf・krpdfのもののみ) - key */
	public static final String OWNER_PASSWORD = "owner_password";
	/** CRC32 */
	public static final String CRC32 = "crc32";
	/** KRPDF暗号化キー */
	public static final String ENCRYPTION_KEY = "EncryptionKey";
	/** KRPDF暗号化フォーマットバージョン */
	public static final String KRPDF_FORMAT_VER = "krpdf_format_ver";

	/** コンテンツのファイルサイズ(byte) */
	public static final String FILESIZE = "file_size";
	/** オリジナルファイル名 */
	public static final String ORIGINAL_FILE_NAME = "original_file_name";
	/** コンテンツのページ数 */
	public static final String PAGE_COUNT = "page_count";
	/** クライアント側でのデータ最終更新日時(UTC) */
	public static final String LAST_MODIFY = "last_modify";
	/** コンテンツについているラベルのID */
	public static final String LABEL_ID = "label_id";
	/** コンテンツについているセールス情報のID */
	public static final String SALES_ID = "sales_id";
	/** 共有台数(分母) */
	public static final String SHARED_DEVICE_D = "shared_device_d";
	/** 閲覧回数(分母) */
	public static final String BROWSE_D = "browse_d";
	/** 印刷回数(分母) */
	public static final String PRINT_D = "print_d";
	/** 共有台数(分子) */
	public static final String SHARED_DEVICE_M = "shared_device_m";
	/** 閲覧回数(分子) */
	public static final String BROWSE_M = "browse_m";
	/** 印刷回数(分子) */
	public static final String PRINT_M = "print_m";
	/** 共有台数 */
	public static final String SHARED_DEVICE = "shared_device";
	/** 閲覧回数 */
	public static final String BROWSE = "browse";
	/** ダウンロードステータス */
	public static final String DL_STATUS = "dl_status";
	/** ダウンロード進捗 */
	public static final String DL_PROGRESS = "dl_progress";
	
	/** Webサイト誘導用テーブル名[navi_sales] */
	public static final String NAVI_SALES_TABLE = "navi_sales";
	/** Webサイト誘導URL */
	public static final String NAVIGATE_URL = "navi_url";
	/** Webサイト誘導メッセージ([言語タイプ1];[メッセージ1] ;[言語タイプ2];[メッセージ2] ....) */
	public static final String NAVIGATE_MESSAGE = "navi_message";
	
	/** InAppBilling テーブル名 [Purchase] */
	public static final String PURCHASED_TABLE = "purchased";
	/** InAppBilling オーダーID */
	public static final String PURCHASED_ORDER_ID = "purchased_order_id";
	/** InAppBilling プロダクトID */
	public static final String PURCHASED_PRODUCT_ID = "purchased_product_id";
	/** InAppBilling コンテンツのダウンロードID */
	public static final String PURCHASED_DOWNLOAD_ID = "purchased_download_id";
	
	//**********************************************
	// contentsテーブル
	//**********************************************
	
	/** contentsテーブル作成SQL */
	public static final String CREATE_CONTENTS_TABLE = "create table " + CONTENTS_TABLE + "("
		+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
		+ CONTENTS_ID + " TEXT NOT NULL,"
		+ DOWNLOAD_ID + " TEXT UNIQUE NOT NULL,"
		+ TITLE + " TEXT,"
		+ AUTHOR + " TEXT,"
		+ KEYWORDS + " TEXT,"
		+ DISTRIBUTOR_NAME + " TEXT,"
		+ DISTRIBUTOR_URL + " TEXT,"
		+ FILE_PATH + " TEXT,"
		+ ENCRYPTION_KEY + " TEXT,"
		+ KRPDF_FORMAT_VER + " INTEGER,"
		+ CONTENTS_DL_URL + " TEXT,"
		+ THUMB_PATH + " TEXT,"
		+ THUMB_DL_URL + " TEXT,"
		+ DOWNLOAD_DATE + " TEXT,"
		+ LAST_ACCESS_DATE + " TEXT,"
		+ EXPIRY_DATE + " TEXT NOT NULL,"
		+ INVALID_PLATFORM + " TEXT,"
		+ FILESIZE + " TEXT,"
		+ ORIGINAL_FILE_NAME + " TEXT,"
		+ PAGE_COUNT + " INTEGER,"
		+ LAST_MODIFY + " TEXT,"
		+ LABEL_ID + " TEXT,"
		+ SALES_ID + " TEXT,"
		+ SHARED_DEVICE_M + " INTEGER,"
		+ SHARED_DEVICE_D + " INTEGER,"
		+ BROWSE_M + " INTEGER,"
		+ BROWSE_D + " INTEGER,"
		+ PRINT_M + " INTEGER,"
		+ PRINT_D + " INTEGER,"
		+ TYPE + " TEXT,"
		+ OWNER_PASSWORD + " TEXT,"
		+ CRC32 + " TEXT,"
		+ MAIN_VIEW + " INTEGER,"
		+ DL_STATUS + " INTEGER,"
		+ DL_PROGRESS + " INTEGER,"
		+ DELETE_FLAG + " TEXT NOT NULL);";

	/** 削除テーブル名[contents] */
	public static final String DROP_CONTENTS_TABLE = "DROP TABLE " + CONTENTS_TABLE + ";";


	//**********************************************
	// UpdateContentsテーブル
	//**********************************************
	
	/** UpdateContentsテーブル作成SQL */
	public static final String CREATE_UP_CON_TABLE = "create table " + UPDATE_CONTENTS_TABLE + "("
		+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
		+ DOWNLOAD_ID + " TEXT UNIQUE NOT NULL,"
		+ LAST_ACCESS_DATE + " TEXT,"
		+ DELETE_FLAG + " TEXT,"
		+ LAST_MODIFY + " TEXT);";

	/** 削除テーブル名[update_contents] */
	public static final String DROP_UP_CON_TABLE = "DROP TABLE " + UPDATE_CONTENTS_TABLE + ";";
	
	//**********************************************
	// UpdateLicenseテーブル
	//**********************************************

	/** UpdateLicenseテーブル作成SQL */
	public static final String CREATE_UP_LIC_TABLE = "create table " + UPDATE_LICENSE_TABLE + "("
		+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
		+ DOWNLOAD_ID + " TEXT UNIQUE NOT NULL,"
		+ SHARED_DEVICE + " TEXT,"
		+ BROWSE + " INTEGER);";

	/** 削除テーブル名[update_license] */
	public static final String DROP_UP_LIC_TABLE = "DROP TABLE " + UPDATE_LICENSE_TABLE + ";";
	
	
	//**********************************************
	// NavigateSalesテーブル
	//**********************************************
	
	/** NavigateSalesテーブル作成SQL */
	public static final String CREATE_NAVI_SALES_TABLE = "create table " + NAVI_SALES_TABLE + "("
		+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
		+ DOWNLOAD_ID + " TEXT UNIQUE NOT NULL,"
		+ NAVIGATE_URL + " TEXT,"
		+ NAVIGATE_MESSAGE + " TEXT);";

	/** 削除テーブル名[navi_sales] */
	public static final String DROP_NAVI_SALES_TABLE = "DROP TABLE " + NAVI_SALES_TABLE + ";";
	
	//********************************************
	// Purchaseテーブル
	//********************************************
	
	/** Purchaseテーブル作成SQL */
	public static final String CREATE_PURCHASE_TABLE = "create table " + PURCHASED_TABLE + "("
		+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
		+ PURCHASED_ORDER_ID + " TEXT UNIQUE NOT NULL,"
		+ PURCHASED_PRODUCT_ID + " TEXT,"
		+ PURCHASED_DOWNLOAD_ID + " TEXT);";
	
	/** 削除テーブル名[purchased] */
	public static final String DROP_PURCHASE_TABLE = "DROP TABLE " + PURCHASED_TABLE + ";";
	

	/** カラム名配列 */
	//public static final String[] COLUMNS = {CONTENTS_ID,DOWNLOAD_ID,TYPE,TITLE,AUTHOR,KEYWORDS,DISTRIBUTOR_NAME,DISTRIBUTOR_URL,FILE_PATH,THUMB_PATH,DOWNLOAD_DATE,LAST_ACCESS_DATE,EXPIRY_DATE,INVALID_PLATFORM};

	/** ソート方法：取得日時(降順)...初期表示 */
	public static int DESCEND_DATE = 100;

	/** ソート方法：取得日時(昇順) */
	public static int ASCEND_DATE = 101;

	/** ソート方法：タイトル名(降順) */
	public static int DESCEND_NAME = 102;

	/** ソート方法：タイトル名(昇順) */
	public static int ASCEND_NAME = 103;

}
