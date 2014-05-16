package net.keyring.bookend.asynctask;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import net.keyring.bookend.ImageCache;
import net.keyring.bookend.Logput;
import net.keyring.bookend.util.StringUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * リストに表示する画像取得<br>
 *
 * AsyncTask[Params(String)、Progress(Void)、Result(Bitmap)]<br><br>
 *
 *   [Params] … Activityからスレッド処理へ渡したい変数の型<br>
 *			※ Activityから呼び出すexecute()の引数の型<br>
 *          ※ doInBackground()の引数の型<br><br>
 *
 *   [Progress] … 進捗度合を表示する時に利用したい型<br>
 *          ※ onProgressUpdate()の引数の型<br><br>
 *
 *   [Result] … バックグラウンド処理完了時に受け取る型<br>
 *          ※ doInBackground()の戻り値の型<br>
 *          ※ onPostExecute()の引数の型<br><br>
 *
 *   ※ それぞれ不要な場合は、Voidを設定すれば良い<br>
 *
 * @author Hamaji
 */;
public class ImageGetTask extends AsyncTask<String,Void,Bitmap> {
	/** 画像を表示するview */
	private ImageView mImage;
	/** プログレスバー */
    private ProgressBar mProgress;
    /** イメージタグ */
    private String mTag;
    /** 画像ダウンロードURL */
    private String mImageURL;
	/** 画像取得失敗時の表示画像 */
	private Drawable mErrorImage;

    /**
     * コンストラクタ
     * @param image	画像表示view
     * @param progress	プログレスバー
     * @param imageURL	画像ダウンロードURL
     * @param listener 
     */
    public ImageGetTask(ImageView image, ProgressBar progress, Drawable errorImage) {
        //対象の項目を保持しておく
    	mImage = image;
    	mProgress = progress;
        mTag = mImage.getTag().toString();
        mImageURL = mTag;
        mErrorImage = errorImage;
    }

    /**
     * 画像のダウンロード(バックグラウンド処理)
     * @param 進捗具合を表すパラメータ
     */
    @Override
    protected Bitmap doInBackground(String... params) {
        // ここでHttp経由で画像を取得します。取得後Bitmapで返します。
        synchronized (ImageGetTask.this){
        	String keyURL = params[0];
            try {
                //キャッシュより画像データを取得
                Bitmap image = ImageCache.getImage(keyURL);
                if (image == null) {
                    // キャッシュにデータが存在しない場合
                	if(!StringUtil.isEmpty(mImageURL)){
                		URL imageUrl = new URL(mImageURL);
                        InputStream imageIs;
                        imageIs = imageUrl.openStream();
                        image = BitmapFactory.decodeStream(imageIs);
                        //取得した画像データをキャッシュに保持
                        ImageCache.setImage(keyURL, image);
                        imageIs.close();
                	}
                }
                return image;
            } catch (MalformedURLException e) {
            	Logput.w("URL error = " + keyURL);
                return null;
            } catch (OutOfMemoryError e){
            	Logput.w("OutOfMemoryError = " + keyURL);
            	return null;
            } catch (IOException e) {
            	Logput.w("Error cause = " + e.getCause() + "/ " + e.getMessage());
                return null;
            } catch (Exception e){
            	Logput.w("Error cause = " + e.getCause() + "/ " + e.getMessage());
            	return null;
            }
        }
    }

    /**
     * 処理完了時に呼ばれるメソッド(GUIと同じスレッド)
     * doInBackground()の戻り値がここに通知される
     */
    @Override
    protected void onPostExecute(Bitmap result) {
        // Tagが同じものか確認して、同じであれば画像を設定する
        // （Tagの設定をしないと別の行に画像が表示されてしまう）
        if(mTag.equals(mImage.getTag())){
            if(result!=null){
                //画像の設定
            	mImage.setImageBitmap(result);
            }
            else{
                //エラーの場合は×印を表示
            	mImage.setImageDrawable(mErrorImage);
            }
            //プログレスバーを隠し、取得した画像を表示
            mProgress.setVisibility(View.GONE);
            mImage.setVisibility(View.VISIBLE);
        }
    }
}