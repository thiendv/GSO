/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.app.scan.application;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import jp.co.ricoh.ssdk.sample.app.scan.R;
import jp.co.ricoh.ssdk.sample.app.scan.activity.MainActivity;
import jp.co.ricoh.ssdk.sample.app.scan.activity.PreviewActivity;
import jp.co.ricoh.ssdk.sample.app.scan.activity.ScanActivity;
import jp.co.ricoh.ssdk.sample.function.scan.ScanImage;
import jp.co.ricoh.ssdk.sample.function.scan.ScanPDF;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.HashScanRequestAttributeSet;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.ScanException;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.ScanJobAttributeSet;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.ScanRequestAttributeSet;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.ScanResponseException;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.*;

import java.util.Map;

/**
 * スキャンサンプルアプリのステートマシン
 * [処理内容]
 * ジョブのハンドリングを行います。
 * また、起動時及びジョブ発行後の以下のダイアログの生成・表示・破棄はすべてこのステートマシンを通して実行します。
 *   ・お待ち下さいダイアログ
 *   ・スキャン中ダイアログ
 *   ・次原稿待ちダイアログ
 *   ・プレビュー画面
 *
 * State machine of scan sample application.
 * Handles the Scan job.
 * Display/update/hide processes of the following dialog/screen are always executed by this statemachine.
 *   - Please wait dialog
 *   - Scanning dialog
 *   - Dialog asking for next original(s)
 *   - Preview screen
 */
public class ScanStateMachine {
    private static String TAG = "ScanStateMachine";

    /**
     * デフォルトのダイアログの横幅
     * Default dialog width
     */
    private static final int DEFAULT_DIALOG_WIDTH = 400;

    /**
     * メインアクティビティへの参照
     * Reference to Main activity
     */
    private static Activity mActivity;

    /**
     * お待ちくださいダイアログ
     * "Please wait" dialog
     */
    private ProgressDialog mPleaseWaitDialog;

    /**
     * 初期化失敗ダイアログ
     * Boot failed dialog
     */
    private AlertDialog mBootFailedDialog;

    /**
     * スキャン中ダイアログ
     * Scanning dialog
     */
    protected ProgressDialog mScanningDialog;

    /**
     * カウントダウンあり次原稿待ちダイアログ
     * "Set next original" dialog : with count down
     */
    protected Dialog mCountDownDialog;

    /**
     * カウントダウンなし次原稿待ちダイアログ
     * "Set next original" dialog: without count down
     */
    protected Dialog mNoCountDownDialog;

    /**
     * スキャンサンプルアプリケーション
     * Scan sample application object
     */
    private static ScanSampleApplication mApplication;

    /**
     * UIスレッドのハンドラー
     * UI thread handler
     */
    private Handler mHandler;

    ScanStateMachine(ScanSampleApplication app, Handler handler) {
        mApplication = app;
        mHandler = handler;
    }

    /**
     * メインアクティビティを登録します。
     * Registers the MainActivity.
     *
     * @param act MainActivity
     */
    public void registActivity(Activity act) {
        mActivity = act;
    }

    /**
     * 状態遷移イベント
     * State transition event
     */
    public enum ScanEvent {

        /**
         * ジョブ状態がPENDING（ジョブ実行前）に遷移したことを示すイベント
         * The event to indicate that the job state has changed to PENDING (before the job starts)
         */
        CHANGE_JOB_STATE_PENDING,

        /**
         * ジョブ状態がPROCESSING（ジョブ実行中）に遷移したことを示すイベント
         * The event to indicate that the job state has changed to PROCESSING (the job is processing)
         */
        CHANGE_JOB_STATE_PROCESSING,

        /**
         * ジョブ状態がABORTED（システム側で読取中止）に遷移したことを示すイベント
         * The event to indicate that the job state has changed to ABORTED (the job is aborted by system)
         */
        CHANGE_JOB_STATE_ABORTED,

        /**
         * ジョブ状態がCANCELED（ユーザー操作で読取中止）に遷移したことを示すイベント
         * The event to indicate that the job state has changed to CANCELED (the job is canceled by user)
         */
        CHANGE_JOB_STATE_CANCELED,

        /**
         * 送信前プレビュー状態に遷移したことを示すイベント
         * The event to indicate that the job state has changed to the preview state before the data is sent
         */
        CHANGE_JOB_STATE_STOPPED_PREVIEW,

        /**
         * 一時停止中のカウントダウン状態に遷移したことを示すイベント
         * The event to indicate that the job state has changed to the countdown state for pausing
         */
        CHANGE_JOB_STATE_STOPPED_COUNTDOWN,

        /**
         * ジョブ状態がCOMPLETED（ジョブ正常終了）に遷移したことを示すイベント
         * The event to indicate that the job state has changed to COMPLETED (the job ended successfully)
         */
        CHANGE_JOB_STATE_COMPLETED,


        /**
         * ジョブ実行のリクエストイベント
         * Job start request event
         */
        REQUEST_JOB_START,

        /**
         * ジョブキャンセルのリクエストイベント
         * Job cancel request event
         */
        REQUEST_JOB_CANCEL,

