package landau.sweb;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslCertificate;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.HttpAuthHandler;
import android.webkit.JavascriptInterface;
import android.webkit.MimeTypeMap;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import chm.cblink.nb.chmreader.lib.CHMFile;
import chm.cblink.nb.chmreader.lib.Utils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import landau.sweb.MainActivity;
import net.gnu.common.AndroidUtils;
import net.gnu.common.ExceptionLogger;
import net.gnu.common.ParentActivity;
import net.gnu.common.PlacesDbHelper;
import net.gnu.epub.BookCreateTask;
import net.gnu.util.CompressedFile;
import net.gnu.util.FileUtil;
import net.gnu.util.HtmlUtil;
import net.gnu.util.Mht2Htm;
import net.gnu.util.Util;
import org.apache.commons.compress.PasswordRequiredException;
import org.geometerplus.android.fbreader.DictionaryUtil;
import org.json.JSONArray;
import org.json.JSONException;
import java.net.URLEncoder;
import android.widget.LinearLayout;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import android.content.res.Configuration;
import android.media.*;

public class MainActivity extends ParentActivity {
	private PowerManager.WakeLock mWakeLock;
	
    private static final String TAG = "MainActivity";
	private static final int DARK_BACKGROUND = 0xffc0c0c0;
	private static final int LIGHT_BACKGROUND = 0xfffffff0;
	static String SCRAP_PATH;
	private static final Pattern FAVICON_PATTERN = Pattern.compile(".*?/favicon\\.(ico|png|bmp|jpe?g|gif)", Pattern.CASE_INSENSITIVE);
	//private static final String SRC_LINK_STR = 
	//"(src|href)\\s*=\\s*([^'\"][^\\s\"<>\\|]+|'[^\\s'\"]+'|\"[^\\s\"]+\")|url\\s*\\(\\s*([^'\"][^\\s\"<>\\|]+|'[^\\s'\"]+'|\"[^\\s\"]+\")\\s*\\)";
	private static final String HREF_STR = 
	"href\\s*=\\s*([^'\"][^\\s\"'>]+|'[^'\"]+'|\"[^\"]+\")";
	
	private static final Pattern LINK_PATTTERN = Pattern.compile(HREF_STR, Pattern.CASE_INSENSITIVE);
	private static final Pattern BASE_PATTERN = Pattern.compile("<base[^<>]+?href\\s*=[^<>]+?>", Pattern.CASE_INSENSITIVE);
	private static final Pattern COMMENT_PATTERN = Pattern.compile("<!--[\u0000-\uffff]*?-->", Pattern.CASE_INSENSITIVE);
	private static final Pattern SCRIPT_PATTERN = Pattern.compile("<script[^>]*?>[\u0000-\uffff]*?</script>", Pattern.CASE_INSENSITIVE);
	
	private static final String CSS_PAT = "[^\"]*?[\\./]css[^\"]*?";
	private static final String JAVASCRIPT_PAT = "[^\"]*?[\\./]js[^\"]*?";
	
	private static final Pattern CSS_PATTERN = Pattern.compile(CSS_PAT, Pattern.CASE_INSENSITIVE);
	private static final Pattern JAVASCRIPT_PATTERN = Pattern.compile(JAVASCRIPT_PAT, Pattern.CASE_INSENSITIVE);
	private static float pixelsInDP = 0;
//	public static String[] returnImageUrlsFromHtml(String htmlCode) {
//        List<String> imageSrcList = new ArrayList<String>();
//        Pattern p = Pattern.compile("<img\\b[^>]*\\bsrc\\b\\s*=\\s*('|\")?([^'\"\n\r\f>]+(\\.jpe?g|\\.bmp|\\.eps|\\.gif|\\.mif|\\.miff|\\.png|\\.tif|\\.tiff|\\.svg|\\.wmf|\\.jpe|\\.webp|\\.dib|\\.ico|\\.tga|\\.cut|\\.pic|\\b)\\b)[^>]*>", Pattern.CASE_INSENSITIVE);
//        Matcher m = p.matcher(htmlCode);
//        String quote = null;
//        String src = null;
//        while (m.find()) {
//            quote = m.group(1);
//            src = (quote == null || quote.trim().length() == 0) ? m.group(2).split("//s+")[0] : m.group(2);
//            imageSrcList.add(src);
//        }
//        if (imageSrcList.size() == 0) {
//            return null;
//        }
//        return imageSrcList.toArray(new String[imageSrcList.size()]);
//    }
//	public class ImageJavascriptInterface {
//
//		private Context context;
//		private String[] imageUrls;    //Picture Collection
//
//		public ImageJavascriptInterface(Context context, String[] imageUrls) {
//			this.context = context;
//			this.imageUrls = imageUrls;
//		}
//
//		@JavascriptInterface
//		public void openImage(String image,int position) {
//			//todo jump to the picture view page
//		}
//	}
	
	
	private boolean FULL_INCOGNITO = Build.VERSION.SDK_INT >= 28;
	private boolean WEB_RTC = Build.VERSION.SDK_INT >= 21;
	private boolean THIRD_PARTY_COOKIE_BLOCKING = Build.VERSION.SDK_INT >= 21;
	
    private static final String searchUrl = "https://www.google.com/search?q=%s";
    static final String searchCompleteUrl = "https://www.google.com/complete/search?client=firefox&q=%s";
    private static final String androidPhoneUA = "Mozilla/5.0 (Linux; Android 9; Pixel 3) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/96.0.4664.104 Mobile Safari/537.36";//"Mozilla/5.0 (Linux; Android 11) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Mobile Safari/537.36";
    private static final String androidTabletUA = "Mozilla/5.0 (Linux; Android 7.1.1; Nexus 9 Build/N4F26M) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.90 Safari/537.36";//"Mozilla/5.0 (Linux; Android 11) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Mobile Safari/537.36";
    private static final String iOSPhoneUA = "Mozilla/5.0 (iPhone; CPU iPhone OS 12_0_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/12.0 Mobile/15E148 Safari/604.1";
    private static final String iOSTabletUA = "Mozilla/5.0 (iPad; CPU OS 12_0_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/12.0 Mobile/15E148 Safari/604.1";
    private static final String windowsPC = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36";
    private static final String macOSPC = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit/601.3.9 (KHTML, like Gecko) Version/9.0.2 Safari/601.3.9";
    private String userAgentString = androidPhoneUA;
	static final String[] adblockRulesList = {
            "https://easylist.to/easylist/easylist.txt",
            "https://easylist.to/easylist/easyprivacy.txt",
            "https://pgl.yoyo.org/adservers/serverlist.php?hostformat=hosts&showintro=1&mimetype=plaintext",    // Peter's
            "https://easylist.to/easylist/fanboy-social.txt",
            "https://easylist-downloads.adblockplus.org/advblock.txt",  // RU AdList
    };

    static final int FORM_FILE_CHOOSER = 1;

    static final int PERMISSION_REQUEST_EXPORT_BOOKMARKS = 1;
    static final int PERMISSION_REQUEST_IMPORT_BOOKMARKS = 2;
    static final int PERMISSION_REQUEST_EXPORT_FILTERS = 3;
    static final int PERMISSION_REQUEST_IMPORT_FILTERS = 4;
    static final int PERMISSION_REQUEST_DOWNLOAD = 5;
	static final int PERMISSION_REQUEST_READ_EXTERNAL = 6;
	
    ArrayList<Tab> tabs = new ArrayList<>();
    int currentTabIndex;
    private FrameLayout webviews;
	private View main_layout;
	private ViewGroup address;
	private ListView requestList;
	private ImageView faviconImage;
	private AutoCompleteTextView et;
	private View searchPane;
	private ImageView goStop;
	private ProgressBar progressBar;
	LinearLayout tabDialog;
	private ImageView undoCloseBtn;
	private ImageView newTabBtn;
	private ViewGroup toolbar;
    private boolean isNightMode;
    private boolean isFullscreen;
    private SharedPreferences prefs;
    private boolean useAdBlocker;
    private AdBlocker adBlocker;
    private boolean isLogRequests;
    private final View[] fullScreenView = new View[1];
    private final WebChromeClient.CustomViewCallback[] fullScreenCallback = new WebChromeClient.CustomViewCallback[1];
    private EditText searchEdit;
    private TextView searchCount;
	private TextView txtTabCount;
	private ImageView blockImagesImageView;
	private ImageView searchFindPrev;
	private ImageView searchFindNext;
	private ImageView searchClose;
	private ImageView searchClear;
	
	private boolean blockImages;
    private boolean blockMedia;
	private boolean isFullMenu;
    private boolean enableCookies;
	private boolean saveHistory;
    private boolean accept3PartyCookies;
	private boolean saveFormData;
	private boolean javaScriptEnabled;
	//private boolean pluginsEnabled;
	private boolean appCacheEnabled;
	private boolean mediaPlaybackRequiresUserGesture;
	private boolean loadWithOverviewMode;
	private boolean textReflow;
	private boolean domStorageEnabled;
	//private boolean enableSmoothTransition;
	private boolean geolocationEnabled;
	private int mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW;
	private boolean databaseEnabled;
	private boolean offscreenPreRaster;
	//private boolean savePassword;
	//private boolean supportMultipleWindows;
	//private boolean useWebViewBackgroundForOverscrollBackground;
	//private boolean navDump;
	//private boolean lightTouchEnabled;
	//private boolean useDoubleTree;
	private boolean allowFileAccess;
	private boolean allowContentAccess;
	private boolean allowFileAccessFromFileURLs;
	private boolean allowUniversalAccessFromFileURLs;
	private boolean blockNetworkLoads;
	private boolean javaScriptCanOpenWindowsAutomatically;
	private boolean blockFonts;
	private boolean blockCSS;
	private boolean blockJavaScript;
	private boolean autoHideToolbar;
	private boolean autoHideAddressbar;
	private int renderMode;
	private boolean removeIdentifyingHeaders;
	private String downloadLocation;
	int textColor;
	int backgroundColor;
	private String textEncoding;
	private String deleteAfter;
	private boolean saveImage;
	private boolean saveMedia;
	private boolean saveResources;
	private boolean keepTheScreenOn;
	private int cacheMode;
	private boolean isDesktop;
	private boolean requestSaveData;
	private boolean doNotTrack;
	private Paint paint = new Paint();
	
    private SQLiteDatabase placesDb;
	private PrintJob printJob;
	
    private ValueCallback<Uri[]> fileUploadCallback;
    private boolean fileUploadCallbackShouldReset;
	
	private boolean invertPage = false;
	//private WebViewHandler webViewHandler = new WebViewHandler(this);
    private int SCROLL_UP_THRESHOLD = 50;//dpToPx(10f);
	private float maxFling = 50;
	private GestureDetector gestureDetector;// = new GestureDetector(this, new CustomGestureListener());
	private boolean textChanged;
	private boolean userScriptEnabled;
	private ArrayList<UserScript> userScriptList;
	private boolean showHistoryInSpeedDial = true;
	static boolean autoLookup = false;
	private int restoreTabs;
    
	private Runnable swipeLeft = new FlingLeft();
	private Runnable swipeRight = new FlingRight();
	private boolean fullScreenshot = false;
	private boolean cacheOffline = false;
	private boolean popupMode = false;
	private ListView tabsListView;
	private ArrayAdapter<Tab> tabAdapter;
	private View tabView;
	
	private float[] negativeColorArray = new float[] {
		-1.0f, 0f, 0f, 0f, 255f, // red
		0f, -1.0f, 0f, 0f, 255f, // green
		0f, 0f, -1.0f, 0f, 255f, // blue
		0f, 0f, 0f, 1.0f, 0f // alpha
	};
	private float[] increaseContrastColorArray = new float[] {
		2.0f, 0f, 0f, 0f, -160f, // red
		0f, 2.0f, 0f, 0f, -160f, // green
		0f, 0f, 2.0f, 0f, -160f, // blue
		0f, 0f, 0f, 1.0f, 0f // alpha
	};
	private boolean secure;
	
	private OnFocusChangeListener tabsListViewFocusChange = new View.OnFocusChangeListener() {
		@Override
		public void onFocusChange(final View v, final boolean hasFocus) {
			//ExceptionLogger.d(TAG, "onFocusChange " + v + " hasFocus " + hasFocus);
			if (hasFocus && v != tabView && tabDialog != null) {
				tabDialog.setVisibility(View.GONE);
				if (v.getParent() != toolbar) {
					hideKeyboard();
				}
			}
		}
	};

	MenuActionArrayAdapter fullMenuActionAdapter;
	MenuActionArrayAdapter uaAdapter;
	@SuppressWarnings("unchecked")
    final MenuAction[] menuActions = new MenuAction[]{
		new MenuAction("Menu", R.drawable.menu, new Runnable() {
				@Override
				public void run() {
					showMenu();
				}
			}),
		new MenuAction("Full menu", R.drawable.menu, new Runnable() {
				@Override
				public void run() {
					isFullMenu = !isFullMenu;
					prefs.edit().putBoolean("isFullMenu", isFullMenu).apply();
					showMenu();
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return isFullMenu;
				}
			}),
		new MenuAction("Default Encoding", 0, new Runnable() {
				@Override
				public void run() {
					encodingDialog(true);
				}
			}),
		new MenuAction("Secure Screen (need restart)", 0, new Runnable() {
				@Override
				public void run() {
					secure = !secure;
					prefs.edit().putBoolean("secure", secure).apply();
//					Intent i = new Intent(MainActivity.this, MainActivity.class);
//					startActivity(i);
//					finish();
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return secure;
				}
			}),
		new MenuAction("Save Page", R.drawable.ic_action_save, new Runnable() {
				@Override
				public void run() {
					final WebView currentWebView = getCurrentWebView();
					saveWebArchive(currentWebView, null);
				}
			}),

		new MenuAction("Save Form Data", 0, new Runnable() {
				@Override
				public void run() {
					saveFormData = !saveFormData;
					prefs.edit().putBoolean("saveFormData", saveFormData).apply();
					for (Tab t : tabs) {
						if (!t.isIncognito)
							t.webview.getSettings().setSaveFormData(saveFormData);
					}
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return saveFormData;
				}
			}),
		new MenuAction("Find on page", R.drawable.find_on_page, new Runnable() {
				@Override
				public void run() {
					searchEdit.setText("");
					searchPane.setVisibility(View.VISIBLE);
					searchEdit.requestFocus();
					showKeyboard();
				}
			}),

		new MenuAction("Wide Mode", R.drawable.ua, new Runnable() {
				@Override
				public void run() {
					wideMode(true);
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return isDesktop;
				}
			}
		),

		new MenuAction("User Agent", R.drawable.ua, new Runnable() {
				@Override
				public void run() {
					setupUserAgentMenu(true);
				}
			}),

		new MenuAction("Do Not Track", 0, new Runnable() {
				@Override
				public void run() {
					doNotTrack = !doNotTrack;
					prefs.edit().putBoolean("doNotTrack", doNotTrack).apply();
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return doNotTrack;
				}
			}),
		new MenuAction("Request 'Save-Data'", 0, new Runnable() {
				@Override
				public void run() {
					requestSaveData = !requestSaveData;
					prefs.edit().putBoolean("requestSaveData", requestSaveData).apply();
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return requestSaveData;
				}
			}),
		new MenuAction("Remove Identifying Headers", R.drawable.ic_delete_white_36dp, new Runnable() {
				@Override
				public void run() {
					removeIdentifyingHeaders = !removeIdentifyingHeaders;
					prefs.edit().putBoolean("removeIdentifyingHeaders", removeIdentifyingHeaders).apply();
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return removeIdentifyingHeaders;
				}
			}),
		new MenuAction("Full screenshot (slow, new tab)", R.drawable.adblocker, new Runnable() {
				@Override
				public void run() {
					fullScreenshot = !fullScreenshot;
					prefs.edit().putBoolean("fullScreenshot", fullScreenshot).apply();
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
						&& fullScreenshot) {
						WebView.enableSlowWholeDocumentDraw();
					}
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return fullScreenshot;
				}
			}),
		new MenuAction("Cache Offline Browser", R.drawable.adblocker, new Runnable() {
				@Override
				public void run() {
					cacheOffline = !cacheOffline;
					prefs.edit().putBoolean("cacheOffline", cacheOffline).apply();
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return cacheOffline;
				}
			}),
		new MenuAction("Block CSS", R.drawable.adblocker, new Runnable() {
				@Override
				public void run() {
					blockCSS = !blockCSS;
					prefs.edit().putBoolean("blockCSS", blockCSS).apply();
					for (Tab t : tabs) {
						t.blockCSS = blockCSS;
					}
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return blockCSS;
				}
			}),
		new MenuAction("Block Fonts", R.drawable.adblocker, new Runnable() {
				@Override
				public void run() {
					blockFonts = !blockFonts;
					prefs.edit().putBoolean("blockFonts", blockFonts).apply();
					for (Tab t : tabs) {
						t.blockFonts = blockFonts;
					}
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return blockFonts;
				}
			}),
		new MenuAction("Block Images", R.drawable.ic_doc_image, new Runnable() {
				@Override
				public void run() {
					blockImages = !blockImages;
					prefs.edit().putBoolean("blockImages", blockImages).apply();
					for (Tab t : tabs) {
						t.blockImages = blockImages;
						//t.webview.getSettings().setBlockNetworkImage(blockImages);
						//t.webview.getSettings().setLoadsImagesAutomatically(!blockImages);
					}
					if (blockImages) {
						AndroidUtils.toast(MainActivity.this, "Blocked Images");
						blockImagesImageView.setImageResource(R.drawable.adblocker);
					} else {
						AndroidUtils.toast(MainActivity.this, "Unblocked Images");
						blockImagesImageView.setImageResource(R.drawable.ic_doc_image);
					}
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return blockImages;
				}
			}),
		new MenuAction("Block Media", R.drawable.adblocker, new Runnable() {
				@Override
				public void run() {
					blockMedia = !blockMedia;
					prefs.edit().putBoolean("blockMedia", blockMedia).apply();
					for (Tab t : tabs) {
						t.blockMedia = blockMedia;
					}
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return blockMedia;
				}
			}),
		new MenuAction("Block JavaScript", R.drawable.adblocker, new Runnable() {
				@Override
				public void run() {
					blockJavaScript = !blockJavaScript;
					prefs.edit().putBoolean("blockJavaScript", blockJavaScript).apply();
					for (Tab t : tabs) {
						t.blockJavaScript = blockJavaScript;
					}
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return blockJavaScript;
				}
			}),
		new MenuAction("Block Network Loads", R.drawable.adblocker, new Runnable() {
				@Override
				public void run() {
					blockNetworkLoads = !blockNetworkLoads;
					prefs.edit().putBoolean("blockNetworkLoads", blockNetworkLoads).apply();
					for (Tab t : tabs) {
						t.webview.getSettings().setBlockNetworkLoads(blockNetworkLoads);
					}
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return blockNetworkLoads;
				}
			}),
		new MenuAction("Ad Blocker", R.drawable.adblocker, new Runnable() {
				@Override
				public void run() {
					useAdBlocker = !useAdBlocker;
					initAdblocker();
					prefs.edit().putBoolean("adblocker", useAdBlocker).apply();
					AndroidUtils.toast(MainActivity.this, "Ad Blocker " + (useAdBlocker ? "enabled" : "disabled"));
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return useAdBlocker;
				}
			}),
		new MenuAction("Add Block Rules", R.drawable.adblocker, new Runnable() {
				@Override
				public void run() {
					addBlockRules(null);
				}
			}),
		new MenuAction("Update adblock rules", R.drawable.adblocker, new Runnable() {
				@Override
				public void run() {
					updateAdblockRules();
				}
			}),
		new MenuAction("Database Enabled", 0, new Runnable() {
				@Override
				public void run() {
					databaseEnabled = !databaseEnabled;
					prefs.edit().putBoolean("databaseEnabled", databaseEnabled).apply();
					for (Tab t : tabs) {
						if (!t.isIncognito)
							t.webview.getSettings().setDatabaseEnabled(databaseEnabled);
					}
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return databaseEnabled;
				}
			}),
		new MenuAction("App Cache Enabled", 0, new Runnable() {
				@Override
				public void run() {
					appCacheEnabled = !appCacheEnabled;
					prefs.edit().putBoolean("appCacheEnabled", appCacheEnabled).apply();
					for (Tab t : tabs) {
						if (!t.isIncognito)
							t.webview.getSettings().setAppCacheEnabled(appCacheEnabled);
					}
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return appCacheEnabled;
				}
			}),
		new MenuAction("DomStorage Enabled", 0, new Runnable() {
				@Override
				public void run() {
					domStorageEnabled = !domStorageEnabled;
					prefs.edit().putBoolean("domStorageEnabled", domStorageEnabled).apply();
					for (Tab t : tabs) {
						if (!t.isIncognito)
							t.webview.getSettings().setDomStorageEnabled(domStorageEnabled);
					}
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return domStorageEnabled;
				}
			}),
		new MenuAction("Geolocation Enabled", 0, new Runnable() {
				@Override
				public void run() {
					geolocationEnabled = !geolocationEnabled;
					prefs.edit().putBoolean("geolocationEnabled", geolocationEnabled).apply();
					for (Tab t : tabs) {
						if (!t.isIncognito)
							t.webview.getSettings().setGeolocationEnabled(geolocationEnabled);
					}
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return geolocationEnabled;
				}
			}),
		new MenuAction("Cookies Enabled", R.drawable.cookies, new Runnable() {
				@Override
				public void run() {
					enableCookies = !enableCookies;
					prefs.edit().putBoolean("enableCookies", enableCookies).apply();
					CookieManager.getInstance().setAcceptCookie(enableCookies);
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return CookieManager.getInstance().acceptCookie();
				}
			}),
		new MenuAction("3rd party cookies", R.drawable.cookies_3rdparty, new Runnable() {
				@Override
				public void run() {
					accept3PartyCookies = !accept3PartyCookies;
					prefs.edit().putBoolean("accept3PartyCookies", accept3PartyCookies).apply();
					final CookieManager instance = CookieManager.getInstance();
					for (Tab t : tabs) {
						if (!t.isIncognito)
							instance.setAcceptThirdPartyCookies(t.webview, accept3PartyCookies);
							//t.webview.getSettings().setAcceptThirdPartyCookies(accept3PartyCookies); //only for android-23
					}
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return accept3PartyCookies;
				}
			}),
		new MenuAction("JavaScript Enabled", 0, new Runnable() {
				@Override
				public void run() {
					javaScriptEnabled = !javaScriptEnabled;
					prefs.edit().putBoolean("javaScriptEnabled", javaScriptEnabled).apply();
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return javaScriptEnabled;
				}
			}),

