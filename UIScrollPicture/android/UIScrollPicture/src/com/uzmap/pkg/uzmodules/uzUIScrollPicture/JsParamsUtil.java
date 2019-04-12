package com.uzmap.pkg.uzmodules.uzUIScrollPicture;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.Gravity;

import com.uzmap.pkg.uzcore.UZCoreUtil;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import com.uzmap.pkg.uzkit.UZUtility;

public class JsParamsUtil {
	private static JsParamsUtil instance;

	public static JsParamsUtil getInstance() {
		if (instance == null) {
			instance = new JsParamsUtil();
		}
		return instance;
	}

	public JSONObject paramJSONObject(UZModuleContext moduleContext, String name) {
		if (!moduleContext.isNull(name)) {
			return moduleContext.optJSONObject(name);
		}
		return null;
	}

	public int x(UZModuleContext moduleContext) {
		JSONObject rect = paramJSONObject(moduleContext, "rect");
		if (rect != null) {
			return rect.optInt("x", 0);
		}
		return 0;
	}

	public int y(UZModuleContext moduleContext) {
		JSONObject rect = paramJSONObject(moduleContext, "rect");
		if (rect != null) {
			return rect.optInt("y", 0);
		}
		return 0;
	}

	public int w(UZModuleContext moduleContext, Context act) {
		JSONObject rect = paramJSONObject(moduleContext, "rect");
		if (rect != null) {
			String auto = rect.optString("w");
			if (auto != null && auto.equals("auto")) {
				return getScreenWidth((Activity) act);
			}
		}
		if (rect != null) {
			return rect.optInt("w", getScreenWidth((Activity) act));
		}
		return getScreenWidth((Activity) act);
	}

	public int h(UZModuleContext moduleContext, Context act) {
		JSONObject rect = paramJSONObject(moduleContext, "rect");
		if (rect != null) {
			String auto = rect.optString("h");
			if (auto != null && auto.equals("auto")) {
				return UZUtility.parseCssPixel("auto");
			}
		}

		int defaultH = (int) (w(moduleContext, act) * 2.0 / 3.0);
		if (rect != null) {
			return rect.optInt("h", defaultH);
		}
		return defaultH;
	}

	public int pixW(UZModuleContext moduleContext, Context act) {
		JSONObject rect = paramJSONObject(moduleContext, "rect");
		if (rect != null) {
			String auto = rect.optString("w");
			if (auto != null && auto.equals("auto")) {
				return UZUtility.dipToPix(getScreenWidth((Activity) act));
			}
		}
		if (rect != null) {
			return UZUtility.dipToPix(rect.optInt("w",
					getScreenWidth((Activity) act)));
		}
		return UZUtility.dipToPix(getScreenWidth((Activity) act));
	}

	public int pixH(UZModuleContext moduleContext, Context act) {
		JSONObject rect = paramJSONObject(moduleContext, "rect");
		if (rect != null) {
			String auto = rect.optString("h");
			if (auto != null && auto.equals("auto")) {
				return UZUtility.parseCssPixel("auto");
			}
		}
		int defaultH = (int) (w(moduleContext, act) * 2.0 / 3.0);
		if (rect != null) {
			return UZUtility.dipToPix(rect.optInt("h", defaultH));
		}
		return UZUtility.dipToPix(defaultH);
	}

	public List<String> innerParamJSONArray(UZModuleContext moduleContext,
			String parentName, String name) {
		JSONObject data = paramJSONObject(moduleContext, parentName);
		List<String> list = new ArrayList<String>();
		if (data != null) {
			if (!data.isNull(name)) {
				JSONArray pathsArray = data.optJSONArray(name);
				if (pathsArray.length() > 0) {
					for (int i = 0; i < pathsArray.length(); i++) {
						list.add(pathsArray.optString(i));
					}
					return list;
				}
			}
		}
		return null;
	}

	public List<String> paths(UZModuleContext moduleContext) {
		return innerParamJSONArray(moduleContext, "data", "paths");
	}

	public List<String> captions(UZModuleContext moduleContext) {
		return innerParamJSONArray(moduleContext, "data", "captions");
	}

	public JSONObject innerParamJSONObject(UZModuleContext moduleContext,
			String parentName, String name) {
		JSONObject styles = paramJSONObject(moduleContext, parentName);
		if (styles != null) {
			JSONObject innerObject = styles.optJSONObject(name);
			if (innerObject != null) {
				return innerObject;
			}
		}
		return null;
	}

	public int captionHeight(UZModuleContext moduleContext) {
		JSONObject caption = innerParamJSONObject(moduleContext, "styles",
				"caption");
		if (caption != null) {
			return caption.optInt("height", 35);
		}
		return 35;
	}

	public int captionColor(UZModuleContext moduleContext) {
		JSONObject caption = innerParamJSONObject(moduleContext, "styles",
				"caption");
		if (caption != null) {
			return UZUtility.parseCssColor(caption
					.optString("color", "#E0FFFF"));
		}
		return UZUtility.parseCssColor("#E0FFFF");
	}

	public int captionBgColor(UZModuleContext moduleContext) {
		JSONObject caption = innerParamJSONObject(moduleContext, "styles",
				"caption");
		if (caption != null) {
			return UZUtility.parseCssColor(caption.optString("bgColor",
					"#696969"));
		}
		return UZUtility.parseCssColor("#696969");
	}

	public int captionSize(UZModuleContext moduleContext) {
		JSONObject caption = innerParamJSONObject(moduleContext, "styles",
				"caption");
		if (caption != null) {
			return caption.optInt("size", 13);
		}
		return 13;
	}

