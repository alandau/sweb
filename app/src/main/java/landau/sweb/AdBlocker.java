package landau.sweb;


import android.net.Uri;
import android.os.Debug;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

@SuppressWarnings({"StatementWithEmptyBody", "WeakerAccess"})
public class AdBlocker {

    static final String TAG = AdBlocker.class.getSimpleName();

    static class Rule {
        Pattern regex;
        ArrayList<String> paths;
        HashSet<String> domains;
        boolean inverseDomains;
    }

    private Pattern pattern;
    private HashMap<String, Rule> rulesByDomain = new HashMap<>();

    int count = 0;
    StringBuilder sb = new StringBuilder();
    static final int OTHER = 0, COMMENT = 1, ELEM_HIDING = 2, ANCHORED = 3, EXCEPTION = 4, SIMPLE = 5, REGEX_NOOPT = 6;
    int[] counters = new int[50];

    public AdBlocker() {
        long start = System.currentTimeMillis();
        File f = new File(Environment.getExternalStorageDirectory(), "sweb");
        File[] files = f.listFiles();
        if (files != null) {
            for (File file : files) {
                loadFromFile(file);
            }
        }
        if (sb.length() > 0) {
            String regex = sb.substring(1);
            pattern = Pattern.compile(regex);
        }

        Debug.MemoryInfo info = new Debug.MemoryInfo();
        Debug.getMemoryInfo(info);

        long end = System.currentTimeMillis();
        long diff = end - start;
        Log.i(TAG, "counters= " + Arrays.toString(counters));
        Log.i(TAG, "count= " + count + ", time=" + diff + "ms" + " mem=" + info.getTotalPss() + "kb");
    }

