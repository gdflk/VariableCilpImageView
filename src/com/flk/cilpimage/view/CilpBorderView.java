package com.flk.cilpimage.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.flk.cilpimage.R;

/**
 * @author flk
 *
 * @description 可变裁剪区域
 */
public class CilpBorderView extends View {

	private int borderColor = Color.parseColor("#FFFFFF");
	private int outSideColor = Color.parseColor("#20000000");

	private float borderWidth = 2;
	private float lineWidth = 1;

	private Rect[] rects=new Rect[2];

	private Paint cutPaint;
	private Paint outSidePaint;


	private RectF cilpRectF;
	//图片右坐标
	private int iconRight=0;
	//图片左坐标
	private int iconLeft=0;

	private Bitmap bitmap;
	private int iconOffset;

	private int width=0;
	private int height=0;

	private int verLine1;
	private int verLine2;

	private Options options;

	private static final int TOP_ICON_ACTION=1;
    private static final int BOTTOM_ICON_ACTION=2;

    private int action=-1;
    private float actionY;
    
	public CilpBorderView(Context context) {
		super(context);
		initView();
	}
	public CilpBorderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
	public CilpBorderView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();
	}

	private void initView() {
		cutPaint = new Paint();
		cutPaint.setColor(borderColor);
		cutPaint.setStrokeWidth(borderWidth);
		cutPaint.setStyle(Paint.Style.STROKE);

		outSidePaint=new Paint();
		outSidePaint.setAntiAlias(true);
		outSidePaint.setColor(outSideColor);
		outSidePaint.setStyle(Paint.Style.FILL);

		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_crop_drag_y);
		iconOffset = bitmap.getWidth()/2;

		options = new Options();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (cilpRectF == null) {
			cilpRectF = new RectF(options.paddingWidth, (getHeight() - options.cilpHeight) / 2, getWidth() - options.paddingWidth, (getHeight() + options.cilpHeight) / 2);
			verLine1 = (int) (cilpRectF.left + cilpRectF.width() / 3);
			verLine2 = (int) (cilpRectF.left + cilpRectF.width() * 2 / 3);
		}
		if (width == 0)
			width = getWidth();
		if (height == 0)
			height = getHeight();
		canvas.save();

		drawLine(canvas);
		drawRound(canvas);
		drawIcon(canvas);

		canvas.restore();

		super.onDraw(canvas);
	}

    private void drawIcon(Canvas canvas) {
        if (iconLeft == 0 && iconRight == 0) {
            iconLeft = width / 2 - iconOffset;
            iconRight=width / 2 + iconOffset;
        }
        canvas.drawBitmap(bitmap, iconLeft, cilpRectF.top-iconOffset, null);
        canvas.drawBitmap(bitmap, iconLeft, cilpRectF.bottom-iconOffset, null);

        Rect rect=new Rect(iconLeft-options.iconClick, (int)(cilpRectF.top-iconOffset)-options.iconClick,iconRight+options.iconClick, 
        		(int)(cilpRectF.top+iconOffset)+options.iconClick);
        rects[0]=rect;

        rect=new Rect(iconLeft-options.iconClick,(int)(cilpRectF.bottom-iconOffset)-options.iconClick,iconRight+options.iconClick,
        		(int)(cilpRectF.bottom+iconOffset)+options.iconClick);
        rects[1]=rect;
    }
    
    private void drawLine(Canvas canvas){
        cutPaint.setStrokeWidth(lineWidth);
        float p = cilpRectF.top + cilpRectF.height() / 3;
        //横线
        canvas.drawLine(options.paddingWidth, p, width-options.paddingWidth, p, cutPaint);
        p = cilpRectF.top + cilpRectF.height() * 2 / 3;
        canvas.drawLine(options.paddingWidth, p, width-options.paddingWidth, p, cutPaint);
        //竖线
        canvas.drawLine(verLine1, cilpRectF.top, verLine1, cilpRectF.bottom, cutPaint);
        canvas.drawLine(verLine2, cilpRectF.top, verLine2, cilpRectF.bottom, cutPaint);
    }

    private void drawRound(Canvas canvas) {
        //绘制边框
        cutPaint.setStrokeWidth(borderWidth);
        canvas.drawRect(cilpRectF, cutPaint);
        //绘制外区域
        //左中框
        canvas.drawRect(0, cilpRectF.top, options.paddingWidth, cilpRectF.bottom, outSidePaint);
        //上框
        canvas.drawRect(0, 0, width, cilpRectF.top, outSidePaint);
        //右中框
        canvas.drawRect(cilpRectF.right, cilpRectF.top, width, cilpRectF.bottom, outSidePaint);
        //下框
        canvas.drawRect(0, cilpRectF.bottom, width, height, outSidePaint);
    }
    
	public void setOptions(Options options) {
		this.options = options;
	}
	
	public boolean iconOntouch(MotionEvent event,RectF imgRect){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (rects[0].contains((int)event.getX(),(int)event.getY())) {
                    action=TOP_ICON_ACTION;
                }
                if (rects[1].contains((int)event.getX(),(int)event.getY())) {
                    action=BOTTOM_ICON_ACTION;
                }
                actionY=event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float y=actionY-event.getY();
                switch (action){
                    case TOP_ICON_ACTION:
                        cilpRectF.top=cilpRectF.top-y;

                        break;
                    case BOTTOM_ICON_ACTION:
                        cilpRectF.bottom=cilpRectF.bottom-y;
                        break;
                }
                checkBroad(imgRect);
                actionY=event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                action=-1;
                break;
        }
        return true;
    }
	/**
	 * @description 边界校验
	 * @param imgRect
	 */
	private void checkBroad(RectF imgRect) {
        if ((cilpRectF.bottom-cilpRectF.top) < options.min_height){//高度少于最小高度
            switch (action) {
                case TOP_ICON_ACTION:
                    cilpRectF.top=cilpRectF.bottom - options.min_height;
                    break;
                case BOTTOM_ICON_ACTION:
                    cilpRectF.bottom=cilpRectF.top + options.min_height;
                    break;
            }
        } else if ((cilpRectF.bottom-cilpRectF.top) > options.max_height){//高度大于最大高度
            switch (action) {
                case TOP_ICON_ACTION:
                    cilpRectF.top=cilpRectF.bottom - options.max_height;
                    break;
                case BOTTOM_ICON_ACTION:
                    cilpRectF.bottom=cilpRectF.top + options.max_height;
                    break;
            }
        }

        if (cilpRectF.top < options.paddingHeight) {
            cilpRectF.top = options.paddingHeight;
        }

        if (cilpRectF.bottom > height-options.paddingHeight){
            cilpRectF.bottom = height-options.paddingHeight;
        }

        if ( cilpRectF.top < imgRect.top) {
            cilpRectF.top = imgRect.top;
        }

        if ( cilpRectF.bottom > imgRect.bottom) {
            cilpRectF.bottom = imgRect.bottom;
        }
    }
	
	public boolean isIconClick(MotionEvent event){
        if (rects[0].contains((int)event.getX(), (int)event.getY())) {
        	System.out.println("点击顶部图标");
            action=TOP_ICON_ACTION;
            return true;
        }
        if (rects[1].contains((int)event.getX(), (int)event.getY())) {
        	System.out.println("点击底部图标");
            action=BOTTOM_ICON_ACTION;
            return true;
        }
        actionY=event.getY();
        return false;
    }


    public RectF getCilpRectF() {
        return cilpRectF;
    }

    public void setCilpRectF(RectF cilpRectF) {
        this.cilpRectF = cilpRectF;
        invalidate();
    }
	
}
