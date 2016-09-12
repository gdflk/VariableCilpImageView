package com.flk.cilpimage;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import com.flk.cilpimage.view.CilpImageLayout;
import com.flk.cilpimage.view.Options;

public class CilpImageActivity extends Activity{
	private CilpImageLayout layout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cilpimage);
		
		layout = (CilpImageLayout) findViewById(R.id.cilpimage_layout);
		layout.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.image));
		//≤√ºÙ≈‰÷√
//		Options options = new Options();
//		options.min_height = Options.dp2px(FLKApp.getInstance(),200);
//		options.cilpHeight = Options.dp2px(FLKApp.getInstance(),200);
//		
//		layout.setOptions(options);
	}
	
	public void getimage(View v) {
		MainActivity.img = layout.cilpBitmap();
		setResult(RESULT_OK);
		finish();
	}
}