		new MenuAction("UserScript Enabled", 0, new Runnable() {
				@Override
				public void run() {
					userScriptEnabled = !userScriptEnabled;
					prefs.edit().putBoolean("userScriptEnabled", userScriptEnabled).apply();
					for (Tab t : tabs) {
						t.userScriptEnabled = userScriptEnabled;
					}
					if (userScriptEnabled) {
						javaScriptEnabled = true;
						prefs.edit().putBoolean("javaScriptEnabled", javaScriptEnabled).apply();
					}
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return userScriptEnabled;
				}
			}),
		new MenuAction("Add UserScript", 0, new Runnable() {
				@Override
				public void run() {
					editUserScript("Add UserScript", null);
				}
			}),
		new MenuAction("UserScript List", 0, new Runnable() {
				@Override
				public void run() {
					if (placesDb == null)
						return;
					selectedItems = new ArrayList<>();
					cursor = placesDb.rawQuery("SELECT title, data, enabled, _id FROM userscripts", null);
					adapter = new SimpleCursorAdapter(MainActivity.this,
													  R.layout.userscript_list_item,
													  cursor,
													  new String[] { "title" },
													  new int[] { R.id.name }) {
						@Override
						public View getView(int position, View convertView, ViewGroup parent) {
							final UserScriptHolder holder;
							if (convertView == null) {
								convertView = super.getView(position, convertView, parent);
								holder = new UserScriptHolder(convertView);
							} else {
								holder = (UserScriptHolder) convertView.getTag();
							}
							holder.position = position;
							cursor.moveToPosition(position);
							holder.id = cursor.getInt(cursor.getColumnIndex("_id"));
							if (selectedItems.contains(holder.id)) {
								holder.iconView.setImageResource(R.drawable.ic_accept);
							} else {
								holder.iconView.setImageResource(R.drawable.dot);
							}
							final TextView titleView = holder.titleView;
							final String title = cursor.getString(cursor.getColumnIndex("title"));
							titleView.setText(title);
							final int enabled = cursor.getInt(cursor.getColumnIndex("enabled"));
							if (enabled != 1) {
								titleView.setTextColor(0xfff00000);
							} else {
								titleView.setTextColor(0xff00ffff);
							}
							return convertView;
						}
					};
					final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
						.setTitle("UserScript List")
						.setPositiveButton("OK", onClickDismiss)
						.setNegativeButton("Delete", new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								final int size = selectedItems.size();
								if (size > 0) {
									final StringBuilder sb = new StringBuilder();
									for (int i = 0; i < size; i++) {
										sb.append("?");
										if (i < size - 1) {
											sb.append(",");
										}
									}
									cursor.close();
									placesDb.execSQL("DELETE FROM userscripts WHERE _id IN (" + sb.toString()+ ")", selectedItems.toArray());
									adapter.swapCursor(cursor);
								}

							}})
						.setOnDismissListener(new OnDismissListener() {
							public void onDismiss(android.content.DialogInterface p1) {
								cursor.close();}})
						.setAdapter(adapter, new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								cursor.moveToPosition(which);
								boolean enabled = cursor.getInt(cursor.getColumnIndex("enabled")) == 1;
								String data = cursor.getString(cursor.getColumnIndex("data"));
								int id = cursor.getInt(cursor.getColumnIndex("_id"));
								UserScript userScript = new UserScript(id, data, enabled);
								userScript.name = cursor.getString(cursor.getColumnIndex("title"));
								cursor.close();
								editUserScript("Edit UserScript", userScript);
							}})
						.create();
					dialog.show();
				}
			}),
		new MenuAction("Popup Windows", 0, new Runnable() {
				@Override
				public void run() {
					javaScriptCanOpenWindowsAutomatically = !javaScriptCanOpenWindowsAutomatically;
					prefs.edit().putBoolean("javaScriptCanOpenWindowsAutomatically", javaScriptCanOpenWindowsAutomatically).apply();
					for (Tab t : tabs) {
						t.webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(javaScriptCanOpenWindowsAutomatically);
					}
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return javaScriptCanOpenWindowsAutomatically;
				}
			}),
		new MenuAction("Auto Hide Toolbar", 0, new Runnable() {
				@Override
				public void run() {
					autoHideToolbar = !autoHideToolbar;
					prefs.edit().putBoolean("autoHideToolbar", autoHideToolbar).apply();
					if (autoHideToolbar) {
						for (Tab t : tabs) {
							t.webview.setOnTouchListener(new TouchListener());
						}
					} else if (!autoHideToolbar && !autoHideAddressbar) {
						for (Tab t : tabs) {
							t.webview.setOnTouchListener(null);
						}
					}
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return autoHideToolbar;
				}
			}),
		new MenuAction("Auto Hide Addressbar", 0, new Runnable() {
				@Override
				public void run() {
					autoHideAddressbar = !autoHideAddressbar;
					prefs.edit().putBoolean("autoHideAddressbar", autoHideAddressbar).apply();
					if (autoHideAddressbar) {
						for (Tab t : tabs) {
							t.webview.setOnTouchListener(new TouchListener());
						}
					} else {
						address.setVisibility(View.VISIBLE);
						if (!autoHideToolbar)
							for (Tab t : tabs) {
							t.webview.setOnTouchListener(null);
						}
					}
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return autoHideAddressbar;
				}
			}),
		new MenuAction("Auto Lookup", 0, new Runnable() {
				@Override
				public void run() {
					autoLookup = !autoLookup;
					prefs.edit().putBoolean("autoLookup", autoLookup).apply();

				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return autoLookup;
				}
			}),
		new MenuAction("Show History In SpeedDial", 0, new Runnable() {
				@Override
				public void run() {
					showHistoryInSpeedDial = !showHistoryInSpeedDial;
					prefs.edit().putBoolean("showHistoryInSpeedDial", showHistoryInSpeedDial).apply();

				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return showHistoryInSpeedDial;
				}
			}),
		new MenuAction("Download Location", 0, new Runnable() {
				@Override
				public void run() {
					final ArrayList<MenuAction> actions = new ArrayList<>(2);
					final MenuActionArrayAdapter adapter = new MenuActionArrayAdapter(
						MainActivity.this,
						android.R.layout.simple_list_item_1,
						actions);
					actions.add(new MenuAction("Default", 0, new Runnable() {
										@Override
										public void run() {
											downloadLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
											prefs.edit().putString("downloadLocation", downloadLocation).apply();
											SCRAP_PATH = downloadLocation + "/sweb";
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return downloadLocation.equals(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
										}
									}));
					actions.add(new MenuAction("Custom", 0, new Runnable() {
										@Override
										public void run() {
											final EditText editView = new EditText(MainActivity.this);
											editView.setText(downloadLocation);
											editView.setSingleLine(true);
											editView.setSelection(downloadLocation.length());
											final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
												.setTitle("Edit Download Location")
												.setView(editView)
												.setPositiveButton("Apply", new OnClickListener() {
													public void onClick(DialogInterface dialog, int which) {
														String toString = editView.getText().toString().replaceAll("/{2,}", "/");
														if (toString.endsWith("/")) {
															toString = toString.substring(0, toString.lastIndexOf("/"));
														}
														if (FileUtil.ILLEGAL_FILE_CHARS.matcher(toString).matches()) {
															Toast.makeText(MainActivity.this, "Folder must not contain ?\\:*|\"<>#+%", Toast.LENGTH_LONG).show();
															return;
														}
														final File file = new File(toString);
														if (!file.exists()) {
															Toast.makeText(MainActivity.this, toString + " is not existed. Create New Ditectory", Toast.LENGTH_LONG).show();
															file.mkdirs();
														} else if (!file.isDirectory()) {
															Toast.makeText(MainActivity.this, toString + " is not a folder", Toast.LENGTH_LONG).show();
															return;
														} if (!file.canWrite()) {
															Toast.makeText(MainActivity.this, toString + " is read only", Toast.LENGTH_LONG).show();
															return;
														}
														downloadLocation = toString;
														prefs.edit().putString("downloadLocation", downloadLocation).apply();
														SCRAP_PATH = downloadLocation + "/sweb";
														adapter.notifyDataSetChanged();
													}})
												.setNegativeButton("Cancel", onClickDismiss)
												.show();
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return !downloadLocation.equals(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
										}
									}));
					
					AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
						.setPositiveButton("OK", onClickDismiss)
						.setTitle("Download Location")
						.create();
					ListView tv = new ListView(MainActivity.this);
					tv.setAdapter(adapter);
					dialog.setView(tv);
					dialog.show();
				}
			}),
		new MenuAction("Night mode", R.drawable.night, new Runnable() {
				@Override
				public void run() {
					isNightMode = !isNightMode;
					prefs.edit().putBoolean("night_mode", isNightMode).apply();
					onNightModeChange();
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return isNightMode;
				}
			}),
		new MenuAction("Show Log", R.drawable.url_bar, new Runnable() {
				@Override
				public void run() {
					Tab currentTab = getCurrentTab();
					if (requestList.getVisibility() == View.VISIBLE) {
						requestList.setVisibility(View.GONE);
						currentTab.showRequestList = false;
					} else {
						requestList.setVisibility(View.VISIBLE);
						currentTab.showRequestList = true;
						if (currentTab.logAdapter == null || currentTab.saveImage == true) {
							log(null, true);
							requestList.requestFocus();
						} else {
							log(null, false);
						}
					}
				}
			}),
		new MenuAction("Full screen", R.drawable.fullscreen, new Runnable() {
				@Override
				public void run() {
					isFullscreen = !isFullscreen;
					updateFullScreen();
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return isFullscreen;
				}
			}),
		new MenuAction("Tab history", R.drawable.left_right, new Runnable() {
				@Override
				public void run() {
					showTabHistory();
				}
			}),
		new MenuAction("Log requests", R.drawable.log_requests, new Runnable() {
				@Override
				public void run() {
					isLogRequests = !isLogRequests;
					prefs.edit().putBoolean("isLogRequests", isLogRequests).apply();
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return isLogRequests;
				}
			}),
//		new MenuAction("Show Log Requests", R.drawable.log_requests, new Runnable() {
//				@Override
//				public void run() {
////					StringBuilder sb = new StringBuilder("<title>Request Log</title><h1>Request Log</h1>");
////					for (String url : getCurrentTab().requestsLog) {
////						sb.append("<a href=\"");
////						sb.append(url);
////						sb.append("\">");
////						sb.append(url);
////						sb.append("</a><br><br>");
////					}
////					String base64 = Base64.encodeToString(sb.toString().getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
////					newBackgroundTab("data:text/html;base64," + base64, false);
////					switchToTab(tabs.size() - 1);
//					
//				}
//			}),
		new MenuAction("Media Playback Requires Gesture", 0, new Runnable() {
				@Override
				public void run() {
					mediaPlaybackRequiresUserGesture = !mediaPlaybackRequiresUserGesture;
					prefs.edit().putBoolean("mediaPlaybackRequiresUserGesture", mediaPlaybackRequiresUserGesture).apply();
					for (Tab t : tabs) {
						t.webview.getSettings().setMediaPlaybackRequiresUserGesture(mediaPlaybackRequiresUserGesture);
					}
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return mediaPlaybackRequiresUserGesture;
				}
			}),
		
		new MenuAction("Text Reflow", 0, new Runnable() {
				@Override
				public void run() {
					textReflow = !textReflow;
					prefs.edit().putBoolean("textReflow", textReflow).apply();
					Tab t = getCurrentTab();
					WebSettings settings = t.webview.getSettings();
					if (textReflow) {
						settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
						try {
							settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
						} catch (Exception e) {
							// This shouldn't be necessary, but there are a number
							// of KitKat devices that crash trying to set this
							ExceptionLogger.e("Problem setting LayoutAlgorithm to TEXT_AUTOSIZING", e.getMessage());
						}
					} else {
						settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
					}
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return textReflow;
				}
			}),
		new MenuAction("Offscreen PreRaster", 0, new Runnable() {
				@Override
				public void run() {
					offscreenPreRaster = !offscreenPreRaster;
					prefs.edit().putBoolean("offscreenPreRaster", offscreenPreRaster).apply();
					for (Tab t : tabs) {
						t.webview.getSettings().setOffscreenPreRaster(offscreenPreRaster);
					}
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return offscreenPreRaster;
				}
			}),
		new MenuAction("Load With Overview Mode", 0, new Runnable() {
				@Override
				public void run() {
					loadWithOverviewMode = !loadWithOverviewMode;
					prefs.edit().putBoolean("loadWithOverviewMode", loadWithOverviewMode).apply();
					for (Tab t : tabs) {
						t.webview.getSettings().setLoadWithOverviewMode(loadWithOverviewMode);
					}
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return loadWithOverviewMode;
				}
			}),
		new MenuAction("Access Mode", 0, new Runnable() {
				@Override
				public void run() {
					ArrayList<MenuAction> actions = new ArrayList<>(5);
					actions.add(new MenuAction("Allow File Access", 0, new Runnable() {
										@Override
										public void run() {
											allowFileAccess = !allowFileAccess;
											prefs.edit().putBoolean("allowFileAccess", allowFileAccess).apply();
											for (Tab t : tabs) {
												t.webview.getSettings().setAllowFileAccess(allowFileAccess);
											}
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return allowFileAccess;
										}
									}));
					actions.add(new MenuAction("Allow Content Access", 0, new Runnable() {
										@Override
										public void run() {
											allowContentAccess = !allowContentAccess;
											prefs.edit().putBoolean("allowContentAccess", allowContentAccess).apply();
											for (Tab t : tabs) {
												t.webview.getSettings().setAllowContentAccess(allowContentAccess);
											}
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return allowContentAccess;
										}
									}));
					actions.add(new MenuAction("Allow File Access From File URLs", 0, new Runnable() {
										@Override
										public void run() {
											allowFileAccessFromFileURLs = !allowFileAccessFromFileURLs;
											prefs.edit().putBoolean("allowFileAccessFromFileURLs", allowFileAccessFromFileURLs).apply();
											for (Tab t : tabs) {
												t.webview.getSettings().setAllowFileAccessFromFileURLs(allowFileAccessFromFileURLs);
											}
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return allowFileAccessFromFileURLs;
										}
									}));
					actions.add(new MenuAction("Allow Universal Access From File URLs", 0, new Runnable() {
										@Override
										public void run() {
											allowUniversalAccessFromFileURLs = !allowUniversalAccessFromFileURLs;
											prefs.edit().putBoolean("allowUniversalAccessFromFileURLs", allowUniversalAccessFromFileURLs).apply();
											for (Tab t : tabs) {
												t.webview.getSettings().setAllowUniversalAccessFromFileURLs(allowUniversalAccessFromFileURLs);
											}
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return allowUniversalAccessFromFileURLs;
										}
									}));
					
					AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
						.setPositiveButton("OK", onClickDismiss)
						.setTitle("Access Mode")
						.create();
					ListView tv = new ListView(MainActivity.this);
					MenuActionArrayAdapter adapter = new MenuActionArrayAdapter(
						MainActivity.this,
						android.R.layout.simple_list_item_1,
						actions);
					tv.setAdapter(adapter);
					dialog.setView(tv);
					dialog.show();
				}
			}),
		new MenuAction("Render Mode", 0, new Runnable() {
				@Override
				public void run() {
					ArrayList<MenuAction> actions = new ArrayList<>(5);
					actions.add(new MenuAction("NORMAL", 0, new Runnable() {
										@Override
										public void run() {
											renderMode(0);
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return renderMode == 0;
										}
									}));
					actions.add(new MenuAction("INVERTED", 0, new Runnable() {
										@Override
										public void run() {
											renderMode(1);
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return renderMode == 1;
										}
									}));
					actions.add(new MenuAction("GRAYSCALE", 0, new Runnable() {
										@Override
										public void run() {
											renderMode(2);
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return renderMode == 2;
										}
									}));
					actions.add(new MenuAction("INVERTED GRAYSCALE", 0, new Runnable() {
										@Override
										public void run() {
											renderMode(3);
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return renderMode == 3;
										}
									}));
					actions.add(new MenuAction("INCREASE CONTRAST", 0, new Runnable() {
										@Override
										public void run() {
											renderMode(4);
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return renderMode == 4;
										}
									}));

					AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
						.setPositiveButton("OK", onClickDismiss)
						.setTitle("Render Mode")
						.create();
					ListView tv = new ListView(MainActivity.this);
					MenuActionArrayAdapter adapter = new MenuActionArrayAdapter(
						MainActivity.this,
						android.R.layout.simple_list_item_1,
						actions);
					tv.setAdapter(adapter);
					dialog.setView(tv);
					dialog.show();
				}
			}),
		new MenuAction("Cache Mode", 0, new Runnable() {
				@Override
				public void run() {
					ArrayList<MenuAction> actions = new ArrayList<>(4);
					actions.add(new MenuAction("LOAD DEFAULT", 0, new Runnable() {
										@Override
										public void run() {
											cacheMode(WebSettings.LOAD_DEFAULT);
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return cacheMode == WebSettings.LOAD_DEFAULT;
										}
									}));
					actions.add(new MenuAction("LOAD CACHE ELSE NETWORK", 0, new Runnable() {
										@Override
										public void run() {
											cacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return cacheMode == WebSettings.LOAD_CACHE_ELSE_NETWORK;
										}
									}));
					actions.add(new MenuAction("LOAD NO CACHE", 0, new Runnable() {
										@Override
										public void run() {
											cacheMode(WebSettings.LOAD_NO_CACHE);
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return cacheMode == WebSettings.LOAD_NO_CACHE;
										}
									}));
					actions.add(new MenuAction("LOAD CACHE ONLY", 0, new Runnable() {
										@Override
										public void run() {
											cacheMode(WebSettings.LOAD_CACHE_ONLY);
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return cacheMode == WebSettings.LOAD_CACHE_ONLY;
										}
									}));

					AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
						.setPositiveButton("OK", onClickDismiss)
						.setTitle("Cache Mode")
						.create();
					ListView tv = new ListView(MainActivity.this);
					MenuActionArrayAdapter adapter = new MenuActionArrayAdapter(
						MainActivity.this,
						android.R.layout.simple_list_item_1,
						actions);
					tv.setAdapter(adapter);
					dialog.setView(tv);
					dialog.show();
				}
			}),
		
		new MenuAction("Back", R.drawable.back, new Runnable() {
				@Override
				public void run() {
					if (getCurrentWebView().canGoBack())
						getCurrentWebView().goBack();
				}
			}),
		new MenuAction("Forward", R.drawable.forward, new Runnable() {
				@Override
				public void run() {
					if (getCurrentWebView().canGoForward())
						getCurrentWebView().goForward();
				}
			}),
		new MenuAction("Scroll to top", R.drawable.top, new Runnable() {
				@Override
				public void run() {
					getCurrentWebView().pageUp(true);
				}
			}),
		new MenuAction("Scroll to bottom", R.drawable.bottom, new Runnable() {
				@Override
				public void run() {
					getCurrentWebView().pageDown(true);
				}
			}),
		new MenuAction("Save Images", 0, new Runnable() {
				@Override
				public void run() {
					saveImage = !saveImage;
					prefs.edit().putBoolean("saveImage", saveImage).apply();
					for (Tab t : tabs) {
						t.saveImage = saveImage;
					}
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return saveImage;
				}
			}),
		new MenuAction("Save Media", 0, new Runnable() {
				@Override
				public void run() {
					saveMedia = !saveMedia;
					prefs.edit().putBoolean("saveMedia", saveMedia).apply();
					for (Tab t : tabs) {
						t.saveMedia = saveMedia;
					}
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return saveMedia;
				}
			}),
		
		new MenuAction("Save Resources", 0, new Runnable() {
				@Override
				public void run() {
					saveResources = !saveResources;
					prefs.edit().putBoolean("saveResources", saveResources).apply();
					for (Tab t : tabs) {
						t.saveResources = saveResources;
					}
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return saveResources;
				}
			}),
		
		new MenuAction("Keep the screen on", 0, new Runnable() {
				@Override
				public void run() {
					keepTheScreenOn = !keepTheScreenOn;
					if (keepTheScreenOn) {
						getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
					} else {
						getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
					}
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return keepTheScreenOn;
				}
			}),

		new MenuAction("Keep History", R.drawable.ic_history_black_36dp, new Runnable() {
				@Override
				public void run() {
					saveHistory = !saveHistory;
					prefs.edit().putBoolean("saveHistory", saveHistory).apply();
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return saveHistory;
				}
			}),
		new MenuAction("Delete all history", R.drawable.ic_delete_white_36dp, new Runnable() {
				@Override
				public void run() {
					deleteAllHistory();
				}
			}),
		new MenuAction("Delete History After", R.drawable.ic_delete_white_36dp, new Runnable() {
				@Override
				public void run() {
					final EditText editView = new EditText(MainActivity.this);
					editView.setText(deleteAfter);
					editView.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
					editView.setSelection(deleteAfter.length());
					new AlertDialog.Builder(MainActivity.this)
						.setTitle("Delete History After Days")
						.setView(editView)
						.setPositiveButton("Apply", new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								deleteAfter = editView.getText().toString();
								prefs.edit().putString("deleteAfter", deleteAfter).apply();
								if (placesDb != null) 
									placesDb.execSQL("DELETE FROM history WHERE date_created < DATETIME('now', '-" + deleteAfter + " day')", new Object[] {});
							}})
						.setNegativeButton("Cancel", onClickDismiss)
						.show();
				}
			}),
		new MenuAction("Show History", R.drawable.ic_history_black_36dp, new Runnable() {
				@Override
				public void run() {
					showHistory();
				}
			}),
		new MenuAction("Show Bookmarks", R.drawable.bookmarks, new Runnable() {
				@Override
				public void run() {
					showBookmarks();
				}
			}),
		new MenuAction("Add bookmark", R.drawable.bookmark_add, new Runnable() {
				@Override
				public void run() {
					addBookmark();
				}
			}),
		new MenuAction("Export bookmarks", R.drawable.bookmarks_export, new Runnable() {
				@Override
				public void run() {
					exportBookmarks();
				}
			}),
		new MenuAction("Import bookmarks", R.drawable.bookmarks_import, new Runnable() {
				@Override
				public void run() {
					importBookmarks();
				}
			}),
		new MenuAction("Delete all bookmarks", R.drawable.ic_delete_white_36dp, new Runnable() {
				@Override
				public void run() {
					deleteAllBookmarks();
				}
			}),
		new MenuAction("Export custom filters", R.drawable.bookmarks_export, new Runnable() {
				@Override
				public void run() {
					exportFilters();
				}
			}),
		new MenuAction("Import custom filters", R.drawable.bookmarks_export, new Runnable() {
				@Override
				public void run() {
					importFilters();
				}
			}),
		new MenuAction("Delete all debug logs", R.drawable.ic_delete_white_36dp, new Runnable() {
				@Override
				public void run() {
					final File[] fs = externalLogFilesDir.listFiles();
					if (fs != null) {
						for (File f : fs) {
							if (!f.equals(ExceptionLogger.file))
								f.delete();
						}
						AndroidUtils.toast(MainActivity.this, "Finished delete all logs");
					}
				}
			}),
		
		new MenuAction("Clear history and cache", R.drawable.ic_delete_white_36dp, new Runnable() {
				@Override
				public void run() {
					new AlertDialog.Builder(MainActivity.this)
						.setTitle("Clear history and cache")
						.setItems(new String[] {"Clear history", "Clear cookies", "Clear cache", "Clear all"}, new OnClickListener() {
							public void onClick(DialogInterface subDialog, int which) {
								final WebView v = getCurrentWebView();
								switch (which) {
									case 0: {
										v.clearHistory();
										break;
										}
									case 1: {
										CookieManager.getInstance().removeAllCookies(null);
										break;
										}
									case 2:
										v.clearCache(true);
										v.clearFormData();
										break;
									case 3:
										v.clearCache(true);
										v.clearFormData();
										v.clearHistory();
										CookieManager.getInstance().removeAllCookies(null);
										WebStorage.getInstance().deleteAllData();
										break;
								}
							}})
						.show();
				}
			}),

		new MenuAction("Show tabs", R.drawable.tabs, new Runnable() {
				@Override
				public void run() {
					if (tabAdapter == null) {
						tabAdapter = new ArrayAdapterWithCurrentItemClose<Tab>(
							MainActivity.this,
							R.layout.tab_item,
							tabs);
						tabsListView.setAdapter(tabAdapter);
					} else {
						//updatePreviewImage(getCurrentWebView());
						if (tabAdapter != null) {
							tabAdapter.notifyDataSetChanged();
						}
					}
					showOpenTabs();
				}
			}),
		new MenuAction("New tab", R.drawable.tab_new, new Runnable() {
				@Override
				public void run() {
					newForegroundTab("", false, null);
					//switchToTab(tabs.size() - 1);
				}
			}),
		new MenuAction("Close tab", R.drawable.tab_close, new Runnable() {
				@Override
				public void run() {
					if (tabs.size() > 1) {
						closeTab(getCurrentWebView(), currentTabIndex, true);
					} else {
						newBackgroundTab("about:blank", false, null);
						closeTab(getCurrentWebView(), currentTabIndex, true);
					}
				}
			}),
		new MenuAction("Restore tabs on startup", R.drawable.tab_new, new Runnable() {
				@Override
				public void run() {
					ArrayList<MenuAction> actions = new ArrayList<>(5);
					actions.add(new MenuAction("No Restore", 0, new Runnable() {
										@Override
										public void run() {
											restoreTabs = 0;
											prefs.edit().putInt("restoreTabs", restoreTabs).apply();
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return restoreTabs == 0;
										}
									}));
					actions.add(new MenuAction("Restore", 0, new Runnable() {
										@Override
										public void run() {
											restoreTabs = 1;
											prefs.edit().putInt("restoreTabs", restoreTabs).apply();
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return restoreTabs == 1;
										}
									}));
					actions.add(new MenuAction("Ask First", 0, new Runnable() {
										@Override
										public void run() {
											restoreTabs = 2;
											prefs.edit().putInt("restoreTabs", restoreTabs).apply();
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return restoreTabs == 2;
										}
									}));
					
					final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
						.setPositiveButton("OK", onClickDismiss)
						.setTitle("Restore tabs")
						.create();
					final ListView tv = new ListView(MainActivity.this);
					final MenuActionArrayAdapter adapter = new MenuActionArrayAdapter(
						MainActivity.this,
						android.R.layout.simple_list_item_1,
						actions);
					tv.setAdapter(adapter);
					dialog.setView(tv);
					dialog.show();
				}
			}, new MyBooleanSupplier() {
				@Override
				public boolean getAsBoolean() {
					return restoreTabs == 1;
				}
			}),
	};

	private void setupUserAgentMenu(final boolean all) {
		final Tab currentTab = getCurrentTab();
		final ArrayList<MenuAction> actions = new ArrayList<>(9);
		final MenuActionArrayAdapter favAdapter = new MenuActionArrayAdapter(
			MainActivity.this,
			android.R.layout.simple_list_item_1,
			actions);
		actions.add(new MenuAction("Default", 0, new Runnable() {
							@Override
							public void run() {
								if (all) {
									userAgentString = currentTab.webview.getSettings().getDefaultUserAgent(MainActivity.this);
								} else {
									currentTab.userAgent = currentTab.webview.getSettings().getDefaultUserAgent(MainActivity.this);
								}
								setupUA(all);
							}
						}, new MyBooleanSupplier() {
							@Override
							public boolean getAsBoolean() {
								if (all) {
									return userAgentString.equals(currentTab.webview.getSettings().getDefaultUserAgent(MainActivity.this));
								} else {
									return currentTab.userAgent.equals(currentTab.webview.getSettings().getDefaultUserAgent(MainActivity.this));
								}
							}
						}));
		actions.add(new MenuAction("Android Phone", 0, new Runnable() {
							@Override
							public void run() {
								if (all) {
									userAgentString = androidPhoneUA;
								} else {
									currentTab.userAgent = androidPhoneUA;
								}
								setupUA(all);
							}
						}, new MyBooleanSupplier() {
							@Override
							public boolean getAsBoolean() {
								if (all) {
									return userAgentString.equals(androidPhoneUA);
								} else {
									return currentTab.userAgent.equals(androidPhoneUA);
								}
							}
						}));
		actions.add(new MenuAction("Android Tablet", 0, new Runnable() {
							@Override
							public void run() {
								if (all) {
									userAgentString = androidTabletUA;
								} else {
									currentTab.userAgent = androidTabletUA;
								}
								setupUA(all);
							}
						}, new MyBooleanSupplier() {
							@Override
							public boolean getAsBoolean() {
								if (all) {
									return userAgentString.equals(androidTabletUA);
								} else {
									return currentTab.userAgent.equals(androidTabletUA);
								}
							}
						}));
		actions.add(new MenuAction("iOS Phone", 0, new Runnable() {
							@Override
							public void run() {
								if (all) {
									userAgentString = iOSPhoneUA;
								} else {
									currentTab.userAgent = iOSPhoneUA;
								}
								setupUA(all);
							}
						}, new MyBooleanSupplier() {
							@Override
							public boolean getAsBoolean() {
								if (all) {
									return userAgentString.equals(iOSPhoneUA);
								} else {
									return currentTab.userAgent.equals(iOSPhoneUA);
								}
							}
						}));
		actions.add(new MenuAction("iOS Tablet", 0, new Runnable() {
							@Override
							public void run() {
								if (all) {
									userAgentString = iOSTabletUA;
								} else {
									currentTab.userAgent = iOSTabletUA;
								}
								setupUA(all);
							}
						}, new MyBooleanSupplier() {
							@Override
							public boolean getAsBoolean() {
								if (all) {
									return userAgentString.equals(iOSTabletUA);
								} else {
									return currentTab.userAgent.equals(iOSTabletUA);
								}
							}
						}));
		actions.add(new MenuAction("Windows PC", 0, new Runnable() {
							@Override
							public void run() {
								if (all) {
									userAgentString = windowsPC;
								} else {
									currentTab.userAgent = windowsPC;
								}
								setupUA(all);
							}
						}, new MyBooleanSupplier() {
							@Override
							public boolean getAsBoolean() {
								if (all) {
									return userAgentString.equals(windowsPC);
								} else {
									return currentTab.userAgent.equals(windowsPC);
								}
							}
						}));
		actions.add(new MenuAction("MacOS PC", 0, new Runnable() {
							@Override
							public void run() {
								if (all) {
									userAgentString = macOSPC;
								} else {
									currentTab.userAgent = macOSPC;
								}
								setupUA(all);
							}
						}, new MyBooleanSupplier() {
							@Override
							public boolean getAsBoolean() {
								if (all) {
									return userAgentString.equals(macOSPC);
								} else {
									return currentTab.userAgent.equals(macOSPC);
								}
							}
						}));
		actions.add(new MenuAction("Custom", 0, new Runnable() {
							@Override
							public void run() {
								final EditText editView = new EditText(MainActivity.this);
								if (all) {
									editView.setText(userAgentString);
								} else {
									editView.setText(currentTab.userAgent);
								}
								editView.setSelection(editView.getText().length());
								new AlertDialog.Builder(MainActivity.this)
									.setTitle("Custom Tablet User Agent")
									.setView(editView)
									.setPositiveButton("Apply", new OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											if (all) {
												userAgentString = editView.getText().toString();
											} else {
												currentTab.userAgent = editView.getText().toString();
											}
											favAdapter.notifyDataSetChanged();
											setupUA(all);
										}})
									.setNegativeButton("Cancel", onClickDismiss)
									.show();
							}
						}, new MyBooleanSupplier() {
							@Override
							public boolean getAsBoolean() {
								if (all) {
									return !userAgentString.equals(androidPhoneUA)
										&& !userAgentString.equals(androidTabletUA)
										&& !userAgentString.equals(iOSPhoneUA)
										&& !userAgentString.equals(iOSTabletUA)
										&& !userAgentString.equals(windowsPC)
										&& !userAgentString.equals(macOSPC)
										&& !userAgentString.equals(currentTab.webview.getSettings().getDefaultUserAgent(MainActivity.this));
								} else {
									return !currentTab.userAgent.equals(androidPhoneUA)
										&& !currentTab.userAgent.equals(androidTabletUA)
										&& !currentTab.userAgent.equals(iOSPhoneUA)
										&& !currentTab.userAgent.equals(iOSTabletUA)
										&& !currentTab.userAgent.equals(windowsPC)
										&& !currentTab.userAgent.equals(macOSPC)
										&& !currentTab.userAgent.equals(currentTab.webview.getSettings().getDefaultUserAgent(MainActivity.this));
								}
							}
						}));
		AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
			.setPositiveButton("OK", onClickDismiss)
			.setTitle("User Agent")
			.create();
		ListView tv = new ListView(MainActivity.this);
		tv.setAdapter(favAdapter);
		dialog.setView(tv);
		dialog.show();
	}

	private void wideMode(boolean all) {
		if (all) {
			isDesktop = !isDesktop;
			prefs.edit().putBoolean("isDesktop", isDesktop).apply();
			fullMenuActionAdapter.notifyDataSetChanged();
			for (Tab t : tabs) {
				WebSettings settings = t.webview.getSettings();
				settings.setUseWideViewPort(isDesktop);
				settings.setLoadWithOverviewMode(isDesktop);
				t.isDesktop = isDesktop;
				t.webview.reload();
			}
		} else {
			Tab tab = getCurrentTab();
			tab.isDesktop = !tab.isDesktop;
			uaAdapter.notifyDataSetChanged();
			WebView currentWebView = tab.webview;
			WebSettings settings = currentWebView.getSettings();
			settings.setUseWideViewPort(tab.isDesktop);
			settings.setLoadWithOverviewMode(isDesktop);
			currentWebView.reload();
		}
	}

	private void setupUA(boolean all) {
		if (all) {
			prefs.edit().putString("userAgentString", userAgentString).apply();
			fullMenuActionAdapter.notifyDataSetChanged();
			for (Tab t : tabs) {
				WebSettings settings = t.webview.getSettings();
				settings.setUserAgentString(userAgentString);//isDesktopUA ? desktopUA : androidUA);
				t.userAgent = userAgentString;
				t.webview.reload();
			}
		} else {
			uaAdapter.notifyDataSetChanged();
			Tab tab = getCurrentTab();
			WebView currentWebView = tab.webview;
			WebSettings settings = currentWebView.getSettings();
			settings.setUserAgentString(tab.userAgent);//tab.isDesktopUA ? desktopUA : androidUA);
			currentWebView.reload();
		}
	}

	private void encodingDialog(final boolean all) {
		final ArrayList<MenuAction> actions = new ArrayList<>(8);
		textEncoding(actions, all);

		final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
			.setPositiveButton("OK", onClickDismiss)
			.setTitle("Default Encoding")
			.create();
		final ListView tv = new ListView(MainActivity.this);
		final MenuActionArrayAdapter adapter = new MenuActionArrayAdapter(
			MainActivity.this,
			android.R.layout.simple_list_item_1,
			actions);
		tv.setAdapter(adapter);
		dialog.setView(tv);
		dialog.show();
	}

	private void textEncoding(final ArrayList<MenuAction> actions, final boolean all) {
		createEncodingMenu(actions, "ISO-8859-1", all);
		createEncodingMenu(actions, "UTF-8", all);
		createEncodingMenu(actions, "GBK", all);
		createEncodingMenu(actions, "Big5", all);
		createEncodingMenu(actions, "ISO-2022-JP", all);
		createEncodingMenu(actions, "SHIFT_JS", all);
		createEncodingMenu(actions, "EUC-JP", all);
		createEncodingMenu(actions, "EUC-KR", all);
	}

	private void createEncodingMenu(final ArrayList<MenuAction> actions, final String encode, final boolean all) {
		actions.add(new MenuAction(encode, 0, new Runnable() {
							@Override
							public void run() {
								if (all) {
									textEncoding = encode;
									prefs.edit().putString("textEncoding", textEncoding).apply();
									for (Tab t : tabs) {
										t.webview.getSettings().setDefaultTextEncodingName(textEncoding);
									}
								} else {
									getCurrentWebView().getSettings().setDefaultTextEncodingName(encode);
									getCurrentTab().textEncoding = textEncoding;
								}
							}
						}, new MyBooleanSupplier() {
							@Override
							public boolean getAsBoolean() {
								if (all)
									return encode.equals(textEncoding);
								else
									return encode.equals(getCurrentWebView().getSettings().getDefaultTextEncodingName());
							}
						}));
	}

	private void saveWebArchive(final WebView currentWebView, final ValueCallback<String> callback) {
		if (hasOrRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
								   null,
								   PERMISSION_REQUEST_DOWNLOAD)) {
			if (!currentWebView.getUrl().equals("about:blank")) {
				final String url = savedName(currentWebView);
				final String mhtUniqueName = getUniqueName(downloadLocation, url, ".mht");
				ExceptionLogger.d(TAG, "mhtUniqueName " + mhtUniqueName);
				if (callback != null) {
					currentWebView.saveWebArchive(mhtUniqueName, false, callback);
				} else {
					currentWebView.saveWebArchive(mhtUniqueName, false, new ValueCallback<String>() {
						@Override
						public void onReceiveValue(final String mhtUniqueName) {
							ExceptionLogger.d(TAG, "onReceiveValue1 " + mhtUniqueName);
							saveEpub(mhtUniqueName);
						}
					});
				}
				//AndroidUtils.toast(MainActivity.this, "Saved " + uniqueName);
			}
		}
	}

	private void cacheMode(final int mode) {
		cacheMode = mode;
		prefs.edit().putInt("cacheMode", cacheMode).apply();
		for (Tab t : tabs) {
			if (!t.isIncognito)
				t.webview.getSettings().setCacheMode(cacheMode);
		}
	}

	private void renderMode(final int which) {
		renderMode = which;
		prefs.edit().putInt("renderMode", renderMode).apply();
		for (Tab t : tabs) {
			setRenderMode(t.webview, which);
		}
	}
	
    final String[][] toolbarActions = {
		{"Back", "Scroll to top", "Tab history"},
		{"Forward", "Scroll to bottom", "Block Images"},
		{"Show Bookmarks", "Show History", "Add bookmark"},
		{"Save Page", "Night mode", "Full screen"},
		{"Show tabs", "New tab", "Close tab"},
		{"Menu", "Find on page", "Show Log"},
    };

    final String[] shortMenu = {
		"Full menu", "New tab", "User Agent", "Show History", "Tab history", "Show Log", 
		"Find on page", "Block Images", "Add bookmark", "Full screen", "Close tab"
    };

    MenuAction getAction(String name) {
        MenuAction action = MenuAction.actions.get(name);
        if (action == null) throw new IllegalArgumentException("name");
        return action;
    }

    static class TitleAndUrl {
        String title;
        String url;
    }

    static class TitleAndBundle {
        String title;
        Bundle bundle;
		boolean isIncognito;
    }

    private ArrayList<TitleAndBundle> closedTabs = new ArrayList<>();

    Tab getCurrentTab() {
        return tabs.get(currentTabIndex);
    }

    private CustomWebView getCurrentWebView() {
        return getCurrentTab().webview;
    }
	
	@JavascriptInterface
	public void showSource(final String tabId, String html, final String url) {
		ExceptionLogger.d(TAG, "showSource " + url);
		try {
			Tab tab = null;
			for (Tab t : tabs) {
				if (t.toString().equals(tabId)) {
					tab = t;
					break;
				}
			}
			if (tab == null) {
				return;
			}
			final Tab currentTab = tab;
			currentTab.source = html;
			final int idx = url.indexOf("://");
			int lastIndexOf = url.lastIndexOf("/");
			if (idx > 0 && lastIndexOf <= idx + 4) {
				lastIndexOf = url.length();
			}
			//https://connect.facebook.net/vi_VN
			final String dirOriUrl = url.substring(0, lastIndexOf);
			final int indexOf = url.indexOf("/", idx + 3);
			final String hostUrl = url.substring(0, indexOf > 0 ? indexOf : url.length());
			//https://connect.facebook.net
			ExceptionLogger.d(TAG, "dirOriUrl " + dirOriUrl
							  + ",\nhostUrl " + hostUrl);
			final StringBuffer sb = new StringBuffer();
			if (!currentTab.batchRunning) {
				saveHtml(currentTab, html, url);
			} else {
				final Matcher mat = LINK_PATTTERN.matcher(html);
				while (mat.find()) {
					ExceptionLogger.d(TAG, "mat.group() " + mat.group());
					String urlInside = mat.group(1);

					if (urlInside.endsWith("'") || urlInside.endsWith("\"")) {
						urlInside = urlInside.substring(1, urlInside.length() - 1);
					}

					//urlInside = Util.skipParam(urlInside, "?");
					urlInside = Util.skipParam(urlInside, "#");
					//urlInside = Util.decodeUrlToFS(urlInside);

					String newLink = "";
					ExceptionLogger.d(TAG, "urlInside " + urlInside);
					//link tuong doi
					if (urlInside.length() > 0) {//} && urlInside.indexOf(":") < 0){
						if (urlInside.startsWith("./")) {
							// thư mục hiện hành
							urlInside = urlInside.substring(1);
							newLink = dirOriUrl + urlInside;
						} else if (urlInside.startsWith("//")) {
							//src="//connect.facebook.net/vi_VN/sdk.js
							newLink = dirOriUrl.substring(0, dirOriUrl.indexOf(":") + 1) + urlInside;
							if (currentTab.saveHtml && currentTab.localization) {
								final int idxSlash = newLink.indexOf("://") + 3;
								urlInside = url.substring(idxSlash, url.lastIndexOf("/")+1).replaceAll("[^/]+?/", "../") + newLink.substring(idxSlash);
								if (urlInside.endsWith("/")) {
									urlInside = urlInside + "index.html";
								}
								urlInside = urlInside.replace("?", "@");
								mat.appendReplacement(sb, "href=\""+urlInside+"\"");
							}
						} else if (urlInside.startsWith("/")) {
							//href="/category/tin-tuc/"
							newLink = hostUrl + urlInside;
							if (currentTab.saveHtml && currentTab.localization) {
								final int idxSlash = newLink.indexOf("://") + 3;
								urlInside = url.substring(idxSlash, url.lastIndexOf("/")+1).replaceAll("[^/]+?/", "../") + newLink.substring(idxSlash);
								if (urlInside.endsWith("/")) {
									urlInside = urlInside + "index.html";
								}
								urlInside = urlInside.replace("?", "@");
								mat.appendReplacement(sb, "href=\""+urlInside+"\"");
							}
						} else if (urlInside.startsWith("../")) {
							// thư mục cha tương đối
							String tempEntryName = dirOriUrl;
							String urlInside2 = urlInside;
							while (urlInside2.startsWith("../")) {
								urlInside2 = urlInside2.substring("../".length());
								//ExceptionLogger.d(TAG, "tempEntryName " + tempEntryName);
								lastIndexOf = tempEntryName.lastIndexOf("/");
								if (lastIndexOf >= 0) {
									tempEntryName = tempEntryName.substring(0, lastIndexOf);
								} else {
									tempEntryName = "";
								}
							}
							if (tempEntryName.length() > 0) {
								newLink = tempEntryName + "/" + urlInside2;
							} else {
								newLink = urlInside2;
							}
						} else if (urlInside.indexOf(":/") > 0) {
							newLink = urlInside;
							if (currentTab.saveHtml && currentTab.localization) {
								final int idxSlash = newLink.indexOf("://") + 3;
								urlInside = url.substring(idxSlash, url.lastIndexOf("/")+1).replaceAll("[^/]+?/", "../") + newLink.substring(idxSlash);
								if (urlInside.endsWith("/")) {
									urlInside = urlInside + "index.html";
								}
								urlInside = urlInside.replace("?", "@");
								mat.appendReplacement(sb, "href=\""+urlInside+"\"");
							}
						} else {
							newLink = dirOriUrl + "/" + urlInside;
						}
						//ExceptionLogger.d(TAG, "crawlPattern " + currentTab.crawlPattern);
						//ExceptionLogger.d(TAG, "excludeCrawlPattern " + currentTab.excludeCrawlPattern);
						final boolean accept = Util.accept(newLink, currentTab.crawlPattern, currentTab.excludeCrawlPattern, currentTab.excludeLinkFirst);
						ExceptionLogger.d(TAG, "newLink " + newLink 
										  + ", accept " + accept
//											  + ", batchDownloadSet " + currentTab.batchDownloadSet.contains(newLink) 
//											  + ", batchDownloadedSet " + currentTab.batchDownloadedSet.contains(newLink)
										  );
						if (accept && (currentTab.level < 0 || (currentTab.level > 0 && currentTab.curLevel < currentTab.level))) {
							final CrawlerInfo crawlerInfo = new CrawlerInfo(newLink, currentTab.curLevel + 1);
							if (!currentTab.batchDownloadedSet.contains(crawlerInfo)
								&& !currentTab.batchDownloadSet.contains(crawlerInfo)
								&& newLink.length() < 5000) {
								if (currentTab.catchAMP && newLink.length() < 4995) {
									newLink += "/amp/";
								}
								currentTab.batchDownloadSet.add(crawlerInfo);
								ExceptionLogger.d(TAG, "newLink2 " + newLink);
							}
						}
					}
				}
				if (currentTab.saveHtml && currentTab.localization) {
					mat.appendTail(sb);
					html = sb.toString();
					html = BASE_PATTERN.matcher(html).replaceFirst("");
				}
				saveHtml(currentTab, html, url);
				if (currentTab.batchDownloadSet.size() > 0) {
					if (!currentTab.autoscroll) {
						requestList.post(new Runnable() {
								@Override
								public void run() {
									final CrawlerInfo ci = currentTab.batchDownloadSet.first();
									currentTab.lastUrl = ci.url;
									currentTab.curLevel = ci.level;
									currentTab.webview.loadUrl(currentTab.lastUrl);
								}
							});
					}
				} else {
					requestList.postDelayed(new Runnable() {
							@Override
							public void run() {
								currentTab.batchRunning = false;
								if (mWakeLock != null) {
									mWakeLock.release();
									mWakeLock = null;
								}
							}
						}, 6000);
					Toast.makeText(MainActivity.this, "Batch Download " + currentTab.batchLinkPatternStr + " finished", Toast.LENGTH_LONG).show();
				}
			}
		} catch (Throwable t) {
			ExceptionLogger.e(TAG, t.getMessage(), t);
		}
	}

	private void saveHtml(final Tab currentTab, String html, final String url) throws IOException {
		if (currentTab.saveHtml) {
			final int idx = url.indexOf("://");
			int lastIndexOf = url.lastIndexOf("/");
			if (idx > 0 && lastIndexOf <= idx + 4) {
				lastIndexOf = url.length();
			}
			//https://connect.facebook.net/vi_VN
			final String dirOriUrl = url.substring(0, lastIndexOf);
			final int indexOf = url.indexOf("/", idx + 3);
			final String hostUrl = url.substring(0, indexOf > 0 ? indexOf : url.length());
			if (currentTab.removeComment) {
				html = COMMENT_PATTERN.matcher(html).replaceAll("");
			}
			if (currentTab.removeJavascript) {
				//html = COMMENT_PATTERN.matcher(html).replaceAll("");
				html = SCRIPT_PATTERN.matcher(html).replaceAll("");
			}
			if (currentTab.replace.length() > 0) {
				for (int i = 0; i < currentTab.bys.length; i++) {
					html = currentTab.replacePat[i].matcher(html).replaceAll(currentTab.bys[i]);
				}
			}
			//final String path = url.substring(url.indexOf("//") + 1, url.lastIndexOf("/"));
			final String savedFilePath = SCRAP_PATH + dirOriUrl.substring(dirOriUrl.indexOf(":/")+2);//path;
			final String fileNameFromUrl = FileUtil.getFileNameFromUrl(url.endsWith("/") ? (url + "index.html") : url.equals(hostUrl) ? (url + "/index.html") : url, true);
			//final File file = new File(Util.decodeUrlToFS(savedFilePath), Util.decodeUrlToFS(fileNameFromUrl));
			//ExceptionLogger.d(TAG, "save exist " + file.exists() + ", " + file.getAbsolutePath());
			final String s = FileUtil.saveISToFile(new ByteArrayInputStream(html.getBytes()),
												   URLDecoder.decode(savedFilePath),
												   URLDecoder.decode(fileNameFromUrl),
												   false, false);
			ExceptionLogger.d(TAG, "saved successfully " + s);
		}
	}
	
	@SuppressLint({"SetJavaScriptEnabled", "DefaultLocale"})
    private CustomWebView createWebView(final Bundle bundle) {
        
		final CustomWebView webview = new CustomWebView(this);
		//webview.setOnFocusChangeListener(tabsListViewFocusChange);
		webview.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View p1, MotionEvent p2) {
					tabDialog.setVisibility(View.GONE);
					hideKeyboard();
					return false;
				}
			});
				
		if (bundle != null) {
            webview.restoreState(bundle);
        }
		webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webview.setBackgroundColor(isNightMode ? Color.BLACK : Color.WHITE);
        final WebSettings settings = webview.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        webview.setWebChromeClient(new WebChromeClient() {
				@Override
				public void onProgressChanged(final WebView view, final int newProgress) {
					super.onProgressChanged(view, newProgress);
					injectCSS(view);
					final Tab tabOfWebView = ((CustomWebView)view).tab;
					if (getCurrentTab() == tabOfWebView) {
						if (newProgress == 100) {
							progressBar.setVisibility(View.GONE);
						} else {
							progressBar.setProgress(newProgress);
						}
					}
				}

				@Override
				public void onShowCustomView(final View view, final CustomViewCallback callback) {
					fullScreenView[0] = view;
					fullScreenCallback[0] = callback;
					main_layout.setVisibility(View.INVISIBLE);
					ViewGroup fullscreenLayout = (ViewGroup) MainActivity.this.findViewById(R.id.fullScreenVideo);
					fullscreenLayout.addView(view);
					fullscreenLayout.setVisibility(View.VISIBLE);
				}

				@Override
				public void onHideCustomView() {
					if (fullScreenView[0] == null)
						return;
					final ViewGroup fullscreenLayout = (ViewGroup) MainActivity.this.findViewById(R.id.fullScreenVideo);
					fullscreenLayout.removeView(fullScreenView[0]);
					fullscreenLayout.setVisibility(View.GONE);
					fullScreenView[0] = null;
					fullScreenCallback[0] = null;
					main_layout.setVisibility(View.VISIBLE);
				}

				@Override
				public boolean onShowFileChooser(final WebView webView, final ValueCallback<Uri[]> filePathCallback, final FileChooserParams fileChooserParams) {
					if (!hasOrRequestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
												null,
												0)) {
						AndroidUtils.toast(MainActivity.this, "No permission to read file");
						fileUploadCallback = null;
						return false;
					}
					if (fileUploadCallback != null) {
						fileUploadCallback.onReceiveValue(null);
					}

					fileUploadCallback = filePathCallback;
					final Intent intent = fileChooserParams.createIntent();
					try {
						fileUploadCallbackShouldReset = true;
						startActivityForResult(intent, FORM_FILE_CHOOSER);
						return true;
					} catch (ActivityNotFoundException e) {
						// Continue below
					}

					// FileChooserParams.createIntent() copies the <input type=file> "accept" attribute to the intent's getType(),
					// which can be e.g. ".png,.jpg" in addition to mime-type-style "image/*", however startActivityForResult()
					// only accepts mime-type-style. Try with just */* instead.
					intent.setType("*/*");
					try {
						fileUploadCallbackShouldReset = false;
						startActivityForResult(intent, FORM_FILE_CHOOSER);
						return true;
					} catch (ActivityNotFoundException e) {
						// Continue below
					}

					// Everything failed, let user know
					AndroidUtils.toast(MainActivity.this, "Can't open file chooser");
					fileUploadCallback = null;
					return false;
				}
				
				@Override
				public void onReceivedTitle(final WebView view, final String title) {
					if (view.getVisibility() == View.VISIBLE) {
						if (!textChanged) {
							et.setTag(title);
							et.setText(title);
						}
					}
				}
				
				@Override
				public void onReceivedIcon(final WebView view, final Bitmap icon) {
					final Tab tab = ((CustomWebView)view).tab;
					if (icon != null) {
						try {
							final URL url = new URL(view.getUrl());
							final String host = url.getHost();
							final File fav = new File(externalFavFilesDir, host + ".webp");
							final FileOutputStream fos = new FileOutputStream(fav);
							final BufferedOutputStream bos = new BufferedOutputStream(fos);
							AndroidUtils.resize(icon, 96, 96).compress(Bitmap.CompressFormat.WEBP, 100, bos);
							FileUtil.flushClose(bos, fos);
						} catch (Throwable e) {
							ExceptionLogger.e(TAG, e.getMessage(), e);
						}
						if (view.getVisibility() == View.VISIBLE) {
							faviconImage.clearColorFilter();
							faviconImage.setImageBitmap(icon);
						}
					}
					tab.favicon = icon;
				}
				
//				@Override
//				public boolean onConsoleMessage(ConsoleMessage cm ) {
//					ExceptionLogger.d(TAG, cm.message() + " -- From line " + cm.lineNumber() + " of " + cm.sourceId());
//					return false ;
//				}
//				
//				@Override
//				public boolean onJsAlert(WebView view, String url, String message, JsResult result ) {
//					ExceptionLogger.d(TAG, " onJsAlert " + message);
//					return false ;
//				}
				
			});
