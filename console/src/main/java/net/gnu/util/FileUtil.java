package net.gnu.util;

//import android.util.Log;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.security.MessageDigest;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
//import org.mozilla.universalchardet.*;
import java.nio.channels.*;
import java.nio.*;
import java.net.*;

public class FileUtil {
	
	private static final String TAG = "FileUtil";
	
	private static final String ISO_8859_1 = "ISO-8859-1";
	private static final Pattern encodingCss = Pattern.compile("@charset\\s+\"([^\"]+)\";");
	public static final Pattern ILLEGAL_FILE_CHARS = Pattern.compile("[^\n]*?[?/\\:*|\"<>#+%][^\n]*?");
	public static void close(final Closeable... closable) {
		if (closable != null && closable.length > 0) {
			for (Closeable c : closable) {
				try {
					if (c != null) {
						c.close();
					}
				} catch (IOException e) {
					ExceptionLogger.e(TAG, e.getMessage());
				}
			}
		}
	}

	public static void flushClose(final OutputStream... closable) {
		if (closable != null && closable.length > 0) {
			for (OutputStream c : closable) {
				if (c != null) {
					try {
						c.flush();
					} catch (IOException e) {
						ExceptionLogger.e(TAG, e.getMessage(), e);
					}
					try {
						c.close();
					} catch (IOException e) {
						ExceptionLogger.e(TAG, e.getMessage(), e);
					}
				}
			}
		}
	}
	
	public static byte[] is2Barr(final InputStream is, final boolean autoClose) throws IOException {
		ExceptionLogger.d(TAG, "is2Barr is " + is);
		if (is == null) {
			return new byte[0];
		}
		int count = 0;
		int len = 65536;
		final byte[] buffer = new byte[len];
		final BufferedInputStream bis = new BufferedInputStream(is);
		final ByteArrayOutputStream bb = new ByteArrayOutputStream(65536);
		while ((count = bis.read(buffer, 0, len)) > 0) {
			bb.write(buffer, 0, count);
		}
		if (autoClose) {
			FileUtil.close(bis, is);
		} 

		return bb.toByteArray();
	}
	
	
	public static byte[] readFileToMemory(final File file) throws IOException {
		final long length = file.length();
		if (length < Integer.MAX_VALUE) {
			final FileInputStream fis = new FileInputStream(file);
			final BufferedInputStream bis = new BufferedInputStream(fis);
			final int len = (int) length;
			final byte[] data = new byte[len];
			int start = 0;
			int read = 0;
			try {
				while ((read = bis.read(data, start, len - start)) > 0) {
					start += read;
				}
			} finally {
				close(bis, fis);
			}
			return data;
		} else {
			throw new IOException("File size " + Util.nf.format(length) + " exceed " + Integer.MAX_VALUE);
		}
	}

	public static String[] readFileByMetaTag(File file)
	throws FileNotFoundException, IOException {
		final byte[] byteArr = FileUtil.readFileToMemory(file);
//		String encoding = UniversalDetector.detectCharset(new ByteArrayInputStream(byteArr));
//		encoding = encoding == null ? "utf-8" : encoding;
		final String content = new String(byteArr, "utf-8");
		final String charsetName = HtmlUtil.readValue(content, "charset");
		if (charsetName.length() > 0) {
			ExceptionLogger.d(TAG, file.getAbsolutePath() + " charset: " + charsetName);
			try {
				return new String[]{new String(byteArr, charsetName), charsetName};
			} catch (UnsupportedEncodingException e) {
				return new String[]{content, "utf-8"};
			}
		} else {
			return new String[]{content, "utf-8"};
		}
	}

	public static String[] readCss(File file)
	throws FileNotFoundException, IOException {
		final byte[] byteArr = FileUtil.readFileToMemory(file);
//		String encoding = UniversalDetector.detectCharset(new ByteArrayInputStream(byteArr));
//		encoding = encoding == null ? "utf-8" : encoding;
		final String content = new String(byteArr, "utf-8");
		final Matcher matcher = encodingCss.matcher(content);
		String charsetName = "";
		if (matcher.find()) {
			charsetName = matcher.group(1);
		}
		if (charsetName.length() > 0) {
			ExceptionLogger.d(TAG, file.getAbsolutePath() + " charset: " + charsetName);
			try {
				return new String[]{new String(byteArr, charsetName), charsetName};
			} catch (UnsupportedEncodingException e) {
				return new String[]{content, "utf-8"};
			}
		} else {
			return new String[]{content, "utf-8"};
		}
	}

//	public static String readFileWithCheckEncode(File filePath)
//	throws FileNotFoundException, IOException,
//	UnsupportedEncodingException {
//		final byte[] byteArr = FileUtil.readFileToMemory(filePath);
//		return readFileWithCheckEncode(filePath, byteArr);
//	}
//
//	public static String readFileWithCheckEncode(File f, byte[] byteArr) throws IOException {
//		final String encoding = UniversalDetector.detectCharset(f);
//		ExceptionLogger.d(TAG, f.getAbsolutePath() + " detectCharset: " + encoding);
//		return new String(byteArr, encoding);
//	}
	
