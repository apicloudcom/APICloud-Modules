package com.uzmap.pkg.uzmodules.uzUIScrollPicture;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import com.uzmap.pkg.uzkit.UZUtility;
import com.uzmap.pkg.uzmodules.uzUIScrollPicture.round.ViewStyleSetter;

import android.graphics.Paint;

public class UzUIScrollPicture extends UZModule implements OnPageChangeListener ,OnTouchListener{
	private static final int VIEW_PAGER_ID = 1;
	private static final int CAPTION_VIEW_ID = 2;
	public static final int INDICATOR_HEIGHT = 15;
	private UZModuleContext mModuleContext;
	private UZModuleContext mScrollModuleContext;
	private JsParamsUtil mJsParamsUtil;
	private MainLayout mMainView;
	private UzViewPager mViewPager;
	private UzPagerAdapter mPagerAdapter;
	private IndictorView mIndicatorView;
	private TextView mCaptionView;
	private List<ImageView> mImageViewList;
	private List<ImageView> mPreImageViewList;
	private List<String> mImagePaths;
	private List<String> mPreImagePaths;
	private List<String> mCaptions;
	private List<String> mPreCaptions;
	private Handler mCircleHandler;
	private long mIntervalTime;
	private boolean isLoop;
	private boolean isScrollCallBack;
	private int mCurrentIndex;
	boolean mIsAuto;
	private boolean touchWaite = false;
	private boolean touched = false;
	public UzUIScrollPicture(UZWebView webView) {
		super(webView);
	}

	public void jsmethod_open(UZModuleContext moduleContext) {
		this.mModuleContext = moduleContext;
		mJsParamsUtil = JsParamsUtil.getInstance();
		if (mMainView != null) {
			mMainView.setVisibility(View.VISIBLE);
		} else {
			init();
		}
	}

	public void jsmethod_hide(UZModuleContext moduleContext) {
		if (mMainView != null) {
			mMainView.setVisibility(View.GONE);
		}
	}

	public void jsmethod_show(UZModuleContext moduleContext) {
		if (mMainView != null) {
			mMainView.setVisibility(View.VISIBLE);
		}
	}

	public void jsmethod_close(UZModuleContext moduleContext) {
		clean();
	}

	public void jsmethod_setCurrentIndex(UZModuleContext moduleContext) {
		if (mMainView != null) {
			int index = mJsParamsUtil.currentIndex(moduleContext);
			if (mImagePaths != null) {
				if (index < mImagePaths.size()) {
					if (isLoop) {
						mViewPager.setCurrentItem(index + 1);
					} else {
						mViewPager.setCurrentItem(index);
					}
				}
			}
		}
	}

	public void jsmethod_reloadData(UZModuleContext moduleContext) {
		reloadCaptions(moduleContext);
		reloadImgPaths(moduleContext);
		reloadIndicator();
		mPagerAdapter = new UzPagerAdapter(mImageViewList);
		mViewPager.setAdapter(mPagerAdapter);
		if (isLoop) {
			mViewPager.setCurrentItem(1);
		} else {
			mViewPager.setCurrentItem(0);
		}
	}

	public void jsmethod_addEventListener(UZModuleContext moduleContext) {
		this.mScrollModuleContext = moduleContext;
		isScrollCallBack = true;
	}

	public void jsmethod_clearCache(UZModuleContext moduleContext) {
		ClearCacheUtil.cleanApplicationData(context());
	}

	private void reloadCaptions(UZModuleContext moduleContext) {
		mCaptions = mJsParamsUtil.captions(moduleContext);
		if (mCaptions == null || mCaptions.size() == 0) {
			mCaptions = mPreCaptions;
			if (mCaptions == null || mCaptions.size() == 0) {
				mCaptionView.setVisibility(View.GONE);
			}
		} else {
			mPreCaptions = mCaptions;
			mViewPager.setLayoutParams(viewPagerLayout());
			mCaptionView.setText(mCaptions.get(0));
			mCaptionView.setVisibility(View.VISIBLE);
		}
	}

