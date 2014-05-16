package net.keyring.bookend.activity;

import java.util.List;
import java.util.Locale;

import net.keyring.bookend.Logput;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.R;
import net.keyring.bookend.action.DialogAction;
import net.keyring.bookend.asynctask.ResetTask;
import net.keyring.bookend.asynctask.ResetTask.ResetListener;
import net.keyring.bookend.bean.NaviSalesBeans;
import net.keyring.bookend.constant.ConstViewDefault;
import net.keyring.bookend.db.NaviSalesDao;
import net.keyring.bookend.util.StringUtil;
import net.keyring.bookend.util.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Bookend専用Activity
 * 
 * @author Hamaji
 * 
 */
public abstract class BookendActivity extends Activity implements ConstViewDefault,ResetListener{
	
	/** Preferencesクラス */
	public Preferences mPref = null;
	/** レイアウト */
	public LinearLayout mLayout = null;
	/** NaviSalesDaoクラス */
	private NaviSalesDao mNaviSalesDao = null;
	/** Webサイト誘導URL */
	private String mNaviUrl = null;
	
	/**
	 * 画面の向きが変わったときに呼ばれる<br>
	 * ※向きが変わったときに再起動させない為
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//	ログ出力を行うか判定する
		Logput.isLogCat();
	}
	
	@Override
	public void onStart(){
		super.onStart();
		// xmlにwebサイト誘導用ダウンロードIDが保存されていた場合はダイヤログを表示する
		if(mPref == null) mPref = new Preferences(getApplicationContext());
		String message = checkNavigate(mPref.getNaviDlId());
		if(!StringUtil.isEmpty(message)){
			dialog(DIALOG_ID_NAVI, message);
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
		progress_stop();
		showDialog(-1, null);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mLayout != null) {
			mLayout.removeAllViews();
			mLayout = null;
		}
	}
	
	// -------------------------------------------------
	// DIALOG - DialogFragmentを使うことにより、ライフサイクルも Activity管理になる
	// -------------------------------------------------

	/** ダイヤログ表示メッセージ */
	public String mDialogMessage = null;
	/** ダイヤログ表示タイトル */
	public String mDialogTitle = "";
	/** プログレスダイアログ */
	public ProgressDialog mProgressDialog = null;
	/** DialogActionクラス */
	public DialogAction mDialogAction = null;
	/** ダイアログビルダー */
	public AlertDialog.Builder mBuilder = null;

	/**
	 * 初めてダイアログが表示されるときのみ呼ばれる
	 */
	@Override
	protected Dialog onCreateDialog(int id){
		return createDialog(id);
	}
	
	/**
	 * ★ダイアログ表示
	 * @param ダイアログid
	 * @param ダイアログに表示するメッセージ
	 */
	public void dialog(int id, String message){
		mDialogMessage = message;
		mDialogTitle = "";
		showDialog(id);
	}
	/**
	 * ★ダイアログ表示
	 * @param ダイアログid
	 * @param ダイアログに表示するタイトル
	 * @param ダイアログに表示するメッセージ
	 */
	public void dialog(int id, String title, String message){
		mDialogMessage = message;
		mDialogTitle = title;
		showDialog(id);
	}
	
	/**
	 * メイン画面から有効期限切れコンテンツ選択時に表示するエラー＋webサイト誘導ダイヤログ
	 * @param downloadID
	 */
	public void expiryDateNavidialog(String downloadID){
		mDialogMessage = checkNavigate(downloadID);
		if(!StringUtil.isEmpty(mDialogMessage)){
			mDialogMessage = "*" + getString(R.string.expiry_date_error) + "\n\n" + mDialogMessage;
			showDialog(DIALOG_ID_NAVI);
		}else{
			mDialogMessage = getString(R.string.expiry_date_error);
			showDialog(DIALOG_ID_VIEW);
		}
	}