	public static List<File> getFiles(final File f, boolean includeDir, final Pattern includePat, final Pattern excludePat) {
		ExceptionLogger.d(TAG, "getFiles " + f.getAbsolutePath() + ", includePat " + includePat + ", excludePat " + excludePat);
		final List<File> fList = new LinkedList<File>();
		String name;
		if (f != null) {
			final LinkedList<File> folderQueue = new LinkedList<File>();
			name = f.getName();
			if (f.isDirectory()) {
				folderQueue.push(f);
			} else if ((includePat == null && excludePat == null)
					   || (includePat != null && excludePat != null
					   && includePat.matcher(name).matches() && !excludePat.matcher(name).matches())
					   || (includePat != null && includePat.matcher(name).matches())
					   || (excludePat != null && !excludePat.matcher(name).matches())) {
				fList.add(f);
				//ExceptionLogger.d(TAG, "getFiles.add " + f.getAbsolutePath());
			}
			File fi = null;
			File[] fs;
			while (folderQueue.size() > 0) {
				fi = folderQueue.removeFirst();
				fs = fi.listFiles();
				if (fs != null) {
					for (File f2 : fs) {
						if (f2.isDirectory()) {
							folderQueue.push(f2);
							if (includeDir) {
								fList.add(f2);
							}
						} else {
							name = f2.getName();
							if ((includePat == null && excludePat == null)
								|| (includePat != null && excludePat != null
								&& includePat.matcher(name).matches() && !excludePat.matcher(name).matches())
								|| (includePat != null && includePat.matcher(name).matches())
								|| (excludePat != null && !excludePat.matcher(name).matches())) {
								fList.add(f2);
								//ExceptionLogger.d(TAG, "getFiles.add2 " + f2.getAbsolutePath());
							}
						}
					}
				}
			}
		}
		return fList;
	}

	public static String getPathFromUrl(String url, final boolean includeQuestion) {
		if (includeQuestion) {
			return url.substring(url.indexOf("//") + 1).replaceAll("\\?", "@");
		} else {
			final int indexOfQuestion = url.indexOf("?");
			final int indexOfSharp;
			return url.substring(url.indexOf("//") + 1, (indexOfQuestion > 0 ? indexOfQuestion : (indexOfSharp  = url.indexOf("#")) > 0 ? indexOfSharp : url.length()));
		}
	}
	
	public static String getFileNameFromUrl(String url, final boolean includeQuestion) {
		url = URLDecoder.decode(url);
		int slashIndex = url.lastIndexOf("/");
		int indexOfQuestion = url.indexOf("?");
		if ((url.startsWith("http") || url.startsWith("ftp"))) {
			if (indexOfQuestion < 0) {
				url = url.substring(++slashIndex);
			} else {
				final String urlTemp = url.substring(0, indexOfQuestion);
				slashIndex = urlTemp.lastIndexOf("/");
				url = url.substring(++slashIndex);
			}
		}
		final int indexOfSharp = url.indexOf("#");
		if (includeQuestion) {
			url = url.replaceAll("\\?", "@");
			return url.substring(0, indexOfSharp > 0 ? indexOfSharp : url.length());
		} else {
			indexOfQuestion = url.indexOf("?");
			return url.substring((indexOfQuestion != 0 ? 0 : 1), (indexOfQuestion > 0 ? indexOfQuestion : indexOfSharp > 0 ? indexOfSharp : url.length()));
		}
	}
	
	public static String saveISToFile(final InputStream is, String dirParent, String url, final boolean autoRename, final boolean overwrite) throws IOException {
		ExceptionLogger.d(TAG, "saveISToFile " + dirParent + "/" + url + ", autoRename: " + autoRename + ", overwrite " + overwrite);
		if (!dirParent.endsWith("/")) {
			dirParent = dirParent + "/";
		}
		final int slashIndex = url.lastIndexOf("/");
		if (slashIndex >= 0) {
			url = url.substring(slashIndex + 1);
		}
		final int indexOfQuestion = url.indexOf("?");
		final int indexOfSharp = url.indexOf("#");
		String name = url.substring((indexOfQuestion != 0 ? 0 : 1), (indexOfQuestion > 0 ? indexOfQuestion : indexOfSharp > 0 ? indexOfSharp : url.length()));
		final int lastIndexOfDot = name.lastIndexOf(".");
		final String ext = lastIndexOfDot >= 0 ? name.substring(lastIndexOfDot, name.length()) : "";
		String newName = name;
		if (lastIndexOfDot >= 0) {
			name = name.substring(0, lastIndexOfDot);
		} 
		final File file = new File(dirParent, newName);
		if (file.exists()) {
			if (autoRename) {
				int inc = 2;
				while (new File(dirParent, newName = (name+"_"+inc++ +ext)).exists()) {
				}
			} else if (!overwrite) {
				return dirParent + newName;
			}
		} else {
			file.getParentFile().mkdirs();
		}
		final File savedFile = new File(dirParent, newName);
		ExceptionLogger.d(TAG, "savedFile " + savedFile.getAbsolutePath());
		final FileOutputStream fos = new FileOutputStream(savedFile);
		final BufferedOutputStream bos = new BufferedOutputStream(fos);
		final BufferedInputStream bis = new BufferedInputStream(is);
		final byte[] barr = new byte[65536];
		int read = 0;
		int size = 0;
		try {
			while ((read = bis.read(barr)) > 0) {
				bos.write(barr, 0, read);
				size += read;
			}
			ExceptionLogger.d(TAG, newName + " size " + size);
		} finally {
			close(bis, is);
			flushClose(bos);
			flushClose(fos);
		}
		return savedFile.getName();
	}

