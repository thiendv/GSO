package com.gso.hogoapi;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.gso.hogoapi.fragement.EncodeFileFragment;
import com.gso.hogoapi.fragement.LoginFragment;
import com.gso.hogoapi.fragement.UploadFileFragment;
import com.gso.hogoapi.model.FileData;

public class MainActivity extends FragmentActivity {

	private ProgressBar mPrBar;
	private FrameLayout mContent;
	private FragmentManager mFramentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mFramentManager = getSupportFragmentManager();
		mPrBar = (ProgressBar) findViewById(R.id.pr_bar);
		mContent = (FrameLayout) findViewById(R.id.content);
		initContent();

	}

	private void initContent() {
		// TODO Auto-generated method stub
		if (HoGoApplication.instace().getToken(this) != null) {
			UploadFileFragment loginFragment = new UploadFileFragment();
			FragmentTransaction transaction = mFramentManager
					.beginTransaction();
			transaction.add(R.id.content, loginFragment).commit();

		} else {
			LoginFragment loginFragment = new LoginFragment();
			FragmentTransaction transaction = mFramentManager
					.beginTransaction();
			transaction.add(R.id.content, loginFragment).commit();

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void gotoUpdateScreen() {
		// TODO Auto-generated method stub
		UploadFileFragment fragement = new UploadFileFragment();
		FragmentTransaction transaction = mFramentManager.beginTransaction();
		transaction.addToBackStack(null);
		transaction.replace(R.id.content, fragement).commit();

	}

	public void setProgressVisibility(boolean isShow) {
		mPrBar.setVisibility(isShow ? View.VISIBLE : View.GONE);
	}

	public void showLogin() {
		// TODO Auto-generated method stub
		LoginFragment fragement = new LoginFragment();
		FragmentTransaction transaction = mFramentManager.beginTransaction();
		transaction.replace(R.id.content, fragement).commit();
	}

	public void gotoEncodeScreen(FileData parseData) {
		// TODO Auto-generated method stub
		EncodeFileFragment fragement = new EncodeFileFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable("file", parseData);
		fragement.setArguments(bundle);
		FragmentTransaction transaction = mFramentManager.beginTransaction();
		transaction.addToBackStack(null);
		transaction.replace(R.id.content, fragement).commit();
	}
}
