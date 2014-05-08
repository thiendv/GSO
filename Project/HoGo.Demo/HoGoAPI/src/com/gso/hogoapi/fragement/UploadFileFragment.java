package com.gso.hogoapi.fragement;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.mime.content.FileBody;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.artifex.mupdf.MuPDFFragment;
import com.gso.hogoapi.APIType;
import com.gso.hogoapi.FileDialog;
import com.gso.hogoapi.HoGoApplication;
import com.gso.hogoapi.MainActivity;
import com.gso.hogoapi.R;
import com.gso.hogoapi.model.FileData;
import com.gso.hogoapi.model.FileUpload;
import com.gso.hogoapi.model.ResponseData;
import com.gso.hogoapi.service.DataParser;
import com.gso.serviceapilib.IServiceListener;
import com.gso.serviceapilib.Service;
import com.gso.serviceapilib.ServiceAction;
import com.gso.serviceapilib.ServiceResponse;
import com.squareup.picasso.Picasso;

public class UploadFileFragment extends MuPDFFragment implements OnClickListener,
		IServiceListener {

	private static final int SELECT_FILE = 0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	private File mFilePath;
	private EditText mEtFilePath;
	private String mPath;
	private FileUpload mFileUpload;
	private String mFileName;
	private String mCurrentDateandTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		mFileUpload = (FileUpload) bundle.getSerializable("file");
		core = openFile(Uri.decode(mFileUpload.getPdfPath()));
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.uploadfile_screen, container, false);
		ImageButton btnUpload = (ImageButton) v
				.findViewById(R.id.btn_upload_file);
		Button btnUploadExe = (Button) v.findViewById(R.id.btn_upload_file_exe);
		Button btnEncode = (Button) v.findViewById(R.id.btn_encode_file);
		Button btnCheckEncode = (Button) v
				.findViewById(R.id.btn_check_encode_statsu);
		mEtFilePath = (EditText) v.findViewById(R.id.et_file_path);

		mFileName = getFileNameWithoutExtn(mFileUpload.getPdfPath());
		
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH) + 1;

		int hours = c.get(Calendar.HOUR_OF_DAY);
		int minutes = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);
		
		mCurrentDateandTime = year + "-" + month + "-" + day;
		mEtFilePath.setText(
                "" + mFileName + "_" + mCurrentDateandTime + "_" + hours + "-" + minutes + "-"
                        + second);

		if (core == null) {
			ImageView imgPreview = (ImageView) v.findViewById(R.id.img_preview);
			imgPreview.setVisibility(View.VISIBLE);
			Picasso.with(getActivity()).load(new File(mFileUpload.getJpgPath()))
					.into(imgPreview);	
		} else{
			FrameLayout imgPreviewContainer = (FrameLayout) v.findViewById(R.id.img_preview_container);
			View superView = super.onCreateView(inflater, imgPreviewContainer, savedInstanceState);
			imgPreviewContainer.addView(superView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
		}
		
		btnUpload.setOnClickListener(this);
		btnEncode.setOnClickListener(this);
		btnCheckEncode.setOnClickListener(this);
		btnUploadExe.setOnClickListener(this);
		
		return v;
	}

	public String getFileName(String url) {

		String fileName = url.substring(url.lastIndexOf('/') + 1, url.length());
		return fileName;
	}

	public static String getFileNameWithoutExtn(String url) {
		String fileName = url.substring(url.lastIndexOf('/') + 1, url.length());
		String fileNameWithoutExtn = fileName.substring(0,
				fileName.lastIndexOf('.'));
		return fileNameWithoutExtn;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);

	}

	private void loadFileList() {
		// TODO Auto-generated method stub
		// Intent intent = new Intent();
		// intent.setType(".pdf/*");
		// intent.setAction(Intent.ACTION_GET_CONTENT);
		// startActivityForResult(Intent.createChooser(intent,
		// "Select Picture"), SELECT_FILE);
		File mPath = new File(Environment.getExternalStorageDirectory()
				+ "//DIR//");
		FileDialog fileDialog = new FileDialog(getActivity(), mPath);
		fileDialog.setFileEndsWith(".pdf");
		fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
			public void fileSelected(File file) {
				Log.d(getClass().getName(), "selected file " + file.toString());
				onItemSelected(file);
			}
		});

		fileDialog.showDialog();
	}

	protected void onItemSelected(File file) {
		// TODO Auto-generated method stub
		mFilePath = file;
		mEtFilePath.setText("" + file.getAbsolutePath());
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = getActivity().managedQuery(uri, projection, null, null,
				null);
		if (cursor != null) {
			// HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
			// THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} else
			return null;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btn_upload_file) {
			loadFileList();
		} else if (v.getId() == R.id.btn_encode_file) {
			loadFileList();
		} else if (v.getId() == R.id.btn_check_encode_statsu) {
			loadFileList();
		} else if (v.getId() == R.id.btn_upload_file_exe) {
			exeUploadFile();
		}
	}

	private void exeUploadFile() {
		// TODO Auto-generated method stub
		if (checkInputData()) {
			Service service = new Service(this);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("SessionID",
					HoGoApplication.instace().getToken(getActivity()));
			try {
				String oldFile = getFileNameWithoutExtn(mFileUpload.getPdfPath());
//				String url = mFileUpload.getPdfPath().replace(oldFile, mEtFilePath.getText());
				File file = new File("" + mFileUpload.getPdfPath());
				if (file.exists()) {
					FileBody encFile = new FileBody(file, "pdf");
					params.put("File", encFile);
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			service.login(ServiceAction.ActionUpload, APIType.UPLOAD, params);
			((MainActivity) getActivity()).setProgressVisibility(true);

		} else {

		}
	}

	private boolean checkInputData() {
		// TODO Auto-generated method stub
		boolean isEnought = true;
		if (mEtFilePath.getText().length() == 0) {
			isEnought = false;
			Toast.makeText(getActivity(), "Scan again", Toast.LENGTH_LONG)
					.show();
		}
		if (HoGoApplication.instace().getToken(getActivity()) == null) {
			isEnought = false;
			((MainActivity) getActivity()).showLogin();
		}
		return isEnought;
	}

	@Override
	public void onCompleted(Service service, ServiceResponse result) {
		// TODO Auto-generated method stub
		if (result.isSuccess()
				&& result.getAction() == ServiceAction.ActionUpload) {
			Log.d("onCompleted", "onCompleted " + result.getData());
			DataParser parser = new DataParser(true);
			ResponseData resData = parser.parseUpdateResult((String) result
					.getData());
			FileData parseData = (FileData) resData.getData();
			
			if (resData.getStatus().equals("OK")) {
				if(getActivity()!=null &&!getActivity().isFinishing()){
					Toast.makeText(getActivity(), "Document uploaded successfully, please waiting for encoding data.",
							Toast.LENGTH_LONG).show();
					parseData.setFileTitle(""+mEtFilePath.getText().toString());
				}

				((MainActivity) getActivity()).gotoEncodeScreen(parseData);
			} else if (resData.getStatus()
					.equalsIgnoreCase("SessionIdNotFound")) {
				HoGoApplication.instace().setToken(getActivity(), null);
				((MainActivity) getActivity()).gotologinScreen();
			} else {
				if(getActivity()!=null &&!getActivity().isFinishing()){
					Toast.makeText(getActivity(), "Upload Fail", Toast.LENGTH_LONG)
					.show();	
				}

			}
		}
		((MainActivity) getActivity()).setProgressVisibility(false);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SELECT_FILE) {
			Uri selectedImageUri = data.getData();

			mEtFilePath.setText("" + getPath(selectedImageUri));

		}
	}

}
