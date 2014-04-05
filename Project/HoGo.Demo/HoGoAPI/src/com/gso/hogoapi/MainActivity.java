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
import com.gso.hogoapi.views.RadioGroupController;
import com.gso.hogoapi.views.TabButton;

public class MainActivity extends FragmentActivity implements RadioGroupController.OnCheckedChangeListener{

	private ProgressBar mPrBar;
	private FrameLayout mContent;
    private View mTopBar;
    private View mBottomBar;
	private FragmentManager mFramentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mFramentManager = getSupportFragmentManager();
		mPrBar = (ProgressBar) findViewById(R.id.pr_bar);
        mTopBar = findViewById(R.id.top_bar);
        mBottomBar = findViewById(R.id.bottom_bar);
        mContent = (FrameLayout) findViewById(R.id.content);
        initBottomBar();
		initContent();
	}


    @Override
    public void onCheckedChanged(int checkedId) {
        switch (checkedId) {
            case R.id.tab_send_document:
                gotoUpdateScreen();
                break;
            case R.id.tab_my_document:
                gotoEncodeScreen(new FileData());
                break;
            case R.id.tab_send_history:
                gotoHistoryScreen();
                break;
        }
    }

    private void initBottomBar() {
        final TabButton left = (TabButton) findViewById(R.id.tab_send_document);
        left.setType(TabButton.Type.left);
        final TabButton middle = (TabButton) findViewById(R.id.tab_my_document);
        middle.setType(TabButton.Type.center);
        final TabButton right = (TabButton) findViewById(R.id.tab_send_history);
        right.setType(TabButton.Type.right);

        RadioGroupController radioGroupController = new RadioGroupController();
        radioGroupController.setOnCheckedChangeListener(this);
        radioGroupController.setRadioButtons(left, middle, right);
        radioGroupController.setSelection(0);
    }

	private void initContent() {
		// TODO Auto-generated method stub
		if (HoGoApplication.instace().getToken(this) != null) {
            exitLogin();
			UploadFileFragment loginFragment = new UploadFileFragment();
			FragmentTransaction transaction = mFramentManager
					.beginTransaction();
			transaction.add(R.id.content, loginFragment).commit();

		} else {
            mTopBar.setVisibility(View.GONE);
            mBottomBar.setVisibility(View.GONE);
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
		UploadFileFragment fragement = new UploadFileFragment();
		FragmentTransaction transaction = mFramentManager.beginTransaction();
		transaction.replace(R.id.content, fragement).commit();

	}

	public void setProgressVisibility(boolean isShow) {
		mPrBar.setVisibility(isShow ? View.VISIBLE : View.GONE);
	}

	public void showLogin() {
		mTopBar.setVisibility(View.GONE);
		mBottomBar.setVisibility(View.GONE);
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
		transaction.replace(R.id.content, fragement).commit();
	}


    public void exitLogin() {
        mTopBar.setVisibility(View.VISIBLE);
        mBottomBar.setVisibility(View.VISIBLE);
    }

    private void gotoHistoryScreen() {

    }

}
