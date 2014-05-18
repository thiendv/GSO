package net.keyring.bookend.request;

import java.io.IOException;
import java.util.ArrayList;

import net.keyring.bookend.Logput;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.R;
import net.keyring.bookend.constant.ConstReq;
import net.keyring.bookend.util.DecryptUtil;
import net.keyring.bookend.util.StringUtil;
import net.keyring.bookend.util.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
/**
 * Activate2リクエスト処理
 * @author Hamaji
 *
 */
public class Activate2 implements ConstReq {

	/** Context */
	private Context			mCon;
	/** Preferencesクラス */
	private Preferences		mPref;
	
	/** 受け取ったUserID */
	private String			mUserID = null;
	/** 受け取ったAccessCode */
	private String			mAccessCode = null;
	/** Activate2レスポンスステータス詳細 */
	private String			mDescription = null;
	
	/**
	 * コンストラクタ
	 * @param con Context
	 */
	public Activate2(Context con){
		this.mCon = con;
	}
	
	/**
	 * Activate2リクエスト実行
	 * @return ステータスコード
	 */
	public boolean execute(){
		boolean check = false;
		String status = "";
		// パラメータセット
		ArrayList<NameValuePair> params = setParams();
		// BookendMailリクエスト
		try {
			XmlPullParser parser = request(params);
			if(parser != null){
				ArrayList<ArrayList<String>> response = Utils.getResponseList(parser);
				status = getResDetail(mCon, response);
				check = Activate2_checkStatus(status);
				if(check){
					// OK - UserID,AccessCode登録
					if(mPref == null) mPref = new Preferences(mCon);
					mPref.setUserID(this.mUserID);
					mPref.setAccessCode(this.mAccessCode);
				}
			}
		} catch (NumberFormatException e){
			e.printStackTrace();
			mDescription = mCon.getString(R.string.status_false, ACTIVATE2, status);
			Logput.w(mDescription);
		} catch(NullPointerException e){
			e.printStackTrace();
			mDescription = mCon.getString(R.string.status_null, ACTIVATE2);
			Logput.w(mDescription);
		} catch (IOException e) {
			e.printStackTrace();
			//	Utils.isConnected()がtrueでインターネット接続が無い場合、ここに来る
			mDescription = mCon.getString(R.string.first_activation_offline);
			Logput.e(e.getMessage(), e);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Logput.e(e.getMessage(), e);
		}
		Logput.v("---------------------------------");
		return check;
	}
	
	/**
	 * Activate2リクエストを行い、受け取った値を返す
	 * @param params リクエストパラメータ
	 * @return XmlPullParser
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 */
	private XmlPullParser request(ArrayList<NameValuePair> params) throws IOException, XmlPullParserException{
		XmlPullParser parser = null;
		RequestServer req = new RequestServer(params, ACTIVATE2, mCon);
		parser = req.execute();
		return parser;
	}
	
	/**
	 * Activate2リクエストに必要なパラメータセット
	 * @return リクエストパラメータリスト
	 */
	private ArrayList<NameValuePair> setParams(){
		if(mPref == null) mPref = new Preferences(mCon);
		String libraryID = mPref.getLibraryID();
		String userID = mPref.getUserID();
		String ownerID = mCon.getString(R.string.owner_id);
		
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		// ライブラリIDがNULLだった場合は乱数をセットしてsettings.xmlに登録
		if(StringUtil.isEmpty(libraryID)){
    		// ライブラリID新規作成	※乱数16byteのBase16エンコード文字列
    		byte[] random = DecryptUtil.genRandomData(16);
    		libraryID = DecryptUtil.base16enc(random);
    		mPref.setLibraryID(libraryID);
    		Logput.d("Make LibraryID = " + libraryID);
		}
		params.add(new BasicNameValuePair(LIBRARY_ID, libraryID));
		if(ownerID != null){
			// Web書庫を専有化しない場合もパラメーターつける
			params.add(new BasicNameValuePair(OWNER_ID_L, ownerID));
		}
		
		if(!StringUtil.isEmpty(userID))params.add(new BasicNameValuePair(USER_ID, userID));
		return params;
	}
	
	/**
	 * 受け取ったレスポンスxmlを解析して変数にセット
	 * @param xmlの内容をList化したもの
	 */
	private String getResDetail(Context con, ArrayList<ArrayList<String>> list){
		String status = null;
		if(list != null){
			for(int i=0; i<list.size(); i++){
				ArrayList<String> array = list.get(i);
				if(array.size() >= 2){
					String tagName = array.get(0);
					String value = array.get(1);
					if(tagName.equals(STATUS)){
						status = value;
					}else if(tagName.equals(ACCESSCODE)){
						this.mAccessCode = value;
					}else if(tagName.equals(USER_ID)){
						this.mUserID = value;
					}
					StringUtil.putResponce(tagName, value);
				}
			}
		}
		return status;
	}
	
	/**
	 * Activate2レスポンスステータスチェック
	 * @param statusStr ステータスコード
	 * @return　正常に終了した場合はtrue,それ以外はfalseを返す
	 */
	private boolean Activate2_checkStatus(String status) throws NumberFormatException,NullPointerException{
		switch(Utils.changeNum(status)){
		case 20000:
			return true;
		case 20010:
			mDescription = mCon.getString(R.string.status_parameter_error, status);
			break;
		case 20011:
			mDescription = mCon.getString(R.string.activation_status_20011);
			break;
		case 20012:
			mDescription = mCon.getString(R.string.activation_status_20012);
			break;
		case 20013:
			mDescription = mCon.getString(R.string.activation_status_20013);
			break;
		case 20014:
			mDescription = mCon.getString(R.string.activation_status_20014);
			break;
		case 20015:
			mDescription = mCon.getString(R.string.invalid_userid, status);
			break;
		case 20099:
			mDescription = mCon.getString(R.string.status_server_internal_error, status);
			break;
		default:
			mDescription = mCon.getString(R.string.status_false, ACTIVATE2, status);
			break;
		}
		return false;
	}
	
	/**
	 * Activate2レスポンスエラー詳細表示文字列取得
	 * @return　Activate2レスポンスエラー詳細表示文字列
	 */
	public String getDescription(){
		return this.mDescription;
	}
}