//        String[] imgs = returnImageUrlsFromHtml(htmlData);
//		webview.addJavascriptInterface(new ImageJavascriptInterface(MainActivity.this, imgs), "click");
		webview.addJavascriptInterface(MainActivity.this, "HTMLOUT");
		webview.setWebViewClient(new WebViewClient() {
				@Override
				public void onPageStarted(final WebView view, String url, final Bitmap favicon) {
					final Tab tabOfWebView = ((CustomWebView)view).tab;
					if (url != null && tabOfWebView.listSite != null && tabOfWebView.md5File != null) {
						if (url.startsWith("file") && !url.endsWith(tabOfWebView.md5File+".html") && !url.endsWith(tabOfWebView.md5File+"_nopreview.html")) {
							String temp = url.substring("file://".length());
							if (!temp.startsWith(tabOfWebView.extractPath)) {
								url = "file://" + tabOfWebView.extractPath + temp;
							} else {
								temp = temp.substring(tabOfWebView.extractPath.length());
							}
							int i = 0;
							for (String s : tabOfWebView.listSite) {
								final String insideFileName = insideFileName(tabOfWebView, temp);
								if (s.equalsIgnoreCase(insideFileName)
									|| ("/"+s).equalsIgnoreCase(insideFileName)) {
									tabOfWebView.historyIndex = i;
									break;
								}
								i++;
							}
						}
					} else {
						applyUserScript(view, url, UserScript.RunAt.START);
					}
					super.onPageStarted(view, url, favicon);
					tabOfWebView.loading = true;
					tabOfWebView.started = true;
					tabOfWebView.favicon = null;
					tabOfWebView.printWeb = null;
					final boolean visible = view.getVisibility() == View.VISIBLE;
					ExceptionLogger.d(TAG, "onPageStarted " + url + ", VISIBLE " + visible);
					if (visible) {
						progressBar.setProgress(0);
						progressBar.setVisibility(View.VISIBLE);
						et.setTag(url);
						et.setText(url);
						goStop.setImageResource(R.drawable.stop);
						faviconImage.setImageResource(R.drawable.page_info);
						faviconImage.setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
						view.requestFocus();
					}
					//view.evaluateJavascript("window.alert = \"\";", null);
					injectCSS(view);
					if (tabAdapter != null)
						tabAdapter.notifyDataSetChanged();
				}

				@Override
				public void onPageFinished(final WebView view, final String url) {
					super.onPageFinished(view, url);
					ExceptionLogger.d(TAG, "onPageFinished " + url);
					final Tab tabOfWebView = ((CustomWebView)view).tab;
					tabOfWebView.loading = false;

					if (tabOfWebView.md5File != null) {
						//Utils.saveBookmark(tabOfWebView.extractPath, tabOfWebView.md5File, tabOfWebView.listBookmark);
						saveHistory(tabOfWebView);//.extractPath, t.md5File, t.historyIndex);
					}
					if (!url.equals(tabOfWebView.lastUrl)) {
						final CrawlerInfo crawlerInfo = new CrawlerInfo(tabOfWebView.lastUrl, tabOfWebView.curLevel);
						tabOfWebView.batchDownloadSet.remove(crawlerInfo);
						tabOfWebView.batchDownloadedSet.add(crawlerInfo);
					}
					final CrawlerInfo crawlerInfo = new CrawlerInfo(url, tabOfWebView.curLevel);
					tabOfWebView.batchDownloadSet.remove(crawlerInfo);
					tabOfWebView.batchDownloadedSet.add(crawlerInfo);
					
					if (view.getVisibility() == View.VISIBLE) {
						// Don't use the argument url here since navigation to that URL might have been
						// cancelled due to SSL error
						goStop.setImageResource(R.drawable.reload);
						if (et.isFocused()) {
							if (et.getText().toString().equals(view.getUrl()))
							// If user haven't started typing anything, focus on webview
								view.requestFocus();
						} else {
							et.setTag(view.getTitle());
							et.setText(view.getTitle());
							view.requestFocus();
						}
					}
//					String jsCode = "javascript:(function(){" +
//						"var imgs=document.getElementsByTagName(\"img\");" +
//						"for(var i=0;i<imgs.length;i++){" +
//						"imgs[i].pos = i;"+
//						"imgs[i].οnclick=function(){" +
//						"click.openImage(this.src,this.pos);" +
//						"}}})()";
//                    view.loadUrl(jsCode);
					tabOfWebView.printWeb = view;
					if (tabOfWebView.started
						&& !url.startsWith("data") // ulr has % is invalid urldecode to save file
						&& !url.equals("about:blank")
						&& !url.equals("file:///android_asset/")) {
						if (saveHistory 
							&& !tabOfWebView.isIncognito) {
							addHistory(tabOfWebView.webview, url);
						}
						tabOfWebView.started = false;
						applyUserScript(view, url, UserScript.RunAt.IDLE);
						if (tabOfWebView.autoscroll) {
							start(tabOfWebView);
						} else {
							final boolean javaScriptEnabled = tabOfWebView.javaScriptEnabled;
							view.getSettings().setJavaScriptEnabled(true);
							//view.evaluateJavascript("window.alert(\"ghj\");", null);
							view.loadUrl("javascript:window.HTMLOUT.showSource(\"" + tabOfWebView.toString() + "\", document.documentElement.outerHTML, \"" + url + "\")");
							ExceptionLogger.d(TAG, "javascript:window.HTMLOUT.showSource(\"" + tabOfWebView.toString() + "\", document.documentElement.outerHTML, \"" + url + "\")" + ", source.length " + tabOfWebView.source.length());
							view.getSettings().setJavaScriptEnabled(javaScriptEnabled);
						}
					}
					if (requestList.getVisibility() == View.VISIBLE
						&& view.getVisibility() == View.VISIBLE
						&& tabOfWebView.logAdapter != null) {
							if (tabOfWebView.logAdapter.showImages) {
								tabOfWebView.logAdapter.notifyDataSetChanged();
							} else {
								log("", false);
							}
					}
					if (tabOfWebView.openAndClose) {
						if (tabs.contains(tabOfWebView)) {
							if (tabs.size() > 1) {
								closeTab(view, tabs.indexOf(tabOfWebView), true);
							} else {
								newBackgroundTab("about:blank", false, null);
								closeTab(view, tabs.indexOf(tabOfWebView), true);
							}
						}
					}
					if (tabOfWebView.saveAndClose) {
						saveWebArchive(view, new ValueCallback<String>() {
								@Override
								public void onReceiveValue(final String uniqueName) {
									ExceptionLogger.d(TAG, "onReceiveValue2 " + uniqueName);
									saveEpub(uniqueName);
									if (tabs.contains(tabOfWebView)) {
										if (tabs.size() > 1) {
											closeTab(view, tabs.indexOf(tabOfWebView), true);
										} else {
											newBackgroundTab("about:blank", false, null);
											closeTab(view, tabs.indexOf(tabOfWebView), true);
										}
									}
								}
						});
					}
					injectCSS(view);
					//updatePreviewImage((CustomWebView)view);
					if (tabAdapter != null) {
						tabAdapter.notifyDataSetChanged();
					}
					//ExceptionLogger.d(TAG, "after injecting");
//					view.evaluateJavascript("", new ValueCallback<String>() {
//							@Override
//							public void onReceiveValue(String s) {
//								//ExceptionLogger.d("js", s);
//							}});
				}

//				@Override
//				public void onPageCommitVisible(final WebView view, final String url) {
//					super.onPageCommitVisible(view, url);
//					//ExceptionLogger.d(TAG, "onPageCommitVisible " + view + ", url " + url);
//					updatePreviewImage((CustomWebView)view);
//				}
				
				//@Override
				public void onDomContentLoaded(final WebView web) {
					applyJavascriptInjection(((CustomWebView)web).tab, web, web.getUrl());
				}

				@Override
				public void onReceivedHttpAuthRequest(WebView view, final HttpAuthHandler handler, String host, String realm) {
					new AlertDialog.Builder(MainActivity.this)
                        .setTitle(host)
                        .setView(R.layout.sweb_login_password)
                        .setCancelable(false)
						.setPositiveButton("OK", new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								final String username = ((EditText) ((Dialog) dialog).findViewById(R.id.username)).getText().toString();
								final String password = ((EditText) ((Dialog) dialog).findViewById(R.id.password)).getText().toString();
								handler.proceed(username, password);
							}})
						.setNegativeButton("Cancel", new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								handler.cancel();}}).show();
				}

				final WebResourceResponse emptyResponse = new WebResourceResponse("text/plain", "UTF-8", new ByteArrayInputStream(new byte[0]));
				
				String lastMainPage = "";
				@Override
				public WebResourceResponse shouldInterceptRequest(final WebView view, final WebResourceRequest request) {
					final Tab currentTab = ((CustomWebView)view).tab;
					try {
						final Uri url = request.getUrl();
						final String urlToString = url.toString();

						if (currentTab.utils != null && urlToString.startsWith("file") && !urlToString.endsWith(currentTab.md5File+".html") && !urlToString.endsWith(currentTab.md5File+"_nopreview.html")) {
							final String insideFileName = insideFileName(currentTab, urlToString);
							ExceptionLogger.d(TAG, "shouldInterceptRequest insideFileName " + insideFileName + ", url " + url);

							if (MainActivity.HTML_PATTERN.matcher(insideFileName).matches()
								|| MainActivity.IMAGES_PATTERN.matcher(insideFileName).matches()
								|| MainActivity.MEDIA_PATTERN.matcher(insideFileName).matches()) {
								try {
									final String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(FileUtil.getExtension(insideFileName));
									boolean ok = false;
									for (String s : currentTab.listSite) {
										if (s.equals(insideFileName) || ("/"+s).equals(insideFileName)) {
											ok = true;
											break;
										}
									}
									ExceptionLogger.d(TAG, "shouldInterceptRequest offline " + insideFileName + ", " + ok);
									if (!ok) {
										return emptyResponse;
									}
									final File file = new File(currentTab.extractPath + insideFileName);
									//ExceptionLogger.e(TAG, "passwordOK " + passwordOK);
									if (!file.exists() || (file.exists() && !currentTab.passwordOK)) {
										final InputStream resourceAsStream = currentTab.utils.chm.getResourceAsStream(insideFileName);
										currentTab.passwordOK = true;
										//ExceptionLogger.e(TAG, "passwordOK " + passwordOK);
										if (!file.exists() && (cacheOffline || currentTab.cacheMedia) && resourceAsStream != null) {
											FileUtil.is2File(resourceAsStream, file.getAbsolutePath());
										}
										return resourceAsStream != null ? new WebResourceResponse(mime, null, resourceAsStream) : emptyResponse;
									} else {
										//ExceptionLogger.e(TAG, "file.exists() && passwordOK " + passwordOK);
										return new WebResourceResponse(mime, null, new BufferedInputStream(new FileInputStream(file)));
									}
								} catch (PasswordRequiredException e) {
									enterPassword(currentTab, e);
									//return new WebResourceResponse("", "", new ByteArrayInputStream(new byte[0]));
								} catch (Throwable e) {
									ExceptionLogger.e(TAG, e.getMessage(), e);
								}
							} else {
								try {
									currentTab.utils.extractSpecificFile(currentTab.chmFilePath, currentTab.extractPath + insideFileName, insideFileName);
								} catch (PasswordRequiredException e) {
									enterPassword(currentTab, e);
								} catch (Throwable e) {
									ExceptionLogger.e(TAG, "Error extract file: " + insideFileName, e);
								} 
							}
							return super.shouldInterceptRequest(view, request);
						}

						if (isLogRequests) {
							currentTab.requestsLog.add(urlToString);
							if (currentTab.logAdapter != null) {
								currentTab.logAdapter.notifyDataSetChanged();
							}
						}
						if (currentTab.useAdBlocker) {//adBlocker != null) {
							if (request.isForMainFrame()) {
								lastMainPage = urlToString;
							}
							if (adBlocker.shouldBlock(url, lastMainPage)) {
								return emptyResponse;
							}
						}
						if (currentTab.batchRunning && request.isForMainFrame()) {
							final boolean accept = Util.accept(urlToString, currentTab.crawlPattern, currentTab.excludeCrawlPattern, currentTab.excludeLinkFirst);
							if (!accept) {//redirect
								final CrawlerInfo crawlerInfo = new CrawlerInfo(urlToString, currentTab.curLevel);
								if (!currentTab.batchDownloadedSet.contains(crawlerInfo)
									&& !currentTab.batchDownloadSet.contains(crawlerInfo)) {
									return emptyResponse;
								}
							}
						}

						final Map<String, String> requestHeaders = request.getRequestHeaders();
						requestHeaders.remove("Save-Data");
						if (currentTab.requestSaveData) {
							requestHeaders.put("Save-Data", "on");
						}

						requestHeaders.remove("DNT");
						if (currentTab.doNotTrack) {
							requestHeaders.put("DNT", "1");
						}

						requestHeaders.remove("X-Requested-With");
						requestHeaders.remove("X-Wap-Profile");
						if (currentTab.removeIdentifyingHeaders) {
							requestHeaders.put("X-Requested-With", "");
							requestHeaders.put("X-Wap-Profile", "");
						}

						final String scheme = url.getScheme();
						final String fileName = FileUtil.getFileNameFromUrl(urlToString, false);//url.getLastPathSegment(); có space
						ExceptionLogger.d(TAG, "getFileNameFromUrl " + fileName + ", urlToString = " + urlToString + ", isForMainFrame " + request.isForMainFrame() + ", saveHtml " + currentTab.saveHtml);
						if (request.isForMainFrame()) {
							if (//currentTab.saveHtml && fileName != null
							//&& 
								currentTab.batchRunning
								&& currentTab.update
							//&& ((currentTab.saveResources && (CSS_PATTERN.matcher(fileName).matches() || JAVASCRIPT_PATTERN.matcher(fileName).matches() || FONT_PATTERN.matcher(fileName).matches()))
							//|| (currentTab.saveImage && IMAGES_PATTERN.matcher(fileName).matches()))
								) {
								final String savedPath = getSavedFilePath(urlToString, SCRAP_PATH, true);
								final File file = new File(savedPath);
								ExceptionLogger.d(TAG, "Main.savedPath " + savedPath);
								//return PipeInputStream.getResponse(PipeInputStream.SAVE_AND_USE, urlToString, savedPath, null, requestHeaders);
								if (file.exists() && file.isFile()) {
									return new WebResourceResponse(
										MimeTypeMap.getSingleton().getMimeTypeFromExtension(FileUtil.getExtension(file.getName())),
										null,
										new BufferedInputStream(new FileInputStream(file)));
								}
							}
						} else if (fileName != null
								   &&
								   (scheme.startsWith("http")
								   || scheme.startsWith("ftp"))) {
							final boolean savePattern = Util.accept(urlToString, currentTab.includeResPattern, currentTab.excludeResPattern, currentTab.excludeResFirst);
							ExceptionLogger.d(TAG, "savePattern " + savePattern + ", saveImage " + currentTab.saveImage + ", includeResPattern " + currentTab.includeResPattern + ", excludeResPattern " + currentTab.excludeResPattern + ", excludeResFirst " + currentTab.excludeResFirst);
							if (IMAGES_PATTERN.matcher(fileName).matches()) {
								final Runnable updateUI = new Runnable() {
									@Override
									public void run() {
										final DownloadInfo downloadInfo = new DownloadInfo(urlToString, true);
										currentTab.downloadInfos.remove(downloadInfo);
//										currentTab.batchDownloadSet.remove(urlToString);
//										currentTab.batchDownloadedSet.add(urlToString);
										requestList.post(new Runnable() {
												@Override
												public void run() {
													if (!currentTab.downloadedInfos.contains(downloadInfo)) {
														currentTab.downloadedInfos.add(downloadInfo);
														if (currentTab.logAdapter != null && currentTab.logAdapter.showImages) {
															currentTab.logAdapter.notifyDataSetChanged();
														}
													}
												}
											});
									}
								};
								if (savePattern || currentTab.saveImage) {
									final boolean shouldAdd = currentTab.addImage(urlToString, currentTab.exactImageUrl);
									if (shouldAdd) {
										return PipeInputStream.getResponse(PipeInputStream.Mode.SAVE_AND_USE, urlToString, updateUI, requestHeaders);
									} else {
										return PipeInputStream.getResponse(PipeInputStream.Mode.USE_EXIST_ONLY, urlToString, updateUI, requestHeaders);
									}
								}
								if (currentTab.blockImages) {
									return PipeInputStream.getResponse(PipeInputStream.Mode.USE_EXIST_ONLY, urlToString, updateUI, requestHeaders);
								}
							} else if (savePattern) {
								//final String savedPath = getSavedFilePath(urlToString, SCRAP_PATH, true);
								return PipeInputStream.getResponse(PipeInputStream.Mode.SAVE_AND_USE, urlToString, null, requestHeaders);
							} else if (MEDIA_PATTERN.matcher(fileName).matches()) {
								if (currentTab.saveMedia) {
									//final String savedPath = getSavedFilePath(urlToString, SCRAP_PATH, true);
									return PipeInputStream.getResponse(PipeInputStream.Mode.SAVE_AND_USE, urlToString, null, requestHeaders);
								}
								if (currentTab.blockMedia) {
									//final String savedPath = getSavedFilePath(urlToString, SCRAP_PATH, true);
									return PipeInputStream.getResponse(PipeInputStream.Mode.USE_EXIST_ONLY, urlToString, null, requestHeaders);
									//return emptyResponse;
								}
							} else {
								final boolean cssMatches = CSS_PATTERN.matcher(fileName).matches();
								final boolean javascriptMatches = JAVASCRIPT_PATTERN.matcher(fileName).matches();
								final boolean fontMatches = FONT_PATTERN.matcher(fileName).matches();
								if (cssMatches || javascriptMatches || fontMatches) {
									if (currentTab.saveResources) {
										//final String savedPath = getSavedFilePath(urlToString, SCRAP_PATH, true);
										return PipeInputStream.getResponse(PipeInputStream.Mode.SAVE_AND_USE, urlToString, null, requestHeaders);
									}
									if (currentTab.blockCSS && cssMatches
										|| currentTab.blockJavaScript && javascriptMatches
										|| currentTab.blockFonts && fontMatches) {
										//final String savedPath = getSavedFilePath(urlToString, SCRAP_PATH, true);
										return PipeInputStream.getResponse(PipeInputStream.Mode.USE_EXIST_ONLY, urlToString, null, requestHeaders);
									}
								}
							}
						}
					} catch (Throwable t) {
						ExceptionLogger.e(TAG, t.getMessage(), t);
					}

					return super.shouldInterceptRequest(view, request);
				}
				
				@Override
				public boolean shouldOverrideUrlLoading(final WebView view, String url) {
					ExceptionLogger.d(TAG, "shouldOverrideUrlLoading " + url);
					// For intent:// URLs, redirect to browser_fallback_url if given
					final Tab currentTab = ((CustomWebView)view).tab;
					if (currentTab.utils != null) {
						if (url.startsWith("file") && !url.endsWith(currentTab.md5File+".html") && !url.endsWith(currentTab.md5File+"_nopreview.html")) {
							final String temp = url.substring("file://".length());
							if (!temp.startsWith(currentTab.extractPath)) {
								url = "file://" + currentTab.extractPath + temp;
								view.loadUrl(url);
								return true;
							}
						}
						return false;
					}
					if (url.startsWith("file")
						&&
						(CHMFile.CHM_PATTERN.matcher(url).matches()
						|| CompressedFile.SZIP_PATTERN.matcher(url).matches()
						|| CompressedFile.ZIP_PATTERN.matcher(url).matches()
						|| CompressedFile.ARCHIVE_PATTERN.matcher(url).matches()
						|| CompressedFile.TAR_PATTERN.matcher(url).matches()
						|| CompressedFile.COMPRESSOR_PATTERN.matcher(url).matches())) {
						ExceptionLogger.d(TAG, "shouldOverrideUrlLoading " + currentTab.chmFilePath);
						currentTab.chmFilePath = URLDecoder.decode(url.substring("file://".length()));
						initFile(currentTab);
						return true;
					} else if (url.startsWith("intent://")) {
						int start = url.indexOf(";S.browser_fallback_url=");
						if (start != -1) {
							start += ";S.browser_fallback_url=".length();
							int end = url.indexOf(';', start);
							if (end != -1 && end != start) {
								url = url.substring(start, end);
								url = Uri.decode(url);
								loadUrl(url, view);
								return true;
							}
						}
					}
					if (currentTab.batchRunning) {
						final boolean accept = Util.accept(url, currentTab.crawlPattern, currentTab.excludeCrawlPattern, currentTab.excludeLinkFirst);
						if (accept) {
							return false;
						} else {
							return true;
						}
					}
					return false;
				}
				
				//@Override
				public boolean shouldOverrideUrlLoading(final WebView view, final WebResourceRequest request) {
					return shouldOverrideUrlLoading(view, request.getUrl().toString());
				}
				
				@Override
				public void onLoadResource(final WebView view, String url) {
					ExceptionLogger.d(TAG, "onLoadResource " + url);
					final Tab tab = ((CustomWebView)view).tab;
					if (tab.md5File != null && url.startsWith("file") && !url.endsWith(tab.md5File+".html") && !url.endsWith(tab.md5File+"_nopreview.html")) {
						final String temp = url.substring("file://".length());
						if (!temp.startsWith(tab.extractPath)) {
							url = "file://" + tab.extractPath + temp;
						}
					}
					super.onLoadResource(view, url);
				}

				final String[] sslErrors = {"Not yet valid", "Expired", "Hostname mismatch", "Untrusted CA", "Invalid date", "Unknown error"};

				@Override
				public void onReceivedSslError(final WebView view, final SslErrorHandler handler, SslError error) {
					final int primaryError = error.getPrimaryError();
					final String errorStr = primaryError >= 0 && primaryError < sslErrors.length ? sslErrors[primaryError] : "Unknown error " + primaryError;
					new AlertDialog.Builder(MainActivity.this)
						.setTitle("Insecure connection")
						.setMessage(String.format("Error: %s\nURL: %s\n\nCertificate:\n%s",
												  errorStr, error.getUrl(), AndroidUtils.certificateToStr(error.getCertificate())))
						.setPositiveButton("Proceed", new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								handler.proceed();}})
						.setNegativeButton("Cancel", new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								handler.cancel();}})
						.show();
				}

				@Override
				public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
					super.onReceivedError(view, request, error);
					final String urlToString = request.getUrl().toString();
					ExceptionLogger.d(TAG, "onReceivedError " + error.getDescription() + ", code " + error.getErrorCode() + ", " + urlToString);
					final Tab currentTab = ((CustomWebView)view).tab;
					if (currentTab.batchDownloadSet.size() > 0) {
						if (!urlToString.equals(currentTab.lastUrl)) {
							final CrawlerInfo crawlerInfo = new CrawlerInfo(currentTab.lastUrl, currentTab.curLevel);
							currentTab.batchDownloadSet.remove(crawlerInfo);
							currentTab.batchDownloadedSet.add(crawlerInfo);
						}
						final CrawlerInfo crawlerInfo = new CrawlerInfo(urlToString, currentTab.curLevel);
						currentTab.batchDownloadedSet.add(crawlerInfo);
						currentTab.batchDownloadSet.remove(crawlerInfo);
						if (currentTab.batchDownloadSet.size() > 0) {
							final CrawlerInfo ci = currentTab.batchDownloadSet.first();
							currentTab.lastUrl = ci.url;
							currentTab.curLevel = ci.level;
							loadUrl(currentTab.lastUrl, currentTab.webview);
						}
					}
				}
        });
		
        webview.setOnLongClickListener(new View.OnLongClickListener() {
				public boolean onLongClick(View v) {
					String url = null, imageUrl = null, text = "";
					final WebView.HitTestResult r = ((WebView) v).getHitTestResult();
					final Handler handler = new Handler();
					final Message message = handler.obtainMessage();
					((WebView)v).requestFocusNodeHref(message);
					Bundle bundle = message.getData();
					switch (r.getType()) {
						case WebView.HitTestResult.SRC_ANCHOR_TYPE:
							url = r.getExtra();
							text = bundle.getString("title");
							if (text == null) {
								text = "";
							}
//							ExceptionLogger.d(TAG, "SRC_ANCHOR_TYPE.bundle: " + bundle + ", VALUE.size: " + bundle.size());
//							for (String key : bundle.keySet()) {
//								ExceptionLogger.d(TAG, "SRC_ANCHOR_TYPE.KEY: " + key + ", VALUE: " + bundle.get(key));
//							}
							break;
						case WebView.HitTestResult.IMAGE_TYPE: {
								imageUrl = r.getExtra();
								text = bundle.getString("title");
								if (text == null || text.length() == 0) {
									text = bundle.getString("alt");
									if (text == null)
										text = "";
								}
//								ExceptionLogger.d(TAG, "IMAGE_TYPE.bundle: " + bundle + ", VALUE.size: " + bundle.size());
//								for (String key : bundle.keySet()) {
//									ExceptionLogger.d(TAG, "IMAGE_TYPE.KEY: " + key + ", VALUE: " + bundle.get(key));
//								}
								break;
							}
						case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
						case WebView.HitTestResult.PHONE_TYPE:
						case WebView.HitTestResult.GEO_TYPE:
						case WebView.HitTestResult.EMAIL_TYPE:
						case WebView.HitTestResult.UNKNOWN_TYPE:
							url = bundle.getString("url");
							if ("".equals(url)) {
								url = null;
							}
							imageUrl = bundle.getString("src");
							if ("".equals(imageUrl)) {
								imageUrl = null;
							}
							if (url == null && imageUrl == null) {
								return false;
							}
							text = bundle.getString("title");
							if (text == null || text.length() == 0) {
								text = bundle.getString("alt");
								if (text == null)
									text = "";
							}
//							ExceptionLogger.d(TAG, "bundle: " + bundle + ", VALUE: " + bundle.size());
//							for (String key : bundle.keySet()) {
//								ExceptionLogger.d(TAG, "KEY: " + key + ", VALUE: " + bundle.get(key));
//							}
							break;
						default:
							return false;
					}
					showLongPressMenu(url, imageUrl, text);
					return true;
				}});
        webview.setDownloadListener(new DownloadListener() {
				public void onDownloadStart(final String url, final String userAgent, final String contentDisposition, final String mimetype, final long contentLength) {
					ExceptionLogger.d("onDownloadStart", url + ", userAgent " + userAgent + ", contentDisposition" + contentDisposition +", contentLength " + contentLength + ", mimetype " + mimetype);
					final String filename = FileUtil.getFileNameFromUrl(url, false);//URLUtil.guessFileName(url, contentDisposition, mimetype);
					final TextView editView = new TextView(MainActivity.this);
					editView.setText(String.format("File name: %s\nSize: %.2f MB\nURL: %s\nMime type: %s",
												   filename,
												   contentLength / 1024.0 / 1024.0,
												   url,
												   mimetype));
					editView.setPadding(28, 28, 28, 28);
					editView.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View p1) {
								AndroidUtils.copyClipboard(MainActivity.this, "Text", url);
							}
					});
					new AlertDialog.Builder(MainActivity.this)
						.setTitle("Download")
						.setView(editView)
						.setPositiveButton("Download", new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								startDownload(url, filename);}})
						.setNeutralButton("Open", new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								ExceptionLogger.d(TAG, "files-path " + MainActivity.this.getFilesDir());
								ExceptionLogger.d(TAG, "cache-path " + MainActivity.this.getCacheDir());
								ExceptionLogger.d(TAG, "external-path " + Environment.getExternalStorageDirectory());
								ExceptionLogger.d(TAG, "external-files-path " + MainActivity.this.getExternalFilesDir(null));
								ExceptionLogger.d(TAG, "external-cache-path " + MainActivity.this.getExternalCacheDir());
								ExceptionLogger.d(TAG, "external-media-path " + MainActivity.this.getExternalMediaDirs());
								
								if (url.startsWith("file://")) {
									final File newFile = new File(URLDecoder.decode(url.substring("file://".length()))); 
									final Uri contentUri = FileProvider.getUriForFile(MainActivity.this, "landau.sweb.fileprovider", newFile);
									final Intent intent = new Intent(Intent.ACTION_SEND);
									//i.setData(Uri.parse(url));
									intent.setData(contentUri);
									intent.setClipData(ClipData.newRawUri("From Sweb", contentUri)); 
									intent.addFlags( 
										Intent.FLAG_GRANT_READ_URI_PERMISSION
										| Intent.FLAG_GRANT_WRITE_URI_PERMISSION); 
									
									try {
										startActivity(intent);
									} catch (ActivityNotFoundException e) {
										new AlertDialog.Builder(MainActivity.this)
											.setTitle("Open")
											.setMessage("Can't open files of this type. Try downloading instead.")
											.setPositiveButton("OK", onClickDismiss)
											.show();
									}
								}
							}})
						.setNegativeButton("Cancel", onClickDismiss)
						.show();
				}});
        webview.setFindListener(new WebView.FindListener() {
				public void onFindResultReceived(final int activeMatchOrdinal, final int numberOfMatches, final boolean isDoneCounting) {
					searchCount.setText(numberOfMatches == 0 ? "Not found" :
										String.format("%d / %d", activeMatchOrdinal + 1, numberOfMatches));
				}});
        return webview;
    }

	static String getSavedFilePath(final String url, final String parentPath, final boolean includeQuestion) {
		final String path = URLDecoder.decode(url).substring(url.indexOf("//") + 1, url.lastIndexOf("/"));
		final String savedFilePath = parentPath + path;
		String fileNameFromUrl = FileUtil.getFileNameFromUrl(url, includeQuestion);
		if (fileNameFromUrl.length() == 0) {
			fileNameFromUrl = "index.html";
		}
		return savedFilePath + "/" + fileNameFromUrl;
	}
	
    void showLongPressMenu(final String linkUrl, final String imageUrl, final String text) {
        final Tab currentTab = getCurrentTab();
		final String url;
        final String title;
        if (currentTab.md5File != null) {
			String[] options = new String[]{"Add Bookmark", "Copy link text", "Copy link", "Show link", "Share link", "Download"};
			final String[] imageOptions = new String[]{
				"Add Bookmark", "Copy link text", "Copy link", "Show link", "Share link", "Download", 
				"Copy image link", "Show image link", "Share image link", "Download Image"};

			if (imageUrl == null) {
				if (linkUrl == null) {
					throw new IllegalArgumentException("Bad null arguments in showLongPressMenu");
				} else {
					// Text link
					url = linkUrl;
					title = linkUrl;
//					if (!url.startsWith("file://")) {
//						options = new String[]{"Open in background", "Open in new tab", "Add Bookmark", "Copy link text", "Copy link", "Show link", "Share link", "Download"};
//					}
				}
			} else {
				if (linkUrl == null) {
					// Image without link
					url = imageUrl;
					title = "Image: " + imageUrl;
				} else {
					// Image with link
					url = linkUrl;
					title = linkUrl;
					options = imageOptions;
//					if (!url.startsWith("file://")) {
//						options = new String[]{
//							"Open in background", "Open in new tab", "Add Bookmark", "Copy link text", "Copy link", "Show link", "Share link", "Download", 
//							"Copy image link", "Show image link", "Share image link", "Download Image"};
//					}
				}
			}
			final String downloadLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
			new AlertDialog.Builder(this)
				.setTitle(title)
				.setItems(options, new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int which) {
						switch (which) {
							case 0:
								if (url.startsWith("file://" + currentTab.extractPath)) {
									final String url2 = URLDecoder.decode(url).substring(("file://" + currentTab.extractPath).length() + 1);
									if (currentTab.listBookmark.indexOf(url2) == -1) {
										currentTab.listBookmark.add(url2);
										Utils.saveBookmark(currentTab.extractPath, currentTab.md5File, currentTab.listBookmark);
									} else {
										Toast.makeText(MainActivity.this, "Bookmark already exist", Toast.LENGTH_SHORT).show();
									}
								} else {
									Toast.makeText(MainActivity.this, "Can't bookmark external link", Toast.LENGTH_LONG).show();
								}
								break;
							case 1:
								AndroidUtils.copyClipboard(MainActivity.this, "Text", text.trim());
								break;
							case 2:
								AndroidUtils.copyClipboard(MainActivity.this, "URL", url);
								break;
							case 3:
								new AlertDialog.Builder(MainActivity.this)
									.setTitle("Full URL")
									.setMessage(url)
									.setPositiveButton("OK", onClickDismiss)
									.show();
								break;
							case 4:
								AndroidUtils.shareUrl(MainActivity.this, url);
								break;
							case 5:
								try {
									if (url.startsWith("file:/")) {
										final String insideFileName = insideFileName(currentTab, url);
										final InputStream resourceAsStream = currentTab.utils.chm.getResourceAsStream(insideFileName);
										final String name = insideFileName.substring(insideFileName.lastIndexOf("/") + 1);
										FileUtil.saveISToFile(resourceAsStream, downloadLocation, name, true, false);
										AndroidUtils.toast(MainActivity.this, "Saved " + downloadLocation + "/" + name);
									} else {
										Toast.makeText(MainActivity.this, "Can't save " + url, Toast.LENGTH_LONG).show();
									}
								} catch (Throwable e) {
									Toast.makeText(MainActivity.this, "Can't save " + url, Toast.LENGTH_LONG).show();
									ExceptionLogger.e(TAG, e.getMessage(), e);
								}
								break;
							case 6:
								AndroidUtils.copyClipboard(MainActivity.this, "URL", imageUrl);
								break;
							case 7:
								new AlertDialog.Builder(MainActivity.this)
									.setTitle("Full imageUrl")
									.setMessage(imageUrl)
									.setPositiveButton("OK", onClickDismiss)
									.show();
								break;
							case 8:
								AndroidUtils.shareUrl(MainActivity.this, imageUrl);
								break;
							case 9:
								try {
									if (imageUrl.startsWith("file:/")) {
										final String insideFileName = insideFileName(currentTab, imageUrl);
										final InputStream resourceAsStream = currentTab.utils.chm.getResourceAsStream(insideFileName);
										final String name = insideFileName.substring(insideFileName.lastIndexOf("/") + 1);
										FileUtil.saveISToFile(resourceAsStream, downloadLocation, name, true, false);
										AndroidUtils.toast(MainActivity.this, "Saved " + downloadLocation + "/" + name);
									} else {
										Toast.makeText(MainActivity.this, "Can't save " + imageUrl, Toast.LENGTH_LONG).show();
									}
								} catch (Throwable e) {
									Toast.makeText(MainActivity.this, "Can't save " + imageUrl, Toast.LENGTH_LONG).show();
									ExceptionLogger.e(TAG, e.getMessage(), e);
								}
								break;
						}
					}}).show();
			return;
		}
        String[] options = new String[]{"Open in background", "Open in new tab", "Add Bookmark", "Copy link text", "Copy link", "Show link", "Share link", "Download", "Open & Close", "Save & Close", "Block"};
		final String[] imageOptions = new String[]{
			"Open in background", "Open in new tab", "Add Bookmark", "Copy link text", "Copy link", "Show link", "Share link", "Download", "Open & Close", "Save & Close", "Block",
			"Open image in background", "Open image in new tab", "Copy image link", "Show image link", "Share image link", "Download image"};
		
        if (imageUrl == null) {
            if (linkUrl == null) {
                throw new IllegalArgumentException("Bad null arguments in showLongPressMenu");
            } else {
                // Text link
                url = linkUrl;
                title = linkUrl;
            }
        } else {
            if (linkUrl == null) {
                // Image without link
                url = imageUrl;
                title = "Image: " + imageUrl;
            } else {
                // Image with link
                url = linkUrl;
                title = linkUrl;
                options = imageOptions;
            }
        }
		new AlertDialog.Builder(MainActivity.this)
			.setTitle(title)
			.setItems(options, new OnClickListener() {
				public void onClick(final DialogInterface dialog, final int which) {
					switch (which) {
						case 0:
							Tab tab = newBackgroundTab(url, getCurrentTab().isIncognito, getCurrentTab());
							break;
						case 1:
							tab = newForegroundTab(url, getCurrentTab().isIncognito, getCurrentTab());
							break;
						case 2:
							addBookmark(url, title);
							break;
						case 3:
							AndroidUtils.copyClipboard(MainActivity.this, "Text", text.trim());
							break;
						case 4:
							AndroidUtils.copyClipboard(MainActivity.this, "URL", url);
							break;
						case 5:
							new AlertDialog.Builder(MainActivity.this)
								.setTitle("Full URL")
								.setMessage(url)
								.setPositiveButton("OK", onClickDismiss)
								.show();
							break;
						case 6:
							AndroidUtils.shareUrl(MainActivity.this, url);
							break;
						case 7:
							startDownload(url, null);
							break;
						case 8:
							newBackgroundTab(url, currentTab.isIncognito, currentTab).openAndClose = true;
//							CustomWebView webview = createWebView(null);
//							tab = newTabCommon(webview, false);
//							tab.copyTab(getCurrentTab());
//							tab.openAndClose = true;
//							loadUrl(url, webview);
							break;
						case 9:
							newBackgroundTab(url, currentTab.isIncognito, currentTab).saveAndClose = true;
//							webview = createWebView(null);
//							tab = newTabCommon(webview, false);
//							tab.copyTab(getCurrentTab());
//							tab.saveAndClose = true;
//							loadUrl(url, webview);
							break;
						case 10:
							if (!url.startsWith("/") && !url.startsWith("file")) {
								final Uri parse = Uri.parse(url);
								//ExceptionLogger.d(TAG, url + ", Authority " + parse.getAuthority() + ", Host " + parse.getHost() + ", LastPathSegment " + parse.getLastPathSegment() + ", Fragment " + parse.getFragment() + ", Path " + parse.getPath());
								addBlockRules(parse.getHost());
							}
							break;
						case 11:
							tab = newBackgroundTab(imageUrl, getCurrentTab().isIncognito, getCurrentTab());
							break;
						case 12:
							tab = newForegroundTab(imageUrl, getCurrentTab().isIncognito, getCurrentTab());
							break;
						case 13:
							AndroidUtils.copyClipboard(MainActivity.this, "URL", imageUrl);
							break;
						case 14:
							new AlertDialog.Builder(MainActivity.this)
								.setTitle("Full imageUrl")
								.setMessage(imageUrl)
								.setPositiveButton("OK", onClickDismiss)
								.show();
							break;
						case 15:
							AndroidUtils.shareUrl(MainActivity.this, imageUrl);
							break;
						case 16:
							startDownload(imageUrl, null);
							break;
					}
				}}).show();
	}

	private boolean startDownload(final String url, String filename) {
        if (!hasOrRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
									null,
									PERMISSION_REQUEST_DOWNLOAD)) {
            return false;
        }
        if (filename == null) {
            filename = URLUtil.guessFileName(url, null, null);
        }
        final DownloadManager.Request request;
        try {
            request = new DownloadManager.Request(Uri.parse(url));
		ExceptionLogger.d(TAG, "startDownload " + downloadLocation + "/" + filename);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		File fileResult = new File(downloadLocation, filename);
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
			request.setDestinationUri(Uri.fromFile(fileResult));
		} else {
			request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
			fileResult = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
		}
		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
									   | DownloadManager.Request.NETWORK_MOBILE)
			.setAllowedOverRoaming(false)
			.setTitle("Download")
			.setDescription("Downloading...");
		final String cookie = CookieManager.getInstance().getCookie(url);
        if (cookie != null) {
            request.addRequestHeader("Cookie", cookie);
        }
        final DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        assert dm != null;
        getCurrentTab().lastDownload = dm.enqueue(request);
			MediaScannerConnection.scanFile(MainActivity.this, new String[]{fileResult.getAbsolutePath()}, null,
			new MediaScannerConnection.OnScanCompletedListener() {
				public void onScanCompleted(String path, Uri uri) {
					ExceptionLogger.i(TAG, "Scanned " + path + ":");
				}
			});
		} catch (Throwable e) {
            new AlertDialog.Builder(MainActivity.this)
				.setTitle("Can't Download URL")
				.setMessage(url)
				.setPositiveButton("OK", onClickDismiss)
				.show();
            return false;
        }
		return true;
    }

	private void queryStatus(final View v, final long lastDownload) {
		final DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		final Cursor c = dm.query(new DownloadManager.Query().setFilterById(lastDownload));
		if (c == null) {
			AndroidUtils.toast(MainActivity.this, "Download Not Found");
		} else {
			c.moveToFirst();
			final String name = getClass().getName();
			ExceptionLogger.d(name,
				  "COLUMN_ID: "
				  + c.getLong(c.getColumnIndex(DownloadManager.COLUMN_ID)));
			ExceptionLogger.d(name,
				  "COLUMN_BYTES_DOWNLOADED_SO_FAR: "
				  + c.getLong(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)));
			ExceptionLogger.d(name,
				  "COLUMN_LAST_MODIFIED_TIMESTAMP: "
				  + c.getLong(c.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP)));
			ExceptionLogger.d(name,
				  "COLUMN_LOCAL_URI: "
				  + c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
			ExceptionLogger.d(name,
				  "COLUMN_STATUS: "
				  + c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS)));
			ExceptionLogger.d(name,
				  "COLUMN_REASON: "
				  + c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON)));
			AndroidUtils.toast(MainActivity.this, statusMessage(c));
			c.close();
		}
	}

	private String statusMessage(final Cursor c) {
		String msg="???";
		switch (c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
			case DownloadManager.STATUS_FAILED:
				msg = "Download failed";
				break;
			case DownloadManager.STATUS_PAUSED:
				msg = "Download paused";
				break;
			case DownloadManager.STATUS_PENDING:
				msg = "Download pending";
				break;
			case DownloadManager.STATUS_RUNNING:
				msg = "Download in progress";
				break;
			case DownloadManager.STATUS_SUCCESSFUL:
				msg = "Download complete";
				break;
			default:
				msg = "Download is nowhere in sight";
				break;
		}
		return msg;
	}
  
	private final BroadcastReceiver onEvent = new BroadcastReceiver() {
		public void onReceive(final Context ctxt, final Intent i) {
			if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(i.getAction())) {
				queryStatus(toolbar, i.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));
			} else if (getCurrentTab().sourceName != null) {
				final long downloadId = i.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
				final Tab currentTab = getCurrentTab();
				if (currentTab.lastDownload == downloadId) {
					final String toString = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + currentTab.sourceName)).toString();
					ExceptionLogger.d(TAG, getCurrentTab().sourceName + ", " + toString);
					loadUrl(toString, currentTab.webview);
					currentTab.sourceName = null;
				}
			}
		}
	};
	
    private Tab newTabCommon(final CustomWebView webview, boolean isIncognito) {
        final WebSettings settings = webview.getSettings();
		settings.setUserAgentString(userAgentString);//isDesktopUA ? desktopUA : androidUA);
        settings.setUseWideViewPort(isDesktop);
		
		settings.setAllowContentAccess(allowContentAccess);
		settings.setMediaPlaybackRequiresUserGesture(mediaPlaybackRequiresUserGesture);
		settings.setLoadWithOverviewMode(loadWithOverviewMode);
		settings.setMixedContentMode(mixedContentMode);
		settings.setOffscreenPreRaster(offscreenPreRaster);
		settings.setAppCachePath(getExternalFilesDir("cache").getAbsolutePath());
		settings.setDatabasePath(getExternalFilesDir("db").getAbsolutePath());
		
		settings.setAllowFileAccess(allowFileAccess);
		settings.setAllowFileAccessFromFileURLs(allowFileAccessFromFileURLs);
		settings.setAllowUniversalAccessFromFileURLs(allowUniversalAccessFromFileURLs);
		settings.setBlockNetworkLoads(blockNetworkLoads);
		settings.setDefaultTextEncodingName(textEncoding);
		if (autoHideToolbar) {
			webview.setOnTouchListener(new TouchListener());
		}
		if (autoHideAddressbar) {
			webview.setOnTouchListener(new TouchListener());
		}
		setRenderMode(webview, renderMode);
		webview.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        webview.setVisibility(View.GONE);
		//enableWVCache(webview);
		
        final Tab tab = new Tab(webview, isIncognito);
		tab.userAgent = MainActivity.this.userAgentString;
		tab.blockCSS = blockCSS;
        tab.blockFonts = blockFonts;
        tab.blockImages = blockImages;
        tab.blockMedia = blockMedia;
        tab.blockJavaScript = blockJavaScript;
        tab.blockNetworkLoads = blockNetworkLoads;
		tab.saveMedia = saveMedia;
        tab.saveResources = saveResources;
        tab.saveImage = saveImage;
		tab.isDesktop = isDesktop;
		tab.javaScriptEnabled = javaScriptEnabled;
		tab.loadWithOverviewMode = loadWithOverviewMode;
		tab.useAdBlocker = useAdBlocker;
		tab.offscreenPreRaster = offscreenPreRaster;
		tab.requestSaveData = requestSaveData;
		tab.doNotTrack = doNotTrack;
		tab.javaScriptCanOpenWindowsAutomatically = javaScriptCanOpenWindowsAutomatically;
		tab.removeIdentifyingHeaders = removeIdentifyingHeaders;
		tab.textReflow = textReflow;
		tab.userScriptEnabled = userScriptEnabled;
        
		if (tab.isIncognito) {
			CookieManager.getInstance().setAcceptThirdPartyCookies(webview, false);
			settings.setAppCacheEnabled(false);
			settings.setDomStorageEnabled(false);
			settings.setDatabaseEnabled(false);
			settings.setGeolocationEnabled(false);
			settings.setSaveFormData(false);
			settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
			CookieManager.getInstance().setAcceptCookie(false);
//			final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//			int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, displayMetrics);
//            final Drawable left = getResources().getDrawable(R.drawable.ic_notification_incognito, null);
//            left.setBounds(0, 0, size, size);
//            et.setCompoundDrawables(left, null, null, null);
//            et.setCompoundDrawablePadding(
//				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, displayMetrics));
		} else {
			tab.accept3PartyCookies = accept3PartyCookies;
			CookieManager.getInstance().setAcceptThirdPartyCookies(webview, accept3PartyCookies);
			settings.setAppCacheEnabled(appCacheEnabled);
			settings.setDomStorageEnabled(domStorageEnabled);
			settings.setDatabaseEnabled(databaseEnabled);
			settings.setGeolocationEnabled(geolocationEnabled);
			settings.setSaveFormData(saveFormData);
			settings.setCacheMode(cacheMode);
			tab.enableCookies = enableCookies;
			CookieManager.getInstance().setAcceptCookie(enableCookies);
		}
		tabs.add(tabs.size()==0?0:currentTabIndex+1, tab);
        webviews.addView(webview);
        setTabCountText(tabs.size());
		return tab;
    }
	
    private Tab newBackgroundTab(final String url, final boolean isIncognito, final Tab srcTab) {
        final CustomWebView webview = createWebView(null);
        final Tab tab = newTabCommon(webview, isIncognito);
        if (srcTab != null) {
			tab.copyTab(srcTab);
		}
        loadUrl(url, webview);
		return tab;
    }

    Tab newForegroundTab(final String url, final boolean isIncognito, final Tab srcTab) {
        final Tab tab = newBackgroundTab(url, isIncognito, srcTab);
		switchToTab(tabs.size()==0?0:currentTabIndex+1);
		return tab;
    }

    private Tab newTabFromBundle(final Bundle bundle, boolean isIncognito) {
        final CustomWebView webview = createWebView(bundle);
        final Tab tab = newTabCommon(webview, isIncognito);
		return tab;
    }

    void switchToTab(final int tab) {
		final CustomWebView wv = getCurrentWebView();
		//updatePreviewImage(wv);
        wv.setVisibility(View.GONE);
        currentTabIndex = tab;
        final Tab currentTab = getCurrentTab();
		currentTab.webview.setVisibility(View.VISIBLE);
		if (currentTab.sourceName != null) {
			final String toString = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + currentTab.sourceName)).toString();
			//ExceptionLogger.d(TAG, getCurrentTab().sourceName + ", " + toString);
			loadUrl(toString, currentTab.webview);
			currentTab.sourceName = null;
			faviconImage.setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
			faviconImage.setImageResource(R.drawable.page_info);
			return;
		}
		if (currentTab.favicon != null) {
			faviconImage.clearColorFilter();
			faviconImage.setImageBitmap(currentTab.favicon);
		} else {
			faviconImage.setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
			faviconImage.setImageResource(R.drawable.page_info);
		}
		if (currentTab.loading) {
      		et.setTag(currentTab.webview.getUrl());
			et.setText(currentTab.webview.getUrl());
			goStop.setImageResource(R.drawable.stop);
			progressBar.setVisibility(View.VISIBLE);
			progressBar.setProgress(currentTab.webview.getProgress());
		} else {
			et.setTag(currentTab.webview.getTitle());
			et.setText(currentTab.webview.getTitle());
			goStop.setImageResource(R.drawable.reload);
			progressBar.setVisibility(View.GONE);
		}
		if (currentTab.isIncognito) {
			final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
			final int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, displayMetrics);
            final Drawable left = getResources().getDrawable(R.drawable.ic_notification_incognito, null);
			left.setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
            left.setBounds(0, 0, size, size);
            et.setCompoundDrawables(left, null, null, null);
            et.setCompoundDrawablePadding(
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, displayMetrics));
		} else {
			et.setCompoundDrawables(null, null, null, null);
		}
		//hideKeyboard();
		currentTab.webview.requestFocus();
		requestList.setAdapter(currentTab.logAdapter);
		if (currentTab.showRequestList) {
			requestList.setVisibility(View.VISIBLE);
			currentTab.logAdapter.notifyDataSetChanged();
			requestList.requestFocus();
		} else {
			requestList.setVisibility(View.GONE);
		}
		toolbar.setVisibility(View.VISIBLE);
		//updatePreviewImage(currentTab.webview);
		if (tabAdapter != null) {
			tabAdapter.notifyDataSetChanged();
		}
	}

