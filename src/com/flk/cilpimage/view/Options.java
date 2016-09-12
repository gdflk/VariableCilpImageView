package com.flk.cilpimage.view;

import android.content.Context;

import com.flk.cilpimage.FLKApp;
/**
 * 
 * @author flk
 *
 * @description 裁剪参数
 */
public class Options {
	//默认裁剪高度
	public int max_height=dp2px(FLKApp.getInstance(),300);
	public int min_height=dp2px(FLKApp.getInstance(),100);
	//默认可裁剪高度两边与屏幕边距
	public int paddingHeight=dp2px(FLKApp.getInstance(),50);
	//默认可裁剪宽度两边与屏幕边距
	public int paddingWidth=dp2px(FLKApp.getInstance(),10);
	//默认裁剪高度
	public int cilpHeight=dp2px(FLKApp.getInstance(),150);
	//图标可点击范围
	public int iconClick = 50;
	/**
	 * dp转px
	 */
	public static int dp2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
}
