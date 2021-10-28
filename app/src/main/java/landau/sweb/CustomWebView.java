package landau.sweb;

import android.webkit.*;
import android.content.*;
import android.util.*;

public class CustomWebView extends WebView {
	public CustomWebView(Context context) {
		super(context);
	}

    public CustomWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

    public CustomWebView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

    public CustomWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
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
