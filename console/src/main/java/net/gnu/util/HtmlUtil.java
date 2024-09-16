package net.gnu.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import android.util.Log;
import java.io.ByteArrayInputStream;

public class HtmlUtil {
	//<(?:"[^"]*"['"]*|'[^']*'['"]*|[^'">])+(?<!/\s*)>
	public static final String LEEDS_BIT_PALI_TRANSLIT = "LeedsBit PaliTranslit";
	public static final String NORMAN = "Norman";
	public static final String TIMES_CSX_1 = "Times_CSX+1";
	public static final String TIMES_CSX = "Times_CSX+";
	public static final String VZ_TIME = "VZTime";
	public static final String VRI_ROMAN_PALI = "VriRomanPali";
	public static final String VU_TIMES = "VU Times";
	public static final String UNKNOWN1 = "Unknown1";
	public static final String UNKNOWN2 = "Unknown2";
	public static final String CONVERTED_TXT = ".converted.txt";
	// public static final Logger LOGGER = Logger.getLogger(Util.class.getName());
	public static final String UTF8 = "UTF-8";

	public static final Pattern META_PATTERN = Pattern.compile("<meta[^>]+?>",
			Pattern.CASE_INSENSITIVE);
	public static final Pattern BR_PATTERN = Pattern.compile("<br[^>]*?>",
			Pattern.CASE_INSENSITIVE);
	public static final Pattern LINK_PATTERN = Pattern.compile("<link[^>]*?>",
			Pattern.CASE_INSENSITIVE);
	public static final Pattern HR_PATTERN = Pattern.compile(
			"<hr[\\p{Space}]+([^>]*?)>", Pattern.CASE_INSENSITIVE);
	public static final Pattern COL_PATTERN = Pattern.compile(
			"<col[\\p{Space}]+([^>]*?)>", Pattern.CASE_INSENSITIVE);
	public static final Pattern IMG_PATTERN = Pattern.compile(
			"<IMG[\\p{Space}]+([^>]*?)>", Pattern.CASE_INSENSITIVE);

	public static final Pattern H1_PATTERN = Pattern.compile(
			"<H1[\\p{Space}]+([^>]*?)>", Pattern.CASE_INSENSITIVE);
	public static final Pattern H2_PATTERN = Pattern.compile(
			"<H2[\\p{Space}]+([^>]*?)>", Pattern.CASE_INSENSITIVE);
	public static final Pattern H3_PATTERN = Pattern.compile(
			"<H3[\\p{Space}]+([^>]*?)>", Pattern.CASE_INSENSITIVE);
	public static final Pattern H4_PATTERN = Pattern.compile(
			"<H4[\\p{Space}]+([^>]*?)>", Pattern.CASE_INSENSITIVE);
	public static final Pattern H5_PATTERN = Pattern.compile(
			"<H5[\\p{Space}]+([^>]*?)>", Pattern.CASE_INSENSITIVE);
	public static final Pattern H6_PATTERN = Pattern.compile(
			"<H6[\\p{Space}]+([^>]*?)>", Pattern.CASE_INSENSITIVE);

	public static final Pattern P_PATTERN = Pattern.compile(
			"<p[\\p{Space}]+([^>]*?)>", Pattern.CASE_INSENSITIVE);
	public static final Pattern DIV_PATTERN = Pattern.compile(
			"<div[\\p{Space}]+([^>]*?)>", Pattern.CASE_INSENSITIVE);
	public static final Pattern FONT_PATTERN = Pattern.compile(
			"<font[\\p{Space}]+([^>]*?)>", Pattern.CASE_INSENSITIVE);
	public static final Pattern SPAN_PATTERN = Pattern.compile(
			"<span[\\p{Space}]+([^>]*?)>", Pattern.CASE_INSENSITIVE);
	public static final Pattern A_PATTERN = Pattern.compile(
			"<a[\\p{Space}]+([^>]*?)>", Pattern.CASE_INSENSITIVE);
	public static final Pattern TABLE_PATTERN = Pattern.compile(
			"<table[\\p{Space}]+([^>]*?)>", Pattern.CASE_INSENSITIVE);
	public static final Pattern TR_PATTERN = Pattern.compile(
			"<tr[\\p{Space}]+([^>]*?)>", Pattern.CASE_INSENSITIVE);
	public static final Pattern TD_PATTERN = Pattern.compile(
			"<td[\\p{Space}]+([^>]*?)>", Pattern.CASE_INSENSITIVE);
	public static final Pattern STYLE_PATTERN = Pattern.compile(
			"<style[\\p{Space}]+([^>]*?)>", Pattern.CASE_INSENSITIVE);
	public static final Pattern BODY_PATTERN = Pattern.compile(
			"<body[\\p{Space}]+([^>]*?)>", Pattern.CASE_INSENSITIVE);

	public static final Pattern[] ELEMENTS_PATTERNS = { META_PATTERN,
			BR_PATTERN, LINK_PATTERN, P_PATTERN, DIV_PATTERN, FONT_PATTERN,
			SPAN_PATTERN, A_PATTERN, HR_PATTERN, TABLE_PATTERN, COL_PATTERN,
			TR_PATTERN, TD_PATTERN, STYLE_PATTERN, BODY_PATTERN, H1_PATTERN,
			H2_PATTERN, H3_PATTERN, H4_PATTERN, H5_PATTERN, H6_PATTERN,
			IMG_PATTERN };

