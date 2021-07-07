package landau.sweb.utils;

import android.annotation.SuppressLint;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import landau.sweb.*;

@SuppressWarnings("WeakerAccess")
public class ExceptionLogger {
    private static final File file = new File(MainActivity.externalLogFilesDir, "sweb_" + new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date()) + ".log");
	private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static PrintWriter printWriter;
	static {
		try {
            printWriter = new PrintWriter(new FileWriter(file, true));
        } catch (final IOException e1) {
            e1.printStackTrace();
        }
	}
	@SuppressLint("SimpleDateFormat")
    public static void e(final Throwable e) {
        printWriter.println("Exception on " + simpleDateFormat.format(new Date()));
		e.printStackTrace(printWriter);
		printWriter.flush();
		e.printStackTrace();
    }
	
	@SuppressLint("SimpleDateFormat")
    public static void e(final String msg, final Throwable e) {
        printWriter.println("Exception on " + simpleDateFormat.format(new Date()));
		e.printStackTrace(printWriter);
		printWriter.flush();
		e.printStackTrace();
    }

	public static void d(final CharSequence tag, final CharSequence st) {
        printWriter.println(simpleDateFormat.format(new Date()) + ": " + tag + ": " + st);
		printWriter.flush();
    }
}
