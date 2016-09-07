package cn.program.astudio.as.base;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import cn.program.astudio.as.AppConfig;
import cn.program.astudio.as.AppContext;
import cn.program.astudio.as.AppManager;
import cn.program.astudio.as.R;
import cn.program.astudio.as.util.DoubleClickExitHelper;
import cn.program.astudio.as.widget.KXContentParentLayout;
import cn.program.astudio.as.widget.util.TapBack;
import cn.program.astudio.as.widget.util.TapBackHelper;

/**
 * Created by JUNX on 2016/8/26.
 */
public class BaseActivity extends AppCompatActivity implements KXContentParentLayout.InputMethodEvent {

    public final static String TAG="BASEACTIVITY";

    private final int INPUTREMEASURE_TIME=100;

    protected boolean isKeyBoardOpened;

    private KXContentParentLayout kxContentParentLayout;

    private TapBackHelper mTapBackHelper;

    private TapBack inputTapBack;
    private View focusView;

    private boolean isFocusChange;

    private Runnable inputTapRunnable=new Runnable() {
        @Override
        public void run() {
            if(isFocusChange==false){
                hideKeyboard();
            }
            mTapBackHelper.setIsTapPerforming(false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputTapBack = new TapBack(null, false, new TapBackHelper.TapCallBack() {
            @Override
            public void onTap() {
                isFocusChange=false;
                kxContentParentLayout.postDelayed(inputTapRunnable,INPUTREMEASURE_TIME);
            }
        });
    }

    @Override
    public void setContentView(int layoutResID) {
        kxContentParentLayout = (KXContentParentLayout)LayoutInflater.from(this).inflate(R.layout.base_framelayout,null,false);
        FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        LayoutInflater.from(getBaseContext()).inflate(layoutResID, kxContentParentLayout);
        setContentView(kxContentParentLayout, lp);

        mTapBackHelper= kxContentParentLayout.getmTapBackHelper();
        kxContentParentLayout.setInputMethodListener(this);

        kxContentParentLayout.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                if ((oldFocus != null && oldFocus.getId() != -1 || newFocus != null && newFocus.getId() != -1) && newFocus instanceof EditText) {
                    Log.d(TAG,""+(oldFocus!=null?oldFocus.getId():"--")+" "+(newFocus!=null?newFocus.getId():"--"));

                    focusView = (newFocus != null && newFocus.getId() != -1) ? newFocus : null;

                    isFocusChange = true;

                    inputTapBack.setView(focusView);
                }
            }
        });

        AppManager.getAppManager().addActivity(this);
    }

    public TapBackHelper getmTapBackHelper(){
        return mTapBackHelper;
    }

    @Override
    public void finish() {
        AppManager.getAppManager().finishActivity();
    }

    public void hideKeyboard() {
        IBinder token=getWindow().getDecorView().getWindowToken();
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }

        if(inputTapBack!=null&&inputTapBack.isStack()){
            mTapBackHelper.remove(inputTapBack);
        }
    }

    @Override
    public void onInputMethodOpen() {
        Log.d(TAG, "onInputMethodOpen");
        mTapBackHelper.addTapBackLayer(inputTapBack);
    }

    @Override
    public void onInputMethodClose() {
        if(inputTapBack!=null&&inputTapBack.isStack()){
            mTapBackHelper.remove(inputTapBack);
        }
        if(focusView!=null){
            focusView.clearFocus();
        }
        Log.d(TAG,"onInputMethodClose");
    }
}
