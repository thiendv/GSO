/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.app.scan.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import jp.co.ricoh.ssdk.sample.app.scan.R;
import jp.co.ricoh.ssdk.sample.app.scan.application.ScanSampleApplication;
import jp.co.ricoh.ssdk.sample.function.scan.ScanJob;
import jp.co.ricoh.ssdk.sample.function.scan.ScanThumbnail;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.ScanJobScanningInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * プレビュー画像を表示するアクティビティです。
 * The activity to display preview screen.
 */
public class PreviewActivity extends Activity{

    /**
     * このアクティビティのリクエストコード
     * Request code of preview activity
     */
    public static final int REQUEST_CODE_PREVIEW_ACTIVITY = 2000;
    private static final String TAG = PreviewActivity.class.getSimpleName();

    /**
     * サムネイルの総数
     * Total number of thumbnails.
     */
    private int mTotalPage;

    /**
     * 表示中のサムネイルのページ番号
     * Page number of the currently displayed thumbnail.
     */
    private int mCurrentPage;

    /**
     * 総ページ数のラベル
     * The text label to indicate the total page
     */
    private TextView mTotalPageNoView;

    /**
     * 現在のページ数のラベル
     * The text label to indicate the current page
     */
    private TextView mCurrentPageNoView;

    /**
     * 次のページへボタン
     * Next page button
     */
    private View mNextPageArrow;

    /**
     * 前のページへボタン
     * Previout page button
     */
    private View mPrevPageArrow;

    /**
     * プログレスバー
     * Progress bar
     */
    private ProgressBar mProgressBar;

    /**
     * サムネイル画像のイメージビュー
     * Thumbnail image view
     */
    private ImageView mPreviewImage;

    /**
     * サムネイル画像オブジェクト
     * Thumnail image object
     */
    private ScanThumbnail mThumbnail;

    /**
     * サムネイル画像を読み込むタスク
     * The task to load thumbnail image
     */
    private LoadThumbnailImageTask mLoaderTask;


