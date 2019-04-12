package com.uzmap.pkg.uzmodules.uzUIScrollPicture;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;

import com.uzmap.pkg.uzcore.UZResourcesIDFinder;

import org.xutils.x;

/**
 * Created by lanjingmin on 2019/1/21.
 */
public class xUtilsImageUtils {
    /**
     * 显示图片（默认情况）
     *
     * @param imageView 图像控件
     * @param iconUrl   图片地址
     */
    public static void display(ImageView imageView, String iconUrl) {
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setIgnoreGif(false)//是否忽略gif图。false表示不忽略。不写这句，默认是true
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setFailureDrawableId(UZResourcesIDFinder.getResDrawableID("mo_uiscroll_slide_holder"))
                .setLoadingDrawableId(UZResourcesIDFinder.getResDrawableID("mo_uiscroll_slide_holder"))
                .build();
        x.image().bind(imageView, iconUrl,imageOptions);
    }

    /**
     * 显示圆角图片
     *
     * @param imageView 图像控件
     * @param iconUrl   图片地址
     * @param radius    圆角半径，
     */
    public static void display(ImageView imageView, String iconUrl, String contentMode, int radius, Bitmap placehoderImg, Callback.CommonCallback<Drawable> callback) {
    		ImageOptions.Builder builder = new ImageOptions.Builder();
    		builder.setImageScaleType(TextUtils.equals(contentMode, "scaleAspectFit") ? ImageView.ScaleType.FIT_CENTER : ImageView.ScaleType.FIT_XY);
    		builder.setRadius(DensityUtil.dip2px(radius));
    		builder.setIgnoreGif(false);
    		builder.setCrop(false);//是否对图片进行裁剪
    		builder.setFadeIn(false);
    		if (placehoderImg != null) {
			builder.setFailureDrawable(new BitmapDrawable(placehoderImg));
			builder.setLoadingDrawable(new BitmapDrawable(placehoderImg));
		}
    		//builder.setFailureDrawableId(UZResourcesIDFinder.getResDrawableID("mo_uiscroll_slide_holder"));
    		//builder.setLoadingDrawableId(UZResourcesIDFinder.getResDrawableID("mo_uiscroll_slide_holder"));
    		ImageOptions imageOptions = builder.build();
        x.image().bind(imageView, iconUrl, imageOptions, callback);
    }

    /**
     * 显示圆形头像，第三个参数为true
     *
     * @param imageView  图像控件
     * @param iconUrl    图片地址
     * @param isCircluar 是否显示圆形
     */
    public static void display(ImageView imageView, String iconUrl, boolean isCircluar) {
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setCircular(isCircluar)
                .setCrop(true)
                .setLoadingDrawableId(UZResourcesIDFinder.getResDrawableID("mo_uiscroll_slide_holder"))
                .setFailureDrawableId(UZResourcesIDFinder.getResDrawableID("mo_uiscroll_slide_holder"))
                .build();
        x.image().bind(imageView, iconUrl, imageOptions);
    }
}
