package net.keyring.bookend;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.keyring.bookend.util.FileUtil;
import net.keyring.bookend.util.Utils;
import android.os.Environment;
import android.util.Log;

/**
 * Logcat・Logファイルに出力<br>
 * エラーはerrorディレクトリ、その他は全てlogディレクトリ
 * 
 * @author Hamaji
 * 
 */
public class Logput {

	/**
	 * ERROR:	このレベルは何か致命的なことが発生したときに使うべきです。つまり、ユーザにとって目に見えて重大で、
	 * 			明確にデータの削除やアプリケーションのアンインストール、データパーティションの削除、電話全体の再起動（あるいは、
	 * 			もっともっと悪いこと）をしなければ復旧不能な問題が発生したときです 。このレベルは必ずログに記録しておきましょう。
	 * 			通常、ERRORレベルでログ記録するような問題は、統計収集サーバに報告するようにしておくのが望ましいです。
	 * WARNING:	このレベルは深刻な予期せぬことが発生したときに使うべきです。つまり、ユーザにとって目に見えて重大ですが、
	 * 			特定のアクションを実行すればデータを失うことなく復旧可能な問題が発生したときです。
	 * 			このアクションには、しばらく待つだけやアプリを再起動することから、アプリケーションの新しいバージョンを再ダウンロードしたり、
	 * 			デバイスをリブートすることまで、さまざまです。このレベルは必ずログに記録しておきましょう。
	 * 			WARNINGレベルでログ記録するような問題も、統計収集サーバに報告するのを検討した方が望ましいです。 
	 * INFORMATIVE:このレベルは多くの人にとって関心のあることが発生したことを通知するために使うべきです 。
	 * 			つまり、広範囲に影響がありそうだが、必ずしもエラーではないような状況です 。
	 * 			これは、そのドメインでいちばん信頼できると思われるモジュールだけが、ログを記録すべきです。
	 * 			（信頼できないコンポーネントによって、ログが二重に記録されるのを防ぐため）。このレベルは必ずログに記録しておきましょう。
	 * DEBUG:	このレベルはデバイスに何が発生しているのか 、想定外の動作を調査、デバッグするための情報を記録するときに使うべきだ。
	 * 			あなたのコンポーネントに何が起こっているのか、必要十分な情報だけをログに記録すべきです。
	 * 			 もしデバッグログがログの大部分を占めるようであれば、VERBOSEレベルで記録すべきでしょう。
	 * 			DEBUG レベルはリリースビルドでも記録されるため、if (LOCAL_LOG) ブロックや if(LOCAL_LOGD)ブロックで囲んでおく必要があります。
	 * 			この LOCAL_LOG[D]はクラスやサブコンポーネントで定義され、こうしたログをすべて無効にできるようになっています。 
	 * 			そのため、if (LOCAL_LOG)ブロックにはアクティブな論理を入れてはいけません。
	 * 			ログのための文字列構築も if (LOCAL_LOG)ブロックのなかに入れておく必要があります。
	 * 			もし if (LOCAL_LOG)ブロックの外側で文字列構築しようとしているなら、ログ記録の呼び出しをメソッド呼び出しにリファクタリングすべきではありません。
	 * 			まだ if(localLOGV) を使っているコードもあります。これもまだ使えますが、名前付けは Android の標準に従っていません。
	 * VERBOSE:	以上のどのレベルにも当てはまらないログはすべて、VERBOSE レベルを使うべきです。このレベルはデバッグビルドだけでログが記録されるよう、
	 * 			if(LOCAL_LOGV) ブロック（あるいは同等のもの）で囲んでおくべきです。こうすることで、デフォルトではコンパイルされなくなります。
	 * 			ログに関する文字列構築はリリースビルドに入らないよう、if (LOCAL_LOGV) ブロックのなかに入れておく必要があります。
	 */

	/** Logcat タグ名 */
	public static final String TAG = "BOOKEND";
	/** エラーLOG名用Dateパターン */
	private static final String LOG_DATE_PATTERN = "yyyyMMdd";
	/** Logcat出力フラグテキストファイル */
	public static final String LOGCAT = "LogCat.txt";
	/** エラーログ出力ディレクトリ名 */
	public static final String ERROR_DIR = "error";
	/** ログ出力フラグディレクトリ名 */
	public static final String LOG_DIR = "log";

	/** Logをテキストに出力するかどうかフラグ - true:出力,false:出力しない */
	public static boolean isLogput = false;
	/** LogCatに出力するかどうかフラグ - true:出力,false:出力しない */
	public static boolean isLogcat = true;

	
	// ----------------------------------------
	// 	LogCat
	// --------------------------
	
	/**
	 * LogCatに出力：レベル=デバッグ
	 * 
	 * @param msg
	 *            出力メッセージ
	 */
	public static void d(String msg) {
		if (isLogcat) {
			Log.d(TAG, msg + getPoint());
		}
		if(isLogput){
			putLog(msg + getPoint());
		}
	}

	/**
	 * LogCatに出力：レベル=デバッグ
	 * 
	 * @param msg
	 *            出力メッセージ
	 */
	public static void i(String msg) {
		if (isLogcat) {
			Log.i(TAG, msg + getPoint());
		}
		if(isLogput){
			putLog(msg + getPoint());
		}
	}

	/**
	 * LogCatに出力：レベル=デバッグ
	 * @param msg 出力メッセージ
	 */
	public static void e(String msg) {
		String errorMessage = msg + getPoint();
		Log.e(TAG, errorMessage);
		String line =  "\n---------------------------\n";
		errorMessage = line + errorMessage + line;
		putError(errorMessage);
	}
	
