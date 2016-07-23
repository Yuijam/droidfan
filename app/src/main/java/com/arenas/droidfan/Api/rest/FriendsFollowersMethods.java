package com.arenas.droidfan.api.rest;


import com.arenas.droidfan.api.ApiException;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.data.model.UserModel;

import java.util.List;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.22
 */
public interface FriendsFollowersMethods {

    List<String> getFriendsIDs(String id, Paging paging) throws ApiException;

    List<String> getFollowersIDs(String id, Paging paging) throws ApiException;

    List<UserModel> getFriends(String id, Paging paging) throws ApiException;

    List<UserModel> getFollowers(String id, Paging paging) throws ApiException;

}
