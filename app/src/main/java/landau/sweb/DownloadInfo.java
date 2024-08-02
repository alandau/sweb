package landau.sweb;

import java.io.*;
import net.gnu.util.*;

public class DownloadInfo implements Serializable, Comparable<DownloadInfo> {
	String url;
	String status;
	String savedPath;
	String name;

	DownloadInfo(final String url, boolean exact) {
		this.url = url;
		this.name = FileUtil.getFileNameFromUrl(url, exact);
		this.savedPath = MainActivity.getSavedFilePath(url, MainActivity.SCRAP_PATH, false);
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof DownloadInfo) {
			return url.equals(((DownloadInfo) o).url);
		} else {
			return false;
		}
	}
	@Override
	public int compareTo(final DownloadInfo p1) {
		return url.compareTo(p1.url);
	}
}
