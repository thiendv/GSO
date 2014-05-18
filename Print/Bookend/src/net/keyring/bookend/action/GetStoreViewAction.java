package net.keyring.bookend.action;

import java.util.ArrayList;
import java.util.Map;

import net.keyring.bookend.Preferences;
import net.keyring.bookend.R;
import net.keyring.bookend.activity.StoreListActivity;
import net.keyring.bookend.constant.ConstReq;
import net.keyring.bookend.request.GetStoreList;
import net.keyring.bookend.util.StringUtil;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * 販売サイト情報取得・表示
 * @author Hamaji
 *
 */
public class GetStoreViewAction {
	
	public GetStoreViewAction(Context con){
		this.con = con;
	}
	/** Context */
	private Context con = null;

	/**
	 * 販売サイト一覧取得
	 * @return エラーが発生した場合はエラーメッセージを返す
	 */
	public String execute(){
		String errorMessage = null;
		if(Preferences.sStoreList == null){
    		// GetStoreList => static変数に保存  => 表示
    		errorMessage = getStoreList();
    	}else{
    		// 表示
    		errorMessage = store(Preferences.sStoreList);
    	}
		return errorMessage;
	}
	
	/**
     * GetStoreListリクエストし、販売ストア情報一覧取得
     * @return エラーが発生した場合はエラーメッセージを返す
     */
    private String getStoreList(){
    	String errorMessage = null;
    	// staticに保存してある販売サイト情報がない場合はサーバーにリクエストする
    	GetStoreList getStoreList = new GetStoreList();
    	if(getStoreList.execute(con)){
    		// GetStoreList - OK ... static変数に保存
    		Preferences.sStoreList = getStoreList.getStoreList();
    		// 販売サイト(リスト)表示
    		errorMessage = store(Preferences.sStoreList);
    	}else{
    		// GetStoreList - NG
    		errorMessage = getStoreList.getDescription();
    	}
    	return errorMessage;
    }
    
    /**
     * 販売サイト表示
     * @return エラーが発生した場合はエラーメッセージを返す
     */
    private String store(ArrayList<Map<String,String>> storeList){
    	String errorMessage = null;
    	if(storeList == null){
    		errorMessage = "Stores none.";
		}else{
			if(storeList.size() == 1){
				// そのままサイト表示
				errorMessage = viewStore();
			}else{
				// リスト表示
				viewStoreList();
			}
		}
    	return errorMessage;
    }
    
    /**
     * StoreListActivityに遷移
     */
    private void viewStoreList(){
		// 販売ストア一覧表示
		Intent store = new Intent(con, StoreListActivity.class);
		store.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		con.startActivity(store);
    }

   
    /**
     * 販売サイト表示(１つの場合)<br>
     * ※ StoreTypeが2以外の場合はエラーダイアログ表示
     * @return エラーが発生した場合はエラーメッセージを返す
     */
    private String viewStore(){
    	String errorMessage = null;
    	Map<String,String> store = Preferences.sStoreList.get(0);
    	String store_url = store.get(ConstReq.STORE_URL);
		String store_type = store.get(ConstReq.STORE_TYPE);
		if(!StringUtil.isEmpty(store_type)){
			if(store_type.equals("2")){
				// ブラウザアプリで販売サイト表示
				Intent store_site = new Intent(Intent.ACTION_VIEW,Uri.parse(store_url));
				store_site.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        	con.startActivity(store_site);
			}else{
				errorMessage = con.getString(R.string.store_type_NG);
			}
		}
		return errorMessage;
    }
}
