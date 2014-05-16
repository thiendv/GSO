package net.keyring.bookend.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.zip.CRC32;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import android.util.Base64;

import net.keyring.bookend.constant.Const;
import net.keyring.krpdflib.InvalidPdfException;
import net.keyring.krpdflib.PdfFile;
import net.keyring.krpdflib.UnsupportedPdfException;

/**
 * エンコード・デコード・復号 Utility<br><br>
 *
 * * ランダムデータの生成<br>
 * * Base16エンコード/デコード<br>
 * * PDFファイルからDocumentIDを取得(PdfReader使用)<br>
 * * XMPメタデータから指定したタグのデータを取得する<br>
 * * 受け取った文字列をBase64でエンコードしたバイト配列に変換<br>
 * * decryptXMPData(String data, String docID)<br>
 * * RC4復号<br>
 *
 * @author Hamaji
 *
 */
public class DecryptUtil implements Const {

	/**
	 * ランダムデータの生成
	 * @param dataSize
	 * @return byte[]
	 */
	public static byte[] genRandomData(int dataSize) {
		byte[] data = new byte[dataSize];
		Random rand = new Random();
		rand.nextBytes(data);
		return data;
	}
	
	/**
	 * ファイルのCRC32を取得する
	 * @param file
	 * @return
	 */
	public static String getCRC32(File file) 
		throws FileNotFoundException, IOException
	{
		//	ファイル全体のCRC32を生成する
		int buffSize = 4096;
		CRC32 crc32 = new CRC32();
		FileInputStream srcFileStream = new FileInputStream(file.getPath());
		while(srcFileStream.available() > 0) {
			int readSize = srcFileStream.available() > buffSize ? buffSize : srcFileStream.available();
			byte[] buf = new byte[readSize];
			srcFileStream.read(buf, 0, readSize);
			crc32.update(buf);
		}
		long value = crc32.getValue();
		ByteBuffer byteBuff = ByteBuffer.allocate(4);
		byteBuff.putInt((int)value);
		//	Base16エンコードして返す
		return base16enc(byteBuff.array());
	}

	/**
	 * Base16エンコード/デコード
	 * @param data
	 * @return
	 */
	public static String base16enc(byte[] data) {
		if(data == null) return null;
		if(data.length < 1) return null;
		StringBuffer stBuf = new StringBuffer(data.length * 2);
		for(int i = 0; i < data.length; i++) {
			int value = data[i] & 0xff;
			if(value < 0x10) {stBuf.append("0");}
			stBuf.append(Integer.toHexString(value));
		}
		return stBuf.toString();
	}
	public static byte[] base16dec(String data) {
		if(data == null || data.length() < 1) return null;
		if(data.length() % 2 != 0) {throw new IllegalArgumentException("string length is not even.");}
		byte[] buf = new byte[data.length() / 2];
		for(int i = 0, j = 0; i < data.length(); i+=2, j++) {
			buf[j] = (byte)Integer.parseInt(data.substring(i, i + 2), 16);
		}
		return buf;
	}

	/**
	 * Base64エンコード/デコード
	 * @param data
	 * @return
	 */
	public static String base64enc(byte[] data) {
		byte[] encData = Base64.encode(data, Base64.DEFAULT);
		//	最後に改行文字 '\n' がついているのでこれを除いて返す
		return new String(encData, 0, encData.length - 1);
	}
	/**
	 * 受け取った文字列をBase64でエンコードしたバイト配列に変換
	 * @param エンコードしたい文字列
	 * @return Base64のバイト配列
	 */
	public static byte[] base64dec(String data) {
		return Base64.decode(data, Base64.DEFAULT);
	}

