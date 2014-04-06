package com.gso.hogoapi.fragement;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.gso.hogoapi.R;
import com.gso.hogoapi.model.FileData;
import com.squareup.picasso.Picasso;

/**
 * Created by GIANG on 4/5/14.
 */
public class BookShelfFragment extends Fragment {
	
	private BookShelfAdapter mBookShelfAdapter;
	private static int sRowHeight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bookshelf, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ListView listView = (ListView) view.findViewById(R.id.list_view);
        final ViewTreeObserver viewTreeObserver = listView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				viewTreeObserver.removeGlobalOnLayoutListener(this);
				sRowHeight = listView.getHeight() / 3;
				// load data to adapter.
		        mBookShelfAdapter.swapData(getMockData(10));
			}
		});
        mBookShelfAdapter = new BookShelfAdapter(getActivity());
        listView.setAdapter(mBookShelfAdapter);
        
    }
    
    private List<FileData> getMockData(int count) {
    	final String url = "http://www.booksshouldbefree.com/image/detail/Art-of-Public-Speaking.jpg";
    	final List<FileData> result = new ArrayList<>(count);
    	for (int i = 0; i < count; i++) {
			final FileData file = new FileData();
			file.setCoverImageUrl(url);
			result.add(file);
		}
    	return result;
    }

    private static class BookShelfAdapter extends BaseAdapter {

    	private List<FileData> mItems;
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
    		notifyDataSetChanged();
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

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
        	final ViewHolder viewHolder;
        	if (view == null) {
        		view = mInflater.inflate(R.layout.book_row, viewGroup, false);
        		view.getLayoutParams().height = sRowHeight;
        		viewHolder = new ViewHolder();
        		viewHolder.book1 = (ImageView) view.findViewById(R.id.book_1);
        		viewHolder.book2 = (ImageView) view.findViewById(R.id.book_2);
        		viewHolder.book3 = (ImageView) view.findViewById(R.id.book_3);
        	} else {
        		viewHolder = (ViewHolder) view.getTag();
        	}
        	
        	FileData file;
        	int index = i * 3;
        	if (index < mItems.size()) {
        		// The first column.
        		file = mItems.get(index);
            	Picasso.with(mContext).load(file.getCoverImageUrl()).into(viewHolder.book1);		
        	}
        
        	index++;
        	if (index < mItems.size()) {
        		// The second column.
        		file = mItems.get(index);
            	Picasso.with(mContext).load(file.getCoverImageUrl()).into(viewHolder.book2);	
        	}
        	
        	index++;
        	if (index < mItems.size()) {
        		// The third column.
        		file = mItems.get(index);
            	Picasso.with(mContext).load(file.getCoverImageUrl()).into(viewHolder.book3);
        	}
        	
            return view;
        }
        
        private static class ViewHolder {
        	public ImageView book1;
        	public ImageView book2;
        	public ImageView book3;
        }
    }
}
