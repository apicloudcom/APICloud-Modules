/**
 * APICloud Modules
 * Copyright (c) 2014-2019 by APICloud, Inc. All Rights Reserved.
 * Licensed under the terms of the The MIT License (MIT).
 * Please see the license.html included with this distribution for details.
 */

#import "UZUIScrollPicture.h"
#import "NSDictionaryUtils.h"
#import "UZUIAsyncImageView.h"
#import "UZUICursor.h"
#import "UZRahmen.h"
#import "UZUIPageControl.h"
#import "SDWebImageManager.h"
#import "UZScrollView.h"
//#import "SDWebImageCodersManager.h"
//#import "SDWebImageGIFCoder.h"

#define UISCROLLWIDTH  _scrollerView.bounds.size.width

@interface UZUIScrollPicture ()
<UIScrollViewDelegate> {
    UIView *_mainView, *_titleView;
    NSArray *_captionsArr;
    NSDictionary *_dataSource, *_stylesInfo, *_indicator;
    UILabel *_titleLabel;
    UIImage *_placeholderImg;
    UZScrollView *_scrollerView;
    NSTimer *_timer;
    UIPageControl *_pageControl;
    UZUICursor *_cursor;
    NSString *_placeholderPath;
    
    BOOL scrollLoop, orignalLoop ,touchWait;
    float intervaL;//图片轮换时间间隔
    NSInteger openCbid, listenerId;
    NSInteger currentIndex;//当前展示的是第几张图片
}

@property (nonatomic, strong) UIView *mainView, *titleView;
@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) UIImage *placeholderImg;
@property (nonatomic, strong) NSDictionary *dataSource, *stylesInfo, *indicator;
@property (nonatomic, strong) NSArray *captionsArr;
@property (nonatomic, strong) NSMutableArray *slideImages;
@property (nonatomic, strong) UZScrollView *scrollerView;
@property (nonatomic, strong) NSTimer *timer;
@property (nonatomic, strong) UIPageControl *pageControl;
@property (nonatomic, strong) UZUICursor *cursor;
@property (nonatomic, strong) UZRahmen *preRahmen, *curRahmen, *nextRahmen;
@property (nonatomic, assign) BOOL isDot;
/** 页面控制器的宽度 */
@property (nonatomic, assign) float controlw;
/** 自定义页面控制器时的 margin */
@property (nonatomic, assign) float marginX;

@end

@implementation UZUIScrollPicture
@synthesize mainView = _mainView, titleView = _titleView;
@synthesize dataSource = _dataSource, stylesInfo = _stylesInfo, indicator = _indicator;
@synthesize captionsArr = _captionsArr;
@synthesize titleLabel = _titleLabel;
@synthesize placeholderImg = _placeholderImg;
@synthesize scrollerView = _scrollerView;
@synthesize timer = _timer;
@synthesize pageControl = _pageControl;
@synthesize cursor = _cursor;
@synthesize slideImages;
@synthesize preRahmen, curRahmen, nextRahmen;

#pragma mark - lifeCycle -

- (void)dispose {
    [self close:nil];
}

- (id)initWithUZWebView:(UZWebView *)webView {
    self = [super initWithUZWebView:webView];
    if (self != nil) {
        listenerId = -1;
        self.slideImages = [NSMutableArray arrayWithCapacity:1];
//        [[SDWebImageCodersManager sharedInstance] addCoder:[SDWebImageGIFCoder sharedCoder]];
    }
    return self;
}

#pragma mark - interFace -

- (void)close:(NSDictionary *)params_ {
    listenerId = -1;
    if (_mainView) {
        [_mainView removeFromSuperview];
        self.mainView = nil;
    }
    if (_dataSource) {
        self.dataSource = nil;
    }
    if (_captionsArr) {
        self.captionsArr = nil;
    }
    if (_stylesInfo) {
        self.stylesInfo = nil;
    }
    if (_titleView) {
        [_titleView removeFromSuperview];
        self.titleView = nil;
    }
    if (_titleLabel) {
        [_titleLabel removeFromSuperview];
        self.titleLabel = nil;
    }
    if (_placeholderImg) {
        self.placeholderImg = nil;
    }
    if (slideImages) {
        [slideImages removeAllObjects];
        self.slideImages = nil;
    }
    if (_scrollerView) {
        [_scrollerView removeFromSuperview];
        _scrollerView.delegate = nil;
        self.scrollerView = nil;
    }
    if (curRahmen) {
        [curRahmen removeFromSuperview];
        self.curRahmen = nil;
    }
    if (preRahmen) {
        [preRahmen removeFromSuperview];
        self.preRahmen = nil;
    }
    if (nextRahmen) {
        [nextRahmen removeFromSuperview];
        self.nextRahmen = nil;
    }
    if (_timer) {
        [_timer invalidate];
        self.timer = nil;
    }
    if (_indicator) {
        self.indicator = nil;
    }
    if (_pageControl) {
        [_pageControl removeFromSuperview];
        self.pageControl = nil;
    }
    if (_cursor) {
        [_cursor removeFromSuperview];
        self.cursor = nil;
    }
}