	public String captionPosition(UZModuleContext moduleContext) {
		JSONObject caption = innerParamJSONObject(moduleContext, "styles",
				"caption");
		if (caption != null) {
			return caption.optString("position", "bottom");
		}
		return "bottom";
	}

	public int captionAlignment(UZModuleContext moduleContext) {
		JSONObject caption = innerParamJSONObject(moduleContext, "styles",
				"caption");
		if (caption != null) {
			String alignment = caption.optString("alignment", "left");
			if (alignment.equals("right")) {
				return Gravity.RIGHT;
			} else if (alignment.equals("center")) {
				return Gravity.CENTER_HORIZONTAL;
			}
		}
		return Gravity.LEFT;
	}

	public boolean isIndicatorShow(UZModuleContext moduleContext) {
		JSONObject indicator = innerParamJSONObject(moduleContext, "styles",
				"indicator");
		if (indicator != null) {
			return true;
		}
		return false;
	}

	public String indicatorAlign(UZModuleContext moduleContext) {
		JSONObject indicator = innerParamJSONObject(moduleContext, "styles",
				"indicator");
		if (indicator != null) {
			return indicator.optString("align", "center");
		}
		return "center";
	}

	public int indicatorColor(UZModuleContext moduleContext) {
		JSONObject indicator = innerParamJSONObject(moduleContext, "styles",
				"indicator");
		if (indicator != null) {
			return UZUtility.parseCssColor(indicator.optString("color",
					"#FFFFFF"));
		}
		return UZUtility.parseCssColor("#FFFFFF");
	}

	public int indicatorActiveColor(UZModuleContext moduleContext) {
		JSONObject indicator = innerParamJSONObject(moduleContext, "styles",
				"indicator");
		if (indicator != null) {
			return UZUtility.parseCssColor(indicator.optString("activeColor",
					"#DA70D6"));
		}
		return UZUtility.parseCssColor("#DA70D6");
	}

	public Bitmap placeholderImg(UZModuleContext moduleContext, UZModule module) {
		String path = moduleContext.optString("placeholderImg");
		path = module.makeRealPath(path);
		return getBitmap(path);
	}

	public String contentMode(UZModuleContext moduleContext) {
		return moduleContext.optString("contentMode", "scaleToFill");
	}

	public int interval(UZModuleContext moduleContext) {
		return moduleContext.optInt("interval", 3);
	}

	public boolean auto(UZModuleContext moduleContext) {
		return moduleContext.optBoolean("auto", true);
	}
	
	public boolean touchWait(UZModuleContext moduleContext) {
		return moduleContext.optBoolean("touchWait", false);
	}

	public boolean loop(UZModuleContext moduleContext) {
		return moduleContext.optBoolean("loop", true);
	}

	public String fixedOn(UZModuleContext moduleContext) {
		return moduleContext.optString("fixedOn");
	}

	public boolean fixed(UZModuleContext moduleContext) {
		return moduleContext.optBoolean("fixed", true);
	}

	public int currentIndex(UZModuleContext moduleContext) {
		return moduleContext.optInt("index", 0);
	}

	public Bitmap getBitmap(String path) {
		Bitmap bitmap = null;
		InputStream input = null;
		try {
			input = UZUtility.guessInputStream(path);
			bitmap = BitmapFactory.decodeStream(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	public int getScreenWidth(Activity act) {
		DisplayMetrics metric = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return UZCoreUtil.pixToDip(metric.widthPixels);
	}

	public int getScreenHeight(Activity act) {
		DisplayMetrics metric = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return UZCoreUtil.pixToDip(metric.heightPixels);
	}

	public int pixScreenHeight(Activity act) {
		DisplayMetrics metric = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric.heightPixels;
	}

	public int indicatorDotW(UZModuleContext moduleContext) {
		JSONObject indicator = innerParamJSONObject(moduleContext, "styles",
				"indicator");
		if (indicator != null) {
			JSONObject dot = indicator.optJSONObject("dot");
			if(dot == null)
				return 20;
			else
				return dot.optInt("w", 20);
		}
		return 0;
	}

	public int indicatorDotH(UZModuleContext moduleContext) {
		JSONObject indicator = innerParamJSONObject(moduleContext, "styles",
				"indicator");
		if (indicator != null) {
			JSONObject dot = indicator.optJSONObject("dot");
			if(dot == null)
				return 10;
			else
				return dot.optInt("h", 10);
		}
		return 0;
	}

	public int indicatorDotR(UZModuleContext moduleContext) {
		JSONObject indicator = innerParamJSONObject(moduleContext, "styles",
				"indicator");
		if (indicator != null) {
			JSONObject dot = indicator.optJSONObject("dot");
			if(dot == null ) {
				return 5;
			}else {
				int r = dot.optInt("r", 5);
				if (r <= 0) {
					r = 5;
				}
				return r;
			}
		}
		return 5;
	}

	public int indicatorDotMargin(UZModuleContext moduleContext) {
		JSONObject indicator = innerParamJSONObject(moduleContext, "styles", "indicator");
		if (indicator != null) {
			JSONObject dot = indicator.optJSONObject("dot");
			if(dot == null)
				return 20;
			else
				return dot.optInt("margin", 20);
		}
		return 0;
	}
	
	/**
	 * 获取圆角半径
	 * @param moduleContext
	 * @return
	 */
	public int cornerRadius(UZModuleContext moduleContext) {
		int radius = 0;
		return moduleContext.optInt("cornerRadius", radius);
	}

}
