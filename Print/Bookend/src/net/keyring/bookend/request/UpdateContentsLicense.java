package net.keyring.bookend.request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.keyring.bookend.Logput;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.bean.BookBeans;
import net.keyring.bookend.constant.ConstReq;
import net.keyring.bookend.db.UpdateContentsDao;
import net.keyring.bookend.db.UpdateLicenseDao;
import net.keyring.bookend.util.DateUtil;
import net.keyring.bookend.util.StringUtil;
import net.keyring.bookend.util.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;

/**
 * UpdateContentsリクエスト,UpdateLicenseリクエスト処理<br>
 * ※ DBに保存されている情報がある場合一斉リクエスト
 * @author Hamaji
 *
 */
public class UpdateContentsLicense implements ConstReq{
	/** Context */
	private Context _con;
	/** Preferencesクラス */
	private Preferences _pref;
	/** UpdateContentsリクエストに成功したrowIDリスト */
	private ArrayList<Long> _rowIdList;
	/** AccessCode */
	private String _accessCode;
	/** LibraryID */
	private String _libraryID;
	/** UpdateContentsDaoクラス */
	private UpdateContentsDao _updateContentsDao;
	/** UpdateLicenseクラス */
	private UpdateLicenseDao _updateLicenseDao;
	
	public UpdateContentsLicense(Context con){
		this._con = con;
		if(_pref == null) _pref = new Preferences(con);
		_accessCode = _pref.getAccessCode();
		_libraryID = _pref.getLibraryID();
	}
	
