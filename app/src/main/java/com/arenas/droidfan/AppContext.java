package com.arenas.droidfan;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.arenas.droidfan.api.Api;
import com.arenas.droidfan.api.ApiFactory;
import com.arenas.droidfan.config.AccountInfo;
import com.arenas.droidfan.config.AccountStore;
import com.arenas.droidfan.data.model.UserModel;

import org.oauthsimple.model.OAuthToken;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * @author mcxiaoke
 * @version 7.5 2012.02.27
 */

public class AppContext extends Application {

    public final static boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "Application";
    public static int versionCode;
    public static String versionName;
    public static String packageName;
    public static PackageInfo info;
    public static boolean active;
    public static boolean homeVisible;
    private static HashMap<String, WeakReference<Activity>> contexts = new HashMap<>();
    private static AccountInfo accountInfo;

    private static Api api;
    private static AppContext instance;

    private static boolean firstLoad;

    public static void doLogin(Context context) {
        if (DEBUG) {
            Log.v(TAG, "doLogin()");
        }
        AppContext.clearAccountInfo(context);
//        UIController.showLogin(context);
    }

    public static void clearAccountInfo(Context context) {
        accountInfo.clear();
        api.setAccessToken(null);
        api.setAccount(null);
        AccountStore store = new AccountStore(context);
        store.clear();

        if (DEBUG) {
            Log.v(TAG, "clearAccountInfo()");
        }
    }

    public static void updateLoginInfo(Context context, String loginName,
                                       String loginPassword) {

        accountInfo.setLoginInfo(loginName, loginPassword);

        AccountStore store = new AccountStore(context);
        store.saveLoginInfo(loginName, loginPassword);
        if (DEBUG) {
            Log.v("App", "loginPassword loginName: " + loginName);
            Log.v("App", "loginPassword loginPassword: " + loginPassword);
        }

    }

    public static void updateUserInfo(Context context, final UserModel u) {

        String account = u.getId();
        String screenName = u.getScreenName();
        String profileImage = u.getProfileImageUrlLarge();

        accountInfo.setAccount(account);
        accountInfo.setScreenName(screenName);
        accountInfo.setProfileImage(profileImage);
        api.setAccount(account);

        AccountStore store = new AccountStore(context);
        store.saveUserInfo(account, screenName, profileImage);
        if (DEBUG) {
            Log.v("App", "updateUserInfo UserModel: " + u);
            Log.v("App", "updateUserInfo accountInfo: " + accountInfo);
        }

    }

    public static void updateAccessToken(Context context, OAuthToken token) {
        accountInfo.setAccessToken(token);
        api.setAccessToken(token);
        AccountStore store = new AccountStore(context);
        store.saveAccessToken(token);
        if (DEBUG) {
            Log.v("App", "updateAccessToken AccessToken: " + token);
            Log.v("App", "updateAccessToken accountInfo: " + accountInfo);
        }
    }

    public static boolean isFirstLoad() {
        return firstLoad;
    }

    public static void setFirstLoad(boolean firstLoad) {
        AppContext.firstLoad = firstLoad;
    }

    public static String getAccount() {
        return accountInfo.getAccount();
    }

    public static AccountInfo getAccountInfo() {
        return accountInfo;
    }

    public static String getScreenName() {
        return accountInfo.getScreenName();
    }

    public static String getAvatarUrl(){
        return accountInfo.getProfileImage();
    }

    public static Api getApi() {
        return api;
    }

    public static AppContext getApp() {
        return instance;
    }

    public static boolean isVerified() {
        return accountInfo != null && accountInfo.isVerified();
    }

    public static synchronized void setActiveContext(Activity context) {
        WeakReference<Activity> reference = new WeakReference<Activity>(context);
        contexts.put(context.getClass().getSimpleName(), reference);
    }

    public static synchronized Activity getActiveContext(String className) {
        WeakReference<Activity> reference = contexts.get(className);
        if (reference == null) {
            return null;
        }

        final Activity context = reference.get();

        if (context == null) {
            contexts.remove(className);
        }

        return context;
    }

    public AppContext getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initAppInfo();
//        initialize();
        initAccountInfo();
    }

//    private void initialize() {
//        instance = this;
//        MobclickAgent.setDebugMode(DEBUG);
//        ImageLoader.getInstance().init(getDefaultImageLoaderConfiguration());
//        FANFOU_DATE_FORMAT.setTimeZone(
//                getTimeZone("GMT"));
//    }

    private void initAccountInfo() {
        AccountStore store = new AccountStore(this);
        accountInfo = store.read();

        api = ApiFactory.getDefaultApi();
        if (accountInfo.isVerified()) {
            api.setAccessToken(accountInfo.getAccessToken());
            api.setAccount(accountInfo.getAccount());
        }

//        if (DEBUG) {
//            Log.v(TAG, "initAccountInfo() accountInfo: " + accountInfo);
//        }
    }

    private void initAppInfo() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            packageName = packageInfo.packageName;
            versionCode = packageInfo.versionCode;
            versionName = packageInfo.versionName;
//            Bundle bundle = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA).metaData;
            if (DEBUG) {
                Log.v(TAG, "initAppInfo() versionCode: " + versionCode
                        + " versionName: " + versionName);
            }
        } catch (NameNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

//    private DisplayImageOptions getDefaultDisplayImageOptions() {
//        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
//        builder.cacheInMemory(true).cacheOnDisc(true);
//        builder.bitmapConfig(Bitmap.Config.RGB_565);
//        builder.showImageForEmptyUri(R.drawable.ic_head);
//        builder.showImageOnFail(R.drawable.ic_head);
//        builder.showImageOnLoading(R.drawable.ic_head);
//        builder.imageScaleType(ImageScaleType.IN_SAMPLE_INT);
//        return builder.build();
//    }
//
//    private ImageLoaderConfiguration getDefaultImageLoaderConfiguration() {
//        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
//                this);
//        builder.defaultDisplayImageOptions(getDefaultDisplayImageOptions());
//        builder.denyCacheImageMultipleSizesInMemory();
//        builder.discCacheSize(100 * 1024 * 1024);
//        builder.memoryCacheSizePercentage(25);
//        builder.tasksProcessingOrder(QueueProcessingType.FIFO);
//        return builder.build();
//    }

}
