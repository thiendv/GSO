package net.keyring.bookend.asynctask;

import java.io.File;

import net.keyring.bookend.ImageCache;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.R;
import net.keyring.bookend.constant.Const;
import net.keyring.bookend.db.ContentsDao;
import net.keyring.bookend.db.NaviSalesDao;
import net.keyring.bookend.db.UpdateContentsDao;
import net.keyring.bookend.db.UpdateLicenseDao;
import net.keyring.bookend.request.Activate2;
import net.keyring.bookend.request.Reset;
import net.keyring.bookend.util.FileUtil;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
/**
 * Reset処理をバックグラウンドで行う
 * AsyncTask<T1, T2, T3> T1:実行時に渡すクラス,T2:途中経過を伝えるクラス,T3:処理結果を伝えるクラス
 * @author Hamaji
 *
 */
public class ResetTask extends AsyncTask<String, String, String> {

	/** Activity */
	private Activity		mActivity;
	/** progressDialog */
	private ProgressDialog	mProgress;
	/** リスナー */
	private ResetListener	mListener = null;
	
	/**
	 * 結果通知用のリスナー
	 * @author Hamaji
	 *
	 */
	public interface ResetListener {
		public void result_reset(String result);
	}
	
	/**
	 * コンストラクタ
	 * @param a Activity
	 */
	public ResetTask(ResetListener listener, Activity a){
		this.mActivity = a;
		this.mListener = listener;
	}
	
	/**
	 * バックグラウンド処理(GUIとは別のスレッド)	※この中からUIをさわってはダメ
	 * @param リクエストパラメータ
	 * @retun 取得エラーメッセージ、問題なければNULL
	 */
	@Override
	protected String doInBackground(String... params) {
		String errorMessage = null;
		// 自分もリセット
		Preferences pref = new Preferences(mActivity.getApplicationContext());
		String libraryID = pref.getLibraryID();
		Reset reset = new Reset();
		if(reset.execute(mActivity.getApplicationContext(), libraryID, true)){
			// Reset - OK
			// ダウンロード済みのすべてのコンテンツを削除
			if(Preferences.sExternalStrage == null) Preferences.sExternalStrage = mActivity.getExternalFilesDir(Const.BOOKEND).getPath();
			FileUtil.deleteFile(new File(Preferences.sExternalStrage + "/" + Const.CONTENTS_DIR_NAME));
			// サムネイル削除
			FileUtil.deleteFile(new File(Preferences.sExternalStrage + "/" + Const.THUMBS_DIR_NAME));
			// 念のため、アプリケーション領域のコンテンツフォルダも削除
			FileUtil.deleteFile(mActivity.getFilesDir());
			// DB初期化
			ContentsDao contentsDao = new ContentsDao(mActivity.getApplicationContext());
			contentsDao.delete(-1);
			UpdateContentsDao updateContentsDao = new UpdateContentsDao(mActivity.getApplicationContext());
			updateContentsDao.delete(-1);
			UpdateLicenseDao updateLicenseDao = new UpdateLicenseDao(mActivity.getApplicationContext());
			updateLicenseDao.delete(-1);
			NaviSalesDao naviSalesDao = new NaviSalesDao(mActivity.getApplicationContext());
			naviSalesDao.delete(-1);
			// cache削除
			ImageCache.clearCache();
			// Activate2リクエスト
			Activate2 activate = new Activate2(mActivity.getApplicationContext());
			if(activate.execute()){
	    		// setting.xmlに保存してあるLastSyncDateも初期化
	    		pref.setLastSyncDate_book(null);
			}else{
				errorMessage =  activate.getDescription();
			}
		}else{
			// Reset - NG
			errorMessage = reset.getDescription();
		}
		return errorMessage;
	}
	
	/**
	 * スレッド開始直後に呼び出される - プログレスバー表示
	 */
	@Override  
    protected void onPreExecute(){
		progress_start();
    }
	
	/**
	 * 処理完了時に呼ばれるメソッド(GUIと同じスレッド)<br>
	 * ※doInBackground()の戻り値がここに通知される
	 */
	@Override
	protected void onPostExecute(String result) {
		progress_stop();
		if (mListener != null) {
			mListener.result_reset(result);
		}
	}
	
	/**
	 * プログレスバー
	 * @param message
	 * @param true:STYLE_HORIZONTAL,false:STYLE_SPINNER
	 */
	private void progress_start(){
		// プログレスダイアログ
		mProgress = new ProgressDialog(this.mActivity);
		mProgress.setIcon(R.drawable.icon);
		// ProgressDialog の確定（false）／不確定（true）を設定
		mProgress.setIndeterminate(true);
		// スタイルを設定
		mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgress.setMessage(mActivity.getString(R.string.reset));
		// 実際に行いたい処理を別スレッドで実行
		mProgress.show();
	}
	
	/**
	 * プログレスバーストップ
	 */
	private void progress_stop(){
		if(mProgress != null){
			mProgress.cancel();
			mProgress.dismiss();
		}
	}
}
