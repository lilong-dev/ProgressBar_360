package com.ll.progressbar;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by admin on 2016/11/28.
 */

public class DpUtil {

    public static  int dp2px(Context context, int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,value,context.getResources().getDisplayMetrics());
    }

    public static  int sp2px(Context context,int value) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,value,context.getResources().getDisplayMetrics());
    }
}
