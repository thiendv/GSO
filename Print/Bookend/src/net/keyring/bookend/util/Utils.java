package net.keyring.bookend.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import net.keyring.bookend.Logput;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.R;
import net.keyring.bookend.bean.BookBeans;
import net.keyring.bookend.constant.Const;
import net.keyring.bookend.constant.ConstQuery;
import net.keyring.bookend.db.ContentsDao;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;

/**
 * Utility<br><br>
 *
 * * ネットワークの状態を返す<br>
 * * 指定パスにファイルが存在するかどうかチェックする<br>
 * * エラーログ出力(text)<br>
 * * エラーログ出力(StackTrance)<br>
 * * ログ出力<br>
 * * 指定パスがディレクトリかどうかチェック、なければ作成<br>
 * * 指定ディレクトリをなければ作成、チェックする<br>
 * * ファイルやフォルダを削除<br>
 *
 * @author Hamaji
*/
public class Utils implements Const{
	
	/**
	 * ファイルタイプがPDFかどうかチェック
	 * @param fileType
	 * @return ファイルタイプがpdfの場合はtrue,それ以外はfalseを返す
	 */
	public static boolean isPdf(String fileType) throws NullPointerException{
		String fileType_num = "1";
		try{
			if(fileType_num.equals(fileType) || ConstQuery.PDF.equals(fileType)){
				return true;
			}else{
				return false;
			}
		}catch(NullPointerException e){
			throw new NullPointerException("FileType is null.");
		}
	}
	
	/**
	 * ファイルタイプがKRPDFかどうかチェック
	 * @param fileType
	 * @return ファイルタイプがkrpdfの場合はtrue,それ以外はfalseを返す
	 */
	public static boolean isKrpdf(String fileType) throws NullPointerException{
		String fileType_num = "2";
		try{
			if(fileType_num.equals(fileType) || ConstQuery.KRPDF.equals(fileType)){
				return true;
			}else{
				return false;
			}
		}catch(NullPointerException e){
			throw new NullPointerException("FileType is null.");
		}
	}
	
	/**
	 * ファイルタイプがKRPDFXかどうかチェック
	 * @param fileType
	 * @return ファイルタイプがkrpdfxの場合はtrue,それ以外はfalseを返す
	 */
	public static boolean isKrpdfx(String fileType) throws NullPointerException{
		String fileType_num = "10";
		try{
			if(fileType_num.equals(fileType) || ConstQuery.KRPDFX.equals(fileType)){
				return true;
			}else{
				return false;
			}
		}catch(NullPointerException e){
			throw new NullPointerException("FileType is null.");
		}
	}
	
	/**
	 * ファイルタイプがEPUBかどうかチェック
	 * @param fileType
	 * @return ファイルタイプがepubの場合はtrue,それ以外はfalseを返す
	 */
	public static boolean isEPub(String fileType) throws NullPointerException{
		String fileType_num = "6";
		try{
			if(fileType_num.equals(fileType) || ConstQuery.EPUB.equals(fileType)){
				return true;
			}else{
				return false;
			}
		}catch(NullPointerException e){
			throw new NullPointerException("FileType is null.");
		}
	}
	/**
	 * ファイルタイプがKREPUBかどうかチェック
	 * @param fileType
	 * @return ファイルタイプがkrepubの場合はtrue,それ以外はfalseを返す
	 */
	public static boolean isKrEPub(String fileType) throws NullPointerException{
		String fileType_num = "7";
		try{
			if(fileType_num.equals(fileType) || ConstQuery.KREPUB.equals(fileType)){
				return true;
			}else{
				return false;
			}
		}catch(NullPointerException e){
			throw new NullPointerException("FileType is null.");
		}
	}
	
	/**
	 * ファイルタイプがKREPUBかどうかチェック
	 * @param fileType
	 * @return ファイルタイプがkrepubの場合はtrue,それ以外はfalseを返す
	 */
	public static boolean isBec(String fileType) throws NullPointerException{
		String fileType_num = "8";
		try{
			if(fileType_num.equals(fileType) || ConstQuery.BEC.equals(fileType)){
				return true;
			}else{
				return false;
			}
		}catch(NullPointerException e){
			throw new NullPointerException("FileType is null.");
		}
	}
	
