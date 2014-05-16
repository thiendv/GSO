package net.keyring.bookend.bean;

/**
 * 1レコードのデータを保持するオブジェクト3
 * Intentに詰めてやり取りするのでSerializableをimplementsする
 * @author Hamaji
 *
 */
public class BookBeans{
	// プロパティ
	private long rowId = -1;
	private String contents_id = null;
	private String download_id = null;
	private String type = null;
	private String title = null;
	private String author = null;
	private String keywords = null;
	private String distributor_name = null;
	private String distributor_url = null;
	private String file_path = null;
	private String contents_DL_URL = null;
	private String thumb_path = null;
	private String thumb_DL_URL = null;
	private String download_date = null;
	private String last_access_date = null;
	private String expiry_date = null;
	private String delete_flag = null;
	private String invalidPlatform = null;
	private int main_view = -1;
	private String key = null;
	private String crc32 = null;
	private String encryptionKey = null;
	private int krpdfFormatVer = 0;
	private int dl_status;
	private int dl_progress;

	private String fileSize = null;
	private String originalFileName = null;
	private int pageCount = -1;
	private String lastModify = null;
	private String labelID = null;
	private String salesID = null;

	private int d_sharedDevice = -1;
	private int m_sharedDevice = -1;
	private int d_browse = -1;
	private int m_browse = -1;
	private int d_print = -1;
	private int m_print = -1;
	