	private static final Pattern[] ILLEGAL_PATTERNS = { META_PATTERN,
		BR_PATTERN, LINK_PATTERN, HR_PATTERN, COL_PATTERN, IMG_PATTERN };

	private static String TAG = "HtmlUtil";

	public static Set<String>[] replace(final String html, final String[] tag, final String[] attr, final String[] replacesStr, final String[] bysStr, final Set<String> ssException) {
		final TreeSet<String> set = new TreeSet<>();
		final TreeSet<String> setAll = new TreeSet<>();
		if (tag != null && attr != null
			&& tag.length > 0 && tag.length == attr.length) {
			for (int i = 0; i < tag.length; i++) {
				final String searchPatStr = "<" + tag[i] + "[^>]+?" + attr[i] + "\\s*=\\s*([\"'])([^\1]+?)\\1[^>]*>";
				//Log.d(TAG, "html " + html);
				//Log.d(TAG, "replace searchPatStr " + searchPatStr);
				final Pattern patTag = Pattern.compile(searchPatStr, Pattern.CASE_INSENSITIVE);
				final Matcher tagMatcher = patTag.matcher(html);
				String urlGroup;
				if (replacesStr != null && replacesStr.length > 0) {
					final Pattern[] replacesPat = new Pattern[replacesStr.length];
					for (int j = 0; j < replacesStr.length; j++) {
						replacesPat[j] = Pattern.compile(replacesStr[j], Pattern.CASE_INSENSITIVE);
					}
					while (tagMatcher.find()) {
						urlGroup = tagMatcher.group(2);
						//Log.d(TAG, "g1 " + group);
						int replaced = 0;
						for (int j = 0; j < replacesStr.length; j++) {
							if (replacesPat[j] != null) {
								final Matcher replaceMatcher = replacesPat[j].matcher(urlGroup);
								if (replaceMatcher.find()) {
									replaced++;
									urlGroup = replaceMatcher.replaceFirst(bysStr[j]);
								}
							}
						}
						if (replaced == replacesStr.length) {
							setAll.add(urlGroup);
							if (ssException == null 
								|| !ssException.contains(urlGroup)) {
								set.add(urlGroup);
							}
						}
					}
				} else {
					while (tagMatcher.find()) {
						urlGroup = tagMatcher.group(2);
						//Log.d(TAG, "g2 " + group);
						setAll.add(urlGroup);
						if (ssException == null 
							|| !ssException.contains(urlGroup)) {
							set.add(urlGroup);
						}
					}
				}
			}
		}
		Log.d(TAG, "new " + set.size() + ", duplicated " + (setAll.size()-set.size()));
		return new TreeSet[]{set, setAll};
	}
	
	public static Set<String>[] extractLink(final String html, final String[] tag, final String[] attr, final Set<String> ssException, final Set<String> set, final Set<String> setAll) {
		//final TreeSet<String> set = new TreeSet<>();
		//final TreeSet<String> setAll = new TreeSet<>();
		if (tag != null && attr != null
			&& tag.length > 0 && tag.length == attr.length) {
			for (int i = 0; i < tag.length; i++) {
				final String searchPatStr = "<" + tag[i] + "[^>]+?" + attr[i] + "\\s*=\\s*([\"'])([^\1]+?)\\1[^>]*>";
				//Log.d(TAG, "html " + html);
				//Log.d(TAG, "replace searchPatStr " + searchPatStr);
				final Pattern patTag = Pattern.compile(searchPatStr, Pattern.CASE_INSENSITIVE);
				final Matcher m = patTag.matcher(html);
				String group;
				int no = 0;
				int newVideo = 0;
				while (m.find()) {
					group = m.group(2);
					//Log.d(TAG, "g2 " + group);
					setAll.add(group);
					no++;
					if (ssException == null 
						|| !ssException.contains(group)) {
						set.add(group);
						newVideo++;
					}
				}
				Log.d(TAG, "extractLink " + no + ", dup " + (no-newVideo));
			}
		}
		return new Set[]{set, setAll};
	}
	
	public static String getTagValue(String tag, String html) {
		String st = "<" + tag + "[^>]*?>" + "([^<]*?)</" + tag + "\\s*>";
		ExceptionLogger.d(TAG, "getTagValue " + st);
		Pattern patTitle = Pattern.compile(st, Pattern.CASE_INSENSITIVE);
		Matcher m = patTitle.matcher(html);
		if (m.find()) {
			return m.group(1).replaceAll("\\s{2,}", " ");
		} else {
			return "";
		}
	}
	
	public static String readValue(String whole, String key) {
		Pattern pattern = Pattern.compile(key + "\\s*=\\s*([\"'])(.+?)\\1",
				Pattern.CASE_INSENSITIVE);
		Matcher mat = pattern.matcher(whole);
		if (mat.find()) {
			return mat.group(2);
		} else {
			pattern = Pattern.compile(key
					+ "\\s*=\\s*([^ \t\r\n\f\b\"'/>]+?)[ \t\r\n\f\b\"'/>]",
					Pattern.CASE_INSENSITIVE);
			mat = pattern.matcher(whole);
			if (mat.find()) {
				return mat.group(1);
			}
		}
		return "";
	}

	public static boolean isUTF8(String st) {
		int ch;
		for (int i = 0; i < HtmlUtil.VUTimesUniqueChars.length; i++) {
			if ((ch = st.indexOf(HtmlUtil.VUTimesUniqueChars[i])) >= 0) {
				ExceptionLogger.d("UTF8 char", HtmlUtil.VUTimesUniqueChars[i] 
						+ " at " + ch);
				return true;
			}
		}
		return false;
	}