	/**
	 * ファイルタイプがKREPUBかどうかチェック
	 * @param fileType
	 * @return ファイルタイプがkrepubの場合はtrue,それ以外はfalseを返す
	 */
	public static boolean isKrbec(String fileType) throws NullPointerException{
		String fileType_num = "9";
		try{
			if(fileType_num.equals(fileType) || ConstQuery.KRBEC.equals(fileType)){
				return true;
			}else{
				return false;
			}
		}catch(NullPointerException e){
			throw new NullPointerException("FileType is null.");
		}
	}

	/**
	 * ファイルタイプがMCMかどうかチェック
	 * @param fileType
	 * @return ファイルタイプがmcmの場合はtrue,それ以外はfalseを返す
	 */
	public static boolean isMcm(String fileType) throws NullPointerException{
		String fileType_num = "11";
		try{
			if(fileType_num.equals(fileType) || ConstQuery.MCMAGAZINE.equals(fileType)){
				return true;
			}else{
				return false;
			}
		}catch(NullPointerException e){
			throw new NullPointerException("FileType is null.");
		}
	}
	
	/**
	 * ファイルタイプがMCBookかどうかチェック
	 * @param fileType
	 * @return ファイルタイプがmcbookの場合はtrue,それ以外はfalseを返す
	 */
	public static boolean isMcbook(String fileType) throws NullPointerException{
		String fileType_num = "12";
		try{
			if(fileType_num.equals(fileType) || ConstQuery.MCBOOK.equals(fileType)){
				return true;
			}else{
				return false;
			}
		}catch(NullPointerException e){
			throw new NullPointerException("FileType is null.");
		}
	}
	
	/**
	 * ファイルタイプがMCComicかどうかチェック
	 * @param fileType
	 * @return ファイルタイプがmccomicの場合はtrue,それ以外はfalseを返す
	 */
	public static boolean isMccomic(String fileType) throws NullPointerException{
		String fileType_num = "13";
		try{
			if(fileType_num.equals(fileType) || ConstQuery.MCCOMIC.equals(fileType)){
				return true;
			}else{
				return false;
			}
		}catch(NullPointerException e){
			throw new NullPointerException("FileType is null.");
		}
	}
	
	/**
	 * ファイルタイプがKREPAかどうかチェック
	 * @param fileType
	 * @return ファイルタイプがkrepaの場合はtrue,それ以外はfalseを返す
	 */
	public static boolean isKrEPA(String fileType) throws NullPointerException{
		String fileType_num = "14";
		try{
			if(fileType_num.equals(fileType) || ConstQuery.KREPA.equals(fileType)){
				return true;
			}else{
				return false;
			}
		}catch(NullPointerException e){
			throw new NullPointerException("FileType is null.");
		}
	}
		
	/**
	 * ● USBデバックが有効かどうか
	 * @return USBデバックモードが有効の場合はtrue,無効の場合・チェックしない場合はfalseを返す
	 */
	public static boolean isDebugMode(Context con){
		return false;
	/*	
		boolean check = false;
		// USBデバッグモードを許可するかどうか(有効OK:true,有効NG:false)
		if(!Utils.checkFile(Environment.getExternalStorageDirectory().getPath() + "/" + USB_DEBUG_FILE)){
			// USBデバッグモード 1:有効, 0:無効...有効になっていた場合はアプリを終了する
    		if(isOsDebugMode(con) == 1){
    			check = true;
    		}
		}
		return check;
	*/
	}
	
	/**
	 * USBデバッグがonになっていないかチェック
	 * @return ONの場合は1を返す
	 */
	private static int isOsDebugMode(Context con){
		int isOsDebugMode = -1;
		try{
    		ContentResolver resolver = con.getContentResolver();
    		isOsDebugMode = Settings.Secure.getInt(resolver, Settings.Secure.ADB_ENABLED);
		}catch(Settings.SettingNotFoundException e){
    		Logput.e(e.getMessage(), e);
        }
		return isOsDebugMode;
	}	
	/**
	 * booleanできたものを文字列で返す
	 * @param which boolean
	 * @return boolean:true="true",boolean:false="false"
	 */
	public static String changeStrFlag(boolean which){
		if(which){
			return TRUE;
		}else{
			return FALSE;
		}
	}
	
	/**
	 * OS仕様言語取得
	 * @return OS仕様言語
	 */
	public static String getLanguage(){
		String language = null;
		language = Locale.getDefault().toString();
		if(!StringUtil.isEmpty(language)){
			if(language.equals("ja_JP")) language = "ja-JP";
		}
		return language;
	}
	
