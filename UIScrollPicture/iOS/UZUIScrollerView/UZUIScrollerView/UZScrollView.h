/**
 * APICloud Modules
 * Copyright (c) 2014-2019 by APICloud, Inc. All Rights Reserved.
 * Licensed under the terms of the The MIT License (MIT).
 * Please see the license.html included with this distribution for details.
 */

#import <UIKit/UIKit.h>

typedef void(^TouchWaitBlock)(BOOL);

NS_ASSUME_NONNULL_BEGIN

@interface UZScrollView : UIScrollView
@property (nonatomic, assign) BOOL touchWait;
@property (nonatomic, copy) TouchWaitBlock touchWaitBlock;
@end

NS_ASSUME_NONNULL_END
