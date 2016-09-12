package com.flk.cilpimage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends Activity {
	//intent传递大小有限制，用static传递bitmap或者文件传递
	public static Bitmap img;
	
	private ImageView imageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		imageView = (ImageView) findViewById(R.id.image);
	}
	
	public void cilp(View v) {
		startActivityForResult(new Intent(this,CilpImageActivity.class), 999);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 999 && resultCode == RESULT_OK) {
			if (img != null) {
				imageView.setImageBitmap(img);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
