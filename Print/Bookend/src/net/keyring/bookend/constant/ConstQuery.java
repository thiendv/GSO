package net.keyring.bookend.constant;

/**
 * Queryタグ - 定数定義
 * @author Hamaji
 *
 */
public interface ConstQuery {
	
	// DL URLパラメータ　※KEYが増えた場合QUERY_KEYSにも追記すること
	public static final String AUTHKEY = "AuthKey";
	public static final String AUTHKEY_ID = "AuthKeyID";
	public static final String CONTENTS_TYPE = "ContentsType";
	public static final String DISTRIBUTOR_NAME = "DistributorName";
	public static final String DISTRIBUTOR_URL = "DistributorURL";
	public static final String TITLE = "Title";
	public static final String AUTHOR = "Author";
	public static final String KEYWORDS = "Keywords";
	public static final String TAG = "Tag";
	public static final String CONTENTS_URL = "ContentsURL";
	public static final String THUMB_URL = "ThumbURL";
	public static final String VALID_TERM = "ValidTerm";
	public static final String NUMBER_OF_BROWSING = "NumberOfBrowsing";
	public static final String NUMBER_OF_PRINTING = "NumberOfPrinting";
	public static final String INVALID_PLATFORM = "InvalidPlatform";
	public static final String MAX_SHARED_DEVICE = "MaxSharedDevice";
	public static final String SITE_HOST = "SiteHost";
	public static final String DOWNLOAD_ID = "DownloadID";
	public static final String NAVIGATE_URL = "NavigateURL";
	public static final String NAVIGATE_MESSAGE = "NavigateMessage";
	public static final String PRODUCT_ID = "ProductID";
	public static final String OPEN = "Open";
	public static final String TEMPORARY = "Temporary";
	
	/** pdf */
	public static final String PDF = "pdf";
	/** krpdf */
	public static final String KRPDF = "krpdf";
	/** epub */
	public static final String EPUB = "epub";
	/** krepub */
	public static final String KREPUB = "krepub";
	/** bec */
	public static final String BEC = "bec";
	/** krbec */
	public static final String KRBEC = "krbec";
	/** krbec */
	public static final String MCMAGAZINE = "mcm";
	/** krbec */
	public static final String MCBOOK = "epubx";
	/** krbec */
	public static final String MCCOMIC = "epubxf";
	/** krepa */
	public static final String KREPA = "krepa";
	/** krpdfx */
	public static final String KRPDFX = "krpdfx";
	
	/** URL内のキーチェックするための文字配列 */
	public static final String[] QUERY_KEYS = {AUTHKEY, AUTHKEY_ID, CONTENTS_TYPE,
		DISTRIBUTOR_NAME, DISTRIBUTOR_URL, TITLE, AUTHOR, KEYWORDS, TAG, CONTENTS_URL,
		THUMB_URL, VALID_TERM, NUMBER_OF_BROWSING, NUMBER_OF_PRINTING, INVALID_PLATFORM,
		MAX_SHARED_DEVICE, SITE_HOST, NAVIGATE_URL, NAVIGATE_MESSAGE, PRODUCT_ID, OPEN, TEMPORARY};
	
}
