package landau.sweb;

import java.io.*;

public class CrawlerInfo implements Serializable, Comparable<CrawlerInfo> {

	final String url;
	final int level;

	CrawlerInfo(final String url, final int level) {
		this.url = url;
		this.level = level;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null) {
			return false;
		} else if (o instanceof String) {
			return url.equals((String) o);
		} else if (o instanceof CrawlerInfo) {
			return url.equals(((CrawlerInfo) o).url);
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(final CrawlerInfo p1) {
		return url.compareTo(p1.url);
	}
}
