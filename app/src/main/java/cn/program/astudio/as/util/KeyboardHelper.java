package cn.program.astudio.as.util;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;

import java.lang.ref.WeakReference;

/**
 * Created by JUNX on 2016/8/26.
 */
public class KeyboardHelper {

    /*public KeyboardHelper(Activity activity){
        activityWeakReference=new WeakReference<Activity>(activity);

        activity.getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {


            View mContext = null;
            mContext=activityWeakReference

            @Override
            public void onGlobalLayout() {
                if (mContent.getHeight() < mContent.getRootView().getHeight() - 200) {
                    this.isKeyBoardOpened = true;
                } else {
                    isKeyBoardOpened = false;
                }
            }
        });

        activity.getWindow().getDecorView().getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                if (newFocus instanceof EditText) {
                    isEditFocused = true;
                    newFocus.getGlobalVisibleRect(originalRect);
                } else isEditFocused = false;
            }
        });
    }

    public void bind(){



    }*/
}
