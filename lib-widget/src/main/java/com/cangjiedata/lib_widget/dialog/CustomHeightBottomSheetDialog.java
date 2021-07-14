package com.cangjiedata.lib_widget.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;

import com.cangjiedata.lib_widget.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class CustomHeightBottomSheetDialog extends BottomSheetDialog {

    public CustomHeightBottomSheetDialog(@NonNull Context context, int peekHeight, int maxHeight) {
        super(context, R.style.Dialog_FullScreen);
        mWindow = getWindow();
        mPeekHeight = peekHeight;
        mMaxHeight = maxHeight;
    }

    private int mPeekHeight = 0;
    private int mMaxHeight = 0;
    private boolean mCreated = false;
    private Window mWindow = null;
    private BottomSheetBehavior mBottomSheetBehavior = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCreated = true;
        mBottomSheetBehavior = BottomSheetBehavior.from(mWindow.findViewById(com.google.android.material.R.id.design_bottom_sheet));
        setPeekHeight();
        setMaxHeight();
        setBottomSheetCallback();
    }

    public void setmPeekHeight(int mPeekHeight) {
        this.mPeekHeight = mPeekHeight;
        if (mCreated) {
            setPeekHeight();
        }
    }

    private void setPeekHeight() {
        if (mMaxHeight <= 0) {
            return;
        }
        mBottomSheetBehavior.setPeekHeight(mPeekHeight);
    }

    public void setmMaxHeight(int mMaxHeight) {
        this.mMaxHeight = mMaxHeight;
        if (mCreated) {
            setMaxHeight();
        }
    }

    public void setMaxHeight() {
        if (mMaxHeight <= 0) {
            return;
        }
        mWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, mMaxHeight);
        mWindow.setGravity(Gravity.BOTTOM);
    }

    public void setBottomSheetCallback() {
        if (mBottomSheetBehavior != null) {
            mBottomSheetBehavior.setBottomSheetCallback(mBottomSheetCallback);
        }
    }

    BottomSheetBehavior.BottomSheetCallback mBottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }

        @Override
        public void onSlide(@NonNull View view, float v) {

        }
    };

    private void setGravity(int gravity) {
        if (mCreated) {
            mWindow.setGravity(gravity);
        }
    }
}
