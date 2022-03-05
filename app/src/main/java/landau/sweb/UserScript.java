/*
 * Copyright (C) 2017-2021 Hazuki
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package landau.sweb;

import android.os.Parcel;
import android.os.Parcelable;
//import jp.hazuki.yuzubrowser.core.utility.extensions.forEachLine
//import jp.hazuki.yuzubrowser.core.utility.log.ErrorReport
//import jp.hazuki.yuzubrowser.core.utility.log.Logger
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Pattern;
import java.util.*;
import landau.sweb.utils.*;
import java.util.regex.*;
import java.io.Serializable;

public class UserScript implements Parcelable, Serializable {

    UserScriptInfo info;

    String name = null;
    String version = null;
    String author = null;
    String description = null;
    ArrayList<Pattern> include = new ArrayList<Pattern>(0);
    ArrayList<Pattern> exclude = new ArrayList<Pattern>(0);
    boolean isUnwrap = false;
    RunAt runAt = RunAt.END;

    public UserScript() {
        info = new UserScriptInfo();
    }

    public UserScript(long id, String data, boolean enabled) {
        info = new UserScriptInfo(id, data, enabled);
        loadHeaderData();
    }

    public UserScript(String data) {
        info = new UserScriptInfo(data);
        loadHeaderData();
    }

    public UserScript(UserScriptInfo info) {
        this.info = info;
        loadHeaderData();
    }

	public void setId(long id) {
		info.id = id;
	}

	public Long getId() {
		return info.id;
	}

	public void setData(String data) {
		info.data = data;
		loadHeaderData();
	}

	public String getData() {
		return info.data;
	}

	public String getRunnable() {
		if (isUnwrap) {
            return info.data;
        } else {
            return "(function() {\n" + info.data + "\n})()";
        }
	}

	public void setEnabled(boolean isEnabled) {
		info.isEnabled = isEnabled;
	}

	public boolean getEnabled() {
		return info.isEnabled;
	}

    @Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(info.id);
        dest.writeString(info.data);
        dest.writeInt(info.isEnabled? 1 : 0);
	}
	
    public UserScript(Parcel source) {
        long id = source.readLong();
        String data = source.readString();
        boolean enabled = source.readInt() == 1;
        info = new UserScriptInfo(id, data, enabled);
        loadHeaderData();
    }

    private void loadHeaderData() {
        //name = null;
        version = null;
        description = null;
        include.clear();
        exclude.clear();

        try {
			//ExceptionLogger.e(TAG, "info.data " + info.data);
			BufferedReader reader = new BufferedReader(new StringReader(info.data));
			String it = reader.readLine();
			if (!sHeaderStartPattern.matcher(it).matches()) {
                ExceptionLogger.e(TAG, "Header (start) parser error");
                return;
            }

			String line;
			Matcher matcher;
			String field;
			String value;
			while (reader.ready()) {
				line = reader.readLine();
				if (line != null && line.trim().length() > 0) {
					matcher = sHeaderMainPattern.matcher(line);
					if (!matcher.matches()) {
						if (sHeaderEndPattern.matcher(line).matches()) {
							return;
						}
						ExceptionLogger.e(TAG, "Unknown header : " + line);
					} else {
						field = matcher.group(1);
						value = matcher.group(2);
						readData(field, value, line);
					}
				}
			}
            ExceptionLogger.e(TAG, "Header (end) parser error");
        } catch (IOException e) {
            ExceptionLogger.e(TAG, e.getMessage(),e);
        }
    }

    private void readData(String field, String value, String line) {
		//ExceptionLogger.d(TAG, "readData.field " + field + ", value " + value);
		if (value == null) {
			return;
		}
        if ("name".equalsIgnoreCase(field)) {
            //name = value;
        } else if ("version".equalsIgnoreCase(field)) {
            version = value;
        } else if ("author".equalsIgnoreCase(field)) {
            author = value;
        } else if ("description".equalsIgnoreCase(field)) {
            description = value;
        } else if ("include".equalsIgnoreCase(field)
				 || "match".equalsIgnoreCase(field)) {
//            makeUrlPattern(value)?.let {
//                include.add(it)
//            }
			final String newValue = Util.textToRegex(value);
			include.add(Pattern.compile(newValue));
        } else if ("exclude".equalsIgnoreCase(field)) {
//            makeUrlPattern(value)?.let {
//                exclude.add(it)
//            }
			final String newValue = Util.textToRegex(value);
			exclude.add(Pattern.compile(newValue));
        } else if ("unwrap".equalsIgnoreCase(field)) {
            isUnwrap = true;
        } else if ("run-at".equalsIgnoreCase(field)) {
			if (value.equalsIgnoreCase("document-start")) {
				runAt = RunAt.START;
			} else if (value.equalsIgnoreCase("document-idle")) {
				runAt = RunAt.IDLE;
			} else {
				runAt = RunAt.END;
			}
//        } else if ("match".equalsIgnoreCase(field) && value != null) {
//            String patternUrl = "^" + value.replace("?", "\\?").replace(".", "\\.")
//                    .replace("*", ".*").replace("+", ".+")
//                    .replace("://.*\\.", "://((?![\\./]).)*\\.").replace("^\\.\\*://".toRegex(), "https?://");
//            makeUrlPatternParsed(patternUrl)?.let {
//                include.add(it)
//            }
        } else {
            ExceptionLogger.e(TAG, "Unknown header : " + line);
        }
    }

    enum RunAt {
        START,
        END,
        IDLE
    }

	public Parcelable.Creator<UserScript> CREATOR = new Parcelable.Creator<UserScript>() {
		@Override
		public UserScript createFromParcel(Parcel parcel) {
			return new UserScript(parcel);
		}

		@Override
		public UserScript[] newArray(int size) {
			return new UserScript[size];
		}
    };

	private final String TAG = "UserScript";
	
	private Pattern sHeaderStartPattern = Pattern.compile("\\s*//\\s*==UserScript==\\s*", Pattern.CASE_INSENSITIVE);
	private Pattern sHeaderEndPattern = Pattern.compile("\\s*//\\s*==/UserScript==\\s*", Pattern.CASE_INSENSITIVE);
	private Pattern sHeaderMainPattern = Pattern.compile("\\s*//\\s*@(\\S+)(?:\\s+(.*))?", Pattern.CASE_INSENSITIVE);
}
