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
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.FileSetting.FileFormat;

import java.util.Map;

/**
 * ã‚¹ã‚­ãƒ£ãƒ³ã‚µãƒ³ãƒ—ãƒ«ã‚¢ãƒ—ãƒªã�®ã‚¹ãƒ†ãƒ¼ãƒˆãƒžã‚·ãƒ³
 * [å‡¦ç�†å†…å®¹]
 * ã‚¸ãƒ§ãƒ–ã�®ãƒ�ãƒ³ãƒ‰ãƒªãƒ³ã‚°ã‚’è¡Œã�„ã�¾ã�™ã€‚
 * ã�¾ã�Ÿã€�èµ·å‹•æ™‚å�Šã�³ã‚¸ãƒ§ãƒ–ç™ºè¡Œå¾Œã�®ä»¥ä¸‹ã�®ãƒ€ã‚¤ã‚¢ãƒ­ã‚°ã�®ç”Ÿæˆ�ãƒ»è¡¨ç¤ºãƒ»ç ´æ£„ã�¯ã�™ã�¹ã�¦ã�“ã�®ã‚¹ãƒ†ãƒ¼ãƒˆãƒžã‚·ãƒ³ã‚’é€šã�—ã�¦å®Ÿè¡Œã�—ã�¾ã�™ã€‚
 *   ãƒ»ã�Šå¾…ã�¡ä¸‹ã�•ã�„ãƒ€ã‚¤ã‚¢ãƒ­ã‚°
 *   ãƒ»ã‚¹ã‚­ãƒ£ãƒ³ä¸­ãƒ€ã‚¤ã‚¢ãƒ­ã‚°
 *   ãƒ»æ¬¡åŽŸç¨¿å¾…ã�¡ãƒ€ã‚¤ã‚¢ãƒ­ã‚°
 *   ãƒ»ãƒ—ãƒ¬ãƒ“ãƒ¥ãƒ¼ç”»é�¢
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
     * ãƒ‡ãƒ•ã‚©ãƒ«ãƒˆã�®ãƒ€ã‚¤ã‚¢ãƒ­ã‚°ã�®æ¨ªå¹…
     * Default dialog width
     */
    private static final int DEFAULT_DIALOG_WIDTH = 400;

    /**
     * ãƒ¡ã‚¤ãƒ³ã‚¢ã‚¯ãƒ†ã‚£ãƒ“ãƒ†ã‚£ã�¸ã�®å�‚ç…§
     * Reference to Main activity
     */
    private static Activity mActivity;

    /**
     * ã�Šå¾…ã�¡ã��ã� ã�•ã�„ãƒ€ã‚¤ã‚¢ãƒ­ã‚°
     * "Please wait" dialog
     */
    private ProgressDialog mPleaseWaitDialog;

    /**
     * åˆ�æœŸåŒ–å¤±æ•—ãƒ€ã‚¤ã‚¢ãƒ­ã‚°
     * Boot failed dialog
     */
    private AlertDialog mBootFailedDialog;

    /**
     * ã‚¹ã‚­ãƒ£ãƒ³ä¸­ãƒ€ã‚¤ã‚¢ãƒ­ã‚°
     * Scanning dialog
     */
    protected ProgressDialog mScanningDialog;

    /**
     * ã‚«ã‚¦ãƒ³ãƒˆãƒ€ã‚¦ãƒ³ã�‚ã‚Šæ¬¡åŽŸç¨¿å¾…ã�¡ãƒ€ã‚¤ã‚¢ãƒ­ã‚°
     * "Set next original" dialog : with count down
     */
    protected Dialog mCountDownDialog;

    /**
     * ã‚«ã‚¦ãƒ³ãƒˆãƒ€ã‚¦ãƒ³ã�ªã�—æ¬¡åŽŸç¨¿å¾…ã�¡ãƒ€ã‚¤ã‚¢ãƒ­ã‚°
     * "Set next original" dialog: without count down
     */
    protected Dialog mNoCountDownDialog;

    /**
     * ã‚¹ã‚­ãƒ£ãƒ³ã‚µãƒ³ãƒ—ãƒ«ã‚¢ãƒ—ãƒªã‚±ãƒ¼ã‚·ãƒ§ãƒ³
     * Scan sample application object
     */
    private static ScanSampleApplication mApplication;

    /**
     * UIã‚¹ãƒ¬ãƒƒãƒ‰ã�®ãƒ�ãƒ³ãƒ‰ãƒ©ãƒ¼
     * UI thread handler
     */
    private Handler mHandler;

    ScanStateMachine(ScanSampleApplication app, Handler handler) {
        mApplication = app;
        mHandler = handler;
    }

    /**
     * ãƒ¡ã‚¤ãƒ³ã‚¢ã‚¯ãƒ†ã‚£ãƒ“ãƒ†ã‚£ã‚’ç™»éŒ²ã�—ã�¾ã�™ã€‚
     * Registers the MainActivity.
     *
     * @param act MainActivity
     */
    public void registActivity(Activity act) {
        mActivity = act;
    }

    /**
     * çŠ¶æ…‹é�·ç§»ã‚¤ãƒ™ãƒ³ãƒˆ
     * State transition event
     */
    public enum ScanEvent {

        /**
         * ã‚¸ãƒ§ãƒ–çŠ¶æ…‹ã�ŒPENDINGï¼ˆã‚¸ãƒ§ãƒ–å®Ÿè¡Œå‰�ï¼‰ã�«é�·ç§»ã�—ã�Ÿã�“ã�¨ã‚’ç¤ºã�™ã‚¤ãƒ™ãƒ³ãƒˆ
         * The event to indicate that the job state has changed to PENDING (before the job starts)
         */
        CHANGE_JOB_STATE_PENDING,

        /**
         * ã‚¸ãƒ§ãƒ–çŠ¶æ…‹ã�ŒPROCESSINGï¼ˆã‚¸ãƒ§ãƒ–å®Ÿè¡Œä¸­ï¼‰ã�«é�·ç§»ã�—ã�Ÿã�“ã�¨ã‚’ç¤ºã�™ã‚¤ãƒ™ãƒ³ãƒˆ
         * The event to indicate that the job state has changed to PROCESSING (the job is processing)
         */
        CHANGE_JOB_STATE_PROCESSING,

        /**
         * ã‚¸ãƒ§ãƒ–çŠ¶æ…‹ã�ŒABORTEDï¼ˆã‚·ã‚¹ãƒ†ãƒ å�´ã�§èª­å�–ä¸­æ­¢ï¼‰ã�«é�·ç§»ã�—ã�Ÿã�“ã�¨ã‚’ç¤ºã�™ã‚¤ãƒ™ãƒ³ãƒˆ
         * The event to indicate that the job state has changed to ABORTED (the job is aborted by system)
         */
        CHANGE_JOB_STATE_ABORTED,

        /**
         * ã‚¸ãƒ§ãƒ–çŠ¶æ…‹ã�ŒCANCELEDï¼ˆãƒ¦ãƒ¼ã‚¶ãƒ¼æ“�ä½œã�§èª­å�–ä¸­æ­¢ï¼‰ã�«é�·ç§»ã�—ã�Ÿã�“ã�¨ã‚’ç¤ºã�™ã‚¤ãƒ™ãƒ³ãƒˆ
         * The event to indicate that the job state has changed to CANCELED (the job is canceled by user)
         */
        CHANGE_JOB_STATE_CANCELED,

        /**
         * é€�ä¿¡å‰�ãƒ—ãƒ¬ãƒ“ãƒ¥ãƒ¼çŠ¶æ…‹ã�«é�·ç§»ã�—ã�Ÿã�“ã�¨ã‚’ç¤ºã�™ã‚¤ãƒ™ãƒ³ãƒˆ
         * The event to indicate that the job state has changed to the preview state before the data is sent
         */
        CHANGE_JOB_STATE_STOPPED_PREVIEW,

        /**
         * ä¸€æ™‚å�œæ­¢ä¸­ã�®ã‚«ã‚¦ãƒ³ãƒˆãƒ€ã‚¦ãƒ³çŠ¶æ…‹ã�«é�·ç§»ã�—ã�Ÿã�“ã�¨ã‚’ç¤ºã�™ã‚¤ãƒ™ãƒ³ãƒˆ
         * The event to indicate that the job state has changed to the countdown state for pausing
         */
        CHANGE_JOB_STATE_STOPPED_COUNTDOWN,

        /**
         * ã‚¸ãƒ§ãƒ–çŠ¶æ…‹ã�ŒCOMPLETEDï¼ˆã‚¸ãƒ§ãƒ–æ­£å¸¸çµ‚äº†ï¼‰ã�«é�·ç§»ã�—ã�Ÿã�“ã�¨ã‚’ç¤ºã�™ã‚¤ãƒ™ãƒ³ãƒˆ
         * The event to indicate that the job state has changed to COMPLETED (the job ended successfully)
         */
        CHANGE_JOB_STATE_COMPLETED,


        /**
         * ã‚¸ãƒ§ãƒ–å®Ÿè¡Œã�®ãƒªã‚¯ã‚¨ã‚¹ãƒˆã‚¤ãƒ™ãƒ³ãƒˆ
         * Job start request event
         */
        REQUEST_JOB_START,

        /**
         * ã‚¸ãƒ§ãƒ–ã‚­ãƒ£ãƒ³ã‚»ãƒ«ã�®ãƒªã‚¯ã‚¨ã‚¹ãƒˆã‚¤ãƒ™ãƒ³ãƒˆ
         * Job cancel request event
         */
        REQUEST_JOB_CANCEL,

        /**
         * ã‚¸ãƒ§ãƒ–ç¶šè¡Œã�®ãƒªã‚¯ã‚¨ã‚¹ãƒˆã‚¤ãƒ™ãƒ³ãƒˆ
         * Job continue request event
         */
        REQUEST_JOB_CONTINUE,

        /**
         * ã‚¸ãƒ§ãƒ–çµ‚äº†ã�®ãƒªã‚¯ã‚¨ã‚¹ãƒˆã‚¤ãƒ™ãƒ³ãƒˆ
         * Job end request event
         */
        REQUEST_JOB_END,


        /**
         * ã‚¹ã‚­ãƒ£ãƒ³å®Ÿè¡Œä¸­ã�®æƒ…å ±å¤‰æ›´ã‚¤ãƒ™ãƒ³ãƒˆ
         * Information change event for scan job in process
         */
        UPDATE_JOB_STATE_PROCESSING,

        /**
         * ã‚¢ã‚¯ãƒ†ã‚£ãƒ“ãƒ†ã‚£ç”Ÿæˆ�ã‚¤ãƒ™ãƒ³ãƒˆ
         * MainActivity created event
         */
        ACTIVITY_CREATED,

        /**
         * ã‚¢ã‚¯ãƒ†ã‚£ãƒ“ãƒ†ã‚£çµ‚äº†ã‚¤ãƒ™ãƒ³ãƒˆ
         * MainActivity destroyed event
         */
        ACTIVITY_DESTROYED,


        /**
         * åˆ�æœŸåŒ–å®Œäº†ã‚¤ãƒ™ãƒ³ãƒˆ
         * Initialization completed event
         */
        ACTIVITY_BOOT_COMPLETED,

        /**
         * åˆ�æœŸåŒ–å¤±æ•—ã‚¤ãƒ™ãƒ³ãƒˆ
         * Initialization failed event
         */
        ACTIVITY_BOOT_FAILED,

        /**
         * ã‚¸ãƒ§ãƒ–ãƒªã‚»ãƒƒãƒˆå®Œäº†ã‚¤ãƒ™ãƒ³ãƒˆ
         * Job reset completed event
         */
        REBOOT_COMPLETED,
    }

    /**
     * ã‚¹ãƒ†ãƒ¼ãƒˆãƒžã‚·ãƒ³ã�®åˆ�æœŸçŠ¶æ…‹
     * Statemachine initial state
     */
    private State mState = State.INITIAL;

    /**
     * çŠ¶æ…‹å®šç¾©
     * State definition
     */
    public enum State {
        /**
         * åˆ�æœŸçŠ¶æ…‹
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
         * ã‚¸ãƒ§ãƒ–ç”Ÿæˆ�å¾Œã�®ã‚¸ãƒ§ãƒ–é–‹å§‹å‰�çŠ¶æ…‹
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
         * ã‚¸ãƒ§ãƒ–é–‹å§‹å¾…ã�¡çŠ¶æ…‹
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
         * ã‚¸ãƒ§ãƒ–é–‹å§‹å¾Œã�®å®Ÿè¡Œå‰�çŠ¶æ…‹
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
         * ã‚¸ãƒ§ãƒ–å®Ÿè¡Œä¸­
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
         * ã‚·ã‚¹ãƒ†ãƒ å�´ã�«ã‚ˆã‚Šã‚¸ãƒ§ãƒ–ã‚­ãƒ£ãƒ³ã‚»ãƒ«
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
         * ã‚¸ãƒ§ãƒ–ã‚­ãƒ£ãƒ³ã‚»ãƒ«ä¸­
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
         * ã‚¸ãƒ§ãƒ–ä¸€æ™‚å�œæ­¢ä¸­(æ¬¡åŽŸç¨¿å¾…ã�¡ãƒ€ã‚¤ã‚¢ãƒ­ã‚°è¡¨ç¤º)
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
         * ã‚¸ãƒ§ãƒ–ä¸€æ™‚å�œæ­¢ä¸­ï¼ˆãƒ—ãƒ¬ãƒ“ãƒ¥ãƒ¼è¡¨ç¤ºï¼‰
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
         * ã‚¸ãƒ§ãƒ–å†�é–‹å¾…ã�¡
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
         * ã‚¸ãƒ§ãƒ–ã‚­ãƒ£ãƒ³ã‚»ãƒ«å¾…ã�¡
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
         * ã‚¸ãƒ§ãƒ–çµ‚äº†å¾…ã�¡
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
         * ã‚¸ãƒ§ãƒ–æ­£å¸¸çµ‚äº†
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
         * æ¬¡ã�®çŠ¶æ…‹ã‚’å�–å¾—ã�—ã�¾ã�™ã€‚
         * å�„çŠ¶æ…‹ã�Œã‚ªãƒ¼ãƒ�ãƒ¼ãƒ©ã‚¤ãƒ‰ã�—ã�¾ã�™ã€‚
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
         * çŠ¶æ…‹ã�«å…¥ã‚‹ã�¨ã��ã�«å‘¼ã�°ã‚Œã‚‹ãƒ¡ã‚½ãƒƒãƒ‰ã�§ã�™ã€‚
         * å�„çŠ¶æ…‹ã�Œå¿…è¦�ã�«å¿œã�˜ã�¦ã‚ªãƒ¼ãƒ�ãƒ¼ãƒ©ã‚¤ãƒ‰ã�—ã�¾ã�™ã€‚
         * This method is called when entering a state.
         * Each state should override this method if necessary.
         *
         * @param sm
         * @param prm Object for additional information
         */
        public void entry(final ScanStateMachine sm, final Object prm) {
        }

        /**
         * çŠ¶æ…‹ã�‹ã‚‰æŠœã�‘ã‚‹ã�¨ã��ã�«å‘¼ã�°ã‚Œã‚‹ãƒ¡ã‚½ãƒƒãƒ‰ã�§ã�™ã€‚
         * å�„çŠ¶æ…‹ã�Œå¿…è¦�ã�«å¿œã�˜ã�¦ã‚ªãƒ¼ãƒ�ãƒ¼ãƒ©ã‚¤ãƒ‰ã�—ã�¾ã�™ã€‚
         * This method is called when exiting a state.
         * Each state should override this method if necessary.
         *
         * @param sm
         * @param prm Object for additional information
         */
        public void exit(final ScanStateMachine sm, final Object prm) {
        }


        // +++++++++++++++++++++++++++++++++++++++++
        // ã‚¢ã‚¯ã‚·ãƒ§ãƒ³é–¢æ•°
        // Action method
        // +++++++++++++++++++++++++++++++++++++++++

        /**
         * PleaseWaitç”»é�¢ã�®è¡¨ç¤º
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
         * PleaseWaitç”»é�¢ã�®æ¶ˆåŽ»
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
         * åˆ�æœŸåŒ–å¤±æ•—ç”»é�¢ã�®è¡¨ç¤º
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
         * ã‚¹ã‚­ãƒ£ãƒ³ä¸­ç”»é�¢ã�®è¡¨ç¤º
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
         * ã‚¹ã‚­ãƒ£ãƒ³ä¸­ç”»é�¢ã�®æ¶ˆåŽ»
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
         * ã‚¹ã‚­ãƒ£ãƒ³ä¸­ç”»é�¢ã�®æ›´æ–°
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
         * æ¬¡åŽŸç¨¿å¾…ã�¡ãƒ€ã‚¤ã‚¢ãƒ­ã‚°ã�®è¡¨ç¤º
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
         * æ¬¡åŽŸç¨¿å¾…ã�¡ãƒ€ã‚¤ã‚¢ãƒ­ã‚°ã�®æ¶ˆåŽ»
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
         * ãƒ—ãƒ¬ãƒ“ãƒ¥ãƒ¼ç”»é�¢ã�®è¡¨ç¤º
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
         * ãƒ—ãƒ¬ãƒ“ãƒ¥ãƒ¼ç”»é�¢ã�®æ¶ˆåŽ»
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
         * Toastãƒ¡ãƒƒã‚»ãƒ¼ã‚¸è¡¨ç¤º
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
         * ã‚¹ã‚­ãƒ£ãƒ³ã‚¸ãƒ§ãƒ–ã�®åˆ�æœŸåŒ–
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
     * çŠ¶æ…‹é�·ç§»ã‚’è¡Œã�„ã�¾ã�™ã€‚
     * Changes states.
     *
     * @param event
     */
    public void procScanEvent(final ScanEvent event) {
        procScanEvent(event, null);
    }

    /**
     * çŠ¶æ…‹é�·ç§»ã‚’è¡Œã�„ã�¾ã�™ã€‚
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
     * ã‚¹ãƒ†ãƒ¼ãƒˆãƒžã‚·ãƒ³ã�‹ã‚‰å‘¼ã�°ã‚Œã‚‹publicãƒ¡ã‚½ãƒƒãƒ‰
     * public methos called by statemachine
     *=============================================================*/

    /**
     * ãƒ€ã‚¤ã‚¢ãƒ­ã‚°ã‚’æŒ‡å®šã�•ã‚Œã�Ÿå¹…ã�§è¡¨ç¤ºã�—ã�¾ã�™ã€‚
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
     * ãƒ€ã‚¤ã‚¢ãƒ­ã‚°ã‚’ãƒ‡ãƒ•ã‚©ãƒ«ãƒˆã‚µã‚¤ã‚ºã�§è¡¨ç¤ºã�—ã�¾ã�™ã€‚
     * Displays the dialog in default size.
     * @param d dialog
     */
    private void showDialog(Dialog d) {
        showDialog(d, DEFAULT_DIALOG_WIDTH);
    }

    /**
     * ã�Šå¾…ã�¡ä¸‹ã�•ã�„ãƒ€ã‚¤ã‚¢ãƒ­ã‚°ã‚’è¡¨ç¤ºã�—ã�¾ã�™ã€‚
     * Displays please wait dialog.
     */
    public void showPleaseWaitDialog() {
        if(mPleaseWaitDialog==null || mPleaseWaitDialog.isShowing()==false) {
            mPleaseWaitDialog = createPleaseWaitDialog();
            showDialog(mPleaseWaitDialog);
        }
    }

    /**
     * ã�Šå¾…ã�¡ä¸‹ã�•ã�„ãƒ€ã‚¤ã‚¢ãƒ­ã‚°ã‚’é–‰ã�˜ã�¾ã�™ã€‚
     * Hides please wait dialog.
     */
    public void closePleaseWaitDialog() {
        if(mPleaseWaitDialog!=null && mPleaseWaitDialog.isShowing()) {
            mPleaseWaitDialog.dismiss();
            mPleaseWaitDialog = null;
        }
    }

    /**
     * åˆ�æœŸåŒ–å¤±æ•—ãƒ€ã‚¤ã‚¢ãƒ­ã‚°ã‚’è¡¨ç¤ºã�—ã�¾ã�™ã€‚
     * Displays boot failed dialog.
     */
    public void showBootFailedDialog() {
        if(mBootFailedDialog==null || mBootFailedDialog.isShowing()==false) {
            mBootFailedDialog = createBootFailedDialog();
            showDialog(mBootFailedDialog);
        }
    }

    /**
     * ã‚¹ã‚­ãƒ£ãƒ³ä¸­ãƒ€ã‚¤ã‚¢ãƒ­ã‚°ã‚’è¡¨ç¤ºã�—ã�¾ã�™ã€‚
     * Displays scanning dialog.
     */
    public void showScanningDialog() {
        if( mScanningDialog==null || mScanningDialog.isShowing()==false) {
            mScanningDialog = createScanProgressDialog();
            showDialog(mScanningDialog);
        }
    }

    /**
     * ã‚¹ã‚­ãƒ£ãƒ³ä¸­ãƒ€ã‚¤ã‚¢ãƒ­ã‚°ã‚’é–‰ã�˜ã�¾ã�™ã€‚
     * Hides scanning dialog.
     */
    public void closeScanningDialog() {
        if(mScanningDialog!=null && mScanningDialog.isShowing()) {
            mScanningDialog.cancel();
            mScanningDialog = null;
        }
    }

    /**
     * ã‚«ã‚¦ãƒ³ãƒˆãƒ€ã‚¦ãƒ³ã�‚ã‚Šæ¬¡åŽŸç¨¿å¾…ã�¡ãƒ€ã‚¤ã‚¢ãƒ­ã‚°ã‚’è¡¨ç¤ºã�—ã�¾ã�™ã€‚
     * Displays "set next original" dialog (with count down)
     */
    public void showCountDownDialog() {
        if(mCountDownDialog==null || mCountDownDialog.isShowing()==false) {
            mCountDownDialog = createCountDownDialog();
            mCountDownDialog.show();
        }
    }

    /**
     * ã‚«ã‚¦ãƒ³ãƒˆãƒ€ã‚¦ãƒ³ã�‚ã‚Šæ¬¡åŽŸç¨¿å¾…ã�¡ãƒ€ã‚¤ã‚¢ãƒ­ã‚°ã‚’é–‰ã�˜ã�¾ã�™ã€‚
     * Hides "set next original" dialog (with count down)
     */
    public void closeCountDownDialog() {
        if(mCountDownDialog!=null && mCountDownDialog.isShowing()) {
            mCountDownDialog.cancel();
            mCountDownDialog = null;
        }
    }

    /**
     * ã‚«ã‚¦ãƒ³ãƒˆãƒ€ã‚¦ãƒ³ã�ªã�—æ¬¡åŽŸç¨¿å¾…ã�¡ãƒ€ã‚¤ã‚¢ãƒ­ã‚°ã‚’è¡¨ç¤ºã�—ã�¾ã�™ã€‚
     * Displays "set next original" dialog (without count down)
     */
    public void showNoCountDownDialog() {
        if(mNoCountDownDialog==null || mNoCountDownDialog.isShowing()==false) {
            mNoCountDownDialog = createNoCountDownDialog();
            mNoCountDownDialog.show();
        }
    }

    /**
     * ã‚«ã‚¦ãƒ³ãƒˆãƒ€ã‚¦ãƒ³ã�ªã�—æ¬¡åŽŸç¨¿å¾…ã�¡ãƒ€ã‚¤ã‚¢ãƒ­ã‚°ã‚’é–‰ã�˜ã�¾ã�™ã€‚
     * Hides "set next original" dialog (without count down)
     */
    public void closeNoCountDownDialog() {
        if(mNoCountDownDialog!=null && mNoCountDownDialog.isShowing()) {
            mNoCountDownDialog.cancel();
            mNoCountDownDialog = null;
        }
    }

    /**
     * ã‚¹ã‚­ãƒ£ãƒ³ãƒ€ã‚¤ã‚¢ãƒ­ã‚°ã�®è¡¨ç¤ºã‚’æ›´æ–°ã�—ã�¾ã�™ã€‚
     * Updates scanning dialog.
     */
    public void updateScanDialogMessage(String message) {
        if(mScanningDialog!=null && mScanningDialog.isShowing()) {
            mScanningDialog.setMessage(message);
        }
    }

    /**
     * ãƒ—ãƒ¬ãƒ“ãƒ¥ãƒ¼ç”»é�¢ã�«é�·ç§»ã�—ã�¾ã�™ã€‚
     * Changes to the preview screen.
     */
    public void showPreview() {
        Intent intent = new Intent(mActivity, PreviewActivity.class);
        mActivity.startActivityForResult(intent, PreviewActivity.REQUEST_CODE_PREVIEW_ACTIVITY);
    }

    /**
     * ãƒ—ãƒ¬ãƒ“ãƒ¥ãƒ¼ç”»é�¢ã‚’æ¶ˆåŽ»ã�—ã�¾ã�™ã€‚
     * Hides preview screen
     */
    public void closePreview() {
        mActivity.finishActivity(PreviewActivity.REQUEST_CODE_PREVIEW_ACTIVITY);
    }

    /**
     * Toastãƒ¡ãƒƒã‚»ãƒ¼ã‚¸ã‚’è¡¨ç¤ºã�—ã�¾ã�™ã€‚
     * Displays a toast message.
     */
    void showToastMessage(String message) {
        Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
    }

    /*=============================================================
     * ãƒ€ã‚¤ã‚¢ãƒ­ã‚°ç”Ÿæˆ�
     * Creates dialog
     *=============================================================*/

    /**
     * ã�—ã�°ã‚‰ã��ã�Šå¾…ã�¡ä¸‹ã�•ã�„ãƒ€ã‚¤ã‚¢ãƒ­ã‚°ã‚’ç”Ÿæˆ�ã�—ã�¾ã�™ã€‚
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
     * åˆ�æœŸåŒ–å¤±æ•—ãƒ€ã‚¤ã‚¢ãƒ­ã‚°ã‚’ç”Ÿæˆ�ã�—ã�¾ã�™ã€‚
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
     * ã‚¹ã‚­ãƒ£ãƒ³ä¸­ãƒ€ã‚¤ã‚¢ãƒ­ã‚°ã‚’ç”Ÿæˆ�ã�—ã�¾ã�™ã€‚
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
     * ä¸€æ™‚å�œæ­¢ã‚¤ãƒ™ãƒ³ãƒˆä¸­ã�®ã‚«ã‚¦ãƒ³ãƒˆãƒ€ã‚¦ãƒ³ãƒ€ã‚¤ã‚¢ãƒ­ã‚°ã‚’ç”Ÿæˆ�ã�—ã�¾ã�™ã€‚
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
    * ä¸€æ™‚å�œæ­¢ã‚¤ãƒ™ãƒ³ãƒˆä¸­ã�®ã‚«ã‚¦ãƒ³ãƒˆãƒ€ã‚¦ãƒ³ã�ªã�—ãƒ€ã‚¤ã‚¢ãƒ­ã‚°ã‚’ç”Ÿæˆ�ã�—ã�¾ã�™ã€‚
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
    * ã‚¹ã‚­ãƒ£ãƒ³ã‚¸ãƒ§ãƒ–æ“�ä½œã�®é�žå�ŒæœŸã‚¿ã‚¹ã‚¯
    * The asynchronous task to start the scan job.
    *=============================================================*/

   /**
    * ScanResponseExceptionã�®ã‚¨ãƒ©ãƒ¼æƒ…å ±ã‚’æ–‡å­—åˆ—åŒ–ã�—ã�¾ã�™ã€‚
    * ãƒ•ã‚©ãƒ¼ãƒžãƒƒãƒˆã�¯ä»¥ä¸‹ã�®é€šã‚Šã�§ã�™ã€‚
    * Creates the string of the ScanResponseException error information.
    * The format is as below.
    *
    * base[separator]
    * [separator]
    * message_id: message[separator]
    * message_id: message[separator]
    * message_id: message
    *
    * @param e æ–‡å­—åˆ—åŒ–å¯¾è±¡ã�®ScanResponseException
    *          ScanResponseException to be converted as a string
    * @param base ãƒ¡ãƒƒã‚»ãƒ¼ã‚¸å…ˆé ­æ–‡å­—åˆ—
    *             Starting string of the message
    * @return ãƒ¡ãƒƒã‚»ãƒ¼ã‚¸æ–‡å­—åˆ—
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
    * ã‚¹ã‚­ãƒ£ãƒ³ã‚¸ãƒ§ãƒ–ã‚’å®Ÿè¡Œé–‹å§‹ã�™ã‚‹ã�Ÿã‚�ã�®é�žå�ŒæœŸã‚¿ã‚¹ã‚¯ã�§ã�™ã€‚
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
           Log.d(TAG, "getSelectedColorValue: " + ScanColor.AUTO_COLOR);
           Log.d(TAG, "getSelectedSideValue: " + OriginalSide.ONE_SIDE);
           Log.d(TAG, "getSelectedPreviewValue: " + OriginalPreview.OFF);
           Log.d(TAG, "getSelectedFileFormatValue: " + scanSetDataHolder.getSelectedFileFormatValue());
           Log.d(TAG, "getSelectedMultiPageValue: " + scanSetDataHolder.getSelectedMultiPageValue());

           // Sets scan attributes.
           ScanRequestAttributeSet requestAttributes;
           requestAttributes = new HashScanRequestAttributeSet();
           requestAttributes.add(AutoCorrectJobSetting.AUTO_CORRECT_ON);
           requestAttributes.add(JobMode.SCAN_AND_STORE_TEMPORARY);
           requestAttributes.add(ScanColor.AUTO_COLOR);
           requestAttributes.add(OriginalSide.ONE_SIDE);
           requestAttributes.add(OriginalPreview.OFF);

           FileSetting fileSetting = new FileSetting();
           fileSetting.setFileFormat(FileFormat.PDF);
           fileSetting.setMultiPageFormat(true);
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
    * ã‚¹ã‚­ãƒ£ãƒ³ã‚¸ãƒ§ãƒ–ã‚’ã‚­ãƒ£ãƒ³ã‚»ãƒ«ã�™ã‚‹ã�Ÿã‚�ã�®é�žå�ŒæœŸã‚¿ã‚¹ã‚¯ã�§ã�™ã€‚
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
    * ã‚¹ã‚­ãƒ£ãƒ³ã‚¸ãƒ§ãƒ–ã‚’å†�é–‹ã�™ã‚‹ã�Ÿã‚�ã�®é�žå�ŒæœŸã‚¿ã‚¹ã‚¯ã�§ã�™ã€‚
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
    * ã‚¹ã‚­ãƒ£ãƒ³ã‚¸ãƒ§ãƒ–ã‚’çµ‚äº†ã�™ã‚‹ã�Ÿã‚�ã�®é�žå�ŒæœŸã‚¿ã‚¹ã‚¯
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
