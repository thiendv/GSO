//package com.gso.hogoapi.util.pdf;
//
//import java.io.ByteArrayOutputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//public class AndroidInternalStorageActivity extends Activity {
//	
//	EditText edFileName, edContent;
//	Button btnSave;
//	
//    /** Called when the activity is first created. */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//        edFileName = (EditText)findViewById(R.id.filename);
//        edContent = (EditText)findViewById(R.id.content);
//    	btnSave = (Button)findViewById(R.id.save);
//    	
//    	btnSave.setOnClickListener(new Button.OnClickListener(){
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				String fileName = edFileName.getText().toString();
//				String content = edContent.getText().toString();
//				
//				FileOutputStream fos;
//				try {
//					
//					fos = openFileOutput(fileName, Context.MODE_PRIVATE);
//					//fos.write(content.getBytes());
//					//fos.close();
//					
//					//Load image sample
//					Bitmap defaultIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
//					ByteArrayOutputStream stream = new ByteArrayOutputStream();
//					defaultIcon.compress(Bitmap.CompressFormat.PNG, 100, stream);
//					byte[] byteArray = stream.toByteArray();
//					
//					//Create PDF
//					JpegToPDF jpegToPDF=new JpegToPDF();
//					jpegToPDF.convertJpegToPDF2(byteArray, fos);
//					
//					Toast.makeText(
//							AndroidInternalStorageActivity.this, 
//							fileName + " saved", 
//							Toast.LENGTH_LONG).show();
//					
//				} catch (FileNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//			}});
//    	
//    }
//}