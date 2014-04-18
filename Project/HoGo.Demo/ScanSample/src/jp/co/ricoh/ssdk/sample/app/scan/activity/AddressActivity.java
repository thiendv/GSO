/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
 package jp.co.ricoh.ssdk.sample.app.scan.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import jp.co.ricoh.ssdk.sample.app.scan.R;
import jp.co.ricoh.ssdk.sample.app.scan.application.DestinationSettingDataHolder;
import jp.co.ricoh.ssdk.sample.app.scan.application.ScanSampleApplication;
import jp.co.ricoh.ssdk.sample.function.common.SmartSDKApplication;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.AddressbookDestinationSetting;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.AddressbookDestinationSetting.DestinationKind;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.DestinationSettingItem;
import jp.co.ricoh.ssdk.sample.wrapper.client.BasicRestContext;
import jp.co.ricoh.ssdk.sample.wrapper.common.Request;
import jp.co.ricoh.ssdk.sample.wrapper.common.RequestHeader;
import jp.co.ricoh.ssdk.sample.wrapper.common.Response;
import jp.co.ricoh.ssdk.sample.wrapper.rws.addressbook.Addressbook;
import jp.co.ricoh.ssdk.sample.wrapper.rws.addressbook.Entry;
import jp.co.ricoh.ssdk.sample.wrapper.rws.addressbook.GetEntryResponseBody;
import jp.co.ricoh.ssdk.sample.wrapper.rws.addressbook.GetTagListResponseBody;
import jp.co.ricoh.ssdk.sample.wrapper.rws.addressbook.Tag;
import jp.co.ricoh.ssdk.sample.wrapper.rws.addressbook.TagArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * アドレス帳を表示するアクティビティです。
 * The activity to display address book.
 */
public class AddressActivity extends Activity {
    private static final String TAG = "AddressActivity";

    /**
     * このアクティビティのリクエストコード
     * Request code of this activity
     */
    public static final int REQUEST_CODE_ADDRESS_ACTIVITY = 1000;

    /**
     * 宛先種別のキー
     * Destination type key
     */
    public static final String KEY_DEST_KIND = "destination_kind";

    /**
     * 宛先表示名のキー
     * Destination display name key
     */
    public static final String KEY_DISPLAY = "key_display";

    /**
     * OKボタン
     * OK button
     */
    private Button mOkButton;

    /**
     * キャンセルボタン
     * Cancel button
     */
    private Button mCancelButton;

    /**
     * 見出しグループのビューフリッパー
     * View flipper of tag group
     */
    private ViewFlipper mTagGroupFlipper;

    /**
     * 見出しグループ1
     * Tag group 1
     */
    private ViewGroup mTagGroup1;

    /**
     * 見出しグループ2
     * Tag group 2
     */
    private ViewGroup mTagGroup2;

    /**
     * 見出しグループ3
     * Tag group 3
     */
    private ViewGroup mTagGroup3;

    /**
     * 見出しボタンのリスト
     * List of tag buttons
     */
    private List<Button> mTagButtons;

    /**
     * 見出しグループ切替ボタン
     * Tag group switching button
     */
    private Button mChangeTagGroupButton;

    /**
     * プログレスバー表示エリア
     * Progress bar display area
     */
    private ViewGroup mProgressArea;

    /**
     * エントリーリスト表示エリア
     * Entry list display area
     */
    private ViewGroup mEntryListArea;

    /**
     * エントリーリストのビュー
     * Entry list view
     */
    private ListView mEntryListView;

    /**
     * 選択中のエントリー
     * Selected entry
     */
    private int mSelectedTagId = -1;

    /**
     * 選択中のエントリーID
     * Selected entry ID
     */
    private String mSelectedEntryId = null;

    /**
     * 選択中のエントリーの表示名
     * Display name of selected entry
     */
    private String mSelectedKeyDisplay;

    /**
     * 見出しの表示順と見出しのマップ
     * Map for title display order and title
     */
    private Map<Integer, Tag> mTagMap;

    /**
     * エントリーリスト
     * Entry list
     */
    private List<Entry> mEntryList;

    /**
     * エントリーリストのアダプター
     * Entry list adapter
     */
    private EntryListAdapter mEntryListAdapter;

