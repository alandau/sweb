package landau.sweb;

import android.webkit.CookieManager;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;
import net.gnu.util.ExceptionLogger;
import net.gnu.util.FileUtil;

public class PipeInputStream extends InputStream {

	private static final String TAG = "PipeInputStream";
	public static final int USE_EXIST_ONLY = 0;
	public static final int SAVE_AND_USE = 1;
	public static final int NO_SAVE_ONLY_NEW = 3;
	
	final OutputStream writeSave;
	final InputStream in;
	final Runnable updateUI;
	final long dateTime;
	final File savedFile;
	
	public PipeInputStream(final String savedFilePath,
						   final InputStream in,
						   final OutputStream writeSave, 
						   final Runnable updateUI,
						   final long dateTime) {
		this.savedFile = new File(savedFilePath);
		this.in = in;
		this.writeSave = writeSave;
		this.updateUI = updateUI;
		this.dateTime = dateTime;
		//ExceptionLogger.d(TAG, "savedFilePath " + savedFilePath + ", writeSave " + writeSave);
	}
	
	public static WebResourceResponse getResponse(
		final int readWay,
		final String urlToString, 
		final String savedFilePath,
		final Runnable updateUI,
		final Map<String, String> requestHeaders) throws IOException {
		final File file = new File(savedFilePath);
		ExceptionLogger.d(TAG, "readWay " + readWay + ", exists " + file.exists() + ", urlToString " + urlToString + ", savedFilePath " + savedFilePath);// + ", requestHeaders " + requestHeaders);
		if (readWay == PipeInputStream.USE_EXIST_ONLY) {
			if (file.exists() && file.isFile()) {
				if (updateUI != null) {
					updateUI.run();
				}
				return new WebResourceResponse(
					MimeTypeMap.getSingleton().getMimeTypeFromExtension(FileUtil.getExtension(file.getName())),
					null,
					new BufferedInputStream(new FileInputStream(file)));
			}
		} else {
			if (!file.exists()) {
				file.getParentFile().mkdirs();
			}
			final URL url = new URL(urlToString);
			final URLConnection conn = url.openConnection();
			if (readWay == PipeInputStream.SAVE_AND_USE) {
				final String cookies = CookieManager.getInstance().getCookie(urlToString);
				//ExceptionLogger.d(TAG, "cookies " + cookies + ", requestHeaders " + requestHeaders);
				conn.addRequestProperty("Cookie", cookies);
				final Set<Map.Entry<String, String>> s = requestHeaders.entrySet();
				for (Map.Entry<String, String> e : s) {
					conn.addRequestProperty(e.getKey(), e.getValue());
				}
				conn.setDoInput(true);
				ExceptionLogger.d(TAG, "conn.RequestProperties " + conn.getRequestProperties());
				//long dateTime = conn.getLastModified();
				if (urlToString.startsWith("http")
					|| urlToString.startsWith("ftp")) {
					final HttpURLConnection httpConn = (HttpURLConnection)conn;
					//ExceptionLogger.d(TAG, "dateTime " + Util.dtf.format(dateTime) + "\n httpConn.getHeaderFields() " + httpConn.getHeaderFields());
					if (file.exists() && file.isFile()) {//} && file.lastModified() == dateTime && dateTime > 0) {
						//ExceptionLogger.d(TAG, "existed");
						return new WebResourceResponse(
							MimeTypeMap.getSingleton().getMimeTypeFromExtension(FileUtil.getExtension(file.getName())),
							null,
							new BufferedInputStream(new FileInputStream(file)));
					}
					if (!file.exists()) {
						file.getParentFile().mkdirs();
					}
					//ExceptionLogger.d(TAG, "not existed");
					return new WebResourceResponse(
						MimeTypeMap.getSingleton().getMimeTypeFromExtension(FileUtil.getExtension(file.getName())),
						httpConn.getContentEncoding(),
						httpConn.getResponseCode(),
						httpConn.getResponseMessage(),
						null,
						new PipeInputStream(savedFilePath, httpConn.getInputStream(), new BufferedOutputStream(new FileOutputStream(file)), updateUI, System.currentTimeMillis()));
				}
			} else if (readWay == PipeInputStream.NO_SAVE_ONLY_NEW) {
				//long dateTime = conn.getLastModified();
				return new WebResourceResponse(
					MimeTypeMap.getSingleton().getMimeTypeFromExtension(FileUtil.getExtension(file.getName())),
					null,
					new PipeInputStream(savedFilePath, conn.getInputStream(), null, updateUI, System.currentTimeMillis()));
			}
		}
		//ExceptionLogger.d(TAG, "getResponse return new byte[0]");
		return new WebResourceResponse("text/plain", "utf-8",
									   new ByteArrayInputStream(new byte[0]));
	}
	
	@Override
	public int read() throws IOException {
		final int read = in.read();
		if (writeSave != null && read != -1) {
			writeSave.write(read);
			writeSave.flush();
			savedFile.setLastModified(dateTime);
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
			savedFile.setLastModified(dateTime);
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
			savedFile.setLastModified(dateTime);
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
			savedFile.setLastModified(dateTime);
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
