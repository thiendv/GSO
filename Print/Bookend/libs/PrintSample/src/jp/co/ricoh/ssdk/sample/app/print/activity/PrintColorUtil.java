/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.app.print.activity;

import android.content.Context;
import android.content.res.Resources;

import jp.co.ricoh.ssdk.sample.app.print.R;
import jp.co.ricoh.ssdk.sample.app.print.application.PrintSampleApplication;
import jp.co.ricoh.ssdk.sample.app.print.application.PrintSettingSupportedHolder;
import jp.co.ricoh.ssdk.sample.function.print.PrintFile;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.PrintColor;

import java.util.List;
import java.util.Map;

/**
 * 印刷カラー指定に関連する処理をまとめたクラスです。
 * Print color utility class
 */
class PrintColorUtil{

    /**
     * 印刷カラーの設定値から表示文字を取得します。
     * Obtains text label from scan color setting value
     *
     * @param context
     * @param printColor
     * @return
     */
    static String getPrintColorResourceString(Context context, PrintColor printColor){
        String ret = null;
        switch (printColor) {
            case AUTO_COLOR:
                ret = context.getString(R.string.color_auto_color);
                break;
            case MONOCHROME:
                ret = context.getString(R.string.color_monochrome);
                break;
            case COLOR:
                ret = context.getString(R.string.color_color);
                break;
            case RED_AND_BLACK:
                ret = context.getString(R.string.color_red_and_black);
                break;
            case TWO_COLOR:
                ret = context.getString(R.string.color_two_color);
                break;
            case SINGLE_COLOR:
                ret = context.getString(R.string.color_single_color);
                break;
            default:
                ret = null;
        }
        return ret;
    }

    /**
     * 色を表す文字列に応じたPrintColorオブジェクトを取得します。
     * Obtains the PrintColor object from the print color text.
     *
     * @param context メインアクティビティへのコンテキスト
     *                Context of MainActivity
     * @param colorValue 色を表す文字列
     *                   Text value to indicate print color
     * @return PrintColorオブジェクト
     *         PrintColor object
     */
    static PrintColor getPrintColorFromResourceString(Context context, String colorValue) {
        Resources resource = context.getResources();
        PrintColor retColor = null;

        if(resource.getString(R.string.color_auto_color).equals(colorValue)) {
            retColor = PrintColor.AUTO_COLOR;
        } else if(resource.getString(R.string.color_monochrome).equals(colorValue)) {
            retColor = PrintColor.MONOCHROME;
        } else if(resource.getString(R.string.color_color).equals(colorValue)) {
            retColor = PrintColor.COLOR;
        } else if(resource.getString(R.string.color_red_and_black).equals(colorValue)) {
            retColor = PrintColor.RED_AND_BLACK;
        } else if(resource.getString(R.string.color_two_color).equals(colorValue)) {
            retColor = PrintColor.TWO_COLOR;
        } else if(resource.getString(R.string.color_single_color).equals(colorValue)) {
            retColor = PrintColor.SINGLE_COLOR;
        } else {
            return null;
        }

        return retColor;
    }

    /**
     * 設定可能な色を取得します。
     * Obtains the list of supported colors.
     *
     * @param context メインアクティビティのコンテキスト
     *                Context of MainActivity
     * @return 設定可能な色を示すPrintColorオブジェクトのリスト
     *         List of supported ScanColor objects.
     */
    static List<PrintColor> getSelectablePrintColorList(Context context){
        PrintSampleApplication app = (PrintSampleApplication)context.getApplicationContext();
        Map<PrintFile.PDL, PrintSettingSupportedHolder> supportedMap = app.getSettingSupportedDataHolders();
        PrintFile.PDL currentPDL = ((MainActivity)context).getSettingHolder().getSelectedPDL();

        if(null == currentPDL){
            return null;
        }

        return supportedMap.get(currentPDL).getSelectablePrintColorList();
    }

}
