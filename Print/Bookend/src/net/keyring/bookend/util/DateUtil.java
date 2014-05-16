package net.keyring.bookend.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import net.keyring.bookend.constant.Const;

/**
 * 日時に関するUrilクラス
 * @author Hamaji
 *
 */
public class DateUtil {
	
	/**
	 * DBから取得した日時文字列をOSタイムゾーンに合わせた日時文字列に変換
	 * @param indefinitely 日時指定がない場合の文字列
	 * @param title 日時前につける見出し
	 * @param db_date 最終閲覧日(UTC)
	 * @return os_date OSタイムゾーンに合わせた日時文字列
	 */
	public static String change_viewDate(String indefinitely, String title, String db_date){
		String os_date = indefinitely;
		if(!StringUtil.isEmpty(db_date)){
			if(!DateUtil.isUnlimitedDate(db_date)){
				os_date = DateUtil.toDbString(DateUtil.toDate(db_date,"UTC"));
			}
		}
		if(title != null){
			return title + os_date;
		}else{
			return os_date;
		}
	}

    /**
     * 指定された文字列からDate型の日時(OS time)へ変換します。
     * ※新しい日時フォーマット対応版です。
     *
     * @param dateStr   日時文字列
     * @param tz		日時文字列のタイムゾーン
     *
     * @return  <tt>dateStr</tt> が想定した形式の文字列の場合はDateオブジェクト
     *          を返します。想定外の形式が指定された場合は <tt>null</tt> を返します。
     */
    public static Date toDate(String dateStr,TimeZone tz) {
        return toDate(dateStr,tz, null);
    }

    /**
     * 指定された文字列からDate型の日時へ変換します。
     * ※新しい日時フォーマット対応版です。
     *
     * @param dateStr   日時文字列
     * @param tz		日時文字列のタイムゾーン文字列
     *
     * @return  <tt>dateStr</tt> が想定した形式の文字列の場合はDateオブジェクト
     *          を返します。想定外の形式が指定された場合は <tt>null</tt> を返します。
     */
    public static Date toDate(String dateStr,String timeZone) {
    	TimeZone tz = TimeZone.getTimeZone(timeZone);
        return toDate(dateStr,tz, null);
    }

