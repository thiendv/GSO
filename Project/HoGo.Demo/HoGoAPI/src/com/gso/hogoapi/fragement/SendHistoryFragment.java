package com.gso.hogoapi.fragement;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gso.hogoapi.APIType;
import com.gso.hogoapi.HoGoApplication;
import com.gso.hogoapi.MainActivity;
import com.gso.hogoapi.R;
import com.gso.hogoapi.model.LoginData;
import com.gso.hogoapi.service.DataParser;
import com.gso.serviceapilib.IServiceListener;
import com.gso.serviceapilib.Service;
import com.gso.serviceapilib.ServiceAction;
import com.gso.serviceapilib.ServiceResponse;

public class SendHistoryFragment extends Fragment implements IServiceListener {

	/**
	 * @param args
	 */

	private Object mUserName;
	private Object mPassword;
	private Object mIsKeepMein;
	private EditText mEtUsername;
	private EditText mEtUserpassword;
	private ProgressBar mPrBar;
	private Button mBtnLogin;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.send_history_screen, container, false);


		return v;
	}

	public void onLoginClicked() {
		if (checkInputData()) {
			exeLogin();
		} else {

		}

	}

	private boolean checkInputData() {
		// TODO Auto-generated method stub
		boolean isEnough = true;
		Animation shake = AnimationUtils.loadAnimation(getActivity(),
				R.anim.shake);
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
		Service service = new Service(this);
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
					Toast.makeText(getActivity(), "Login success",
							Toast.LENGTH_LONG).show();
					HoGoApplication.instace().setToken(getActivity() ,parseData.getToken());
                    MainActivity activity = (MainActivity) getActivity();
                    activity.gotoMainScreen();
                    activity.exitLogin();
				} else {
					Toast.makeText(getActivity(), "Login Fail",
							Toast.LENGTH_LONG).show();
				}
			}

		}
		setProgressVisibility(false);
	}

	private void setProgressVisibility(boolean isShow) {
		mPrBar.setVisibility(isShow ? View.VISIBLE : View.GONE);
	}

}
