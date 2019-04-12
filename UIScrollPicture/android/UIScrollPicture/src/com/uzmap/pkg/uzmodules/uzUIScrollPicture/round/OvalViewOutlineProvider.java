/*
 * Copyright 2017 jiajunhui<junhui_jia@163.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.uzmap.pkg.uzmodules.uzUIScrollPicture.round;

import android.annotation.TargetApi;
import android.graphics.Outline;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;

/**
 * Created by ShadowWalker on 2018/8/27.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class OvalViewOutlineProvider extends ViewOutlineProvider {


    public OvalViewOutlineProvider() {
    }

    @Override
    public void getOutline(final View view, final Outline outline) {
        Rect selfRect;
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        selfRect = getOvalRect(rect);
        outline.setOval(selfRect);
    }

    /**
     * 以矩形的中心点为圆心,较短的边为直径画圆
     *
     * @param rect
     * @return
     */
    private Rect getOvalRect(Rect rect) {
        int width = rect.right - rect.left;
        int height = rect.bottom - rect.top;
        int left, top, right, bottom;
        int dW = width / 2;
        int dH = height / 2;
        if (width > height) {
            left = dW - dH;
            top = 0;
            right = dW + dH;
            bottom = dH * 2;
        } else {
            left = dH - dW;
            top = 0;
            right = dH + dW;
            bottom = dW * 2;
        }
        return new Rect(left, top, right, bottom);
    }

}
