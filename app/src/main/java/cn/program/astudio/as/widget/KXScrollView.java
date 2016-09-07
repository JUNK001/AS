package cn.program.astudio.as.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

import org.kymjs.kjframe.widget.KJScrollView;

/**
 * Created by JUNX on 2016/8/5.
 */
public class KXScrollView extends ScrollView {
    private static final float MOVE_FACTOR = 0.5F;
    private static final int ANIM_TIME = 300;
    private float startY;
    private View contentView;
    private final Rect originalRect = new Rect();
    private boolean canPullDown = false;
    private boolean canPullUp = false;
    private boolean isMoved = false;

    public KXScrollView(Context context) {
        super(context);
    }

    public KXScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        if(this.getChildCount() > 0) {
            this.contentView = this.getChildAt(0);
        }
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(this.contentView != null) {
            this.originalRect.set(this.contentView.getLeft(), this.contentView.getTop(), this.contentView.getRight(), this.contentView.getBottom());
        }
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(this.contentView == null) {
            return super.dispatchTouchEvent(ev);
        } else {
            boolean isTouchOutOfScrollView = ev.getY() >= (float)this.getHeight() || ev.getY() <= 0.0F;
            if(isTouchOutOfScrollView) {
                if(this.isMoved) {
                    this.boundBack();
                }
                return true;
            } else {
                int action = ev.getAction();
                switch(action) {
                    case MotionEvent.ACTION_DOWN:
                        this.canPullDown = this.isCanPullDown();
                        this.canPullUp = this.isCanPullUp();
                        this.startY = ev.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        this.boundBack();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(!this.canPullDown && !this.canPullUp) {
                            this.startY = ev.getY();
                            this.canPullDown = this.isCanPullDown();
                            this.canPullUp = this.isCanPullUp();
                        } else {
                            float nowY = ev.getY();
                            int deltaY = (int)(nowY - this.startY);
                            boolean shouldMove = this.canPullDown && deltaY > 0 || this.canPullUp && deltaY < 0 || this.canPullUp && this.canPullDown;
                            if(shouldMove) {
                                int offset = (int)((float)deltaY * 0.5F);
                                this.contentView.layout(this.originalRect.left, this.originalRect.top + offset, this.originalRect.right, this.originalRect.bottom + offset);
                                this.isMoved = true;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if(isMoved){
                            this.boundBack();
                        }
                    default:
                        ;
                }
                return super.dispatchTouchEvent(ev);
            }
        }
    }

    public void boundBack() {
        if(this.isMoved) {
            TranslateAnimation anim = new TranslateAnimation(0.0F, 0.0F, (float)this.contentView.getTop(), (float)this.originalRect.top);
            anim.setDuration(300L);
            this.contentView.startAnimation(anim);
            this.contentView.layout(this.originalRect.left, this.originalRect.top, this.originalRect.right, this.originalRect.bottom);
            this.canPullDown = false;
            this.canPullUp = false;
            this.isMoved = false;
        }
    }

    private boolean isCanPullDown() {
        return this.getScrollY() == 0 || this.contentView.getHeight() < this.getHeight() + this.getScrollY();
    }

    private boolean isCanPullUp() {
        return this.contentView.getHeight() <= this.getHeight() + this.getScrollY();
    }
}
