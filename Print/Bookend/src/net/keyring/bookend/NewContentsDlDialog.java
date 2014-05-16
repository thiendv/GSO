package net.keyring.bookend;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import net.keyring.bookend.activity.BookendActivity;
import net.keyring.bookend.billing.Security;
import net.keyring.bookend.constant.Const;
import net.keyring.bookend.constant.ConstQuery;
import net.keyring.bookend.constant.ConstViewDefault;
import net.keyring.bookend.db.PurchaseDao;
import net.keyring.bookend.service.BillingService;
import net.keyring.bookend.service.NewDownloadService;
import net.keyring.bookend.util.StringUtil;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NewContentsDlDialog extends Dialog implements ConstQuery,OnClickListener,ConstViewDefault,DialogInterface.OnCancelListener {

	private BookendActivity			mActivity;
	private Map<String, String>		mQueryList;
	private BillingService			mBillingService;
	private PurchaseDao				mPurchaseDao;
	private boolean				mIsPurchased = false;	// ProductIDが設定されている場合、購入済みか否かフラグ
	private String					mProductID;

	private TextView	mTitle;
	private TextView	mAuthor;
	private TextView	mDistributer;
	private ImageView	mThumbnail;
	private TextView	mPurchased;
	private Button		mDlBtn;
	private Button		mCancelBtn;
	
	/**
	 * コンストラクタ - Web書庫用DLダイアログ
	 * 
	 * @param con
	 *            Context
	 * @param listener
	 *            DownloadListener
	 * @param view
	 *            選択されたView
	 * @param book
	 *            　選択されたコンテンツ情報
	 */
	public NewContentsDlDialog(BookendActivity a, BillingService billingService, Map<String, String> queryList){
		super(a, R.style.Theme_CustomDialog);
		this.mActivity = a;
		this.mQueryList = queryList;
		this.mBillingService = billingService;
		setDialog();
	}

	private void setDialog() {
		// レイアウトを決定
		setContentView(R.layout.dl_dialog);
		// サムネイルビューを取得
		mThumbnail = (ImageView) findViewById(R.id.dl_dialog_thumb);
		// タイトルビューを取得
		mTitle = (TextView) findViewById(R.id.dl_dialog_title);
		// 著者ビューを取得
		mAuthor = (TextView) findViewById(R.id.dl_dialog_author);
		// 配布者名ビューを取得
		mDistributer = (TextView) findViewById(R.id.dl_dialog_distribution);
		// OKボタンビューを取得
		mProductID = mQueryList.get(PRODUCT_ID);
		mDlBtn = (Button) findViewById(R.id.new_download_btn);
		mDlBtn.setOnClickListener(this);
		if(!StringUtil.isEmpty(mProductID)){
			mPurchased = (TextView) findViewById(R.id.isPurchased);
			mPurchased.setVisibility(View.VISIBLE);
			if(!checkProductID(mProductID)){
				// 購入していない場合はボタン表示文字列を「購入が必要」に変更
				Logput.d(mActivity.getString(R.string.must_purchased));
				mPurchased.setText(mActivity.getString(R.string.must_purchased));
			}else{
				// 購入済みの場合はボタン表示文字列を「購入済み」に変更
				Logput.d(mActivity.getString(R.string.purchased));
				mPurchased.setText(mActivity.getString(R.string.purchased));
			}
		}
		// Cancelボタンビューを取得
		mCancelBtn = (Button) findViewById(R.id.new_dl_cancel_btn);
		mCancelBtn.setOnClickListener(this);

		//	キャンセル(Backキー押下)時の処理
		setOnCancelListener(this);
		
		// タイトル・著者名を設定
		checkContentText(mTitle, mQueryList.get(TITLE), mAuthor, mQueryList.get(AUTHOR), mDistributer, mQueryList.get(DISTRIBUTOR_NAME));
		// サムネイル画像設定(サムネイルがない場合は非表示)
		Bitmap bitmap = null;
		String thumbnailURL = mQueryList.get(THUMB_URL);
		try{
			bitmap = loadImage(thumbnailURL);
			mThumbnail.setImageBitmap(bitmap);
		}catch(MalformedURLException e){
			mThumbnail.setVisibility(View.GONE);
			Logput.w(e.getMessage(), e);
		}catch (IOException e){
			mThumbnail.setVisibility(View.GONE);
			Logput.w("Get thumbnail fail = " + thumbnailURL);
		}
	}
	
	@Override
	public void onClick(View view){
		if (view == mDlBtn) {
			if(mIsPurchased){
				// DownloadIDをつける
				String payload = mQueryList.get(DOWNLOAD_ID);
				if(checkInAppBilling()){
					if (!mBillingService.requestPurchase(mProductID, payload)) {
						// Android Market 課金サービス利用不可
						mActivity.dialog(DIALOG_BILLING_NOT_SUPPORTED_ID, mActivity.getString(R.string.billing_not_supported_title), mActivity.getString(R.string.billing_not_supported_message));
					}
				}
			}else{
				// DL中のものがなければダウンロード開始
				if(!NewDownloadService.sDlFlag){
					Logput.d("Set queryList.");
					NewDownloadService.sQueryList = mQueryList;
				}else{
					Logput.d("Other file downloading.");
					Toast.makeText(mActivity, "Other file downloading.", Toast.LENGTH_SHORT);
				}
			}
		}
		else if(view == mCancelBtn) {
			//	キャンセルされた場合はIntentにキャンセルフラグをセットする(このフラグは使用してません)
			mActivity.getIntent().putExtra(Const.IS_CANCELED, true);
		}
		dismiss();
	}
	
	@Override
	public void onCancel(DialogInterface dialog) {
		//	キャンセルされた場合はIntentにキャンセルフラグをセットする(このフラグは使用してません)
		mActivity.getIntent().putExtra(Const.IS_CANCELED, true);
	}

	/**
	 * ProductIDが設定されている場合は購入済みではないかチェックする
	 * @return 購入済みの場合はtrue,その他はfalseを返す
	 */
	private boolean checkProductID(String productID){
		if(productID != null){
			mIsPurchased = true;
			// ProductIDが設定されている場合
			Logput.d("ProductID = " + productID);
			// 購入済みではないかチェック
			Set<String> productIdList = getAllPurchasedItems();
			for(String id : productIdList){
				if(productID.equals(id)){
					mIsPurchased = false;
					Logput.d("Purchased [ " + productID + " ]");
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * InAppBilling画面遷移前チェック
	 * @return
	 */
	private boolean checkInAppBilling(){
		boolean check = false;
		// Android Market接続不可
		if (mBillingService.checkBillingSupported()) {
			if(!Preferences.isInAppBillingPublicKeyCheck){
				// keyの確認をしていない場合はチェックする
				try{
					Security.generatePublicKey(mActivity.getString(R.string.inAppBilling_publicKey));
					Preferences.isInAppBillingPublicKeyCheck = true;
					check = true;
				}catch(Exception e){
					Logput.w(mActivity.getString(R.string.error_inappbilling_publicKey));
					mActivity.dialog(DIALOG_ID_VIEW, mActivity.getString(R.string.error_inappbilling_publicKey));
				}
			}else{
				check = true;
			}
		}else{
			Logput.i(mActivity.getString(R.string.cannot_connect_title));
			mActivity.dialog(DIALOG_BILLING_NOT_SUPPORTED_ID,
					mActivity.getString(R.string.cannot_connect_title),
					mActivity.getString(R.string.cannot_connect_message));
		}
		return check;
	}
	
	/**
	 * タイトル・著者名をセット：どちらも指定ない場合は非表示
	 * @param titleView
	 * @param title
	 * @param authorView
	 * @param author
	 */
	private void checkContentText(TextView titleView, String title,
			TextView authorView, String author, TextView distributerView, String distributer){
		boolean titleCheck = StringUtil.isEmpty(title);
		boolean authorCheck = StringUtil.isEmpty(author);
		boolean distributerCheck = StringUtil.isEmpty(distributer);
		if(titleCheck && authorCheck){
			LinearLayout textLayout = (LinearLayout)findViewById(R.id.dl_content_text);
			textLayout.setVisibility(View.GONE);
		}else{
			setText(true, titleCheck, titleView, title);
			setText(false, authorCheck, authorView, author);
			if(!distributerCheck){
				distributerView.setText(mActivity.getString(R.string.detail_distributor_name) + distributer);
			}else{
				distributerView.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 文字列が空の場合は非表示、指定されている場合は表示する
	 * @param textView
	 * @param text
	 * @return 表示する場合はtrue,それ以外はfalseを返す
	 */
	private boolean setText(boolean isTitle, boolean isEmpty, TextView textView, String text) {
		if (isEmpty) {
			if(isTitle){
				textView.setText(mActivity.getString(R.string.detail_title) + " - ");
			}else{
				textView.setText(mActivity.getString(R.string.detail_auther) + " - ");
			}
			return false;
		}else{
			if(isTitle){
				textView.setText(mActivity.getString(R.string.detail_title) + text);
			}else{
				textView.setText(mActivity.getString(R.string.detail_auther) + text);
			}
			return true;
		}
	}

	/**
	 * URLからDrawable取得
	 * @param url サムネイルDL URl
	 * @return Drawable
	 */
	private Bitmap loadImage(String url) throws MalformedURLException, IOException{
		int BUFFER_IO_SIZE = 8000;
		BufferedInputStream bis = new BufferedInputStream(new URL(url).openStream(), BUFFER_IO_SIZE);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(baos, BUFFER_IO_SIZE);
        copy(bis, bos);
        bos.flush();
        return BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.size());
	}
	private void copy(final InputStream bis, final OutputStream baos) throws IOException {
        byte[] buf = new byte[256];
        int l;
        while ((l = bis.read(buf)) >= 0) baos.write(buf, 0, l);
    }
	
	/**
     * DBから購入済ProductIDを全て取得
     */
    private Set<String> getAllPurchasedItems(){
    	if(mPurchaseDao == null) mPurchaseDao = new PurchaseDao(mActivity.getApplicationContext());
    	return mPurchaseDao.queryAllPurchasedItems();
    }
}
