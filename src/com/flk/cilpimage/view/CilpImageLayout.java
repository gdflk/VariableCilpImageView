package com.flk.cilpimage.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * @author flk
 *
 * @description 可变裁剪区及缩放裁剪图片
 */
public class CilpImageLayout extends RelativeLayout{
	
	private CilpBorderView borderView;
    private CilpImageView imageView;
	
    public CilpImageLayout(Context context) {
        super(context);
        initView();
    }

    public CilpImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CilpImageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        borderView=new CilpBorderView(getContext());
        imageView=new CilpImageView(getContext());

        CilpTouchView keyView=new CilpTouchView(getContext(), borderView, imageView);

        android.view.ViewGroup.LayoutParams lp = new LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);

        setLayoutParams(lp);

        addView(imageView,lp);
        addView(borderView,lp);
        addView(keyView,lp);
    }

    public void setImageBitmap(Bitmap bitmap){
        if (imageView != null)
            imageView.setImageBitmap(bitmap);
    }

    public Bitmap cilpBitmap(){
        if (imageView!=null)
            return imageView.clip(borderView.getCilpRectF());
        return null;
    }
    public void setOptions(Options options) {
    	if (imageView != null)
			imageView.setOptions(options);
    	if (borderView != null)
    		borderView.setOptions(options);
		invalidate();
	}
}
