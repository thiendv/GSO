/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\Project\\HoGo\\Android\\DEMO\\GSO\\Print\\Bookend\\src\\net\\keyring\\bookend\\callback\\NewDownloadCallback.aidl
 */
package net.keyring.bookend.callback;
/**
* Callback登録用インターフェイス
*/
public interface NewDownloadCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements net.keyring.bookend.callback.NewDownloadCallback
{
private static final java.lang.String DESCRIPTOR = "net.keyring.bookend.callback.NewDownloadCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an net.keyring.bookend.callback.NewDownloadCallback interface,
 * generating a proxy if needed.
 */
public static net.keyring.bookend.callback.NewDownloadCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof net.keyring.bookend.callback.NewDownloadCallback))) {
return ((net.keyring.bookend.callback.NewDownloadCallback)iin);
}
return new net.keyring.bookend.callback.NewDownloadCallback.Stub.Proxy(obj);
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
case TRANSACTION_registerListener:
{
data.enforceInterface(DESCRIPTOR);
net.keyring.bookend.callback.ICallbackListener _arg0;
_arg0 = net.keyring.bookend.callback.ICallbackListener.Stub.asInterface(data.readStrongBinder());
this.registerListener(_arg0);
return true;
}
case TRANSACTION_removeListener:
{
data.enforceInterface(DESCRIPTOR);
net.keyring.bookend.callback.ICallbackListener _arg0;
_arg0 = net.keyring.bookend.callback.ICallbackListener.Stub.asInterface(data.readStrongBinder());
this.removeListener(_arg0);
return true;
}
case TRANSACTION_callback:
{
data.enforceInterface(DESCRIPTOR);
this.callback();
return true;
}
case TRANSACTION_setProgress:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setProgress(_arg0);
return true;
}
case TRANSACTION_initDlStatus:
{
data.enforceInterface(DESCRIPTOR);
this.initDlStatus();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements net.keyring.bookend.callback.NewDownloadCallback
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
/*
	* コールバックインターフェイス登録処理
	*/
@Override public void registerListener(net.keyring.bookend.callback.ICallbackListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerListener, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
/**
	* コールバックインターフェイス登録解除処理
	*/
@Override public void removeListener(net.keyring.bookend.callback.ICallbackListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_removeListener, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
/**
	* ダウンロード中のものがある場合はcallback処理を行う
	*/
@Override public void callback() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_callback, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
/**
	* ダウンロード進捗をセット
	*/
@Override public void setProgress(int progress) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(progress);
mRemote.transact(Stub.TRANSACTION_setProgress, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
/**
	* DownloadStatusをnullに,ダウンロードフラグにfalseをセット
	*/
@Override public void initDlStatus() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_initDlStatus, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
}
static final int TRANSACTION_registerListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_removeListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_callback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_setProgress = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_initDlStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
/*
	* コールバックインターフェイス登録処理
	*/
public void registerListener(net.keyring.bookend.callback.ICallbackListener listener) throws android.os.RemoteException;
/**
	* コールバックインターフェイス登録解除処理
	*/
public void removeListener(net.keyring.bookend.callback.ICallbackListener listener) throws android.os.RemoteException;
/**
	* ダウンロード中のものがある場合はcallback処理を行う
	*/
public void callback() throws android.os.RemoteException;
/**
	* ダウンロード進捗をセット
	*/
public void setProgress(int progress) throws android.os.RemoteException;
/**
	* DownloadStatusをnullに,ダウンロードフラグにfalseをセット
	*/
public void initDlStatus() throws android.os.RemoteException;
}
