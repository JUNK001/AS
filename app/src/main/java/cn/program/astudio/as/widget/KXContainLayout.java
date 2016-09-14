package cn.program.astudio.as.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.IBinder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import java.util.HashMap;

import cn.program.astudio.as.widget.util.TapBack;
import cn.program.astudio.as.widget.util.TapBackHelper;

/**
 * Created by JUNX on 2016/8/12.
 */
public class KXContainLayout extends FrameLayout {

    private final static String TAG="KXPARENTLAYOUT";

    private int MEASUREALTER_SENSITIVE =100;
    private boolean mFirstMeasure;
    private int widthMeasureSpec;
    private int heightMeasureSpec;
    private boolean isInputMethodOpen;

    private InputMethodEvent inputMethodListener;

    public final static HashMap<Context, KXContainLayout> containLayouts=new HashMap<Context, KXContainLayout>();

    private TapBackHelper mTapBackHelper;

    private TapBack inputTapBack;

    private View inputFocusView;

    private Context mContext;

    public KXContainLayout(Context context) {
        this(context,null);
    }

    public KXContainLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KXContainLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);

        this.mContext=context;
        inputTapBack=new TapBack(inputFocusView,false,new InputTapBack());

        containLayouts.put(context,this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public KXContainLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mTapBackHelper=new TapBackHelper(context,this);
    }

    public static KXContainLayout get(Context context) {
        KXContainLayout ret=null;
        if((ret=containLayouts.get(context))==null){
            Log.w(TAG, "the activity not use KXContentParentLayout or is other context");
        }
        return ret;
    }

    public TapBackHelper getmTapBackHelper(){
        return mTapBackHelper;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFirstMeasure=true;
        isInputMethodOpen=false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mFirstMeasure=true;
        isInputMethodOpen=false;
    }

    boolean isAlterToInput(int heightMeasureSpec,int heightMeasureSpeclocal){
        int heightsize=MeasureSpec.getSize(heightMeasureSpec);
        int heightsizelocal=MeasureSpec.getSize(heightMeasureSpeclocal);

        Log.d(TAG,""+heightsize+heightsizelocal);
        return heightsize<heightsizelocal- MEASUREALTER_SENSITIVE ||heightsize>heightsizelocal+ MEASUREALTER_SENSITIVE;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(mFirstMeasure){
                mFirstMeasure=false;
                this.widthMeasureSpec=widthMeasureSpec;
                this.heightMeasureSpec=heightMeasureSpec;
        }else{
            boolean alter= isAlterToInput(heightMeasureSpec,this.heightMeasureSpec);
            if(isInputMethodOpen&&alter==false){

                Log.d(TAG,"onInputMethodClose");

                inputFocusView.clearFocus();
                isInputMethodOpen=false;
                if(inputMethodListener!=null) {
                    inputMethodListener.onInputMethodClose();
                }
            }
            if(alter){
                if(MeasureSpec.getSize(heightMeasureSpec)<MeasureSpec.getSize(this.heightMeasureSpec)){
                    if(isInputMethodOpen==false){
                        Log.d(TAG,"onInputMethodOpen");

                        this.isInputMethodOpen=true;
                        if(inputMethodListener!=null){
                            inputMethodListener.onInputMethodOpen();
                        }
                    }
                }
                widthMeasureSpec=this.widthMeasureSpec;
                heightMeasureSpec=this.heightMeasureSpec;
            }else{
                this.widthMeasureSpec=widthMeasureSpec;
                this.heightMeasureSpec=heightMeasureSpec;
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(mTapBackHelper.shouldInterceptTouchEvent(ev)){
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mTapBackHelper.processTouchEvent(event)){
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void setInputMethodListener(InputMethodEvent inputMethodListener) {
        this.inputMethodListener = inputMethodListener;
    }

    public void removeInputTapBack() {
        mTapBackHelper.remove(inputTapBack);
    }

    public void addInputTapBack(View focusEditText) {
        this.inputFocusView=focusEditText;
        inputTapBack.setView(focusEditText);
        mTapBackHelper.addTapBackLayer(inputTapBack);
    }

    public interface InputMethodEvent{
        void onInputMethodOpen();
        void onInputMethodClose();
    }

    private class InputTapBack implements TapBackHelper.TapCallBack{

        public void onTap(){
            hideKeyboard();
        }
    }

    public void hideKeyboard() {
        IBinder token= getWindowToken();
        if (token != null) {
            InputMethodManager im = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
