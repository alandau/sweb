package landau.sweb;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AdblockRulesLoader extends AsyncTaskLoader<Integer> {
    private static final String TAG = AdblockRulesLoader.class.getSimpleName();
    private static final int MAX_FILE_SIZE = 10*1024*1024;

    private String[] urlList;
    private File outputDir;

    public AdblockRulesLoader(Context context, String[] urlList, File outputDir) {
        super(context);
        this.urlList = urlList;
        this.outputDir = outputDir;
        Log.i(TAG, "ctor");
    }

    private boolean downloadUrl(String url, File outputFile) {
        int total = 0;
        byte[] data = new byte[16384];
        try {
            URL urlObj = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) urlObj.openConnection();
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                try (OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile))) {
                    int count;
                    while (total < MAX_FILE_SIZE && (count = in.read(data)) != -1) {
                        out.write(data, 0, count);
                        total += count;
                    }
                }
            } finally {
                urlConnection.disconnect();
            }
            return true;
        } catch (IOException e) {
            Log.i(TAG, "Failed downloading " + url, e);
            return false;
        }
    }

    @Override
    public Integer loadInBackground() {
        Log.i(TAG, "loadInBackground");
        int count = 0;
        for (int i = 0; i < urlList.length; i++) {
            if (downloadUrl(urlList[i], new File(outputDir, "internal" + i + ".txt"))) {
                count++;
            }
        }
        return count;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
