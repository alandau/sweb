package net.gnu.common;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslCertificate;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.widget.Toast;
import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AndroidUtils {

	private static final String TAG = "AndroidUtils";

	private static final Pattern CHAR_CODE_PATTERN = Pattern.compile("&#[xX]?([0-9a-zA-F]{2,8});");
	private static final Pattern UCHAR_CODE_PATTERN = Pattern.compile("\\\\u([0-9a-zA-F]{4})");

	public static String fixCharCode(CharSequence wholeFile) {
		Matcher mat = UCHAR_CODE_PATTERN.matcher(wholeFile);
		StringBuffer sb = new StringBuffer();
		while (mat.find()) {
			mat.appendReplacement(sb,
								  ((char) Integer.parseInt(mat.group(1), 16) + ""));
		}
		mat.appendTail(sb);
		mat = CHAR_CODE_PATTERN.matcher(sb);
		sb = new StringBuffer();
		while (mat.find()) {
			mat.appendReplacement(sb,
								  ((char) Integer.parseInt(mat.group(1), 10) + ""));
		}
		mat.appendTail(sb);
		return sb.toString();
	}

	public static Bitmap resize(final Bitmap image, final int maxWidth, final int maxHeight) {
		final int width = image.getWidth();
		final int height = image.getHeight();
		if (width < maxWidth && height < maxHeight 
			|| maxWidth <= 0
			|| maxHeight <= 0) {
			return image;
		} else {
			final float ratioBitmap = (float) width / (float) height;
			final float ratioMax = (float) maxWidth / (float) maxHeight;

			int finalWidth = maxWidth;
			int finalHeight = maxHeight;
			if (ratioMax > ratioBitmap) {
				finalWidth = (int) ((float)maxHeight * ratioBitmap);
			} else {
				finalHeight = (int) ((float)maxWidth / ratioBitmap);
			}
			return Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
		}
	}

	public static void toast(final Context ctx, final String st) {
		Toast.makeText(ctx, st, Toast.LENGTH_LONG).show();
	}

	public static void copyClipboard(final Context ctx, final CharSequence label, final CharSequence text) {
		final ClipboardManager clipboard = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
		assert clipboard != null;
		final ClipData clipData = ClipData.newPlainText(label, text);
		clipboard.setPrimaryClip(clipData);
		AndroidUtils.toast(ctx, "Copied \"" + text + "\"");
	}

	public static CharSequence getClipboardText(final Context ctx) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) { //Android 2.3 and below
			final android.text.ClipboardManager clipboard = (android.text.ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
			return clipboard.getText();
		} else { //Android 3.0 and higher
			final android.content.ClipboardManager cm = (android.content.ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
			//ClipDescription description = cm.getPrimaryClipDescription();
            ClipData clipData = cm.getPrimaryClip();
            if (clipData != null)
                return clipData.getItemAt(0).getText();
            else
                return null;
        }
    }

    public static void shareUrl(final Context ctx, final String url) {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        intent.putExtra(Intent.EXTRA_TEXT, url);
        intent.setType("text/plain");
        ctx.startActivity(Intent.createChooser(intent, "Share URL"));
    }

    public static int dpToPx(final float dp) {
        final DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) (dp * metrics.density + 0.5f);
    }

	@SuppressLint("DefaultLocale")
    public static String certificateToStr(final SslCertificate certificate) {
        if (certificate == null) {
            return null;
        }
        String s = "";
        final SslCertificate.DName issuedTo = certificate.getIssuedTo();
        if (issuedTo != null) {
            s += "Issued to: " + issuedTo.getDName() + "\n";
        }
        final SslCertificate.DName issuedBy = certificate.getIssuedBy();
        if (issuedBy != null) {
            s += "Issued by: " + issuedBy.getDName() + "\n";
        }
        final Date issueDate = certificate.getValidNotBeforeDate();
        if (issueDate != null) {
            s += String.format("Issued on: %tF %tT %tz\n", issueDate, issueDate, issueDate);
        }
        final Date expiryDate = certificate.getValidNotAfterDate();
        if (expiryDate != null) {
            s += String.format("Expires on: %tF %tT %tz\n", expiryDate, expiryDate, expiryDate);
        }
        return s;
    }

	public static String uri2RawPath(ContentResolver cr, Uri uri) {
		String path = "";
		final String scheme = uri.getScheme();
		if (ContentResolver.SCHEME_FILE.equals(scheme)) {
			path = uri.getEncodedPath();
			//ExceptionLogger.log(TAG, "Uri.decode(uri.getEncodedPath()) " + path);
		} else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
			//ContentResolver cr = getContentResolver();
			Cursor cur = null;
			try {
				cur = cr.query(uri, null, null, null, null);
			} catch (Exception e) {
				ExceptionLogger.e(TAG, e);
			}
			if (cur != null) {
				cur.moveToFirst();
				try {
					path = cur.getString(cur.getColumnIndex(MediaStore.Files.FileColumns.DATA));
					ExceptionLogger.d(TAG, "cur.getColumnIndex " + path);
					if (path == null
						|| !path.startsWith(Environment.getExternalStorageDirectory()
											.getPath())) {
						// from content provider
						path = uri.toString();
					}
				} catch (Exception e) {
					path = uri.toString();
				}
			} else {
				path = uri.toString();
			}
		} else{
			path = uri.toString();
		}
		return path;
	}
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {
		if (uri == null) {
			return null;
		}
		ExceptionLogger.d(TAG, "uri.authority " + uri.getAuthority() + ", uri.path " + uri.getPath());
//		ExceptionLogger.d(TAG, "DocumentsContract.isDocumentUri " + DocumentsContract.isDocumentUri(context, uri));
//      ExceptionLogger.d(TAG, "DocumentsContract.isTreeUri " + DocumentsContract.isTreeUri(uri));
        
		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		
        if (isKitKat) {
			if (DocumentsContract.isTreeUri(uri)) {
				final String[] split = uri.getPath().split(":");
				final String type = split[0];
				//ExceptionLogger.d(TAG, "type " + type);
				if (type.endsWith("primary")) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				} else {
					return "/storage" + type.substring(type.lastIndexOf("/")) + "/" + split[1];
				}
			} else if (DocumentsContract.isDocumentUri(context, uri)) {
				// ExternalStorageProvider
				if (isExternalStorageDocument(uri)) {
					final String docId = DocumentsContract.getDocumentId(uri);
					final String[] split = docId.split(":");
					final String type = split[0];
//					ExceptionLogger.d(TAG, "docId " + docId);
//					ExceptionLogger.d(TAG, "type " + type);
					if ("primary".equalsIgnoreCase(type)) {
						return Environment.getExternalStorageDirectory() + "/" + split[1];
					} else {
						return "/storage/" + type + "/" + split[1];
					}
				}
				// DownloadsProvider
				else if (isDownloadsDocument(uri)) {
					final String id = DocumentsContract.getDocumentId(uri);
					final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id.split(":")[1]));

					return getDataColumn(context, contentUri, null, null);
				}
				// MediaProvider
				else if (isMediaDocument(uri)) {
					final String docId = DocumentsContract.getDocumentId(uri);
					final String[] split = docId.split(":");
					final String type = split[0];

					Uri contentUri = null;
					if ("image".equals(type)) {
						contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
					} else if ("video".equals(type)) {
						contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
					} else if ("audio".equals(type)) {
						contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
					}

					final String selection = "_id=?";
					final String[] selectionArgs = new String[] {
						split[1]
					};

					return getDataColumn(context, contentUri, selection, selectionArgs);
				}
			}
			// MediaStore (and general)
			else if ("content".equalsIgnoreCase(uri.getScheme())) {

				// Return the remote address
				if (isGooglePhotosUri(uri))
					return uri.getLastPathSegment();

				return getDataColumn(context, uri, null, null);
			}
			// File
			else if ("file".equalsIgnoreCase(uri.getScheme())) {
				return uri.getPath();
			}
		}
        return null;
    }

    public static String getDataColumn(final Context context, Uri uri, String selection,
                                       final String[] selectionArgs) {

        Cursor cursor = null;
        final String column = MediaStore.Files.FileColumns.DATA;//"_data";
        final String[] projection = {
			column
        };
        try {
            cursor = context.getContentResolver().query(uri,
														projection,// Which columns to return
														selection, // WHERE clause; which rows to return (all rows)
														selectionArgs,// WHERE clause selection arguments (none)
														null);// Order-by clause (ascending by name)
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
		} finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

	public static String getFile(final Context context, final Intent intent) {
		Uri uri = intent.getData();
		String type = intent.getType();
		ExceptionLogger.d(TAG, "Pick completed: " + uri + " " + type);
		String path = "";
		if (uri != null) {
			path = uri.toString();
			if (path.toLowerCase().startsWith("file://")) {
// Selected file/directory path is below
				path = (new File(URI.create(path))).getAbsolutePath();
			} else {
				path = AndroidUtils.getPath(context, uri);
			}
		} else {
		}
		return path;
	}

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(final Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(final Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(final Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(final Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
	
	public static boolean checkWifiOnAndConnected(final Context context) {
		final WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON
			final WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
			if (wifiInfo.getNetworkId() == -1) {
				return false; // Not connected to an access point
			}
			ExceptionLogger.d(TAG, "checkWifiOnAndConnected " + wifiInfo.toString());
			return true; // Connected to an access point
		} else {
			return false; // Wi-Fi adapter is OFF
		}
	}
}