	/**
	 * KRPDF暗号化フォーマットバージョンを取得
	 * @return krpdfFormatVer KRPDF暗号化フォーマットバージョン
	 */
	public int getKrpdfFormatVer() {
		return krpdfFormatVer;
	}
	/**
	 * KRPDF暗号化フォーマットバージョンをセット
	 * @param krpdfFormatVer KRPDF暗号化フォーマットバージョン
	 */
	public void setKrpdfFormatVer(int krpdfFormatVer) {
		this.krpdfFormatVer = krpdfFormatVer;
	}
	/**
	 * @return encryprionKey
	 */
	public String getEncryptionKey() {
		return encryptionKey;
	}
	/**
	 * @param encryprionKey セットする encryprionKey
	 */
	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}
	/**
	 * @return crc32
	 */
	public String getCRC32() {
		return crc32;
	}
	/**
	 * @param crc32 セットする crc32
	 */
	public void setCRC32(String crc32) {
		this.crc32 = crc32;
	}


	private String shared_device = null;
	private int browse = 0;

	public long getRowId() {
		return rowId;
	}
	public void setRowId(long rowId) {
		this.rowId = rowId;
	}

	/**
	 * オーナーパスワードを取得
	 * @return key
	 */
	public String getKey() {
		return key;
	}
	/**
	 * オーナーパスワードをセット
	 * @param key セットする key
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * @return contents_DL_URL
	 */
	public String getContents_DL_URL() {
		return contents_DL_URL;
	}
	/**
	 * @param contents_DL_URL セットする contents_DL_URL
	 */
	public void setContents_DL_URL(String contents_DL_URL) {
		this.contents_DL_URL = contents_DL_URL;
	}
	/**
	 * @return thumb_DL_URL
	 */
	public String getThumb_DL_URL() {
		return thumb_DL_URL;
	}
	/**
	 * @param thumb_DL_URL セットする thumb_DL_URL
	 */
	public void setThumb_DL_URL(String thumb_DL_URL) {
		this.thumb_DL_URL = thumb_DL_URL;
	}
	/**
	 * UpdateLicense用 - 共有台数 をセット
	 * @param ダウンロード:"true" or 削除:"false"
	 */
	public void setSharedDevice(String sharedDevice){
		this.shared_device = sharedDevice;
	}

	/**
	 * UpdateLicense用 - 共有台数 を取得
	 * @return ダウンロード:"true" or 削除:"false"
	 */
	public String getSharedDevice(){
		return this.shared_device;
	}

	/**
	 * UpdateLicense用 - 残り閲覧回数の変化値 をセット
	 * @param 残り閲覧回数の変化値
	 */
	public void setBrowse(int browse){
		this.browse = browse;
	}

	/**
	 * UpdateLicense用 - 残り閲覧回数の変化値 を取得
	 * @param 残り閲覧回数の変化値
	 */
	public int getBrowse(){
		return this.browse;
	}

	/**
	 * メイン画面に表示するかフラグを取得
	 * @return 表示する場合は0、それ以外は1を返す
	 */
	public int getMainView(){
		return this.main_view;
	}

	/**
	 * メイン画面に表示するかフラグをセット
	 * @param flag
	 */
	public void setMainView(int flag){
		this.main_view = flag;
	}

	/**
	 * コンテンツIDを取得
	 * @return コンテンツID
	 */
	public String getContents_id() {
		return contents_id;
	}

	/**
	 * コンテンツIDをセット
	 * @param コンテンツID
	 */
	public void setContents_id(String contents_id) {
		this.contents_id = contents_id;
	}

	/**
	 * ダウンロードIDを取得
	 * @return ダウンロードID
	 */
	public String getDownload_id() {
		return download_id;
	}

	/**
	 * ダウンロードIDをセット
	 * @param ダウンロードID
	 */
	public void setDownload_id(String download_id) {
		this.download_id = download_id;
	}

	/**
	 * コンテンツタイプを取得
	 * @return コンテンツタイプ
	 */
	public String getType() {
		return type;
	}
	/**
	 * コンテンツタイプををセット
	 * @param コンテンツタイプ
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * タイトルを取得
	 * @return タイトル
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * タイトルをセット
	 * @param タイトル
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 著者を取得
	 * @return 著者名
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * 著者をセット
	 * @param 著者名
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * キーワードを取得
	 * @return キーワード
	 */
	public String getKeywords() {
		return keywords;
	}

	/**
	 * キーワードをセット
	 * @param キーワード
	 */
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	/**
	 * 配布者の表示名を取得
	 * @return 配布者の表示名
	 */
	public String getDistributor_name() {
		return distributor_name;
	}

	/**
	 * 配布者の表示名をセット
	 * @param 配布者の表示名
	 */
	public void setDistributor_name(String distributor_name) {
		this.distributor_name = distributor_name;
	}

	/**
	 * 配布者のURLを取得
	 * @return 配布者のURLを取得
	 */
	public String getDistributor_url() {
		return distributor_url;
	}

	/**
	 * 配布者のURLをセット
	 * @param 配布者のURL
	 */
	public void setDistributor_url(String distributor_url) {
		this.distributor_url = distributor_url;
	}

	/**
	 * ファイルパスを取得
	 * @return ファイルパスを取得
	 */
	public String getFile_path() {
		return file_path;
	}

	/**
	 * ダウンロードしたファイルの保存先パスをセット
	 * @param ダウンロードしたファイルの保存先パス
	 */
	public void setFile_path(String file_path) {
		this.file_path = file_path;
	}
	public String getThumb_path() {
		return thumb_path;
	}
	public void setThumb_path(String thumb_path) {
		this.thumb_path = thumb_path;
	}

	/**
	 * ダウンロード日時(UTC)を取得
	 * @return ダウンロード日時(UTC)...yyyy-MM-dd HH:mm:ss
	 */
	public String getDownload_date() {
		return download_date;
	}

	/**
	 * ダウンロード日時(UTC)をセット
	 * @param ダウンロード日時
	 */
	public void setDownload_date(String download_date) {
		this.download_date = download_date;
	}

	/**
	 * 最終閲覧日時(UTC)を取得
	 * @return 最終閲覧日時(UTC) yyyy-MM-dd HH:mm:ss
	 */
	public String getLast_access_date() {
		return last_access_date;
	}

	/**
	 * 最終閲覧日時(UTC)をセット
	 * @param 最終閲覧日時(UTC)
	 */
	public void setLast_access_date(String last_access_date) {
		this.last_access_date = last_access_date;
	}

	/**
	 * 閲覧期限を取得
	 * @return 閲覧期限...-1(無期限)又はyyyy-MM-dd HH:mm:ss
	 */
	public String getExpiry_date() {
		return this.expiry_date;
	}

	/**
	 * 閲覧期限([-1]の場合は無期限)をセット
	 * @param 閲覧期限(URLから取得した時間差分(分))
	 */
	public void setExpiry_date(String expiry_date) {
		this.expiry_date = expiry_date;
	}


	/**
	 * 削除フラグを取得
	 * @return 削除フラグ(サーバーから削除された場合は"TRUE"それ以外は"FALSE")
	 */
	public String getDeleteFlag(){
		return delete_flag;
	}

	/**
	 * 削除フラグをセット
	 * @param 削除フラグ(サーバーから削除された場合は"TRUE"それ以外は"FALSE")
	 */
	public void setDeleteFlag(String flag){
		this.delete_flag = flag;
	}

	/**
	 * 閲覧を許可しないプラットフォームをセット
	 * @param 閲覧を許可しないプラットフォーム
	 */
	public void setInvalidPlatform(String invalidPlatform){
		this.invalidPlatform = invalidPlatform;
	}

	/**
	 * 閲覧を許可しないプラットフォームを取得
	 * @return 閲覧を許可しないプラットフォーム
	 */
	public String getInvalidPlatform(){
		return this.invalidPlatform;
	}

	/**
	 * コンテンツのファイルサイズを取得
	 * @return コンテンツのファイルサイズ
	 */
	public String getFileSize() {
		return this.fileSize;
	}
	/**
	 * コンテンツのファイルサイズをセット
	 * @param コンテンツのファイルサイズ
	 */
	public void setFileSize(String fileSize) {

		this.fileSize = fileSize;
	}
	/**
	 * オリジナルファイル名を取得
	 * @return オリジナルファイル名
	 */
	public String getOriginalFileName() {
		return originalFileName;
	}
	/**
	 * オリジナルファイル名をセット
	 * @param オリジナルファイル名
	 */
	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}
	/**
	 * コンテンツのページ数を取得
	 * @return コンテンツのページ数
	 */
	public int getPageCount() {
		return pageCount;
	}
	/**
	 * コンテンツのページ数をセット
	 * @param コンテンツのページ数
	 */
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	/**
	 * クライアント側でのデータ最終更新日時(UTC)を取得
	 * @return クライアント側でのデータ最終更新日時(UTC)
	 */
	public String getLastModify() {
		return lastModify;
	}
	/**
	 * クライアント側でのデータ最終更新日時(UTC)をセット
	 * @param クライアント側でのデータ最終更新日時(UTC)
	 */
	public void setLastModify(String lastModify) {
		this.lastModify = lastModify;
	}
	/**
	 * コンテンツについているラベルのIDを取得
	 * @return コンテンツについているラベルのID文字配列
	 */
	public String getLabelID() {
		return labelID;
	}
	/**
	 * コンテンツについているラベルのIDをセット
	 * @param コンテンツについているラベルのID文字配列
	 */
	public void setLabelID(String labelID) {
		this.labelID = labelID;
	}
	/**
	 * コンテンツについているセールス情報ID文字配列を取得
	 * @return コンテンツについているセールス情報ID文字配列
	 */
	public String getSalesID() {
		return this.salesID;
	}
	/**
	 * コンテンツについているセールス情報ID文字列をセット<br>
	 *  ※(複数の場合はカンマ(",")区切りにすること)
	 * @param コンテンツについているセールス情報ID文字配列
	 */
	public void setSalesID(String salesID) {
		this.salesID = salesID;
	}
	/**
	 * 最大共有可能台数(-1以上))を取得...分母
	 * @return 最大共有可能台数(-1以上))
	 */
	public int getSharedDevice_D() {
		return d_sharedDevice;
	}
	/**
	 * 最大共有可能台数(-1以上))をセット...分母
	 * @param 最大共有可能台数(-1以上))
	 */
	public void setSharedDevice_D(int d_sharedDevice) {
		this.d_sharedDevice = d_sharedDevice;
	}
	/**
	 * 共有台数(残り共有可能台数(-1以上)...分子
	 * @return 共有台数(残り共有可能台数(-1以上)
	 */
	public int getSharedDevice_M() {
		return m_sharedDevice;
	}
	/**
	 * 共有台数(残り共有可能台数(-1以上)...分子
	 * @param 共有台数(残り共有可能台数(-1以上)
	 */
	public void setSharedDevice_M(int m_sharedDevice) {
		this.m_sharedDevice = m_sharedDevice;
	}
	/**
	 * 最大閲覧回数(-1以上))を取得...分母
	 * @return 最大閲覧回数(-1以上))
	 */
	public int getBrowse_D() {
		return d_browse;
	}
	/**
	 * 最大閲覧回数(-1以上))をセット...分母
	 * @param 最大閲覧回数(-1以上))
	 */
	public void setBrowse_D(int d_browse) {
		this.d_browse = d_browse;
	}
	/**
	 * 閲覧回数(残り閲覧回数(-1以上)...分子
	 * @return 閲覧回数(残り閲覧回数(-1以上)
	 */
	public int getBrowse_M() {
		return m_browse;
	}
	/**
	 * 閲覧回数(残り閲覧回数(-1以上)...分子
	 * @param 閲覧回数(残り閲覧回数(-1以上)
	 */
	public void setBrowse_M(int m_browse) {
		this.m_browse = m_browse;
	}
	/**
	 * 最大印刷回数(-1以上))を取得...分母
	 * @return 最大印刷回数(-1以上))
	 */
	public int getPrint_D() {
		return d_print;
	}
	/**
	 * 最大印刷回数(-1以上))をセット...分母
	 * @param 最大印刷回数(-1以上))
	 */
	public void setPrint_D(int d_print) {
		this.d_print = d_print;
	}
	/**
	 * 印刷回数(残り印刷回数(-1以上)...分子
	 * @return 印刷回数(残り印刷回数(-1以上)
	 */
	public int getPrint_M() {
		return m_print;
	}
	/**
	 * 印刷回数(残り印刷回数(-1以上)...分子
	 * @param 印刷回数(残り印刷回数(-1以上)
	 */
	public void setPrint_M(int m_print) {
		this.m_print = m_print;
	}
	
	/**
	 * @return dl_status
	 */
	public int getDlStatus() {
		return dl_status;
	}
	/**
	 * @param dl_status セットする dl_status
	 */
	public void setDlStatus(int dl_status) {
		this.dl_status = dl_status;
	}
	/**
	 * @return dl_progress
	 */
	public int getDlProgress() {
		return dl_progress;
	}
	/**
	 * @param dl_progress セットする dl_progress
	 */
	public void setDlProgress(int dl_progress) {
		this.dl_progress = dl_progress;
	}
	/**
	 * ListView表示の際に利用するので タイトル/著者名 を返す
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if( getTitle() != null){
			builder.append(getTitle());
		}else{
			builder.append(" - ");
		}
		if( getAuthor() != null){
			builder.append(" / ");
			builder.append(getAuthor());
		}else{
			builder.append(" - ");
		}
		return builder.toString();
	}

}
