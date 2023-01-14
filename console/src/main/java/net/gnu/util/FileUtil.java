package net.gnu.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

public class FileUtil {
	
	private static final String TAG = "FileUtil";
	
	private static final String ISO_8859_1 = "ISO-8859-1";
	private static final Pattern encodingCss = Pattern.compile("@charset\\s+\"([^\"]+)\";");
	public static final Pattern ILLEGAL_FILE_CHARS = Pattern.compile("[^\n]*?[?/\\:*|\"<>#+%][^\n]*?");
	
	public static void main(String[] args) throws Exception {

		//exec("/system/bin/cat", "/proc/meminfo");
		
		System.out.println(Util.mapToString(System.getenv(), true, "\n"));
		exec("pwd");

		//System.out.println(System.getenv("SECONDARY_STORAGE"));
		System.out.println(System.getenv("EXTERNAL_STORAGE"));
		
		final Object[] objs = getDirSize(new File("/data/user/0/com.aide.ui/no_backup/.aide/maven"), true, Pattern.compile("^[^\"]*?\\.aar$", Pattern.CASE_INSENSITIVE), null);
		for (File f : (List<File>)objs[3]) {
			if (f.isDirectory() && f.getName().endsWith(".aar")) {
				System.out.println(f.getAbsolutePath());
				//deleteFolders(f, true, null, null);
			}
		}
		exec("/system/bin/cp", "-f", "/sdcard/.aide/com.aide.ui_preferences.xml", "/data/data/com.aide.ui/shared_prefs");
		//exec("/system/bin/cp", "-fr", "/data/data/com.aide.ui/shared_prefs/com.aide.ui_preferences.xml", "/sdcard/.aide");
		
		System.out.println(new File("/storage/emulated/0/AppProjects/").getAbsolutePath());
		
//		Entry<BufferedReader, PrintStream> e = execInteract("/system/bin/cp", "-R", "-i", "/sdcard/rar", "/sdcard/.com.free.searcher");
//		PrintStream p = e.getValue();
//		BufferedReader b = e.getKey();
//		Scanner input = new Scanner(System.in);
//
//		//System.out.print("Enter: ");
//		String str = "";//input.nextLine();
//		while (!"exit".equals(str)) {
//			while (b.ready()) {
//				System.out.println(b.readLine());
//			}
//			str = input.nextLine();
//			p.println(str);
//		}

		//compare2Folder(new File("/storage/emulated/0/tmp"), new File("/storage/emulated/0/tmp"));
		//compare2Folder(new File("/storage/emulated/0/tmp"), new File("/storage/emulated/0/rar/b"));
		
		//filterFiles("/storage/sdcard0/Download/HTTrack/Websites/oldmt/32", "-\\S{9}\\s+\\S+\\s+\\S+\\s+\\d*?\\s+\\S+\\s+\\S+\\s+([^/]+?)\\n", ".*?\\.(bmp|gif|ico|cur|jpeg|jpg|jpe|pcx|png|tiff|tif|psd|dwg|pct|pic|3gpp|3gp|3gpp2|3g2|avi|m4v|mpeg|mpg|mpe|mp4|vob|qt|mov|webm|asf|asx|wm|wmv|wmx|mpa|flv|mkv)");

		//testPattern();

	}
	
