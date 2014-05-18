package net.keyring.bookend.action;

import java.util.ArrayList;
import java.util.List;

import net.keyring.bookend.bean.BookBeans;
import net.keyring.bookend.constant.ConstReq;
import net.keyring.bookend.db.ContentsDao;
import net.keyring.bookend.db.NaviSalesDao;
import net.keyring.bookend.util.StringUtil;
import net.keyring.bookend.util.Utils;

import org.apache.http.NameValuePair;

import android.content.Context;

/**
 * GetContentsリクエストで受け取った情報を処理してDBに登録
 * @author Hamaji
 *
 */
public class GetContentsAction implements ConstReq{
	/**
	 * コンストラクタ
	 * @param context Context
	 */
	public GetContentsAction(Context context){
		this.mCon = context;
	}

	/** Context */
	private Context mCon;
    /** ContentsDaoクラス */
    private ContentsDao mContentsDao;
    /** NaviSalesDaoクラス */
    private NaviSalesDao mNaviSalesDao;
    /** CommonActionクラス */
    private CommonAction mComAction;

	 /**
     * ★GetContentsリクエストで取得した情報をDBに登録
     * @param contentsUpdateList
     * @param contentsNewList
     * @param licenseList
     * @return DB登録が全て無事終了したらtrue,一つでも失敗していた場合はfalseを返す
     */
    public boolean saveDB_GetContents(ArrayList<ArrayList<NameValuePair>> contentsNewList,
    		ArrayList<ArrayList<NameValuePair>> contentsUpdateList, ArrayList<ArrayList<NameValuePair>> licenseList){

    	boolean check_insert = true;
    	boolean check_update = true;
    	boolean check_license = true;
    	if(!isNull_list(contentsNewList)){
    		// <contentsNew>
    		check_insert = insertDB(contentsNewList);
    	}
    	if(!isNull_list(contentsUpdateList)){
    		// <contentsUpdate>
    		check_update = updateDB(contentsUpdateList);
    	}
    	if(!isNull_list(licenseList)){
    		// <License>
    		check_license = updateLicense(licenseList);
    	}
    	
    	if( check_insert && check_update && check_license ){
    		return true;
    	}else{
    		return false;
    	}
    }

    /**
     * ListがNULL,もしくはリストサイズが1以下ではないかチェック
     * @return List内にitemがない場合はtrue,それ以外はfalseを返す
     */
    private boolean isNull_list(ArrayList<ArrayList<NameValuePair>> list){
    	boolean isNull = true;
    	if(list != null){
    		if(list.size() > 0){
    			isNull = false;
    		}
    	}
    	return isNull;
    }

    /**
     * [***_name] => [name]
     * @param [***_name]
     * @return [name]
     */
    private String changeName(String name){
    	List<String> nameList = StringUtil.parseStr(name, '_');
    	if(nameList == null) return null;
    	else if(nameList.size() <= 0) return null;
		return nameList.get(1);
    }
    
    private boolean updateLicense(ArrayList<ArrayList<NameValuePair>> licenseList){
    	BookBeans book = null;
    	ArrayList<BookBeans> updateLicense = new ArrayList<BookBeans>();
    	
    	for(ArrayList<NameValuePair> license_list : licenseList){
    		String downloadID = null;
        	String ownerPassword = null;
        	int sharedDevice_m = -1;
        	int sharedDevice_d = -1;
        	int browse_m = -1;
        	int browse_d = -1;
        	int print_m = -1;
        	int print_d = -1;
        	String invalidPlatform = null;
        	String expiryDate = null;
	    	for(NameValuePair nameValue_license : license_list){
				String license_name = nameValue_license.getName();
				license_name = changeName(license_name);
				if(!StringUtil.isEmpty(license_name)){
					List<String> list;
					if(license_name.equals(DOWNLOAD_ID)){
						downloadID = nameValue_license.getValue();
					}else if(license_name.equals(SHARED_DEVICE)){
						list = StringUtil.parseStr(nameValue_license.getValue(), '/');
						if(list.size() == 2){
							sharedDevice_m = Utils.changeNum(list.get(0));
							sharedDevice_d = Utils.changeNum(list.get(1));
						}
					}else if(license_name.equals(BROWSE)){
						list = StringUtil.parseStr(nameValue_license.getValue(), '/');
						if(list.size() == 2){
							browse_m = Utils.changeNum(list.get(0));
							browse_d = Utils.changeNum(list.get(1));
						}
					}else if(license_name.equals(PRINT)){
						list = StringUtil.parseStr(nameValue_license.getValue(), '/');
						if(list.size() == 2){
							print_m = Utils.changeNum(list.get(0));
							print_d = Utils.changeNum(list.get(1));
						}
					}else if(license_name.equals(INVALID_PLATFORM)){
						invalidPlatform = nameValue_license.getValue();
					}else if(license_name.equals(EXPIRY_DATE)){
						expiryDate = nameValue_license.getValue();
					}else if(license_name.equals(KEY)){
						ownerPassword = nameValue_license.getValue();
					}
				}
			}
	    	if(mContentsDao == null) mContentsDao = new ContentsDao(mCon);
	    	book = mContentsDao.setBook(downloadID, ownerPassword, sharedDevice_m, sharedDevice_d, browse_m,
	    			browse_d, print_m, print_d, invalidPlatform, expiryDate);
	    	if(book != null){
	    		updateLicense.add(book);
	    	}
    	}
	    
    	int count = -1;
    	if(book != null){
    		if(mContentsDao == null) mContentsDao = new ContentsDao(mCon);
    		count = mContentsDao.save(updateLicense);
    	}
    	if(count != -1){
    		return true;
    	}else{
    		return false;
    	}
    }

