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
 * RegistMailAddress2リクエスト
 * @author Hamaji
 *
 */
public class RegistMailAddress2 implements ConstReq {

	/** Context */
	private Context			mCon;
	/** Preferencesクラス */
	private Preferences		mPref;

	/** RegistMailAddress2リクエストレスポンスステータス詳細 */
	private String			mDescription = null;
	/** UserID */
	private String			mUserID;
	
	/**
	 * コンストラクタ
	 * @param con Context
	 */
	public RegistMailAddress2(Context con){
		this.mCon = con;
	}
	
	/**
	 * RegistMailAddress2リクエストで受け取ったUserIDを返す
	 * @return UserID
	 */
	public String getUserID(){
		return this.mUserID;
	}
	/**
	 * RegistMailAddress2リクエストレスポンスエラー詳細文字列を返す
	 * @return RegistMailAddress2リクエストレスポンスエラー詳細文字列
	 */
	public String getDescription(){
		return this.mDescription;
	}
	
	/**
	 * RegistMailAddress2リクエスト実行
	 * @param mailAddress 登録メールアドレス
	 * @param update trueの場合パラメータのUserIDに既にメールアドレスが登録されていても、新しいメールアドレスで上書きします
	 * @return ステータスコード
	 */
	public String execute(String mailAddress, boolean update){
		String status = null;
		
		if(!StringUtil.isEmpty(mailAddress)){
			// パラメータセット
			ArrayList<NameValuePair> params = setParams(mailAddress, update);
			// BookendMailリクエスト
			XmlPullParser parser;
			try {
				parser = request(params);
				if(parser != null){
					ArrayList<ArrayList<String>> response = Utils.getResponseList(parser);
					status = getResDetail(mCon, response);
					registMailAddress2_checkStatus(status);
				}
			} catch (NumberFormatException e){
				this.mDescription = mCon.getString(R.string.status_null, REGIST_MAIL_ADDRESS2);
				Logput.w(mDescription);
			} catch (NullPointerException e){
				this.mDescription = mCon.getString(R.string.status_null, REGIST_MAIL_ADDRESS2);
				Logput.w(mDescription);
			} catch (IOException e) {
				Logput.e(e.getMessage(), e);
			} catch (XmlPullParserException e) {
				Logput.e(e.getMessage(), e);
			}
		}
		Logput.v("---------------------------------");
		return status;
	}
	
	/**
	 * RegistMailAddress2リクエストを行い、受け取った値を返す
	 * @param params リクエストパラメータ
	 * @return XmlPullParser
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 */
	private XmlPullParser request(ArrayList<NameValuePair> params) throws IOException, XmlPullParserException{
		XmlPullParser parser = null;
		RequestServer req = new RequestServer(params, REGIST_MAIL_ADDRESS2, mCon);
		parser = req.execute();
		return parser;
	}
	
	/**
	 * RegistMailAddress2リクエストに必要なパラメータセット
	 * @param mailAddress メールアドレス
	 * @param update PINを送信する場合:true,しない場合:false
	 * @return リクエストパラメータリスト
	 */
	private ArrayList<NameValuePair> setParams(String mailAddress, boolean update){
		if(mPref == null) mPref = new Preferences(mCon);
		String userID = mPref.getUserID();
		
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(MAIL_ADDRESS, mailAddress));
		params.add(new BasicNameValuePair(USER_ID, userID));
		params.add(new BasicNameValuePair(UPDATE, Utils.changeStrFlag(update)));
		String language = Utils.getLanguage();
		if(!StringUtil.isEmpty(language)){
			params.add(new BasicNameValuePair(LANGUAGE, language));
		}
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
	private String getResDetail(Context con, ArrayList<ArrayList<String>> list){
		String status = null;
		if(list != null){
			for(int i=0; i<list.size(); i++){
				ArrayList<String> array = list.get(i);
				if(array.size() >= 2){
					String tagName = array.get(0);
					if(tagName.equals(STATUS)){
						// Statusコード
						status = array.get(1);
					}else if(tagName.equals(CHECK_CODE)){
						// checkCode : settings.xmlに一時保存
						if(mPref == null) mPref = new Preferences(con);
						mPref.setCheckCode(array.get(1));
					}else if(tagName.equals(MAIL_ADDRESS)){
						if(mPref == null) mPref = new Preferences(con);
						mPref.setMailAddress_temporary(array.get(1));
					}else if(tagName.equals(USER_ID)){
						this.mUserID = array.get(1);
					}
					StringUtil.putResponce(tagName, array.get(1));
				}
			}
		}
		return status;
	}
	
	/**
	 * リクエスト：registMailAddress2 ステータスコードチェック
	 * @param statusコード
	 * @return OKの場合はtrue,それ以外はfalseを返す
	 */
	private void registMailAddress2_checkStatus(String status) throws NumberFormatException,NullPointerException{
		switch(Utils.changeNum(status)){
		case 71001:
			this.mDescription = mCon.getString(R.string.regist_mailaddress2_status_71001);
			break;
		case 71002:
			this.mDescription = mCon.getString(R.string.regist_mailaddress2_status_71002);
			break;
		case 71010:
			this.mDescription = mCon.getString(R.string.status_parameter_error, status);
			break;
		case 71011:
			this.mDescription = mCon.getString(R.string.invalid_userid, status);
			break;
		case 71012:
			this.mDescription = mCon.getString(R.string.regist_mailaddress2_status_71012);
			break;
		case 71013:
			this.mDescription = mCon.getString(R.string.regist_mailaddress2_status_71013);
			break;
		case 71014:
			this.mDescription = mCon.getString(R.string.regist_mailaddress2_status_71014);
			break;
		case 71099:
			this.mDescription = mCon.getString(R.string.status_server_internal_error, status);
			break;
		default:
			this.mDescription = mCon.getString(R.string.status_false, REGIST_MAIL_ADDRESS2, status);
			break;
		}
	}
}
