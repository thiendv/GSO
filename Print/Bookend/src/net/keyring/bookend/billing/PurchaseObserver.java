// Copyright 2010 Google Inc. All Rights Reserved.

package net.keyring.bookend.billing;


import java.lang.reflect.Method;

import net.keyring.bookend.Logput;
import net.keyring.bookend.Consts.PurchaseState;
import net.keyring.bookend.Consts.ResponseCode;
import net.keyring.bookend.service.BillingService;
import net.keyring.bookend.service.BillingService.RequestPurchase;
import net.keyring.bookend.service.BillingService.RestoreTransactions;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Handler;

/**
 * 購入に関連した変更を観測するための抽象クラス<br>
 * An interface for observing changes related to purchases. The main application
 * extends this class and registers an instance of that derived class with
 * {@link ResponseHandler}. The main application implements the callbacks
 * {@link #onBillingSupported(boolean)} and
 * {@link #onPurchaseStateChange(PurchaseState, String, int, long)}.  These methods
 * are used to update the UI.
 */
public abstract class PurchaseObserver {
    private final Activity mActivity;
    private final Handler mHandler;
    private Method mStartIntentSender;
    private Object[] mStartIntentSenderArgs = new Object[5];
    private static final Class[] START_INTENT_SENDER_SIG = new Class[] {
        IntentSender.class, Intent.class, int.class, int.class, int.class
    };

    public PurchaseObserver(Activity activity, Handler handler) {
    	Logput.d("- Activity :" + activity.getClass().getName());
        mActivity = activity;
        mHandler = handler;
        initCompatibilityLayer();
    }

    /**
     * Androidマーケットが応答するときに呼び出されるコールバック<br>
     * This is the callback that is invoked when Android Market responds to the
     * {@link BillingService#checkBillingSupported()} request.
     * @param supported true if in-app billing is supported.
     */
    public abstract void onBillingSupported(boolean supported);

    /**
     * アイテムが購入、払い戻し、またはキャンセルされたときに呼び出されるコールバック<br>
     * 呼び出した{@ link＃BillingService requestPurchase（文字列）}に応じて呼び出されるコールバックです。<br><br>
     * 
     * This is the callback that is invoked when an item is purchased,
     * refunded, or canceled.  It is the callback invoked in response to
     * calling {@link BillingService#requestPurchase(String)}.  It may also
     * be invoked asynchronously when a purchase is made on another device
     * (if the purchase was for a Market-managed item), or if the purchase
     * was refunded, or the charge was canceled.  This handles the UI
     * update.  The database update is handled in
     * {@link ResponseHandler#purchaseResponse(Context, PurchaseState,
     * String, String, long)}.
     * @param purchaseState the purchase state of the item
     * @param itemId a string identifying the item (the "SKU")
     * @param quantity the current quantity of this item after the purchase
     * @param purchaseTime the time the product was purchased, in
     * milliseconds since the epoch (Jan 1, 1970)
     */
    public abstract void onPurchaseStateChange(PurchaseState purchaseState,
            String itemId, long purchaseTime, String developerPayload);

    /**
     * 
     * RequestPurchase要求に対して市場からの応答コードを受信したときに呼び出されます<br>
     * 
     * This is called when we receive a response code from Market for a
     * RequestPurchase request that we made.  This is NOT used for any
     * purchase state changes.  All purchase state changes are received in
     * {@link #onPurchaseStateChange(PurchaseState, String, int, long)}.
     * This is used for reporting various errors, or if the user backed out
     * and didn't purchase the item.  The possible response codes are:
     *   RESULT_OK means that the order was sent successfully to the server.
     *       The onPurchaseStateChange() will be invoked later (with a
     *       purchase state of PURCHASED or CANCELED) when the order is
     *       charged or canceled.  This response code can also happen if an
     *       order for a Market-managed item was already sent to the server.
     *   RESULT_USER_CANCELED means that the user didn't buy the item.
     *   RESULT_SERVICE_UNAVAILABLE means that we couldn't connect to the
     *       Android Market server (for example if the data connection is down).
     *   RESULT_BILLING_UNAVAILABLE means that in-app billing is not
     *       supported yet.
     *   RESULT_ITEM_UNAVAILABLE means that the item this app offered for
     *       sale does not exist (or is not published) in the server-side
     *       catalog.
     *   RESULT_ERROR is used for any other errors (such as a server error).
     */
    public abstract void onRequestPurchaseResponse(RequestPurchase request,
            ResponseCode responseCode);

    /**
     * RestoreTransactions要求に対してはAndroidマーケットから取得してレスポンスコードを受信したときに呼び出されます<br>
     * 
     * This is called when we receive a response code from Android Market for a
     * RestoreTransactions request that we made.  A response code of
     * RESULT_OK means that the request was successfully sent to the server.
     */
    public abstract void onRestoreTransactionsResponse(RestoreTransactions request,
            ResponseCode responseCode);

    private void initCompatibilityLayer() {
    	try{
            mStartIntentSender = mActivity.getClass().getMethod("startIntentSender",
                    START_INTENT_SENDER_SIG);
        } catch (SecurityException e) {
            mStartIntentSender = null;
        } catch (NoSuchMethodException e) {
            mStartIntentSender = null;
        }
    }

    /**
     * 販売ページを開く
     * @param pendingIntent
     * @param intent
     */
    void startBuyPageActivity(PendingIntent pendingIntent, Intent intent) {
        if (mStartIntentSender != null) {
        	// Android2.0以降
        	// アプリ内購入ページのアクティビティは、アプリケーションのアクティビティスタック上にある必要があります。
            // This is on Android 2.0 and beyond.  The in-app buy page activity
            // must be on the activity stack of the application.
            try {
                // This implements the method call:
                // mActivity.startIntentSender(pendingIntent.getIntentSender(),
                //     intent, 0, 0, 0);
                mStartIntentSenderArgs[0] = pendingIntent.getIntentSender();
                mStartIntentSenderArgs[1] = intent;
                mStartIntentSenderArgs[2] = Integer.valueOf(0);
                mStartIntentSenderArgs[3] = Integer.valueOf(0);
                mStartIntentSenderArgs[4] = Integer.valueOf(0);
                Logput.d("mStartIntentSender.getName() = " + mStartIntentSender.getName());
                mStartIntentSender.invoke(mActivity, mStartIntentSenderArgs);
            } catch (Exception e) {
                Logput.e("error starting activity", e);
            }
        } else {
        	// Android1.6以上
            // This is on Android version 1.6. The in-app buy page activity must be on its
            // own separate activity stack instead of on the activity stack of
            // the application.
            try {
            	Logput.d("send");
                pendingIntent.send(mActivity, 0 /* code */, intent);
            } catch (CanceledException e) {
                Logput.e("error starting activity", e);
            }
        }
    }

    /**
     * UI更新
     * 
     * Updates the UI after the database has been updated.  This method runs
     * in a background thread so it has to post a Runnable to run on the UI
     * thread.
     * @param purchaseState the purchase state of the item
     * @param itemId a string identifying the item
     */
    void postPurchaseStateChange(final PurchaseState purchaseState, final String itemId,
            final long purchaseTime, final String developerPayload) {
        mHandler.post(new Runnable() {
            @Override
			public void run() {
                onPurchaseStateChange(
                        purchaseState, itemId, purchaseTime, developerPayload);
            }
        });
    }
}