- (void)hide:(NSDictionary *)params_ {
    _mainView.hidden = YES;
}

- (void)show:(NSDictionary *)params_ {
    _mainView.hidden = NO;
}

- (void)open:(NSDictionary *)params_ {
    if (_mainView) {
        [[_mainView superview] bringSubviewToFront:_mainView];
        _mainView.hidden = NO;
        return;
    }
    //数据源
    _dataSource = [params_ dictValueForKey:@"data" defaultValue:@{}];
    NSArray *pathAry = [NSArray arrayWithArray:[_dataSource arrayValueForKey:@"paths" defaultValue:@[]]];
    _captionsArr = [NSArray arrayWithArray:[_dataSource arrayValueForKey:@"captions" defaultValue:@[]]];
    if (!pathAry || pathAry.count == 0) {
        [self sendResultEventWithCallbackId:openCbid dataDict:[NSDictionary dictionaryWithObject:[NSNumber numberWithBool:NO] forKey:@"status"] errDict:nil doDelete:NO];
        return;
    }
    
    currentIndex = 0;
    openCbid = [params_ integerValueForKey:@"cbId" defaultValue:-1];
    NSDictionary *rectInfo = [params_ dictValueForKey:@"rect" defaultValue:@{}];
    NSString *fixedOn = [params_ stringValueForKey:@"fixedOn" defaultValue:nil];
    UIView *superView = [self getViewByName:fixedOn];
    float mX = [rectInfo floatValueForKey:@"x" defaultValue:0];
    float mY = [rectInfo floatValueForKey:@"y" defaultValue:0];
    float mW = [rectInfo floatValueForKey:@"w" defaultValue:superView.frame.size.width];
    float mH = [rectInfo floatValueForKey:@"h" defaultValue:mW*(2.0/3.0)];
    CGRect defaultRect = CGRectMake(mX, mY, mW, mH);
    CGRect newRect = [params_ rectValueForKey:@"rect" defaultValue:defaultRect relativeToSuperView:superView];
     mW = newRect.size.width;
    mH = newRect.size.height;
    _mainView = [[UIView alloc] initWithFrame:newRect];
    _mainView.backgroundColor = [UIColor clearColor];
    
    //占位图
    NSString *placeholdImgPath = [params_ stringValueForKey:@"placeholderImg" defaultValue:nil];
    if ([placeholdImgPath isKindOfClass:[NSString class]] && placeholdImgPath.length>0) {
        _placeholderPath = [self getPathWithUZSchemeURL:placeholdImgPath];
    } else {
        _placeholderPath = [[NSBundle mainBundle]pathForResource:@"res_UIScrollPicture/default" ofType:@"png"];
    }
    _placeholderImg = [UIImage imageWithContentsOfFile:_placeholderPath];
    
    //标题信息读取
    self.stylesInfo = [params_ dictValueForKey:@"styles" defaultValue:@{}];
    NSDictionary *captionInfo = [_stylesInfo dictValueForKey:@"caption" defaultValue:@{}];
    float titleHeight = [captionInfo floatValueForKey:@"height" defaultValue:35.0];
    NSString *titleBgColor = [captionInfo stringValueForKey:@"bgColor" defaultValue:@"#696969"];
    NSString *titleColor = [captionInfo stringValueForKey:@"color" defaultValue:@"#E0FFFF"];
    float titleSize = [captionInfo floatValueForKey:@"size" defaultValue:13.0];
    NSString *position = [captionInfo stringValueForKey:@"position" defaultValue:@"bottom"];
    NSString *textAlign = [captionInfo stringValueForKey:@"alignment" defaultValue:@"left"];
    NSTextAlignment alignText = NSTextAlignmentLeft;
    if ([textAlign isEqualToString:@"center"]) {
        alignText = NSTextAlignmentCenter;
    } else if ([textAlign isEqualToString:@"right"]) {
        alignText = NSTextAlignmentRight;
    }
    //初始化图片路径数组
    if (!self.slideImages) {
        self.slideImages = [NSMutableArray arrayWithCapacity:1];
    }
    for (NSString *url in pathAry) {
        if (![url isKindOfClass:[NSString class]] || url.length==0) {
            [self.slideImages addObject:_placeholderPath];
            continue;
        }
        if ([url hasPrefix:@"http"]) {
            [self.slideImages addObject:url];
        } else {
            NSString *realUrl = [self getPathWithUZSchemeURL:url];
            [self.slideImages addObject:realUrl];
        }
    }

    //scrollView初始化并设置相关属性
    float heightSV = mH;
    if ([position isEqualToString:@"bottom"] && _captionsArr.count>0) {
        heightSV = mH - titleHeight;
    } else {
        heightSV = mH;
    }
    touchWait = [params_ boolValueForKey:@"touchWait" defaultValue:NO];
    _scrollerView = [[UZScrollView alloc] initWithFrame:_mainView.bounds];
    _scrollerView.scrollsToTop = NO;
    CGRect svRect = _scrollerView.frame;
    svRect.size.height = heightSV;
    _scrollerView.frame = svRect;
    _scrollerView.pagingEnabled = YES;
    _scrollerView.bounces = NO;
    _scrollerView.directionalLockEnabled = YES;
    _scrollerView.showsHorizontalScrollIndicator = NO;
    _scrollerView.showsVerticalScrollIndicator = NO;
    _scrollerView.backgroundColor = [UIColor clearColor];
    [_scrollerView setContentSize:CGSizeMake(mW * 3, heightSV)];
    [_scrollerView setContentOffset:CGPointMake(mW, 0) animated:NO];
    _scrollerView.delegate = self;
    if (touchWait) {
        _scrollerView.touchWait = touchWait;
        __weak typeof(self) weakSelf = self;
        _scrollerView.touchWaitBlock = ^(BOOL touchWait) {
            __strong typeof(weakSelf) strongSelf = weakSelf;
            if (touchWait) {
                if (strongSelf.timer != nil) {
                    [strongSelf.timer invalidate];
                    strongSelf.timer = nil;
                }
                [strongSelf handleSingleTap:nil];
                
            }else{
                if (strongSelf.timer == nil) {
                    strongSelf.timer = [NSTimer scheduledTimerWithTimeInterval:strongSelf->intervaL target:strongSelf selector:@selector(autoTurnPage:) userInfo:nil repeats:YES];
                    [[NSRunLoop currentRunLoop] addTimer:strongSelf.timer forMode:NSRunLoopCommonModes];
                }
            }
        };
    }else{
        //监听点击
        UITapGestureRecognizer *singleTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleSingleTap:)];
        singleTap.delaysTouchesBegan = YES;
        singleTap.numberOfTapsRequired = 1;
        [_scrollerView addGestureRecognizer:singleTap];
    }
    
    [_mainView addSubview:_scrollerView];
    
    
    
    
    //标题
    _titleView = [[UIView alloc] initWithFrame:CGRectMake(0, mH - titleHeight, mW, titleHeight)];
    _titleView.backgroundColor = [UZAppUtils colorFromNSString:titleBgColor];
    CGRect titleLabelRect = _titleView.bounds;
    titleLabelRect.origin.x = 5;
    titleLabelRect.size.width -= 10;
    _titleLabel = [[UILabel alloc] initWithFrame:titleLabelRect];
    _titleLabel.backgroundColor = [UIColor clearColor];
    _titleLabel.textColor = [UZAppUtils colorFromNSString:titleColor];
    _titleLabel.font = [UIFont systemFontOfSize:titleSize];
    _titleLabel.textAlignment = alignText;
    _titleLabel.numberOfLines = 0;
    [_titleView addSubview:_titleLabel];
    [_mainView addSubview:_titleView];
    
    //个性化设置
    BOOL isAuto = [params_ boolValueForKey:@"auto" defaultValue:true];//是否自动切换
    
    orignalLoop = [params_ boolValueForKey:@"loop" defaultValue:true];//是否循环
    scrollLoop = orignalLoop;
    if (self.slideImages.count == 1) {
        scrollLoop = NO;
    }
    NSString *contentMode = [params_ stringValueForKey:@"contentMode" defaultValue:@"scaleToFill"];
    float cornerRadius = [params_ floatValueForKey:@"cornerRadius" defaultValue:0];
    float scrollerCorner = [params_ floatValueForKey:@"scrollerCorner" defaultValue:0];

    //前一个视图
    self.preRahmen = [[UZRahmen alloc]init];
    self.preRahmen.placeholder = _placeholderImg;
    self.preRahmen.frame = CGRectMake(0, 0, mW, heightSV);
    self.preRahmen.imgContentMode = contentMode;
    self.preRahmen.imgCornerRadius = cornerRadius;
    #pragma mark - 加载图片
    [self.preRahmen setPath:self.slideImages.lastObject];
    [_scrollerView addSubview:self.preRahmen];
    //当前视图
    self.curRahmen = [[UZRahmen alloc]init];
    self.curRahmen.placeholder = _placeholderImg;
    self.curRahmen.frame = CGRectMake(mW, 0, mW, heightSV);
    self.curRahmen.imgContentMode = contentMode;
    self.curRahmen.imgCornerRadius = cornerRadius;
    #pragma mark - 加载图片
    [self.curRahmen setPath:self.slideImages[0]];
    [_scrollerView addSubview:self.curRahmen];
    //下一个视图
    NSString *nextImvPath = nil;
    if (slideImages.count >= 2) {
        nextImvPath = self.slideImages[1];
    } else {
        nextImvPath = self.slideImages[0];
    }
    self.nextRahmen = [[UZRahmen alloc]init];
    self.nextRahmen.placeholder = _placeholderImg;
    self.nextRahmen.frame = CGRectMake(mW*2, 0, mW, heightSV);
    self.nextRahmen.imgContentMode = contentMode;
    self.nextRahmen.imgCornerRadius = cornerRadius;
    #pragma mark - 加载图片
    [self.nextRahmen setPath:nextImvPath];
    [_scrollerView addSubview:self.nextRahmen];
    
    _scrollerView.layer.cornerRadius = scrollerCorner;
    
    //图片自动联播
    intervaL = [params_ floatValueForKey:@"interval" defaultValue:3.0];
    if (isAuto) {
        _timer = [NSTimer scheduledTimerWithTimeInterval:intervaL target:self selector:@selector(autoTurnPage:) userInfo:nil repeats:YES];
        [[NSRunLoop currentRunLoop] addTimer:_timer forMode:NSRunLoopCommonModes];
    }

    
