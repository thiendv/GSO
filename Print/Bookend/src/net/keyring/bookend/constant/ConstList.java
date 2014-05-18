package net.keyring.bookend.constant;

/**
 * リスト表示に必要な定数定義
 * @author Hamaji
 *
 */
public interface ConstList {
	
	//	Adobe ReaderのアプリケーションID
	//	*Google Playのページを開くために使用されます。
	public final String ADOBE_READER = "com.adobe.reader";
	
	//	EPubViewerのアプリケーションID
	//	*Google Playのページを開くために使用されます。
	public static final String EPUB_VIEWER = "net.keyring.bookend.viewer";

	//	Mcm_Epub_EpubxfViewer
	//	*単なる識別名なので、値は何でも構いません。
	public static final String MCM_PUBX_PUBXF_VIEWER = "net.keyring.bookend.morisawaviewer";
	
	//	Audio Viewer 
	//	*単なる識別名なので、値は何でも構いません。
	public static final String AUDIO_VIEWER = ".net.keyring.audioviewer";
	
	// 順番変えないこと
	public static final int PDF = 1;
	public static final int KRPDF = 2;
	public static final int EPUB = 3;
	public static final int KREPUB = 4;
	public static final int BEC = 5;
	public static final int KRBEC = 6;
	
	public static final int KRPDFX = 10;
	
	public static final int MCM = 11;
	public static final int EPUBX =12;
	public static final int EPUBXF =13;
	public static final int KREPA = 14;

	/** オンライン閲覧 - アプリない領域コピーファイル名 */
	public static final String TEMPORARY_FILE = "temporary";
	
	// ------------- PDFコピーフラグ --------------
	/** PDFファイルをコピー...SDカード=>アプリケーション領域 */
	public static final int COPY_PDF = 400;
	/** KRPDFファイルを暗号化してコピー...アプリケーション領域=>SDカード */
	public static final int COPY_KRPDF_EN = 401;
	/** KRPDFファイルを復号化してコピー...SDカード=>アプリケーション領域 */
	public static final int COPY_KRPDF_DE = 402;
	
	// ------------- 表示リスト取得条件 -------------
	
	/** ファイルの存在するコンテンツのみ表示(一部例外有り) */
	public static final int VIEW_EXIST_CONTENTS = 200;
	
	/** ファイルの存在しないコンテンツのみ表示 */
	public static final int VIEW_DELETE_CONTENTS = 201;
	
	/** DBに登録されている全てのコンテンツを表示 */
	public static final int VIEW_ALL_CONTENTS = 202;
	

	// -------------- コンテンツステータス ------------
	
	/** 既にダウンロード済 */
	public static final int DOWNLOADED = 300;
	
	/** 閲覧期限超え */
	public static final int EXPIRYDATE_NG = 301;
	
	/** 閲覧回数超え */
	public static final int BROWSE_NG = 302;
	
	/** 共有台数超え */
	public static final int SHARED_DEVICE_NG = 303;
	
	/** InvalidPlatformがAndroid */
	public static final int INVALID_PLATFORM_NG = 304;
	
	/** ファイルが存在しない */
	public static final int FILE_NONE = 305;
	
	/** ファイルタイプがkrpdf,epub,krepub以外の場合 */
	public static final int FILE_TYPE_NG = 306;
	
	/** OS時計チェックNG */
	public static final int OS_TIME_NG = 307;
	
	/** オフライン時に閲覧制限があるものを閲覧しようとした場合 */
	public static final int OFFLINE_BROWSE_COUNT = 308;
	
	/** KRPDFファイルのレイヤー削除エラー */
	public static final int ERROR_REMOVE_LAYER = 309;
	
	/** DL中のkrpdfファイルがある(Web書庫からDL) */
	public static final int KRPDF_COUNT_NG = 310;
	
	/** krepub鍵取得エラー */
	public static final int ERROR_KREPUB_KEY = 312;
	
	/** Authkeyチェックエラー - コンテンツID取得エラー */
	public static final int AUTHKEY_ERROR_CONTENTS_ID = 317;
	
	/** Authkeyチェックエラー  - host null */
	public static final int AUTHKEY_ERROR_HOST_NULL = 318;
	
	/** Authkeyチェックエラー - Authkey null */
	public static final int AUTHKEY_NULL = 319;
	
	/** Authkeyチェックエラー - Ahthkey認証エラー */
	public static final int AUTHKEY_ERROR = 320;
	
	/** ダウンロード完了問題なし */
	public static final int DL_OK = 322;
	
	// ------------ 表示リストキー ----------------
	
	/** リストキー - ID */
	public static final String ID = "id";
	
	/** リスト表示キー - ステータス */
	public static final String STATUS = "status";
	
	/** リスト表示キー - タイトル */
	public static final String TITLE = "title";
	
	/** リスト表示キー - 著者名 */
	public static final String AUTHOR = "author";
	
	/** リスト表示キー - サムネイルURL */
	public static final String THUMB_URL = "thumbnail_url";
	
	/** webサイト誘導用 リストキー - ダウンロードID */
	public static final String DOWNLOAD_ID = "downloadID";
}