    private void loadFromAdblockFile(String firstline, BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            if (line.isEmpty()) {
                // nothing
            } else if (line.startsWith("!")) {
                counters[COMMENT]++;
            } else if (line.contains("##") || line.contains("#@#") || line.contains("#?#")) {
                counters[ELEM_HIDING]++;
            } else if (line.startsWith("@@")) {
                counters[EXCEPTION]++;
            } else if (line.startsWith("||")) {
                counters[ANCHORED]++;

                int dollar = line.indexOf('$');
                HashSet<String> domains = null;
                boolean inverseDomains = false;
                if (dollar != -1) {
                    int domainsIndex = line.indexOf("domain=", dollar+1);
                    if (domainsIndex != -1) {
                        String list = line.substring(domainsIndex + 7);
                        String[] res = list.split("\\|");
                        if (res.length > 0 && res[0].startsWith("~")) {
                            inverseDomains = true;
                        }
                        domains = new HashSet<>(res.length);
                        for (String s : res) {
                            domains.add(s.startsWith("~") ? s.substring(1) : s);
                        }
                    }
                }
                String rule = dollar == -1 ? line.substring(2) : line.substring(2, dollar);
                int caret = rule.indexOf('^');
                if (caret == -1) caret = rule.length();
                int slash = rule.indexOf('/');
                if (slash == -1) slash = rule.length();
                String domain = rule.substring(0, Math.min(caret, slash));
                if (domain.indexOf('*') == -1) {
                    Rule r = rulesByDomain.get(domain);
                    boolean created = false;
                    if (r == null) {
                        r = new Rule();
                        rulesByDomain.put(domain, r);
                        created = true;
                    }
                    String path = rule.substring(Math.min(caret, slash));
                    //if (!created || path.length() > 1 || (path.length() == 1 && path.charAt(0) != '^' && path.charAt(0) != '/')) {
                        if (r.paths == null) r.paths = new ArrayList<>();
                        //if (!created) r.paths.add("/");
                        r.paths.add(path);
                    //}
                    if (domains != null) {
                        if (created /* implies r.domains == null */) {
                            r.domains = domains;
                        } else if (r.domains != null){
                            r.domains.addAll(domains);
                        }
                        r.inverseDomains = inverseDomains;
                    }
                }
            } else {
                counters[OTHER]++;
                if (line.indexOf('$') == -1) {
                    counters[REGEX_NOOPT]++;
                    boolean anchorStart = false;
                    if (line.startsWith("|")) {
                        anchorStart = true;
                        line = line.substring(1);
                    }
                    line = ruleToRegex(line);
                    if (anchorStart) {
                        line = "^" + line;
                    }
                    sb.append('|');
                    sb.append(line);
                }
            }
            count++;
        }
        br.close();
    }

    private void addSimpleDomain(String domain) {
        Rule r = rulesByDomain.get(domain);
        if (r == null) {
            r = new Rule();
            rulesByDomain.put(domain, r);
        }
        if (r.paths != null) {
            r.paths.add("/");
        }
        if (r.domains != null) {
            r.domains = null;
        }
    }
    private void loadFromHostsFile(String firstline, BufferedReader br) throws IOException {
        String line = firstline;
        while (line != null) {
            if (line.isEmpty() || line.startsWith("#")) {
                // comment
            } else {
                String[] components = line.split("\\s+");
                if (components.length == 1) {
                    // Host only
                    addSimpleDomain(components[0]);
                } else {
                    // First component is IP (127.0.0.1)
                    for (int i = 1; i < components.length; i++) {
                        addSimpleDomain(components[i]);
                    }
                }
                count++;
                counters[SIMPLE]++;
            }
            line = br.readLine();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private void loadFromFile(File f) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null && line.isEmpty()) {}
            if (line == null) return;
            if (line.startsWith("[")) {
                loadFromAdblockFile(line, br);
            } else {
                loadFromHostsFile(line, br);
            }
        } catch (IOException e) {
            //You'll need to add proper error handling here
            Log.e("AdBlocker", "exception on file " + f.getName(), e);
        }
    }

    boolean shouldBlockHashRule(Rule r, Uri url, String mainPage) {
        if (r.domains != null) {
            boolean domainMatched = false;
            String mainDomain = Uri.parse(mainPage).getHost();
            if (mainDomain == null || mainPage.isEmpty()) return false;
            int period;
            while ((period = mainDomain.indexOf('.')) != -1) {
                if (r.domains.contains(mainDomain)) {
                    domainMatched = true;
                    break;
                }
                mainDomain = mainDomain.substring(period + 1);
            }
            if (r.inverseDomains) domainMatched = !domainMatched;
            if (!domainMatched) {
                return false;
            }
        }
        if (r.regex == null && r.paths == null) {
            // Only domain, block all paths
            return true;
        }
        if (r.regex == null) {
            // r.paths is not null, so compile a regex
            // Have at least 1 path, compile a regex
            if (r.paths.size() == 1) {
                String path = r.paths.get(0);
                if (path.equals("/") || path.equals("^")) {
                    // Shortcut if path is / or ^
                    r.paths = null;
                    return true;
                }
                r.regex = Pattern.compile("^" + path);
            } else {
                StringBuilder sb = new StringBuilder("^(?:");
                sb.append(r.paths.get(0));
                for (int i = 1; i < r.paths.size(); i++) {
                    sb.append('|');
                    sb.append(ruleToRegex(r.paths.get(i)));
                }
                sb.append(')');
                r.regex = Pattern.compile(sb.toString());
            }
            r.paths = null; // free memory
        }
        // r.regex is not null, use it
        return r.regex.matcher(url.getEncodedPath()).find();
    }

    boolean shouldBlockHash(Uri url, String mainPage) {
        String host = url.getHost();
        int period;
        while ((period = host.indexOf('.')) != -1) {
            Rule r = rulesByDomain.get(host);
            if (r != null && shouldBlockHashRule(r, url, mainPage)) {
                return true;
            }
            host = host.substring(period + 1);
        }
        return false;
    }

    boolean shouldBlock(Uri url, String mainPage) {
        long start2 = System.currentTimeMillis();
        boolean result2 = shouldBlockHash(url, mainPage);
        long end2 = System.currentTimeMillis();

        long start = System.currentTimeMillis();
        boolean result = result2 || (pattern != null && pattern.matcher(url.toString()).find());
        long end = System.currentTimeMillis();

        Log.i(TAG, "RES: " + result + "," + result2 + "    MATCH TIME: " + (end - start) + ", TIME2: " + (end2-start2) + ", url: " + url);
        result |= result2;
        return result;
    }

    static String ruleToRegex(String rule) {
        boolean anchorEnd = false;
        if (rule.endsWith("|")) {
            anchorEnd = true;
            rule = rule.substring(0, rule.length() - 1);
        }
        rule = rule.replaceAll("([|.$+?{}()\\[\\]\\\\])", "\\\\$1");
        rule = rule.replace("^", "(?:[^\\w\\d_\\-.%]|$)");
        rule = rule.replace("*", ".*?");
        if (anchorEnd)
            rule = rule + "$";
        return rule;
    }
}
