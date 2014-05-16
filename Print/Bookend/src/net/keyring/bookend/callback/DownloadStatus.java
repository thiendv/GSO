package net.keyring.bookend.callback;

import net.keyring.bookend.constant.Const;
import android.os.Parcel;
import android.os.Parcelable;
/**
 * コンテンツのダウンロード状態を表すオブジェクト
 * @author Hamaji
 *
 */
public class DownloadStatus implements Parcelable,Const{
	
	/** downloadテーブルid */
	public long	mRowID;
	/** ダウンロード状態 */
	public int		mStatus = STATUS_INIT;
	/** status=2(DL中)で使用…DL進捗(%) */
	public int		mProgress;
	/** status=4(エラー発生)で使用…エラーメッセージ */
	public String	mError;
	
	public void readFromParcel(Parcel in) {
		mRowID = in.readLong();
        mStatus = in.readInt();
        mProgress = in.readInt();
        mError = in.readString();
    } 
	
	public DownloadStatus(long rowID, int status, int progress, String error) {
		this.mRowID = rowID;
        this.mStatus = status;
        this.mProgress = progress;
        this.mError = error;
    }
	
	public DownloadStatus(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		// 順番関係有...DownloadStatus(int status, int progress, String error)
		out.writeLong(mRowID);
		out.writeInt(mStatus);
		out.writeInt(mProgress);
		out.writeString(mError);
	}
	
	public static final Parcelable.Creator<DownloadStatus> CREATOR = new Parcelable.Creator<DownloadStatus>() {
		@Override
		public DownloadStatus createFromParcel(Parcel in){
			return new DownloadStatus(in);
		}
		@Override
		public DownloadStatus[] newArray(int size) {  
            return new DownloadStatus[size];  
        }
	};
}
