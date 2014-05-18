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
 * BookendMailリクエスト処理
 * @author Hamaji
 *
 */
public class BookendMail implements ConstReq{
	
	/** Preferencesクラス */
	private Preferences		mPref = null;
	/** Context */
	private Context			mCon;
	/** BookendMailレスポンスステータス詳細 */
	private String			mDescription = null;
	
	/**
	 * BookendMailリクエスト実行
	 * @param con Context
	 * @param mailAddress メールアドレス
	 * @param pinFlag PIN送信フラグ
	 * @return ステータスコード
	 */
	public boolean execute(Context con, String mailAddress, boolean pinFlag){
		boolean check = false;
		mCon = con;
		if(!StringUtil.isEmpty(mailAddress)){
			// パラメータセット
			ArrayList<NameValuePair> params = setParams(mailAddress, pinFlag);
			// BookendMailリクエスト
			XmlPullParser parser;
			try {
				parser = request(params);
				if(parser != null){
					ArrayList<ArrayList<String>> response = Utils.getResponseList(parser);
					String status = getResDetail(response);
					check = bookendMail_statusCheck(status);
					if(!check){
						// NG - static変数に一時保存してあったアドレスをnullに
						if(mPref == null) mPref = new Preferences(con);
						mPref.setMailAddress_temporary(null);
					}
				}
			} catch(NumberFormatException e){
				mDescription = con.getString(R.string.status_null, BOOKEND_MAIL);
				Logput.w(mDescription);
			} catch(NullPointerException e){
				mDescription = con.getString(R.string.status_null, BOOKEND_MAIL);
				Logput.w(mDescription);
			} catch (IOException e) {
				Logput.e(e.getMessage(), e);
			} catch (XmlPullParserException e) {
				Logput.e(e.getMessage(), e);
			}
		}
		Logput.v("---------------------------------");
		return check;
	}
	
	/**
	 * BookendMailリクエストを行い、受け取った値を返す
	 * @param params パラメータ
	 * @return XmlPullParser
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 */
	private XmlPullParser request(ArrayList<NameValuePair> params) throws IOException, XmlPullParserException{
		XmlPullParser parser = null;
		RequestServer req = new RequestServer(params, BOOKEND_MAIL, mCon);
		parser = req.execute();
		return parser;
	}
	
	/**
	 * BookendMailリクエストに必要なパラメータセット
	 * @param mailAddress メールアドレス
	 * @param pinFlag PINを送信する場合:true,しない場合:false
	 * @return リクエストパラメータリスト
	 */
	private ArrayList<NameValuePair> setParams(String mailAddress, boolean pinFlag){
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(MAIL_ADDRESS, mailAddress));
		params.add(new BasicNameValuePair(PIN, Utils.changeStrFlag(pinFlag)));
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
					}else if(tagName.equals(CHECK_CODE)){
						// checkCode : settings.xmlに一時保存
						if(mPref == null) mPref = new Preferences(mCon);
						mPref.setCheckCode(array.get(1));
					}
					StringUtil.putResponce(tagName, array.get(1));
				}
			}
		}
		return status;
	}
	
	/**
	 * BookendMailレスポンスチェック
	 * @param statusコード
	 * @return OKの場合はtrue,それ以外はfalseを返す
	 */
	private boolean bookendMail_statusCheck(String status)throws NumberFormatException,NullPointerException{
		switch(Utils.changeNum(status)){
		case 73000:
			return true;
		case 73010:
			mDescription = mCon.getString(R.string.status_parameter_error, status);
			break;
		case 73011:
			mDescription = mCon.getString(R.string.bookendmail_status_73011);
			break;
		case 73012:
			mDescription = mCon.getString(R.string.bookendmail_status_73012);
			break;
		case 73013:
			mDescription = mCon.getString(R.string.bookendmail_status_73013);
			break;
		case 73099:
			mDescription = mCon.getString(R.string.status_server_internal_error, status);
			break;
		default:
			mDescription = mCon.getString(R.string.status_false, BOOKEND_MAIL, status);
			break;
		}
		return false;
	}
	
	/**
	 * BookendMailレスポンスエラー詳細表示文字列を返す
	 * @return BookendMailレスポンスエラー詳細表示文字列
	 */
	public String getDescription(){
		return mDescription;
	}
	
}
