package net.keyring.bookend;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;

import net.keyring.bookend.constant.Const;
import net.keyring.bookend.constant.ConstReq;
import net.keyring.bookend.util.DecryptUtil;
import net.keyring.bookend.util.StringUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

/**
 * SetContents リクエストに必要なパラメータ setter
 * @author Hamaji
 *
 */
public class SetContentsParams implements ConstReq{
	public SetContentsParams(Context context){
		//Utils.debug(">> SetContentsParams");
		mPref = new Preferences(context);
		mLibraryID = mPref.getLibraryID();
		mAccessCode = mPref.getAccessCode();
		mUserID = mPref.getUserID();
	}

	/** Preferencesクラス */
	private Preferences mPref;
	/** ライブラリID */
	private String mLibraryID;
	/** アクセスコード */
	private String mAccessCode;
	/** ユーザーID */
	private String mUserID;

	/**
	 * SetContentsのパラメータをチェックしてセット<br>
	 * ※省略の場合はnull or false/trueをセットする<br>
	 *
	 * @param dataCount 送信するデータ個数 … 必須
	 * @param downloadID ダウンロード時のID … 必須
	 * @param contentsID コンテンツID … 必須
	 * @param fileType ファイルタイプ … 必須
	 * @param downloadDate ダウンロード日時(UTC) … 必須
	 * @param lastAccessDate 最終閲覧日時(UTC)
	 * @param lastModify クライアント側でのデータ最終更新日時(UTC) … 必須
	 * @param donwloaded ダウンロード済みかを示すフラグ(省略時:false)
	 * @param delete コンテンツが削除されたかどうか(省略時:false)
	 * @param licenseExist ライセンスがパラメータに含まれているかどうか(省略時:true)
	 * @param labelID ラベルID
	 * @param labelName ラベル名(ラベルIDがセットされているときは必須)
	 * @param labelLastModify クライアント側でのラベル最終更新日時 yyyy-MM-dd HH:mm:ss(UTC) (ラベルIDがセットされているときは必須)
	 * @param licenseMaxSharedDevice 同期可能回数(-1の場合は無制限,省略時は2)
	 * @param licenseMaxBrowse 閲覧可能回数(省略時は-1…無制限)
	 * @param licenseMaxPrint 印刷可能回数(省略時は-1…無制限)
	 * @param licenseExpiry 有効期限(UTC)
	 * @param licenseInvalidPlatform 閲覧を許可しないプラットフォーム
	 * @param title タイトル(UTF8)
	 * @param author 著者(UTF8)
	 * @param keywords キーワード(UTF8)
	 * @param distributorName コンテンツ配布者(UTF8)
	 * @param distributorURL コンテンツ配布者情報を表示するURL
	 * @param naviUrl Webサイト誘導URL
	 * @param naviMessage Webサイト誘導メッセージ
	 *
	 * @return パラメータリスト
	 */
	public ArrayList<NameValuePair> setContentsParams(String downloadID,String contentsID,String fileType,String downloadDate,
			String lastModify,boolean donwloaded, boolean delete,boolean licenseExist,
			String labelName,String labelLastModify,String licenseMaxSharedDevice,String licenseMaxBrowse,
			String licenseMaxPrint,String licenseExpiry,String licenseInvalidPlatform,String title,String author,
			String keywords,String distributorName,String distributorURL,String naviUrl,String naviMessage){

		// 新規ダウンロードは１つづつ
		int dataCount = 1;
		// パラメータリスト
		ArrayList<NameValuePair> params;

		// 必須パラメータにNullがないかチェック
		String type = fileType(fileType);
		if(check_param(downloadID, contentsID, type, downloadDate, lastModify)){
			params = new ArrayList<NameValuePair>();
			
			params.add(new BasicNameValuePair(DATA_COUNT, String.valueOf(dataCount)));
			params.add(new BasicNameValuePair(LIBRARY_ID , mLibraryID));
			params.add(new BasicNameValuePair(ACCESSCODE, mAccessCode));
			params.add(new BasicNameValuePair(USER_ID, mUserID));
			params.add(new BasicNameValuePair(DOWNLOAD_ID + dataCount, downloadID));
			params.add(new BasicNameValuePair(CONTENTS_ID + dataCount, contentsID));
			params.add(new BasicNameValuePair(FILE_TYPE + dataCount, type));
			params.add(new BasicNameValuePair(DOWNLOAD_DATE + dataCount, downloadDate));
			params.add(new BasicNameValuePair(LAST_MODIFY + dataCount, lastModify));
			params.add(new BasicNameValuePair(DOWNLOADED + dataCount, checkDownloaded(donwloaded)));
			params.add(new BasicNameValuePair(DELETE + dataCount, checkDelete(delete)));
			params.add(new BasicNameValuePair(LICENSE_EXIST + dataCount, checkLicenseExist(licenseExist)));
			
			// LabelID(labelName,labelLastModify - ラベルIDがセットされている場合は必須)
			if(!StringUtil.isEmpty(labelName)){
				if(StringUtil.isEmpty(labelLastModify)){
					return null;
				}else{
					// タグがセットされている場合は乱数8バイトを生成して、乱数をBase16エンコードした文字列をLabelIDとする
					byte[] random = DecryptUtil.genRandomData(8);
					String labelID = DecryptUtil.base16enc(random);
					params.add(new BasicNameValuePair(LABEL_ID + dataCount, labelID));
					params.add(new BasicNameValuePair(LABEL_NAME + dataCount, labelName));
					params.add(new BasicNameValuePair(LABEL_LAST_MODIFY + dataCount, labelLastModify));
				}
			}
			if(!StringUtil.isEmpty(licenseMaxSharedDevice, true)){
				params.add(new BasicNameValuePair(LICENSE_MAX_SHARED_DEVICE + dataCount, licenseMaxSharedDevice));
			}
			if(!StringUtil.isEmpty(licenseMaxBrowse, true)){
				params.add(new BasicNameValuePair(LICENSE_MAX_BROWSE + dataCount, licenseMaxBrowse));
			}
			if(!StringUtil.isEmpty(licenseMaxPrint, true)){
				params.add(new BasicNameValuePair(LICENSE_MAX_PRINT + dataCount,licenseMaxPrint));
			}
			if(!StringUtil.isEmpty(licenseExpiry, true)){
				if(!licenseExpiry.equals("-1")) params.add(new BasicNameValuePair(LICENSE_EXPIRY + dataCount,licenseExpiry));
			}
			if(!StringUtil.isEmpty(licenseInvalidPlatform, true)){
				params.add(new BasicNameValuePair(LICENSE_INVALID_PLATFORM + dataCount,checkLicenseInvalidPlatform(licenseInvalidPlatform)));
			}
			if(!StringUtil.isEmpty(title, true)){
				params.add(new BasicNameValuePair(TITLE + dataCount,checkTitle(title)));
			}
			if(!StringUtil.isEmpty(author, true)){
				params.add(new BasicNameValuePair(AUTHOR + dataCount,checkAuthor(author)));
			}
			if(!StringUtil.isEmpty(keywords, true)){
				params.add(new BasicNameValuePair(KEYWORDS + dataCount,checkKeywords(keywords)));
			}
			if(!StringUtil.isEmpty(distributorName, true)){
				params.add(new BasicNameValuePair(DISTRIBUTOR_NAME + dataCount,checkDistributorName(distributorName)));
			}
			if(!StringUtil.isEmpty(distributorURL, true)){
				params.add(new BasicNameValuePair(DISTRIBUTOR_Url + dataCount,checkDistributorURL(distributorURL)));
			}
			if(!StringUtil.isEmpty(naviUrl)){
				params.add(new BasicNameValuePair(NAVIGATE_URL + dataCount,naviUrl));
			}
			if(!StringUtil.isEmpty(naviMessage)){
				params.add(new BasicNameValuePair(NAVIGATE_MESSAGE + dataCount,naviMessage));
			}
			// Listにセット
			//Utils.debug("SetContents params >> " + params.toString());
			return params;
		}else{
			return null;
		}
	}

