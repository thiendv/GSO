/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.function.common;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * SmartSDK上で動作するアプリケーションを示すクラスです。
 * SmartSDK上で動作するアプリを作成するときは、このクラスを継承します。
 * This class indicates the application which runs on SmartSDK.
 * This class is inherited when creating an application which runs on SmartSDK.
 */
public class SmartSDKApplication extends Application {

	private static String TAG = SmartSDKApplication.class.getSimpleName();

	private static Context mApplicationContext;
	
    /**
     *  ロック対象の電力モード：エンジンOFF状態
     */
    private static final int PROHIBITED_POWER_MODE = 4;

    /**
     * SDKServiceのパーミッション
     */
    private static final String APP_CMD_PERMISSION =
            "jp.co.ricoh.isdk.sdkservice.common.SdkService.APP_CMD_PERMISSION";

	/**
	 * プロダクトID
	 * productId
	 */
	private static String mProductId = null;

	/**
	 * 内部管理するレシーバーリスト
	 * ブロードキャストレシーバーの登録解除忘れのため、内部で登録されたレシーバーを管理します
	 * The receiver list managed internally
	 * Manages the receivers registered internally for the case of missing to clear broadcast receiver registration.
	 */
	private final List<BroadcastReceiver> receiverList = new ArrayList<BroadcastReceiver>();

	/**
	 * 起動直後にContextを参照した際に、nullを返す可能性があるため同期処理用ロック
	 * Lock synchronous processes since null may be returned at the time of checking Context immediately after startup
	 */
	private static final Object contextLock = new Object();


	@Override
	public void onCreate() {
		super.onCreate();

		synchronized (contextLock) {
			mApplicationContext = getApplicationContext();
			contextLock.notifyAll();
		}

		// obtain product id from the application tag in AndroidManifest
		try {
			ApplicationInfo appInfo = getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
			mProductId = String.valueOf(appInfo.metaData.getInt("productId",0));
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			mProductId = "";
		}
	}

	@Override
	public void onTerminate() {
		super.onTerminate();

		synchronized (this.receiverList){
			for(BroadcastReceiver receiver : receiverList) {
				this.unregisterReceiver(receiver);
			}
		}

	}

