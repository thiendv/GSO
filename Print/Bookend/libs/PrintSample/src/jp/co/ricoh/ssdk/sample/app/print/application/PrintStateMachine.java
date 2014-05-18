/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.app.print.application;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import jp.co.ricoh.ssdk.sample.app.print.R;
import jp.co.ricoh.ssdk.sample.app.print.activity.DialogUtil;
import jp.co.ricoh.ssdk.sample.function.print.PrintFile;
import jp.co.ricoh.ssdk.sample.function.print.PrintJob;
import jp.co.ricoh.ssdk.sample.function.print.attribute.PrintException;
import jp.co.ricoh.ssdk.sample.function.print.attribute.PrintRequestAttributeSet;
import jp.co.ricoh.ssdk.sample.function.print.attribute.PrintResponseException;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.PrintJobPrintingInfo;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.PrinterStateReason;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.PrinterStateReasons;

import java.util.Map;

/**
 * プリントサンプルのジョブステートマシン
 *  起動時及びジョブ発行後の以下のダイアログの生成・表示・破棄はすべてこのステートマシンを通して実行します
 * ・お待ちくださいダイアログ
 * ・印刷中ダイアログ
 *
 * Job state machine of print sample application.
 * Display/update/hide processes of the following dialog/screen are always executed by this statemachine.
 *   - Please wait dialog
 *   - Printing dialog
 */
public class PrintStateMachine {

    private static Handler mHandler;
    private static PrintSampleApplication mApplication;
    private static Context mContext;
    private static ProgressDialog mProgressDialog;
    private static ProgressDialog mPleaseWaitDialog;
    private static AlertDialog mBootFailedDialog;
    private static PrintJob currentPrintJob;

