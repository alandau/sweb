package landau.sweb;


import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

public class AdBlocker {

    private Pattern pattern;

    public AdBlocker() {
        long start = System.currentTimeMillis();
        int count = 0;
        File f = Environment.getExternalStorageDirectory().getAbsoluteFile();
        f = new File(f, "easylist.txt");
        StringBuilder sb = new StringBuilder();
        int[] counters = new int[50];
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            final int OTHER = 0, COMMENT = 1, ELEM_HIDING = 2, ANCHORED = 3, EXCEPTION = 4, KNOWN_OPTS = 5, REGEX_NOOPT = 6;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    // nothing
                } else if (line.startsWith("!")) {
                    counters[COMMENT]++;
                } else if (line.contains("##") || line.contains("@#@")) {
                    counters[ELEM_HIDING]++;
                } else if (line.startsWith("@@")) {
                    counters[EXCEPTION]++;
                } else if (line.startsWith("a||")) {
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
                    if (line.indexOf('$') == -1) {
                        counters[REGEX_NOOPT]++;
                        line = line.replaceAll("([.$+?{}()\\[\\]\\\\])", "\\\\$1");
                        line = line.replace("^", "(?:[^\\w\\d_\\-.%]|$)");
                        line = line.replace("*", ".*?");
                        if (line.endsWith("|")) line = line.substring(0, line.length() - 1) + "$";
                        if (line.startsWith("||")) {
                            counters[ANCHORED]++;
                            if (line.length() > 2)
                                line = "^(?:[^:/?#]+:)??(?://(?:[^/?#]*\\.)??)??" + line.substring(2);
                        } else if (line.startsWith("|")) {
                            line = "^" + line.substring(1);
                        }
                        line = line.replaceAll("\\|([^$])", "\\\\|$1");
                        sb.append('|');
                        sb.append(line);
                    }
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
        Log.i("qwe", "counters= " + Arrays.toString(counters));
        Log.i("qwe", "count= " + count + ", time=" + diff + "ms");
        String regex = sb.substring(1);
        pattern = Pattern.compile(regex);
    }

    boolean shouldBlock(String url, String domain) {
        long start = System.currentTimeMillis();
        boolean result = pattern.matcher(url).find();
        long end = System.currentTimeMillis();
        Log.i("qwe", "RES: " + result + "    MATCH TIME: " + (end-start) + ", url: " + url);
        return result;
    }
}
