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
import android.util.Base64;
/**
 * ã‚³ãƒ³ãƒ†ãƒ³ãƒ„é–²è¦§ã‚¿ã‚¹ã‚¯
 * AsyncTask<T1, T2, T3> T1:å®Ÿè¡Œæ™‚ã�«æ¸¡ã�™ã‚¯ãƒ©ã‚¹,T2:é€”ä¸­çµŒé�Žã‚’ä¼�ã�ˆã‚‹ã‚¯ãƒ©ã‚¹,T3:å‡¦ç�†çµ�æžœã‚’ä¼�ã�ˆã‚‹ã‚¯ãƒ©ã‚¹
 * @author Hamaji
 *
 */
public class ViewTask extends AsyncTask<ViewTaskParam, Integer, String> implements ConstList{
	
	/** ãƒªã‚¹ãƒŠãƒ¼ */
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
	
	BookBeans book;
	
	public ViewTask(Activity a, ViewListener listener){
		this.mActivity = a;
		this.mCon = a.getApplicationContext();
		this.mListener = listener;
	}
	
	/**
	 * çµ�æžœé€šçŸ¥ç”¨ã�®ãƒªã‚¹ãƒŠãƒ¼
	 * @author Hamaji
	 *
	 */
	public interface ViewListener {
		public void result_view(String result);
	}
	
	/**
	 * ã‚¹ãƒ¬ãƒƒãƒ‰é–‹å§‹ç›´å¾Œã�«å‘¼ã�³å‡ºã�•ã‚Œã‚‹ - ãƒ—ãƒ­ã‚°ãƒ¬ã‚¹ãƒ�ãƒ¼è¡¨ç¤º
	 */
	@Override  
    protected void onPreExecute(){  
		Logput.v(">>ViewTask Start");
		//progress_start(false, mCon.getString(R.string.progress_processing));
    }