        /**
         * ジョブ続行のリクエストイベント
         * Job continue request event
         */
        REQUEST_JOB_CONTINUE,

        /**
         * ジョブ終了のリクエストイベント
         * Job end request event
         */
        REQUEST_JOB_END,


        /**
         * スキャン実行中の情報変更イベント
         * Information change event for scan job in process
         */
        UPDATE_JOB_STATE_PROCESSING,

        /**
         * アクティビティ生成イベント
         * MainActivity created event
         */
        ACTIVITY_CREATED,

        /**
         * アクティビティ終了イベント
         * MainActivity destroyed event
         */
        ACTIVITY_DESTROYED,


        /**
         * 初期化完了イベント
         * Initialization completed event
         */
        ACTIVITY_BOOT_COMPLETED,

        /**
         * 初期化失敗イベント
         * Initialization failed event
         */
        ACTIVITY_BOOT_FAILED,

        /**
         * ジョブリセット完了イベント
         * Job reset completed event
         */
        REBOOT_COMPLETED,
    }

    /**
     * ステートマシンの初期状態
     * Statemachine initial state
     */
    private State mState = State.INITIAL;

    /**
     * 状態定義
     * State definition
     */
    public enum State {
        /**
         * 初期状態
         * Initial state
         */
        INITIAL {
            @Override
            public State getNextState(final ScanStateMachine sm, final ScanEvent event, final Object prm) {
                switch (event) {
                case ACTIVITY_CREATED:
                    actShowPleaseWait(sm, prm);
                    return INITIAL;
                case ACTIVITY_BOOT_COMPLETED:
                    actClosePleaseWait(sm, prm);
                    return IDLE;
                case ACTIVITY_BOOT_FAILED:
                    actClosePleaseWait(sm, prm);
                    actShowBootFailedDialog(sm, prm);
                    return INITIAL;
                default:
                    return super.getNextState(sm, event, prm);
                }
            }
        },
        /**
         * ジョブ生成後のジョブ開始前状態
         * The state before the job is started after the job has been created
         */
        IDLE {
            @Override
            public State getNextState(final ScanStateMachine sm, final ScanEvent event, final Object prm) {
                switch (event) {
                    case CHANGE_JOB_STATE_PENDING :
                        return JOB_PENDING;
                    case REQUEST_JOB_START :
                        return WAITING_JOB_START;
                default:
                    return super.getNextState(sm, event, prm);
                }
            }
        },
        /**
         * ジョブ開始待ち状態
         * Job start waiting state
         */
        WAITING_JOB_START {
            @Override
            public State getNextState(final ScanStateMachine sm, final ScanEvent event, final Object prm) {
                switch (event) {
                case REQUEST_JOB_CANCEL:
                    return WAITING_JOB_CANCEL;

                case CHANGE_JOB_STATE_PENDING :
                    return JOB_PENDING;
                case CHANGE_JOB_STATE_PROCESSING :
                    return JOB_PROCESSING;

                case CHANGE_JOB_STATE_ABORTED :
                    return JOB_ABORTED;
                case CHANGE_JOB_STATE_CANCELED :
                    return JOB_CANCELED;

                case ACTIVITY_DESTROYED:
                    return WAITING_JOB_CANCEL;
                default:
                    return super.getNextState(sm, event, prm);
                }
            }
            @Override
            public void entry(final ScanStateMachine sm, final Object prm) {
                super.entry(sm, prm);
                actShowScanningDialog(sm, prm);
                sm.new StartScanJobTask().execute();
            }
        },
        /**
         * ジョブ開始後の実行前状態
         * The state before the job is started after the job has been started
         */
        JOB_PENDING {
            @Override
            public State getNextState(final ScanStateMachine sm, final ScanEvent event, final Object prm) {
                switch (event) {
                    case CHANGE_JOB_STATE_PROCESSING :
                        return JOB_PROCESSING;
                    case REQUEST_JOB_CANCEL:
                        return WAITING_JOB_CANCEL;

                    case CHANGE_JOB_STATE_ABORTED :
                        return JOB_ABORTED;
                    case CHANGE_JOB_STATE_CANCELED :
                        return JOB_CANCELED;

                    case ACTIVITY_DESTROYED:
                        return WAITING_JOB_CANCEL;
                default:
                    return super.getNextState(sm, event, prm);
                }
            }
        },
        /**
         * ジョブ実行中
         * Job processing
         */
        JOB_PROCESSING {
            @Override
            public State getNextState(final ScanStateMachine sm, final ScanEvent event, final Object prm) {
                switch (event) {
                    case CHANGE_JOB_STATE_ABORTED :
                        return JOB_ABORTED;
                    case CHANGE_JOB_STATE_CANCELED :
                        return JOB_CANCELED;
                    case CHANGE_JOB_STATE_STOPPED_PREVIEW :
                        return JOB_STOPPED_PREVIEW;
                    case CHANGE_JOB_STATE_STOPPED_COUNTDOWN :
                        return JOB_STOPPED_COUNTDOWN;
                    case CHANGE_JOB_STATE_COMPLETED :
                        return JOB_COMPLETED;
                    case REQUEST_JOB_CANCEL:
                        return WAITING_JOB_CANCEL;
                    case UPDATE_JOB_STATE_PROCESSING :
                        return JOB_PROCESSING;

                    case ACTIVITY_DESTROYED:
                        return WAITING_JOB_CANCEL;
                    default:
                        return super.getNextState(sm, event, prm);
                }
            }
            @Override
            public void entry(final ScanStateMachine sm, final Object prm) {
                super.entry(sm, prm);
                actUpdateScanningDialog(sm, prm);
            }
        },
        /**
         * システム側によりジョブキャンセル
         * Job cancelling by system
         */
        JOB_ABORTED {
            public State getNextState(final ScanStateMachine sm, final ScanEvent event, final Object prm) {
                switch (event) {
                    case REBOOT_COMPLETED :
                        return IDLE;
                    default:
                        return super.getNextState(sm, event, prm);
                }
            }
            @Override
            public void entry(final ScanStateMachine sm, final Object prm) {
                super.entry(sm, prm);
                actCloseScanningDialog(sm,prm);
                actCloseWaitForNextOriginal(sm,prm);
                actClosePreview(sm,prm);

                // show toast message with aborted reason
                String message = "job aborted.";
                if (prm instanceof ScanJobAttributeSet) {
                    ScanJobAttributeSet attributes = (ScanJobAttributeSet) prm;
                    ScanJobStateReasons reasons = (ScanJobStateReasons) attributes.get(ScanJobStateReasons.class);
                    if (reasons != null) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(message);
                        sb.append(System.getProperty("line.separator"));
                        sb.append(reasons.getReasons().toString());
                        message = sb.toString();
                    }
                }
                actShowToastMessage(sm, message);

                actInitJobSetting(sm, prm);
            }
        },
        /**
         * ジョブキャンセル中
         * Job cancelling
         */
        JOB_CANCELED {
            public State getNextState(final ScanStateMachine sm, final ScanEvent event, final Object prm) {
                switch (event) {
                    case CHANGE_JOB_STATE_PENDING:
                        return JOB_PENDING;
                    case REBOOT_COMPLETED :
                        return IDLE;
                    default:
                        return super.getNextState(sm, event, prm);
                }
            }
            @Override
            public void entry(final ScanStateMachine sm, final Object prm) {
                super.entry(sm, prm);
                actCloseScanningDialog(sm,prm);
                actCloseWaitForNextOriginal(sm,prm);
                actClosePreview(sm,prm);
                actInitJobSetting(sm, prm);
            }
        },
        /**
         * ジョブ一時停止中(次原稿待ちダイアログ表示)
         * Job pausing (display "set next original" dialog)
         */
        JOB_STOPPED_COUNTDOWN {
            @Override
            public State getNextState(final ScanStateMachine sm, final ScanEvent event, final Object prm) {
                switch (event) {
                    case REQUEST_JOB_CONTINUE:
                        return WAITING_JOB_CONTINUE;
                    case REQUEST_JOB_CANCEL:
                        return WAITING_JOB_CANCEL;
                    case REQUEST_JOB_END:
                        return WAITING_JOB_END;

                    case CHANGE_JOB_STATE_PROCESSING :
                        return JOB_PROCESSING;
                    case CHANGE_JOB_STATE_COMPLETED :
                        return JOB_COMPLETED;
                    case CHANGE_JOB_STATE_ABORTED :
                        return JOB_ABORTED;
                    case CHANGE_JOB_STATE_CANCELED :
                        return JOB_CANCELED;

                    case CHANGE_JOB_STATE_STOPPED_PREVIEW :
                        return JOB_STOPPED_PREVIEW;

                    case ACTIVITY_DESTROYED:
                        return WAITING_JOB_CANCEL;
                    default:
                        return super.getNextState(sm, event, prm);
                }
            }
            @Override
            public void entry(final ScanStateMachine sm, final Object prm) {
                super.entry(sm, prm);
                actWaitForNextOriginal(sm, prm);
            }
        },
        /**
         * ジョブ一時停止中（プレビュー表示）
         * Job pausing (display preview)
         */
        JOB_STOPPED_PREVIEW {
            @Override
            public State getNextState(final ScanStateMachine sm, final ScanEvent event, final Object prm) {
                switch (event) {
                case REQUEST_JOB_END:
                    return WAITING_JOB_END;
                case REQUEST_JOB_CANCEL:
                    return WAITING_JOB_CANCEL;

                case CHANGE_JOB_STATE_PROCESSING :
                    return JOB_PROCESSING;
                case CHANGE_JOB_STATE_COMPLETED :
                    return JOB_COMPLETED;
                case CHANGE_JOB_STATE_ABORTED :
                    return JOB_ABORTED;
                case CHANGE_JOB_STATE_CANCELED :
                    return JOB_CANCELED;

                case ACTIVITY_DESTROYED:
                    return WAITING_JOB_CANCEL;
                default:
                    return super.getNextState(sm, event, prm);
                }
            }
            @Override
            public void entry(final ScanStateMachine sm, final Object prm) {
                super.entry(sm, prm);
                actShowPreview(sm, prm);
            }
        },
        /**
         * ジョブ再開待ち
         * Job waiting to be resumed
         */
        WAITING_JOB_CONTINUE {
            @Override
            public State getNextState(final ScanStateMachine sm, final ScanEvent event, final Object prm) {
                switch (event) {
                    case CHANGE_JOB_STATE_PROCESSING :
                        return JOB_PROCESSING;

                    case CHANGE_JOB_STATE_COMPLETED :
                        return JOB_COMPLETED;
                    case CHANGE_JOB_STATE_ABORTED :
                        return JOB_ABORTED;
                    case CHANGE_JOB_STATE_CANCELED :
                        return JOB_CANCELED;

                    case ACTIVITY_DESTROYED:
                        return WAITING_JOB_CANCEL;
                default:
                    return super.getNextState(sm, event, prm);
                }
            }
            @Override
            public void entry(final ScanStateMachine sm, final Object prm) {
                super.entry(sm, prm);
                sm.new ContinueScanJobTask().execute();
            }
        },
        /**
         * ジョブキャンセル待ち
         * Job waiting to be cancelled
         */
        WAITING_JOB_CANCEL {
            @Override
            public State getNextState(final ScanStateMachine sm, final ScanEvent event, final Object prm) {
                switch (event) {
                    case CHANGE_JOB_STATE_CANCELED:
                        return JOB_CANCELED;

                    case CHANGE_JOB_STATE_PROCESSING :
                        return JOB_PROCESSING;
                    case CHANGE_JOB_STATE_COMPLETED :
                        return JOB_COMPLETED;
                    case CHANGE_JOB_STATE_ABORTED :
                        return JOB_ABORTED;
                default:
                    return super.getNextState(sm, event, prm);
                }
            }
            @Override
            public void entry(final ScanStateMachine sm, final Object prm) {
                super.entry(sm, prm);
                sm.new CancelScanJobTask().execute();
            }
        },
        /**
         * ジョブ終了待ち
         * Job waiting to be finished
         */
        WAITING_JOB_END {
            @Override
            public State getNextState(final ScanStateMachine sm, final ScanEvent event, final Object prm) {
                switch (event) {
                    case CHANGE_JOB_STATE_COMPLETED :
                        return JOB_COMPLETED;
                    case CHANGE_JOB_STATE_STOPPED_PREVIEW :
                        return JOB_STOPPED_PREVIEW;

                    case CHANGE_JOB_STATE_ABORTED :
                        return JOB_ABORTED;
                    case CHANGE_JOB_STATE_CANCELED :
                        return JOB_CANCELED;
                default:
                    return super.getNextState(sm, event, prm);
                }
            }
            @Override
            public void entry(final ScanStateMachine sm, final Object prm) {
                super.entry(sm, prm);
                sm.new EndScanJobTask().execute();
            }
        },