	public static StringBuilder exec(final String... cmd) {
		if (cmd == null || cmd.length == 0) {
			return new StringBuilder();
		}
		BufferedReader pout = null;
		PrintStream pin = null;
		final StringBuilder sb = new StringBuilder();
		try {
			final ProcessBuilder builder = new ProcessBuilder(cmd);
			builder.directory(new File(System.getenv("EXTERNAL_STORAGE")+"/.aide"));
			builder.redirectErrorStream(true);
//			pb.redirectInput(ProcessBuilder.Redirect.from(new File("in.txt")));
//			pb.redirectOutput(ProcessBuilder.Redirect.to(new File("out.txt")));
//			pb.redirectError(ProcessBuilder.Redirect.appendTo(new File("error.log")));

			Process p = builder.start();
			//Process p = Runtime.getRuntime().exec(cmd);  
			// Execute with input/output

			// Write into the standard input of the subprocess
			pin = new PrintStream(new BufferedOutputStream(p.getOutputStream()));
			// Read from the standard output of the subprocess
			pout = new BufferedReader(new InputStreamReader(p.getInputStream()));

			// Pump in input
//			pin.print("1 2");
//			pin.close();

			// Save the output in a StringBuffer for further processing
			int ch;
			String arrayToString = Util.arrayToString(cmd, false, " ");
			System.out.println("\n" + "\n" + arrayToString);

			while ((ch = pout.read()) != -1) {
				sb.append((char)ch);
				System.out.print((char)ch);
			}

			//System.out.println(sb);
//			BufferedReader perr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
//			while ((ch = perr.read()) != -1) {
//				System.out.print((char)ch);
//			}
			final int exitValue = p.waitFor();
			//System.out.print(sb);
			System.out.println(arrayToString + " : exit code " + exitValue);
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		} finally {
			FileUtil.close(pin, pout);
		}
		return sb;
	}

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

	public static void copy(final File src, final File destFile) throws IOException {
		is2File(new FileInputStream(src), destFile.getAbsolutePath());
		destFile.setLastModified(src.lastModified());
	}

	public static void copyKeepBothAutoRename(final File src, final File destFile) throws IOException {
		final String keepBothAutoRename = keepBothAutoRename(destFile);
		is2File(new FileInputStream(src), keepBothAutoRename);
		new File(keepBothAutoRename).setLastModified(src.lastModified());
	}

	public static String keepBothAutoRename(final File destFile) {
		final String name = destFile.getName();
		final String destParent = destFile.getParent();
		int inc = 2;
		String newName = "";
		String nameWithoutExtension = name;
		String ext = "";
		if (destFile.isFile()) {
			final int lastIndexOfDot = name.lastIndexOf(".");
			if (lastIndexOfDot >= 0) {
				nameWithoutExtension = name.substring(0, lastIndexOfDot);
				ext = name.substring(lastIndexOfDot);
			}
		}
		while (new File(destParent, newName = (nameWithoutExtension + "_" + inc++ + ext)).exists()) {
		}
		return destParent + "/" + newName;
	}
	
	public static int is2Barr(final BufferedInputStream in, final byte[] bytes) throws IOException {
		final int length = bytes.length;
		int count = 0;
		int read = 0;
		while (read < length && (count = in.read(bytes, read, length - read)) > 0) {
			read += count;
			//Log.d(TAG, "count " + count);
		}
		return read;
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
		final byte[] byteArr = readFileToMemory(file);
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
		final List<File> fList = new LinkedList<File>();
		String name;
		long totalSize = 0;
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
				totalSize += f.length();
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
								totalSize += f2.length();
								//ExceptionLogger.d(TAG, "getFiles.add2 " + f2.getAbsolutePath());
							}
						}
					}
				}
			}
		}
		ExceptionLogger.d(TAG, "getFiles " + f.getAbsolutePath() + ", includePat " + includePat + ", excludePat " + excludePat + ", totalSize " + totalSize);
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
		is2File(is, dirParent + newName);
