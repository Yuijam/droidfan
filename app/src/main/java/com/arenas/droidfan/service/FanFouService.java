package com.arenas.droidfan.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.api.Api;
import com.arenas.droidfan.api.ApiException;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.data.HomeStatusColumns;
import com.arenas.droidfan.data.ProfileColumns;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.DirectMessageModel;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.data.model.UserModel;
import com.arenas.droidfan.main.MainActivity;
import com.arenas.droidfan.main.hometimeline.HomeTimelineFragment;
import com.arenas.droidfan.users.UserListActivity;

import java.io.File;
import java.util.List;

/**
 * Created by Arenas on 2016/7/10.
 */
public class FanFouService extends IntentService {

    private static final String TAG = FanFouService.class.getSimpleName();
    //intent extra
    public static final String EXTRA_REQUEST = "extra_fanfou";
    public static final String EXTRA_PAGING = "extra_paging";
    public static final String EXTRA_STATUS_TEXT = "extra_status_text";
    public static final String EXTRA_PHOTO = "extra_photo";
    public static final String EXTRA_MSG_ID = "extra_msg_id";
    public static final String EXTRA_USER_ID = "extra_user_id";
    public static final String EXTRA_USER_A = "extra_user_a";
    public static final String EXTRA_USER_B = "extra_user_b";
    public static final String EXTRA_DM_TEXT = "extra_dm_text";

    public static final String EXTRA_IS_FRIEND = "extra_user_is_friend";
    public static final String EXTRA_HAS_NEW = "extra_has_new";
    public static final String EXTRA_SUCCESS = "extra_has_success";
    public static final String EXTRA_IDS = "extra_has_ids";

    //requestCode
    public static final int HOME_TIMELINE = 1;
    public static final int UPDATE_STATUS = 2;
    public static final int UPLOAD_PHOTO = 3;
    public static final int REPLY = 4;
    public static final int RETWEET = 5;
    public static final int FAVORITE = 6;
    public static final int UNFAVORITE = 7;
    public static final int MENTIONS = 8;
    public static final int PUBLIC = 9;
    public static final int PROFILE_TIMELINE = 10;
    public static final int USER = 11;
    public static final int FAVORITES_LIST = 12;
    public static final int DELETE = 13;
    public static final int IS_FRIEND = 14;
    public static final int FOLLOW = 15;
    public static final int UNFOLLOW = 16;
    public static final int FOLLOWERS = 17;
    public static final int FOLLOWING = 18;
    public static final int CONVERSATION_LIST = 20;
    public static final int CONVERSATION = 21;
    public static final int SEND_DM = 22;
    public static final int PHOTO_TIMELINE = 23;

    //filter
    public static final String FILTER_USERS = "com.arenas.droidfan.USERS";
    public static final String FILTER_CONVERSATION_LIST = "com.arenas.droidfan.CONVERSATION_LIST";
    public static final String FILTER_CONVERSATION = "com.arenas.droidfan.CONVERSATION";
    public static final String FILTER_PHOTOTIMELINE = "com.arenas.droidfan.PHOTOTIMELINE";

    private FanFouDB mFanFouDB;
    private static final Api mApi = AppContext.getApi();
    private String mFilterAction;

    private boolean mHasNewData;
    private boolean mIsFriend;
    private boolean mSuccess;

    public FanFouService(){
        super("FanFouService");
    }

    public static void getPhotoTimeline(Context context , String owner , Paging paging){
        start(context , PHOTO_TIMELINE , paging , null , owner , null);
    }

    public static void sendDM(Context context , String recipientId , String reply_msg_id , String text){
        Intent intent = new Intent(context , FanFouService.class);
        intent.putExtra(EXTRA_REQUEST , SEND_DM);
        intent.putExtra(EXTRA_USER_ID , recipientId);
//        intent.putExtra(EXTRA_USER_B , recipientId);
        intent.putExtra(EXTRA_DM_TEXT , text);
        context.startService(intent);
    }

    public static void getConversationList(Context context , Paging paging ){
        start(context , CONVERSATION_LIST , paging , null , null , null);
    }

    public static void getConversation(Context context , Paging paging , String userId){
        start(context , CONVERSATION , paging , null , userId , null);
    }

    public static void getFollowing(Context context , String userId){
        start(context , FOLLOWING, null , null , userId , null);
    }

    public static void getFollowers(Context context , String userId){
        start(context , FOLLOWERS, null , null , userId , null);
    }

    public static void unfollow(Context context , String userId){
        start(context , UNFOLLOW , null , null , userId , null);
    }

    public static void follow(Context context , String userId){
        start(context , FOLLOW , null , null , userId , null);
    }