        /**
         * ジョブ正常終了
         * Job completed successfully
         */
        JOB_COMPLETED {
            @Override
            public State getNextState(final ScanStateMachine sm, final ScanEvent event, final Object prm) {
                switch (event) {
                case REBOOT_COMPLETED :
                    return IDLE;
                default:
                    return super.getNextState(sm, event, prm);
                }
            }
            @Override
            public void entry(final ScanStateMachine sm, final Object prm) {
                super.entry(sm, prm);
                actCloseScanningDialog(sm,prm);
                actCloseWaitForNextOriginal(sm,prm);
                actClosePreview(sm,prm);
                actInitJobSetting(sm, prm);
                actPostJobCompleted(sm, prm);
            }
        },
        ;

        private static void actPostJobCompleted(ScanStateMachine sm, Object prm) {
            ((ScanActivity) mActivity).onJobCompleted();
        }


        /**
         * 次の状態を取得します。
         * 各状態がオーバーライドします。
         * Obtains the next state.
         * Each state should override this method.
         *
         * @param sm
         * @param event
         * @param prm Object for additional information
         * @return
         */
        public State getNextState(final ScanStateMachine sm, final ScanEvent event, final Object prm) {
            switch (event) {
            default:
                return null;
            }
        }

        /**
         * 状態に入るときに呼ばれるメソッドです。
         * 各状態が必要に応じてオーバーライドします。
         * This method is called when entering a state.
         * Each state should override this method if necessary.
         *
         * @param sm
         * @param prm Object for additional information
         */
        public void entry(final ScanStateMachine sm, final Object prm) {
        }