	public static void isAppendFile(final InputStream is, final String fileName) throws IOException {
		final FileOutputStream fos = new FileOutputStream(fileName, true);
		final BufferedOutputStream bos = new BufferedOutputStream(fos);
		final BufferedInputStream bis = new BufferedInputStream(is);
		final byte[] barr = new byte[32768];
		int read = 0;
		try {
			while ((read = bis.read(barr)) > 0) {
				bos.write(barr, 0, read);
			}
		} finally {
			close(is, bis);
			flushClose(bos, fos);
		}
	}
	
	public static void is2File(final InputStream is, final String fileName) throws IOException {
		ExceptionLogger.d(TAG, "is2File " + is + ", " + fileName);
		final File file = new File(fileName);
		file.getParentFile().mkdirs();
		final File tempFile = new File(fileName + ".tmp");
		final FileOutputStream fos = new FileOutputStream(tempFile);
		final BufferedOutputStream bos = new BufferedOutputStream(fos);
		final BufferedInputStream bis = new BufferedInputStream(is);
		final byte[] barr = new byte[32768];
		int read = 0;
		try {
			while ((read = bis.read(barr)) > 0) {
				bos.write(barr, 0, read);
			}
		} finally {
			close(is, bis);
			flushClose(bos, fos);
			file.delete();
			tempFile.renameTo(file);
		}
	}

	public static void writeFileAsCharset(File file, String contents,
										  String newCharset) throws IOException {
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		final FileOutputStream fos = new FileOutputStream(file);
		final FileChannel fileChannel = fos.getChannel();
		fileChannel.write(ByteBuffer.wrap(contents.getBytes(newCharset)));
		fileChannel.force(true);
		close(fileChannel);
		flushClose(fos);
	}
	

	public static String getPathHash(final String filepath) {
		try {
			final MessageDigest md = MessageDigest.getInstance("MD5");
			final byte data[] = (filepath + new File(filepath).lastModified()).getBytes();
			final BigInteger bigInteger = new BigInteger(1, md.digest(data));
			return bigInteger.toString(16);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("cannot initialize MD5 hash function", e);
		}
	}
	
	public static String getExtension(final String fName) {
		if (fName != null) {
			int lastIndexOf = fName.lastIndexOf(".");
			if (lastIndexOf >= 0)
				return fName.substring(++lastIndexOf).toLowerCase();
		}
		return "";
	}

	public static void deleteFolders(final File f, boolean includeDir, final Pattern includePat, final Pattern excludePat) {
		final List<File> fs = FileUtil.getFiles(f, includeDir, includePat, excludePat);
		Collections.sort(fs, new Comparator<File>() {
				@Override
				public int compare(final File p1, final File p2) {
					return p2.getAbsolutePath().compareTo(p1.getAbsolutePath());
				}
			});
		for (File ff : fs) {
			ff.delete();
		}
		f.delete();
	}

	/**
	 0: total length
	 1: number of files
	 2: number of directories
	 */
	public static final long[] getDirSize(final File f, final Pattern includePat, final Pattern excludePat) {
		String name;
		final long[] l = new long[]{0, 0, 0};
		if (f.isFile()) {
			name = f.getName();
			if ((includePat == null && excludePat == null)
				|| (includePat != null && excludePat != null
				&& includePat.matcher(name).matches() && !excludePat.matcher(name).matches())
				|| (includePat != null && includePat.matcher(name).matches())
				|| (excludePat != null && !excludePat.matcher(name).matches())) {
				l[0] += f.length();
				l[1]++;
				//ExceptionLogger.d(TAG, "getFiles.add2 " + f2.getAbsolutePath());
			}
		} else if (f.isDirectory()) {
			final LinkedList<File> folderQueue = new LinkedList<File>();
			folderQueue.add(f);
			File fi = null;
			File[] fs;
			while (folderQueue.size() > 0) {
				fi = folderQueue.pop();
				fs = fi.listFiles();
				if (fs != null)
					for (File f2 : fs) {
						if (f2.isDirectory()) {
							folderQueue.push(f2);
							l[2]++;
						} else {
							name = f2.getName();
							if ((includePat == null && excludePat == null)
								|| (includePat != null && excludePat != null
								&& includePat.matcher(name).matches() && !excludePat.matcher(name).matches())
								|| (includePat != null && includePat.matcher(name).matches())
								|| (excludePat != null && !excludePat.matcher(name).matches())) {
								l[0] += f2.length();
								l[1]++;
								//ExceptionLogger.d(TAG, "getFiles.add2 " + f2.getAbsolutePath());
							}
						}
					}
			}
		}
		return l;
	}
}
