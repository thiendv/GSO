package net.keyring.bookend.constant;
/**
 * ServerRequest用定数定義クラス
 * @author Hamaji
 *
 */
public interface ConstReq {

	// リクエストアクションフラグ ※リクエストURL末に付く文字列にすること
	public static final String ACTIVATION = "Activate";
	public static final String CHECK_ACTIVATION = "CheckActivation";
	public static final String AUTH_WEB_SITE = "AuthWebSite";
	public static final String REGIST_MAIL_ADDRESS2 = "RegistMailAddress2";
	public static final String BOOKEND_PIN2 = "BookendPIN2";
	public static final String DEACTIVATE = "Deactivate";
	public static final String SET_CONTENTES = "SetContents";
	public static final String ACTIVATE2 = "Activate2";
	public static final String GET_CONTENTS = "GetContents";
	public static final String GET_AWS_INFO = "GetAwsInfo";
	public static final String UPDATE_LICENSE = "UpdateLicense";
	public static final String UPDATE_CONTENTS = "UpdateContents";
	public static final String CHANGE_USERID = "ChangeUserId";
	public static final String GET_STORE_LIST = "GetStoreList";
	public static final String GET_LICENSE = "GetLicense";
	public static final String GET_CONTENTS_INFO = "GetContentsInfo";
	public static final String GET_VERSION = "GetVersion";
	public static final String CHECK_MAIL_ADDRESS = "CheckMailAddress";
	public static final String REQ_RESET = "Reset";
	public static final String BOOKEND_MAIL = "BookendMail";

	// xmlタグ名, param名
	/** AccessCode */
	public static final String ACCESSCODE = "AccessCode";
	/** UserID */
	public static final String USER_ID = "UserID";
	/** Status */
	public static final String STATUS = "Status";
	/** StatusDescription */
	public static final String DESCRIPTION = "StatusDescription";
	/** Error */
	public static final String ERROR = "Error";
	/** LibraryID */
	public static final String LIBRARY_ID = "LibraryID";
	/** Reset */
	public static final String RESET = "Reset";
	/** Update */
	public static final String UPDATE = "Update";
	/** MailAddress */
	public static final String MAIL_ADDRESS = "MailAddress";
	/** update_flag */
	public static final String UPDATE_FLAG = "Update";
	/** PIN */
	public static final String PIN = "PIN";
	/** language */
	public static final String LANGUAGE = "Language";
	/** Authkey */
	public static final String AUTHKEY = "AuthKey";
	/** AuthKeyID */
	public static final String AUTHKEYID = "AuthKeyID";
	/** WebSiteHost */
	public static final String HOST = "WebSiteHost";
	/** IPAddress */
	public static final String IP = "IPAddress";
	/** ContentsID */
	public static final String CONTENTS_ID = "ContentsID";
	/** CheckCode */
	public static final String CHECK_CODE = "CheckCode";
	/** LastSyncDate */
	public static final String LAST_SYNC_DATE = "LastSyncDate";
	/** DataMax */
	public static final String DATA_MAX = "DataMax";
	/** Offset */
	public static final String OFF_SET = "Offset";
	/** Verb */
	public static final String VERB = "Verb";
	/** Date */
	public static final String DATE = "Date";
	/** Path */
	public static final String PATH = "Path";
	/** S3Host */
	public static final String S3HOST = "S3Host";
	/** HMAC */
	public static final String HMAC = "HMAC";
	/** BucketName */
	public static final String BUCKETNAME = "BucketName";
	/** owner_id */
	public static final String OWNER_ID = "owner_id";
	/** Owner_id */
	public static final String OWNER_ID_L = "OwnerID";
	/** OS */
	public static final String OS = "OS";
	/** store */
	public static final String STORE = "store";
	/** StoreName */
	public static final String STORE_NAME = "StoreName";
	/** StoreURL */
	public static final String STORE_URL = "StoreURL";
	/** StoreImageURL */
	public static final String STORE_IMAGE_URL = "StoreImageURL";
	/** StoreType */
	public static final String STORE_TYPE = "StoreType";
	/** OrgUserID */
	public static final String ORG_USERID = "OrgUserID";
	/** NewUserID */
	public static final String NEW_USERID = "NewUserID";
	/** Program */
	public static final String PROGRAM = "Program";
	/** OSVersion */
	public static final String OS_VERSION = "OSVersion";
	/** Version */
	public static final String VERSION = "Version";
	/** ReferURL */
	public static final String REFER_URL = "ReferURL";
	/** Downloadurl */
	public static final String DOWNLOAD_URL = "DownloadURL";
	/** ForceUpdate */
	public static final String FORCE_UPDATE = "ForceUpdate";
	/** AllReset */
	public static final String ALL_RESET = "AllReset";
	/** ProcessInfo */
	public static final String PROCESS_INFO = "ProcessInfo";
	/** PrinterInfo */
	public static final String PRINTER_INFO = "PrinterInfo";
	/** GetContents Compress */
	public static final String COMPRESS = "Compress";