        /**
         * 状態から抜けるときに呼ばれるメソッドです。
         * 各状態が必要に応じてオーバーライドします。
         * This method is called when exiting a state.
         * Each state should override this method if necessary.
         *
         * @param sm
         * @param prm Object for additional information
         */
        public void exit(final ScanStateMachine sm, final Object prm) {
        }


        // +++++++++++++++++++++++++++++++++++++++++
        // アクション関数
        // Action method
        // +++++++++++++++++++++++++++++++++++++++++

        /**
         * PleaseWait画面の表示
         * Displays please wait dialog
         */
        protected void actShowPleaseWait(final ScanStateMachine sm, final Object prm) {
            sm.mHandler.post(new Runnable() {
                @Override
                public void run() {
                    sm.showPleaseWaitDialog();
                }
            });
        }

        /**
         * PleaseWait画面の消去
         * Hides please wait dialog
         */
        protected void actClosePleaseWait(final ScanStateMachine sm, final Object prm) {
            sm.mHandler.post(new Runnable() {
                @Override
                public void run() {
                    sm.closePleaseWaitDialog();
                }
            });
        }

        /**
         * 初期化失敗画面の表示
         * Displays boot failed dialog
         */
        protected void actShowBootFailedDialog(final ScanStateMachine sm, final Object prm) {
            sm.mHandler.post(new Runnable() {
                @Override
                public void run() {
                    sm.showBootFailedDialog();
                }
            });
        }

