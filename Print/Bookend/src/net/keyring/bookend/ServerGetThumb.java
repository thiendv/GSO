package net.keyring.bookend;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import net.keyring.bookend.constant.Const;
import net.keyring.bookend.request.GetAwsInfo;
import net.keyring.bookend.util.FileUtil;
import net.keyring.bookend.util.StringUtil;
import net.keyring.bookend.util.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;

public class ServerGetThumb {
	public ServerGetThumb(Context con){
		this.mCon = con;
	}
	private Context mCon;

	/**
	 * ★contentsIDからサムネイルダウンロードURLを取得
	 * @param context
	 * @param contentsID
	 * @return サムネイルダウンロードURL
	 */
	public String getDL_URL(String contentsID){
		if(StringUtil.isEmpty(contentsID)) return null;
		
		String s3host = Preferences.sS3Host;
		String bucketName = Preferences.sBucketName;
		
		if(StringUtil.isEmpty(s3host) || StringUtil.isEmpty(bucketName)){
			// GetAwsInfoリクエストを行う(S3Host,BucketNameを取得)
			GetAwsInfo getAwsInfo = new GetAwsInfo();
			if(getAwsInfo.execute(mCon)){
				// GetAwsInfo - OK
				s3host = Preferences.sS3Host;
				bucketName = Preferences.sBucketName;
			}else{
				// GetAwsInfo - NG
				return null;
			}
		}
		// contentsIDの先頭二文字を取得
		String key = contentsID.substring(0, 2);
		String url = "http://" + bucketName + "." + s3host + "/contents/"+ key + "/" + contentsID + "_thumb";
		return url;
	}


	/**
	 * ★サムネイル取得
	 * @param Context
	 * @return サムネイルを保存したローカルパス
	 */
	public String getThumbnail(String thumbURL){
		String thumb_path = null;
		// サムネイル取得
		HttpResponse dl_Resp = httpGet(thumbURL);
		if(dl_Resp == null) return null;
		else {
			thumb_path = dl_Thumb(dl_Resp);
		}
		return thumb_path;
	}

	/**
	 * 指定URLにアクセスし、結果を受取る
	 * @param ダウンロードURL
	 * @return レスポンスコード
	 */
	private HttpResponse httpGet(String dl_URL){
		if(StringUtil.isEmpty(dl_URL)) return null;
	    int responseCode = -1;
	    HttpClient hClient = null;
	    HttpResponse hResp = null;
	    HttpGet hGet = null;
	    URI url = null;
	    try{
	    	url = new URI(dl_URL);
	    	hClient = new DefaultHttpClient();
		    // HttpGet オブジェクト
		    hGet = new HttpGet();
		    hClient.getParams().setParameter("http.connection.timeout", new Integer(15000));
		    // HttpRequestの結果を受け取る
		    //Utils.debug("GET : " + url);
		    hGet.setURI(url);
		    hResp = hClient.execute(hGet);
		    responseCode = hResp.getStatusLine().getStatusCode();
	    }catch(IllegalStateException e){
	    	Logput.e(e.getMessage() + " ... URI = " + url, e);
		}catch(URISyntaxException e){
			Logput.e(e.getMessage(), e);
	    }catch(ClientProtocolException e){
	    	Logput.e(e.getMessage(), e);
	    }catch(IOException e){
	    	Logput.e(e.getMessage(), e);
	    }
	    // リクエストに成功しなかった場合はnullを返す
	    if (responseCode != HttpStatus.SC_OK){
	    	hResp = null;
	    }
		return hResp;
	}

	/**
	 * サムネイルダウンロード
	 * @param レスポンスコード
	 * @param サムネイルダウンロードURL
	 * @return サムネイルファイル保存パスを返す
	 */
	private String dl_Thumb(HttpResponse dl_Resp){
		if(dl_Resp == null) return null;

		// ランダムなファイル名
		String fileName = Utils.getRandomThumbFileName();
		// サムネイル保存ディレクトリがなければ作成
		String local_thumbDirPath = null;
		String local_ThumbPath = null;
			local_thumbDirPath = setDir(Const.THUMBS_DIR_NAME);
			if(StringUtil.isEmpty(local_thumbDirPath)) return null;
		if(!StringUtil.isEmpty(local_thumbDirPath)){
			// ファイルパス
			local_ThumbPath = local_thumbDirPath + "/" + fileName;
			// ダウンロード
			if(download(dl_Resp,local_ThumbPath)){
				// ダウンロード成功
			}else{
				Logput.w("Thumbnail Download : Failed");
				return null;
			}
		}else{
			Logput.v("image save dir path : null");
		}
		return local_ThumbPath;
	}

	/**
	 * ファイル保存ディレクトリがなければ作成
	 * @param ディレクトリ名
	 * @return ディレクトリパス
	 */
	private String setDir(String dirName){
		if(Preferences.sExternalStrage == null) Preferences.sExternalStrage = mCon.getExternalFilesDir(Const.BOOKEND).getPath();
		String contentsDir = Preferences.sExternalStrage + "/" + dirName;
		try {
			FileUtil.mkdir_p(contentsDir);
			return contentsDir;
		} catch (IOException e) {
			Logput.w(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * ダウンロード
	 * @param リクエストコード
	 * @param ダウンロードファイル保存パス
	 * @return ダウンロードに成功したらtrueを返す
	 */
	private boolean download(HttpResponse hResp,String path){
		boolean downloadCheck = false;
		int BUFFER_SIZE = 10240;
    	File file = new File(path);
    	try{
	        // BufferdInputStream オブジェクトのインスタンスからデータがなくなるまで、バッファサイズ分ずつ読み込み
	    	InputStream is = hResp.getEntity().getContent();
	    	BufferedInputStream in = new BufferedInputStream(is, BUFFER_SIZE);	// ※第二引数のバッファサイズを指定しない場合 = 8K
	        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file, false), BUFFER_SIZE);
	        byte buf[] = new byte[BUFFER_SIZE];
	        int size = -1;
	        while((size = in.read(buf)) != -1) {
	            out.write(buf, 0, size);
	        }
	        out.flush();
	        out.close();
	        in.close();
	        downloadCheck = true;
    	}catch(FileNotFoundException e){
    		Logput.e(e.getMessage(), e);
    	}catch(IOException e){
    		Logput.e(e.getMessage(), e);
    	}
    	return downloadCheck;
    }

}