    public static void isFriend(Context context , String userA , String userB){
        Intent intent = new Intent(context , FanFouService.class);
        intent.putExtra(EXTRA_REQUEST , IS_FRIEND);
        intent.putExtra(EXTRA_USER_A , userA);
        intent.putExtra(EXTRA_USER_B , userB);
        context.startService(intent);
    }

    public static void delete(Context context , String msgId){
        start(context , DELETE , null , msgId , null , null);
    }

    public static void getFavoritesList(Context context , String userId , Paging paging){
        start(context , FAVORITES_LIST , paging , null , userId , null);
    }

    public static void getUser(Context context , String userId ){
        start(context , USER , null , null , userId , null);
    }

    public static void getProfileTimeline(Context context , Paging paging , String userId){
        start(context , PROFILE_TIMELINE , paging , null , userId , null);
    }

    public static void reply(Context context , String msgId , String statusText){
        start(context , REPLY , null , msgId , null , statusText);
    }

    public static void retweet(Context context , String msgId , String statusText){
        start(context , RETWEET , null , msgId , null , statusText);
    }

    public static void newStatus(Context context , String statusText){
        start(context , UPDATE_STATUS , null , null , null , statusText);
    }

    public static void favorite(Context context , String msgId){
        start(context , FAVORITE , null , msgId , null , null);
    }

    public static void unfavorite(Context context , String msgId){
        start(context , UNFAVORITE , null , msgId , null , null);
    }

    public static void getMentions(Context context , Paging paging){
        start(context , MENTIONS , paging , null , null , null);
    }

    public static void getHomeTimeline(Context context , Paging paging){
        start(context , HOME_TIMELINE , paging , null , null , null);
    }

    public static void getPublicTimeline(Context context ){
        start(context , PUBLIC , null , null , null , null);
    }