    PrintStateMachine(PrintSampleApplication application, Handler handler) {
        mApplication = application;
        mHandler = handler;
        mContext = null;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public void removeContext() {
        mContext = null;
    }

    /**
     * 状態遷移するためのイベント
     * State transition event.
     */
    public enum PrintEvent {
        CHANGE_APP_ACTIVITY_INITIAL,
        CHANGE_APP_ACTIVITY_STARTED,
        CHANGE_APP_ACTIVITY_START_FAILED,
        CHANGE_APP_ACTIVITY_DESTROYED,
        CHANGE_JOB_STATE_INITIAL,
        CHANGE_JOB_STATE_PRE_PROCESS,
        CHANGE_JOB_STATE_PRE_PENDING,
        CHANGE_JOB_STATE_PENDING,
        CHANGE_JOB_STATE_PROCESSING,
        CHANGE_JOB_STATE_PROCESSING_STOPPED,
        CHANGE_JOB_STATE_COMPLETED,
        CHANGE_JOB_STATE_ABORTED,
        CHANGE_JOB_STATE_CANCELED,
        REQUEST_JOB_CANCEL,
    }

    /**
     * 現在のステートマシンの状態
     * Current state of the state machine
     */
    private State mState = State.STATE_APP_INITIAL;

    /**
     * アプリの状態を示します。
     * アプリの状態に応じた処理を定義します。
     * State definition.
     */
    public enum State {
        /**
         * 初期状態
         * Initial state
         */
        STATE_APP_INITIAL {
            @Override
            public State getNextState(PrintEvent event, Object param) {
                switch (event) {
                    case CHANGE_APP_ACTIVITY_INITIAL:
                        showPleaseWaitDialog(mContext);
                        return STATE_APP_INITIAL;
                    case CHANGE_APP_ACTIVITY_STARTED:
                        closePleaseWaitDialog();
                        return STATE_JOB_INITIAL;
                    case CHANGE_APP_ACTIVITY_START_FAILED:
                        closePleaseWaitDialog();
                        showBootFailedDialog();
                        return STATE_APP_INITIAL;
                    default:
                        return super.getNextState(event,param);
                }
            }
        },
        /**
         * ジョブ初期状態
         * Job initial state
         */
        STATE_JOB_INITIAL {
            @Override
            public State getNextState(PrintEvent event, Object param) {
                switch (event) {
                    case CHANGE_JOB_STATE_PRE_PROCESS:
                        if (param == null)
                            return STATE_JOB_INITIAL;
                        if (!(param instanceof PrintSettingDataHolder))
                            return STATE_JOB_INITIAL;

                        new StartPrintJobTask().execute((PrintSettingDataHolder) param);

                        return STATE_JOB_PRE_PROCESS;

                    case CHANGE_JOB_STATE_INITIAL:
                        return STATE_JOB_INITIAL;
                    default:
                        return super.getNextState(event, param);
                }
            }
        },

        /**
         * 印刷前の属性設定状態
         * The state before the job is started after attribute set.
         */
        STATE_JOB_PRE_PROCESS {
            @Override
            public State getNextState(PrintEvent event, Object param) {
                switch ( event) {
                    case CHANGE_JOB_STATE_PRE_PENDING :
                        return STATE_JOB_PRE_PENDING;

                    case CHANGE_JOB_STATE_INITIAL:
                        //ジョブ発行に失敗した場合
                        //failed to start job
                        closePrintingDialog();
                        return STATE_JOB_INITIAL;
                    default:
                        return super.getNextState(event, param);
                }
            }

            @Override
            public void entry(Object... params) {
                //印刷開始ダイアログの表示
                //show the print start dialog
                showPrintingDialog(mContext);
                setPrintingDialogCancelable(false);
            }
        },

        /**
         * ジョブ実行後の状態
         * The state after the job is started.
         */
        STATE_JOB_PRE_PENDING {
            @Override
            public State getNextState(PrintEvent event, Object param) {
                switch (event) {
                    case CHANGE_JOB_STATE_PENDING:
                        return STATE_JOB_PENDING;
                    case CHANGE_JOB_STATE_PROCESSING:
                        return STATE_JOB_PROCESSING;

                    case CHANGE_APP_ACTIVITY_DESTROYED:
                        return WAITING_JOB_CANCEL;
                    default:
                        return super.getNextState(event, param);
                }
            }

        },
        /**
         * ジョブ待機中状態
         * Job pending
         */
        STATE_JOB_PENDING {
            @Override
            public State getNextState(PrintEvent event, Object param) {
                switch (event) {
                    case CHANGE_JOB_STATE_PROCESSING:
                        return STATE_JOB_PROCESSING;
                    case REQUEST_JOB_CANCEL:
                        return WAITING_JOB_CANCEL;
                    case CHANGE_JOB_STATE_PROCESSING_STOPPED:
                        return STATE_JOB_PROCESSING_STOPPED;

                    case CHANGE_APP_ACTIVITY_DESTROYED:
                        return WAITING_JOB_CANCEL;
                    default:
                        return super.getNextState(event, param);
                }
            }
        },
        /**
         * ジョブ実行中状態
         * Job processing
         */
        STATE_JOB_PROCESSING {
            @Override
            public State getNextState(PrintEvent event, Object param) {
                switch (event) {
                    case CHANGE_JOB_STATE_ABORTED:
                        return STATE_JOB_ABORTED;

                    case CHANGE_JOB_STATE_CANCELED:
                        return STATE_JOB_CANCELED;

                    case CHANGE_JOB_STATE_COMPLETED:
                        return STATE_JOB_COMPLETED;

                    case CHANGE_JOB_STATE_PROCESSING:
                        return STATE_JOB_PROCESSING;

                    case REQUEST_JOB_CANCEL:
                        return WAITING_JOB_CANCEL;

                    case CHANGE_JOB_STATE_PROCESSING_STOPPED:
                        return STATE_JOB_PROCESSING_STOPPED;

                    case CHANGE_APP_ACTIVITY_DESTROYED:
                        return WAITING_JOB_CANCEL;

                    default:
                        return super.getNextState(event, param);
                }
            }

            @Override
            public void entry(Object... params) {
                // Show print start dialog
                if (params[0] instanceof PrintJobPrintingInfo) {
                    PrintJobPrintingInfo printingInfo = (PrintJobPrintingInfo) params[0];
                    if (printingInfo == null)
                        return;

                    String printedMessage = String.format(
                            mContext.getResources().getString(
                                    R.string.dlg_printing_message_printing),
                            printingInfo.getPrintedCount());

                    updatePrintingDialog(mContext, printedMessage);
                    setPrintingDialogCancelable(true);
                }
            }
        },
        /**
         * ジョブ一時停止中状態*
         * Job pausing
         */
        STATE_JOB_PROCESSING_STOPPED {
            @Override
            public State getNextState(PrintEvent event, Object param) {
                switch (event) {
                    case CHANGE_JOB_STATE_ABORTED:
                        return STATE_JOB_ABORTED;

                    case CHANGE_JOB_STATE_CANCELED:
                        return STATE_JOB_CANCELED;

                    case CHANGE_JOB_STATE_COMPLETED:
                        return STATE_JOB_COMPLETED;

                    case CHANGE_JOB_STATE_PROCESSING:
                        return STATE_JOB_PROCESSING;

                    case REQUEST_JOB_CANCEL:
                        return WAITING_JOB_CANCEL;
                    case CHANGE_APP_ACTIVITY_DESTROYED:
                        return WAITING_JOB_CANCEL;

                    default:
                        return super.getNextState(event, param);
                }
            }

            @Override
            public void entry(Object... params) {
                if (params[0] == null)
                    return;

                PrinterStateReasons reasons = (PrinterStateReasons) params[0];
                StringBuilder sb = new StringBuilder();
                for (PrinterStateReason reason : reasons.getReasons()) {
                    sb.append(reason.toString());
                    sb.append("\n");
                }

                // Update job pausing dialog
                updatePrintingDialog(
                        mContext,
                        mContext.getResources().getString(
                                R.string.dlg_printing_message_printing_stopped) + "\n"
                                + sb.toString());
            }
        },

        /**
         * ジョブキャンセル待ち
         * Job waiting to be canceled
         */
        WAITING_JOB_CANCEL {
            @Override
            public State getNextState(PrintEvent event, Object param) {
                switch (event) {
                    case CHANGE_JOB_STATE_INITIAL:
                        return STATE_JOB_INITIAL;
                    case CHANGE_JOB_STATE_CANCELED:
                        return STATE_JOB_CANCELED;
                    case CHANGE_JOB_STATE_ABORTED:
                        return STATE_JOB_ABORTED;
                    case CHANGE_JOB_STATE_PROCESSING:
                        return STATE_JOB_PROCESSING;
                    case CHANGE_JOB_STATE_COMPLETED:
                        return STATE_JOB_COMPLETED;
                    default:
                        return super.getNextState(event, param);
                }
            }

            @Override
            public void entry(Object... params) {
                new CancelPrintJobTask().execute();
            }

        },

        /**
         * ジョブ完了状態
         * Job completed
         */
        STATE_JOB_COMPLETED {
            @Override
            public State getNextState(PrintEvent event, Object param) {
                switch (event) {
                    case CHANGE_JOB_STATE_INITIAL:
                        return STATE_JOB_INITIAL;

                    default:
                        return super.getNextState(event, param);
                }
            }

            @Override
            public void entry(Object... params) {
                unLockMode();
                // close printing dialog
                closePrintingDialog();
            }
        },
        /**
         * ジョブ中断状態
         * Job aborted
         */
        STATE_JOB_ABORTED {
            @Override
            public State getNextState(PrintEvent event, Object param) {
                switch (event) {
                    case CHANGE_JOB_STATE_INITIAL:
                        return STATE_JOB_INITIAL;

                    default:
                        return super.getNextState(event, param);
                }
            }

            @Override
            public void entry(Object... params) {
                unLockMode();
                // close printing dialog
                closePrintingDialog();

                // show toast message with aborted reason
                String message = "job aborted.";
                if ((params != null) && (params.length > 0)) {
                    if (params[0] instanceof PrinterStateReasons) {
                        PrinterStateReasons reasons = (PrinterStateReasons) params[0];
                        StringBuilder sb = new StringBuilder();
                        sb.append(message);
                        sb.append(System.getProperty("line.separator"));
                        sb.append(reasons.getReasons().toString());
                        message = sb.toString();
                    }
                }
                showToastMessage(mContext, message);
            }
        },
        /**
         * ジョブキャンセル状態
         * Job canceled
         */
        STATE_JOB_CANCELED {
            @Override
            public State getNextState(PrintEvent event, Object param) {
                switch (event) {
                    case CHANGE_JOB_STATE_INITIAL:
                        return STATE_JOB_INITIAL;

                    default:
                        return super.getNextState(event, param);
                }
            }

            @Override
            public void entry(Object... params) {
                unLockMode();
                // close printing dialog
                closePrintingDialog();
            }
        };

        /******************************************************************
         * Stateの共通処理
         * 呼び出し順番は、getNextState()=>exit()=>entry()になります。
         *
         * Common methods.
         * These methods are called in the following order.
         * ・getNextState()
         * ・exit()
         * ・entry()
         ******************************************************************/

        /**
         * 状態遷移
         * Obtains the next methods.
         */
        public State getNextState(final PrintEvent event, final Object param) {
            switch (event) {
                default:
                    return null;
            }
        }

        /**
         * 入場メソッド
         * Entry method
         */
        public void entry(final Object... params) {

        }

        /**
         * 退場メソッド
         * Exit method
         */
        public void exit(final Object... params) {

        }
    }

    private static void unLockMode() {
        /* 省エネロック解除 */
        mApplication.unlockPowerMode();
        /* オフラインロック解除 */
        mApplication.unlockOffline();
    }

    /**
     * 状態遷移を行います。
     * Changes states.
     */
    public void procPrintEvent(final PrintEvent event) {
        procPrintEvent(event, null);
    }

    /**
     * 状態遷移を行います。
     * Changes states.
     */
    public void procPrintEvent(final PrintEvent event, final Object prm) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                State newState = mState.getNextState(event, prm);
                if (newState != null) {
                    Log.i(getClass().getSimpleName(), "#evtp :" + event + " state:" + mState
                            + " > " + newState);
                    mState.exit(prm);
                    mState = newState;
                    mState.entry(prm);
                }
            }
        });
    }

    /******************************************************************
     * アクションメソッド Action method.
     ******************************************************************/

    /**
     * PleaseWait画面の表示 Displays please wait dialog
     */
    private static void showPleaseWaitDialog(Context context) {
        mPleaseWaitDialog = new ProgressDialog(context);
        mPleaseWaitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mPleaseWaitDialog
                .setMessage(context.getResources().getString(R.string.dlg_waiting_message));
        mPleaseWaitDialog.setCancelable(false);
        mPleaseWaitDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                mContext.getString(R.string.btn_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity) mContext).finish();
                    }
                });
        DialogUtil.showDialog(mPleaseWaitDialog);
    }

    /**
     * PleaseWait画面の消去 Hides please wait dialog
     */
    private static void closePleaseWaitDialog() {
        if (mPleaseWaitDialog == null)
            return;
        mPleaseWaitDialog.dismiss();
        mPleaseWaitDialog = null;
    }

    /**
     * 初期化失敗ダイアログの表示 Displays boot failed dialog
     */
    private static void showBootFailedDialog() {
        if (mBootFailedDialog == null || mBootFailedDialog.isShowing() == false) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(R.string.error_title);
            builder.setMessage(R.string.error_cannot_connect);
            builder.setCancelable(false);
            builder.setNegativeButton(R.string.btn_close,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((Activity) mContext).finish();
                        }
                    });
            mBootFailedDialog = builder.create();
            DialogUtil.showDialog(mBootFailedDialog);
        }
    }

    /**
     * 印刷中ダイアログの表示
     * Displays scanning dialog.
     * @param context メインアクテビティのコンテキスト
     *                Context of MainActivity
     */
    private static void showPrintingDialog(Context context) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setTitle(context.getResources().getString(R.string.dlg_printing_title));
        mProgressDialog.setMessage(
                context.getResources().getString(R.string.dlg_printing_message_send_file));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                context.getResources().getString(R.string.dlg_printing_button_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mApplication.getStateMachine()
                                .procPrintEvent(PrintEvent.REQUEST_JOB_CANCEL);
                    }
                });
        DialogUtil.showDialog(mProgressDialog);
    }

    /**
     * 印刷中ダイアログの文言更新
     * Updates the printing dialog.
     *
     * @param context メインアクテビティのコンテキスト
     *                Context of MainActivity
     * @param updateMessage ダイアログに表示する文言
     *                      String to display on the dialog
     */
    private static void updatePrintingDialog(Context context, String updateMessage) {
        if (mProgressDialog == null)
            return;
        mProgressDialog.setMessage(updateMessage);
    }

    /**
     * 印刷中ダイアログの非表示 Close the printing dialog,
     */
    private static void closePrintingDialog() {
        if (mProgressDialog == null)
            return;
        mProgressDialog.dismiss();
        mProgressDialog = null;
        ((Activity)mContext).finish();
    }

    /**
     * 印刷中ダイアログのキャンセルボタンの有無を設定する
     * Display/Hide the cancel button in the printing dialog
     * @param enable true:キャンセルボタン表示/false:キャンセルボタン非表示
     *               true:display false:hide
     */
    private static void setPrintingDialogCancelable(boolean enable) {
        if (mProgressDialog == null)
            return;

        Button cancelButton = mProgressDialog.getButton(ProgressDialog.BUTTON_NEGATIVE);
        if (cancelButton == null)
            return;

        if (enable) {
            cancelButton.setVisibility(View.VISIBLE);
        } else {
            cancelButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Toastメッセージ表示
     * Display the toast message
     *
     * @param context メインアクテビティのコンテキスト
     *                Context of MainActivity
     * @param updateMessage Toastに表示する文言
     *                       String to display in the toast
     */
    private static void showToastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /******************************************************************
     * 非同期タスク Asynchronous task
     ******************************************************************/

    /**
     * PrintResponseExceptionのエラー情報を文字列化します。
     * フォーマットは以下の通りです。
     * Creates the string of the ScanResponseException error information.
     * The format is as below.
     *
     * base[separator]
     * [separator]
     * message_id: message[separator]
     * message_id: message[separator]
     * message_id: message
     *
     * @param e 文字列化対象のPrintResponseException
     *          ScanResponseException to be converted as a string
     * @param base メッセージ先頭文字列
     *             Starting string of the message
     * @return メッセージ文字列
     *         Message string
     */
    private String makeJobErrorResponceMessage(PrintResponseException e, String base) {
        StringBuilder sb = new StringBuilder(base);
        if (e.hasErrors()) {
            Map<String, String> errors = e.getErrors();
            if (errors.size() > 0) {
                String separator = System.getProperty("line.separator");
                sb.append(separator);

                for (Map.Entry<String, String> entry : errors.entrySet()) {
                    sb.append(separator);
                    // message_id
                    sb.append(entry.getKey());
                    // message (exists only)
                    String message = entry.getValue();
                    if ((message != null) && (message.length() > 0)) {
                        sb.append(": ");
                        sb.append(message);
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * ジョブを開始するタスクです。 The asynchronous task to start the scan job.
     */
    static class StartPrintJobTask extends AsyncTask<PrintSettingDataHolder, Void, Boolean> {

        private String message = null;

        @Override
        protected Boolean doInBackground(PrintSettingDataHolder... holders) {
            /* 省エネロック、オフラインロック */
            if (!mApplication.lockPowerMode() || !mApplication.lockOffline()) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(
                                mContext,
                                "Error: cannot start scan job. LockPowerMode() or lockOffline() failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }

            PrintFile printFile;
            try {
                printFile = holders[0].fromAssets() ? holders[0].getPrintFile(mApplication
                        .getResources()) : holders[0].getPrintFile();
            } catch (PrintException e) {
                e.printStackTrace();
                message = "Print Job Failed. " + e.getMessage();
                if (e instanceof PrintResponseException) {
                    message = mApplication.getStateMachine().makeJobErrorResponceMessage(
                            (PrintResponseException) e, message);
                }
                return false;
            }

            PrintRequestAttributeSet attributeSet = holders[0].getPrintRequestAttributeSet();
            if (printFile == null || attributeSet == null) {
                return false;
            }

            try {
                currentPrintJob = mApplication.getPrintJob();
                currentPrintJob.print(printFile, attributeSet);
                return true;
            } catch (PrintException e) {
                e.printStackTrace();
                message = "Print Job Failed. " + e.getMessage();
                if (e instanceof PrintResponseException) {
                    message = mApplication.getStateMachine().makeJobErrorResponceMessage(
                            (PrintResponseException) e, message);
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                mApplication.getStateMachine().procPrintEvent(PrintEvent.CHANGE_JOB_STATE_INITIAL);
                unLockMode();
                return;
            }

            mApplication.getStateMachine().procPrintEvent(
                    PrintStateMachine.PrintEvent.CHANGE_JOB_STATE_PRE_PENDING);
        }
    }

    /**
     * 現在印刷中のジョブを中止するタスクです。 The task to cancel the job.
     */
    static class CancelPrintJobTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean result = false;

            if (currentPrintJob == null)
                return false;

            try {
                result = currentPrintJob.cancelPrintJob();
            } catch (PrintException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

}
