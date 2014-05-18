package net.keyring.bookend.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.keyring.bookend.Logput;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.R;
import net.keyring.bookend.bean.BookBeans;
import net.keyring.bookend.constant.Const;
import net.keyring.bookend.constant.ConstList;
import net.keyring.bookend.db.ContentsDao;
import net.keyring.bookend.request.GetLicense;
import net.keyring.bookend.request.UpdateContents;
import net.keyring.bookend.request.UpdateLicense;
import net.keyring.bookend.service.ProcessCheckService;
import net.keyring.bookend.util.DateUtil;
import net.keyring.bookend.util.DecryptUtil;
import net.keyring.bookend.util.FileUtil;
import net.keyring.bookend.util.Utils;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

public class ViewAction implements ConstList{
	
	/** Context */
	private Context mCon;
	
	private Activity mActivity;
	/** Preferences */
	private Preferences mPref;
	/** ContentsDao */
	private ContentsDao mDao;
	
	/**
	 * ViewActionクラスコンストラクタ
	 * @param con Context
	 */
	public ViewAction(Context con, Activity activity){
		this.mCon = con;
		this.mActivity = activity;
	}
	
	/**
	 * ビューワでコンテンツ閲覧
	 * @param fileType ファイルタイプフラグ
	 * @param book BookBeans
	 * @return 暗号化キーが不明の場合(暗号化ファイル)falseを返す
	 */
	public boolean startViewer(int fileType, BookBeans book, boolean isTemporary) throws Exception{
		// オンライン閲覧ではない場合
		//updateLicenseリクエスト
		updateLicense(book);
		// 最終閲覧日DB更新(contentsテーブル,update_contentsテーブル)
		lastAccessDate(book.getRowId(), book.getDownload_id());
		// 覧中コンテンツダウンロードIDをxmlに保存...閲覧後webサイト誘導ダイヤログを表示するため
		if(mPref == null) mPref = new Preferences(mCon);
		mPref.setNaviDlId(book.getDownload_id());
		
		//	ファイルタイプ毎のビューアを起動する
		switch(fileType) {
		case BEC:
		case KRBEC:
		case EPUB:
		case KREPUB:
			return sendEpubViewer(fileType, book, isTemporary);
		case EPUBX:
			sendMCBookViewer(book, isTemporary);
			//	暗号キーは必要ないので true を返す
			return true;
		case MCM:
		case EPUBXF:
			sendMCComicViewer(book, isTemporary);
			//	暗号キーは必要ないので true を返す
			return true;
		case KREPA:
			return sendAudioViewer(fileType, book, isTemporary);
		default:
			throw new Exception(String.format("filetype:%d is not supported.", fileType));
		}
	}
	
	/**
	 * [必須]ファイルタイプチェック
	 * @param fileType
	 * @return ファイルタイプフラグ
	 * @throws NullPointerException
	 */
	public int fileTypeCheck(String fileType) throws NullPointerException{
		Logput.v("FileType = " + fileType);
		if(Utils.isKrpdf(fileType)){
			return KRPDF;
		}else if(Utils.isPdf(fileType)){
			return PDF;
		}else if(Utils.isKrEPub(fileType)){
			return KREPUB;
		}else if(Utils.isEPub(fileType)){
			return EPUB;
		}else if(Utils.isKrbec(fileType)){
			return KRBEC;
		}else if(Utils.isBec(fileType)){
			return BEC;
		}else if (Utils.isMcm(fileType)){
			return MCM;
		} else if (Utils.isMcbook(fileType)){
			return EPUBX; 
		} else if (Utils.isMccomic(fileType)){
			return EPUBXF; 
		}else if(Utils.isKrEPA(fileType)){
			return KREPA;
		} else if(Utils.isKrpdfx(fileType)) {
			return KRPDFX;
		} else {
			Logput.d("FileType is false.");
			return -1;
		}
	}
	
	/**
	 * オンライン時のみGetLicenseリクエスト
	 * @return downloadID ダウンロードID
	 */
	public BookBeans getLicense(BookBeans book){
		if(Utils.isConnected(mCon)){
			GetLicense getLicense = new GetLicense();
			book = getLicense.execute(mCon, book.getDownload_id());
		}
		return book;
	}
	