//    if(touchWait){
//        UILongPressGestureRecognizer * longPressGesture =[[UILongPressGestureRecognizer alloc]initWithTarget:self action:@selector(longPressAction:)];
//        longPressGesture.minimumPressDuration=.1f;//设置长按 时间
//        [_scrollerView addGestureRecognizer:longPressGesture];
//    }
    
   
    #pragma mark - 页面控制器
    _indicator = [_stylesInfo dictValueForKey:@"indicator" defaultValue:nil];
    if (_indicator) {
        
        NSDictionary * dot = [_indicator dictValueForKey:@"dot" defaultValue:nil];
        if (dot) { // 自定义页面控制器的小圆点形状，间距
            self.isDot = YES;
            float w = [dot floatValueForKey:@"w" defaultValue:0];
            float h = [dot floatValueForKey:@"h" defaultValue:0];
            float r = [dot floatValueForKey:@"r" defaultValue:0];
            float margin = [dot floatValueForKey:@"margin" defaultValue:0];
            
            self.marginX = w + margin;
            self.controlw = (self.slideImages.count - 1 ) * self.marginX + w;
            float controlx;
            NSString *align = [_indicator stringValueForKey:@"align" defaultValue:@"center"];
            if ([align isEqualToString:@"center"]) {
                controlx = mW/2.0 - self.controlw/2;
            } else if ([align isEqualToString:@"left"]) {
                controlx = 0;
            } else {
                controlx = mW - self.controlw;
            }
            float conY = mH - (h + 20 + 1) - titleHeight;
            if (_captionsArr.count == 0) {
                conY = mH - (h + 20 + 1);
            }
            UZUIPageControl * pageControl = [[UZUIPageControl alloc]initWithFrame:CGRectMake(controlx,conY,self.controlw,h + 20)];
            pageControl.width = w;
            pageControl.height = h;
            pageControl.radius = r;
            pageControl.margin = margin;
            _pageControl = pageControl;
        }else{ // 使用系统页面控制器的样式
            self.controlw = self.slideImages.count*20.0;
            float controlx;
            NSString *align = [_indicator stringValueForKey:@"align" defaultValue:@"center"];
            if ([align isEqualToString:@"center"]) {
                controlx = mW/2.0 - self.controlw/2;
            } else if ([align isEqualToString:@"left"]) {
                controlx = 0;
            } else {
                controlx = mW - self.controlw;
            }
            float conY = mH - 19.0 - titleHeight;
            if (_captionsArr.count == 0) {
                conY = mH - 19.0;
            }
            _pageControl = [[UIPageControl alloc]initWithFrame:CGRectMake(controlx,conY,self.controlw,18)];
        }
        if ([[[UIDevice currentDevice] systemVersion] floatValue] >= 6.0) {
            NSString *activeColor = [_indicator stringValueForKey:@"activeColor" defaultValue:@"#DA70D6"];
            NSString *color = [_indicator stringValueForKey:@"color" defaultValue:@"#FFFFFF"];
            [_pageControl setCurrentPageIndicatorTintColor:[UZAppUtils colorFromNSString:activeColor]];
            [_pageControl setPageIndicatorTintColor:[UZAppUtils colorFromNSString:color]];
        }
        _pageControl.numberOfPages = self.slideImages.count;
        _pageControl.currentPage = 0;
        [_pageControl addTarget:self action:@selector(pageControlTurnPage:) forControlEvents:UIControlEventTouchUpInside];
        [_mainView addSubview:_pageControl];
        #pragma mark - 游标
        float curosrX;
        if (self.isDot ) {
            if (self.slideImages.count%2 == 1) {
                curosrX = _pageControl.frame.origin.x + self.controlw/2.0 - (self.slideImages.count/2) * self.marginX - 6.5;
            } else {
                curosrX = _pageControl.frame.origin.x + self.controlw/2.0 - (self.slideImages.count/2)*self.marginX + self.marginX * 0.5 - 6.5;
            }
        }else{
            if (self.slideImages.count%2 == 1) {
                curosrX = _pageControl.frame.origin.x + self.controlw/2.0 - (self.slideImages.count/2)*16.0 - 8;
            } else {
                curosrX = _pageControl.frame.origin.x + self.controlw/2.0 - (self.slideImages.count/2)*16.0;
            }
        }
        float curosrY = _titleView.frame.origin.y;
        CGRect rectCur = CGRectMake(curosrX, curosrY-7, 13, 7);
        _cursor = [[UZUICursor alloc]initWithFrame:rectCur andColor:[UZAppUtils colorFromNSString:titleBgColor]];
        _cursor.backgroundColor = [UIColor clearColor];
        [_mainView addSubview:_cursor];
    }
    if (_captionsArr.count > 0) {
        _titleLabel.text = _captionsArr[0];
    } else {
        _titleView.hidden = YES;
        _cursor.hidden = YES;
    }
    BOOL fixed = [params_ boolValueForKey:@"fixed" defaultValue:true];
    [self addSubview:_mainView fixedOn:fixedOn fixed:fixed];
    
    //回调
    NSMutableDictionary *sendDict = [NSMutableDictionary dictionaryWithCapacity:3];
    [sendDict setObject:@"show" forKey:@"eventType"];
    [sendDict setObject:[NSNumber numberWithBool:YES] forKey:@"status"];
    [sendDict setObject:[NSNumber numberWithInt:0] forKey:@"index"];
    [self sendResultEventWithCallbackId:openCbid dataDict:sendDict errDict:nil doDelete:NO];
}

