//
//  AsyncImageView.h
//  UIScrollPicture
//
//  Created by Anonymity on 15-6-28.
//  Copyright (c) 2015年 ？？？. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UZUIAsyncImageView : UIImageView

@property (nonatomic, assign) float imgCornerRadius;
@property (nonatomic, assign) BOOL UZneedClip;

- (void)loadImage:(NSString *)imageURL;
- (void)loadImage:(NSString *)imageURL withPlaceholdImage:(UIImage *)image;

@end
