package net.keyring.bookend.request;

import java.io.IOException;
import java.util.ArrayList;

import net.keyring.bookend.Logput;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.constant.ConstRegist;
import net.keyring.bookend.constant.ConstReq;
import net.keyring.bookend.util.StringUtil;
import net.keyring.bookend.util.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;

/**
 * GetAwsInfoリクエスト処理
 * @author Hamaji
 *
 */
public class GetAwsInfo implements ConstReq, ConstRegist{
	/** Statusコード */
	private String mStatus;
	/** ステータスコードフラグ */
	private int mStatusFlag = -1;
	/** Context */
	private Context			mCon;
	
	/**
	 * GetAwsInfo - Statusコード取得
	 * @return status ステータスコード
	 */
	public String getStatus() {
		return mStatus;
	}
	/**
	 * GetAwsInfo - Statusフラグ取得
	 * @return status ステータスフラグ
	 */
	public int getStatusFlag() {
		return mStatusFlag;
	}
	
	/**
	 * GetAwsInfoリクエスト実行
	 * @return リクエスト・レスポンスに問題がなければtrue,それ以外はfalseを返す
	 */
	public boolean execute(Context con){
		boolean check = false;
		ArrayList<NameValuePair> params = setParams();
		mCon = con;
		try {
			// GetAwsInfoリクエスト
			XmlPullParser parser = request(params);
			// 返ってきたparserを解析
			ArrayList<ArrayList<String>> respanse = Utils.getResponseList(parser);
			getResDetail(respanse);
			// status詳細文字列セット
			check = setStatusFlag(mStatus);
		} catch (IOException e) {
			Logput.e(e.getMessage(), e);
		} catch (XmlPullParserException e) {
			Logput.e(e.getMessage(), e);
		}
		Logput.v("---------------------------------");
		return check;
	}
	
	/**
	 * GetAwsInfoリクエストを行い、受け取った値を返す
	 * @param params パラメータ
	 * @return XmlPullParser
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 */
	private XmlPullParser request(ArrayList<NameValuePair> params) throws IOException, XmlPullParserException{
		XmlPullParser parser = null;
		RequestServer req = new RequestServer(params, GET_AWS_INFO, mCon);
		parser = req.execute();
		return parser;
	}
	
	/**
	 * GetAwsInfo - パラメータセット
	 * @param params
	 * @return params
	 */
	private ArrayList<NameValuePair> setParams(){
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		// サーバ側で無意味なHMACの計算を行う必要が無くなる
		params.add(new BasicNameValuePair(HMAC, "false"));
		return params;
	}
	
	/**
	 * 受け取ったレスポンスxmlを解析して変数にセット<br>
	 * ※[AccessID]と[HMAC]は未使用のため何もしていない
	 * @param xmlの内容をList化したもの
	 */
	private void getResDetail(ArrayList<ArrayList<String>> list){
		if(list != null){
			for(int i=0; i<list.size(); i++){
				ArrayList<String> array = list.get(i);
				if(array.size() >= 2){
					String tagName = array.get(0);
					if(tagName.equals(STATUS)){
						// Statusコード
						this.mStatus = array.get(1);
					}else if(tagName.equals(S3HOST)){
						// S3Host - static変数にセット
						Preferences.sS3Host = array.get(1);
					}else if(tagName.equals(BUCKETNAME)){
						// BucketName - static変数にセット
						Preferences.sBucketName = array.get(1);
					}
					StringUtil.putResponce(tagName, array.get(1));
				}
			}
		}
	}
	
	/**
	 * statusチェック
	 */
	private boolean setStatusFlag(String status){
		try{
			switch(Utils.changeNum(status)){
			case 50000:
				return true;
			case 50010:	// パラメータエラー
				mStatusFlag = GET_AWS_INFO_50010;
				break;
			case 50011:	// サービス停止中
				mStatusFlag = GET_AWS_INFO_50011;
				break;
			case 50012:	// メンテナンス中
				mStatusFlag = GET_AWS_INFO_50012;
				break;
			case 50099:	// サーバ内部エラー
				mStatusFlag = GET_AWS_INFO_50099;
				break;
			default:
				mStatusFlag = GET_AWS_INFO_NG;
				break;
			}
		}catch(NumberFormatException e){
			mStatusFlag = GET_AWS_INFO_NG;
		}catch(NullPointerException e){
			mStatusFlag = GET_AWS_INFO_NG;
		}
		return false;
	}
	
}
