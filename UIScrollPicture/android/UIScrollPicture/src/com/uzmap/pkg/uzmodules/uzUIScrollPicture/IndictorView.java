package com.uzmap.pkg.uzmodules.uzUIScrollPicture;

import com.uzmap.pkg.uzkit.UZUtility;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

public class IndictorView extends View {
	public static final int RADIUS = 3;
	private Paint mPaint;
	private Path mPath;
	private float mCenterX;
	private float mCenterY;
	private float mRadius;
	private int mPointNums;
	private int mCurrentIndex;
	private int mNormalColor;
	private int mActiveColor;
	private int mIndicatorColor;
	private int mOffset;
	private boolean isShowIndicator;
	//add 
	private int dotW;
	private int dotH;
	private int dotR;
	private int dotMargin;
	private int currPage;
	private boolean hasDot = false;

	public IndictorView(Context context) {
		super(context);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.FILL);
		mPath = new Path();
		mRadius = UZUtility.dipToPix(RADIUS);
	}

	public void initParams(float centerX, float centerY, int pointNums,
			int normalColor, int activeColor, int indicatorColor,
			int dotW,int dotH,int dotR,int dotMargin,boolean hasDot) {
		
		this.mCenterX = centerX;
		this.mCenterY = centerY;
		this.mPointNums = pointNums;
		this.mNormalColor = normalColor;
		this.mActiveColor = activeColor;
		this.mIndicatorColor = indicatorColor;
		//add
		this.dotW = dotW;
		this.dotH = dotH;
		this.dotR = dotR;
		this.dotMargin = dotMargin;
		this.hasDot = hasDot;
	}

	public void setIsShowIndicator(boolean isShow) {
		isShowIndicator = isShow;
	}

	public void setCurrentIndex(int index) {
		mCurrentIndex = index;
//		invalidate();
	}

	public void moveIndicator(int offset,int currPage) {
		mOffset = offset;
		this.currPage = currPage;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		drawPoints(canvas);
		if (isShowIndicator) {
			drawIndicator(canvas);
		}
	}
	
	private void drawPoints(Canvas canvas) {
		for (int i = 0; i < mPointNums; i++) {
			if (i == mCurrentIndex) {
				mPaint.setColor(mActiveColor);
			} else {
				mPaint.setColor(mNormalColor);
			}
			if(hasDot){
				float centerX = mCenterX + dotW * i + dotMargin*2*i;
				float centerY = mCenterY;
				float rectLeft = centerX - dotW/2;
				float rectTop = centerY - dotR;
				float rectRight = centerX + dotW/2;
				float rectBottom = centerY + dotR;
		        RectF oval3 = new RectF(rectLeft, rectTop, rectRight, rectBottom);
		        canvas.drawRoundRect(oval3, dotR, dotR, mPaint); 
			}else{
				canvas.drawCircle(mCenterX + mRadius * 4 * i, mCenterY, mRadius,
						mPaint);
			}
		}
	}

	private void drawIndicator(Canvas canvas) {
		mPaint.setColor(mIndicatorColor);
		int h = UZUtility.dipToPix(UzUIScrollPicture.INDICATOR_HEIGHT + 1);
		float leftX = mCenterX + mOffset - mRadius * 2;
		float middleX = mCenterX + mOffset;
		float middleH = h / 2.0f + mRadius;
		float rightX = mCenterX + mOffset + mRadius * 2;
		if(hasDot){
			leftX = mCenterX + (dotW+ dotMargin*2) * currPage - mRadius * 2;
			middleX =  mCenterX + (dotW+ dotMargin*2) * currPage;
			middleH = h / 2.0f + mRadius;
			rightX = mCenterX + (dotW+ dotMargin*2) * currPage + mRadius * 2;
		}
		mPath.moveTo(leftX, h);
		mPath.lineTo(middleX, middleH);
		mPath.lineTo(rightX, h);
		mPath.close();
		canvas.drawPath(mPath, mPaint);
		mPath.reset();
	}

}
