package cn.program.astudio.as.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

import org.kymjs.kjframe.widget.KJScrollView;

/**
 * Created by JUNX on 2016/8/5.
 */
public class KXScrollView extends ScrollView {
    public final static String TAG="KXSCROLLVIEW";

    private static final float TOUCH_SLOP_SENSITIVITY = 0.5f;

    private ViewDragHelper mDragerHelper;
    private View contentView;

    public KXScrollView(Context context) {
        this(context, null);
    }

    public KXScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        DragerCallback callback=new DragerCallback();
        mDragerHelper=ViewDragHelper.create(this, TOUCH_SLOP_SENSITIVITY,callback );
        callback.setDragger(mDragerHelper);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        if(this.getChildCount() > 0) {
            this.contentView = this.getChildAt(0);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(mDragerHelper.shouldInterceptTouchEvent(ev)){
            requestDisallowInterceptTouchEvent(true);
            Log.d(TAG,"GET");
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        if(mDragerHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action=ev.getActionMasked();
        mDragerHelper.processTouchEvent(ev);
        switch (action){
            case MotionEvent.ACTION_UP:{
                requestDisallowInterceptTouchEvent(false);
            }
            break;
        }
        return super.onTouchEvent(ev);
    }

    private class DragerCallback extends ViewDragHelper.Callback {
        private ViewDragHelper mDragger;

        public void setDragger(ViewDragHelper dragger) {
            mDragger = dragger;
        }

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child==contentView;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            mDragger.settleCapturedViewAt(releasedChild.getLeft(), 0);
            invalidate();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return contentView.getHeight();
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return contentView.getLeft();
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return top;
        }
    }
}