    /**
     * アドレス帳
     * Address book
     */
    private Addressbook mAddressbook;

    /**
     * 宛先種別
     * Destination kind
     */
    private DestinationKind mKind = null;

    /**
     * 宛先の設定値
     * Destination setting
     */
    private DestinationSettingDataHolder mDestinationSettingDataHolder;

    /**
     * エントリ一覧取得タスク
     * Entry list acquisition task
     */
    private GetEntryListTask mGetEntryListTask;

    /**
     * 見出し一覧取得タスク
     * Tag list acquisition task
     */
    private GetTagListTask mGetTagListTask;

    /**
     * アクティビティが生成されると呼び出されます。
     * [処理内容]
     *   (1)アプリケーションの初期化
     *   (2)宛先タイプの取得とタイトルの設定
     *   (3)宛先選択状態の設定
     *   (4)見出しボタンの設定
     *   (5)見出し切替ボタンの設定
     *   (6)宛先表示欄の設定
     *   (7)OKボタンの設定
     *   (8)キャンセルボタンの設定
     *
     * Called when an activity is created.
     * [Processes]
     *   (1) Initialize application
     *   (2) Obtain destination type and set title
     *   (3) Set destination selection state
     *   (4) Set tag button
     *   (5) Set tag group switching button
     *   (6) Set destination display area
     *   (7) Set OK button
     *   (8) Set cancel button
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        //(1)
        ScanSampleApplication app = (ScanSampleApplication)getApplication();
        mDestinationSettingDataHolder = app.getDestinationSettingDataHolder();
        // Addressbook API supported only https
        mAddressbook = new Addressbook(new BasicRestContext("https"));

        //(2)
        Intent intent = getIntent();
        if (intent != null) {
            mKind = DestinationKind.valueOf(intent.getStringExtra(KEY_DEST_KIND));
        }
        if (mKind == null) {
            setResult(RESULT_CANCELED);
            finish();
        }
        TextView title = (TextView) findViewById(R.id.textView_title);
        title.setText("Select from addressbook (" + mKind + ")");

        //(3)
        DestinationSettingItem destItem = mDestinationSettingDataHolder.getDestinationSettingItem();
        if (destItem instanceof AddressbookDestinationSetting) {
            AddressbookDestinationSetting dest = (AddressbookDestinationSetting)destItem;
            DestinationKind destKind = dest.getDestinationKind();
            if (destKind == mKind) {
                mSelectedEntryId = dest.getEntryId();
            }
        }

        //(4)
        mTagButtons = new ArrayList<Button>();
        mTagGroupFlipper = (ViewFlipper) findViewById(R.id.flipper_tag_group);
        mTagGroup1 = (ViewGroup) findViewById(R.id.tag_group_1);
        mTagGroup2 = (ViewGroup) findViewById(R.id.tag_group_2);
        mTagGroup3 = (ViewGroup) findViewById(R.id.tag_group_3);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedTagId = ((Integer)v.getTag()).intValue();
                changeTagId(selectedTagId);
            }
        };

        Button button = (Button) mTagGroup1.getChildAt(0);
        button.setOnClickListener(listener);
        button.setTag(Integer.valueOf(1));
        mTagButtons.add(button);

        button = (Button) mTagGroup2.getChildAt(0);
        button.setOnClickListener(listener);
        button.setTag(Integer.valueOf(1));
        mTagButtons.add(button);

        button = (Button) mTagGroup3.getChildAt(0);
        button.setOnClickListener(listener);
        button.setTag(Integer.valueOf(1));
        mTagButtons.add(button);

        int tagIndex = 2;
        for (int i = 1; i < mTagGroup1.getChildCount(); i++) {
            button = (Button) mTagGroup1.getChildAt(i);
            button.setOnClickListener(listener);
            button.setTag(Integer.valueOf(tagIndex));
            mTagButtons.add(button);
            tagIndex++;
        }
        for (int i = 1; i < mTagGroup2.getChildCount(); i++) {
            button = (Button) mTagGroup2.getChildAt(i);
            button.setOnClickListener(listener);
            button.setTag(Integer.valueOf(tagIndex));
            mTagButtons.add(button);
            tagIndex++;
        }
        for (int i = 1; i < mTagGroup3.getChildCount(); i++) {
            button = (Button) mTagGroup3.getChildAt(i);
            button.setOnClickListener(listener);
            button.setTag(Integer.valueOf(tagIndex));
            mTagButtons.add(button);
            tagIndex++;
        }

        //(5)
        mChangeTagGroupButton = (Button) findViewById(R.id.button_change_tag_group);
        mChangeTagGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTagGroupFlipper.showNext();

                //見出しグループを変更した際は常用(tagId=1)を選択状態とします
                changeTagId(1);
            }
        });
        mTagGroupFlipper.setVisibility(View.INVISIBLE);

        //(6)
        mProgressArea = (ViewGroup) findViewById(R.id.layout_progress);
        mEntryListArea = (ViewGroup) findViewById(R.id.layout_entry_view);

        mEntryList = new ArrayList<Entry>();
        mEntryListAdapter = new EntryListAdapter();

        mEntryListView = (ListView) findViewById(R.id.listview_address_entry);
        mEntryListView.setAdapter(mEntryListAdapter);

        mEntryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedEntryId = mEntryList.get(position).getEntryId();
                mSelectedKeyDisplay = mEntryList.get(position).getKeyDisplay();
                mOkButton.setEnabled(true);
            }
        });

        //(7)
        mOkButton = (Button)findViewById(R.id.button_ok);
        if (mSelectedEntryId==null) {
            mOkButton.setEnabled(false);
        }
        mOkButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (mSelectedEntryId != null) {
                    DestinationSettingItem destItem = DestinationSettingDataHolder.createAddressbookDestinationSetting(mKind, mSelectedEntryId);
                    mDestinationSettingDataHolder.setDestinationSettingItem(destItem);

                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_DISPLAY, mSelectedKeyDisplay);
                    Intent data = new Intent();
                    data.putExtras(bundle);
                    setResult(RESULT_OK, data);
                } else {
                    setResult(RESULT_CANCELED);
                }
                finish();
            }
        });

        //(8)
        mCancelButton = (Button)findViewById(R.id.button_cancel);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

    }

    /**
     * このアクティビティが開始した際に呼び出されます。
     * 見出し一覧取得タスクを開始します。
     * Called when this activity starts.
     * Starts the task to obtain title list.
     */
    @Override
    public void onStart() {
        super.onStart();
        mGetTagListTask = new GetTagListTask();
        mGetTagListTask.execute();
    }

