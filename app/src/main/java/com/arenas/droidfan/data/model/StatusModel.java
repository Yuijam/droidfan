package com.arenas.droidfan.data.model;

import java.util.List;


public class StatusModel {
    public static final int TYPE_HOME = 101;
    public static final int TYPE_MENTIONS = 102;
    public static final int TYPE_PUBLIC = 103;
    public static final int TYPE_USER = 104;
    public static final int TYPE_SEARCH = 105;
    public static final int TYPE_CONTEXT = 106;
    public static final int TYPE_FAVORITES = 107;
    public static final int TYPE_RETWEET = 108;
    public static final int TYPE_PHOTO = 109;

    public static final String TAG = StatusModel.class.getSimpleName();
    private int _id;
    private String id;
    private String account;
    private String owner;
    private long rawId;
    private long time;
    private String text; // html format text
    private String simpleText; // plain text
    private String source; // source
    private String geo; // geo location info
    private String photo;// photo url or video url
    private String userId; //
    private String userScreenName;
    private String userProfileImageUrl;
    private String inReplyToStatusId;
    private String inReplyToUserId;
    private String inReplyToScreenName;
    private String rtStatusId;
    private String rtUserId;
    private String rtScreenName;
    private String photoImageUrl;
    private String photoThumbUrl;
    private String photoLargeUrl;
    private int truncated;
    private int favorited;
    private int retweeted;
    private int self;
    private int read;
    private int thread;
    private int special;
    private List<String> urls;
    private List<String> hashtags;
    private List<String> mentions;
    private UserModel user;

    public StatusModel() {
    }

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

    public long getRawId() {
        return rawId;
    }

    public void setRawId(long rawId) {
        this.rawId = rawId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSimpleText() {
        return simpleText;
    }

    public void setSimpleText(String simpleText) {
        this.simpleText = simpleText;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserScreenName() {
        return userScreenName;
    }

    public void setUserScreenName(String userScreenName) {
        this.userScreenName = userScreenName;
    }

    public String getInReplyToStatusId() {
        return inReplyToStatusId;
    }

    public void setInReplyToStatusId(String inReplyToStatusId) {
        this.inReplyToStatusId = inReplyToStatusId;
    }

    public String getInReplyToUserId() {
        return inReplyToUserId;
    }

    public void setInReplyToUserId(String inReplyToUserId) {
        this.inReplyToUserId = inReplyToUserId;
    }

    public String getInReplyToScreenName() {
        return inReplyToScreenName;
    }

    public void setInReplyToScreenName(String inReplyToScreenName) {
        this.inReplyToScreenName = inReplyToScreenName;
    }

    public String getRtStatusId() {
        return rtStatusId;
    }

    public void setRtStatusId(String rtStatusId) {
        this.rtStatusId = rtStatusId;
    }

    public String getRtUserId() {
        return rtUserId;
    }

    public void setRtUserId(String rtUserId) {
        this.rtUserId = rtUserId;
    }

    public String getRtScreenName() {
        return rtScreenName;
    }

    public void setRtScreenName(String rtScreenName) {
        this.rtScreenName = rtScreenName;
    }

    public String getUserProfileImageUrl() {
        return userProfileImageUrl;
    }

    public void setUserProfileImageUrl(String userProfileImageUrl) {
        this.userProfileImageUrl = userProfileImageUrl;
    }

    public String getPhotoImageUrl() {
        return photoImageUrl;
    }

    public void setPhotoImageUrl(String photoImageUrl) {
        this.photoImageUrl = photoImageUrl;
    }

    public String getPhotoThumbUrl() {
        return photoThumbUrl;
    }

    public void setPhotoThumbUrl(String photoThumbUrl) {
        this.photoThumbUrl = photoThumbUrl;
    }

    public String getPhotoLargeUrl() {
        return photoLargeUrl;
    }

    public void setPhotoLargeUrl(String photoLargeUrl) {
        this.photoLargeUrl = photoLargeUrl;
    }

    public void setTruncated(int truncated) {
        this.truncated = truncated;
    }

    public int getTruncated(){
        return truncated;
    }

    public void setFavorited(int favorited) {
        this.favorited = favorited;
    }

    public int getFavorited(){
        return favorited;
    }

    public void setRetweeted(int retweeted) {
        this.retweeted = retweeted;
    }

    public int getRetweeted(){
        return retweeted;
    }

    public int getSelf() {
        return self;
    }

    public void setSelf(int self) {
        this.self = self;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public int getThread() {
        return thread;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }

    public int getSpecial() {
        return special;
    }

    public void setSpecial(int special) {
        this.special = special;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public List<String> getMentions() {
        return mentions;
    }

    public void setMentions(List<String> mentions) {
        this.mentions = mentions;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
        if (user != null) {
            this.userId = user.getId();
            this.userScreenName = user.getScreenName();
            this.userProfileImageUrl = user.getProfileImageUrlLarge();
        }
    }

    @Override
    public String toString() {
        return "StatusModel{" +
                "text='" + text + '\'' +
                ", simpleText='" + simpleText + '\'' +
                ", source='" + source + '\'' +
                ", geo='" + geo + '\'' +
                ", photo='" + photo + '\'' +
                ", userId='" + userId + '\'' +
                ", userScreenName='" + userScreenName + '\'' +
                ", userProfileImageUrl='" + userProfileImageUrl + '\'' +
                ", inReplyToStatusId='" + inReplyToStatusId + '\'' +
                ", inReplyToUserId='" + inReplyToUserId + '\'' +
                ", inReplyToScreenName='" + inReplyToScreenName + '\'' +
                ", rtStatusId='" + rtStatusId + '\'' +
                ", rtUserId='" + rtUserId + '\'' +
                ", rtScreenName='" + rtScreenName + '\'' +
                ", photoImageUrl='" + photoImageUrl + '\'' +
                ", photoThumbUrl='" + photoThumbUrl + '\'' +
                ", photoLargeUrl='" + photoLargeUrl + '\'' +
                ", truncated=" + truncated +
                ", favorited=" + favorited +
                ", retweeted=" + retweeted +
                ", self=" + self +
                ", read=" + read +
                ", thread=" + thread +
                ", special=" + special +
                ", urls=" + urls +
                ", hashtags=" + hashtags +
                ", mentions=" + mentions +
                ", user=" + user +
                '}';
    }

}
