package net.keyring.bookend.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.keyring.bookend.R;
import net.keyring.bookend.action.WebBookShelfAction;
import net.keyring.bookend.asynctask.ImageGetTask;
import net.keyring.bookend.bean.BookBeans;
import net.keyring.bookend.constant.Const;
import net.keyring.bookend.constant.ConstList;
import net.keyring.bookend.db.ContentsDao;
import net.keyring.bookend.util.DateUtil;
import net.keyring.bookend.util.StringUtil;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * コンテンツ表示リストアダプター - Web書庫画面
 * 
 * @author Hamaji
 * 
 */
public class WebBookShelfAdapter extends SimpleAdapter implements Const{
	/** Inflater */
	private LayoutInflater mInflater;
	/** Web書庫情報リスト(サムネイル・タイトル・著者・rowID) */
	private ArrayList<Map<String, Object>> mItems;
	/** Context */
	private Context mCon;
	/** エラーイメージ */
	private Drawable mErrorImage;
	/** サムネイル仮画像 */
	private Drawable mTentativeThumb;
	/** WebBookShelfAction */
	private WebBookShelfAction mAction;
	/** ContentsDao */
	private ContentsDao mDao;
	
	/**
	 * TextViewを保存するホルダー
	 * 
	 * @author Hamaji
	 * 
	 */
	private static class ViewHolder {
		TextView	title;
		TextView	lastAccessDate;
		TextView	sharedDevice;
		ImageView	thumbnail;
		ProgressBar	waitBar;
		Button		downloadBtn;
		TextView	status;
		Button		cancelBtn;
		Button		retryBtn;
		TextView	errorMessage;
		ProgressBar	progressBar;
		LinearLayout	dl_init_set;
		LinearLayout	dl_ing_set;
		LinearLayout	dl_error_set;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param context
	 * @param data
	 *            表示データ
	 * @param resource
	 *            リストレイアウト
	 * @param from
	 *            表示キー
	 * @param to
	 *            レイアウトID
	 */
	public WebBookShelfAdapter(Context con, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
		super(con, data, resource, from, to);
		// Viewを作成するLayoutInflaterを取得
		this.mInflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mCon = con;
		mErrorImage = mCon.getResources().getDrawable(android.R.drawable.ic_dialog_alert);
		mTentativeThumb = mCon.getResources().getDrawable(android.R.drawable.ic_menu_report_image);
	}

	/** データ内容を保持 */
	public void setListData(ArrayList<Map<String, Object>> data) {
		mItems = data;
	}

