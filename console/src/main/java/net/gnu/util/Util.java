package net.gnu.util;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
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
	public static final Pattern numPat = Pattern.compile("([^\\d]*?)(\\d+)([^\\d]*?)");

	public static String mapToString(final Map<?, ?> list, final boolean number, final String sep) {
		if (list == null) {
			return "";
		}
		final StringBuilder sb = new StringBuilder();
		int len = list.size() - 1;
		int c = 0;
		if (!number) {
			for (Map.Entry obj : list.entrySet()) {
				sb.append(obj.getKey());
				sb.append("=");
				sb.append(obj.getValue());
				if (c++ < len) {
					sb.append(sep);
				}
			}
		} else {
			int counter = 0;
			for (Map.Entry obj : list.entrySet()) {
				sb.append(++counter + ": ");
				sb.append(obj.getKey());
				sb.append("=");
				sb.append(obj.getValue());
				if (c++ < len) {
					sb.append(sep);
				}
			}
		}
		return sb.toString();
	}

	public static String collectionToString(final Collection<?> list, final boolean number, final String sep) {
		if (list == null) {
			return "";
		}
		final StringBuilder sb = new StringBuilder();
		int len = list.size() - 1;
		int c = 0;
		if (!number) {
			for (Object obj : list) {
				sb.append(obj);
				if (c++ < len) {
					sb.append(sep);
				}
			}
		} else {
			int counter = 0;
			for (Object obj : list) {
				sb.append(++counter + ": ").append(obj);
				if (c++ < len) {
					sb.append(sep);
				}
			}
		}
		return sb.toString();
	}

	public static String arrayToString(final Object[] list, final boolean number, final String sep) {
		if (list == null) {
			return "";
		}
		final StringBuilder sb = new StringBuilder();
		int len = list.length - 1;
		int c = 0;
		if (!number) {
			for (Object obj : list) {
				sb.append(obj);
				if (c++ < len) {
					sb.append(sep);
				}
			}
		} else {
			int counter = 0;
			for (Object obj : list) {
				sb.append(++counter + ": ").append(obj);
				if (c++ < len) {
					sb.append(sep);
				}
			}
		}
		return sb.toString();
	}

	public static String replaceAll(String s, String as[], String as1[]) {
		//long millis = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder();
		for (int k = 0; k < as.length; k++) {
			if (as[k].length() > 0) {
				int i = 0;
				sb.setLength(0);
				int j;
				while ((j = s.indexOf(as[k], i)) >= 0) {
					sb.append(s, i, j);
					sb.append(as1[k]);
					//LOGGER.info("replaced " + as[k] + " = " + as1[k]);
					i = j + as[k].length();
				}
				sb.append(s, i, s.length());
				s = sb.toString();
			}
		}
		//LOGGER.info("replaced result: " + s);
		return s;
	}
	public static String skipParam(final String newLink, final String sign) {
		final int indexQ = newLink.indexOf(sign);
		if (indexQ >= 0) {
			return newLink.substring(0, indexQ);
		}
		return newLink;
	}

	public static boolean accept(final String urlToString, final Pattern includePattern, final Pattern excludePattern, final boolean or) {
		if (or) {
			if (includePattern != null) {
				if (includePattern.matcher(urlToString).matches()) {
					return true;
				}
			}
			if (excludePattern != null) {
				if (!excludePattern.matcher(urlToString).matches()) {
					return true;
				}
			}
		} else {
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
				}
			}
		}
		return false;
	}

	public static String textToRegex(final String s) {
		if (s != null) {
			return s
				.replaceAll("\\\\", "\\\\\\\\")
				.replaceAll("\\?", "\\\\?")
				.replaceAll("\\.", "\\\\.")
				.replaceAll("\\*", ".*?")
				.replaceAll("\\+", ".+?")
				.replaceAll("\\(", "\\\\(")
				.replaceAll("\\)", "\\\\)")
				.replaceAll("\\[", "\\\\[")
				.replaceAll("\\]", "\\\\]")
				.replaceAll("\\$", "\\\\\\$")
				.replaceAll("\\^", "\\\\^");
		} else {
			return "";
		}
	}

	public static int compareNumberString(final String file1, final String file2) {
		int lastIndexOf1 = file1.indexOf("/");
		int lastIndexOf2 = file2.indexOf("/");

		String parent1 = "";
		String parent2 = "";
		if (lastIndexOf1 < 0 && lastIndexOf2 >= 0) {
			return -1;
		} else if (lastIndexOf1 >= 0 && lastIndexOf2 < 0) {
			return 1;
		} else if (lastIndexOf1 >= 0 && lastIndexOf2 >= 0) {
			parent1 = file1.substring(0, lastIndexOf1);
			parent2 = file2.substring(0, lastIndexOf2);
			int indexOf1 = lastIndexOf1;
			int indexOf2 = lastIndexOf2;
			String p1, p2;
			while (parent1.equals(parent2)) {
				indexOf1 = file1.indexOf("/", indexOf1+1);
				indexOf2 = file2.indexOf("/", indexOf2+1);
				if (indexOf1 > 0 && indexOf2 > 0
					&& (p1=file1.substring(0, indexOf1)).equals((p2=file2.substring(0, indexOf2)))) {
					lastIndexOf1 = indexOf1;
					lastIndexOf2 = indexOf2;
					parent1 = p1;
					parent2 = p2;
				} else {
					break;
				}
			}
		}
		if (parent1.equals(parent2)) {
			final String name1 = lastIndexOf1 > 0 ? file1.substring(lastIndexOf1) : file1;
			final Matcher matcher1 = numPat.matcher(name1);
			final String name2 = lastIndexOf2 > 0 ? file2.substring(lastIndexOf2) : file2;
			final Matcher matcher2 = numPat.matcher(name2);
			if (matcher1.matches() && matcher2.matches()
				&& matcher1.group(1).equalsIgnoreCase(matcher2.group(1))
				&& matcher1.group(3).equalsIgnoreCase(matcher2.group(3))
				) {
				final BigInteger valueOf1 = new BigInteger(matcher1.group(2));
				final BigInteger valueOf2 = new BigInteger(matcher2.group(2));
				return valueOf1.compareTo(valueOf2);
			}
		}
		return file1.compareToIgnoreCase(file2);
	}

//	public static void setProxy(String ip, int port) {
//		ProxyConfig proxyConfig = new ProxyConfig.Builder()
//			.addProxyRule(ip + ":" + port)
//			//.addBypassRule("www.excluded.*")
//			.addDirect()
//			.build();
//		ProxyController.getInstance().setProxyOverride(proxyConfig,
//			new Executor() {
//				@Override
//				public void execute(Runnable command) {
//					//do nothing
//				}
//			}, new Runnable() {
//				@Override
//				public void run() {
//				}
//			});
//		//ProxyController.getInstance().clearProxyOverride(executor, listener);
//	}
}
