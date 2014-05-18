package net.keyring.bookend.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;

import net.keyring.bookend.Logput;

/**
 * 文字列に関するUtilクラス
 * @author Hamaji
 *
 */
public class StringUtil {
	
	/**
	 * 受け取ったXML解析の結果をlog表示
	 * @param タグ名
	 * @param status
	 */
	public static void putResponce(String tagName, String status){
		Logput.v("<" + tagName + "> " + status);
	}
	
	/**
	 * 入力されたPINコードに誤りがないか計算チェック
	 * @param 入力されたPINコード
	 * @return 不備がなければtyue,その他はfalseを返す
	 */
	public static boolean check_pin(String pin){
		Logput.v("PIN [" + pin + "]");
		boolean check = false;
		if(StringUtil.isEmpty(pin)){
			return check;
		}else if(pin.length() != 5){
			return check;
		}else{
			try{
				int one = Utils.changeNum(pin.substring(0, 1));
				int twe = Utils.changeNum(pin.substring(1, 2));
				int tree = Utils.changeNum(pin.substring(2, 3));
				int four = Utils.changeNum(pin.substring(3, 4));
				int five = Utils.changeNum(pin.substring(4, 5));
				
				int checkNum = check_digit((four * 2)) + tree + check_digit((twe * 2)) + one;
				checkNum = checkNum % 10;
				if(checkNum != 0){
					checkNum = 10 - checkNum;
				}
				if(checkNum == five){
					// 計算の答えと5桁目が同じならばtrue
					Logput.v("PIN check = OK");
					check = true;
				}else{
					Logput.d("PIN check = NG : " + checkNum);
				}
			}catch(NumberFormatException e){
				Logput.e(e.getMessage(), e);
			}
			return check;
		}
	}
	
	/**
	 * 指定数値が二桁以上の場合はそれぞれの桁を足して一桁にして返す
	 * @param チェックする数値
	 * @return 一桁の数値
	 */
	private static int check_digit(int checkNum){
		if(checkNum < 10){
			// 1桁の場合はそのまま帰す
			return checkNum;
		}else{
			// 2桁以上の場合
			String numStr = String.valueOf(checkNum);
			int one = Utils.changeNum(numStr.substring(0, 1));
			int twe = Utils.changeNum(numStr.substring(1, 2));
			return one + twe;
		}
	}

	/**
	 * 引数で指定された文字列を２文字毎に区切り、それぞれを１バイトの１６進数とした
     * バイト配列に変換して戻します。
	 *
	 * @param 文字列
	 * @return バイト配列
	 */
    public static byte[] toHexArray(String str) {
        if (str == null || str.trim().length() != 32)
            throw new IllegalStateException(
                    "Hex String was too short. [" + str + "]");
        byte[] bytes = new byte[16];
        for (int kIdx = 0, bIdx = 0; kIdx < str.length(); kIdx+=2, bIdx++)
            bytes[bIdx] =
            (byte) Integer.parseInt(str.substring(kIdx, kIdx + 2), 16);

        return bytes;
    }

    /**
     * 指定された文字列がIntegerに変換可能な文字列かどうかを判断します。
     *
     * @param str   チェック対象文字列
     *
     * @return  Integer変換可能な場合は <tt>true</tt> を返します。
     */
    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * <tt>str</tt> が空文字の場合に <tt>true</tt> を返します。
     * 文字列が空白のみの場合でも空白とはみなしません。（指定された文字列の
     * 長さがゼロより大きければ空文字ではないと判断します。）
     *
     * @param   str 検査する文字列。
     *
     * @return  空文字なら <tt>true</tt> を返します。
     */
    public static boolean isEmpty(String str) {
        return isEmpty(str, false);
    }
    /**
     * <tt>str</tt> が空文字の場合に <tt>true</tt> を返します。
     *
     * @param   str     検査する文字列。
     * @param   trim    <tt>true</tt> の場合、<tt>str</tt> の先頭と末尾の空白
     *                  を取り除いてから空文字かどうかをチェックします。
     *
     * @return  <tt>str</tt> が空文字なら <tt>true</tt> を返します。
     */
    public static boolean isEmpty(String str, boolean trim) {
        if (str == null)
            return true;
        return trim ? str.trim().length() == 0 : str.length() == 0;
    }

