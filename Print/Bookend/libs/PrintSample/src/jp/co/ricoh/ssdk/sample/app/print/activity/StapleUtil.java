/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */

package jp.co.ricoh.ssdk.sample.app.print.activity;

import android.content.Context;

import jp.co.ricoh.ssdk.sample.app.print.R;
import jp.co.ricoh.ssdk.sample.app.print.application.PrintSampleApplication;
import jp.co.ricoh.ssdk.sample.app.print.application.PrintSettingSupportedHolder;
import jp.co.ricoh.ssdk.sample.function.print.PrintFile;
import jp.co.ricoh.ssdk.sample.function.print.PrintFile.PDL;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.Staple;

import java.util.List;
import java.util.Map;

/**
 * ステープル設定に関連する処理をまとめたクラスです。
 * Staple setting utility class.
 */
public class StapleUtil{

    /**
     * ステープル設定を示す文字列を取得します。
     * Obtains the text label to indicate staple setting.
     *
     * @param context メインアクティビティのコンテキスト
     *                Context of MainActivity
     * @param staple ステープル設定を表すStapleオブジェクト
     *               Staple setting object
     * @return ステープル設定を示す文字列
     *         String to indicate staple setting
     */
    public static String getStapleString(Context context, Staple staple){
        String ret = null;
         switch (staple) {
             case DUAL_LEFT:
                 ret = context.getString(R.string.staple_dual_left);
                                 break;
             case DUAL_RIGHT:
                 ret = context.getString(R.string.staple_dual_right);
                 break;
             case DUAL_TOP:
                 ret = context.getString(R.string.staple_dual_top);
                 break;
             case SADDLE_STITCH:
                 ret = context.getString(R.string.staple_saddle_stitch);
                 break;
             case TOP_LEFT:
                 ret = context.getString(R.string.staple_top_left);
                 break;
             case TOP_LEFT_SLANT:
                 ret = context.getString(R.string.staple_top_left_slant);
                 break;
             case TOP_RIGHT:
                 ret = context.getString(R.string.staple_top_right);
                 break;
             case TOP_RIGHT_SLANT:
                 ret = context.getString(R.string.staple_top_right_slant);
                 break;
             case BOTTOM_LEFT:
                 ret = context.getString(R.string.staple_bottom_left);
                 break;
             case BOTTOMLEFT_SLANT:
                 ret = context.getString(R.string.staple_bottom_left_slant);
                 break;
             case NONE:
                 ret = context.getString(R.string.staple_none);
                 break;
         }
         return ret;
     }

    /**
     * 設定可能なステープル設定の一覧を取得します。
     * Obtains the list of supported staple setting.
     *
     * @param context メインアクティビティのコンテキスト
     *                Context of MainAcitivity
     * @return ステープル設定の一覧
     *         List of the supported Staple objects.
     */
    public static List<Staple> getSelectableStapleList(Context context){

        PrintSampleApplication app = (PrintSampleApplication)((MainActivity)context).getApplication();
        Map<PDL, PrintSettingSupportedHolder> supportedMap = app.getSettingSupportedDataHolders();
        PrintFile.PDL currentPDL = ((MainActivity)context).getSettingHolder().getSelectedPDL();

        if(null == currentPDL){
            return null;
        }

        return supportedMap.get(currentPDL).getSelectableStapleList();
    }
}