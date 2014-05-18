package net.keyring.bookend.request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.TimeZone;

import net.keyring.bookend.Logput;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.constant.Const;
import net.keyring.bookend.util.DateUtil;
import net.keyring.bookend.util.StringUtil;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
/**
 * 時間設定の不正がないかチェックする
 * @author Hamaji
 *
 */
public class ServerGetTime{
	// XMLタグ名
	private static final String TIME = "Time";
	private static final String STATUS = "Status";
	private static final String STATUS_DES = "StatusDescription";
	// ステータスコード
	private static final String OK = "30000";
	private static final String PARAMETER_ERROR = "30010";
	private static final String SERVER_ERROR = "30099";

	/** HttpResponse */
	private HttpResponse _response = null;
	
	/**
	 * サーバーから現在時刻を取得
	 * @return 現在日時文字列
	 */
	public String getNow(){
		_response = postRequest();
		if(_response != null){
			// サーバーから時間取得（serverStatusも取得）
			return responseWrite(_response);
		}else{
			return null;
		}
	}

	/**
	 * 時計チェック
	 * @param 最終起動時刻
	 * @return 異常なければtrue、その他はfalseを返す
	 */
	public boolean checkClock(){
		boolean check = false;
		String time = getNow();
		
		// サーバーから時間が取得できていた場合はチェックを行う
		if(!StringUtil.isEmpty(time)){
			// サーバーから取得した時間とOSの時計を比較
			if(nowTimeCheck(time)){
				check = true;
			}
		}else{
			Logput.e("Server get date or Last startup date is NULL");
		}
		return check;
	}

	/**
	 * GetTimeリクエストをPOST
	 */
	private HttpResponse postRequest(){
		String url;
		if(!Preferences.sMode){
			// 本番環境
			url = Const.GETTIME;
		}else{
			url = Const.GETTIME_DEMO;
		}
		// HttpClient取得
	    HttpClient httpclient = new DefaultHttpClient();
	    //HttpPostを生成する
	    HttpPost request = new HttpPost(url);


	    // リクエスト
	    try {
	    	_response = httpclient.execute(request);
	    }catch(ClientProtocolException e){
	    	Logput.e(e.getMessage(), e);
	    } catch (IOException e) {
	    	Logput.e(e.getMessage(), e);
	    }
	    return _response;
	}

	/**
	 * レスポンスをOutputStreamに書きだし、xmlパーサにセット
	 * @param response
	 * @return serverから取得した時間
	 */
	private String responseWrite(HttpResponse response){
		String time = null;
		// リクエストした結果のステータスを取得
    	int status = response.getStatusLine().getStatusCode();
    	if(status == HttpStatus.SC_OK){
			try{
				// レスポンスをOutputStreamに書きだす
				ByteArrayOutputStream outstream = new ByteArrayOutputStream();
				response.getEntity().writeTo(outstream);

				// xmlパーサーを生成
				final XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				final XmlPullParser parser = factory.newPullParser();
				// xmlパーサに解析したい内容を設定
				parser.setInput(new StringReader(outstream.toString()));
				// serverの時間を取得
				time = getServerTime(parser);
			}catch(IOException e){
				Logput.e(e.getMessage(), e);
			}catch(XmlPullParserException e){
				Logput.e(e.getMessage(), e);
			}
    	}else{
    		Logput.i("status != HttpStatus.SC_OK : " + status);
    	}
    	return time;
	}

	/**
	 * xmlからサーバ側の時間取得
	 */
	private String getServerTime(XmlPullParser parser){
		//*********************************************
		// <?xml version=”1.0” ?>
		// <GetTime version=”0.1”>
		// 	<Time>サーバ側のUTC時間の文字列</Time>
		// 	<Status>ステータスコード</Status>
		// 	<StatusDescription>ステータス情報</StatusDescription>
		// </GetTime>
		//**********************************************

		String time = null;
		try{
			Logput.v(">>GetTime From Server");
			int eventType = parser.getEventType();
			String tagName = null;
			while(eventType != XmlPullParser.END_DOCUMENT){
				tagName = parser.getName();
				if(eventType == XmlPullParser.START_TAG){
					if(tagName.equals(TIME)){	// <Time>
						parser.next();
						time = parser.getText();
						Logput.d("<time>" + time);
					}else if(tagName.equals(STATUS)){
						parser.next();
						String serverStatus = parser.getText();
						if(serverStatus.equals(OK)){
							Logput.v("<Status>OK ");
						}else if(serverStatus.equals(PARAMETER_ERROR)){
							Logput.i("<Status>PARAMETER_ERROR ");
							break;
						}else if(serverStatus.equals(SERVER_ERROR)){
							Logput.i("<Status>SERVER_ERROR ");
						}else{
							Logput.i("<Status> " + serverStatus);
							break;
						}
					}else if(tagName.equals(STATUS_DES)){
						parser.next();
						String description = parser.getText();
						Logput.v("<StatusDescription> " + description);
					}
				}
				eventType = parser.next();
			}
		}catch(IOException e){
			Logput.e(e.getMessage(), e);
		}catch(XmlPullParserException e){
			Logput.e(e.getMessage(), e);
		}
		return time;
	}

	/**
	 * サーバーから取得した時間とOSの時計を比較
	 * @param サーバーから取得した日時文字列
	 * @return サーバーから取得した時間よりOSの時計が10分以上前ならfalseを返す
	 */
	private boolean nowTimeCheck(String serverDate){
		boolean timeCheck = true;
		// サーバーから取得した日時文字列をData型に変更
		Date serverNow = DateUtil.toDate(serverDate,TimeZone.getTimeZone("UTC"));
		// OSの今の日時を取得
		Date osNow = DateUtil.getNow();
		Logput.d("[OS_TIME]" + osNow + " [SERVER_TIME]" + serverNow);
		// サーバーから取得した日時がOSの時間より後なら問題なし
		if(osNow.after(serverNow)){
			// サーバーから取得した日時よりOSの日時が10分以上後だったらfalse
			serverNow = new Date(serverNow.getTime() + (600000));
			//Logput.d(">>>Server Date - 10m : " + serverNow + "/ osNow : " + osNow);
			if(osNow.after(serverNow)){
				Logput.w(">>>ERROR : Server Date + 10m < Now date");
				timeCheck = false;
			}
		}else{
			// サーバーから取得した日時よりOSの日時が10分以上前だったらfalse
			serverNow = new Date(serverNow.getTime() + (-600000));
			Logput.d(">>>Server Date - 10m : " + serverNow + "/ osNow : " + osNow);
			if(osNow.before(serverNow)){
				Logput.w(">>>ERROR : Server Date - 10m > Now date");
				timeCheck = false;
			}
		}
		return timeCheck;
	}
}
