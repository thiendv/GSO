/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.app.scan.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import jp.co.ricoh.ssdk.sample.app.scan.R;
import jp.co.ricoh.ssdk.sample.app.scan.application.DestinationSettingDataHolder;
import jp.co.ricoh.ssdk.sample.app.scan.application.ScanSampleApplication;
import jp.co.ricoh.ssdk.sample.app.scan.application.ScanSettingDataHolder;
import jp.co.ricoh.ssdk.sample.app.scan.application.ScanStateMachine;
import jp.co.ricoh.ssdk.sample.app.scan.application.ScanStateMachine.ScanEvent;
import jp.co.ricoh.ssdk.sample.function.common.impl.AsyncConnectState;
import jp.co.ricoh.ssdk.sample.function.scan.ScanImage;
import jp.co.ricoh.ssdk.sample.function.scan.ScanPDF;
import jp.co.ricoh.ssdk.sample.function.scan.ScanService;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.ScanServiceAttributeSet;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.AddressbookDestinationSetting;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.AddressbookDestinationSetting.DestinationKind;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.DestinationSettingItem;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.FtpAddressManualDestinationSetting;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.MailAddressManualDestinationSetting;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.NcpAddressManualDestinationSetting;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.OccuredErrorLevel;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.ScannerState;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.ScannerStateReason;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.ScannerStateReasons;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.SmbAddressManualDestinationSetting;
import jp.co.ricoh.ssdk.sample.function.scan.event.ScanServiceAttributeEvent;
import jp.co.ricoh.ssdk.sample.function.scan.event.ScanServiceAttributeListener;

import java.util.List;
import java.util.Set;

/**
 * ã‚¹ã‚­ãƒ£ãƒ³ã‚µãƒ³ãƒ—ãƒ«ã‚¢ãƒ—ãƒªã�®ãƒ¡ã‚¤ãƒ³ã‚¢ã‚¯ãƒ†ã‚£ãƒ“ãƒ†ã‚£ã�§ã�™ã€‚
 * Scan sample application: Main activity
 */
public class MainActivity extends Activity {

	private final static String TAG = MainActivity.class.getSimpleName();

    /**
     * ã‚¢ãƒ—ãƒªã‚±ãƒ¼ã‚·ãƒ§ãƒ³ã�®ç¨®åˆ¥
     * ã‚·ã‚¹ãƒ†ãƒ è­¦å‘Šãƒ€ã‚¤ã‚¢ãƒ­ã‚°ã�®è¨­å®šã�«ä½¿ç”¨ã�—ã�¾ã�™ã€‚
     * Application type
     * Used for setting system warning dialog.
     */
	private final static String ALERT_DIALOG_APP_TYPE_SCANNER = "SCANNER";

    /**
     * ã��ã�®ä»–ã�®è¨­å®šãƒ€ã‚¤ã‚¢ãƒ­ã‚°ã�®æ¨ªå¹…
     * Other Setting dialog width
     */
    private final int OTHER_SETTING_DIALOG_WIDTH = 500;

    /**
     * ã‚¹ã‚­ãƒ£ãƒ³ã‚µãƒ³ãƒ—ãƒ«ã‚¢ãƒ—ãƒªã‚±ãƒ¼ã‚·ãƒ§ãƒ³ã�®ã‚ªãƒ–ã‚¸ã‚§ã‚¯ãƒˆ
     * Application object
     */
	private ScanSampleApplication mApplication;

	/**
	 * è¨­å®šç”»é�¢ã�‹ã‚‰ã�®é€šçŸ¥ã‚’å�—ã�‘å�–ã‚‹ãƒ–ãƒ­ãƒ¼ãƒ‰ã‚­ãƒ£ã‚¹ãƒˆãƒ¬ã‚·ãƒ¼ãƒ�ãƒ¼
	 * Broadcast receiver to accept intents from setting dialog
	 */
    private BroadcastReceiver mReceiver;

    /**
     * èª­å�–ã‚«ãƒ©ãƒ¼è¨­å®šãƒœã‚¿ãƒ³
     * Scan color setting button
     */
	private Button mButtonColor;

	/**
	 * ãƒ•ã‚¡ã‚¤ãƒ«è¨­å®šãƒœã‚¿ãƒ³
	 * File setting button
	 */
	private Button mButtonFileSetting;

	/**
	 * ã‚¹ã‚­ãƒ£ãƒ³é�¢è¨­å®šãƒœã‚¿ãƒ³
	 * Scan side setting button
	 */
	private Button mButtonSide;

	/**
	 * å®›å…ˆè¨­å®šãƒœã‚¿ãƒ³
	 * Destination setting button
	 */
    private Button mButtonDestination;

    /**
     * ã��ã�®ä»–è¨­å®šãƒœã‚¿ãƒ³
     * Other setting button
     */
	private LinearLayout mButtonOther;

	/**
	 * èª­å�–é–‹å§‹ãƒœã‚¿ãƒ³
	 * Scan start button
	 */
    private RelativeLayout mButtonStart;

    /**
     * ã‚¹ã‚­ãƒ£ãƒ³ã‚µãƒ¼ãƒ“ã‚¹çŠ¶æ…‹ãƒªã‚¹ãƒŠãƒ¼
     * Scan service attribute listener
     */
    private ScanServiceAttributeListener mScanServiceAttrListener;

    /**
     * ã‚¹ã‚­ãƒ£ãƒ³è¨­å®š
     * Scan setting
     */
    private ScanSettingDataHolder mScanSettingDataHolder;

    /**
     * å®›å…ˆè¨­å®š
     * Destination Setting
     */
    private DestinationSettingDataHolder mDestSettingDataHolder;

    /**
     * ã‚¹ãƒ†ãƒ¼ãƒˆãƒžã‚·ãƒ³
     * State machine
     */
    private ScanStateMachine mStateMachine;

    /**
     * ã‚¹ã‚­ãƒ£ãƒ³ã‚µãƒ¼ãƒ“ã‚¹ã�¨æŽ¥ç¶šã�™ã‚‹ã‚¿ã‚¹ã‚¯
     * Task to connect with scan service
     */
    private ScanServiceInitTask mScanServiceInitTask;

    /**
     * ã‚¹ã‚­ãƒ£ãƒ³ã‚µãƒ¼ãƒ“ã‚¹çŠ¶æ…‹è¡¨ç¤ºãƒ©ãƒ™ãƒ«
     * Scan service state display label
     */
    private TextView text_state;