    /**
     * ContentsNew+License情報をDBにInsert
     * @return 全てのInsertに問題がなければtrue,それ以外はfalseを返す
     */
    private boolean insertDB(ArrayList<ArrayList<NameValuePair>> contentsNewList){
    	ArrayList<BookBeans> insertBooks = new ArrayList<BookBeans>();
    	for(ArrayList<NameValuePair> contentsNew : contentsNewList){
        	String downloadID = null;
        	String contentsID = null;
        	String fileType = null;
        	String title = null;
        	String author = null;
        	String keywords = null;
        	String distributorName = null;
        	String distributorURL = null;
        	String fileSize = null;
        	String originalFileName = null;
        	int pageCount = -1;
        	String downloadDate = null;
        	String lastAccessDate = null;
        	String lastModify = null;
        	String labelID = null;
        	String salesID = null;
        	String contents_DL_URL = null;
        	String crc32 = null;
        	String thumb_DL_URL = null;
        	String naviMessage = null;
        	String naviUrl = null;

        	if(contentsNew == null) break;
        	int listSize = contentsNew.size();
    		for(int i=0; i<listSize; i++){
    			NameValuePair nameValue = contentsNew.get(i);
    			String name = nameValue.getName();
    			name = changeName(name);
    			// <ContentsNew>
    			if(name.equals(DOWNLOAD_ID)) downloadID = nameValue.getValue();
    			else if(name.equals(CONTENTS_ID)) contentsID = nameValue.getValue();
    			else if(name.equals(FILE_TYPE)) fileType = nameValue.getValue();
    			else if(name.equals(TITLE)) title = nameValue.getValue();
    			else if(name.equals(AUTHOR)) author = nameValue.getValue();
    			else if(name.equals(KEYWORDS)) keywords = nameValue.getValue();
    			else if(name.equals(DISTRIBUTOR_NAME)) distributorName = nameValue.getValue();
    			else if(name.equals(DISTRIBUTOR_URL)) distributorURL = nameValue.getValue();
    			else if(name.equals(FILE_SIZE)) fileSize = nameValue.getValue();
    			else if(name.equals(ORIGINAL_FILE_NAME)) originalFileName = nameValue.getValue();
    			else if(name.equals(PAGE_COUNT)){
    				String pageCountStr = nameValue.getValue();
    				if(StringUtil.isInteger(pageCountStr)){
    					pageCount = Integer.parseInt(pageCountStr);
    				}
    			}else if(name.equals(DOWNLOAD_DATE)) downloadDate = nameValue.getValue();
    			else if(name.equals(LAST_ACCESS_DATE)) lastAccessDate = nameValue.getValue();
    			else if(name.equals(LAST_MODIFY)) lastModify = nameValue.getValue();
    			else if(name.equals(CRC32)) crc32 = nameValue.getValue();
    			else if(name.equals(LABEL_ID)) labelID = nameValue.getValue();
    			else if(name.equals(SALES_ID)){
    				if(StringUtil.isEmpty(salesID)) salesID = nameValue.getValue();
    				else salesID = salesID + "," + nameValue.getValue();
    			}else if(name.equalsIgnoreCase(NAVIGATE_URL)) naviUrl = nameValue.getValue();
    			else if(name.equalsIgnoreCase(NAVIGATE_MESSAGE)) naviMessage = nameValue.getValue();
    			else if(name.equals(CONTENTS_URL)) contents_DL_URL = nameValue.getValue();
    			else if(name.equals(THUMB_URL)) thumb_DL_URL = nameValue.getValue();
    			if(i == listSize-1){
    				if(!StringUtil.isEmpty(naviMessage)){
    					if(mNaviSalesDao == null) mNaviSalesDao = new NaviSalesDao(mCon);
        				mNaviSalesDao.save(-1, downloadID, naviUrl, naviMessage);
    				}
    				if(mContentsDao == null) mContentsDao = new ContentsDao(mCon);
    				BookBeans book = mContentsDao.setBook(downloadID, contentsID, fileType, title, author, keywords, distributorName,
    						distributorURL, fileSize, originalFileName, pageCount, downloadDate, lastAccessDate,
    						lastModify, crc32, labelID, salesID, contents_DL_URL, thumb_DL_URL);
    				if(book != null){
    					insertBooks.add(book);
    				}
    			}
    		}
    	}
    	
    	boolean check = false;
    	if(insertBooks != null){
    		if(mContentsDao == null) mContentsDao = new ContentsDao(mCon);
        	int count = mContentsDao.save(insertBooks);
    		if(count != -1){
    			check = true;
    		}
    	}
    	return check;
    }

