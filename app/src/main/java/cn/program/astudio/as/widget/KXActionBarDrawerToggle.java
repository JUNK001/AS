package cn.program.astudio.as.widget;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.StringRes;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Method;

import cn.program.astudio.as.AppContext;

/**
 * Created by JUNX on 2016/8/19.
 */
public class KXActionBarDrawerToggle implements KXDrawerLayout.DrawerListener {

    public final static String TAG="KXACTIONBARDRAWERTOGGLE";

    private final Delegate mActivityImpl;
    private final KXDrawerLayout mDrawerLayout;

    private DrawerArrowDrawable mSlider;

    public KXActionBarDrawerToggle(Activity activity, KXDrawerLayout drawerLayout) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mActivityImpl = new JellybeanMr2Delegate(activity);
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            mActivityImpl = new HoneycombDelegate(activity);
        }else{
            mActivityImpl=null;
            Log.w(TAG, "Version at last 16");
        }
        mDrawerLayout = drawerLayout;
        mSlider = new DrawerArrowDrawable(mActivityImpl.getActionBarThemedContext());
    }

    public void syncState() {
        Log.d(TAG, "syncState");
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT) ){
            setProgress(1);
        } else {
            setProgress(0);
        }
        mActivityImpl.setActionBarUpIndicator(mSlider, 0);
    }

    public void setProgress(float slideOffset){
        if (slideOffset==1f ){
            mSlider.setVerticalMirror(true);
        } else if(slideOffset==0f){
            mSlider.setVerticalMirror(false);
        }
        mSlider.setProgress(slideOffset);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if (item != null && item.getItemId() == android.R.id.home) {
            mDrawerLayout.toggle(Gravity.LEFT);
            return true;
        }
        return false;
    }

    @Override
    public void onOpenStart(View drawerView) {

    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        setProgress(Math.min(1f, Math.max(0f, slideOffset)));
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        setProgress(1);
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        setProgress(0);
    }

    @Override
    public void onDrawerStateChanged(View drawerView, int newState) {

    }

    @Override
    public void onDrawerCaptured(View drawerView) {

    }

    public interface Delegate {

        /**
         * Set the Action Bar's up indicator drawable and content description.
         *
         * @param upDrawable     - Drawable to set as up indicator
         * @param contentDescRes - Content description to set
         */
        void setActionBarUpIndicator(Drawable upDrawable, @StringRes int contentDescRes);

        /**
         * Returns the context of ActionBar
         */
        Context getActionBarThemedContext();
    }

    /**
     * Delegate if SDK version is JB MR2 or newer
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private static class JellybeanMr2Delegate implements Delegate {

        final Activity mActivity;

        private JellybeanMr2Delegate(Activity activity) {
            mActivity = activity;
        }

        @Override
        public Context getActionBarThemedContext() {
            final ActionBar actionBar = mActivity.getActionBar();
            final Context context;
            if (actionBar != null) {
                context = actionBar.getThemedContext();
            } else {
                context = mActivity;
            }
            return context;
        }

        @Override
        public void setActionBarUpIndicator(Drawable drawable, int contentDescRes) {
            final ActionBar actionBar = mActivity.getActionBar();
            if (actionBar != null) {
                actionBar.setHomeAsUpIndicator(drawable);
                actionBar.setHomeActionContentDescription(contentDescRes);
            }
        }
    }

    /**
     * Delegate if SDK version is between honeycomb and JBMR2
     */
    private static class HoneycombDelegate implements Delegate {

        final Activity mActivity;
        ActionBarDrawerToggleHoneycomb.SetIndicatorInfo mSetIndicatorInfo;

        private HoneycombDelegate(Activity activity) {
            mActivity = activity;
        }

        @Override
        public Context getActionBarThemedContext() {
            final ActionBar actionBar = mActivity.getActionBar();
            final Context context;
            if (actionBar != null) {
                context = actionBar.getThemedContext();
            } else {
                context = mActivity;
            }
            return context;
        }

        @Override
        public void setActionBarUpIndicator(Drawable themeImage, int contentDescRes) {
            mActivity.getActionBar().setDisplayShowHomeEnabled(true);
            mSetIndicatorInfo = ActionBarDrawerToggleHoneycomb.setActionBarUpIndicator(
                    mSetIndicatorInfo, mActivity, themeImage, contentDescRes);
            mActivity.getActionBar().setDisplayShowHomeEnabled(false);
        }
    }

    /**
     * Get by CV
     * This class encapsulates some awful hacks.
     *
     * Before JB-MR2 (API 18) it was not possible to change the home-as-up indicator glyph
     * in an action bar without some really gross hacks. Since the MR2 SDK is not published as of
     * this writing, the new API is accessed via reflection here if available.
     *
     * Moved from Support-v4
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static class ActionBarDrawerToggleHoneycomb {
        private static final String TAG = "ActionBarDrawerToggle11";

        private static final int[] THEME_ATTRS = new int[] {
                android.R.attr.homeAsUpIndicator
        };

        public static SetIndicatorInfo setActionBarUpIndicator(SetIndicatorInfo info, Activity activity,
                                                               Drawable drawable, int contentDescRes) {
            if (true || info == null) {
                info = new SetIndicatorInfo(activity);
            }
            if (info.setHomeAsUpIndicator != null) {
                try {
                    final ActionBar actionBar = activity.getActionBar();
                    info.setHomeAsUpIndicator.invoke(actionBar, drawable);
                    info.setHomeActionContentDescription.invoke(actionBar, contentDescRes);
                } catch (Exception e) {
                    Log.w(TAG, "Cn't set home-as-up via JB-MR2 API", e);
                }
            } else if (info.upIndicatorView != null) {
                info.upIndicatorView.setImageDrawable(drawable);
            } else {
                Log.w(TAG, "Couldn't set home-as-up indicator");
            }
            return info;
        }

        public static SetIndicatorInfo setActionBarDescription(SetIndicatorInfo info, Activity activity,
                                                               int contentDescRes) {
            if (info == null) {
                info = new SetIndicatorInfo(activity);
            }
            if (info.setHomeAsUpIndicator != null) {
                try {
                    final ActionBar actionBar = activity.getActionBar();
                    info.setHomeActionContentDescription.invoke(actionBar, contentDescRes);
                    if (Build.VERSION.SDK_INT <= 19) {
                        // For API 19 and earlier, we need to manually force the
                        // action bar to generate a new content description.
                        actionBar.setSubtitle(actionBar.getSubtitle());
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Couldn't set content description via JB-MR2 API", e);
                }
            }
            return info;
        }

        public static Drawable getThemeUpIndicator(Activity activity) {
            final TypedArray a = activity.obtainStyledAttributes(THEME_ATTRS);
            final Drawable result = a.getDrawable(0);
            a.recycle();
            return result;
        }

        static class SetIndicatorInfo {
            public Method setHomeAsUpIndicator;
            public Method setHomeActionContentDescription;
            public ImageView upIndicatorView;

            SetIndicatorInfo(Activity activity) {
                try {
                    setHomeAsUpIndicator = ActionBar.class.getDeclaredMethod("setHomeAsUpIndicator",
                            Drawable.class);
                    setHomeActionContentDescription = ActionBar.class.getDeclaredMethod(
                            "setHomeActionContentDescription", Integer.TYPE);

                    // If we got the method we won't need the stuff below.
                    return;
                } catch (NoSuchMethodException e) {
                    // Oh well. We'll use the other mechanism below instead.
                }

                final View home = activity.findViewById(android.R.id.home);
                if (home == null) {
                    // Action bar doesn't have a known configuration, an OEM messed with things.
                    return;
                }

                final ViewGroup parent = (ViewGroup) home.getParent();
                final int childCount = parent.getChildCount();
                if (childCount != 2) {
                    // No idea which one will be the right one, an OEM messed with things.
                    return;
                }

                final View first = parent.getChildAt(0);
                final View second = parent.getChildAt(1);
                final View up = first.getId() == android.R.id.home ? second : first;

                if (up instanceof ImageView) {
                    // Jackpot! (Probably...)
                    upIndicatorView = (ImageView) up;
                }
            }
        }
    }
}