	/**
	 * 必須パラメータにNULLがないかチェック
	 * @return 問題なければtrue、それ以外はfalseを返す
	 */
	private boolean check_param(String downloadID, String contentsID, String fileType,
			String downloadDate, String lastModify){
		if(StringUtil.isEmpty(mLibraryID, true)){
			Logput.i("SetContents param LibraryID is NULL.");
			return false;
		}else if(StringUtil.isEmpty(mAccessCode, true)){
			Logput.i("SetContents param AccessCode is NULL.");
			return false;
		}else if(StringUtil.isEmpty(mUserID, true)){
			Logput.i("SetContents param UserID is NULL.");
			return false;
		}else if(StringUtil.isEmpty(downloadID, true)){
			Logput.i("SetContents param DownloadID is NULL.");
			return false;
		}else if(StringUtil.isEmpty(contentsID, true)){
			Logput.i("SetContents param ContentsID is NULL.");
			return false;
		}else if(StringUtil.isEmpty(fileType, true)){
			Logput.i("SetContents param FileType is NULL.");
			return false;
		}else if(StringUtil.isEmpty(downloadDate, true)){
			Logput.i("SetContents param DonwloadDate is NULL.");
			return false;
		}else if(StringUtil.isEmpty(lastModify, true)){
			Logput.i("SetContents param LastModify is NULL.");
			return false;
		}else{
			return true;
		}
	}