- (void)reloadData:(NSDictionary *)params_ {
    if (!_mainView) {
        return;
    }
    NSDictionary *newData = [params_ dictValueForKey:@"data" defaultValue:@{}];
    NSArray *newPathAry = [newData arrayValueForKey:@"paths" defaultValue:@[]];
    NSArray *newCaptionAry = [newData arrayValueForKey:@"captions" defaultValue:@[]];
    if (newPathAry.count > 0) {
        [self.slideImages removeAllObjects];
        for (NSString *url in newPathAry) {
            if (![url isKindOfClass:[NSString class]] || url.length==0) {
                [self.slideImages addObject:_placeholderPath];
                continue;
            }
            if ([url hasPrefix:@"http"]) {
                [slideImages addObject:url];
            } else {
                NSString *realUrl = [self getPathWithUZSchemeURL:url];
                [slideImages addObject:realUrl];
            }
        }
    }
    if (self.slideImages.count == 1) {
        scrollLoop = NO;
    } else {
        scrollLoop = orignalLoop;
    }
    if (newCaptionAry.count > 0) {
        self.captionsArr = newCaptionAry;
        self.titleLabel.text = newCaptionAry[0];
        if (_titleView.hidden) {
            _titleView.hidden = NO;
            _cursor.hidden = NO;
        }
    } else {
        _titleView.hidden = YES;
        _cursor.hidden = YES;
    }
    NSDictionary *caption = [_stylesInfo dictValueForKey:@"caption" defaultValue:@{}];
    float titleHeight = [caption floatValueForKey:@"height" defaultValue:35.0];
    float totalH = _mainView.frame.size.height;
    float totalW = _scrollerView.frame.size.width;
    float controlw = self.slideImages.count*20;
    if (_indicator) {//刷新页面控制器
        NSDictionary * dot = [_indicator dictValueForKey:@"dot" defaultValue:nil];
        
        if (dot) {
            self.isDot = YES;
            float w = [dot floatValueForKey:@"w" defaultValue:0];
            float h = [dot floatValueForKey:@"h" defaultValue:0];
            float r = [dot floatValueForKey:@"r" defaultValue:0];
            float margin = [dot floatValueForKey:@"margin" defaultValue:0];
            
            self.marginX = w + margin;
            self.controlw = (self.slideImages.count - 1 ) * self.marginX + w;
            float controlx;
            NSString *align = [_indicator stringValueForKey:@"align" defaultValue:@"center"];
            if ([align isEqualToString:@"center"]) {
                controlx = totalW/2.0 - self.controlw/2;
            } else if ([align isEqualToString:@"left"]) {
                controlx = 0;
            } else {
                controlx = totalW - self.controlw;
            }
            float conY = totalH - (h + 20 + 1) - titleHeight;
            if (_captionsArr.count == 0) {
                conY = totalH - (h + 20 + 1);
            }
            _pageControl.frame = CGRectMake(controlx,conY,self.controlw,h + 20);
//            UZUIPageControl * pageControl = [[UZUIPageControl alloc]initWithFrame:CGRectMake(controlx,conY,self.controlw,h + 20)];
//            pageControl.width = w;
//            pageControl.height = h;
//            pageControl.radius = r;
//            pageControl.margin = margin;
//            _pageControl = pageControl;
        }else {
            float controlx;
            NSString *align = [_indicator stringValueForKey:@"align" defaultValue:@"center"];
            if ([align isEqualToString:@"center"]) {
                controlx = totalW/2.0 - controlw/2.0;
            } else if ([align isEqualToString:@"left"]){
                controlx = 0;
            } else {
                controlx = totalW - controlw;
            }
            float conY = totalH - 19 - titleHeight;
            if (_captionsArr.count == 0) {
                conY = totalH - 19;
            }
            _pageControl.frame = CGRectMake(controlx,conY,controlw,18);
        }
        
        _pageControl.numberOfPages = self.slideImages.count;
        _pageControl.currentPage = 0;
    }
    {//刷新游标
        float curosrX;
        if (self.slideImages.count % 2 == 1){
            curosrX = _pageControl.frame.origin.x + controlw/2.0 - (self.slideImages.count/2)*16.0 - 8;
        } else {
            curosrX = _pageControl.frame.origin.x + controlw/2.0 - (self.slideImages.count/2)*16.0;
        }
        float y = _titleView.frame.origin.y;
        if (_titleView.hidden) {
            y = 0;
        }
        CGRect rectCur = CGRectMake(curosrX, y-7, 13, 7);
        _cursor.frame = rectCur;
        self.preRahmen.path = [self.slideImages lastObject];
        self.curRahmen.path = self.slideImages[0];
        NSString *nextImvPath = nil;
        if (slideImages.count >= 2) {
            nextImvPath = self.slideImages[1];
        } else {
            nextImvPath = self.slideImages[0];
        }
        self.nextRahmen.path = nextImvPath;
        _scrollerView.contentOffset = CGPointMake(UISCROLLWIDTH, 0);
        currentIndex = 0;
    }
    if (_timer) {
        [_timer invalidate];
        self.timer = nil;
        _timer = [NSTimer scheduledTimerWithTimeInterval:intervaL
                                                  target:self
                                                selector:@selector(autoTurnPage:)
                                                userInfo:nil
                                                 repeats:YES];
        [[NSRunLoop currentRunLoop] addTimer:_timer forMode:NSRunLoopCommonModes];
    }
}

