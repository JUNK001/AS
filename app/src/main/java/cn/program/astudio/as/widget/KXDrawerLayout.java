package cn.program.astudio.as.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ScrollerCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import cn.program.astudio.as.widget.util.TapBack;
import cn.program.astudio.as.widget.util.TapBackHelper;

/**
 * Created by JUNX on 2016/8/17.
 */
public class KXDrawerLayout extends ViewGroup {
    public static final String TAG = "KXDRAWERLAYOUT";

    private static final float TOUCH_SLOP_SENSITIVITY = 1.f;

    private static final int[] LAYOUT_ATTRS = new int[]{
            android.R.attr.layout_gravity
    };

    public static final int STATE_IDLE = ViewDragHelper.STATE_IDLE;

    public static final int STATE_DRAGGING = ViewDragHelper.STATE_DRAGGING;

    public static final int STATE_SETTLING = ViewDragHelper.STATE_SETTLING;

    private static final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };
    private static final int MIN_FLING_VELOCITY = 400;
    private ScrollerCompat mTopScroller;

    private ViewDragCallback mLeftCallback;
    private ViewDragHelper mLeftDragger;
    private View mLeftDrawer;
    private View mTopDrawer;
    private View mContent;

    private static final int DEFAULT_SCRIM_COLOR = 0x99000000;
    private int mScrimColor = DEFAULT_SCRIM_COLOR;
    private float mScrimOpacity;
    private Paint mScrimPaint = new Paint();

    //leftDragState location ViewDargHelper
    private int mTopDragState;

    private DrawerListener drawerListener;
    private int mDrawerState;

    private boolean mFirstLayout;
    private boolean isInLayout;

    private DrawerListener mleftDrawerToggle;

    private DrawerListener mTopDrawerToggle;
    private boolean disallowIntercept=false;
    private float mInitialMotionX;
    private float mInitialMotionY;

    public boolean isDrawerOpen(View view) {
        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        if ((lp.openState & LayoutParams.FLAG_IS_OPENED) > 0) {
            Log.d(TAG, "check" + getGravityName(view) + "open");
        } else {
            Log.d(TAG, "check" + getGravityName(view) + "close");
        }
        return (lp.openState & LayoutParams.FLAG_IS_OPENED) > 0;
    }

    public boolean isDrawerOpening(View view) {
        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        return (lp.openState & LayoutParams.FLAG_IS_OPENING) > 0;
    }

    public boolean isDrawerOpen(int gravity) {
        if (gravity == Gravity.LEFT) {
            return isDrawerOpen(mLeftDrawer);
        } else if (gravity == Gravity.TOP) {
            return isDrawerOpen(mTopDrawer);
        }
        return false;
    }

    public void setDrawerTogggleListener(int gravity, DrawerListener mDrawerToggle) {
        if (gravity == Gravity.LEFT) {
            mleftDrawerToggle = mDrawerToggle;
        } else {
            mTopDrawerToggle = mDrawerToggle;
        }
    }

    public interface DrawerListener {
        public void onOpenStart(View drawerView);

        public void onDrawerSlide(View drawerView, float slideOffset);

        public void onDrawerOpened(View drawerView);

        public void onDrawerClosed(View drawerView);

        public void onDrawerStateChanged(View drawerView, int newState);

        public void onDrawerCaptured(View drawerView);
    }

    public static class SimpleDrawerListener implements DrawerListener {
        public void onOpenStart(View drawerView) {
        }

        public void onDrawerSlide(View drawerView, float slideOffset) {
        }

        public void onDrawerOpened(View drawerView) {
        }

        public void onDrawerClosed(View drawerView) {
        }

        public void onDrawerStateChanged(View drawerView, int newState) {
        }

        public void onDrawerCaptured(View drawerView) {
        }
    }

    public KXDrawerLayout(Context context) {
        super(context, null);
    }

    public KXDrawerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KXDrawerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final float density = getResources().getDisplayMetrics().density;
        final float minVel = MIN_FLING_VELOCITY * density;

        mLeftCallback = new ViewDragCallback();

        mLeftDragger = ViewDragHelper.create(this, TOUCH_SLOP_SENSITIVITY, mLeftCallback);
        mLeftDragger.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
        mLeftDragger.setMinVelocity(minVel);
        mLeftCallback.setDragger(mLeftDragger);

        mTopScroller = ScrollerCompat.create(context, sInterpolator);

        mDrawerState = STATE_IDLE;
    }

    public void setDrawerListener(DrawerListener drawerListener) {
        this.drawerListener = drawerListener;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (this.disallowIntercept == disallowIntercept) {
            return;
        }
        this.disallowIntercept = disallowIntercept;
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFirstLayout = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mFirstLayout = true;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        final SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        if (ss.openDrawerGravity != Gravity.NO_GRAVITY) {
            final View toOpen = findDrawerWithGravity(ss.openDrawerGravity);
            if (toOpen != null) {
                openDrawer(toOpen);
            }
        }
    }

    private View findDrawerWithGravity(int openDrawerGravity) {
        if (openDrawerGravity == Gravity.LEFT) {
            return mLeftDrawer;
        } else {
            return mTopDrawer;
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        final SavedState ss = new SavedState(superState);

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            // Is the current child fully opened (that is, not closing)?
            boolean isOpenedAndNotClosing = (lp.openState == LayoutParams.FLAG_IS_OPENED);
            // Is the current child opening?
            boolean isClosedAndOpening = (lp.openState == LayoutParams.FLAG_IS_OPENING);
            if (isOpenedAndNotClosing || isClosedAndOpening) {
                // If one of the conditions above holds, save the child's gravity
                // so that we open that child during state restore.
                ss.openDrawerGravity = lp.gravity;
                break;
            }
        }

        return ss;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure");

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY) {
            throw new IllegalArgumentException(
                    "DrawerLayout must be measured with MeasureSpec.EXACTLY.");
        }

        Log.d(TAG, "Measure" + String.valueOf(widthSize) + " " + String.valueOf(heightSize));
        setMeasuredDimension(widthSize, heightSize);

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }

            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            if (isDrawerView(child) == false) {
                // Content views get measured at exactly the layout's size.
                final int contentWidthSpec = MeasureSpec.makeMeasureSpec(
                        widthSize - lp.leftMargin - lp.rightMargin, MeasureSpec.EXACTLY);
                final int contentHeightSpec = MeasureSpec.makeMeasureSpec(
                        heightSize - lp.topMargin - lp.bottomMargin, MeasureSpec.EXACTLY);
                child.measure(contentWidthSpec, contentHeightSpec);
            } else {
                final int drawerWidthSpec = getChildMeasureSpec(widthMeasureSpec,
                        0, lp.width);
                final int drawerHeightSpec = getChildMeasureSpec(heightMeasureSpec,
                        0, lp.height);
                child.measure(drawerWidthSpec, drawerHeightSpec);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(TAG, "onLayout");
        isInLayout = true;

        Log.d(TAG, "layout" + String.valueOf(r - l) + " " + String.valueOf(b - t));
        int width = r - l;
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }

            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            if (isDrawerView(child) == false) {
                child.layout(lp.leftMargin, lp.topMargin,
                        lp.leftMargin + child.getMeasuredWidth(),
                        lp.topMargin + child.getMeasuredHeight());
            } else {
                final int childWidth = child.getMeasuredWidth();
                final int childHeight = child.getMeasuredHeight();

                int childLeft;
                int childTop;
                final float newOffset;
                final float newOffsetTop;
                if (lp.gravity == Gravity.LEFT) {
                    childLeft = -childWidth + (int) (childWidth * lp.onScreen);
                    newOffset = (float) (childWidth + childLeft) / childWidth;

                    childTop = 0;
                    newOffsetTop = 0;
                } else {
                    childLeft = width - childWidth;
                    newOffset = 0;

                    childTop = -childHeight + (int) (childHeight * lp.onScreen);
                    newOffsetTop = (float) (childTop + childHeight) / childHeight;
                }

                boolean changeOffset = lp.gravity == Gravity.LEFT && newOffset != lp.onScreen;
                boolean changeOffsetTop = lp.gravity == Gravity.TOP && newOffsetTop != lp.onScreen;

                child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);

                if (changeOffset) {
                    updateDrawerOffset(child, newOffset);
                } else if (changeOffsetTop) {
                    updateDrawerOffset(child, newOffsetTop);
                }
            }
        }
        isInLayout = false;
        mFirstLayout = false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(disallowIntercept){
            return false;
        }
        final int action = MotionEventCompat.getActionMasked(ev);

        final boolean interceptForDrag = mLeftDragger.shouldInterceptTouchEvent(ev);

        boolean interceptForTap=false;
        switch (action){
            case MotionEvent.ACTION_DOWN:
            {
                final float x = ev.getX();
                final float y = ev.getY();
                mInitialMotionX = x;
                mInitialMotionY = y;
                final View touchedView = mLeftDragger.findTopChildUnder((int) x, (int) y);
                if (mScrimOpacity>0&&touchedView != null && isDrawerView(touchedView)==false){
                    interceptForTap=true;
                }
                break;
            }
        }
        return interceptForDrag || interceptForTap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mLeftDragger.processTouchEvent(event);

        int action=event.getActionMasked();
        switch (action){
            case MotionEvent.ACTION_UP: {
                final float x = event.getX();
                final float y = event.getY();
                final View touchedView = mLeftDragger.findTopChildUnder((int) x, (int) y);
                if (mScrimOpacity > 0 && touchedView != null && isDrawerView(touchedView) == false) {
                    final float dx = x - mInitialMotionX;
                    final float dy = y - mInitialMotionY;
                    final int slop = mLeftDragger.getTouchSlop();
                    if (dx * dx + dy * dy < slop * slop) {
                        // Taps close a dimmed open drawer but only if it isn't locked open.
                        int gravity = findDrawerOpend();
                        if (gravity != Gravity.NO_GRAVITY) {
                            closeDrawer(gravity);
                        }
                    }
                }
                break;
            }
        }
        return true;
    }

    @Override
    public void computeScroll() {
        final int childCount = getChildCount();
        float scrimOpacity = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (isDrawerView(child) == false) continue;
            final float onscreen = ((LayoutParams) child.getLayoutParams()).onScreen;
            scrimOpacity = Math.max(scrimOpacity, onscreen);
        }
        mScrimOpacity = scrimOpacity;

        boolean isNeedPostInvalidate = false;

        if (mLeftDragger.continueSettling(true)) {
            isNeedPostInvalidate = true;
        }

        if (mTopDragState == STATE_SETTLING) {
            boolean keepGoing = mTopScroller.computeScrollOffset();
            final int x = mTopScroller.getCurrX();
            final int y = mTopScroller.getCurrY();
            final int dx = x - mTopDrawer.getLeft();
            final int dy = y - mTopDrawer.getTop();

            if (dx != 0) {
                ViewCompat.offsetLeftAndRight(mTopDrawer, dx);
            }
            if (dy != 0) {
                ViewCompat.offsetTopAndBottom(mTopDrawer, dy);
            }

            if (dx != 0 || dy != 0) {
                final LayoutParams lp = (LayoutParams) mTopDrawer.getLayoutParams();
                float offsetTop = (1.0f * mTopDrawer.getHeight() + y) / mTopDrawer.getHeight();

                updateDrawerOffset(mTopDrawer, offsetTop);
            }
            if (keepGoing && x == mTopScroller.getFinalX() && y == mTopScroller.getFinalY()) {
                // Close enough. The interpolator/scroller might think we're still moving
                // but the user sure doesn't.
                mTopScroller.abortAnimation();
                keepGoing = false;
            }
            if (keepGoing) {
                isNeedPostInvalidate = true;
            } else {
                updateDrawerState(mTopDrawer, STATE_IDLE);
            }
        }

        if (isNeedPostInvalidate) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public void requestLayout() {
        if (isInLayout) {
            return;
        }
        super.requestLayout();
    }

    @Override
    protected void onFinishInflate() {
        int childcount=getChildCount();
        for(int i=0;i<childcount;i++){
            View child=getChildAt(i);
            if(isDrawerView(child)==false){
                mContent=child;
                continue;
            }
            if(checkDrawerViewAbsoluteGravity(child,Gravity.LEFT)){
                mLeftDrawer=child;
            }else if(checkDrawerViewAbsoluteGravity(child,Gravity.TOP)){
                mTopDrawer=child;
            }
        }
        super.onFinishInflate();
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {

        final boolean drawingContent = isDrawerView(child) == false;
        int clipLeft = 0, clipRight = getWidth();

        final boolean result;
        Path clipPath = new Path();
        final int restoreCount = canvas.save();
        if (drawingContent) {
            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View v = getChildAt(i);
                if (v == child || v.getVisibility() != VISIBLE || !isDrawerView(v)) {
                    continue;
                }

                if (checkDrawerViewAbsoluteGravity(v, Gravity.LEFT)) {
                    final int vright = v.getRight();
                    if (vright > clipLeft) clipLeft = vright;
                } else {
                    final int vleft = v.getLeft();
                    if (vleft < clipRight) clipRight = vleft;
                }
            }
            if (clipLeft < clipRight) {
                clipPath.addRect(clipLeft, 0, clipRight, getHeight(), Path.Direction.CW);
            }
            if (mTopDrawer.getVisibility() == View.VISIBLE && getHeight() > mTopDrawer.getBottom()) {
                clipPath.addRect(mTopDrawer.getLeft(), mTopDrawer.getBottom(), getWidth(), getHeight(), Path.Direction.CW);
            }
            canvas.clipPath(clipPath);
        }
        result = super.drawChild(canvas, child, drawingTime);
        canvas.restoreToCount(restoreCount);

        if (mScrimOpacity > 0 && drawingContent) {
            final int baseAlpha = (mScrimColor & 0xff000000) >>> 24;
            final int imag = (int) (baseAlpha * mScrimOpacity);
            final int color = imag << 24 | (mScrimColor & 0xffffff);
            mScrimPaint.setColor(color);

            canvas.drawRect(clipLeft, 0, clipRight, getHeight(), mScrimPaint);

            if (mTopDrawer.getVisibility() == View.VISIBLE) {
                canvas.drawRect(clipRight, mTopDrawer.getBottom(), getWidth(), getHeight(), mScrimPaint);
            }
        }
        return result;
    }

    //ViewDragCallBack for leftdragDrawer
    private class ViewDragCallback extends ViewDragHelper.Callback {
        private ViewDragHelper mDragger;

        public void setDragger(ViewDragHelper dragger) {
            mDragger = dragger;
        }

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child!=null && checkDrawerViewAbsoluteGravity(child, Gravity.LEFT);
        }

        @Override
        public void onViewDragStateChanged(int state) {
            updateDrawerState(mLeftDrawer, state);
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            final int childWidth = changedView.getWidth();
            float offset = (float) (childWidth + left) / childWidth;

            updateDrawerOffset(changedView, offset);

            invalidate();
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            final LayoutParams lp = (LayoutParams) capturedChild.getLayoutParams();
            if (drawerListener != null) {
                drawerListener.onDrawerCaptured(mLeftDrawer);
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            mDragger.settleCapturedViewAt(releasedChild.getLeft() > -releasedChild.getWidth() / 2 ? 0 : -releasedChild.getWidth(), releasedChild.getTop());
            invalidate();
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            mDragger.captureChildView(mLeftDrawer, pointerId);
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return child.getWidth();
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return Math.max(-child.getWidth(), Math.min(left, 0));
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return child.getTop();
        }
    }

    private void updateDrawerOffset(View mDrawer, float offset) {
        LayoutParams lp = (LayoutParams) mDrawer.getLayoutParams();
        lp.onScreen = offset;

        mDrawer.setVisibility(offset == 0f ? View.GONE : VISIBLE);

        drawerListener.onDrawerSlide(mDrawer, offset);

        DrawerListener drawerToggle = lp.gravity == Gravity.LEFT ? mleftDrawerToggle : mTopDrawerToggle;
        if (drawerToggle != null) {
            drawerToggle.onDrawerSlide(mDrawer, offset);
        }
    }

    private void updateDrawerState(View mDrawer, int activestate) {
        if (checkDrawerViewAbsoluteGravity(mDrawer, Gravity.TOP)) {
            mTopDragState = activestate;
        }
        final int leftState = mLeftDragger.getViewDragState();
        final int topState = mTopDragState;

        final int state;
        if (leftState == STATE_DRAGGING || topState == STATE_DRAGGING) {
            state = STATE_DRAGGING;
        } else if (leftState == STATE_SETTLING || topState == STATE_SETTLING) {
            state = STATE_SETTLING;
        } else {
            state = STATE_IDLE;
        }

        if (activestate == STATE_IDLE) {
            final LayoutParams lp = (LayoutParams) mDrawer.getLayoutParams();
            if (lp.onScreen == 0) {
                lp.openState = LayoutParams.FLAG_IS_CLOSED;

                drawerListener.onDrawerClosed(mDrawer);

                DrawerListener drawerToggle = lp.gravity == Gravity.LEFT ? mleftDrawerToggle : mTopDrawerToggle;
                if (drawerToggle != null) {
                    drawerToggle.onDrawerClosed(mDrawer);
                }
            } else if (lp.onScreen == 1) {
                lp.openState = LayoutParams.FLAG_IS_OPENED;

                drawerListener.onDrawerOpened(mDrawer);

                DrawerListener drawerToggle = lp.gravity == Gravity.LEFT ? mleftDrawerToggle : mTopDrawerToggle;
                if (drawerToggle != null) {
                    drawerToggle.onDrawerOpened(mDrawer);
                }
            }
        }

        if (state != mDrawerState) {
            mDrawerState = state;
            drawerListener.onDrawerStateChanged(mDrawer, state);
        }
    }

    public void closeDrawer(int gravity) {
        if (gravity == Gravity.LEFT) {
            closeDrawer(mLeftDrawer);
        } else if (gravity == Gravity.TOP) {
            closeDrawer(mTopDrawer);
        }
    }

    private void closeDrawer(View v) {
        if (v == null) {
            return;
        }

        LayoutParams lp = (LayoutParams) v.getLayoutParams();

        if (isDrawerView(v) == false || lp.openState != LayoutParams.FLAG_IS_OPENED) {
            return;
        }

        if (mFirstLayout) {
            Log.d(TAG, "closeDrawer" + getGravityName(v) + "faster");
            lp.onScreen = 0;
            lp.openState = LayoutParams.FLAG_IS_CLOSED;
        } else {
            Log.d(TAG, "closeDrawer" + getGravityName(v));
            lp.openState |= LayoutParams.FLAG_IS_CLOSING;
            if (lp.gravity == Gravity.LEFT) {
                mLeftDragger.smoothSlideViewTo(mLeftDrawer, -mLeftDrawer.getWidth(), mLeftDrawer.getTop());
            } else if (lp.gravity == Gravity.TOP) {
                mTopScroller.startScroll(mTopDrawer.getLeft(), mTopDrawer.getTop(), 0, -mTopDrawer.getHeight());
                updateDrawerState(v, STATE_SETTLING);
            }
        }

        invalidate();
    }

    private String getGravityName(View v) {
        if (checkDrawerViewAbsoluteGravity(v, Gravity.NO_GRAVITY)) {
            return "NO_GRAVITY";
        } else if (checkDrawerViewAbsoluteGravity(v, Gravity.LEFT)) {
            return "LEFT";
        } else if (checkDrawerViewAbsoluteGravity(v, Gravity.TOP)) {
            return "TOP";
        }
        return "gravity cn't be used";
    }

    public void openDrawer(int gravity) {
        if (gravity == Gravity.LEFT) {
            openDrawer(mLeftDrawer);
        } else {
            openDrawer(mTopDrawer);
        }
    }

    private void openDrawer(View v) {
        if (v == null) {
            return;
        }

        LayoutParams lp = (LayoutParams) v.getLayoutParams();
        if (isDrawerView(v) == false || lp.openState != LayoutParams.FLAG_IS_CLOSED) {
            return;
        }
        drawerListener.onOpenStart(v);

        if (mFirstLayout) {
            Log.d(TAG, "openDrawer" + getGravityName(v) + "faster");
            lp.onScreen = 1f;
            lp.openState = LayoutParams.FLAG_IS_OPENED;
        } else {
            Log.d(TAG, "openDrawer" + getGravityName(v));
            lp.openState |= LayoutParams.FLAG_IS_OPENING;
            if (lp.gravity == Gravity.LEFT) {
                mLeftDragger.smoothSlideViewTo(mLeftDrawer, 0, mLeftDrawer.getTop());
            } else if (lp.gravity == Gravity.TOP) {
                mTopScroller.startScroll(mTopDrawer.getLeft(), mTopDrawer.getTop(), 0, mTopDrawer.getHeight());

                updateDrawerState(v, STATE_SETTLING);
            }
        }
        invalidate();
    }

    public boolean toggle(int gravity) {
        if (isDrawerOpen(gravity)) {
            closeDrawer(gravity);
        } else {
            openDrawer(gravity);
        }
        return true;
    }

    public boolean checkDrawerViewAbsoluteGravity(View child, int gravity) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        return lp.gravity == gravity;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        Log.d(TAG, "generateDefaultLayoutParams");
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        Log.d(TAG, "generateLayoutParams attr");
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        Log.d(TAG, "generateLayoutParams laypam");
        return p instanceof LayoutParams
                ? new LayoutParams((LayoutParams) p)
                : p instanceof ViewGroup.MarginLayoutParams
                ? new LayoutParams((MarginLayoutParams) p)
                : new LayoutParams(p);
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        private static final int FLAG_IS_CLOSED = 0;
        private static final int FLAG_IS_OPENED = 0x1;
        private static final int FLAG_IS_OPENING = 0x2;
        private static final int FLAG_IS_CLOSING = 0x4;

        public int gravity = Gravity.NO_GRAVITY;
        private float onScreen = 0f;
        private int openState = FLAG_IS_CLOSED;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            final TypedArray a = c.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
            this.gravity = a.getInt(0, Gravity.NO_GRAVITY);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            this.gravity = source.gravity;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }
    }

    /**
     * State persisted across instances
     */
    protected static class SavedState extends BaseSavedState {
        int openDrawerGravity = Gravity.NO_GRAVITY;

        public SavedState(Parcel in) {
            super(in);
            openDrawerGravity = in.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(openDrawerGravity);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    @Override
                    public SavedState createFromParcel(Parcel source) {
                        return new SavedState(source);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    private boolean isDrawerView(View child) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        return lp.gravity != Gravity.NO_GRAVITY;
    }

    public int findDrawerOpend() {
        if (isDrawerOpen(Gravity.LEFT)) {
            return Gravity.LEFT;
        } else if (isDrawerOpen(Gravity.TOP)) {
            return Gravity.TOP;
        }
        return Gravity.NO_GRAVITY;
    }
}
