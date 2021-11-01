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
	


	public static String replaceAll(String s, String as[], String as1[]) {
		// long millis = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder();
		for (int k = 0; k < as.length; k++) {
			if (as[k].length() > 0) {
				int i = 0;
				sb.setLength(0);
				int j;
				while ((j = s.indexOf(as[k], i)) >= 0) {
					sb.append(s, i, j);
					sb.append(as1[k]);
					// LOGGER.info("replaced " + as[k] + " = " + as1[k]);
					i = j + as[k].length();
				}
				sb.append(s, i, s.length());
				s = sb.toString();
			}
		}
		// LOGGER.info("replaced result: " + s);
		return s;
	}
	
}