	/**
	 * コンテンツタイプをチェックして返す - 必須
	 * @param コンテンツタイプ名
	 */
	private String fileType(String fileType) {
		String _fileType = "-1";
		if(fileType.equals("pdf")) _fileType = "1";
		else if(fileType.equals("krpdf")) _fileType = "2";
		else if(fileType.equals("xmdf")) _fileType = "3";
		else if(fileType.equals("book")) _fileType = "4";
		else if(fileType.equals("krm")) _fileType = "5";
		else if(fileType.equals("epub")) _fileType = "6";
		else if(fileType.equals("krepub")) _fileType = "7";
		else if(fileType.equals("bec")) _fileType = "8";
		else if(fileType.equals("krbec")) _fileType = "9";
		else if(fileType.equals("krpdfx")) _fileType = "10";
		else if(fileType.equals("mcm")) _fileType = "11";
		else if(fileType.equals("epubx")) _fileType = "12";
		else if(fileType.equals("epubxf")) _fileType = "13";
		else if(fileType.equals("krepa")) _fileType = "14";
		else Logput.i("FileType false = " + fileType);
		return _fileType;
	}

	/**
	 * ダウンロード済かどうかを示すフラグを文字列にして返す(ダウンロード直後だから全てtrueのはず)
	 * @param クライアントにダウンロード済の場合は"true",それ以外は"false"…省略時は"false"
	 */
	private String checkDownloaded(boolean downloaded) {
		String _downloaded = Const.FALSE;
		if(downloaded) _downloaded = Const.TRUE;
		return _downloaded;
	}

	/**
	 * コンテンツが削除されているかどうかを示すフラグをチェック
	 * @param コンテンツが削除されている場合は"true",それ以外は"false" … 省略時は"false"
	 */
	private String checkDelete(boolean delete) {
		String _delete = Const.FALSE;
		if(delete) _delete = Const.TRUE;
		return _delete;
	}
	/**
	 * ライセンスがパラメータに含まれているかどうかを示すフラグをチェック
	 * @return ライセンスがパラメータに含まれている場合は"true",それ以外は"false" … 省略時は"true"
	 */
	private String checkLicenseExist(boolean licenseExist) {
		String _licenseExist = Const.TRUE;
		if(!licenseExist) _licenseExist = Const.FALSE;
		return _licenseExist;
	}

