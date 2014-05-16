/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\MD101\\Downloads\\Bookend\\src\\net\\keyring\\bookend\\callback\\ICallbackListener.aidl
 */
package net.keyring.bookend.callback;
/**
* DownloadStatus更新インターフェイス
*/
public interface ICallbackListener extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements net.keyring.bookend.callback.ICallbackListener
{
private static final java.lang.String DESCRIPTOR = "net.keyring.bookend.callback.ICallbackListener";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an net.keyring.bookend.callback.ICallbackListener interface,
 * generating a proxy if needed.
 */
public static net.keyring.bookend.callback.ICallbackListener asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof net.keyring.bookend.callback.ICallbackListener))) {
return ((net.keyring.bookend.callback.ICallbackListener)iin);
}
return new net.keyring.bookend.callback.ICallbackListener.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_updateDownloadStatus:
{
data.enforceInterface(DESCRIPTOR);
net.keyring.bookend.callback.DownloadStatus _arg0;
if ((0!=data.readInt())) {
_arg0 = net.keyring.bookend.callback.DownloadStatus.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.updateDownloadStatus(_arg0);
reply.writeNoException();
if ((_arg0!=null)) {
reply.writeInt(1);
_arg0.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements net.keyring.bookend.callback.ICallbackListener
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/**
	 * ダウンロードの状態が変更
	 */
@Override public void updateDownloadStatus(net.keyring.bookend.callback.DownloadStatus status) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((status!=null)) {
_data.writeInt(1);
status.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_updateDownloadStatus, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
status.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_updateDownloadStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
/**
	 * ダウンロードの状態が変更
	 */
public void updateDownloadStatus(net.keyring.bookend.callback.DownloadStatus status) throws android.os.RemoteException;
}
