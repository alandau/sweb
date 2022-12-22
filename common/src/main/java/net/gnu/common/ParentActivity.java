package net.gnu.common;

import java.util.regex.Pattern;
import android.app.Activity;
import android.app.AlertDialog;
import java.io.File;
import android.os.Bundle;
import android.os.Build;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.ClipData;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.KeyEvent;
import java.net.URLDecoder;

public class ParentActivity extends Activity {
	
	private static final String TAG = "ParentActivity";
	public static File externalLogFilesDir = null;
	protected String urlIntent;
    protected static final long TIME_INTERVAL = 250000000L;
	protected long mBackPressed = System.nanoTime();
	
	public static final String IMAGE_PAT = "[^\"]*?\\.(gif|jpe?g|png|bmp|webp|tiff?|wmf|psd|pic|ico|svg)$";
	public static final String MEDIA_PAT = "[^\"]*?\\.(avi|mp4v?|mp5|mpv|vob|rmvb|m2ts?|mp2t|r?t[sp]|webm|wmv|asf|mkv|av1|mov|mp2v|mpg4?|qt|mpeg4?|flv|mp21|mp3|opus|aac|pcm|flac|wav|wma|amr|og[gv]|vp[967]|vc1|rm|ram|m4a|m3u8?|3[gp]2|3gpp?2?)[^\"]*?";
	public static final String FONT_PAT = "[^\"]*?\\.(otf|ttf|ttc|woff|woff2|eot)[^\"]*?";
	
	public static Pattern HTML_PATTERN = Pattern.compile("[^\"]*?\\.([xds]?html?|php|txt|java|cpp|hpp|c|h|log)", Pattern.CASE_INSENSITIVE);
	public static final Pattern IMAGES_PATTERN = Pattern.compile(IMAGE_PAT, Pattern.CASE_INSENSITIVE);
	public static final Pattern MEDIA_PATTERN = Pattern.compile(MEDIA_PAT, Pattern.CASE_INSENSITIVE);
	public static final Pattern FONT_PATTERN = Pattern.compile(FONT_PAT, Pattern.CASE_INSENSITIVE);

    public static class EmptyOnClickListener implements DialogInterface.OnClickListener {
		@Override
		public void onClick(final DialogInterface p1, final int p2) {
		}
	}
	
	@Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		externalLogFilesDir = getExternalFilesDir("logs");
		ExceptionLogger.init();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
				private Thread.UncaughtExceptionHandler defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
				@Override
				public void uncaughtException(final Thread t, final Throwable e) {
					ExceptionLogger.e(TAG, e);
					defaultUEH.uncaughtException(t, e);
				}
			});
	}

    protected boolean hasOrRequestPermission(final String permission, String explanation, final int requestCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted
            return true;
        }
        if (explanation != null && shouldShowRequestPermissionRationale(permission)) {
            new AlertDialog.Builder(this)
				.setTitle("Permission Required")
				.setMessage(explanation)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						requestPermissions(new String[] {permission}, requestCode);}})
				.show();
            return false;
        }
        requestPermissions(new String[] {permission}, requestCode);
        return false;
    }

    protected String getUrlFromIntent(final Intent intent) {
		ExceptionLogger.d(TAG, "getUrlFromIntent " + intent.getAction()
						  + ", getDataString " + intent.getDataString()
						  + ", Intent.EXTRA_TEXT " + intent.getStringExtra(Intent.EXTRA_TEXT)
						  + ", query " + intent.getStringExtra("query"));
		if (Intent.ACTION_SEND_MULTIPLE.equals(intent.getAction())
			|| Intent.ACTION_SEND.equals(intent.getAction())
			|| Intent.ACTION_VIEW.equals(intent.getAction())
			|| Intent.ACTION_SENDTO.equals(intent.getAction())) {
			Uri uri = intent.getData();
			ExceptionLogger.d(TAG, "URI to open is: " + uri + ", intent " + intent + ", " + intent.getClipData());
			if (uri != null) {
				return getPath(uri);
			} else {
				final ClipData clip = intent.getClipData();
				if (clip != null) {
					final int itemCount = clip.getItemCount();
					if (itemCount > 0) {
						uri = clip.getItemAt(0).getUri();
						if (uri != null) {
							return getPath(uri);
						}
					}
				}
				return intent.getStringExtra(Intent.EXTRA_TEXT);
			}
		}
        if (Intent.ACTION_VIEW.equals(intent.getAction()) && intent.getData() != null) {
            return intent.getDataString();
        } else if (Intent.ACTION_SEND.equals(intent.getAction()) && "text/plain".equals(intent.getType())) {
            return intent.getStringExtra(Intent.EXTRA_TEXT);
        } else if (Intent.ACTION_WEB_SEARCH.equals(intent.getAction()) && intent.getStringExtra("query") != null) {
            return intent.getStringExtra("query");
        } else {
            return "";
        }
    }

	// return real local path
	private String getPath(final Uri uri) {
		String ret;
		final String scheme = uri.getScheme();
		if (scheme.startsWith("content")) {
			ret = AndroidUtils.getPath(ParentActivity.this, uri);
		} else {
			ret = uri.toString();
			if (ret.startsWith("file://")) {
				ret = URLDecoder.decode(ret.substring("file://".length()));
			}
		}
		ExceptionLogger.d(TAG, "getPath: " + uri + ", ret " + ret);
		return ret;
	}

	@Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL >= System.nanoTime()) {
			ExceptionLogger.d(TAG, "super.onBackPressed()");
			super.onBackPressed();
			finish();
		} else {
			mBackPressed = System.nanoTime();
		}
    }

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		ExceptionLogger.d(TAG, "onKeyLongPress.keyCode=" + keyCode + ", event=" + event);
		if (keyCode == KeyEvent.KEYCODE_BACK
			&& event.getAction() == KeyEvent.ACTION_DOWN) {
			super.onBackPressed();
			finish();
			return true;
		}
		return onKeyLongPress(keyCode, event);
	}
}