        /**
         * スキャン中画面の表示
         * Diaplays scanning dialog
         */
        protected void actShowScanningDialog(final ScanStateMachine sm, final Object prm) {
            sm.mHandler.post(new Runnable() {
                @Override
                public void run() {
                    sm.showScanningDialog();
                }
            });
        }

        /**
         * スキャン中画面の消去
         * Hides scanning dialog
         */
        protected void actCloseScanningDialog(final ScanStateMachine sm, final Object prm) {
            sm.mHandler.post(new Runnable() {
                @Override
                public void run() {
                    sm.closeScanningDialog();
                }
            });
        }

        /**
         * スキャン中画面の更新
         * Updates scanning dialog
         */
        protected void actUpdateScanningDialog(final ScanStateMachine sm, final Object prm) {
            if(prm instanceof String) {
                sm.mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        sm.updateScanDialogMessage((String)prm);
                    }
                });
            }
        }

        /**
         * 次原稿待ちダイアログの表示
         * Displays "set next original" dialog
         */
        protected void actWaitForNextOriginal(final ScanStateMachine sm, final Object prm) {
            if (mApplication.getTimeOfWaitingNextOriginal() == 0)
                sm.mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        sm.showNoCountDownDialog();
                    }
                });
            else
                sm.mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        sm.showCountDownDialog();
                    }
                });
        }

        /**
         * 次原稿待ちダイアログの消去
         * Hides "set next original" dialog
         */
        protected void actCloseWaitForNextOriginal(final ScanStateMachine sm, final Object prm) {
            sm.mHandler.post(new Runnable() {
                @Override
                public void run() {
                    sm.closeCountDownDialog();
                    sm.closeNoCountDownDialog();
                }
            });
        }

        /**
         * プレビュー画面の表示
         * Displays preview screen
         */
        protected void actShowPreview(final ScanStateMachine sm, final Object prm) {
            sm.mHandler.post(new Runnable() {
                @Override
                public void run() {
                    sm.showPreview();
                }
            });
        }

        /**
         * プレビュー画面の消去
         * Hides preview screen
         */
        protected void actClosePreview(final ScanStateMachine sm, final Object prm) {
            sm.mHandler.post(new Runnable() {
                @Override
                public void run() {
                    sm.closePreview();
                }
            });
        }

        /**
         * Toastメッセージ表示
         * Displays toast message
         */
        protected void actShowToastMessage(final ScanStateMachine sm, final Object prm) {
            sm.mHandler.post(new Runnable() {
                @Override
                public void run() {
                    sm.showToastMessage((String)prm);
                }
            });
        }

        /**
         * スキャンジョブの初期化
         * Initializes scan job
         */
        protected void actInitJobSetting(final ScanStateMachine sm, final Object prm) {
            sm.mHandler.post(new Runnable() {
                @Override
                public void run() {
                    /* unlock power mode*/
                    mApplication.unlockPowerMode();
                    /* unlock offline */
                    mApplication.unlockOffline();

                    mApplication.initJobSetting();
                    sm.procScanEvent(ScanEvent.REBOOT_COMPLETED);
                }
            });
        }
    }


    /**
     * 状態遷移を行います。
     * Changes states.
     *
     * @param event
     */
    public void procScanEvent(final ScanEvent event) {
        procScanEvent(event, null);
    }

    /**
     * 状態遷移を行います。
     * Changes states.
     *
     * @param event
     * @param prm  Object for additional information
     */
    public void procScanEvent(final ScanEvent event, final Object prm) {
        Log.i(TAG, ">evtp :" + event);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                State newState = mState.getNextState(ScanStateMachine.this, event, prm);
                if (newState != null) {
                    Log.i(TAG, "#evtp :" + event + " state:" + mState + " > " + newState);
                    mState.exit(ScanStateMachine.this, prm);
                    mState = newState;
                    mState.entry(ScanStateMachine.this, prm);
                }
            }
        });
    }

    /*=============================================================
     * ステートマシンから呼ばれるpublicメソッド
     * public methos called by statemachine
     *=============================================================*/

    /**
     * ダイアログを指定された幅で表示します。
     * Displays the dialog in specified width.
     *
     * @param d dialog
     * @param width dialog width
     */
    private void showDialog(Dialog d, int width) {
        d.show();
        WindowManager.LayoutParams lp = d.getWindow().getAttributes();
        lp.width = width;
        d.getWindow().setAttributes(lp);
    }

    /**
     * ダイアログをデフォルトサイズで表示します。
     * Displays the dialog in default size.
     * @param d dialog
     */
    private void showDialog(Dialog d) {
        showDialog(d, DEFAULT_DIALOG_WIDTH);
    }

    /**
     * お待ち下さいダイアログを表示します。
     * Displays please wait dialog.
     */
    public void showPleaseWaitDialog() {
        if(mPleaseWaitDialog==null || mPleaseWaitDialog.isShowing()==false) {
            mPleaseWaitDialog = createPleaseWaitDialog();
            showDialog(mPleaseWaitDialog);
        }
    }

    /**
     * お待ち下さいダイアログを閉じます。
     * Hides please wait dialog.
     */
    public void closePleaseWaitDialog() {
        if(mPleaseWaitDialog!=null && mPleaseWaitDialog.isShowing()) {
            mPleaseWaitDialog.dismiss();
            mPleaseWaitDialog = null;
        }
    }

    /**
     * 初期化失敗ダイアログを表示します。
     * Displays boot failed dialog.
     */
    public void showBootFailedDialog() {
        if(mBootFailedDialog==null || mBootFailedDialog.isShowing()==false) {
            mBootFailedDialog = createBootFailedDialog();
            showDialog(mBootFailedDialog);
        }
    }

    /**
     * スキャン中ダイアログを表示します。
     * Displays scanning dialog.
     */
    public void showScanningDialog() {
        if( mScanningDialog==null || mScanningDialog.isShowing()==false) {
            mScanningDialog = createScanProgressDialog();
            showDialog(mScanningDialog);
        }
    }

    /**
     * スキャン中ダイアログを閉じます。
     * Hides scanning dialog.
     */
    public void closeScanningDialog() {
        if(mScanningDialog!=null && mScanningDialog.isShowing()) {
            mScanningDialog.cancel();
            mScanningDialog = null;
        }
    }

    /**
     * カウントダウンあり次原稿待ちダイアログを表示します。
     * Displays "set next original" dialog (with count down)
     */
    public void showCountDownDialog() {
        if(mCountDownDialog==null || mCountDownDialog.isShowing()==false) {
            mCountDownDialog = createCountDownDialog();
            mCountDownDialog.show();
        }
    }

    /**
     * カウントダウンあり次原稿待ちダイアログを閉じます。
     * Hides "set next original" dialog (with count down)
     */
    public void closeCountDownDialog() {
        if(mCountDownDialog!=null && mCountDownDialog.isShowing()) {
            mCountDownDialog.cancel();
            mCountDownDialog = null;
        }
    }

    /**
     * カウントダウンなし次原稿待ちダイアログを表示します。
     * Displays "set next original" dialog (without count down)
     */
    public void showNoCountDownDialog() {
        if(mNoCountDownDialog==null || mNoCountDownDialog.isShowing()==false) {
            mNoCountDownDialog = createNoCountDownDialog();
            mNoCountDownDialog.show();
        }
    }

    /**
     * カウントダウンなし次原稿待ちダイアログを閉じます。
     * Hides "set next original" dialog (without count down)
     */
    public void closeNoCountDownDialog() {
        if(mNoCountDownDialog!=null && mNoCountDownDialog.isShowing()) {
            mNoCountDownDialog.cancel();
            mNoCountDownDialog = null;
        }
    }

    /**
     * スキャンダイアログの表示を更新します。
     * Updates scanning dialog.
     */
    public void updateScanDialogMessage(String message) {
        if(mScanningDialog!=null && mScanningDialog.isShowing()) {
            mScanningDialog.setMessage(message);
        }
    }

    /**
     * プレビュー画面に遷移します。
     * Changes to the preview screen.
     */
    public void showPreview() {
        Intent intent = new Intent(mActivity, PreviewActivity.class);
        mActivity.startActivityForResult(intent, PreviewActivity.REQUEST_CODE_PREVIEW_ACTIVITY);
    }

    /**
     * プレビュー画面を消去します。
     * Hides preview screen
     */
    public void closePreview() {
        mActivity.finishActivity(PreviewActivity.REQUEST_CODE_PREVIEW_ACTIVITY);
    }

    /**
     * Toastメッセージを表示します。
     * Displays a toast message.
     */
    void showToastMessage(String message) {
        Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
    }

    /*=============================================================
     * ダイアログ生成
     * Creates dialog
     *=============================================================*/

    /**
     * しばらくお待ち下さいダイアログを生成します。
     * Creates the "please wait" dialog.
     */
    private ProgressDialog createPleaseWaitDialog() {
        ProgressDialog dialog = new ProgressDialog(mActivity);
        dialog.setMessage(mActivity.getString(R.string.txid_cmn_d_wait));
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, mActivity.getString(R.string.txid_cmn_b_cancel),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mActivity.finish();
                }
            });
        return dialog;
    }

    /**
     * 初期化失敗ダイアログを生成します。
     * Creates the "boot failed" dialog.
     */
    private AlertDialog createBootFailedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(R.string.txid_cmn_b_error);
        builder.setMessage(R.string.txid_cmn_d_cannot_connect);
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.txid_cmn_b_close,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mActivity.finish();
                }
            });
        return builder.create();
    }

    /**
     * スキャン中ダイアログを生成します。
     * Creates the scanning dialog.
     */
    private ProgressDialog createScanProgressDialog() {
        ProgressDialog dialog = new ProgressDialog(mActivity);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(mActivity.getString(R.string.txid_scan_d_scanning));
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, mActivity.getString(R.string.txid_cmn_b_cancel),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    procScanEvent(ScanEvent.REQUEST_JOB_CANCEL);
                }
            });
        return dialog;
    }

    /**
     * 一時停止イベント中のカウントダウンダイアログを生成します。
     * Creates the "set next original" dialog (with countdown) for the pause event.
     */
   private Dialog createCountDownDialog() {
        final Dialog countDownDialog = new Dialog(mActivity);
        countDownDialog.setTitle("Scanning");
        countDownDialog.setContentView(R.layout.dlg_count_down);
        countDownDialog.show();

        final CountDownTimer timer = new CountDownTimer((mApplication.getTimeOfWaitingNextOriginal()+1)*1000, 1000) {
            @Override
            public void onTick(long millis) {
                ((TextView) countDownDialog.findViewById(R.id.txt_count_down))
                .setText(String.format(mActivity.getString(R.string.txid_scan_t_count_down_text),
                        millis / 1000));
            }

            @Override
            public void onFinish() {
                countDownDialog.dismiss();
                procScanEvent(ScanEvent.REQUEST_JOB_END);
            }
        }.start();

        countDownDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                timer.cancel();
            }
        });

        // continue button
        Button start = (Button) countDownDialog.findViewById(R.id.btn_count_down_start);
        start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                countDownDialog.dismiss();
                procScanEvent(ScanEvent.REQUEST_JOB_CONTINUE);
            }
        });

        // finish button
        Button finish = (Button) countDownDialog.findViewById(R.id.btn_count_down_finish);
        finish.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                countDownDialog.dismiss();
                procScanEvent(ScanEvent.REQUEST_JOB_END);
            }
        });

        return countDownDialog;
    }

   /**
    * 一時停止イベント中のカウントダウンなしダイアログを生成します。
    * Creates "set next original" dialog (without countdown) for the pause event.
    */
  private Dialog createNoCountDownDialog() {
       final Dialog noCountDownDialog = new Dialog(mActivity);
       noCountDownDialog.setTitle("Scanning");
       noCountDownDialog.setContentView(R.layout.dlg_no_count_down);
       noCountDownDialog.show();

       // continue button
       Button start = (Button) noCountDownDialog.findViewById(R.id.btn_count_down_start);
       start.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               noCountDownDialog.dismiss();
               procScanEvent(ScanEvent.REQUEST_JOB_CONTINUE);
           }
       });

       // finish button
       Button finish = (Button) noCountDownDialog.findViewById(R.id.btn_count_down_finish);
       finish.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               noCountDownDialog.dismiss();
               procScanEvent(ScanEvent.REQUEST_JOB_END);
           }
       });

       return noCountDownDialog;
   }

   /*=============================================================
    * スキャンジョブ操作の非同期タスク
    * The asynchronous task to start the scan job.
    *=============================================================*/

   /**
    * ScanResponseExceptionのエラー情報を文字列化します。
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
    * @param e 文字列化対象のScanResponseException
    *          ScanResponseException to be converted as a string
    * @param base メッセージ先頭文字列
    *             Starting string of the message
    * @return メッセージ文字列
    *         Message string
    */
   private String makeJobErrorResponceMessage(ScanResponseException e, String base) {
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
                   // message (if exists)
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
    * スキャンジョブを実行開始するための非同期タスクです。
    * The asynchronous task to start the scan job.
    */
   private class StartScanJobTask extends AsyncTask<Void, Void, Boolean> {

       private String message = null;

       @Override
       protected Boolean doInBackground(Void... param) {
           // lock power mode, lock offline
           if (!mApplication.lockPowerMode() || !mApplication.lockOffline()) {
               mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showToastMessage("Error: cannot start scan job. LockPowerMode() or lockOffline() failed.");
                    }
               });
               return false;
           }

           ScanSettingDataHolder scanSetDataHolder = mApplication.getScanSettingDataHolder();
