package net.gnu.util;

import java.io.InputStream;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import java.io.IOException;

public class ZipIS extends InputStream {
	private String TAG = "ZipIS";

	final ZipInputStream zis;
	final int length;
    int pos = 0;

	public ZipIS(final ZipInputStream zis, final int length) {
		ExceptionLogger.d(TAG, "length " + length + ", zis " + zis);
		this.zis = zis;
		this.length = length;
	}

	@Override
    public int read() throws IOException {
		final int read = zis.read();
		pos++;
		//ExceptionLogger.d(TAG, "read1 pos " + pos + ", " + read);
		return read;
	}

	@Override
    public int read(final byte[] barr) throws IOException {
		final int read = zis.read(barr, 0, barr.length);
		pos += read;
		//ExceptionLogger.d(TAG, "read2 pos " + pos + ", " + read);
		return read;
	}

	@Override
    public int read(final byte[] barr, final int off, final int len) throws IOException {
		final int read = zis.read(barr, off, len);
		pos += read;
		//ExceptionLogger.d(TAG, "read3 pos " + pos + ", " + read);
		return read;
	}

	@Override
    public long skip(final long n) throws IOException {
		ExceptionLogger.d(TAG, "skip   " + n);
		long k = length - pos;
        if (n < k) {
            k = n < 0 ? 0 : n;
        }
		final long k2 = k;
		if (k > 0) {
			final int LEN = 4096;
			final byte[] barr = new byte[LEN];
			int read = 0;
			while ((read = zis.read(barr, 0, Math.min(LEN, (int)k))) != -1) {
				k -= read;
			}
			pos += k2;
		}
		ExceptionLogger.d(TAG, "skipped " + k2);
		return k2;
	}

	@Override
    public int available() {
		//ExceptionLogger.d(TAG, "available " + (length - pos));
		return length - pos;
	}

	@Override
    public void close() throws IOException {
		ExceptionLogger.d(TAG, "close pos=" + pos);
		zis.close();
	}

	@Override
    public synchronized void mark(final int readlimit) {
		ExceptionLogger.d(TAG, "mark " + readlimit);
		throw new UnsupportedOperationException();
	}

	@Override
    public synchronized void reset() {
		ExceptionLogger.d(TAG, "reset");
		throw new UnsupportedOperationException();
	}

	@Override
    public boolean markSupported() {
		ExceptionLogger.d(TAG, "markSupported false");
		return false;
	}
}

