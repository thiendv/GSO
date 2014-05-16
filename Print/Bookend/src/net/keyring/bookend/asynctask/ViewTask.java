package net.keyring.bookend.asynctask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.keyring.bookend.Logput;
import net.keyring.bookend.R;
import net.keyring.bookend.action.RemoveLayerAction;
import net.keyring.bookend.action.ViewAction;
import net.keyring.bookend.bean.BookBeans;
import net.keyring.bookend.constant.Const;
import net.keyring.bookend.constant.ConstList;
import net.keyring.bookend.util.DecryptUtil;
import net.keyring.bookend.util.StringUtil;
import net.keyring.bookend.util.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
/**
 * コンテンツ閲覧タスク
 * AsyncTask<T1, T2, T3> T1:実行時に渡すクラス,T2:途中経過を伝えるクラス,T3:処理結果を伝えるクラス
 * @author Hamaji
 *
 */
public class ViewTask extends AsyncTask<ViewTaskParam, Integer, String> implements ConstList{
	
	/** リスナー */
	private ViewListener mListener = null;
	/** Activity */
	private Activity mActivity;
	/** progressDialog */
	private ProgressDialog mProgress;
	/** Context */
	private Context mCon;
	/** ViewAction */
	private ViewAction mViewAction;
	/** RemoveLayer */
	private RemoveLayerAction mRemoveLayer;
	
	private final int FILE_TYPE_ERROR = -1;
	private final int EPUBVIEWER_MARKET = -2;
	private final int EPUBVIEWER_UPDATE = -3;
	private final int ADOBEREADER_MARKET = -4;
	private final int ERROR_REMOVE_LAYER = -5;
	private final int CONTENT_NONE = -6;
	private final int ERROR_VIEW_PDF = -7;
	private final int VIEWER_START = -8;
	private final int KEY_NULL = -9;
	private final int ERROR_SEND_MARKET = -10;
	private final int PROGRESS_PROCESSING = -11;
	
	public ViewTask(Activity a, ViewListener listener){
		this.mActivity = a;
		this.mCon = a.getApplicationContext();
		this.mListener = listener;
	}
	
	/**
	 * 結果通知用のリスナー
	 * @author Hamaji
	 *
	 */
	public interface ViewListener {
		public void result_view(String result);
	}
	
	/**
	 * スレッド開始直後に呼び出される - プログレスバー表示
	 */
	@Override  
    protected void onPreExecute(){  
		Logput.v(">>ViewTask Start");
		//progress_start(false, mCon.getString(R.string.progress_processing));
    }

