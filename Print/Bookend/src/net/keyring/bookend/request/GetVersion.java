package net.keyring.bookend.request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.keyring.bookend.Logput;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.R;
import net.keyring.bookend.constant.Const;
import net.keyring.bookend.constant.ConstReq;
import net.keyring.bookend.constant.ConstStartUp;
import net.keyring.bookend.util.StringUtil;
import net.keyring.bookend.util.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.os.Build;
/**
 * GetVersionリクエスト処理
 * @author Hamaji
 *
 */
public class GetVersion implements ConstReq,ConstStartUp {
	/** Preferencesクラス */
	private Preferences pref = null;
	/** GetVersionレスポンスステータス詳細 */
	private String description = null;
	/** 最新バージョン */
	private String newVer = null;
	/** アップデート情報のURL */
	private String referURL = null;
	/** ダウンロードURL */
	private String downloadURL = null;
	/** 通常アップデート or 強制アップデート */
	private String forceUpdate = null;
	/** Context */
	private Context			mCon;
	
	/**
	 * GetVersionリクエスト実行
	 * @param con Context
	 * @param sLibraryID ライブラリーID(必須)
	 * @param resetFlag リセットフラグ(自分も解除する場合は"true",自分以外を解除する場合は"false")
	 * @return Resetリクエスト処理が正常終了した場合はtrue,それ以外はfalseを返す
	 */
	public Map<String,Object> execute(Context con, Map<String,Object> resultList){
		if(resultList == null) resultList = new HashMap<String,Object>();
		// パラメータセット
		ArrayList<NameValuePair> params = setParams(con);
		mCon = con;
		try {
			// Resetリクエスト
			XmlPullParser parser = request(params);
			if(parser != null){
				ArrayList<ArrayList<String>> response = Utils.getResponseList(parser);
				String status = getResDetail(response);
				if(Utils.equal_str("40000", status)){
					// 正常終了
					// 現在のバージョン
	    			String nowVer = Utils.getBookendVer(con);
					
					if(isForceUpdate(forceUpdate)){
	    				// 強制アップデート
	    				resultList.put(STATUS_KEY, CHECK_VERSIONUP_FORCE);
	    				// アップデートクライアントダウンロードサイトURL
	        			resultList.put(UPDATE_URL, this.downloadURL);
	        			// ダイヤログに表示するメッセージ
	        			String message = getVerUpDialogMes(con, this.newVer, nowVer, this.referURL);
	        			resultList.put(DIALOG_MESSAGE, message);
	    			}else if(isVersionUP(this.newVer, nowVer)){
		    			// 通常アップデート
	    				resultList.put(STATUS_KEY, CHECK_VERSIONUP_USUALLY);
	    				// アップデート情報サイトURL
	        			resultList.put(UPDATE_URL, this.downloadURL);
	        			// ダイヤログに表示するメッセージ
	        			String message = getVerUpDialogMes(con, this.newVer, nowVer, this.referURL);
	        			resultList.put(DIALOG_MESSAGE, message);
	    			}else{
	    				// バージョンアップの必要がない場合はOK
	        			resultList.put(STATUS_KEY, CHECK_OK);
	    			}
				}
				else {
					resultList.put(STATUS_KEY, CHECK_ERROR_VERSION_CHECK);
				}
			}
		} catch (IOException e) {
			Logput.e(e.getMessage(), e);
			resultList = null;
		} catch (XmlPullParserException e) {
			Logput.e(e.getMessage(), e);
			resultList = null;
		}
		Logput.v("---------------------------------");
		return resultList;
	}
	
	/**
	 * GetVersionリクエストを行い、受け取った値を返す
	 * @param params パラメータ
	 * @return XmlPullParser
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 */
	private XmlPullParser request(ArrayList<NameValuePair> params) throws IOException, XmlPullParserException{
		XmlPullParser parser = null;
		RequestServer req = new RequestServer(params, GET_VERSION, mCon);
		parser = req.execute();
		return parser;
	}
	
	/**
	 * GetVersionリクエストに必要なパラメータセット
	 * @param sLibraryID ライブラリID
	 * @param pinFlag PINを送信する場合:true,しない場合:false
	 * @return リクエストパラメータリスト
	 */
	private ArrayList<NameValuePair> setParams(Context con){
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		
		// プログラム登録名(必須)
		String program = con.getString(R.string.custom_name);
		if(program.equalsIgnoreCase(Const.BOOKEND)){
			// 標準bookendアプリの場合
			params.add(new BasicNameValuePair(PROGRAM, "BookEndLibraryAndroid"));
		}else{
			params.add(new BasicNameValuePair(PROGRAM, "BookEndLibraryAndroid" + program));
		}
		// OS名(省略可)
		params.add(new BasicNameValuePair(OS, "ANDROID"));
		
		// クライアントのバージョンをマニフェストから取得(省略可)
		// デバイス名,OSバージョン
		String os_info = Build.DEVICE + "," + Build.VERSION.RELEASE;
		if(!StringUtil.isEmpty(os_info)){
			params.add(new BasicNameValuePair(OS_VERSION, os_info));
		}
		
		// 現在のクライアントバージョン(省略可)
		String version = Utils.getBookendVer(con);
		if(!StringUtil.isEmpty(version)){
			params.add(new BasicNameValuePair(VERSION, version));
		}
		
		// LibraryID(省略可)
		if(pref == null) pref = new Preferences(con);
		String libraryID = pref.getLibraryID();
		if(!StringUtil.isEmpty(libraryID)){
			params.add(new BasicNameValuePair(LIBRARY_ID, libraryID));
		}
		return params;
	}
	
