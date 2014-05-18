package net.keyring.bookend.request;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;

import net.keyring.bookend.Logput;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.R;
import net.keyring.bookend.bean.BookBeans;
import net.keyring.bookend.constant.ConstReq;
import net.keyring.bookend.db.ContentsDao;
import net.keyring.bookend.util.StringUtil;
import net.keyring.bookend.util.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
/**
 * GetContentsInfoリクエスト処理
 * @author Hamaji
 *
 */
public class GetContentsInfo implements ConstReq {
	
	private Context		mCon = null;
	
	/** statusコード */
	private String		mStatus = null;
	/** status詳細 */
	private String		mDescription = null;
	/** コンテンツタイトル */
	private String		mTitle = null;
	/** 著者名 */
	private String		mAuthor = null;
	/** ファイルタイプ */
	private String		mFileType = null;
	/** "true"=本番, "false"=テスト用 コンテンツ  */
	private String		mProduct = null;	//  ※現在未使用
	/** キーワード */
	private String		mKeywords = null;
	/** 配布者名 */
	private String		mDistributorName = null;
	/** 配布者URL */
	private String		mDistributorURL = null;
	/** ファイルサイズ */
	private String		mFileSize = null;
	/** オリジナルファイル名 */
	private String		mOriginalFileName = null;
	/** ページ数 */
	private String		mPageCount = null;
	/** crc32 */
	private String		mCrc32 = null;
	/** コンテンツダウンロードURL */
	private String		mContentsURL = null;
	/** サムネイルダウンロードURL */
	private String		mThumbURL = null;
	/** セールスID */
	private String		mSalesID = null;
	
	public BookBeans	mBook = null;
	
	/**
	 * BooksBeansを返す
	 * @return
	 */
	public BookBeans getBookBeans(){
		return this.mBook;
	}
	/**
	 * GetContentsInfoレスポンスステータスを返す
	 * @return
	 */
	public String getStatus(){
		return this.mStatus;
	}
	
	/**
	 * GetContentsInfoリクエスト実行
	 * @param con Context
	 * @param rowID
	 * @param contentsID コンテンツID(必須)
	 */
	public void execute(Context con, long rowID, String contentsID){
		this.mCon = con;
		// パラメータセット
		ArrayList<NameValuePair> params = setParams(contentsID);
		try {
			// GetContentsInfoリクエスト
			XmlPullParser parser = request(params);
			if(parser != null){
				// 受け取った情報を変数にセット
				getResDetail(parser);
				if(checkStatus(mStatus, contentsID)){
					// 正常終了 - DBに登録
					mBook = saveDB(rowID, contentsID);
				}else{
					Logput.w("GetContentsInfo : ERROR <" + this.mStatus + ">");
				}
			}
		} catch (NumberFormatException e){
			mDescription = con.getString(R.string.status_null, GET_CONTENTS_INFO);
			Logput.w(mDescription);
		} catch (NullPointerException e){
			mDescription = con.getString(R.string.status_null, GET_CONTENTS_INFO);
			Logput.w(mDescription);
		} catch (IOException e) {
			Logput.e(e.getMessage(), e);
		} catch (XmlPullParserException e) {
			Logput.e(e.getMessage(), e);
		}
		Logput.v("---------------------------------");
	}
	
	/**
	 * GetContentsInfoリクエストを行い、受け取った値を返す
	 * @param params パラメータ
	 * @return XmlPullParser
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 */
	private XmlPullParser request(ArrayList<NameValuePair> params) throws IOException, XmlPullParserException{
		XmlPullParser parser = null;
		RequestServer req = new RequestServer(params, GET_CONTENTS_INFO, mCon);
		parser = req.execute();
		return parser;
	}
	
