package net.keyring.bookend.request;

import java.io.IOException;
import java.util.ArrayList;

import net.keyring.bookend.Logput;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.constant.ConstDB;
import net.keyring.bookend.constant.ConstReq;
import net.keyring.bookend.db.ContentsDao;
import net.keyring.bookend.db.UpdateContentsDao;
import net.keyring.bookend.util.DateUtil;
import net.keyring.bookend.util.StringUtil;
import net.keyring.bookend.util.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
/**
 * UpdateContentsリクエスト - 最終閲覧日更新(dataCount=1)<br>
 *  contentsテーブルlastAccessDate更新
 * @author Hamaji
 *
 */
public class UpdateContents implements ConstReq {

	/** Context */
	private Context mCon;
	/** Preferencesクラス */
	private Preferences mPref;
	/** AccessCode */
	private String mAccessCode;
	/** LibraryID */
	private String mLibraryID;
	/** ContentsDaoクラス */
	private ContentsDao mContentsDao;
	/** UpdateContentsDaoクラス */
	private UpdateContentsDao mUpdateContentsDao;
	
	/**
	 * コンストラクタ - LibraryID,AccessCode取得・セット
	 * @param con Context
	 */
	public UpdateContents(Context con){
		this.mCon = con;
		if(mPref == null) mPref = new Preferences(con);
		mAccessCode = mPref.getAccessCode();
		mLibraryID = mPref.getLibraryID();
	}
	
	/**
	 * UpdateContents - 最終閲覧日更新
	 * @param downlaodID ダウンロードID
	 * @param lastAccessDate 最終閲覧日
	 */
	public boolean execute(long rowID, String downloadID, String lastAccessDate, String deleteFlag){
		boolean isSuccess = true;
		ArrayList<NameValuePair> params = setParams(downloadID, lastAccessDate, deleteFlag);
		if(params != null){
			try {
				// リクエスト
				XmlPullParser parser = request(params);
				// レスポンスxml解析
				if(parser != null){
					ArrayList<ArrayList<String>> response = Utils.getResponseList(parser);
					String status = getResDetail(response);
					
					if(Utils.equal_str("15000", status)){
						// 正常終了
						Logput.v("UpdateContents Request : Success");
					}else{
						isSuccess = false;
					}
				}else{
					// リクエストエラー
					isSuccess = false;
				}
			} catch (IOException e) {
				isSuccess = false;
				Logput.w(e.getMessage(), e);
			} catch (XmlPullParserException e) {
				isSuccess = false;
				Logput.w(e.getMessage(), e);
			}
		}else{
			isSuccess = false;
		}
		
		if(deleteFlag.equals("false")){
			// リクエストエラーだった場合はupdateContentsテーブルに登録
			if(!isSuccess){
				reqestError(rowID, downloadID, lastAccessDate);
			}
			// contentsテーブル - 最終閲覧日更新
			if(mContentsDao == null) mContentsDao = new ContentsDao(mCon);
			mContentsDao.updateColmun(rowID, ConstDB.LAST_ACCESS_DATE, lastAccessDate);
		}
		Logput.v("---------------------------------");
		return isSuccess;
	}
	
	/**
	 * updateContentsリクエスト処理ができなかった場合はDBに保存する
	 * @param rowID
	 * @param downloadID
	 * @param lastAccessDate
	 */
	private void reqestError(long rowID, String downloadID, String lastAccessDate){
		// エラー
		Logput.i("UpdateContents Request : Error");
		// update_contentsテーブルに保存
		// 最終閲覧日UpdateContents登録
		if(mUpdateContentsDao == null) mUpdateContentsDao = new UpdateContentsDao(mCon);
		long updateContents_rowID = mUpdateContentsDao.getRowID(downloadID);
		mUpdateContentsDao.save(updateContents_rowID, downloadID, lastAccessDate);
		if(mContentsDao == null) mContentsDao = new ContentsDao(mCon);
		mContentsDao.updateColmun(rowID, ConstDB.LAST_ACCESS_DATE, lastAccessDate);
		mContentsDao.updateColmun(rowID, ConstDB.LAST_MODIFY, lastAccessDate);
	}
	
	private ArrayList<NameValuePair> setParams(String downlaodID, String lastAccessDate, String deleteFlag){
		ArrayList<NameValuePair> params = null;
		if(!StringUtil.isEmpty(mLibraryID) && !StringUtil.isEmpty(mAccessCode) && !StringUtil.isEmpty(downlaodID)){
			String dataCount = "1";
			if(StringUtil.isEmpty(lastAccessDate)){
				lastAccessDate = DateUtil.getNowUTC();
			}
			String lastModify = lastAccessDate;
			params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(LIBRARY_ID, mLibraryID));
			params.add(new BasicNameValuePair(ACCESSCODE, mAccessCode));
			params.add(new BasicNameValuePair(DATA_COUNT, dataCount));
			params.add(new BasicNameValuePair(DOWNLOAD_ID + dataCount, downlaodID));
			params.add(new BasicNameValuePair(LAST_ACCESS_DATE + dataCount, lastAccessDate));
			params.add(new BasicNameValuePair(LAST_MODIFY + dataCount, lastModify));
			params.add(new BasicNameValuePair(DELETE + dataCount, deleteFlag));
		}
		return params;
	}
	
	/**
	 * Request
	 * @param action リクエストアクション
	 * @param params パラメータリスト
	 * @return XmlPullParser レスポンスxml
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private XmlPullParser request(ArrayList<NameValuePair> params) throws IOException, XmlPullParserException{
		// リクエスト
		RequestServer req = new RequestServer(params, UPDATE_CONTENTS, mCon);
		return req.execute();
	}
	
	/**
	 * 受け取ったレスポンスxmlを解析
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
						status = array.get(1);
					}
					StringUtil.putResponce(tagName, array.get(1));
				}
			}
		}
		return status;
	}
	
}
