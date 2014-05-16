@echo off
rem ==========================================================
rem
rem .apkファイルに署名を付ける 
rem
rem ==========================================================

rem パラメータ
rem ------------------------------------------------
set _JDK_BIN=C:\Program Files\Java\jdk1.6.0_16\bin\
set _KEY_STORE=bookend.keystore
set _ALIES_NAME=bookend
set _PASSWORD=bookend
rem ------------------------------------------------

rem このファイルのフォルダに移動
pushd "%~dp0"

rem 署名
"%_JDK_BIN%jarsigner" -verbose -sigalg MD5withRSA -digestalg SHA1 -keystore %_KEY_STORE% -storepass %_PASSWORD% "%~1" %_ALIES_NAME%

popd
pause