//           DestinationSettingDataHolder destSetDataHolder = mApplication.getDestinationSettingDataHolder();

           Log.d(TAG, "before request...");
           Log.d(TAG, "getSelectedColorValue: " + scanSetDataHolder.getSelectedColorValue());
           Log.d(TAG, "getSelectedSideValue: " + scanSetDataHolder.getSelectedSideValue());
           Log.d(TAG, "getSelectedPreviewValue: " + scanSetDataHolder.getSelectedPreviewValue());
           Log.d(TAG, "getSelectedFileFormatValue: " + scanSetDataHolder.getSelectedFileFormatValue());
           Log.d(TAG, "getSelectedMultiPageValue: " + scanSetDataHolder.getSelectedMultiPageValue());

           // Sets scan attributes.
           ScanRequestAttributeSet requestAttributes;
           requestAttributes = new HashScanRequestAttributeSet();
           requestAttributes.add(AutoCorrectJobSetting.AUTO_CORRECT_ON);
           requestAttributes.add(JobMode.SCAN_AND_STORE_TEMPORARY);
           requestAttributes.add(scanSetDataHolder.getSelectedColorValue());
           requestAttributes.add(scanSetDataHolder.getSelectedSideValue());
           requestAttributes.add(OriginalPreview.OFF);

           FileSetting fileSetting = new FileSetting();
           fileSetting.setFileFormat(scanSetDataHolder.getSelectedFileFormatValue());
           fileSetting.setMultiPageFormat(scanSetDataHolder.getSelectedMultiPageValue());
           requestAttributes.add(fileSetting);

