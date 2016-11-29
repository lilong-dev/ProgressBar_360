package com.ll.progressbar.view;

import com.ll.progressbar.DpUtil;

/**
 * Created by admin on 2016/11/25.
 */

public class Point {
    private float x;
    private float y;
    private float radius;//小球的半径
    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public Point(float x, float y,float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x){
        this.x = x;
    }

    public void setY(float y ){
        this.y = y;
    }

    public float getRadius(){
        return radius;
    }
    public void setPointRadius(float radius){
        this.radius = radius;
    }
}