	// ---------- GetContents ----------
	public static final String CONTENTS_NEW = "ContentsNew";
	public static final String CONTENTS_UPDATE = "ContentsUpdate";
	public static final String LICENSE = "License";
	public static final String FILE_SIZE = "FileSize";
	public static final String PAGE_COUNT = "PageCount";
	public static final String SALES_ID = "SalesID";
	public static final String DELETE_FLAG = "DeleteFlag";
	public static final String MAX_SHARED_DEVICE = "MaxSharedDevice";
	public static final String SHARED_DEVICE = "SharedDevice";
	public static final String MAX_BROWSE = "MaxBrowse";
	public static final String BROWSE = "Browse";
	public static final String MAX_PRINT = "MaxPrint";
	public static final String PRINT = "Print";
	public static final String INVALID_PLATFORM = "InvalidPlatform";
	public static final String EXPIRY_DATE = "ExpiryDate";
	public static final String ORIGINAL_FILE_NAME = "OriginalFileName";
	public static final String CONTENTS_URL = "ContentsURL";
	public static final String THUMB_URL = "ThumbURL";
	public static final String KEY = "Key";

	// ------- GetContentsInfo --------
	public static final String DISTRIBUTOR_URL = "DistributorURL";
	public static final String CRC32 = "CRC32";
	public static final String PRODUCT = "Product";

	// ---------- SetContents ----------
	public static final String DATA_COUNT = "DataCount";
	public static final String DOWNLOAD_ID = "DownloadID";
	public static final String FILE_TYPE = "FileType";
	public static final String DOWNLOAD_DATE = "DownloadDate";
	public static final String LAST_ACCESS_DATE = "LastAccessDate";
	public static final String LAST_MODIFY = "LastModify";
	public static final String DOWNLOADED = "Downloaded";
	public static final String DELETE = "Delete";
	public static final String LICENSE_EXIST = "LicenseExist";
	public static final String LABEL_ID = "LabelID";
	public static final String LABEL_NAME = "LabelName";
	public static final String LABEL_LAST_MODIFY = "LabelLastModify";
	public static final String LICENSE_MAX_SHARED_DEVICE = "LicenseMaxSharedDevice";
	public static final String LICENSE_MAX_BROWSE = "LicenseMaxBrowse";
	public static final String LICENSE_MAX_PRINT = "LicenseMaxPrint";
	public static final String LICENSE_EXPIRY = "LicenseExpiry";
	public static final String LICENSE_INVALID_PLATFORM = "LicenseInvalidPlatform";
	public static final String TITLE = "Title";
	public static final String AUTHOR = "Author";
	public static final String KEYWORDS = "Keywords";
	public static final String DISTRIBUTOR_NAME = "DistributorName";
	public static final String DISTRIBUTOR_Url = "DistributorUrl";
	public static final String NAVIGATE_URL = "NavigateUrl";
	public static final String NAVIGATE_MESSAGE = "NavigateMessage";

}
