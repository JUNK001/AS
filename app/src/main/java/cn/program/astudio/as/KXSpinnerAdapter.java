package cn.program.astudio.as;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by JUNX on 2016/9/6.
 */
public class KXSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

    private Context mContext;
    private ArrayList<String> arrayList;

    public KXSpinnerAdapter(Context context,ArrayList<String> arrayList){
        this.mContext =context;
        this.arrayList=arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView=new TextView(mContext);
        textView.setText(arrayList.get(position));
        return textView;
    }
}