//	private void updatePreviewImage(final CustomWebView wv) {
////		if (wv.tab.loading) {
////			return;
////      	}
////		wv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
////				   View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//		//wv.layout(0, 0, wv.getMeasuredWidth(), wv.getMeasuredHeight());
//		//final Bitmap drawingCache = AndroidUtils.getBitmapFromView(wv);
//		wv.setDrawingCacheEnabled(true);
//		//wv.buildDrawingCache();
//		final Bitmap drawingCache = wv.getDrawingCache();
//		if (drawingCache != null) {
//			final Bitmap returnedBitmap = Bitmap.createBitmap(drawingCache);
//			final Bitmap previewImage = wv.tab.previewImage;
//			wv.tab.previewImage = AndroidUtils.resize(returnedBitmap, (int)(64*pixelsInDP), (int)(64*pixelsInDP));
//			if (tabAdapter != null) {
//				tabAdapter.notifyDataSetChanged();
//			}
//			if (previewImage != null) {
//				previewImage.recycle();
//			}
//		}
//		//wv.destroyDrawingCache();
//		wv.setDrawingCacheEnabled(false);
//	}

    private void updateFullScreen() {
        final int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        final boolean fullscreenNow = (getWindow().getDecorView().getSystemUiVisibility() & flags) == flags;
        if (fullscreenNow != isFullscreen) {
            getWindow().getDecorView().setSystemUiVisibility(isFullscreen ? flags : 0);
        }
    }

    ArrayList<UserScript> getEnableJsDataList() {
		ArrayList<UserScript> list = new ArrayList<UserScript>();
		int offset = 0;
		//do {
		Cursor c = placesDb.rawQuery("SELECT title, data, enabled, _id FROM userscripts", null);
		//db.query(TABLE_NAME, arrayOf(COLUMN_ID, COLUMN_DATA), COLUMN_ENABLED + " <> 0", null, null, null, null, offset.toString() + ", 10");
		if (c.moveToFirst()) {
			do {
				UserScript data = new UserScript(c.getLong(c.getColumnIndex("_id")), c.getString(c.getColumnIndex("data")), c.getInt(c.getColumnIndex("enabled"))==1?true:false);
				list.add(data);
			} while (c.moveToNext());
		}
		c.close();

		offset += 10;
		//} while (list.size() == offset);
		return list;
	}

    private void resetUserScript() {
        if (placesDb == null)
			return;
		userScriptList = getEnableJsDataList();
    }

    private void applyUserScript(final WebView web, final String url, final UserScript.RunAt runAt) {
		final boolean userScriptEnabled = ((CustomWebView)web).tab.userScriptEnabled;
        final boolean javaScriptEnabled = ((CustomWebView)web).tab.javaScriptEnabled;
        if (userScriptEnabled && javaScriptEnabled) {
			SCRIPT_LOOP:
			for (UserScript script : userScriptList) {
				ExceptionLogger.d(TAG, script.runAt + ", script.include " + script.include + ", script.exclude " + script.exclude);
				if (runAt != script.runAt || !script.getEnabled())
                    continue;

                for (Pattern pattern : script.exclude) {
                    if (pattern.matcher(url).find()) {
                        ExceptionLogger.d(TAG, "exclude script.name " + script.name);
                        continue SCRIPT_LOOP;
					}
                }

                for (Pattern pattern : script.include) {
                    if (pattern.matcher(url).find()) {
                        web.evaluateJavascript(script.getRunnable(), null);
						ExceptionLogger.d(TAG, "run script.name " + script.name);
                        continue SCRIPT_LOOP;
                    }
                }
			}
		}
    }

	private void applyJavascriptInjection(Tab tab, WebView web, String url) {
//		if (tab.renderingMode >= 0) {
//			applyRenderingMode(web, tab.renderingMode)
//			tab.resetRenderingMode()
//		}
//		if (web.isInvertMode) {
//			web.evaluateJavascript(invertEnableJs, null)
//		}
//		val adBlockController = adBlockController
//		if (adBlockController != null) {
//			adBlockController.loadScript(Uri.parse(url))?.let {
//				web.evaluateJavascript(it, null)
//			}
//		}
		applyUserScript(web, url, UserScript.RunAt.END);
	}

	void editUserScript(final String dialogTitle, final UserScript userScript) {
		final View add_user_script = getLayoutInflater().inflate(R.layout.add_user_script, null);
		final EditText titleET = (EditText)add_user_script.findViewById(R.id.title);
		final EditText dataET = (EditText)add_user_script.findViewById(R.id.data);
		titleET.setSingleLine();
		if (userScript != null) {
			final String title = userScript.name;
			titleET.setText(title);
			final String data = userScript.getData();
			dataET.setText(data);
		} else {
			dataET.setText("//==UserScript==\n"
						   + "//@name\n"
						   + "//@version\n"
						   + "//@author\n"
						   + "//@description\n"
						   + "//@include\n"
						   + "//@exclude\n"
						   + "//@match\n"
						   + "//@unwrap\n"
						   + "//@run-at document-idle\n"
						   + "//document-start\n"
						   + "//document-end\n"
						   + "//document-idle\n"
						   + "(function() {\n\n\n\n\n"
						   + "})();\n"
						   + "//==/UserScript==");
		}
		new AlertDialog.Builder(MainActivity.this)
			.setTitle(dialogTitle)
			.setView(add_user_script)
			.setPositiveButton("Apply", new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (placesDb == null || dataET.getText().toString().trim().length() == 0 || !placesDb.isOpen())
						return;
					if (userScript == null) {
						final ContentValues values = new ContentValues(3);
						values.put("title", titleET.getText().toString());
						values.put("data", dataET.getText().toString());
						values.put("enabled", 1);
						placesDb.insert("userscripts", null, values);
						AndroidUtils.toast(MainActivity.this, "Added " + titleET.getText() + " to userscripts");
					} else {
						placesDb.execSQL("UPDATE userscripts SET title=?, data=? WHERE _id=?", new Object[] {titleET.getText().toString(), dataET.getText().toString(), userScript.getId()});
						cursor = placesDb.rawQuery("SELECT title, data, enabled, _id FROM userscripts", null);
						adapter.swapCursor(cursor);
						AndroidUtils.toast(MainActivity.this, "Updated " + titleET.getText() + " to userscripts");
					}
					resetUserScript();
				}})
			.setNegativeButton("Cancel", new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (userScript != null) {
						if (placesDb == null || !placesDb.isOpen())
							return;
						cursor = placesDb.rawQuery("SELECT title, data, enabled, _id FROM userscripts", null);
						adapter.swapCursor(cursor);
					}
				}})
			.show();

    }
	
//	@Override
//	public void onUserLeaveHint () {
//		if (popupMode) {
//			enterPictureInPictureMode();
//		}
//	}
	
//	@Override
//	public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
//		if (isInPictureInPictureMode) {
//			
//		} else {
//			
//		}
//	}

	private void saveEpub(final String mhtName) {
		try {
			Mht2Htm.mht2html(mhtName, downloadLocation, true, true);
			new File(mhtName).delete();
			new BookCreateTask(MainActivity.this,
							   mhtName + "_files/",
							   downloadLocation,
							   "",
							   "",
							   mhtName + ".html",
							   "",
							   "",
							   "",
							   "<base[^<>]+?href\\s*=[^<>]+?>",
							   "",
							   null,
							   true).execute();
		} catch (Throwable e) {
			Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
			ExceptionLogger.e(TAG, e.getMessage(), e);
		}
	}
	
	public void onConfigurationChanged(final Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		tabsListView.setAdapter(null);
		tabsListView.setAdapter(tabAdapter);
	}
	
	public static File externalFavFilesDir = null;
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
		externalFavFilesDir = getExternalFilesDir("favicon");
        
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
        fullScreenshot = prefs.getBoolean("fullScreenshot", false);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
				&& fullScreenshot) {
			WebView.enableSlowWholeDocumentDraw();
		}
		
        final IntentFilter f = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		f.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
		registerReceiver(onEvent, f);
		
        try {
            placesDb = new PlacesDbHelper(this).getWritableDatabase();
        } catch (SQLiteException e) {
            ExceptionLogger.e(TAG, "Can't open database", e);
        }
		//WebScraper webScraper = new WebScraper(this);
        setContentView(R.layout.sweb_activity_main);
        secure = prefs.getBoolean("secure", false);
		if (secure) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
								 WindowManager.LayoutParams.FLAG_SECURE);
		}

		
		getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
				public void onSystemUiVisibilityChange(int p1) {
					updateFullScreen();}});
		isFullscreen = false;
        isNightMode = prefs.getBoolean("night_mode", false);
		pixelsInDP = AndroidUtils.convertPixelsToDp(this);
		DictionaryUtil.init(this, null);
		final CharSequence text = getIntent().getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
        ExceptionLogger.d(TAG, "onCreate text " + text);
		if (text != null) {
			DictionaryUtil.openTextInDictionary(MainActivity.this, text+"", false, 100, 10);
		}
        
        webviews = (FrameLayout) findViewById(R.id.webviews);
        currentTabIndex = 0;
		address = (ViewGroup)findViewById(R.id.address);
		faviconImage = (ImageView) findViewById(R.id.favicon);
        et = (AutoCompleteTextView) findViewById(R.id.et);
		goStop = (ImageView) findViewById(R.id.goStop);
		main_layout = findViewById(R.id.main_layout);
		toolbar = (ViewGroup)findViewById(R.id.toolbar);
		searchPane = findViewById(R.id.searchPane);
		requestList = (ListView)findViewById(R.id.requestList);
		progressBar = (ProgressBar) findViewById(R.id.progressbar);
		
		//requestList.setScrollBarSize(0);
		//requestList.setScrollbarFadingEnabled(true);
