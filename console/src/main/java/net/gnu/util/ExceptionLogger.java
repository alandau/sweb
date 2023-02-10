package net.gnu.util;

import java.io.PrintWriter;
import java.io.File;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.util.Log;

public class ExceptionLogger {
	
    private static String TAG = "ExceptionLogger";
	
	public static File file;
	
	protected static PrintWriter printWriter;

	static {
		init();
	}

	public static void init() {
		try {
			printWriter = new PrintWriter(System.out);
            file = new File("/storage/emulated/0/Android/data/landau.sweb/files/logs/log.txt");
			file.getParentFile().mkdirs();
			printWriter = new PrintWriter(new FileWriter(file, false));
            //d(TAG, System.getenv());
        } catch (Throwable e) {
			ExceptionLogger.e(TAG, e);
		}
	}
	
    public static void e(final String tag, final String st, final Throwable e) {
        Log.e(tag, st, e);
        printWriter.println(tag + ": " + st);//simpleDateFormat.format(new Date()) + ": " + 
		e.printStackTrace(printWriter);
		printWriter.flush();
    }

    public static void e(final String tag, final Throwable e) {
        Log.e(tag, e.getMessage(), e);
        //printWriter.println(simpleDateFormat.format(new Date()) + ": ");
		printWriter.println(tag);
		e.printStackTrace(printWriter);
		printWriter.flush();
    }

    public static void e(final String tag, final String st) {
        Log.e(tag, st);
        printWriter.println(tag + ": Exception on " + st);//simpleDateFormat.format(new Date()) + 
		printWriter.flush();
    }

	public static void w(final String tag, final String st) {
		Log.w(tag, st);
        printWriter.println(tag + ": " + st); //simpleDateFormat.format(new Date()) + ": " + 
		printWriter.flush();
    }

	public static void i(final String tag, final String st) {
		Log.i(tag, st);
        printWriter.println(tag + ": " + st); //simpleDateFormat.format(new Date()) + ": " + 
		printWriter.flush();
    }

	public static void d(final String tag, final List<String> list) {
		if (list != null) {
			int i = 0;
			for (String st : list) {
				Log.d(tag, ++i + ". " + st);
				printWriter.println(i + ". " + tag + ": " + st);
			}
			printWriter.flush();
		}
	}

	@SuppressWarnings("rawtypes")
	public static void d(final String tag, final Map list) {
		if (list != null) {
			int i = 0;
			for (Object o : list.entrySet()) {
				final Map.Entry st = (Map.Entry)o;
				Log.d(tag, ++i + ". " + st.getKey() + "=" + st.getValue());
				printWriter.println(tag + ": " + i + ". " + st.getKey() + "="+st.getValue());
			}
			printWriter.flush();
		}
	}

	public static void d(final String tag, final String st) {
		Log.d(tag, st);
        printWriter.println(tag + ": " + st); //simpleDateFormat.format(new Date()) + ": " + 
		printWriter.flush();
    }
}
