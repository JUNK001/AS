package cn.program.astudio.as.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import cn.program.astudio.as.AppContext;
import cn.program.astudio.as.R;
import cn.program.astudio.as.base.BaseFragment;

/**
 * Created by CC on 2016/6/18.
 */
public class ContestFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_main_contest,container,false);
        initView();
        return view;
    }

    public void initView(){

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }
}
