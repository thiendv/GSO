package com.gso.hogoapi.fragement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gso.hogoapi.R;

public class AddFileFragment extends Fragment implements OnClickListener {
	
	private TextView mTbStatus;
	private ImageView mImgStatus;
	private View mPrgBar;
	private Button mBtnSubmit;
	
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
		mBtnSubmit = (Button)view.findViewById(R.id.btn_submit);
		mBtnSubmit.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_submit:
			
			break;

		default:
			break;
		}
	}

}
