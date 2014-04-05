package com.gso.hogoapi.views;

import android.view.View;

/**
 * Created by TALE on 3/20/2014.
 */
public class RadioGroupController implements View.OnClickListener {

    private IRadioButton[] radioButtons;
    private int selectedIndex;
    private OnCheckedChangeListener onCheckedChangeListener;

    public void setRadioButtons(IRadioButton... buttons) {
        if (buttons == null || buttons.length == 0) {
            return;
        }

        radioButtons = new IRadioButton[buttons.length];
        for (int i = 0; i < buttons.length; i++) {
            final View view = (View) buttons[i];
            view.setTag(i);
            view.setOnClickListener(this);
            radioButtons[i] = (IRadioButton) view;
        }
        selectedIndex = 0;
        radioButtons[selectedIndex].setCheck(true);
    }

    @Override
    public void onClick(View v) {
        setSelection(v);
    }

    public void setSelection(int position) {
        if (radioButtons == null || radioButtons.length <= position || position < 0) {
            return;
        }

        setSelection(((View) radioButtons[position]));

    }

    public int getCheckedRadioButtonId() {
        return ((View) radioButtons[selectedIndex]).getId();
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public static interface OnCheckedChangeListener {
        void onCheckedChanged(int checkedId);
    }

    private void setSelection(View v) {
        if (v == radioButtons[selectedIndex]) {
            return;
        }

        ((IRadioButton) v).setCheck(true);
        if (selectedIndex >= 0) {
            radioButtons[selectedIndex].setCheck(false);
        }
        selectedIndex = (Integer) v.getTag();
        if (onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChanged(v.getId());
        }
    }
}
