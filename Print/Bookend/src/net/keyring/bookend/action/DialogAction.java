package net.keyring.bookend.action;

import java.io.File;

import net.keyring.bookend.ImageCache;
import net.keyring.bookend.Logput;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.R;
import net.keyring.bookend.activity.BookendActivity;
import net.keyring.bookend.constant.ConstViewDefault;
import net.keyring.bookend.db.ContentsDBAccess;
import net.keyring.bookend.util.DateUtil;
import net.keyring.bookend.util.FileUtil;
import net.keyring.bookend.util.StringUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ダイヤログアクション
 * @author Hamaji
 *
 */
public class DialogAction implements ConstViewDefault{
	/** バージョンアップ - クライアントDLサイトURL */
	private String verUP_URL = null;
	/** Preferencesクラス */
	private Preferences pref = null;
	/** マーケットID */
	private String marketID = null;
	
	/**
	 * アンドロイドマーケットIDをセット
	 * @param marketID アンドロイドマーケットID
	 */
	public void setMarketID(String marketID){
		this.marketID = marketID;
	}
	/**
	 * アンドロイドマーケットIDを取得
	 * @return アンドロイドマーケットID
	 */
	public String getMarketID(){
		return this.marketID;
	}
	
	/**
	 * バージョンアップ - クライアントDLサイトURLをセット
	 * @param url クライアントDLサイトURL
	 */
	public void setVerUP_URL(String url){
		this.verUP_URL = url;
	}
	/**
	 * バージョンアップ - クライアントDLサイトURLを取得
	 * @return クライアントDLサイトURL
	 */
	public String getVerUP_URL(){
		return this.verUP_URL;
	}

	/**
	 * ダイヤログセット
	 * @param a Activity
	 * @param dialogMessage ダイヤログメッセージ
	 * @return ダイヤログ
	 */
	public AlertDialog.Builder setDialog(Activity a, String dialogMessage){
		return setDialog(a, false, null, dialogMessage);
	}
	
