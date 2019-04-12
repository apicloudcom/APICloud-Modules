/**
 * APICloud Modules
 * Copyright (c) 2014-2019 by APICloud, Inc. All Rights Reserved.
 * Licensed under the terms of the The MIT License (MIT).
 * Please see the license.html included with this distribution for details.
 */

#import "UZUIPageControl.h"

@interface UZUIPageControl ()

@end

@implementation UZUIPageControl

-(void)setCurrentPage:(NSInteger)page
{
    [super setCurrentPage:page];
    
    [self setUpDots];
    
}

-(void)setUpDots
{
    for (int i = 0; i < self.subviews.count; i++) {
        
        UIView* dot = [self.subviews objectAtIndex:i];
        
        dot.layer.cornerRadius = self.radius;
    }
}
- (void)layoutSubviews
{
    [super layoutSubviews];
    
    CGFloat marginX = self.width + self.margin;
    
    CGFloat newW = (self.subviews.count - 1 ) * marginX + self.width;
    
    self.frame = CGRectMake(self.frame.origin.x, self.frame.origin.y, newW, self.height + 20);
    
    for (int i = 0; i < self.subviews.count; i++) {
        UIView * dot = [self.subviews objectAtIndex:i];
        
        [dot setFrame:CGRectMake(i * marginX, 10, self.width, self.height)];
    }
}



@end

