package net.keyring.bookend.asynctask;

import java.util.HashMap;
import java.util.Map;

import net.keyring.bookend.R;
import net.keyring.bookend.action.StartUpAction;
import net.keyring.bookend.constant.ConstStartUp;
import net.keyring.bookend.request.GetVersion;
import android.content.Context;
import android.os.AsyncTask;
/**
 * 初期起動時チェック(バックグラウンド処理)
 * AsyncTask<T1, T2, T3> T1:実行時に渡すクラス,T2:途中経過を伝えるクラス,T3:処理結果を伝えるクラス
 * 
 * @author Hamaji
 *
 */
public class StartUpCheckTask extends AsyncTask<Context, String, Map<String,Object>> implements ConstStartUp{

	/** リスナー */
	private StartUpCheckListener listener = null;
	
	/**
	 * 結果通知用のリスナー
	 * @author Hamaji
	 *
	 */
	public interface StartUpCheckListener {
		public void result_startUpCheck(Map<String,Object> result);
	}
	
	/**
	 * コンストラクタで処理完了時のリスナーを渡しておく
	 * 
	 * @param listener
	 */
	public StartUpCheckTask(StartUpCheckListener listener){
		this.listener = listener;
	}
	
	/**
	 * バックグラウンド処理(GUIとは別のスレッド)	※この中からUIをさわってはダメ
	 * @param リクエストパラメータ
	 * @retun エラーが有った場合はエラーメッセージ
	 */
	@Override
	protected Map<String,Object> doInBackground(Context... params) {
		Context con =  params[0];
		
		StartUpAction startUpCheck = new StartUpAction(con);
		int checkFlag = startUpCheck.startUpCheck();
		Map<String,Object> resultList = check_startUp(con, checkFlag, startUpCheck);
		
		startUpCheck.checkDlFailContents();
		
		return resultList;
	}
	
	/**
	 * GUIと同じスレッド
	 */
	@Override
	protected void onProgressUpdate(String... values) {
		// publishProgress()で詰められたクラスはここに通知される
	}

	/**
	 * 処理完了時に呼ばれるメソッド(GUIと同じスレッド)<br>
	 * ※doInBackground()の戻り値がここに通知される
	 */
	@Override
	protected void onPostExecute(Map<String,Object> result) {
		if (listener != null) {
			listener.result_startUpCheck(result);
		}
	}
	
	/**
	 * 起動時チェック処理
	 * @param status　ステータスコード
	 * @return 問題なければnull,それ以外はエラーメッセージを返す
	 */
	private Map<String,Object> check_startUp(Context con, int status, StartUpAction action){
		Map<String,Object> resultList = new HashMap<String,Object>();
		switch(status){
		case CHECK_OK:
			resultList.put(STATUS_KEY, status);
			return resultList;
    	case CHECK_ERROR_SD:	// SDカードがセットされていない
    		resultList.put(STATUS_KEY, status);
    		resultList.put(DIALOG_MESSAGE, con.getString(R.string.caution_sdcard));
    		return resultList;
    	case CHECK_ERROR_ACTIVATION:	// Activationエラー
    		resultList.put(STATUS_KEY, status);
    		resultList.put(DIALOG_MESSAGE, action.getErrorMessage());
    		return resultList;
    	case CHECK_OFFLINE_ACTIVATION:	// 初回Activationオフラインエラー
    		resultList.put(STATUS_KEY, status);
    		resultList.put(DIALOG_MESSAGE, con.getString(R.string.first_activation_offline));
    		return resultList;
    	case CHECK_ERROR_CHECKACTIVATION:	// CheckActivationエラー
    		resultList.put(STATUS_KEY, status);
    		resultList.put(DIALOG_MESSAGE, action.getErrorMessage());
    		return resultList;
    	case CHECK_REQUEST_ACTIVATION:	// 再Activation確認ダイアログ
    		resultList.put(STATUS_KEY, status);
    		resultList.put(DIALOG_MESSAGE, con.getString(R.string.reset_init_restart, con.getString(R.string.app_name)));
    		return resultList;
    	case CHECK_ERROR_CLOCK:	// OS時計設定エラー
    		resultList.put(STATUS_KEY, status);
    		resultList.put(DIALOG_MESSAGE, con.getString(R.string.clock_check_error));
    		return resultList;
    	case CHECK_VERSIONUP_REQUEST:	// バージョンアップリクエスト必要
    		GetVersion getVersion = new GetVersion();
    		return getVersion.execute(con, resultList);
    	default:
    		return null;
    	}
	}

}
