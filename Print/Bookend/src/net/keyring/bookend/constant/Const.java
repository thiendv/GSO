package net.keyring.bookend.constant;

import android.os.Environment;

/**
 * 定数定義クラス
 * @author Hamaji
 *
 */
public interface Const {
	
	//--------------- ファイルパス・名前 -------------------
	/** アプリ名 - Bookend */
	public static final String BOOKEND = "Bookend";
	
	/** KRPDF 暗号化フォーマットバージョン ... ※フォーマットが変更されたら数値を上げていくこと */
	// バージョン変更時はViewTaskのdecrypt()メソッドに処理を追加すること
	public static final int KRPDF_FORMAT_VER = 1;

	/** android - InvalidPlatformで使用 */
	public static final String ANDROID = "android";

	/** ローカルファイルパス */
	public static final String LOCAL_PATH = "/data/data/net.keyring.bookend";
	
	/** コンテンツ保存ディレクトリ */
	public static final String CONTENTS_DIR_NAME = "files";

	/** サムネイル保存ディレクトリ */
	public static final String THUMBS_DIR_NAME = "thumbs";
	
	/** 販売サイト画像保存ディレクトリ */
	public static final String STORE_IMG_DIR_NAME = "store";

	/** 販売サイト画像保存ディレクトリ */
	public static final String STORE_IMAGES_DIR = "store_images";

	/** ステージング環境で実行することを示すァイル名 */
	public static final String STG_FILE = "STG.txt";
	
	/** 常にバージョンチェックするかどうか判断するファイル名 */
	public static final String IS_VER_CHECK = "version.txt";

	/** このファイルが専用ディレクトリ内にある場合はUSBデバッグモードを許可する */
	public static final String USB_DEBUG_FILE = "USB_DEBUG.txt";

	/** 
	 * 監視対象のスクリーンキャプチャー保存フォルダ 
	 * *ファイルパスに Screenshots が含まれるファイルについては別途 ContentProvider で取得して監視しています
	 *  ここに記載されているディレクトリと二重監視になりますが、念のため残してあります。
	 */
	public static final String[] SCREENCAPTURE_DIR = {
		Environment.getExternalStorageDirectory().getPath() + "/ScreenCapture",	//	Galaxy 2.x, 3.x
		Environment.getExternalStorageDirectory().getPath() + "/Pictures/Screenshots",	//	Android 4.x 標準
		Environment.getExternalStorageDirectory().getPath() + "/Screenshots",	//	ASUS
		Environment.getExternalStorageDirectory().getPath() + "/screen shot",	//	SHARP
		"/mnt/ext_sdcard/Pictures/Screenshots",	//	HUAWEI
		"/sdcard/Pictures/Screenshots",	//	Nexus7(ASUS)
	};

	/** none */
	public static final String NONE = "none";
	
	/** false */
	public static final String FALSE = "false";
	
	/** true */
	public static final String TRUE = "true";
	
	// DL status...Web書庫から ※ステータス変更する場合はCallback.updateViewも直すこと
	/** DL status - 初期値(意味はない) */
	public static final int STATUS_INIT = 0;
	/** DL status - ダウンロード完了 */
	public static final int STATUS_DL_COMPLETE_WEB = 1;
	/** DL status - ダウンロード待ち */
	public static final int STATUS_WAIT_FOR_DL_WEB = 2;
	/** DL status - ダウンロード中 */
	public static final int STATUS_DOWNLOADING_WEB = 3;
	/** DL status - ダウンロードエラー発生 */
	public static final int STATUS_DL_ERROR_WEB = 4;
	// DL status...新規DL
	public static final int STATUS_COMPLETE_MAIN = 6;
	public static final int STATUS_ERROR_MAIN = 7;
	public static final int STATUS_DOWNLOADING_MAIN = 8;
	public static final int STATUS_REGIST_MAIN = 9;
	public static final int STATUS_REMOVE_LAYER_MAIN = 10;
	public static final int STATUS_SEND_VIEWER_MAIN = 11;
	public static final int STATUS_INVALID_PLATFORM_MAIN = 12;
	public static final int EPUBVIEWER_MARKET = 13;
	public static final int EPUBVIEWER_UPDATE = 14;
	public static final int ADOBEREADER_MARKET = 15;
	public static final int OPEN_CONTENT = 16;	//	Openパラメータによる閲覧を開始する必要がある
	public static final int OPEN_TEMPORARY_CONTENT = 17;	//	Temporaryパラメータによる閲覧を開始する必要がある
	// Activity flag
	public static final int MAIN_ACTIVITY = 0;
	public static final int WEB_BOOK_SHELF_ACTIVITY = 1;

	//---------- URL -----------
	// ● URL public
	/** 本番ドメイン */
	public static final String DOMAIN = "http://license.keyring.net/BookEnd/";
	/** 本番：現在時刻取得ＵＲＬ */
	public static final String GETTIME = "http://license.keyring.net/BookEnd/GetTime";

	// ● URL STG
	/** ステージングドメイン */
	public static final String DOMAIN_DEMO = "http://licensedemo.keyring.net/BookEnd/";
	/** ＳＴＧ：現在時刻取得ＵＲＬ */
	public static final String GETTIME_DEMO = "http://licensedemo.keyring.net/BookEnd/GetTime";


	// --------- パターン定義 ---------
	/** Dateパターン定義 */
	public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	/** Dateパターン定義 タイムゾーン付き */
	public static final String DATE_PATTERN_TIMEZONE = "yyyy-MM-dd HH:mm:ss z";
	/** 最大日時（無制限） */
	public static final String DATE_MAX = "9999-12-31 23:59:59";
	/** 最小日時 */
    public static final String DATE_MIN = "1000-01-02 00:00:00";

	// --------------- putExtraキー ----------------
	/** エラー */
	public static final String ERROR = "error";

	/** コンテンツダウンロードのインテントを処理したかどうかのフラグ */
	public static final String IS_PROCESSED = "isProcessed";
	
	/**  コンテンツダウンロードのインテントをキャンセルしたかどうかのフラグ */
	public static final String IS_CANCELED = "isCanceled";

	/** RemoveLayerServiceへ復号化するファイルパスを渡すキー */
	public static final String DECRYPT_FILE_PATH = "decryptFilePath";

	/** EncryptionServiceへ暗号化するファイルパスを渡すキー */
	public static final String ENCRYPT_FILE_NAME = "encryptFileName";

	/** ProcessCheckServiceに閲覧するファイル名を渡すキー */
	public static final String VIEW_CONTENT_NAME = "view_content_name";
	
	/** ProcessCheckServiceにプロセス終了を通知するキー */
	public static final String FINISH_PROCESS = "finish_process";

	/** 新しくDBに登録したコンテンツ情報を渡すキー */
	public static final String NEW_CONTENTS = "new_contents";

	/** 初期起動時ダウンロードリンクから来た場合のintent保存フラグ */
	public static final String FROM_DLLINK_FIRST = "fromDlLinkFirst";

	/** Web書庫からダウンロードする際必要なコンテンツIDを渡すキー */
	public static final String DL_DOWNLOAD_ID = "dl_download_id";
	
	//--------------- scheme flag ----------------
		/** コンテンツDLリンク - scheme=behttp */
		public static final int SCHEME_BEHTTP = 30;
		/** Webページリンク - scheme=beinfo */
		public static final int SCHEME_BEINFO = 31;
		
}
