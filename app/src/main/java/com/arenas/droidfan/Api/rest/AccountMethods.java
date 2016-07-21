package com.arenas.droidfan.api.rest;


import com.arenas.droidfan.api.ApiException;
import com.arenas.droidfan.data.model.UserModel;

import java.io.File;


/**
 * @author mcxiaoke
 * @version 1.0 2012.02.23
 */
public interface AccountMethods {

    UserModel verifyCredentials() throws ApiException;

    UserModel updateProfile(String url, String location,
                                   String description, String name) throws ApiException;

    UserModel updateProfileImage(File image) throws ApiException;

//    RateLimitStatus getRateLimitStatus() throws ApiException;

//    Notifications getNotifications() throws ApiException;

}
