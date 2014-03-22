package com.gso.serviceapilib;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Service implements Runnable {

	private static final String TAG = Service.class.getSimpleName();
	private HttpURLConnection mConnection;
	private ServiceAction mAction;
	private ArrayList<IServiceListener> mListener;
	private boolean mConnecting;
	private Thread mThread;
	private String mActionURI;
	private Map<String, Object> mParams;
	private boolean mIncludeHost;
	private boolean mIsGet;
	private Service mService;
	private boolean mIsBitmap;
	private HttpClient httpclient;

	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			for (IServiceListener listener : mListener) {
				if (listener != null)
					listener.onCompleted(mService, (ServiceResponse) msg.obj);
			}

		}
	};

	public Service(IServiceListener... listeners) {
		mAction = ServiceAction.ActionNone;
		mListener = new ArrayList<IServiceListener>();
		if (listeners != null)
			for (IServiceListener listener : listeners) {
				mListener.add(listener);
			}
		mConnecting = false;
		mIncludeHost = true;
		mService = this;
		mIsBitmap = false;
	}

	public void addListener(IServiceListener listener) {
		mListener.add(listener);
	}

	public boolean isConnecting() {
		return mConnecting;
	}

	public void stop() {
		// cleanUp();
		if (httpclient != null)
			httpclient.getConnectionManager().shutdown();
		mAction = ServiceAction.ActionNone;
		mConnecting = false;
	}

	private boolean request(String uri, Map<String, Object> params,
			boolean includeHost, boolean isGet) {
		mIsGet = isGet;
		request(uri, params, includeHost);
		return true;
	}

	private boolean request(String uri, Map<String, Object> params,
			boolean includeHost) {
		if (mConnecting)
			return false;
		mConnecting = true;
		mActionURI = uri;
		mParams = params;
		mIncludeHost = includeHost;
		mThread = new Thread(this);
		mThread.start();
		return true;
	}

	private void processError(ResultCode errorCode) {
		// if (mListener == null || mAction == ServiceAction.ActionNone
		// || !mConnecting)
		// return;
		Message msg = handler.obtainMessage(0, new ServiceResponse(mAction,
				null, errorCode));
		handler.sendMessage(msg);
	}

	private void dispatchResult(String result) {
		if (mListener == null || mAction == ServiceAction.ActionNone
				|| !mConnecting)
			return;

		ServiceAction act = mAction;
		Object resObj = result;
		ServiceResponse response = null;
		switch (act) {
		// TODO
		case ActionNone:
			break;
		default:
			resObj = result;
			break;
		}
		if (resObj == null)
			response = new ServiceResponse(act, null, ResultCode.Failed);
		else
			response = new ServiceResponse(act, resObj);
		stop();
		Message msg = handler.obtainMessage(0, response);
		handler.sendMessage(msg);
	}

	private void dispatchResult(Bitmap result) {
		if (mListener == null || mAction == ServiceAction.ActionNone
				|| !mConnecting)
			return;
		ServiceAction act = mAction;
		ServiceResponse response = null;
		if (result == null)
			response = new ServiceResponse(act, null, ResultCode.Failed);
		else
			response = new ServiceResponse(act, result);
		stop();
		Message msg = handler.obtainMessage(0, response);
		handler.sendMessage(msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Log.d(mAction.toString(), "=========== run ===========\n" + mActionURI);
		httpclient = new DefaultHttpClient();
		// AndroidHttpClient httpclient = AndroidHttpClient
		// .newInstance(mActionURI);
		HttpParams httpParameters = httpclient.getParams();
		// HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 30000);
		HttpConnectionParams.setSoTimeout(httpParameters, 600000);
		HttpConnectionParams.setTcpNoDelay(httpParameters, true);
		try {
			String urlString;
			urlString = mIncludeHost ? API.hostURL + mActionURI : mActionURI;
			HttpRequestBase request = null;

			if (mIsGet) {
				request = new HttpGet();
				if (mParams != null) {
					attachUriWithQuery(request, Uri.parse(urlString), mParams);
				}
			} else {
				if(mAction == ServiceAction.ActionUpload){
					Log.d("ActionUpload","ActionUpload"+ServiceAPILibApplication.token);
					urlString+="?SessionID="+ServiceAPILibApplication.token;
					Log.d("ActionUpload","ActionUpload"+urlString);
				}
				request = new HttpPost(urlString);
				if (mParams != null) {
					MultipartEntity reqEntity = paramsToList2(mParams);

					// UrlEncodedFormEntity formEntity = new
					// UrlEncodedFormEntity(
					// paramsToList(mParams), HTTP.UTF_8);
					((HttpPost) request).setEntity(reqEntity);
				}
			}

			// Set default headers
			HttpResponse response = httpclient.execute(request);
			String token = ServiceAPILibApplication.token;
			if (token != null ){
//				request.addHeader("Authorization", ""+token);
				request.setHeader("Content-Type", "x-zip");
				request.setHeader("Authorization", "" + token); // OAuth
			}
			InputStream in = null;

			if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
				Header[] header = response.getHeaders("Content-Encoding");
				if (header != null && header.length != 0) {
					for (Header h : header) {
						if (h.getName().trim().equalsIgnoreCase("gzip"))
							in = new GZIPInputStream(response.getEntity()
									.getContent());
					}
				}

				if (in == null)
					in = new BufferedInputStream(response.getEntity()
							.getContent());

				if (mIsBitmap) {
					Bitmap bm = BitmapFactory.decodeStream(in);
					dispatchResult(bm);
				} else {
					String temp = convertStreamToString(in);// text.toString();
					Log.d(mAction.toString(), "==" + temp + "");
					in.close();
					dispatchResult(temp);
				}

			} else if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_NOT_FOUND)
				processError(ResultCode.Failed);
			else if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_SERVER_ERROR)
				processError(ResultCode.ServerError);
			else
				processError(ResultCode.NetworkError);

		} catch (Exception e) {
			e.printStackTrace();
			processError(ResultCode.NetworkError);
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}

	private void attachUriWithQuery(HttpRequestBase request, Uri uri,
			Map<String, Object> params) {
		try {
			if (params == null) {
				// No params were given or they have already been
				// attached to the Uri.
				request.setURI(new URI(uri.toString()));
			} else {
				Uri.Builder uriBuilder = uri.buildUpon();

				// Loop through our params and append them to the Uri.
				for (BasicNameValuePair param : paramsToList(params)) {
					uriBuilder.appendQueryParameter(param.getName(),
							param.getValue());
				}

				uri = uriBuilder.build();
				request.setURI(new URI(uri.toString()));
			}
		} catch (URISyntaxException e) {
			Log.e(TAG, "URI syntax was incorrect: " + uri.toString());
		}
	}

	private String convertStreamToString(InputStream is) {
		// TODO Auto-generated method stub

		/*
		 * To convert the InputStream to String we use the Reader.read(char[]
		 * buffer) method. We iterate until the Reader return -1 which means
		 * there's no more data to read. We use the StringWriter class to
		 * produce the string.
		 */

		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is,
						"UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} catch (IOException e) {
				return "";
			} finally {
				try {
					is.close();
				} catch (IOException e) {

				}
			}
			return writer.toString();
		} else {
			return "";
		}

	}

	private static List<BasicNameValuePair> paramsToList(
			Map<String, Object> params) {
		ArrayList<BasicNameValuePair> formList = new ArrayList<BasicNameValuePair>(
				params.size());

		for (String key : params.keySet()) {
			Object value = params.get(key);

			if (value != null)
				formList.add(new BasicNameValuePair(key, value.toString()));
		}

		return formList;
	}

	public static MultipartEntity paramsToList2(Map<String, Object> params) {
		MultipartEntity reqEntity = new MultipartEntity();
		for (String key : params.keySet()) {
			try {

				Object value = params.get(key);
				if (key.toUpperCase().equals("FILE")) {
					reqEntity.addPart(key, (ContentBody) value);
				} else {
					Charset chars = Charset.forName("UTF-8");
					reqEntity.addPart(key, new StringBody(value.toString(),
							chars));
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		return reqEntity;
	}

	public int getConnectionTimeout() {
		return mConnection.getConnectTimeout();
	}

	public void login(ServiceAction action, String serviceType, Map<String, Object> params) {
		mAction = action;
		request(serviceType, params, true, false);
	}

}
