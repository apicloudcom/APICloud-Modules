/**
 * APICloud Modules
 * Copyright (c) 2014-2019 by APICloud, Inc. All Rights Reserved.
 * Licensed under the terms of the The MIT License (MIT).
 * Please see the license.html included with this distribution for details.
 */
#import "UZScrollView.h"

@implementation UZScrollView

- (instancetype)init
{
    self = [super init];
    if (self) {

        
    }
    return self;
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate{
    NSLog(@"-----------------结束");

}
- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    if (self.touchWait) {
        NSLog(@"==============开始点击");
        if (self.touchWaitBlock) {
            self.touchWaitBlock(YES);
        }
    }
}

- (void)touchesEnded:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    if (self.touchWait) {
        NSLog(@"-----------------结束");
        if (self.touchWaitBlock) {
            self.touchWaitBlock(NO);
        }
    }
}
//- (void)pressesBegan:(NSSet<UIPress *> *)presses withEvent:(nullable UIPressesEvent *)event{
//    if (self.touchWait) {
//        if (self.touchWaitBlock) {
//            self.touchWaitBlock(YES);
//        }
//    }
//}
//- (void)pressesEnded:(NSSet<UIPress *> *)presses withEvent:(nullable UIPressesEvent *)event{
//    if (self.touchWait) {
//        if (self.touchWaitBlock) {
//            self.touchWaitBlock(NO);
//        }
//    }
//}


@end

