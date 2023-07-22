package net.gnu.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import android.util.Log;

public class EntityDownload {

	private static final String TAG = "EntityDownload";

    public static interface IDownloadCallback {
		void onPreExecute(); // Callback to tell that the downloading is going to start
		void onFailure(Object o); // Failed to download file
		void onSuccess(String path, Object o); // Downloaded file successfully with downloaded path
		void showProgress(EntityDownload EntityDownload); // Show progress
	}

	int progress; // range from 1-100
    long fileSize;// Total size of file to be downlaoded
    long downloadedSize; // Size of the downlaoded file

    public void setProgress(int progress) {
		this.progress = progress;
	}

    public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

    public void setDownloadedSize(long downloadedSize) {
		this.downloadedSize = downloadedSize;
	}

	/*
	 * @param callback = To update the UI with appropriate action
	 * @param fileName = Name of the file by which downloaded file will be saved.
	 * @param downloadURL = File downloading URL
	 * @param filePath = Path where file will be saved
	 * @param object = Any object you want in return after download 
	 *                 is completed to do certain operations like 
	 *                 insert in DB or show toast
	 */
	public static void download(final IDownloadCallback callback, 
								String fileName, 
								String downloadURL, 
								String filePath, 
								Object object) {
		callback.onPreExecute(); // Callback to tell that the downloading is going to start
		int count = 0;
		File outputFile = null; // Path where file will be downloaded
		try {
			File file = new File(filePath);
			file.mkdirs();
			long range = 0;
			outputFile = new File(file, fileName);
			/**
			 * Check whether the file exists or not
			 * If file doesn't exists then create the new file and range will be zero.
			 * But if file exists then get the length of file which will be the starting range,
			 * from where the file will be downloaded
			 */
			if (!outputFile.exists()) {
				outputFile.createNewFile();
				range = 0;
			} else {
				range = outputFile.length();
			}
			//Open the Connection
			URL url = new URL(downloadURL);
			URLConnection con = url.openConnection();
			// Set the range parameter in header and give the range from where you want to start the downloading
			con.setRequestProperty("Range", "bytes=" + range + "-");
			/**
			 * The total length of file will be the total content length given by the server + range.
			 * Example: Suppose you have a file whose size is 1MB and you had already downloaded 500KB of it.
			 * Then you will pass in Header as "Range":"bytes=500000".
			 * Now the con.getContentLength() will be 500KB and range will be 500KB.
			 * So by adding the two you will get the total length of file which will be 1 MB
			 */
			final long lengthOfFile = (int) con.getContentLength() + range;
			Log.d(TAG, "lengthOfFile " + lengthOfFile);
			FileOutputStream fileOutputStream = new FileOutputStream(outputFile, true);
			InputStream inputStream = con.getInputStream();

			byte[] buffer = new byte[4096];

			long total = range;
			/**
			 * Download the save the content into file
			 */
			while ((count = inputStream.read(buffer)) != -1) {
				total += count;
				int progress = (int) (total * 100 / lengthOfFile);
				EntityDownload entityDownload = new EntityDownload();
				entityDownload.setProgress(progress);
				entityDownload.setDownloadedSize(total);
				entityDownload.setFileSize(lengthOfFile);
				callback.showProgress(entityDownload);
				fileOutputStream.write(buffer, 0, count);
			}
			//Close the outputstream
			fileOutputStream.close();
			// Disconnect the Connection
			if (con instanceof HttpsURLConnection) {
				((HttpsURLConnection) con).disconnect();
			} else if (con instanceof HttpURLConnection) {
				((HttpURLConnection) con).disconnect();
			}
			inputStream.close();
			/**
			 * If file size is equal then return callback as success with downlaoded filepath and the object
			 * else return failure
			 */
			if (lengthOfFile == -1 || lengthOfFile == outputFile.length()) {
				callback.onSuccess(outputFile.getAbsolutePath(), object);
			} else {
				callback.onFailure(object);
			}
		} catch (Exception e) {
			e.printStackTrace();
			callback.onFailure(object);
		}
	}

	public static void main(final String[] args) {
		try {
			IDownloadCallback cb = new IDownloadCallback() {
				long start = 0;
				@Override
				public void onPreExecute() {
					start = System.currentTimeMillis();
					Log.d(TAG, "Start " + Util.DATETIME_FORMAT.format(start));
				}

				@Override
				public void onFailure(Object o) {
					Log.d(TAG, "Failure " + Util.DATETIME_FORMAT.format(System.currentTimeMillis()) + ", took " + Util.nf.format(System.currentTimeMillis()-start) + " ns");
				}

				@Override
				public void onSuccess(String path, Object o) {
					Log.d(TAG, "Success " + Util.DATETIME_FORMAT.format(System.currentTimeMillis()) + ", took " + Util.nf.format(System.currentTimeMillis()-start) + " ns");
				}

				@Override
				public void showProgress(EntityDownload entityDownload) {
					Log.d(TAG, entityDownload.progress + " bytes, took " + Util.nf.format(System.currentTimeMillis()-start) + " ns");
				}
			};
			download(cb, 
					 "a.txt", 
					 "https://m.ntdvn.net/van-hoa/nhung-bi-mat-tu-vet-bot-431966.html",
					 "/storage/emulated/0/Download/Bb", 
					 null);
		} catch (Throwable e) {
			Log.d(TAG, e.getMessage(), e);
		}
	}
}

