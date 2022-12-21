package net.gnu.common;

import android.content.Context;
import android.webkit.WebView;

public class CustomWebView extends WebView {

	public CustomWebView(final Context context) {
		super(context);
	}

	public int computeVerticalScrollOffsetMethod() {
		return computeVerticalScrollOffset();
	}

	public int computeVerticalScrollRangeMethod() {
		return computeVerticalScrollRange();
	}

	public int computeVerticalScrollExtentMethod() {
		return computeVerticalScrollExtent();
	}
    
}
