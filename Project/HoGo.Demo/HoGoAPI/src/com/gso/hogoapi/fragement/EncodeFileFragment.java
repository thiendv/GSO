package com.gso.hogoapi.fragement;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gso.hogoapi.APIType;
import com.gso.hogoapi.HoGoApplication;
import com.gso.hogoapi.MainActivity;
import com.gso.hogoapi.R;
import com.gso.hogoapi.model.FileData;
import com.gso.hogoapi.model.ResponseData;
import com.gso.hogoapi.service.DataParser;
import com.gso.serviceapilib.IServiceListener;
import com.gso.serviceapilib.Service;
import com.gso.serviceapilib.ServiceAction;
import com.gso.serviceapilib.ServiceResponse;

public class EncodeFileFragment extends DialogFragment implements
		IServiceListener {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	private FileData mFile;
	private TextView tvFileName;
	private EditText etFileDes;
	private Timer timer;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			mFile = (FileData)getArguments()
					.getSerializable("file");

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.encode_screen, container, false);
		timer = new Timer();
		tvFileName = (TextView) v.findViewById(R.id.tv_filename);
		etFileDes = (EditText) v.findViewById(R.id.et_des_file);
		tvFileName.setText("" + mFile.getFileTitle());
		if( mFile.getFileDescription()!=null)
			etFileDes.setText("" + mFile.getFileDescription());
		Button btnEncode = (Button) v.findViewById(R.id.btn_encode);
		btnEncode.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (checkDescriptionInput()) {
					exeEncodeFile();
				}
			}
		});
		exeEncodeFile();
		return v;
	}

	protected boolean checkDescriptionInput() {
		// TODO Auto-generated method stub
		if (etFileDes.getText().length() == 0) {
			Animation anim = AnimationUtils.loadAnimation(getActivity(),
					R.anim.shake);
			etFileDes.startAnimation(anim);
			return false;
		}
		return true;
	}

	private void exeEncodeFile() {
		// TODO Auto-generated method stub
		((MainActivity) getActivity()).setProgressVisibility(true);
		Service service = new Service(this);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("SessionID",
				HoGoApplication.instace().getToken(getActivity()));
		params.put("QueueID", mFile.getQueueId());
		params.put("Title", tvFileName.getText());
		params.put("Description", etFileDes.getText());
		service.login(ServiceAction.ActionEncode, APIType.ENCODE, params);
		((MainActivity) getActivity()).setProgressVisibility(true);

	}

	private void exeCheckEncodeFile() {
		// TODO Auto-generated method stub
		((MainActivity) getActivity()).setProgressVisibility(true);
		Service service = new Service(this);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("SessionID",
				HoGoApplication.instace().getToken(getActivity()));
		params.put("QueueID", mFile.getQueueId());
		service.login(ServiceAction.ActionCheckEncodeStatus,
				APIType.CHECK_ENCODE, params);
		((MainActivity) getActivity()).setProgressVisibility(true);

	}

	@Override
	public void onCompleted(Service service, ServiceResponse result) {
		// TODO Auto-generated method stub
		if (result.isSuccess()
				&& result.getAction() == ServiceAction.ActionEncode) {
			DataParser parser = new DataParser(true);
			ResponseData resData = parser
					.parseEncodeResult((String) result.getData());
			boolean encodeStatus = resData.getStatus().equalsIgnoreCase("OK");
			if (encodeStatus) {
				Toast.makeText(getActivity(),
						"Upload Successful waiting Encode data",
						Toast.LENGTH_LONG).show();
				checkEncodeData();
			} else if(resData.getStatus().equalsIgnoreCase("SessionIdNotFound")){
				HoGoApplication.instace().setToken(getActivity(), null);
				((MainActivity)getActivity()).gotologinScreen();
			}else {
				Toast.makeText(getActivity(), "Upload Fail", Toast.LENGTH_LONG)
						.show();
			}
		}
		if (!result.isSuccess()
				&& result.getAction() == ServiceAction.ActionEncode) {
			((MainActivity) getActivity()).setProgressVisibility(false);
		}
		if (result.isSuccess()
				&& result.getAction() == ServiceAction.ActionCheckEncodeStatus) {
			DataParser parser = new DataParser(true);
			ResponseData encodeStatus = (ResponseData) parser
					.parseCheckEncodeResult((String) result.getData());
			if (encodeStatus.getStatus()!=null && encodeStatus.getStatus().equalsIgnoreCase("OK")) {
				if(timer!=null){
					timer.cancel();
				}
				Toast.makeText(getActivity(),
						"Upload Successful and Encode data sucessful",
						Toast.LENGTH_LONG).show();
				((MainActivity) getActivity()).setProgressVisibility(false);
				((MainActivity) getActivity()).gotoAddScreen((FileData)encodeStatus.getData());
			} else if (encodeStatus.getStatus()!=null && encodeStatus.getStatus().equals("4")) {
				if(timer!=null){
					timer.cancel();
				}
				Toast.makeText(getActivity(), "Upload and encode Fail",
						Toast.LENGTH_LONG).show();
				((MainActivity) getActivity()).setProgressVisibility(false);

			} else {
	
//				Toast.makeText(getActivity(), "Upload Fail", Toast.LENGTH_LONG)
//						.show();
			}
		}
		if (!result.isSuccess()
				&& result.getAction() == ServiceAction.ActionCheckEncodeStatus) {
			((MainActivity) getActivity()).setProgressVisibility(false);
		}

	}

	private void checkEncodeData() {
		// TODO Auto-generated method stub
		Calendar c = Calendar.getInstance();
		

		long seconds = c.get(Calendar.MILLISECOND);
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						exeCheckEncodeFile();		
					}
				});
				
			}
		}, seconds, 5000);
	}
}
