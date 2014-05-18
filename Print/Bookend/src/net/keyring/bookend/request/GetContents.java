package net.keyring.bookend.request;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;

import net.keyring.bookend.Logput;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.constant.ConstReq;
import net.keyring.bookend.util.StringUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;

/**
 * GetContentsリクエスト
 * @author Hamaji
 *
 */
public class GetContents implements ConstReq{
	
	/** Context */
	private Context mCon;
	/** Pref */
	private Preferences mPref;
	/** statusコード */
	private String mStatus;
	/** status詳細 */
	private String mDescription;
	/** offset */
	private String mOffset;
	/** ContentsNew List */
	private ArrayList<ArrayList<NameValuePair>> mContentsNewList = null;
	/** ContentsUpdate List */
	private ArrayList<ArrayList<NameValuePair>> mContentsUpdateList = null;
	/** License List */
	private ArrayList<ArrayList<NameValuePair>> mLicenseList = null;
	
	public GetContents(Context con){
		this.mCon = con;
	}

	/**
	 * パラメータセット
	 * @param dataMax 取得する更新情報の最大数(省略時無制限)
	 * @return パラメータリスト
	 */
	public ArrayList<NameValuePair> getParams(String dataMax){
		if(mPref == null) mPref = new Preferences(mCon);
		String libraryID = mPref.getLibraryID();
		String accessCode = mPref.getAccessCode();
		String userID = mPref.getUserID();
		// ライブラリID、アクセスコード、ユーザーIDは必須
		if(StringUtil.isEmpty(libraryID) || StringUtil.isEmpty(accessCode) || StringUtil.isEmpty(userID)){
			return null;
		}else{
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(LIBRARY_ID, libraryID));
			params.add(new BasicNameValuePair(ACCESSCODE, accessCode));
			params.add(new BasicNameValuePair(USER_ID, userID));
//			if(mCon.getString(R.string.webshelf_flag).equals("1")){
//				// カスタムWEB書庫の場合はパラメータにオーナーＩＤをセット
//				params.add(new BasicNameValuePair(OWNER_ID, mCon.getString(R.string.owner_id)));
//			}
			// レスポンスをdeflect圧縮するかどうかのフラグ(true:圧縮/false:圧縮しない（省略時）)...true固定
			params.add(new BasicNameValuePair(COMPRESS, "true"));
			// 最後にサーバと同期した日時(UTC)をsettings.xmlから取得
			String last_sync_date = mPref.getLastSyncDate_book();
			Logput.v("Last sync date = " + last_sync_date);
			if(!StringUtil.isEmpty(last_sync_date)){
				params.add(new BasicNameValuePair(LAST_SYNC_DATE,last_sync_date));
			}
			if(!StringUtil.isEmpty(dataMax)){
				params.add(new BasicNameValuePair(DATA_MAX, dataMax));
				// 前回のリクエストレスポンスで返されたOffsetを取得
				String offset =  mPref.getOffset();
				if(!StringUtil.isEmpty(offset)) params.add(new BasicNameValuePair(OFF_SET,offset));
			}
			return params;
		}
	}
	
	/**
	 * 帰ってきたxmlの最初のタグ名から、response解析振り分け - GetContents専用
	 * @return リクエストステータスコード
	 */
	public String getContentsResponseList(XmlPullParser parser){
		ArrayList<ArrayList<NameValuePair>> list = null;
		try{
			parser.next();
			// xmlを読み終わるまで回す
			list = new ArrayList<ArrayList<NameValuePair>>();
			getTagValue(parser, list, "");
		}catch(NullPointerException e){
			Logput.e(e.getMessage(), e);
			return null;
		}catch(XmlPullParserException e){
			Logput.e(e.getMessage(), e);
			return null;
		}catch(IOException e){
			Logput.e(e.getMessage(), e);
			return null;
		}
		// SDカード直下に「LogCat.txt」がある場合のみLogCatに出力する
		if(Logput.isLogcat){
		    for(ArrayList<NameValuePair> namevalueList : list){
		    	Logput.v("------------------------");
		    	for(NameValuePair namevalue : namevalueList){
		    		StringUtil.putResponce(namevalue.getName(), namevalue.getValue());
		    	}
		    }
		}
		return getContentsResponse(list);
	}
	
	/**
	 * 受け取ったレスポンスxmlを解析して各リストにセット - GetContents専用
	 * @param xmlの内容をList化したもの
	 */
	private String getContentsResponse(ArrayList<ArrayList<NameValuePair>> list){
		if(list == null) return null;
		
		for(int i=0; i<list.size(); i++){
			ArrayList<NameValuePair> nameValueList = list.get(i);
			for(NameValuePair nameValue : nameValueList){
				String tagName = nameValue.getName();
				String value = nameValue.getValue();
				
				if(tagName.startsWith(CONTENTS_NEW)){
					if(mContentsNewList == null) mContentsNewList = new ArrayList<ArrayList<NameValuePair>>();
					mContentsNewList.add(nameValueList);
					break;
				}else if(tagName.startsWith(CONTENTS_UPDATE)){
					if(mContentsUpdateList == null) mContentsUpdateList = new ArrayList<ArrayList<NameValuePair>>();
					mContentsUpdateList.add(nameValueList);
					break;
				}else if(tagName.startsWith(LICENSE)){
					if(mLicenseList == null) mLicenseList = new ArrayList<ArrayList<NameValuePair>>();
					mLicenseList.add(nameValueList);
					break;
				}else if(tagName.equals(OFF_SET)){
					mOffset = value;
				}
				else if(tagName.equals(STATUS)) mStatus = value;
				else if(tagName.equals(DESCRIPTION)) mDescription = value;
			}
		}
		return mStatus;
	}
	
	/**
	 * Parserの解析
	 * @param parser
	 * @param list
	 * @param name
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private void getTagValue(XmlPullParser parser, ArrayList<ArrayList<NameValuePair>> list, String name)
			throws IOException, XmlPullParserException{
		while(true){
			int eventType = parser.next();
			switch(eventType) {
			case XmlPullParser.START_TAG:
				//	親タグ名が無ければ新しいNameValuePairリストを追加
				if(name.length() == 0) {
					ArrayList<NameValuePair> nameValueList = new ArrayList<NameValuePair>();
					list.add(nameValueList);
				}
				//	親タグ名を連結して再帰
				String new_tag_name = null;
				new_tag_name = name.length() > 0 ? name + "_" + parser.getName() : parser.getName();
				getTagValue(parser, list, new_tag_name);
				break;
			case XmlPullParser.TEXT:
				//	値を取得してURLデコード
				String value = parser.getText();
				if(!StringUtil.isEmpty(value)){
					value = URLDecoder.decode(value);
				}
				//	リスト最後尾のNameValuePairリストに追加
				list.get(list.size() - 1).add(new BasicNameValuePair(name,value));
				break;
			case XmlPullParser.END_TAG:
				return;
			}
		}
	}

	
	// ------- GetContentsレスポンス情報getter --------
	/**
	 * GetContents - レスポンス詳細取得
	 * @return mDescription レスポンス詳細
	 */
	public String getDescription() {
		return mDescription;
	}

	/**
	 * GetContents - offsetを取得
	 * @return offset
	 */
	public String getOffset() {
		return mOffset;
	}

	/**
	 * GetContents - 新規登録コンテンツ情報リストを取得
	 * @return contentsNewList 新規登録コンテンツ情報リスト
	 */
	public ArrayList<ArrayList<NameValuePair>> getContentsNewList() {
		return mContentsNewList;
	}

	/**
	 * GetContents - 更新コンテンツ情報リストを取得
	 * @return contentsUpdateList 更新コンテンツ情報リスト
	 */
	public ArrayList<ArrayList<NameValuePair>> getContentsUpdateList() {
		return mContentsUpdateList;
	}

	/**
	 * GetContents - ライセンス情報リストを取得
	 * @return licenseList ライセンス情報リスト
	 */
	public ArrayList<ArrayList<NameValuePair>> getLicenseList() {
		return mLicenseList;
	}
	
	
}
