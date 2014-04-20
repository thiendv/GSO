package com.gso.hogoapi;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jp.co.ricoh.ssdk.sample.app.scan.activity.ScanActivity;
import jp.co.ricoh.ssdk.sample.app.scan.application.ScanSampleApplication;
import jp.co.ricoh.ssdk.sample.function.scan.ScanPDF;

import org.apache.http.HttpEntity;
import org.apache.http.ProtocolException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gso.hogoapi.fragement.AddFileSuccessfulFragment;
import com.gso.hogoapi.fragement.AppScanFragment;
import com.gso.hogoapi.fragement.BookShelfFragment;
import com.gso.hogoapi.fragement.EncodeFileFragment;
import com.gso.hogoapi.fragement.LoginFragment;
import com.gso.hogoapi.fragement.SendFileFragment;
import com.gso.hogoapi.fragement.SendHistoryFragment;
import com.gso.hogoapi.fragement.UploadFileFragment;
import com.gso.hogoapi.model.FileData;
import com.gso.hogoapi.model.FileUpload;
import com.gso.hogoapi.model.SendData;
import com.gso.hogoapi.util.pdf.JpegToPDF;
import com.gso.hogoapi.views.RadioGroupController;
import com.gso.hogoapi.views.TabButton;

public class MainActivity extends ScanActivity implements RadioGroupController.OnCheckedChangeListener, OnClickListener{

	protected static final String TAG = MainActivity.class.getSimpleName();
	private ProgressBar mPrBar;
	private FrameLayout mContent;
    private View mTopBar;
    private View mBottomBar;
	private FragmentManager mFramentManager;
	private View mBtnActionAdd;
	private View mBtnActionSend; 

	
	public static List<FileData> fileDataList = new ArrayList<FileData>();
    private ScanPDF mScanPDF;

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
            gotoScanScreen();

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

	public void gotoUpdateScreen(FileUpload file) {
		UploadFileFragment fragement = new UploadFileFragment();
		FragmentTransaction transaction = mFramentManager.beginTransaction();
		Bundle bundle = new Bundle();
		bundle.putSerializable("file", file);
		fragement.setArguments(bundle);
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
		FileData item = new FileData();
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
//			onBackPressed();
		}
		return super.onKeyDown(keyCode, event);
	}

    public void gotoScanScreen() {
    	AppScanFragment fragement = new AppScanFragment();
        FragmentTransaction transaction = mFramentManager.beginTransaction();
        transaction.replace(R.id.content, fragement).commit();
        findViewById(R.id.top_bar).setVisibility(View.VISIBLE);
    }

    @Override
    public void onJobCompleted() {
        super.onJobCompleted();
        mScanPDF = new ScanPDF(((ScanSampleApplication) getApplication()).getScanJob());
        /** Continue by change to send screen.
         * After user click send. You can get inputStream by call ((MainActivity)getActivity).getPDFInputStream().
         * */
        new AsyncTask<Void, Void, FileUpload>() {
            @Override
            protected FileUpload doInBackground(Void... params) {
                // How to get inputStream.
            	final String localPath = MainActivity.this.getFilesDir() + "/hogodoc_scan.jpg";
            	final String pdfPath = MainActivity.this.getFilesDir() + "/hogodoc_scan.pdf";
            	Log.d("pdfPath","pdfPath"+pdfPath);
            	InputStream in = null;
				try {
					Log.d(TAG,"path: "  + mScanPDF.getImageFilePath());
					write(mScanPDF.getImageInputStream(), localPath);
					in = mScanPDF.getImageInputStream();
//					in = convert2PDF(localPath);
					JpegToPDF convert = new JpegToPDF();
//					FileOutputStream fos = openFileOutput(pdfPath, Context.MODE_PRIVATE);
//					fos = new FileOutputStream(pdfPath,true);
					File file = new File(pdfPath);
					FileOutputStream fos = new FileOutputStream(file);
//					convert.convertJpegToPDF2( readBytes(in), fos);
					convert.convertJpegToPDF(localPath, fos);
//					if (in != null) {
//						write(in, pdfPath);
//						Log.d(TAG, "ConvertPDFSucceed!");
//						return pdfPath;
//					}
					FileUpload item = new FileUpload();
					item.setPdfPath(pdfPath);
					item.setJpgPath(localPath);
					return item;
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
                return null;
            }
            
            @Override
            protected void onPostExecute(FileUpload result) {
            	super.onPostExecute(result);
            	if (result != null) {
            		gotoUpdateScreen(result);
            	}
            }
        }.execute();
        
    }
    
    public byte[] readBytes(InputStream inputStream) throws IOException {
    	  // this dynamically extends to take the bytes you read
    	  ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

    	  // this is storage overwritten on each iteration with bytes
    	  int bufferSize = 1024;
    	  byte[] buffer = new byte[bufferSize];

    	  // we need to know how may bytes were read to write them to the byteBuffer
    	  int len = 0;
    	  while ((len = inputStream.read(buffer)) != -1) {
    	    byteBuffer.write(buffer, 0, len);
    	  }

    	  // and then we can return your byte array.
    	  return byteBuffer.toByteArray();
    	}
    
    private static InputStream convert2PDF(String filePath) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL("http://do.convertapi.com/Image2Pdf");
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("Content-Type", "multipart/form-data");
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setUseCaches(true);

            // Write post data
            conn.setDoOutput(true);
            OutputStream out = conn.getOutputStream();
            MultipartEntity requestData = new MultipartEntity();
            requestData.addPart("ApiKey", new StringBody("260387366"));
            requestData.addPart("file", new FileBody(new File(filePath), "pdf"));
            requestData.writeTo(out);
            out.close();
            InputStream is = null;
            if (conn.getResponseCode() >= 400) {
                is = conn.getErrorStream();
            } else {
                is = conn.getInputStream();
            }

            if (is == null) {
                return null;
            }

            return is;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }
    
    
    public static void write(InputStream inStream, String output)
            throws IOException {
        final File outputFile = new File(output);
        final File parent = outputFile.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        FileOutputStream outStream = new FileOutputStream(outputFile);
        byte[] buf = new byte[1024];
        int l;
        while ((l = inStream.read(buf)) >= 0) {
            outStream.write(buf, 0, l);
        }
        inStream.close();
        outStream.flush();
        outStream.close();
    }

    /**
     * NOTE: this method should be called in worker thread.
     * @return Return the scanned pdf by input stream.
     */
    public InputStream getPDFInputStream() {
        return mScanPDF.getImageInputStream();
    }
}