- (void)setCurrentIndex:(NSDictionary *)params_ {
    NSInteger index = [params_ integerValueForKey:@"index" defaultValue:0];
    if (index > currentIndex) {
        self.nextRahmen.path = [self.slideImages objectAtIndex:currentIndex];
        [_scrollerView setContentOffset:CGPointMake(UISCROLLWIDTH*2.0, 0) animated:YES];
    } else if (index < currentIndex) {
        self.preRahmen.path = [self.slideImages objectAtIndex:currentIndex];
        [_scrollerView setContentOffset:CGPointMake(0, 0) animated:YES];
    } else {
        return;
    }
    currentIndex = index;
    [self updateCursorPosition:currentIndex];
    if (_timer) {
        [_timer invalidate];
        self.timer = nil;
        _timer = [NSTimer scheduledTimerWithTimeInterval:intervaL target:self selector:@selector(autoTurnPage:) userInfo:nil repeats:YES];
        [[NSRunLoop currentRunLoop] addTimer:_timer forMode:NSRunLoopCommonModes];
    }
}

- (void)addEventListener:(NSDictionary *)params_ {
    NSString *name = [params_ stringValueForKey:@"name" defaultValue:@"scroll"];
    listenerId = -1;
    if ([name isEqualToString:@"scroll"]) {
        listenerId = [params_ integerValueForKey:@"cbId" defaultValue:-1];
    }
}

