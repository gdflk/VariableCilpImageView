package com.flk.cilpimage.view;

import android.content.Context;

import com.flk.cilpimage.FLKApp;
/**
 * 
 * @author flk
 *
 * @description �ü�����
 */
public class Options {
	//Ĭ�ϲü��߶�
	public int max_height=dp2px(FLKApp.getInstance(),300);
	public int min_height=dp2px(FLKApp.getInstance(),100);
	//Ĭ�Ͽɲü��߶���������Ļ�߾�
	public int paddingHeight=dp2px(FLKApp.getInstance(),50);
	//Ĭ�Ͽɲü������������Ļ�߾�
	public int paddingWidth=dp2px(FLKApp.getInstance(),10);
	//Ĭ�ϲü��߶�
	public int cilpHeight=dp2px(FLKApp.getInstance(),150);
	//ͼ��ɵ����Χ
	public int iconClick = 50;
	/**
	 * dpתpx
	 */
	public static int dp2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
}
