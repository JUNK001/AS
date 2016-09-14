package cn.program.astudio.as.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.HashMap;

import cn.program.astudio.as.R;
import cn.program.astudio.as.base.BaseFragment;
import cn.program.astudio.as.ui.MainActivity;
import cn.program.astudio.as.widget.FilterLayout;

/**
 * Created by JUNX on 2016/9/7.
 */
public class FilterFragment extends BaseFragment implements FilterLayout.OnFilterListener {

    public static final String TAG="FILTERFRAGMENT";

    private int resid=0;
    public void setResId(int resid){
        this.resid=resid;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(resid==0){
            Log.w(TAG,"must setResid before call addFragment");
        }

        View view=inflater.inflate(R.layout.fragment_filter,container,false);

        ((FilterLayout)view).setFilterRes(resid);
        ((FilterLayout)view).setOnFilterListener(this);

        return view;
    }

    @Override
    public void onFilter(HashMap<String, String> filterParams) {
        ((MainActivity)getActivity()).toggleDrawer(Gravity.TOP);
        ((MainActivity)getActivity()).updateListDataByFilter(filterParams);
    }
}
