package com.gso.hogoapi.fragement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.ClipData.Item;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.squareup.picasso.Picasso;

/**
 * Created by GIANG on 4/5/14.
 */
public class BookShelfFragment extends Fragment implements IServiceListener {
	
	private BookShelfAdapter mBookShelfAdapter;
	private static int sRowHeight;
	private MainActivity mActivity;
	private ListView mListView;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof MainActivity) {
			mActivity = (MainActivity) activity;
		}
	}

	private void getDocumentList() {
		// TODO Auto-generated method stub
		Service service = new Service(this);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("SessionID",
				HoGoApplication.instace().getToken(getActivity()));
		
		service.login(ServiceAction.ActionGetMyDocument, APIType.GET_MY_DOCUMENT, params);
		((MainActivity) getActivity()).setProgressVisibility(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mActivity != null) {
			mActivity.enterBookShelfScreen();
		}
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bookshelf, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView) view.findViewById(R.id.list_view);
//        final ViewTreeObserver viewTreeObserver = mListView.getViewTreeObserver();
//        viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
//  			
//			@Override
//			public void onGlobalLayout() {
//				viewTreeObserver.removeGlobalOnLayoutListener(this);
//				
//				sRowHeight = mListView.getHeight() / 3;
//				// load data to adapter.
////		        mBookShelfAdapter.swapData(getMockData(10));
//		        getDocumentList();
//			}
//		});
        sRowHeight = mListView.getHeight() / 3;
        getDocumentList();
    }
    
    public  void bindDataToListview(List<FileData> data){
    	if (mBookShelfAdapter !=null) {
			mBookShelfAdapter.swapData(data);
			mBookShelfAdapter.notifyDataSetChanged();
			mListView.setAdapter(mBookShelfAdapter);
		}else{
	        mBookShelfAdapter = new BookShelfAdapter(getActivity());
	        mBookShelfAdapter.swapData(data);
	        mListView.setAdapter(mBookShelfAdapter);
		}
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	if (mActivity != null) {
    		mActivity.exitBookShelfScreen();
//    		BookShelfAdapter.mItems = null;
    		((MainActivity)getActivity()).changeToAdd();
    	}
    }
    
    private List<FileData> getMockData(int count) {
    	final String url = "http://www.booksshouldbefree.com/image/detail/Art-of-Public-Speaking.jpg";
    	final List<FileData> result = new ArrayList<FileData>();
    	for (int i = 0; i < count; i++) {
			final FileData file = new FileData();
			file.setCoverImageUrl(url);
			result.add(file);
		}
    	return result;
    }

    
    public static class BookShelfAdapter extends BaseAdapter implements OnCheckedChangeListener {

    	public static List<FileData> mItems;
    	private Context mContext;
    	private LayoutInflater mInflater;
    	
    	public BookShelfAdapter(Context context) {
    		mContext = context;
    		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	}
    	
    	public void swapData(List<FileData> newData) {
    		if (mItems == newData) {
    			return;
    		}
    		
    		mItems = newData;
//    		notifyDataSetChanged();
    	}
    	
        @Override
        public int getCount() {
        	if (mItems != null) {
        		int count = mItems.size() / 3;
        		if (mItems.size() % 3 > 0) {
        			count++;
        		}
        		
        		if (count < 3) {
        			count = 3;
        		}
        		return count;
        	}
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return mItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }
        final String url = "http://www.booksshouldbefree.com/image/detail/Art-of-Public-Speaking.jpg";
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
        	final ViewHolder viewHolder;
        	if (view == null) {
        		view = mInflater.inflate(R.layout.book_row, viewGroup, false);
//        		view.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
        		viewHolder = new ViewHolder();
        		viewHolder.book1 = (ImageView) view.findViewById(R.id.book_1);
        		viewHolder.book2 = (ImageView) view.findViewById(R.id.book_2);
        		viewHolder.book3 = (ImageView) view.findViewById(R.id.book_3);
        		
        		viewHolder.tvFile1 = (TextView) view.findViewById(R.id.tv_name_file_1);
        		viewHolder.tvFile2 = (TextView) view.findViewById(R.id.tv_name_file_2);
        		viewHolder.tvFile3 = (TextView) view.findViewById(R.id.tv_name_file_3);

        		viewHolder.cbxFile1 = (CheckBox) view.findViewById(R.id.cbx_item1);
        		viewHolder.cbxFile2 = (CheckBox) view.findViewById(R.id.cbx_item2);
        		viewHolder.cbxFile3 = (CheckBox) view.findViewById(R.id.cbx_item3);
        		
        		
        		
        		viewHolder.cbxFile1.setOnCheckedChangeListener(this);
        		viewHolder.cbxFile2.setOnCheckedChangeListener(this);
        		viewHolder.cbxFile3.setOnCheckedChangeListener(this);

        		view.setTag(viewHolder);
        	} else {
        		viewHolder = (ViewHolder) view.getTag();
        	}
        	
        	FileData file;
        	int index = i * 3;
        	if (index < mItems.size()) {
        		// The first column.
        		file = mItems.get(index);
        		file.setCoverImageUrl(url);
            	Picasso.with(mContext).load(file.getCoverImageUrl()).into(viewHolder.book1);	
            	viewHolder.tvFile1.setText(file.getDocumentId());
            	
            	viewHolder.cbxFile1.setTag(file);
            	
            	viewHolder.cbxFile1.setChecked(file.getIsChecked());

        	}
        
        	index++;
        	if (index < mItems.size()) {
        		// The second column.
        		file = mItems.get(index);
        		file.setCoverImageUrl(url);
            	Picasso.with(mContext).load(file.getCoverImageUrl()).into(viewHolder.book2);
            	viewHolder.tvFile2.setText(file.getDocumentId());

            	viewHolder.cbxFile2.setTag(file);
            	
            	viewHolder.cbxFile2.setChecked(file.getIsChecked());

        	}
        	
        	index++;
        	if (index < mItems.size()) {
        		// The third column.
        		file = mItems.get(index);
        		file.setCoverImageUrl(url);
            	Picasso.with(mContext).load(file.getCoverImageUrl()).into(viewHolder.book3);
            	viewHolder.tvFile3.setText(file.getDocumentId());
            	
            	viewHolder.cbxFile3.setTag(file);
            	
            	viewHolder.cbxFile3.setChecked(file.getIsChecked());


        	}
        	
            return view;
        }
        
        private static class ViewHolder {
        	public CheckBox cbxFile3;
			public CheckBox cbxFile2;
			public CheckBox cbxFile1;
			public TextView tvFile3;
			public TextView tvFile2;
			public TextView tvFile1;
			public ImageView book1;
        	public ImageView book2;
        	public ImageView book3;
        }

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
				updateListItem((FileData) buttonView.getTag(), isChecked);
				super.notifyDataSetChanged();
		}

		private void updateListItem(FileData tag, boolean isChecked) {
			// TODO Auto-generated method stub
			if(tag !=null && tag.getDocumentId() !=null){
				for(FileData item: mItems){
					if(item.getDocumentId().equals(tag.getDocumentId())){
						item.setIsChecked(isChecked);
					}
				}
			}
			boolean isSend = false;
			for(FileData item: mItems){
				if(item.getIsChecked()){
					((MainActivity)mContext).changeAddtoSendData();
					isSend = true;
				}
			}
			if(!isSend){
				((MainActivity)mContext).changeToAdd();
			}
		}
    }

	@Override
	public void onCompleted(Service service, ServiceResponse result) {
		// TODO Auto-generated method stub
		if (result.isSuccess()
				&& result.getAction() == ServiceAction.ActionGetMyDocument) {
			Log.d("onCompleted", "onCompleted " + result.getData());
			DataParser parser = new DataParser(true);
			ResponseData resData =  parser
					.parseGetDocumentListResult((String) result.getData());
			List<FileData> parseData = (List<FileData>)resData.getData();
			if (resData.getStatus().equalsIgnoreCase("OK")) {
//				Toast.makeText(getActivity(), "Upload Successful",
//						Toast.LENGTH_LONG).show();
				bindDataToListview(parseData);
			} else if(resData.getStatus().equalsIgnoreCase("SessionIdNotFound")){
				HoGoApplication.instace().setToken(getActivity(), null);
				((MainActivity)getActivity()).gotologinScreen();
			}else {
//				Toast.makeText(getActivity(), "Upload Fail", Toast.LENGTH_LONG)
//						.show();
			}
		}else{
			Toast.makeText(getActivity(), "Can not get data for now!", Toast.LENGTH_LONG)
			.show();
		}
		if(!getActivity().isFinishing()){
			((MainActivity) getActivity()).setProgressVisibility(false);	
		}
		
	}
}