//		requestList.setHorizontalScrollBarEnabled(false);
//		requestList.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//		requestList.setVerticalScrollBarEnabled(true);
		//requestList.setSelector(R.drawable.top);
		
		requestList.setSmoothScrollbarEnabled(true);
        requestList.setScrollingCacheEnabled(true);
        requestList.setFocusable(true);
        requestList.setFocusableInTouchMode(true);
        requestList.setFastScrollEnabled(true);
		requestList.setCacheColorHint(Color.WHITE);
        
		faviconImage.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View button) {
					getCurrentWebView().requestFocus();
					final Tab currentTab = getCurrentTab();
					final ListView menuListView = new ListView(MainActivity.this);
					final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
						.setPositiveButton("Close", onClickDismiss)
						.setTitle("Tab Settings")
						.create();
					final ArrayList<MenuAction> actions = new ArrayList<>(21);
					if (currentTab.listSite != null && currentTab.utils != null) {
						actions.add(new MenuAction("Back", R.drawable.back, new Runnable() {
											@Override
											public void run() {
												if (currentTab.historyIndex == 1) {
													Toast.makeText(MainActivity.this, "First site", Toast.LENGTH_SHORT).show();
												} else {
													if (currentTab.historyIndex > 1) {//} && tempIndex - 1 < listSite.size()) {
														currentTab.webview.loadUrl("file://" + currentTab.extractPath + "/" + currentTab.listSite.get(--currentTab.historyIndex));
														dialog.dismiss();
													}
												}
											}
										}));
						actions.add(new MenuAction("Forward", R.drawable.forward, new Runnable() {
											@Override
											public void run() {
												if (currentTab.historyIndex == currentTab.listSite.size() - 1) {
													Toast.makeText(MainActivity.this, "End site", Toast.LENGTH_SHORT).show();
												} else {
													//if (currentTab.historyIndex < currentTab.listSite.size() - 1) {//tempIndex - 1 >= 0 && 
														currentTab.webview.loadUrl("file://" + currentTab.extractPath + "/" + currentTab.listSite.get(++currentTab.historyIndex));
														dialog.dismiss();
													//}
												}
											}
										}));
						actions.add(new MenuAction("Media List", 0, new Runnable() {
											@Override
											public void run() {
												try {
													final String content = new String(FileUtil.readFileToMemory(new File(currentTab.extractPath + "/site_map_" + currentTab.md5File)));
													final Tab currentTab = getCurrentTab();
													final ArrayList<MenuAction> actions = new ArrayList<>(9);
													final AlertDialog dlg = new AlertDialog.Builder(MainActivity.this)
														.setPositiveButton("OK", onClickDismiss)
														.setTitle("Media List")
														.create();
													final MenuActionArrayAdapter favAdapter = new MenuActionArrayAdapter(
														MainActivity.this,
														android.R.layout.simple_list_item_1,
														actions);
													final String[] ss = content.split("\n");
													for (String s1 : ss) {
														if (MEDIA_PATTERN.matcher(s1).matches()) {
															final String s = s1;
															actions.add(new MenuAction(s, 0, new Runnable() {
																				@Override
																				public void run() {
																					currentTab.webview.loadUrl("file://" + currentTab.extractPath + "/" + s);
																					dlg.dismiss();
																				}
																			}, new MyBooleanSupplier() {
																				@Override
																				public boolean getAsBoolean() {
																					return URLDecoder.decode(currentTab.webview.getUrl().replaceAll("file:/+", "/")).equals(currentTab.extractPath + "/" + s);
																				}
																			}));
														}
													}
													final ListView tv = new ListView(MainActivity.this);
													tv.setAdapter(favAdapter);
													dlg.setView(tv);
													dlg.show();
												} catch (IOException e) {
													ExceptionLogger.e(TAG, e);
													Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
												}
												dialog.dismiss();
											}
										}));
						actions.add(new MenuAction("Site Map", 0, new Runnable() {
											@Override
											public void run() {
												if (CHMFile.CHM_PATTERN.matcher(currentTab.chmFilePath).matches()) {
													if (currentTab.utils != null) {
														currentTab.webview.loadUrl("file://" + currentTab.extractPath + "/" + currentTab.listSite.get(0));
													}
												} else {
													new AlertDialog.Builder(MainActivity.this)
														.setTitle("Preview Media")
														.setMessage("Preview Images and Videos?")
														.setPositiveButton("No", new DialogInterface.OnClickListener() {
															public void onClick(DialogInterface dialog, int which) {
																if (currentTab.utils != null) {
																	currentTab.webview.loadUrl("file://" + currentTab.extractPath + "/" + currentTab.listSite.get(0)+"_nopreview.html");
																}
															}})
														.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
															public void onClick(DialogInterface dialog, int which) {
																if (currentTab.utils != null) {
																	currentTab.webview.loadUrl("file://" + currentTab.extractPath + "/" + currentTab.listSite.get(0)+".html");
																}
															}})
														.show();
												}
												dialog.dismiss();
											}
										}));
						actions.add(new MenuAction("Home", 0, new Runnable() {
											@Override
											public void run() {
												currentTab.webview.loadUrl("file://" + currentTab.extractPath + "/" + currentTab.listSite.get(1));
												dialog.dismiss();
											}
										}));
						actions.add(new MenuAction("Bookmark", 0, new Runnable() {
											@Override
											public void run() {
												currentTab.offlineBookmarkDialog = new OfflineDialogBookmark(MainActivity.this, currentTab);
												currentTab.offlineBookmarkDialog.show();
												dialog.dismiss();
											}
										}));
						actions.add(new MenuAction("Search all", 0, new Runnable() {
											@Override
											public void run() {
												CustomDialogSearchAll searchALlDialog = new CustomDialogSearchAll(MainActivity.this, currentTab);
												searchALlDialog.show();
												dialog.dismiss();
											}
										}));
						if (currentTab.password != null && currentTab.password.length() > 0) {
							actions.add(new MenuAction("Save Password", 0, new Runnable() {
												@Override
												public void run() {
													currentTab.savePassword = !currentTab.savePassword;
													if (currentTab.savePassword) {
														prefs.edit().putString(currentTab.md5File+".password", currentTab.password).apply();
													} else {
														prefs.edit().putString(currentTab.md5File + ".password", "").apply();
													}
												}
											}, new MyBooleanSupplier() {
												@Override
												public boolean getAsBoolean() {
													return currentTab.savePassword;
												}
											}));
						}
						actions.add(new MenuAction("Enable Cache", 0, new Runnable() {
											@Override
											public void run() {
												currentTab.cacheMedia = !currentTab.cacheMedia;
												prefs.edit().putBoolean(currentTab.md5File+".cacheMedia", currentTab.cacheMedia).apply();
											}
										}, new MyBooleanSupplier() {
											@Override
											public boolean getAsBoolean() {
												return currentTab.cacheMedia;
											}
										}));
						actions.add(new MenuAction("Clear Cache", 0, new Runnable() {
											@Override
											public void run() {
												final File file = new File(currentTab.extractPath);
												//final Pattern dotPattern = Pattern.compile("[^\"]*?\\.[^\"]*?");
												final Object[] cur = FileUtil.getDirSize(file, true, null, null);
												final Object[] all = FileUtil.getDirSize(file.getParentFile(), true, null, null);
												new AlertDialog.Builder(MainActivity.this)
													.setTitle("Delete cache?")
													.setMessage("This action cannot be undone\nCurrent compressed file: "
																+ cur[0] + " bytes, " + cur[1] + " files, " + cur[2] + " folders."
																+ "\nAll cache: " + all[0] + " bytes, " + all[1] + " files, " + all[2] + " folders.")
													.setPositiveButton("Delete Current", new DialogInterface.OnClickListener() {
														public void onClick(final DialogInterface dialog, final int which) {
															FileUtil.deleteFolders(new File(currentTab.extractPath), true, null, null);
															AndroidUtils.toast(MainActivity.this, "Finished delete current cache");
														}
													})
													.setNegativeButton("Delete All", new DialogInterface.OnClickListener() {
														public void onClick(final DialogInterface dialog, final int which) {
															FileUtil.deleteFolders(MainActivity.this.getExternalFilesDir("Reader"), true, null, null);
															AndroidUtils.toast(MainActivity.this, "Finished delete all cache");
														}
													})
													.setNeutralButton("Cancel", onClickDismiss)
													.show();
												dialog.dismiss();
											}
										}));
					}
					
