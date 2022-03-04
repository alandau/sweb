package landau.sweb;

import android.webkit.*;
import java.io.*;
import java.net.*;
import java.util.*;
import landau.sweb.utils.*;
import android.webkit.CookieManager;

public class PipeInputStream extends InputStream {

	private static final String TAG = "PipeInputStream";
	public static final int USE_EXIST_ONLY = 0;
	public static final int SAVE_AND_USE = 1;
	public static final int FORCE_SAVE = 2;
	public static final int NO_SAVE_ONLY_NEW = 3;
	
	final OutputStream writeSave;
	final InputStream in;
	final Runnable updateUI;
	
	public PipeInputStream(final InputStream in,
						   final OutputStream writeSave, 
						   final Runnable updateUI) {
		this.in = in;
		this.writeSave = writeSave;
		this.updateUI = updateUI;
	}
	
	public static WebResourceResponse getResponse(
		final int readWay,
		final String urlToString, 
		final String savedFilePath,
		final Runnable updateUI,
		final Map<String, String> requestHeaders) throws MalformedURLException, IOException {
		final File file = new File(savedFilePath);
		ExceptionLogger.d(TAG, "readWay " + readWay + ", exists " + file.exists() + ", urlToString " + urlToString + ", savedFilePath " + savedFilePath + ", requestHeaders " + requestHeaders);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}
		if (readWay == PipeInputStream.SAVE_AND_USE
			|| readWay == PipeInputStream.USE_EXIST_ONLY) {
			if (file.exists() && file.isFile()) {
				if (updateUI != null) {
					updateUI.run();
				}
				return new WebResourceResponse(
					MimeTypeMap.getSingleton().getMimeTypeFromExtension(FileUtil.getExtension(savedFilePath)),
					null,
					new BufferedInputStream(new FileInputStream(file)));
			}
		}
		final URL url = new URL(urlToString);
		final URLConnection conn = url.openConnection();
		if (readWay == PipeInputStream.SAVE_AND_USE
			|| readWay == PipeInputStream.FORCE_SAVE) {
			final String cookies = CookieManager.getInstance().getCookie(urlToString);
			ExceptionLogger.d(TAG, "cookies " + cookies + ", requestHeaders " + requestHeaders);
			conn.addRequestProperty("Cookie", cookies);
			ExceptionLogger.d(TAG, "conn.RequestProperties " + conn.getRequestProperties());
			final Set<Map.Entry<String, String>> s = requestHeaders.entrySet();
			for (Map.Entry<String, String> e : s) {
				conn.addRequestProperty(e.getKey(), e.getValue());
			}
			if (urlToString.startsWith("http")
				|| urlToString.startsWith("ftp")) {
				final HttpURLConnection httpConn = (HttpURLConnection)conn;
				ExceptionLogger.d(TAG, "httpConn.getHeaderFields() " + httpConn.getHeaderFields());
				return new WebResourceResponse(
					MimeTypeMap.getSingleton().getMimeTypeFromExtension(FileUtil.getExtension(savedFilePath)),
					httpConn.getContentEncoding(),
					httpConn.getResponseCode(),
					httpConn.getResponseMessage(),
					null,//getHeader(httpConn.getHeaderFields()),
					new PipeInputStream(conn.getInputStream(), new BufferedOutputStream(new FileOutputStream(file)), updateUI));
			}
		} else if (readWay == PipeInputStream.NO_SAVE_ONLY_NEW) {
			return new WebResourceResponse(
				MimeTypeMap.getSingleton().getMimeTypeFromExtension(FileUtil.getExtension(savedFilePath)),
				null,
				new PipeInputStream(conn.getInputStream(), null, updateUI));
		}
		return new WebResourceResponse(
			MimeTypeMap.getSingleton().getMimeTypeFromExtension(FileUtil.getExtension(savedFilePath)),
			null,
			new ByteArrayInputStream(new byte[0]));

	}
	
	@Override
	public int read() throws IOException {
		final int read = in.read();
		if (writeSave != null && read != -1) {
			writeSave.write(read);
			writeSave.flush();
		}
		if (read == -1 && updateUI != null) {
			updateUI.run();
		}
		return read;
	}

	@Override
	public int read(final byte[] buffer) throws IOException {
		final int read = in.read(buffer);
		if (writeSave != null && read != -1) {
			writeSave.write(buffer, 0, read);
			writeSave.flush();
		}
		if (read == -1 && updateUI != null) {
			updateUI.run();
		}
		return read;
	}

	@Override
    public int read(final byte[] buffer, final int byteOffset, final int byteCount) throws IOException {
		final int read = in.read(buffer, byteOffset, byteCount);
		if (writeSave != null && read != -1) {
			writeSave.write(buffer, byteOffset, read);
			writeSave.flush();
		}
		if (read == -1 && updateUI != null) {
			updateUI.run();
		}
		return read;
	}
	
	@Override
	public void close() throws IOException {
		super.close();
		in.close();
		if (writeSave != null) {
			FileUtil.flushClose(writeSave);
		}
		if (updateUI != null) {
			updateUI.run();
		}
	}
	
	@Override
	public int available() throws IOException {
		return in.available();
	}

	@Override
	public void mark(final int readlimit) {
		in.mark(readlimit);
	}

	@Override
    public boolean markSupported() {
		return in.markSupported();
	}

	@Override
	public synchronized void reset() throws IOException {
		in.reset();
	}

	@Override
    public long skip(final long byteCount) throws IOException {
		return in.skip(byteCount);
	}
	
}
