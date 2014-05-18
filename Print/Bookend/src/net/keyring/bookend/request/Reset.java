package net.keyring.bookend.request;

import java.io.IOException;
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
 * Resetリクエスト処理
 * @author Hamaji
 *
 */
public class Reset implements ConstReq {
	/** Resetレスポンスステータス詳細 */
	private String description = null;
	/** Context */
	private Context			mCon;
	
	/**
	 * Resetリクエスト実行
	 * @param con Context
	 * @param libraryID ライブラリーID(必須)
	 * @param resetFlag リセットフラグ(自分も解除する場合は"true",自分以外を解除する場合は"false")
	 * @return Resetリクエスト処理が正常終了した場合はtrue,それ以外はfalseを返す
	 */
	public boolean execute(Context con, String libraryID, boolean resetFlag){
		boolean check = false;
		if(StringUtil.isEmpty(libraryID)) return check;
		
		// パラメータセット
		ArrayList<NameValuePair> params = setParams(libraryID, resetFlag);
		mCon = con;
		try {
			// Resetリクエスト
			XmlPullParser parser = request(params);
			if(parser != null){
				ArrayList<ArrayList<String>> response = Utils.getResponseList(parser);
				String status = getResDetail(response);
				if(Utils.equal_str("22000", status)){
					// 正常終了
					check = true;
				}else{
					// エラー - ダイアログ表示メッセージセット
					setErrorMessage(con, status);
				}
			}
		} catch (NumberFormatException e){
			this.description = con.getString(R.string.status_null, RESET);
			Logput.w(description);
		} catch (NullPointerException e){
			this.description = con.getString(R.string.status_null, RESET);
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
	 * Resetリクエストを行い、受け取った値を返す
	 * @param params パラメータ
	 * @return XmlPullParser
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 */
	private XmlPullParser request(ArrayList<NameValuePair> params) throws IOException, XmlPullParserException{
		XmlPullParser parser = null;
		RequestServer req = new RequestServer(params, RESET, mCon);
		parser = req.execute();
		return parser;
	}
	
	/**
	 * Resetリクエストに必要なパラメータセット
	 * @param libraryID ライブラリID
	 * @param pinFlag PINを送信する場合:true,しない場合:false
	 * @return リクエストパラメータリスト
	 */
	private ArrayList<NameValuePair> setParams(String libraryID, boolean resetFlag){
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(ALL_RESET, Utils.changeStrFlag(resetFlag)));
		params.add(new BasicNameValuePair(LIBRARY_ID, libraryID));
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
					}
					StringUtil.putResponce(tagName, array.get(1));
				}
			}
		}
		return status;
	}
	
	/**
	 * Resetレスポンスチェック
	 * @param statusコード
	 */
	private void setErrorMessage(Context con, String status) throws NumberFormatException,NullPointerException{
		switch(Utils.changeNum(status)){
		case 22010:
			this.description = con.getString(R.string.status_parameter_error, status);
			break;
		case 22011:
			this.description = con.getString(R.string.library_not_registered, status);
			break;
		case 22012:
			this.description = con.getString(R.string.reset_status_22012);
			break;
		case 22013:
			this.description = con.getString(R.string.invalid_userid, status);
			break;
		case 22099:
			this.description = con.getString(R.string.status_server_internal_error, status);
			break;
		default:
			this.description = con.getString(R.string.status_false, RESET, status);
			break;
		}
	}
	
	/**
	 * Resetレスポンスエラー詳細表示文字列を返す
	 * @return Resetレスポンスエラー詳細表示文字列
	 */
	public String getDescription(){
		return description;
	}
}