//					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)//26
//						actions.add(new MenuAction("Popup mode", 0, new Runnable() {
//											@Override
//											public void run() {
//												popupMode = !popupMode;
//												if (popupMode) {
//													MainActivity.this.enterPictureInPictureMode();
//												} else {
//
//												}
//											}
//										}, new MyBooleanSupplier() {
//											@Override
//											public boolean getAsBoolean() {
//												return popupMode;
//											}
//										}));
					actions.add(new MenuAction("New Igcognito Tab", 0, new Runnable() {
										@Override
										public void run() {
											final CustomWebView webview = createWebView(null);
											newTabCommon(webview, true);
											switchToTab(currentTabIndex+1);
											loadUrl("", webview);
											dialog.dismiss();
										}
									}));
					actions.add(new MenuAction("Save as Pdf", 0, new Runnable() {
										@Override
										public void run() {
											dialog.dismiss();
											final Tab currentTab = getCurrentTab();
											if (currentTab.printWeb != null) {
												if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
													// Calling createWebPrintJob()
													printTheWebPage(currentTab.printWeb);
												} else {
													AndroidUtils.toast(MainActivity.this, "Not available for device below Android LOLLIPOP");
												}
											} else {
												AndroidUtils.toast(MainActivity.this, "WebPage not fully loaded");
											}
										}
									}));
					actions.add(new MenuAction("Save as image", 0, new Runnable() {
										@Override
										public void run() {
											dialog.dismiss();
											takeScreenshot(-1, -1);
										}
									}));
					actions.add(new MenuAction("Save as mhtml", 0, new Runnable() {
										@Override
										public void run() {
											dialog.dismiss();
											saveWebArchive(getCurrentWebView(), new ValueCallback<String>() {
													@Override
													public void onReceiveValue(final String uniqueName) {
														ExceptionLogger.d(TAG, "onReceiveValue3 " + uniqueName);
														Toast.makeText(MainActivity.this, "Saved \"" + uniqueName + "\"", Toast.LENGTH_LONG).show();
													}
												});
										}
									}));
					actions.add(new MenuAction("Auto Reload", 0, new Runnable() {
										@Override
										public void run() {
											final EditText editView = new EditText(MainActivity.this);
											editView.setInputType(InputType.TYPE_CLASS_NUMBER);
											editView.setText(currentTab.autoReload + "");
											editView.selectAll();
											editView.requestFocus();
											new AlertDialog.Builder(MainActivity.this)
												.setTitle("Auto Reload Every (sec)")
												.setView(editView)
												.setPositiveButton("Apply", new OnClickListener() {
													public void onClick(DialogInterface dialog, int which) {
														currentTab.autoReload = Integer.valueOf(editView.getText().toString());
														currentTab.handler.removeCallbacks(currentTab.autoRerun);
														currentTab.autoRerun = new Runnable() {
															@Override
															public void run() {
																if (currentTab.chmFilePath.length() == 0) {
																	currentTab.webview.reload();
																} else {
																	if (currentTab.historyIndex < currentTab.listSite.size() - 1) {
																		currentTab.webview.loadUrl("file://" + currentTab.extractPath + "/" + currentTab.listSite.get(++currentTab.historyIndex));
																	} else {
																		currentTab.historyIndex = 0;
																		currentTab.webview.loadUrl("file://" + currentTab.extractPath + "/" + currentTab.listSite.get(0));
																	}
																}
																currentTab.handler.postDelayed(this, currentTab.autoReload*1000);
															}
														};
														if (currentTab.autoReload > 0) {
															currentTab.handler.post(currentTab.autoRerun);
														} 
													}})
												.setNegativeButton("Cancel", onClickDismiss)
												.show();
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.autoReload > 0;
										}
									}));
					actions.add(new MenuAction("Auto Scroll", 0, new Runnable() {
										@Override
										public void run() {
											final Tab currentTab = getCurrentTab();
											if (currentTab.autoscroll) {
												stop(currentTab);
												uaAdapter.notifyDataSetChanged();
											} else {
												final View rootView = getLayoutInflater().inflate(R.layout.auto_scroll_webview, null);
												final EditText delayET = (EditText) rootView.findViewById(R.id.delayET);
												delayET.setInputType(InputType.TYPE_CLASS_NUMBER);
												delayET.setText("" + (currentTab.delay*1));
												final EditText lengthET = (EditText) rootView.findViewById(R.id.lengthET);
												lengthET.setInputType(InputType.TYPE_CLASS_NUMBER);
												lengthET.setText("" + currentTab.length);
												new AlertDialog.Builder(MainActivity.this)
													.setTitle("Scroll")
													.setView(rootView)
													.setPositiveButton("Apply", new OnClickListener() {
														public void onClick(DialogInterface dialog, int which) {
															currentTab.delay = Integer.valueOf(delayET.getText().toString());
															if (currentTab.delay <= 0) {
																currentTab.delay = 1000;
															}
															currentTab.length = Integer.valueOf(lengthET.getText().toString());
															if (currentTab.length <= 0) {
																currentTab.length = 768;
															}
															start(currentTab);
															uaAdapter.notifyDataSetChanged();
														}})
													.setNegativeButton("Cancel", onClickDismiss)
													.show();
											}
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.scrolling;
										}
									}));
					actions.add(new MenuAction("Save Html", 0, new Runnable() {
										@Override
										public void run() {
											currentTab.saveHtml = !currentTab.saveHtml;
											try {
												if (!currentTab.started) {
													currentTab.webview.loadUrl("javascript:window.HTMLOUT.showSource(\"" + currentTab.toString() + "\", document.documentElement.outerHTML, \"" + currentTab.webview.getUrl() + "\")");
													saveHtml(currentTab, currentTab.source, currentTab.webview.getUrl());
												}
											} catch (IOException e) {
												ExceptionLogger.e(TAG, e);
											}
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.saveHtml;
										}
									}));
					actions.add(new MenuAction("Save Images", 0, new Runnable() {
										@Override
										public void run() {
											currentTab.saveImage = !currentTab.saveImage;
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.saveImage;
										}
									}));
					actions.add(new MenuAction("Save Media", 0, new Runnable() {
										@Override
										public void run() {
											currentTab.saveMedia = !currentTab.saveMedia;
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.saveMedia;
										}
									}));
					actions.add(new MenuAction("Save Resources", 0, new Runnable() {
										@Override
										public void run() {
											currentTab.saveResources = !currentTab.saveResources;
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.saveResources;
										}
									}));
					actions.add(new MenuAction("Web Downloader", 0, new Runnable() {
										@Override
										public void run() {
											final Tab currentTab = getCurrentTab();
											final View saveImageLayout = getLayoutInflater().inflate(R.layout.save_image, null);
											final CheckBox updateCbx = (CheckBox)saveImageLayout.findViewById(R.id.update);
											
											final EditText batchLinkPattern = (EditText)saveImageLayout.findViewById(R.id.batch_link_pattern);
											final String batchLinkPatternStr = currentTab.batchLinkPatternStr.length() == 0 ? !"about:blank".equals(currentTab.webview.getUrl()) ? currentTab.webview.getUrl() : AndroidUtils.getClipboardText(MainActivity.this).toString() : currentTab.batchLinkPatternStr;
											batchLinkPattern.setText(batchLinkPatternStr);
											
											final EditText crawlPattern = (EditText)saveImageLayout.findViewById(R.id.crawlPattern);
											if (currentTab.crawlPatternStr.length() > 0) {
												crawlPattern.setText(currentTab.crawlPatternStr);
											} else {
												crawlPattern.setText(batchLinkPatternStr + "/[^\"]*?");
											}
											
											final EditText excludeCrawlPattern = ((EditText)saveImageLayout.findViewById(R.id.excludeCrawlPattern));
											excludeCrawlPattern.setText(currentTab.excludeCrawlPatternStr);
											
											final EditText from_link = (EditText)saveImageLayout.findViewById(R.id.from_link);
											from_link.setText(currentTab.from == 0 ? "" : currentTab.from + "");
											
											final EditText to_link = (EditText)saveImageLayout.findViewById(R.id.to_link);
											to_link.setText(currentTab.to == 0 ? "" : currentTab.to + "");
											
											final EditText levelEt = (EditText)saveImageLayout.findViewById(R.id.level);
											levelEt.setText(currentTab.level + "");
											
											final EditText replaceEt = (EditText)saveImageLayout.findViewById(R.id.replaceEt);
											replaceEt.setText(currentTab.replace);

											final EditText byEt = (EditText)saveImageLayout.findViewById(R.id.byEt);
											byEt.setText(currentTab.by);
											
											final TextView statusTv = (TextView)saveImageLayout.findViewById(R.id.statusTv);
											
											final CheckBox saveHtmlCbx = (CheckBox)saveImageLayout.findViewById(R.id.saveHtml);
											final CheckBox useAdBlockCbx = (CheckBox)saveImageLayout.findViewById(R.id.useAdBlock);
											final CheckBox saveResourcesCbx = (CheckBox)saveImageLayout.findViewById(R.id.saveResources);
											final CheckBox saveImageCbx = (CheckBox)saveImageLayout.findViewById(R.id.save_image);
											final CheckBox javaScriptEnabledCbx = (CheckBox)saveImageLayout.findViewById(R.id.javaScriptEnabled);
											final CheckBox localizationCbx = (CheckBox)saveImageLayout.findViewById(R.id.localization);
											final CheckBox catchAMPCbx = (CheckBox)saveImageLayout.findViewById(R.id.catch_amp);
											final CheckBox autoscrollCbx = (CheckBox)saveImageLayout.findViewById(R.id.autoscroll);
											final CheckBox removeCommentCbx = (CheckBox)saveImageLayout.findViewById(R.id.remove_comment);
											final CheckBox removeJavascriptCbx = (CheckBox)saveImageLayout.findViewById(R.id.remove_javascript);
											updateCbx.setChecked(currentTab.update);
											saveHtmlCbx.setChecked(currentTab.saveHtml);
											useAdBlockCbx.setChecked(currentTab.useAdBlocker);
											saveResourcesCbx.setChecked(currentTab.saveResources);
											saveImageCbx.setChecked(currentTab.saveImage);
											localizationCbx.setChecked(currentTab.localization);
											javaScriptEnabledCbx.setChecked(currentTab.javaScriptEnabled);
											catchAMPCbx.setChecked(currentTab.catchAMP);
											autoscrollCbx.setChecked(currentTab.autoscroll);
											removeCommentCbx.setChecked(currentTab.removeComment);
											removeJavascriptCbx.setChecked(currentTab.removeJavascript);
											final CheckBox includeResCbx = (CheckBox)saveImageLayout.findViewById(R.id.include_res);
											final CheckBox excludeResCbx = (CheckBox)saveImageLayout.findViewById(R.id.exclude_res);
											if (currentTab.batchRunning) {
												statusTv.setText("Running " + currentTab.batchDownloadedSet.size() + "/" + currentTab.batchDownloadSet.size() + "links");
											}
											final CheckBox orUrl = (CheckBox)saveImageLayout.findViewById(R.id.or_url);
											final CheckBox orRes = (CheckBox)saveImageLayout.findViewById(R.id.or_res);
											final EditText includeResET = (EditText)saveImageLayout.findViewById(R.id.include_res_pattern);
											final EditText excludeResET = (EditText)saveImageLayout.findViewById(R.id.exclude_res_pattern);
											includeResET.setText(currentTab.includeResPatternStr);
											excludeResET.setText(currentTab.excludeResPatternStr);
											new AlertDialog.Builder(MainActivity.this)
												.setTitle("Auto Download / Save Images")
												.setView(saveImageLayout)
												.setPositiveButton("Start", new OnClickListener() {
													public void onClick(DialogInterface dialog, int which) {
														try {
															if (!hasOrRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
																						null,
																						PERMISSION_REQUEST_DOWNLOAD)) {
																AndroidUtils.toast(MainActivity.this, "No permission to save images");
																return;
															}
															if (batchLinkPattern.getText().length() == 0) {
																AndroidUtils.toast(MainActivity.this, "No link to run");
																return;
															}
															currentTab.batchRunning = false;
															currentTab.batchDownloadSet = new TreeSet<>();
															currentTab.batchDownloadedSet = new TreeSet<>();
															
															currentTab.includeResPatternStr = includeResET.getText().toString().trim();
															ExceptionLogger.d(TAG, "includeImagePatternStr " + currentTab.includeResPatternStr);
															currentTab.saveImage = saveImageCbx.isChecked();
															if (includeResCbx.isChecked() && currentTab.includeResPatternStr.length() > 0) {
																currentTab.includeResPattern = Pattern.compile(currentTab.includeResPatternStr, Pattern.CASE_INSENSITIVE);
															} else {
																currentTab.includeResPattern = null;
															}
															currentTab.excludeResPatternStr = excludeResET.getText().toString().trim();
															ExceptionLogger.d(TAG, "excludeImagePatternStr " + currentTab.excludeResPatternStr);
															if (excludeResCbx.isChecked() && currentTab.excludeResPatternStr.length() > 0) {
																currentTab.excludeResPattern = Pattern.compile(currentTab.excludeResPatternStr, Pattern.CASE_INSENSITIVE);
															} else {
																currentTab.excludeResPattern = null;
															}
															
															currentTab.batchLinkPatternStr = batchLinkPattern.getText().toString().trim();
															ExceptionLogger.d(TAG, "batchLinkPatternStr " + currentTab.batchLinkPatternStr);
															final String from = from_link.getText().toString().trim();
															if (from.length() > 0) {
																currentTab.from = Integer.valueOf(from);
															} else {
																currentTab.from = 0;
															}

															final String to = to_link.getText().toString().trim();
															if (to.length() > 0) {
																currentTab.to = Integer.valueOf(to);
															} else {
																currentTab.to = 0;
															}
															if (currentTab.to > currentTab.from) {
																for (int i = currentTab.from; i <= currentTab.to; i++) {
																	final String replace = currentTab.batchLinkPatternStr.replace("*", i + "");
																	ExceptionLogger.d(TAG, "batchLinkPatternStr.replace " + replace);
																	currentTab.batchDownloadSet.add(new CrawlerInfo(replace, 0));
																}
															} else {
																currentTab.batchDownloadSet.add(new CrawlerInfo(currentTab.batchLinkPatternStr, 0));
															}
															currentTab.level = levelEt.getText().length() > 0 ? Integer.parseInt(levelEt.getText().toString()) : -1;

															currentTab.excludeLinkFirst = orUrl.isChecked();
															currentTab.excludeResFirst = orRes.isChecked();

															currentTab.crawlPatternStr = crawlPattern.getText().toString().trim();
															currentTab.excludeCrawlPatternStr = excludeCrawlPattern.getText().toString().trim();
															if (currentTab.crawlPatternStr.length() > 0) {
																currentTab.crawlPattern = Pattern.compile(currentTab.crawlPatternStr.replaceAll("\\s+", "|"), Pattern.CASE_INSENSITIVE);
															} else {
																currentTab.crawlPattern = null;
															}
															if (currentTab.excludeCrawlPatternStr.length() > 0) {
																currentTab.excludeCrawlPattern = Pattern.compile(currentTab.excludeCrawlPatternStr.replaceAll("\\s+", "|"), Pattern.CASE_INSENSITIVE);
															} else {
																currentTab.excludeCrawlPattern = null;
															}
															currentTab.replace = replaceEt.getText().toString();
															String[] ss = currentTab.replace.split("\n+");
															currentTab.replacePat = new Pattern[ss.length];
															for (int i = 0; i < ss.length; i++) {
																currentTab.replacePat[i] = Pattern.compile(ss[i], Pattern.CASE_INSENSITIVE);
															}
															currentTab.by = byEt.getText().toString();
															ss = currentTab.by.split("\n+");
															currentTab.bys = new String[currentTab.replacePat.length];
															for (int i = 0; i < currentTab.replacePat.length; i++) {
																if (i < ss.length) {
																	currentTab.bys[i] = ss[i];
																} else {
																	currentTab.bys[i] = "";
																}
															}
															currentTab.update = updateCbx.isChecked();
															currentTab.saveHtml = saveHtmlCbx.isChecked();
															currentTab.useAdBlocker = useAdBlockCbx.isChecked();
															currentTab.saveResources = saveResourcesCbx.isChecked();
															currentTab.javaScriptEnabled = javaScriptEnabledCbx.isChecked();
															currentTab.autoscroll = autoscrollCbx.isChecked();
															currentTab.removeComment = removeCommentCbx.isChecked();
															currentTab.removeJavascript = removeJavascriptCbx.isChecked();
															ExceptionLogger.d(TAG, "currentTab.saveHtml " + currentTab.saveHtml);
															ExceptionLogger.d(TAG, "currentTab.useAdBlocker " + currentTab.useAdBlocker);
															ExceptionLogger.d(TAG, "currentTab.saveResources " + currentTab.saveResources);
															ExceptionLogger.d(TAG, "currentTab.javaScriptEnabled " + currentTab.javaScriptEnabled);
															currentTab.crawlPatternStr = crawlPattern.getText().toString().trim();
															ExceptionLogger.d(TAG, "crawlPattern " + currentTab.crawlPattern);
															currentTab.excludeCrawlPatternStr = excludeCrawlPattern.getText().toString().trim();
															ExceptionLogger.d(TAG, "excludeCrawlPattern " + currentTab.excludeCrawlPattern);
															final CrawlerInfo ci = currentTab.batchDownloadSet.first();
															currentTab.lastUrl = ci.url;
															currentTab.curLevel = ci.level;
															currentTab.batchRunning = true;
															loadUrl(currentTab.lastUrl, currentTab.webview);

															final PowerManager pm = (PowerManager) MainActivity.this.getSystemService(Context.POWER_SERVICE);
															mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
															mWakeLock.acquire();

															uaAdapter.notifyDataSetChanged();
														} catch (Throwable t) {
															ExceptionLogger.e(TAG, t.getMessage(), t);
														}
													}})
												.setNegativeButton("Stop", new OnClickListener() {
													public void onClick(DialogInterface dialog, int which) {
														final Tab currentTab = getCurrentTab();
														currentTab.batchDownloadSet.clear();
														currentTab.batchRunning = false;
														currentTab.scrolling = false;
														uaAdapter.notifyDataSetChanged();
														if (mWakeLock != null) {
															mWakeLock.release();
															mWakeLock = null;
														}
													}})
												.show();
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.batchRunning;
										}
									}));
					actions.add(new MenuAction("Page Info", 0, new Runnable() {
										@Override
										public void run() {
											dialog.dismiss();
											final CustomWebView currentWebView = getCurrentWebView();
											final SslCertificate certificate = currentWebView.getCertificate();
											final View pageInfoLayout = getLayoutInflater().inflate(R.layout.page_info, null);
											final TextView urlTv = (TextView)pageInfoLayout.findViewById(R.id.url);
											final TextView titleTv = (TextView)pageInfoLayout.findViewById(R.id.title);
											final TextView certificateTv = (TextView)pageInfoLayout.findViewById(R.id.certificate);
											final EditText cookiesTv = (EditText)pageInfoLayout.findViewById(R.id.cookies);
											final EditText userAgentET = (EditText)pageInfoLayout.findViewById(R.id.userAgent);
											final String url = currentWebView.getUrl();
											urlTv.setText(url);
											titleTv.setText(currentWebView.getTitle());
											certificateTv.setText(certificate == null ? "Not secure" : AndroidUtils.certificateToStr(certificate));
											final String cookies = CookieManager.getInstance().getCookie(url);
											cookiesTv.setText(cookies);
											final WebSettings settings = currentWebView.getSettings();
											userAgentET.setText(settings.getUserAgentString());
											//ExceptionLogger.d(TAG, settings.getDefaultUserAgent(MainActivity.this));
											final View.OnClickListener onClick = new View.OnClickListener() {
												@Override
												public void onClick(View tv) {
													AndroidUtils.copyClipboard(MainActivity.this, "Text", ((TextView)tv).getText());
												}
											};
											urlTv.setOnClickListener(onClick);
											titleTv.setOnClickListener(onClick);
											certificateTv.setOnClickListener(onClick);
											//cookiesTv.setOnClickListener(onClick);
											new AlertDialog.Builder(MainActivity.this)
												.setTitle("Page info")
												.setView(pageInfoLayout)
												.setNegativeButton("Cancel", onClickDismiss)
												.setPositiveButton("Set", new DialogInterface.OnClickListener() {
													@Override
													public void onClick(final DialogInterface p1, final int p2) {
														CookieManager.getInstance().setCookie(url, cookiesTv.getText().toString());
														CookieManager.getInstance().flush();
														settings.setUserAgentString(userAgentET.getText().toString());
													}
												})
												.show();
										}
									}));
					actions.add(new MenuAction("Block CSS", 0, new Runnable() {
										@Override
										public void run() {
											currentTab.blockCSS = !currentTab.blockCSS;
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.blockCSS;
										}
									}));
					actions.add(new MenuAction("Block Fonts", 0, new Runnable() {
										@Override
										public void run() {
											currentTab.blockFonts = !currentTab.blockFonts;
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.blockFonts;
										}
									}));
					actions.add(new MenuAction("Block Media", 0, new Runnable() {
										@Override
										public void run() {
											currentTab.blockMedia = !currentTab.blockMedia;
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.blockMedia;
										}
									}));
					actions.add(new MenuAction("Block Images", 0, new Runnable() {
										@Override
										public void run() {
											currentTab.blockImages = !currentTab.blockImages;
											AndroidUtils.toast(MainActivity.this, "Images " + !currentTab.blockImages);
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.blockImages;
										}
									}));
					actions.add(new MenuAction("Block JavaScript", 0, new Runnable() {
										@Override
										public void run() {
											currentTab.blockJavaScript = !currentTab.blockJavaScript;
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.blockJavaScript;
										}
									}));
					actions.add(new MenuAction("Block Network Loads", 0, new Runnable() {
										@Override
										public void run() {
											currentTab.blockNetworkLoads = !currentTab.blockNetworkLoads;
											final WebSettings settings = currentTab.webview.getSettings();
											settings.setBlockNetworkLoads(currentTab.blockNetworkLoads);
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.blockNetworkLoads;
										}
									}));
					actions.add(new MenuAction("JavaScript Enabled", 0, new Runnable() {
										@Override
										public void run() {
											currentTab.javaScriptEnabled = !currentTab.javaScriptEnabled;
											currentTab.webview.getSettings().setJavaScriptEnabled(currentTab.javaScriptEnabled);
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.javaScriptEnabled;
										}
									}));
					actions.add(new MenuAction("UserScript Enabled", 0, new Runnable() {
										@Override
										public void run() {
											currentTab.userScriptEnabled = !currentTab.userScriptEnabled;
											if (currentTab.userScriptEnabled) {
												currentTab.javaScriptEnabled = true;
												currentTab.webview.getSettings().setJavaScriptEnabled(true);
											}
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.userScriptEnabled;
										}
									}));
					actions.add(new MenuAction("Cookies Enabled", R.drawable.adblocker, new Runnable() {
										@Override
										public void run() {
											currentTab.enableCookies = !currentTab.enableCookies;
											CookieManager.getInstance().setAcceptCookie(currentTab.enableCookies);
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return CookieManager.getInstance().acceptCookie();
										}
									}));
					actions.add(new MenuAction("3rd party cookies", R.drawable.adblocker, new Runnable() {
										@Override
										public void run() {
											if (!currentTab.isIncognito) {
												currentTab.accept3PartyCookies = !currentTab.accept3PartyCookies;
												CookieManager.getInstance().setAcceptThirdPartyCookies(currentTab.webview, currentTab.accept3PartyCookies);
											}
											//currentTab.webview.getSettings().setAcceptThirdPartyCookies(currentTab.accept3PartyCookies);
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.accept3PartyCookies;
										}
									}));
					actions.add(new MenuAction("Wide Mode", 0, new Runnable() {
										@Override
										public void run() {
											wideMode(false);
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.isDesktop;
										}
									}
								));
					actions.add(new MenuAction("User Agent", 0, new Runnable() {
										@Override
										public void run() {
											setupUserAgentMenu(false);
										}
									}));
					actions.add(new MenuAction("Load With Overview Mode", 0, new Runnable() {
							@Override
							public void run() {
								currentTab.loadWithOverviewMode = !currentTab.loadWithOverviewMode;
								final WebSettings settings = currentTab.webview.getSettings();
								settings.setLoadWithOverviewMode(currentTab.loadWithOverviewMode);
							}
						}, new MyBooleanSupplier() {
							@Override
							public boolean getAsBoolean() {
								return currentTab.loadWithOverviewMode;
							}
						}));
					actions.add(new MenuAction("Ad Blocker", R.drawable.adblocker, new Runnable() {
							@Override
							public void run() {
								currentTab.useAdBlocker = !currentTab.useAdBlocker;
							}
						}, new MyBooleanSupplier() {
							@Override
							public boolean getAsBoolean() {
								return currentTab.useAdBlocker;
							}
						}));
					
					actions.add(new MenuAction("Offscreen PreRaster", R.drawable.adblocker, new Runnable() {
										@Override
										public void run() {
											currentTab.offscreenPreRaster = !currentTab.offscreenPreRaster;
											final WebSettings settings = currentTab.webview.getSettings();
											settings.setOffscreenPreRaster(currentTab.offscreenPreRaster);
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.offscreenPreRaster;
										}
									}));
					actions.add(new MenuAction("Popup Windows", R.drawable.adblocker, new Runnable() {
										@Override
										public void run() {
											currentTab.javaScriptCanOpenWindowsAutomatically = !currentTab.javaScriptCanOpenWindowsAutomatically;
											currentTab.webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(currentTab.javaScriptCanOpenWindowsAutomatically);
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.javaScriptCanOpenWindowsAutomatically;
										}
									}));
					actions.add(new MenuAction("Request 'Save-Data'", R.drawable.adblocker, new Runnable() {
										@Override
										public void run() {
											currentTab.requestSaveData = !currentTab.requestSaveData;
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.requestSaveData;
										}
									}));
					actions.add(new MenuAction("Do Not Track", R.drawable.adblocker, new Runnable() {
										@Override
										public void run() {
											currentTab.doNotTrack = !currentTab.doNotTrack;
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.doNotTrack;
										}
									}));
					actions.add(new MenuAction("Remove Identifying Headers", R.drawable.adblocker, new Runnable() {
										@Override
										public void run() {
											currentTab.removeIdentifyingHeaders = !currentTab.removeIdentifyingHeaders;
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.removeIdentifyingHeaders;
										}
									}));
					actions.add(new MenuAction("Text Reflow", R.drawable.adblocker, new Runnable() {
										@Override
										public void run() {
											currentTab.textReflow = !currentTab.textReflow;
											WebSettings settings = currentTab.webview.getSettings();
											if (currentTab.textReflow) {
												settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
												try {
													settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
												} catch (Exception e) {
													// This shouldn't be necessary, but there are a number
													// of KitKat devices that crash trying to set this
													ExceptionLogger.e("Problem setting LayoutAlgorithm to TEXT_AUTOSIZING", e.getMessage());
												}
											} else {
												settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
											}
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.textReflow;
										}
									}));
					actions.add(new MenuAction("Text Encoding", 0, new Runnable() {
										@Override
										public void run() {
											encodingDialog(false);
										}
									}));
					actions.add(new MenuAction("View Source", 0, new Runnable() {
										@Override
										public void run() {
											boolean ret = false;
											final String url = currentTab.webview.getUrl();
											if (url.startsWith("http")
												|| url.startsWith("ftp")) {
												//currentTab.sourceName = savedName(currentTab.webview) + ".txt";
												//ret = startDownload(currentTab.webview.getUrl(), currentTab.sourceName);
												currentTab.webview.loadData(currentTab.source, "text/txt", "utf-8");
												ret = true;
											}
											if (!ret && currentTab.webview.getSettings().getJavaScriptEnabled()
												&& url.startsWith("http")
												|| url.startsWith("ftp")) {
												if (Build.VERSION.SDK_INT >= 19) {
													currentTab.webview.evaluateJavascript("javascript:(function(){return '<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>';})()", new ValueCallback<String>() {
															@Override
															public void onReceiveValue(String s) {
																currentTab.webview.loadData(AndroidUtils.fixCharCode(s.replaceAll("\\\\n", "\n").replaceAll("\\\\\"", "\"").replaceAll("\\\\b", "\b").replaceAll("\\\\t", "\t").replaceAll("\\\\r", "\r")), "text/txt", "utf-8");
															}
														});
												} 
											} else if (url.startsWith("file")) {
												final File f = new File(Uri.decode(url).substring("file:".length()));
												try {
													final String s = readTextFile(f).toString();
													currentTab.webview.loadData(AndroidUtils.fixCharCode(s.replaceAll("\\\\n", "\n").replaceAll("\\\\\"", "\"").replaceAll("\\\\b", "\b").replaceAll("\\\\t", "\t").replaceAll("\\\\r", "\r")), "text/txt", "utf-8");
												} catch (IOException e) {
													ExceptionLogger.e(TAG, e.getMessage());
												}
											}
											dialog.dismiss();
										}
									}));
					actions.add(new MenuAction("Open In App", 0, new Runnable() {
										@Override
										public void run() {
											dialog.dismiss();
											final Intent i = new Intent(Intent.ACTION_VIEW);
											i.setData(Uri.parse(getCurrentWebView().getUrl()));
											try {
												startActivity(i);
											} catch (ActivityNotFoundException e) {
												new AlertDialog.Builder(MainActivity.this)
													.setTitle("Open in app")
													.setMessage("No app can open this URL.")
													.setPositiveButton("OK", onClickDismiss)
													.show();
											}
										}
									}));
					actions.add(new MenuAction("Share URL", 0, new Runnable() {
										@Override
										public void run() {
											dialog.dismiss();
											AndroidUtils.shareUrl(MainActivity.this, getCurrentWebView().getUrl());
										}
									}));
					actions.add(new MenuAction("Image Viewer", 0, new Runnable() {
										@Override
										public void run() {
											if (currentTab.showRequestList && currentTab.logAdapter != null && currentTab.logAdapter.showImages) {
												requestList.setVisibility(View.GONE);
												currentTab.showRequestList = false;
											} else {
												log(null, true);
											}
											dialog.dismiss();
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.showRequestList && currentTab.logAdapter != null && currentTab.logAdapter.showImages;
										}
									}));
					actions.add(new MenuAction("All Log", 0, new Runnable() {
										@Override
										public void run() {
											if (currentTab.showRequestList && currentTab.logAdapter != null && currentTab.recentConstraint == null && !currentTab.logAdapter.showImages) {
												requestList.setVisibility(View.GONE);
												currentTab.showRequestList = false;
											} else {
												log(null, false);
											}
											dialog.dismiss();
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.showRequestList && currentTab.logAdapter != null && currentTab.recentConstraint == null && !currentTab.logAdapter.showImages;
										}
									}));
					actions.add(new MenuAction("CSS Log", 0, new Runnable() {
										@Override
										public void run() {
											if (currentTab.showRequestList && currentTab.logAdapter != null && CSS_PAT.equals(currentTab.recentConstraint) && !currentTab.logAdapter.showImages) {
												requestList.setVisibility(View.GONE);
												currentTab.showRequestList = false;
											} else {
												log(CSS_PAT, false);
											}
											dialog.dismiss();
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.showRequestList && currentTab.logAdapter != null && CSS_PAT.equals(currentTab.recentConstraint) && !currentTab.logAdapter.showImages;
										}
									}));
					actions.add(new MenuAction("Media Log", 0, new Runnable() {
										@Override
										public void run() {
											if (currentTab.showRequestList && currentTab.logAdapter != null && MEDIA_PAT.equals(currentTab.recentConstraint) && !currentTab.logAdapter.showImages) {
												currentTab.showRequestList = false;
												requestList.setVisibility(View.GONE);
											} else {
												log(MEDIA_PAT, false);
											}
											dialog.dismiss();
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.showRequestList && currentTab.logAdapter != null && MEDIA_PAT.equals(currentTab.recentConstraint) && !currentTab.logAdapter.showImages;
										}
									}));
					actions.add(new MenuAction("Image Log", 0, new Runnable() {
										@Override
										public void run() {
											if (currentTab.showRequestList && currentTab.logAdapter != null && IMAGE_PAT.equals(currentTab.recentConstraint) && !currentTab.logAdapter.showImages) {
												requestList.setVisibility(View.GONE);
												currentTab.showRequestList = false;
											} else {
												log(IMAGE_PAT, false);
											}
											dialog.dismiss();
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.showRequestList && currentTab.logAdapter != null && IMAGE_PAT.equals(currentTab.recentConstraint) && !currentTab.logAdapter.showImages;
										}
									}));
					actions.add(new MenuAction("JavaScript Log", 0, new Runnable() {
										@Override
										public void run() {
											if (currentTab.showRequestList && currentTab.logAdapter != null && JAVASCRIPT_PAT.equals(currentTab.recentConstraint) && !currentTab.logAdapter.showImages) {
												requestList.setVisibility(View.GONE);
												currentTab.showRequestList = false;
											} else {
												log(JAVASCRIPT_PAT, false);
											}
											dialog.dismiss();
										}
									}, new MyBooleanSupplier() {
										@Override
										public boolean getAsBoolean() {
											return currentTab.showRequestList && currentTab.logAdapter != null && JAVASCRIPT_PAT.equals(currentTab.recentConstraint) && !currentTab.logAdapter.showImages;
										}
									}));
					
					uaAdapter = new MenuActionArrayAdapter(
						MainActivity.this,
						android.R.layout.simple_list_item_1,
						actions);
					menuListView.setAdapter(uaAdapter);
					dialog.setView(menuListView);
					dialog.show();
				}
		});
		
        registerForContextMenu(et);  
        // setup edit text
        et.setAdapter(new SearchAutocompleteAdapter(this, new SearchAutocompleteAdapter.OnSearchCommitListener() {
							  public void onSearchCommit(final String text) {
								  et.setText(text);
								  et.setSelection(text.length());
							  }}));
        et.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
					final CustomWebView currentWebView = getCurrentWebView();
					loadUrl(et.getText().toString(), currentWebView);
					currentWebView.requestFocus();
				}});

		et.setOnKeyListener(new View.OnKeyListener() {
				public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
					if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
						final CustomWebView currentWebView = getCurrentWebView();
						loadUrl(et.getText().toString(), currentWebView);
						currentWebView.requestFocus();
						return true;
					} else {
						return false;
					}
				}});
				
		et.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(final CharSequence p1, int p2, int p3, int p4) {
				}
				@Override
				public void onTextChanged(final CharSequence p1, final int p2, final int p3, final int p4) {
					final String newTextStr = p1.toString();
					if (p1.length() > 0 
						&& !"about:blank".equals(newTextStr)
						&& !newTextStr.equals(et.getTag())) {
						goStop.setImageResource(R.drawable.forward);
						textChanged = true;
						et.setTag("");
					} else {
						textChanged = false;
					}
				}
				@Override
				public void afterTextChanged(final Editable p1) {
				}
			});
		
		et.setOnFocusChangeListener(new OnFocusChangeListener() {          
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					final Tab currentTab = getCurrentTab();
					if (hasFocus) {
						tabDialog.setVisibility(View.GONE);
						et.setTag(currentTab.webview.getUrl());
						et.setText(currentTab.webview.getUrl());
						et.setSelection(0, et.getText().length());
					} else {
						if (currentTab.loading) {
							et.setTag(currentTab.webview.getUrl());
							et.setText(currentTab.webview.getUrl());
							goStop.setImageResource(R.drawable.stop);
						} else {
							et.setTag(currentTab.webview.getTitle());
							et.setText(currentTab.webview.getTitle());
							goStop.setImageResource(R.drawable.reload);
						}
					}
				}
			});
        goStop.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(final View p1) {
					final Tab t = getCurrentTab();
					final WebView currentWebView = t.webview;
					if (t.loading) {
						currentWebView.stopLoading();
						goStop.setImageResource(R.drawable.forward);
						t.loading = false;
					} else {
						if (textChanged) {
							loadUrl(et.getText().toString(), currentWebView);
						} else {
							t.loading = true;
							currentWebView.reload();
							goStop.setImageResource(R.drawable.stop);
						}
					}
					getCurrentWebView().requestFocus();
				}
		});
        
        searchEdit = (EditText) findViewById(R.id.searchEdit);
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {}

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                getCurrentWebView().findAllAsync(s.toString());
            }

            @Override
            public void afterTextChanged(final Editable s) {}
        });
        searchCount = (TextView) findViewById(R.id.searchCount);
        
		(searchFindNext = (ImageView) findViewById(R.id.searchFindNext)).setOnClickListener(new View.OnClickListener() {
				public void onClick(final View v) {
					getCurrentWebView().requestFocus();
					hideKeyboard();
					getCurrentWebView().findNext(true);
				}});
        (searchFindPrev = (ImageView) findViewById(R.id.searchFindPrev)).setOnClickListener(new View.OnClickListener() {
				public void onClick(final View v) {
					hideKeyboard();
					getCurrentWebView().requestFocus();
					getCurrentWebView().findNext(false);
				}});
        (searchClose = (ImageView) findViewById(R.id.searchClose)).setOnClickListener(new View.OnClickListener() {
				public void onClick(final View v) {
					getCurrentWebView().requestFocus();
					getCurrentWebView().clearMatches();
					searchEdit.setText("");
					searchPane.setVisibility(View.GONE);
					hideKeyboard();
				}});
		(searchClear = (ImageView) findViewById(R.id.searchClear)).setOnClickListener(new View.OnClickListener() {
				public void onClick(final View v) {
					getCurrentWebView().clearMatches();
					searchEdit.requestFocus();
					searchEdit.setText("");
				}});
				
        useAdBlocker = prefs.getBoolean("adblocker", true);
        initAdblocker();
		
		blockImages = prefs.getBoolean("blockImages", false);
        blockMedia = prefs.getBoolean("blockMedia", false);
        blockCSS = prefs.getBoolean("blockCSS", false);
        blockJavaScript = prefs.getBoolean("blockJavaScript", false);
        blockFonts = prefs.getBoolean("blockFonts", false);
        isLogRequests = prefs.getBoolean("isLogRequests", true);
		enableCookies = prefs.getBoolean("enableCookies", true);
		saveHistory = prefs.getBoolean("saveHistory", true);
		accept3PartyCookies = prefs.getBoolean("accept3PartyCookies", false);
		isFullMenu = prefs.getBoolean("isFullMenu", false);
		saveFormData = prefs.getBoolean("saveFormData", true);
		autoHideToolbar = prefs.getBoolean("autoHideToolbar", false);
        autoHideAddressbar = prefs.getBoolean("autoHideAddressbar", false);
		removeIdentifyingHeaders = prefs.getBoolean("removeIdentifyingHeaders", true);
		saveResources = prefs.getBoolean("saveResources", false);
		saveImage = prefs.getBoolean("saveImage", false);
		saveMedia = prefs.getBoolean("saveMedia", false);
		
		javaScriptEnabled = prefs.getBoolean("javaScriptEnabled", true);
		appCacheEnabled = prefs.getBoolean("appCacheEnabled", true);
		allowContentAccess = prefs.getBoolean("allowContentAccess", true);
		mediaPlaybackRequiresUserGesture = prefs.getBoolean("mediaPlaybackRequiresUserGesture", true);
		loadWithOverviewMode = prefs.getBoolean("loadWithOverviewMode", false);
		domStorageEnabled = prefs.getBoolean("domStorageEnabled", true);
		geolocationEnabled = prefs.getBoolean("geolocationEnabled", false);
		mixedContentMode = prefs.getInt("mixedContentMode", 0);
		databaseEnabled = prefs.getBoolean("databaseEnabled", true);
		offscreenPreRaster = prefs.getBoolean("offscreenPreRaster", false);
		userAgentString = prefs.getString("userAgentString", androidPhoneUA);
		allowFileAccess = prefs.getBoolean("allowFileAccess", true);
		allowFileAccessFromFileURLs = prefs.getBoolean("allowFileAccessFromFileURLs", true);
		allowUniversalAccessFromFileURLs = prefs.getBoolean("allowUniversalAccessFromFileURLs", true);
		blockNetworkLoads = prefs.getBoolean("blockNetworkLoads", false);
		javaScriptCanOpenWindowsAutomatically = prefs.getBoolean("javaScriptCanOpenWindowsAutomatically", false);
		cacheMode = prefs.getInt("cacheMode", WebSettings.LOAD_DEFAULT);
		isDesktop = prefs.getBoolean("isDesktop", false);
		requestSaveData = prefs.getBoolean("requestSaveData", true);
		doNotTrack = prefs.getBoolean("doNotTrack", true);
		renderMode = prefs.getInt("renderMode", 0);
		downloadLocation = prefs.getString("downloadLocation", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
		textEncoding = prefs.getString("textEncoding", "UTF-8");
		deleteAfter = prefs.getString("deleteAfter", "30");
		userScriptEnabled = prefs.getBoolean("userScriptEnabled",true);
		showHistoryInSpeedDial = prefs.getBoolean("showHistoryInSpeedDial", true);
		autoLookup = prefs.getBoolean("autoLookup", false);
		SCRAP_PATH = downloadLocation + "/sweb";
		cacheOffline = prefs.getBoolean("cacheOffline", false);
		restoreTabs = prefs.getInt("restoreTabs", 0);
		ExceptionLogger.d(TAG, "Cache dir " + SCRAP_PATH);
		new Thread(new Runnable() {
				@Override
				public void run() {
					if (placesDb != null)
						placesDb.execSQL("DELETE FROM history WHERE date_created < DATETIME('now', '-" + deleteAfter + " day')", new Object[] {});
					final File[] fs = externalLogFilesDir.listFiles();
					if (fs != null) {
						final long sevenDays = 7*24*60*60*1000;
						final long now = System.currentTimeMillis();
						for (File f : fs) {
							if (!f.equals(ExceptionLogger.file) && now - f.lastModified() > sevenDays)
								f.delete();
						}
					}
				}
			}).start();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			WebView.setWebContentsDebuggingEnabled(true);
		}
		resetUserScript();
		setupToolbar(toolbar);
		et.setSelected(false);
        
		if (restoreTabs == 1) {
			restoreTabs();
		}
		loadIntent(getIntent());
        final WebView currentWebView = getCurrentWebView();
		currentWebView.setVisibility(View.VISIBLE);
        onNightModeChange();
		gestureDetector = new GestureDetector(this, new CustomGestureListener());
		
		tabDialog = (LinearLayout)findViewById(R.id.tabDialog);
		undoCloseBtn = (ImageView)findViewById(R.id.undoCloseBtn);
		tabsListView = (ListView)findViewById(R.id.tabList);
		faviconImage.setOnFocusChangeListener(tabsListViewFocusChange);
		goStop.setOnFocusChangeListener(tabsListViewFocusChange);
		newTabBtn = (ImageView)findViewById(R.id.new_tab_button);
		newTabBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1) {
					newTabBtn.requestFocus();
					newForegroundTab("", false, null);
				}
		});
    }
	
	@Override
    protected void onResume() {
        ExceptionLogger.d(TAG, "onResume");
		super.onResume();
		if (printJob != null && printBtnPressed) {
            if (printJob.isCompleted()) {
                AndroidUtils.toast(this, "Printing Completed");
            } else if (printJob.isStarted()) {
                AndroidUtils.toast(this, "Printing isStarted");
            } else if (printJob.isBlocked()) {
                AndroidUtils.toast(this, "Printing isBlocked");
            } else if (printJob.isCancelled()) {
                AndroidUtils.toast(this, "Printing isCancelled");
            } else if (printJob.isFailed()) {
                AndroidUtils.toast(this, "Printing isFailed");
            } else if (printJob.isQueued()) {
                AndroidUtils.toast(this, "Printing isQueued");
            }
            printBtnPressed = false;
        }
    }

	@Override
    protected void onPause() {
        ExceptionLogger.d(TAG, "onPause");
		super.onPause();
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ObjectOutputStream oos = null;
        try {
			for (Tab t : tabs) {
				final String url = t.webview.getUrl();
				if (restoreTabs != 0 && url != null && url.trim().length() > 0) {
					t.url = url;
				}
			}
			if (restoreTabs != 0) {
				fos = new FileOutputStream(new File(getExternalFilesDir(null), "tabs.ser"));
				bos = new BufferedOutputStream(fos);
				oos = new ObjectOutputStream(bos);
				oos.writeObject(tabs);
			}
		} catch (Throwable ignored) {
			ExceptionLogger.e(TAG, ignored);
        } finally {
			FileUtil.flushClose(oos, bos, fos);
		}
    }
	
    @Override
    protected void onDestroy() {
        ExceptionLogger.d(TAG, "onDestroy");
		super.onDestroy();
		unregisterReceiver(onEvent);
		if (placesDb != null) {
            placesDb.close();
        }
	}

    @Override
	public void finish() {
		ExceptionLogger.d(TAG, "finish");
		try {
			for (Tab t : tabs) {
				if (t.utils != null) {
					t.utils.chm.close();
					t.utils = null;
				}
			}
		} catch (Exception ignored) {
        }
		super.finish();
	}

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == FORM_FILE_CHOOSER) {
            if (fileUploadCallback != null) {
                // When the first file chooser activity fails to start due to an intent type not being a mime-type,
                // we should not reset the callback since we'd be called back soon with the */* type.
                if (fileUploadCallbackShouldReset) {
                    fileUploadCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                    fileUploadCallback = null;
                } else {
                    fileUploadCallbackShouldReset = true;
                }
            }
        }
    }
	@Override  
    public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenu.ContextMenuInfo menuInfo) {  
        super.onCreateContextMenu(menu, v, menuInfo);  
        final MenuInflater inflater = getMenuInflater();
		if (v == et) {
			inflater.inflate(R.menu.address, menu);  
			menu.setHeaderTitle("Select an action");
		} 
    }  
    @Override  
    public boolean onContextItemSelected(final MenuItem item){  
        switch (item.getItemId())  {
			case R.id.paste:
				String clipboardText = AndroidUtils.getClipboardText(this).toString();
				if (clipboardText.length() < 5000) {
					et.requestFocus();
					et.setText(clipboardText);
					et.setSelection(clipboardText.length());
					goStop.setImageResource(R.drawable.forward);
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					assert imm != null;
					imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
				} else {
					Toast.makeText(MainActivity.this, "Url must less than 5000 characters", Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.pasteOpen:
				clipboardText = AndroidUtils.getClipboardText(this) + "";
				if (clipboardText.length() < 5000) {
					et.requestFocus();
					et.setText(clipboardText);
					loadUrl(clipboardText, getCurrentWebView());
				} else {
					Toast.makeText(MainActivity.this, "Url must less than 5000 characters", Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.selectAll:
				et.selectAll();
				break;
			case R.id.copyAll:
				AndroidUtils.copyClipboard(MainActivity.this, "URL", et.getText());
				break;
			case R.id.clear:
				et.setText("");
				break;
		}
        return true;  
    }

	private void restoreTabs() {
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(new File(getExternalFilesDir(null), "tabs.ser"));
			bis = new BufferedInputStream(fis);
			ois = new ObjectInputStream(bis);
			final ArrayList<Tab> ts = (ArrayList<Tab>) ois.readObject();
			for (Tab tab : ts) {
				if (!tab.isIncognito) {
					tab.webview = createWebView(null);
					tab.webview.tab = tab;
					tab.requestsLog = new LinkedList<>();
					tab.handler = new Handler();
					final WebSettings settings = tab.webview.getSettings();
					settings.setUserAgentString(tab.userAgent);
					settings.setUseWideViewPort(tab.isDesktop);
					settings.setJavaScriptEnabled(tab.javaScriptEnabled);
					settings.setJavaScriptCanOpenWindowsAutomatically(tab.javaScriptCanOpenWindowsAutomatically);

					settings.setAllowContentAccess(allowContentAccess);
					settings.setMediaPlaybackRequiresUserGesture(mediaPlaybackRequiresUserGesture);
					settings.setLoadWithOverviewMode(tab.loadWithOverviewMode);
					settings.setMixedContentMode(mixedContentMode);
					settings.setOffscreenPreRaster(tab.offscreenPreRaster);
					settings.setAppCachePath(getExternalFilesDir("cache").getAbsolutePath());
					settings.setDatabasePath(getExternalFilesDir("db").getAbsolutePath());

					settings.setAllowFileAccess(allowFileAccess);
					settings.setAllowFileAccessFromFileURLs(allowFileAccessFromFileURLs);
					settings.setAllowUniversalAccessFromFileURLs(allowUniversalAccessFromFileURLs);
					settings.setBlockNetworkLoads(tab.blockNetworkLoads);
					settings.setDefaultTextEncodingName(tab.textEncoding);
					if (autoHideToolbar) {
						tab.webview.setOnTouchListener(new TouchListener());
					}
					if (autoHideAddressbar) {
						tab.webview.setOnTouchListener(new TouchListener());
					}
					setRenderMode(tab.webview, renderMode);
					tab.webview.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
					tab.webview.setVisibility(View.GONE);
					//enableWVCache(webview);

					settings.setAppCacheEnabled(appCacheEnabled);
					settings.setDomStorageEnabled(domStorageEnabled);
					settings.setDatabaseEnabled(databaseEnabled);
					settings.setGeolocationEnabled(geolocationEnabled);
					settings.setSaveFormData(saveFormData);
					settings.setCacheMode(cacheMode);
					final CookieManager cookiesInstance = CookieManager.getInstance();
					cookiesInstance.setAcceptThirdPartyCookies(tab.webview, tab.accept3PartyCookies);
					cookiesInstance.setAcceptCookie(tab.enableCookies);

					tabs.add(tabs.size()==0?0:currentTabIndex+1, tab);
					webviews.addView(tab.webview);
					setTabCountText(tabs.size());
					switchToTab(tabs.size()==1?0:currentTabIndex+1);
					loadUrl(tab.url, tab.webview);
				}
			}

		} catch (Throwable t) {
			ExceptionLogger.e(TAG, t);
		} finally {
			FileUtil.close(ois, bis, fis);
		}
	}

	private void start(final Tab currentTab) {
		currentTab.scrolling = true;
		currentTab.scrollY = currentTab.webview.computeVerticalScrollOffsetMethod();
		new Thread(new Runnable() {
				@Override
				public void run() {
					while (currentTab.scrolling) {
						try {
							Thread.sleep(currentTab.delay);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (currentTab.scrolling) {
							currentTab.webview.post(new Runnable() {
									@Override
									public void run() {
										currentTab.scrollY = currentTab.webview.getScrollY() + currentTab.length;
										currentTab.scrollMax = currentTab.webview.computeVerticalScrollRangeMethod() - currentTab.webview.computeVerticalScrollExtentMethod();
										if (currentTab.scrollY > currentTab.scrollMax) {
											currentTab.scrollY = currentTab.scrollMax;
											stop(currentTab);
											final boolean javaScriptEnabled = currentTab.javaScriptEnabled;
											currentTab.webview.getSettings().setJavaScriptEnabled(true);
											//view.evaluateJavascript("window.alert(\"ghj\");", null);
											currentTab.webview.loadUrl("javascript:window.HTMLOUT.showSource(\"" + currentTab.toString() + "\", document.documentElement.outerHTML, \"" + currentTab.webview.getUrl() + "\")");
											ExceptionLogger.d(TAG, "javascript:window.HTMLOUT.showSource(\"" + currentTab.toString() + "\", document.documentElement.outerHTML, \"" + currentTab.webview.getUrl() + "\")" + ", source.length " + currentTab.source.length());
											currentTab.webview.getSettings().setJavaScriptEnabled(javaScriptEnabled);
										}
										currentTab.webview.scrollTo(currentTab.webview.getScrollX(), currentTab.scrollY);
//									if (save && ((currentTab.scrollY - currentTab.lastYSaved) >= currentTab.pagesToSave*currentTab.webview.getMeasuredHeight())) {//} || !currentTab.isRunning)) { //}(%1000 == 0) {
//										saveWebArchive(currentTab.webview, null);
//										currentTab.lastYSaved = currentTab.scrollY;
//									}
									}
								});
						}
					}
				}
			}).start();
	}
	
	private void stop(final Tab currentTab) {
		currentTab.scrolling = false;
        //onStopListener?.invoke();
		if (currentTab.batchDownloadSet.size() > 0
			&& currentTab.autoscroll) {
			requestList.post(new Runnable() {
					@Override
					public void run() {
						final CrawlerInfo ci = currentTab.batchDownloadSet.first();
						currentTab.lastUrl = ci.url;
						currentTab.curLevel = ci.level;
						//ExceptionLogger.d(TAG, "first " + currentTab.lastUrl);
						currentTab.webview.loadUrl(currentTab.lastUrl);
					}
				});
		} else {
			if (mWakeLock != null) {
				mWakeLock.release();
				mWakeLock = null;
			}
		}
    }

	//image = null, all=.
	private void log(final String pat, final boolean showImages) {
		final Tab currentTab = getCurrentTab();
		if (!showImages) {
			currentTab.logAdapter = new LogArrayAdapter(MainActivity.this,
													 R.layout.image,
													 currentTab.requestsLog);
			currentTab.logAdapter.setNotifyOnChange(true);
			requestList.setAdapter(currentTab.logAdapter);
			currentTab.logAdapter.getFilter().filter(pat);
		} else {
			currentTab.logAdapter = new LogArrayAdapter(MainActivity.this,
													 R.layout.image,
													 currentTab.downloadedInfos);
			requestList.setAdapter(currentTab.logAdapter);
		}
		currentTab.logAdapter.showImages = showImages;
		currentTab.showRequestList = true;
		requestList.setVisibility(View.VISIBLE);
		currentTab.logAdapter.notifyDataSetChanged();
	}

    private void setTabCountText(final int count) {
        if (txtTabCount != null) {
            txtTabCount.setText(String.valueOf(count));
        }
    }

    private void maybeSetupTabCountTextView(final View view, final String name) {
        if ("Show tabs".equals(name)) {
            txtTabCount = (TextView) view.findViewById(R.id.txtText);
			tabView = view;
        } else {
			view.setOnFocusChangeListener(tabsListViewFocusChange);
			if ("Block Images".equals(name)) {
				blockImagesImageView = (ImageView) view.findViewById(R.id.btnSwipeUp);//(ImageView) view.getChildAt(1)
				if (blockImages) {
					blockImagesImageView.setImageResource(R.drawable.adblocker);
				} else {
					blockImagesImageView.setImageResource(R.drawable.ic_doc_image);
				}
			}
		}
    }

    private void setupToolbar(final ViewGroup parent) {
        final LayoutInflater layoutInflater = getLayoutInflater();
		View v;
		Runnable a1, a2, a3;
		for (String[] actions : toolbarActions) {
            v = layoutInflater.inflate(R.layout.sweb_toolbar_button, parent, false);
            parent.addView(v);
			a1 = null; a2 = null; a3 = null;
            if (actions[0] != null) {
                MenuAction action = getAction(actions[0]);
                ((ImageView) v.findViewById(R.id.btnShortClick)).setImageResource(action.icon);
                maybeSetupTabCountTextView(v, actions[0]);
                a1 = action.action;
            }
            if (actions[1] != null) {
                MenuAction action = getAction(actions[1]);
                ((ImageView) v.findViewById(R.id.btnLongClick)).setImageResource(action.icon);
                maybeSetupTabCountTextView(v, actions[1]);
                a2 = action.action;
            }
            if (actions[2] != null) {
                MenuAction action = getAction(actions[2]);
                ((ImageView) v.findViewById(R.id.btnSwipeUp)).setImageResource(action.icon);
                maybeSetupTabCountTextView(v, actions[2]);
                a3 = action.action;
            }
            setToolbarButtonActions(v, a1, a2, a3);
        }
    }

    void showOpenTabs() {
		hideKeyboard();
		if (tabDialog.getVisibility() == View.VISIBLE) {
			tabDialog.setVisibility(View.GONE);
		} else {
			tabDialog.setVisibility(View.VISIBLE);
			if (closedTabs.isEmpty()) {
				undoCloseBtn.setVisibility(View.GONE);
			} else {
				undoCloseBtn.setVisibility(View.VISIBLE);
				undoCloseBtn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View p1) {
							final int size = closedTabs.size();
							final String[] items1 = new String[size];
							for (int i = 0; i < size; i++) {
								items1[i] = closedTabs.get(i).title;
							}
							final AlertDialog undoClosedTabsDialog = new AlertDialog.Builder(MainActivity.this)
								.setTitle("Undo closed tabs")
								.setItems(items1, new OnClickListener() {
									public void onClick(DialogInterface dialog, int which1) {
										TitleAndBundle get = closedTabs.get(which1);
										final Bundle bundle = get.bundle;
										closedTabs.remove(which1);
										newTabFromBundle(bundle, get.isIncognito);
										switchToTab(currentTabIndex+1);
									}})
								.create();
							undoClosedTabsDialog.getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
									public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
										undoClosedTabsDialog.dismiss();
										new AlertDialog.Builder(MainActivity.this)
											.setTitle("Remove closed tab?")
											.setMessage(closedTabs.get(position).title)
											.setNegativeButton("Cancel", onClickDismiss)
											.setPositiveButton("Remove", new OnClickListener() {
												public void onClick(DialogInterface dialog, int which) {
													closedTabs.remove(position);
												}})
											.show();
										return true;
									}});
							undoClosedTabsDialog.show();
						}
					});
			}
		}
