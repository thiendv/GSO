package net.keyring.bookend.request;

import java.io.IOException;
import java.util.ArrayList;

import net.keyring.bookend.Logput;
import net.keyring.bookend.Preferences;
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
 * CheckActivationリクエスト処理
 * @author Hamaji
 *
 */
public class CheckActivation implements ConstReq {
	
	/** Context */
	private Context			mCon;
	/** Preferencesクラス */
	private Preferences		mPref;
	
	/** 受け取ったAccessCode */
	private String			mAccessCode;
	/** Description */
	private String			mDescription;
	
	/**
	 * コンストラクタ
	 * @param con Context
	 */
	public CheckActivation(Context con){
		this.mCon = con;
	}
	
	/**
	 * レスポンスステータス詳細を取得
	 * @return　レスポンスステータス詳細
	 */
	public String getDescription(){
		return mDescription;
	}
	
	/**
	 * CheckActivationリクエスト実行
	 * @return ステータスコード
	 */
	public String execute(){
		String status = null;
		// パラメータセット
		ArrayList<NameValuePair> params = setParams();
		// BookendMailリクエスト
		try {
			if(params != null){
				XmlPullParser parser = request(params);
				if(parser != null){
					ArrayList<ArrayList<String>> response = Utils.getResponseList(parser);
					status = getResDetail(mCon, response);
					setDescription(status);
				}
			}
		} catch (IOException e) {
			//	Utils.isConnected()がtrueでインターネット接続が無い場合、ここに来る
			mDescription = mCon.getString(R.string.status_io_exception);
			Logput.e(e.getMessage(), e);
		} catch (XmlPullParserException e) {
			Logput.e(e.getMessage(), e);
		}
		Logput.v("---------------------------------");
		return status;
	}
	
	/**
	 * CheckActivationリクエストを行い、受け取った値を返す
	 * @param params リクエストパラメータ
	 * @return XmlPullParser
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 */
	private XmlPullParser request(ArrayList<NameValuePair> params) throws IOException, XmlPullParserException{
		XmlPullParser parser = null;
		RequestServer req = new RequestServer(params, CHECK_ACTIVATION, mCon);
		parser = req.execute();
		return parser;
	}
	
	/**
	 * CheckActivationリクエストに必要なパラメータセット
	 * @return リクエストパラメータリスト
	 */
	private ArrayList<NameValuePair> setParams(){
		ArrayList<NameValuePair> params = null;
		
		if(mPref == null) mPref = new Preferences(mCon);
		String libraryID = mPref.getLibraryID();
		String userID = mPref.getUserID();
		String accessCode = mPref.getAccessCode();
		
		if(StringUtil.isEmpty(libraryID) || StringUtil.isEmpty(userID) || StringUtil.isEmpty(accessCode)){
			// どれか一つでもNULLだった場合はNG
			return null;
		}else{
			params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(LIBRARY_ID, libraryID));
			params.add(new BasicNameValuePair(USER_ID, userID));
			params.add(new BasicNameValuePair(ACCESSCODE, accessCode));
		}
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
					}else if(tagName.equals(DESCRIPTION)){
						this.mDescription = value;
					}
					StringUtil.putResponce(tagName, value);
				}
			}
		}
		return status;
	}
	
	/**
	 * レスポンスステータス詳細をセット
	 * @param status
	 */
	private void setDescription(String status){
		try{
			switch(Utils.changeNum(status)){
			case 23000:	// OK - AccessCode登録
				if(mPref == null) mPref = new Preferences(mCon);
				mPref.setAccessCode(this.mAccessCode);
				break;
			case 23001:
				mDescription = mCon.getString(R.string.check_activation_23001);
				break;
			case 23010:
				mDescription = mCon.getString(R.string.status_parameter_error, status);
				break;
			case 23011:	// 他のクライアントからリセットされた状態：初期化して再起動
				mDescription = mCon.getString(R.string.library_not_registered, status);
				break;
			case 23012:
				mDescription = mCon.getString(R.string.check_activation_23012);
				break;
			case 23013:
				mDescription = mCon.getString(R.string.invalid_userid, status);
				break;
			case 23099:
				mDescription = mCon.getString(R.string.status_server_internal_error, status);
				break;
			}
		}catch(NumberFormatException e){
			mDescription = mCon.getString(R.string.status_null, CHECK_ACTIVATION);
		}catch(NullPointerException e){
			mDescription = mCon.getString(R.string.status_null, CHECK_ACTIVATION);
		}
	}
}