	@Override
	public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String permission, Handler schedule) {
		Intent retIntent = super.registerReceiver(receiver,filter,permission,schedule);

		synchronized (this.receiverList){
			this.receiverList.add(receiver);
		}

		return retIntent;
	}

	@Override
	public void unregisterReceiver(BroadcastReceiver receiver) {
		super.unregisterReceiver(receiver);
		synchronized (this.receiverList) {
			this.receiverList.remove(receiver);
		}
	}

	/**
	 * 自身のコンテキストへの参照を返します。
	 * Returns the reference to its own context.
	 * @return コンテキスト
	 *         context
	 */
	public static synchronized Context getContext(){
		synchronized (contextLock) {
		    // wait until onCreate() called (timeout: 5sec)
			if(mApplicationContext == null) {
				try{
					contextLock.wait(5000);
				}catch(InterruptedException ex){
					ex.printStackTrace();
				}
			}
		}
		return mApplicationContext;
	}

	public static String getProductId() {
		return mProductId;
	}

	private static String DISPLAY_ALERT_DIALOG = "jp.co.ricoh.isdk.sdkservice.panel.intent.AlertDialog.DISPLAY_ALERT_DIALOG";
	private static String UPDATE_ALERT_DIALOG = "jp.co.ricoh.isdk.sdkservice.panel.intent.AlertDialog.UPDATE_ALERT_DIALOG";
	private static String HIDE_ALERT_DIALOG = "jp.co.ricoh.isdk.sdkservice.panel.intent.AlertDialog.HIDE_ALERT_DIALOG";

	/**
	 * 警告ダイアログを表示します。
	 * アプリの状態とその理由を表示します。
	 * Displays warning dialog.
	 * Displays application state and its reason.
	 *
	 * @param appType アプリケーションの種別
	 *                Application type
	 * @param state アプリの状態
	 *              Application state
	 * @param reason 理由
	 *               reason
	 */
	public void displayAlertDialog(String appType, String state, String reason) {
		Intent intent = new Intent(DISPLAY_ALERT_DIALOG);
		intent.putExtra("PACKAGE_NAME", mApplicationContext.getPackageName());
		intent.putExtra("APP_TYPE", appType);
		intent.putExtra("STATE", state);
		intent.putExtra("STATE_REASON", reason);
		mApplicationContext.sendBroadcast(intent);
		Log.d(TAG, "DISPLAY_ALERT_DIALOG:" + intent.getExtras().getString("STATE_REASON"));
	}

	/**
	 * 警告ダイアログを更新します。
	 * Updates the warning dialog.
	 * @param appType アプリケーションの種別
	 *                Application type
	 * @param state アプリの状態
	 *              Application state
	 * @param reason 理由
	 *               reason
	 */
	public void updateAlertDialog(String appType, String state, String reason) {
		Intent intent = new Intent(UPDATE_ALERT_DIALOG);
		intent.putExtra("PACKAGE_NAME", mApplicationContext.getPackageName());
		intent.putExtra("APP_TYPE", appType);
		intent.putExtra("STATE", state);
		intent.putExtra("STATE_REASON", reason);
		mApplicationContext.sendBroadcast(intent);
		Log.d(TAG, "UPDATE_ALERT_DIALOG:" + intent.getExtras().getString("STATE_REASON"));
	}

	/**
	 * 警告ダイアログを非表示にします。
	 * Hides the warning dialog.
	 * @param appType アプリケーションの種別
	 *                Application type
	 * @param activityName アクティビティの名称
	 *                     Activity name
	 */
	public void hideAlertDialog(String appType, String activityName) {
		Intent intent = new Intent(HIDE_ALERT_DIALOG);
		intent.putExtra("PACKAGE_NAME", mApplicationContext.getPackageName());
		intent.putExtra("ACTIVITY_NAME", activityName);
		mApplicationContext.sendBroadcast(intent);
		Log.d(TAG, "HIDE_ALERT_DIALOG:" + intent.getExtras().getString("ACTIVITY_NAME"));
	}

    /**
     * 電力モード移行ロックします。 ロックするモードは低電力モードです。
     * 
     * @return true:成功 false:失敗
     */
    public Boolean lockPowerMode() {
        final String packageName = mApplicationContext.getPackageName();

        final Intent intent = new Intent();
        intent.setAction("jp.co.ricoh.isdk.sdkservice.system.PowerMode.LOCK_POWER_MODE");
        intent.putExtra("PACKAGE_NAME", packageName);
        intent.putExtra("POWER_MODE", PROHIBITED_POWER_MODE);

        Log.d(TAG,
                "lockPowerMode() PACKAGE_NAME=" + packageName + ", POWER_MODE="
                        + PROHIBITED_POWER_MODE);

        final Bundle resultExtra = syncExecSendOrderedBroadCast(intent);
        if (resultExtra == null) {
            Log.e(TAG, "LOCK_POWER_MODE request : No response.(timeout)");
            return false;
        }
        return (Boolean) resultExtra.get("RESULT");
    }

    /**
     * 電力モード移行ロックを解除します。
     * 
     * @return
     */
    public Boolean unlockPowerMode() {
        final String packageName = mApplicationContext.getPackageName();

        final Intent intent = new Intent();
        intent.setAction("jp.co.ricoh.isdk.sdkservice.system.PowerMode.UNLOCK_POWER_MODE");
        intent.putExtra("PACKAGE_NAME", packageName);
        intent.putExtra("POWER_MODE", PROHIBITED_POWER_MODE);

        Log.d(TAG,
                "unlockPowerMode() PACKAGE_NAME=" + packageName + ", POWER_MODE="
                        + PROHIBITED_POWER_MODE);

        mApplicationContext.sendBroadcast(intent, APP_CMD_PERMISSION);
        return true;
    }

    /**
     * オフライン移行ロックします。
     * 
     * @return true: 成功 false: 失敗
     */
    public Boolean lockOffline() {
        final String packageName = mApplicationContext.getPackageName();

        final Intent intent = new Intent();
        intent.setAction("jp.co.ricoh.isdk.sdkservice.system.OfflineManager.LOCK_OFFLINE");
        intent.putExtra("PACKAGE_NAME", packageName);

        Log.d(TAG,
                "lockOffline() PACKAGE_NAME=" + packageName);

        final Bundle resultExtra = syncExecSendOrderedBroadCast(intent);
        if (resultExtra == null) {
            Log.e(TAG, "LOCK_OFFLINE request : No response.(timeout)");
            return false;
        }
        return (Boolean) resultExtra.get("RESULT");
    }

    /**
     * オフライン移行ロックを解除します。
     * 
     * @return
     */
    public Boolean unlockOffline() {
        final String packageName = mApplicationContext.getPackageName();

        final Intent intent = new Intent();
        intent.setAction("jp.co.ricoh.isdk.sdkservice.system.OfflineManager.UNLOCK_OFFLINE");
        intent.putExtra("PACKAGE_NAME", packageName);

        Log.d(TAG,
                "unlockOffline() PACKAGE_NAME=" + packageName);

        mApplicationContext.sendBroadcast(intent, APP_CMD_PERMISSION);
        return true;
    }

    private Bundle syncExecSendOrderedBroadCast(Intent intent) {

        final SystemResultReceiver resultReceiver = new SystemResultReceiver();

        this.sendOrderedBroadcast(
                intent, // intent
                APP_CMD_PERMISSION, // permission
                resultReceiver, // receiver
                null, // scheduler
                0, // initialCode
                null, // initialData
                new Bundle()); // initialExtras

        return resultReceiver.getResultExtras();
    }

    /**
     * ブロードキャストの結果を受け取るレシーバーです。
     */
    private static class SystemResultReceiver extends BroadcastReceiver {
        private Bundle resultExtras = null;
        private Object lockObj = new Object();

        @Override
        public void onReceive(Context context, Intent intent) {
            synchronized (lockObj) {
                resultExtras = getResultExtras(true);
                lockObj.notifyAll();
            }
        }

        public Bundle getResultExtras() {
            synchronized (lockObj) {
                for (int i = 0; i < 5; i++) {
                    if (resultExtras != null) {
                        return resultExtras;
                    }

                    try {
                        lockObj.wait(1000);
                    } catch (InterruptedException ignore) {
                    }
                }
                return null;
            }
        }
    }
}