	public static String fixEndTags(String str) {
		StringBuffer sb = new StringBuffer();
		// str = encodeForRegex(str);
		for (int i = 0; i < ILLEGAL_PATTERNS.length; i++) {
			Matcher m = ILLEGAL_PATTERNS[i].matcher(str);
			// Loop through and create a new String
			// with the replacements
			while (m.find()) {
				if (!m.group(0).endsWith("/>")) {
					m.appendReplacement(sb, m.group(0).replaceAll(">", "/>"));
				}
			}
			// Add the last segment of input to
			// the new String
			m.appendTail(sb);
			str = sb.toString();
			sb = new StringBuffer();
			// System.gc();
		}
		// str = decodeForRegex(str);
		return str;
	}

	private static final Pattern CHAR_CODE_PATTERN = Pattern.compile("&#(\\d{2,6});");

	public static String fixCharCode(String wholeFile) {
		Matcher mat = CHAR_CODE_PATTERN.matcher(wholeFile);
		StringBuffer sb = new StringBuffer();
		while (mat.find()) {
			// System.gc();
			mat.appendReplacement(sb,
					((char) Integer.parseInt(mat.group(1)) + ""));
		}
		mat.appendTail(sb);
		return sb.toString();
	}

	public static final Pattern KEY_VALUE_WITH_APOS_PATTERN = Pattern.compile(
			"((\\w+?)=['\"][^'\"]+?)['\"]", Pattern.CASE_INSENSITIVE);

	static Pattern CHARSET_PATTERN = Pattern.compile("(charset)=([^'\"/> \\t\\r\\n\\f]+)");

	public static String fixMetaTagsUTF8(String str) {
		// str = Util.encodeForRegex(str);
		StringBuffer sb = new StringBuffer();
		Matcher m = HtmlUtil.META_PATTERN.matcher(str);
		// Loop through and create a new String
		// with the replacements
		boolean stop = false;
		while (m.find()) {
			String tag = m.group(0);
			ExceptionLogger.d("tag1: ", tag);
			// System.out.println("tag: " + tag);
			// Matcher mat2 = KEY_VALUE_WITH_APOS_PATTERN.matcher(tag);
			Matcher mat2 = CHARSET_PATTERN.matcher(tag);
			StringBuffer sb2 = new StringBuffer();
			// System.gc();
			while (mat2.find()) {
				ExceptionLogger.d("mat2.group()1: ", mat2.group());
				// System.out.println("mat2.group(1): " + mat2.group(1));
				// System.out.println("mat2.group(2): " + mat2.group(2));
				// System.out.println("mat2.groupCount(): " +
				// mat2.groupCount());
				if ("charset".equalsIgnoreCase(mat2.group(1))) {
					// System.out.println("fixMetaTagsUTF8: " + mat2.group());
					mat2.appendReplacement(sb2, "charset=UTF-8");
					stop = true;
					break;
				}
			}
			mat2.appendTail(sb2);
			m.appendReplacement(sb, sb2.toString());
			if (stop) {
				break;
			}
			sb2 = new StringBuffer();
		}
		m.appendTail(sb);
		str = sb.toString();
		// str = Util.decodeForRegex(str);
		return str;
	}

	// private static final String DOCTYPE_PATTERN = "<!DOCTYPE .+?>";

	// cần uri để download dtd
	public static final String DTD = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"file:data/xhtml1-transitional.dtd\">\r\n";

	// public static final String DTD =
	// "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"data/xhtml1-transitional.dtd\">\r\n";

	public static String initOriHtml(File inFile, File newFile)
			throws FileNotFoundException, IOException {
		String wholeFile = FileUtil.readFileByMetaTag(inFile)[0];

		// wholeFile = fixAttrValueApos(wholeFile);
		// wholeFile = fixMetaTagsUTF8(wholeFile);
		// wholeFile = fixEndTags(wholeFile);
		wholeFile = fixCharCode(wholeFile);
		// wholeFile = removeComment(wholeFile);
		wholeFile = Util.replaceAll(wholeFile, ENTITY_NAME, ENTITY_CODE);
		// wholeFile = HTMLToText.stringToUTF8(wholeFile);

		// if (wholeFile.indexOf("<!DOCTYPE") < 0) {
		// wholeFile = new StringBuilder(DTD).append(wholeFile).toString();
		// } else {
		// wholeFile = wholeFile.replaceFirst(DOCTYPE_PATTERN, DTD);
		// }
		FileUtil.writeFileAsCharset(newFile, wholeFile, UTF8);

		ExceptionLogger.d("Wrote to new temp file: ", newFile.getAbsolutePath());
		return wholeFile;
	}

