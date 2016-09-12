package com.flk.cilpimage.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;
import android.view.ViewTreeObserver;
/**
 * @author flk
 *
 * @description ����ͼƬ
 */
public class CilpImageView extends ImageView implements ScaleGestureDetector.OnScaleGestureListener,
ViewTreeObserver.OnGlobalLayoutListener{
	private static float SCALE_MID = 0.1f;
	/**
	 * ��ʼ��ʱ�����ű��������ͼƬ���ߴ�����Ļ����ֵ��С0
	 */
	private float initScale = 1.0f;
	/**
	 * ���ŵ����Ƽ��
	 */
	private ScaleGestureDetector scaleGestureDetector = null;

	private final Matrix scaleMatrix = new Matrix();
	private final float[] matrixValues = new float[9];
	/**
	 * ����˫������
	 */
	private GestureDetector gestureDetector;
	//�Ƿ��Զ���������
	private boolean isAutoScale;

	private float mLastX;
	private float mLastY;
	private float centerX;
	private float centerY;

	//ͼƬԭʼ���
	private int drawableW;
	private int drawableH;

	private boolean isCanDrag;
	private int lastPointerCount;

	private RectF borderRectF;

	private CilpRectFChangeListener cilpRectFChangeListener;

	private Options options;

	public CilpImageView(Context context) {
		super(context);
		initView(context);
	}

	public CilpImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public CilpImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	private void initView(Context context) {
		options = new Options();
		setScaleType(ScaleType.MATRIX);
		setBackgroundColor(Color.BLACK);
		gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

			@Override
			public boolean onDoubleTap(MotionEvent e) {//˫��
				if (isAutoScale)
					return true;
				float x=e.getX();
				float y=e.getY();
				if (getScale() != initScale) {
					CilpImageView.this.postDelayed(new AutoScaleRunnable(initScale, x, y),16);
				} else {
					CilpImageView.this.postDelayed(new AutoScaleRunnable(initScale * 2, x, y),16);
				}
				isAutoScale=true;
				return true;
			}
		});

		scaleGestureDetector=new ScaleGestureDetector(context,this);

	}


	/**
	 * ��õ�ǰ�����ű���
	 *
	 * @return
	 */
	public final float getScale() {
		scaleMatrix.getValues(matrixValues);
		return matrixValues[Matrix.MSCALE_X];
	}
	/**
	 * �߽�У��
	 */
	private void checkBorder(){
		RectF rectF=getMatrixRectF();
		float deltaX = 0;
		float deltaY = 0;

		int width=getWidth();

		// ������ߴ�����Ļ������Ʒ�Χ,�����0.001����Ϊ���ȶ�ʧ��������⣬�������һ���С������ֱ�Ӽ���һ��0.01
		if (rectF.width() + 0.01 >= width - 2 * options.paddingWidth) {
			if (rectF.left > options.paddingWidth) {
				deltaX = -rectF.left + options.paddingWidth;
			}

			if (rectF.right < width - options.paddingWidth){
				deltaX = width - options.paddingWidth - rectF.right;
			}
		}

		if (rectF.height() +0.01 >= borderRectF.height()){
			if (rectF.top > borderRectF.top){
				deltaY = -rectF.top + borderRectF.top;
			}

			if (rectF.bottom < borderRectF.bottom){
				deltaY = borderRectF.bottom-rectF.bottom;
			}
		}

		scaleMatrix.postTranslate(deltaX,deltaY);

	}

	/**
	 * ���ݵ�ǰͼƬ��Matrix���ͼƬ�ķ�Χ
	 */
	public RectF getMatrixRectF() {
		Matrix matrix = scaleMatrix;
		RectF rect = new RectF();
		Drawable d = getDrawable();
		if (null != d) {
			rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			matrix.mapRect(rect);
		}
		return rect;
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		if (getDrawable() == null) {
			return true;
		}
		float scaleFactor=detector.getScaleFactor();
		scaleMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
		checkBorder();
		setImageMatrix(scaleMatrix);
		setCutRectFIfNeed();
		return true;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {

	}


	public boolean onImageTouch(MotionEvent event, RectF borderRect) {
		this.borderRectF=borderRect;
		if (gestureDetector.onTouchEvent(event)){
			return true;
		}
		scaleGestureDetector.onTouchEvent(event);

		float x = 0, y = 0;
		// �õ�������ĸ���
		final int pointerCount = event.getPointerCount();
		// �õ�����������x��y
		for (int i = 0; i < pointerCount; i++) {
			x += event.getX(i);
			y += event.getY(i);
		}
		x = x / pointerCount;
		y = y / pointerCount;

		/**
		 * ÿ�������㷢���仯ʱ������mLasX , mLastY
		 */
		if (pointerCount != lastPointerCount) {
			isCanDrag = false;
			mLastX = x;
			mLastY = y;
		}

		lastPointerCount = pointerCount;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			resetMidScale();
			mLastX = x;
			mLastY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			float dx = x - mLastX;
			float dy = y - mLastY;

			if (!isCanDrag) {
				isCanDrag = isCanDrag(dx, dy);
			}
			if (isCanDrag) {
				if (getDrawable() != null) {
					RectF rectF = getMatrixRectF();
					// ������С����Ļ��ȣ����ֹ�����ƶ�
					if (rectF.width() <= borderRectF.width()) {
						dx = 0;
					}
					// ����߶�С����Ļ�߶ȣ����ֹ�����ƶ�
					if (rectF.height() <= borderRectF.height()) {
						dy = 0;
					}
					scaleMatrix.postTranslate(dx, dy);
					checkBorder();
					setImageMatrix(scaleMatrix);
				}
			}
			mLastX = x;
			mLastY = y;
			break;
		case MotionEvent.ACTION_UP:
			if (SCALE_MID>getScale()) {
				postDelayed(
						new AutoScaleRunnable(SCALE_MID, getWidth()/2, event.getY()), 1);
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			lastPointerCount = 0;
			break;
		}
		return true;
	}

	/**
	 * �Ƿ����϶���Ϊ
	 */
	private boolean isCanDrag(float dx, float dy) {
		return Math.sqrt((dx * dx) + (dy * dy)) >= 0;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			getViewTreeObserver().removeOnGlobalLayoutListener(this);
		} else {
			getViewTreeObserver().removeGlobalOnLayoutListener(this);
		}
	}

	@Override
	public void onGlobalLayout() {
		if (0!=getHeight()) {
			if (borderRectF==null)
				borderRectF=new RectF(options.paddingWidth,(getHeight() - options.cilpHeight)/2,
						getWidth()-options.paddingWidth,(getHeight() + options.cilpHeight)/2);
			int height=getHeight();
			Drawable d = getDrawable();
			if (d == null)
				return;
			int width = getWidth();
			// �õ�ͼƬ�Ŀ�͸�
			drawableW = d.getIntrinsicWidth();
			drawableH = d.getIntrinsicHeight();
			float scale ;
			scale= borderRectF.width() /drawableW;
			scaleMatrix.reset();
			initScale = scale;
			SCALE_MID=initScale;

			centerX=(width - (borderRectF.width())) / 2;
			centerY=(height - drawableH*scale) / 2;
			scaleMatrix.postTranslate(centerX,	centerY);

			scaleMatrix.postScale(scale, scale,centerX,centerY);
			// ͼƬ�ƶ�����Ļ����
			setImageMatrix(scaleMatrix);
			setCutRectFIfNeed();
		}
	}
	//У��ü��߽�
	private void setCutRectFIfNeed(){
		RectF rectF=getMatrixRectF();
		if (cilpRectFChangeListener!=null && (rectF.height() < borderRectF.height())){

			borderRectF.top = rectF.top;
			borderRectF.bottom = rectF.bottom;

			rectF.right = borderRectF.right;
			rectF.left = borderRectF.left;

			cilpRectFChangeListener.onChange(rectF);
		}
	}

	private void resetMidScale(){
		int distanceW = (int) (drawableW - borderRectF.width());
		int distanceH = (int) (drawableH - borderRectF.height());

		if (distanceH < distanceW) {//���߶�����С���ű���
			SCALE_MID= borderRectF.height() /drawableH;
		} else {
			SCALE_MID= borderRectF.width() /drawableW;
		}

	}


	/**
	 * ����ͼƬ�����ؼ��к��bitmap����
	 *
	 * @return
	 */
	public Bitmap clip(RectF rectF) {
		Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		draw(canvas);
		bitmap=Bitmap.createBitmap(bitmap, (int)rectF.left,
				(int)rectF.top,(int)rectF.width(),(int)rectF.height());
		return bitmap;
	}
	/**
	 * �Զ���������
	 */
	private class AutoScaleRunnable implements Runnable{
		static final float BIGGER=1.07f;
		static final float SMALLER=0.93f;

		private float tarScale;
		private float tmpScale;
		/**
		 * ���ŵ�������
		 */
		private float x;
		private float y;

		AutoScaleRunnable(float tarScale, float x, float y) {
			this.tarScale=tarScale;
			this.x=x;
			this.y=y;

			if (getScale() < tarScale){
				tmpScale=BIGGER;
			} else {
				tmpScale=SMALLER;
			}
		}

		@Override
		public void run() {
			scaleMatrix.postScale(tmpScale, tmpScale, x, y);
			checkBorder();
			setImageMatrix(scaleMatrix);

			final float currentScale =  getScale();
			// ���ֵ�ںϷ���Χ�ڣ���������
			if ( ((tmpScale > 1f) && (currentScale < tarScale)) ||
					((tmpScale < 1f) && (tarScale <currentScale))){
				final float nextScale = tmpScale * currentScale;//�´ε����ű���

				if (((tmpScale > 1f) && (currentScale <tarScale))){//�Ŵ�
					if (nextScale > tarScale){
						tmpScale = tarScale / currentScale;
					}
				}
				if (((tmpScale < 1f) && (currentScale > tarScale))){//��С
					if (nextScale < tarScale){
						tmpScale = tarScale / currentScale;
					}
				}

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					CilpImageView.this.postOnAnimation(this);
				}else{
					CilpImageView.this.postDelayed(this,1);
				}
			} else {
				setCutRectFIfNeed();
				isAutoScale=false;
			}
		}
	}

	public void setCilpRectFChangeListener(CilpRectFChangeListener cilpRectFChangeListener) {
		this.cilpRectFChangeListener = cilpRectFChangeListener;
	}

	public void setOptions(Options options) {
		this.options = options;
	}
	
	public interface CilpRectFChangeListener{
		void onChange(RectF rectF);
	}
}