    /**
     * 指定時刻を指定タイムゾーンの日時に合わせる
     * @param 指定日時
     * @param 指定日時タイムゾーン
     * @param 変更したいタイムゾーン
     * @return
     */
    public static Date toDate(String dateStr,TimeZone tz, TimeZone set_tz){
    	if (dateStr == null) return null;
    	if (set_tz == null) set_tz = TimeZone.getDefault();
    	SimpleDateFormat sdf = new SimpleDateFormat(Const.DATE_PATTERN);
    	sdf.setTimeZone(set_tz);
    	// テキストからDateに変換
		try {
			Date data = sdf.parse(dateStr);
			// Dateからフォーマット
	    	long difference = set_tz.getRawOffset() - tz.getRawOffset();
	    	return new Date(data.getTime() + difference);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
    }

    /**
     * 指定されたDateオブジェクトをデータベースで扱うことができる日時文字列
     * に変換します。
     *
     * @param date      変換対象日時のDateオブジェクト
     * @param timezone  変換時に使用するタイムゾーン文字列
     *
     * @return  変換した日時文字列
     */
    public static String toDbString(Date date, String timeZone) {
        return toDbString(date, TimeZone.getTimeZone(timeZone));
    }
    /**
     * 指定されたDateオブジェクトをデータベースで扱うことができる日時文字列
     * に変換します。
     *
     * @param date      変換対象日時のDateオブジェクト
     * @param timezone  変換時に使用するタイムゾーン
     *
     * @return  変換した日時文字列
     */
    public static String toDbString(Date date, TimeZone timezone) {
        if (date == null)
            throw new IllegalArgumentException("date is null.");
        if (timezone == null) timezone = TimeZone.getDefault();
        SimpleDateFormat dbFmt = new SimpleDateFormat(Const.DATE_PATTERN);
        dbFmt.setTimeZone(timezone);
        return dbFmt.format(date);
    }

    /**
     * 指定されたDateオブジェクトをデータベースで扱うことができる日時文字列に変換
     * @param 変換対象日時のDateオブジェクト
     * @return 変換した日時文字列
     */
    public static String toDbString(Date date){
    	if(date == null) throw new IllegalArgumentException("date is null.");
    	SimpleDateFormat dbFmt = new SimpleDateFormat(Const.DATE_PATTERN);
        return dbFmt.format(date);
    }

    /**
     * 指定されたDateオブジェクトをXMLで扱う形式の日時文字列に変換します。
     * 変換するときのTimeZoneは、システム起動時のデフォルトのタイムゾーン
     * を使用します。
     *
     * @param date  変換対象日時のDateオブジェクト
     *
     * @return  変換した日時文字列
     */
    public static String formatXml(Date date) {
        return formatXml(date, null);
    }

    /**
     * 指定されたDateオブジェクトをXMLで扱う形式の日時文字列に変換します。
     * 変換するときに指定されたTimeZoneで変換します。
     *
     * @param date      変換対象日時のDateオブジェクト
     * @param timeZone  変換対象のタイムゾーン
     *
     * @return  変換した日時文字列
     */
    public static String formatXml(Date date, TimeZone tz) {
        if (tz == null)
            tz = TimeZone.getDefault();
        if (date.after(getMax()))
            date = getMax();
        SimpleDateFormat outputXmlFmt =
            new SimpleDateFormat(Const.DATE_PATTERN);
        outputXmlFmt.setTimeZone(tz);
        String result =
            outputXmlFmt.format(date) + " " + getGMTString(date, tz);
        return result;
    }

    /**
     * 現在の日時に指定された秒を加算した日時をDate型で返します。
     * <tt>sec</tt> が負数の場合は現在の日時から指定された秒を差し引いた
     * 日時を返します。
     *
     * @param sec   現在日時からの差分を示す秒
     * @return  現在日時から指定された秒を加減算した日時のDataオブジェクト
     */
    public static Date getNow(long sec) {
        return new Date(System.currentTimeMillis() + (sec * 1000));
    }


    /**
     * 現在の日時をDate型で返します。
     * 実際には{@link #getDate(int)} のsecを0指定して呼び出した結果と
     * 同じです。
     *
     * @return  現在日時のDateオブジェクト
     */
    public static Date getNow() {
        return getNow(0);
    }

    /**
     * 現在時刻(UTC)をString型で返します
     * @return 現在時刻(UTC)...yyyy-MM-dd HH:mm:ss
     */
    public static String getNowUTC(){
    	return toDbString(DateUtil.getNow(), "UTC");
    }

    /**
     * 指定日時が期限切れかどうかチェックします。
     * 指定日時が現在の日時よりも前の場合に期限切れと判断しtureを返します。
     */
    public static boolean isExpired(Date date) {
    	if(date.getYear() == 9999) return false;
        return getNow().after(date);
    }

    /**
     * MySQLのdatetime型で扱う日時の最大値をDate型で返します。
     *
     * @return  9999年12月31日 23時59分59秒000をあらわすDateオブジェクト
     */
    private static Date getMax() {
        return toDate(Const.DATE_MAX,TimeZone.getDefault());
    }

    /**
     * MySQLのdatetime型で扱う日時の最小値をDate型で返します。
     *
     * @return  1000年1月1日 0時0分0秒000をあらわすDateオブジェクト
     */
    private static Date getMin() {
        return toDate(Const.DATE_MIN,TimeZone.getDefault());
    }

    private static String getGMTString(Date date, TimeZone tz) {
        DecimalFormat fmt = new DecimalFormat("'GMT'+00':00';'GMT'-00':00'");
        int off = tz.getRawOffset();
        if (tz.inDaylightTime(date))
            off += 3600000;
        off = off / 3600000;
        return fmt.format(off);
    }
    
	/**
	 * 指定された日時の文字列が無期限がどうか調べる
	 * @param dateStr	"yyyy-hh-mm" ...の文字列
	 * @return
	 * @note 10000年のような文字列になっている場合があるので、'-'までの文字列を数値に変換して 9000以上なら無期限とする
	 * 		フォーマットが不正な場合は false
	 */
	public static boolean isUnlimitedDate(String dateStr) {
		dateStr = dateStr.trim();
		int posHyphen = dateStr.indexOf('-');
		if(posHyphen == -1) {return false;}
		int years = Integer.parseInt(dateStr.substring(0, posHyphen));
		return years >= 9000 ? true : false;
	}
}
