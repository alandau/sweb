package landau.sweb;

import android.webkit.*;
import android.content.*;
import android.util.*;

public class CustomWebView extends WebView {
	
	MainActivity.Tab tab;
	
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