	/**
	 * LogCatに出力：レベル=デバッグ
	 * 
	 * @param msg 出力メッセージ
	 * @param e エラー
	 */
	public static void e(String msg, Throwable e) {
		String errorMessage = msg + getPoint();
		Log.e(TAG, ">>ERROR" , e.getCause());
		Log.e(TAG, errorMessage , e);
		String line =  "\n---------------------------\n";
		errorMessage = line + errorMessage + "\n" + exceptionText(e) + line;
		putError(errorMessage);
	}

	/**
	 * LogCatに出力：レベル=デバッグ
	 * 
	 * @param msg
	 *            出力メッセージ
	 */
	public static void v(String msg) {
		if (isLogcat) {
			Log.v(TAG, msg + getPoint());
		}
		if(isLogput){
			putLog(msg + getPoint());
		}
	}

	/**
	 * LogCatに出力：レベル=警告
	 * 
	 * @param msg
	 *            出力メッセージ
	 */
	public static void w(String msg) {
		if (isLogcat) {
			Log.w(TAG, msg + getPoint());
		}
		if(isLogput){
			putLog(msg + getPoint());
		}
	}
	/**
	 * LogCatに出力：レベル=警告
	 * 
	 * @param msg 出力メッセージ
	 * @param e 警告
	 */
	public static void w(String msg, Exception e) {
		String message = null;
		if (isLogcat) {
			message = msg + getPoint();
			Log.w(TAG, message, e);
		}
		if(isLogput){
			if(message == null){
				message = msg + getPoint() + "\n" + exceptionText(e);
			}else{
				message = message + "\n" + exceptionText(e);
			}
			putLog(message);
		}
	}

	/**
	 * クラス・メソッド名・列数を取得
	 * 
	 * @return クラス・メソッド名・列数
	 */
	private static String getPoint() {
		StackTraceElement stack = Thread.currentThread().getStackTrace()[4];
		return "  (" + stack.getClassName() + "." + stack.getMethodName() + "(" + stack.getLineNumber() + "))";
	}

	/**
	 * Logcatを出力するかどうかチェック<br>
	 * StartUp時にチェックすること
	 * 
	 * @return true:出力する,false:出力しない
	 */
	public static void isLogCat() {
		// SDカード直下にLogCat.txt がある場合は出力する
		String sd = Environment.getExternalStorageDirectory().getPath();
		if (Utils.checkFile(sd + "/" + LOGCAT)) {
			isLogcat = true;
		}
	}
	
	// ----------------------------------------
	// 	Put
	// --------------------------
	
	/**
	 * エラーログをSDカードに出力
	 * @param message
	 */
	private static void putError(String message){
		// エラーログ出力ディレクトリパス
		String putFilePath = Preferences.sExternalStrage + "/" + ERROR_DIR;
		try {
			FileUtil.mkdir_p(putFilePath);
			// fileName=yyyyMMdd.txt
			putFilePath = putFilePath + "/Error_" + logPutDate() + ".txt";
			output(putFilePath, message);
		} catch (IOException e) {
			// エラー出力ディレクトリが作成できなかった場合 or 既に作成済みの場合 ... Logcatに表示
			Log.w(TAG, e.getMessage());
		}
	}
	
	/**
	 * デバッグ用ログを出力
	 * @param message 出力メッセージ
	 */
	private static void putLog(String message){
		// ログ出力ディレクトリパス
		String putFilePath = Preferences.sExternalStrage + "/" + LOG_DIR;
		if(Utils.checkFile(putFilePath)){
			// fileName=yyyyMMdd.txt
			putFilePath = putFilePath + "/" + logPutDate() + ".txt";
			output(putFilePath, message);
		}else{
			// 初期同時チェックではログ出力フラグディレクトリが存在していたが途中で削除した可能性がある為フラグをfalseに設定
			isLogput = false;
		}
	}
	
	/**
	  * ファイル出力
	  * @param text 出力コメント
	  * @param filePath 出力ファイルパス
	  */
	private static void output(String filePath, String text) {
		try {
			BufferedWriter bw = null;
			FileOutputStream file = new FileOutputStream(filePath, true);
			bw = new BufferedWriter(new OutputStreamWriter(file, "UTF-8"));
			bw.append(text + "\n");
			bw.close();
			bw = null;
		} catch (FileNotFoundException e) {
			Log.e(TAG, "ERROR:LOGPUT" + "/" + e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "ERROR:LOGPUT" + "/" + e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, "ERROR:LOGPUT" + "/" + e.getMessage(), e);
		}
	}

	// ----------------------------------------
	// 	ログ出力必要メソッド
	// --------------------------
	
	/**
	 * exceptionをtextにして返す
	 * @param e Exception
	 * @return エラーテキスト
	 */
	private static String exceptionText(Throwable e){
		String errorText = null;
		for (StackTraceElement s : e.getStackTrace()) {
			if(errorText == null){
				errorText = s.toString();
			}else{
				errorText = errorText + "\n" + s.toString();
			}
		}
		return errorText;
	}
	
	/**
	 * エラーログファイル名用日時文字列 - yyyyMMdd
	 * 
	 * @return yyyyMMddの日時文字列(OS時計本日)
	 */
	private static String logPutDate() {
		SimpleDateFormat fmt = new SimpleDateFormat(LOG_DATE_PATTERN);
		return fmt.format(new Date());
	}
}
