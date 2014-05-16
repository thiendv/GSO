package net.keyring.bookend.activity;

import net.keyring.bookend.Logput;
import net.keyring.bookend.R;
import net.keyring.bookend.asynctask.SendWebBookShelfTask;
import net.keyring.bookend.asynctask.SendWebBookShelfTask.RegistConfirmListener;
import net.keyring.bookend.asynctask.StoreTask;
import net.keyring.bookend.util.Utils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;

/**
 * メニューバーの処理を行うクラス
 * @author shindo
 *
 */
public class MenuBarSubActivity {
	
	//	アクティビティの識別番号
	public static final int	ACTIVITY_MAIN = 1;
	public static final int ACTIVITY_WEBSHELF = 2;
	
	private Activity	mActivity = null;
	private int			mActivityNumber = 0;
	
	private RegistConfirmListener mRegistConfirmListener = null;
	
	private ImageButton	mButtonBookshelf = null;
	private ImageButton	mButtonStore = null;
	private ImageButton	mButtonWebLibrary = null;
	private ImageButton	mButtonOption = null;
	private ImageButton	mButtonHelp = null;
	
	/**
	 * 初期化処理
	 * @param activity			表示されているアクティビティのインスタンス
	 * @param parentActivity 	アクティビティの識別番号
	 */
	public void init(Activity activity, int activityNumber) {
		Logput.v("init");
		mActivity = activity;
		mActivityNumber = activityNumber;
		mButtonBookshelf = (ImageButton)mActivity.findViewById(R.id.button_shelf);
		mButtonStore = (ImageButton)mActivity.findViewById(R.id.button_shop);
		mButtonWebLibrary = (ImageButton)mActivity.findViewById(R.id.button_sync);
		mButtonOption = (ImageButton)mActivity.findViewById(R.id.button_gear);
		mButtonHelp = (ImageButton)mActivity.findViewById(R.id.button_help);
	}
	
	/**
	 * Web書庫登録確認用のリスナーを登録
	 * @param listener
	 */
	public void setRegistConfirmListener(RegistConfirmListener listener) {
		mRegistConfirmListener = listener;
	}
	
	/**
	 * ボタンのイベントハンドラを登録する
	 */
	public void setupEventListener() {
		Logput.v("setupEventListener");
				
		//	書庫画面に移動
		mButtonBookshelf.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				Logput.v("Click Bookshelf Button");
				//	現在のアクティビティが書庫画面なら何もしない
				if(mActivityNumber == ACTIVITY_MAIN) {
					return;
				}
				//	書庫画面に移動
				Intent intent = new Intent(mActivity, MainActivity.class);
				mActivity.startActivity(intent);				
			}
		});
				
		//	ストア画面に移動
		mButtonStore.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Logput.v("Click Store Button");
				StoreTask store = new StoreTask(mActivity);
				store.execute("");		
			}
		});
				
		//	Web書庫画面に移動
		mButtonWebLibrary.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Logput.v("Click WebLibrary Button");
				//	現在のアクティビティがWeb書庫画面なら何もしない
				if(mActivityNumber == ACTIVITY_WEBSHELF) {
					return;
				}
				//	Web書庫画面に移動
				SendWebBookShelfTask RegistCheck = new SendWebBookShelfTask(mRegistConfirmListener, mActivity);
				RegistCheck.execute("");			
			}
		});
		
		//	オプション画面に移動
		mButtonOption.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Logput.v("Click Option Button");
				Intent intent = new Intent(mActivity, OptionActivity.class);
				mActivity.startActivity(intent);			
			}
		});
				
		//	オンラインヘルプを開く
		mButtonHelp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Logput.v("Click Help Button");
				String online_help = Utils.getHelpURL(mActivity);
				Logput.v("Online help : " + online_help);
				try{
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(online_help));
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mActivity.startActivity(intent);	
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * ボタンの有効/無効状態を更新
	 */
	public void updateButtonState(Context context) {
		//	現在表示されているアクティビティへの移動ボタンは選択状態にする
		switch(mActivityNumber) {
		case ACTIVITY_MAIN:
			mButtonBookshelf.setSelected(true);
			mButtonBookshelf.setEnabled(false);
			break;
		case ACTIVITY_WEBSHELF:
			mButtonWebLibrary.setSelected(true);
			mButtonWebLibrary.setEnabled(false);
			break;
		}
		
		// オフライン環境で利用できないボタンは無効にする
		if(Utils.isConnected(mActivity.getApplicationContext()) == false) {
			mButtonStore.setEnabled(false);
			mButtonWebLibrary.setEnabled(false);
			mButtonHelp.setEnabled(false);
		}
		
		//	ストアボタンを非表示にする
		if(context.getString(R.string.store_flag).equals("1")){
			View shopButtonGroup = mActivity.findViewById(R.id.group_button_shop);
			if(shopButtonGroup != null) {
				shopButtonGroup.setVisibility(View.GONE);
			}
		}
		//	Web書庫ボタンを非表示にする
		//	* Androidアプリでは本棚ボタンの意味がなくなるので、それも非表示にする
		if(context.getString(R.string.webshelf_btn_flag).equals("1")){
			View syncButtonGroup = mActivity.findViewById(R.id.group_button_sync);
			if(syncButtonGroup != null) {
				syncButtonGroup.setVisibility(View.GONE);
			}
			View shelfButtonGroup = mActivity.findViewById(R.id.group_button_shelf);
			if(shelfButtonGroup != null) {
				shelfButtonGroup.setVisibility(View.GONE);
			}
		}
	}
}
