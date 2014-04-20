package com.gso.hogoapi.fragement;

import jp.co.ricoh.ssdk.sample.app.scan.activity.ScanFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gso.hogoapi.R;

public class AppScanFragment extends ScanFragment {
	
	
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	        return inflater.inflate(R.layout.fragment_scan, container, false);
	    }

}
