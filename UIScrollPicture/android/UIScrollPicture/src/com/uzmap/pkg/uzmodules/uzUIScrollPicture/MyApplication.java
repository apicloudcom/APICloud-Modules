package com.uzmap.pkg.uzmodules.uzUIScrollPicture;

import org.xutils.x;

import com.uzmap.pkg.uzcore.uzmodule.AppInfo;
import com.uzmap.pkg.uzcore.uzmodule.ApplicationDelegate;

import android.app.Application;
import android.content.Context;

public class MyApplication extends ApplicationDelegate{
	@Override
	public void onApplicationCreate(Context context, AppInfo info) {
		super.onApplicationCreate(context, info);
		x.Ext.init((Application)context);
	}
}
