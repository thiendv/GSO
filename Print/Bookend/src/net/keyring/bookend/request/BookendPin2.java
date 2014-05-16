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
 * BookendPin2リクエスト処理
 * @author Hamaji
 *
 */
public class BookendPin2 implements ConstReq {
	
	/** Preferencesクラス */
	private Preferences pref = null;
	/** PINにヒモ付されたuserID */
	private String new_userID = null;
	/** status詳細 */
	private String description = null;
	/** Context */
	private Context			mCon;
	
	/**
	 * BookendPin2リクエスト実行
	 * @param con Context
	 * @param pin 入力されたPINコード
	 * @return BookendPin2リクエスト、UserIDチェックが正常に終わった場合はtrue,それ以外はfalseを返す
	 */
	public boolean execute(Context con, String pin){
		boolean check = false;
		
		// パラメータセット
		ArrayList<NameValuePair> params = setParams(con, pin);
		mCon = con;
		
		// BookendMailリクエスト
		XmlPullParser parser;
		try {
			parser = request(params);
			if(parser != null){
				ArrayList<ArrayList<String>> response = Utils.getResponseList(parser);
				String status = getResDetail(con, response);
				if(Utils.equal_str("60000", status)){
					// OK - UserIDチェック
					check = checkUserID(con);
				}else{
					// NG - エラー時表示する文字列セット
					bookendPin2_statusCheck(con,status);
				}
			}
		} catch (NumberFormatException e){
			description = con.getString(R.string.status_null, BOOKEND_PIN2);
			Logput.w(description);
		} catch(NullPointerException e){
			description = con.getString(R.string.status_null, BOOKEND_PIN2);
		} catch (IOException e) {
			Logput.e(e.getMessage(), e);
		} catch (XmlPullParserException e) {
			Logput.e(e.getMessage(), e);
		}
		Logput.v("---------------------------------");
		return check;
	}
	
	/**
	 * BookendPin2リクエストを行い、受け取った値を返す
	 * @param params リクエストパラメータ
	 * @return XmlPullParser
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 */
	private XmlPullParser request(ArrayList<NameValuePair> params) throws IOException, XmlPullParserException{
		XmlPullParser parser = null;
		RequestServer req = new RequestServer(params, BOOKEND_PIN2, mCon);
		parser = req.execute();
		return parser;
	}
	
	/**
	 * BookendPin2リクエストに必要なパラメータセット
	 * @param pin 入力されたPINコード
	 * @return リクエストパラメータリスト
	 */
	private ArrayList<NameValuePair> setParams(Context con, String pin){
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		// PINかUserIDどちらか片方が必須(両方指定時はエラー)
		if(!StringUtil.isEmpty(pin)){
			params.add(new BasicNameValuePair(PIN, pin));
		}else{
			if(pref == null) pref = new Preferences(con);
			String userID = pref.getUserID();
			params.add(new BasicNameValuePair(USER_ID, userID));
		}
		
		if(pref == null) pref = new Preferences(con);
		String checkCode = pref.getCheckCode();
		if(!StringUtil.isEmpty(checkCode)){
			params.add(new BasicNameValuePair(CHECK_CODE, checkCode));
		}
		
		String ownerId = con.getString(R.string.owner_id);
		if(ownerId != null){
			params.add(new BasicNameValuePair(OWNER_ID_L, ownerId));
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
					}else if(tagName.equals(USER_ID)){
						// PINにヒモ付されたuserID
						new_userID = array.get(1);
					}
					StringUtil.putResponce(tagName, array.get(1));
				}
			}
		}
		return status;
	}
	
	/**
	 * PINにヒモ付されたUserIDチェック、必要な場合はChangeUserIDで変更
	 * @param con Context
	 */
	private boolean checkUserID(Context con){
		boolean check = false;
		// PINに紐付けされているUserIDと保存していたUserIDが同じならばメールアドレスを保存
		if(pref == null) pref = new Preferences(con);
		String org_userID = pref.getUserID();
		String new_userID = this.new_userID;
		if(org_userID.equals(new_userID)){
			check = true;
		}else{
			// UserIDが異なっていた場合は続けて<メールアドレスの登録3: 使用するユーザIDの切り替え処理＞を行う
			// OK - ChangeUserIDリクエストを行う
			ChangeUserId changeUserId = new ChangeUserId();
			check = changeUserId.execute(con, new_userID, org_userID);
		}
		// UserIDチェックが正常に終了した場合はメールアドレス登録、一時保存していたアドレス・CheckCode削除
		if(check){
			pref.setMailAddress(pref.getMailAddress_temporary());
			pref.setMailAddress_temporary(null);
			pref.setCheckCode(null);
		}
		return check;
	}
	
	/**
	 * BookendPin2レスポンスチェック
	 * @param statusコード
	 */
	private void bookendPin2_statusCheck(Context con, String status) throws NumberFormatException, NullPointerException{
		switch(Utils.changeNum(status)){
		case 60010:
			description = con.getString(R.string.bookendpin2_status_60011);
			break;
		case 60012:
			description = con.getString(R.string.bookendpin2_status_60011);
			break;
		case 60013:
			description = con.getString(R.string.bookendpin2_status_60013);
			break;
		case 60014:
			description = con.getString(R.string.bookendpin2_status_60014);
			break;
		case 60015:
			description = con.getString(R.string.bookendpin2_status_60015);
			break;
		case 60016:
			description = con.getString(R.string.bookendpin2_status_60016);
			break;
		case 60099:
			description = con.getString(R.string.status_server_internal_error, status);
			break;
		default:
			description = con.getString(R.string.status_false, BOOKEND_PIN2, status);
			break;
		}
	}
	
	/**
	 * BookendPin2レスポンスエラー詳細表示文字列を返す
	 * @return BookendPin2レスポンスエラー詳細表示文字列
	 */
	public String getDescription(){
		return this.description;
	}
}
