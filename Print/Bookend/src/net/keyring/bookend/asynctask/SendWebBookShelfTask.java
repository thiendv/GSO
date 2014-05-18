package net.keyring.bookend.asynctask;

import java.util.ArrayList;

import net.keyring.bookend.R;
import net.keyring.bookend.action.RegistConfirmAction;
import net.keyring.bookend.constant.ConstRegist;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
/**
 * Web書庫に遷移する前チェック
 * AsyncTask<T1, T2, T3> T1:実行時に渡すクラス,T2:途中経過を伝えるクラス,T3:処理結果を伝えるクラス
 * @author Hamaji
 *
 */
public class SendWebBookShelfTask extends AsyncTask<String, Integer, Integer>  implements ConstRegist{
	
	/** 取得する更新情報の最大数(省略時無制限) */
	private final String DATA_MAX = "";

	/** リスナー */
	private RegistConfirmListener mListener = null;
	/** Context */
	private Context mCon;
	/** Activity */
	private Activity mActivity;
	/** progressDialog */
	private ProgressDialog mProgress;
	/** Action */
	private RegistConfirmAction mAction;
	
	public SendWebBookShelfTask(RegistConfirmListener listener, Activity activity){
		this.mListener = listener;
		this.mActivity = activity;
		this.mCon = activity.getApplicationContext();
	}
	
	/**
	 * 結果通知用のリスナー
	 * @author Hamaji
	 *
	 */
	public interface RegistConfirmListener {
		public void result_registConfirm(int message);
	}
	
	/**
	 * スレッド開始直後に呼び出される - プログレスバー表示
	 */
	@Override  
    protected void onPreExecute(){
		progress_start(mCon.getString(R.string.checking));
    }
	
	/**
	 * バックグラウンド処理(GUIとは別のスレッド)	※この中からUIをさわってはダメ
	 * @param リクエストパラメータ ※未使用
	 * @retun 処理ステータスコード
	 */
	@Override
	protected Integer doInBackground(String... params) {
		int status = NO_SEND;
		if(mAction == null) mAction = new RegistConfirmAction(mCon);
		if(mAction.isRegist()){
			// メールアドレス登録済み => getContentsしてWeb書庫へ
			status = mAction.getAwsInfo();
			if(status == -1){
				publishProgress(10);
				// UpdateContentsリクエスト(UpdateLicenseレコードにに保存されているものがあった場合はそれをUpdateLicenseリクエストする)
				mAction.updateContents();
				publishProgress(30);
				// GetContentsリクエスト
				ArrayList<NameValuePair> param = mAction.setParams(DATA_MAX);
				publishProgress(50);
				XmlPullParser parser = mAction.getContents(param);
				publishProgress(80);
				String getContentsStatus = mAction.getResponseList(parser);
				publishProgress(100);
				// GetContentsレスポンス処理
				status = mAction.checkGetContentsStatus(getContentsStatus);
			}
		}else{
			// メールアドレス未登録 => メールアドレス登録画面へ
			publishProgress(100);
			status = SEND_REGIST_MAILADDRESS;
		}
		return status;
	}
	
	/**
	 * GUIと同じスレッド - publishProgress()で詰められたクラスはここに通知される
	 */
	@Override
	protected void onProgressUpdate(Integer... progress) {
		int prog = progress[0];
		mProgress.setProgress(progress[0]);
		if(prog != 0){
			mProgress.setTitle(mCon.getString(R.string.getting));
		}
	}
	
	/**
	 * 処理完了時に呼ばれるメソッド(GUIと同じスレッド)<br>
	 * ※doInBackground()の戻り値がここに通知される
	 */
	@Override
	protected void onPostExecute(Integer result) {
		progress_stop();
		if (mListener != null) {
			mListener.result_registConfirm(result);
		}
	}
	
	/**
	 * ダイヤログ
	 * @param type 表示ダイアログスタイルフラグ
	 * @param title ダイアログタイトル
	 * @param message ダイアログメッセージ
	 */
	public void progress_start(String message){
		progress_stop();
		mProgress = new ProgressDialog(this.mActivity);
		mProgress.setIcon(R.drawable.icon);
		mProgress.setTitle(message);
		// スタイルを設定
		mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		// ProgressDialog の確定（false）／不確定（true）を設定
		mProgress.setIndeterminate(false);
		mProgress.setMax(100); // max 100%
		mProgress.setProgress(0); // 初期値 0%
		mProgress.setCancelable(false);	//	キャンセル不可
		mProgress.show();
	}
	
	private void progress_stop(){
		if(mProgress != null){
			mProgress.cancel();
			mProgress.dismiss();
		}
	}
	
}