	/**
	 * GetContentsInfoリクエストに必要なパラメータセット
	 * @param con Context
	 * @param contentsID コンテンツID
	 * @return リクエストパラメータリスト
	 */
	private ArrayList<NameValuePair> setParams(String contentsID){
		ArrayList<NameValuePair> params = null;
		Preferences pref = new Preferences(mCon);
		String accessCode = pref.getAccessCode();
		String libraryID = pref.getLibraryID();
		// パラメータ全て必須
		if(!StringUtil.isEmpty(contentsID) && !StringUtil.isEmpty(accessCode) && !StringUtil.isEmpty(libraryID)){
			params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(LIBRARY_ID, libraryID));
			params.add(new BasicNameValuePair(ACCESSCODE, accessCode));
			params.add(new BasicNameValuePair(CONTENTS_ID, contentsID));
		}
		return params;
	}
	
	/**
	 * 帰ってきたxmlの最初のタグ名から、response解析振り分け(xml階層１段のもののみ)
	 * @return レスポンスリスト
	 * @throws IOException 
	 * @throws XmlPullParserException 
	 */
	private void getResDetail(XmlPullParser parser) throws XmlPullParserException, IOException{
		if(parser != null){
			int eventType = parser.getEventType();
			parser.next();
			String xmlName = parser.getName();
			Logput.v("--- <" + xmlName + "> ---");
			// xmlを読み終わるまで回す
			while(eventType != XmlPullParser.END_DOCUMENT){
				if(eventType == XmlPullParser.START_TAG){
					String tagName = parser.getName();
					if(!StringUtil.isEmpty(tagName)){
						eventType = setDetail(parser, tagName);
					}else{
						eventType = parser.next();
					}
				}else{
					eventType = parser.next();
				}
			}
		}
	}
	
	/**
	 * 受け取ったレスポンスxmlを解析して変数にセット
	 * @param xmlの内容をList化したもの
	 * @throws IOException 
	 * @throws XmlPullParserException 
	 */
	private int setDetail(XmlPullParser parser, String tagName) throws XmlPullParserException, IOException{
		int eventType = parser.next();
		if(eventType == XmlPullParser.TEXT){
			String value = parser.getText();
			value = URLDecoder.decode(value);
			if(!StringUtil.isEmpty(value)){
				StringUtil.putResponce(tagName, value);
				if(tagName.equals(STATUS)){
					mStatus = value;
				}else if(tagName.equals(TITLE)){
					if(StringUtil.isEmpty(this.mTitle)){
						// salesの<title>を取得しないように
						this.mTitle = value;
					}
				}else if(tagName.equals(AUTHOR)){
					this.mAuthor = value;
				}else if(tagName.equals(FILE_TYPE)){
					this.mFileType = value;
				}else if(tagName.equals(PRODUCT)){
					this.mProduct = value;
				}else if(tagName.equals(KEYWORDS)){
					this.mKeywords = value;
				}else if(tagName.equals(DISTRIBUTOR_NAME)){
					this.mDistributorName = value;
				}else if(tagName.equals(DISTRIBUTOR_URL)){
					this.mDistributorURL = value;
				}else if(tagName.equals(FILE_SIZE)){
					this.mFileSize = value;
				}else if(tagName.equals(ORIGINAL_FILE_NAME)){
					this.mOriginalFileName = value;
				}else if(tagName.equals(PAGE_COUNT)){
					this.mPageCount = value;
				}else if(tagName.equals(CRC32)){
					this.mCrc32 = value;
				}else if(tagName.equals(CONTENTS_URL)){
					this.mContentsURL = value;
				}else if(tagName.equals(THUMB_URL)){
					this.mThumbURL = value;
				}else if(tagName.equals(SALES_ID)){
					if(!StringUtil.isEmpty(this.mSalesID)){
						this.mSalesID = this.mSalesID + "," + value;
					}else{
						this.mSalesID = value;
					}
				}
				eventType = parser.next();
			}
		}
		return eventType;
	}
	
	/**
	 * GetContentsInfoで受け取った値で更新
	 * @param long rowID
	 * @param contentsID コンテンツID
	 */
	private BookBeans saveDB(long rowID, String contentsID){
		BookBeans book = null;
		int page = 0;
		if(!StringUtil.isEmpty(mPageCount)){
			page = Utils.changeNum(mPageCount);
		}
		ContentsDao dao = new ContentsDao(mCon);
		book = dao.setBook(rowID, mFileType, mTitle, mAuthor, mKeywords, mDistributorName, mDistributorURL,
				mFileSize, mOriginalFileName, page, mContentsURL, mThumbURL, mCrc32, mSalesID);
		if(book != null){
			book = dao.save(book);
		}
		
		if(book == null){
			Logput.i("UPDATE DB : FAIL ... GetContentsInfo");
		}else{
			Logput.v("UPDATE DB : SUCCESS ... GetContentsInfo");
		}
		return book;
	}
	
	/**
	 * GetContentsInfo - エラーメッセージを返す
	 * @return description エラーメッセージ
	 */
	public String getErrorMessage(){
		return this.mDescription;
	}
	
	private boolean checkStatus(String status, String contentsID) throws NumberFormatException, NullPointerException{
		switch(Utils.changeNum(status)){
		case 11100:
			return true;
		case 11110:
			mDescription = mCon.getString(R.string.status_parameter_error, status);
			break;
		case 11111:
			mDescription = mCon.getString(R.string.library_not_registered, status);
			break;
		case 11112:
			mDescription = mCon.getString(R.string.getcontentsinfo_11112);
			break;
		case 11113:
			mDescription = mCon.getString(R.string.getcontentsinfo_11113);
			Logput.w(mCon.getString(R.string.getcontentsinfo_11113) + " / contentsID = " + contentsID);
			break;
		case 11199:
			mDescription = mCon.getString(R.string.status_server_internal_error, status);
			break;
		default:
			mDescription = mCon.getString(R.string.status_false, GET_CONTENTS_INFO, status);
			break;
		}
		return false;
	}
}