    private static void start(Context context , int requestCode , Paging paging , String msgId ,
                              String userId ,String statusText ){
        Intent intent = new Intent(context , FanFouService.class);
        intent.putExtra(EXTRA_REQUEST , requestCode);
        intent.putExtra(EXTRA_PAGING , paging);
        intent.putExtra(EXTRA_MSG_ID , msgId);
        intent.putExtra(EXTRA_USER_ID , userId);
        intent.putExtra(EXTRA_STATUS_TEXT , statusText);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int request = intent.getIntExtra(EXTRA_REQUEST , 0);
        String mStatusText = intent.getStringExtra(EXTRA_STATUS_TEXT);
        String mId = intent.getStringExtra(EXTRA_MSG_ID);
        String userA = intent.getStringExtra(EXTRA_USER_A);
        String userB = intent.getStringExtra(EXTRA_USER_B);
        String userId = intent.getStringExtra(EXTRA_USER_ID);
        Paging p = intent.getParcelableExtra(EXTRA_PAGING);
        String message = intent.getStringExtra(EXTRA_DM_TEXT);
        mFanFouDB = FanFouDB.getInstance(this);
        try {
            switch (request){
                case HOME_TIMELINE:
                    if (mApi.getHomeTimeline(p).size() == 0){
                        mHasNewData = false;
                    }else {
                        saveHomeTLStatus(mApi.getHomeTimeline(p));
                        mHasNewData = true;
                    }
                    mFilterAction = HomeTimelineFragment.FILTER_HOMETIMELINE;
                    break;
                case UPDATE_STATUS:
                    mApi.updateStatus(mStatusText , "" , "" , "");
                    Log.d(TAG , "UPDATE STATUS -------- > ");
                    break;
                case UPLOAD_PHOTO:
                    File photo = (File)intent.getSerializableExtra(EXTRA_PHOTO);
                    mApi.uploadPhoto(photo , mStatusText , "");
                    break;
                case REPLY:
                    mApi.updateStatus(mStatusText , mId , "" , "");
                    Log.d(TAG , "REPLY STATUS -------- > ");
                    break;
                case RETWEET:
                    mApi.updateStatus(mStatusText , "" , mId , "");
                    Log.d(TAG , "RETWEET STATUS -------- > ");
                    break;
                case FAVORITE:
                    mApi.favorite(mId);
                    break;
                case UNFAVORITE:
                    mApi.unfavorite(mId);
                    break;
                case MENTIONS:
                    if (mApi.getMentions(p).size() == 0){
                        mHasNewData = false;
                    }else {
                        saveMentions(mApi.getMentions(p));
                        mHasNewData = true;
                    }
                    mFilterAction = HomeTimelineFragment.FILTER_HOMETIMELINE;
                    Log.d(TAG , "getMetions--------->");
                    break;
                case PUBLIC:
                    savePublicStatus(mApi.getPublicTimeline());
                    mFilterAction = HomeTimelineFragment.FILTER_PUBLICTIMELINE;
                    Log.d(TAG , "getPublicStatus------->");
                    break;
                case PROFILE_TIMELINE:
                    mFilterAction = HomeTimelineFragment.FILTER_PROFILETIMELINE;
                    if (mApi.getUserTimeline(userId , p).size() == 0 ){
                        mHasNewData = false;
                    }else {
                        saveProfileStatus(mApi.getUserTimeline(userId , p));
                        mHasNewData = true;
                    }
                    break;
                case USER:
                    mFilterAction = HomeTimelineFragment.FILTER_USER;
                    saveUser(mApi.showUser(userId) , 0);
                    break;
                case FAVORITES_LIST:
                    Log.d(TAG , "get favorites list-------->");
                    mFilterAction = HomeTimelineFragment.FILTER_FAVORITES;
                    if (mApi.getFavorites(userId , p).size() == 0){
                        mHasNewData = false;
                    }else {
                        saveFavoritesList(mApi.getFavorites(userId , p));
                        mHasNewData = true;
                    }
                    break;
                case DELETE:
                    mFanFouDB.deleteItem(HomeStatusColumns.TABLE_NAME , mId);
                    mFanFouDB.deleteItem(ProfileColumns.TABLE_NAME , mId);
                    mApi.deleteStatus(mId);
                    break;
                case IS_FRIEND:
                    mIsFriend = mApi.isFriends(userA , userB);
                    break;
                case FOLLOW:
                    saveUser(mApi.follow(userId) , 0);
                    mFilterAction = HomeTimelineFragment.FILTER_PROFILETIMELINE;
                    break;
                case UNFOLLOW:
                    saveUser(mApi.unfollow(userId) , 0);
                    mFilterAction = HomeTimelineFragment.FILTER_PROFILETIMELINE;
                    break;
                case FOLLOWERS:
                    mFanFouDB.saveFollowers(mApi.getFollowers(userId , p) , userId);
                    mFilterAction = FILTER_USERS;
                    break;
                case FOLLOWING:
                    mFanFouDB.saveFollowing(mApi.getFriends(userId , p) , userId);
                    mFilterAction = FILTER_USERS;
                    break;
                case CONVERSATION:
                    Log.d(TAG , "userId = " + userId);
                    mFanFouDB.saveDirectMessages(mApi.getConversation(userId , p));
                    mFilterAction = FILTER_CONVERSATION;
                    break;
                case CONVERSATION_LIST:
                    mFanFouDB.saveConversationList(mApi.getConversationList(p));
                    mFilterAction = FILTER_CONVERSATION_LIST;
                    break;
                case SEND_DM:
                    DirectMessageModel model = mApi.createDirectmessage(userId , message , userB);
                    model.conversationId = userId;
                    mFanFouDB.saveDirectMessage(model);
                    mFilterAction = FILTER_CONVERSATION;
                    break;
                case PHOTO_TIMELINE:
                    mFanFouDB.savePhotoTimeline(mApi.getPhotosTimeline(userId , p));
                    mFilterAction = FILTER_PHOTOTIMELINE;
                    break;
            }
        }catch (ApiException e){
            e.toString();
        }

    }

    private void saveFavoritesList(List<StatusModel> statusModels){
        for (StatusModel s : statusModels){
            mFanFouDB.saveFavorites(s);
        }
    }

    private void saveUserList(List<UserModel> userModels , int type){
        for (UserModel u : userModels){
            saveUser(u , type);
        }
    }

    private void saveUser(UserModel user , int type){
        mFanFouDB.saveUser(user , type);
    }

    private void saveProfileStatus(List<StatusModel> statusModels){
        for (StatusModel s : statusModels){
            mFanFouDB.saveProfileStatus(s);
        }
    }

    private void savePublicStatus(List<StatusModel> statusModels){
        for (StatusModel s : statusModels){
            mFanFouDB.savePublicStatus(s);
        }
    }

    private void saveMentions(List<StatusModel> statusModels){
        for (StatusModel s : statusModels){
            mFanFouDB.saveNoticeStatus(s);
        }
    }

    private void saveHomeTLStatus(List<StatusModel> statusModels){
        for (StatusModel s : statusModels){
            mFanFouDB.saveHomeTLStatus(s);
        }
    }

    private void sendLocalBroadcast(String filterAction){
        Intent intent = new Intent(filterAction);
        Log.d(TAG , "filterAction = " + filterAction);
        intent.putExtra(EXTRA_HAS_NEW , mHasNewData);
        intent.putExtra(EXTRA_IS_FRIEND , mIsFriend);
        intent.putExtra(EXTRA_SUCCESS , mSuccess);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sendLocalBroadcast(mFilterAction);
    }
}
