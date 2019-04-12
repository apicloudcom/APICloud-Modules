/**
 * APICloud Modules
 * Copyright (c) 2014-2019 by APICloud, Inc. All Rights Reserved.
 * Licensed under the terms of the The MIT License (MIT).
 * Please see the license.html included with this distribution for details.
 */

#import "UZRahmen.h"
@interface UZRahmen ()

@end

@implementation UZRahmen

- (id)init {
    self = [super init];
    if (self != nil) {
        self.backgroundColor = [UIColor clearColor];
        
        self.asyImg = [[UZUIAsyncImageView alloc]init];
        self.img = [[UIImageView alloc]init];
        self.asyImg.frame = CGRectZero;
        [self addSubview:self.asyImg];
        [self addSubview:self.img];
        self.img.hidden = YES;
        self.asyImg.hidden = YES;
    }
    return self;
}

- (void)drawRect:(CGRect)rect {
    self.asyImg.frame = CGRectMake(0, 0, rect.size.width, rect.size.height);
    self.img.frame = CGRectMake(0, 0, rect.size.width, rect.size.height);
    if ([self.imgContentMode isEqualToString:@"scaleAspectFit"]) {
        self.asyImg.contentMode = UIViewContentModeScaleAspectFit;
        self.img.contentMode = UIViewContentModeScaleAspectFit;
    } else {
        self.asyImg.contentMode = UIViewContentModeScaleToFill;
        self.img.contentMode = UIViewContentModeScaleToFill;
    }
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

- (void)setPath:(NSString *)path {
    if (![path isKindOfClass:[NSString class]] || path.length==0) {
        self.img.hidden = NO;
        self.asyImg.hidden = YES;
        UIImage *image = [UIImage imageWithContentsOfFile:path];
        image = [self imageWithImage:image];
        self.img.image = image;
        if (self.img.image == nil) {
            self.placeholder = [self imageWithImage:self.placeholder];
            self.img.image = self.placeholder;
        }
    }
    #pragma mark - 加载网络图片
    if ([path hasPrefix:@"http"]) {
        self.img.hidden = YES;
        self.asyImg.hidden = NO;
        self.asyImg.imgCornerRadius = self.imgCornerRadius;
        [self.asyImg loadImage:path withPlaceholdImage:[self imageWithImage:self.placeholder]];
    } else {
        self.img.hidden = NO;
        self.asyImg.hidden = YES;
        UIImage *image = [UIImage imageWithContentsOfFile:path];
        image = [self imageWithImage:image];
        self.img.image = image;
        if (self.img.image == nil) {
            self.placeholder = [self imageWithImage:self.placeholder];
            self.img.image = self.placeholder;
        }
        
    }
}

@end