	/**
	 * ダイアログセット
	 * @param a Activity
	 * @param isLink リンクする場合はtrue,それ以外はfalse
	 * @param title ダイヤログタイトル
	 * @param dialogMessage ダイヤログメッセージ
	 * @return ダイヤログ
	 */
	public AlertDialog.Builder setDialog(Activity a, boolean isLink, String title, String dialogMessage){
		LayoutInflater inflater = LayoutInflater.from(a);
		View view = inflater.inflate(R.layout.alert, null);
		TextView message =(TextView)view.findViewById(R.id.alert_message);
		if(isLink){
			message.setAutoLinkMask(Linkify.WEB_URLS);
			message.setText(Html.fromHtml(dialogMessage));
		}else{
			message.setText(dialogMessage);
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(a);
		builder.setIcon(R.drawable.icon);
		if(!StringUtil.isEmpty(title)){
			builder.setTitle(title);
		}
		builder.setView(view);
		return builder;
	}
	
	/**
	 * 通常バージョンアップ - ダウンロード 
	 * @param a Activity
	 */
	public void updateUtill_DL(BookendActivity a){
		a.removeDialog(DIALOG_ID_UPDATE_UTILL);
		String url = getVerUP_URL();
		if(!StringUtil.isEmpty(url)){
			// 取得日時更新
			if(pref == null) pref = new Preferences(a.getApplicationContext());
			pref.setVersion_lastGetDate(DateUtil.getNowUTC());
			Intent update_usually = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
			update_usually.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			try{
				a.startActivity(update_usually);
			}catch(ActivityNotFoundException e){
				Logput.e(e.getMessage(), e);
				a.dialog(DIALOG_ID_ERROR, a.getString(R.string.open_fail_updatepage));
			}
		}else{
			Toast.makeText(a, "URL false.", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 通常バージョンアップ - 何もしない
	 * @param a Activity
	 */
	public void updateUtill_After(Activity a){
		// デフォルトチェック済
		Preferences.sDefaultCheck = true;
		// 取得日時更新
		if(pref == null) pref = new Preferences(a.getApplicationContext());
		pref.setVersion_lastGetDate(DateUtil.getNowUTC());
		a.removeDialog(DIALOG_ID_UPDATE_UTILL);
	}
	
	/**
	 * 強制バージョンアップ
	 * @param a Activity
	 */
	public void updateForce_DL(Activity a){
		a.removeDialog(DIALOG_ID_UPDATE_FORCE);
		String url = getVerUP_URL();
		if(!StringUtil.isEmpty(url)){
			// 取得日時更新
			if(pref == null) pref = new Preferences(a.getApplicationContext());
			pref.setVersion_lastGetDate(DateUtil.getNowUTC());
			Intent update_usually = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
			update_usually.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            a.startActivity(update_usually);
		}else{
			Toast.makeText(a, "URL false.", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * プログレスダイアログ - 円形
	 * @param a Activity
	 * @param message 表示メッセージ
	 */
	public ProgressDialog progress_SPINNER(Activity a, String message){
		 ProgressDialog progress = new ProgressDialog(a);
		 // スタイルを設定
		 progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		 progress.setIcon(R.drawable.icon);
		 progress.setMessage(message);
		 // ProgressDialog の確定（false）／不確定（true）を設定
		 progress.setIndeterminate(true);
		 // キャンセル可能かどうか
		 progress.setCancelable(true);
		// 実際に行いたい処理を別スレッドで実行
		return progress;
	}
	
	/**
	 * リセット
	 * @param a Activity
	 * @return 初期化が正常に終了した場合はtrue,それ以外はfalseを返す
	 */
	public boolean reset_OK(Activity a){
		// LibraryIDのみ残して初期化し、再起動
    	if(init(a.getApplicationContext())){
    		// 初期化が正常に終わった場合は再起動する
    		a.removeDialog(DIALOG_ID_REACTIVATION);
    		return true;
    	}else{
    		a.removeDialog(DIALOG_ID_REACTIVATION);
    		return false;
    	}
	}
	
	/**
	 * DL済みコンテンツ初期化
	 * @param con Context
	 * @return 問題なく初期化できた場合はtrue,それ以外はfalseを返す
	 */
	private boolean init(Context con){
		Logput.v("------- INIT START --------");
		boolean check = true;
		// 全てのキャッシュを削除
		ImageCache.clearCache();
		// DB削除
		check = deleteDB(con);
		// DB削除に問題がなかった場合はコンテンツ等削除
		if(check){
			// DBエンジン初期化
			ContentsDBAccess.initInstance(con);
			// 外部アプリケーション領域にあるフォルダを削除
			delete_external_strage();
			// APP領域にあるfileディレクトリ(復号化済みファイルディレクトリ)を削除
			delete_app_fileDir(con);
			// Perferences削除(LibraryIDのみ保持)
			init_pref(con);
		}
		Logput.v("------- INIT FINISH --------");
		return check;
	}
	
	/**
	 * Perferences削除(LibraryIDのみ保持)
	 * @param con Context
	 */
	public void init_pref(Context con){
		if(pref == null) pref = new Preferences(con);
		String libraryID = pref.getLibraryID();
		Logput.d("Save LibraryID = " + libraryID);
		
		Editor editor = Preferences.pref.edit();
		editor.clear();
		editor.commit();
		
		// static変数初期化
		pref.init_pref();
		// 新規Preferences作成
		pref = new Preferences(con);
		// LibraryID保存
		pref.setLibraryID(libraryID);
		// InAppBillingチェック
		pref.setDbInitialized(false);
	}
	
	/**
	 * APP領域にあるfileディレクトリ(復号化済みファイルディレクトリ)を削除
	 * @param con Context
	 */
	private void delete_app_fileDir(Context con){
		File app_file = con.getFilesDir();
		if(FileUtil.deleteFile(app_file)){
			Logput.v("Delete APP file Dir = " + app_file.getPath());
			Logput.v("Delete APP file Dir : success");
		}else{
			Logput.v("Delete APP file Dir = " + app_file.getPath());
			Logput.v("Delete APP file Dir : fail");
		}
	}
	
	/**
	 * 外部アプリケーション領域にあるフォルダを削除
	 */
	private void delete_external_strage(){
		Logput.v("Delete external strage Dir = " + Preferences.sExternalStrage);
		try{
			if(FileUtil.deleteFile(new File(Preferences.sExternalStrage))){
				Logput.v("Delete external strage Dir : success");
			}else{
				Logput.v("Delete external strage Dir : fail");
			}
		}catch(NullPointerException e){
			Logput.i("Delete external strage : fail..." + e.getMessage());
		}
	}
	
	/**
	 * contents.DB削除
	 * @param con Context
	 * @return 削除に問題がなければtrue,それ以外はfalseを返す
	 */
	private boolean deleteDB(Context con){
		boolean check = true;
		String[] dbList = con.databaseList();
		for(String db : dbList){
			check = con.deleteDatabase(db);
			if(check) Logput.v("Delete DB : success >> " + db);
			else Logput.v("Delete DB : fail >> " + db);
		}
		return check;
	}
}