	/**
	 * 受け取ったレスポンスxmlを解析して変数にセット
	 * @param xmlの内容をList化したもの
	 */
	private String getResDetail(ArrayList<ArrayList<String>> list){
		String status = null;
		ArrayList<String> processInfoList = null;
		ArrayList<String> printerInfoList = null;
		
		if(list != null){
			for(int i=0; i<list.size(); i++){
				ArrayList<String> array = list.get(i);
				if(array.size() >= 2){
					String tagName = array.get(0);
					String value = array.get(1);
					if(tagName.equals(STATUS)){
						status = array.get(1);
					}else if(tagName.equals(VERSION)){
						this.newVer = value;
					}else if(tagName.equals(REFER_URL)){
						this.referURL = value;
					}else if(tagName.equals(DOWNLOAD_URL)){
						this.downloadURL = value;
					}else if(tagName.equals(FORCE_UPDATE)){
						this.forceUpdate = value;
					}else if(tagName.equals(PROCESS_INFO)){
						// TODO 現在未使用のためコメントアウト
//						if(processInfoList == null){
//							processInfoList = new ArrayList<String>();
//						}
//						processInfoList.add(value);
					}else if(tagName.equals(PRINTER_INFO)){
						// TODO 現在未使用のためコメントアウト
//						if(printerInfoList == null){
//							printerInfoList = new ArrayList<String>();
//						}
//						printerInfoList.add(value);
					}
					StringUtil.putResponce(tagName, value);
				}
			}
		}
		return status;
	}
	
	/**
	 * アップデートダイアログに表示するメッセージ
	 * @param con Context
	 * @param newVer 最新クライアントバージョン
	 * @param nowVer 現在インストールされているクライアントのバージョン
	 * @param url アップデート情報のURL
	 */
	private String getVerUpDialogMes(Context con, String newVer, String nowVer, String url){
		String message = null;
		message = ">> " + con.getString(R.string.new_ver) + " "  + newVer
			+ "<br>"
			+ "(" + con.getString(R.string.now_ver) + " " + nowVer + ")"
			+ "<br><br>"
			+ con.getString(R.string.ver_refer_url_1)
			+ "<br>"
			+ con.getString(R.string.ver_refer_url_2)
			+ "<br>"
			+ url;
		return message;
	}
	
	/**
     * 強制アップデートフラグチェック
     * @param 強制アップデートフラグ
     * @return 強制アップデートが必要な場合はtrue,それ以外はfalseを返す
     */
	private boolean isForceUpdate(String forceUpdate){
    	boolean check = false;
    	if(!StringUtil.isEmpty(forceUpdate)){
    		if(forceUpdate.equals(Const.TRUE)){
    			check = true;
    		}
    	}
    	return check;
    }
	
	/**
     * 現在使用しているBookendのバージョンとGetVersionで取得したバージョンを比較
     * @param Request
     * @param 使用bookendバージョン
     * @return バージョンアップが必要な場合はtrue,それ以外はfalseを返す
     */
	private boolean isVersionUP(String getVer, String bookendVer){
    	boolean isVerUP = false;
    	List<String> getVerList = StringUtil.parseStr(getVer, '.');
    	List<String> bookendVerList = StringUtil.parseStr(bookendVer, '.');
    	if(getVerList == null) return isVerUP;
    	else if(getVerList.size() <= 0) return isVerUP;
    	else if(bookendVerList == null) return isVerUP;
    	int beListSize = bookendVerList.size();

    	// Versionカンマ区切りで比較
    	for(int i=0; i < getVerList.size(); i++){
    		int getV = -1;
    		int beV = -1;
    		if(beListSize > i){
    			if(Utils.isNum(getVerList.get(i))) getV = Utils.changeNum(getVerList.get(i));
    			if(Utils.isNum(bookendVerList.get(i))) beV = Utils.changeNum(bookendVerList.get(i));
    			if(getV == -1 || beV == -1) return isVerUP;
    			// GetVersionで取得した数のほうが大きい場合はバージョンアップが必要
    			if(getV != beV){
    				if(getV > beV){
	    				isVerUP = true;
	    				break;
    				}else{
    					break;
    				}
    			}
    		}
    	}
    	return isVerUP;
    }
	
	/**
	 * Resetレスポンスエラー詳細表示文字列を返す
	 * @return Resetレスポンスエラー詳細表示文字列
	 */
	public String getDescription(){
		return description;
	}
}
