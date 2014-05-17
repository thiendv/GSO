package com.gso.hogoapi.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import com.gso.hogoapi.R;

/**
 * Created by GIANG on 4/5/14.
 */
public class TabButton extends Button implements IRadioButton {

    public static enum Type {
        left, center, right;
    }

    private Type type;

    public TabButton(Context context) {
        this(context, null);
    }

    public TabButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setType(Type.center);
        setCheck(false);
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public void setCheck(boolean checked) {
        int resId = 0;
        switch (type) {
            case left:
                resId = checked ? R.drawable.ic_btn_wooden_segmentbar_left_selected : R.drawable.ic_btn_wooden_segmentbar_left_normal;
                break;
            case center:
                resId = checked ? R.drawable.ic_btn_wooden_segmentbar_middle_selected: R.drawable.ic_btn_wooden_segmentbar_middle_normal;
                break;
            case right:
                resId = checked ? R.drawable.ic_btn_wooden_segmentbar_right_selected: R.drawable.ic_btn_wooden_segmentbar_right_normal;
                break;
        }
        if (resId != 0) {
            setBackgroundResource(resId);
        }
    }
}
