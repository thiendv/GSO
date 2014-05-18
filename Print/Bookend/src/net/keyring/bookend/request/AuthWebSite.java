package net.keyring.bookend.request;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import net.keyring.bookend.Logput;
import net.keyring.bookend.R;
import net.keyring.bookend.constant.ConstReq;
import net.keyring.bookend.util.StringUtil;
import net.keyring.bookend.util.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
/**
 * AuthWebSiteリクエスト処理
 * @author Hamaji
 *
 */
public class AuthWebSite implements ConstReq {
	
	private Context		mCon;
	private String		mStatus;
	private String		mDescription;
	
	/**
	 * AuthWebSite - レスポンスステータスコード取得
	 * @return status
	 */
	public String getStatus(){
		return mStatus;
	}
	/**
	 * AuthWebSite - レスポンスステータス詳細取得
	 * @return ステータス詳細
	 */
	public String getDescription(){
		return mDescription;
	}
	
	/**
	 * AuthWebSiteリクエスト実行
	 * @param authkey (authkeyIDとどちらかが必須)
	 * @param authkeyID (authkeyとどちらかが必須)
	 * @param host 販売サイトホスト名(必須)
	 * @param contentsID コンテンツID(必須)
	 * @return Authkeyチェックに問題なければtrue,それ以外はfalseを返す
	 */
	public boolean execute(Context con, String authkey,String authkeyID, String host, String contentsID){
		mCon = con;
		boolean check = false;
		// パラメータセット
		ArrayList<NameValuePair> params = setParams(authkey, authkeyID, host, contentsID);
		try {
			// AuthWebSiteリクエスト
			XmlPullParser parser = request(params);
			if(parser != null){
				ArrayList<ArrayList<String>> response = Utils.getResponseList(parser);
				String status = getResDetail(response);
				check = checkStatus(status);
			}
		} catch (IOException e) {
			Logput.e(e.getMessage(), e);
		} catch (XmlPullParserException e) {
			Logput.e(e.getMessage(), e);
		}
		Logput.v("---------------------------------");
		return check;
	}
	
	/**
	 * AuthWebSiteリクエストを行い、受け取った値を返す
	 * @param resetFlag リセットフラグ
	 * @return XmlPullParser
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 */
	private XmlPullParser request(ArrayList<NameValuePair> params) throws IOException, XmlPullParserException{
		XmlPullParser parser = null;
		RequestServer req = new RequestServer(params, AUTH_WEB_SITE, mCon);
		parser = req.execute();
		return parser;
	}
	
	/**
	 * AuthWebSiteリクエストに必要なパラメータセット
	 * @param libraryID ライブラリID
	 * @param pinFlag PINを送信する場合:true,しない場合:false
	 * @return リクエストパラメータリスト
	 */
	private ArrayList<NameValuePair> setParams(String authkey,String authkeyID, String host, String contentsID){
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		// AuthkeyまたはAuthkeyIDどちらか必須
		if(!StringUtil.isEmpty(authkey)){
			params.add(new BasicNameValuePair(AUTHKEY, authkey));
		}
    	if(!StringUtil.isEmpty(authkeyID)){
    		params.add(new BasicNameValuePair(AUTHKEYID, authkeyID));
    	}
    	// IP取得
    	String ip = getIP(host);
    	if(!StringUtil.isEmpty(ip)){
    		params.add(new BasicNameValuePair(IP, ip));
    	}
    	params.add(new BasicNameValuePair(HOST, host));
    	params.add(new BasicNameValuePair(CONTENTS_ID, contentsID));
    	return params;
	}
	
	/**
	 * SiteHostの名前解決（名前解決に失敗した場合はエラーにせず、IPアドレスを認証に使用しない）
	 * @return IPアドレス
	 */
	private String getIP(String host){
		String ip = null;
		try{
			// IPアドレス取得
			InetAddress ipAddress = InetAddress.getByName(host) ;
			ip = ipAddress.getHostAddress();
			Logput.v("IP:" + ip);
		}catch(UnknownHostException e){
			Logput.w("UnknownHostException...Get IP address is false", e);
		}
		return ip;
	}
	
	/**
	 * 受け取ったレスポンスxmlを解析して変数にセット
	 * @param xmlの内容をList化したもの
	 */
	private String getResDetail(ArrayList<ArrayList<String>> list){
		if(list != null){
			for(int i=0; i<list.size(); i++){
				ArrayList<String> array = list.get(i);
				if(array.size() >= 2){
					String tagName = array.get(0);
					if(tagName.equals(STATUS)){
						// Statusコード
						mStatus = array.get(1);
					}else if(tagName.equals(DESCRIPTION)){
						mDescription = array.get(1);
					}
					StringUtil.putResponce(tagName, array.get(1));
				}
			}
		}
		return mStatus;
	}
	
	private boolean checkStatus(String status){
		try{
			switch(Utils.changeNum(status)){
			case 75000:
				return true;
			case 75010:
				mDescription = mCon.getString(R.string.status_parameter_error, status);
				break;
			case 75011:
				mDescription = mCon.getString(R.string.authwebsite_75011);
				break;
			case 75012:
				mDescription = mCon.getString(R.string.authwebsite_75012);
				break;
			case 75013:
				mDescription = mCon.getString(R.string.authwebsite_75013);
				break;
			case 75099:
				mDescription = mCon.getString(R.string.status_server_internal_error, status);
				break;
			default:
				mDescription = mCon.getString(R.string.status_false, AUTH_WEB_SITE, status);
				break;
			}
		}catch(NumberFormatException e){
			mDescription = mCon.getString(R.string.status_null, AUTH_WEB_SITE);
		}catch(NullPointerException e){
			mDescription = mCon.getString(R.string.status_null, AUTH_WEB_SITE);
		}
		return false;
	}
}
