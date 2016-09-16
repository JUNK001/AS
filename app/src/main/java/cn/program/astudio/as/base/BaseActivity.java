package cn.program.astudio.as.base;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import cn.program.astudio.as.AppManager;
import cn.program.astudio.as.R;
import cn.program.astudio.as.widget.KXContainLayout;
import cn.program.astudio.as.widget.util.TapBack;
import cn.program.astudio.as.widget.util.TapBackHelper;

/**
 * Created by JUNX on 2016/8/26.
 */
public class BaseActivity extends AppCompatActivity {

    public final static String TAG="BASEACTIVITY";

    private KXContainLayout kxContainLayout;

    private TapBackHelper mTapBackHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        kxContainLayout = (KXContainLayout)LayoutInflater.from(this).inflate(R.layout.base_framelayout,null,false);
        FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        LayoutInflater.from(getBaseContext()).inflate(layoutResID, kxContainLayout);
        setContentView(kxContainLayout, lp);

        mTapBackHelper= kxContainLayout.getmTapBackHelper();

        AppManager.getAppManager().addActivity(this);
    }

    public TapBackHelper getmTapBackHelper(){
        return mTapBackHelper;
    }

    public KXContainLayout getKxContainLayout(){
        return kxContainLayout;
    }

    @Override
    public void finish() {
        AppManager.getAppManager().finishActivity();
    }

    protected void hideKeyboard() {
        IBinder token= getWindow().getDecorView().getWindowToken();
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
