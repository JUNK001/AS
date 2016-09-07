package cn.program.astudio.as.widget.util;

import android.view.View;

/**
 * Created by JUNX on 2016/8/29.
 */
public class TapBack {
    private View view;
    private TapBackHelper.TapCallBack tapCallBack;
    private boolean isInterceptTap;
    private boolean isStack;

    public TapBack(View view, boolean isInterceptTap, TapBackHelper.TapCallBack tapCallBack) {
        this.view = view;
        this.tapCallBack = tapCallBack;
        this.isInterceptTap = isInterceptTap;
        this.isStack=false;
    }

    public boolean isStack() {
        return isStack;
    }

    public void setIsStack(boolean isStack) {
        this.isStack = isStack;
    }

    public void setView(View view) {
        this.view = view;
    }

    public View getView() {
        return view;


    }

    public TapBackHelper.TapCallBack getTapCallBack() {
        return tapCallBack;
    }

    public boolean isInterceptTap() {
        return isInterceptTap;
    }
}
