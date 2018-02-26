package landau.sweb.utils;

import android.annotation.SuppressLint;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("WeakerAccess")
public class ExceptionLogger {
    @SuppressLint("SimpleDateFormat")
    public static void logException(Throwable e) {
        File file = new File(Environment.getExternalStorageDirectory(), "sweb.log");
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(file, true));
            printWriter.println("Exception on " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            e.printStackTrace(printWriter);
            printWriter.close();
        } catch (IOException e1) {
            // Ignore
        }
    }
}
