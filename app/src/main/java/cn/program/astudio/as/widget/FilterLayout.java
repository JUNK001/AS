package cn.program.astudio.as.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
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

    private HashMap<String, String> resultMap;

    private Context mContext;

    private float density;

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

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.filter);

        textsize = 17;//a.getInt(R.styleable.filter_textsize, getResources().getDimensionPixelSize(R.dimen.text_size_15));
        buttonsize = 15;//a.getInt(R.styleable.filter_textsize, getResources().getDimensionPixelSize(R.dimen.text_size_17));

        a.recycle();

        resultMap = new HashMap<String, String>();

        initLayoutParam();
    }

    public void setFilterRes(int resid) {
        this.resid = resid;

        initView(getResources().getXml(resid));
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

    private void xml(XmlResourceParser xrp, ViewGroup parent) throws Exception {
        int eventtype = xrp.next();
        String tagname = xrp.getName();
        if (tagname != null && tagname.equals("match")) {
            EditText cchild = new EditText(getContext());
            cchild.setLayoutParams(editlp);
            parent.addView(cchild);

            eventtype = xrp.next();
        }
        if (tagname != null && tagname.equals("list")) {
            Spinner cchild = new Spinner(getContext());
            cchild.setLayoutParams(spinnerlp);

            ArrayList<String> array = new ArrayList<String>();
            KXSpinnerAdapter adapter = new KXSpinnerAdapter(getContext(), array);
            cchild.setAdapter(adapter);

            eventtype = xrp.next();
            while (eventtype == XmlPullParser.START_TAG) {
                String value = xrp.getAttributeValue(null, "name");
                array.add(value);

                eventtype = xrp.next();
                eventtype = xrp.next();
            }

            adapter.notifyDataSetChanged();
            parent.addView(cchild);
        }
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
                            cchild = new TextView(getContext());
                            ((TextView) cchild).setTextSize(buttonsize);
                            ((TextView) cchild).setText(tagid);
                            cchild.setLayoutParams(buttonlp);

                            addView(cchild);
                        }
                        if (tagname != null && tagname.equals("item")) {
                            childview = new LinearLayout(getContext());
                            childview.setOrientation(LinearLayout.HORIZONTAL);
                            childview.setLayoutParams(childlp);

                            String tagid = xrp.getAttributeValue(null, "name");

                            cchild = new TextView(getContext());
                            ((TextView) cchild).setTextSize(textsize);
                            ((TextView) cchild).setGravity(Gravity.BOTTOM);
                            ((TextView) cchild).setText(tagid);
                            cchild.setLayoutParams(textlp);
                            childview.addView(cchild);

                            xml(xrp, childview);

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

    public HashMap<String, String> getResultMap() {
        HashMap<String, String> map = new HashMap<String, String>();


        return map;
    }
}
