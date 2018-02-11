package landau.sweb;


import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class AdBlocker {
    public AdBlocker() {
        long start = System.currentTimeMillis();
        int count = 0;
        File f = Environment.getExternalStorageDirectory().getAbsoluteFile();
        f = new File(f, "easylist.txt");
        int[] counters = new int[50];
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            final int OTHER = 0, COMMENT = 1, ELEM_HIDING = 2, ANCHORED = 3, EXCEPTION = 4, KNOWN_OPTS = 5;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    // nothing
                } else if (line.startsWith("!")) {
                    counters[COMMENT]++;
                } else if (line.contains("##") || line.contains("@#@")) {
                    counters[ELEM_HIDING]++;
                } else if (line.startsWith("@@")) {
                    counters[EXCEPTION]++;
                } else if (line.startsWith("||")) {
                    counters[ANCHORED]++;
                    int dollar = line.indexOf('$');
                    if (dollar != -1) {
                        dollar++;
                        boolean allKnown = true;
                        while (true) {
                            if (line.startsWith("third-party", dollar) || line.startsWith("domain=")) {
                                // known option
                            } else {
                                allKnown = false;
                                break;
                            }
                            dollar = line.indexOf(',', dollar);
                            if (dollar == -1) {
                                break;
                            } else {
                                dollar++;
                            }
                        }
                        if (allKnown) {
                            counters[KNOWN_OPTS]++;
                            
                        }
                    }
                } else {
                    counters[OTHER]++;
                }
                count++;
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
        long end = System.currentTimeMillis();
        long diff = end - start;
        Log.i("qwe", "count= " + count + ", time=" + diff + "ms");
    }
}
