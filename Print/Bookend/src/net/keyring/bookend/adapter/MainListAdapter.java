package net.keyring.bookend.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.keyring.bookend.Logput;
import net.keyring.bookend.R;
import net.keyring.bookend.action.MainListAction;
import net.keyring.bookend.asynctask.ImageGetTask;
import net.keyring.bookend.bean.BookBeans;
import net.keyring.bookend.constant.ConstList;
import net.keyring.bookend.db.ContentsDao;
import net.keyring.bookend.util.StringUtil;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * コンテンツ表示リストアダプター - メイン画面
 * 
 * @author Hamaji
 * 
 */
public class MainListAdapter extends SimpleAdapter {
	/** Inflater */
	private LayoutInflater mInflater;
	/** 書庫情報リスト(サムネイル・タイトル・著者・rowID) */
	private ArrayList<Map<String, Object>> mItems;
	/** Context */
	private Context mCon;
	/** ContentsDaoクラス */
	private ContentsDao mDao;
	/** MainListActionクラス */
	private MainListAction mAction;

	/**
	 * TextViewを保存するホルダー
	 * 
	 * @author Hamaji
	 * 
	 */
	private static class ViewHolder {
		TextView title;
		TextView autor;
		TextView status;
		ImageView thumbnail;
		ProgressBar waitBar;
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
	public MainListAdapter(Context con, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {
		super(con, data, resource, from, to);
		// Viewを作成するLayoutInflaterを取得
		this.mInflater = (LayoutInflater) con
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mCon = con;
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
		
		// 書庫情報を取得
		Map<String, Object> settingData = mItems.get(position);
		if (settingData == null) return v;

		if (v == null) {
			// Web書庫レイアウト
			v = mInflater.inflate(R.layout.main_list, null);
			holder = new ViewHolder();
			holder.thumbnail = (ImageView) v.findViewById(R.id.thumbnail);
			holder.waitBar = (ProgressBar) v.findViewById(R.id.WaitBar);
			holder.title = (TextView) v.findViewById(R.id.title);
			holder.autor = (TextView) v.findViewById(R.id.author);
			holder.status = (TextView)v.findViewById(R.id.status);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}

		long rowID = (Long)settingData.get(ConstList.ID);
		if(mDao == null) mDao = new ContentsDao(mCon);
		BookBeans book = null;
		String status = null;
		try{
			book = mDao.load(rowID);
			holder.title.setText(viewStrSet(book.getTitle()));
			holder.autor.setText(viewStrSet(book.getAuthor()));
			
			status = getStatus(book);
		}catch(NullPointerException e){
			Logput.w("Null : RowID = " + rowID);
		}
		holder.status.setText(status);
		holder.status.setTag(mCon.getString(R.string.status_main_tag) + rowID);

		// 画像を隠し、プログレスバーを表示
		holder.waitBar.setVisibility(View.VISIBLE);
		holder.thumbnail.setVisibility(View.GONE);
		// 仮の画像設定
		holder.thumbnail.setImageDrawable(mCon.getResources().getDrawable(android.R.drawable.ic_menu_report_image));
		// 画像読込
		try {
			// サムネイルのダウンロードURL
			String thumbURL = settingData.get(ConstList.THUMB_URL).toString();
			holder.thumbnail.setTag(thumbURL);
			// AsyncTaskは１回しか実行できない為、毎回インスタンスを生成
			ImageGetTask task = new ImageGetTask(holder.thumbnail, holder.waitBar, mCon.getResources().getDrawable(
							android.R.drawable.ic_dialog_alert));
			// 画像URLをセット
			task.execute(thumbURL);
		} catch (Exception e) {
			// 取得失敗
			// <!>アイコン
			holder.thumbnail.setImageDrawable(mCon.getResources().getDrawable(
					android.R.drawable.ic_dialog_alert));
			// プログレスバー非表示＆画像表示
			holder.waitBar.setVisibility(View.GONE);
			holder.thumbnail.setVisibility(View.VISIBLE);
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
		if(mAction == null) mAction = new MainListAction();
		return mAction.getStatus(mCon, book);
	}
}