	private void reloadImgPaths(UZModuleContext moduleContext) {
		initImgPaths(moduleContext);
		if (mImageViewList.size() == 0) {
			mImageViewList = mPreImageViewList;
		}
		if (mImagePaths.size() == 0) {
			mImagePaths = mPreImagePaths;
		}
	}

	private void reloadIndicator() {
		initIndicatorLayout();
		initIndicatorParams();
		mIndicatorView.setCurrentIndex(0);
	}

	private void init() {
		//initBitmapUtils();
		initCaptions();
		initImgPaths(mModuleContext);
		initView();
		//initBitmapLoadCallBack();
		initIndicatorParams();
		initAdapter();
		insertView();
		initCircleParams();
	}

	private void initView() {
		initViewPager();
		initCaptionView();
		initIndicatorView();
	}

	private void initViewPager() {
		mViewPager = new UzViewPager(context());
		mViewPager.setScroll(this);
		mViewPager.setId(VIEW_PAGER_ID);
		mViewPager.setLayoutParams(viewPagerLayout());
		mViewPager.setOnPageChangeListener(this);
		mViewPager.setOnTouchListener(this);
		
		int radius = mModuleContext.optInt("scrollerCorner",0);
		
		ViewStyleSetter viewStyleSetter = new ViewStyleSetter(mViewPager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewStyleSetter.setRound(radius);
        }
		
		
//        float[] outerR = new float[] { radius, radius, radius, radius,radius, radius, radius, radius };
//        RoundRectShape roundRectShape = new RoundRectShape(outerR,null, null);  // 构造一个圆角矩形,可以使用其他形状，这样ShapeDrawable就会根据形状来绘制。
//        //RectShape rectShape = new RectShape(); //如果要构造直角矩形可以
//        ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape); //组合圆角矩形和ShapeDrawable
//        shapeDrawable.getPaint().setColor(UZUtility.parseCssColor("#ffffff"));       //设置形状的颜色
//        shapeDrawable.getPaint().setStyle(Paint.Style.FILL);  // 设置绘制方式为填充
//        mViewPager.setBackground(shapeDrawable);
        
//		mViewPager.setBackgroundColor(Color.RED);
	}

	private LayoutParams viewPagerLayout() {
		String captionPosition = mJsParamsUtil.captionPosition(mModuleContext);
		LayoutParams viewPagerParams = null;
		int w = mJsParamsUtil.pixW(mModuleContext, context());
		int h = mJsParamsUtil.pixH(mModuleContext, context());
		if (captionPosition.equals("bottom")) {
			if (mCaptions != null && mCaptions.size() > 0) {
				int captionHeight = mJsParamsUtil.captionHeight(mModuleContext);
				captionHeight = UZUtility.dipToPix(captionHeight);
				if (h == -1) {
					viewPagerParams = new LayoutParams(w, h);
				} else {
					viewPagerParams = new LayoutParams(w, h - captionHeight);
				}
			} else {
				viewPagerParams = new LayoutParams(w, h);
			}
		} else {
			viewPagerParams = new LayoutParams(w, h);
		}
		return viewPagerParams;
	}

	private void initCaptionView() {
		mCaptionView = new TextView(context());
		mCaptionView.setId(CAPTION_VIEW_ID);
		mCaptionView.setLayoutParams(captionLayout());
		initCaptionStyles();
	}

	private LayoutParams captionLayout() {
		int w = mJsParamsUtil.pixW(mModuleContext, context());
		int h = UZUtility.dipToPix(mJsParamsUtil.captionHeight(mModuleContext));
		LayoutParams layoutParams = new LayoutParams(w, h);
		String captionPosition = mJsParamsUtil.captionPosition(mModuleContext);
		if (captionPosition.equals("bottom")) {
			layoutParams.addRule(RelativeLayout.BELOW, VIEW_PAGER_ID);
		} else {
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		}
		return layoutParams;
	}