	/**
	 * 必ず呼ばれる
	 * 
	 * @param position
	 * @param convertView
	 *            レイアウト
	 * @param parent
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder;
		
		// Web書庫情報を取得
		Map<String, Object> settingData = mItems.get(position);
		if (settingData == null) return v;

		if (v == null) {
			// Web書庫レイアウト
			v = mInflater.inflate(R.layout.web_book, null);
			holder = new ViewHolder();
			holder.thumbnail = (ImageView) v.findViewById(R.id.thumbnail_web);
			holder.waitBar = (ProgressBar) v.findViewById(R.id.WaitBar_web);
			holder.progressBar = (ProgressBar) v.findViewById(R.id.dl_progress);
			holder.title = (TextView) v.findViewById(R.id.title_web);
			holder.lastAccessDate = (TextView) v.findViewById(R.id.last_access_date);
			holder.sharedDevice = (TextView)v.findViewById(R.id.shared_device);
			holder.downloadBtn = (Button) v.findViewById(R.id.web_dl_btn);
			holder.status = (TextView) v.findViewById(R.id.dl_status);
			holder.cancelBtn = (Button) v.findViewById(R.id.dl_cancel_btn);
			holder.retryBtn = (Button) v.findViewById(R.id.dl_retry_btn);
			holder.errorMessage = (TextView) v.findViewById(R.id.dl_error_mes);
			holder.dl_init_set = (LinearLayout) v.findViewById(R.id.dl_init_set);
			holder.dl_ing_set = (LinearLayout) v.findViewById(R.id.dl_ing_set);
			holder.dl_error_set = (LinearLayout) v.findViewById(R.id.dl_error_set);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}

		long rowID = (Long)settingData.get(ConstList.ID);
		if(mDao == null) mDao = new ContentsDao(mCon);
		BookBeans book = mDao.load(rowID);
		holder.title.setText(viewStrSet(book.getTitle()));
		holder.lastAccessDate.setText(viewStrSet(DateUtil.change_viewDate(mCon.getString(R.string.indefinitely), null, book.getExpiry_date())));
		if(book.getSharedDevice_D() != -1){
			holder.sharedDevice.setText(book.getSharedDevice_M() + "/" + book.getSharedDevice_D());
			holder.sharedDevice.setTag(mCon.getString(R.string.shareddevice_tag) + rowID);
		}else{
			holder.sharedDevice.setText(mCon.getString(R.string.unlimited));
		}
		
		// タグをセット
		holder.dl_init_set.setTag(mCon.getString(R.string.dl_init_set_tag) + rowID);
		holder.dl_ing_set.setTag(mCon.getString(R.string.dl_ing_set_tag) + rowID);
		holder.dl_error_set.setTag(mCon.getString(R.string.dl_error_set_tag) + rowID);
		holder.downloadBtn.setTag(rowID);
		holder.status.setTag(mCon.getString(R.string.status_web_tag) + rowID);
		holder.progressBar.setTag(mCon.getString(R.string.progress_tag) + rowID);
		holder.cancelBtn.setTag(rowID);
		holder.retryBtn.setTag(rowID);
		holder.errorMessage.setTag(mCon.getString(R.string.dl_error_tag) + rowID);
		
		String status = "";
    	switch(book.getDlStatus()){
    	case STATUS_WAIT_FOR_DL_WEB:	// 「ダウンロード待ち」
    		holder.dl_init_set.setVisibility(View.GONE);
    		holder.dl_ing_set.setVisibility(View.VISIBLE);
    		holder.dl_error_set.setVisibility(View.GONE);
    		break;
    	case STATUS_DOWNLOADING_WEB:	// 「ダウンロード中」
    		holder.dl_init_set.setVisibility(View.GONE);
    		holder.dl_ing_set.setVisibility(View.VISIBLE);
    		holder.dl_error_set.setVisibility(View.GONE);
    		break;
    	case STATUS_DL_COMPLETE_WEB:	// 「ダウンロード完了」
    		holder.dl_init_set.setVisibility(View.VISIBLE);
    		holder.dl_ing_set.setVisibility(View.GONE);
    		holder.dl_error_set.setVisibility(View.GONE);
    		// ダウンロードボタンを非表示にして、テキスト表示
    		holder.downloadBtn.setVisibility(View.GONE);
    		holder.status.setVisibility(View.VISIBLE);
    		holder.status.setText(mCon.getString(R.string.download_finish));
    		break;
    	case STATUS_DL_ERROR_WEB:	// 「ダウンロードエラー」
    		holder.dl_init_set.setVisibility(View.GONE);
    		holder.dl_ing_set.setVisibility(View.GONE);
    		holder.dl_error_set.setVisibility(View.VISIBLE);
			holder.errorMessage.setText(mCon.getString(R.string.download_fail));
    		break;
    	default:
    		holder.dl_init_set.setVisibility(View.VISIBLE);
    		holder.dl_ing_set.setVisibility(View.GONE);
    		holder.dl_error_set.setVisibility(View.GONE);
    		holder.downloadBtn.setText(mCon.getString(R.string.download));
    		status = getStatus(book);
    		if(status.equals("")){
    			holder.status.setVisibility(View.GONE);
    			holder.downloadBtn.setVisibility(View.VISIBLE);
    		}else{
    			holder.downloadBtn.setVisibility(View.GONE);
        		holder.status.setVisibility(View.VISIBLE);
        		holder.status.setText(status);
    		}
    		break;
    	}

		// 画像を隠し、プログレスバーを表示
		holder.thumbnail.setVisibility(View.GONE);
		holder.waitBar.setVisibility(View.VISIBLE);
		holder.thumbnail.setImageDrawable(mTentativeThumb);
		// 画像読込
		try {
			String thumbURL = settingData.get(ConstList.THUMB_URL).toString();
			holder.thumbnail.setTag(thumbURL);
			// AsyncTaskは１回しか実行できない為、毎回インスタンスを生成
			ImageGetTask task = new ImageGetTask(holder.thumbnail, holder.waitBar, mErrorImage);
			task.execute(thumbURL);
		} catch (Exception e) {
			// サムネイル取得失敗 - プログレスバー非表示 ＆ <!>画像表示
			holder.waitBar.setVisibility(View.GONE);
			holder.thumbnail.setVisibility(View.VISIBLE);
			holder.thumbnail.setImageDrawable(mErrorImage);
		}
		return v;
	}

	/**
	 * 表示する項目がNULLの場合は[ ― ]を表示
	 * 
	 * @param チェック文字列
	 * @return 表示文字列
	 */
	private String viewStrSet(String value) {
		String result = " - ";
		if (!StringUtil.isEmpty(value)) {
			result = value;
		}
		return result;
	}
	
	/**
	 * ＤＬステータスをDBから取得
	 * @param rowID
	 * @return
	 */
	private String getStatus(BookBeans book){
		if(mAction == null) mAction = new WebBookShelfAction();
		return mAction.getStatus(mCon, book);
	}
}
