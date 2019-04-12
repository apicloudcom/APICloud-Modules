/**
 * APICloud Modules
 * Copyright (c) 2014-2019 by APICloud, Inc. All Rights Reserved.
 * Licensed under the terms of the The MIT License (MIT).
 * Please see the license.html included with this distribution for details.
 */

#import <UIKit/UIKit.h>
#import "UZUIAsyncImageView.h"

@interface UZRahmen : UIView

@property (nonatomic, strong) UZUIAsyncImageView *asyImg;
@property (nonatomic, strong) UIImageView *img;
@property (nonatomic, strong) NSString *path;
@property (nonatomic, strong) UIImage *placeholder;
@property (nonatomic, strong) NSString *imgContentMode;
@property (nonatomic, assign) float imgCornerRadius;

@end