	/**
	 * 指定IDのをダイアログ生成
	 * 
	 * @param id
	 * @return dialog
	 */
	public Dialog createDialog(final int id) {
		if (mDialogAction == null) mDialogAction = new DialogAction();
		switch (id) {
		case DIALOG_ID_ERROR: // エラー表示後アプリ終了
			progress_stop();
			mBuilder = mDialogAction.setDialog(this, false, "ERROR", mDialogMessage);
			mBuilder.setPositiveButton(getString(R.string.ok),
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							removeDialog(id);
							appFinish();
						}
					});
			return mBuilder.create();
		case DIALOG_ID_VIEW: // エラー表示後画面表示
			progress_stop();
			// ダイヤログ表示
			mBuilder = mDialogAction.setDialog(this, mDialogMessage);
			mBuilder.setPositiveButton(getString(R.string.ok),
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							removeDialog(id);
						}
					});
			return mBuilder.create();
		case DIALOG_ID_UPDATE_UTILL: // 通常アップデート
			mBuilder = mDialogAction.setDialog(this, true, getString(R.string.ver_dialog_title), mDialogMessage);
			mBuilder.setPositiveButton(this.getString(R.string.download),
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 最新版をDL
							mDialogAction.updateUtill_DL(BookendActivity.this);
						}
					});
			mBuilder.setNegativeButton(this.getString(R.string.after),
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 後で通知 - 何もしない
							mDialogAction.updateUtill_After(BookendActivity.this);
							onResume();
						}
					});
			return mBuilder.create();
		case DIALOG_ID_UPDATE_FORCE:
			String mes = mDialogMessage + "<br>"
					+ getString(R.string.ver_force_message);
			mBuilder = mDialogAction.setDialog(this, true,
					getString(R.string.ver_dialog_title), mes);
			mBuilder.setPositiveButton(this.getString(R.string.download),
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 最新版をDL
							mDialogAction.updateForce_DL(BookendActivity.this);
						}
					});
			mBuilder.setNegativeButton(this.getString(R.string.cancel),
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// キャンセル - アプリ終了
							removeDialog(id);
							appKill();
						}
					});
			return mBuilder.create();
		case DIALOG_ID_RESET:	// リセットダイヤログ表示
    		if (mDialogAction == null) mDialogAction = new DialogAction();
    		mBuilder = mDialogAction.setDialog(this, false, getString(R.string.reset), mDialogMessage);
        	// OK
    		mBuilder.setPositiveButton(this.getString(R.string.ok), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                	reset();
                }
            });
            // Cancel
    		mBuilder.setNegativeButton(this.getString(R.string.cancel), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                	removeDialog(id);
                }
            });
    		return mBuilder.create();
		case DIALOG_ID_PROGRESS:
			// プログレスダイアログ
			progress_stop();
			mProgressDialog = mDialogAction.progress_SPINNER(this, mDialogMessage);
			return mProgressDialog;
		case DIALOG_ID_REACTIVATION: // 他のクライアントからリセットされていた場合の処理(アクティベート) : 23011
			progress_stop();
			mBuilder = mDialogAction.setDialog(this, false, "RESET", mDialogMessage);
			mBuilder.setPositiveButton(R.string.ok, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// ネットワークチェック
					if (Utils.isConnected(getApplicationContext())) {
						if (!mDialogAction.reset_OK(BookendActivity.this)) {
							// 初期化が正常に終わらなかった場合はエラーダイアログ表示
							dialog(DIALOG_ID_ERROR, "INIT : FAIL");
						} else {
							onResume();
						}
					} else {
						removeDialog(id);
						// 初期アクティベーション時はオンラインでなくてはいけない
						dialog(DIALOG_ID_ERROR, getString(R.string.first_activation_offline));
					}
				}
			});
			// アクティベーションしない場合はアプリ終了
			mBuilder.setNegativeButton(R.string.cancel, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					removeDialog(id);
					appFinish();
					return;
				}
			});
			return mBuilder.create();
		case DIALOG_ID_SEND_MAIN:
    		mBuilder = mDialogAction.setDialog(this, false, "ERROR", mDialogMessage);
    		mBuilder.setPositiveButton(this.getString(R.string.ok), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                	removeDialog(id);
                	Intent main = new Intent(BookendActivity.this, MainActivity.class);
                	startActivity(main);
                }
            });
    		return mBuilder.create();
		case DIALOG_ID_NAVI:	// Webサイト誘導ダイヤログ
			if (mDialogAction == null) mDialogAction = new DialogAction();
    		mBuilder = mDialogAction.setDialog(this, false, mDialogTitle, mDialogMessage);
        	// OK
    		mBuilder.setPositiveButton(this.getString(R.string.ok), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                	removeDialog(id);
                	try{
	                	Intent naviSales = new Intent(Intent.ACTION_VIEW);
	                	naviSales.setData(Uri.parse(mNaviUrl));
	                	Logput.v("[NavigateURL] " + mNaviUrl);
	                	startActivity(naviSales);
                	}catch(NullPointerException e){
                		// Webサイト誘導URLがnullだった場合
                		Logput.w(getString(R.string.naviUrl_null), e);
                		Toast.makeText(BookendActivity.this, getString(R.string.naviUrl_null), Toast.LENGTH_SHORT).show();
                	}
                	// xmlに保存してあるwebサイト誘導用ダウンロードIDをnullに
                	if(mPref == null) mPref = new Preferences(getApplicationContext());
        			mPref.setNaviDlId(null);
                }
            });
            // Cancel
    		mBuilder.setNegativeButton(this.getString(R.string.cancel), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                	// xmlに保存してあるwebサイト誘導用ダウンロードIDをnullに
                	if(mPref == null) mPref = new Preferences(getApplicationContext());
        			mPref.setNaviDlId(null);
                	removeDialog(id);
                }
            });
    		return mBuilder.create();
		case DIALOG_BILLING_NOT_SUPPORTED_ID:
			String helpUrl = replaceLanguageAndRegion(getString(R.string.billing_help_url));
	        Logput.v(helpUrl);
	        final Uri helpUri = Uri.parse(helpUrl);
	        if (mDialogAction == null) mDialogAction = new DialogAction();
    		mBuilder = mDialogAction.setDialog(this, false, mDialogTitle, mDialogMessage);
    		mBuilder.setPositiveButton(this.getString(R.string.ok), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                		removeDialog(id);
	                }
	            });
    		// 「詳細」
    		mBuilder.setNegativeButton(this.getString(R.string.learn_more), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                	Intent intent = new Intent(Intent.ACTION_VIEW, helpUri);
                	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    removeDialog(id);
                }
            });
			return mBuilder.create();
		default:
			return null;
		}
	}

	/**
	 * Progress stop
	 */
	public void progress_stop() {
		if (mProgressDialog != null) {
			mProgressDialog.cancel();
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
		removeDialog(DIALOG_ID_PROGRESS);
	}

	/** アプリを終了させる */
	public void appFinish() {
		progress_stop();
		Preferences.sDefaultCheck = false;
		onStop();
		onDestroy();
		Logput.v("------ " + getString(R.string.app_name) + " Finish ------");
		finish();
	}
	
	public void appKill(){
		progress_stop();
		Preferences.sDefaultCheck = false;
		onPause();
		onStop();
		onDestroy();
		Logput.v("------ " + getString(R.string.app_name) + " Finish ------");
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	/**
     * リセットバックグラウンド処理開始
     */
    private void reset(){
    	ResetTask reset = new ResetTask(this, BookendActivity.this);
    	reset.execute("");
    }
	
	/**
	 * ResetTask返り値
	 * @param errorMessage エラーメッセージ
	 */
	@Override
	public void result_reset(String errorMessage){
		if(!StringUtil.isEmpty(errorMessage)){
			dialog(DIALOG_ID_VIEW, errorMessage);
		}else{
			onStart();
			Toast.makeText(this, getString(R.string.reset_complete), Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
     * OSの国・言語設定から文字列を置き換え
     *
     * @param str the string to replace the language/country within
     * @return a string containing the local language and region codes
     */
    private String replaceLanguageAndRegion(String str) {
        // Substitute language and or region if present in string
        if (str.contains("%lang%") || str.contains("%region%")) {
            Locale locale = Locale.getDefault();
            str = str.replace("%lang%", locale.getLanguage().toLowerCase());
            str = str.replace("%region%", locale.getCountry().toLowerCase());
        }
        return str;
    }
	
	/**
	 * Webサイト誘導
	 */
	public String checkNavigate(String downloadID){
		String message = null;
		if(mNaviSalesDao == null) mNaviSalesDao = new NaviSalesDao(getApplicationContext());
		NaviSalesBeans bean = mNaviSalesDao.load(downloadID);
		if(bean == null) return message;
		mNaviUrl = null;
		String naviMessage = bean.getNaviMessage();
		if(!StringUtil.isEmpty(naviMessage)){
			// ロケールにあった言語のメッセージを取得
			String language = Locale.getDefault().toString();
			List<String> messages = StringUtil.parseStr(naviMessage, ';');
			for(int i=0; i<messages.size(); i++){
				if(i%2 == 0){
					if(language.startsWith(messages.get(i))){
						message = messages.get(i + 1);
						continue;
					}
				}
			}
			if(StringUtil.isEmpty(message)){
				// 適する言語のメッセージがない場合は最初に指定されているメッセージを表示
				message = messages.get(1);
			}
			if(!StringUtil.isEmpty(message)){
				// 改行コード「%n」を「\n」に置き換え
				message = message.replaceAll("%n", "\\\n");
			}
			Logput.v("Local language [" + language + "] " + message);
			mNaviUrl = bean.getNaviURL();
		}
		return message;
	}
}
