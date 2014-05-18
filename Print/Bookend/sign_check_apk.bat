@echo off
rem ==========================================================
rem
rem .apkファイルの署名をチェック 
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

rem 署名のチェック
"%_JDK_BIN%jarsigner" -verify -verbose -certs "%~1"

popd
pause
