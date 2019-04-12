/**
 * APICloud Modules
 * Copyright (c) 2014-2019 by APICloud, Inc. All Rights Reserved.
 * Licensed under the terms of the The MIT License (MIT).
 * Please see the license.html included with this distribution for details.
 */

#import <UIKit/UIKit.h>

@interface UZUIPageControl : UIPageControl

/** 小圆点间距 */
@property (nonatomic, assign) CGFloat margin;

/** 小圆点宽度 */
@property (nonatomic, assign) CGFloat width;

/** 小圆点高度 */
@property (nonatomic, assign) CGFloat height;

/** 小圆点半径 */
@property (nonatomic, assign) CGFloat radius;

-(void)setUpDots;

@end

