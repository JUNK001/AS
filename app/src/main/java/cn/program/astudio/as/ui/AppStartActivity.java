package cn.program.astudio.as.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.program.astudio.as.AppManager;
import cn.program.astudio.as.R;
import cn.program.astudio.as.util.UIHelper;

public class AppStartActivity extends Activity {

    private final int DELAY_TIME=2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 防止第三方跳转时出现双实例
        Activity aty = AppManager.getActivity(MainActivity.class);
        if (aty != null && !aty.isFinishing()) {
            finish();
        }
        setContentView(R.layout.activity_appstart);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                UIHelper.showMainActivity(AppStartActivity.this);
            }
        }, DELAY_TIME);
    }
}
