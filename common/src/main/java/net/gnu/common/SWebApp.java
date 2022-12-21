package net.gnu.common;

import android.app.Application;
import android.os.StrictMode;

public class SWebApp extends Application {

    public SWebApp() {
        if (BuildConfig.DEBUG) {
            StrictMode.enableDefaults();
		}
    }

}
