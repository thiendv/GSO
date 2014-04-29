package com.gso.hogoapi.fragement;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gso.hogoapi.APIType;
import com.gso.hogoapi.HoGoApplication;
import com.gso.hogoapi.MainActivity;
import com.gso.hogoapi.R;
import com.gso.hogoapi.model.AddressBookItem;
import com.gso.hogoapi.model.FileData;
import com.gso.hogoapi.model.PackageData;
import com.gso.hogoapi.model.ResponseData;
import com.gso.hogoapi.model.SendData;
import com.gso.hogoapi.model.TransferData;
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
	private String mLocalCopies = "2";
	private String mFolder = "Folder";
	private boolean isPrint = true;
	private Button btnDateExprid;
	private int year = 0;
	private int month = 0;
	private int day = 0;
	private RelativeLayout rLDateExprid;
	private String currentDateandTime;
	private CheckBox cbxIsPrint;
	private Button btnAddressBook;

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
		sendData = (SendData) getArguments().getSerializable("send_data");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.sendfile_screen, container, false);
		Button btnSendFile = (Button) v.findViewById(R.id.btn_send_file_exe);
		etMailto = (EditText) v.findViewById(R.id.et_mail_to);
		btnDateExprid = (Button) v.findViewById(R.id.btn_doc_exquiry_date);
		rLDateExprid = (RelativeLayout) v.findViewById(R.id.rl_doc_exprid_date);
		btnAddressBook = (Button) v.findViewById(R.id.btn_address_book);
		btnSendFile.setOnClickListener(this);
		btnDateExprid.setOnClickListener(this);
		rLDateExprid.setOnClickListener(this);
		btnAddressBook.setOnClickListener(this);
		cbxIsPrint = (CheckBox) v.findViewById(R.id.chx_allow_printing);
		cbxIsPrint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				isPrint = isChecked;

			}
		});

		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		// currentDateandTime = sdf.format(new Date());

		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH) + 1;
		day = c.get(Calendar.DAY_OF_MONTH) + 1;

		currentDateandTime = year + "/" + month + "/" + day;
		btnDateExprid.setText(currentDateandTime);
		return v;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btn_send_file_exe) {
			if (sendData != null) {
				exeSendFile();
			}
		} else if (v.getId() == R.id.btn_doc_exquiry_date) {
			showDatePicker();
		} else if (v.getId() == R.id.btn_address_book) {
			exeGetAddressBook();
		}
	}

	private void exeGetAddressBook() {
		// TODO Auto-generated method stub
		AddressBookFragement fragment = new AddressBookFragement();
		fragment.setTargetFragment(this, 1);

		getFragmentManager().beginTransaction().add(fragment, "address_book").commit();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if (resultCode == Activity.RESULT_OK) {

				Bundle b = data.getExtras();
				TransferData dataTr = (TransferData) b.getSerializable("data");
				if (dataTr != null) {
					updateEmailSend(dataTr.getList());
				}

			} else if (resultCode == Activity.RESULT_CANCELED) {

			}
		}
	}

	protected void updateEmailSend(List<AddressBookItem> list) {
		// TODO Auto-generated method stub
		String mailTo = "";
		for (AddressBookItem item : list) {
			mailTo += mailTo.equals("") ? "" + item.getEmail() : "," + item.getEmail();
		}
		etMailto.setText(mailTo);
	}

	private void showDatePicker() {
		// TODO Auto-generated method stub
		Log.d("showDatePicker", "showDatePicker");
		new DatePickerDialog(getActivity(), datePickerListener, year, month, day).show();
	}

	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;

			// set selected date into textview
			btnDateExprid.setText(new StringBuilder().append(year).append("/").append(month + 1)
					.append("/").append(day));

			// set selected date into datepicker also
			// dpResult.init(year, month, day, null);

		}
	};

	private void exeSendFile() {
		// TODO Auto-generated method stub
		String stringDataSend = getDocumentListId();
		mailTo = getMailToList();

		if (mailTo != null && mailTo.length() == 0) {
			Toast.makeText(getActivity(), "Please check input data", Toast.LENGTH_LONG).show();
		} else {
			Service service = new Service(this);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("SessionID", HoGoApplication.instace().getToken(getActivity()));
			params.put("Documents", "" + stringDataSend);
			params.put("Method", "1");
			params.put("status_desc", "Test");
			params.put("Recipients", "" + mailTo);
			params.put("Printing", cbxIsPrint.isChecked());

			service.login(ServiceAction.ActionSend, APIType.SEND, params);
			((MainActivity) getActivity()).setProgressVisibility(true);
		}

	}

	private String getMailToList() {
		// TODO Auto-generated method stub
		Log.d("getMailToList", "mailTo " + etMailto.getText().toString());
		return etMailto.getText().toString();
	}

	private String getDocumentListId() {
		// TODO Auto-generated method stub
		String result = null;
		for (FileData item : sendData.getDataList()) {
			if (result == null) {
				result = "" + item.getDocumentId();
			} else {
				result += "," + item.getDocumentId();
			}
		}
		Log.d("getDocumentListId", "getDocumentListId " + result);
		return result;
	}

	@Override
	public void onCompleted(Service service, ServiceResponse result) {
		// TODO Auto-generated method stub
		if (result.isSuccess() && result.getAction() == ServiceAction.ActionSend) {
			Log.d("onCompleted", "onCompleted " + result.getData());
			DataParser parser = new DataParser(true);
			ResponseData resData = parser.parseSendResponse((String) result.getData());

			if (resData.getStatus().equalsIgnoreCase("OK")) {
				// Toast.makeText(getActivity(), "Send Successful",
				// Toast.LENGTH_LONG).show();
				PackageData packageData = (PackageData) resData.getData();
				sendJPackageNote(packageData);
				// ((MainActivity) getActivity()).gotoMainScreen();
			} else if (resData.getStatus().equalsIgnoreCase("SessionIdNotFound")) {
				HoGoApplication.instace().setToken(getActivity(), null);
				((MainActivity) getActivity()).gotologinScreen();
				((MainActivity) getActivity()).setProgressVisibility(false);
			} else {
				if(getActivity()!=null &&!getActivity().isFinishing()){
					Toast.makeText(getActivity(), "Send Fail", Toast.LENGTH_LONG).show();
					((MainActivity) getActivity()).setProgressVisibility(false);
				}

			}
		} else if (result.isSuccess() && result.getAction() == ServiceAction.ActionSendPackageNote) {
			Log.d("onCompleted", "ActionSendPackageNote " + result.getData());
			DataParser parser = new DataParser(true);
			ResponseData resData = parser.parseSendResponse((String) result.getData());
			((MainActivity) getActivity()).setProgressVisibility(false);
			if(getActivity()!=null &&!getActivity().isFinishing()){
				if (resData.getStatus().equalsIgnoreCase("OK")) {
					Toast.makeText(getActivity(), "Send Successful", Toast.LENGTH_LONG).show();

					((MainActivity) getActivity()).gotoScanScreen();
				} else if (resData.getStatus().equalsIgnoreCase("SessionIdNotFound")) {
					HoGoApplication.instace().setToken(getActivity(), null);
					((MainActivity) getActivity()).gotologinScreen();
				} else {
					Toast.makeText(getActivity(), "Send Fail", Toast.LENGTH_LONG).show();
				}
			}

		} else {
			((MainActivity) getActivity()).setProgressVisibility(false);
		}

	}

	private void sendJPackageNote(PackageData packageData) {
		// TODO Auto-generated method stub
		Service service = new Service(this);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("SessionID", HoGoApplication.instace().getToken(getActivity()));
		params.put("PackageID", "" + packageData.getId());
		params.put("EmailAddress", mailTo);
		// params.put("Subject",);// "HoGo has sent you the following documents"
		// params.put("Message", "");

		service.login(ServiceAction.ActionSendPackageNote, APIType.SEND_PACKAGE_NOTE, params);
		((MainActivity) getActivity()).setProgressVisibility(true);
	}

	public void updateSendEmail(AddressBookItem item) {
		// TODO Auto-generated method stub

	}

}