//		final File savedFile = new File(dirParent, newName);
//		ExceptionLogger.d(TAG, "savedFile " + savedFile.getAbsolutePath());
//		final FileOutputStream fos = new FileOutputStream(savedFile);
//		final BufferedOutputStream bos = new BufferedOutputStream(fos);
//		final BufferedInputStream bis = new BufferedInputStream(is);
//		final byte[] barr = new byte[65536];
//		int read = 0;
//		int size = 0;
//		try {
//			while ((read = bis.read(barr)) > 0) {
//				bos.write(barr, 0, read);
//				size += read;
//			}
//			ExceptionLogger.d(TAG, newName + " size " + size);
//		} finally {
//			close(bis, is);
//			flushClose(bos);
//			flushClose(fos);
//		}
		return newName;//savedFile.getName();
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
		final File parentFile = file.getParentFile();
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}
		if (file.exists() && !file.delete()) {
			throw new IOException("Can't delete " + fileName);
		}
		final FileOutputStream fos = new FileOutputStream(file);
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
			//tempFile.renameTo(file);
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
	
	public static boolean compareFileContent(final File f1, final File f2) throws IOException {
		//Log.d("compare file ", f1.getName() + " and " + f2.getName());
		final long len = f1.length();
		if (len != f2.length()) {
			return false;
		} else if (len == 0 || f1.equals(f2)) {
			return true;
		}
		FileInputStream fis1 = null;
		FileInputStream fis2 = null;
		BufferedInputStream bis1 = null;
		BufferedInputStream bis2 = null;
		try {
			fis1 = new FileInputStream(f1);
			fis2 = new FileInputStream(f2);
			bis1 = new BufferedInputStream(fis1);
			bis2 = new BufferedInputStream(fis2);

			final int BUFFER_SIZE = 65536;
			final byte[] bArr1 = new byte[BUFFER_SIZE];
			final byte[] bArr2 = new byte[BUFFER_SIZE];

			int read = 0;
			long counter = 0;
			int i;
			while (counter < len && (read = is2Barr(bis1, bArr1)) == is2Barr(bis2, bArr2)) {
				counter += read;
				for (i = 0; i < read; i++) {
					if (bArr1[i] != bArr2[i]) {
						return false;
					}
				}
			} 
			return true;
		} finally {
			close(bis1, bis2, fis1, fis2);
		}
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
	 2: number of folders
	 3: list of files
	 */
	public static final Object[] getDirSize(final File f, final boolean includeDir, final Pattern includePat, final Pattern excludePat) {
		String name;
		final Object[] l = new Object[4];
		long length = 0;
		long files = 0;
		long folders = 0;
		List<File> lf = new LinkedList<>();
		if (f.isFile()) {
			name = f.getName();
			if ((includePat == null && excludePat == null)
				|| (includePat != null && excludePat != null
				&& includePat.matcher(name).matches() && !excludePat.matcher(name).matches())
				|| (includePat != null && includePat.matcher(name).matches())
				|| (excludePat != null && !excludePat.matcher(name).matches())) {
				length += f.length();
				files++;
				lf.add(f);
				//ExceptionLogger.d(TAG, "getFiles.add2 " + f2.getAbsolutePath());
			}
		} else if (f.isDirectory()) {
			final LinkedList<File> folderQueue = new LinkedList<File>();
			folderQueue.push(f);
			folders++;
			if (includeDir) {
				lf.add(f);
			}
			File fi = null;
			File[] fs;
			while (folderQueue.size() > 0) {
				fi = folderQueue.pop();
				fs = fi.listFiles();
				if (fs != null)
					for (File f2 : fs) {
						if (f2.isDirectory()) {
							folderQueue.push(f2);
							folders++;
							if (includeDir) {
								lf.add(f2);
							}
						} else {
							name = f2.getName();
							if ((includePat == null && excludePat == null)
								|| (includePat != null && excludePat != null
								&& includePat.matcher(name).matches() && !excludePat.matcher(name).matches())
								|| (includePat != null && includePat.matcher(name).matches())
								|| (excludePat != null && !excludePat.matcher(name).matches())) {
								length += f2.length();
								files++;
								lf.add(f2);
								//ExceptionLogger.d(TAG, "getFiles.add2 " + f2.getAbsolutePath());
							}
						}
					}
			}
		}
		l[0] = length;
		l[1] = files;
		l[2] = folders;
		l[3] = lf;
		return l;
	}
}
