package cn.program.astudio.as.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Map;

import cn.program.astudio.as.AppManager;
import cn.program.astudio.as.R;
import cn.program.astudio.as.base.BaseActivity;
import cn.program.astudio.as.base.BaseFragment;
import cn.program.astudio.as.ui.fragment.DrawerNavigationFragment;
import cn.program.astudio.as.ui.fragment.FilterFragment;
import cn.program.astudio.as.ui.fragment.MainContent;
import cn.program.astudio.as.util.DoubleClickExitHelper;
import cn.program.astudio.as.widget.KXActionBarDrawerToggle;
import cn.program.astudio.as.widget.KXDrawerLayout;
import cn.program.astudio.as.widget.util.TapBackHelper;

public class MainActivity extends BaseActivity implements
        DrawerNavigationFragment.OnClickNavMenu {

    private String TAG="MAINACTIVITY";

    final String DRAWER_MENU_TAG = "drawer_menu";
    final String DRAWER_FILTER_TAG = "drawer_filter";

    private DrawerNavigationFragment mDrawerNavigationFragment;
    private DrawerNavigationFragment mDrawerNavigationFragment_1;
    private KXDrawerLayout mDrawerLayout;

    private KXActionBarDrawerToggle mDrawerToggle;
    private MainContent CONTENT[]= MainContent.values();

    // 当前显示的界面标识
    private ActionBar mActionBar;
    private MainContent currentMainContent;
    private BaseFragment currentFragment;
    private DoubleClickExitHelper mDoubleClickExit;

    private TapBackHelper mTapHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView(Bundle savedInstanceState) {
        mDoubleClickExit=new DoubleClickExitHelper(this);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

        mDrawerLayout = (KXDrawerLayout) findViewById(R.id.ly_main);
        mDrawerLayout.setDrawerListener(new DrawerListener());
        mDrawerToggle = new KXActionBarDrawerToggle(this,mDrawerLayout);
        mDrawerLayout.setDrawerTogggleListener(Gravity.LEFT, mDrawerToggle);

        mDrawerNavigationFragment=new DrawerNavigationFragment();

        if(savedInstanceState==null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.drawer_left, mDrawerNavigationFragment, DRAWER_MENU_TAG);
            ft.commit();
        }

        mTapHelper =getmTapBackHelper();

        AppManager.getAppManager().addActivity(this);;
    }

    public void toggleDrawer(int gravity){
        mDrawerLayout.toggle(gravity);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        if(currentFragment.onOptionsItemSelected(item)){
            return true;
        }
        return mDrawerToggle.onOptionsItemSelected(item)||super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
        if(currentMainContent!=null){
            getMenuInflater().inflate(currentMainContent.getActionbar_menu(), menu);
        }
        return true;
    }

    @Override
    public void onCheckChanged(int check) {
        changeMainContent(CONTENT[check]);
    }

    @Override
    public void onClickPasteboard() {

    }

    @Override
    public void onClickStarContent() {

    }

    @Override
    public void onClickSetting() {

    }

    @Override
    public void onClickExit() {
        AppManager.getAppManager().finishAllActivity();
    }

    private class DrawerListener extends KXDrawerLayout.SimpleDrawerListener {

        @Override
        public void onOpenStart(View drawerView) {
            for(int i=mTapHelper.size()-1;i>=0;i--){
                mTapHelper.tap(i);
            }
        }

        @Override
        public void onDrawerCaptured(View drawerView) {
            for(int i=mTapHelper.size()-1;i>=0;i--){
                mTapHelper.tap(i);
            }
        }
    }

    public void changeMainContent(MainContent mainContent){
        if(mainContent==null||currentMainContent==mainContent){
            return ;
        }

        MainContent lastMainContent=currentMainContent;
        currentMainContent=mainContent;
        mActionBar.setTitle(mainContent.getActionbar_title());
        supportInvalidateOptionsMenu();

        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction ft=manager.beginTransaction();
        if(lastMainContent!=null){
            Fragment lastFragment=manager.findFragmentByTag(lastMainContent.getTag());
            if(lastFragment!=null){
                ft.hide(lastFragment);
                Fragment filterFragment=manager.findFragmentByTag(currentMainContent.getTag() + "FILTER");
                if(filterFragment!=null){
                    ft.hide(filterFragment);
                }
            }
        }
        if((currentFragment=(BaseFragment)manager.findFragmentByTag(mainContent.getTag()))==null){
            try {
                currentFragment=(BaseFragment)currentMainContent.getCls().newInstance();
                if(currentMainContent.getFilterResId()!=0){

                    FilterFragment filterFragment=new FilterFragment();
                    filterFragment.setResId(currentMainContent.getFilterResId());

                    ft.add(R.id.drawer_top,filterFragment ,currentMainContent.getTag()+"FILTER");
                }

                ft.add(R.id.fl_content,currentFragment,currentMainContent.getTag());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        };
        if(currentFragment!=null){
            ft.show(currentFragment);
            Fragment filterFragment=manager.findFragmentByTag(currentMainContent.getTag() + "FILTER");
            if(filterFragment!=null){
                ft.show(filterFragment);
            }
        }
        ft.commit();
    }

    public void updateListData(Map<String,String> data){

    }
}
