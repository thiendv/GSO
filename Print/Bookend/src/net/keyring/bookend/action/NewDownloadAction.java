package net.keyring.bookend.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import net.keyring.bookend.Logput;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.R;
import net.keyring.bookend.SetContentsParams;
import net.keyring.bookend.bean.BookBeans;
import net.keyring.bookend.callback.CallBack;
import net.keyring.bookend.callback.NewDownloadCallback;
import net.keyring.bookend.constant.Const;
import net.keyring.bookend.constant.ConstQuery;
import net.keyring.bookend.db.ContentsDao;
import net.keyring.bookend.db.NaviSalesDao;
import net.keyring.bookend.request.AuthWebSite;
import net.keyring.bookend.request.GetContentsInfo;
import net.keyring.bookend.request.GetLicense;
import net.keyring.bookend.util.DateUtil;
import net.keyring.bookend.util.DecryptUtil;
import net.keyring.bookend.util.FileUtil;
import net.keyring.bookend.util.StringUtil;
import net.keyring.bookend.util.Utils;
import net.keyring.krpdflib.InvalidPdfException;
import net.keyring.krpdflib.PdfFile;
import net.keyring.krpdflib.UnsupportedPdfException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.os.RemoteException;

/**
 * コンテンツ新規ダウンロード - アクションクラス
 * @author Hamaji
 *
 */
public class NewDownloadAction extends CommonAction implements ConstQuery{
	/**
	 * コンストラクタ
	 * @param con Context
	 */
	public NewDownloadAction(Context con){
		this.mCon = con;
	}
	
	/** Context */
	private Context					mCon;
	/** NewDownloadService */
	private NewDownloadCallback		mNewDlService;
	
	/** 保存先コンテンツパス */
	private String mLocalContentsPath = null;
	/** 保存先サムネイルパス */
	private String mLocalThumbPath = null;
	
	// Book情報--------------------------
	/** ダウンロード日時 */
	private String mDownloadDate = null;
	/** Authkey(未使用) */
	private String mAuthKey = null;
	/** AuthkeyID(必須) */
	private String mAuthKeyID = null;
	/** ファイルタイプ … 必須 */
	private String mFileType = null;
	/** 配布者名 */
	private String mDistributorName = null;
	/** 配布者URL */
	private String mDistributorURL = null;
	/** タイトル */
	private String mTitle = null;
	/** 著者 */
	private String mAuthor = null;
	/** キーワード */
	private String mKeywords = null;
	/** ラベル名 */
	private String mTagName = null;
	/** コンテンツダウンロードURL */
	private String mBookDL_URL = null;
	/** サムネイルダウンロードURL */
	private String mThumbDL_URL = null;
	/** サイトホスト */
	private String mHost = null;
	/** ダウンロード時のID … globalDownloadIDどちらか必須 */
	private String mDownloadID = null;
	/** コンテンツID … 必須 */
	private String mContentsID = null;
	/** 閲覧可能回数 */
	private String mBrowse = null;
	/** 印刷可能回数 */
	private String mPrint = null;
	/** 有効期限(UTC) ...validTerm */
	private String mExpiryDate = null;
	/** 閲覧を許可しないプラットフォーム */
	private String mInvalidPlatform = null;
	/** 同期可能回数 */
	private String mSharedDevice = null;
	/** クライアント側でのデータ最終更新日時(UTC) … 必須 */
	private String mLastModify = null;
	/** WEBサイト誘導URL */
	private String mNaviUrl = null;
	/** WEBサイト誘導メッセージ */
	private String mNaviMessage = null;
	
	int retry_count = 0;
    int retry_interval = 0;
	
	/**
	 * フラグチェック
	 * @param queryKey 取得したいフラグのパラメータ名
	 * @return true又はfalseを返す
	 */
	public boolean getFlag(Map<String,String> queryList, String queryKey){
		try{
			String flag = queryList.get(queryKey);
			if(flag.equals("true")){
				return true;
			}
		}catch(Exception e){
		}
		return false;
	}
	
	// --------- ダウンロード前の準備 -------------
	
