package net.keyring.bookend.action;

import java.io.File;
import java.util.Date;
import java.util.List;

import net.keyring.bookend.Logput;
import net.keyring.bookend.bean.BookBeans;
import net.keyring.bookend.constant.Const;
import net.keyring.bookend.db.ContentsDao;
import net.keyring.bookend.db.UpdateContentsDao;
import net.keyring.bookend.db.UpdateLicenseDao;
import net.keyring.bookend.util.DateUtil;
import net.keyring.bookend.util.FileUtil;
import net.keyring.bookend.util.StringUtil;
import android.content.Context;
/**
 * メインコンテンツリスト・Web書庫リスト 共通アクション static
 * @author Hamaji
 *
 */
public class CommonAction {
	
	/** UpdateContentsDaoクラス */
	private UpdateContentsDao mUpConDao;
	/** UpdateLicenseDaoクラス */
	private UpdateLicenseDao mUpLiDao;
	/** ContentsDaoクラス */
	private ContentsDao mContentsDao;
	
	/**
	 * DBからコンテンツ登録を削除
	 * @param book 削除コンテンツ
	 */
	public void deleteContent(Context con, BookBeans book){
		String filePath = book.getFile_path();
		String downloadID = book.getDownload_id();
		String thumbPath = book.getThumb_path();
		try{
			// DLされている場合はファイル・サムネイル削除
			FileUtil.deleteFile(new File(filePath));
			FileUtil.deleteFile(new File(thumbPath));
		}catch(NullPointerException e){
		}
		// DBから削除
		if(mUpConDao == null) mUpConDao = new UpdateContentsDao(con);
		long upConID = mUpConDao.getRowID(downloadID);
		if(upConID != -1){
			mUpConDao.delete(upConID);
		}
		if(mUpLiDao == null) mUpLiDao = new UpdateLicenseDao(con);
		long upLiID = mUpLiDao.getRowID(downloadID);
		if(upLiID != -1){
			mUpLiDao.delete(upLiID);
		}
		if(mContentsDao == null) mContentsDao = new ContentsDao(con);
		mContentsDao.delete(book.getRowId());
	}
	
    /**
     * 閲覧期限(expiryDate)チェック
     * @param 閲覧期限
     * @return 閲覧期限が過ぎていた場合はfalse,それ以外はtrueを返す
     */
    public boolean check_ExpiryDate(String expiryDate){
    	try{
	    	if(DateUtil.isUnlimitedDate(expiryDate)){
	    		//Logput.v("ExpiryDate = " + Const.DATE_MAX);
	    		return true;
	    	}else{
	    		// OSから今の時間を取得する
	    		Date expiry_date = DateUtil.toDate(expiryDate, "UTC");
	    		Date now_date = DateUtil.toDate(DateUtil.getNowUTC(), "UTC");
	    		if(now_date.after(expiry_date)){
	    			//Logput.v("  [ExpiryDate]" + expiry_date + " / [Now]" + now_date + " (OS default timezone)");
	    			return false;
	    		}else{
	    			return true;
	    		}
	    	}
    	}catch(NullPointerException e){
    		Logput.w("ExpiryDate is null.", e);
	    	return false;
	    }
    }
    
    /**
     * Androidでの閲覧が許可されているかチェック(invalidPlatform)
     * @param 閲覧が許可されていないプラットフォーム名(なしの場合は"none")
     * @return Androidでの閲覧が許可されていればtrue,許可されていなければfalseを返す
     */
    public boolean check_InvalidPlatform(String invalidPlatform){
    	if(StringUtil.isEmpty(invalidPlatform)){
    		// 指定がnullの場合はtrue
    		return true;
    	}else{
    		List<String> list = StringUtil.parseStr(invalidPlatform, ',');
    		for(String osName : list){
    			if(!StringUtil.isEmpty(osName)){
    				if(osName.equals(Const.ANDROID)){
    		    		// Androidが指定されていた場合はfalse
    		    		//Utils.debug("invalidPlatform : " + Const.ANDROID);
    		    		return false;
    		    	}
    			}
    		}
    		return true;
    	}
    }
    
    /**
	 * DBから取得した詳細情報を表示用に
	 * @param title 詳細情報見出し
	 * @param value 詳細情報
	 * @return 詳細表示文字列
	 */
	public String setDetailValue(String title, String value){
		String dTitle = " - ";
		if(!StringUtil.isEmpty(value)){
			dTitle = value;
		}
		dTitle = title + dTitle;
		return dTitle;
	}
	
}
