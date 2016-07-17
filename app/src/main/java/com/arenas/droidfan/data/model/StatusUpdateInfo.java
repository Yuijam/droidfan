package com.arenas.droidfan.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class StatusUpdateInfo implements Parcelable {
    public static final String TAG = StatusUpdateInfo.class.getSimpleName();
    public static final int TYPE_NONE = 0;
    public static final int TYPE_REPLY = 1;
    public static final int TYPE_REPOST = 2;
    public static final Parcelable.Creator<StatusUpdateInfo> CREATOR = new Creator<StatusUpdateInfo>() {
        @Override
        public StatusUpdateInfo createFromParcel(Parcel source) {
            return new StatusUpdateInfo(source);
        }

        @Override
        public StatusUpdateInfo[] newArray(int size) {
            return new StatusUpdateInfo[size];
        }
    };
    public int id;
    public int type;
    public String userId;
    public String text;
    public String location;
    public String reply;
    public String repost;
    public String fileName;

    public StatusUpdateInfo() {

    }

    public StatusUpdateInfo(Parcel in) {
        this.id = in.readInt();
        this.type = in.readInt();
        this.userId = in.readString();
        this.text = in.readString();
        this.location = in.readString();
        this.reply = in.readString();
        this.repost = in.readString();
        this.fileName = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.type);
        dest.writeString(this.userId);
        dest.writeString(this.text);
        dest.writeString(this.location);
        dest.writeString(this.reply);
        dest.writeString(this.repost);
        dest.writeString(this.fileName);

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StatusUpdateInfo{");
        sb.append("fileName='").append(fileName).append('\'');
        sb.append(", type=").append(type);
        sb.append(", userId='").append(userId).append('\'');
        sb.append(", text='").append(text).append('\'');
        sb.append(", location='").append(location).append('\'');
        sb.append(", reply='").append(reply).append('\'');
        sb.append(", repost='").append(repost).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
