package landau.sweb;

import android.annotation.*;
import android.content.*;
import android.content.res.*;
import android.database.*;
import android.net.*;
import android.net.http.*;
import android.os.*;
import android.provider.*;
import android.util.*;
import java.util.*;
import landau.sweb.utils.*;
import java.util.regex.*;
import java.io.*;
import android.widget.*;

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

	public static void toast(final Context ctx, final String st) {
		Toast.makeText(ctx, st, Toast.LENGTH_LONG).show();
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
//		ExceptionLogger.d(TAG, "uri.getAuthority() " + uri.getAuthority());
//      ExceptionLogger.d(TAG, "uri.getPath() " + uri.getPath());
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
        final String column = "_data";
        final String[] projection = {
			column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
														null);
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

	public static List<File> getFiles(final File f, final Pattern includePat, final Pattern excludePat) {
		ExceptionLogger.d(TAG, "getFiles " + f.getAbsolutePath());
		final List<File> fList = new LinkedList<File>();
		String name;
		if (f != null) {
			final LinkedList<File> folderQueue = new LinkedList<File>();
			name = f.getName();
			if (f.isDirectory()) {
				folderQueue.push(f);
			} else if ((includePat == null && excludePat == null)
					   || (includePat != null && includePat.matcher(name).matches()
					   && excludePat != null && !excludePat.matcher(name).matches())
					   || (includePat != null && includePat.matcher(name).matches())
					   || (excludePat != null && !excludePat.matcher(name).matches())) {
				fList.add(f);
			}
			File fi = null;
			File[] fs;
			while (folderQueue.size() > 0) {
				fi = folderQueue.removeFirst();
				fs = fi.listFiles();
				if (fs != null) {
					for (File f2 : fs) {
						if (f2.isDirectory()) {
							folderQueue.push(f2);
						} else {
							name = f2.getName();
							if ((includePat == null && excludePat == null)
								|| (includePat != null && includePat.matcher(name).matches()
								&& excludePat != null && !excludePat.matcher(name).matches())
								|| (includePat != null && includePat.matcher(name).matches())
								|| (excludePat != null && !excludePat.matcher(name).matches())) {
								fList.add(f2);
							}
						}
					}
				}
			}
		}
		return fList;
	}
}