	private void initCaptionStyles() {
		int bgColor = mJsParamsUtil.captionBgColor(mModuleContext);
		mCaptionView.setBackgroundColor(bgColor);
		mCaptionView.setTextColor(mJsParamsUtil.captionColor(mModuleContext));
		mCaptionView.setTextSize(mJsParamsUtil.captionSize(mModuleContext));
		mCaptionView.setGravity(mJsParamsUtil.captionAlignment(mModuleContext)
				| Gravity.CENTER_VERTICAL);
		mCaptionView.setPadding(UZUtility.dipToPix(5), 0,
				UZUtility.dipToPix(5), 0);
	}

	private void initIndicatorView() {
		mIndicatorView = new IndictorView(context());
		initIndicatorLayout();
	}

	private void initIndicatorLayout() {
		int w = mJsParamsUtil.pixW(mModuleContext, context());
		int h = UZUtility.dipToPix(INDICATOR_HEIGHT);
		LayoutParams layoutParams = new LayoutParams(w, h);
		if (mCaptions != null && mCaptions.size() > 0) {
			mIndicatorView.setIsShowIndicator(true);
			layoutParams.addRule(RelativeLayout.ABOVE, CAPTION_VIEW_ID);
		} else {
			mIndicatorView.setIsShowIndicator(false);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		}
		mIndicatorView.setLayoutParams(layoutParams);
	}
	int dotW = 0;
	int dotH = 0;
	int dotR = 0;
	int dotMargin = 0;
	boolean hasDot = false;

	private void initIndicatorParams() {
		int parentW = mJsParamsUtil.pixW(mModuleContext, context());
		int pointNums = 0;
		if (mImagePaths != null) {
			pointNums = mImagePaths.size();
		}
		int centerY = UZUtility.dipToPix(INDICATOR_HEIGHT) / 2;
		int w = (2 * pointNums - 1) * UZUtility.dipToPix(3) * 2;
		int normalColor = mJsParamsUtil.indicatorColor(mModuleContext);
		int activeColor = mJsParamsUtil.indicatorActiveColor(mModuleContext);
		int indicatorColor = mJsParamsUtil.captionBgColor(mModuleContext);
		String align = mJsParamsUtil.indicatorAlign(mModuleContext);
		if(!mModuleContext.isNull("styles") ){
			JSONObject stylesObj = mModuleContext.optJSONObject("styles");
			if(!stylesObj.isNull("indicator")){
				JSONObject indicatorObj = stylesObj.optJSONObject("indicator");
				if(!indicatorObj.isNull("dot")){
					JSONObject dotObj = stylesObj.optJSONObject("dot");
					dotW = UZUtility.dipToPix(mJsParamsUtil.indicatorDotW(mModuleContext));
					dotH = UZUtility.dipToPix(mJsParamsUtil.indicatorDotH(mModuleContext));
					dotR = UZUtility.dipToPix(mJsParamsUtil.indicatorDotR(mModuleContext));
					dotMargin = UZUtility.dipToPix(mJsParamsUtil.indicatorDotMargin(mModuleContext));
					hasDot = true;//需要找到合适的地方置false
				}
			}
		}
		//add by lihongl at 2017年9月15日 16:12:10   end
		if (align.equals("left")) {
			leftIndicator(centerY, pointNums, normalColor, activeColor,
					indicatorColor);
		} else if (align.equals("right")) {
			rightIndicator(parentW, w, centerY, pointNums, normalColor,
					activeColor, indicatorColor);
		} else {
			centerIndicator(parentW, w, centerY, pointNums, normalColor,
					activeColor, indicatorColor);
		}
	}

	private void leftIndicator(float centerY, int pointNums, int normalColor,
			int activeColor, int indicatorColor) {
		int centerX = UZUtility.dipToPix(10);
		if(hasDot){
			centerX = dotMargin + dotW/2;
		}
		mIndicatorView.initParams(centerX, centerY, pointNums, normalColor,
				activeColor, indicatorColor,dotW,dotH,dotR,dotMargin,hasDot);//
	}

	private void centerIndicator(int parentW, int w, float centerY,
			int pointNums, int normalColor, int activeColor, int indicatorColor) {
		int centerX = (int) (parentW / 2.0 - w / 2.0);
		if(hasDot){
			centerX = (int) (parentW / 2.0 - (dotW+dotMargin*2)*pointNums/2 + dotW/2 + dotMargin);
		}
		mIndicatorView.initParams(centerX, centerY, pointNums, normalColor,
				activeColor, indicatorColor,dotW,dotH,dotR,dotMargin,hasDot);
	}

