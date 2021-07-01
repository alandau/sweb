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
    @SuppressLint("SimpleDateFormat")
    public static void logException(final Throwable e) {
        final File file = new File(MainActivity.externalLogFilesDir, "sweb_" + new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date()) + ".log");
        try {
            final PrintWriter printWriter = new PrintWriter(new FileWriter(file, true));
            printWriter.println("Exception on " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            e.printStackTrace(printWriter);
            printWriter.close();
			e.printStackTrace();
        } catch (final IOException e1) {
            // Ignore
        }
    }
}
