/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\Project\\HoGo\\Android\\DEMO\\GSO\\Print\\Bookend\\src\\net\\keyring\\bookend\\callback\\WebBookShelfDlCallback.aidl
 */
package net.keyring.bookend.callback;
/**
* Callback登録用インターフェイス
*/
public interface WebBookShelfDlCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements net.keyring.bookend.callback.WebBookShelfDlCallback
{
private static final java.lang.String DESCRIPTOR = "net.keyring.bookend.callback.WebBookShelfDlCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an net.keyring.bookend.callback.WebBookShelfDlCallback interface,
 * generating a proxy if needed.
 */
public static net.keyring.bookend.callback.WebBookShelfDlCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof net.keyring.bookend.callback.WebBookShelfDlCallback))) {
return ((net.keyring.bookend.callback.WebBookShelfDlCallback)iin);
}
return new net.keyring.bookend.callback.WebBookShelfDlCallback.Stub.Proxy(obj);
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
case TRANSACTION_startDownload_web:
{
data.enforceInterface(DESCRIPTOR);
net.keyring.bookend.callback.ICallbackListener _arg0;
_arg0 = net.keyring.bookend.callback.ICallbackListener.Stub.asInterface(data.readStrongBinder());
long _arg1;
_arg1 = data.readLong();
this.startDownload_web(_arg0, _arg1);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements net.keyring.bookend.callback.WebBookShelfDlCallback
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
	* Web書庫からダウンロード開始
	*/
@Override public void startDownload_web(net.keyring.bookend.callback.ICallbackListener listener, long movieID) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
_data.writeLong(movieID);
mRemote.transact(Stub.TRANSACTION_startDownload_web, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
}
static final int TRANSACTION_registerListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_removeListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_startDownload_web = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
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
	* Web書庫からダウンロード開始
	*/
public void startDownload_web(net.keyring.bookend.callback.ICallbackListener listener, long movieID) throws android.os.RemoteException;
}
