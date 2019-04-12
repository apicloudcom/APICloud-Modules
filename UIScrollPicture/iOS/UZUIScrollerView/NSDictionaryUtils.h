//
//  NSDictionaryUtils.h
//  UZEngine
//
//  Created by kenny on 14-4-9.
//  Copyright (c) 2014年 APICloud. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSDictionary (Utils)

- (bool)existsValue:(NSString*)expectedValue forKey:(NSString*)key;
- (NSInteger)integerValueForKey:(NSString*)key defaultValue:(NSInteger)defaultValue withRange:(NSRange)range;
- (NSInteger)integerValueForKey:(NSString*)key defaultValue:(NSInteger)defaultValue;
- (int)intValueForKey:(NSString*)key defaultValue:(int)defaultValue;
- (long long)longlongValueForKey:(NSString*)key defaultValue:(long long)defaultValue;
- (float)floatValueForKey:(NSString*)key defaultValue:(float)defaultValue;
- (BOOL)boolValueForKey:(NSString*)key defaultValue:(BOOL)defaultValue;
- (NSString*)stringValueForKey:(NSString*)key defaultValue:(NSString*)defaultValue;
- (NSArray *)arrayValueForKey:(NSString *)key defaultValue:(NSArray *)defaultValue;
- (NSDictionary *)dictValueForKey:(NSString *)key defaultValue:(NSDictionary *)defaultValue;
- (CGRect)rectValueForKey:(NSString *)key defaultValue:(CGRect)defaultValue relativeToSuperView:(UIView *)superView;
- (BOOL)typeValueForKey:(NSString*)key isArray:(BOOL*)bArray isNull:(BOOL*)bNull isNumber:(BOOL*)bNumber isString:(BOOL*)bString;
- (BOOL)valueForKeyIsArray:(NSString*)key;
- (BOOL)valueForKeyIsNull:(NSString*)key;
- (BOOL)valueForKeyIsString:(NSString*)key;
- (BOOL)valueForKeyIsNumber:(NSString*)key;

- (NSDictionary*)dictionaryWithLowercaseKeys;

@end
