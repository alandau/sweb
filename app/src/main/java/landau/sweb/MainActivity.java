package landau.sweb;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends Activity {

    private static class Tab {
        Tab(WebView w) {
            this.webview = w;
        }

        WebView webview;
    }

    final String searchUrl = "https://www.google.com/search?q=%s";
    private ArrayList<Tab> tabs = new ArrayList<>();
    private int currentTabIndex;
    private FrameLayout webviews;
    private ImageButton newActivityBtn;
    private EditText et;
    private boolean isNightMode;
    private SharedPreferences prefs;

    static class TitleAndUrl {
        String title;
        String url;
    }

    private ArrayList<TitleAndUrl> closedTabs = new ArrayList<>();

    private Tab getCurrentTab() {
        return tabs.get(currentTabIndex);
    }

    private WebView getCurrentWebView() {
        return getCurrentTab().webview;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private WebView createWebView() {
        final ProgressBar progressBar = findViewById(R.id.progress1);

        WebView webview = new WebView(this);
        webview.setBackgroundColor(isNightMode ? Color.BLACK : Color.WHITE);
        webview.setWebChromeClient(new WebChromeClient());
        WebSettings settings = webview.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                injectCSS(view);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setProgress(newProgress);
                }
            }
        });
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setProgress(0);
                progressBar.setVisibility(View.VISIBLE);
                if (view == getCurrentWebView()) {
                    et.setText(url);
                }
                injectCSS(view);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (view == getCurrentWebView()) {
                    et.setText(url);
                }
                injectCSS(view);
            }

            final Set<String> adHosts = new HashSet<>(Arrays.asList(
                    "mc.yandex.ru",
                    "mobtop.ru",
                    "counter.yadro.ru",
                    "top.list.ru",
                    "an.yandex.ru",
                    "pagead2.googlesyndication.com",
                    "jsc.marketgid.com"
            ));
            final InputStream emptyInputStream = new ByteArrayInputStream(new byte[0]);

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String host = request.getUrl().getHost();
                if (adHosts.contains(host)) {
                    return new WebResourceResponse("text/plain", "UTF-8", emptyInputStream);
                }
                return super.shouldInterceptRequest(view, request);
            }
        });
        webview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WebView.HitTestResult r = ((WebView) v).getHitTestResult();
                if (r.getType() != WebView.HitTestResult.SRC_ANCHOR_TYPE && r.getType() != WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                    return false;
                }
                final String url = r.getExtra();
                new AlertDialog.Builder(MainActivity.this).setTitle(url).setItems(new String[]{"Open", "Open in new tab"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                et.setText(url);
                                loadUrl(url, getCurrentWebView());
                                break;
                            case 1:
                                newTab(url);
                                break;
                        }
                    }
                }).show();
                return true;
            }
        });
        return webview;
    }

    private void newTab(String url) {
        WebView webview = createWebView();
        webview.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        webview.setVisibility(View.GONE);
        tabs.add(new Tab(webview));
        webviews.addView(webview);
        ((TextView) findViewById(R.id.btnTabsCount)).setText(String.valueOf(tabs.size()));
        loadUrl(url, webview);
    }

    private void switchToTab(int tab) {
        getCurrentWebView().setVisibility(View.GONE);
        currentTabIndex = tab;
        getCurrentWebView().setVisibility(View.VISIBLE);
        et.setText(getCurrentWebView().getUrl());
        getCurrentWebView().requestFocus();
    }

    @Override
    @SuppressLint("SetTextI18n")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_main);

        isNightMode = prefs.getBoolean("night_mode", false);

        webviews = findViewById(R.id.webviews);
        currentTabIndex = 0;

        newActivityBtn = findViewById(R.id.new_activity);
        et = findViewById(R.id.et);

        // setup edit text
        et.setSelected(false);
        if (Intent.ACTION_VIEW.equals(getIntent().getAction()) && getIntent().getData() != null) {
            et.setText(getIntent().getDataString());
        } else if (Intent.ACTION_WEB_SEARCH.equals(getIntent().getAction()) && getIntent().getStringExtra("query") != null) {
            et.setText(getIntent().getStringExtra("query"));
        } else {
            et.setText("");
        }

        final GestureDetector backGestureDetector = new GestureDetector(this, new MyGestureDetector(this) {
            @Override
            boolean onFlingUp() {
                WebBackForwardList list = getCurrentWebView().copyBackForwardList();
                final int idx = list.getCurrentIndex();
                String[] items = new String[list.getSize()];
                for (int i = 0; i < list.getSize(); i++) {
                    items[i] = list.getItemAtIndex(i).getTitle();
                }
                ArrayAdapter<String> adapter = new ArrayAdapterWithCurrentItem<>(
                        MainActivity.this,
                        android.R.layout.select_dialog_item,
                        items,
                        idx);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Navigation History")
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getCurrentWebView().goBackOrForward(which - idx);
                            }
                        })
                        .show();
                return true;
            }
        });

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentWebView().canGoBack()) {
                    getCurrentWebView().goBack();
                }
            }
        });
        btnBack.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                getCurrentWebView().pageUp(true);
                return true;
            }
        });
        //noinspection AndroidLintClickableViewAccessibility
        btnBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return backGestureDetector.onTouchEvent(event);
            }
        });

        ImageView btnForward = findViewById(R.id.btnForward);
        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentWebView().canGoForward()) {
                    getCurrentWebView().goForward();
                }
            }
        });
        btnForward.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                getCurrentWebView().pageDown(true);
                return true;
            }
        });

        ImageView btnReload = findViewById(R.id.btnReload);
        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentWebView().reload();
            }
        });
        btnReload.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                getCurrentWebView().stopLoading();
                return true;
            }
        });

        final GestureDetector nightModeGestureDetector = new GestureDetector(this, new MyGestureDetector(this) {
            @Override
            boolean onFlingUp() {
                boolean fullscreen = (getWindow().getDecorView().getSystemUiVisibility() & View.SYSTEM_UI_FLAG_FULLSCREEN) != 0;
                int flags = !fullscreen ?
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        : 0;
                getWindow().getDecorView().setSystemUiVisibility(flags);
                return true;
            }
        });

        ImageView btnNightMode = findViewById(R.id.btnNightMode);
        btnNightMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNightMode = !isNightMode;
                prefs.edit().putBoolean("night_mode", isNightMode).apply();
                onNightModeChange();
            }
        });
        //noinspection AndroidLintClickableViewAccessibility
        btnNightMode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return nightModeGestureDetector.onTouchEvent(event);
            }
        });


        final GestureDetector gestureDetector = new GestureDetector(this, new MyGestureDetector(this) {
            @Override
            boolean onFlingUp() {
                if (!getCurrentWebView().getUrl().equals("about:blank")) {
                    TitleAndUrl titleAndUrl = new TitleAndUrl();
                    titleAndUrl.title = getCurrentWebView().getTitle();
                    titleAndUrl.url = getCurrentWebView().getUrl();
                    closedTabs.add(0, titleAndUrl);
                }
                ((FrameLayout) findViewById(R.id.webviews)).removeView(getCurrentWebView());
                getCurrentWebView().destroy();
                tabs.remove(currentTabIndex);
                if (currentTabIndex >= tabs.size()) {
                    currentTabIndex = tabs.size() - 1;
                }
                if (currentTabIndex == -1) {
                    // We just closed the last tab
                    newTab("");
                    currentTabIndex = 0;
                }
                getCurrentWebView().setVisibility(View.VISIBLE);
                et.setText(getCurrentWebView().getUrl());
                ((TextView) findViewById(R.id.btnTabsCount)).setText(String.valueOf(tabs.size()));
                getCurrentWebView().requestFocus();
                return true;
            }
        });

        ImageView btnTabs = findViewById(R.id.btnTabs);
        btnTabs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] items = new String[tabs.size()];
                for (int i = 0; i < tabs.size(); i++) {
                    items[i] = tabs.get(i).webview.getTitle();
                }
                ArrayAdapter<String> adapter = new ArrayAdapterWithCurrentItem<>(
                        MainActivity.this,
                        android.R.layout.select_dialog_item,
                        items,
                        currentTabIndex);
                AlertDialog.Builder tabsDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Tabs")
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switchToTab(which);
                            }
                        });
                if (!closedTabs.isEmpty()) {
                    tabsDialog.setNeutralButton("Undo closed tabs", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String[] items = new String[closedTabs.size()];
                            for (int i = 0; i < closedTabs.size(); i++) {
                                items[i] = closedTabs.get(i).title;
                            }
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Undo closed tabs")
                                    .setItems(items, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String url = closedTabs.get(which).url;
                                            closedTabs.remove(which);
                                            newTab(url);
                                            switchToTab(tabs.size() - 1);
                                        }
                                    })
                                    .show();
                        }
                    });
                }
                tabsDialog.show();
            }
        });
        btnTabs.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                newTab("");
                switchToTab(tabs.size() - 1);
                return true;
            }
        });
        //noinspection AndroidLintClickableViewAccessibility
        btnTabs.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
        ((TextView) findViewById(R.id.btnTabsCount)).setText("1");

        final String[] menuItems = {
                // Title, then url
                "Landley", "http://landley.net/notes.html",
                "LWN.net", "https://lwn.net/Articles/?offset=0",
                "OSNews", "http://mobile.osnews.com",
                "Eli Bendersky", "https://eli.thegreenplace.net",
                "OldNewThing", "https://blogs.msdn.microsoft.com/oldnewthing/",
                "Anekdotov.net", "http://pda.anekdotov.net/",
                "anekdot.ru", "https://www.anekdot.ru/last/anekdot/",
                "Grooming", "http://www.kongsbergers.org/GroomingReport.html",
        };
        ImageView btnBookmakrs = findViewById(R.id.btnBookmarks);
        btnBookmakrs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, v);
                Menu menu = popup.getMenu();
                for (int i = 0; i < menuItems.length; i += 2) {
                    menu.add(0, i / 2, 0, menuItems[i]);
                }
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int index = item.getItemId();
                        if (index >= 0 && index < menuItems.length / 2) {
                            String url = menuItems[index * 2 + 1];
                            et.setText(url);
                            loadUrl(url, getCurrentWebView());
                            return true;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

        //noinspection AndroidLintClickableViewAccessibility
        newActivityBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    newActivityBtn.setColorFilter(getResources().getColor(android.R.color.holo_blue_dark));
                    return false;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    newActivityBtn.setColorFilter(null);
                    return false;
                }
                return false;
            }
        });
        newActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.putExtra("url", et.getText().toString());
                startActivity(intent);
            }
        });
        et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    loadUrl(et.getText().toString(), getCurrentWebView());
                    return true;
                } else {
                    return false;
                }
            }
        });

        newTab(et.getText().toString());
        getCurrentWebView().setVisibility(View.VISIBLE);
        getCurrentWebView().requestFocus();
        onNightModeChange();
    }

    private void onNightModeChange() {
        if (isNightMode) {
            getCurrentWebView().setBackgroundColor(Color.BLACK);
        } else {
            getCurrentWebView().setBackgroundColor(Color.WHITE);
        }
        injectCSS(getCurrentWebView());
    }

    private void loadUrl(String url, WebView webview) {
        url = url.trim();
        if (url.isEmpty()) {
            url = "about:blank";
        }
        if (url.indexOf(' ') == -1 && (url.startsWith("about:") || url.startsWith("javascript:") || Patterns.WEB_URL.matcher(url).matches())) {
            url = URLUtil.guessUrl(url);
        } else {
            url = URLUtil.composeSearchUrl(url, searchUrl, "%s");
        }
        webview.loadUrl(url);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        if (getCurrentWebView().canGoBack()) {
            getCurrentWebView().goBack();
        } else {
            super.onBackPressed();
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
                js = "if (document.head && !document.getElementById('" + styleElementId + "')) {" +
                        "   var style = document.createElement('style');" +
                        "   style.id = '" + styleElementId + "';" +
                        "   style.type = 'text/css';" +
                        "   style.innerHTML = '" + css + "';" +
                        "   document.head.appendChild(style);" +
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
                        "}" +
                        "var iframes = document.getElementsByTagName('iframe');" +
                        "for (var i = 0; i < iframes.length; i++) {" +
                        "   var fr = iframes[i];" +
                        "   var style = fr.contentWindow.document.getElementById('" + styleElementId + "');" +
                        "   fr.contentDocument.head.removeChild(style);" +
                        "}";
            }
            webview.evaluateJavascript("javascript:(function() {" + js + "})()", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private static final double FORBIDDEN_ZONE_MIN = Math.PI / 4 - Math.PI / 12;
        private static final double FORBIDDEN_ZONE_MAX = Math.PI / 4 + Math.PI / 12;
        private static final int MIN_VELOCITY_DP = 80;  // 0.5 inch/sec
        private static final int MIN_DISTANCE_DP = 80;  // 0.5 inch
        private final float MIN_VELOCITY_PX;
        private final float MIN_DISTANCE_PX;

        MyGestureDetector(Context context) {
            float density = context.getResources().getDisplayMetrics().density;
            MIN_VELOCITY_PX = MIN_VELOCITY_DP * density;
            MIN_DISTANCE_PX = MIN_DISTANCE_DP * density;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float velocitySquared = velocityX * velocityX + velocityY * velocityY;
            if (velocitySquared < MIN_VELOCITY_PX * MIN_VELOCITY_PX) {
                // too slow
                return false;
            }

            float deltaX = e2.getX() - e1.getX();
            float deltaY = e2.getY() - e1.getY();

            if (Math.abs(deltaX) < MIN_DISTANCE_PX && Math.abs(deltaY) < MIN_DISTANCE_PX) {
                // small movement
                return false;
            }

            double angle = Math.atan2(Math.abs(deltaY), Math.abs(deltaX));
            if (angle > FORBIDDEN_ZONE_MIN && angle < FORBIDDEN_ZONE_MAX) {
                return false;
            }

            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                if (deltaX > 0) {
                    return onFlingRight();
                } else {
                    return onFlingLeft();
                }
            } else {
                if (deltaY > 0) {
                    return onFlingDown();
                } else {
                    return onFlingUp();
                }
            }
        }

        boolean onFlingRight() {
            return true;
        }

        boolean onFlingLeft() {
            return true;
        }

        boolean onFlingUp() {
            return true;
        }

        boolean onFlingDown() {
            return false;
        }
    }

    static class ArrayAdapterWithCurrentItem<T> extends ArrayAdapter<T> {
        int currentIndex;

        ArrayAdapterWithCurrentItem(@NonNull Context context, int resource, @NonNull T[] objects, int currentIndex) {
            super(context, resource, objects);
            this.currentIndex = currentIndex;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView textView = view.findViewById(android.R.id.text1);
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    position == currentIndex ? android.R.drawable.ic_menu_mylocation : 0, 0, 0, 0);
            textView.setCompoundDrawablePadding(
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getContext().getResources().getDisplayMetrics()));
            return view;
        }
    }

}
