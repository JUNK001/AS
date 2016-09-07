package cn.program.astudio.as.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.program.astudio.as.R;

/**
 * Created by CC on 2016/6/16.
 */
public class DrawerNavigationFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {

    @InjectView(R.id.navmenu_rg)
    RadioGroup navmenuRg;
    @InjectView(R.id.navmenu_btn_pasteboard)
    TextView navmenuBtnPasteboard;
    @InjectView(R.id.navmenu_btn_starcontent)
    TextView navmenuBtnStarcontent;
    @InjectView(R.id.navmenu_setting)
    TextView navmenuSetting;
    @InjectView(R.id.navmenu_exit)
    TextView navmenuExit;
    @InjectView(R.id.navmenu_rbtn_problem)
    RadioButton navmenuRbtnProblem;
    @InjectView(R.id.navmenu_rbtn_content)
    RadioButton navmenuRbtnContent;
    @InjectView(R.id.navmenu_rbtn_proxy)
    RadioButton navmenuRbtnProxy;

    private OnClickNavMenu checkCallBack;

    public interface OnClickNavMenu{

        void onCheckChanged(int check);
        void onClickPasteboard();
        void onClickStarContent();
        void onClickSetting();
        void onClickExit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        checkCallBack = (OnClickNavMenu) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main_drawernavigation, container, false);
        ButterKnife.inject(this, view);
        navmenuRg = (RadioGroup) view.findViewById(R.id.navmenu_rg);
        initView();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        navmenuRg.check(navmenuRbtnProblem.getId());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        checkCallBack = null;
    }

    public void initView() {
        navmenuRg.setOnCheckedChangeListener(this);
    }

    @OnClick({R.id.navmenu_btn_pasteboard, R.id.navmenu_btn_starcontent, R.id.navmenu_setting, R.id.navmenu_exit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.navmenu_btn_pasteboard:
                checkCallBack.onClickPasteboard();
                break;
            case R.id.navmenu_btn_starcontent:
                checkCallBack.onClickStarContent();
                break;
            case R.id.navmenu_setting:
                checkCallBack.onClickSetting();
                break;
            case R.id.navmenu_exit:
                checkCallBack.onClickExit();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId==navmenuRbtnProblem.getId()){
            checkCallBack.onCheckChanged(0);
        }else if(checkedId==navmenuRbtnContent.getId()) {
            checkCallBack.onCheckChanged(1);
        }else if(checkedId==navmenuRbtnProxy.getId()) {
            checkCallBack.onCheckChanged(2);
        }else checkCallBack.onCheckChanged(-1);;
    }
}