	@Override
	protected String doInBackground(ViewTaskParam... params) {
		BookBeans book = params[0].mBook;
		boolean isTemporary = params[0].mIsTemporary;
		boolean success = false;
		try {
			mViewAction = new ViewAction(mCon, mActivity);
			boolean isInstallViewer = false;
			int fileType = -1;
			// ファイルタイプチェック
			try{
				fileType = mViewAction.fileTypeCheck(book.getType());
				if(fileType == -1){
					Logput.d("filetype is NG");
					publishProgress(FILE_TYPE_ERROR);
				}
			}catch(NullPointerException e){
				Logput.w("filetype is null", e);
				publishProgress(FILE_TYPE_ERROR);
			}
			
			// viewerアプリがあるかチェック
			String viewerApp = null;
			if(fileType == PDF || fileType == KRPDF || fileType == KRPDFX){
				viewerApp = ADOBE_READER;
			} else if (fileType == EPUB || fileType == KREPUB || fileType == BEC || fileType == KRBEC){
				viewerApp = EPUB_VIEWER;			
			} else if (fileType == MCM || fileType == EPUBX || fileType == EPUBXF ){
				viewerApp = MCM_PUBX_PUBXF_VIEWER;			
			} else if (fileType == KREPA){
				viewerApp = AUDIO_VIEWER;
			}
		/*			
			//	Adobe ReaderかePubViewerならバージョンチェックを行う
			try{
				if ((viewerApp == ADOBE_READER) || (viewerApp == EPUB_VIEWER)) {  
					mViewAction.isInstallApp(viewerApp);
					isInstallViewer = true;
				}
			}catch (NameNotFoundException e) {
				// ダイヤログ[OK]=>AppMarket処理
				Logput.w(viewerApp + " is not found.");
				if(viewerApp == ADOBE_READER){
					publishProgress(ADOBEREADER_MARKET);
					return null;
				}else if(viewerApp == EPUB_VIEWER){
					publishProgress(EPUBVIEWER_MARKET);
					return null;
				}
			}
		*/	
			// epubViewerのバージョンチェック
			if(viewerApp == EPUB_VIEWER && isInstallViewer){
				int versionCode = mViewAction.getEPubViewerVersionCode(mCon.getPackageManager(), EPUB_VIEWER);
				if(versionCode < 4){
					// バージョンコード4以下はbecファイル未対応のためアップデートを促す
					Logput.d("UPDATE : ePubViewer version code = " + versionCode);
					publishProgress(EPUBVIEWER_UPDATE);
					return null;
				}
			}
			
			// krpdf以外のファイルはGetLicenseする（krpdfはRemoveLayer.service内でリクエストする）
			if(fileType == PDF){
				publishProgress(VIEWER_START);
				// pdf - アプリケーション領域にコピー(アクセス権限変更)し、閲覧開始
				viewPDF(book, isTemporary);
			}
			else if(fileType == KRPDF){
				publishProgress(PROGRESS_PROCESSING);
				//	GetLicense
				book = mViewAction.getLicense(book);
				// krpdf - 復号化,閲覧開始
				// Web書庫からDLした場合など、まだレイヤーが削除されていない(DBに鍵が保存されていない)場合は先に削除して暗号化する
				String key = book.getEncryptionKey();
				if(StringUtil.isEmpty(key)){
					if(mRemoveLayer == null) mRemoveLayer = new RemoveLayerAction(mCon);
					book = mRemoveLayer.krpdfProcess(fileType, book);
				}
				decrypt(book, isTemporary);				
			}
			else if(fileType == KRPDFX) {
				publishProgress(PROGRESS_PROCESSING);
				//	GetLicense
				book = mViewAction.getLicense(book);
				
				// -----------------------------------------------
				//	password of PDF file
				byte[] userPassword = Utils.getBytesKeyFromHexStr(book.getKey(), 32);
				Logput.d("userPassword = " + DecryptUtil.base16enc(userPassword));
				//	file path of PDF file
				String filePath = book.getFile_path();
				Logput.d("filePath = " + filePath);
				
				//	do something here.
				// -----------------------------------------------
				
				//	delete this content
				//mViewAction.temporary(book);
			} 
			else { 
				// epub, krepub, bec, krbec, mcm, epubx, epubxf, krepa の場合
				//	プログレス表示
				publishProgress(VIEWER_START);
				//	GetLicense
				book = mViewAction.getLicense(book);
				// ePubViewer起動・閲覧開始
				try {
					if(!mViewAction.startViewer(fileType, book, isTemporary)){
						// krkeyが取得できないエラー
						publishProgress(KEY_NULL);
					}
				} catch (Exception e) {
					e.printStackTrace();
					return "File copy error.";
				}
			}
			
			success = true;
		}
		finally {
			// Temporary が指定されていて処理が失敗した場合はコンテンツ・データ削除
			if(!success && isTemporary){
				mViewAction.temporary(book);
			}
		}
		return null;
	}
	