	public static final String[] ENTITY_CODE = new String[] { "" + (char) 161,
			"" + (char) 162, "" + (char) 163, "" + (char) 164, "" + (char) 165,
			"" + (char) 166, "" + (char) 167, "" + (char) 168, "" + (char) 169,
			"" + (char) 170, "" + (char) 171, "" + (char) 172, "" + (char) 173,
			"" + (char) 174, "" + (char) 175, "" + (char) 176, "" + (char) 177,
			"" + (char) 178, "" + (char) 179, "" + (char) 180, "" + (char) 181,
			"" + (char) 182, "" + (char) 183, "" + (char) 184, "" + (char) 185,
			"" + (char) 186, "" + (char) 187, "" + (char) 188, "" + (char) 189,
			"" + (char) 190, "" + (char) 191, "" + (char) 192, "" + (char) 193,
			"" + (char) 194, "" + (char) 195, "" + (char) 196, "" + (char) 197,
			"" + (char) 198, "" + (char) 199, "" + (char) 200, "" + (char) 201,
			"" + (char) 202, "" + (char) 203, "" + (char) 204, "" + (char) 205,
			"" + (char) 206, "" + (char) 207, "" + (char) 208, "" + (char) 209,
			"" + (char) 210, "" + (char) 211, "" + (char) 212, "" + (char) 213,
			"" + (char) 214, "" + (char) 215, "" + (char) 216, "" + (char) 217,
			"" + (char) 218, "" + (char) 219, "" + (char) 220, "" + (char) 221,
			"" + (char) 222, "" + (char) 223, "" + (char) 224, "" + (char) 225,
			"" + (char) 226, "" + (char) 227, "" + (char) 228, "" + (char) 229,
			"" + (char) 230, "" + (char) 231, "" + (char) 232, "" + (char) 233,
			"" + (char) 234, "" + (char) 235, "" + (char) 236, "" + (char) 237,
			"" + (char) 238, "" + (char) 239, "" + (char) 240, "" + (char) 241,
			"" + (char) 242, "" + (char) 243, "" + (char) 244, "" + (char) 245,
			"" + (char) 246, "" + (char) 247, "" + (char) 248, "" + (char) 249,
			"" + (char) 250, "" + (char) 251, "" + (char) 252, "" + (char) 253,
			"" + (char) 254, "" + (char) 255,

			// "" + (char) 338, "" + (char) 339,
			//
			// "" + (char) 352, "" + (char) 353, "" + (char) 376,
			//
			// "" + (char) 710, "" + (char) 732, "" + (char) 8194,
			// "" + (char) 8195, "" + (char) 8201, "" + (char) 8204,
			// "" + (char) 8205, "" + (char) 8206, "" + (char) 8207,
			//
			// "" + (char) 8212,
			//
			// "" + (char) 8218, "" + (char) 8220, "" + (char) 8221,
			// "" + (char) 8222, "" + (char) 8224, "" + (char) 8225,
			// "" + (char) 8240, "" + (char) 8249, "" + (char) 8250,
			// "" + (char) 8364,
			//
			// "" + (char) 402, "" + (char) 8226, "" + (char) 8230,
			// "" + (char) 8242, "" + (char) 8243, "" + (char) 8254,
			// "" + (char) 8260, "" + (char) 8465, "" + (char) 8472,
			// "" + (char) 8476, "" + (char) 8482, "" + (char) 8501,
			// "" + (char) 8592, "" + (char) 8593, "" + (char) 8594,
			// "" + (char) 8595, "" + (char) 8596, "" + (char) 8629,
			// "" + (char) 8656, "" + (char) 8657, "" + (char) 8658,
			// "" + (char) 8659, "" + (char) 8660, "" + (char) 8704,
			// "" + (char) 8706, "" + (char) 8707, "" + (char) 8709,
			// "" + (char) 8711, "" + (char) 8712, "" + (char) 8713,
			// "" + (char) 8715, "" + (char) 8719, "" + (char) 8721,
			// "" + (char) 8722, "" + (char) 8727, "" + (char) 8730,
			// "" + (char) 8733, "" + (char) 8734, "" + (char) 8736,
			// "" + (char) 8743, "" + (char) 8744, "" + (char) 8745,
			// "" + (char) 8746, "" + (char) 8747, "" + (char) 8756,
			// "" + (char) 8764, "" + (char) 8773, "" + (char) 8776,
			// "" + (char) 8800, "" + (char) 8801, "" + (char) 8804,
			// "" + (char) 8805, "" + (char) 8834, "" + (char) 8835,
			// "" + (char) 8836, "" + (char) 8838, "" + (char) 8839,
			// "" + (char) 8853, "" + (char) 8855, "" + (char) 8869,
			// "" + (char) 8901, "" + (char) 8968, "" + (char) 8969,
			// "" + (char) 8970, "" + (char) 8971, "" + (char) 9001,
			// "" + (char) 9002, "" + (char) 913, "" + (char) 914,
			// "" + (char) 915, "" + (char) 916, "" + (char) 917, "" + (char)
			// 918,
			// "" + (char) 919, "" + (char) 920, "" + (char) 921, "" + (char)
			// 922,
			// "" + (char) 923, "" + (char) 924, "" + (char) 925, "" + (char)
			// 926,
			// "" + (char) 927, "" + (char) 928, "" + (char) 929, "" + (char)
			// 931,
			// "" + (char) 932, "" + (char) 933, "" + (char) 934, "" + (char)
			// 935,
			// "" + (char) 936, "" + (char) 937, "" + (char) 945, "" + (char)
			// 946,
			// "" + (char) 947, "" + (char) 948, "" + (char) 949, "" + (char)
			// 950,
			// "" + (char) 951, "" + (char) 952, "" + (char) 953, "" + (char)
			// 954,
			// "" + (char) 955, "" + (char) 956, "" + (char) 957, "" + (char)
			// 958,
			// "" + (char) 959, "" + (char) 960, "" + (char) 961, "" + (char)
			// 962,
			// "" + (char) 963, "" + (char) 964, "" + (char) 965, "" + (char)
			// 966,
			// "" + (char) 967, "" + (char) 9674, "" + (char) 968,
			// "" + (char) 969, "" + (char) 977, "" + (char) 978, "" + (char)
			// 982,
			// "" + (char) 9824, "" + (char) 9827, "" + (char) 9829,
			// "" + (char) 9830,

			"’", "‘", "–", " ", "\"", "\'", "<", ">", "&", "“", "”", " ", "…", };

