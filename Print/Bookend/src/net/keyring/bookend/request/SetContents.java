package net.keyring.bookend.request;

import java.io.IOException;
import java.util.ArrayList;

import net.keyring.bookend.Logput;
import net.keyring.bookend.R;
import net.keyring.bookend.constant.ConstReq;
import net.keyring.bookend.util.StringUtil;
import net.keyring.bookend.util.Utils;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
/**
 * SetContentsリクエスト処理
 * @author Hamaji
 *
 */
public class SetContents implements ConstReq {
	
	private Context		mCon = null;
	private String		mStatus = null;
	private String		mDescription = null;
	
	/**
	 * SetContentsリクエスト実行
	 * @param con Context
	 * @param リクエストパラメータリスト ArrayList<NameValuePair>
	 * @return SetContentsリクエスト処理が正常終了した場合はtrue,それ以外はfalseを返す
	 */
	public boolean execute(Context con, ArrayList<NameValuePair> params){
		mCon = con;
		boolean check = false;
		try {
			// SetContentsリクエスト
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
		} catch (Exception e){
			Logput.e(e.getMessage(), e);
		}
		Logput.v("---------------------------------");
		return check;
	}
	
	/**
	 * SetContentsリクエストを行い、受け取った値を返す
	 * @param params パラメータ
	 * @return XmlPullParser
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 */
	private XmlPullParser request(ArrayList<NameValuePair> params) throws IOException, XmlPullParserException{
		XmlPullParser parser = null;
		RequestServer req = new RequestServer(params, SET_CONTENTES, mCon);
		parser = req.execute();
		return parser;
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
					if(tagName.equals(STATUS) && mStatus == null){
						mStatus = array.get(1);
					}else if(tagName.equals(DESCRIPTION) && mDescription == null){
						mDescription = array.get(1);
					}
					StringUtil.putResponce(tagName, array.get(1));
				}
			}
		}
		return mStatus;
	}
	
	/**
	 * SetContents ステータスチェック
	 * @param status ステータスコード文字列
	 * @return　正常に処理された場合はtrue,それ以外はfalseを返す
	 */
	private boolean checkStatus(String status){
		try{
			switch(Utils.changeNum(status)){
			case 10000:
				return true;
			case 10010:
				mDescription = mCon.getString(R.string.status_parameter_error);
				break;
			case 10011:
				mDescription = mCon.getString(R.string.library_not_registered, status);
				break;
			case 10012:
				mDescription = mCon.getString(R.string.setcontents_10012);
				break;
			case 10013:
				mDescription = mCon.getString(R.string.invalid_userid, status);
				break;
			case 10099:
				mDescription = mCon.getString(R.string.status_server_internal_error, status);
				break;
			case 001:
				mDescription = mCon.getString(R.string.status_parameter_error);
				break;
			case 002:
				mDescription = mCon.getString(R.string.setcontents_002);
				break;
			case 003:
				mDescription = mCon.getString(R.string.setcontents_003);
				break;
			case 999:
				mDescription = mCon.getString(R.string.status_server_internal_error, status);
				break;
			default:
				mDescription = mCon.getString(R.string.status_false, SET_CONTENTES, status);
				break;
			}
		}catch(NumberFormatException e){
			mDescription = mCon.getString(R.string.status_null, SET_CONTENTES);
		}catch(NullPointerException e){
			mDescription = mCon.getString(R.string.status_null, SET_CONTENTES);
		}
		Logput.i("SetContents Error <" + status + ">");
		return false;
	}

	/**
	 * SetContents - statusコード取得
	 * @return mStatus
	 */
	public String getmStatus() {
		return mStatus;
	}

	/**
	 * SetContents - status詳細取得
	 * @return mDescription
	 */
	public String getmDescription() {
		return mDescription;
	}
	
	
}
