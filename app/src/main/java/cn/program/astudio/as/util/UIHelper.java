package cn.program.astudio.as.util;

import android.content.Context;
import android.content.Intent;

import cn.program.astudio.as.ui.MainActivity;

/**
 * 界面帮助类
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2014年10月10日 下午3:33:36
 * 
 */
public class UIHelper {

    //null
    public static void sendAppCrashReport(final Context context) {

    }

    public static void showMainActivity(Context context) {
        Intent intent=new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
}
