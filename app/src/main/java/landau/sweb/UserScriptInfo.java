/*
 * Copyright (C) 2017 Hazuki
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
import java.io.Serializable;

public class UserScriptInfo implements Parcelable, Serializable {

    long id = -1;
    String data = "";
    boolean isEnabled = true;

    public UserScriptInfo(long id, String data, boolean enabled) {
        this.id = id;
        this.data = data;
        this.isEnabled = enabled;
    }

    public UserScriptInfo(String data) {
        this.data = data;
    }

    public UserScriptInfo() {
	}

    private UserScriptInfo(Parcel parcel) {
        id = parcel.readLong();
        data = parcel.readString();
        isEnabled = parcel.readByte() != 0;
    }

	@Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(data);
        dest.writeByte((byte)(isEnabled ? 1 : 0));
    }

    public int describeContents() {
		return 0;
	}

    public Parcelable.Creator<UserScriptInfo> CREATOR = new Parcelable.Creator<UserScriptInfo>() {
		@Override
		public UserScriptInfo createFromParcel(Parcel parcel) {
			return new UserScriptInfo(parcel);
		}

		@Override
		public UserScriptInfo[] newArray(int size) {
			return new UserScriptInfo[size];
		}
    };

}
