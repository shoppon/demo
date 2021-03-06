/**
 * 
 */
package cn.shoppon.demos.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import cn.shoppon.demos.R;

/**
 * 
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012. All rights reserved.
 * </p>
 * 
 * @author <a href="www.shoppon.cn">Shoppon</a>
 * @version
 * @see
 */
public class ZoomImageView extends ImageView {
	private static final String TAG = "ZoomImageView";

	private Bitmap mBitmap;
	private Paint mPaint;
	private ZoomState mZoomState;

	private Rect mSrcRect;
	private Rect mDestRect;

	/** start position **/
	private float mStartX;
	private float mStartY;
	/** end position **/
	private float mEndX;
	private float mEndY;

	private float mOriginDistance;

	public ZoomImageView(Context context) {
		super(context);
	}

	public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ZoomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background);
		mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);

		mSrcRect = new Rect();
		mDestRect = new Rect();
		mZoomState = new ZoomState();

	}

	public void setBitmap(Bitmap bitmap) {
		this.mBitmap = bitmap;
		mSrcRect.left = 0;
		mSrcRect.right = mBitmap.getWidth();
		mSrcRect.top = 0;
		mSrcRect.bottom = mBitmap.getHeight();
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int bitmapWidth = mBitmap.getWidth();
		int bitmapHeight = mBitmap.getHeight();
		int viewWidth = getWidth();
		int viewHeight = getHeight();

		Log.d(TAG, "ZoomState zoom--->" + mZoomState.getZoom());
		Log.d(TAG, "ZoomState panX--->" + mZoomState.getPanx());
		Log.d(TAG, "ZoomState panY--->" + mZoomState.getPany());

		int width = mSrcRect.right - mSrcRect.left;
		int height = mSrcRect.bottom - mSrcRect.top;
		mSrcRect.left = (int) (mSrcRect.left * (1 + mZoomState.getZoom()) / 2 + mSrcRect.right * (1 - mZoomState.getZoom()) / 2 + width
				* mZoomState.getPanx());
		mSrcRect.right = (int) (mSrcRect.left + width * mZoomState.getZoom() + width * mZoomState.getPanx());
		mSrcRect.top = (int) (mSrcRect.top * (1 + mZoomState.getZoom()) / 2 + mSrcRect.bottom * (1 - mZoomState.getZoom()) / 2 + height
				* mZoomState.getPany());
		mSrcRect.bottom = (int) (mSrcRect.top + height * mZoomState.getZoom() + height * mZoomState.getPany());

		Log.d(TAG, "mSrcRect left--->" + mSrcRect.left);
		Log.d(TAG, "mSrcRect right--->" + mSrcRect.right);
		Log.d(TAG, "mSrcRect top--->" + mSrcRect.top);
		Log.d(TAG, "mSrcRect bottom--->" + mSrcRect.bottom);

		mDestRect.left = getLeft();
		mDestRect.top = getTop();
		mDestRect.right = getRight();
		mDestRect.bottom = getBottom();
		canvas.drawBitmap(mBitmap, mSrcRect, mDestRect, mPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int count = event.getPointerCount();

		float x = event.getX();
		float y = event.getY();

		if (count == 1) {
			switch (action) {
			case MotionEvent.ACTION_MOVE:
				float dx = (mStartX - x) / getWidth();
				float dy = (mStartY - y) / getHeight();
				mZoomState.setPanx(mZoomState.getPanx() + dx * 0.5f);
				mZoomState.setPany(mZoomState.getPany() + dy * 0.5f);
				mStartX = x;
				mStartY = y;
				invalidate();
				break;
			case MotionEvent.ACTION_DOWN:
				mStartX = x;
				mStartY = y;
				break;
			case MotionEvent.ACTION_UP:
				break;
			}
		} else if (count == 2) {
			float x0 = event.getX(event.getPointerId(0));
			float y0 = event.getY(event.getPointerId(0));

			float x1 = event.getX(event.getPointerId(1));
			float y1 = event.getY(event.getPointerId(1));

			float distance = measureDistance(x0, y0, x1, y1);
			switch (action) {
			case MotionEvent.ACTION_MOVE:
				mZoomState.setZoom(mOriginDistance / distance);
				mOriginDistance = distance;
				invalidate();
				break;
			case MotionEvent.ACTION_POINTER_1_DOWN:
			case MotionEvent.ACTION_POINTER_2_DOWN:
				mOriginDistance = distance;
				break;
			case MotionEvent.ACTION_POINTER_1_UP:
				mStartX = x1;
				mStartY = y1;
				break;
			case MotionEvent.ACTION_POINTER_2_UP:
				mStartX = x0;
				mStartY = y0;
				break;
			}
		}
		return true;
	}

	private float measureDistance(float x0, float y0, float x1, float y1) {
		double dX2 = Math.pow(x0 - x1, 2);
		double dY2 = Math.pow(y1 - y1, 2);
		return (float) Math.pow(dX2 + dY2, 0.5);
	}

	public ZoomState getZoomState() {
		return mZoomState;
	}

	public void setZoomState(ZoomState zoomState) {
		mZoomState = zoomState;
	}
}
