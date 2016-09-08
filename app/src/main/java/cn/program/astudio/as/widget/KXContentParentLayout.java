package cn.program.astudio.as.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.IBinder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import cn.program.astudio.as.widget.util.TapBack;
import cn.program.astudio.as.widget.util.TapBackHelper;

/**
 * Created by JUNX on 2016/8/12.
 */
public class KXContentParentLayout extends FrameLayout {

    private final static String TAG="KXPARENTLAYOUT";

    private int MEASUREALTER_SENSITIVE =100;
    private boolean mFirstMeasure;
    private int widthMeasureSpec;
    private int heightMeasureSpec;
    private boolean isInputMethodOpen;

    private InputMethodEvent inputMethodListener;

    private TapBackHelper mTapBackHelper;

    public KXContentParentLayout(Context context) {
        this(context,null);
    }

    public KXContentParentLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KXContentParentLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public KXContentParentLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mTapBackHelper=new TapBackHelper(context,this);
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

                isInputMethodOpen=false;
                if(inputMethodListener!=null){
                    inputMethodListener.onInputMethodClose();
                }
            }
            if(alter){
                if(MeasureSpec.getSize(heightMeasureSpec)<MeasureSpec.getSize(this.heightMeasureSpec)){
                    if(isInputMethodOpen==false){
                        if(inputMethodListener!=null){
                            inputMethodListener.onInputMethodOpen();
                        }
                        this.isInputMethodOpen=true;
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

    public interface InputMethodEvent{
        void onInputMethodOpen();
        void onInputMethodClose();
    }
}