	public static final String[] ENTITY_NAME = new String[] { "&iexcl;",
			"&cent;", "&pound;", "&curren;", "&yen;", "&brvbar;", "&sect;",
			"&uml;", "&copy;", "&ordf;", "&laquo;", "&not;", "&shy;", "&reg;",
			"&macr;", "&deg;", "&plusmn;", "&sup2;", "&sup3;", "&acute;",
			"&micro;", "&para;", "&middot;", "&cedil;", "&sup1;", "&ordm;",
			"&raquo;", "&frac14;", "&frac12;", "&frac34;", "&iquest;",
			"&Agrave;", "&Aacute;", "&Acirc;", "&Atilde;", "&Auml;", "&Aring;",
			"&AElig;", "&Ccedil;", "&Egrave;", "&Eacute;", "&Ecirc;", "&Euml;",
			"&Igrave;", "&Iacute;", "&Icirc;", "&Iuml;", "&ETH;", "&Ntilde;",
			"&Ograve;", "&Oacute;", "&Ocirc;",
			"&Otilde;",
			"&Ouml;",
			"&times;",
			"&Oslash;",
			"&Ugrave;",
			"&Uacute;",
			"&Ucirc;",
			"&Uuml;",
			"&Yacute;",
			"&THORN;",
			"&szlig;",
			"&agrave;",
			"&aacute;",
			"&acirc;",
			"&atilde;",
			"&auml;",
			"&aring;",
			"&aelig;",
			"&ccedil;",
			"&egrave;",
			"&eacute;",
			"&ecirc;",
			"&euml;",
			"&igrave;",
			"&iacute;",
			"&icirc;",
			"&iuml;",
			"&eth;",
			"&ntilde;",
			"&ograve;",
			"&oacute;",
			"&ocirc;",
			"&otilde;",
			"&ouml;",
			"&divide;",
			"&oslash;",
			"&ugrave;",
			"&uacute;",
			"&ucirc;",
			"&uuml;",
			"&yacute;",
			"&thorn;",
			"&yuml;",

			// "&OElig;", "&oelig;",
			//
			// "&Scaron;", "&scaron;", "&Yuml;",
			//
			// "&circ;", "&tilde;", "&ensp;", "&emsp;", "&thinsp;", "&zwnj;",
			// "&zwj;", "&lrm;", "&rlm;",
			//
			// "&mdash;",
			//
			// "&sbquo;", "&ldquo;", "&rdquo;", "&bdquo;", "&dagger;",
			// "&Dagger;",
			// "&permil;", "&lsaquo;", "&rsaquo ;", "&euro;",
			//
			// "&fnof;", "&bull;", "&hellip;", "&prime;", "&Prime;", "&oline;",
			// "&frasl;", "&image;", "&weierp;", "&real;", "&trade;",
			// "&alefsym;",
			// "&larr;", "&uarr;", "&rarr;", "&darr;", "&harr;", "&crarr;",
			// "&lArr;", "&uArr;", "&rArr;", "&dArr;", "&hArr;", "&forall;",
			// "&part;", "&exist;", "&empty;", "&nabla;", "&isin;", "&notin;",
			// "&ni;", "&prod;", "&sum;", "&minus;", "&lowast;", "&radic;",
			// "&prop;", "&infin;", "&ang;", "&and;", "&or;", "&cap;", "&cup;",
			// "&int;", "&there4;", "&sim;", "&cong;", "&asymp;", "&ne;",
			// "&equiv;", "&le;", "&ge;", "&sub;", "&sup;", "&nsub;", "&sube;",
			// "&supe;", "&oplus;", "&otimes;", "&perp;", "&sdot;", "&lceil;",
			// "&rceil;", "&lfloor;", "&rfloor;", "&lang;", "&rang;", "&Alpha;",
			// "&Beta;", "&Gamma;", "&Delta;", "&Epsilon;", "&Zeta;", "&Eta;",
			// "&Theta;", "&Iota;", "&Kappa;", "&Lambda;", "&Mu;", "&Nu;",
			// "&Xi;",
			// "&Omicron;", "&Pi;", "&Rho;", "&Sigma;", "&Tau;", "&Upsilon;",
			// "&Phi;", "&Chi;", "&Psi;", "&Omega;", "&alpha;", "&beta;",
			// "&gamma;", "&delta;", "&epsilon;", "&zeta;", "&eta;", "&theta;",
			// "&iota;", "&kappa;", "&lambda;", "&mu;", "&nu;", "&xi;",
			// "&omicron;", "&pi;", "&rho;", "&sigmaf;", "&sigma;", "&tau;",
			// "&upsilon;", "&phi;", "&chi;", "&loz;", "&psi;", "&omega;",
			// "&thetasym;", "&upsih;", "&piv;", "&spades;", "&clubs;",
			// "&hearts;", "&diams;",

			"&rsquo;", "&lsquo;", "&ndash;", "&nbsp;", "&quot;", "&apos;",
			"&lt;", "&gt;", "&amp;", "&ldquo;", "&rdquo;", "", "&hellip;", };

