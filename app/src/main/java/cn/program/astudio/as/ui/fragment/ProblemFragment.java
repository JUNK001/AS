package cn.program.astudio.as.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import cn.program.astudio.as.AppContext;
import cn.program.astudio.as.R;
import cn.program.astudio.as.base.BaseFragment;
import cn.program.astudio.as.ui.MainActivity;

/**
 * Created by CC on 2016/6/18.
 */
public class ProblemFragment extends BaseFragment {

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_main_problem,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        view.findViewById(R.id.et1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppContext.showToast("asd");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemid=item.getItemId();
        switch (itemid){
            case R.id.filter:{
                ((MainActivity)getActivity()).toggleDrawer(Gravity.TOP);
                return true;
            }

        }
        return false;
    }
}
