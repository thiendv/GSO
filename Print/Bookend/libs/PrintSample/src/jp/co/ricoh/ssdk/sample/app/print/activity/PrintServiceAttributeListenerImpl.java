/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.app.print.activity;

import android.os.Handler;
import android.widget.TextView;

import jp.co.ricoh.ssdk.sample.app.print.R;
import jp.co.ricoh.ssdk.sample.function.print.attribute.PrintServiceAttributeSet;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.PrinterState;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.PrinterStateReasons;
import jp.co.ricoh.ssdk.sample.function.print.event.PrintServiceAttributeEvent;
import jp.co.ricoh.ssdk.sample.function.print.event.PrintServiceAttributeListener;

/**
 * プリントサービスがポストするイベントを受け取るリスナークラスです。
 * The listener class to monitor scan service attribute changes.
 */
class PrintServiceAttributeListenerImpl implements PrintServiceAttributeListener {

    /**
     * メインアクテビティへの参照
     * Reference to MainActivity
     */
    MainActivity mActivity;

    /**
     * UIスレッドのハンドラ
     * UI thread handler
     */
    Handler mHandler;

    /**
     * プリントサービスから受け取ったイベント
     * Event received from print service
     */
    PrintServiceAttributeEvent mEvent;

    public PrintServiceAttributeListenerImpl(MainActivity activity, Handler handler){
        mActivity = activity;
        mHandler = handler;
    }

    /**
     * プリントサービスからイベントを受け取ったときに呼び出されるメソッドです。
     * The method called when receive an event from the print service.
     *
     * @param event
     */
    @Override
    public void attributeUpdate(PrintServiceAttributeEvent event) {
        mEvent = event;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                PrintServiceAttributeSet attributes = mEvent.getAttributes();
                TextView statusView = (TextView)mActivity.findViewById(R.id.txt_state);
                StringBuilder statusString = new StringBuilder();

                PrinterState state = (PrinterState)attributes.get(PrinterState.class);
                if(state != null) {
                    switch(state){
                        case IDLE:
                            statusString.append(mActivity.getResources().getString(R.string.printer_status_idle));
                            break;
                        case MAINTENANCE:
                            statusString.append(mActivity.getResources().getString(R.string.printer_status_maintenance));
                            break;
                        case PROCESSING:
                            statusString.append(mActivity.getResources().getString(R.string.printer_status_processing));
                            break;
                        case STOPPED:
                            statusString.append(mActivity.getResources().getString(R.string.printer_status_stopped));
                            break;
                        case UNKNOWN:
                            statusString.append(mActivity.getResources().getString(R.string.printer_status_unknown));
                            break;
                    }
                }

                PrinterStateReasons reasons = (PrinterStateReasons)attributes.get(PrinterStateReasons.class);
                if(reasons != null) {
                    statusString.append("：");
                    statusString.append(reasons.getReasons());
                }

                statusView.setText(statusString.toString());

            }
        });

    }
}