package net.keyring.bookend.request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.zip.InflaterInputStream;

import net.keyring.bookend.Logput;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.R;
import net.keyring.bookend.constant.Const;
import net.keyring.bookend.constant.ConstReq;
import net.keyring.bookend.util.StringUtil;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

/**
 * bookendサーバーにリクエストを投げ、レスポンス(XMLparser)を返す
 * @author Hamaji
 *
 */
public class RequestServer {
	
	/** リクエストパラメータ */
	private ArrayList<NameValuePair> mParams;
	/** リクエストアクション */
	private String mAction;
	/** Context */
	private Context	 mCon;
	/**
	 * コンストラクタ
	 * @param params パラメータリスト
	 * @param action リクエストアクション
	 */
	public RequestServer(ArrayList<NameValuePair> params, String action, Context cont){
		this.mParams = params;
		this.mAction = action;
		Logput.d(">>Request Action [" + action + "] ---------------");
		Logput.d("Request Params " + params.toString());
		mCon = cont;
	}
	
	/**
	 * Request - メインメソッド
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 */
	public XmlPullParser execute() throws IOException, XmlPullParserException{
		XmlPullParser parser = null;
		if(!isParamsNull(mParams)){
			String url = setRequestURL(mAction);
			Logput.d("Request URL = " + url);
			if(!StringUtil.isEmpty(url)){
				//HttpPostを生成する
			    HttpPost request = new HttpPost(url);
		    	// request.getParams();
			    try{
			    	request.setEntity(new UrlEncodedFormEntity(mParams, HTTP.UTF_8));
			    }catch(IOException e){
			    	throw new IOException("The encoding isn't supported.");
			    }
		    	parser = setParser(httpPost(request));
			}
		}
		return parser;
	}
	
	/**
	 * HTTPリクエストを行う
	 * @param request
	 * @param con_timuout	ネットワーク接続確立までの待ち時間(msec) -1:デフォルト値を使用, 0:無期限
	 * @param so_timeout	データ受信までの待ち時間(msec)　-1:デフォルト値を使用, 0:無期限
	 * @param retry_count	リトライ回数
	 * @param retry_interval	リトライまでの待ち時間(sec)
	 * @return HttpResponse
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	static public HttpResponse sendRequest(HttpUriRequest request, int con_timuout, int so_timeout, int retry_count, int retry_interval) 
			throws IOException, ClientProtocolException 
	{
		Logput.i("INFO : request = " + request);
		Logput.i("INFO : con_timuout = " + con_timuout);
	    Logput.i("INFO : so_timeout = " + so_timeout);
	    Logput.i("INFO : retry_count = " + retry_count);
	    Logput.i("INFO : retry_interval = " + retry_interval);
	    
		HttpResponse response = null;
	    
		for(; retry_count >= 0; retry_count--){
			//	タイムアウトの設定
			HttpParams httpParams = new BasicHttpParams();
			if(con_timuout >= 0) {
		    	HttpConnectionParams.setConnectionTimeout(httpParams, con_timuout);
		    }
		    if(so_timeout >= 0) {
		    	HttpConnectionParams.setSoTimeout(httpParams, so_timeout);
		    }
		    HttpClient httpclient = new DefaultHttpClient(httpParams);
		    
		    // リクエスト
		    try{
		    	response = httpclient.execute(request);
		    	int responseCode = response.getStatusLine().getStatusCode();
		    	// リクエストに成功した場合はループを抜ける
			    if (responseCode == HttpStatus.SC_OK){
			    	break;
			    }
		    }catch(ClientProtocolException e){
		    	e.printStackTrace();
		    	if(retry_count <= 0) {
		    		throw e;
		    	}
		    }catch(IOException e){
		    	e.printStackTrace();
		    	if(retry_count <= 0) {
		    		throw e;
		    	}
		    } 
		    
		    //	リトライまで待つ
		    if(retry_count > 0){
	    		Logput.i("FAIL : retry = " + retry_count);
	    		try {
	    			Thread.sleep(retry_interval*1000);
	    		}catch(InterruptedException e) {
	    		}
	    	}
		}
		
		return response;
	}

	/**
	 * HttpPost実行
	 * @param HttpPost
	 * @return XmlPullParser
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 */
	private HttpResponse httpPost(HttpPost request) throws IOException, ClientProtocolException {
		
		//retry
	    int retry_count = Integer.valueOf(mCon.getString(R.string.network_retry_count));
	    int retry_interval = Integer.valueOf(mCon.getString(R.string.network_retry_interval));
	    int timeOutValue = Integer.valueOf(mCon.getString(R.string.network_timeout))*1000;
		
		return sendRequest(request, timeOutValue, timeOutValue, retry_count, retry_interval);
	}
	
	/**
	 * サーバーから受け取ったxmlレスポンスをXmlPullParserに設定
	 * @param サーバーから受け取ったxmlレスポンス
	 * @return レスポンス内容をセットしたXmlPullParser
	 * @throws IOException
	 * @throws XmlPullParserException 
	 */
	private XmlPullParser setParser(HttpResponse response) throws IOException, XmlPullParserException, IllegalStateException{
		Logput.v("response = " + response);
		if(response == null) return null;
		// リクエストした結果のステータスを取得
    	int status = response.getStatusLine().getStatusCode();
    	XmlPullParser parser = null;
    	if(status == HttpStatus.SC_OK){
    		if(mAction == ConstReq.GET_CONTENTS){
	        	try{
	        		InputStream instream =  new InflaterInputStream(response.getEntity().getContent());
	        		// xmlパーサーを生成
	    			final XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	    			parser = factory.newPullParser();
	    			// xmlパーサに解析したい内容を設定
	    			parser.setInput(instream, "UTF-8");
	        		Logput.v("[Zip file]");
	        	}catch(IllegalStateException e){
	        		throw new IOException("this entity is not repeatable and the stream has already been obtained previously  ");
	        	}catch(IOException e){
	        		throw new IOException("Format != Zip : the stream could not be created");
	        	}
    		}else{
    			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
    			response.getEntity().writeTo(outstream);
    			// xmlパーサーを生成
    			final XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
    			parser = factory.newPullParser();
    			// xmlパーサに解析したい内容を設定
    			parser.setInput(new StringReader(outstream.toString()));
    			Logput.d("XML = " + outstream.toString());
    		}
    	}else{
    		Logput.i("HttpStatus failed <" + status + ">");
    	}
    	return parser;
	}
	
	/**
	 * リクエストURLをセット
	 * @param リクエストアクション
	 * @return リクエストURL
	 */
	private String setRequestURL(String request){
		String url = null;
		if(!StringUtil.isEmpty(request)){
			url = Const.DOMAIN;
			if(Preferences.sMode) url = Const.DOMAIN_DEMO;
			url = url + request;
		}
		return url;
	}
	
	/**
	 * パラメータリストNULLチェック
	 * @return パラメータリストが空の場合はtrue,それ以外はfalseを返す
	 */
	private boolean isParamsNull(ArrayList<NameValuePair> params){
		if(params == null) return true;
		else if(params.size() < 1) return true;
		else return false;
	}
}
