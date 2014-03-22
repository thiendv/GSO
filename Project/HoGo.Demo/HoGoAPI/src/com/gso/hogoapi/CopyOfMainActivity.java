package com.gso.hogoapi;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gso.hogoapi.fragement.UploadFileFragment;
import com.gso.hogoapi.model.LoginData;
import com.gso.hogoapi.service.DataParser;
import com.gso.serviceapilib.IServiceListener;
import com.gso.serviceapilib.Service;
import com.gso.serviceapilib.ServiceAction;
import com.gso.serviceapilib.ServiceResponse;

public class CopyOfMainActivity extends FragmentActivity implements IServiceListener {

	private Object mUserName;
	private Object mPassword;
	private Object mIsKeepMein;
	private EditText mEtUsername;
	private EditText mEtUserpassword;
	private ProgressBar mPrBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mEtUsername = (EditText) findViewById(R.id.et_user_name);
		mEtUserpassword = (EditText) findViewById(R.id.et_user_password);
		mPrBar = (ProgressBar) findViewById(R.id.pr_bar_login);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onLoginClicked(View v) {
		if (checkInputData()) {
			exeLogin();
		} else {

		}

	}

	private boolean checkInputData() {
		// TODO Auto-generated method stub
		boolean isEnough = true;
		Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
		String user = mEtUsername.getText().toString();
		String pass = mEtUserpassword.getText().toString();
		if (user.length() == 0) {
			mEtUsername.setFocusable(true);
			mEtUsername.startAnimation(shake);
			mEtUsername.requestFocus();
			isEnough = false;
		}
		if (pass.length() == 0) {
			mEtUserpassword.setFocusable(true);
			mEtUserpassword.startAnimation(shake);
			mEtUserpassword.requestFocus();
			isEnough = false;
		} else {
		}
		return isEnough;
	}

	private void exeLogin() {
		// TODO Auto-generated method stub
		mUserName = "lxanh@tma.com.vn";
		mPassword = "e10adc3949ba59abbe56e057f20f883e";
		Service service = new Service(CopyOfMainActivity.this);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("EmailAddress", mUserName);
		params.put("Password", mPassword);
		params.put("KeepMeLogin", mIsKeepMein);
		service.login(ServiceAction.ActionLogin, APIType.LOGIN, params);
		setProgressVisibility(true);
	}

	@Override
	public void onCompleted(com.gso.serviceapilib.Service service,
			ServiceResponse result) {
		// TODO Auto-generated method stub
		if (result.isSuccess()
				&& result.getAction() == ServiceAction.ActionLogin) {
			String resultString = (String) result.getData();
			Log.d("onCompleted", "onCompleted " + result.getAction() + " "
					+ resultString);
			if (resultString != null) {
				DataParser parser = new DataParser(true);
				LoginData parseData = (LoginData) parser
						.parseLogin(resultString);
				if (parseData.isStatus()) {
					Toast.makeText(this, "Login success", Toast.LENGTH_LONG)
							.show();
					gotoUpdateScreen();
				} else {
					Toast.makeText(this, "Login Fail", Toast.LENGTH_LONG)
							.show();
				}
			}

		}
		setProgressVisibility(false);
	}

	private void setProgressVisibility(boolean isShow) {
		mPrBar.setVisibility(isShow ? View.VISIBLE : View.GONE);
	}

	private void gotoUpdateScreen() {
		// TODO Auto-generated method stub
		UploadFileFragment fragement = new UploadFileFragment();
		getSupportFragmentManager().beginTransaction()
				.add(fragement, "upload_fragment").commit();
	}
}