    /**
     * アクティビティが生成されると呼び出されます。
     * [処理内容]
     *   (1)参照の設定
     *   (2)総ページ数取得タスクの開始
     *   (3)サムネイル画像読み込みタスクの開始
     *   (4)キャンセルボタンの設定
     *   (5)送信ボタンの設定
     *   (6)前のページへボタンの設定
     *   (7)次のページへボタンの設定
     *
     * Called when an activity is created.
     * [Processes]
     *  (1) Set reference
     *  (2) Execute the task to obtain the total number of pages
     *  (3) Execte the task to load thumbnail image
     *  (4) Set cancel button
     *  (5) Set send button
     *  (6) Set next page button
     *  (7) Set previous page button
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        //(1)
        ScanSampleApplication app = (ScanSampleApplication) getApplication();
        Log.d(TAG, "onCreate");
        mThumbnail = new ScanThumbnail(app.getScanJob());
        mTotalPageNoView = (TextView) findViewById(R.id.text_total_page);
        mCurrentPageNoView = (TextView) findViewById(R.id.text_cur_page);
        mNextPageArrow = findViewById(R.id.btn_page_next);
        mPrevPageArrow = findViewById(R.id.btn_page_prev);

        mProgressBar = (ProgressBar) findViewById(R.id.preview_proc_bar);
        mProgressBar.setVisibility(View.INVISIBLE);
        mPreviewImage = (ImageView) findViewById(R.id.image_preview);

        //(2)
        mTotalPage = 1;
        mCurrentPage = 1;
        new GetTotalPageTask().execute(app.getScanJob());

        //(3)
        mLoaderTask = new LoadThumbnailImageTask();
        mLoaderTask.execute(mCurrentPage);

        //(4)
        Button btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        //(5)
        Button btn_start = (Button)findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


        //(6)
        findViewById(R.id.btn_page_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPage >= mTotalPage) {
                    return;
                }
                mCurrentPage++;
                updatePageNavigator();

                if (mLoaderTask != null) {
                    mLoaderTask.cancel(false);
                }
                mLoaderTask = new LoadThumbnailImageTask();
                mLoaderTask.execute(mCurrentPage);
            };
        });

        //(7)
        findViewById(R.id.btn_page_prev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPage <= 1) {
                    return;
                }
                mCurrentPage--;
                updatePageNavigator();

                if (mLoaderTask != null) {
                    mLoaderTask.cancel(false);
                }
                mLoaderTask = new LoadThumbnailImageTask();
                mLoaderTask.execute(mCurrentPage);
            };
        });

    }

    /**
     * アクティビティの再開時に呼び出されます。
     * Called when the activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        sendBroadcast(new Intent(DialogUtil.INTENT_ACTION_SUB_ACTIVITY_RESUMED));
    }

    /**
     * ページ番号、次ページ・前ページ遷移用の矢印を更新します。
     * Updates the page labels and navigator images.
     */
    private void updatePageNavigator() {
        mCurrentPageNoView.setText(Integer.toString(mCurrentPage));
        mTotalPageNoView.setText(Integer.toString(mTotalPage));
        mNextPageArrow.setVisibility((mCurrentPage < mTotalPage)? View.VISIBLE : View.INVISIBLE);
        mPrevPageArrow.setVisibility((mCurrentPage > 1)? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * 読み取り枚数を取得して画面を更新する非同期タスクです。
     * The asynchronous task to obtain the total number of pages and update the screen.
     */
    private class GetTotalPageTask extends AsyncTask<ScanJob, Void, Integer> {

        @Override
        protected Integer doInBackground(ScanJob... params) {
            ScanJob scanJob = params[0];
            ScanJobScanningInfo scanningInfo = (ScanJobScanningInfo) scanJob.getJobAttribute(ScanJobScanningInfo.class);
            if (scanningInfo == null) {
                return 1;
            }
            return scanningInfo.getScannedCount();
        }

        @Override
        protected void onPostExecute(Integer result) {
            mTotalPage = result;
            updatePageNavigator();
        }

    }

    /**
     * 指定されたページのプレビュー画像を取得・表示する非同期タスクです。
     * 処理中はプログレスバーを表示します。
     * サムネイル画像をバイト配列形式で取得します。
     * 画像サイズからImageViewに収まる比率を求めた後に、画像をロードします。
     *
     * The asynchronous task to obtain and display the preview image of the specified page.
     * During the process, displays a progress bar.
     * Obtains the thumbnail image in byte array.
     * Obtains the ratio to fit within ImageView from the image size and then loads the image.
     *
     */
    private class LoadThumbnailImageTask extends AsyncTask<Integer, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            byte[] bytes = getThumbnailBytes(params[0]);
            if (bytes == null) {
                return null;
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

            float scaleW = (float)options.outWidth / mPreviewImage.getWidth();
            float scaleH = (float)options.outHeight / mPreviewImage.getHeight();
            int scale = (int) Math.ceil(Math.max(scaleW,  scaleH));

            options.inJustDecodeBounds = false;
            options.inSampleSize = calculateSampleSize(scale);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
            return bitmap;
        }

        private byte[] getThumbnailBytes(int pageNo) {
            InputStream in = mThumbnail.getThumbnailInputStream(pageNo);
            if (in == null) {
                return null;
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                byte[] buffer = new byte[1024];
                int size;
                while ((size = in.read(buffer)) != -1) {
                    out.write(buffer, 0, size);
                }
                out.flush();
                return out.toByteArray();
            } catch (IOException e) {
                return null;
            } finally {
                try {
                    out.close();
                } catch (IOException ignore) {
                }
                try {
                    in.close();
                } catch (IOException ignore) {
                }
            }
        }

        /**
         * BitmapFactory.Options.inSampleSize の設定値を計算します。
         * 引数で渡された値以上で一番近い2のべき乗を返却します。
         * Calculates the setting value of BitmapFactory.Options.inSampleSize.
         * Returns the nearest exponentiation of 2 that is equal to or larger than the value passed by the argument.
         */
        private int calculateSampleSize(int scale) {
            int result = scale;
            for (int i = 1; ; i*=2) {
                if (i >= scale) {
                    result = i;
                    break;
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (! isCancelled()) {
                mPreviewImage.setImageBitmap(result);
            }
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onCancelled() {
            mProgressBar.setVisibility(View.INVISIBLE);
        }

    }

}
