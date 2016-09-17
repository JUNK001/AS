package cn.program.astudio.as.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * Created by JUNX on 2016/9/13.
 */
public class KXEditText extends EditText {

    public static final String TAG="KXEDITTEXT";

    private Context mContext;

    public KXEditText(Context context) {
        this(context, null);
    }

    public KXEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext =context;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        KXContainLayout containLayout=KXContainLayout.get(mContext);
        if(containLayout==null){
            super.onFocusChanged(focused, direction, previouslyFocusedRect);

            return ;
        }

        if(focused==false){
            containLayout.removeInputTapBack();
        }else{
            containLayout.addInputTapBack(this);
        }
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }
}