	/**
	 * GUIと同じスレッド - publishProgress()で詰められたクラスはここに通知される
	 */
	@Override
	protected void onProgressUpdate(Integer... progress) {
		try{
			int prog = progress[0];
			if(prog <= -1){	// -1以下の場合はエラーアラート表示(ファイルタイプエラー)
				switch(prog){
				case FILE_TYPE_ERROR:
					errorDialog(mCon.getString(R.string.filetype_error));
					break;
				case CONTENT_NONE:
					errorDialog(mCon.getString(R.string.contents_no));
					break;
				case EPUBVIEWER_MARKET:
					sendMarketDialog(mCon.getString(R.string.viewer_install), mCon.getString(R.string.caution_epubviewer), EPUB_VIEWER);
					break;
				case EPUBVIEWER_UPDATE:
					sendMarketDialog(mCon.getString(R.string.viewer_update), mCon.getString(R.string.epubviewer_versionup), EPUB_VIEWER);
					break;
				case ADOBEREADER_MARKET:
					sendMarketDialog(mCon.getString(R.string.viewer_install), mCon.getString(R.string.caution_adobereader), ADOBE_READER);
					break;
				case ERROR_REMOVE_LAYER:
					errorDialog(mCon.getString(R.string.error_remove_layer));
					break;
				case ERROR_VIEW_PDF:
					errorDialog(mCon.getString(R.string.error_view_pdf));
					break;
				case VIEWER_START:
					progress_start(false, mCon.getString(R.string.viewer_start));
					break;
				case KEY_NULL:
					errorDialog(mCon.getString(R.string.key_fail));
					break;
				case ERROR_SEND_MARKET:
					errorDialog(mCon.getString(R.string.error_send_market));
					break;
				case PROGRESS_PROCESSING:
					if(mProgress == null){
						progress_start(false, mCon.getString(R.string.progress_processing));
					}
					break;
				}
			}else{
				this.mProgress.setProgress(prog);
				if(prog == 0){
					progress_stop();
					progress_start(true, mCon.getString(R.string.progress_processing));
				}else if(prog == 100){
					progress_stop();
					progress_start(false, mCon.getString(R.string.viewer_start));
				}
			}
		}catch(NullPointerException e){
			progress_start(true, mCon.getString(R.string.progress_processing));
		}
	}
	
	/**
	 * 処理完了時に呼ばれるメソッド(GUIと同じスレッド)<br>
	 * ※doInBackground()の戻り値がここに通知される
	 */
	@Override
	protected void onPostExecute(String result) {
		progress_stop();
		if(mListener != null){
			mListener.result_view(result);
		}
	}
	
	/**
	 * プログレスバー
	 * @param message
	 * @param true:STYLE_HORIZONTAL, false:STYLE_SPINNER
	 */
	private void progress_start(boolean style, String message){
		// プログレスダイアログ
		if(style){
			mProgress = new ProgressDialog(this.mActivity);
			mProgress.setMessage(message);
			mProgress.setIcon(R.drawable.icon);
			// ProgressDialog の確定（false）／不確定（true）を設定
			mProgress.setIndeterminate(false);
			// スタイルを設定
			mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgress.setMax(100); // max ページ数%
			mProgress.setProgress(0); // 初期値 0%
			// キャンセル可能かどうか
			mProgress.setCancelable(true);
			// 実際に行いたい処理を別スレッドで実行
			mProgress.show();
		}else{
			mProgress = new ProgressDialog(this.mActivity);
			mProgress.setIcon(R.drawable.icon);
			// ProgressDialog の確定（false）／不確定（true）を設定
			mProgress.setIndeterminate(true);
			// スタイルを設定
			mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgress.setMessage(message);
			// キャンセル可能かどうか
			mProgress.setCancelable(false);
			// 実際に行いたい処理を別スレッドで実行
			mProgress.show();
		}
	}
	
	private void progress_stop(){
		if(mProgress != null){
			mProgress.cancel();
			mProgress.dismiss();
			mProgress = null;
		}
	}
	
