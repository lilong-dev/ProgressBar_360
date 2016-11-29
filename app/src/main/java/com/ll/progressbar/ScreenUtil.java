/**
 * 
 */
package com.ll.progressbar;

import java.lang.reflect.Field;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * @author ll
 *
 */
public class ScreenUtil {
	public static float getDensity(Context context) {
		WindowManager w = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display d = w.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		d.getMetrics(metrics);
		return metrics.density;
	}

	public static int getScreenWidth(Context context) {
		WindowManager w = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display d = w.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		d.getMetrics(metrics);
		return metrics.widthPixels;
	}
	public static int getScreenHeight(Context context) {
		WindowManager w = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display d = w.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		d.getMetrics(metrics);
		return metrics.heightPixels;
	}
}
