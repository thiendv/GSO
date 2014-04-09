package com.gso.hogoapi.fragement;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gso.hogoapi.APIType;
import com.gso.hogoapi.HoGoApplication;
import com.gso.hogoapi.MainActivity;
import com.gso.hogoapi.R;
import com.gso.hogoapi.model.FileData;
import com.gso.hogoapi.model.ResponseData;
import com.gso.hogoapi.model.SendData;
import com.gso.hogoapi.service.DataParser;
import com.gso.serviceapilib.IServiceListener;
import com.gso.serviceapilib.Service;
import com.gso.serviceapilib.ServiceAction;
import com.gso.serviceapilib.ServiceResponse;

public class SendFileFragment extends Fragment implements OnClickListener, IServiceListener {

	private static final int SELECT_FILE = 0;
	private SendData sendData;
	private String mailTo;
	private EditText etMailto;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	

	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		sendData = (SendData)getArguments().getSerializable("send_data");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.sendfile_screen, container, false);
		Button btnSendFile = (Button) v.findViewById(R.id.btn_send_file_exe);
		etMailto = (EditText)v.findViewById(R.id.et_mail_to);
		btnSendFile.setOnClickListener(this);
		return v;
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btn_send_file_exe) {
			if(sendData!=null){
				exeSendFile();
			}
		}
	}

	private void exeSendFile() {
		// TODO Auto-generated method stub
		String stringDataSend = getDocumentListId();
		mailTo = getMailToList();

		if(mailTo!=null && mailTo.length() ==0){
			Toast.makeText(getActivity(), "Please check input data", Toast.LENGTH_LONG).show();
		}else{
			Service service = new Service(this);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("SessionID", HoGoApplication.instace().getToken(getActivity()));
			params.put("Documents", ""+stringDataSend);
			params.put("Method", "1");
			params.put("status_desc", "Test");
			params.put("Recipients", ""+mailTo);
			
			service.login(ServiceAction.ActionSend, APIType.SEND, params);
			((MainActivity) getActivity()).setProgressVisibility(true);
		}

	}

	private String getMailToList() {
		// TODO Auto-generated method stub
		Log.d("getMailToList","mailTo "+etMailto.getText().toString());
		return etMailto.getText().toString();
	}




	private String getDocumentListId() {
		// TODO Auto-generated method stub
		String result = null;
		for(FileData item: sendData.getDataList()){
			if(result == null){
				result=""+item.getDocumentId();
			}else{
				result+=","+item.getDocumentId();
			}
		}
		Log.d("getDocumentListId","getDocumentListId "+result);
		return result;
	}

	@Override
	public void onCompleted(Service service, ServiceResponse result) {
		// TODO Auto-generated method stub
		if (result.isSuccess() && result.getAction() == ServiceAction.ActionSend) {
			Log.d("onCompleted", "onCompleted " + result.getData());
			DataParser parser = new DataParser(true);
			ResponseData resData = parser.parseSendResponse((String) result.getData());
			
			if (resData.getStatus().equalsIgnoreCase("OK") ) {
				Toast.makeText(getActivity(), "Send Successful", Toast.LENGTH_LONG).show();
				((MainActivity) getActivity()).gotoMainScreen();
			} else if(resData.getStatus().equalsIgnoreCase("SessionIdNotFound")){
				HoGoApplication.instace().setToken(getActivity(), null);
				((MainActivity)getActivity()).gotologinScreen();
			}else {
				Toast.makeText(getActivity(), "Send Fail", Toast.LENGTH_LONG).show();
			}
		}
		((MainActivity) getActivity()).setProgressVisibility(false);
	}

}
