package com.uzmap.pkg.uzmodules.uzUIScrollPicture;

import com.uzmap.pkg.uzkit.UZUtility;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class CustomRoundAngleImageView extends ImageView{
	float width, height;

    public CustomRoundAngleImageView(Context context) {
        this(context, null);
        init(context, null);
    }

    public CustomRoundAngleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context, attrs);
    }

    public CustomRoundAngleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
    
    private void init(Context context, AttributeSet attrs) {
        if (Build.VERSION.SDK_INT < 18) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
    }
    
    private int radius = UZUtility.dipToPix(12);
    @Override
    protected void onDraw(Canvas canvas) {
        if (width >= radius && height > radius) {
            Path path = new Path();
            //四个角：右上，右下，左下，左上
            path.moveTo(radius, 0);
            path.lineTo(width - radius, 0);
            path.quadTo(width, 0, width, radius);

            path.lineTo(width, height - radius);
            path.quadTo(width, height, width - radius, height);

            path.lineTo(radius, height);
            path.quadTo(0, height, 0, height - radius);

            path.lineTo(0, radius);
            path.quadTo(0, 0, radius, 0);

            canvas.clipPath(path);
        }
        super.onDraw(canvas);
    }
}
