package net.keyring.bookend.asynctask;

import net.keyring.bookend.R;
import net.keyring.bookend.action.GetStoreViewAction;
import net.keyring.bookend.util.StringUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
/**
 * Store一覧取得表示
 * AsyncTask<T1, T2, T3> T1:実行時に渡すクラス,T2:途中経過を伝えるクラス,T3:処理結果を伝えるクラス
 * @author Hamaji
 *
 */
public class StoreTask extends AsyncTask<String, String, String> {

	/** Activity */
	private Activity a;
	/** progressDialog */
	private ProgressDialog progress;
	
	/**
	 * コンストラクタ
	 * @param con Context
	 */
	public StoreTask(Activity a){
		this.a = a;
	}
	
	/**
	 * バックグラウンド処理(GUIとは別のスレッド)	※この中からUIをさわってはダメ
	 * @param リクエストパラメータ
	 * @retun 取得エラーメッセージ、問題なければNULL
	 */
	@Override
	protected String doInBackground(String... params) {
		GetStoreViewAction store = new GetStoreViewAction(a.getApplicationContext());
		String errorMessage = store.execute();
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
		if(!StringUtil.isEmpty(result)){
			AlertDialog.Builder dialog = new AlertDialog.Builder(a);
			dialog.setIcon(R.drawable.icon);
			dialog.setTitle(result);
			dialog.show();
		}
	}
	
	/**
	 * プログレスバー
	 * @param true:STYLE_HORIZONTAL,false:STYLE_SPINNER
	 */
	private void progress_start(){
		// プログレスダイアログ
		progress = new ProgressDialog(this.a);
		progress.setIcon(R.drawable.icon);
		// ProgressDialog の確定（false）／不確定（true）を設定
		progress.setIndeterminate(true);
		// スタイルを設定
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setMessage(a.getString(R.string.get_store));
		// 実際に行いたい処理を別スレッドで実行
		progress.show();
	}
	
	/**
	 * プログレスバーストップ
	 */
	private void progress_stop(){
		if(progress != null){
			progress.cancel();
			progress.dismiss();
		}
	}
}
