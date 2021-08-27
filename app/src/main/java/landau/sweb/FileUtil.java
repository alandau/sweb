package landau.sweb;

import java.io.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import landau.sweb.utils.*;
import java.util.*;
import java.util.regex.*;

public class FileUtil {
	
	private static final String TAG = "FileUtil";
	
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
	
	public static String readHtml(final File ff) throws IOException {
		final Document doc = Jsoup.parse(ff, null);
		ExceptionLogger.d("charset", doc.charset().toString());
		
		return doc.outerHtml();
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
	
	
}