    /**
     * このアクティビティが停止した際に呼び出されます。
     * 非同期タスクが実行中だった場合、キャンセルします。
     * Called when this activity is stopped.
     * If asynchronous task is in process, the task is cancelled
     */
    @Override
    public void onStop() {
        if (mGetEntryListTask != null) {
            mGetEntryListTask.cancel(false);
            mGetEntryListTask = null;
        }
        if (mGetTagListTask != null) {
            mGetTagListTask.cancel(false);
            mGetTagListTask = null;
        }
        super.onStop();
    }

    /**
     * このアクティビティが再開した際に呼び出されます。
     * Called when this activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        sendBroadcast(new Intent(DialogUtil.INTENT_ACTION_SUB_ACTIVITY_RESUMED));
    }

    /**
     * アクティビティが破棄される際に呼び出されます。
     * Called when this activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOkButton = null;
        mCancelButton = null;
        mTagGroupFlipper = null;
        mTagGroup1 = null;
        mTagGroup2 = null;
        mTagGroup3 = null;
        mTagButtons = null;
        mChangeTagGroupButton = null;
        mProgressArea = null;
        mEntryListArea = null;
        mEntryListView = null;
        mSelectedEntryId = null;
        mSelectedKeyDisplay = null;
        mTagMap = null;
        mEntryList = null;
        mEntryListAdapter = null;
        mAddressbook = null;
        mKind = null;
        mDestinationSettingDataHolder = null;
    }

    /**
     * プログレスバーの表示・非表示を行います。
     * Displays/hides the progress bar.
     *
     * @param show
     */
    private void showProgressBar(boolean show) {
        if (show) {
            mEntryListArea.setVisibility(View.INVISIBLE);
            mProgressArea.setVisibility(View.VISIBLE);
        } else {
            mProgressArea.setVisibility(View.INVISIBLE);
            mEntryListArea.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 指定された見出しボタンを選択状態にします。
     * Changes the selection state of the specified title button to "selected."
     *
     * @param selectTagId
     */
    private void selectTagButton(int selectTagId) {
        for (int i = 0; i < mTagButtons.size(); i++) {
            Button button = mTagButtons.get(i);
            int tagId = ((Integer)button.getTag()).intValue();
            boolean selected = (tagId == selectTagId);
            if (button.isSelected() != selected) {
                button.setSelected(selected);
            }
        }
    }

    /**
     * 選択中の見出しを変更したときの処理を行います。
     * [処理内容]
     *   (1)プログレスバーを表示します。
     *   (2)新しく選択した見出しを選択状態にします。
     *   (3)選択された見出しに所属するエントリID一覧を取得します。
     *   (4)エントリリスト取得タスクを開始します。
     *
     * Performs the process for the time the selected title is changed.
     * [Processes]
     *   (1) Displays the progress bar.
     *   (2) Changes the selection state of the newly selected title to "selected."
     *   (3) Obtains the list of entry IDs which belong to the selected title.
     *   (4) Starts the task to obtain entry list.
     *
     * @param newTagId
     */
    private void changeTagId(int newTagId) {
        if (mSelectedTagId != newTagId) {
            mSelectedTagId = newTagId;

            //(1)
            showProgressBar(true);

            //(2)
            selectTagButton(newTagId);

            //(3)
            Tag tag = mTagMap.get(newTagId);
            List<String> entryIdList;
            if (tag != null && tag.getTagsEntryList() != null) {
                entryIdList = new ArrayList<String>(tag.getEntryNum());
                putToEntryIdList(entryIdList, tag.getTagsEntryList().getTagPriority1());
                putToEntryIdList(entryIdList, tag.getTagsEntryList().getTagPriority2());
                putToEntryIdList(entryIdList, tag.getTagsEntryList().getTagPriority3());
                putToEntryIdList(entryIdList, tag.getTagsEntryList().getTagPriority4());
                putToEntryIdList(entryIdList, tag.getTagsEntryList().getTagPriority5());
                putToEntryIdList(entryIdList, tag.getTagsEntryList().getTagPriority6());
                putToEntryIdList(entryIdList, tag.getTagsEntryList().getTagPriority7());
                putToEntryIdList(entryIdList, tag.getTagsEntryList().getTagPriority8());
                putToEntryIdList(entryIdList, tag.getTagsEntryList().getTagPriority9());
                putToEntryIdList(entryIdList, tag.getTagsEntryList().getTagPriority10());
            } else {
                entryIdList = Collections.emptyList();
            }

            //(4)
            if (mGetEntryListTask!=null) {
                mGetEntryListTask.cancel(false);
            }
            mGetEntryListTask = new GetEntryListTask();
            mGetEntryListTask.execute(entryIdList.toArray(new String[entryIdList.size()]));
        }
    }


    /**
     * エントリIDリストをコピーします。
     * Copies the entry ID list.
     *
     * @param entryIdList 出力先のエントリIDリスト
     *                    Output entry ID list
     * @param tagPriorityIdList 入力のエントリIDリスト
     *                    Input entry ID list
     */
    private void putToEntryIdList(List<String> entryIdList, List<String> tagPriorityIdList) {
        if (tagPriorityIdList != null) {
            for (String entryId : tagPriorityIdList) {
                if (entryId != null) {
                    entryIdList.add(entryId);
                }
            }
        }
    }

    /**
     * 見出し一覧を取得するタスクです。
     * The task to obtain title list.
     */
    class GetTagListTask extends AsyncTask<Void, Void, Map<Integer, Tag>> {

        @Override
        protected Map<Integer, Tag> doInBackground(Void... ignore) {
            LinkedHashMap<Integer, Tag> tagMap = new LinkedHashMap<Integer, Tag>();

            RequestHeader header = new RequestHeader();
            header.put(RequestHeader.KEY_X_APPLICATION_ID, SmartSDKApplication.getProductId());

            Request request = new Request();
            request.setHeader(header);
            try {
                Response<GetTagListResponseBody> response = mAddressbook.getTagList(request);

                TagArray tagArray = response.getBody().getTags();
                for(int i=0; i<tagArray.size(); ++i) {
                    Tag tag = tagArray.get(i);
                    tagMap.put(tag.getTagId(), tag);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return tagMap;
        }

        @Override
        protected void onPostExecute(Map<Integer, Tag> result) {
            Button button;
            Tag tag;
            for (int i = 0; i < mTagButtons.size(); i++) {
                button = mTagButtons.get(i);
                tag = result.get((Integer)button.getTag());

                if (tag != null) {
                    button.setText(tag.getKeyDisplay());
                } else {
                    button.setText("");
                    button.setEnabled(false);
                }
            }

            mTagMap = result;
            mTagGroupFlipper.setVisibility(View.VISIBLE);

            changeTagId(1);
        }

    }

    /**
     * エントリ一覧を取得するタスクです。
     * The task to obtain entry list.
     */
    private class GetEntryListTask extends AsyncTask<String, Entry, List<Entry>> {

        @Override
        protected void onPreExecute() {
            mEntryList = new ArrayList<Entry>();
            mEntryListView.removeAllViewsInLayout();
        }

        @Override
        protected List<Entry> doInBackground(String... params) {
            ArrayList<Entry> entryList = new ArrayList<Entry>();

            RequestHeader header = new RequestHeader();
            header.put(RequestHeader.KEY_X_APPLICATION_ID, SmartSDKApplication.getProductId());

            Request request = new Request();
            request.setHeader(header);

            try {
                for (int i = 0; i < params.length; i++) {
                    if (isCancelled()) {
                        Log.d(TAG, "getEntry task aborted");
                        break;
                    }

                    String entryId = params[i];
                    Response<GetEntryResponseBody> response = mAddressbook.getEntry(request, entryId);

                    if (isCancelled()) {
                        Log.d(TAG, "getEntry task aborted");
                        break;
                    }

                    Entry entry = response.getBody();
                    if (mKind == DestinationKind.FOLDER) {
                        if (entry.getFolderData().getServerName() != null) {
                            entryList.add(entry);
                            publishProgress(entry);
                        }
                    } else if (mKind == DestinationKind.MAIL) {
                        if (entry.getMailData().getMailAddress() != null) {
                            entryList.add(entry);
                            publishProgress(entry);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return entryList;
        }

        @Override
        protected void onProgressUpdate(Entry... values) {
            mEntryList.add(values[0]);
            mEntryListAdapter.notifyDataSetChanged();
            showProgressBar(false);
        }

        @Override
        protected void onPostExecute(List<Entry> result) {
            mEntryListAdapter.notifyDataSetChanged();
            showProgressBar(false);
        }

    }

    /**
     * 宛先リストビューの要素を格納するクラスです。
     * This class stores the elements of destination list view.
     */
    private static class ViewHolder {
        ViewGroup container;
        TextView registNo;
        TextView keyDisplay;
    }

    /**
     * 宛先リストのアダプタークラスです。
     * Adapter class of destination list
     */
    private class EntryListAdapter extends BaseAdapter {

        private final LayoutInflater mInflater;

        EntryListAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_row_address_entry, null);
                holder.container = (ViewGroup) convertView.findViewById(R.id.address_container);
                holder.registNo = (TextView) convertView.findViewById(R.id.text_regist_no);
                holder.keyDisplay = (TextView) convertView.findViewById(R.id.text_key_display);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Entry entry = (Entry) getItem(position);
            holder.registNo.setText(String.format("%05d", entry.getRegistrationNumber()));
            holder.keyDisplay.setText(entry.getKeyDisplay());
            if (entry.getEntryId().equals(mSelectedEntryId)) {
                holder.container.setBackgroundResource(R.drawable.sim_bt_button_w);
                mSelectedKeyDisplay = entry.getKeyDisplay();
            } else {
                holder.container.setBackgroundResource(R.drawable.sim_bt_button_n);
            }

            return convertView;
        }

        @Override
        public int getCount() {
            return mEntryList.size();
        }

        @Override
        public Object getItem(int position) {
            return mEntryList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

    }

}