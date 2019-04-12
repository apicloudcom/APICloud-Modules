package com.uzmap.pkg.uzmodules.uzUIScrollPicture;

import java.util.ArrayList;
import java.util.List;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class UzPagerAdapter extends PagerAdapter {
	private List<ImageView> mImgViewList = new ArrayList<ImageView>();

	public UzPagerAdapter(List<ImageView> viewList) {
		this.mImgViewList = viewList;
	}

	public List<ImageView> getImgViewList() {
		return mImgViewList;
	}

	@Override
	public int getCount() {
		return mImgViewList.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return view == obj;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		if (mImgViewList != null) {
			container.removeView(mImgViewList.get(position));
		}
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if (mImgViewList != null) {
			ImageView imgView = mImgViewList.get(position);
			container.addView(imgView);
			return imgView;
		}
		return null;
	}

}
