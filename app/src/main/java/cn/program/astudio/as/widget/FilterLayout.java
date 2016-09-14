package cn.program.astudio.as.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.HashMap;

import cn.program.astudio.as.KXSpinnerAdapter;
import cn.program.astudio.as.R;

/**
 * Created by JUNX on 2016/8/31.
 */
public class FilterLayout extends LinearLayout {

    public final static String TAG = "FILTERFRAGMENT";

    public final static int TEXTWIDTH = 60;
    public final static int CHILDHEIGHT = 52;
    private int buttonsize;
    private int textsize;

    private int resid;

    private LinearLayout.MarginLayoutParams childlp;

    private LinearLayout.LayoutParams textlp;
    private LinearLayout.LayoutParams editlp;
    private LinearLayout.LayoutParams spinnerlp;
    private LinearLayout.LayoutParams buttonlp;

    private HashMap<String, View> items;

    private Context mContext;

    private float density;

    private OnFilterListener onFilterListener;

    public FilterLayout(Context context) {
        this(context, null);
    }

    public FilterLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilterLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public FilterLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs,defStyleAttr);

        mContext=context;

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.filter);

        textsize = 15;//a.getInt(R.styleable.filter_textsize, getResources().getDimensionPixelSize(R.dimen.text_size_15));
        buttonsize = 17;//a.getInt(R.styleable.filter_textsize, getResources().getDimensionPixelSize(R.dimen.text_size_17));

        a.recycle();

        items = new HashMap<String, View>();

        initLayoutParam();
    }

    public void setFilterRes(int resid) {
        this.resid = resid;

        initView(getResources().getXml(resid));
    }

    public void setOnFilterListener(OnFilterListener listener){
        this.onFilterListener=listener;
    }

    private int dp2sp(int dp) {
        return (int) (dp * density + 0.5);
    }

    private void initLayoutParam() {
        density = getResources().getDisplayMetrics().density;

        childlp = new LinearLayout.MarginLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp2sp(CHILDHEIGHT));
        childlp.setMargins(dp2sp(4), dp2sp(2), dp2sp(4), dp2sp(2));

        textlp = new LinearLayout.LayoutParams(dp2sp(TEXTWIDTH), ViewGroup.LayoutParams.MATCH_PARENT);
        editlp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        spinnerlp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        buttonlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp2sp(CHILDHEIGHT));
        buttonlp.gravity = Gravity.CENTER_HORIZONTAL;
    }

    private View xml(XmlResourceParser xrp, ViewGroup parent) throws Exception {
        int eventtype = xrp.next();
        String tagname = xrp.getName();
        if (tagname != null && tagname.equals("match")) {
            KXEditText cchild = new KXEditText(mContext);
            cchild.setLayoutParams(editlp);
            parent.addView(cchild);

            eventtype = xrp.next();

            return cchild;
        }
        if (tagname != null && tagname.equals("list")) {
            Spinner cchild = new Spinner(mContext);
            cchild.setLayoutParams(spinnerlp);

            ArrayList<String> array = new ArrayList<String>();
            KXSpinnerAdapter adapter = new KXSpinnerAdapter(mContext, array);


            eventtype = xrp.next();
            while (eventtype == XmlPullParser.START_TAG) {
                String value = xrp.getAttributeValue(null, "name");
                Log.d(TAG, value);
                array.add(value);

                eventtype = xrp.next();
                eventtype = xrp.next();
            }

            cchild.setAdapter(adapter);

            parent.addView(cchild);

            return cchild;
        }
        return null;
    }

    private void initView( XmlResourceParser xrp) {
        LinearLayout childview = null;

        View cchild = null;

        int eventType = -1;
        try {
            eventType = xrp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG: {
                        String tagname = xrp.getName();
                        if (tagname != null && tagname.equals("button")) {
                            String tagid = xrp.getAttributeValue(null, "name");
                            cchild = new TextView(mContext);
                            ((TextView) cchild).setTextSize(buttonsize);
                            ((TextView) cchild).setText(tagid);
                            //((TextView) cchild).setBackground(getResources().getDrawable(R.drawable.color_white2gray_press));
                            ((TextView) cchild).setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(onFilterListener!=null){
                                        onFilterListener.onFilter(getFilterParam());
                                    }
                                }
                            });
                            cchild.setLayoutParams(buttonlp);

                            addView(cchild);
                        }
                        if (tagname != null && tagname.equals("item")) {
                            childview = new LinearLayout(mContext);
                            childview.setOrientation(LinearLayout.HORIZONTAL);
                            childview.setLayoutParams(childlp);

                            String tagid = xrp.getAttributeValue(null, "name");

                            cchild = new TextView(mContext);
                            ((TextView) cchild).setTextSize(textsize);
                            ((TextView) cchild).setText(tagid);
                            cchild.setLayoutParams(textlp);
                            childview.addView(cchild);

                            View resultView=xml(xrp, childview);

                            items.put(tagid, resultView);

                            addView(childview);
                        }
                    }
                    break;
                    case XmlResourceParser.END_TAG:
                        break;
                }

                eventType = xrp.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cchild = null;
        childview = null;
    }

    public HashMap<String, String> getFilterParam () {
        HashMap<String, String> filterParams = new HashMap<String, String>();

        for(String key: items.keySet()){
            View view= items.get(key);

            if(view==null)continue;

            if(view instanceof EditText){
                filterParams.put(key,((EditText)view).getText().toString());
            }else if(view instanceof Spinner){
                filterParams.put(key,((TextView)((Spinner)view).getSelectedView()).getText().toString());
            }
        }

        return filterParams;
    }

    public interface OnFilterListener{
        void onFilter(HashMap<String, String> filterParams);
    }
}