    /**
     * ã‚·ã‚¹ãƒ†ãƒ è­¦å‘Šç”»é�¢ã�Œè¡¨ç¤ºã�•ã‚Œã�¦ã�„ã‚‹ã�‹ã�®ãƒ•ãƒ©ã‚°
     * Flag to indicate if system warning screen is displayed
     */
    private volatile boolean mAlertDialogDisplayed = false;

    /**
     * ç�¾åœ¨ç™ºç”Ÿã�—ã�¦ã�„ã‚‹ã‚¨ãƒ©ãƒ¼ã�®ã‚¨ãƒ©ãƒ¼ãƒ¬ãƒ™ãƒ«
     * Level of the currently occurring error
     */
    private OccuredErrorLevel mLastErrorLevel = null;

    /**
     * ã‚·ã‚¹ãƒ†ãƒ è­¦å‘Šç”»é�¢è¡¨ç¤ºã‚¿ã‚¹ã‚¯
     * Asynchronous task to request to display system warning screen
     */
    private AlertDialogDisplayTask mAlertDialogDisplayTask = null;

    /**
     * ãƒ¡ã‚¤ãƒ³ã‚¢ã‚¯ãƒ†ã‚£ãƒ“ãƒ†ã‚£èµ·å‹•æ¸ˆã�¿ãƒ•ãƒ©ã‚°
     * trueã�§ã�‚ã‚Œã�°ã€�ã�™ã�§ã�«MainActivityã�Œèµ·å‹•æ¸ˆã�¿ã�§ã�™ã€‚
     * MainActivity running flag
     * If true, another Mainactivity instance is running.
     */
    private boolean mMultipleRunning = false;

