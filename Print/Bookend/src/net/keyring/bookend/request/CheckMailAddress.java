package net.keyring.bookend.request;

import java.io.IOException;
import java.util.ArrayList;

import net.keyring.bookend.Logput;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.constant.ConstReq;
import net.keyring.bookend.util.StringUtil;
import net.keyring.bookend.util.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
/**
 * CheckMailAddressリクエスト
 * @author Hamaji
 *
 */
public class CheckMailAddress implements ConstReq {

	/**
	 * コンストラクタ
	 * @param con Context(Preferencesの登録・取得に必要)
	 */
	public CheckMailAddress(Context con){
		this.mCon = con;
	}
	
	/** Context */
	private Context mCon;
	/** Preferencesクラス */
	private Preferences mPref;
	
	/**
	 * CheckMailAddressリクエスト実行
	 * @return ステータスコード
	 */
	public String execute(){
		String status = null;
		// パラメータ取得
		ArrayList<NameValuePair> params = setParams();
		try {
			XmlPullParser parser = request(params);
			if(parser != null){
				ArrayList<ArrayList<String>> response = Utils.getResponseList(parser);
				status = getResDetail(response);
			}
		} catch (IOException e) {
			Logput.e(e.getMessage(), e);
		} catch (XmlPullParserException e) {
			Logput.e(e.getMessage(), e);
		}
		Logput.v("---------------------------------");
		return status;
	}
	
	/**
	 * CheckMailAddressリクエストを行い、受け取った値を返す
	 * @return XmlPullParser
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 */
	private XmlPullParser request(ArrayList<NameValuePair> params) throws IOException, XmlPullParserException{
		XmlPullParser parser = null;
		RequestServer req = new RequestServer(params, CHECK_MAIL_ADDRESS, mCon);
		parser = req.execute();
		return parser;
	}
	
	private ArrayList<NameValuePair> setParams(){
		// パラメータに必要なUserIDを取得
		if(mPref == null) mPref = new Preferences(mCon);
		String userID = mPref.getUserID();
		
		// パラメータセット
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(USER_ID, userID));
		
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
					}else if(tagName.equals(MAIL_ADDRESS)){
						// MailAddress : settings.xmlに
						if(mPref == null) mPref = new Preferences(mCon);
						mPref.setMailAddress(array.get(1));
					}
					StringUtil.putResponce(tagName, array.get(1));
				}
			}
		}
		return status;
	}
}
