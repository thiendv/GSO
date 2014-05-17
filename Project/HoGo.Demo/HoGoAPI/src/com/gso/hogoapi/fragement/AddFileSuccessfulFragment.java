package com.gso.hogoapi.fragement;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gso.hogoapi.MainActivity;
import com.gso.hogoapi.R;
import com.gso.hogoapi.model.SendData;

public class AddFileSuccessfulFragment extends Fragment implements OnClickListener {
	
	private TextView mTbStatus;
	private ImageView mImgStatus;
	private View mPrgBar;
	private Button mBtnSubmit;
	private SendData sendData;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		sendData = (SendData)getArguments().getSerializable("send_data");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_add_file, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		mTbStatus = (TextView) view.findViewById(R.id.tb_status);
		mImgStatus = (ImageView) view.findViewById(R.id.ic_status);
		mPrgBar = view.findViewById(R.id.progress_bar);
		mBtnSubmit = (Button)view.findViewById(R.id.btn_send_this_document);
		mBtnSubmit.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_send_this_document:
			((MainActivity)getActivity()).gotoSendDocumentScreen(sendData);
			break;

		default:
			break;
		}
	}

}
