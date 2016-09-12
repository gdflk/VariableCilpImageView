package com.flk.cilpimage.view;

import android.content.Context;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author flk
 *
 * @description ¼àÌý´¥ÃþÊ±¼ä
 */
public class CilpTouchView extends View implements View.OnTouchListener{
	
    private CilpImageView imageView;
    private CilpBorderView borderView;

    private boolean iconClick;

    private RectF changeRect;

    public CilpTouchView(Context context, CilpBorderView borderView, final CilpImageView imageView) {
        super(context);
        if (borderView == null || imageView == null) {
            throw new NullPointerException("view is null");
        }

        this.borderView=borderView;
        this.imageView=imageView;

        imageView.setCilpRectFChangeListener(new CilpImageView.CilpRectFChangeListener() {
            @Override
            public void onChange(RectF rectF) {
                CilpTouchView.this.borderView.setCilpRectF(rectF);
            }
        });

        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (borderView.isIconClick(event)){
                iconClick = true;
                changeRect= imageView.getMatrixRectF();
            } else {
                iconClick = false;
                changeRect=borderView.getCilpRectF();
            }
        }

        if (iconClick){
            borderView.iconOntouch(event, changeRect);
        } else {
            imageView.onImageTouch(event, changeRect);
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            iconClick=false;
        }
        return true;
    }

}