	/**
	 * 受け取った文字列がイコールかチェック
	 * @param compare 比較元
	 * @param value 比較対象
	 * @return　イコールの場合はtrue,それ以外はfalseを返す
	 */
	public static boolean equal_str(String compare, String value){
		boolean check = false;
		try{
			if(value.equals(compare)){
				check = true;
			}
		}catch(NullPointerException e){
		}
		return check;
	}
	
	/**
	 * 帰ってきたxmlの最初のタグ名から、response解析振り分け(xml階層１段のもののみ)
	 * @return レスポンスリスト
	 * @throws IOException 
	 * @throws XmlPullParserException 
	 */
	public static ArrayList<ArrayList<String>> getResponseList(XmlPullParser parser) throws XmlPullParserException, IOException{
		if(parser == null) return null;
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		ArrayList<String> array = null;
		int eventType = parser.getEventType();
		parser.next();
		String xmlName = parser.getName();
		// xmlを読み終わるまで回す
		while(eventType != XmlPullParser.END_DOCUMENT){
			switch(eventType){
			case XmlPullParser.START_TAG:
				array = new ArrayList<String>();
				array.add(0, parser.getName());
				eventType = parser.next();
				break;
			case XmlPullParser.TEXT:
				array.add(1,parser.getText());
				eventType = parser.next();
				break;
			case XmlPullParser.END_TAG:
				String endTag = parser.getName();
				if(!endTag.equals(xmlName)) list.add(array);
				eventType = parser.next();
				break;
			default:
				eventType = parser.next();
				break;
			}
		}
		return list;
	}
	
	/**
	 * Bookendのバージョンを取得
	 * @param con Context
	 * @return Bookendバージョン
	 */
	public static String getBookendVer(Context con){
		// クライアントのバージョン
		String version = null;
		try {
			// クライアントのバージョンをマニフェストから取得(省略可)
			PackageInfo packageInfo = con.getPackageManager().getPackageInfo(con.getPackageName(), PackageManager.GET_ACTIVITIES);
			version = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			Logput.e(e.getMessage(), e);
		}
		return version;
	}

	/**
	 * 文字列が数値に変換できるかチェック
	 * @param 文字列
	 * @return 数値に変換できる場合はtrueを返す
	 */
	public static boolean isNum(String strNum){
		try{
			// 問題なく数値に戻せる文字列ならばtrueを返す
			Integer.parseInt(strNum);
			return true;
		}catch(NumberFormatException e){
			Logput.w(e.getMessage(), e);
			// 数値に戻せない場合はfalseを返す
			return false;
		} catch (NullPointerException e){
			Logput.w(e.getMessage(), e);
			return false;
		}
	}

	/**
	 * 文字列を数値に変換
	 * @param 文字列
	 * @return 数値
	 * @throws NumberFormatException
	 */
	public static int changeNum(String strNum) throws NumberFormatException, NullPointerException{
		int num = Integer.parseInt(strNum);
		return num;
	}

	/**
     * ダウンロードリンクから飛んできたのかチェック
     * @param intent
     * @return schemeフラグ
     */
	public static int isDownloadLink(Uri uri, String customName){
		int flag = -1;
		if(uri != null){
			String scheme = uri.getScheme();
			String behttp = "behttp";
			String beinfo = "beinfo";
			try{
				if(!customName.equalsIgnoreCase(Const.BOOKEND)){
					behttp = behttp + customName;
					beinfo = beinfo + customName;
				}
			}catch(NullPointerException e){
				Logput.w("customName is null.");
			}
			if(behttp.equals(scheme)){
				flag = SCHEME_BEHTTP;
			}else if(beinfo.equals(scheme)){
				flag = SCHEME_BEINFO;
			}
		}
		return flag;
    }

	/** メールアドレス文字列正規表現確認用 */
	public final static String MATCH_MAIL = "([a-zA-Z0-9][a-zA-Z0-9_.+\\-]*)@(([a-zA-Z0-9][a-zA-Z0-9_\\-]+\\.)+[a-zA-Z]{2,6})";
	/**
	 * 入力されたメールアドレスに不正がないかチェック
	 * @param メールアドレス
	 * @return 問題なければtrue,問題ある場合はfalseを返す
	 */
	public static boolean checkMailAddress(String address){
		if(StringUtil.isEmpty(address)){
			Logput.v("MailAddress is NULL");
			return false;
		}else{
			// 前後に空白があった場合は削除
			address = address.trim();
			if (address.matches(MATCH_MAIL)) {
			return true;
			} else {
				Logput.d("MailAddress check : NG = " + address);
			return false;
			}
		}
	}

