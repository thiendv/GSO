package net.keyring.bookend.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.DataFormatException;

import net.keyring.bookend.Logput;
import net.keyring.bookend.bean.BookBeans;
import net.keyring.bookend.constant.Const;
import net.keyring.bookend.constant.ConstList;
import net.keyring.bookend.db.ContentsDao;
import net.keyring.bookend.util.DecryptUtil;
import net.keyring.bookend.util.Utils;
import net.keyring.cipher.CipherException;
import net.keyring.krpdflib.InvalidPdfException;
import net.keyring.krpdflib.PdfFile;
import net.keyring.krpdflib.UnsupportedPdfException;
import android.content.Context;

/**
 * krpdfの復号化処理
 * @author Hamaji
 *
 */
public class RemoveLayerAction implements Const {
	
	/** ContentsDaoクラス */
	private ContentsDao mDao = null;
	/** ViewActionクラス */
	private ViewAction mViewAction = null;
	/** Context */
	private Context mCon;
	
	public RemoveLayerAction(Context con){
		this.mCon = con;
	}
	
	/**
	 * ● krpdfファイルのレイヤーを削除しRC4暗号化、鍵をDBに保存
	 * @param con Context
	 * @param book BookBeans
	 * @return BookBeans エラーが起きた場合はnullを返す
	 */
	public BookBeans krpdfProcess(int fileType, BookBeans book){
		String filePath = null;
		String fileName = null;
		try {
			Logput.d(">>RemoveLayer");
			// レイヤー削除
			filePath = book.getFile_path();
			fileName = new File(filePath).getName();
			//	.kfpdfの場合
			byte[] userPassword = null;
			boolean removeLayer = true;
			//	.krpdfxの場合
			if(fileType == ConstList.KRPDFX) {
				userPassword = Utils.getBytesKeyFromHexStr(book.getKey(), 32);
				removeLayer = false;
			}
			
			if(removeLayer(filePath, fileName, userPassword, removeLayer)){
				// 暗号化鍵作成(32byte乱数)
				byte[] key = DecryptUtil.genRandomData(32);
				// RC4暗号化
				if(mViewAction == null) mViewAction = new ViewAction(mCon, null);
				mViewAction.pdfCopy(ConstList.COPY_KRPDF_EN, filePath, fileName, key);
				// 鍵をDBに保存
				String keyStr = DecryptUtil.base64enc(key);
				if(mDao == null) mDao = new ContentsDao(mCon);
				mDao.updateKrpdfKey(book.getRowId(), keyStr);
				book.setEncryptionKey(keyStr);
			}
		} catch (UnsupportedEncodingException e) {
			// byte[]=>String 変換エラー
			book = null;
			Logput.e(e.getMessage(), e);
		} catch (IOException e) {
			// PDFファイル暗号化エラー
			book = null;
			deleteFile(fileName + ".pdf");
			Logput.e(e.getMessage(), e);
		} catch (NullPointerException e){
			book = null;
			deleteFile(fileName + ".pdf");
			Logput.e(e.getMessage(), e);
		} catch (Exception e){
			book = null;
			deleteFile(fileName + ".pdf");
			Logput.e(e.getMessage(), e);
		}
		return book;
	}
	
	/**
	 * アプリケーション領域にあるファイルを削除
	 * @param fileName 削除するファイル名
	 */
	private void deleteFile(String fileName){
		try{
			mCon.deleteFile(fileName + ".pdf");
		}catch(Exception ex){
		}
	}
	
	/**
	 * レイヤーを削除しオーナパスワードをかけ直す
	 * @param path ローカルファイルパス
	 * @param　fileName 拡張子なしファイル名
	 * @throws DocumentException 
	 * @throws Exception　
	 */
	private boolean removeLayer(String path, String fileName, byte[] userPassword, boolean removeLayer) 
		throws IOException, DataFormatException, UnsupportedPdfException, CipherException, NoSuchAlgorithmException, InvalidPdfException 
	{
		if(Utils.checkFile(path)){
			// ファイル出力（全てのアプリからの読取を許可）
			FileOutputStream out = mCon.openFileOutput(fileName + ".pdf", Context.MODE_WORLD_READABLE);
			
			//	処理が高速化されますが、スレッドセーフでなくなります
			PdfFile.enableStaticBuffer(true);
			//	ログを出力する場合は以下のコメントアウトを外して下さい
			//Log.enable(true);
			//Log.level(Log.LEVEL_DEBUG);
			//	復号して保護レイヤを削除して再暗号化
			PdfFile pdfFile = new PdfFile();
			pdfFile.decrypt(new File(path), out, userPassword, "".getBytes(), removeLayer);
			
			out.flush();
			out.close();
			return true;
		}else{
			return false;
		}
	}
	

	

}
