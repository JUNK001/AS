package cn.program.astudio.as.widget.util;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by JUNX on 2016/8/27.
 */
public class TapBackHelper {

    public final static String TAG="TAPBACKHELPER";

    private static final int DEFAULT_SCRIM_COLOR = 0x99ffffff;
    private Paint mScrimPaint = new Paint();

    private LinkedList<TapBack> tapBacks;
    private float mInitPointX;
    private float mInitPointY;
    private View mContent;
    private int touchDownTapLayersNum =0;

    private int mTouchSlop;

    private boolean isTapPerforming;

    public final static HashMap<Context,TapBackHelper> tapBackHelpers=new HashMap<Context,TapBackHelper>();

    public TapBackHelper(Context context, View view){
        tapBacks =new LinkedList<TapBack>();
        this.mContent =view;

        mTouchSlop=ViewConfiguration.get(context).getScaledTouchSlop();
        mScrimPaint.setColor(DEFAULT_SCRIM_COLOR);

        tapBackHelpers.put(context,this);
    }

    public static TapBackHelper get(Context context) {
        if(tapBackHelpers.get(context)==null){
            Log.w(TAG, "the activity or other context not use BaseActivity or KXContentParentLayout");
        }
        return tapBackHelpers.get(context);
    }

    public int size(){
        return tapBacks.size();
    }

    public boolean shouldInterceptTouchEvent(MotionEvent ev){
        final float x=ev.getX();
        final float y=ev.getY();

        int action=ev.getActionMasked();

        if(isTapPerforming||touchDownTapLayersNum !=0&&action==MotionEvent.ACTION_DOWN){
            return true;
        }

        boolean needIntercept=false;
        switch (action){
            case MotionEvent.ACTION_DOWN:
                mInitPointX=x;
                mInitPointY=y;
                touchDownTapLayersNum =0;
                TapBack curtapBack=null;
                Iterator iterator= tapBacks.descendingIterator();
                while(needIntercept==false&&iterator.hasNext()){
                    curtapBack=(TapBack)iterator.next();
                    View curView=curtapBack.getView();
                    Rect curRect=getRectLocation(curView);
                    if((x>=curRect.left&&x<=curRect.right&&y>=curRect.top&&y<=curRect.bottom)==false){
                        touchDownTapLayersNum++;
                        if(curtapBack.isInterceptTap()==Boolean.TRUE){
                            needIntercept=true;
                        }
                    }else{
                        break;
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                float dx=x-mInitPointX;
                float dy=y-mInitPointY;
                boolean isTapTouch=dx *dx +dy*dy<mTouchSlop*mTouchSlop;
                if(isTapTouch){
                    while(touchDownTapLayersNum >0) {
                        Log.d(TAG,"ontap");
                        isTapPerforming=true;
                        tapBacks.getLast().getTapCallBack().onTap();
                        touchDownTapLayersNum--;
                    }
                }else {
                    touchDownTapLayersNum =0;
                }

                break;
        }
        return needIntercept;
    }

    public boolean processTouchEvent(MotionEvent event){
        if(touchDownTapLayersNum ==0){
            return false;
        }
        final float x=event.getX();
        final float y=event.getY();

        int action=event.getActionMasked();
        switch (action){
            case MotionEvent.ACTION_UP:
                float dx=x-mInitPointX;
                float dy=y-mInitPointY;
                boolean isTapTouch=dx *dx +dy*dy<mTouchSlop*mTouchSlop;
                if(isTapTouch){
                    while(touchDownTapLayersNum >0) {
                        Log.d(TAG,"ontap");
                        isTapPerforming=true;
                        tapBacks.getLast().getTapCallBack().onTap();
                        touchDownTapLayersNum--;
                    }
                }else {
                    touchDownTapLayersNum =0;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                touchDownTapLayersNum =0;
                break;
        }
        return true;
    }

    private Rect getRectLocation(View view) {
        View curView=view;
        Rect ret=new Rect(curView.getLeft(),curView.getTop(),curView.getRight(),curView.getBottom());
        while(curView.getParent()!=null&& mContent.equals((View)curView.getParent())==false){
            ViewGroup parent= (ViewGroup) curView.getParent();
            int dx=parent.getLeft();
            int dy=parent.getTop();
            ret.left+=dx;
            ret.right+=dx;
            ret.top+=dy;
            ret.bottom+=dy;
            curView=(View)parent;
        }
        return ret;
    }

    public void addTapBackLayer(TapBack tapBack){
        Log.d(TAG,"TapBackAdd");
        tapBack.setIsStack(true);
        tapBacks.addLast(tapBack);
    }

    public void remove(TapBack tapBack) {
        Log.d(TAG,"TapBackDel");
        if(tapBacks.isEmpty()==false&& tapBacks.getLast()==tapBack){
            tapBacks.remove(tapBack);
            tapBack.setIsStack(false);
            isTapPerforming=false;
        }else{
            Log.w(TAG,"last tapBack may not be remove");
        }
    }

    public void tap(int index){
        if(tapBacks.isEmpty()==false){
            TapBack tapBack= tapBacks.get(index);
            tapBack.getTapCallBack().onTap();
        }
    }

    public void setIsTapPerforming(boolean isTapPerforming) {
        this.isTapPerforming = isTapPerforming;
    }

    public interface TapCallBack {
        void onTap();
    }
}