	/**
	 * 許可しないプラットフォームをチェック
	 * @param 許可しないプラットフォーム(","区切りで複数指定も可)
	 * @return 許可しないプラットフォーム … 省略時は"none"
	 */
	private String checkLicenseInvalidPlatform(String licenseInvalidPlatform) {
		// NULLの場合は”none”を代入
		if(StringUtil.isEmpty(licenseInvalidPlatform, true)) licenseInvalidPlatform = "none";
		return licenseInvalidPlatform;
	}
	/**
	 * タイトルをセット(UTF8)
	 * @param タイトル(UTF8,128byte以下の文字列…それ以上の場合は切り捨て)
	 */
	private String checkTitle(String title) {
		int titleLength = -1;
		try {
			titleLength = title.getBytes("UTF-8").length;
			// 128byte以下の文字列ならそのままセット
			if(titleLength <= 128) return title;
			else{
				// 128byte以上の文字列は切り捨ててからセット
				title = StringUtil.trunc(title,128,"UTF-8");
				Logput.v("title = " + title);
				Logput.v("title byte = " + title.getBytes("UTF-8").length);
				return title;
			}
		} catch (UnsupportedEncodingException e) {
			Logput.e(e.getMessage(), e);
			return null;
		} catch (UnsupportedCharsetException e) {
			Logput.e(e.getMessage(), e);
			return null;
		} catch (CharacterCodingException e) {
			Logput.e(e.getMessage(), e);
			return null;
		}
	}
	/**
	 * 著者をチェック(UTF8,128byte以下の文字列… それ以上の場合は切り捨て)
	 * @param 著者
	 * @return 著者(UTF8,128byte以下)
	 */
	private String checkAuthor(String author) {
		int authorLength = -1;
		try {
			authorLength = author.getBytes("UTF-8").length;
			// 128byte以下の文字列ならそのままセット
			if(authorLength <= 128) return author;
			else{
				// 128byte以上の文字列は切り捨ててからセット
				author = StringUtil.trunc(author,128,"UTF-8");
				Logput.v("author = " + author);
				Logput.v("author byte = " + author.getBytes("UTF-8").length);
				return author;
			}
		} catch (UnsupportedEncodingException e) {
			Logput.e(e.getMessage(), e);
			return null;
		} catch (UnsupportedCharsetException e) {
			Logput.e(e.getMessage(), e);
			return null;
		} catch (CharacterCodingException e) {
			Logput.e(e.getMessage(), e);
			return null;
		}
	}
	/**
	 * キーワード(UTF8)をチェックする(255byte以下の文字列 … それ以上は切り捨て)
	 * @param キーワード
	 * @return キーワード(UTF8)
	 */
	private String checkKeywords(String keywords) {
		int keywordsLength = -1;
		try {
			keywordsLength = keywords.getBytes("UTF-8").length;
			// UTF8,255byte以下の文字列ならそのままセット
			if(keywordsLength <= 255) return keywords;
			else{
				// UTF8,255byte以上の文字列は切り捨ててからセット
				keywords = StringUtil.trunc(keywords,255,"UTF-8");
				Logput.v("keywords = " + keywords);
				Logput.v("keywords byte = " + keywords.getBytes("UTF-8").length);
				return keywords;
			}
		} catch (UnsupportedEncodingException e) {
			Logput.e(e.getMessage(), e);
			return null;
		} catch (UnsupportedCharsetException e) {
			Logput.e(e.getMessage(), e);
			return null;
		} catch (CharacterCodingException e) {
			Logput.e(e.getMessage(), e);
			return null;
		}
	}
	/**
	 * コンテンツ配布者(UTF8)をチェック(128byte以下の文字列 … それ以上の場合は切り捨て)
	 * @param コンテンツ配布者
	 * @return コンテンツ配布者(UTF8)
	 */
	private String checkDistributorName(String distributorName) {
		int distributorNameLength = -1;
		try {
			distributorNameLength = distributorName.getBytes("UTF-8").length;
			// 128byte以下の文字列ならそのままセット
			if(distributorNameLength <= 128) return distributorName;
			else{
				// 128byte以上の文字列は切り捨ててからセット
				distributorName = StringUtil.trunc(distributorName,255,"UTF-8");
				Logput.v("distributorName = " + distributorName);
				Logput.v("distributorName byte = " + distributorName.getBytes("UTF-8").length);
				return distributorName;
			}
		} catch (UnsupportedEncodingException e) {
			Logput.e(e.getMessage(), e);
			return null;
		} catch (UnsupportedCharsetException e) {
			Logput.e(e.getMessage(), e);
			return null;
		} catch (CharacterCodingException e) {
			Logput.e(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * コンテンツ配布者の情報を表示するURLをチェック(1024byte以下の文字列 … それ以上の場合は切り捨て)
	 * @param コンテンツ配布者の情報を表示するURL
	 */
	private String checkDistributorURL(String distributorURL) {
		// Link文字列か確認
		if(StringUtil.isLink(distributorURL)){
			int distributorURLLength = -1;
			try {
				distributorURLLength = distributorURL.getBytes("UTF-8").length;
				// 1024byte以下の文字列ならそのままセット
				if(distributorURLLength <= 1024) return distributorURL;
				else{
					// 1024byte以上の文字列は切り捨ててからセット
					distributorURL = StringUtil.trunc(distributorURL,1024,"UTF-8");
					Logput.v("distributorURL = " + distributorURL);
					Logput.v("distributorURL byte = " + distributorURL.getBytes("UTF-8").length);
					return distributorURL;
				}
			} catch (UnsupportedEncodingException e) {
				Logput.e(e.getMessage(), e);
				return null;
			} catch (UnsupportedCharsetException e) {
				Logput.e(e.getMessage(), e);
				return null;
			} catch (CharacterCodingException e) {
				Logput.e(e.getMessage(), e);
				return null;
			}
		}else{
			return null;
		}
	}
}
