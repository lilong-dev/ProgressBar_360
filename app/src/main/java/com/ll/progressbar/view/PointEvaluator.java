package com.ll.progressbar.view;

import android.animation.TypeEvaluator;
import android.util.Log;


/**
 * PositionEvaluator位置估值器
 * Created by admin on 2016/11/25.
 */

public class PointEvaluator implements TypeEvaluator {
    private float mMaxRangeY;
    private float mRadius;
    public PointEvaluator(float maxRangeY,float radius){
        this.mMaxRangeY = maxRangeY;
        this.mRadius = radius;
    }
    @Override
    public Object evaluate(float fraction, Object startValue, Object endValue) {
        Point startPoint = (Point)startValue;
        // 调用forCurrentX()方法计算X坐标
        float x = forCurrentX(fraction,startPoint.getX());
        // 调用forCurrentY()方法计算Y坐标
        float y = forCurrentY(fraction,startPoint.getY());
        return new Point(x,y,mRadius);
    }

    /**
     * 计算X坐标
     */
    private float forCurrentX(float fraction,float currentX) {
        float resultX = currentX;
        resultX = resultX - resultX *fraction;
        return resultX;
    }

    /**
     * 计算Y坐标
     */
    private float forCurrentY(float fraction,float currentY) {
        float resultY = currentY + (float) Math.sin((6 * fraction) * Math.PI) * mMaxRangeY;// 周期为3，故为6fraction
        return resultY;
    }
}
