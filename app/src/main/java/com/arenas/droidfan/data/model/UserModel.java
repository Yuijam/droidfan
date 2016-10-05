package com.arenas.droidfan.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public final class UserModel implements Parcelable {
    public static final int TYPE_FRIENDS = 201;
    public static final int TYPE_FOLLOWERS = 202;
    public static final int TYPE_SEARCH = 203;
    public static final int TYPE_BLOCK = 204;
    public static final int TYPE_SPECIAL = 205;

    public static final String TAG = UserModel.class.getSimpleName();
    private int _id;
    private String id;// id in string format
    private String account; // related account id/userid
    private String owner; // owner id of the item

    private long time; // created at of the item
    private int type;
    private String screenName;
    private String location;
    private String gender;
    private String birthday;
    private String description;
    private String profileImageUrl;
    private String profileImageUrlLarge;
    private String url;
    private String status;
    private int followersCount;
    private int friendsCount;
    private int favouritesCount;
    private int statusesCount;
    private int following;
    private int protect;
    private int notifications;
    private int verified;
    private int followMe;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getProfileImageUrlLarge() {
        return profileImageUrlLarge;
    }

    public void setProfileImageUrlLarge(String profileImageUrlLarge) {
        this.profileImageUrlLarge = profileImageUrlLarge;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }

    public int getFavouritesCount() {
        return favouritesCount;
    }

    public void setFavouritesCount(int favouritesCount) {
        this.favouritesCount = favouritesCount;
    }

    public int getStatusesCount() {
        return statusesCount;
    }

    public void setStatusesCount(int statusesCount) {
        this.statusesCount = statusesCount;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public int getNotifications() {
        return notifications;
    }

    public void setNotifications(int notifications) {
        this.notifications = notifications;
    }

    public int getProtect() {
        return protect;
    }

    public void setProtect(int protect) {
        this.protect = protect;
    }

    public int getVerified() {
        return verified;
    }

    public void setVerified(int verified) {
        this.verified = verified;
    }

    public int getFollowMe() {
        return followMe;
    }

    public void setFollowMe(int followMe) {
        this.followMe = followMe;
    }

    @Override
    public String toString() {
        return "UserModel [ " + ", screenName=" + screenName
                + ", location=" + location + ", gender=" + gender
                + ", birthday=" + birthday + ", description=" + description
                + ", profileImageUrl=" + profileImageUrl
                + ", profileImageUrlLarge=" + profileImageUrlLarge + ", url="
                + url + ", status=" + status + ", followersCount="
                + followersCount + ", friendsCount=" + friendsCount
                + ", favouritesCount=" + favouritesCount + ", statusesCount="
                + statusesCount + ", following=" + following + ", protect="
                + protect + ", notifications=" + notifications + ", verified="
                + verified + ", followMe=" + followMe + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this._id);
        dest.writeString(this.id);
        dest.writeString(this.account);
        dest.writeString(this.owner);
        dest.writeLong(this.time);
        dest.writeInt(this.type);
        dest.writeString(this.screenName);
        dest.writeString(this.location);
        dest.writeString(this.gender);
        dest.writeString(this.birthday);
        dest.writeString(this.description);
        dest.writeString(this.profileImageUrl);
        dest.writeString(this.profileImageUrlLarge);
        dest.writeString(this.url);
        dest.writeString(this.status);
        dest.writeInt(this.followersCount);
        dest.writeInt(this.friendsCount);
        dest.writeInt(this.favouritesCount);
        dest.writeInt(this.statusesCount);
        dest.writeInt(this.following);
        dest.writeInt(this.protect);
        dest.writeInt(this.notifications);
        dest.writeInt(this.verified);
        dest.writeInt(this.followMe);
    }

    public UserModel() {
    }

    protected UserModel(Parcel in) {
        this._id = in.readInt();
        this.id = in.readString();
        this.account = in.readString();
        this.owner = in.readString();
        this.time = in.readLong();
        this.type = in.readInt();
        this.screenName = in.readString();
        this.location = in.readString();
        this.gender = in.readString();
        this.birthday = in.readString();
        this.description = in.readString();
        this.profileImageUrl = in.readString();
        this.profileImageUrlLarge = in.readString();
        this.url = in.readString();
        this.status = in.readString();
        this.followersCount = in.readInt();
        this.friendsCount = in.readInt();
        this.favouritesCount = in.readInt();
        this.statusesCount = in.readInt();
        this.following = in.readInt();
        this.protect = in.readInt();
        this.notifications = in.readInt();
        this.verified = in.readInt();
        this.followMe = in.readInt();
    }

    public static final Parcelable.Creator<UserModel> CREATOR = new Parcelable.Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel source) {
            return new UserModel(source);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };
}