	private void rightIndicator(int parentW, int w, float centerY,
			int pointNums, int normalColor, int activeColor, int indicatorColor) {
		int centerX = parentW - w;
		if(hasDot){
			centerX = parentW - (dotW+dotMargin*2)*pointNums + dotW/2 + dotMargin;
		}
		mIndicatorView.initParams(centerX, centerY, pointNums, normalColor,
				activeColor, indicatorColor,dotW,dotH,dotR,dotMargin,hasDot);
	}

//	private void initBitmapUtils() {
//		mBitmapUtils = new BitmapUtils(context(), OtherUtils.getDiskCacheDir(context(), ""));
//		Bitmap bitmap = mJsParamsUtil.placeholderImg(mModuleContext, this);
//		if (bitmap != null) {
//			mBitmapUtils.configDefaultLoadingImage(bitmap);
//			mBitmapUtils.configDefaultLoadFailedImage(bitmap);
//		}
//		mBitmapUtils.configDefaultBitmapMaxSize(
//				mJsParamsUtil.pixW(mModuleContext, context()),
//				mJsParamsUtil.pixH(mModuleContext, context()));
//		mBitmapUtils.setScroll(true);
//	}

//	private void initBitmapLoadCallBack() {
//		mBitmapLoadCallBack = new BitmapLoadCallBack<View>() {
//			@Override
//			public void onLoadCompleted(final View container, String uri,
//					final Bitmap bitmap, BitmapDisplayConfig displayConfig,
//					BitmapLoadFrom from) {
//				runOnUiThread(new Runnable() {
//					public void run() {
//						if (container instanceof ImageView) {
//							((ImageView) container)
//									.setImageDrawable(new BitmapDrawable(
//											container.getResources(), bitmap));
//							//((ImageView) container).setBackgroundColor(Color.TRANSPARENT);
//						}
//					}
//				});
//			}
//
//			@Override
//			public void onLoading(View container, String uri,
//					BitmapDisplayConfig config, long total, long current) {
//			}
//
//			@Override
//			public void onLoadFailed(View container, String uri,
//					Drawable failedDrawable) {
//			}
//		};
//	}

	private void initCaptions() {
		mCaptions = mJsParamsUtil.captions(mModuleContext);
		mPreCaptions = mCaptions;
	}

	private void initCircleParams() {
		mCircleHandler = new Handler();
		mIntervalTime = mJsParamsUtil.interval(mModuleContext) * 1000;
		mIsAuto = mJsParamsUtil.auto(mModuleContext);
		touchWaite = mJsParamsUtil.touchWait(mModuleContext);
		if (mIsAuto && mImagePaths.size() > 1) {
			mCircleHandler.postDelayed(mCircleRunnable, mIntervalTime);
		}
	}
	
	public List<String> getImagePath() {
		return mImagePaths;
	}

	private void initImgPaths(UZModuleContext moduleContext) {
		mImageViewList = new ArrayList<ImageView>();
		mImagePaths = new ArrayList<String>();
		List<String> paths = mJsParamsUtil.paths(moduleContext);
		String contentMode = mJsParamsUtil.contentMode(mModuleContext);
		isLoop = mJsParamsUtil.loop(mModuleContext);
		if (paths != null && paths.size() > 0) {
			if (isLoop) {
				mImageViewList
						.add(createImageView(
								generatePath(paths.get(paths.size() - 1)),
								contentMode));
			}
			for (String path : paths) {
				mImagePaths.add(generatePath(path));
				mImageViewList.add(createImageView(generatePath(path),
						contentMode));
			}
			if (isLoop) {
				mImageViewList.add(createImageView(generatePath(paths.get(0)),
						contentMode));
			}
		}
		recordImageViewList();
		recordImagePaths();
	}

