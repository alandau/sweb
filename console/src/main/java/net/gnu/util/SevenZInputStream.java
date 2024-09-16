package net.gnu.util;

import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import java.io.InputStream;
import java.io.IOException;

public class SevenZInputStream extends InputStream {
	private String TAG = "SevenZInputStream";

	final SevenZFile sevenZFile;
	final int length;
	final String fileName;
	final String entryName;
	final long nano;
    int pos = 0;

	public SevenZInputStream(final SevenZFile sevenZFile, final int length, final String fileName, final String entryName) {
		ExceptionLogger.d(TAG, "length " + Util.nf.format(length) + ", fileName " + fileName +", entryName " + entryName);
		this.sevenZFile = sevenZFile;
		this.length = length;
		this.fileName = fileName;
		this.entryName = entryName;
		nano = System.nanoTime();
	}

	@Override
    public int read() throws IOException {
		final int read = sevenZFile.read();
		pos++;
		//ExceptionLogger.d(TAG, "read1 pos " + pos + ", " + read);
		return read;
	}

	@Override
    public int read(final byte[] barr) throws IOException {
		final int read = sevenZFile.read(barr, 0, barr.length);
		pos += read;
		//ExceptionLogger.d(TAG, "read2 pos " + pos + ", " + read);
		return read;
	}

	@Override
    public int read(final byte[] barr, final int off, final int len) throws IOException {
		final int read = sevenZFile.read(barr, off, len);
		pos += read;
		//ExceptionLogger.d(TAG, "read3 pos " + pos + ", " + read);
		return read;
	}

	@Override
    public long skip(final long n) throws IOException {
		ExceptionLogger.d(TAG, "skip   " + Util.nf.format(n));
		long k = length - pos;
        final long possibleSkipRange = (n < 0) ? 0 : (n < k) ? n : k;
        
		k = possibleSkipRange;
		if (k > 0) {
			final int LEN = 4096;
			final byte[] barr = new byte[LEN];
			int read = 0;
			while (k > 0 && (read = sevenZFile.read(barr, 0, (int)(k > LEN ? LEN : k))) > 0) {
				k -= read;
			}
			pos += (possibleSkipRange - k);
		}
		ExceptionLogger.d(TAG, "skipped " + Util.nf.format(possibleSkipRange));
		return possibleSkipRange - k;
	}

	@Override
    public int available() {
		//ExceptionLogger.d(TAG, "available " + (length - pos));
		return length - pos;
	}

	@Override
    public void close() throws IOException {
		ExceptionLogger.d(TAG, "close pos=" + Util.nf.format(pos) + ", fileName " + fileName +", entryName " + entryName + ", took " + Util.nf.format(System.nanoTime() - nano));
		sevenZFile.close();
	}

	@Override
    public synchronized void mark(final int readlimit) {
		ExceptionLogger.d(TAG, "UnsupportedOperationException mark " + readlimit);
		throw new UnsupportedOperationException();
	}

	@Override
    public synchronized void reset() {
		ExceptionLogger.d(TAG, "UnsupportedOperationException reset");
		throw new UnsupportedOperationException();
	}

	@Override
    public boolean markSupported() {
		ExceptionLogger.d(TAG, "markSupported false");
		return false;
	}
}
