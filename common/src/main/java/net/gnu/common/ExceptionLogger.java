package net.gnu.common;

import android.annotation.SuppressLint;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import android.util.Log;
import net.gnu.util.FileUtil;

@SuppressWarnings("WeakerAccess")
public class ExceptionLogger extends net.gnu.util.ExceptionLogger {
    
	public static void init() {
		try {
            file = new File(ParentActivity.externalLogFilesDir, "sweb_" + new SimpleDateFormat("yyyy.MM.dd HH.mm.ss").format(new Date()) + ".log");
			printWriter = new PrintWriter(new FileWriter(file, true));
        } catch (final IOException e) {
            e.printStackTrace();
        }
	}
}
