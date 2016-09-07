package cn.program.astudio.as.ui.fragment;

import android.support.v4.app.Fragment;

import cn.program.astudio.as.AppContext;
import cn.program.astudio.as.R;

/**
 * Created by CC on 2016/6/18.
 */
public enum MainContent {
    MAIN_PROBLEM("CONTENT_PROBLEM",ProblemFragment.class, R.menu.main_program_actionbar_menu,R.string.drawer_menu_problem_text,R.xml.filterproblem_res),
    MAIN_CONTEST("CONTENT_CONTEST",ContestFragment.class,R.menu.main_contest_actionbar_menu,R.string.drawer_menu_contest_text,R.xml.filtercontest_res),
    MAIN_PROXY("CONTENT_PROXY",ProxyFragment.class,R.menu.main_proxy_actionbar_menu,R.string.drawer_menu_proxy_text,0),
    MAIN_CPROBLEM("CONTENT_PROBLEM",CProblemFragment.class,R.menu.main_program_actionbar_menu,0,0);

    private String tag;
    private Class<? extends Fragment> cls;
    private int actionbar_menu;
    private int actionbar_title;
    private int filterResId;

    private MainContent(String tag,Class<? extends Fragment> cls, int actionbar_menu, int nav_res_name,int filter_resid){
        this.tag=tag;
        this.cls=cls;
        this.actionbar_menu=actionbar_menu;
        this.actionbar_title =nav_res_name;
        this.filterResId= filter_resid;
    }
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Class<? extends Fragment> getCls() {
        return cls;
    }

    public void setCls(Class<? extends Fragment> cls) {
        this.cls = cls;
    }

    public int getActionbar_menu() {
        return actionbar_menu;
    }

    public void setActionbar_menu(int actionbar_menu) {
        this.actionbar_menu = actionbar_menu;
    }

    public int getActionbar_title() {
        return actionbar_title;
    }

    public int getFilterResId() {
        return filterResId;
    }

    public void setFilterResId(int filterResId) {
        this.filterResId = filterResId;
    }

    public void setActionbar_title(int actionbar_title) {
        this.actionbar_title = actionbar_title;
    }
}
