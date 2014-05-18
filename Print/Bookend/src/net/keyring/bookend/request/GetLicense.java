package net.keyring.bookend.request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.keyring.bookend.Logput;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.bean.BookBeans;
import net.keyring.bookend.constant.ConstReq;
import net.keyring.bookend.db.ContentsDao;
import net.keyring.bookend.util.StringUtil;
import net.keyring.bookend.util.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
/**
 * GetLicenseリクエスト処理
 * @author Hamaji
 *
 */
public class GetLicense implements ConstReq {
	
	/** key */
	private String key = null;
	/** sharedDevice */
	private String sharedDevice = null;
	/** browse */
	private String browse = null;
	/** print */
	private String print = null;
	/** invalidPlatform */
	private String invalidPlatform = null;
	/** expiryDate */
	private String expiryDate = null;
	/** Context */
	private Context			mCon;
	
	/**
	 * GetLicenseリクエスト実行
	 * @param con Context
	 * @param downloadID ダウンロードID(必須)
	 * @return 登録したコンテンツ情報 Book
	 */
	public BookBeans execute(Context con, String downloadID){
		BookBeans book = null;
		if(StringUtil.isEmpty(downloadID)) return null;
		
		// パラメータセット
		ArrayList<NameValuePair> params = setParams(con, downloadID);
		mCon = con;
		try {
			// GetLicenseリクエスト
			XmlPullParser parser = request(params);
			if(parser != null){
				ArrayList<ArrayList<String>> response = Utils.getResponseList(parser);
				String status = getResDetail(response);
				if(Utils.equal_str("19000", status)){
					// 正常終了 - DB登録
					book = updateLicense(con, downloadID);
					Logput.v("DB UPDATE : SUCCESS ... GetLicense");
				}
			}
		} catch (IOException e) {
			Logput.e(e.getMessage(), e);
		} catch (XmlPullParserException e) {
			Logput.e(e.getMessage(), e);
		}
		Logput.v("---------------------------------");
		return book;
	}
	
	/**
	 * GetLicenseリクエストを行い、受け取った値を返す
	 * @param params パラメータ
	 * @return XmlPullParser
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 */
	private XmlPullParser request(ArrayList<NameValuePair> params) throws IOException, XmlPullParserException{
		XmlPullParser parser = null;
		RequestServer req = new RequestServer(params, GET_LICENSE, mCon);
		parser = req.execute();
		return parser;
	}
	
	/**
	 * GetLicenseリクエストに必要なパラメータセット
	 * @param con Context
	 * @param downloadID ダウンロードID
	 * @return リクエストパラメータリスト
	 */
	private ArrayList<NameValuePair> setParams(Context con, String downloadID){
		ArrayList<NameValuePair> params = null;
		Preferences pref = new Preferences(con);
		String accessCode = pref.getAccessCode();
		String libraryID = pref.getLibraryID();
		if(!StringUtil.isEmpty(accessCode) && !StringUtil.isEmpty(libraryID)){
			params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(DOWNLOAD_ID, downloadID));
			params.add(new BasicNameValuePair(LIBRARY_ID, libraryID));
			params.add(new BasicNameValuePair(ACCESSCODE, accessCode));
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
					String value = array.get(1);
					if(tagName.equals(STATUS)){
						status = value;
					}else if(tagName.equals(KEY)){
						this.key = value;
					}else if(tagName.equals(SHARED_DEVICE)){
						this.sharedDevice = value;
					}else if(tagName.equals(BROWSE)){
						this.browse = value;
					}else if(tagName.equals(PRINT)){
						this.print = value;
					}else if(tagName.equals(INVALID_PLATFORM)){
						this.invalidPlatform = value;
					}else if(tagName.equals(EXPIRY_DATE)){
						this.expiryDate = value;
					}
					StringUtil.putResponce(tagName, array.get(1));
				}
			}
		}
		return status;
	}
	
	/**
	 * Contentsテーブルのライセンス情報を更新する
	 * @param ステータスコード
	 */
	public BookBeans updateLicense(Context con, String downloadID){
		// 共有台数
		List<String> sharedDeviceList = StringUtil.parseStr(this.sharedDevice, '/');
		int sharedDevice_m = Utils.changeNum(sharedDeviceList.get(0));
		int sharedDevice_d = Utils.changeNum(sharedDeviceList.get(1));
		// 閲覧制限回数
		List<String> browseList = StringUtil.parseStr(this.browse, '/');
		int browse_m = Utils.changeNum(browseList.get(0));
		int browse_d = Utils.changeNum(browseList.get(1));
		// 印刷制限回数
		List<String> printList = StringUtil.parseStr(this.print, '/');
		int print_m = Utils.changeNum(printList.get(0));
		int print_d = Utils.changeNum(printList.get(1));
		// Contentsテーブル更新
		ContentsDao dao = new ContentsDao(con);
		BookBeans book = dao.setBook(downloadID, this.key, sharedDevice_m, sharedDevice_d, browse_m, browse_d,
				print_m, print_d, this.invalidPlatform, this.expiryDate);
		book = dao.save(book);
		return book;
	}
}
