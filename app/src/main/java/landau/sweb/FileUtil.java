package landau.sweb;

import android.util.Log;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import landau.sweb.utils.*;
import java.security.MessageDigest;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import org.mozilla.universalchardet.*;
import java.nio.channels.*;
import java.nio.*;

public class FileUtil {
	
	private static final String TAG = "FileUtil";
	private static final String ISO_8859_1 = "ISO-8859-1";
	public static void close(final Closeable... closable) {
		if (closable != null && closable.length > 0) {
			for (Closeable c : closable) {
				try {
					if (c != null) {
						c.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
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
						e.printStackTrace();
					}
					try {
						c.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
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
			throw new IOException("File is bigger than " + Util.nf.format(Integer.MAX_VALUE) + " bytes");
		}
	}

	public static String readFileByMetaTag(File file)
	throws FileNotFoundException, IOException {
		final byte[] byteArr = FileUtil.readFileToMemory(file);
		final String encoding = UniversalDetector.detectCharset(new ByteArrayInputStream(byteArr));
		final String content = new String(byteArr, encoding == null ? "utf-8" : encoding);
		final String charsetName = HtmlUtil.readValue(content, "charset");
		if (charsetName.length() > 0) {
			ExceptionLogger.d(TAG, file.getAbsolutePath() + " charset: " + charsetName);
			return new String(byteArr, charsetName);
		} else {
			return content;
		}
	}

	public static String readFileWithCheckEncode(File filePath)
	throws FileNotFoundException, IOException,
	UnsupportedEncodingException {
		final byte[] byteArr = FileUtil.readFileToMemory(filePath);
		return readFileWithCheckEncode(filePath, byteArr);
	}

	public static String readFileWithCheckEncode(File f, byte[] byteArr) throws IOException {
		final String encoding = UniversalDetector.detectCharset(f);
		ExceptionLogger.d(TAG, f.getAbsolutePath() + " detectCharset: " + encoding);
		return new String(byteArr, encoding);
	}
	
	public static List<File> getFiles(final File f, final Pattern includePat, final Pattern excludePat) {
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
				ExceptionLogger.d(TAG, "getFiles.add " + f.getAbsolutePath());
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
						} else {
							name = f2.getName();
							if ((includePat == null && excludePat == null)
								|| (includePat != null && excludePat != null
								&& includePat.matcher(name).matches() && !excludePat.matcher(name).matches())
								|| (includePat != null && includePat.matcher(name).matches())
								|| (excludePat != null && !excludePat.matcher(name).matches())) {
								fList.add(f2);
								ExceptionLogger.d(TAG, "getFiles.add2 " + f2.getAbsolutePath());
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
		int slashIndex = url.lastIndexOf("/");
		if (slashIndex >= 0) {
			url = url.substring(++slashIndex);
		}
		if (includeQuestion) {
			return url.replaceAll("\\?", "@");
		} else {
			final int indexOfQuestion = url.indexOf("?");
			final int indexOfSharp;
			return url.substring(0, (indexOfQuestion > 0 ? indexOfQuestion : (indexOfSharp  = url.indexOf("#")) > 0 ? indexOfSharp : url.length()));
		}
	}
	
	public static String saveISToFile(final InputStream is, String dirParent, String name, final boolean autoRename, final boolean overwrite) throws IOException {
		ExceptionLogger.d(TAG, "saveISToFile " + name + ", autoRename: " + autoRename + ", overwrite " + overwrite);
		if (!dirParent.endsWith("/")) {
			dirParent = dirParent + "/";
		}
//		final int slashIndex = url.lastIndexOf("/");
//		if (slashIndex >= 0) {
//			url = url.substring(slashIndex + 1);
//		}
//		final int indexOfQuestion = url.indexOf("?");
//		final int indexOfSharp = url.indexOf("#");
//		String name = url.substring(0, (indexOfQuestion > 0 ? indexOfQuestion : indexOfSharp > 0 ? indexOfSharp : url.length()));
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
		return savedFile.getAbsolutePath();
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
		file.delete();
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


}
