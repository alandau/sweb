package net.gnu.util;

import java.io.File;
import java.util.Comparator;

public class FileNumberComparator implements Comparator<File> {
	
	@Override
	public int compare(final File file1, final File file2) {
		return Util.compareNumberString(file1.getAbsolutePath(), file2.getAbsolutePath());
	}
}