	private ImageView createImageView(String path, String contentMode) {
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);
		ImageView imageView = new ImageView(context());
		if (contentMode.equals("scaleToFill")) {
			imageView.setScaleType(ScaleType.FIT_XY);
		} else {
			imageView.setScaleType(ScaleType.FIT_CENTER);
		}
		imageView.setLayoutParams(layoutParams);
		Bitmap placeholderImg = mJsParamsUtil.placeholderImg(mModuleContext, this);
		//mBitmapUtils.display(imageView, path, mBitmapLoadCallBack);
		xUtilsImageUtils.display(imageView, path, contentMode, mJsParamsUtil.cornerRadius(mModuleContext), placeholderImg, null);
		return imageView;
	}

	private void recordImageViewList() {
		if (mImageViewList.size() > 0) {
			mPreImageViewList = mImageViewList;
		} else {
			if (mPreImageViewList == null) {
				mPreImageViewList = new ArrayList<ImageView>();
			}
		}
	}

	private void recordImagePaths() {
		if (mImagePaths.size() > 0) {
			mPreImagePaths = mImagePaths;
		} else {
			if (mPreImagePaths == null) {
				mPreImagePaths = new ArrayList<String>();
			}
		}
	}

	public void callBack(boolean status, String eventType,
			UZModuleContext moduleContext) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("status", status);
			if (eventType != null) {
				ret.put("eventType", eventType);
			}
			if (isLoop) {
				ret.put("index", mCurrentIndex - 1);
			} else {
				ret.put("index", mCurrentIndex);
			}

			moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void initAdapter() {
		mPagerAdapter = new UzPagerAdapter(mImageViewList);
		mViewPager.setAdapter(mPagerAdapter);
		if (isLoop) {
			mViewPager.setCurrentItem(1);
		} else {
			mViewPager.setCurrentItem(0);
		}

	}

	private void insertView() {
		mMainView = new MainLayout(context());
		//mMainView.setBackgroundColor(Color.TRANSPARENT);
		addChildViews();
		String fixedOn = mJsParamsUtil.fixedOn(mModuleContext);
		boolean fixed = mJsParamsUtil.fixed(mModuleContext);
		insertViewToCurWindow(mMainView, mainLayout(), fixedOn, fixed, true);
		callBack(true, "show", mModuleContext);
		mMainView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				callBack(true, "click", getModuleContext());
			}
		});
	}

	private void addChildViews() {
		mMainView.removeAllViewsInLayout();
		mMainView.addView(mViewPager);
		mMainView.addView(mCaptionView);
		if (mJsParamsUtil.isIndicatorShow(mModuleContext)) {
			mMainView.addView(mIndicatorView);
		}
		if (mCaptions != null && mCaptions.size() > 0) {
			mCaptionView.setText(mCaptions.get(0));
		} else {
			mCaptionView.setVisibility(View.GONE);
		}
	}

	private LayoutParams mainLayout() {
		int w = mJsParamsUtil.w(mModuleContext, context());
		int h = mJsParamsUtil.h(mModuleContext, context());
		int x = mJsParamsUtil.x(mModuleContext);
		int y = mJsParamsUtil.y(mModuleContext);
		LayoutParams params = new LayoutParams(w, h);
		params.setMargins(x, y, 0, 0);
		return params;
	}

	private Runnable mCircleRunnable = new Runnable() {
		@Override
		public void run() {
			if (touched && touchWaite) {
				mCircleHandler.postDelayed(mCircleRunnable, mIntervalTime);
			}else {
				int index = mViewPager.getCurrentItem();
				if (index == mImageViewList.size() - 1) {
					mCurrentIndex = mViewPager.getCurrentItem();
				} else {
					mCurrentIndex = index + 1;
				}
				mViewPager.setCurrentItem(mCurrentIndex);
				
			}
			
		}
	};

	public String generatePath(String pathname) {
//		String path = UZUtility.makeRealPath(pathname, getWidgetInfo());
//		if (!TextUtils.isEmpty(path)) {
//			String sharePath;
//			if (path.contains("file://")) {
//				sharePath = substringAfter(path, "file://");
//			} else if (path.contains("android_asset")) {
//				sharePath = path;
//			} else {
//				sharePath = path;
//			}
//			return sharePath;
//		}
		
		if (pathname.startsWith("fs://")) {
			return makeRealPath(pathname);
		}else if (pathname.startsWith("widget://")) {
			String realPath = makeRealPath(pathname);
			if (realPath.startsWith("file:///android_asset/")) {
				return realPath.replace("file:///android_asset/", "assets://");
			}else if(realPath.startsWith("file:///")){
				return realPath.substring("file://".length());
			}
		}else {
			return makeRealPath(pathname);
		}
		
		return null;
	}

	public String substringAfter(String str, String separator) {
		if (isEmpty(str)) {
			return str;
		}
		if (separator == null) {
			return "";
		}
		int pos = str.indexOf(separator);
		if (pos == -1) {
			return "";
		}
		return str.substring(pos + separator.length());
	}

	public boolean isEmpty(CharSequence cs) {
		return (cs == null) || (cs.length() == 0);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		switch (state) {
		case 0:
			if (mIsAuto && mImagePaths.size() > 1) {
				mCircleHandler.removeCallbacks(mCircleRunnable);
				if (mCurrentIndex == 0 && isLoop) {
					mViewPager.setCurrentItem(mImageViewList.size() - 2, false);
				}
				if (mCurrentIndex == mImageViewList.size() - 1 && isLoop) {
					mViewPager.setCurrentItem(1, false);
				}
				mCircleHandler.postDelayed(mCircleRunnable, mIntervalTime);
			} else {
				if (mCurrentIndex == 0 && isLoop) {
					mViewPager.setCurrentItem(mImageViewList.size() - 2, false);
				}
				if (mCurrentIndex == mImageViewList.size() - 1 && isLoop) {
					mViewPager.setCurrentItem(1, false);
				}
				
			}
			break;
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		int unitOffset = UZUtility.dipToPix(IndictorView.RADIUS * 4);
		int index = 0;
		if (isLoop) {
			if (position == 0) {
				index = mImageViewList.size() - 2;
			} else if (position == mImageViewList.size() - 1) {
				index = 0;
			} else {
				index = position - 1;
			}
		} else {
			index = position;
		}
		int offset = index * unitOffset;
		if (mIndicatorView != null) {
			offset += positionOffset * unitOffset;
			mIndicatorView.moveIndicator(offset,index);
		}
	}

	@Override
	public void onPageSelected(int position) {
		mCurrentIndex = position;
		int index = 0;
		if (isLoop) {
			if (position == 0) {
				index = mImageViewList.size() - 2 - 1;
			} else if (position == mImageViewList.size() - 1) {
				index = 0;
			} else {
				index = position - 1;
			}
		} else {
			index = position;
		}
		refreshIndicator(index);
		refreshCaption(index);
		if (isLoop) {
			if (mCurrentIndex > 0 && mCurrentIndex <= mImagePaths.size()) {
				if (isScrollCallBack) {
					callBack(true, null, mScrollModuleContext);
				}
			}
		} else {
			if (mCurrentIndex >= 0 && mCurrentIndex < mImagePaths.size()) {
				if (isScrollCallBack) {
					callBack(true, null, mScrollModuleContext);
				}
			}
		}

	}

	private void refreshIndicator(int position) {
		mIndicatorView.setCurrentIndex(position);
	}

	private void refreshCaption(int position) {
		int pos = position % mImagePaths.size();
		if (mCaptions != null) {
			if (pos < mCaptions.size()) {
				mCaptionView.setText(mCaptions.get(pos));
			} else {
				mCaptionView.setText("");
			}
		}
	}

	@Override
	protected void onClean() {
		//clean();
		super.onClean();
	}

	private void clean() {
		if (mMainView != null) {
			removeViewFromCurWindow(mMainView);
			mMainView = null;
			mCircleHandler.removeCallbacks(mCircleRunnable);
			isScrollCallBack = false;
		}
	}

	public UZModuleContext getModuleContext() {
		return mModuleContext;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touched = true;
			break;
		case MotionEvent.ACTION_UP:
			touched = false;
			break;

		default:
			break;
		}
		return false;
	}
}
