package com.gso.hogoapi;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gso.hogoapi.fragement.AddFileSuccessfulFragment;
import com.gso.hogoapi.fragement.BookShelfFragment;
import com.gso.hogoapi.fragement.EncodeFileFragment;
import com.gso.hogoapi.fragement.LoginFragment;
import com.gso.hogoapi.fragement.SendFileFragment;
import com.gso.hogoapi.fragement.SendHistoryFragment;
import com.gso.hogoapi.fragement.UploadFileFragment;
import com.gso.hogoapi.model.FileData;
import com.gso.hogoapi.model.SendData;
import com.gso.hogoapi.views.RadioGroupController;
import com.gso.hogoapi.views.TabButton;

public class MainActivity extends FragmentActivity implements RadioGroupController.OnCheckedChangeListener, OnClickListener{

	private ProgressBar mPrBar;
	private FrameLayout mContent;
    private View mTopBar;
    private View mBottomBar;
	private FragmentManager mFramentManager;
	private View mBtnActionAdd;
	private View mBtnActionSend; 

	
	public static List<FileData> fileDataList = new ArrayList<FileData>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mBtnActionAdd = findViewById(R.id.action_add);
		mBtnActionSend = findViewById(R.id.action_send);
		mBtnActionAdd.setOnClickListener(this);
		mBtnActionSend.setOnClickListener(this);
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
                gotoBookShelfScreen();
                break;
            case R.id.tab_send_history:
                gotoHistoryScreen();
                break;
        }
    }

    private void gotoBookShelfScreen() {
    	BookShelfFragment fragement = new BookShelfFragment();
		FragmentTransaction transaction = mFramentManager.beginTransaction();
		transaction.replace(R.id.content, fragement).commit();
	}


	private void initBottomBar() {
//        final TabButton left = (TabButton) findViewById(R.id.tab_send_document);
//        left.setType(TabButton.Type.left);
        final TabButton middle = (TabButton) findViewById(R.id.tab_my_document);
        middle.setType(TabButton.Type.left);
        final TabButton right = (TabButton) findViewById(R.id.tab_send_history);
        right.setType(TabButton.Type.right);

        RadioGroupController radioGroupController = new RadioGroupController();
        radioGroupController.setOnCheckedChangeListener(this);
        radioGroupController.setRadioButtons(middle, right);
        radioGroupController.setSelection(0);
    }

	private void initContent() {
		// TODO Auto-generated method stub
		if (HoGoApplication.instace().getToken(this) != null) {
            exitLogin();
//			UploadFileFragment loginFragment = new UploadFileFragment();
//			FragmentTransaction transaction = mFramentManager
//					.beginTransaction();
//			transaction.add(R.id.content, loginFragment).commit();
            gotoMainScreen();

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
		transaction.addToBackStack(null);
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
		transaction.addToBackStack(null);
		transaction.replace(R.id.content, fragement).commit();
		findViewById(R.id.top_bar).setVisibility(View.VISIBLE);
	}


    public void exitLogin() {
        mTopBar.setVisibility(View.VISIBLE);
//        mBottomBar.setVisibility(View.VISIBLE);
    }
    
    public void enterBookShelfScreen() {
    	mBtnActionAdd.setVisibility(View.VISIBLE);
    }
    public void exitBookShelfScreen() {
    	mBtnActionAdd.setVisibility(View.GONE);
    }

    private void gotoHistoryScreen() {
    	SendHistoryFragment fragement = new SendHistoryFragment();
    	FragmentTransaction transaction = mFramentManager.beginTransaction();
    	transaction.replace(R.id.content, fragement).commit();
    }

	public void gotoAddScreen(FileData fileData) {
		AddFileSuccessfulFragment fragement = new AddFileSuccessfulFragment();
		FragmentTransaction transaction = mFramentManager.beginTransaction();
		Bundle bundle = new Bundle();
		SendData sendData = new SendData();
		List<FileData> dataList = new ArrayList<FileData>();
		dataList.add(fileData);
		sendData.setDataList(dataList);
		bundle.putSerializable("send_data", sendData);
		fragement.setArguments(bundle);
		transaction.replace(R.id.content, fragement);
		transaction.addToBackStack(null);
		transaction.commit();
		findViewById(R.id.top_bar).setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.action_add:
//			gotoAddScreen();
			gotoUpdateScreen();
			break;
		case R.id.action_send:
//			gotoAddScreen();
			gotoSendScreen();
			break;
		default:
			break;
		}
	}


	private void gotoSendScreen() {
		// TODO Auto-generated method stub
		List<FileData> items = new ArrayList<FileData>();
		SendData sendData = new SendData();
		for(FileData item: BookShelfFragment.BookShelfAdapter.mItems){
			if(item.getIsChecked()){
				items.add(item);
			}
		}
		if(items.size() > 0){
			sendData.setDataList(items);
			gotoSendDocumentScreen(sendData);
			
		}else{
			Toast.makeText(this, "Please choose any item before sending", Toast.LENGTH_LONG).show();
		}
	}


	public void gotoSendDocumentScreen(SendData sendData) {
		// TODO Auto-generated method stub
		SendFileFragment fragement = new SendFileFragment();
		FragmentTransaction transaction = mFramentManager.beginTransaction();
		Bundle bundle = new Bundle();
		bundle.putSerializable("send_data", sendData);
		fragement.setArguments(bundle);
		transaction.addToBackStack(null);
		transaction.replace(R.id.content, fragement).commit();
		findViewById(R.id.top_bar).setVisibility(View.GONE);
	}


	public void gotoMainScreen() {
		// TODO Auto-generated method stub
		BookShelfFragment fragement = new BookShelfFragment();
		FragmentTransaction transaction = mFramentManager.beginTransaction();
		transaction.replace(R.id.content, fragement).commit();
		findViewById(R.id.top_bar).setVisibility(View.VISIBLE);
	}


	public void changeAddtoSendData() {
		// TODO Auto-generated method stub
		findViewById(R.id.action_send).setVisibility(View.VISIBLE);
		findViewById(R.id.action_add).setVisibility(View.GONE);
	}


	public void gotologinScreen() {
		// TODO Auto-generated method stub
		LoginFragment fragement = new LoginFragment();
		FragmentTransaction transaction = mFramentManager.beginTransaction();
		transaction.replace(R.id.content, fragement).commit();
	}


	public void changeToAdd() {
		// TODO Auto-generated method stub
		findViewById(R.id.action_send).setVisibility(View.GONE);
		findViewById(R.id.action_add).setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == event.KEYCODE_BACK){
			findViewById(R.id.top_bar).setVisibility(View.VISIBLE);
			onBackPressed();
		}
		return super.onKeyDown(keyCode, event);
	}

}
