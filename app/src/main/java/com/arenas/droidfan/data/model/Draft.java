package com.arenas.droidfan.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Draft implements Parcelable {
    public static final String TAG = Draft.class.getSimpleName();
    public static final int TYPE_NONE = 0;
    public static final int TYPE_REPLY = 1;
    public static final int TYPE_REPOST = 2;
    public static final Parcelable.Creator<Draft> CREATOR = new Creator<Draft>() {
        @Override
        public Draft createFromParcel(Parcel source) {
            return new Draft(source);
        }

        @Override
        public Draft[] newArray(int size) {
            return new Draft[size];
        }
    };
    public int id;
    public int type;
    public String text;
    public String reply;
    public String repost;
    public String fileName;

    public Draft() {

    }

    public Draft(Parcel in) {
        this.id = in.readInt();
        this.type = in.readInt();
        this.text = in.readString();
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
        dest.writeString(this.text);
        dest.writeString(this.reply);
        dest.writeString(this.repost);
        dest.writeString(this.fileName);

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Draft{");
        sb.append("fileName='").append(fileName).append('\'');
        sb.append(", type=").append(type);
        sb.append(", userId='").append('\'');
        sb.append(", text='").append(text).append('\'');
        sb.append(", location='").append('\'');
        sb.append(", reply='").append(reply).append('\'');
        sb.append(", repost='").append(repost).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
