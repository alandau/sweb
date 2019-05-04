package landau.sweb;


import android.net.Uri;
import android.os.Debug;
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

import landau.sweb.utils.ExceptionLogger;

@SuppressWarnings({"StatementWithEmptyBody", "WeakerAccess"})
public class AdBlocker {

    static final String TAG = AdBlocker.class.getSimpleName();

    static class RuleWithDomain {
        boolean matchAllPaths;
        Pattern regex;
        String path;
        HashSet<String> domains;
        boolean inverseDomains;
    }
    static class Rule {
        boolean matchAllPaths;
        Pattern regex;
        ArrayList<String> paths;
        ArrayList<RuleWithDomain> rulesWithDomain;
    }

    private Pattern pattern;
    private HashMap<String, Rule> rulesByDomain = new HashMap<>();

    int count = 0;
    StringBuilder sb = new StringBuilder();
    static final int OTHER = 0, COMMENT = 1, ELEM_HIDING = 2, ANCHORED = 3, EXCEPTION = 4, SIMPLE = 5, REGEX_NOOPT = 6;
    int[] counters = new int[50];

    public AdBlocker(File rootDir) {
        long start = System.currentTimeMillis();
        File[] files = rootDir.listFiles();
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

    @SuppressWarnings("unused")
    private void loadFromAdblockFile(String firstline, BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            count++;
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
                    } else {
                        if ("object".equals(line.substring(dollar + 1))) {
                            continue;
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
                    if (r == null) {
                        r = new Rule();
                        rulesByDomain.put(domain, r);
                    }
                    String path = rule.substring(Math.min(caret, slash));
                    if (domains == null) {
                        if (r.paths == null) r.paths = new ArrayList<>();
                        r.paths.add(path);
                    } else {
                        RuleWithDomain ruleWithDomain = new RuleWithDomain();
                        ruleWithDomain.path = path;
                        ruleWithDomain.domains = domains;
                        ruleWithDomain.inverseDomains = inverseDomains;
                        if (r.rulesWithDomain == null) r.rulesWithDomain = new ArrayList<>();
                        r.rulesWithDomain.add(ruleWithDomain);
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
        }
        br.close();
    }

    private void addSimpleDomain(String domain) {
        Rule r = rulesByDomain.get(domain);
        if (r == null) {
            r = new Rule();
            rulesByDomain.put(domain, r);
        }
        if (r.paths == null) r.paths = new ArrayList<>();
        r.paths.add("/");
    }

    private static Pattern spaceRegex = Pattern.compile("\\s+");

    private void loadFromHostsFile(String firstline, BufferedReader br) throws IOException {
        String line = firstline;
        while (line != null) {
            if (line.isEmpty() || line.startsWith("#")) {
                // comment
            } else {
                String[] components = spaceRegex.split(line);
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

    String getPath(Uri url) {
        String query = url.getEncodedQuery();
        if (query == null) {
            return url.getEncodedPath();
        } else {
            return url.getEncodedPath() + "?" + query;
        }
    }

    @SuppressWarnings("unused")
    boolean shouldBlockHashRegular(Rule r, Uri url, String mainPage) {
        if (r.matchAllPaths) {
            // Only domain, block all paths
            return true;
        }
        if (r.regex == null && r.paths == null) {
            return false;
        }
        if (r.regex == null) {
            // r.paths is not null, so compile a regex
            // Have at least 1 path, compile a regex
            if (r.paths.size() == 1) {
                String path = r.paths.get(0);
                if (path.equals("/") || path.equals("^")) {
                    // Shortcut if path is / or ^
                    r.paths = null;
                    r.matchAllPaths = true;
                    return true;
                }
                r.regex = Pattern.compile("^" + ruleToRegex(path));
            } else {
                StringBuilder sb = new StringBuilder("^(?:");
                sb.append(ruleToRegex(r.paths.get(0)));
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
        return r.regex.matcher(getPath(url)).find();
    }

    boolean shouldBlockHashWithDomain(Rule r, Uri url, String mainPage) {
        if (r.rulesWithDomain == null) {
            return false;
        }
        String mainDomain = Uri.parse(mainPage).getHost();
        if (mainDomain == null || mainPage.isEmpty()) return false;
        String path = null;
        for (RuleWithDomain ruleWithDomain : r.rulesWithDomain) {
            int period;
            while ((period = mainDomain.indexOf('.')) != -1) {
                if (ruleWithDomain.domains.contains(mainDomain) != ruleWithDomain.inverseDomains) {
                    if (ruleWithDomain.matchAllPaths) {
                        // Match all paths
                        return ruleWithDomain.inverseDomains;
                    }
                    if (ruleWithDomain.regex == null) {
                        // Build regex the first time
                        if (ruleWithDomain.path.equals("/") || ruleWithDomain.path.equals("^")) {
                            // Shortcut if path is / or ^
                            ruleWithDomain.path = null;
                            ruleWithDomain.matchAllPaths = true;
                            return true;
                        }
                        ruleWithDomain.regex = Pattern.compile("^" + ruleToRegex(ruleWithDomain.path));
                    }
                    if (path == null) path = getPath(url);
                    if (ruleWithDomain.regex.matcher(path).find()) {
                        // Regex not null, use it
                        return true;
                    }
                }
                mainDomain = mainDomain.substring(period + 1);
            }
        }
        return false;
    }

    boolean shouldBlockHashRule(Rule r, Uri url, String mainPage) {
        return shouldBlockHashRegular(r, url, mainPage) || shouldBlockHashWithDomain(r, url, mainPage);
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

    // Runs on a WebView private thread
    boolean shouldBlock(Uri url, String mainPage) {
        try {
            if (!"http".equals(url.getScheme()) && !"https".equals(url.getScheme())) {
                // E.g. data url
                return false;
            }
            long start2 = System.currentTimeMillis();
            boolean result2 = shouldBlockHash(url, mainPage);
            long end2 = System.currentTimeMillis();

            long start = System.currentTimeMillis();
            boolean result = result2 || (pattern != null && pattern.matcher(url.toString()).find());
            long end = System.currentTimeMillis();

            Log.i(TAG, "RES: " + result + "," + result2 + "    MATCH TIME: " + (end - start) + ", TIME2: " + (end2 - start2) + ", url: " + url);
            result |= result2;
            return result;
        } catch (Exception e) {
            ExceptionLogger.logException(e);
            return false;
        }
    }

    static String ruleToRegex(String rule) {
        boolean anchorEnd = false;
        int len = rule.length();
        if (rule.endsWith("|")) {
            anchorEnd = true;
            len--;
        }
        StringBuilder sb = new StringBuilder(len+32);
        for (int i = 0; i < len; i++) {
            char c = rule.charAt(i);
            switch (c) {
                case '^':
                    sb.append("(?:[^\\w\\d_\\-.%]|$)");
                    break;
                case '*':
                    sb.append(".*?");
                    break;
                case '|': case '.': case '$': case '+': case '?': case '{': case '}':
                case '(': case ')': case '[': case ']': case '\\':
                    sb.append('\\');
                    sb.append(c);
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        if (anchorEnd)
            sb.append('$');
        return sb.toString();
    }
}
