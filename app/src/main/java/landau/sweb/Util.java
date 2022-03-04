package landau.sweb;
import java.text.*;

import android.icu.text.CharsetDetector;
import android.icu.text.CharsetMatch;
import landau.sweb.utils.*;
import java.io.*;
import java.util.regex.Pattern;

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
	public static String skipParam(final String newLink, final String sign) {
		final int indexQ = newLink.indexOf(sign);
		if (indexQ >= 0) {
			return newLink.substring(0, indexQ);
		}
		return newLink;
	}
	
	private static int parseHex(byte b) {
        if (b >= '0' && b <= '9') return (b - '0');
        if (b >= 'A' && b <= 'F') return (b - 'A' + 10);
        if (b >= 'a' && b <= 'f') return (b - 'a' + 10);

        throw new IllegalArgumentException("Invalid hex char '" + b + "'");
    }
	

	public static byte[] decode(byte[] url) throws IllegalArgumentException {
        if (url.length == 0) {
            return new byte[0];
        }

        // Create a new byte array with the same length to ensure capacity
        final byte[] tempData = new byte[url.length];

        int tempCount = 0;
        for (int i = 0; i < url.length; i++) {
            byte b = url[i];
            if (b == '%') {
                if (url.length - i > 2) {
                    b = (byte) (parseHex(url[i + 1]) * 16
						+ parseHex(url[i + 2]));
                    i += 2;
                } else {
                    throw new IllegalArgumentException("Invalid format");
                }
            }
            tempData[tempCount++] = b;
        }
        final byte[] retData = new byte[tempCount];
        System.arraycopy(tempData, 0, retData, 0, tempCount);
        return retData;
    }

	public static String decodeUrlToFS(final String filename) {
		final byte[] bArr = filename.getBytes();
		final byte[] bDest = decode(bArr);
		try {
			return new String(bDest, "utf-8");
		} catch (UnsupportedEncodingException e) {
			return new String(bDest);
		}
	}

	public static boolean accept(final String urlToString, final Pattern includePattern, final Pattern excludePattern) {
		if (includePattern != null) {
			if (includePattern.matcher(urlToString).matches()) {
				if (excludePattern != null) {
					if (!excludePattern.matcher(urlToString).matches()) {
						return true;
					}
				} else {
					return true;
				}
			}
		} else {
			if (excludePattern != null) {
				if (!excludePattern.matcher(urlToString).matches()) {
					return true;
				}
			} else {
				return true;
			}
		}
		return false;
	}
	
}