	/**
	 * XMPメタデータから指定したタグのデータを取得する
	 * @param xmpData	XMPメタデータ全体のString
	 * @param tagName	取得対象のタグ名("<",">"は含まない)
	 * @return	取得したデータ, 見つからない場合は空文字列
	 */
	public static String getXmpData(String xmpData, String tagName)
	{
		//	開始、終了タグの文字列
		String tag_begin = "<" + tagName + ">";
		String tag_end = "</" + tagName + ">";
		//	開始タグを検索
		int pos_from = xmpData.indexOf(tag_begin);
		if(pos_from == -1) {return "";}
		pos_from += tag_begin.length();
		//	終了タグを検索
		int pos_to = xmpData.indexOf(tag_end, pos_from + tag_begin.length());
		if(pos_to == -1) {return "";}
		//	タグの値を返す
		return xmpData.substring(pos_from, pos_to);
	}

	/**
	 * コンテンツからContentsIDを取得
	 * @param data
	 * @param docID
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws BadPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 */
	public static String decryptXMPData(String data, String docID)
	throws NoSuchAlgorithmException, NoSuchPaddingException,
		BadPaddingException, InvalidKeyException, IllegalBlockSizeException
		{
	//	ソルトと暗号化データをBase64デコード
	byte[] salt = base64dec(data.substring(0, 4));
	byte[] cipher = base64dec(data.substring(4, data.length()));
	//	復号鍵を生成
	byte[] hash = makeKey(salt, docID);
	//	復号化
	byte[] plain = decrypt(cipher, hash);
	return new String(plain);
	}
	
	/**
	 * ファイルから暗号鍵を生成
	 * @param path 暗号鍵を作るファイルパス
	 * @return 暗号鍵
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws BadPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws IOException
	 */
	public static byte[] makeKey(String path)
		throws NoSuchAlgorithmException, NoSuchPaddingException,
			BadPaddingException, InvalidKeyException, IllegalBlockSizeException, IOException,
			UnsupportedPdfException, InvalidPdfException
	{
		PdfFile pdfFile = new PdfFile();
		pdfFile.open(new File(path));
		String docID = pdfFile.getFileID()[0];
		byte[] xmp = pdfFile.getMetaDataStream().get_data();
		String did = DecryptUtil.getXmpData(new String(xmp), "krns:cid");
		DecryptUtil.decryptXMPData(did, docID);
		byte[] salt = DecryptUtil.base64dec(did.substring(0, 4));
	
		return DecryptUtil.makeKey(salt, docID);
	}
	
	/**
	 * 復号鍵を生成
	 * @return 復号鍵
	 * @throws NoSuchAlgorithmException 
	 */
	private static byte[] makeKey(byte[] salt, String docID) throws NoSuchAlgorithmException{
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(salt);
		md5.update(base16dec(docID));
		return md5.digest();
	}

	/**
	 * RC4復号
	 * @param cipher (Array)plain data byte array
	 * @param key (Array)secret key byte array
	 * @return (Array)encrypted data byte array
	 */
	public static byte[] decrypt(byte[] cipher, byte[] key){
		return decrypt(cipher, key, new byte[cipher.length]);
	}
	
	/**
	 * RC4復号
	 * Androidではbyte[]を何度もnewするとパフォーマンスが悪いので、newせずに指定されたバッファを使用する
	 * @param cipher (Array)plain data byte array
	 * @param key (Array)secret key byte array
	 * @param decrypt (Array)decrypted byte array
	 * @return (Array)encrypted data byte array
	 */
	public static byte[] decrypt(byte[] cipher, byte[] key, byte[] decrypt){
		byte[] s = new byte[256];
		for (int i=0; i<256; i++) {
			s[i] = (byte)i;
		}
		int j = 0;
		byte x;
		for (int k=0; k<256; k++) {
			j = (j + s[k] + key[k % key.length]) % 256;
			x = s[k];
			j = (j & 0xFF);
			s[k] = s[j];
			s[j] = x;
		}
		j = 0;
		int i = 0;
		byte[] ct = decrypt;
		for (int y=0; y<cipher.length; y++) {
			i = (i + 1) % 256;
			i = (i & 0xFF);
			j = (j + s[i]) % 256;
			j = (j & 0xFF);
			x = s[i];
			s[i] = s[j];
			s[j] = x;
			int b = (s[i] + s[j]) % 256;
			b = (b & 0xff);

			ct[y] = (byte)(cipher[y] ^ s[b]);
		}
		return ct;
	}	
}
