//package chm.cblink.nb.chmreader;
//
//import android.preference.PreferenceManager;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteException;
//import android.database.Cursor;
//import android.widget.FrameLayout;
//import android.app.ProgressDialog;
//import android.widget.ProgressBar;
//import java.util.ArrayList;
//import android.content.SharedPreferences;
//import android.widget.SearchView;
//import android.os.PowerManager;
//import android.os.Bundle;
//import android.content.Intent;
//import android.widget.Toast;
//import java.net.URLDecoder;
//import chm.cblink.nb.chmreader.lib.Utils;
//import android.widget.SimpleCursorAdapter;
//import android.view.Menu;
//import android.content.Context;
//import android.app.SearchManager;
//import android.webkit.WebSettings;
//import android.webkit.WebChromeClient;
//import android.content.ContentValues;
//import java.util.List;
//import android.webkit.WebView;
//import android.view.MenuItem;
//import java.lang.reflect.Method;
//import android.view.View;
//import android.widget.EditText;
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import java.io.File;
//import android.support.annotation.RequiresApi;
//import android.webkit.WebViewClient;
//import java.io.ByteArrayInputStream;
//import android.webkit.WebResourceRequest;
//import android.webkit.WebResourceResponse;
//import android.view.KeyEvent;
//import java.io.IOException;
//import java.io.InputStream;
//import android.widget.ImageView;
//import android.os.Build;
//import android.widget.TextView;
//import android.graphics.PorterDuff;
//import android.view.ViewGroup;
//import android.app.Activity;
//import android.widget.Button;
//import android.os.Handler;
//import android.os.Environment;
//import android.os.Message;
//import android.widget.ListView;
//import android.graphics.Bitmap;
//import android.os.AsyncTask;
//import chm.cblink.nb.chmreader.lib.CHMFile;
//import android.app.Dialog;
//import android.widget.ArrayAdapter;
//import android.widget.AdapterView;
//import java.io.BufferedInputStream;
//import android.text.Html;
//import android.Manifest;
//import org.apache.commons.compress.PasswordRequiredException;
//import net.gnu.util.FileUtil;
//import net.gnu.util.HtmlUtil;
//import net.gnu.common.ParentActivity;
//import net.gnu.common.ExceptionLogger;
//import net.gnu.common.PlacesDbHelper;
//import net.gnu.common.AndroidUtils;
//import net.gnu.common.CustomWebView;
//import net.gnu.chm.R;
//import android.view.WindowManager;
//import android.text.InputType;
//import java.util.regex.Pattern;
//import android.webkit.MimeTypeMap;
//import java.io.FileInputStream;
//import android.text.method.PasswordTransformationMethod;
//
//public class CHMActivity extends ParentActivity {
//
//	private static final int PERMISSION_REQUEST_READ_EXTERNAL = 4;
//
//	private FrameLayout webviews;
//	CustomWebView webview;
//	Utils utils;
//    String chmFilePath = "", extractPath, md5File, password;
//    private ProgressDialog progress;
//    private ProgressBar progressLoadWeb;
//    ArrayList<String> listSite;
//    ArrayList<String> listBookmark;
//    private int historyIndex = -1;
//
//	private SharedPreferences prefs;
//	private boolean wideMode;
//    private boolean enableJavascript;
//    private boolean blockNetwork;
//	private SearchView searchView;
//    private SQLiteDatabase placesDb;
//	CustomDialogBookmark bookmarkDialog;
//	private String TAG = "CHMActivity";
//	private boolean searchChanged;
//	private boolean secure;
//	private volatile boolean onEnterPassword = false;
//	private boolean cacheMedia;
//	private boolean passwordOK = false;
//
//	private boolean scrolling;
//    private PowerManager.WakeLock mWakeLock;
//
//	private int delay = 1000;
//	private int length = 768;
//	private int scrollY;
//	private int scrollMax;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//        ExceptionLogger.d(TAG, "onCreate " + savedInstanceState);
//        setContentView(R.layout.activity_chm);
//        webviews = (FrameLayout) findViewById(R.id.webviews);
//        prefs = PreferenceManager.getDefaultSharedPreferences(this);
//		wideMode = prefs.getBoolean("wideMode", true);
//        enableJavascript = prefs.getBoolean("enableJavascript", true);
//        blockNetwork = prefs.getBoolean("blockNetwork", true);
//		secure = prefs.getBoolean("secureChm", false);
//		if (secure) {
//			getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
//								 WindowManager.LayoutParams.FLAG_SECURE);
//		}
//		cacheMedia = prefs.getBoolean("cacheMedia", false);
//
//		final PowerManager pm = (PowerManager) CHMActivity.this.getSystemService(Context.POWER_SERVICE);
//		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
//		final Intent intent = getIntent();
//        //String dataString = revIntent.getData().getPath();
//		urlIntent = getUrlFromIntent(intent);
//        if (urlIntent != null && !urlIntent.isEmpty()) {
//			if (!hasOrRequestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
//										null,
//										PERMISSION_REQUEST_READ_EXTERNAL)) {
//				return;
//			}
//        }
//
//        try {
//            placesDb = new PlacesDbHelper(this).getWritableDatabase();
//        } catch (SQLiteException e) {
//            ExceptionLogger.e(TAG, "Can't open database", e);
//        }
//		initView();
//		if (savedInstanceState != null) {
//			listSite = savedInstanceState.getStringArrayList("listSite");
//			listBookmark = savedInstanceState.getStringArrayList("listBookmark");
//			chmFilePath = savedInstanceState.getString("chmFilePath");
//			extractPath = savedInstanceState.getString("extractPath");
//			md5File = savedInstanceState.getString("md5File");
//
//			initFile();
//			ExceptionLogger.d(TAG, "onCreate1 chmFilePath " + chmFilePath);
//		} else {
//			if (urlIntent.contains("+")) {
//				Toast.makeText(this, "Invalid File Name. File name must not have + character.", Toast.LENGTH_LONG).show();
//			} else if (urlIntent.contains("%")) {
//				Toast.makeText(this, "Invalid File Name. File name must not have % character.", Toast.LENGTH_LONG).show();
//			} else {
//				chmFilePath = URLDecoder.decode(urlIntent);//dataString);//.getStringExtra("fileName");
//				initFile();
//				ExceptionLogger.d(TAG, "onCreate2 chmFilePath " + chmFilePath + ", urlIntent " + urlIntent);
//			}
//		}
//    }
//
//    @Override
//    protected void onRestart() {
//		ExceptionLogger.d(TAG, "onRestart utils " + utils);
//		super.onRestart();
//	}
//
//    @Override
//    protected void onStart() {
//		ExceptionLogger.d(TAG, "onStart utils " + utils);
//		super.onStart();
//	}
//
//	@Override
//    protected void onNewIntent(final Intent intent) {
//		password = null;
//		passwordOK = false;
//        urlIntent = getUrlFromIntent(intent);
//        ExceptionLogger.d(TAG, "onNewIntent old utils " + utils + ", urlIntent " + urlIntent);
//        if (urlIntent != null && !urlIntent.isEmpty()) {
//			if (!hasOrRequestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
//										null,
//										PERMISSION_REQUEST_READ_EXTERNAL)) {
//				return;
//			}
//        } else {
//			return;
//		}
//		handleIntent(intent);
//	}
//
//    @Override
//    public boolean onCreateOptionsMenu(final Menu menu) {
//        ExceptionLogger.d(TAG, "onCreateOptionsMenu utils " + utils);
//        getMenuInflater().inflate(R.menu.menu_chm, menu);
//		menu.findItem(R.id.menu_wide_mode).setChecked(wideMode);
//        menu.findItem(R.id.menu_enable_javascript).setChecked(enableJavascript);
//        menu.findItem(R.id.menu_block_network).setChecked(blockNetwork);
//        menu.findItem(R.id.autoScroll).setChecked(scrolling);
//        menu.findItem(R.id.menu_secure).setChecked(secure);
//        menu.findItem(R.id.menu_enable_cache).setChecked(cacheMedia);
//
//        final SearchManager searchManager =
//			(SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        searchView =
//			(SearchView) menu.findItem(R.id.menu_search).getActionView();
//        searchView.setSearchableInfo(
//			searchManager.getSearchableInfo(getComponentName()));
//        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//				@Override
//				public boolean onClose() {
//					webview.clearMatches();
//					return false;
//				}
//			});
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//				@Override
//				public boolean onQueryTextSubmit(final String query) {
//					webview.findAllAsync(query);
//					searchChanged = false;
//					return false;
//				}
//
//				@Override
//				public boolean onQueryTextChange(final String newText) {
//					searchChanged = true;
//					return false;
//				}
//			});
//        return true;
//    }
//
//    @Override
//	protected void onRestoreInstanceState(Bundle savedInstanceState) {
//		ExceptionLogger.d(TAG, "onRestoreInstanceState savedInstanceState " + savedInstanceState);
//		super.onRestoreInstanceState(savedInstanceState);
//	}
//
//    @Override
//    protected void onResume() {
//        ExceptionLogger.d(TAG, "onResume utils " + utils + ", chmFilePath " + chmFilePath);
//		super.onResume();
//	}
//
//	@Override
//    protected void onPause() {
//        ExceptionLogger.d(TAG, "onPause utils " + utils);
//		super.onPause();
//        try {
//			if (progress != null) { 
//				progress.dismiss(); 
//				progress = null;
//			}
//            Utils.saveBookmark(extractPath, md5File, listBookmark);
//            saveHistory();
//		} catch (Exception ignored) {
//        }
//    }
//
//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		ExceptionLogger.d(TAG, "onSaveInstanceState outState " + outState);
//        outState.putStringArrayList("listSite", listSite);
//		outState.putStringArrayList("listBookmark", listBookmark);
//		outState.putString("chmFilePath", chmFilePath);
//		outState.putString("extractPath", extractPath);
//		outState.putString("md5File", md5File);
//
//		super.onSaveInstanceState(outState);
//	}
//
//    @Override
//	protected void onStop() {
//		ExceptionLogger.d(TAG, "onStop utils " + utils);
//		super.onStop();
//	}
//
//    @Override
//    protected void onDestroy() {
//		ExceptionLogger.d(TAG, "onDestroy utils " + utils);
//		super.onDestroy();
//		if (utils != null) {
//			utils.chm.close();
//			utils = null;
//		}
//	}
//
//    private void handleIntent(final Intent intent) {
//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            String query = intent.getStringExtra(SearchManager.QUERY);
//            webview.findAllAsync(query);
//            try {
//                for (Method m : WebView.class.getDeclaredMethods()) {
//                    if (m.getName().equals("setFindIsUp")) {
//                        m.setAccessible(true);
//                        m.invoke((webview), true);
//                        break;
//                    }
//                }
//            } catch (Exception t) {
//				ExceptionLogger.e(TAG, t.getMessage(), t);
//            }
//        } else {
//			if (urlIntent.contains("+")) {
//				Toast.makeText(this, "Invalid File Name. File name must not have + character.", Toast.LENGTH_LONG).show();
//			} else if (urlIntent.contains("%")) {
//				Toast.makeText(this, "Invalid File Name. File name must not have % character.", Toast.LENGTH_LONG).show();
//			} else {
//				chmFilePath = URLDecoder.decode(urlIntent);//intent.getData().getPath());//.getStringExtra("fileName");
//				ExceptionLogger.d(TAG, "handleIntent utils " + utils + ", chmFilePath " + chmFilePath);
//				initFile();
//				ExceptionLogger.d(TAG, "handleIntent utils " + utils + ", intent " + intent);
//			}
//		}
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(final MenuItem item) {
//        final int id = item.getItemId();
//		ExceptionLogger.d(TAG, "onOptionsItemSelected " + item + ", id " + id);
//		try {
//			if (id == R.id.menu_home) {
//				if (utils != null) {
//					webview.loadUrl("file://" + extractPath + "/" + listSite.get(1));
//				}
//			} else if (id == R.id.menu_sitemap) {
//				if (CHMFile.CHM_PATTERN.matcher(chmFilePath).matches()) {
//					if (utils != null) {
//						webview.loadUrl("file://" + extractPath + "/" + listSite.get(0));
//					}
//				} else {
//					new AlertDialog.Builder(CHMActivity.this)
//						.setTitle("Preview Media")
//						.setMessage("Preview Images and Videos?")
//						.setPositiveButton("No", new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int which) {
//								if (utils != null) {
//									webview.loadUrl("file://" + extractPath + "/" + listSite.get(0)+"_nopreview");
//								}
//							}})
//						.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int which) {
//								if (utils != null) {
//									webview.loadUrl("file://" + extractPath + "/" + listSite.get(0));
//								}
//							}})
//						.show();
//				}
//			} else if (id == R.id.autoScroll) {
//				if (scrolling) {
//					stop();
//					item.setChecked(scrolling);
//				} else {
//					final View rootView = getLayoutInflater().inflate(R.layout.auto_scroll_webview, null);
//					final EditText delayET = (EditText) rootView.findViewById(R.id.delayET);
//					delayET.setText("" + (delay*1));
//					delayET.setInputType(InputType.TYPE_CLASS_NUMBER);
//					final EditText lengthET = (EditText) rootView.findViewById(R.id.lengthET);
//					lengthET.setInputType(InputType.TYPE_CLASS_NUMBER);
//					lengthET.setText("" + length);
//					new AlertDialog.Builder(CHMActivity.this)
//						.setTitle("Scroll")
//						.setView(rootView)
//						.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int which) {
//								delay = Integer.valueOf(delayET.getText().toString());
//								if (delay <= 0) {
//									delay = 1000;
//								}
//								length = Integer.valueOf(lengthET.getText().toString());
//								if (length <= 0) {
//									length = 768;
//								}
//								if (mWakeLock != null) {
//									mWakeLock.acquire();
//								}
//								start();
//								item.setChecked(scrolling);
//							}})
//						.setNegativeButton("Cancel", new EmptyOnClickListener())
//						.show();
//				}
//			} else if (id == R.id.menu_back_page) {
//				ExceptionLogger.d(TAG, "menu_back_page utils " + utils);
//				if (utils != null) {
//					final CharSequence query = searchView.getQuery();
//					if (query.length() > 0) {
//						if (searchChanged) {
//							webview.findAllAsync(query.toString());
//							searchChanged = false;
//						}
//						webview.findNext(false);
//					} else
//					if (historyIndex == 1) {
//						Toast.makeText(this, "First site", Toast.LENGTH_SHORT).show();
//					} else {
//						if (historyIndex > 1) {//} && tempIndex - 1 < listSite.size()) {
//							webview.loadUrl("file://" + extractPath + "/" + listSite.get(--historyIndex));
//						}
//					}
//				}
//			} else if (id == R.id.menu_next_page) {
//				ExceptionLogger.d(TAG, "menu_next_page utils " + utils);
//				if (utils != null) {
//					final CharSequence query = searchView.getQuery();
//					if (query.length() > 0) {
//						if (searchChanged) {
//							webview.findAllAsync(query.toString());
//							searchChanged = false;
//						}
//						webview.findNext(true);
//					} else
//					if (historyIndex == listSite.size() - 1) {
//						Toast.makeText(this, "End site", Toast.LENGTH_SHORT).show();
//					} else {
//						if (historyIndex < listSite.size() - 1) {//tempIndex - 1 >= 0 && 
//							webview.loadUrl("file://" + extractPath + "/" + listSite.get(++historyIndex));
//						}
//					}
//				}
//			} else if (id == R.id.menu_enable_cache) {
//				cacheMedia = !cacheMedia;
//				prefs.edit().putBoolean("cacheMedia", cacheMedia).apply();
//				item.setChecked(cacheMedia);
//			} else if (id == R.id.menu_history) {
//				showHistory();
//			} else if (id == R.id.menu_bookmark) {
//				if (utils != null) {
//					bookmarkDialog = new CustomDialogBookmark(this);
//					bookmarkDialog.show();
//				}
//			} else if (id == android.R.id.home) {
//				this.finish();
//			} else if (id == R.id.menu_wide_mode) {
//				wideMode = !wideMode;
//				prefs.edit().putBoolean("wideMode", wideMode).apply();
//				final WebSettings settings = webview.getSettings();
//				settings.setUseWideViewPort(wideMode);
//				settings.setLoadWithOverviewMode(wideMode);
//				item.setChecked(wideMode);
//			} else if (id == R.id.menu_enable_javascript) {
//				enableJavascript = !enableJavascript;
//				prefs.edit().putBoolean("enableJavascript", enableJavascript).apply();
//				final WebSettings settings = webview.getSettings();
//				settings.setJavaScriptEnabled(enableJavascript);
//				item.setChecked(enableJavascript);
//			} else if (id == R.id.menu_block_network) {
//				blockNetwork = !blockNetwork;
//				prefs.edit().putBoolean("blockNetwork", blockNetwork).apply();
//				final WebSettings settings = webview.getSettings();
//				settings.setBlockNetworkLoads(blockNetwork);
//				item.setChecked(blockNetwork);
//			} else if (id == R.id.menu_secure) {
//				secure = !secure;
//				prefs.edit().putBoolean("secureChm", secure).apply();
//				item.setChecked(secure);
//				finish();
//			} else if (id == R.id.menu_clear_cache) {
//				final File file = new File(extractPath);
//				final Pattern dotPattern = Pattern.compile("[^\"]*?\\.[^\"]*?");
//				final long[] cur = FileUtil.getDirSize(file, dotPattern, null);
//				final long[] all = FileUtil.getDirSize(file.getParentFile(), dotPattern, null);
//				new AlertDialog.Builder(this)
//					.setTitle("Delete cache?")
//					.setMessage("This action cannot be undone\nCurrent compressed file: "
//								+ cur[0] + " bytes, " + cur[1] + " files, " + cur[2] + " folders."
//								+ "\nAll cache: " + all[0] + " bytes, " + all[1] + " files, " + all[2] + " folders.")
//					.setPositiveButton("Delete Current", new DialogInterface.OnClickListener() {
//						public void onClick(final DialogInterface dialog, final int which) {
//							FileUtil.deleteFolders(new File(extractPath), true, dotPattern, null);
//							AndroidUtils.toast(CHMActivity.this, "Finished delete current cache");
//						}
//					})
//					.setNegativeButton("Delete All", new DialogInterface.OnClickListener() {
//						public void onClick(final DialogInterface dialog, final int which) {
//							FileUtil.deleteFolders(CHMActivity.this.getExternalFilesDir("Reader"), true, dotPattern, null);
//							AndroidUtils.toast(CHMActivity.this, "Finished delete all cache");
//						}
//					})
//					.setNeutralButton("Cancel", new EmptyOnClickListener())
//					.show();
//			} else if (id == R.id.menu_search_all) {
//				if (utils != null) {
//					CustomDialogSearchAll searchALlDialog = new CustomDialogSearchAll(this);
//					searchALlDialog.show();
//				}
//			} else if (id == R.id.menu_exit) {
//				finish();
//			}
//		} catch (Throwable t) {
//			ExceptionLogger.e(TAG, t.getMessage(), t);
//		}
//        return true;
//    }
//
//	private void stop() {
//		scrolling = false;
//        //onStopListener?.invoke();
//		if (mWakeLock != null) {
//			mWakeLock.release();
//			mWakeLock = null;
//		}
//    }
//
//	private void start() {
//		scrolling = true;
//		scrollY = webview.computeVerticalScrollOffsetMethod();
//		new Thread(new Runnable() {
//				@Override
//				public void run() {
//					while (scrolling) {
//						try {
//							Thread.sleep(delay);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//						if (scrolling) {
//							webview.post(new Runnable() {
//									@Override
//									public void run() {
//										scrollY = webview.getScrollY() + length;
//										scrollMax = webview.computeVerticalScrollRangeMethod() - webview.computeVerticalScrollExtentMethod();
//										if (scrollY > scrollMax) {
//											scrollY = scrollMax;
//											stop();
//										}
//										webview.scrollTo(webview.getScrollX(), scrollY);
//									}
//								});
//						}
//					}
//				}
//			}).start();
//	}
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_DOWN) {
//            switch (keyCode) {
//                case KeyEvent.KEYCODE_BACK:
//					if (webview.canGoBack()) {
//                        webview.goBack();
//                    } else {
//                        finish();
//                    }
//                    //return false;
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//	public void finish() {
//		if (utils != null) {
//			utils.chm.close();
//			utils = null;
//		}
//		password = null;
//		passwordOK = false;
//		super.finish();
//	}
//
//    private void initView() {
//		if (webview != null) {
//			return;
//		}
//        //webview = (WebView) findViewById(R.id.webview);
//        webview = new CustomWebView(this);
//		webview.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
//        webview.setWebChromeClient(new WebChromeClient() {
//				@Override
//				public void onProgressChanged(WebView view, int newProgress) {
//					super.onProgressChanged(view, newProgress);
//					progressLoadWeb.setProgress(newProgress);
//				}
//			});
//        webview.setOnLongClickListener(new View.OnLongClickListener() {
//				public boolean onLongClick(View v) {
//					String url = null, imageUrl = null, text = "";
//					final WebView.HitTestResult r = ((WebView) v).getHitTestResult();
//					final Handler handler = new Handler();
//					final Message message = handler.obtainMessage();
//					((WebView)v).requestFocusNodeHref(message);
//					Bundle bundle = message.getData();
//					switch (r.getType()) {
//						case WebView.HitTestResult.SRC_ANCHOR_TYPE:
//							url = r.getExtra();
//							text = bundle.getString("title");
//							if (text == null) {
//								text = "";
//							}
//							break;
//						case WebView.HitTestResult.IMAGE_TYPE: {
//								imageUrl = r.getExtra();
//								text = bundle.getString("title");
//								if (text == null || text.length() == 0) {
//									text = bundle.getString("alt");
//									if (text == null)
//										text = "";
//								}
//								break;
//							}
//						case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
//						case WebView.HitTestResult.PHONE_TYPE:
//						case WebView.HitTestResult.GEO_TYPE:
//						case WebView.HitTestResult.EMAIL_TYPE:
//						case WebView.HitTestResult.UNKNOWN_TYPE:
//							url = bundle.getString("url");
//							if ("".equals(url)) {
//								url = null;
//							}
//							imageUrl = bundle.getString("src");
//							if ("".equals(imageUrl)) {
//								imageUrl = null;
//							}
//							if (url == null && imageUrl == null) {
//								return false;
//							}
//							text = bundle.getString("title");
//							if (text == null || text.length() == 0) {
//								text = bundle.getString("alt");
//								if (text == null)
//									text = "";
//							}
//							break;
//						default:
//							return false;
//					}
//					showLongPressMenu(url, imageUrl, text);
//					return true;
//				}});
//		webview.setWebViewClient(new WebViewClient() {
//				@Override
//				public void onPageStarted(final WebView view, String url, Bitmap favicon) {
//					ExceptionLogger.d(TAG, "onPageStarted " + url);
//					if (url.startsWith("file") && !url.endsWith(md5File) && !url.endsWith(md5File+"_nopreview")) {
//						String temp = url.substring("file://".length());
//						if (!temp.startsWith(extractPath)) {
//							url = "file://" + extractPath + temp;
//						} else {
//							temp = temp.substring(extractPath.length());
//						}
//						int i = 0;
//						for (String s : listSite) {
//							final String insideFileName = insideFileName(temp);
//							if (s.equalsIgnoreCase(insideFileName)
//								|| ("/"+s).equalsIgnoreCase(insideFileName)) {
//								historyIndex = i;
//								break;
//							}
//							i++;
//						}
//					}
//
//					super.onPageStarted(view, url, favicon);
//					progressLoadWeb.setProgress(50);
//				}
//
//				@Override
//				public void onPageFinished(WebView view, String url) {
//					super.onPageFinished(view, url);
//					progressLoadWeb.setProgress(100);
//				}
//
//				@Override
//				public void onLoadResource(final WebView view, String url) {
//					if (url.startsWith("file") && !url.endsWith(md5File) && !url.endsWith(md5File+"_nopreview")) {
//						final String temp = url.substring("file://".length());
//						if (!temp.startsWith(extractPath)) {
//							url = "file://" + extractPath + temp;
//						}
//					}
//					super.onLoadResource(view, url);
//				}
//
//
//				@Override
//				public WebResourceResponse shouldInterceptRequest(final WebView view, final String url) {
//					//ExceptionLogger.d(TAG, "shouldInterceptRequest Utils.chm " + Utils.chm + ", url " + url);
//					if (utils != null && url.startsWith("file") && !url.endsWith(md5File) && !url.endsWith(md5File+"_nopreview")) {
//						final String insideFileName = insideFileName(url);
//						ExceptionLogger.d(TAG, "shouldInterceptRequest insideFileName " + insideFileName + ", url " + url);
//
//						if (ParentActivity.HTML_PATTERN.matcher(insideFileName).matches()
//							|| ParentActivity.IMAGES_PATTERN.matcher(insideFileName).matches()
//							|| ParentActivity.MEDIA_PATTERN.matcher(insideFileName).matches()) {
//							try {
//								//ExceptionLogger.d(TAG, "shouldInterceptRequest " + file.getAbsolutePath());
//								final String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(FileUtil.getExtension(insideFileName));
//								boolean ok = false;
//								for (String s : listSite) {
//									if (s.equalsIgnoreCase(insideFileName)
//										|| ("/"+s).equalsIgnoreCase(insideFileName)) {
//										ok = true;
//										break;
//									}
//								}
//								if (!ok) {
//									return new WebResourceResponse(mime, "", new ByteArrayInputStream(new byte[0]));
//								}
//								final File file = new File(extractPath + insideFileName);
//								//ExceptionLogger.e(TAG, "passwordOK " + passwordOK);
//								if (!file.exists() || (file.exists() && !passwordOK)) {
//									final InputStream resourceAsStream = utils.chm.getResourceAsStream(insideFileName);
//									passwordOK = true;
//									//ExceptionLogger.e(TAG, "passwordOK " + passwordOK);
//									if (!file.exists() && cacheMedia && resourceAsStream != null) {
//										FileUtil.is2File(resourceAsStream, file.getAbsolutePath());
//									}
//									return new WebResourceResponse(mime, "", resourceAsStream != null ? resourceAsStream : new ByteArrayInputStream(new byte[0]));
//								} else {
//									//ExceptionLogger.e(TAG, "file.exists() && passwordOK " + passwordOK);
//									return new WebResourceResponse(mime, "", new BufferedInputStream(new FileInputStream(file)));
//								}
//							} catch (PasswordRequiredException|IOException e) {
//								enterPassword(e);
//								//return new WebResourceResponse("", "", new ByteArrayInputStream(new byte[0]));
//							} catch (Throwable e) {
//								ExceptionLogger.e(TAG, e.getMessage(), e);
//							}
//						} else {
//							try {
//								utils.extractSpecificFile(chmFilePath, extractPath + insideFileName, insideFileName);
//							} catch (PasswordRequiredException e) {
//								enterPassword(e);
//							} catch (Throwable e) {
//								ExceptionLogger.e(TAG, "Error extract file: " + insideFileName, e);
//							} 
//						}
//					}
//					return super.shouldInterceptRequest(view, url);
//				}
//
//
//				@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//				@Override
//				public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//					return shouldInterceptRequest(view, request.getUrl().toString());
//				}
//
//				@Override
//				public boolean shouldOverrideUrlLoading(WebView view, String url) {
//					ExceptionLogger.d(TAG, "shouldOverrideUrlLoading url " + url);
//					if (url.startsWith("file") && !url.endsWith(md5File) && !url.endsWith(md5File+"_nopreview")) {
//						final String temp = url.substring("file://".length());
//						if (!temp.startsWith(extractPath)) {
//							url = "file://" + extractPath + temp;
//							view.loadUrl(url);
//							return true;
//						}
//					}
//					return false;
//				}
//
//				@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//				@Override
//				public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//					return shouldOverrideUrlLoading(view, request.getUrl().toString());
//					//return super.shouldOverrideUrlLoading(view, request);
//				}
//			});
//        final WebSettings settings = webview.getSettings();
//		settings.setBuiltInZoomControls(true);
//        settings.setDisplayZoomControls(false);
//        settings.setUseWideViewPort(wideMode);
//        settings.setLoadWithOverviewMode(wideMode);
//        settings.setJavaScriptEnabled(enableJavascript);
//        settings.setBlockNetworkLoads(blockNetwork);
//        settings.setLoadsImagesAutomatically(true);
////        ((ClickableWebView) webview).setOnWebViewClickListener(new OnWebViewClicked() {
////            @Override
////            public void onClick(String url) {
////                Toast.makeText(MainActivity.this, url, Toast.LENGTH_SHORT).show();
////            }
////        });
//
//		progressLoadWeb = (ProgressBar) findViewById(R.id.progressBar);
//        progressLoadWeb.setMax(100);
//		webviews.addView(webview);
//
//    }
//
//	private synchronized void enterPassword(IOException e) {
//		if (!onEnterPassword) {
//			ExceptionLogger.e(TAG, "enterPassword");
//			onEnterPassword = true;
//			webview.post(new Runnable() {
//					@Override
//					public void run() {
//						final EditText editView = new EditText(CHMActivity.this);
//						editView.setSingleLine();
//						editView.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
//						editView.setTransformationMethod(PasswordTransformationMethod.getInstance());
//						new AlertDialog.Builder(CHMActivity.this)
//							.setTitle("Password")
//							.setView(editView)
//							.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog, int which) {
//									password = editView.getText().toString();
//									initFile();
//									webview.reload();
//									ExceptionLogger.d(TAG, "Enter password finished");
//								}})
//							.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog, int which) {
//									onEnterPassword = false;
//								}})
//							.setOnDismissListener(new DialogInterface.OnDismissListener() {
//								@Override
//								public void onDismiss(DialogInterface p1) {
//									onEnterPassword = false;
//								}
//							})
//							.show();
//					}
//				});
//		}
//	}
//
//	private String insideFileName(String url) {
//		final String temp = url.replaceAll("file:/+", "/");
//		String insideFileName;
//		if (!temp.startsWith(extractPath)) {
//			url = "file://" + extractPath + temp;
//			insideFileName = temp;
//		} else {
//			insideFileName = temp.substring(extractPath.length());
//		}
//		int indexOf = insideFileName.indexOf("#");
//		if (indexOf > 0) {
//			insideFileName = insideFileName.substring(0, indexOf);
//		}
//		indexOf = insideFileName.indexOf("?");
//		if (indexOf > 0) {
//			insideFileName = insideFileName.substring(0, indexOf);
//		}
//		insideFileName = URLDecoder.decode(insideFileName);
//		return insideFileName;
//	}
//
//    void showLongPressMenu(final String linkUrl, final String imageUrl, final String text) {
//        final String url;
//        final String title;
//        String[] options = new String[]{"Add Bookmark", "Copy link text", "Copy link", "Show link", "Share link", "Download"};
//		final String[] imageOptions = new String[]{
//			"Add Bookmark", "Copy link text", "Copy link", "Show link", "Share link", "Download", 
//			"Copy image link", "Show image link", "Share image link", "Download Image"};
//
//        if (imageUrl == null) {
//            if (linkUrl == null) {
//                throw new IllegalArgumentException("Bad null arguments in showLongPressMenu");
//            } else {
//                // Text link
//                url = linkUrl;
//                title = linkUrl;
//            }
//        } else {
//            if (linkUrl == null) {
//                // Image without link
//                url = imageUrl;
//                title = "Image: " + imageUrl;
//            } else {
//                // Image with link
//                url = linkUrl;
//                title = linkUrl;
//                options = imageOptions;
//            }
//        }
//        final String downloadLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
//		new AlertDialog.Builder(this)
//			.setTitle(title)
//			.setItems(options, new DialogInterface.OnClickListener() {
//				public void onClick(final DialogInterface dialog, final int which) {
//					switch (which) {
//						case 0:
//							if (url.startsWith("file://" + extractPath)) {
//								final String url2 = URLDecoder.decode(url).substring(("file://" + extractPath).length() + 1);
//								if (listBookmark.indexOf(url2) == -1) {
//									listBookmark.add(url2);
//								} else {
//									Toast.makeText(CHMActivity.this, "Bookmark already exist", Toast.LENGTH_SHORT).show();
//								}
//							} else {
//								Toast.makeText(CHMActivity.this, "Can't bookmark external link", Toast.LENGTH_LONG).show();
//							}
//							break;
//						case 1:
//							AndroidUtils.copyClipboard(CHMActivity.this, "Text", text.trim());
//							break;
//						case 2:
//							AndroidUtils.copyClipboard(CHMActivity.this, "URL", url);
//							break;
//						case 3:
//							new AlertDialog.Builder(CHMActivity.this)
//								.setTitle("Full URL")
//								.setMessage(url)
//								.setPositiveButton("OK", new EmptyOnClickListener())
//								.show();
//							break;
//						case 4:
//							AndroidUtils.shareUrl(CHMActivity.this, url);
//							break;
//						case 5:
//							try {
//								if (url.startsWith("file:/")) {
//									final String insideFileName = insideFileName(url);
//									final InputStream resourceAsStream = utils.chm.getResourceAsStream(insideFileName);
//									final String name = insideFileName.substring(insideFileName.lastIndexOf("/") + 1);
//									FileUtil.saveISToFile(resourceAsStream, downloadLocation, name, true, false);
//									AndroidUtils.toast(CHMActivity.this, "Saved " + downloadLocation + "/" + name);
//								} else {
//									Toast.makeText(CHMActivity.this, "Can't save " + url, Toast.LENGTH_LONG).show();
//								}
//							} catch (Throwable e) {
//								Toast.makeText(CHMActivity.this, "Can't save " + url, Toast.LENGTH_LONG).show();
//								ExceptionLogger.e(TAG, e.getMessage(), e);
//							}
//							break;
//						case 6:
//							AndroidUtils.copyClipboard(CHMActivity.this, "URL", imageUrl);
//							break;
//						case 7:
//							new AlertDialog.Builder(CHMActivity.this)
//								.setTitle("Full imageUrl")
//								.setMessage(imageUrl)
//								.setPositiveButton("OK", new EmptyOnClickListener())
//								.show();
//							break;
//						case 8:
//							AndroidUtils.shareUrl(CHMActivity.this, imageUrl);
//							break;
//						case 9:
//							try {
//								if (imageUrl.startsWith("file:/")) {
//									final String insideFileName = insideFileName(imageUrl);
//									final InputStream resourceAsStream = utils.chm.getResourceAsStream(insideFileName);
//									final String name = insideFileName.substring(insideFileName.lastIndexOf("/") + 1);
//									FileUtil.saveISToFile(resourceAsStream, downloadLocation, name, true, false);
//									AndroidUtils.toast(CHMActivity.this, "Saved " + downloadLocation + "/" + name);
//								} else {
//									Toast.makeText(CHMActivity.this, "Can't save " + imageUrl, Toast.LENGTH_LONG).show();
//								}
//							} catch (Throwable e) {
//								Toast.makeText(CHMActivity.this, "Can't save " + imageUrl, Toast.LENGTH_LONG).show();
//								ExceptionLogger.e(TAG, e.getMessage(), e);
//							}
//							break;
//					}
//				}}).show();
//	}
//
//    private void initFile() {
//		ExceptionLogger.d(TAG, "initFile chmFilePath " + chmFilePath + ", password:");
//		if (utils != null) {
//			utils.chm.close();
//			utils = null;
//		}
//		onEnterPassword = false;
//		webview.clearHistory();
//        historyIndex = -1;
//		new AsyncTask<Void, PasswordRequiredException, Void>() {
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                progress = new ProgressDialog(CHMActivity.this);
//                progress.setTitle("Waiting");
//                progress.setMessage("Extracting...");
//                progress.setCancelable(false);
//                progress.show();
//            }
//
//            @Override
//            protected Void doInBackground(Void... voids) {
//                md5File = Utils.checkSum(chmFilePath);
//                extractPath = CHMActivity.this.getExternalFilesDir("Reader") + "/" + md5File;
//                ExceptionLogger.d(TAG, "Utils.chm4 " + utils);
//				try {
//					utils = new Utils(new CHMFile(chmFilePath));
//					utils.chm.setPassword(password);
//					File file = new File(extractPath);
//					if (!(file.exists())) {
//						listBookmark = new ArrayList<>();
//						try {
//							if (utils.extract(chmFilePath, extractPath, password)) {
//								ExceptionLogger.d(TAG, "initFile domparse " + utils);
//								listSite = utils.domparse(chmFilePath, extractPath, md5File);
//							} else {
//								listSite = new ArrayList<>();
//								Toast.makeText(CHMActivity.this, "Can't create temp folder", Toast.LENGTH_LONG).show();
//								ExceptionLogger.d(TAG, "initFile Can't create temp folder. Utils.chm " + utils);
//							}
//						} catch (PasswordRequiredException e) {
//							ExceptionLogger.e(TAG, "initFile publishProgress " + utils);
//							FileUtil.deleteFolders(file, true, null, null);
//							publishProgress(e);
//						}
//					} else {
//						listSite = Utils.getListSite(extractPath, md5File);
//						listBookmark = Utils.getBookmark(extractPath, md5File);
//						historyIndex = Utils.getHistory(extractPath, md5File);
//						ExceptionLogger.d(TAG, "initFile read old saved historyIndex " + historyIndex);// + ", listSite " + listSite);
//					}
//				} catch (IOException e) {
//					ExceptionLogger.e(TAG, "initFile utils " + utils, e);
//					Toast.makeText(CHMActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
//				}
//				return null;
//            }
//
//			@Override
//            protected void onProgressUpdate(PasswordRequiredException...values) {
//				enterPassword(values[0]);
//			}
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//				try {
//					if (progress != null) {
//						progress.dismiss();
//						progress = null;
//					}
//					if (historyIndex == -1) {
//						final String fileLowerCase = chmFilePath.toLowerCase();
//						if (fileLowerCase.endsWith(".epub")) {
//							final String insideFileName = "OEBPS/toc.ncx";
//							final InputStream resourceAsStream = utils.chm.getResourceAsStream(insideFileName);
//							final String content = new String(FileUtil.is2Barr(resourceAsStream, false));
//							final String url = HtmlUtil.readValue(content, "src");
//							historyIndex = listSite.indexOf("OEBPS/" + url);
//						}
//					}
//					if (listSite != null && listSite.size() > 1) {
//						final String history = listSite.get(historyIndex == -1 ? 1 : historyIndex);
//						ExceptionLogger.d(TAG, "initFile read historyIndex " + history);
//						webview.loadUrl("file://" + extractPath + "/" + history);
//						addHistory();
//					}
//				} catch (Throwable t) {
//					Toast.makeText(CHMActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
//					ExceptionLogger.e(TAG, t);
//				}
//            }
//        }.execute();
//    }
//
//	private void saveHistory() {
////		String url = webview.getUrl();
////		int length = ("file://" + extractPath).length() + 1;
////		if (url != null && url.length() > length) {
////			url = URLDecoder.decode(url).substring(length);
////			int index = listSite.indexOf(url);
////			if (index != -1) {
//				Utils.saveHistory(extractPath, md5File, historyIndex);
////			}
////		}
//	}
//
//    private void addHistory() {
//        if (placesDb == null || !placesDb.isOpen())
//			return;
//
//		final ContentValues valuesInsert = new ContentValues(2);
//		valuesInsert.put("title", chmFilePath.substring(chmFilePath.lastIndexOf("/")+1));
//		valuesInsert.put("url", chmFilePath);
//		placesDb.insert("reader_history", null, valuesInsert);
//    }
//
//	private Cursor cursorHistory;
//	private SimpleCursorAdapter historyAdapter;
//	private List<Integer> selectedItems;
//	private class HistoryHolder {
//		final ImageView iconView;
//		final ImageView moreView;
//		final TextView titleView;
//		final TextView domainView;
//		int position;
//		Integer id;
//		String url;
//		HistoryHolder(final View convertView) {
//			moreView = (ImageView) convertView.findViewById(R.id.close);
//			iconView = (ImageView) convertView.findViewById(R.id.icon);
//			titleView = (TextView) convertView.findViewById(R.id.name);
//			domainView = (TextView) convertView.findViewById(R.id.domain);
//
//			moreView.setOnClickListener(new View.OnClickListener() {
//					@Override
//					public void onClick(View button) {
//						cursorHistory.moveToPosition(position);
//						final int rowid = cursorHistory.getInt(cursorHistory.getColumnIndex("_id"));
////						final String title = cursorHistory.getString(cursorHistory.getColumnIndex("title"));
////						final String url = cursorHistory.getString(cursorHistory.getColumnIndex("url"));
//						placesDb.execSQL("DELETE FROM reader_history WHERE _id = ?", new Object[] {rowid});
//						cursorHistory = placesDb.rawQuery("SELECT _id, title, url FROM reader_history ORDER BY title ASC", null);
//						historyAdapter.swapCursor(cursorHistory);
//					}
//				});
//			iconView.setOnClickListener(new View.OnClickListener() {
//					@Override
//					public void onClick(View button) {
//						if (selectedItems.contains(id)) {
//							selectedItems.remove(id);
//							if (chmFilePath.equals(url)) {
//								iconView.setImageResource(R.drawable.ic_action_eye_open);
//							} else {
//								iconView.setImageResource(R.drawable.dot);
//							}
//						} else {
//							selectedItems.add(id);
//							iconView.setImageResource(R.drawable.ic_accept);
//						}
//					}
//				});
//			moreView.setColorFilter(0xff808080, PorterDuff.Mode.SRC_IN);
//			moreView.setTag(this);
//			convertView.setTag(this);
//			iconView.setTag(this);
//			titleView.setTag(this);
//		}
//	}
//	private void showHistory() {
//        if (placesDb == null)
//			return;
//		selectedItems = new ArrayList<>();
//        cursorHistory = placesDb.rawQuery("SELECT _id, title, url FROM reader_history ORDER BY title ASC", null);
//        historyAdapter = new SimpleCursorAdapter(this,
//												 R.layout.reader_history_list_item,
//												 cursorHistory,
//												 new String[] { "title", "url" },
//												 new int[] { R.id.name, R.id.domain }) {
//			@Override
//			public View getView(int position, View convertView, ViewGroup parent) {
//				final HistoryHolder holder;
//				if (convertView == null) {
//					convertView = super.getView(position, convertView, parent);//getLayoutInflater().inflate(R.layout.list_item, parent, false);
//					holder = new HistoryHolder(convertView);
//				} else {
//					holder = (HistoryHolder) convertView.getTag();
//				}
//				holder.position = position;
//				cursorHistory.moveToPosition(position);
//				holder.id = cursorHistory.getInt(cursorHistory.getColumnIndex("_id"));
//				holder.url = cursorHistory.getString(cursorHistory.getColumnIndex("url"));
//				if (chmFilePath.equals(holder.url)) {
//					holder.iconView.setImageResource(R.drawable.ic_action_eye_open);
//				} else if (selectedItems.contains(holder.id)) {
//					holder.iconView.setImageResource(R.drawable.ic_accept);
//				} else {
//					holder.iconView.setImageResource(R.drawable.dot);
//				}
//				final TextView titleView = holder.titleView;
//				final TextView domainView = holder.domainView;
//				final String title = cursorHistory.getString(cursorHistory.getColumnIndex("title"));
//				titleView.setText(title);
//				domainView.setText(holder.url);
//				return convertView;
//			}
//		};
//		final AlertDialog dialog = new AlertDialog.Builder(this)
//			.setTitle("History")
//			.setPositiveButton("Close", new EmptyOnClickListener())
//			.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int which) {
//					final int size = selectedItems.size();
//					if (size > 0) {
//						final StringBuilder sb = new StringBuilder();
//						for (int i = 0; i < size; i++) {
//							sb.append("?");
//							if (i < size - 1) {
//								sb.append(",");
//							}
//						}
//						cursorHistory.close();
//						placesDb.execSQL("DELETE FROM reader_history WHERE _id IN (" + sb.toString()+ ")", selectedItems.toArray());
//					}
//
//				}})
//			.setOnDismissListener(new DialogInterface.OnDismissListener() {
//				public void onDismiss(DialogInterface p1) {
//					cursorHistory.close();}})
//			.setAdapter(historyAdapter, new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int which) {
//					saveHistory();
//					cursorHistory.moveToPosition(which);
//					final String st = cursorHistory.getString(cursorHistory.getColumnIndex("url"));
//					cursorHistory.close();
//					if (!chmFilePath.equalsIgnoreCase(st)) {
//						chmFilePath = st;
//						ExceptionLogger.d(TAG, "History chmFilePath " + chmFilePath);
//						password = null;
//						passwordOK = false;
//
//						initFile();
//					}
//				}})
//			.create();
//        dialog.show();
//	}
//}
//
//class CustomDialogBookmark extends Dialog implements View.OnClickListener {
//
//	final CHMActivity chmActivity;
//	Button add;
//	Button close;
//	ListView listView;
//	ArrayAdapter adapter;
//
//	public CustomDialogBookmark(final CHMActivity activity) {
//		super(activity);
//		this.chmActivity = activity;
//	}
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setTitle("List bookmarks");
//		setContentView(R.layout.dialog_bookmark);
//		setCanceledOnTouchOutside(true);
//		setCancelable(true);
//		add = (Button) findViewById(R.id.btn_addbookmark);
//		add.setOnClickListener(this);
//		close = (Button) findViewById(R.id.btn_close);
//		close.setOnClickListener(this);
//		listView = (ListView) findViewById(R.id.listView);
//		adapter = new ArrayAdapter<String>(chmActivity, android.R.layout.simple_list_item_1, chmActivity.listBookmark);
//		listView.setAdapter(adapter);
//		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//				@Override
//				public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//					chmActivity.webview.loadUrl("file://" + chmActivity.extractPath + "/" + chmActivity.listBookmark.get(i));
//					dismiss();
//				}
//			});
//		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//				@Override
//				public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4) {
//					chmActivity.listBookmark.remove(chmActivity.listBookmark.get(p3));
//					adapter.notifyDataSetChanged();
//					return false;
//				}
//			});
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//			case R.id.btn_addbookmark:
//				String url = URLDecoder.decode(chmActivity.webview.getUrl()).substring(("file://" + chmActivity.extractPath).length() + 1);
//				if (chmActivity.listBookmark.indexOf(url) == -1) {
//					chmActivity.listBookmark.add(url);
//					adapter.notifyDataSetChanged();
//				} else {
//					Toast.makeText(chmActivity, "Bookmark already exist", Toast.LENGTH_SHORT).show();
//				}
//				break;
//			case R.id.btn_close:
//				chmActivity.bookmarkDialog.dismiss();
//				break;
//		}
//	}
//}
//
//class CustomDialogSearchAll extends Dialog implements View.OnClickListener {
//
//	private String TAG = "CustomDialogSearchAll";
//	
//	final CHMActivity chmActivity;
//	Button search;
//	ListView listView;
//	EditText editText;
//	ArrayAdapter adapter;
//	ProgressBar searchProgress;
//	ArrayList<String> searchResult;
//
//	public CustomDialogSearchAll(final CHMActivity activity) {
//		super(activity);
//		this.chmActivity = activity;
//	}
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setTitle("Search all");
//		setContentView(R.layout.dialog_search_all);
//		setCanceledOnTouchOutside(true);
//		setCancelable(true);
//		search = (Button) findViewById(R.id.btn_search);
//		search.setOnClickListener(this);
//		listView = (ListView) findViewById(R.id.list_result);
//		editText = (EditText) findViewById(R.id.edit_search);
//		searchProgress = (ProgressBar) findViewById(R.id.progressBar);
//		searchProgress.setMax(chmActivity.listSite.size() - 1);
//		searchResult = new ArrayList<>();
//		adapter = new ArrayAdapter<String>(chmActivity, android.R.layout.simple_list_item_1, searchResult);
//		listView.setAdapter(adapter);
//		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//				@Override
//				public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//					chmActivity.webview.loadUrl("file://" + chmActivity.extractPath + "/" + searchResult.get(i));
//					dismiss();
//				}
//			});
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//			case R.id.btn_search:
//				if (editText.getText().toString().length() > 0)
//					new AsyncTask<Void, Integer, Void>() {
//						String textSearch;
//
//						@Override
//						protected void onPreExecute() {
//							super.onPreExecute();
//							searchProgress.setProgress(0);
//							textSearch = editText.getText().toString();
//						}
//
//						@Override
//						protected Void doInBackground(Void... voids) {
//							for (int i = 1; i < chmActivity.listSite.size(); i++) {
//								if (searchDoc(chmActivity.listSite.get(i), textSearch)) {
//									publishProgress(i, 1);
//								} else {
//									publishProgress(i, 0);
//								}
//							}
//							return null;
//						}
//
//						@Override
//						protected void onProgressUpdate(Integer... values) {
//							super.onProgressUpdate(values);
//							searchProgress.setProgress(values[0].intValue());
//							if (values[1] == 1) {
//								searchResult.add(chmActivity.listSite.get(values[0]));
//								adapter.notifyDataSetChanged();
//								CustomDialogSearchAll.this.setTitle(searchResult.size() + " Results");
//							}
//						}
//
//						@Override
//						protected void onPostExecute(Void aVoid) {
//							super.onPostExecute(aVoid);
//							CustomDialogSearchAll.this.setTitle(searchResult.size() + " Results");
//						}
//					}.execute();
//		}
//	}
//
//	public boolean searchDoc(final String siteName, final String textSearch) {
//		final StringBuilder reval = new StringBuilder();
//		InputStream in = null;
//		try {
//			final InputStream resourceAsStream = chmActivity.utils.chm.getResourceAsStream("/" + siteName);
//			if (resourceAsStream == null) {
//				return false;
//			}
//			in = new BufferedInputStream(resourceAsStream);
//			final byte[] buf = new byte[1024 * 1024];
//			int c;
//			while ((c = in.read(buf)) >= 0) {
//				reval.append(new String(buf, 0, c));
//			}
//		} catch (Throwable e) {
//			ExceptionLogger.e(TAG, e.getMessage(), e);
//		} finally {
//			FileUtil.close(in);
//		}
//
//		//Document doc = Jsoup.parse(reval.toString());
////            if (doc.text().indexOf(textSearch) > 0)
////				return true;
////            else
////				return false;
//		return Html.escapeHtml(reval.toString()).indexOf(textSearch) > 0;
//	}
//}
