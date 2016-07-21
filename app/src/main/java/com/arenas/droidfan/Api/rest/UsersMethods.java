package com.arenas.droidfan.api.rest;


import com.arenas.droidfan.api.ApiException;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.data.model.UserModel;

import java.util.List;


/**
 * @author mcxiaoke
 * @version 1.0 2012.02.23
 */
public interface UsersMethods {

    List<UserModel> getFriends(String id, Paging paging) throws ApiException;

    List<UserModel> getFollowers(String id, Paging paging) throws ApiException;

    List<UserModel> getUserRecommendation(Paging paging) throws ApiException;

    UserModel ignoreUserRecommendation(String id) throws ApiException;

    List<UserModel> getUsersByTag(String tag, Paging paging) throws ApiException;

    List<String> getUserTags(String id) throws ApiException;

    UserModel showUser(String id) throws ApiException;

}
