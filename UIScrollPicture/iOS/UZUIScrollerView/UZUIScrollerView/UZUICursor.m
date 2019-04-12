/**
 * APICloud Modules
 * Copyright (c) 2014-2019 by APICloud, Inc. All Rights Reserved.
 * Licensed under the terms of the The MIT License (MIT).
 * Please see the license.html included with this distribution for details.
 */

#import "UZUICursor.h"

@implementation UZUICursor
@synthesize color=_color;

- (void)dealloc {
    if (_color) {
        self.color = nil;
    }
}

- (id)initWithFrame:(CGRect)frame andColor:(UIColor*)colors {
    self = [super initWithFrame:frame];
    if (self) {
        self.color = colors;
    }
    return self;
}

- (void)drawRect:(CGRect)rect {
    //一个不透明类型的Quartz 2D绘画环境,相当于一个画布,你可以在上面任意绘画
    CGContextRef context = UIGraphicsGetCurrentContext();
    //画三角形
    //只要三个点就行跟画一条线方式一样，把三点连接起来
    //CGContextSetRGBFillColor (context,  1, 0, 0, 1.0);        //设置填充颜色
    CGContextSetFillColorWithColor(context,_color.CGColor);
    CGContextSetStrokeColorWithColor(context,_color.CGColor);
    CGPoint sPoints[3];                                         //坐标点
    sPoints[0] =CGPointMake(rect.size.width/2.0, 0);            //坐标1
    sPoints[1] =CGPointMake(0, rect.size.height);               //坐标2
    sPoints[2] =CGPointMake(rect.size.width, rect.size.height); //坐标3
    CGContextSetLineWidth(context, 0.15);
    CGContextAddLines(context, sPoints, 3);                     //添加线
    CGContextClosePath(context);                                //封起来
    CGContextDrawPath(context, kCGPathFillStroke);              //根据坐标绘制路径
}

@end
