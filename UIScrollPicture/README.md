# 概述

图片轮播器模块源码（内含iOS和android）

APICloud 的 UIScrollPicture 模块是一个图片轮播器模块，可通过此模块打开一个图片轮播器，支持自定义面板，滑动指示器和标注提示等相关功能。但是由于本模块 UI 布局界面为固定模式，不能满足日益增长的广大开发者对搜索模块样式的需求。因此，广大原生模块开发者，可以参考此模块的开发方式、接口定义等开发规范，或者基于此模块开发出更多符合产品设计的新 UI 布局的模块，希望此模块能起到抛砖引玉的作用。

# 模块文档

<p style="color: #ccc; margin-bottom: 30px;">来自于：官方<a style="background-color: #95ba20; color:#fff; padding:4px 8px;border-radius:5px;margin-left:30px; margin-bottom:0px; font-size:12px;text-decoration:none;" target="_blank" href="//www.apicloud.com/mod_detail/UIScrollPicture">立即使用</a></p>

<div class="outline">

[open](#m1) 
[close](#m2) 
[show](#m6) 
[hide](#m5) 
[setCurrentIndex](#m3) 
[reloadData](#m4) 
[addEventListener](#m7) 
[clearCache](#m8)

</div>

# 论坛示例

为帮助用户更好更快的使用模块，论坛维护了一个[示例](https://community.apicloud.com/bbs/thread-114186-1-1.html)，示例中包含示例代码、知识点讲解、注意事项等，供您参考。 

# **概述**

UIScrollPicture 是一个图片轮播模块，只需传入一组图片地址，即可实现图片轮播效果。

open 接口内的 rect 参数，可控制图片轮播区域的位置和大小。styles 参数可以设置轮播视图底部的标题文字大小及颜色，亦可设置底部页面控制器（几个小圆点）的位置和样式。

有些应用的头条新闻或广告轮播展示都是无限循环自动播放的，本模块亦可以实现相同的功能，可通过open 接口内的 loop 和 auto 参数控制。每张图片自动轮播时的时间间隔也可自定义，用 interval 即可。为了让原生模块真正的嵌入的网页内，让开发者像使用 js 库一样使用原生模块，APICloud 平台特开放了 fixedOn、fixed 参数。通过配置以上两个参数，可实现让轮播视图随 html 页面上下滚动的功能。

本图片轮播器是由原生代码开发，对于网络图片的展示更加人性化。模块内部会做缓存处理，第一次加载网络图片时，模块会根据其路径生成一个 md5 加密的图片名，并缓存在缓存文件夹里。当用户下次打开同路径的图片时，模块直接从缓存文件内读取该图片，从而大大节省了用户流量。开发者可以通过调用 clearCache 接口清楚本模块在本地缓存的图片资源，调用 api.clearCache 接口会清除本 APP 在本地缓存的所有文件。由于原生代码相对前端代码的高效性，本模块相对于前端实现的图片轮播功能更加流畅，内存管理更加优化。当用户需要展示多张图片时，其内存只保留三张图片的地址，然后来回切换图片内容，从而降低了整个 app 内存占用率。具体模块功能请参考模块接口。

 **不能同时添加的模块：fog2**

 **UIScrollPicture 模块是 scrollPicture 模块的优化版。**

<img src="https://docs.apicloud.com/img/docImage/module-doc-img/layout/UIScrollPicture/UIScrollPicture1.PNG" width=400 />


## [实例widget下载地址](https://github.com/XM-Right/UIScrollPicture-Example/archive/master.zip)

# 模块接口

<div id="m1"></div>

# **open**

打开模块

open({params}, callback(ret, err))

## params

rect：

- 类型：JSON 对象
- 描述：（可选项）模块的位置及尺寸
- 内部字段：

```js
{
    x: 0,   //（可选项）数字类型；模块左上角的 x 坐标（相对于所属的 Window 或 Frame）；默认：0
    y: 0,   //（可选项）数字类型；模块左上角的 y 坐标（相对于所属的 Window 或 Frame）；默认：0
    w: 320, //（可选项）数字类型；模块的宽度；默认：'auto'
    h: 200  //（可选项）数字类型；模块的高度；默认：'auto'
}
```

data：

- 类型：JSON 对象
- 描述：模块的图片路径数组，及说明文字数组
- 内部字段：

```js
{
    paths: [],      //数组类型；图片路径数组，支持http://，https://，widget://，fs://协议
    captions: []    //（可选项）数组类型；说明文字数组，与 paths 数组长度一致；不传则不显示说明文字区域
}
```

styles：

- 类型：JSON 对象
- 描述：（可选项）模块各部分的样式
- 内部字段：

```js
{
    caption: {                          //（可选项）JSON对象；说明文字区域样式
        height: 35,                     //（可选项）数字类型；说明文字区域高度；默认：35.0
        color: '#E0FFFF',               //（可选项）字符串类型；说明文字字体颜色，支持 rgb、rgba、#；默认：'#E0FFFF'
        size: 13,                       //（可选项）数字类型；说明文字字体大小；默认：13.0
        bgColor: '#696969',             //（可选项）字符串类型；说明文字区域的背景色，支持 rgb、rgba、#；默认：'#696969'
        position: 'bottom'              //（可选项）字符串类型；说明文字区域的显示位置；默认：'bottom'
                                        //取值范围：
                                        //overlay（悬浮在图片上方，底部与图片底部对齐）
                                        //bottom（紧跟在图片下方，顶部与图片底部对齐）
        alignment: 'center'             //（可选项）字符串类型：说明文字在水平线上的位置；默认：left
                                        //取值范围：
                                        //right（居右限时）
                                        //center（居中显示）
                                        //left（居左显示）                                  
    },
    indicator: {                        //（可选项）JSON对象；指示器样式；不传则不显示指示器
         dot:{                         // （可选项）JSON对象；指示器小圆点样式；不传则使用系统默认小圆点样式
                           w:20,  //（必选项）数字类型；小圆点宽度
                           h:10, //（必选项）数字类型；小圆点高度
                           r:5,  //（必选项）数字类型；corner 页面控制器指示标记（矩形）的圆角大小
                           margin:20  //（必选项）数字类型；小圆点间距
                         },
        align: 'center',                //（可选项）字符串类型；指示器位置；默认：center
                                        //取值范围：
                                        //center（居中）
                                        //left（靠左）
                                        //right（靠右）
        color: '#FFFFFF',               //（可选项）指示器颜色 ，支持 rgb、rgba、#；默认：'#FFFFFF'
        activeColor: '#DA70D6'          //（可选项）当前指示器颜色，支持 rgb、rgba、#；默认：'#DA70D6'
    }
}
```

placeholderImg：

- 类型：字符串
- 描述：（可选项）网络图片未加载完毕时，模块显示的占位图片，要求本地路径（fs://、widget://）

contentMode：

- 类型：字符串
- 描述：（可选项）图片填充模式
- 默认值：'scaleToFill'
- 取值范围：
    * scaleToFill（填充）
    * scaleAspectFit（适应）

cornerRadius：

- 类型：数字
- 描述：（可选项）图片的圆角半径
- 默认值：0

scrollerCorner：

- 类型：数字
- 描述：（可选项）滑动视图的圆角半径
- 默认值：0

interval：

- 类型：数字
- 描述：（可选项）图片轮换时间间隔，单位是秒（s）
- 默认值：3

auto:

- 类型：布尔
- 描述：（可选项）图片是否自动播放
- 默认值：true


loop:

- 类型：布尔
- 描述：（可选项）图片是否循环播放
- 默认值：true

touchWait:

- 类型：布尔
- 描述：（可选项）触摸停止自动播放
- 默认值：false

fixedOn：

- 类型：字符串类型
- 描述：（可选项）模块视图添加到指定 frame 的名字（只指 frame，传 window 无效）
- 默认：模块依附于当前 window

fixed:

- 类型：布尔
- 描述：（可选项）模块是否随所属 window 或 frame 滚动
- 默认值：true（不随之滚动）

## callback(ret, err)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
    status: true,                  //布尔型；true||false
	eventType: 'click'||'show',    //字符串类型；交互事件类型，
                                   //取值范围：
                                   //click（用户点击）
                                   //show（模块打开成功）
	index：0		               //数字类型；当前图片的索引
}
```

## 示例代码

```js
var UIScrollPicture = api.require('UIScrollPicture');
UIScrollPicture.open({
	rect: {
		x: 0,
		y: 0,
		w: api.winWidth,
		h: api.winHeight / 2
	},
	data: {
		paths: [
			'widget://res/img/apicloud.png',
			'widget://res/img/apicloud-gray.png',
			'widget://res/img/apicloud.png',
			'widget://res/img/apicloud-gray.png'
		],
		captions: ['apicloud', 'apicloud', 'apicloud', 'apicloud']
	},
	styles: {
		caption: {
			height: 35,
			color: '#E0FFFF',
			size: 13,
			bgColor: '#696969',
			position: 'bottom'
		},
		indicator: {
		   dot:{
             w:20,
             h:10,
             r:5,
             margin:20
          },
			align: 'center',
			color: '#FFFFFF',
			activeColor: '#DA70D6'
		}
	},
	placeholderImg: 'widget://res/slide1.jpg',
	contentMode: 'scaleToFill',
	interval: 3,
	fixedOn: api.frameName,
	loop: true,
	fixed: false
}, function(ret, err) {
	if (ret) {
		alert(JSON.stringify(ret));
	} else {
		alert(JSON.stringify(err));
	}
});
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本

<div id="m2"></div>
# **close**

关闭模块

close()

## 示例代码

```js
var UIScrollPicture = api.require('UIScrollPicture');
UIScrollPicture.close();
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本

<div id="m6"></div>
# **show**

显示模块

show()

## 示例代码

```js
var UIScrollPicture = api.require('UIScrollPicture');
UIScrollPicture.show();
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本

<div id="m5"></div>
# **hide**

隐藏模块

hide()

## 示例代码

```js
var UIScrollPicture = api.require('UIScrollPicture');
UIScrollPicture.hide();
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本

<div id="m3"></div>
# **setCurrentIndex**

指定当前项

setCurrentIndex({params})

## params

index：

- 类型：数字
- 描述：（可选项）索引值
- 默认值：0

## 示例代码

```js
var UIScrollPicture = api.require('UIScrollPicture');
UIScrollPicture.setCurrentIndex({
	index: 2
});
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本

<div id="m4"></div>
# **reloadData**

更新模块数据

reloadData({params})

## params

data：

- 类型：JSON 对象
- 描述：模块的图片路径数组，及说明文字数组
- 内部字段：

```js
{
    paths: [],      //（可选项）数组类型；图片路径数组；默认：原 paths 数组
    captions: []    //（可选项）数组类型；说明文字数组，默认：原 captions 数组
}
```

## 示例代码

```js
var UIScrollPicture = api.require('UIScrollPicture');
UIScrollPicture.reloadData({
	data: {
		paths: ['widget://res/img/ic/slide1.jpg', 'widget://res/img/ic/slide2.jpg', 'widget://res/img/ic/slide5.jpg'],
		captions: ['title1', 'title2', 'title3']
	}
});
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本

<div id="m7"></div>
# **addEventListener**

事件监听

addEventListener({params}, callback(ret, err))

## params

name：

- 类型：字符串
- 描述：监听的事件名称，取值范围：'scroll'（图片滚动事件）

## callback(ret, err)

ret：

- 类型：JSON 对象
- 描述：事件触发时回调的参数，可能为空
- 内部字段：

```js
{
    status：true,    //布尔型；true||false
    index：0         //数字类型；当前图片的索引
}
```

## 示例代码

```js
var UIScrollPicture = api.require('UIScrollPicture');
UIScrollPicture.addEventListener({
	name: 'scroll'
}, function(ret, err) {
	if (ret) {
		alert(JSON.stringify(ret));
	} else {
		alert(JSON.stringify(err));
	}
});
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本

<div id="m8"></div>
# **clearCache**

清除缓存到本地的网络图片，**本接口只清除本模块缓存的数据，若要清除本 app 缓存的所有数据则调用 api.clearCache**

clearCache()

## 示例代码

```js
var UIScrollPicture = api.require('UIScrollPicture');
UIScrollPicture.clearCache();
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本