	/**
	 * pdfファイル閲覧
	 * @param book BookBeans
	 */
	private void viewPDF(BookBeans book, boolean isTemporary){
		// ファイルをアプリケーション領域にコピー（アクセス権限：MODE_WORLD_READABLE）
		String filePath = book.getFile_path();
		String fileName = new File(filePath).getName();
		try{
			// pdfをアプリケーション領域にコピー（アクセス権限：読み取り可能に）
			mViewAction.pdfCopy(COPY_PDF, filePath, fileName, null);
			String copyPath = mCon.getFilesDir()+ "/" + fileName;
			// オンライン時のみGetLicenseリクエスト - DB更新
			book = mViewAction.getLicense(book);
			// AdobeReaderで閲覧
			mViewAction.sendAdobeReader(book, copyPath, isTemporary);
		} catch (NullPointerException e){
			Logput.w(e.getMessage(), e);
			publishProgress(CONTENT_NONE);
		} catch (FileNotFoundException e) {
			// 指定ファイルが見つからなかった場合
			Logput.w(e.getMessage(), e);
			publishProgress(CONTENT_NONE);
		}catch (ActivityNotFoundException e) {
			// Adobe Readerがインストールされていない場合は下記メッセージが表示される
			Logput.w(e.getMessage(), e);
			publishProgress(ADOBEREADER_MARKET);
		} catch (IOException e) {
			// オリジナルPDF読み込みエラー
			Logput.w(e.getMessage(), e);
			publishProgress(ERROR_VIEW_PDF);
		} catch (Exception e){
			book = null;
			if(!StringUtil.isEmpty(fileName)){
				mCon.deleteFile(fileName + ".pdf");
			}
			Logput.w(e.getMessage(), e);
		}
	}
	
	/**
	 * krpdf - 復号化
	 * @param book
	 */
	private void decrypt(BookBeans book, boolean isTemporary){
		String filePath = book.getFile_path();
		String fileName = new File(filePath).getName();
		
		// オンライン時のみGetLicenseリクエスト - DB更新
		//book = mViewAction.getLicense(book);
		try {
			// 復号化しながらファイルをコピーしてAdobeReaderで閲覧
			String keyStr = book.getEncryptionKey();
			int formatVer = book.getKrpdfFormatVer();
			Logput.d("KRPDF FORMAT VER : now = " + formatVer + "/ new = " + Const.KRPDF_FORMAT_VER);
			if(formatVer == Const.KRPDF_FORMAT_VER){
				byte[] key = DecryptUtil.base64dec(keyStr);
				mViewAction.pdfCopy(COPY_KRPDF_DE, filePath, fileName, key);
			}// ●フォーマットバージョンが変わった場合はここに処理を追加
			String copyPath = mCon.getFilesDir()+ "/" + fileName;
			mViewAction.sendAdobeReader(book, copyPath, isTemporary);
		} catch (NullPointerException e) {
			Logput.w(e.getMessage(), e);
			publishProgress(CONTENT_NONE);
		} catch (ActivityNotFoundException e) {
			// Adobe Readerがインストールされていない場合は下記メッセージが表示される
			Logput.w(e.getMessage(), e);
			publishProgress(ADOBEREADER_MARKET);
		} catch (FileNotFoundException e) {
			// 指定ファイルが見つからなかった場合
			Logput.w(e.getMessage(), e);
			publishProgress(CONTENT_NONE);
		} catch (IOException e) {
			// オリジナルPDF読み込みエラー
			Logput.w(e.getMessage(), e);
			publishProgress(ERROR_VIEW_PDF);
		} catch (Exception e){
			book = null;
			if(!StringUtil.isEmpty(fileName)){
				mCon.deleteFile(fileName + ".pdf");
			}
			Logput.w(e.getMessage(), e);
		}
	}
	
	/**
	 * エラーアラート
	 */
	private void errorDialog(String message){
		progress_stop();
		AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
		alert.setMessage(message);
		alert.setNegativeButton(mCon.getString(R.string.ok), null);
		alert.show();
	}
	
	/**
	 * エラーアラート
	 */
	private void sendMarketDialog(String title, String message, final String marketID){
		progress_stop();
		AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
		alert.setTitle(title);
		alert.setMessage(message);
		alert.setPositiveButton(mCon.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	Logput.v("SEND APP MARKET : ID = " + marketID);
            	//Preferences.sDefaultCheck = false;
            	Intent market = new Intent(Intent.ACTION_VIEW);
            	market.setData(Uri.parse("market://details?id=" + marketID));
            	try{
            		mActivity.startActivity(market);
            	}catch(ActivityNotFoundException e){
            		Logput.w(e.getMessage(), e);
            		publishProgress(ERROR_SEND_MARKET);
            	}
            }
        });
		alert.setNegativeButton(mCon.getString(R.string.cancel), null);
		alert.show();
	}
}
