package com.flk.cilpimage;

import android.app.Application;

public class FLKApp extends Application{

	private static FLKApp instance;
	
	public static FLKApp getInstance() {
		return instance;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}
	
}
