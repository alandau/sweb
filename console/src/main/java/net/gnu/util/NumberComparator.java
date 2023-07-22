package net.gnu.util;

import java.util.Comparator;

public class NumberComparator implements Comparator<String> {

	@Override
	public int compare(final String file1, final String file2) {
		return Util.compareNumberString(file1, file2);
	}
}
