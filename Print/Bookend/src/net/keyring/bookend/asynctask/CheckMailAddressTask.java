package net.keyring.bookend.asynctask;

import net.keyring.bookend.Preferences;
import net.keyring.bookend.R;
import net.keyring.bookend.constant.ConstReq;
import net.keyring.bookend.request.CheckMailAddress;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
/**
 * CheckMailAddressリクエストをバックグラウンド処理
 * AsyncTask<T1, T2, T3> T1:実行時に渡すクラス,T2:途中経過を伝えるクラス,T3:処理結果を伝えるクラス
 * @author Hamaji
 *
 */
public class CheckMailAddressTask extends AsyncTask<String, String, String> implements ConstReq {

	/** リスナー */
	private CheckAddressListener mListener = null;
	/** Activity */
	private Activity mActivity;
	/** progressDialog */
	private ProgressDialog mProgress;
	
	/**
	 * 結果通知用のリスナー
	 * @author Hamaji
	 *
	 */
	public interface CheckAddressListener{
		public void result_checkAddress(String mailAddress);
	}
	
	/**
	 * コンストラクタ
	 * @param listener CheckAddressListener
	 * @param a Activity
	 */
	public CheckMailAddressTask(CheckAddressListener listener, Activity a){
		this.mListener = listener;
		this.mActivity = a;
	}
	
	/**
	 * バックグラウンド処理(GUIとは別のスレッド)	※この中からUIをさわってはダメ
	 * @param リクエストパラメータ
	 * @retun 登録メールアドレス(未登録の場合はnull)
	 */
	@Override
	protected String doInBackground(String... params) {
		CheckMailAddress checkMailAddress = new CheckMailAddress(mActivity.getApplicationContext());
		checkMailAddress.execute();
		return Preferences.sMailAddress;
	}
	
	/**
	 * 処理完了時に呼ばれるメソッド(GUIと同じスレッド)<br>
	 * ※doInBackground()の戻り値がここに通知される
	 */
	@Override
	protected void onPostExecute(String result) {
		progress_stop();
		if (mListener != null) {
			mListener.result_checkAddress(result);
		}
	}
	
	/**
	 * スレッド開始直後に呼び出される - プログレスバー表示
	 */
	@Override  
    protected void onPreExecute(){
		progress_start(mActivity.getString(R.string.regist_check));
    }
	
	/**
	 * プログレスバー
	 * @param message
	 * @param true:STYLE_HORIZONTAL,false:STYLE_SPINNER
	 */
	private void progress_start(String message){
		// プログレスダイアログ
		mProgress = new ProgressDialog(this.mActivity);
		mProgress.setIcon(R.drawable.icon);
		// ProgressDialog の確定（false）／不確定（true）を設定
		mProgress.setIndeterminate(true);
		// スタイルを設定
		mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgress.setMessage(message);
		// 実際に行いたい処理を別スレッドで実行
		mProgress.show();
	}
	
	private void progress_stop(){
		if(mProgress != null){
			mProgress.cancel();
			mProgress.dismiss();
		}
	}
}
