package net.keyring.bookend.request;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import net.keyring.bookend.Logput;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.R;
import net.keyring.bookend.constant.ConstReq;
import net.keyring.bookend.util.StringUtil;
import net.keyring.bookend.util.Utils;
/**
 * ChangeUserIdリクエスト処理
 * @author Hamaji
 *
 */
public class ChangeUserId implements ConstReq {
	
	/** レスポンスで受け取ったAccessCode */
	private String			mAccessCode = null;
	/** Context */
	private Context			mCon;
	/** Preferencesクラス */
	private Preferences		mPref = null;
	
	/**
	 * ChangeUserIdリクエスト実行
	 * @param con Context
	 * @param new_userID 新しいuserID
	 * @param org_userID 以前のuserID
	 * @return 正常に処理が終了し、UserID,AccessCodeを登録した場合はtrue,それ以外はfalseを返す
	 */
	public boolean execute(Context con, String new_userID, String org_userID){
		boolean check = false;
		mCon = con;
		
		// パラメータセット
		ArrayList<NameValuePair> params = setParams(new_userID, org_userID);
		// BookendMailリクエスト
		XmlPullParser parser;
		try {
			parser = request(params);
			if(parser != null){
				ArrayList<ArrayList<String>> response = Utils.getResponseList(parser);
				String status = getResDetail(response);
				if(Utils.equal_str("26000", status)){
					// OK - AccessCode,UserID登録
					if(mPref == null) mPref = new Preferences(con);
					mPref.setUserID(new_userID);
					mPref.setAccessCode(mAccessCode);
					check = true;
				}else{
					Logput.i(con.getString(R.string.change_userid_status_error));
				}
			}
		} catch (IOException e) {
			Logput.e(e.getMessage(), e);
		} catch (XmlPullParserException e) {
			Logput.e(e.getMessage(), e);
		}
		return check;
	}
	
	/**
	 * ChangeUserIdリクエストを行い、受け取った値を返す
	 * @param params リクエストパラメータ
	 * @return XmlPullParser
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 */
	private XmlPullParser request(ArrayList<NameValuePair> params) throws IOException, XmlPullParserException{
		XmlPullParser parser = null;
		RequestServer req = new RequestServer(params, CHANGE_USERID, mCon);
		parser = req.execute();
		return parser;
	}
	
	/**
	 * ChangeUserIdリクエストに必要なパラメータセット
	 * @param pin 入力されたPINコード
	 * @return リクエストパラメータリスト
	 */
	private ArrayList<NameValuePair> setParams(String new_userID, String org_userID){
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		// 全て必須パラメータ
		if(mPref == null) mPref = new Preferences(mCon);
		String libraryID = mPref.getLibraryID();
		params.add(new BasicNameValuePair(LIBRARY_ID, libraryID));
		params.add(new BasicNameValuePair(ORG_USERID, org_userID));
		params.add(new BasicNameValuePair(NEW_USERID, new_userID));
		try{
			// OwnerID
			String ownerId = mCon.getString(R.string.owner_id);
			if(ownerId != null){
				params.add(new BasicNameValuePair(OWNER_ID_L, ownerId));
			}
		}catch(NullPointerException e){
		}
		return params;
	}
	
	/**
	 * 受け取ったレスポンスxmlを解析して変数にセット
	 * @param xmlの内容をList化したもの
	 */
	private String getResDetail(ArrayList<ArrayList<String>> list){
		String status = null;
		if(list != null){
			for(int i=0; i<list.size(); i++){
				ArrayList<String> array = list.get(i);
				if(array.size() >= 2){
					String tagName = array.get(0);
					if(tagName.equals(STATUS)){
						// Statusコード
						status = array.get(1);
					}else if(tagName.equals(ACCESSCODE)){
						// AccessCode
						mAccessCode = array.get(1);
					}
					StringUtil.putResponce(tagName, array.get(1));
				}
			}
		}
		return status;
	}
}