- (void)clearCache:(NSDictionary *)paramsDict_ {
    [[SDWebImageManager sharedManager].imageCache clearDiskOnCompletion:nil];
}

#pragma mark -
#pragma mark UIScrollViewDelegate
#pragma mark -

- (void)scrollViewDidEndScrollingAnimation:(UIScrollView *)scrollView {//ContentOffset动画滚动结束
    //NSLog(@"*******scrollViewDidEndScrollingAnimation");
    //重设图片及其位置
    [self resetPicture];
    //更新标题&回调
    [self updateTitles];
    [self scrollCallBack];
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    //NSLog(@"*******scrollViewDidScroll%f",scrollView.contentOffset.x);
    if (!scrollLoop) {
        if (currentIndex==0 && scrollView.contentOffset.x<UISCROLLWIDTH ) {
            scrollView.contentOffset = CGPointMake(UISCROLLWIDTH, 0);
            return;
        } else if (currentIndex==(self.slideImages.count-1) && scrollView.contentOffset.x>UISCROLLWIDTH) {
            scrollView.contentOffset = CGPointMake(UISCROLLWIDTH, 0);
            return;
        }
    }
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView {

    if (touchWait) {
        if (self.timer != nil) {
            [self.timer invalidate];
            self.timer = nil;
        }
    }
   
}
// called on finger up if the user dragged. velocity is in points/millisecond. targetContentOffset may be changed to adjust where the scroll view comes to rest
- (void)scrollViewWillEndDragging:(UIScrollView *)scrollView withVelocity:(CGPoint)velocity targetContentOffset:(inout CGPoint *)targetContentOffset NS_AVAILABLE_IOS(5_0) {

}
// called on finger up if the user dragged. decelerate is true if it will continue moving afterwards
- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate {

    if (touchWait) {
        if(self.timer != nil) return;
        self.timer = [NSTimer scheduledTimerWithTimeInterval:intervaL target:self selector:@selector(autoTurnPage:) userInfo:nil repeats:YES];
        [[NSRunLoop currentRunLoop] addTimer:self.timer forMode:NSRunLoopCommonModes];
    }
    
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView {//手指拖动滚动减速
    //NSLog(@"*******scrollViewDidEndDecelerating%f",scrollView.contentOffset.x);
    if (_scrollerView.contentOffset.x == 0) {//往前滚
        if (scrollLoop) {
            if (currentIndex == 0) {
                currentIndex = self.slideImages.count-1;
            } else {
                currentIndex --;
            }
        } else {
            if (currentIndex > 0) {
                currentIndex --;
            }
        }
    } else if(_scrollerView.contentOffset.x == UISCROLLWIDTH*2.0) {//往后滚
        if (scrollLoop) {
            if (currentIndex >= self.slideImages.count-1) {
                currentIndex = 0;
            } else {
                currentIndex ++;
            }
        } else {
            if (currentIndex < self.slideImages.count-1) {
                currentIndex ++;
            }
        }
    } else {//没滚
        scrollView.contentOffset = CGPointMake(UISCROLLWIDTH, 0);
        return;
    }
    //重设图片及其位置
    [self resetPicture];
    //更新标题和游标&回调
    [self updateTitles];
    [self updateCursorPosition:currentIndex];
    [self scrollCallBack];
    //重置timer
    if (_timer) {
        [_timer invalidate];
        self.timer = nil;
        _timer = [NSTimer scheduledTimerWithTimeInterval:intervaL target:self selector:@selector(autoTurnPage:) userInfo:nil repeats:YES];
        [[NSRunLoop currentRunLoop] addTimer:_timer forMode:NSRunLoopCommonModes];
    }
}

#pragma mark - 工具类函数 -

- (void)resetPicture {
    if (currentIndex == 0) {
        self.preRahmen.path = [self.slideImages objectAtIndex:self.slideImages.count-1];
        self.curRahmen.path = [self.slideImages objectAtIndex:0];
        if (self.slideImages.count == 1) {
            self.nextRahmen.path = [self.slideImages objectAtIndex:0];
        } else {
            self.nextRahmen.path = [self.slideImages objectAtIndex:1];
        }
    } else if (currentIndex == self.slideImages.count-1) {
        self.preRahmen.path = [self.slideImages objectAtIndex:self.slideImages.count-2];
        self.curRahmen.path = [self.slideImages objectAtIndex:currentIndex];
        self.nextRahmen.path = [self.slideImages objectAtIndex:0];
    } else {
        self.preRahmen.path = [self.slideImages objectAtIndex:currentIndex-1];
        self.curRahmen.path = [self.slideImages objectAtIndex:currentIndex];
        self.nextRahmen.path = [self.slideImages objectAtIndex:currentIndex+1];
    }
    [_scrollerView setContentOffset:CGPointMake(UISCROLLWIDTH, 0) animated:NO];
    _pageControl.currentPage = currentIndex;
}

- (void)updateTitles {
    if (!_titleView.hidden) {
        NSString *str = nil;
        if (currentIndex >= _captionsArr.count) {
            str = @"";
        } else {
            str = _captionsArr[currentIndex];
        }
        _titleLabel.text = str;
    }
}

- (void)updateCursorPosition:(NSInteger)index {
    CGRect rectCur = _cursor.frame;
    float curosrX;
//    float controlw = self.slideImages.count*20;
    if (self.isDot ) {
        if (self.slideImages.count%2 == 1) {
            curosrX = _pageControl.frame.origin.x + self.controlw/2.0 - (self.slideImages.count/2) * self.marginX - 6.5;
        } else {
            curosrX = _pageControl.frame.origin.x + self.controlw/2.0 - (self.slideImages.count/2)*self.marginX + self.marginX * 0.5 - 6.5;
        }
        rectCur.origin.x = curosrX + self.marginX * index;
    }else{
        if(self.slideImages.count % 2 == 1){
            curosrX = _pageControl.frame.origin.x + self.controlw/2.0 - (self.slideImages.count/2)*16.0 - 8;
        } else {
            curosrX = _pageControl.frame.origin.x + self.controlw/2.0 - (self.slideImages.count/2)*16.0;
        }
        rectCur.origin.x = curosrX + 16*index + 1.5;
    }
    
    //移动动画
    [UIView beginAnimations:@"show" context:NULL];
    [UIView setAnimationCurve:UIViewAnimationCurveEaseIn];
    [UIView setAnimationDuration:0.3];
    [_cursor setFrame:rectCur];
    [UIView commitAnimations];
}

- (void)autoTurnPage:(id)info {//定时自动翻页
    if (scrollLoop) {
        [_scrollerView setContentOffset:CGPointMake(UISCROLLWIDTH*2.0, 0) animated:YES];
        if (currentIndex >= self.slideImages.count-1) {
            currentIndex = 0;
        } else {
            currentIndex ++;
        }
        [self updateCursorPosition:currentIndex];
    } else {
        if (currentIndex < self.slideImages.count-1) {
            [_scrollerView setContentOffset:CGPointMake(UISCROLLWIDTH*2.0, 0) animated:YES];
            currentIndex ++;
            [self updateCursorPosition:currentIndex];
        }
    }
}

#pragma mark - 点击页面控制器
- (void)pageControlTurnPage:(id)sender {
    
    if (_pageControl.currentPage > currentIndex) {
        self.nextRahmen.path = [self.slideImages objectAtIndex:currentIndex];
        [_scrollerView setContentOffset:CGPointMake(UISCROLLWIDTH*2.0, 0) animated:YES];
    } else if (_pageControl.currentPage < currentIndex) {
        self.preRahmen.path = [self.slideImages objectAtIndex:currentIndex];
        [_scrollerView setContentOffset:CGPointMake(0, 0) animated:YES];
    } else {
        return;
    }
    currentIndex = _pageControl.currentPage;
    [self updateCursorPosition:currentIndex];
    if (_timer) {
        [_timer invalidate];
        self.timer = nil;
        _timer = [NSTimer scheduledTimerWithTimeInterval:intervaL target:self selector:@selector(autoTurnPage:) userInfo:nil repeats:YES];
        [[NSRunLoop currentRunLoop] addTimer:_timer forMode:NSRunLoopCommonModes];
    }
    
    if (self.isDot) {
        UZUIPageControl * page = (UZUIPageControl*)sender;
        [page setUpDots];
    }
}

- (void)handleSingleTap:(UITapGestureRecognizer *)tap {
    NSMutableDictionary *send = [NSMutableDictionary dictionaryWithCapacity:2];
    [send setObject:[NSNumber numberWithBool:YES] forKey:@"status"];
    [send setObject:@"click" forKey:@"eventType"];
    [send setObject:[NSNumber numberWithInteger:currentIndex] forKey:@"index"];
    [self sendResultEventWithCallbackId:openCbid dataDict:send errDict:nil doDelete:NO];
}

- (void)scrollCallBack {
    if (listenerId >= 0) {
        NSMutableDictionary *sendDict = [NSMutableDictionary dictionaryWithCapacity:2];
        [sendDict setObject:[NSNumber numberWithInteger:currentIndex] forKey:@"index"];
        [sendDict setObject:[NSNumber numberWithBool:YES] forKey:@"status"];
        [self sendResultEventWithCallbackId:listenerId dataDict:sendDict errDict:nil doDelete:NO];
    }
}

-(void)longPressAction:(UILongPressGestureRecognizer *)longRecognizer{
    
    
    if (longRecognizer.state==UIGestureRecognizerStateBegan) {
        [self.timer invalidate];
         self.timer = nil;
    }
    if (longRecognizer.state==UIGestureRecognizerStateEnded) {
        _timer = [NSTimer scheduledTimerWithTimeInterval:intervaL target:self selector:@selector(autoTurnPage:) userInfo:nil repeats:YES];
        [[NSRunLoop currentRunLoop] addTimer:_timer forMode:NSRunLoopCommonModes];
    }
}


@end