	@Override
	protected String doInBackground(ViewTaskParam... params) {
		book = params[0].mBook;
		boolean isTemporary = params[0].mIsTemporary;
		boolean success = false;
		String strPassword = null;
		try {
			mViewAction = new ViewAction(mCon, mActivity);
			boolean isInstallViewer = false;
			int fileType = -1;
			// ãƒ•ã‚¡ã‚¤ãƒ«ã‚¿ã‚¤ãƒ—ãƒ�ã‚§ãƒƒã‚¯
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
			
			// viewerã‚¢ãƒ—ãƒªã�Œã�‚ã‚‹ã�‹ãƒ�ã‚§ãƒƒã‚¯
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
			//	Adobe Readerã�‹ePubViewerã�ªã‚‰ãƒ�ãƒ¼ã‚¸ãƒ§ãƒ³ãƒ�ã‚§ãƒƒã‚¯ã‚’è¡Œã�†
			try{
				if ((viewerApp == ADOBE_READER) || (viewerApp == EPUB_VIEWER)) {  
					mViewAction.isInstallApp(viewerApp);
					isInstallViewer = true;
				}
			}catch (NameNotFoundException e) {
				// ãƒ€ã‚¤ãƒ¤ãƒ­ã‚°[OK]=>AppMarketå‡¦ç�†
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
			// epubViewerã�®ãƒ�ãƒ¼ã‚¸ãƒ§ãƒ³ãƒ�ã‚§ãƒƒã‚¯
			if(viewerApp == EPUB_VIEWER && isInstallViewer){
				int versionCode = mViewAction.getEPubViewerVersionCode(mCon.getPackageManager(), EPUB_VIEWER);
				if(versionCode < 4){
					// ãƒ�ãƒ¼ã‚¸ãƒ§ãƒ³ã‚³ãƒ¼ãƒ‰4ä»¥ä¸‹ã�¯becãƒ•ã‚¡ã‚¤ãƒ«æœªå¯¾å¿œã�®ã�Ÿã‚�ã‚¢ãƒƒãƒ—ãƒ‡ãƒ¼ãƒˆã‚’ä¿ƒã�™
					Logput.d("UPDATE : ePubViewer version code = " + versionCode);
					publishProgress(EPUBVIEWER_UPDATE);
					return null;
				}
			}
			
			// krpdfä»¥å¤–ã�®ãƒ•ã‚¡ã‚¤ãƒ«ã�¯GetLicenseã�™ã‚‹ï¼ˆkrpdfã�¯RemoveLayer.serviceå†…ã�§ãƒªã‚¯ã‚¨ã‚¹ãƒˆã�™ã‚‹ï¼‰
			if(fileType == PDF){
				publishProgress(VIEWER_START);
				// pdf - ã‚¢ãƒ—ãƒªã‚±ãƒ¼ã‚·ãƒ§ãƒ³é ˜åŸŸã�«ã‚³ãƒ”ãƒ¼(ã‚¢ã‚¯ã‚»ã‚¹æ¨©é™�å¤‰æ›´)ã�—ã€�é–²è¦§é–‹å§‹
				viewPDF(book, isTemporary);
			}
			else if(fileType == KRPDF){
				publishProgress(PROGRESS_PROCESSING);
				//	GetLicense
				book = mViewAction.getLicense(book);
				// krpdf - å¾©å�·åŒ–,é–²è¦§é–‹å§‹
				// Webæ›¸åº«ã�‹ã‚‰DLã�—ã�Ÿå ´å�ˆã�ªã�©ã€�ã�¾ã� ãƒ¬ã‚¤ãƒ¤ãƒ¼ã�Œå‰Šé™¤ã�•ã‚Œã�¦ã�„ã�ªã�„(DBã�«é�µã�Œä¿�å­˜ã�•ã‚Œã�¦ã�„ã�ªã�„)å ´å�ˆã�¯å…ˆã�«å‰Šé™¤ã�—ã�¦æš—å�·åŒ–ã�™ã‚‹
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
				// ********* HoGo Custom ********
				// STEP1: Get PathFile and Passwod of PDF file
				//	password of PDF file
				byte[] userPassword = Utils.getBytesKeyFromHexStr(book.getKey(), 32);
				// Parse password String
				strPassword =  DecryptUtil.base16enc(userPassword);
				Logput.d("userPassword = " + strPassword);
				//	file path of PDF file
				String filePath = book.getFile_path();
				Logput.d("filePath = " + filePath);
				
				//	delete this content
				//mViewAction.temporary(book);
			} 
			else { 
				// epub, krepub, bec, krbec, mcm, epubx, epubxf, krepa ã�®å ´å�ˆ
				//	ãƒ—ãƒ­ã‚°ãƒ¬ã‚¹è¡¨ç¤º
				publishProgress(VIEWER_START);
				//	GetLicense
				book = mViewAction.getLicense(book);
				// ePubViewerèµ·å‹•ãƒ»é–²è¦§é–‹å§‹
				try {
					if(!mViewAction.startViewer(fileType, book, isTemporary)){
						// krkeyã�Œå�–å¾—ã�§ã��ã�ªã�„ã‚¨ãƒ©ãƒ¼
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
			// Temporary ã�ŒæŒ‡å®šã�•ã‚Œã�¦ã�„ã�¦å‡¦ç�†ã�Œå¤±æ•—ã�—ã�Ÿå ´å�ˆã�¯ã‚³ãƒ³ãƒ†ãƒ³ãƒ„ãƒ»ãƒ‡ãƒ¼ã‚¿å‰Šé™¤
			if(!success && isTemporary){
				mViewAction.temporary(book);
			}
		}
		return strPassword;
	}
	
	/**
	 * GUIã�¨å�Œã�˜ã‚¹ãƒ¬ãƒƒãƒ‰ - publishProgress()ã�§è©°ã‚�ã‚‰ã‚Œã�Ÿã‚¯ãƒ©ã‚¹ã�¯ã�“ã�“ã�«é€šçŸ¥ã�•ã‚Œã‚‹
	 */
	@Override
	protected void onProgressUpdate(Integer... progress) {
		try{
			int prog = progress[0];
			if(prog <= -1){	// -1ä»¥ä¸‹ã�®å ´å�ˆã�¯ã‚¨ãƒ©ãƒ¼ã‚¢ãƒ©ãƒ¼ãƒˆè¡¨ç¤º(ãƒ•ã‚¡ã‚¤ãƒ«ã‚¿ã‚¤ãƒ—ã‚¨ãƒ©ãƒ¼)
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
	 * å‡¦ç�†å®Œäº†æ™‚ã�«å‘¼ã�°ã‚Œã‚‹ãƒ¡ã‚½ãƒƒãƒ‰(GUIã�¨å�Œã�˜ã‚¹ãƒ¬ãƒƒãƒ‰)<br>
	 * â€»doInBackground()ã�®æˆ»ã‚Šå€¤ã�Œã�“ã�“ã�«é€šçŸ¥ã�•ã‚Œã‚‹
	 */
	@Override
	protected void onPostExecute(String result) {
		progress_stop();
		if(mListener != null){
			mListener.result_view(result);
		}
		   
		    // ********* HoGo Custom *********
		    // STEP2: Call Print service and send FilePath and Passwwod to Printer
			Intent intent = new Intent(mActivity, jp.co.ricoh.ssdk.sample.app.print.activity.MainActivity.class);
			// Set value of File Path
			intent.putExtra("path", book.getFile_path());
			// The value of Password, result=pdfPassword 
			intent.putExtra("key", result);
			mActivity.startActivity(intent);
	}
	
	/**
	 * ãƒ—ãƒ­ã‚°ãƒ¬ã‚¹ãƒ�ãƒ¼
	 * @param message
	 * @param true:STYLE_HORIZONTAL, false:STYLE_SPINNER
	 */
	private void progress_start(boolean style, String message){
		// ãƒ—ãƒ­ã‚°ãƒ¬ã‚¹ãƒ€ã‚¤ã‚¢ãƒ­ã‚°
		if(style){
			mProgress = new ProgressDialog(this.mActivity);
			mProgress.setMessage(message);
			mProgress.setIcon(R.drawable.icon);
			// ProgressDialog ã�®ç¢ºå®šï¼ˆfalseï¼‰ï¼�ä¸�ç¢ºå®šï¼ˆtrueï¼‰ã‚’è¨­å®š
			mProgress.setIndeterminate(false);
			// ã‚¹ã‚¿ã‚¤ãƒ«ã‚’è¨­å®š
			mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgress.setMax(100); // max ãƒšãƒ¼ã‚¸æ•°%
			mProgress.setProgress(0); // åˆ�æœŸå€¤ 0%
			// ã‚­ãƒ£ãƒ³ã‚»ãƒ«å�¯èƒ½ã�‹ã�©ã�†ã�‹
			mProgress.setCancelable(true);
			// å®Ÿéš›ã�«è¡Œã�„ã�Ÿã�„å‡¦ç�†ã‚’åˆ¥ã‚¹ãƒ¬ãƒƒãƒ‰ã�§å®Ÿè¡Œ
			mProgress.show();
		}else{
			mProgress = new ProgressDialog(this.mActivity);
			mProgress.setIcon(R.drawable.icon);
			// ProgressDialog ã�®ç¢ºå®šï¼ˆfalseï¼‰ï¼�ä¸�ç¢ºå®šï¼ˆtrueï¼‰ã‚’è¨­å®š
			mProgress.setIndeterminate(true);
			// ã‚¹ã‚¿ã‚¤ãƒ«ã‚’è¨­å®š
			mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgress.setMessage(message);
			// ã‚­ãƒ£ãƒ³ã‚»ãƒ«å�¯èƒ½ã�‹ã�©ã�†ã�‹
			mProgress.setCancelable(false);
			// å®Ÿéš›ã�«è¡Œã�„ã�Ÿã�„å‡¦ç�†ã‚’åˆ¥ã‚¹ãƒ¬ãƒƒãƒ‰ã�§å®Ÿè¡Œ
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
	 * pdfãƒ•ã‚¡ã‚¤ãƒ«é–²è¦§
	 * @param book BookBeans
	 */
	private void viewPDF(BookBeans book, boolean isTemporary){
		// ãƒ•ã‚¡ã‚¤ãƒ«ã‚’ã‚¢ãƒ—ãƒªã‚±ãƒ¼ã‚·ãƒ§ãƒ³é ˜åŸŸã�«ã‚³ãƒ”ãƒ¼ï¼ˆã‚¢ã‚¯ã‚»ã‚¹æ¨©é™�ï¼šMODE_WORLD_READABLEï¼‰
		String filePath = book.getFile_path();
		String fileName = new File(filePath).getName();
		try{
			// pdfã‚’ã‚¢ãƒ—ãƒªã‚±ãƒ¼ã‚·ãƒ§ãƒ³é ˜åŸŸã�«ã‚³ãƒ”ãƒ¼ï¼ˆã‚¢ã‚¯ã‚»ã‚¹æ¨©é™�ï¼šèª­ã�¿å�–ã‚Šå�¯èƒ½ã�«ï¼‰
			mViewAction.pdfCopy(COPY_PDF, filePath, fileName, null);
			String copyPath = mCon.getFilesDir()+ "/" + fileName;
			// ã‚ªãƒ³ãƒ©ã‚¤ãƒ³æ™‚ã�®ã�¿GetLicenseãƒªã‚¯ã‚¨ã‚¹ãƒˆ - DBæ›´æ–°
			book = mViewAction.getLicense(book);
			// AdobeReaderã�§é–²è¦§
//			mViewAction.sendAdobeReader(book, copyPath, isTemporary);
		} catch (NullPointerException e){
			Logput.w(e.getMessage(), e);
			publishProgress(CONTENT_NONE);
		} catch (FileNotFoundException e) {
			// æŒ‡å®šãƒ•ã‚¡ã‚¤ãƒ«ã�Œè¦‹ã�¤ã�‹ã‚‰ã�ªã�‹ã�£ã�Ÿå ´å�ˆ
			Logput.w(e.getMessage(), e);
			publishProgress(CONTENT_NONE);
		}catch (ActivityNotFoundException e) {
			// Adobe Readerã�Œã‚¤ãƒ³ã‚¹ãƒˆãƒ¼ãƒ«ã�•ã‚Œã�¦ã�„ã�ªã�„å ´å�ˆã�¯ä¸‹è¨˜ãƒ¡ãƒƒã‚»ãƒ¼ã‚¸ã�Œè¡¨ç¤ºã�•ã‚Œã‚‹
			Logput.w(e.getMessage(), e);
			publishProgress(ADOBEREADER_MARKET);
		} catch (IOException e) {
			// ã‚ªãƒªã‚¸ãƒŠãƒ«PDFèª­ã�¿è¾¼ã�¿ã‚¨ãƒ©ãƒ¼
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
	 * krpdf - å¾©å�·åŒ–
	 * @param book
	 */
	private void decrypt(BookBeans book, boolean isTemporary){
		String filePath = book.getFile_path();
		String fileName = new File(filePath).getName();
		
		// ã‚ªãƒ³ãƒ©ã‚¤ãƒ³æ™‚ã�®ã�¿GetLicenseãƒªã‚¯ã‚¨ã‚¹ãƒˆ - DBæ›´æ–°
		//book = mViewAction.getLicense(book);
		try {
			// å¾©å�·åŒ–ã�—ã�ªã�Œã‚‰ãƒ•ã‚¡ã‚¤ãƒ«ã‚’ã‚³ãƒ”ãƒ¼ã�—ã�¦AdobeReaderã�§é–²è¦§
			String keyStr = book.getEncryptionKey();
			int formatVer = book.getKrpdfFormatVer();
			Logput.d("KRPDF FORMAT VER : now = " + formatVer + "/ new = " + Const.KRPDF_FORMAT_VER);
			if(formatVer == Const.KRPDF_FORMAT_VER){
				byte[] key = DecryptUtil.base64dec(keyStr);
				mViewAction.pdfCopy(COPY_KRPDF_DE, filePath, fileName, key);
			}// â—�ãƒ•ã‚©ãƒ¼ãƒžãƒƒãƒˆãƒ�ãƒ¼ã‚¸ãƒ§ãƒ³ã�Œå¤‰ã‚�ã�£ã�Ÿå ´å�ˆã�¯ã�“ã�“ã�«å‡¦ç�†ã‚’è¿½åŠ 
			String copyPath = mCon.getFilesDir()+ "/" + fileName;
//			mViewAction.sendAdobeReader(book, copyPath, isTemporary);
		} catch (NullPointerException e) {
			Logput.w(e.getMessage(), e);
			publishProgress(CONTENT_NONE);
		} catch (ActivityNotFoundException e) {
			// Adobe Readerã�Œã‚¤ãƒ³ã‚¹ãƒˆãƒ¼ãƒ«ã�•ã‚Œã�¦ã�„ã�ªã�„å ´å�ˆã�¯ä¸‹è¨˜ãƒ¡ãƒƒã‚»ãƒ¼ã‚¸ã�Œè¡¨ç¤ºã�•ã‚Œã‚‹
			Logput.w(e.getMessage(), e);
			publishProgress(ADOBEREADER_MARKET);
		} catch (FileNotFoundException e) {
			// æŒ‡å®šãƒ•ã‚¡ã‚¤ãƒ«ã�Œè¦‹ã�¤ã�‹ã‚‰ã�ªã�‹ã�£ã�Ÿå ´å�ˆ
			Logput.w(e.getMessage(), e);
			publishProgress(CONTENT_NONE);
		} catch (IOException e) {
			// ã‚ªãƒªã‚¸ãƒŠãƒ«PDFèª­ã�¿è¾¼ã�¿ã‚¨ãƒ©ãƒ¼
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
	 * ã‚¨ãƒ©ãƒ¼ã‚¢ãƒ©ãƒ¼ãƒˆ
	 */
	private void errorDialog(String message){
		progress_stop();
		AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
		alert.setMessage(message);
		alert.setNegativeButton(mCon.getString(R.string.ok), null);
		alert.show();
	}
	
	/**
	 * ã‚¨ãƒ©ãƒ¼ã‚¢ãƒ©ãƒ¼ãƒˆ
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
