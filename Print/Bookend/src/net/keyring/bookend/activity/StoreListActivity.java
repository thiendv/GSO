package net.keyring.bookend.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.keyring.bookend.ImageCache;
import net.keyring.bookend.Preferences;
import net.keyring.bookend.R;
import net.keyring.bookend.constant.ConstReq;
import net.keyring.bookend.constant.ConstViewDefault;
import net.keyring.bookend.util.StringUtil;
import net.keyring.bookend.util.Utils;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 販売ストアリスト Activityクラス
 * @author Hamaji
 *
 */
public class StoreListActivity extends BookendActivity implements ConstViewDefault,ConstReq{
	private ArrayList<Map<String, String>> mData;
	private StoreListAdapter mAdapter = null;        //独自のアダプタを作成

	private String[] mFromTemplate = {STORE_NAME, STORE_URL, STORE_IMAGE_URL, STORE_TYPE};
	private int[] mToTemplate = {R.id.storeName, R.id.storeUrl, R.id.storeImage};

	@Override
    public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store);
	}
	
	@Override
	public void onRestart(){
		super.onRestart();
		setContentView(R.layout.store);
	}

	@Override
	public void onResume(){
		super.onResume();
		mLayout = (LinearLayout)findViewById(R.id.store);
		// デバックモードチェック
    	if(Utils.isDebugMode(this.getApplicationContext())){
    		// デバックモード有効：NG
    		dialog(DIALOG_ID_ERROR, getString(R.string.debug_mode_message));
    	}else{
	    	ListView list_group = (ListView)findViewById(R.id.store_list);
	    	// 販売サイト情報取得
	    	mData = Preferences.sStoreList;
	
	    	mAdapter = new StoreListAdapter(mData, R.layout.store_list, mFromTemplate, mToTemplate);
	
	    	if(mData != null && mData.size() > 0){
	    	    mAdapter.setListData(mData);    //テンプレートにデータ内容を保持
	
	    	    list_group.setFocusable(false);
	    	    list_group.setAdapter(mAdapter);
	    	    list_group.setScrollingCacheEnabled(false);
	    	    list_group.setTextFilterEnabled(true);
	
	    	    list_group.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    	        @Override
	    	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    	            //リスト選択時の処理
	    	            ImageCache.clearCache();    //キャッシュのクリア
	    	            if(mData == null) mData = Preferences.sStoreList;;
	
	    	            if(mData != null){
			            	Map<String,String> store = mData.get(position);
			            	viewStore(store);
	    	            }else{
	    	            	Toast.makeText(StoreListActivity.this, "Store data : None.", Toast.LENGTH_SHORT).show();
	    	            }
	    	            //終了処理
	    	            onStop();
	    	        }
	    	    });
	    	}
		}
	}


	/**
     * 販売サイト表示<br>
     * ※ StoreTypeが2以外の場合はToast表示
     * @param 販売サイト詳細情報リスト
     */
    private void viewStore(Map<String,String> store){
        String type = store.get(STORE_TYPE).toString();
        if(!StringUtil.isEmpty(type)){
        	if(type.equals("2")){
        		// 販売サイトへ
        		String url = store.get(STORE_URL).toString();
        		// ブラウザアプリで販売サイト表示
				Intent store_site = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
				store_site.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        	startActivity(store_site);
        	}else{
        		Toast.makeText(this, this.getString(R.string.store_type_NG), Toast.LENGTH_SHORT).show();
        	}
        }else{
        	Toast.makeText(this, this.getString(R.string.store_type_NG), Toast.LENGTH_SHORT).show();
        }
    }

	@Override
	public void onStop(){
		super.onStop();
		mData = null;
		mAdapter = null;
		onDestroy();
	}
	
	/**
	 * 販売サイトリストアダプタ
	 * @author Hamaji
	 *
	 */
	class StoreListAdapter extends SimpleAdapter {
	/** Inflater */
    private LayoutInflater mInflater;
    /** Store情報リスト(販売サイト名、販売サイトURL、画像DL-URL、表示タイプ) */
    private ArrayList<Map<String, String>> mStoreItems;

    /**
     * コンストラクタ
     * @param context
     * @param data 表示データ
     * @param resource リストレイアウト
     * @param from 表示キー
     * @param to レイアウトID
     */
    public StoreListAdapter(List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(StoreListActivity.this, data, resource, from, to);
        // Viewを作成するLayoutInflaterを取得
        this.mInflater = (LayoutInflater) StoreListActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

	    /** データ内容を保持 */
	    private void setListData(ArrayList<Map<String, String>> data){
	        mStoreItems = data;
	    }

	    /**
	     * 必ず呼ばれる
	     * @param position
	     * @param convertView レイアウト
	     * @param parent
	     */
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {

	        View v = convertView;
	        if(v == null){
	        	// StoreListレイアウト
	            v = mInflater.inflate(R.layout.store_list, null);
	        }

	        // 販売サイト情報を取得
	        Map<String, String> settingData = mStoreItems.get(position);

	        // 販売サイト名
	        TextView storeName = (TextView)v.findViewById(R.id.storeName);
	        // 遷移先のURL
	        TextView storeURL = (TextView)v.findViewById(R.id.storeUrl);
	        // 販売サイト画像
	        ImageView imageView = (ImageView)v.findViewById(R.id.storeImage);
	        // 画像取得中に表示するプログレスバー
	        ProgressBar waitBar = (ProgressBar)v.findViewById(R.id.WaitBar);

	        if(settingData == null) return v;
	        //画像を隠し、プログレスバーを表示
	        waitBar.setVisibility(View.VISIBLE);
	        imageView.setVisibility(View.GONE);

	        // 販売サイト名セット
	        storeName.setText(settingData.get(STORE_NAME).toString());
	        // サイトURLを保持
	        storeURL.setText(settingData.get(STORE_URL).toString());

	        //仮の画像設定
	        imageView.setImageDrawable(StoreListActivity.this.getResources().getDrawable(android.R.drawable.ic_menu_report_image));
	        //画像読込
	        try{
	            imageView.setTag("icon");
	          //リスト表示用画像のダウンロードURLを取得
	            String thumbURL = settingData.get(STORE_IMAGE_URL).toString();
	            // AsyncTaskは１回しか実行できない為、毎回インスタンスを生成
	            ImageGetTask task = new ImageGetTask(imageView, waitBar, thumbURL);
	            // 画像URLをセット
	            task.execute(thumbURL);
	        }
	        catch(Exception e){
	        	// 取得失敗
	        	// <!>アイコン
	            imageView.setImageDrawable(StoreListActivity.this.getResources().getDrawable(android.R.drawable.ic_dialog_alert));
	            // プログレスバー非表示＆画像表示
	            waitBar.setVisibility(View.GONE);
	            imageView.setVisibility(View.VISIBLE);
	        }
	        return v;
	    }
	}

    /**
     * 画像取得<br>
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
     */
    class ImageGetTask extends AsyncTask<String,Void,Bitmap> {
    	/** 画像を表示するview */
    	private ImageView mImage;
    	/** プログレスバー */
        private ProgressBar mProgress;
        /** イメージタグ */
        private String mTag;
        /** 画像ダウンロードURL */
        private String mImageURL;

        /**
         * コンストラクタ
         * @param image	画像表示view
         * @param progress	プログレスバー
         * @param imageURL	画像ダウンロードURL
         */
        public ImageGetTask(ImageView image, ProgressBar progress, String imageURL) {
            //対象の項目を保持しておく
        	mImage = image;
        	mProgress = progress;
            mTag = mImage.getTag().toString();
            mImageURL = imageURL;
        }

        /**
         * 画像のダウンロード(バックグラウンド処理)
         * @param 進捗具合を表すパラメータ
         */
        @Override
        protected Bitmap doInBackground(String... params) {
            // ここでHttp経由で画像を取得します。取得後Bitmapで返します。
            synchronized (ImageGetTask.this){
                try {
                    //キャッシュより画像データを取得
                    Bitmap image = ImageCache.getImage(params[0]);
                    if (image == null) {
                        //キャッシュにデータが存在しない場合はGetStoreListで取得したURLから取得
                    	URL imageUrl = new URL(mImageURL);
                        InputStream imageIs;
                        imageIs = imageUrl.openStream();
                        image = BitmapFactory.decodeStream(imageIs);
                        //取得した画像データをキャッシュに保持
                        ImageCache.setImage(params[0], image);
                    }
                    return image;
                } catch (MalformedURLException e) {
                    return null;
                } catch (IOException e) {
                    return null;
                }
            }
        }

        /**
         * プログレスを閉じる・結果をコールバックに返す
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
                	mImage.setImageDrawable(StoreListActivity.this.getResources().getDrawable(android.R.drawable.ic_dialog_alert));
                }
                //プログレスバーを隠し、取得した画像を表示
                mProgress.setVisibility(View.GONE);
                mImage.setVisibility(View.VISIBLE);
            }
        }
    }
}
