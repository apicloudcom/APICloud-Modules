package com.uzmap.pkg.uzmodules.uzUIScrollPicture;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class UzViewPager extends ViewPager {
	private GestureDetector mGestureDetector;
	private UzUIScrollPicture mScroll;
	private Path mPath = null;
	private int radius = 0;

	public UzViewPager(Context context) {
		super(context);
		mGestureDetector = new GestureDetector(context,
				new MyOnGestureListener());
	}

	public UzViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		mGestureDetector = new GestureDetector(context,
				new MyOnGestureListener());
	}
	
	
	
	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
		
	}



	int lastX = -1;
    int lastY = -1;
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int x = (int) ev.getRawX();
		int y = (int) ev.getRawY();
		int dealtX = 0;
		int dealtY = 0;
		
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			dealtX = 0;
			dealtY = 0;
			getParent().requestDisallowInterceptTouchEvent(true);
			break;
		case MotionEvent.ACTION_MOVE:
			dealtX += Math.abs(x - lastX);
			dealtY += Math.abs(y - lastY);
			if (dealtX >= dealtY) {
				getParent().requestDisallowInterceptTouchEvent(true);
			}else {
				getParent().requestDisallowInterceptTouchEvent(false);
			}
			lastX = x;
			lastY = y;
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		
		return super.dispatchTouchEvent(ev);
	}

	@SuppressLint("ClickableViewAccessibility")
	public boolean onTouchEvent(MotionEvent ev) {
		mGestureDetector.onTouchEvent(ev);
		if (mScroll.getImagePath().size() ==1) {
			return false;
		}
		return super.onTouchEvent(ev);
	}

	class MyOnGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return true;
		}

		@Override
		public void onShowPress(MotionEvent e) {
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			mScroll.callBack(true, "click", mScroll.getModuleContext());
			return false;
		}
	}

	public void setScroll(UzUIScrollPicture mScroll) {
		this.mScroll = mScroll;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		//requestDisallowInterceptTouchEvent(true);
		//getParent().requestDisallowInterceptTouchEvent(true);
		if (mScroll.getImagePath().size() > 1) {
			return true;
		}
		
		return super.onInterceptTouchEvent(arg0);
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		
		super.onDraw(canvas);
	}
	
	
}
