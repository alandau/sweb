package landau.sweb;

import android.graphics.*;
import android.os.*;
import android.webkit.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import net.gnu.common.*;
import net.gnu.util.*;

import net.gnu.common.ExceptionLogger;
import chm.cblink.nb.chmreader.lib.*;

public class Tab implements Serializable {
	private static final String TAG = "Tab";
	
	Tab() {
	}
	
	Tab(final CustomWebView w, final boolean isIncognito) {
		this.webview = w;
		this.isIncognito = isIncognito;
		this.webview.tab = this;
	}
	String userAgent;
	String textEncoding;
	transient WebView printWeb;
	boolean saveAndClose = false;
	boolean openAndClose = false;
	boolean loading;
	transient CustomWebView webview;
	String url;
	boolean isDesktop;
	long lastDownload = -1L;
	String sourceName;
	boolean isIncognito = false;
	transient LinkedList<String> requestsLog = new LinkedList<>();
	CharSequence recentConstraint;
	boolean useAdBlocker;
	boolean enableCookies;
	boolean accept3PartyCookies;
	boolean offscreenPreRaster;
	boolean requestSaveData;
	boolean removeIdentifyingHeaders;
	boolean doNotTrack;
	boolean javaScriptCanOpenWindowsAutomatically;
	boolean textReflow;
	transient Bitmap favicon;
	transient Bitmap previewImage;
	boolean blockImages;
	boolean blockMedia;
	boolean blockFonts;
	boolean blockCSS;
	boolean blockJavaScript;
	boolean blockNetworkLoads;
	boolean loadWithOverviewMode;
	boolean javaScriptEnabled;
	String source = "";
	ArrayList<String> resourcesList = new ArrayList<>();
	String includeResPatternStr = ".*?\\b(jpg|jpeg|webp|gif|css|js|ico).*?";
	String excludeResPatternStr = ".*?(44884218_345707102882519_2446069589734326272_n.jpg|68d99ba29cc8.png|1b47f9d0e595.png|bcd90c1d4868.png|1075ddfe0f68.png|77929eccc37e.png|.*?\\.png).*?";
	transient Pattern includeResPattern;
	transient Pattern excludeResPattern;
	String batchLinkPatternStr = "";
	int from = 0;
	int to = 0;
	String crawlPatternStr = "";
	String excludeCrawlPatternStr = ".*?(tel|mailto|javascript):.*? .*?(feed|comment|'|google-analytics.com).* [^\"]*?\\.(pdf|docx?|xlsx?|pptx?|epub|prc|mobi|fb2|exe|apk|bin|7z|tgz|tbz2|zstd|zip|bz2|gz|avi|mp4|webm|wmv|asf|mkv|av1|mov|mpeg|flv|mp3|opus|wav|wma|amr|ogg|vp9|pcm|rm|ram|m4a|3gpp?)[^\"]*?";
	transient Pattern crawlPattern = null;
	transient Pattern excludeCrawlPattern = null;
	boolean excludeLinkFirst;
	boolean excludeResFirst;
	String replace = "";
	String by = "";
	transient Pattern[] replacePat;
	String[] bys;

	TreeSet<CrawlerInfo> batchDownloadSet = new TreeSet<>();
	TreeSet<CrawlerInfo> batchDownloadedSet = new TreeSet<>();
	boolean batchRunning = false;
	boolean update = false;

	boolean saveHtml = false;
	boolean saveImage = false;
	boolean exactImageUrl = true;
	boolean saveMedia;
	boolean saveResources;
	boolean catchAMP = false;
	boolean localization = true;
	int autoReload = 0;
	boolean removeComment;
	boolean removeJavascript;
	boolean userScriptEnabled;
	int level = -1;
	String lastUrl = "";
	int curLevel = 0;

	Utils utils = null;
	transient Handler handler = new Handler();
	transient Runnable autoRerun;
	volatile ArrayList<DownloadInfo> downloadInfos = new ArrayList<>();
	volatile LinkedList<DownloadInfo> downloadedInfos = new LinkedList<>();
	transient LogArrayAdapter logAdapter;
	boolean showRequestList = false;
	boolean started = false;
	String chmFilePath = "";
	String extractPath;
	String md5File;
	String password = "";
	ArrayList<String> listSite;
	ArrayList<String> listBookmark;
	int historyIndex = -1;
	transient CustomDialogBookmark bookmarkDialog;
	boolean searchChanged;
	volatile boolean onEnterPassword = false;
	boolean passwordOK = false;
	boolean cacheMedia = false;
	boolean savePassword = false;

	String getSavedPath(final String name) {
		for (int i = 0; i < downloadedInfos.size(); i++) {
			final DownloadInfo di = downloadedInfos.get(i);
			if (name.equals(di.name)) {
				return di.savedPath;
			}
		}
		return "";
	}
	boolean addImage(final String url, final boolean exact) {
		final String onlyName = FileUtil.getFileNameFromUrl(url, false);
		try {
			if (exact) {
				for (DownloadInfo downloadInfo : downloadInfos) {
					if (url.equals(downloadInfo.url)) {
						return false;
					}
				}
			} else {
				for (DownloadInfo downloadInfo : downloadInfos) {
					if (onlyName.equals(downloadInfo.name)) {
						return false;
					}
				}
			}
			if (exact) {
				for (DownloadInfo downloadInfo : downloadedInfos) {
					if (url.equals(downloadInfo.url)) {
						return false;
					}
				}
			} else {
				for (DownloadInfo downloadInfo : downloadedInfos) {
					if (onlyName.equals(downloadInfo.name)) {
						return false;
					}
				}
			}
		} catch (Throwable t) {
			ExceptionLogger.e(TAG, t);
		}
		downloadInfos.add(new DownloadInfo(url, exact));
		return true;
	}

	int delay = 1000;
	boolean autoscroll = false;
	boolean scrolling = false;
	int scrollY;
	int scrollMax;
	int length = 768;
	public void copyTab(final Tab srcTab) {
		this.isDesktop = srcTab.isDesktop;
		this.userAgent = srcTab.userAgent;
		this.isIncognito = srcTab.isIncognito;
		this.blockImages = srcTab.blockImages;
		this.blockMedia = srcTab.blockMedia;
		this.blockFonts = srcTab.blockFonts;
		this.blockCSS = srcTab.blockCSS;
		this.blockJavaScript = srcTab.blockJavaScript;
		this.saveImage = srcTab.saveImage;
		this.saveMedia = srcTab.saveMedia;
		this.saveResources = srcTab.saveResources;
		this.saveHtml = srcTab.saveHtml;
		this.loadWithOverviewMode = srcTab.loadWithOverviewMode;
		this.includeResPatternStr = srcTab.includeResPatternStr;
		this.excludeResPatternStr = srcTab.excludeResPatternStr;
		this.includeResPattern = srcTab.includeResPattern;
		this.excludeResPattern = srcTab.excludeResPattern;
		this.useAdBlocker = srcTab.useAdBlocker;
		this.enableCookies = srcTab.enableCookies;
		this.accept3PartyCookies = srcTab.accept3PartyCookies;
		this.offscreenPreRaster = srcTab.offscreenPreRaster;
		this.requestSaveData = srcTab.requestSaveData;
		this.doNotTrack = srcTab.doNotTrack;
		this.javaScriptCanOpenWindowsAutomatically = srcTab.javaScriptCanOpenWindowsAutomatically;
		this.removeIdentifyingHeaders = srcTab.removeIdentifyingHeaders;
		this.textReflow = srcTab.textReflow;
	}
}