	/**
	 * UpdateContents,UpdateLicenseリクエスト実行
	 */
	public void execute(){
		if(_libraryID != null && _accessCode != null){
			try {
				// UpdateLicense - パラメータセット
				ArrayList<NameValuePair> updateLicense_params = setParams_updateLicense();
				if(updateLicense_params != null){
					// UpdateLicense - リクエスト
					request(UPDATE_LICENSE, updateLicense_params);
					Logput.v("---------------------------------");
				}
				// UpdateContents - パラメータセット
				ArrayList<NameValuePair> updateContents_params = setParams_updateContents();
				if(updateContents_params != null){
					// UpdateContents - リクエスト
					request(UPDATE_CONTENTS, updateContents_params);
					Logput.v("---------------------------------");
				}
			} catch (IOException e) {
				Logput.e(e.getMessage(), e);
			} catch (XmlPullParserException e) {
				Logput.e(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Request
	 * @param action リクエストアクション
	 * @param params パラメータリスト
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private void request(String action, ArrayList<NameValuePair> params) throws IOException, XmlPullParserException{
		// リクエスト
		RequestServer req = new RequestServer(params, action, _con);
		XmlPullParser parser = req.execute();
		if(parser != null){
			// 返ってきたparserを解析
			ArrayList<ArrayList<String>> respanse = Utils.getResponseList(parser);
			if(respanse != null){
				if(respanse.size() > 0){
					String status = responseAnalysis(respanse);
					if(action == UPDATE_LICENSE){
						check_updateLicense(status);
					}else if(action == UPDATE_CONTENTS){
						check_updateContents(status);
					}
				}
			}
		}
	}
	
	/**
	 * UpdateContents - パラメータセット
	 * @return params パラメータリスト
	 */
	private ArrayList<NameValuePair> setParams_updateContents(){
		ArrayList<NameValuePair> params = null;
		// Updateテーブルからアップデート情報を取得
		List<BookBeans> bookList = getUpdateContents();
		if(bookList != null){
			// UpdateContentsリクエストに成功したrowIDリスト
			_rowIdList = new ArrayList<Long>();
			// DataCount取得
			int dataCount = bookList.size();
			
			if(dataCount > 0){
				params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair(LIBRARY_ID, _libraryID));
				params.add(new BasicNameValuePair(ACCESSCODE, _accessCode));
				params.add(new BasicNameValuePair(DATA_COUNT, String.valueOf(dataCount)));
				
				for(int i=1; i<=dataCount; i++){
					BookBeans book = bookList.get(i-1);
					params.add(new BasicNameValuePair(DOWNLOAD_ID + i, book.getDownload_id()));
					params.add(new BasicNameValuePair(LAST_MODIFY + i, book.getLastModify()));
					String lastAccessDate = book.getLast_access_date();
					if(!StringUtil.isEmpty(lastAccessDate)){
						if(!DateUtil.isUnlimitedDate(lastAccessDate)){
							params.add(new BasicNameValuePair(LAST_ACCESS_DATE + i, lastAccessDate));
						}
					}
					// FIXME 削除フラグは現在未使用
					//if(!StringUtil.isEmpty(deleteFlag)) params.add(new BasicNameValuePair(DELETE_FLAG + i, book.getDeleteFlag()));
					_rowIdList.add(book.getRowId());
				}
			}
			
		}
		return params;
	}
	
	/**
	 * UpdateContentsテーブルから情報を取得
	 * @return UpdateContentsリスト(Book)
	 */
	private List<BookBeans> getUpdateContents(){
		if(_updateContentsDao == null) _updateContentsDao = new UpdateContentsDao(_con);
		return _updateContentsDao.loadList();
	}
	
	/**
	 * UpdateLicenseテーブルから情報を取得
	 * @return UpdateLicenseリスト(Book)
	 */
	private List<BookBeans> getUpdateLicense(){
		if(_updateLicenseDao == null) _updateLicenseDao = new UpdateLicenseDao(_con);
		 return _updateLicenseDao.loadList();
	}
	
	/**
	 * UpdateLicense - パラメータセット
	 * @return params　パラメータ
	 */
	private ArrayList<NameValuePair> setParams_updateLicense(){
		ArrayList<NameValuePair> params = null;
		// UpdateLicenseテーブルから情報一覧取得
		List<BookBeans> bookList = getUpdateLicense();
		if(bookList != null){
			int dataCount = bookList.size();
			if(dataCount > 0){
				// 削除したUpdateLicenseテーブルのrowIDリスト
				List<Long> rowIdList = new ArrayList<Long>();
				params=new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair(LIBRARY_ID, _libraryID));
				params.add(new BasicNameValuePair(ACCESSCODE, _accessCode));
				params.add(new BasicNameValuePair(DATA_COUNT, String.valueOf(dataCount)));
				
				for(int i=1; i<=dataCount; i++){
					BookBeans book = bookList.get(i-1);
					params.add(new BasicNameValuePair(DOWNLOAD_ID + i, book.getDownload_id()));
					String sharedDevice = book.getSharedDevice();
					int browse = book.getBrowse();
					// セットされた値がnull・0以上ならば省略
					if(!StringUtil.isEmpty(sharedDevice)){
						params.add(new BasicNameValuePair(SHARED_DEVICE + i, sharedDevice));
					}
					if(browse > 0){
						params.add(new BasicNameValuePair(BROWSE + i, String.valueOf(browse)));
					}
					rowIdList.add(book.getRowId());
				}
			}
		}
		return params;
	}
	
	/**
	 * 受け取ったレスポンスxmlを解析
	 * @param xmlの内容をList化したもの
	 */
	private String responseAnalysis(ArrayList<ArrayList<String>> list){
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
	
	/**
	 * UpdateLicenseレスポンスチェック TODO status=16001の場合全部DBに残したままでいいのか
	 * @return 処理が成功した場合はtrue(UpdateLicenseテーブル削除),その他はfalseを返す
	 */
	private void check_updateLicense(String status){
		if(Utils.equal_str("16000", status)){
			if(_updateLicenseDao == null) _updateLicenseDao = new UpdateLicenseDao(_con);
			_updateLicenseDao.delete(-1);
		}else{
			Logput.i("UpdateLicense ERROR.");
		}
	}
	
	/**
	 * UpdateContentsレスポンスチェック TODO status=15001の場合全部DBに残したままでいいのか
	 * @return 処理が成功した場合はtrue(UpdateContentsテーブル削除),その他はfalseを返す
	 */
	private void check_updateContents(String status){
		if(Utils.equal_str("15000", status)){
			if(_updateContentsDao == null) _updateContentsDao = new UpdateContentsDao(_con);
			_updateContentsDao.delete(-1);
		}else{
			Logput.i("UpdateContents ERROR.");
		}
	}

}