	public static final Pattern KEY_VALUE_PATTERN = Pattern.compile(
			"(\\w+?=[^\\p{Space}\"']+?)([\\p{Space}/>]+?)",
			Pattern.CASE_INSENSITIVE);

	public static String fixAttrValueApos(String str) {
		// str = encodeForRegex(str);
		// LOGGER.info("str: " + str);
		StringBuffer sb = new StringBuffer();
		for (Pattern p : ELEMENTS_PATTERNS) {
			Matcher m = p.matcher(str);
			// Loop through and create a new String
			// with the replacements
			while (m.find()) {
				String tag = m.group(0);
				ExceptionLogger.d("tag2: ", tag);
				Matcher mat2 = KEY_VALUE_PATTERN.matcher(tag);
				StringBuffer sb2 = new StringBuffer();
				while (mat2.find()) {
					ExceptionLogger.d("mat2.group()2: ", mat2.group());
					mat2.appendReplacement(
							sb2,
							mat2.group(1).replaceFirst("=", "='") + "'"
									+ mat2.group(2));
				}
				mat2.appendTail(sb2);
				m.appendReplacement(sb, sb2.toString());
				sb2 = new StringBuffer();
			}
			// Add the last segment of input to
			// the new String
			m.appendTail(sb);
			str = sb.toString();
			sb = new StringBuffer();
		}
		// str = decodeForRegex(str);
		return str;
	}

	public static final String[] VU_Times = new String[] { 
		"ā", "ī", "ū", "ṅ", "ṭ", "ñ", "ḍ", "ṇ", "ḷ", "ṃ", "ṁ", 
		"Ā", "Ī", "Ū", "Ṅ", "Ṭ", "Ñ", "Ḍ", "Ṇ", "Ḷ", "Ṃ", "Ṁ", 
		"ō", "ṣ", "ē",
		" ", "\"", "&", "'", "(", ")", "+", ",", "-", ".", "/", "0", "1", "2", 
		"3", "4", "5", "6", "7", "8", "9", ":", ";", "=", "?", "A", "B", "C", 
		"D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "R", 
		"S", "T", "U", "V", "W", "X", "Y", "[", "]", "_", "a", "b", "c", "d", 
		"e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", 
		"s", "t", "u", "v", "w", "x", "y", "z", "{", "}", "“", "”", "‘", "’", "ṭ" 
	};

	public static final String[] Unknown1 = new String[] { 
		"á", "ì", "ú", "ò", "þ", "ñ", "ð", "ó", "¿", "í", "", 
		"Á", "Ì", "Ú", "Ò", "Þ", "Ñ", "Ð", "Ó", "¿", "Í", "", "õ", "å", "÷"};

//	public static final String[] Unknown2 = new String[] { 
//			"È", "Ê", "|", "~", "Ô", "Ò", "", "Ó", "Ä", "Ñ", "", 
//			"Œ", "", "", "", "", "", "", "", "", "", "", "", "", ""};

	public static final String[] Times_CSX_plus = new String[] { 
		"à", "ã", "å", "ï", "ñ", "¤", "ó", "õ", "ë", "ü", "§", 
		"â", "ä", "æ", "ð", "ò", "¥", "ô", "ö", "ì", "ý", };

	public static final String[] Times_CSX_plus_1 = new String[] { 
		"à", "ã", "å", "ï", "ñ", "¤", "ó", "õ", "ë", "ü", "§", 
		"â", "ä", "æ", "ð", "ò", "¥", "ô", "ö", "ì", "ý", "ą", };

	public static final String[] VriRomanPali = new String[] { 
		"±", "²", "³", "ª", "µ", "ñ", "¹", "º", "¼", "½", "", 
		"¾", "¿", "Ð", "©", "Ý", "Ñ", "Þ", "ð", "ý", "þ", "", };

	public static final String[] VZTime = new String[] { 
		"È", "Ê", "|", "~", "Ô", "Ò", "É", "Ó", "Ä", "", "Ñ", 
		"", "¢", "", "^", "®", "©", "", "ª", "£", "", "¥", };

	public static final String[] LeedsBitPaliTranslit = new String[] { 
		"Œ", "´", "è", "º", "Ê", "–", "¶", "ö", "Â", "µ", "×", 
		"î", "ç", "ò", "Ü", "é", "„", "ß", "ï", "â", "È", "Ṁ", };

	public static final String[] Foreign1 = new String[] { 
		"a", "i", "u", "v", "t", "b", "d", "n", "l", "j", "m", 
		"A", "I", "U", "V", "T", "B", "D", "N", "L", "J", "M", };

	public static final String[] Foreigs1 = new String[] { 
		"a", "i", "u", "v", "t", "b", "d", "n", "l", "j", "m", 
		"A", "I", "U", "V", "T", "B", "D", "N", "L", "J", "M", };

	public static final String[] Norman = new String[] { 
		"", "", "", "", "", "", "", "", "", "", "", 
		"", "", "", "", "", "", "", "", "", "", "", 
		"", "", "", "", "", "", "", "", "", "", "", "", "", "", 
		"", "", "", "", "", "", "", "", "", "", "", "", "", "", 
		"", "", "", "", "", "", "", "", "", "", "", "", "", "", 
		"", "", "", "", "", "", "", "", "", "", "", "", "", "", 
		"", "", "", "", "", "", "", "", "", "", "", "", "", "", 
		"", "", "", "", "", "", "", "", "", "", "", "", "", "", "" 
	};