//		final ArrayAdapter<Tab> adapter = new ArrayAdapterWithCurrentItemClose<Tab>(
//			MainActivity.this,
//			R.layout.tab_item,
//			tabs,
//			currentTabIndex);
//        final AlertDialog.Builder tabsDialog = new AlertDialog.Builder(MainActivity.this)
//			.setTitle("Tabs")
//			.setAdapter(adapter, new OnClickListener() {
//				public void onClick(DialogInterface dialog, int which) {
//					switchToTab(which);}});
//		if (!closedTabs.isEmpty()) {
//            tabsDialog.setNeutralButton("Undo closed tabs", new OnClickListener() {
//					public void onClick(final DialogInterface dialog, final int which) {
//						final int size = closedTabs.size();
//						final String[] items1 = new String[size];
//						for (int i = 0; i < size; i++) {
//							items1[i] = closedTabs.get(i).title;
//						}
//						final AlertDialog undoClosedTabsDialog = new AlertDialog.Builder(MainActivity.this)
//							.setTitle("Undo closed tabs")
//							.setItems(items1, new OnClickListener() {
//								public void onClick(DialogInterface dialog, int which1) {
//									TitleAndBundle get = closedTabs.get(which1);
//									final Bundle bundle = get.bundle;
//									closedTabs.remove(which1);
//									newTabFromBundle(bundle, get.isIncognito);
//									switchToTab(currentTabIndex+1);
//								}})
//							.create();
//						undoClosedTabsDialog.getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
//								public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//									undoClosedTabsDialog.dismiss();
//									new AlertDialog.Builder(MainActivity.this)
//										.setTitle("Remove closed tab?")
//										.setMessage(closedTabs.get(position).title)
//										.setNegativeButton("Cancel", onClickDismiss)
//										.setPositiveButton("Remove", new OnClickListener() {
//											public void onClick(DialogInterface dialog, int which) {
//												closedTabs.remove(position);
//											}})
//										.show();
//									return true;
//								}});
//						undoClosedTabsDialog.show();
//					}});
//		}
//        tabsDialog.show();
    }

    void showTabHistory() {
        final WebBackForwardList list = getCurrentWebView().copyBackForwardList();
        final int size = list.getSize();
        final int idx = size - list.getCurrentIndex() - 1;
        final String[] items = new String[size];
        for (int i = 0; i < size; i++) {
            items[size - i - 1] = list.getItemAtIndex(i).getTitle();
        }
        final ArrayAdapter<String> adapter = new ArrayAdapterWithCurrentItem<String>(
                this,
                android.R.layout.simple_list_item_1,
                items,
                idx);
        new AlertDialog.Builder(this)
			.setTitle("Navigation History")
			.setAdapter(adapter, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					getCurrentWebView().goBackOrForward(idx - which);}})
			.show();
    }
	
	private String savedName(WebView currentWebView) {
		String url = currentWebView.getTitle().replaceAll("[?/\\:*|\"<>#+%]", "_");
		url = url.length() == 0 ? currentWebView.getUrl() : "/" + url;
		int idx = url.indexOf("/");
		url = idx > 0 ? url.substring(idx) : url;
		idx = url.indexOf("?");
		url = (idx > 0) ? url.substring(0, idx) : url;
		idx = url.indexOf("#");
		url = (idx > 0) ? url.substring(0, idx) : url;
		url = url.length() == 1 ? "/index" : url;
		return url;
	}
	
	String getUniqueName(String outputFolder, String name, String ext) {
        int i = 1;
		name = name.length() > 160 ? name.substring(0, 160) : name;
        File file = new File(outputFolder, name + ext);
        if(file.exists()) {
            while (true) {
                file = new File(outputFolder, name + " (" + i + ")" + ext);
                if (!file.exists())
                    return file.getAbsolutePath();
                i++;
            }
        }
        return file.getAbsolutePath();
    }

    public Bitmap takeScreenshot(int width, int height) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			AndroidUtils.toast(this, "Not available for device below Android LOLLIPOP");
			return null;
		}
		final Tab currentTab = getCurrentTab();
		final WebView printWeb = currentTab.printWeb;
		if (printWeb != null) {
			try {
				if (width < 0 || height < 0) {
					printWeb.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
									 View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
				}
				if (width < 0) {
					width = printWeb.getMeasuredWidth();
				}
				if (height < 0) {
					if (fullScreenshot)
						height = printWeb.getMeasuredHeight();
					else
						height = printWeb.getHeight();
				}
				ExceptionLogger.d(TAG, "width, height " + width + ", " + height);
//				web.layout(0, 0, width, height);
//				web.setDrawingCacheEnabled(true);
//				try {
//					Thread.sleep(500);
//				} catch (InterruptedException ignored) {
//				}
				//web.buildDrawingCache();
				//Bitmap createBitmap = Bitmap.createBitmap(web.getDrawingCache());
				//web.setDrawingCacheEnabled(false);
				final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
				final Canvas canvas = new Canvas(bitmap);
				printWeb.draw(canvas);
				if (bitmap != null) {
					final String savedName = savedName(printWeb);
					final String uniqueName = getUniqueName(downloadLocation, savedName, ".png");
					final FileOutputStream fos = new FileOutputStream(uniqueName);
					final BufferedOutputStream bos = new BufferedOutputStream(fos);
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
					FileUtil.flushClose(bos, fos);
					AndroidUtils.toast(this, "Saved \"" + uniqueName + "\"");
				}
				return bitmap;
			} catch (Throwable e) {
				ExceptionLogger.e(TAG, e);
				return null;
			}
		} else {
			AndroidUtils.toast(MainActivity.this, "WebPage not fully loaded");
			return null;
		}
    }
	
	// a boolean to check the status of printing
    boolean printBtnPressed = false;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void printTheWebPage(WebView webView) {
		printBtnPressed = true;
		
        // Creating  PrintManager instance
        PrintManager printManager = (PrintManager) this
			.getSystemService(Context.PRINT_SERVICE);

        // setting the name of job
        String jobName = savedName(webView) + ".pdf";

        // Creating  PrintDocumentAdapter instance
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);

        // Create a print job with name and adapter instance
        assert printManager != null;
        printJob = printManager.print(jobName, printAdapter,
									  new PrintAttributes.Builder().build());
    }
	
    private void initAdblocker() {
        //if (useAdBlocker) {
            File externalFilesDir = getExternalFilesDir("adblock");
			adBlocker = new AdBlocker(externalFilesDir);
        //} else {
        //    adBlocker = null;
        //}
		for (Tab t : tabs) {
			t.useAdBlocker = useAdBlocker;
		}
		ExceptionLogger.d(TAG, "adBlocker " + adBlocker);
    }
	
	void addBlockRules(String address) {
		final File customFilterFile = new File(getExternalFilesDir("adblock").getAbsolutePath() + "/customFilter.txt");
		if (address == null || address.isEmpty()) {
			try {
				if (!customFilterFile.exists()) {
					customFilterFile.createNewFile();
				}
				final String sb = readTextFile(customFilterFile).toString().replaceAll("127.0.0.1 ", "");
				final EditText editView = new EditText(MainActivity.this);
				editView.setText(sb);
				editView.setSelection(sb.length());
				new AlertDialog.Builder(MainActivity.this)
					.setTitle("Edit custom filter")
					.setView(editView)
					.setPositiveButton("Apply", new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							try {
								final FileWriter fr = new FileWriter(customFilterFile);
								final BufferedWriter br = new BufferedWriter(fr);
								final String[] addresses = editView.getText().toString().split("[\\s]+");
								String trim;
								for (String s : addresses) {
									trim = s.trim();
									if (trim.length() > 0)
										br.append("127.0.0.1 " + trim + "\n");
								}
								br.flush();
								fr.flush();
								br.close();
								fr.close();
							} catch (IOException e) {
								ExceptionLogger.e(TAG, e.getMessage());
							}
							initAdblocker();
						}})
					.setNegativeButton("Cancel", onClickDismiss)
					.show();
			} catch (IOException e) {
				ExceptionLogger.e(TAG, e.getMessage());
			}
		} else {
			try {
				final FileWriter fr = new FileWriter(customFilterFile, true);
				final BufferedWriter br = new BufferedWriter(fr);
				br.append("127.0.0.1 " + address.trim() + "\n");
				br.flush();
				fr.flush();
				br.close();
				fr.close();
			} catch (IOException e) {
				ExceptionLogger.e(TAG, e.getMessage());
			}
			initAdblocker();
		}
    }

	private StringBuilder readTextFile(File customFilterFile) throws IOException {
		final FileReader fr = new FileReader(customFilterFile);
		final BufferedReader br = new BufferedReader(fr);
		String ln;
		final StringBuilder sb = new StringBuilder();
		while ((ln = br.readLine()) != null) {
			sb.append(ln).append("\n");
		}
		br.close();
		fr.close();
		return sb;
	}

    void updateAdblockRules() {
        getLoaderManager().restartLoader(0, null, new LoaderManager.LoaderCallbacks<Integer>() {
            @Override
            public Loader<Integer> onCreateLoader(int id, Bundle args) {
                return new AdblockRulesLoader(MainActivity.this, adblockRulesList, getExternalFilesDir("adblock"));
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onLoadFinished(Loader<Integer> loader, Integer data) {
                AndroidUtils.toast(MainActivity.this,
                        String.format("Updated %d / %d adblock subscriptions", data, adblockRulesList.length));
                initAdblocker();
            }

            @Override
            public void onLoaderReset(Loader<Integer> loader) {}
        });
    }
	
	private Cursor cursorHistory;
	private SimpleCursorAdapter historyAdapter;
	private List<Integer> selectedItems;
	private class HistoryHolder {
		final ImageView iconView;
		final ImageView moreView;
		final TextView titleView;
		final TextView domainView;
		final TextView dateView;
		int position;
		Integer id;
		HistoryHolder(final View convertView) {
			moreView = (ImageView) convertView.findViewById(R.id.more);
			iconView = (ImageView) convertView.findViewById(R.id.icon);
			titleView = (TextView) convertView.findViewById(R.id.name);
			domainView = (TextView) convertView.findViewById(R.id.domain);
			dateView = (TextView) convertView.findViewById(R.id.lastAccessed);
			
			moreView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View button) {
						cursorHistory.moveToPosition(position);
						final int rowid = cursorHistory.getInt(cursorHistory.getColumnIndex("_id"));
						final String title = cursorHistory.getString(cursorHistory.getColumnIndex("title"));
						final String url = cursorHistory.getString(cursorHistory.getColumnIndex("url"));
						final PopupMenu popup = new PopupMenu(MainActivity.this, button);
						//Inflating the Popup using xml file
						popup.getMenuInflater().inflate(R.menu.more_history, popup.getMenu());
						
						//registering popup with OnMenuItemClickListener
						popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
								public boolean onMenuItemClick(MenuItem item) {
									switch (item.getItemId())  {
										case R.id.add: 
											addBookmark(url, title);
											break;
										case R.id.copy:
											AndroidUtils.copyClipboard(MainActivity.this, "URL", url);
											break;
										case R.id.show:
											new AlertDialog.Builder(MainActivity.this)
												.setTitle(title)
												.setMessage(url)
												.setPositiveButton("OK", onClickDismiss)
												.show();
											break;
										case R.id.share:
											AndroidUtils.shareUrl(MainActivity.this, url);
											break;
										case R.id.delete:
											cursorHistory.close();
											placesDb.execSQL("DELETE FROM history WHERE _id = ?", new Object[] {rowid});
											cursorHistory = placesDb.rawQuery("SELECT title, url, date_created, _id FROM history ORDER BY date_created DESC", null);
											historyAdapter.swapCursor(cursorHistory);
											break;
									}
									return true;
								}
							});

						popup.show();//showing popup menu
					}
				});
			iconView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View button) {
						if (selectedItems.contains(id)) {
							selectedItems.remove(id);
							iconView.setImageResource(R.drawable.dot);
						} else {
							selectedItems.add(id);
							iconView.setImageResource(R.drawable.ic_accept);
						}
					}
			});
			moreView.setTag(this);
			convertView.setTag(this);
			iconView.setTag(this);
			titleView.setTag(this);
		}
	}
	
	private void showHistory() {
        if (placesDb == null)
			return;
		selectedItems = new ArrayList<>();
        cursorHistory = placesDb.rawQuery("SELECT title, url, date_created, _id FROM history ORDER BY date_created DESC, url ASC", null);
        historyAdapter = new SimpleCursorAdapter(this,
												 R.layout.history_list_item,
												 cursorHistory,
												 new String[] { "title", "url", "date_created" },
												 new int[] { R.id.name, R.id.domain , R.id.lastAccessed }) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				final HistoryHolder holder;
				if (convertView == null) {
					convertView = super.getView(position, convertView, parent);//getLayoutInflater().inflate(R.layout.list_item, parent, false);
					holder = new HistoryHolder(convertView);
				} else {
					holder = (HistoryHolder) convertView.getTag();
				}
				holder.position = position;
				cursorHistory.moveToPosition(position);
				holder.id = cursorHistory.getInt(cursorHistory.getColumnIndex("_id"));
				if (selectedItems.contains(holder.id)) {
					holder.iconView.setImageResource(R.drawable.ic_accept);
				} else {
					holder.iconView.setImageResource(R.drawable.dot);
				}
				final TextView titleView = holder.titleView;
				final TextView domainView = holder.domainView;
				final TextView dateView = holder.dateView;
				final String title = cursorHistory.getString(cursorHistory.getColumnIndex("title"));
				titleView.setText(title);
				final String url = cursorHistory.getString(cursorHistory.getColumnIndex("url"));
				domainView.setText(url);
				final String lastAccessed = cursorHistory.getString(cursorHistory.getColumnIndex("date_created"));
				dateView.setText(lastAccessed);
				return convertView;
			}
		};
		final AlertDialog dialog = new AlertDialog.Builder(this)
			.setTitle("History")
			.setPositiveButton("Close", onClickDismiss)
			.setNegativeButton("Delete", new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					final int size = selectedItems.size();
					if (size > 0) {
						final StringBuilder sb = new StringBuilder();
						for (int i = 0; i < size; i++) {
							sb.append("?");
							if (i < size - 1) {
								sb.append(",");
							}
						}
						cursorHistory.close();
						placesDb.execSQL("DELETE FROM history WHERE _id IN (" + sb.toString()+ ")", selectedItems.toArray());
					}

				}})
			.setOnDismissListener(new OnDismissListener() {
				public void onDismiss(DialogInterface p1) {
					cursorHistory.close();}})
			.setAdapter(historyAdapter, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					final Tab currentTab = getCurrentTab();
					cursorHistory.moveToPosition(which);
					final String url = cursorHistory.getString(cursorHistory.getColumnIndex("url"));
					cursorHistory.close();
					loadBookmarkHistory(currentTab, url);
				}})
			.create();
        dialog.show();
	}

	private void loadBookmarkHistory(final Tab currentTab, final String url) {
		if (currentTab.utils != null) {
			saveHistory(currentTab);//.extractPath, currentTab.md5File, currentTab.historyIndex);
		}
		if (url.startsWith("file://")
			&& 
			(CHMFile.CHM_PATTERN.matcher(url).matches()
			|| CompressedFile.ZIP_PATTERN.matcher(url).matches()
			|| CompressedFile.SZIP_PATTERN.matcher(url).matches()
			|| CompressedFile.ARCHIVE_PATTERN.matcher(url).matches()
			|| CompressedFile.TAR_PATTERN.matcher(url).matches()
			|| CompressedFile.COMPRESSOR_PATTERN.matcher(url).matches())) {
			final String decode = URLDecoder.decode(url.substring("file://".length()));
			int foundIndex = -1;
			int i = 0;
			for (Tab t : tabs) {
				if (decode.equalsIgnoreCase(t.chmFilePath)) {
					foundIndex = i;
					break;
				}
				i++;
			}
			if (foundIndex >= 0) {
				switchToTab(foundIndex);
			} else {
				if (currentTab.utils == null
					|| (currentTab.utils != null
					&& !currentTab.chmFilePath.equalsIgnoreCase(decode))) {
					currentTab.chmFilePath = decode;
					ExceptionLogger.d(TAG, "loadBookmarkHistory chmFilePath " + currentTab.chmFilePath);
					currentTab.password = "";
					currentTab.passwordOK = false;
					initFile(currentTab);
				}
			}
		} else {
			et.setText(url);
			loadUrl(url, currentTab.webview);
		}
	}

	private void saveHistory(final Tab tab) {
//		String url = tab.webview.getUrl();
//		int length = ("file://" + tab.extractPath).length() + 1;
//		if (url != null && url.length() > length) {
//			url = URLDecoder.decode(url).substring(length);
//			int index = tab.listSite.indexOf(url);
//			if (index != -1) {
			if (saveHistory) {
				Utils.saveHistory(tab.extractPath, tab.md5File, tab.historyIndex);
			}
//		}
	}

	private class UserScriptHolder {
		final ImageView iconView;
		final ImageView moreView;
		final TextView titleView;
		int position;
		Integer id;
		UserScriptHolder(final View convertView) {
			moreView = (ImageView) convertView.findViewById(R.id.more);
			iconView = (ImageView) convertView.findViewById(R.id.icon);
			titleView = (TextView) convertView.findViewById(R.id.name);
			
			moreView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View button) {
						cursor.moveToPosition(position);
						final PopupMenu popup = new PopupMenu(MainActivity.this, button);

						popup.getMenuInflater().inflate(R.menu.more_userscript, popup.getMenu());

						popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
								public boolean onMenuItemClick(MenuItem item) {
									switch (item.getItemId())  {
										case R.id.change:
											int enabled = cursor.getInt(cursor.getColumnIndex("enabled"));
											String data = cursor.getString(cursor.getColumnIndex("data"));
											UserScript userScript = new UserScript(id, data, enabled == 1);
											userScript.name = cursor.getString(cursor.getColumnIndex("title"));
											cursor.close();
											editUserScript("Edit UserScript", userScript);
											break;
										case R.id.enabled:
											enabled = cursor.getInt(cursor.getColumnIndex("enabled"));
											data = cursor.getString(cursor.getColumnIndex("data"));
											userScript = new UserScript(id, data, enabled == 1 ? false : true);
											userScript.name = cursor.getString(cursor.getColumnIndex("title"));
											cursor.close();
											placesDb.execSQL("UPDATE userscripts SET enabled=? WHERE _id=?", new Object[] {enabled == 1 ? 0 : 1, userScript.getId()});
											cursor = placesDb.rawQuery("SELECT title, data, enabled, _id FROM userscripts", null);
											adapter.swapCursor(cursor);
											resetUserScript();
											break;
										case R.id.delete:
											cursor.close();
											placesDb.execSQL("DELETE FROM userscripts WHERE _id = ?", new Object[] {id});
											cursor = placesDb.rawQuery("SELECT title, data, enabled, _id FROM userscripts", null);
											adapter.swapCursor(cursor);
											resetUserScript();
											break;
									}
									return true;
								}
							});

						popup.show();
					}
				});
			iconView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View button) {
						if (selectedItems.contains(id)) {
							selectedItems.remove(id);
							iconView.setImageResource(R.drawable.dot);
						} else {
							selectedItems.add(id);
							iconView.setImageResource(R.drawable.ic_accept);
						}
					}
				});
			moreView.setTag(this);
			convertView.setTag(this);
			iconView.setTag(this);
			titleView.setTag(this);
		}
	}
	
	private class BookmarkHolder {
		final ImageView iconView;
		final ImageView moreView;
		final TextView titleView;
		final TextView domainView;
		int position;
		Integer id;
		BookmarkHolder(final View convertView) {
			moreView = (ImageView) convertView.findViewById(R.id.more);
			iconView = (ImageView) convertView.findViewById(R.id.icon);
			titleView = (TextView) convertView.findViewById(R.id.name);
			domainView = (TextView) convertView.findViewById(R.id.domain);
			
			moreView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View button) {
						cursor.moveToPosition(position);
						final int rowid = cursor.getInt(cursor.getColumnIndex("_id"));
						final String title = cursor.getString(cursor.getColumnIndex("title"));
						final String url = cursor.getString(cursor.getColumnIndex("url"));
						final PopupMenu popup = new PopupMenu(MainActivity.this, button);
						
						popup.getMenuInflater().inflate(R.menu.more_bookmark, popup.getMenu());

						popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
								public boolean onMenuItemClick(MenuItem item) {
									switch (item.getItemId())  {
										case R.id.rename: 
											final EditText editView2 = new EditText(MainActivity.this);
											editView2.setText(title);
											new AlertDialog.Builder(MainActivity.this)
												.setTitle("Rename bookmark")
												.setView(editView2)
												.setPositiveButton("Rename", new OnClickListener() {
													public void onClick(DialogInterface dlg, int which) {
														cursor.close();
														placesDb.execSQL("UPDATE bookmarks SET title=? WHERE _id=?", new Object[] {editView2.getText(), rowid});
														cursor = placesDb.rawQuery("SELECT title, url, _id FROM bookmarks", null);
														adapter.swapCursor(cursor);
														
													}})
												.setNegativeButton("Cancel", onClickDismiss)
												.show();
											break;
										case R.id.change:
											final EditText editView = new EditText(MainActivity.this);
											editView.setText(url);
											new AlertDialog.Builder(MainActivity.this)
												.setTitle("Change bookmark URL")
												.setView(editView)
												.setPositiveButton("Change URL", new OnClickListener() {
													public void onClick(DialogInterface dlg, int which) {
														try {
															cursor.close();
															placesDb.execSQL("UPDATE bookmarks SET url=? WHERE _id=?", new Object[] {editView.getText(), rowid});
															cursor = placesDb.rawQuery("SELECT title, url, _id FROM bookmarks", null);
															adapter.swapCursor(cursor);
														} catch (Throwable t) {
															Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
														}
													}})
												.setNegativeButton("Cancel", onClickDismiss)
												.show();
											break;
										case R.id.copy:
											AndroidUtils.copyClipboard(MainActivity.this, "URL", url);
											break;
										case R.id.share:
											AndroidUtils.shareUrl(MainActivity.this, url);
											break;
										case R.id.delete:
											cursor.close();
											placesDb.execSQL("DELETE FROM bookmarks WHERE _id = ?", new Object[] {rowid});
											cursor = placesDb.rawQuery("SELECT title, url, _id FROM bookmarks", null);
											adapter.swapCursor(cursor);
											break;
									}
									return true;
								}
							});

						popup.show();
					}
				});
			iconView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View button) {
						if (selectedItems.contains(id)) {
							selectedItems.remove(id);
							iconView.setImageResource(R.drawable.dot);
						} else {
							selectedItems.add(id);
							iconView.setImageResource(R.drawable.ic_accept);
						}
					}
				});
			moreView.setTag(this);
			convertView.setTag(this);
			iconView.setTag(this);
			titleView.setTag(this);
		}
	}
	private SimpleCursorAdapter adapter;
	private Cursor cursor;
    private void showBookmarks() {
        if (placesDb == null)
			return;
        selectedItems = new ArrayList<>();
        cursor = placesDb.rawQuery("SELECT title, url, _id FROM bookmarks", null);
        adapter = new SimpleCursorAdapter(this,
												  R.layout.bookmark_list_item,
												  cursor,
												  new String[] { "title", "url" },
												  new int[] { R.id.name, R.id.domain }) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				final BookmarkHolder holder;
				if (convertView == null) {
					convertView = super.getView(position, convertView, parent);//getLayoutInflater().inflate(R.layout.list_item, parent, false);
					holder = new BookmarkHolder(convertView);
				} else {
					holder = (BookmarkHolder) convertView.getTag();
				}
				holder.position = position;
				cursor.moveToPosition(position);
				holder.id = cursor.getInt(cursor.getColumnIndex("_id"));
				if (selectedItems.contains(holder.id)) {
					holder.iconView.setImageResource(R.drawable.ic_accept);
				} else {
					holder.iconView.setImageResource(R.drawable.dot);
				}
				final TextView titleView = holder.titleView;
				final TextView domainView = holder.domainView;
				final String title = cursor.getString(cursor.getColumnIndex("title"));
				titleView.setText(title);
				final String url = cursor.getString(cursor.getColumnIndex("url"));
				domainView.setText(url);
				return convertView;
			}
		};
		final AlertDialog dialog = new AlertDialog.Builder(this)
			.setTitle("Bookmarks")
			.setPositiveButton("OK", onClickDismiss)
			.setNegativeButton("Delete", new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					final int size = selectedItems.size();
					if (size > 0) {
						final StringBuilder sb = new StringBuilder();
						for (int i = 0; i < size; i++) {
							sb.append("?");
							if (i < size - 1) {
								sb.append(",");
							}
						}
						cursor.close();
						placesDb.execSQL("DELETE FROM bookmarks WHERE _id IN (" + sb.toString()+ ")", selectedItems.toArray());
					}

				}})
			.setOnDismissListener(new OnDismissListener() {
				public void onDismiss(android.content.DialogInterface p1) {
					cursor.close();}})
			.setAdapter(adapter, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					cursor.moveToPosition(which);
					String url = cursor.getString(cursor.getColumnIndex("url"));
					cursor.close();
					final Tab currentTab = getCurrentTab();
					loadBookmarkHistory(currentTab, url);
				}})
			.create();
        dialog.show();
    }

    private void addHistory(final CustomWebView currentWebView, final String url) {
		if (placesDb == null || !placesDb.isOpen())
			return;
        
		final String selection = "url=? AND date(date_created)=?" ;
		final Calendar cal = Calendar.getInstance();
		final int month = (cal.get(Calendar.MONTH) + 1);
		final int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		final String currentDate = cal.get(Calendar.YEAR) + "-" + (month < 10 ? "0" + month : month) + "-" + (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth);
		final String[] selectionArgs = new String[] {url, currentDate};
		final int deleted = placesDb.delete("history", selection, selectionArgs);
		//ExceptionLogger.d(TAG, "deleted " + deleted + ", currentDate: " + currentDate);

		final ContentValues valuesInsert = new ContentValues(2);
        valuesInsert.put("title", currentWebView.getTitle());
		if (currentWebView.tab.md5File != null) {
			try {
				valuesInsert.put("url", new File(currentWebView.tab.chmFilePath).toURL().toString());
			} catch (MalformedURLException e) {
				AndroidUtils.toast(MainActivity.this, "Invalid url " + currentWebView.tab.chmFilePath);
			}
		} else {
			valuesInsert.put("url", url);
		}
        placesDb.insert("history", null, valuesInsert);
    }

    private void addBookmark() {
        final WebView currentWebView = getCurrentWebView();
		final Tab currentTab = getCurrentTab();
		if (currentTab.utils != null) {
			try {
				addBookmark(new File(currentTab.chmFilePath).toURL().toString(), currentWebView.getTitle());
			} catch (MalformedURLException e) {
				AndroidUtils.toast(MainActivity.this, "Invalid url " + currentTab.chmFilePath);
			}
		} else {
			addBookmark(currentWebView.getUrl(), currentWebView.getTitle());
		}
	}

    private void addBookmark(String url, String title) {
        if (placesDb == null || url == null || url.trim().length() == 0 || url.equalsIgnoreCase("about:blank") || !placesDb.isOpen())
			return;
        final ContentValues values = new ContentValues(2);
        values.put("title", title);
		values.put("url", url);
        placesDb.insert("bookmarks", null, values);
		AndroidUtils.toast(MainActivity.this, "Added " + url + " to bookmarks");
	}

    private void exportFilters() {
        if (!hasOrRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
									null,
									PERMISSION_REQUEST_EXPORT_BOOKMARKS)) {
            return;
        }
        final File customFilterFile = new File(getExternalFilesDir("adblock").getAbsolutePath(), "customFilter.txt");
		final File exportFilterFile = new File(Environment.getExternalStorageDirectory(), "customFilter.txt");
		if (!customFilterFile.exists()) {
			AndroidUtils.toast(this, "There is no custom filter file");
		} else {
			if (exportFilterFile.exists()) {
				new AlertDialog.Builder(this)
					.setTitle("Export custom filters")
					.setMessage("The file customfilters.txt already exists on SD card. Overwrite?")
					.setNegativeButton("Cancel", onClickDismiss)
					.setPositiveButton("Overwrite", new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							//noinspection ResultOfMethodCallIgnored
							exportFilterFile.delete();
							exportFilters();
						}})
					.show();
				return;
			}
		}
		
        try {
            FileUtil.is2File(new FileInputStream(customFilterFile), exportFilterFile.getAbsolutePath());
            AndroidUtils.toast(this, "Custom Filters exported to customfilters.txt on SD card");
        } catch (IOException e) {
            new AlertDialog.Builder(this)
				.setTitle("Export custom filters error")
				.setMessage(e.toString())
				.setPositiveButton("OK", onClickDismiss)
				.show();
        }
    }

    @SuppressLint("DefaultLocale")
    private void importFilters() {
        if (!hasOrRequestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
									null,
									PERMISSION_REQUEST_IMPORT_BOOKMARKS)) {
            return;
        }
        final File customFilterFile = new File(getExternalFilesDir("adblock").getAbsolutePath(), "customFilter.txt");
		final File exportFilterFile = new File(Environment.getExternalStorageDirectory(), "customFilter.txt");
		try {
            FileUtil.isAppendFile(new FileInputStream(exportFilterFile), customFilterFile.getAbsolutePath());
            AndroidUtils.toast(this, "Custom Filters was imported from customfilters.txt on SD card");
        } catch (FileNotFoundException e) {
            new AlertDialog.Builder(this)
				.setTitle("Custom filters error")
				.setMessage("Custom filters should be placed in a customFilter.txt file on the SD Card")
				.setPositiveButton("OK", onClickDismiss)
				.show();
            return;
        } catch (IOException e) {
            new AlertDialog.Builder(this)
				.setTitle("Import custom filters error")
				.setMessage(e.toString())
				.setPositiveButton("OK", onClickDismiss)
				.show();
            return;
        }
    }
	
	private String generateSpeedDial() {
		if (placesDb == null) {
            return "";
		}
		final StringBuilder sb = new StringBuilder(
			"<!DOCTYPE NETSCAPE-Bookmark-file-1>\n" +
			"<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\">\n" +
			"<title>Speed Dial</title>\n" +
			"\n");
		if (showHistoryInSpeedDial) {
			sb.append("<table width=100%><caption>History</caption>\n");
			final List<String> l = new ArrayList<>(96);
			try (Cursor cursor = placesDb.rawQuery("SELECT title, url FROM history ORDER BY date_created DESC, url ASC", null)) {
				final int titleIdx = cursor.getColumnIndex("title");
				final int urlIdx = cursor.getColumnIndex("url");
				int num = 0;
				while (cursor.moveToNext() && num++ <10) {
					genLine(cursor, titleIdx, urlIdx, l, sb);
				}
			}
			sb.append("</table>\n");
		}
		
		sb.append("<table width=100%><caption>Bookmarks</caption>\n");
		try (Cursor cursor = placesDb.rawQuery("SELECT title, url FROM bookmarks", null)) {
			final int titleIdx = cursor.getColumnIndex("title");
			final int urlIdx = cursor.getColumnIndex("url");
			int num = 0;
			while (cursor.moveToNext() && num++ <100) {
				genLine(cursor, titleIdx, urlIdx, null, sb);
			}
		}
		sb.append("</table>\n");
		//ExceptionLogger.d(TAG, sb.toString());
		return sb.toString();
	}

	private void genLine(final Cursor cursor, final int titleIdx, final int urlIdx, final List<String> l, final StringBuilder sb) {
		final String title = cursor.getString(titleIdx);
		final String url = cursor.getString(urlIdx);
		//ExceptionLogger.d(TAG, "url " + url);
		try {
			final URL url2 = new URL(url);
			final String host = url2.getHost();
			final File fav = new File(externalFavFilesDir, host + ".webp");
			if (l == null || (!l.contains(url) && l.add(url))) {
				sb.append("<tr><td><img width=40px src=\"" + (fav.exists()?fav.getAbsolutePath():"") + "\"></td>");
				sb.append("    <td><a href=\"" + url + "\">");
				sb.append(Html.escapeHtml(title));
				sb.append("        </a></td></tr>\n");
			}
		} catch (MalformedURLException e) {
			ExceptionLogger.e(TAG, e.getMessage(), e);
		}
	}

    private void exportBookmarks() {
        if (placesDb == null) {
            new AlertDialog.Builder(this)
				.setTitle("Export bookmarks error")
				.setMessage("Can't open bookmarks database")
				.setPositiveButton("OK", onClickDismiss)
				.show();
            return;
        }
        if (!hasOrRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                null,
                PERMISSION_REQUEST_EXPORT_BOOKMARKS)) {
            return;
        }
        final File file = new File(Environment.getExternalStorageDirectory(), "bookmarks.html");
        if (file.exists()) {
            new AlertDialog.Builder(this)
				.setTitle("Export bookmarks")
				.setMessage("The file bookmarks.html already exists on SD card. Overwrite?")
				.setNegativeButton("Cancel", onClickDismiss)
				.setPositiveButton("Overwrite", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
                        //noinspection ResultOfMethodCallIgnored
                        file.delete();
                        exportBookmarks();
                    }})
				.show();
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write("<!DOCTYPE NETSCAPE-Bookmark-file-1>\n" +
                    "<!-- This is an automatically generated file.\n" +
                    "     It will be read and overwritten.\n" +
                    "     DO NOT EDIT! -->\n" +
                    "<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\">\n" +
                    "<TITLE>Bookmarks</TITLE>\n" +
                    "<H1>Bookmarks Menu</H1>\n" +
                    "\n" +
                    "<DL><p>\n");
            try (Cursor cursor = placesDb.rawQuery("SELECT title, url FROM bookmarks", null)) {
                final int titleIdx = cursor.getColumnIndex("title");
                final int urlIdx = cursor.getColumnIndex("url");
                while (cursor.moveToNext()) {
                    bw.write("    <DT><A HREF=\"" + cursor.getString(urlIdx) + "\" ADD_DATE=\"0\" LAST_MODIFIED=\"0\">");
                    bw.write(Html.escapeHtml(cursor.getString(titleIdx)));
                    bw.write("</A>\n");
                }
            }
            bw.write("</DL>\n");
            bw.close();
            AndroidUtils.toast(this, "Bookmarks exported to bookmarks.html on SD card");
        } catch (IOException e) {
            new AlertDialog.Builder(this)
				.setTitle("Export bookmarks error")
				.setMessage(e.toString())
				.setPositiveButton("OK", onClickDismiss)
				.show();
        }
    }

    @SuppressLint("DefaultLocale")
    private void importBookmarks() {
        if (placesDb == null) {
            new AlertDialog.Builder(this)
				.setTitle("Import bookmarks error")
				.setMessage("Can't open bookmarks database")
				.setPositiveButton("OK", onClickDismiss)
				.show();
            return;
        }
        if (!hasOrRequestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                null,
                PERMISSION_REQUEST_IMPORT_BOOKMARKS)) {
            return;
        }
        File file = new File(Environment.getExternalStorageDirectory(), "bookmarks.html");
        StringBuilder sb = new StringBuilder();
        try {
            char[] buf = new char[16*1024];
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
            int count;
            while ((count = br.read(buf)) != -1) {
                sb.append(buf, 0, count);
            }
            br.close();
        } catch (FileNotFoundException e) {
            new AlertDialog.Builder(this)
				.setTitle("Import bookmarks error")
				.setMessage("Bookmarks should be placed in a bookmarks.html file on the SD Card")
				.setPositiveButton("OK", onClickDismiss)
				.show();
            return;
        } catch (IOException e) {
            new AlertDialog.Builder(this)
				.setTitle("Import bookmarks error")
				.setMessage(e.toString())
				.setPositiveButton("OK", onClickDismiss)
				.show();
            return;
        }

        ArrayList<TitleAndUrl> bookmarks = new ArrayList<>();
        Pattern pattern = Pattern.compile("<A HREF=\"([^\"]*)\"[^>]*>([^<]*)</A>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sb);
        while (matcher.find()) {
            TitleAndUrl pair = new TitleAndUrl();
            pair.url = matcher.group(1);
            pair.title = matcher.group(2);
            if (pair.url == null || pair.title == null) continue;
            pair.title = Html.fromHtml(pair.title).toString();
            bookmarks.add(pair);
        }

        if (bookmarks.isEmpty()) {
            new AlertDialog.Builder(this)
				.setTitle("Import bookmarks")
				.setMessage("No bookmarks found in bookmarks.html")
				.setPositiveButton("OK", onClickDismiss)
				.show();
            return;
        }

        try {
            placesDb.beginTransaction();
            SQLiteStatement stmt = placesDb.compileStatement("INSERT INTO bookmarks (title, url) VALUES (?,?)");
            for (TitleAndUrl pair : bookmarks) {
				try {
					stmt.bindString(1, pair.title);
					stmt.bindString(2, pair.url);
					stmt.execute();
				} catch (SQLiteConstraintException e) {
					ExceptionLogger.e(TAG, e.getMessage() + ", title " + pair.title + ", url " + pair.url);
				}
            }
            placesDb.setTransactionSuccessful();
            AndroidUtils.toast(this, String.format("Imported %d bookmarks", bookmarks.size()));
        } finally {
            placesDb.endTransaction();
        }
    }

    private void deleteAllBookmarks() {
        if (placesDb == null) {
            new AlertDialog.Builder(this)
				.setTitle("Bookmarks error")
				.setMessage("Can't open bookmarks database")
				.setPositiveButton("OK", onClickDismiss)
				.show();
            return;
        }
        new AlertDialog.Builder(this)
			.setTitle("Delete all bookmarks?")
			.setMessage("This action cannot be undone")
			.setNegativeButton("Cancel", onClickDismiss)
			.setPositiveButton("Delete All", new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					placesDb.execSQL("DELETE FROM bookmarks");}})
			.show();
    }

    private void deleteAllHistory() {
        if (placesDb == null) {
            new AlertDialog.Builder(this)
				.setTitle("History error")
				.setMessage("Can't open history database")
				.setPositiveButton("OK", onClickDismiss)
				.show();
            return;
        }
        new AlertDialog.Builder(this)
			.setTitle("Delete all history?")
			.setMessage("This action cannot be undone")
			.setNegativeButton("Cancel", onClickDismiss)
			.setPositiveButton("Delete All", new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					placesDb.execSQL("DELETE FROM history");}})
			.show();
    }

    void closeTab(WebView webView, final int tabIndex, final boolean requestNewFocus) {
		final String url = webView.getUrl();
		if (url != null && !url.equals("about:blank")) {
            final TitleAndBundle titleAndBundle = new TitleAndBundle();
            titleAndBundle.title = webView.getTitle();
            titleAndBundle.bundle = new Bundle();
			titleAndBundle.isIncognito = getCurrentTab().isIncognito;
            webView.saveState(titleAndBundle.bundle);
            closedTabs.add(0, titleAndBundle);
            if (closedTabs.size() > 500) {
                closedTabs.remove(closedTabs.size() - 1);
            }
        }
		if (((CustomWebView)webView).tab.previewImage != null
			&& !((CustomWebView)webView).tab.previewImage.isRecycled()) {
			((CustomWebView)webView).tab.previewImage.recycle();
        }
		if (((CustomWebView)webView).tab.favicon != null
			&& !((CustomWebView)webView).tab.favicon.isRecycled()) {
			((CustomWebView)webView).tab.favicon.recycle();
        }
		webviews.removeView(webView);
        webView.destroy();
        tabs.remove(tabIndex);
        if (currentTabIndex > 0 && currentTabIndex >= tabIndex) {
            currentTabIndex--;
        }
        if (tabs.size() == 0) {
            // We just closed the last tab
            newBackgroundTab("about:blank", false, null);
            currentTabIndex = 0;
        }
        webView = getCurrentWebView();
		webView.setVisibility(View.VISIBLE);
		final Tab currentTab = getCurrentTab();
		if (currentTab.isIncognito) {
			final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
			final int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, displayMetrics);
            final Drawable left = getResources().getDrawable(R.drawable.ic_notification_incognito, null);
            left.setBounds(0, 0, size, size);
            et.setCompoundDrawables(left, null, null, null);
            et.setCompoundDrawablePadding(
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, displayMetrics));
		} else {
			et.setCompoundDrawables(null, null, null, null);
		}
		if (currentTab.loading) {
     		et.setTag(webView.getUrl());
			et.setText(webView.getUrl());
			progressBar.setVisibility(View.VISIBLE);
			progressBar.setProgress(webView.getProgress());
			goStop.setImageResource(R.drawable.stop);
		} else {
			et.setTag(webView.getTitle());
			et.setText(webView.getTitle());
			progressBar.setVisibility(View.GONE);
			goStop.setImageResource(R.drawable.reload);
		}
		setTabCountText(tabs.size());
		if (currentTab.favicon != null) {
			faviconImage.clearColorFilter();
			faviconImage.setImageBitmap(currentTab.favicon);
		} else {
			faviconImage.setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
			faviconImage.setImageResource(R.drawable.page_info);
		}
		//currentTab.webview.requestFocus();
		requestList.setAdapter(currentTab.logAdapter);
		if (currentTab.showRequestList) {
			requestList.setVisibility(View.VISIBLE);
			currentTab.logAdapter.notifyDataSetChanged();
			requestList.requestFocus();
		} else {
			requestList.setVisibility(View.GONE);
		}
		if (requestNewFocus) {
			webView.requestFocus();
		}
		if (tabAdapter != null)
			tabAdapter.notifyDataSetChanged();
	}

	@Override
    protected void onNewIntent(final Intent intent) {
        final CharSequence text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
        ExceptionLogger.d(TAG, "onNewIntent text " + text);
		if (text != null) {
			DictionaryUtil.openTextInDictionary(MainActivity.this, text+"", false, 100, 10);
		} else {
			loadIntent(intent);
		}
    }

	private void loadIntent(final Intent intent) {
		urlIntent = getUrlFromIntent(intent);
        ExceptionLogger.d(TAG, "loadIntent " + intent + ", urlIntent " + urlIntent);
		if (urlIntent != null && !urlIntent.isEmpty()) {
			if (!urlIntent.startsWith("/")) {//http ftp
				et.setText(urlIntent);
				newTab(urlIntent);
			} else { //local
				if (!hasOrRequestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
											null,
											PERMISSION_REQUEST_READ_EXTERNAL)) {
					et.setText("about:blank");
					newTab("about:blank");
				} else if (CHMFile.CHM_PATTERN.matcher(urlIntent).matches()
						   || CompressedFile.SZIP_PATTERN.matcher(urlIntent).matches()
						   || CompressedFile.ZIP_PATTERN.matcher(urlIntent).matches()
						   || CompressedFile.ARCHIVE_PATTERN.matcher(urlIntent).matches()
						   || CompressedFile.TAR_PATTERN.matcher(urlIntent).matches()
						   || CompressedFile.COMPRESSOR_PATTERN.matcher(urlIntent).matches()) {
					int foundIndex = -1;
					int i = 0;
					for (Tab t : tabs) {
						if (urlIntent.equalsIgnoreCase(t.chmFilePath)) {
							foundIndex = i;
							break;
						}
						i++;
					}
					if (foundIndex >= 0) {
						switchToTab(foundIndex);
					} else {
						final Tab tab = newTab("about:blank");
						handleIntent(tab, intent);
					}
				} else {
					try {
						urlIntent = new File(urlIntent).toURL().toString();
						et.setText(urlIntent);
						newTab(urlIntent);
					} catch (MalformedURLException e) {
						ExceptionLogger.e(TAG, e);
					}
				}
			}
		} else {
			if (tabs.size() == 0) {
				et.setText("about:blank");
				newBackgroundTab("about:blank", false, null);
			}
		}
	}

	private Tab newTab(final String url) {
		final Tab tab;
		if (tabs.size() == 0) {
			tab = newBackgroundTab(url, false, null);
		} else {
			tab = newForegroundTab(url, false, null);
		}
		return tab;
	}
	
    private void onNightModeChange() {
		if (isNightMode) {
            textColor = Color.rgb(0xf0, 0xf0, 0xf0);
            backgroundColor = Color.rgb(0x42, 0x42, 0x42);
            progressBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(0, 0x00ffff00, 0)));
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setNavigationBarColor(Color.BLACK);
        } else {
            textColor = Color.BLACK;
            backgroundColor = Color.rgb(0xe0, 0xe0, 0xe0);
            progressBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(0, 0x00cc00cc, 0)));
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
		main_layout.setBackgroundColor(backgroundColor);
		et.setTextColor(textColor);
		et.setBackgroundColor(backgroundColor);
		toolbar.setBackgroundColor(backgroundColor);
		goStop.setColorFilter(textColor);
		
		final Drawable compoundDrawables = et.getCompoundDrawables()[0];
		if (compoundDrawables != null)
			compoundDrawables.setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
		
		if (getCurrentTab().favicon == null) {
			faviconImage.setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
		}
		searchEdit.setTextColor(textColor);
		searchCount.setTextColor(textColor);
		searchFindPrev.setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
		searchFindNext.setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
		searchClose.setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
		searchClear.setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
		
		final int childCount = toolbar.getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View v = toolbar.getChildAt(i);
			final ImageButton btnShortClick = (ImageButton)v.findViewById(R.id.btnShortClick);
			btnShortClick.setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
			
			final ImageButton btnSwipeUp = (ImageButton)v.findViewById(R.id.btnSwipeUp);
			btnSwipeUp.setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
			
			final ImageButton btnLongClick = (ImageButton)v.findViewById(R.id.btnLongClick);
			btnLongClick.setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
			
			final TextView txtText = (TextView)v.findViewById(R.id.txtText);
			txtText.setTextColor(textColor);
		}
        for (int i = 0; i < tabs.size(); i++) {
            tabs.get(i).webview.setBackgroundColor(isNightMode ? Color.BLACK : Color.WHITE);
            injectCSS(tabs.get(i).webview);
        }
    }

    /**
     * This method forces the layer type to hardware, which
     * enables hardware rendering on the WebView instance
     * of the current LightningView.
     */
    private void setHardwareRendering(WebView webview) {
        webview.setLayerType(View.LAYER_TYPE_HARDWARE, paint);
    }

    /**
     * This method sets the layer type to none, which
     * means that either the GPU and CPU can both compose
     * the layers when necessary.
     */
    private void setNormalRendering(WebView webview) {
        webview.setLayerType(View.LAYER_TYPE_NONE, null);
    }

    /**
     * This method forces the layer type to software, which
     * disables hardware rendering on the WebView instance
     * of the current LightningView and makes the CPU render
     * the view.
     */
    void setSoftwareRendering(WebView webview) {
        webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

	/**
	 * Sets the current rendering color of the WebView instance
	 * of the current LightningView. The for modes are normal
	 * rendering, inverted rendering, grayscale rendering,
	 * and inverted grayscale rendering
	 *
	 * @param mode the integer mode to set as the rendering mode.
	 * see the numbers in documentation above for the
	 * values this method accepts.
	 */
	private void setRenderMode(WebView webview, int which) {
		switch (which) {
			case 0: {
					paint.setColorFilter(null);
					// setSoftwareRendering(); // Some devices get segfaults
					// in the WebView with Hardware Acceleration enabled,
					// the only fix is to disable hardware rendering
					setNormalRendering(webview);
					invertPage = false;
					break;
				}
			case 1: {
					ColorMatrixColorFilter filterInvert = new ColorMatrixColorFilter(
						negativeColorArray);
					paint.setColorFilter(filterInvert);
					setHardwareRendering(webview);
					invertPage = true;
					break;
				}
			case 2:
				ColorMatrix cm = new ColorMatrix();
				cm.setSaturation(0f);
				ColorMatrixColorFilter filterGray = new ColorMatrixColorFilter(cm);
				paint.setColorFilter(filterGray);
				setHardwareRendering(webview);
				break;
			case 3:
				ColorMatrix matrix = new ColorMatrix();
				matrix.set(negativeColorArray);
				ColorMatrix matrixGray = new ColorMatrix();
				matrixGray.setSaturation(0f);
				ColorMatrix concat = new ColorMatrix();
				concat.setConcat(matrix, matrixGray);
				ColorMatrixColorFilter filterInvertGray = new ColorMatrixColorFilter(concat);
				paint.setColorFilter(filterInvertGray);
				setHardwareRendering(webview);
				invertPage = true;
				break;
			case 4:
				ColorMatrixColorFilter increaseHighContrast = new ColorMatrixColorFilter(increaseContrastColorArray);
				paint.setColorFilter(increaseHighContrast);
				setHardwareRendering(webview);
				break;
		}
	}
	
	/**
     * The OnTouchListener used by the WebView so we can
     * get scroll events and show/hide the action bar when
     * the page is scrolled up/down.
     */
    private class TouchListener implements View.OnTouchListener {

        float location = 0f;
        float y = 0f;
        int action = 0;

        @SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View view, MotionEvent arg1) {
            if (view == null)
				return false;
            if (!view.hasFocus()) {
                view.requestFocus();
            }
            action = arg1.getAction();
            y = arg1.getY();
            if (action == MotionEvent.ACTION_DOWN) {
                location = y;
            } else if (action == MotionEvent.ACTION_UP) {
                float distance = y - location;
                if (distance > SCROLL_UP_THRESHOLD) {// && view.getScrollY() < SCROLL_UP_THRESHOLD
					if (autoHideAddressbar) {
						address.setVisibility(View.GONE);
					}
                    if (autoHideToolbar) {
                    	toolbar.setVisibility(View.VISIBLE);
					}
                } else if (distance < -SCROLL_UP_THRESHOLD) {
					if (autoHideToolbar) {
                    	toolbar.setVisibility(View.GONE);
					}
					if (autoHideAddressbar) {
						address.setVisibility(View.VISIBLE);
					}
                }
                location = 0f;
            }
            gestureDetector.onTouchEvent(arg1);
            return false;
        }
    }

    /**
     * The SimpleOnGestureListener used by the [TouchListener]
     * in order to delegate show/hide events to the action bar when
     * the user flings the page. Also handles long press events so
     * that we can capture them accurately.
     */
    private class CustomGestureListener extends SimpleOnGestureListener {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			float power = (int) (velocityY * 100 / maxFling);
			if (power < -10) {
				toolbar.setVisibility(View.GONE);
			} else if (power > 15) {
				toolbar.setVisibility(View.VISIBLE);
			}
			return super.onFling(e1, e2, velocityX, velocityY);
		}
    }

	AlertDialog alertDialog;
    void showMenu() {
		if (alertDialog != null) {
			alertDialog.dismiss();
		}
		if (isFullMenu) {
			showFullMenu();
		} else {
			showShortMenu();
		}
	}

    private void showShortMenu() {
        final ArrayList<MenuAction> shortMenuActions = new ArrayList<>(shortMenu.length);
        for (int i = 0; i < shortMenu.length; i++) {
            shortMenuActions.add(getAction(shortMenu[i]));
        }
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		ListView tv = new ListView(this);
		fullMenuActionAdapter = new MenuActionArrayAdapter(
			MainActivity.this,
			android.R.layout.simple_list_item_1,
			shortMenuActions);
        tv.setAdapter(fullMenuActionAdapter);
		builder.setTitle("Actions")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					alertDialog = null;
				}
			});
		builder.setView(tv);
		alertDialog = builder.create();
		alertDialog.show();
    }

    private void showFullMenu() {
		final ArrayList<MenuAction> copyOfRange = new ArrayList<MenuAction>(menuActions.length - toolbarActions.length*toolbarActions[0].length);
		for (MenuAction ma : menuActions) {
			boolean exist = false;
			for (String[] arr : toolbarActions) {
				for (String st : arr) {
					if (ma.title.equals(st)) {
						exist = true;
						break;
					}
				}
				if (exist) 
					break;
			}
			if (!exist) {
				copyOfRange.add(ma);
			}
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		ListView tv = new ListView(this);
		fullMenuActionAdapter = new MenuActionArrayAdapter(
			MainActivity.this,
			android.R.layout.simple_list_item_1,
			copyOfRange);
        tv.setAdapter(fullMenuActionAdapter);
		builder.setTitle("Full menu")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					alertDialog = null;
				}
			});
		builder.setView(tv);
		alertDialog = builder.create();
		alertDialog.show();
    }

    public void loadUrl(String url, final WebView webview) {
		//ExceptionLogger.d("loadUrl ", url);
        if (url == null || url.trim().length() == 0) {
            url = "about:blank";
        }
		url = url.trim();
        if (url.length() >= 5000) {
			Toast.makeText(MainActivity.this, "Url must less than 5000 characters", Toast.LENGTH_LONG).show();
			return;
		}
		if (url.startsWith("/")) {
			url = Uri.fromFile(new File(Uri.decode(url))).toString();
		}
        final Tab currentTab = ((CustomWebView)webview).tab;
		if (url.startsWith("javascript:") 
			|| url.startsWith("file:") 
			|| url.startsWith("data:")) {
//			currentTab.blockImages = false;
//			currentTab.blockJavaScript = false;
//			currentTab.blockCSS = false;
//			currentTab.blockFonts = false;
//			currentTab.blockMedia = false;
			if (url.startsWith("file:") 
				|| url.startsWith("data:")) {
				currentTab.blockNetworkLoads = true;
			} else {
				currentTab.blockNetworkLoads = false;
			}
			final WebSettings settings = webview.getSettings();
			//settings.setBlockNetworkImage(currentTab.blockImages);
			//settings.setLoadsImagesAutomatically(!currentTab.blockImages);
			settings.setJavaScriptEnabled(!currentTab.blockJavaScript);
			settings.setJavaScriptCanOpenWindowsAutomatically(!currentTab.blockJavaScript);
			settings.setBlockNetworkLoads(currentTab.blockNetworkLoads);
			if (url.startsWith("file:")) {
				urlIntent = url;
				if (!hasOrRequestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
											null,
											PERMISSION_REQUEST_READ_EXTERNAL)) {
					return;
				}
			}
		} else if (url.startsWith("content")) {
			url = AndroidUtils.getPath(MainActivity.this, Uri.parse(url));
        } else {
			if (url.indexOf(' ') == -1 && Patterns.WEB_URL.matcher(url).matches()) {
				final int indexOfHash = url.indexOf('#');
				final String guess = URLUtil.guessUrl(url);
				//ExceptionLogger.log("guess1 ", guess);
				if (indexOfHash != -1 && guess.indexOf('#') == -1) {
					// Hash exists in original URL but no hash in guessed URL
					url = guess + url.substring(indexOfHash);
				} else {
					url = guess;
				}
				//ExceptionLogger.log("guess2 ", guess);
			} else if (!url.equals("about:blank")) {
				url = URLUtil.composeSearchUrl(url, searchUrl, "%s");
			}
			final WebSettings settings = webview.getSettings();
			settings.setJavaScriptEnabled(currentTab.javaScriptEnabled);
			settings.setJavaScriptCanOpenWindowsAutomatically(javaScriptCanOpenWindowsAutomatically);
			//settings.setBlockNetworkImage(currentTab.blockImages);
			//settings.setLoadsImagesAutomatically(!currentTab.blockImages);
			settings.setBlockNetworkLoads(currentTab.blockNetworkLoads);
		}
		//ExceptionLogger.log("url2 ", url);
		final ArrayMap<String, String> requestHeaders = new ArrayMap<String, String>();
		if (currentTab.requestSaveData) {
			requestHeaders.put("Save-Data", "on");
		} else {
			requestHeaders.remove("Save-Data");
		}
		if (currentTab.doNotTrack) {
			requestHeaders.put("DNT", "1");
		} else {
			requestHeaders.remove("DNT");
		}
		if (currentTab.removeIdentifyingHeaders) {
			requestHeaders.put("X-Requested-With", "");
			requestHeaders.put("X-Wap-Profile", "");
		}
		if (currentTab.webview == webview && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			if (currentTab.isIncognito) {
				mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW;
			} else {
				mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE;
			}
		} 
		currentTab.loading = true;
		if (url.equals("about:blank")) {
			webview.loadDataWithBaseURL("file:///android_asset/", generateSpeedDial(), "text/html", "utf-8", "");
		} else {
			webview.loadUrl(url, requestHeaders);
		}
		if (currentTab == getCurrentTab()) {
			goStop.setImageResource(R.drawable.stop);
		}
		//webview.requestFocus();
		hideKeyboard();
	}

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }
	
	@Override
    public void onBackPressed() {
		super.onBackPressed();
        if (((View)(findViewById(R.id.fullScreenVideo))).getVisibility() == View.VISIBLE && fullScreenCallback[0] != null) {
            fullScreenCallback[0].onCustomViewHidden();
        } else if (getCurrentWebView().canGoBack()) {
            getCurrentWebView().goBack();
        } else if (tabs.size() > 1) {
            closeTab(getCurrentWebView(), currentTabIndex, true);
        }
    }
	
    private void injectCSS(WebView webview) {
        try {
            String css = "*, :after, :before {background-color: #161a1e !important; color: #61615f !important; border-color: #212a32 !important; background-image:none !important; outline-color: #161a1e !important; z-index: 1 !important} " +
                    "svg, img {filter: grayscale(100%) brightness(50%) !important; -webkit-filter: grayscale(100%) brightness(50%) !important} " +
                    "input {background-color: black !important;}" +
                    "select, option, textarea, button, input {color:#aaa !important; background-color: black !important; border:1px solid #212a32 !important}" +
                    "a, a * {text-decoration: none !important; color:#32658b !important}" +
                    "a:visited, a:visited * {color: #783b78 !important}" +
                    "* {max-width: 100vw !important} pre {white-space: pre-wrap !important}";
/*
            String cssDolphin = "*,:before,:after,html *{color:#61615f!important;-webkit-border-image:none!important;border-image:none!important;background:none!important;background-image:none!important;box-shadow:none!important;text-shadow:none!important;border-color:#212a32!important}\n" +
                    "\n" +
                    "body{background-color:#000000!important}\n" +
                    "html a,html a *{text-decoration:none!important;color:#394c65!important}\n" +
                    "html a:hover,html a:hover *{color:#394c65!important;background:#1b1e23!important}\n" +
                    "html a:visited,html a:visited *,html a:active,html a:active *{color:#58325b!important}\n" +
                    "html select,html option,html textarea,html button{color:#aaa!important;border:1px solid #212a32!important;background:#161a1e!important;border-color:#212a32!important;border-style:solid}\n" +
                    "html select:hover,html option:hover,html button:hover,html textarea:hover,html select:focus,html option:focus,html button:focus,html textarea:focus{color:#bbb!important;background:#161a1e!important;border-color:#777 #999 #999 #777 !important}\n" +
                    "html input,html input[type=text],html input[type=search],html input[type=password]{color:#4e4e4e!important;background-color:#161a1e!important;box-shadow:1px 0 4px rgba(16,18,23,.75) inset,0 1px 4px rgba(16,18,23,.75) inset!important;border-color:#1a1c27!important;border-style:solid!important}\n" +
                    "html input:focus,html input[type=text]:focus,html input[type=search]:focus,html input[type=password]:focus{color:#bbb!important;outline:none!important;background:#161a1e!important;border-color:#1a3973}\n" +
                    "html input:hover,html select:hover,html option:hover,html button:hover,html textarea:hover,html input:focus,html select:focus,html option:focus,html button:focus,html textarea:focus{color:#bbb!important;background:#093681!important;opacity:0.4!important;border-color:#777 #999 #999 #777 !important}\n" +
                    "html input[type=button],html input[type=submit],html input[type=reset],html input[type=image]{color:#4e4e4e!important;border-color:#888 #666 #666 #888 !important}\n" +
                    "html input[type=button],html input[type=submit],html input[type=reset]{border:1px solid #212a32!important;background-image:0 color-stop(1,#181a23))!important}\n" +
                    "html input[type=button]:hover,html input[type=submit]:hover,html input[type=reset]:hover,html input[type=image]:hover{border-color:#777 #999 #999 #777 !important}\n" +
                    "html input[type=button]:hover,html input[type=submit]:hover,html input[type=reset]:hover{border:1px solid #666!important;background-image:0 color-stop(1,#262939))!important}\n" +
                    "html img,html svg{opacity:.5!important;border-color:#111!important}\n" +
                    "html ::-webkit-input-placeholder{color:#4e4e4e!important}\n";
*/
            final String styleElementId = "night_mode_style_4398357";   // should be unique
            String js;
            if (isNightMode) {
                js = "if (document.head) {" +
                        "if (!window.night_mode_id_list) night_mode_id_list = new Set();" +
                        "var newset = new Set();" +
                        "   for (var n of document.querySelectorAll(':not(a)')) { " +
                        "     if (n.closest('a') != null) continue;" +
                        "     if (!n.id) n.id = 'night_mode_id_' + (night_mode_id_list.size + newset.size);" +
                        "     if (!night_mode_id_list.has(n.id)) newset.add(n.id); " +
                        "   }" +
                        "for (var item of newset) night_mode_id_list.add(item);" +
                        "var style = document.getElementById('" + styleElementId + "');" +
                        "if (!style) {" +
                        "   style = document.createElement('style');" +
                        "   style.id = '" + styleElementId + "';" +
                        "   style.type = 'text/css';" +
                        "   style.innerHTML = '" + css + "';" +
                        "   document.head.appendChild(style);" +
                        "}" +
                        "   var css2 = ' ';" +
                        "   for (var nid of newset) css2 += ('#' + nid + '#' + nid + ',');" +
                        "   css2 += '#nonexistent {background-color: #161a1e !important; color: #61615f !important; border-color: #212a32 !important; background-image:none !important; outline-color: #161a1e !important; z-index: 1 !important}';" +
                        "   style.innerHTML += css2;" +
                        "}" +
                        "var iframes = document.getElementsByTagName('iframe');" +
                        "for (var i = 0; i < iframes.length; i++) {" +
                        "   var fr = iframes[i];" +
                        "   var style = fr.contentWindow.document.createElement('style');" +
                        "   style.id = '" + styleElementId + "';" +
                        "   style.type = 'text/css';" +
                        "   style.innerHTML = '" + css + "';" +
                        "   fr.contentDocument.head.appendChild(style);" +
                        "}";
            } else {
                js = "if (document.head && document.getElementById('" + styleElementId + "')) {" +
                        "   var style = document.getElementById('" + styleElementId + "');" +
                        "   document.head.removeChild(style);" +
                        "   window.night_mode_id_list = undefined;" +
                        "}" +
                        "var iframes = document.getElementsByTagName('iframe');" +
                        "for (var i = 0; i < iframes.length; i++) {" +
                        "   var fr = iframes[i];" +
                        "   var style = fr.contentWindow.document.getElementById('" + styleElementId + "');" +
                        "   fr.contentDocument.head.removeChild(style);" +
                        "}";
            }
            webview.evaluateJavascript("javascript:(function() {" + js + "})()", null);
            if (!getCurrentTab().isDesktop) {
                webview.evaluateJavascript("javascript:document.querySelector('meta[name=viewport]').content='width=device-width, initial-scale=1.0, maximum-scale=3.0, user-scalable=1';", null);
            }
        } catch (Throwable t) {
            ExceptionLogger.e(TAG, t.getMessage(), t);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        switch (requestCode) {
            case PERMISSION_REQUEST_EXPORT_BOOKMARKS:
                exportBookmarks();
                break;
            case PERMISSION_REQUEST_IMPORT_BOOKMARKS:
                importBookmarks();
                break;
			case PERMISSION_REQUEST_EXPORT_FILTERS:
                exportFilters();
                break;
            case PERMISSION_REQUEST_IMPORT_FILTERS:
                importFilters();
                break;
			case PERMISSION_REQUEST_READ_EXTERNAL:
				newBackgroundTab(urlIntent, false, null);
				switchToTab(currentTabIndex+1);
				break;
        }
    }

    private void setToolbarButtonActions(final View view, final Runnable click, final Runnable longClick, final Runnable swipeUp) {
        if (click != null) {
            view.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						v.requestFocus();
						click.run();}});
        }
        if (longClick != null) {
            view.setOnLongClickListener(new OnLongClickListener() {
					public boolean onLongClick(View v) {
						v.requestFocus();
						longClick.run();
						return true;
					}});
        }
		final GestureDetector gestureDetector = new GestureDetector(this, new ToolbarGestureDetector(this, swipeLeft, swipeRight, swipeUp, null));
		//noinspection AndroidLintClickableViewAccessibility
		view.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					v.requestFocus();
					return gestureDetector.onTouchEvent(event);}});
    }

	private class FlingRight implements Runnable {
		@Override
		public void run() {
			if (currentTabIndex > 0) {
				switchToTab(currentTabIndex-1);
			}
		}
	}
	
	private class FlingLeft implements Runnable {
		@Override
		public void run() {
			if (currentTabIndex < tabs.size() - 1) {
				switchToTab(currentTabIndex+1);
			}
		}
	}

    private void initFile(final Tab tab) {
		ExceptionLogger.d(TAG, "initFile chmFilePath " + tab.chmFilePath + ", password:");
		if (tab.utils != null) {
			tab.utils.chm.close();
			tab.utils = null;
		}
		tab.onEnterPassword = false;
		tab.webview.clearHistory();
        tab.historyIndex = -1;
		tab.listBookmark = new ArrayList<>();
		tab.listSite = new ArrayList<>();
		new AsyncTask<Void, Object, Void>() {
			ProgressDialog progress;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress = new ProgressDialog(MainActivity.this);
                progress.setTitle("Waiting");
                progress.setMessage("Extracting...");
                progress.setCancelable(false);
                progress.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                tab.md5File = Utils.checkSum(tab.chmFilePath);
                tab.extractPath = MainActivity.this.getExternalFilesDir("Reader") + "/" + tab.md5File;
				if (tab.password == null || tab.password.length() == 0) {
					tab.password = prefs.getString(tab.md5File+".password", "");
					tab.savePassword = tab.password.length() > 0;
				}
				ExceptionLogger.d(TAG, "Utils.chm4 " + tab.utils);// + ", tab.password " + tab.password);
				try {
					tab.utils = new Utils(new CHMFile(tab.chmFilePath));
					tab.utils.chm.setPassword(tab.password);
					final File file = new File(tab.extractPath);
					if (!(file.exists())) {
						try {
							if (tab.utils.extract(tab.chmFilePath, tab.extractPath, tab.password)) {
								ExceptionLogger.d(TAG, "initFile domparse " + tab.utils);
								tab.listSite = tab.utils.domparse(tab.chmFilePath, tab.extractPath, tab.md5File);
							} else {
								tab.listSite = new ArrayList<>();
								Toast.makeText(MainActivity.this, "Can't create temp folder", Toast.LENGTH_LONG).show();
								ExceptionLogger.d(TAG, "initFile Can't create temp folder. utils " + tab.utils);
							}
						} catch (IOException e) {
							ExceptionLogger.e(TAG, "initFile publishProgress " + tab.utils);
							FileUtil.deleteFolders(file, true, null, null);
							publishProgress(e);
						}
					} else {
						tab.listSite = Utils.getListSite(tab.extractPath, tab.md5File);
						tab.listBookmark = Utils.getBookmark(tab.extractPath, tab.md5File);
						tab.historyIndex = Utils.getHistory(tab.extractPath, tab.md5File);
						ExceptionLogger.d(TAG, "initFile read old saved historyIndex " + tab.historyIndex);// + ", listSite " + tab.listSite);
					}
				} catch (IOException e) {
					ExceptionLogger.e(TAG, "initFile utils " + tab.utils, e);
					publishProgress(e.getMessage());
				}
				return null;
            }

			@Override
            protected void onProgressUpdate(final Object...values) {
				if (values[0] instanceof IOException) {
					enterPassword(tab, (IOException)values[0]);
				} else {
					Toast.makeText(MainActivity.this, (String)values[0], Toast.LENGTH_LONG).show();
				}
			}

            @Override
            protected void onPostExecute(final Void aVoid) {
                super.onPostExecute(aVoid);
				try {
					if (progress != null) {
						progress.dismiss();
						progress = null;
					}
					if (tab.historyIndex == -1) {
						final String fileLowerCase = tab.chmFilePath.toLowerCase();
						if (fileLowerCase.endsWith(".epub")) {
							final String insideFileName = "OEBPS/toc.ncx";
							final InputStream resourceAsStream = tab.utils.chm.getResourceAsStream(insideFileName);
							final String content = new String(FileUtil.is2Barr(resourceAsStream, false));
							final String url = HtmlUtil.readValue(content, "src");
							tab.historyIndex = tab.listSite.indexOf("OEBPS/" + url);
						}
					}
					if (tab.listSite != null && tab.listSite.size() > 1) {
						final String history = tab.listSite.get(tab.historyIndex == -1 ? 1 : tab.historyIndex);
						ExceptionLogger.d(TAG, "initFile read historyIndex " + history);
						tab.cacheMedia = prefs.getBoolean(tab.md5File+".cacheMedia", cacheOffline);
						tab.webview.loadUrl("file://" + tab.extractPath + "/" + history);
						if (saveHistory)
							addHistory(tab.webview, new File(tab.chmFilePath).toURL().toString());//tab);
					}
				} catch (Throwable t) {
					Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
					ExceptionLogger.e(TAG, t);
				}
            }
        }.execute();
    }