    /**
     * ã‚¢ã‚¯ãƒ†ã‚£ãƒ“ãƒ†ã‚£ã�Œç”Ÿæˆ�ã�•ã‚Œã‚‹ã�¨å‘¼ã�³å‡ºã�•ã‚Œã�¾ã�™ã€‚
     * [å‡¦ç�†å†…å®¹]
     *   (1)ã‚¢ãƒ—ãƒªã‚±ãƒ¼ã‚·ãƒ§ãƒ³ã�®åˆ�æœŸåŒ–
     *   (2)è¨­å®šãƒ–ãƒ­ãƒ¼ãƒ‰ã‚­ãƒ£ã‚¹ãƒˆãƒ¬ã‚·ãƒ¼ãƒ�ãƒ¼ã�®è¨­å®š
     *   (3)å®›å…ˆæŒ‡å®šãƒœã‚¿ãƒ³ã�®è¨­å®š
     *   (4)èª­ã�¿å�–ã‚Šã‚«ãƒ©ãƒ¼é�¸æŠžãƒœã‚¿ãƒ³ã�®è¨­å®š
     *   (5)ãƒ•ã‚¡ã‚¤ãƒ«å½¢å¼�é�¸æŠžãƒœã‚¿ãƒ³ã�®è¨­å®š
     *   (6)åŽŸç¨¿é�¢é�¸æŠžãƒœã‚¿ãƒ³ã�®è¨­å®š
     *   (7)ã��ã�®ä»–ã�®è¨­å®šãƒœã‚¿ãƒ³ã�®è¨­å®š
     *   (8)èª­å�–é–‹å§‹ãƒœã‚¿ãƒ³ã�®è¨­å®š
     *   (9)å�„ãƒœã‚¿ãƒ³ã�®ç„¡åŠ¹åŒ–
     *   (10)ãƒªã‚¹ãƒŠãƒ¼åˆ�æœŸåŒ–
     *
     * Called when an activity is created.
     * [Processes]
     *   (1) Initialize application
     *   (2) Set setting broadcast receiver
     *   (3) Set destination setting button
     *   (4) Set scan color setting button
     *   (5) Set file setting button
     *   (6) Set scan side setting button
     *   (7) Set other settings button
     *   (8) Set start button
     *   (9) Disable buttons
     *   (10) Initialize listener
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		setContentView(R.layout.activity_main);

		if (getNumActivities(getPackageName()) > 1) {
		    Log.i(TAG, "Another MainActivity instance is already running.");
		    mMultipleRunning = true;
		    finish();
		    return;
		}

		//(1)
		mApplication = (ScanSampleApplication) getApplication();
        mScanServiceAttrListener = new ScanServiceAttributeListenerImpl(new Handler());
        mScanSettingDataHolder = mApplication.getScanSettingDataHolder();
        mDestSettingDataHolder = mApplication.getDestinationSettingDataHolder();
        mStateMachine = mApplication.getStateMachine();
        mStateMachine.registActivity(this);
        text_state = (TextView)findViewById(R.id.text_state);

        //(2)
        IntentFilter filter = new IntentFilter();
        filter.addAction(DialogUtil.INTENT_ACTION_SUB_ACTIVITY_RESUMED);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DialogUtil.INTENT_ACTION_SUB_ACTIVITY_RESUMED.equals(action)) {
                    startAlertDialogDisplayTask();
                }
            }
        };
        registerReceiver(mReceiver, filter);

        //(3)
		mButtonDestination = (Button)findViewById(R.id.btn_destination);;
		mButtonDestination.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
                AlertDialog dialog = DialogUtil.createDestTypeDialog(MainActivity.this, mDestSettingDataHolder);
                DialogUtil.showDialog(dialog);
			}
		});

		//(4)
		mButtonColor = (Button)findViewById(R.id.btn_color);
		mButtonColor.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
                AlertDialog dialog = DialogUtil.createColorSettingDialog(MainActivity.this, mScanSettingDataHolder);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener(){
                    @Override
                    public void onDismiss(DialogInterface paramDialogInterface) {
                        String label = getResources().getString(mScanSettingDataHolder.getSelectedColorLabel());
                        mButtonColor.setText(label);
                    }
                });
                DialogUtil.showDialog(dialog);
			}
		});

		//(5)
		mButtonFileSetting = (Button)findViewById(R.id.btn_file);
		mButtonFileSetting.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
                AlertDialog dialog = DialogUtil.createFileSettingDialog(MainActivity.this, mScanSettingDataHolder);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener(){
                    @Override
                    public void onDismiss(DialogInterface paramDialogInterface) {
                        String label = getResources().getString(mScanSettingDataHolder.getSelectedFileSettingLabel());
                        mButtonFileSetting.setText(label);
                    }
                });
                DialogUtil.showDialog(dialog);
			}
		});

		//(6)
		mButtonSide = (Button)findViewById(R.id.btn_side);
		mButtonSide.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
                AlertDialog dialog = DialogUtil.createSideSettingDialog(MainActivity.this, mScanSettingDataHolder);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener(){
                    @Override
                    public void onDismiss(DialogInterface paramDialogInterface) {
                        String label = getResources().getString(mScanSettingDataHolder.getSelectedSideLabel());
                        mButtonSide.setText(label);
                    }
                });
                DialogUtil.showDialog(dialog);
			}
		});

		//(7)
        mButtonOther = (LinearLayout)findViewById(R.id.btn_other);
        mButtonOther.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog dialog = DialogUtil.createOtherSettingDialog(MainActivity.this, mScanSettingDataHolder);
                DialogUtil.showDialog(dialog, OTHER_SETTING_DIALOG_WIDTH, DialogUtil.DEFAULT_DIALOG_HEIGHT);
            }
        });

		//(8)
		mButtonStart = (RelativeLayout)findViewById(R.id.btn_start);
		mButtonStart.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
                mStateMachine.procScanEvent(ScanEvent.REQUEST_JOB_START);
			}
		});

		//(9)
        disableSettingKey();
        disableStartKey();

        //(10)
        if( mScanServiceInitTask != null ) {
            mScanServiceInitTask.cancel(false);
        }
        mScanServiceInitTask = new ScanServiceInitTask();
        mScanServiceInitTask.execute();

        // send event
        mStateMachine.procScanEvent(ScanEvent.ACTIVITY_CREATED);
	}

    /**
     * é�·ç§»å…ˆã�®ã‚¢ã‚¯ãƒ†ã‚£ãƒ“ãƒ†ã‚£ã�‹ã‚‰ã�®çµ�æžœã‚’å�—ã�‘å�–ã‚Šã�¾ã�™ã€‚
     * [å‡¦ç�†å†…å®¹]
     *   (1)ãƒ—ãƒ¬ãƒ“ãƒ¥ãƒ¼ã�‹ã‚‰æˆ»ã�£ã�¦ã��ã�Ÿå ´å�ˆã€�çµ�æžœã�«å¿œã�˜ã�¦ã‚¹ãƒ†ãƒ¼ãƒˆãƒžã‚·ãƒ³ã�«ã‚¤ãƒ™ãƒ³ãƒˆã‚’é€�ä¿¡ã�—ã�¾ã�™ã€‚
     *   (2)å®›å…ˆè¨­å®šã�‹ã‚‰æˆ»ã�£ã�¦ã��ã�Ÿå ´å�ˆã�¯ã€�çµ�æžœã�«å¿œã�˜ã�¦å®›å…ˆè¡¨ç¤ºæ¬„ã�®è¡¨ç¤ºã‚’æ›´æ–°ã�—ã�¾ã�™ã€‚
     *
     * Receive the result from the activity of the changed state.
     * [Processes]
     *   (1) When returned from preview, sends event to the state machine accordingly to the result.
     *   (2) When returned from destination setting, updates the display of the destination display area accordingly to the result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        //(1)
        if(requestCode == PreviewActivity.REQUEST_CODE_PREVIEW_ACTIVITY) {
            if(resultCode == RESULT_OK){
                mStateMachine.procScanEvent(ScanEvent.REQUEST_JOB_END);
            } else if(resultCode == RESULT_CANCELED){
                mStateMachine.procScanEvent(ScanEvent.REQUEST_JOB_CANCEL);
            } else {
                /* do nothing */
            }
        }
        //(2)
        else if (requestCode == AddressActivity.REQUEST_CODE_ADDRESS_ACTIVITY) {
            if(resultCode == RESULT_OK){
                String keyDisplay = intent.getStringExtra(AddressActivity.KEY_DISPLAY);
                updateDestinationLabel(keyDisplay);
            } else if(resultCode == RESULT_CANCELED){
                /* do nothing */
            } else {
                /* do nothing */
            }
        } else {
            /* do nothing */
        }
    }

    /**
     * ã‚¢ã‚¯ãƒ†ã‚£ãƒ“ãƒ†ã‚£ã�®å†�é–‹æ™‚ã�«å‘¼ã�³å‡ºã�•ã‚Œã�¾ã�™ã€‚
     * ã‚¨ãƒ©ãƒ¼ã�®ç™ºç”Ÿæœ‰ç„¡ã‚’é�žå�ŒæœŸã�§æ¤œæŸ»ã�—ã€�å¿…è¦�ã�§ã�‚ã‚Œã�°ã‚·ã‚¹ãƒ†ãƒ è­¦å‘Šç”»é�¢åˆ‡æ›¿ã�ˆã�¾ã�™ã€‚
     * Called when the activity is resumed.
     * Checks error occurrence asynchronously and switches to a system warning screen if necessary.
     */
    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        if (mMultipleRunning) {
            return;
        }

        startAlertDialogDisplayTask();
    }

    /**
     * ã‚¢ã‚¯ãƒ†ã‚£ãƒ“ãƒ†ã‚£ã�®å�œæ­¢æ™‚ã�«å‘¼ã�³å‡ºã�•ã‚Œã�¾ã�™ã€‚
     * ã‚·ã‚¹ãƒ†ãƒ è­¦å‘Šç”»é�¢è¡¨ç¤ºã‚¿ã‚¹ã‚¯ã�Œå®Ÿè¡Œä¸­ã�§ã�‚ã‚Œã�°ã€�ã‚­ãƒ£ãƒ³ã‚»ãƒ«ã�—ã�¾ã�™ã€‚
     * Called when the activity is stopped.
     * If the system warning screen display task is in process, the task is cancelled.
     */
    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();

        stopAlertDialogDisplayTask();
    }

    /**
     * ã‚¢ã‚¯ãƒ†ã‚£ãƒ“ãƒ†ã‚£ã�Œç ´æ£„ã�•ã‚Œã‚‹éš›ã�«å‘¼ã�³å‡ºã�•ã‚Œã�¾ã�™ã€‚
     * [å‡¦ç�†å†…å®¹]
     *   (1)ãƒ¡ã‚¤ãƒ³ã‚¢ã‚¯ãƒ†ã‚£ãƒ“ãƒ†ã‚£çµ‚äº†ã‚¤ãƒ™ãƒ³ãƒˆã‚’ã‚¹ãƒ†ãƒ¼ãƒˆãƒžã‚·ãƒ³ã�«é€�ã‚‹
     *      èª­å�–ä¸­ã�§ã�‚ã‚Œã�°ã€�èª­å�–ã�Œã‚­ãƒ£ãƒ³ã‚»ãƒ«ã�•ã‚Œã�¾ã�™ã€‚
     *   (2)ã‚µãƒ¼ãƒ“ã‚¹ã�‹ã‚‰ã‚¤ãƒ™ãƒ³ãƒˆãƒªã‚¹ãƒŠãƒ¼ã�¨ãƒ–ãƒ­ãƒ¼ãƒ‰ã‚­ãƒ£ã‚¹ãƒˆãƒ¬ã‚·ãƒ¼ãƒ�ãƒ¼ã‚’é™¤åŽ»ã�™ã‚‹
     *   (3)é�žå�ŒæœŸã‚¿ã‚¹ã‚¯ã�Œå®Ÿè¡Œä¸­ã� ã�£ã�Ÿå ´å�ˆã€�ã‚­ãƒ£ãƒ³ã‚»ãƒ«ã�™ã‚‹
     *   (4)ã‚¢ãƒ—ãƒªã‚±ãƒ¼ã‚·ãƒ§ãƒ³ã�®ä¿�æŒ�ãƒ‡ãƒ¼ã‚¿ã‚’åˆ�æœŸåŒ–ã�™ã‚‹
     *   (5)å�‚ç…§ã‚’ç ´æ£„ã�™ã‚‹
     *
     * Called when the activity is destroyed.
     * [Processes]
     *   (1) Send MainActivity destoyed event to the state machine.
     *       If scanning is in process, scanning is cancelled.
     *   (2) Removes the event listener and the broadcast receiver from the service.
     *   (3) If asynchronous task is in process, the task is cancelled.
     *   (4) Initializes the data saved to the application.
     *   (5) Discards references
     */
    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();

        // if MainActivity another instance is already running, then exit without doing anything
        if (mMultipleRunning) {
            return;
        }

        //(1)
        mStateMachine.procScanEvent(ScanEvent.ACTIVITY_DESTROYED);

        //(2)
        ScanService scanService = mApplication.getScanService();
        try {
            scanService.removeScanServiceAttributeListener(mScanServiceAttrListener);
        } catch (IllegalStateException e){
            /* the listener is not registered. */
        }
        unregisterReceiver(mReceiver);

        //(3)
        stopAlertDialogDisplayTask();
        if( mScanServiceInitTask != null ) {
            mScanServiceInitTask.cancel(false);
            mScanServiceInitTask = null;
        }

        //(4)
        mApplication.init();

        //(5)
        mApplication = null;
        mReceiver = null;
        mButtonColor = null;
        mButtonFileSetting = null;
        mButtonSide = null;
        mButtonDestination = null;
        mButtonOther = null;
        mButtonStart = null;
        mScanSettingDataHolder = null;
        mDestSettingDataHolder = null;
        mStateMachine = null;
        mScanServiceAttrListener = null;
    }

    /**
     * å®›å…ˆè¡¨ç¤ºãƒ©ãƒ™ãƒ«ã‚’æ›´æ–°ã�—ã�¾ã�™ã€‚
     * [è¡¨ç¤ºã�®å½¢å¼�]
     *   (1)ãƒ•ã‚©ãƒ«ãƒ€ï¼ˆç›´æŽ¥å…¥åŠ›ï¼‰
     *      - ãƒ•ã‚©ãƒ«ãƒ€ã‚¢ã‚¤ã‚³ãƒ³ ï¼‹ ãƒ•ã‚©ãƒ«ãƒ€ãƒ‘ã‚¹ + ä»»æ„�æ–‡å­—åˆ—
     *   (2)ãƒ¡ãƒ¼ãƒ«ï¼ˆç›´æŽ¥å…¥åŠ›ï¼‰
     *      - ãƒ¡ãƒ¼ãƒ«ã‚¢ã‚¤ã‚³ãƒ³ + ãƒ¡ãƒ¼ãƒ«ã‚¢ãƒ‰ãƒ¬ã‚¹ + ä»»æ„�æ–‡å­—åˆ—
     *   (3)ãƒ•ã‚©ãƒ«ãƒ€ï¼ˆã‚¢ãƒ‰ãƒ¬ã‚¹å¸³é�¸æŠžï¼‰
     *      - ãƒ•ã‚©ãƒ«ãƒ€ã‚¢ã‚¤ã‚³ãƒ³ + ä»»æ„�æ–‡å­—åˆ—
     *   (4)ãƒ¡ãƒ¼ãƒ«ï¼ˆã‚¢ãƒ‰ãƒ¬ã‚¹å¸³é�¸æŠžï¼‰
     *      - ãƒ¡ãƒ¼ãƒ«ã‚¢ã‚¤ã‚³ãƒ³ + ä»»æ„�æ–‡å­—åˆ—
     *
     * Updates the destination display label.
     * This method must be called from UI thread.
     * [Display format]
     *   (1) For email: address book
     *     - icon (folder) + folder path + string
     *   (2) For email: manual entry
     *     - icon (mail) + email path + string
     *   (3) For folder: address book
     *     - icon (icon) + string
     *   (4) For folder: manual entry
     *     - icon (mail) + string
     *
     * @param optStr : ä»»æ„�æ–‡å­—åˆ— Specified string
     */
	public void updateDestinationLabel(String optStr) {

	    DestinationSettingDataHolder destHolder = mApplication.getDestinationSettingDataHolder();
	    DestinationSettingItem destItem = destHolder.getDestinationSettingItem();

	    DestinationKind destKind = null;
	    String str = null;

	    enableStartKey();

	    //(1)
	    if (destItem instanceof FtpAddressManualDestinationSetting ) {

            FtpAddressManualDestinationSetting item = (FtpAddressManualDestinationSetting)destItem;
            destKind = DestinationKind.FOLDER;
            str = item.getPath();

        } else if (destItem instanceof SmbAddressManualDestinationSetting) {

            SmbAddressManualDestinationSetting item = (SmbAddressManualDestinationSetting)destItem;
            destKind = DestinationKind.FOLDER;
            str = item.getPath();

        } else if (destItem instanceof NcpAddressManualDestinationSetting) {

            NcpAddressManualDestinationSetting item = (NcpAddressManualDestinationSetting)destItem;
            destKind = DestinationKind.FOLDER;
            str = item.getPath();

        }
        //(2)
        else if (destItem instanceof MailAddressManualDestinationSetting) {

            MailAddressManualDestinationSetting item = (MailAddressManualDestinationSetting)destItem;
            destKind = DestinationKind.MAIL;
            str = item.getMailAddress();

        }
        //(3)(4)
        else if (destItem instanceof AddressbookDestinationSetting) {

            AddressbookDestinationSetting item = (AddressbookDestinationSetting)destItem;
            destKind = item.getDestinationKind();
            str = "";

        } else {
            disableStartKey();
            /* do nothing */
        }

        if(optStr!=null) {
            str = str + " " + optStr;
        }
        updateDestinationLabel(destKind, str);
	}

	/**
	 * å®›å…ˆè¡¨ç¤ºãƒ©ãƒ™ãƒ«ã‚’æ›´æ–°ã�—ã�¾ã�™ã€‚
	 * è¡¨ç¤ºå½¢å¼�ã�¯ ã‚¢ã‚¤ã‚³ãƒ³(ãƒ•ã‚©ãƒ«ãƒ€/ãƒ¡ãƒ¼ãƒ«ï¼‰ï¼‹ æ–‡å­—åˆ— ã�§ã�™ã€‚
	 *  Updates the destination display label.
	 *  Display format is "icon (folder/email) + string".
	 *
	 * @param destKind
	 * @param str
	 */
	private void updateDestinationLabel(DestinationKind destKind, String str) {
        final TextView text_dest_title = (TextView)findViewById(R.id.text_dest_title);
        final ImageView img_dest_icon = (ImageView)findViewById(R.id.img_dest_icon);

        if (destKind == DestinationKind.FOLDER) {

            img_dest_icon.setImageResource(R.drawable.icon_folder_small);
            text_dest_title.setText(str);
            img_dest_icon.setVisibility(View.VISIBLE);
            text_dest_title.setVisibility(View.VISIBLE);

        } else if (destKind == DestinationKind.MAIL) {

            img_dest_icon.setImageResource(R.drawable.icon_mail_small);
            text_dest_title.setText(str);
            img_dest_icon.setVisibility(View.VISIBLE);
            text_dest_title.setVisibility(View.VISIBLE);

        } else {
            img_dest_icon.setVisibility(View.INVISIBLE);
            text_dest_title.setVisibility(View.INVISIBLE);
        }
	}

    /**
     * ã‚·ã‚¹ãƒ†ãƒ è­¦å‘Šç”»é�¢è¡¨ç¤ºè¦�æ±‚ã�«æ¸¡ã�™çŠ¶æ…‹æ–‡å­—åˆ—ã‚’ç”Ÿæˆ�ã�—ã�¾ã�™ã€‚
     * Creates the state string to be passed to system warning screen display request.
     *
     * @param state ã‚¹ã‚­ãƒ£ãƒ³ã‚µãƒ¼ãƒ“ã‚¹çŠ¶æ…‹
     *              State of scan service
     * @return çŠ¶æ…‹æ–‡å­—åˆ—
     *         State string
     */
    private String makeAlertStateString(ScannerState state) {
        String stateString = "";
        if (state != null) {
            stateString = state.toString();
        }
        return stateString;
    }

    /**
     * ã‚·ã‚¹ãƒ†ãƒ è­¦å‘Šç”»é�¢è¡¨ç¤ºè¦�æ±‚ã�«æ¸¡ã�™çŠ¶æ…‹ç�†ç”±æ–‡å­—åˆ—ã‚’ç”Ÿæˆ�ã�—ã�¾ã�™ã€‚
     * è¤‡æ•°ã�®çŠ¶æ…‹ç�†ç”±ã�Œã�‚ã�£ã�Ÿå ´å�ˆã€�1ã�¤ç›®ã�®çŠ¶æ…‹ç�†ç”±ã�®ã�¿ã‚’æ¸¡ã�—ã�¾ã�™ã€‚
     * Creates the state reason string to be passed to the system warning screen display request.
     * If multiple state reasons exist, only the first state reason is passed.
     *
     * @param stateReasons ã‚¹ã‚­ãƒ£ãƒŠã‚µãƒ¼ãƒ“ã‚¹çŠ¶æ…‹ç�†ç”±
     *                     Scan service state reason
     * @return çŠ¶æ…‹ç�†ç”±æ–‡å­—åˆ—
     *         State reason string
     */
   private String makeAlertStateReasonString(ScannerStateReasons stateReasons) {
        String reasonString = "";
        if (stateReasons != null) {
            Object[] reasonArray = stateReasons.getReasons().toArray();
            if (reasonArray != null && reasonArray.length > 0) {
                reasonString = reasonArray[0].toString();
            }
        }
        return reasonString;
    }

    /**
     * æŒ‡å®šã�•ã‚Œã�Ÿã‚¢ãƒ—ãƒªã‚±ãƒ¼ã‚·ãƒ§ãƒ³ã�Œãƒ•ã‚©ã‚¢ã‚°ãƒ©ãƒ³ãƒ‰çŠ¶æ…‹ã�«ã�‚ã‚‹ã�‹ã‚’å�–å¾—ã�—ã�¾ã�™ã€‚
     * Obtains whether or not the specified application is in the foreground state.
     *
     * @param packageName ã‚¢ãƒ—ãƒªã‚±ãƒ¼ã‚·ãƒ§ãƒ³ã�®ãƒ‘ãƒƒã‚±ãƒ¼ã‚¸å��
     *                    Application package name
     * @return ãƒ•ã‚©ã‚¢ã‚°ãƒ©ã‚¦ãƒ³ãƒ‰çŠ¶æ…‹ã�«ã�‚ã‚‹å ´å�ˆã�«true
     *         If the application is in the foreground state, true is returned.
     */
    private boolean isForegroundApp(String packageName) {
        boolean result = false;
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
        for (RunningAppProcessInfo info : list) {
            if (packageName.equals(info.processName)) {
                result = (info.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND);
                break;
            }
        }
        return result;
    }

    /**
     * æŒ‡å®šã�•ã‚Œã�Ÿã‚¢ãƒ—ãƒªã‚±ãƒ¼ã‚·ãƒ§ãƒ³ã�®ã‚¢ã‚¯ãƒ†ã‚£ãƒ“ãƒ†ã‚£ã‚¹ã‚¿ãƒƒã‚¯ã�®æœ€ä¸Šä½�ã‚¯ãƒ©ã‚¹ã‚’å�–å¾—ã�—ã�¾ã�™ã€‚
     * Obtains the top class in the activity stack of the specified application.
     *
     * @param packageName ã‚¢ãƒ—ãƒªã‚±ãƒ¼ã‚·ãƒ§ãƒ³ã�®ãƒ‘ãƒƒã‚±ãƒ¼ã‚¸å��
     *                    Application package name
     * @return æœ€ä¸Šä½�ã‚¯ãƒ©ã‚¹ã�®FQCNã‚¯ãƒ©ã‚¹å��. å�–å¾—ã�§ã��ã�ªã�„å ´å�ˆã�¯null
     *         The name of the FQCN class name of the top class. If the name cannot be obtained, null is returned.
     */
    private String getTopActivityClassName(String packageName) {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> list = am.getRunningTasks(30);
        for (RunningTaskInfo info : list) {
            if (packageName.equals(info.topActivity.getPackageName())) {
                return info.topActivity.getClassName();
            }
        }
        return null;
    }

    /**
     * æŒ‡å®šã�•ã‚Œã�Ÿã‚¢ãƒ—ãƒªã‚±ãƒ¼ã‚·ãƒ§ãƒ³ã�®ã‚¢ã‚¯ãƒ†ã‚£ãƒ“ãƒ†ã‚£ã‚¹ã‚¿ãƒƒã‚¯å†…ã�®ã‚¢ã‚¯ãƒ†ã‚£ãƒ“ãƒ†ã‚£æ•°ã‚’å�–å¾—ã�—ã�¾ã�™ã€‚
     * Obtains the number of activities in the activity stack of the specified application.
     *
     * @param packageName ã‚¢ãƒ—ãƒªã‚±ãƒ¼ã‚·ãƒ§ãƒ³ã�®ãƒ‘ãƒƒã‚±ãƒ¼ã‚¸å��
     *                    Application package name
     * @return ã‚¢ã‚¯ãƒ†ã‚£ãƒ“ãƒ†ã‚£æ•°. å�–å¾—ã�§ã��ã�ªã�„å ´å�ˆã�¯0
     *         The number of activitys. If the number cannot be obtained, 0 is returned.
     */
    private int getNumActivities(String packageName) {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> list = am.getRunningTasks(30);
        for (RunningTaskInfo info : list) {
            if (packageName.equals(info.topActivity.getPackageName())) {
                return info.numActivities;
            }
        }
        return 0;
    }

    public void onJobCompleted() {
        Log.d(TAG, "onJobCompleted");
        final ScanPDF scanPDF = new ScanPDF(mApplication.getScanJob());
       
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                final String filePath = scanPDF.getImageFilePath();
                Log.d(TAG, "file path: " + filePath);
                return null;
            }
        }.execute();
    }


    /**
     * ã‚¹ã‚­ãƒ£ãƒŠã‚µãƒ¼ãƒ“ã‚¹ã�®çŠ¶æ…‹å¤‰æ›´ç›£è¦–ãƒªã‚¹ãƒŠãƒ¼ã�§ã�™ã€‚
     * [å‡¦ç�†å†…å®¹]
     *   (1)ã‚¹ã‚­ãƒ£ãƒ³ã‚µãƒ¼ãƒ“ã‚¹ã�®çŠ¶æ…‹ã�«ã‚ˆã�£ã�¦ã€�ã‚¹ã‚­ãƒ£ãƒ³ã‚µãƒ¼ãƒ“ã‚¹çŠ¶æ…‹è¡¨ç¤ºãƒ©ãƒ™ãƒ«ã‚’æ›¸ã��æ�›ã�ˆã�¾ã�™ã€‚
     *   (2)ã‚¨ãƒ©ãƒ¼ç”»é�¢ã�®è¡¨ç¤ºãƒ»æ›´æ–°ãƒ»é�žè¡¨ç¤ºè¦�æ±‚ã‚’è¡Œã�„ã�¾ã�™ã€‚
     * The listener class to monitor scan service attribute changes.
     * [Processes]
     *   (1) Rewrites the scan service state display label accordingly to the scan service state.
     *   (2) Requests to display/update/hide error screens.
     */
    class ScanServiceAttributeListenerImpl implements ScanServiceAttributeListener {

        /**
         * UI thread handler
         */
        private Handler mHandler;

        ScanServiceAttributeListenerImpl(Handler handler) {
            mHandler = handler;
        }

        @Override
        public void attributeUpdate(final ScanServiceAttributeEvent event) {
            ScannerState state = (ScannerState)event.getAttributes().get(ScannerState.class);
            ScannerStateReasons stateReasons = (ScannerStateReasons)event.getAttributes().get(ScannerStateReasons.class);
            OccuredErrorLevel errorLevel = (OccuredErrorLevel) event.getAttributes().get(OccuredErrorLevel.class);

            String stateLabel = "";

            //(1)
            switch(state) {
            case IDLE :
                Log.d(TAG, "ScannerState : IDLE");
                stateLabel = getString(R.string.txid_scan_t_state_ready);
                break;
            case MAINTENANCE:
                Log.d(TAG, "ScannerState : MAINTENANCE");
                stateLabel = getString(R.string.txid_scan_t_state_maintenance);
                break;
            case PROCESSING:
                Log.d(TAG, "ScannerState : PROCESSING");
                stateLabel = getString(R.string.txid_scan_t_state_scanning);
                break;
            case STOPPED:
                Log.d(TAG, "ScannerState : STOPPED");
                stateLabel = getString(R.string.txid_scan_t_state_stopped);
                break;
            case UNKNOWN:
                Log.d(TAG, "ScannerState : UNKNOWN");
                stateLabel = getString(R.string.txid_scan_t_state_unknown);
                break;
            default:
                Log.d(TAG, "ScannerState : never reach here ...");
                /* never reach here */
                break;
            }

            if( stateReasons != null ) {
                Set<ScannerStateReason> reasonSet = stateReasons.getReasons();
                for(ScannerStateReason reason : reasonSet) {
                    switch(reason) {
                        case COVER_OPEN:
                            stateLabel = getString(R.string.txid_scan_t_state_reason_cover_open);
                            break;
                        case MEDIA_JAM:
                            stateLabel = getString(R.string.txid_scan_t_state_reason_media_jam);
                            break;
                        case PAUSED:
                            stateLabel = getString(R.string.txid_scan_t_state_reason_paused);
                            break;
                        case OTHER:
                            stateLabel = getString(R.string.txid_scan_t_state_reason_other);
                            break;
                        default:
                            /* never reach here */
                            break;
                    }
                }
            }

            final String result = stateLabel;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    text_state.setText(result);
                }
            });

            //(2)
            if (OccuredErrorLevel.ERROR.equals(errorLevel)
                    || OccuredErrorLevel.FATAL_ERROR.equals(errorLevel)) {

                String stateString = makeAlertStateString(state);
                String reasonString = makeAlertStateReasonString(stateReasons);

                if (mLastErrorLevel == null) {
                    // Normal -> Error
                    if (isForegroundApp(getPackageName())) {
                        mApplication.displayAlertDialog(ALERT_DIALOG_APP_TYPE_SCANNER, stateString, reasonString);
                        mAlertDialogDisplayed = true;
                    }
                } else {
                    // Error -> Error
                    if (mAlertDialogDisplayed) {
                        mApplication.updateAlertDialog(ALERT_DIALOG_APP_TYPE_SCANNER, stateString, reasonString);
                    }
                }
                mLastErrorLevel = errorLevel;

            } else {
                if (mLastErrorLevel != null) {
                    // Error -> Normal
                    if (mAlertDialogDisplayed) {
                        String activityName = getTopActivityClassName(getPackageName());
                        if (activityName == null) {
                            activityName = MainActivity.class.getName();
                        }
                        mApplication.hideAlertDialog(ALERT_DIALOG_APP_TYPE_SCANNER, activityName);
                        mAlertDialogDisplayed = false;
                    }
                }
                mLastErrorLevel = null;
            }
        }
    }

    /**
     * é�žå�ŒæœŸã�§ã‚¹ã‚­ãƒ£ãƒ³ã‚µãƒ¼ãƒ“ã‚¹ã�¨ã�®æŽ¥ç¶šã‚’è¡Œã�„ã�¾ã�™ã€‚
     * [å‡¦ç�†å†…å®¹]
     *   (1)ã‚¹ã‚­ãƒ£ãƒ³ã‚µãƒ¼ãƒ“ã‚¹ã�®ã‚¤ãƒ™ãƒ³ãƒˆã‚’å�—ä¿¡ã�™ã‚‹ãƒªã‚¹ãƒŠãƒ¼ã‚’è¨­å®šã�—ã�¾ã�™ã€‚
     *      æ©Ÿå™¨ã�Œåˆ©ç”¨å�¯èƒ½ã�«ã�ªã‚‹ã�‹ã€�ã‚­ãƒ£ãƒ³ã‚»ãƒ«ã�ŒæŠ¼ã�•ã‚Œã‚‹ã�¾ã�§ãƒªãƒˆãƒ©ã‚¤ã�—ã�¾ã�™ã€‚
     *   (2)é�žå�ŒæœŸã‚¤ãƒ™ãƒ³ãƒˆã�®æŽ¥ç¶šç¢ºèª�ã‚’è¡Œã�„ã�¾ã�™ã€‚
     *      æŽ¥ç¶šå�¯èƒ½ã�«ã�ªã‚‹ã�‹ã€�ã‚­ãƒ£ãƒ³ã‚»ãƒ«ã�ŒæŠ¼ã�•ã‚Œã‚‹ã�¾ã�§ãƒªãƒˆãƒ©ã‚¤ã�—ã�¾ã�™ã€‚
     *   (3)æŽ¥ç¶šã�«æˆ�åŠŸã�—ã�Ÿå ´å�ˆã�¯ã€�ã‚¹ã‚­ãƒ£ãƒ³ã‚µãƒ¼ãƒ“ã‚¹ã�‹ã‚‰å�„è¨­å®šã�®è¨­å®šå�¯èƒ½å€¤ã‚’å�–å¾—ã�—ã�¾ã�™ã€‚
     *
     * Connects with the scan service asynchronously.
     * [Processes]
     *   (1) Sets the listener to receive scan service events.
     *       This task repeats until the machine becomes available or cancel button is touched.
     *   (2) Confirms the asynchronous connection.
     *       This task repeats until the connection is confirmed or cancel button is touched.
     *   (3) After the machine becomes available and connection is confirmed,
     *       obtains job setting values.
     */
    class ScanServiceInitTask extends AsyncTask<Void, Void, Integer> {

        AsyncConnectState addListenerResult = null;
        AsyncConnectState getAsyncConnectStateResult = null;

        @Override
        protected Integer doInBackground(Void... params) {

            final ScanService scanService = mApplication.getScanService();

            //(1)
            while (true) {
                if(isCancelled()) {
                    return -1;
                }
                addListenerResult = scanService.addScanServiceAttributeListener(mScanServiceAttrListener);

                if (addListenerResult == null) {
                    sleep(100);
                    continue;
                }

                if (addListenerResult.getState() == AsyncConnectState.STATE.CONNECTED) {
                    break;
                }

                if (addListenerResult.getErrorCode() == AsyncConnectState.ERROR_CODE.NO_ERROR){
                    // do nothing
                } else if (addListenerResult.getErrorCode() == AsyncConnectState.ERROR_CODE.BUSY) {
                    sleep(10000);
                } else if (addListenerResult.getErrorCode() == AsyncConnectState.ERROR_CODE.TIMEOUT){
                    // do nothing
                } else if (addListenerResult.getErrorCode() == AsyncConnectState.ERROR_CODE.INVALID){
                    return 0;
                } else {
                    // unknown state
                    return 0;
                }
            }

            if (addListenerResult.getState() != AsyncConnectState.STATE.CONNECTED) {
                return 0;
            }

            //(2)
            while (true) {
                if(isCancelled()) {
                    return -1;
                }
                getAsyncConnectStateResult = scanService.getAsyncConnectState();

                if (getAsyncConnectStateResult == null) {
                    sleep(100);
                    continue;
                }

                if (getAsyncConnectStateResult.getState() == AsyncConnectState.STATE.CONNECTED) {
                    break;
                }

                if (getAsyncConnectStateResult.getErrorCode() == AsyncConnectState.ERROR_CODE.NO_ERROR){
                    // do nothing
                } else if (getAsyncConnectStateResult.getErrorCode() == AsyncConnectState.ERROR_CODE.BUSY) {
                    sleep(10000);
                } else if (getAsyncConnectStateResult.getErrorCode() == AsyncConnectState.ERROR_CODE.TIMEOUT){
                    // do nothing
                } else if (getAsyncConnectStateResult.getErrorCode() == AsyncConnectState.ERROR_CODE.INVALID){
                    return 0;
                } else {
                    // unknown state
                    return 0;
                }
            }

            //(3)
            if (addListenerResult.getState() == AsyncConnectState.STATE.CONNECTED
                    && getAsyncConnectStateResult.getState() == AsyncConnectState.STATE.CONNECTED) {
                mScanSettingDataHolder.init(scanService);
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (addListenerResult==null) {
                Log.d(TAG, "addScanServiceAttributeListener:null");
            } else {
                Log.d(TAG, "addScanServiceAttributeListener:" + addListenerResult.getState() + "," + addListenerResult.getErrorCode());
            }
            if (getAsyncConnectStateResult==null) {
                Log.d(TAG, "getAsyncConnectState:null");
            } else {
                Log.d(TAG, "getAsyncConnectState:" + getAsyncConnectStateResult.getState() + "," + getAsyncConnectStateResult.getErrorCode());
            }

            if (result!=0) {
                /* canceled. */
                return;
            }

            if (addListenerResult.getState() == AsyncConnectState.STATE.CONNECTED
                    && getAsyncConnectStateResult.getState() == AsyncConnectState.STATE.CONNECTED) {
                // connection succeeded.
                mButtonColor.setText(mScanSettingDataHolder.getSelectedColorLabel());
                mButtonFileSetting.setText(mScanSettingDataHolder.getSelectedFileSettingLabel());
                mButtonSide.setText(mScanSettingDataHolder.getSelectedSideLabel());
                enableSettingKey();
                mStateMachine.procScanEvent(ScanEvent.ACTIVITY_BOOT_COMPLETED);
            }
            else {
                // the connection is invalid.
                mStateMachine.procScanEvent(ScanEvent.ACTIVITY_BOOT_FAILED);
            }
        }

        /**
         * æŒ‡å®šã�•ã‚Œã�Ÿæ™‚é–“ã‚«ãƒ¬ãƒ³ãƒˆã‚¹ãƒ¬ãƒƒãƒ‰ã‚’ã‚¹ãƒªãƒ¼ãƒ—ã�—ã�¾ã�™ã€‚
         * sleep for the whole of the specified interval
         */
        private void sleep(long time) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                Log.w(TAG, "", e);
            }
        }
    }

    /**
     * èª­å�–é–‹å§‹ãƒœã‚¿ãƒ³ã‚’æœ‰åŠ¹åŒ–ã�—ã�¾ã�™ã€‚
     * Enables the start button.
     */
    private void enableStartKey() {
        mButtonStart.setEnabled(true);
    }

    /**
     * èª­å�–é–‹å§‹ãƒœã‚¿ãƒ³ã‚’ç„¡åŠ¹åŒ–ã�—ã�¾ã�™ã€‚
     * Disables the start button.
     */
    private void disableStartKey() {
//        mButtonStart.setEnabled(false);
    }

    /**
     * å�„è¨­å®šãƒœã‚¿ãƒ³ã‚’æœ‰åŠ¹åŒ–ã�—ã�¾ã�™ã€‚
     * Enables setting buttons.
     */
    private void enableSettingKey() {
        mButtonColor.setEnabled(true);
        mButtonFileSetting.setEnabled(true);
        mButtonSide.setEnabled(true);
        mButtonDestination.setEnabled(true);
        mButtonOther.setEnabled(true);
    }

    /**
     * å�„è¨­å®šãƒœã‚¿ãƒ³ã‚’ç„¡åŠ¹åŒ–ã�—ã�¾ã�™ã€‚
     * Disables setting buttons.
     */
    private void disableSettingKey() {
        mButtonColor.setEnabled(false);
        mButtonFileSetting.setEnabled(false);
        mButtonSide.setEnabled(false);
        mButtonDestination.setEnabled(false);
        mButtonOther.setEnabled(false);
    }

    /**
     * ã‚·ã‚¹ãƒ†ãƒ è­¦å‘Šç”»é�¢è¡¨ç¤ºã‚¿ã‚¹ã‚¯ã‚’é–‹å§‹ã�—ã�¾ã�™ã€‚
     * Starts the alert dialog display task.
     */
    private void startAlertDialogDisplayTask() {
        if (mAlertDialogDisplayTask != null) {
            mAlertDialogDisplayTask.cancel(false);
        }
        mAlertDialogDisplayTask = new AlertDialogDisplayTask();
        mAlertDialogDisplayTask.execute();
    }

    /**
     * ã‚·ã‚¹ãƒ†ãƒ è­¦å‘Šç”»é�¢è¡¨ç¤ºã‚¿ã‚¹ã‚¯ã‚’ã‚­ãƒ£ãƒ³ã‚»ãƒ«ã�—ã�¾ã�™ã€‚
     * Stop the alert dialog display task.
     */
    private void stopAlertDialogDisplayTask() {
        if (mAlertDialogDisplayTask != null) {
            mAlertDialogDisplayTask.cancel(false);
            mAlertDialogDisplayTask = null;
        }
    }

    /**
     * ã‚·ã‚¹ãƒ†ãƒ è­¦å‘Šç”»é�¢ã�®è¡¨ç¤ºæœ‰ç„¡ã‚’åˆ¤æ–­ã�—ã€�å¿…è¦�ã�ªå ´å�ˆã�¯è¡¨ç¤ºè¦�æ±‚ã‚’è¡Œã�†é�žå�ŒæœŸã‚¿ã‚¹ã‚¯ã�§ã�™ã€‚
     * The asynchronous task to judge to display system warning screen and to request to display the screen if necessary.
     */
    class AlertDialogDisplayTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ScanServiceAttributeSet attributes = mApplication.getScanService().getAttributes();
            OccuredErrorLevel errorLevel = (OccuredErrorLevel) attributes.get(OccuredErrorLevel.class);

            if (OccuredErrorLevel.ERROR.equals(errorLevel)
                    || OccuredErrorLevel.FATAL_ERROR.equals(errorLevel)) {
                ScannerState state = (ScannerState) attributes.get(ScannerState.class);
                ScannerStateReasons stateReasons = (ScannerStateReasons) attributes.get(ScannerStateReasons.class);

                String stateString = makeAlertStateString(state);
                String reasonString = makeAlertStateReasonString(stateReasons);
                if (isCancelled()) {
                    return null;
                }

                mApplication.displayAlertDialog(ALERT_DIALOG_APP_TYPE_SCANNER, stateString, reasonString);

                mAlertDialogDisplayed = true;
                mLastErrorLevel = errorLevel;
            }
            return null;
        }

    }

}