	public static final String[] VUTimesUniqueChars = new String[] { "ā", "ī",
			"ū", "ṅ", "ṭ", "ḍ", "ṇ", "ḷ", "ṃ", "Ā", "Ī", "Ū", "Ṅ", "Ṭ", "Ḍ",
			"Ṇ", "Ḷ", "Ṃ", "đ", "ă", "ắ", "ằ", "ẳ", "ẵ", "ặ", "Ă", "Ắ",
			"Ằ", "Ẳ", "Ẵ", "Ặ", "ấ", "ầ", "ẩ", "ẫ", "ậ", "Ấ", "Ầ", "Ẩ", "Ẫ",
			"Ậ", "ế", "ề", "ể", "ễ", "ệ", "ơ", "ớ", "ờ", "ở", "ỡ", "ợ", "Ơ",
			"Ớ", "Ờ", "Ở", "Ỡ", "Ợ", "ư", "ứ", "ừ", "ử", "ữ", "ự", "Ư", "Ứ",
			"Ừ", "Ử", "Ữ", "Ự", "ổ", "ỗ", "ồ", "ố", "ộ", "Ổ", "Ỗ", "Ồ", "Ố",
			"Ộ" };

	public static final String[] Times_CSXPlu1UniqueChars = new String[] { "ą",
	// "å", "ï", "¤", "ë", "ü", "§", "ä", "æ",
	// "ð", "¥", "ö",
	};

	public static final String[] Times_CSXPlusUniqueChars = new String[] { // "å",
			// "ï", 
			"¤", 
			// "ë", 
			// "ü", 
			// "§", 
			// "ä", 
			"æ", 
			// "ð", 
			// "¥", 
			// "ö", 
			};

	public static final String[] VriRomanPaliUniqueChars = new String[] { "±",
			// "²", 
			"³", "¹", "¼", "½", "¾", 
			// "¿", 
			};

	public static final String[] VZTimeUniqueChars = new String[] { //"|", 
			"Ä", "¢", "®", "£", };

	public static final String[] LeedsBitPaliTranslitUniqueChars = new String[] {
			// "Œ", 
			"´", "¶", "×", "î", "ç", "„", "ß", };

	public static final String[] NormanUniqueChars = new String[] { "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", };

	public static final String[] Unknown1UniqueChars = new String[] {
			"÷", //"Þ","¿"
	};
	
	//public static final String[] Unknown2UniqueChars = new String[] { "|", 
	//	"Ä", };
	
	public static final Map<String, String[]> FONT_NAMES_PROP = new HashMap<String, String[]>();

	public static final List<String> NOT_UTF8_CHARS = new LinkedList<String>();

	private static final String DOCTYPE_PATTERN = "<!DOCTYPE .+?>";
	private static final String DOCTYPE_WITH_DTD = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"data/xhtml1-transitional.dtd\">\r\n";

	public static String initStrictHtml(File oldFile, File newFile)
	throws FileNotFoundException, IOException {
		String wholeFile = FileUtil.readFileByMetaTag(oldFile)[0];
		// String wholeFile =
		// FileUtil.readFileAsCharset(oldFile.getAbsolutePath(),
		// currentCharset);
		wholeFile = HtmlUtil.fixAttrValueApos(wholeFile);
		wholeFile = HtmlUtil.fixMetaTagsUTF8(wholeFile);
		wholeFile = HtmlUtil.fixEndTags(wholeFile);
		wholeFile = HtmlUtil.fixCharCode(wholeFile);
		wholeFile = Util.replaceAll(wholeFile, HtmlUtil.ENTITY_NAME,
										   HtmlUtil.ENTITY_CODE);
		wholeFile = wholeFile.replaceAll("[\\r\\n\\f]+", " ");
		// wholeFile = wholeFile.replaceAll("[\\-]+", "-");
		// wholeFile = wholeFile.replaceAll("<!-", "<!--");
		// wholeFile = wholeFile.replaceAll("->", "-->");
		// wholeFile = wholeFile.replaceAll("&ntilde;", "ñ");
		// wholeFile = wholeFile.replaceAll("&Ntilde;", "Ñ");
		// wholeFile = wholeFile.replaceAll("&rsquo;", "’");
		// wholeFile = wholeFile.replaceAll("&lsquo;", "‘");
		// wholeFile = wholeFile.replaceAll("&ndash;", "–");
		if (wholeFile.indexOf("<!DOCTYPE") < 0) {
			wholeFile = new StringBuilder(DOCTYPE_WITH_DTD).append(wholeFile).toString();
		} else {
			wholeFile = wholeFile.replaceFirst(DOCTYPE_PATTERN, DOCTYPE_WITH_DTD);
		}
		FileUtil.writeFileAsCharset(newFile, wholeFile, "UTF-8");

		return wholeFile;
	}

	// HTMLToText
	// "script", "style"
	private static Pattern SCRIPT_STYLE_REMOVE = Pattern.compile(
		"<(script|style)[^>]*?>([\u0001-\uFFFF]*?)</\\1\\s*>",
		Pattern.CASE_INSENSITIVE);

