package net.gnu.common;

import android.app.Application;
import android.os.StrictMode;
import org.geometerplus.android.fbreader.FBReaderApplication;

public class SWebApp extends FBReaderApplication {

    public SWebApp() {
        if (BuildConfig.DEBUG) {
            StrictMode.enableDefaults();
		}
    }

}
