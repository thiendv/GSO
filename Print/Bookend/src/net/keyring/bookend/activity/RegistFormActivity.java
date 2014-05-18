package net.keyring.bookend.activity;

import net.keyring.bookend.Preferences;
import net.keyring.bookend.R;
import net.keyring.bookend.asynctask.SendWebBookShelfTask;
import net.keyring.bookend.asynctask.SendWebBookShelfTask.RegistConfirmListener;
import net.keyring.bookend.constant.ConstRegist;
import net.keyring.bookend.request.BookendMail;
import net.keyring.bookend.request.BookendPin2;
import net.keyring.bookend.request.RegistMailAddress2;
import net.keyring.bookend.util.StringUtil;
import net.keyring.bookend.util.Utils;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * メールアドレス登録画面
 * @author Hamaji
 *
 */
public class RegistFormActivity extends BookendActivity implements RegistConfirmListener, ConstRegist{
	
	private TextView	mGuideMess;			// 進行メッセージ
	private EditText	mAddressInputForm;	// アドレス入力欄
	private Button		mAddressBtn;		// アドレス送信ボタン
	private EditText	mPinInputForm;		// Pin入力欄
    private Dialog		mDialog;			// ダイヤログ
    private boolean	mIsPinSend = false;	// Pin送信アクションフラグ

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// メールアドレス登録画面レイアウト
		setContentView(R.layout.mail_address_regist);
		setLayout();
	}
	
	@Override
	public void onRestart(){
		super.onRestart();
		setContentView(R.layout.mail_address_regist);
		setLayout();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if (Utils.isDebugMode(this.getApplicationContext())) {
			// デバックモード有効：NG
			dialog(DIALOG_ID_ERROR, getString(R.string.debug_mode_message));
		}else{
			// メールアドレスが一時保存されているかどうかチェックしテキストをセット
			tempAddressCheck();
		}
	}
	
	/**
	 * 登録画面のレイアウトセット
	 */
	public void setLayout(){
		mLayout = (LinearLayout) findViewById(R.id.mail_address_regist);
		mGuideMess = (TextView) mLayout.findViewById(R.id.regist_message);
		mAddressInputForm = (EditText) mLayout.findViewById(R.id.mail_address);
		mAddressBtn = (Button) mLayout.findViewById(R.id.mail_address_send);
		mPinInputForm = (EditText) mLayout.findViewById(R.id.pin);
		
		// メールアドレス入力欄にフォーカスがあたった時、メールアドレス送信ボタンを活性化させる
		mAddressInputForm.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// メールアドレス送信ボタンを活性化
				mAddressBtn.setEnabled(true);
				return false;
			}
		});
	}
	
	/**
	 * Pinボタン押下時アクション
	 * @param view
	 */
	public void pinBtnAction(View view){
		if(mIsPinSend){
			return;
		}
		if (mPref == null) mPref = new Preferences(getApplicationContext());
		String tempAddress = mPref.getMailAddress_temporary();
		if (StringUtil.isEmpty(tempAddress)) {
			// 一時保存メールアドレスがない場合はダイアログ表示
			errorDialog(false, getString(R.string.mail_address_none));
		} else {
			mIsPinSend = true;
			// Pinセット時処理
			pin(tempAddress);
		}
	}
	
	/**
	 * メールアドレス送信ボタン押下時アクション
	 * @param view
	 */
	public void mailSendBtnAction(View view){
		// メールアドレス送信ボタンを非活性化
		view.setEnabled(false);
		
		dialog(DIALOG_ID_PROGRESS, getString(R.string.sendding));
		mailAddress();
	}
	
	/**
	 * メールアドレスが一時保存されているかどうかチェックしテキストをセット
	 */
	private void tempAddressCheck(){
		String tempAddress = mPref.getMailAddress_temporary();
		if (StringUtil.isEmpty(tempAddress)) {
			// メールアドレスが登録されていない場合
			mGuideMess.setText(R.string.bookshelf_regist_message);
		}else{
			// 進行案内メッセージ：メールアドレスが登録済みの場合(PIN入力メッセージ)
			mGuideMess.setText(getString(R.string.shelf_regist_after_mes, tempAddress));
			mAddressInputForm.setText(tempAddress);
			// Pin入力欄にフォーカスできるように
			mPinInputForm.setFocusable(true);
			mPinInputForm.setFocusableInTouchMode(true);
			mPinInputForm.requestFocus();
		}
	}
	
	/**
	 * メールアドレス入力後処理
	 * 
	 * @param address TextView
	 */
	private void mailAddress() {
		// メールアドレス入力ホームテキスト取得
		SpannableStringBuilder sp = (SpannableStringBuilder) mAddressInputForm.getText();
		String mailAddress = sp.toString();
		
		if (Utils.checkMailAddress(mailAddress)) {
			// 入力されたメールアドレスをsettings.xmlに一時保存
			if (mPref == null) mPref = new Preferences(getApplicationContext());
			mPref.setMailAddress_temporary(mailAddress);
			// RegistMailAddress2リクエスト
			registMailAddress2(mailAddress);
		} else {
			// 入力されたメールアドレスに不正があった場合はエラーアラート表示して初期画面に戻る
			dialog(DIALOG_ID_VIEW, getString(R.string.mailaddress_false));
		}
	}
	
	/**
	 * Pin入力後処理
	 * 
	 * @param input_pin
	 */
	private void pin(String mailAddress) {
		dialog(DIALOG_ID_PROGRESS, getString(R.string.getting));
		SpannableStringBuilder sp = (SpannableStringBuilder) mPinInputForm.getText();
		String pin = sp.toString();
		if (!StringUtil.isInteger(pin)) {
			// 入力された文字列が数値ではない場合ははじく
			errorDialog(false, getString(R.string.pin_false));
		} else if (pin.length() > 5) {
			// 10byte(5文字)以下PIN文字列ははじく
			errorDialog(false, getString(R.string.pin_false));
		} else if (!StringUtil.check_pin(pin)) {
			// 入力されたPINを計算チェック
			errorDialog(false, getString(R.string.pin_false));
		} else {
			bookendPin2(pin);
		}
	}
	
	/**
	 * BookendPIN2リクエスト処理
	 */
	private void bookendPin2(String pin){
		BookendPin2 bookendPin2 = new BookendPin2();
		if (bookendPin2.execute(getApplicationContext(), pin)) {
			progress_stop();
			// OK - Web書庫遷移処理チェック
			SendWebBookShelfTask RegistCheck = new SendWebBookShelfTask(this, RegistFormActivity.this);
			RegistCheck.execute("");
		} else {
			errorDialog(false, bookendPin2.getDescription());
			//errorDialog(false, "test");
		}
	}
	
	/**
	 * RegistMailAddress2リクエスト
	 */
	private void registMailAddress2(String mailAddress){
		// 入力されたメールアドレスをチェックする - RegistMailAddress2リクエスト
		RegistMailAddress2 registMailAddress2 = new RegistMailAddress2(getApplicationContext());
		String status = registMailAddress2.execute(mailAddress, false);
		
		// メールアドレス登録が正常に終わったかチェック
		if (Utils.equal_str("71000", status)) {
			// status = 71000 - OK:Pin入力画面表示
			progress_stop();
			tempAddressCheck();
		} else if (Utils.equal_str("71001", status)) {
			// status = 71001 - NG:BookendMailリクエスト
			BookendMail bookendMail = new BookendMail();
			if (bookendMail.execute(getApplicationContext(), mailAddress, true)) {
				// OK - Pin入力画面表示
				progress_stop();
				tempAddressCheck();
			} else {
				// NG - エラーダイヤログ表示
				dialog(DIALOG_ID_VIEW, bookendMail.getDescription());
			}
		} else {
			// static変数に一時保存してあったアドレス・チェックコードをnullに
			mPref.setMailAddress_temporary(null);
			mPref.setCheckCode(null);
			// エラーダイヤログ表示
			dialog(DIALOG_ID_VIEW, registMailAddress2.getDescription());
		}
	}
	
	/**
	 * RegistConfirmTask - Web書庫遷移処理戻り値処理
	 * @param 処理ステータスコード
	 */
	@Override
	public void result_registConfirm(int status) {
		switch(status){
		case SEND_REGIST_MAILADDRESS:	// メールアドレス登録画面へ遷移
			mIsPinSend = false;
			Intent registForm = new Intent(this, RegistFormActivity.class);
			startActivity(registForm);
			break;
		case SEND_WEB_BOOK_SHELF:		// Web書庫画面に遷移
			mIsPinSend = false;
			Intent webBookShelf = new Intent(this, WebBookShelfActivity.class);
			webBookShelf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(webBookShelf);
			// 戻るボタンで戻ってこれなくするためにActivityを終了する
			finish();
			break;
		case RESET_FLAG:				// 他のクライアントでリセットされたためアプリ終了ダイアログ表示
			errorDialog(true, getString(R.string.reset_finish, getString(R.string.app_name)));
			break;
		case DB_ERROR:					//　DB登録エラー発生：メイン画面のまま
			errorDialog(false, getString(R.string.db_error));
			break;
		case GET_CONTENTS_ERROR:		// GetContentsリクエストエラー:メイン画面のまま
			errorDialog(false, getString(R.string.getcontents_error));
			break;
		case GET_AWS_INFO_NG:			// GetAwsInfoリクエストエラー :statusコード不明
			errorDialog(false, getString(R.string.status_false, "GetAwsInfo", 999999));
			break;
		case GET_AWS_INFO_50010:		// GetAwsInfo - パラメーターエラー
			errorDialog(false, getString(R.string.status_parameter_error, 50010));
			break;
		case GET_AWS_INFO_50011:		// GetAwsInfo - サービス停止中
			errorDialog(false, getString(R.string.getawsinfo_status_50011));
			break;
		case GET_AWS_INFO_50012:		// GetAwsInfo - メンテナンス中
			errorDialog(false, getString(R.string.getawsinfo_status_50012));
			break;
		case GET_AWS_INFO_50099:		// GetAwsInfo - サーバ内部エラー
			errorDialog(false, getString(R.string.status_server_internal_error, 50099));
			break;
		}
	}
	
	/**
	 * エラーダイアログ表示 ※フラグを
	 * @param message
	 */
	private void errorDialog(final boolean isFinish, String message){
		progress_stop();
		mIsPinSend = false;
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setIcon(R.drawable.icon);
		alert.setTitle("ERROR");
		alert.setMessage(message);
		alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	if(isFinish){
            		dialogClose();
            		finish();
            	}else{
            		dialogClose();
            	}
            }
        });
		// ダイアログが二重に開いてしまわないための処理
		if(mDialog == null || !mDialog.isShowing()){
			mDialog = alert.create();
			mDialog.show();
		}
	}
	
	/**
	 * ダイヤログを閉じる
	 */
	private void dialogClose(){
		if(mDialog != null){
			mDialog.cancel();
			mDialog.dismiss();
			mDialog = null;
		}
	}
}