//           DestinationSetting destSet = new DestinationSetting();
//           destSet.add(destSetDataHolder.getDestinationSettingItem());
//           requestAttributes.add(destSet);

           // start scan
           boolean result = false;
           try {
               Log.d(TAG, "scan requesting...");
               result = mApplication.getScanJob().scan(requestAttributes);
               Log.d(TAG, "scan requested. result=" + result);
           } catch (ScanException e) {
               message = "job start failed. " + e.getMessage();
               if (e instanceof ScanResponseException) {
                   message = makeJobErrorResponceMessage((ScanResponseException)e, message);
               }
               Log.w(TAG, message, e);
           }
           return result;
       }
       @Override
       protected void onPostExecute(Boolean result) {
           if (message != null) {
               Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
           }
           if (!result) {
               ScanStateMachine.this.procScanEvent(ScanEvent.CHANGE_JOB_STATE_CANCELED);
           }
       }

   }

   /**
    * スキャンジョブをキャンセルするための非同期タスクです。
    * The asynchronous task to cancel the scan job.
    */
   private class CancelScanJobTask extends AsyncTask<Void, Void, Boolean> {
       @Override
       protected Boolean doInBackground(Void... param) {
           boolean result = false;
           try {
               Log.d(TAG, "scan cancel requesting...");
               result = mApplication.getScanJob().cancelScanJob();
               Log.d(TAG, "scan cancel requested. result=" + result);
           } catch (ScanException e) {
               String message = "job cancel failed. " + e.getMessage();
               if (e instanceof ScanResponseException) {
                   message = makeJobErrorResponceMessage((ScanResponseException)e, message);
               }
               Log.w(TAG, message, e);
           }
           return result;
       }
       @Override
       protected void onPostExecute(Boolean result) {
           if (!result) {
               ScanStateMachine.this.procScanEvent(ScanEvent.CHANGE_JOB_STATE_CANCELED);
           }
       }
   }

   /**
    * スキャンジョブを再開するための非同期タスクです。
    * The asynchronous task to resume the scan job.
    */
   private class ContinueScanJobTask extends AsyncTask<Void, Void, Boolean> {

       private String message = null;

       @Override
       protected Boolean doInBackground(Void... param) {
           boolean result = false;
           try {
               Log.d(TAG, "scan continue requesting...");
               result = mApplication.getScanJob().continueScanJob(null);
               Log.d(TAG, "scan continue requested. result=" + result);
           } catch (ScanException e) {
               message = "job continue failed. cancel job. " + e.getMessage();
               if (e instanceof ScanResponseException) {
                   message = makeJobErrorResponceMessage((ScanResponseException)e, message);
               }
               Log.w(TAG, message, e);
           }
           return result;
       }
       @Override
       protected void onPostExecute(Boolean result) {
           if (message != null) {
               Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
           }
           if (!result) {
               new CancelScanJobTask().execute();
           }
       }
   }

   /**
    * スキャンジョブを終了するための非同期タスク
    * The asynchronous task to end the scan job.
    */
   private class EndScanJobTask extends AsyncTask<Void, Void, Boolean> {

       private String message = null;

       @Override
       protected Boolean doInBackground(Void... param) {
           boolean result = false;
           try {
               Log.d(TAG, "scan end requesting...");
               result = mApplication.getScanJob().endScanJob();
               Log.d(TAG, "scan end requested. result=" + result);
           } catch (ScanException e) {
               message = "job end failed. cancel job. " + e.getMessage();
               if (e instanceof ScanResponseException) {
                   message = makeJobErrorResponceMessage((ScanResponseException)e, message);
               }
               Log.w(TAG, message, e);
           }
           return result;
       }
       @Override
       protected void onPostExecute(Boolean result) {
           if (message != null) {
               Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
           }
           if (!result) {
               new CancelScanJobTask().execute();
           }
       }
   }

}