	private static Pattern CR_TAGS = Pattern.compile(
		"</?(blockquote|br|cite|h1|h2|h3|h4|h5|h6|hr|li|p|pre|tr|title|nobr|c1|c2|c3|c4|c5|c6"
		+"|o:p|last-name|book-title|lang|subtitle|empty-line)[^>]*/?>",
		Pattern.CASE_INSENSITIVE);
//		"</?(blockquote|br|li|p|tr|title|h1|h2|h3|h4|h5|h6|hr
//		|o:p|last-name|book-title|lang|subtitle|empty-line)[^>]*/?>",

	// "html", "meta", "head", "table", "col", "colgroup", "tr", "thead",
	// "tbody", "tfoot", "dl", "ol", "ul", "menu"
	// "body", "address", "center", "del", "div",
	// "td", "th", "noscript", "pre",
	// "dt", "dd"
	// "a",
	// "span", "sub", "sup", "strong", "b", "i", "em", "u",
	// "s", "big", "small", "strike"
	// "img", "link" <hgroup class="longsutta"> </q>
	private static Pattern REMOVE_TAGS = Pattern.compile(
		"</?(!DOCTYPE|html|head|table|base|meta|link|noscript"
		+"|frameset|frame|iframe|noframes|nav|footer"
		+"|body|center|div|ul|ol|menu|dir|dl|dt|dd|address|ins|del"
		+"|a|span|bdo|em|strong|dfn|code|samp|kbd|var|abbr|acronym"
		+"|q|sub|sup|tt|i|b|big|small|u|s|strike|basefont|font"
		+"|object|param|applet|img|map|area|form|label|input|select"
		+"|option|textarea|optgroup|fieldset|legend|button|isindex"
		+"|caption|thead|tfoot|tbody|colgroup|col|th|td|hgroup"
		+"|\\?xml|container|rootfiles|rootfile"
		+"|FictionBook|description|title-info|author)[^>]*?/?>",
		Pattern.CASE_INSENSITIVE);
	// <![IF !supportFootnotes]>
	// <![ENDIF]>
	private static Pattern REMOVE_CR_PATTERN = Pattern.compile("[\\r\\n\\t\\f ]+");
	private static Pattern REMOVE_COMMENT_PATTERN = Pattern
	.compile("<!--[\u0000-\uFFFF]*?-->");
	private static Pattern REMOVE_STARTNOTE = Pattern.compile(
		"<![-]*\\[IF [^]]*]>", Pattern.CASE_INSENSITIVE);
	private static Pattern REMOVE_ENDNOTE = Pattern.compile("<!\\[ENDIF][-]*>",
															Pattern.CASE_INSENSITIVE);

	public static String htmlToText(File inFile) throws IOException {
		String wholeFile = FileUtil.readFileByMetaTag(inFile)[0];
		wholeFile = htmlToText(wholeFile);
		return wholeFile;
	}

	public static String htmlToText(String wholeFile) {
		wholeFile = HtmlUtil.removeTags(wholeFile);
		wholeFile = HtmlUtil.fixCharCode(wholeFile);
		wholeFile = Util.replaceAll(wholeFile, HtmlUtil.ENTITY_NAME,
									HtmlUtil.ENTITY_CODE);
		return wholeFile;
	}
	
	public static final String SPECIAL_CHAR_PATTERNSTR = "([{}^$.\\[\\]|*+?()\\\\])";
	public static final String replaceRegexAll(String fileContent, String from, String to, boolean isRegex, boolean caseSensitive) {
		if (!isRegex) {
			//ExceptionLogger.d(from, to);
			from = from.replaceAll(SPECIAL_CHAR_PATTERNSTR, "\\\\$1");
			to = to.replaceAll(SPECIAL_CHAR_PATTERNSTR, "\\\\$1");
			//ExceptionLogger.d(from, to);
		}
		//System.out.println(fileContent);
		Pattern p = null;
		if (!caseSensitive) {
			p = Pattern.compile(from, Pattern.CASE_INSENSITIVE);
			//fileContent = fileContent.replaceAll("(?i)"+from, to);
		} else {
			p = Pattern.compile(from, Pattern.UNICODE_CASE);
			//fileContent = fileContent.replaceAll(from, to);
		}
		fileContent = p.matcher(fileContent).replaceAll(to);
		//System.out.println(fileContent);
		return fileContent;
	}

	public static String removeTags(String wholeFile) {

		wholeFile = REMOVE_CR_PATTERN.matcher(wholeFile).replaceAll(" ");

		wholeFile = REMOVE_COMMENT_PATTERN.matcher(wholeFile).replaceAll("");

		wholeFile = SCRIPT_STYLE_REMOVE.matcher(wholeFile).replaceAll("");

		wholeFile = CR_TAGS.matcher(wholeFile).replaceAll("\r\n");

		wholeFile = REMOVE_TAGS.matcher(wholeFile).replaceAll("");

		wholeFile = REMOVE_STARTNOTE.matcher(wholeFile).replaceAll("");

		wholeFile = REMOVE_ENDNOTE.matcher(wholeFile).replaceAll("");

		return wholeFile;
	}
	
	/**
	 * Xóa nội dung của String defStr theo Pattern pat
	 * @param defStr
	 * @param pat
	 * @return
	 */
	public static StringBuffer removePatternContents(String defStr, Pattern pat) {
		Matcher mat = pat.matcher(defStr);
		StringBuffer sb = new StringBuffer();
		while (mat.find()) {
			mat.appendReplacement(sb, " ");
		}
		mat.appendTail(sb);
		return sb;
	}
}


