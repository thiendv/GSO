package net.keyring.bookend.action;

import java.io.IOException;
import java.util.ArrayList;

import net.keyring.bookend.Logput;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.R;
import net.keyring.bookend.constant.ConstRegist;
import net.keyring.bookend.constant.ConstReq;
import net.keyring.bookend.request.CheckMailAddress;
import net.keyring.bookend.request.GetAwsInfo;
import net.keyring.bookend.request.GetContents;
import net.keyring.bookend.request.RequestServer;
import net.keyring.bookend.request.UpdateContentsLicense;
import net.keyring.bookend.util.DateUtil;
import net.keyring.bookend.util.StringUtil;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;

/**
 * Actionクラス - Web書庫に遷移する前チェック
 * @author Hamaji
 *
 */
public class RegistConfirmAction implements ConstReq, ConstRegist{

	/** Context */
	private Context mCon;
	/** Pref */
	private Preferences mPref;
	/** GetContents */
	private GetContents mGetContents;
	
	public RegistConfirmAction(Context con){
		this.mCon = con;
	}
	
	/**
	 * メールアドレスが登録されているかどうかチェック
	 * @return メールアドレスが登録されている場合はtrue、それ以外はfalseを返す
	 */
	public boolean isRegist(){
		boolean isRegist = false;
		if(mPref == null) mPref = new Preferences(mCon);
		String mailAddress = mPref.getMailAddress();
		if(StringUtil.isEmpty(mailAddress)){
			// mailAddressがsettings.xmlに保存されていない場合はサーバーから取得する
			CheckMailAddress checkMailAddress = new CheckMailAddress(mCon);
			checkMailAddress.execute();
			mailAddress = Preferences.sMailAddress;
			if(!StringUtil.isEmpty(mailAddress)){
				isRegist = true;
			}
		}else{
			isRegist = true;
		}
		return isRegist;
	}
	
	/**
	 * S3チェック - GetAWSinfoリクエスト
	 */
	public int getAwsInfo() {
		int status = -1;
		GetAwsInfo getawsinfo = new GetAwsInfo();
		if(!getawsinfo.execute(mCon)){
			status = getawsinfo.getStatusFlag();
		}
		return status;
	}
	
	/**
	 * UpdateContentsリクエスト(UpdateLicenseレコードにに保存されているものがあった場合はそれをUpdateLicenseリクエストする)
	 */
	public void updateContents(){
		UpdateContentsLicense updateContents = new UpdateContentsLicense(mCon);
    	updateContents.execute();
	}
	
	/**
	 * Getcontents - パラメータ取得
	 * @param dataMax
	 * @return
	 */
	public ArrayList<NameValuePair> setParams(String dataMax){
		if(mGetContents == null) mGetContents = new GetContents(mCon);
		return mGetContents.getParams(dataMax);
	}
	
	/**
	 * GetContentsリクエスト
	 * @param dataMax　取得する更新情報の最大数(省略時無制限)
	 * @return XmlPullParser
	 */
	public XmlPullParser getContents(ArrayList<NameValuePair> params){
		XmlPullParser parser = null;
		RequestServer post = new RequestServer(params, GET_CONTENTS, mCon);
		try {
			parser = post.execute();
		} catch (IOException e) {
			Logput.w(e.getMessage(), e);
		} catch (XmlPullParserException e) {
			Logput.w(e.getMessage(), e);
		}
		return parser;
	}
	
	/**
	 * Getcontents - parser解析
	 * @param parser XmlPullParser
	 * @return GetContentsレスポンスステータスコード
	 */
	public String getResponseList(XmlPullParser parser){
		if(mGetContents == null) mGetContents = new GetContents(mCon);
		return  mGetContents.getContentsResponseList(parser);
	}
	
	/**
	 * GetContentsレスポンス結果処理
	 * @param status ステータスコード
	 * @param description ステータス詳細
	 * @param offset オフセット
	 * @param contentsNewList 新規登録コンテンツ情報リスト
	 * @param contentsUpdateList 更新コンテンツ情報リスト
	 * @param licenseList ライセンス情報リスト
	 * @return エラーの場合はエラーメッセージを返す
	 */
	public int checkGetContentsStatus(String status){
		int actionFlag = SEND_WEB_BOOK_SHELF;
		String description = mGetContents.getDescription();
		String offset = mGetContents.getOffset();
		ArrayList<ArrayList<NameValuePair>> contentsNewList = mGetContents.getContentsNewList();
		ArrayList<ArrayList<NameValuePair>> contentsUpdateList = mGetContents.getContentsUpdateList();
		ArrayList<ArrayList<NameValuePair>> licenseList = mGetContents.getLicenseList();
		try{
			if(status.equals("11000") || status.equals("11001") || status.equals("11002")){
				// GetContents - OK
				if(mPref == null) mPref = new Preferences(mCon);
				// Offset
				if(!StringUtil.isEmpty(offset)){
					mPref.setOffset(offset);
				}
				// サーバと同期した日時(UTC)を更新
				String last_sync_date = DateUtil.getNowUTC();
				Logput.v("Preferences set last sync date = " + last_sync_date);
				mPref.setLastSyncDate_book(last_sync_date);
				
				// 取得したデータをDBに保存
				GetContentsAction saveGetContents = new GetContentsAction(mCon);
				if(!saveGetContents.saveDB_GetContents(contentsNewList, contentsUpdateList, licenseList)){
					actionFlag = DB_ERROR;
					Logput.i(mCon.getString(R.string.db_error));
				}
			}else{
				if(status.equals("11011")){
					// 他のクライアントでリセットされた場合
					Logput.i("[GetContents] status:" + status + " /description:" + description);
					actionFlag = RESET_FLAG;
				}else{
					Logput.d("[GetContents] status:" + status + " /description:" + description);
					actionFlag = GET_CONTENTS_ERROR;
				}
			}
		}catch(NullPointerException e){
			Logput.w("[GetContents] NULL ..." + mCon.getString(R.string.getcontents_error), e);
			actionFlag = GET_CONTENTS_ERROR;
		}
		return actionFlag;
	}
	
}