//    private void addHistory(final Tab tab) {
//        if (placesDb == null || !placesDb.isOpen())
//			return;
//
//		final ContentValues valuesInsert = new ContentValues(2);
//		valuesInsert.put("title", tab.chmFilePath.substring(tab.chmFilePath.lastIndexOf("/")+1));
//		valuesInsert.put("url", tab.chmFilePath);
//		placesDb.insert("reader_history", null, valuesInsert);
//    }
	
    private void handleIntent(final Tab tab, final Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            tab.webview.findAllAsync(query);
            try {
                for (Method m : WebView.class.getDeclaredMethods()) {
                    if (m.getName().equals("setFindIsUp")) {
                        m.setAccessible(true);
                        m.invoke((tab.webview), true);
                        break;
                    }
                }
            } catch (Exception t) {
				ExceptionLogger.e(TAG, t.getMessage(), t);
            }
        } else {
			if (urlIntent.contains("+")) {
				Toast.makeText(this, urlIntent + "\nInvalid File Name. File name must not have + character.", Toast.LENGTH_LONG).show();
			} else if (urlIntent.contains("%")) {
				Toast.makeText(this, urlIntent + "\nInvalid File Name. File name must not have % character.", Toast.LENGTH_LONG).show();
			} else {
				tab.chmFilePath = urlIntent;//URLDecoder.decode()intent.getData().getPath());//.getStringExtra("fileName");
				initFile(tab);
				ExceptionLogger.d(TAG, "handleIntent utils " + tab.utils + ", intent " + intent);
			}
		}
    }

	private synchronized void enterPassword(final Tab tab, IOException e) {
		if (!tab.onEnterPassword) {
			ExceptionLogger.e(TAG, "enterPassword");
			tab.onEnterPassword = true;
			tab.webview.post(new Runnable() {
					@Override
					public void run() {
						final EditText editView = new EditText(MainActivity.this);
						editView.setSingleLine();
						editView.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
						editView.setTransformationMethod(PasswordTransformationMethod.getInstance());
						new AlertDialog.Builder(MainActivity.this)
							.setTitle("Password")
							.setView(editView)
							.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									tab.password = editView.getText().toString();
									initFile(tab);
									tab.webview.reload();
									ExceptionLogger.d(TAG, "Enter password finished");
								}})
							.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									tab.onEnterPassword = false;
								}})
							.setOnDismissListener(new DialogInterface.OnDismissListener() {
								@Override
								public void onDismiss(DialogInterface p1) {
									tab.onEnterPassword = false;
								}
							})
							.show();
					}
				});
		}
	}

	private String insideFileName(final Tab tab, String url) {
		final String temp = url.replaceAll("file:/+", "/");
		String insideFileName;
		if (!temp.startsWith(tab.extractPath)) {
			url = "file://" + tab.extractPath + temp;
			insideFileName = temp;
		} else {
			insideFileName = temp.substring(tab.extractPath.length());
		}
		int indexOf = insideFileName.indexOf("#");
		if (indexOf > 0) {
			insideFileName = insideFileName.substring(0, indexOf);
		}
		indexOf = insideFileName.indexOf("?");
		if (indexOf > 0) {
			insideFileName = insideFileName.substring(0, indexOf);
		}
		insideFileName = URLDecoder.decode(insideFileName);
		return insideFileName;
	}
}
