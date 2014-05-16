package net.keyring.bookend.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import net.keyring.bookend.Logput;

/**
 * Fileに関するUtilクラス
 * @author Hamaji
 *
 */
public class FileUtil {
	/**
	 * 指定パスがディレクトリかどうかチェック、なければ作成
	 * @param ディレクトリパス
	 * @return ディレクトリを新規作成時はtrueを返す
	 * @throws IOException 
	 */
	public static boolean mkdir_p(String dir) throws IOException {
		if(dir == null || dir.length() == 0){
			return false;
		}
		return mkdir_p(new File(dir));
	}

	/**
	 * ファイルやフォルダを削除<br>
	 * フォルダの場合、中にあるすべてのファイルやサブフォルダも削除されます
	 * @param 削除したいファイル
	 * @return 削除成功ならtrue,失敗ならfalseを返す
	 */
    public static boolean deleteFile(File dirOrFile) {
    	try{
	    	Logput.v("DeleteFile = " + dirOrFile.getPath());
	        if (dirOrFile.isDirectory()) {//ディレクトリの場合
	            String[] children = dirOrFile.list();//ディレクトリにあるすべてのファイルを処理する
	            for (int i=0; i<children.length; i++) {
	                boolean success = deleteFile(new File(dirOrFile, children[i]));
	                if (!success) {
	                	Logput.w("Delete Error : " + children[i]);
	                    return false;
	                }
	            }
	        }
    	}catch(NullPointerException e){
    		Logput.v("Delete request file path is null.");
    		return false;
    	}
        // 削除
        return dirOrFile.delete();
    }

	/**
	 * 指定ディレクトリをなければ作成、チェックする
	 * @param ディレクトリファイル
	 * @return ディレクトリを作成したらtrueを返す
	 * @throws IOException
	 */
	public static boolean mkdir_p(File dir) throws IOException {
		if (!dir.exists()) {
			// 指定ディレクトリがない場合は作成する
			if (!dir.mkdirs()) {
				throw new IOException("File.mkdirs() failed.");
			}
			return true;
		} else if (!dir.isDirectory()) {
			// ディレクトリじゃない場合はスロー
			throw new IOException("Cannot create path. " + dir.toString() + " already exists and is not a directory.");
		} else {
			return false;
		}
	}

	/**
	 * ファイルの拡張子を返します。
	 * return "."を含む拡張子。拡張子が無い場合は空文字列が返る
	 */
	public static String getFileExtension(String filePath) {
		//	ファイルパスに含まれる "." 以降の文字を取得する
	    int point = filePath.lastIndexOf(".");
	    if (point != -1) {
	    	String ext = filePath.substring(point);
	    	//	ファイルパスセパレータが含まれている場合は拡張子ではない
	    	if(ext.indexOf(File.separator) != -1) {
	    		return "";
	    	}
	    	return ext;
	    }
	    return "";
	}
	
	/**
	 * ファイルコピーを行う
	 * @param srcFile
	 * @param dstFile
	 */
	public static void copyFile(File srcFile, File dstFile) throws IOException {
		int DEFAULT_BUFFER_SIZE = 1024 * 1024;
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int n = 0;
		FileInputStream input = new FileInputStream(srcFile);
		FileOutputStream output = new FileOutputStream(dstFile);
		while(-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
		}
		input.close();
		output.close();
	}
}
