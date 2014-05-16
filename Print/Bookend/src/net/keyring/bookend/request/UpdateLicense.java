package net.keyring.bookend.request;

import java.io.IOException;
import java.util.ArrayList;

import net.keyring.bookend.Logput;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.R;
import net.keyring.bookend.constant.Const;
import net.keyring.bookend.constant.ConstDB;
import net.keyring.bookend.constant.ConstReq;
import net.keyring.bookend.db.ContentsDao;
import net.keyring.bookend.db.UpdateLicenseDao;
import net.keyring.bookend.util.StringUtil;
import net.keyring.bookend.util.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
/**
 * UpdateLicenseリクエスト処理  ※Printはないものとした処理をしている
 * @author Hamaji
 *
 */
public class UpdateLicense implements ConstReq {
	
	/** ContentsDaoクラス */
	private ContentsDao			contentsDao = null;
	/** UpdateLicenseクラス */
	private UpdateLicenseDao	updateLicenseDao = null;
	/** Context */
	private Context				mCon;
	/** ステータス詳細 */
	private String				mDescription = null;
	
	/**
	 * UpdateLicense - レスポンスステータス詳細を返す
	 * @return レスポンスステータス詳細
	 */
	public String getDescription(){
		return mDescription;
	}
	
	/**
	 * UpdateLicenseリクエスト実行 - 共有台数増減
	 * @param con Context
	 * @param rowID テーブルID
	 * @param downloadID ダウンロードID(必須)
	 * @param sharedDevice_m
	 * @param sharedDevice_flag ダウンロード又は削除("true":DL or "false":削除)
	 * @return　UpdateLicenseリクエスト処理が正常終了した場合はtrue,それ以外はfalseを返す
	 */
	public boolean execute(Context con, long rowID, String downloadID,
			int sharedDevice_m, boolean sharedDevice_flag){
		mCon = con;
		boolean check = execute(con, downloadID, Utils.changeStrFlag(sharedDevice_flag), 0);
		if(check){
			// 正常終了 - contentsテーブルのライセンス情報を更新
			if(contentsDao == null) contentsDao = new ContentsDao(con);
			if(sharedDevice_flag){
				// ダウンロード
				sharedDevice_m = sharedDevice_m - 1;
				// Web書庫からDL成功時：ダウンロードステータスを完了にする（途中でタスクが落ちた場合を考えてUpdatelicense後すぐにDBを更新）
				contentsDao.updateColmun(rowID, ConstDB.DL_STATUS, Const.STATUS_DL_COMPLETE_WEB);
			}else{
				// 削除
				sharedDevice_m = sharedDevice_m + 1;
			}
			if(contentsDao.updateColmun(rowID, ConstDB.SHARED_DEVICE_M, sharedDevice_m)){
				Logput.v("Update contents table : SUCCESS ... UpdateLicense request");
			}else{
				Logput.i("Update contents table : FAIL ... UpdateLicense request");
			}
		}else{
			// リクエストエラー - UpdateLicenseテーブルに保存(共有台数変更登録)
			if(updateLicenseDao == null) updateLicenseDao = new UpdateLicenseDao(con);
			long update_rowID = updateLicenseDao.getRowID(downloadID);
			updateLicenseDao.save(update_rowID, downloadID, Utils.changeStrFlag(sharedDevice_flag));
		}
		Logput.v("---------------------------------");
		return check;
	}
	
	/**
	 * UpdateLicenseリクエスト実行 - 閲覧回数減
	 * @param con Context
	 * @param downloadID ダウンロードID(必須)
	 * @param browse_m DBに保存されている残り閲覧回数
	 * @param browse 残り閲覧回数の変化("-1"以下の数値)
	 * @return　UpdateLicenseリクエスト処理が正常終了した場合はtrue,それ以外はfalseを返す
	 */
	public boolean execute(Context con, long rowID, String downloadID, int browse_m, int browse){
		mCon = con;
		boolean check = execute(con, downloadID, null, browse);
		if(check){
			// 正常終了 - contentsテーブルのライセンス情報を更新
			browse_m = browse_m + browse;
			if(contentsDao == null) contentsDao = new ContentsDao(con);
			if(contentsDao.updateColmun(rowID, ConstDB.BROWSE_M, browse_m)){
				Logput.v("UPDATE DB : SUCCESS ... UpdateLicense");
			}else{
				Logput.i("UPDATE DB : FAIL ... UpdateLicense");
			}
		}else{
			// リクエストエラー - UpdateLicenseテーブルに保存(閲覧回数更新)
			if(updateLicenseDao == null) updateLicenseDao = new UpdateLicenseDao(con);
			updateLicenseDao.save(rowID, downloadID, browse);
		}
		Logput.v("---------------------------------");
		return check;
	}
	