    /**
     * GetContentsで取得したUPDATE情報を更新
     * @return
     */
    private boolean updateDB(ArrayList<ArrayList<NameValuePair>> contentsUpdateList){
    	boolean check = false;
    	ArrayList<BookBeans> updateBooks = new ArrayList<BookBeans>();
    	for(ArrayList<NameValuePair> contentsUpdate : contentsUpdateList){
        	String downloadID = null;
        	BookBeans book = null;
        	String naviMessage = null;
        	String naviUrl = null;

        	int listSize = contentsUpdate.size();
    		for(int i=0; i<listSize; i++){
    			NameValuePair nameValue = contentsUpdate.get(i);
    			String name = nameValue.getName();
    			name = changeName(name);
    			// <ContentsUpdate>
    			if(name.equals(DOWNLOAD_ID)){	// ※順番関係有
    				downloadID = nameValue.getValue();
    				if(mContentsDao == null) mContentsDao = new ContentsDao(mCon);
    				book = mContentsDao.select(downloadID);
    			}else if(name.equals(DELETE_FLAG)){	// ※順番関係有
    				// コンテンツが完全に削除されていた場合
    				String flag = nameValue.getValue();
    				if(flag.equals("true")){
    					if(book != null){
    						// 他のクライアントから削除されている場合DBから削除
    						if(mComAction == null) mComAction = new CommonAction();
    						mComAction.deleteContent(mCon, book);
    					}
    					continue;
    				}
    			}else if(name.equals(CONTENTS_ID)) book.setContents_id(nameValue.getValue());
    			else if(name.equals(TITLE)) book.setTitle(nameValue.getValue());
    			else if(name.equals(AUTHOR)) book.setAuthor(nameValue.getValue());
    			else if(name.equals(KEYWORDS)) book.setKeywords(nameValue.getValue());
    			else if(name.equals(DISTRIBUTOR_NAME)) book.setDistributor_name(nameValue.getValue());
    			else if(name.equals(DISTRIBUTOR_URL)) book.setDistributor_url(nameValue.getValue());
    			else if(name.equals(LAST_ACCESS_DATE)) book.setLast_access_date(nameValue.getValue());
    			else if(name.equals(LAST_MODIFY)) book.setLastModify(nameValue.getValue());
    			else if(name.equals(LABEL_ID)) book.setLabelID(nameValue.getValue());
    			else if(name.equals(SALES_ID)) book.setSalesID(nameValue.getValue());
    			else if(name.equals(CONTENTS_URL)) book.setContents_DL_URL(nameValue.getValue());
    			else if(name.equals(THUMB_URL)) book.setThumb_DL_URL(nameValue.getValue());
    			else if(name.equalsIgnoreCase(NAVIGATE_MESSAGE)) naviMessage = nameValue.getValue();
    			else if(name.equalsIgnoreCase(NAVIGATE_URL)) naviUrl = nameValue.getValue();
    			if(i == listSize-1){
    				if(!StringUtil.isEmpty(naviMessage)){
    					if(mNaviSalesDao == null) mNaviSalesDao = new NaviSalesDao(mCon);
        				mNaviSalesDao.save(-1, downloadID, naviUrl, naviMessage);
    				}
    				if(book != null){
    					updateBooks.add(book);
    				}
    			}
    		}
    	}
    	if(updateBooks != null){
    		if(mContentsDao == null) mContentsDao = new ContentsDao(mCon);
    		int count = mContentsDao.save(updateBooks);
    		if(count != -1){
    			check = true;
    		}
    	}
    	return check;
    }
}