	/**
	 * 文字列を任意の文字で分割
	 * @param str 分割したい文字列
	 * @param sep 分割指定文字
	 * @return 分割した文字列リスト,引数に問題があった場合はNULLを返す
	 */
	public static List<String> parseStr(String str, char sep) {
		if(str == null || str.length() < 1){
			return null;
		}
		int i, j;
		List<String> result = new ArrayList<String>();
		if (str.equals("") || str.charAt(str.length() - 1) != sep)
			str += sep;
		j = 0;
		i = str.indexOf(sep);
		while (i >= 0) {
			result.add(str.substring(j, i));
			j = i + 1;
			i = str.indexOf(sep, j);
		}
		return result;
	}

	/**
	 * 簡易版<br>
	 * 指定した文字エンコーディングでのバイト長で文字列をカットする
	 *
	 *  @param str 処理対象となる文字列
	 * @param capacity カットしたいバイト数
	 * @param csn 文字エンコーディング("EUC-JP", "UTF-8" etc...)
	 * @return カットされた文字列
	 * @throws UnsupportedCharsetException 文字エンコーディングが不正
	 * @throws CharacterCodingException エンコーディング不正
	 */
	public static final String trunc(final String str, final int capacity, final String csn)
			throws UnsupportedCharsetException, CharacterCodingException {
		CharsetEncoder ce = Charset.forName(csn).newEncoder();
		if (capacity >= ce.maxBytesPerChar() * str.length())
			return str;
		CharBuffer cb = CharBuffer.wrap(new char[Math.min(str.length(), capacity)]);
		str.getChars(0, Math.min(str.length(), cb.length()), cb.array(), 0);
		return trunc(ce, cb, capacity).toString();
	}
	/**
	 * 詳細・高速版<br>
	 *
	 * エンコーディング処理を指定したい場合、CharsetEncoder を指定できる本機能を使用してください。<br>
	 * エンコーディング中にエラーが出ても、その文字を「■」に置き換え、処理継続、といったような場合です。<br>
	 *
	 * 呼び出し側でバッファの確保を行いますので、繰り返し処理の中で本機能を呼び出すような場合は、<br>
	 * 簡易版より高速になる場合があります。(1割から3割程度です。)<br>
	 *
	 * 内部でエンコードした文字列を置くためのワークエリアとして capacity バイト分の
	 * バイトバッファを作成します。
	 *
	 * @param ce エンコーダ。
	 * @param in 処理対象となる文字列のバッファ
	 * @param capacity カットしたいバイト数
	 * @return in にて渡されたバッファを指定バイト数で flip() したもの
	 * @throws CharacterCodingException エンコーディング不正
	 */
	public static final CharBuffer trunc(final CharsetEncoder ce, final CharBuffer in,
			final int capacity) throws CharacterCodingException {
		if (capacity >= ce.maxBytesPerChar() * in.limit())
			return in;
		final ByteBuffer out = ByteBuffer.allocate(capacity);
		ce.reset();
		CoderResult cr = in.hasRemaining() ? ce.encode(in, out, true)
				: CoderResult.UNDERFLOW;
		if (cr.isUnderflow())
			cr = ce.flush(out);
		return (CharBuffer)in.flip();
	}

	/**
	 * リンク文字列かどうかチェック
	 * @param URL
	 * @return 半角英数字でプロトコルがhttp,httpsの場合はtrue、それ以外はfalseを返す
	 */
	public static boolean isLink(String url){
		boolean isLink = false;
		// 正規表現でチェック
		final String MATCH_URL = "^(https?|ftp)(:\\/\\/[-_.!~*\\'()a-zA-Z0-9;\\/?:\\@&=+\\$,%#]+)$";
		if (url.matches(MATCH_URL)){
			try{
				URL u = new URL(url);
				String protocol = u.getProtocol();
				if(protocol != null){
					if(protocol.equals("http") || protocol.equals("https")){
						isLink = true;
					}
				}
			}catch(MalformedURLException e){
				Logput.e(e.getMessage(), e);
			}
		}
		return isLink;
	}
}