	/**
	 * ネットワークの状態を返す
	 * @param context
	 * @return オンライン状態ならtrue、オフライン状態ならfalseを返す
	 * @note 端末がWiFiに接続されているが、WiFi自体のインターネット接続が切れている場合、この処理では true が返る
	 */
	 public static boolean isConnected(Context context){
		 boolean ret = false;
		 //	オフラインフラグが立っている場合はfalseを返す
		 //	*このフラグはインターネット接続が無いにも関わらずConnectivityManagerがtrueを返す場合にセットされる
		 if(Preferences.sOffline) {
			 ret = false;
		 }
		 else {
			 //	それ以外の場合はConnectivityManagerでチェックする
	         ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	         NetworkInfo ni = cm.getActiveNetworkInfo();
	         if( ni != null && cm.getActiveNetworkInfo().isConnected()) {
	        	 ret = true;
	         }
		 }
         Logput.d("isConnected = " + ret);
         return ret;
	}

	 /**
	  * 指定パスにファイル又はディレクトリが存在するかどうかチェックする
	  * @param 指定パス
	  * @return ファイルが存在すればtrue,なければfalseを返す
	  */
	 public static boolean checkFile(String path){
		 if(StringUtil.isEmpty(path)) return false;
		 File checkFile = new File(path);
		 if(checkFile.exists()){
	    	return true;
	    }else{
	    	return false;
	    }
	 }

	 /**
	  * androidでの閲覧が許可されているかチェック
	  * @param context
	  * @param rowID
	  * @return androidでの閲覧が許可されていればtrue,許可されていなければfalseを返す
	  */
	 public static boolean checkInvalidPlatform(Context context,long rowID){
    	// InvalidPlatformがandroidのものはtrueを返す
		 ContentsDao dao = new ContentsDao(context);
		 BookBeans contents = dao.load(rowID);
		 String invalidPlatform = contents.getInvalidPlatform();
		 if(invalidPlatform.equals(ANDROID)) return false;
		 else return true;
	 }
	 
	 /**
	  * オンラインヘルプURLを取得
	  * @param context
	  * @return
	  */
	 public static String getHelpURL(Context context) {
		String online_help;
		// 専用ヘルプ
		if(context.getString(R.string.help_flag).equals("1")){	
			online_help = context.getString(R.string.help_url);
		}
		// 標準ヘルプ
		else{	
			online_help = "http://bookend.keyring.net/support/link.php";
			String language = Locale.getDefault().toString();
			if (!StringUtil.isEmpty(language)) {
				if (language.equals("ja_JP"))
					language = "ja-JP";
			}
			String ver = Utils.getBookendVer(context);
			// url~?ver=x.x&locale=xx
			online_help = online_help + "?ver=" + ver + "&locale=" + language;
		}
		return online_help;
	 }
	 
	/**
	 * コンテンツファイル名を乱数から生成
	 * @param fileType	ファイルタイプを表す文字列
	 * @return 生成されたファイル名
	 */
	public static String getRandomContentFileName(String fileType){
		byte[] random = DecryptUtil.genRandomData(4);
		String fileName = DecryptUtil.base16enc(random);
		
		//	FileTypeが mcm, epubx, epubxf の場合はビューアがファイル拡張子のチェックを行なっているので追加する
		if (Utils.isMcm(fileType)){
			fileName = fileName + ".mcm";  
		} else if (Utils.isMcbook(fileType)){
			fileName = fileName + ".epubx";
		} else if (Utils.isMccomic(fileType)){
			fileName = fileName  + ".epubxf";
		} 
							
		return fileName;
	}
	
	/**
	 * サムネイルファイル名を乱数から生成
	 * @return
	 */
	public static String getRandomThumbFileName() {
		byte[] random = DecryptUtil.genRandomData(4);
		return DecryptUtil.base16enc(random);
	}
	
	/**
	 * krepub,krbecのキーを取得
	 * @param keyStr	hex string of key
	 * @param size		bytes length of returned key
	 * @return krepub,krbecのキー
	 */
	public static byte[] getBytesKeyFromHexStr(String keyStr, int size) throws NullPointerException{
		byte[] key = null;
		// Logput.v("key = " + keyStr);
		if(keyStr.length() > (size * 2)){
			keyStr = keyStr.substring(0, (size * 2));
		}
		key = DecryptUtil.base16dec(keyStr);
		return key;
	}
}