	/**
	 * 新規ダウンロード前セット・チェック
	 * @param queryList クエリーリスト
	 * @return ダウンロードステータス
	 */
	public boolean preparation(Map<String,String> queryList){
		if(queryList == null){
			return false;
		}
		// MapListから各々情報をセット
		setDetails(queryList);
		// filetypeがkrpdfのもののみ
		if(checkFileType(mFileType)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * クエリーリストからダウンロード情報詳細を取得
	 * @param クエリー情報が入ったリストList<NameList,valueList>
	 * @return 失敗した場合はfalseを返す
	 */
	private void setDetails(Map<String,String> queryList){
		mAuthKey = queryList.get(AUTHKEY);
		mAuthKeyID = queryList.get(AUTHKEY_ID);
		mFileType = queryList.get(CONTENTS_TYPE);
		mDistributorName = queryList.get(DISTRIBUTOR_NAME);
		mDistributorURL = queryList.get(DISTRIBUTOR_URL);
		mTitle = queryList.get(TITLE);
		mAuthor = queryList.get(AUTHOR);
		mKeywords = queryList.get(KEYWORDS);
		mTagName = queryList.get(TAG);
		mBookDL_URL = queryList.get(CONTENTS_URL);
		mThumbDL_URL = queryList.get(THUMB_URL);
		mExpiryDate = queryList.get(VALID_TERM);
		mBrowse = queryList.get(NUMBER_OF_BROWSING);
		mPrint = queryList.get(NUMBER_OF_PRINTING);
		mInvalidPlatform = queryList.get(INVALID_PLATFORM);
		if(!StringUtil.isEmpty(mInvalidPlatform)){
			mInvalidPlatform = mInvalidPlatform.trim();
		}
		mSharedDevice = queryList.get(MAX_SHARED_DEVICE);
		mHost = queryList.get(SITE_HOST);
		mDownloadID = queryList.get(DOWNLOAD_ID);
		mDownloadDate = DateUtil.getNowUTC();
		mNaviUrl = queryList.get(NAVIGATE_URL);
		mNaviMessage = queryList.get(NAVIGATE_MESSAGE);
	}
	
	/**
	 * ファイルタイプが閲覧可能なものかどうかチェック
	 * @param fileType
	 * @return 閲覧可能なファイルの場合はtrue,それ以外はfalseを返す
	 */
	public boolean checkFileType(String fileType){
		boolean check = false;
		if(!StringUtil.isEmpty(fileType)){
			if(Utils.isKrpdf(fileType) || Utils.isEPub(fileType) || Utils.isKrEPub(fileType) || Utils.isKrEPA(fileType)
					|| Utils.isBec(fileType) || Utils.isKrbec(fileType) || Utils.isPdf(fileType) 
					|| Utils.isMccomic(fileType) || Utils.isMcbook(fileType) || Utils.isMccomic(fileType) || Utils.isKrpdfx(fileType)){
				check = true;
			}else{
				Logput.d("FileType FALSE : " + fileType);
			}
		}
		return check;
	}
	
	// ----------- ダウンロード実行 -------------------
	/**
	 * ダウンロード実行,サイト認証チェック
	 * @param DLファイルフラグ(true:コンテンツ, false:サムネイル)
	 * @return コンテンツ保存パス
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * @throws ClientProtocolException 
	 */
	public String contentsDL(boolean flag) throws FileNotFoundException,ClientProtocolException, URISyntaxException, NullPointerException, IOException{
		String dl_URL = null;
		if(flag){
			// コンテンツダウンロードURL
			dl_URL = this.mBookDL_URL;
		}else{
			// サムネイルダウンロードURL
			dl_URL = this.mThumbDL_URL;
		}
		dl_URL = dl_URL.trim();
		Logput.d("Download URL = " + dl_URL);
		// コンテンツダウンロード
		if(flag){
			mLocalContentsPath = getContents(flag, dl_URL);
			return mLocalContentsPath;
		}else{
			mLocalThumbPath = getContents(flag, dl_URL);
			return mLocalThumbPath;
		}
	}
	
	/**
	 * KRPDFファイルからコンテンツIDを取得する
	 * @param krpdfFile	KRPDFファイル
	 * @return コンテンツID
	 * @throws IOException 
	 * @throws IllegalBlockSizeException 
	 * @throws BadPaddingException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws Exception
	 */
	public String getContentsID(File krpdfFile) 
			throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, 
			BadPaddingException, IllegalBlockSizeException, UnsupportedPdfException, InvalidPdfException 
	{
		String docID = "";
		String did = "";
		try{
			PdfFile pdfFile = new PdfFile();
			pdfFile.open(krpdfFile);
			byte[]xmp = pdfFile.getMetaDataStream().get_data();
			String[] fileIDs = pdfFile.getFileID();
			docID = fileIDs[0];
			did = DecryptUtil.getXmpData(new String(xmp), "krns:cid");
			return DecryptUtil.decryptXMPData(did, docID);
		}catch(IOException e){
			throw new IOException(e.getMessage());
		}catch(InvalidKeyException e){
			throw new InvalidKeyException("Invalid Key : " + did + "/" + docID, e);
		}catch(NoSuchAlgorithmException e){
			throw new NoSuchAlgorithmException("[MD5] " + e.getMessage(), e);
		}catch(NoSuchPaddingException e){
			throw new NoSuchPaddingException(e.getMessage() + " / ErrorCause:" + e.getCause());
		}catch(BadPaddingException e){
			throw new BadPaddingException(e.getMessage() + " / ErrorCause:" + e.getCause());
		}catch(IllegalBlockSizeException e){
			throw new IllegalBlockSizeException(e.getMessage() + " / ErrorCause:" + e.getCause());
		}catch(UnsupportedPdfException e){
			throw new UnsupportedPdfException(e.getMessage() + " / ErrorCause:" + e.getCause());
		}catch(InvalidPdfException e){
			throw new InvalidPdfException(e.getMessage() + " / ErrorCause:" + e.getCause());
		}
	}
	
	/**
	 * DLし、保存先パスを返す
	 * @param DLファイルフラグ(true:コンテンツ, false:サムネイル)
	 * @param dl_url ダウンロードURL
	 * @return コンテンツ保存先パス
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * @throws ClientProtocolException 
	 */
	private String getContents(boolean flag, String dl_url) throws FileNotFoundException,ClientProtocolException, URISyntaxException, NullPointerException, IOException{
		String path = null;
		
		//retry
	    retry_count = Integer.valueOf(mCon.getString(R.string.network_retry_count));
	    retry_interval = Integer.valueOf(mCon.getString(R.string.network_retry_interval));
		
	    Logput.i("INFO : retry_count = " + retry_count);
	    Logput.i("INFO : retry_interval = " + retry_interval);
	    
		HttpResponse hResp = httpGet(dl_url);
		if(hResp != null){
			path = download(flag, hResp, dl_url);
		}
		Logput.d("DL contents path = " + path);
		return path;
	}
	
	/**
	 * 指定URLにアクセスし、結果を受取る
	 * @param ダウンロードURL
	 * @return レスポンスコード
	 * @throws IOException 
	 * @throws ClientProtocolException HTTPプロトコルエラー
	 * @throws URISyntaxException URIの作成中に一部の情報が解析できなかった場合
	 */
	public HttpResponse httpGet(String dl_URL) throws FileNotFoundException,URISyntaxException,ClientProtocolException,NullPointerException,IOException{
	    HttpResponse hResp = null;
	    URI uri = null;
	    
	    int timeOutValue = Integer.valueOf(mCon.getString(R.string.network_timeout))*1000;
		Logput.i("INFO : timeOutValue = " + timeOutValue);
	    
	    for(; retry_count >= 0; retry_count--) {
	    	
	    	try {
	    		//	タイムアウトの設定
			    HttpParams httpParams = new BasicHttpParams();
				if(timeOutValue >= 0) {
			    	HttpConnectionParams.setConnectionTimeout(httpParams, timeOutValue);
			    	HttpConnectionParams.setSoTimeout(httpParams, timeOutValue);
			    }
			    HttpClient httpclient = new DefaultHttpClient(httpParams);
	    		
	    		// HttpGet オブジェクト
			    HttpGet hGet = new HttpGet();
			    uri = new URI(dl_URL);
			    hGet.setURI(uri);
			       
			    // HttpRequestの結果を受け取る
			    hResp = httpclient.execute(hGet);
			    int responseCode = hResp.getStatusLine().getStatusCode();
			    
			    // リクエストに成功した場合はループを抜ける
			    if(responseCode == HttpStatus.SC_OK) {
			    	break;
			    }
			    else {		    	
			    	Logput.i("FAIL : Response Code = " + responseCode);
			    	if(responseCode == HttpStatus.SC_NOT_FOUND){
			    		// IOExceptionにひっかかる
			    		if(retry_count <= 0) {
			    			throw new FileNotFoundException("File not found = " + dl_URL);
			    		}
			    	}
			    	hResp = null;
			    }
	    	} catch (URISyntaxException e) {
	    		e.printStackTrace();
	    		// (例外の原因となった文字列, 例外が発生した理由, 例外が発生した位置)
	    		if(retry_count <= 0) {
	    			throw new URISyntaxException(dl_URL, e.getMessage(), e.getIndex());
	    		}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				if(retry_count <= 0) {
					throw new ClientProtocolException(e.getMessage(), e.getCause());
				}
			} catch (IOException e) {
				e.printStackTrace();
				if(retry_count <= 0) {
					throw new IOException("Cause : " + e.getMessage());
				}
			} catch (NullPointerException e){
				e.printStackTrace();
				if(retry_count <= 0) {
					throw new NullPointerException("DL URI = " + uri);
				}
			} catch (IllegalStateException e){
				e.printStackTrace();
				if(retry_count <= 0) {
					throw new IllegalStateException(e.getMessage(), e.getCause());
				}
			} 
	    
	    	// 	リトライまで待つ
		    if(retry_count > 0){
	    		Logput.i("FAIL : retry = " + retry_count);
	    		try {
	    			Thread.sleep(retry_interval*1000);
	    		}catch(InterruptedException e) {
	    		}
	    	}
	    }
	    
	    return hResp;
	}
	
	/**
	 * ダウンロード処理
	 * @param レスポンスコード
	 * @param DLファイルフラグ(true:コンテンツ, false:サムネイル)
	 * @param コンテンツダウンロードURL
	 * @return コンテンツファイル保存ディレクトリパスを返す
	 */
	public String download(boolean flag, HttpResponse dl_Resp, String dlURL) throws FileNotFoundException, IOException{
		if(dlURL == null) return null;
		// ファイル名を乱数4byteのBASE16エンコードした文字列に
		String local_contentsPath = setFileName(dlURL, setDir(flag));
		if(execute_DL(dl_Resp,local_contentsPath)){
			// DL成功
			Logput.d("-- Donwload Success.");
			return local_contentsPath;
		}else{
			Logput.d("-- Download failed.");
			return null;
		}
	}
	
	/**
	 * ファイル保存ディレクトリがなければ作成
	 * @param DLファイルフラグ(true:コンテンツ, false:サムネイル)
	 * @return ディレクトリパス
	 */
	private String setDir(boolean flag){
		if(Preferences.sExternalStrage == null) Preferences.sExternalStrage = mCon.getExternalFilesDir(null).getPath();
		String contentsDir = null;
		if(flag){
			// コンテンツDLディレクトリセット
			contentsDir = Preferences.sExternalStrage + "/" + Const.CONTENTS_DIR_NAME;
		}else{
			// サムネイルDLディレクトリセット
			contentsDir = Preferences.sExternalStrage + "/" + Const.THUMBS_DIR_NAME;
		}
		try {
			FileUtil.mkdir_p(contentsDir);
			return contentsDir;
		} catch (IOException e) {
			Logput.e(e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * ファイル名を乱数4byteのBASE16エンコードした文字列に
	 * @param ファイルURL
	 * @return リネームしたファイルパス
	 */
	private String setFileName(String url,String dirPath){
		String newFilePath = dirPath + "/" + Utils.getRandomContentFileName(this.mFileType);				
		File newFile = new File(newFilePath);
		return newFile.getPath();
	}
	
	/**
	 * ダウンロード処理
	 * @param リクエストコード
	 * @param ダウンロードファイル保存パス
	 * @return ダウンロードに成功したらtrueを返す
	 */
	private boolean execute_DL(HttpResponse hResp,String path) throws FileNotFoundException, IOException{
		boolean downloadCheck = false;
		int BUFFER_SIZE = 10240;
    	File file = new File(path);
    	int fileSize = (int) hResp.getEntity().getContentLength();
    	CallBack callback_instance = CallBack.getInstance();
    	mNewDlService = callback_instance.getNewDlServiceInstance();
    	try{
	        // BufferdInputStream オブジェクトのインスタンスからデータがなくなるまで、バッファサイズ分ずつ読み込み
	    	InputStream is = hResp.getEntity().getContent();
	    	BufferedInputStream in = new BufferedInputStream(is, BUFFER_SIZE);	// ※第二引数のバッファサイズを指定しない場合 = 8K
	        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file, false), BUFFER_SIZE);
	        byte buf[] = new byte[BUFFER_SIZE];
	        int size = -1;
	        int progress = 0;
	        while((size = in.read(buf)) != -1) {
	            out.write(buf, 0, size);
	            progress = (int) (file.length() * 100 / fileSize);
	            if (progress >= 100){
	            	progress = 100;
	            }
	            mNewDlService.setProgress(progress);
	        }
	        out.flush();
	        out.close();
	        in.close();
	        downloadCheck = true;
    	}catch(FileNotFoundException e){
    		throw new FileNotFoundException(e.getMessage() + " / ErrorCause:" + e.getCause());
    	}catch(IOException e){
    		throw new FileNotFoundException(e.getMessage() + " / ErrorCause:" + e.getCause());
    	}catch(RemoteException e){
    		Logput.w("Callback error : Set DL progress.");
    	}
    	return downloadCheck;
    }
	
	// ----------- Authkey認証チェック ---------------
	
	/**
	 * 認証キーのオーナとコンテンツのオーナが一致することをチェック+IP認証
	 * @param ダウンロードしたコンテンツパス
	 * @return エラーキー
	 */
	public String authkey_check(String contentsPath){
		String fileType = this.mFileType;
		String errorMessage = null;
		try{
			File file = new File(contentsPath);
			// コンテンツIDを取得
			if(Utils.isKrpdf(fileType) || Utils.isKrpdfx(fileType)){
				// krpdf
				mContentsID = getContentsID(file);
			}else if(Utils.isEPub(fileType) || Utils.isBec(fileType) || Utils.isPdf(fileType) || 
					Utils.isMccomic(fileType) || Utils.isMcbook(fileType) || Utils.isMcm(fileType)){
				// epub or bec or pdf
				mContentsID = getContentsID_PLAIN(file);
			}else if(Utils.isKrEPub(fileType) || Utils.isKrbec(fileType) || Utils.isKrEPA(fileType)){
				// krepub or krbec
				mContentsID = getContentsID_KREPUB(file);
			}
			
			if(StringUtil.isEmpty(mAuthKeyID) && StringUtil.isEmpty(mAuthKey)){
				errorMessage = mCon.getString(R.string.authkey_error_key);
				Logput.w("AuthKeyID = " + mAuthKeyID + " / AuthKey = " + mAuthKey);
			}else if(mHost == null){
				errorMessage = mCon.getString(R.string.authkey_error_host);
				Logput.w("Host is null.");
			}else if(mContentsID == null){
				errorMessage = mCon.getString(R.string.authkey_error_contentsid);
				Logput.w("ContentsID is null.");
			}else{
				// AuthWebSiteリクエスト
				AuthWebSite authWebSite = new AuthWebSite();
				if(!authWebSite.execute(mCon, mAuthKey, mAuthKeyID, mHost, mContentsID)){
					errorMessage = authWebSite.getDescription();
				}
			}
		}catch(NullPointerException e){
			throw new NullPointerException("Contents path is null.");
		}catch(Exception e){
			// エラー...DLしたコンテンツを削除
			FileUtil.deleteFile(new File(contentsPath));
			Logput.e(e.getMessage(), e);
		}
		
		if(errorMessage != null){
			// サイト認証:NG...DLしたコンテンツを削除
			FileUtil.deleteFile(new File(contentsPath));
		}
		return errorMessage;
	}
	
	// ----------------- 閲覧有効期限計算 -------------------
	
	/**
	 * 閲覧有効期限が設定されている場合は期限時刻を計算
	 * @return 問題なければtrue,それ以外はfalseを返す
	 */
	public boolean setExpiryDate(){
		boolean check = true;
		if(!StringUtil.isEmpty(mExpiryDate)){
			// 閲覧有効期限が設定されている場合は期限時刻を計算
			if(!mExpiryDate.equals("-1")){
				// 閲覧期限計算
				try{
					long expiryDate = Long.parseLong(mExpiryDate);
					Date now = DateUtil.toDate(mDownloadDate, "UTC");
					Date expiry_date = new Date(now.getTime() + ((expiryDate * 60) * 1000));
					// タイムゾーンをUTCに変更
					mExpiryDate = DateUtil.toDbString(expiry_date, "UTC");
					Logput.v("set LicenseExpiry = " + mExpiryDate);
				}catch(NumberFormatException e){
					check = false;
					e.printStackTrace();
					Logput.e(e.getMessage() + " ... LicenseExpiry set : Fail", e);
				}
			}
		}
		return check;
	}
	
	// -------------- DBにセット -----------------
	
	/**
	 * DBに追加(contents情報+Web誘導情報)
	 * @return Contents
	 */
	public BookBeans setDB(){
		// DBに追加
		int sharedDevice_d = -1;
		int browse_d = -1;
		int print_d = -1;

		try{
			// MaxSharedDeviceを数値に
			if(!StringUtil.isEmpty(mSharedDevice)){
				if(Utils.isNum(mSharedDevice)){
					sharedDevice_d = Utils.changeNum(mSharedDevice);
				}else{
					return null;
				}
			}else{
				// 省略時は2
				sharedDevice_d = 2;
			}
			if(!StringUtil.isEmpty(mBrowse)){
				if(Utils.isNum(mBrowse)){
					browse_d = Utils.changeNum(mBrowse);
				}else{
					return null;
				}
			}else{
				browse_d = -1;
			}
			if(!StringUtil.isEmpty(mPrint)){
				if(Utils.isNum(mPrint)){
					print_d = Utils.changeNum(mPrint);
				}else{
					return null;
				}
			}else{
				print_d = -1;
			}
		}catch(NumberFormatException e){
			Logput.e(e.getMessage(), e);
			return null;
		}catch (Exception e) {
			Logput.e(e.getMessage(), e);
		}

		ContentsDao dao = new ContentsDao(mCon);
		BookBeans book = dao.setBook(mDownloadID, mContentsID, mFileType, mTitle, mAuthor, mKeywords, mDistributorName,
				mDistributorURL, mLocalContentsPath, mLocalThumbPath, mBookDL_URL, mThumbDL_URL,
				mDownloadDate, mExpiryDate, mInvalidPlatform, sharedDevice_d, browse_d, print_d);
		book = dao.save(book);
		
		// WEBサイト誘導メッセージが設定されている場合はDBに保存
		if(!StringUtil.isEmpty(mNaviMessage)){
			setNaviSalesDB(mDownloadID);
		}
		return book;
	}
	
	/**
	 * Webサイト誘導情報をDBに登録
	 * @param contentsRowID コンテンツテーブルID
	 */
	private void setNaviSalesDB(String downloadID){
		NaviSalesDao dao = new NaviSalesDao(mCon);
		dao.save(-1, downloadID, mNaviUrl, mNaviMessage);
	}
	
	// ------------- ePub ----------------
	
		/**
		 * KREPUBファイル、KRBECファイルからコンテンツIDを取得する
		 * @param krpdfFile
		 * @return
		 * @throws Exception
		 */
		public static String getContentsID_KREPUB(File file) throws FileNotFoundException,IOException{
			byte[] xmlData = extractFileData(file.getPath(), "bookend.xml", 4096);
			return getXmlNodeValue(new String(xmlData), "ContentsID");
		}
		
		/**
		 * 非暗号化コンテンツのコンテンツIDを取得する
		 * @param file
		 * @return
		 * @throws NoSuchAlgorithmException 
		 * @throws IOException 
		 */
		public static String getContentsID_PLAIN(File file) throws NoSuchAlgorithmException, IOException {
			try{
				//	ファイル全体のMD5ハッシュを生成する
				int buffSize = 4096;
				MessageDigest md5 = MessageDigest.getInstance("MD5");
				FileInputStream srcFileStream = new FileInputStream(file.getPath());
				while(srcFileStream.available() > 0) {
					int readSize = srcFileStream.available() > buffSize ? buffSize : srcFileStream.available();
					byte[] buf = new byte[readSize];
					srcFileStream.read(buf, 0, readSize);
					md5.update(buf);
				}
				byte[] hash = md5.digest();
				//	Base16エンコードして返す
				return DecryptUtil.base16enc(hash);
			} catch (NoSuchAlgorithmException e){
				throw new NoSuchAlgorithmException("[MD5] " + e.getMessage(), e);
			} catch (IOException e){
				throw new IOException(e.getMessage() + " / ErrorCause:" + e.getCause());
			}
		}
		
		/**
		 * XMLデータから指定したノードの値を取得する
		 * 単純な文字列検索で処理しています。
		 * @param xml	XML文字列
		 * @param nodeName	対象ノード名
		 * @return	対象ノードが存在する場合はその中身、存在しない場合は""
		 */
		public static String getXmlNodeValue(String xml, String nodeName) {
	        String startTag = "<" + nodeName + ">";
	        String endTag = "</" + nodeName + ">";
	        int startIdx = xml.indexOf(startTag);
	        int endIdx = xml.indexOf(endTag);
	        if (startIdx == -1 || endIdx == -1 || startIdx >= endIdx)
	            return "";
	        startIdx += startTag.length();
	        return xml.substring(startIdx, endIdx);
	    }
		
		/**
		 * zipから指定したファイルのデータを取り出す
		 * @param srcFilePath
		 * @param targetEnctryName
		 * @throws FileNotFoundException
		 * @throws IOException
		 */
		public static byte[] extractFileData(String srcFilePath, String targetEnctryName, int buffSize) 
				throws FileNotFoundException, IOException
		{
	        ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(srcFilePath)));            
	        ZipEntry zipEntry = null;      //ZIPファイルから取り出したエントリを格納する変数
	        //ZIPファイルを出力する
	        while( (zipEntry = zipInputStream.getNextEntry()) != null )
	        {
	        	System.out.println(zipEntry.getName());            
	        	//	Zipエントリがディレクトリならディレクトリを生成
	        	//	ファイルならファイルを出力
	        	if(zipEntry.isDirectory() == false && 
	        		targetEnctryName.compareTo(zipEntry.getName()) == 0) {
	        		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		        	copyStream(zipInputStream, outstream, buffSize);
		        	return outstream.toByteArray();
	        	}
	        	//現在のZIPエントリを閉じる
	        	zipInputStream.closeEntry();       	
	        }
	        
	        throw new FileNotFoundException(targetEnctryName + " is not found.");
		}
		
	    /**
		 * InputStream のデータを全て OutputStream に書き込む
		 * @param inputStream
		 * @param outputStream
		 * @throws IOException
		 */
		public static void copyStream(InputStream inputStream, OutputStream outputStream, int buffSize)
			throws IOException
		{
			byte buf[] = new byte[buffSize];
			int count;
			while ((count = inputStream.read(buf, 0, buffSize)) != -1) {
				outputStream.write(buf, 0, count);
			}
		}
		
		// ------------- ePub /----------------
	
	// ----------- SetContents -------------
	
	/**
	 * SetContentsリクエストのパラムをセット
	 * @return パラムMAP
	 */
	public ArrayList<NameValuePair> setParams(){
		String labelLastModify = mDownloadDate;
		mLastModify = mDownloadDate;
		SetContentsParams setcontents = new SetContentsParams(mCon);
		// TODO パラメータ後で見直すこと Androidはラベルがない
		ArrayList<NameValuePair> params = setcontents.setContentsParams(mDownloadID, mContentsID, mFileType, mDownloadDate,
				mLastModify, true, false, true, mTagName, labelLastModify,
				mSharedDevice, mBrowse, mPrint, mExpiryDate, mInvalidPlatform,
				mTitle, mAuthor, mKeywords, mDistributorName, mDistributorURL, mNaviUrl, mNaviMessage);
		return params;
	}
	
	// ----------- GetContentsInfo -------------
	public GetContentsInfo getContentsInfo(long rowID, String contentsID){
		GetContentsInfo getContentsInfo = new GetContentsInfo();
		getContentsInfo.execute(mCon, rowID, contentsID);
		return getContentsInfo;
	}
	
	
	// ----------- GetLicense -------------
	/**
	 * ViewTask,RemoveLayerServiceに行かない場合はここでGetLicenseする必要がある
	 * @return Book コンテンツ情報
	 */
	public BookBeans getLicense(String downloadID){
		GetLicense getLicense = new GetLicense();
		return getLicense.execute(mCon, downloadID);
	}
	
	// EPubViewerバージョンコードチェック
	/**
	 * EPubViewerのアップデートが必要かどうかチェック
	 * @param versionCode 現在インストールされているEPubViewerのバージョンコード
	 * @return アップデートが必要な場合はtrue、それ以外はfalseを返す
	 */
	public boolean CheckEPubViewerVer(int versionCode){
		if(versionCode <=1 ){
			return true;
		}else{
			return false;
		}
	}
}