	/**
	 * PDFファイルをアプリケーション領域にコピー(アクセス権限読み取り可能に)
	 * @param isEncrypt 暗号化する場合はtrue,復号化の場合はfalse
	 * @param sdPath コピー元ファイルパス
	 * @param fileName ファイル名
	 */
	public void pdfCopy(int copyFlag, String sdPath, String fileName, byte[] key) 
			throws NullPointerException, FileNotFoundException, IOException{
		Logput.v("SD path = " + sdPath);
		File sdFile = new File(sdPath);
		Logput.d(">>Copy file size = " + sdFile.length());

		InputStream input = null;
		FileOutputStream output = null;
		int DEFAULT_BUFFER_SIZE = 1024 * 1024;
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		byte[] encBuffer = new byte[DEFAULT_BUFFER_SIZE];	//	暗号化/復号化データのバッファ
		int n = 0;
		switch(copyFlag){
		case COPY_PDF:
			Logput.v("PDF COPY START ---------------");
			input = new FileInputStream(sdFile);
			output = mCon.openFileOutput(fileName + ".pdf", Context.MODE_WORLD_READABLE);
			while (-1 != (n = input.read(buffer))) {
				output.write(buffer, 0, n);
				Logput.v("copy byte = " + n);
			}
			Logput.v(fileName + ".pdf copy finish ----------");
			break;
		case COPY_KRPDF_EN:	// 復号化してコピー...アプリケーション領域=>SDカード
			Logput.v("COPY KRPDF ENCRYPT START ---------------");
			input = mCon.openFileInput(fileName + ".pdf");
			output = new FileOutputStream(sdFile);
			while (-1 != (n = input.read(buffer))) {
				// PDFを暗号化・復号化しながらコピー
				byte[] decryptFile = DecryptUtil.decrypt(buffer, key, encBuffer);
				output.write(decryptFile, 0, n);
				Logput.v("copy byte = " + n);
			}
			Logput.d(">>Encrypt & Copy = " + fileName + ".pdf finish ----------");
			break;
		case COPY_KRPDF_DE:	// 暗号化してコピー...SDカード=>アプリケーション領域
			Logput.v("COPY KRPDF DECRYPT START ---------------");
			input = new FileInputStream(sdFile);
			output = mCon.openFileOutput(fileName + ".pdf", Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
			while (-1 != (n = input.read(buffer))) {
				// PDFを暗号化・復号化しながらコピー
				byte[] decryptFile = DecryptUtil.decrypt(buffer, key, encBuffer);
				output.write(decryptFile, 0, n);
				Logput.v("copy byte = " + n);
			}
			Logput.d(">>Decrypt & Copy = " + fileName + ".pdf finish ----------");
			break;
		}
		input.close();
		output.close();
	 }
	
	/**
	 * AdobeReaderで閲覧開始、キャプチャ制御サービス起動
	 * @param book コンテンツ情報
	 * @param filePath 閲覧ファイルパス
	 * @param isTemporary オンライン閲覧フラグ
	 */
	public void sendAdobeReader(BookBeans book, String filePath, boolean isTemporary) throws ActivityNotFoundException{
		// updateLicenseリクエスト
		updateLicense(book);
		// 最終閲覧日DB更新(contentsテーブル,update_contentsテーブル)
		lastAccessDate(book.getRowId(), book.getDownload_id());
		
		// 明示的インテント adobe reader
		Uri fileUri = Uri.parse("file://" + filePath + ".pdf");
		Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
		intent.setClassName(ADOBE_READER, ADOBE_READER + ".AdobeReader");
		//	Intent.FLAG_ACTIVITY_CLEAR_TASK を指定しないと前に開いていたアクティビティやPDFファイルがそのまま開く場合がある
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		Logput.v(">>Send Adobe Reader");
		mCon.startActivity(intent);
		
		// 覧中コンテンツダウンロードIDをxmlに保存...閲覧後webサイト誘導ダイヤログを表示するため
		if(mPref == null) mPref = new Preferences(mCon);
		mPref.setNaviDlId(book.getDownload_id());

		// AdobeReaderが起動しているかチェックするServiceを起動
		Intent readerCheck = new Intent(mCon, ProcessCheckService.class);
		String fileName = new File(filePath).getName();
		readerCheck.putExtra(Const.VIEW_CONTENT_NAME, fileName);
		mCon.startService(readerCheck);
		
		if(isTemporary){
			// オンライン閲覧の場合はサーバー登録・DB・コンテンツを削除
			temporary(book);
		}
	}
	
	/**
	 * ePubViewerで閲覧,暗号化ファイルの場合はキャプチャ制御サービス起動
	 * @param fileType ファイルタイプフラグ
	 * @param book BookBeans
	 * @return 暗号化キーが不明の場合(暗号化ファイル),もしくはコピーに失敗した場合falseを返す
	 */
	private boolean sendEpubViewer(int fileType, BookBeans book, boolean isTemporary) throws Exception{
		String path = book.getFile_path();
		if(isTemporary){
			// コンテンツをアプリ内領域に移動させる
			try{
				path = copy_temporary(path);
			}catch(Exception e){
				throw new Exception(e.getMessage());
			}
			// サーバー・DBからデータ削除
			temporary(book);
		}
		
		//	ビューア起動
		Logput.v(">>Send ePub viewer [" + path + "]");
		Intent intent = new Intent("net.keyring.bookend.ACTION_VIEW");
		String className = "net.keyring.bookend.viewer.BookViewer";
		intent.setClassName( className.substring(0,className.lastIndexOf('.')), className);
		intent.removeCategory(Intent.CATEGORY_DEFAULT);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addCategory("net.keyring.bookend.DEFAULT");
		intent.setData(Uri.parse(path));
		intent.putExtra("fileType", book.getType());
		
		//	暗号化ファイルの場合の追加処理
		if(fileType == KREPUB || fileType == KRBEC){
			//	キャプチャブロック処理を起動
			Intent processCheck = new Intent(mCon, ProcessCheckService.class);
			mCon.startService(processCheck);
			//	鍵を Intent に含める
			try{
				byte[] key = Utils.getBytesKeyFromHexStr(book.getKey(), 16);
				intent.putExtra("key", key);
			}catch(NullPointerException e){
				Logput.w("Key is null.", e);
				return false;
			}
		}
		
		mCon.startActivity(intent);
		return true;
	}

	/**
	 * AudioViewerで閲覧,暗号化ファイルの場合はキャプチャ制御サービス起動
	 * @param fileType ファイルタイプフラグ
	 * @param book BookBeans
	 * @return 暗号化キーが不明の場合(暗号化ファイル),もしくはコピーに失敗した場合falseを返す
	 */
	private boolean sendAudioViewer(int fileType, BookBeans book, boolean isTemporary) throws Exception{
/*
		String path = book.getFile_path();
		if(isTemporary){
			// コンテンツをアプリ内領域に移動させる
			try{
				path = copy_temporary(path);
			}catch(Exception e){
				throw new Exception(e.getMessage());
			}
			// サーバー・DBからデータ削除
			temporary(book);
		}
		
		//	展開先のディレクトリパスを生成
		//	*復号ファイルが保存されるので、アプリのプライベートディレクトリに展開する
		String extractDir = mCon.getFilesDir() + "/" + path.substring(path.lastIndexOf("/") + 1) + "_";
		
		//	設定を保存するXMLファイルパス
		String settingFilePath = path + "_epa.xml";
		
		//	ビューア起動
		Logput.v(">>Send Audio viewer [" + path + "]");
		
		Intent intent = new Intent(mCon, PlayerControlActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("contents_id", path);
		intent.putExtra("setting_file", settingFilePath);
		intent.putExtra("extract_dir", extractDir);
		
		//	暗号化ファイルの場合の追加処理
		//	キャプチャブロック処理を起動
		Intent processCheck = new Intent(mCon, ProcessCheckService.class);
		mCon.startService(processCheck);
		
		//	鍵を Intent に含める
		try{
			String pass = book.getKey(); 
			intent.putExtra("key", pass);
		}catch(NullPointerException e){
			Logput.w("Key is null.", e);
			return false;
		}
		
		mCon.startActivity(intent);
*/		return true;
	}

	/**
	 * MCComicビューアを起動する
	 * @param book
	 * @param isTemporary
	 * @throws Exception
	 */
	private void sendMCComicViewer(BookBeans book, boolean isTemporary) throws Exception {
/*		String path = book.getFile_path();
		if(isTemporary){
			// コンテンツをアプリ内領域に移動させる
			try{
				path = copy_temporary(path);
			}catch(Exception e){
				throw new Exception(e.getMessage());
			}
			// サーバー・DBからデータ削除
			temporary(book);
		}
		
		//	キャプチャ制御処理を開始する
		Intent processCheck = new Intent(mCon, ProcessCheckService.class);
		mCon.startService(processCheck);
			
		//	ビューア起動
		Logput.v(">>Send MCComic viewer [" + path + "]");
		MCComicViewerUtil mccomicViewerUtil = MCComicViewerUtil.getInstance();
		mccomicViewerUtil.initialize(mActivity);
		mccomicViewerUtil.showViewer(path);
*/	}
	
	/**
	 * MCBookビューアを起動する
	 * @param book
	 * @param isTemporary
	 * @throws Exception
	 */
	private void sendMCBookViewer(BookBeans book, boolean isTemporary) throws Exception {
/*		String path = book.getFile_path();	
		if(isTemporary){
			// コンテンツをアプリ内領域に移動させる
			try{
				path = copy_temporary(path);
			}catch(Exception e){
				throw new Exception(e.getMessage());
			}
			// サーバー・DBからデータ削除
			temporary(book);
		}
		
		//	キャプチャ制御処理を開始する
		Intent processCheck = new Intent(mCon, ProcessCheckService.class);
		mCon.startService(processCheck);
		
		//	ビューア起動
		Logput.v(">>Send MCBook viewer [" + path + "]");
		MCBookViewerUtil.showViewer(this.mActivity, path);	
*/	}
	
	/**
	 * サーバー登録・DB・コンテンツを削除
	 */
	public void temporary(BookBeans book){
		long rowID = book.getRowId();
		if(rowID != -1){
			// DBからデータ削除
			if(mDao == null) mDao = new ContentsDao(mCon);
			mDao.delete(rowID);
		}
		// コンテンツ・サムネイル削除
		FileUtil.deleteFile(new File(book.getFile_path()));
		FileUtil.deleteFile(new File(book.getThumb_path()));
		// サーバーから削除
		UpdateContents updateContents = new UpdateContents(mCon);
		updateContents.execute(rowID, book.getDownload_id(), book.getLast_access_date(), "true");
	}
	
	/**
	 * コンテンツ(bec,krbec,epub,krepub)をアプリ内領域に移動させる
	 * @param filePath
	 * @return コピー先ファイルパス
	 * @throws FileNotFoundException 
	 */
	public String copy_temporary(String filePath) throws FileNotFoundException, IOException{
		File file = new File(filePath);
		InputStream input = null;
		FileOutputStream output = null;
		String fileName = TEMPORARY_FILE + FileUtil.getFileExtension(filePath);
		int n = 0;
		try{
			input = new FileInputStream(file);
			output = mCon.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
			byte[] readBytes = new byte[input.available()];
			while (-1 != (n = input.read(readBytes))) {
				output.write(readBytes, 0, n);
				Logput.v("copy byte = " + n);
			}
			output.flush();
			return mCon.getFilesDir() + "/" + fileName;
		}catch(FileNotFoundException e){
			Logput.e(e.getMessage(), e);
			throw new FileNotFoundException(e.getMessage());
		}catch(IOException e){
			Logput.e(e.getMessage(), e);
			throw new IOException(e.getMessage());
		}
	}
	
	/**
	 * アプリがインストールされてるかどうかチェック
	 * @param con Context
	 * @param app チェックするアプリケーションパッケージ名(Full)
	 * @return インストールされていた場合はtrue,それ以外はfalseを返す
	 */
	public void isInstallApp(String app) throws NameNotFoundException{
		PackageManager pm = mCon.getPackageManager();
		// アプリがインストールされているか判定
		pm.getApplicationInfo(app, 0);
	}
	
	/**
	 * EPubViewerのバージョンコード取得
	 * @param PackageManager パッケージマネージャー
	 * @return EPubViewerのバージョンコード
	 */
	public int getEPubViewerVersionCode(PackageManager packageManager, String packageName){
		return getVersionCode(packageManager, packageName);
	}
	
	/**
	 * 最終閲覧日更新
	 */
	public void lastAccessDate(long rowID, String downloadID){
		String lastAccessDate = DateUtil.getNowUTC();
		// UpdateContents - 最終閲覧日更新
		UpdateContents updateContents = new UpdateContents(mCon);
		updateContents.execute(rowID, downloadID, lastAccessDate, "false");
	}
	
	/**
	 * 閲覧制限が無制限ではない場合はUpdateLicenseを行う
	 * @param book Book
	 */
	public void updateLicense(BookBeans book){
		if(book != null){
			if(book.getBrowse_D() != -1){
				// UpdateLicenseリクエスト：browse-1
				UpdateLicense updateLicense = new UpdateLicense();
				updateLicense.execute(mCon, book.getRowId(), book.getDownload_id(), book.getBrowse_M(), -1);
			}else{
				Logput.v("browse count = " + mCon.getString(R.string.unlimited));
			}
		}
	}

	/**
	 * バージョンコード取得
	 * @param packageManager パッケージマネージャー
	 * @param packageName 取得したいアプリのパッケージ名
	 * @return バージョンコード
	 */
	private int getVersionCode(PackageManager packageManager, String packageName){
		int versionCode = 0;
        try {
               PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
               versionCode = packageInfo.versionCode;
               Logput.d("[VersionCode]" + packageName + " : " + versionCode);
          } catch (NameNotFoundException e) {
               Logput.w(e.getMessage(), e);
          }
        return versionCode;
	}
}
