//
//  AsyncImageView.m
//  UIScrollPicture
//
//  Created by Anonymity on 15-6-28.
//  Copyright (c) 2015年 ？？？. All rights reserved.
//

#import "UZUIAsyncImageView.h"
#import "UIImageView+WebCache.h"
#import "SDWebImageManager.h"

@implementation UZUIAsyncImageView

@synthesize UZneedClip;

- (void)dealloc {
    [[SDWebImageManager sharedManager] cancelAll];
}

- (void)loadImage:(NSString*)imageURL {
	if (![imageURL isKindOfClass:[NSString class]] || [imageURL length]==0) {
		return;
	}
    [self loadImage:imageURL withPlaceholdImage:self.image];
}

- (void)loadImage:(NSString*)imageURL withPlaceholdImage:(UIImage *)placeholdImage {
    
    [self sd_setImageWithURL:[NSURL URLWithString:imageURL] placeholderImage:placeholdImage options:SDWebImageAllowInvalidSSLCertificates completed:^(UIImage * _Nullable image, NSError * _Nullable error, SDImageCacheType cacheType, NSURL * _Nullable imageURL) {
        if (image) {
            if (self.UZneedClip) {
                CGPoint center = self.center;
                self.frame = CGRectMake(self.frame.origin.x, self.frame.origin.y, image.size.width, image.size.height);
                self.center = center;
            }
            
            self.image = [self imageWithImage:image];
        }
    }];
}

- (UIImage *)imageWithImage:(UIImage *)image
{
    UIGraphicsBeginImageContextWithOptions(image.size, NO, 0);
    
    UIBezierPath * path = [UIBezierPath bezierPathWithRoundedRect:CGRectMake(0, 0, image.size.width, image.size.height) cornerRadius:self.imgCornerRadius];
    
    [path addClip];
    
    [image drawAtPoint:CGPointZero];
    
    UIImage * newImage = UIGraphicsGetImageFromCurrentImageContext();
    
    UIGraphicsEndImageContext();
    
    return newImage;
}

@end
