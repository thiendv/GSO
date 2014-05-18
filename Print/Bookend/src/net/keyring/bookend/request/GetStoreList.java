package net.keyring.bookend.request;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
 * GetStoreListリクエスト処理
 * @author Hamaji
 *
 */
public class GetStoreList implements ConstReq {

	/** GetStoreListレスポンスステータス詳細 */
	private String description = null;
	/** Context */
	private Context			mCon;
	
	/** 販売サイト一覧 */
	ArrayList<Map<String, String>> storeList = null;
	
	/**
	 * GetStoreListリクエスト実行
	 * @param con Context
	 * @param libraryID ライブラリーID(必須)
	 * @param resetFlag リセットフラグ(自分も解除する場合は"true",自分以外を解除する場合は"false")
	 * @return GetStoreListリクエスト処理が正常終了した場合はtrue,それ以外はfalseを返す
	 */
	public boolean execute(Context con){
		boolean check = false;
		mCon = con;
		// パラメータセット
		ArrayList<NameValuePair> params = setParams(con);
		try {
			// GetStoreListリクエスト
			XmlPullParser parser = request(params);
			if(parser != null){
				String status = getResDetail(parser);
				if(Utils.equal_str("92000", status)){
					// 正常終了
					check = true;
				}else{
					// エラー - ダイアログ表示メッセージセット
					setErrorMessage(con, status);
				}
			}
		} catch (NumberFormatException e) {
			this.description = con.getString(R.string.status_null, GET_STORE_LIST);
			Logput.w(description);
		} catch (NullPointerException e) {
			this.description = con.getString(R.string.status_null, GET_STORE_LIST);
			Logput.w(description);
		} catch (IOException e) {
			Logput.e(e.getMessage(), e);
		} catch (XmlPullParserException e) {
			Logput.e(e.getMessage(), e);
		}
		Logput.v("---------------------------------");
		return check;
	}
	
	/**
	 * GetStoreListリクエストを行い、受け取った値を返す
	 * @param params パラメータ
	 * @return XmlPullParser
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 */
	private XmlPullParser request(ArrayList<NameValuePair> params) throws IOException, XmlPullParserException{
		XmlPullParser parser = null;
		RequestServer req = new RequestServer(params, GET_STORE_LIST, mCon);
		parser = req.execute();
		return parser;
	}
	
	/**
	 * GetStoreListリクエストに必要なパラメータセット
	 * @return リクエストパラメータリスト
	 */
	private ArrayList<NameValuePair> setParams(Context con){
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		// オーナーID(標準は１)
		String ownerID = "1";
		if(con.getString(R.string.store_flag).equals("2")){
			ownerID = con.getString(R.string.owner_id);
		}
		// (必須）アプリケーションを管理するコンテンツオーナのID
		params.add(new BasicNameValuePair(OWNER_ID, ownerID));
		params.add(new BasicNameValuePair(OS, "ANDROID"));
		return params;
	}
	
	/**
	 * 帰ってきたxmlの最初のタグ名から、response解析振り分け(xml階層１段のもののみ)
	 * @return レスポンスリスト
	 * @throws IOException 
	 * @throws XmlPullParserException 
	 */
	private String getResDetail(XmlPullParser parser) throws XmlPullParserException, IOException{
		String status = null;
		if(parser != null){
			int eventType = parser.getEventType();
			parser.next();
			String xmlName = parser.getName();
			// xmlを読み終わるまで回す
			storeList = new ArrayList<Map<String, String>>();
			Map<String, String> store = null;
			String tagName = null;
			String value = null;
			Logput.v("--- <" + xmlName + "> ---");
			while(eventType != XmlPullParser.END_DOCUMENT){
				switch(eventType){
				case XmlPullParser.START_TAG:
					tagName = parser.getName();
					if(Utils.equal_str(STORE, tagName)){
						// <store> - new Map
						store = new HashMap<String, String>();
					}
					break;
				case XmlPullParser.TEXT:
					value = parser.getText();
					value = URLDecoder.decode(value);
					StringUtil.putResponce(tagName, value);
					if(!StringUtil.isEmpty(tagName) && !StringUtil.isEmpty(value)){
						if(tagName.equals(STORE_NAME)){
							store.put(STORE_NAME, value);
						}else if(tagName.equals(STORE_URL)){
							store.put(STORE_URL, value);
						}else if(tagName.equals(STORE_TYPE)){
							store.put(STORE_TYPE, value);
						}else if(tagName.equals(STORE_IMAGE_URL)){
							store.put(STORE_IMAGE_URL, value);
						}else if(tagName.equals(STATUS)){
							status = value;
						}
					}
					break;
				case XmlPullParser.END_TAG:
					tagName = parser.getName();
					// </store>
					if(Utils.equal_str(STORE, tagName)){
						storeList.add(store);
						Logput.v("-----------");
					}
					tagName = null;
					value = null;
					break;
				}
				eventType = parser.next();
			}
			Logput.v("--- </" + xmlName + "> ---");
		}
		return status;
	}
	
	/**
	 * GetStoreListレスポンスチェック
	 * @param statusコード
	 */
	private void setErrorMessage(Context con, String status) throws NumberFormatException, NullPointerException{
		switch(Utils.changeNum(status)){
		case 92010:
			this.description = con.getString(R.string.status_parameter_error, status);
			break;
		case 92020:
			this.description = con.getString(R.string.getStoreList_status_92020);
			break;
		case 92099:
			this.description = con.getString(R.string.status_server_internal_error, status);
			break;
		default:
			this.description = con.getString(R.string.status_false, GET_STORE_LIST, status);
			break;
		}
	}
	
	/**
	 * GetStoreListレスポンスエラー詳細表示文字列を返す
	 * @return GetStoreListレスポンスエラー詳細表示文字列
	 */
	public String getDescription(){
		return this.description;
	}
	/**
	 * 販売サイト情報一覧を返す
	 * @return 販売サイト情報一覧
	 */
	public ArrayList<Map<String, String>> getStoreList(){
		return this.storeList;
	}
}