	/**
	 * UpdateLicenseリクエスト実行 - printはないものとする
	 * @param con Context
	 * @param downloadId ダウンロードID(必須)
	 * @param sharedDevice ダウンロード又は削除("true":DL or "false":削除 or null)
	 * @param browse 残り閲覧回数の変化("-1"以下の数値...0の場合は省略)
	 * @return UpdateLicenseリクエスト処理が正常終了した場合はtrue,それ以外はfalseを返す
	 */
	private boolean execute(Context con, String downloadID, String sharedDevice, int browse){
		mCon = con;
		boolean check = false;
		if(!Utils.isConnected(con)){
			// オフラインの場合はfalseを返す
			return check;
		}
		
		// パラメータセット
		ArrayList<NameValuePair> params = setParams(con, downloadID, sharedDevice, browse);
		try {
			// UpdateLicenseリクエスト
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
		}
		return check;
	}
	
	/**
	 * UpdateLicenseリクエストを行い、受け取った値を返す
	 * @param params パラメータ
	 * @return XmlPullParser
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 */
	private XmlPullParser request(ArrayList<NameValuePair> params) throws IOException, XmlPullParserException{
		XmlPullParser parser = null;
		RequestServer req = new RequestServer(params, UPDATE_LICENSE, mCon);
		parser = req.execute();
		return parser;
	}
	

	/**
	 * UpdateLicenseリクエストに必要なパラメータセット
	 * @param con Context
	 * @param downloadId ダウンロードID(必須)
	 * @param sharedDevice ダウンロード又は削除("true":DL or "false":削除)
	 * @param browse 残り閲覧回数の変化("-1"以下の数値...0の場合は省略)
	 * @return リクエストパラメータリスト
	 */
	private ArrayList<NameValuePair> setParams(Context con, String downloadID, String sharedDevice, int browse){
		ArrayList<NameValuePair> params = null;
		// LibraryID,AccessCode,downloadID,dataCount取得(必須)
		Preferences pref = new Preferences(con);
		String libraryID = pref.getLibraryID();
		String AccessCode = pref.getAccessCode();
		String dataCount = "1";
		if(!StringUtil.isEmpty(libraryID) && !StringUtil.isEmpty(AccessCode) && !StringUtil.isEmpty(downloadID)){
			params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(LIBRARY_ID, libraryID));
			params.add(new BasicNameValuePair(ACCESSCODE, AccessCode));
			params.add(new BasicNameValuePair(DATA_COUNT, dataCount));
			params.add(new BasicNameValuePair(DOWNLOAD_ID + dataCount, downloadID));
			if(!StringUtil.isEmpty(sharedDevice)){
				params.add(new BasicNameValuePair(SHARED_DEVICE + dataCount, sharedDevice));
			}
			if(browse < 0){
				params.add(new BasicNameValuePair(BROWSE + dataCount, String.valueOf(browse)));
			}
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
					if(tagName.equals(STATUS) && status == null){
						status = array.get(1);
					}else if(tagName.equals(DESCRIPTION) && mDescription == null){
						mDescription = array.get(1);
					}
					StringUtil.putResponce(tagName, array.get(1));
				}
			}
		}
		return status;
	}
	
	private boolean checkStatus(String status){
		try{
			switch(Utils.changeNum(status)){
			case 16000:
				Logput.d("UpdateLicense success.");
				return true;
			case 16011:
				mDescription = mCon.getString(R.string.library_not_registered, status);
				break;
			case 16012:
				mDescription = mCon.getString(R.string.updatelicense_16012);
				break;
			case 16099:
				mDescription = mCon.getString(R.string.status_server_internal_error, status);
				break;
			case 001:
				mDescription = mDescription + " : ErrorCode " +  16001 + "_" + status;
				break;
			case 002:
				mDescription = mDescription + " : ErrorCode " +  16001 + "_" + status;
				break;
			case 003:
				mDescription =  mDescription + " : ErrorCode " +  16001 + "_" + status;
				break;
			case 004:
				mDescription =  mDescription + " : ErrorCode " +  16001 + "_" + status;
				break;
			case 005:
				mDescription =  mDescription + " : ErrorCode " +  16001 + "_" + status;
				break;
			case 006:
				mDescription =  mDescription + " : ErrorCode " +  16001 + "_" + status;
				break;
			case 007:
				mDescription =  mDescription + " : ErrorCode " +  16001 + "_" + status;
				break;
			case 999:
				mDescription = mCon.getString(R.string.status_server_internal_error, status);
				break;
			default:
				mDescription = mCon.getString(R.string.status_false, UPDATE_LICENSE, status);
				break;
			}
		}catch(NumberFormatException e){
			mDescription = mCon.getString(R.string.status_false, UPDATE_LICENSE, status);
		} catch(NullPointerException e){
			mDescription = mCon.getString(R.string.status_null, UPDATE_LICENSE);
		}
		Logput.d("UpdateLicense error : " + mDescription);
		return false;
	}
}
