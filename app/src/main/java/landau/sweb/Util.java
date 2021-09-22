package landau.sweb;
import java.text.*;

import android.icu.text.CharsetDetector;
import android.icu.text.CharsetMatch;
import landau.sweb.utils.*;

public class Util {
	
	private static final String TAG = "Util";

	public static final DateFormat dtf = DateFormat.getDateTimeInstance();
	public static final DateFormat df = DateFormat.getDateInstance();
	public static final NumberFormat nf = NumberFormat.getInstance();

    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss:SSS");
    public static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final DecimalFormat DOUBLE_FORMAT = new DecimalFormat("#,###0.0");
    public static final DecimalFormat INTEGER_FORMAT = new DecimalFormat("#,##0");

	public static final String SPECIAL_CHAR_PATTERNSTR = "([{}^$.\\[\\]|*+?()\\\\])";
	
//	public static CharsetMatch getCharset(final byte [] byteData) {
//		final CharsetDetector detector = new CharsetDetector();
//		detector.setText(byteData);
//		final CharsetMatch match = detector.detect();
//		ExceptionLogger.d(TAG, "match " + match.getName());
//		return match;
//	}
}
