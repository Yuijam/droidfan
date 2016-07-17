package com.arenas.droidfan.Api.rest;


import com.arenas.droidfan.Api.ApiException;
import com.arenas.droidfan.Api.Paging;
import com.arenas.droidfan.data.model.UserModel;

import java.util.BitSet;
import java.util.List;


/**
 * @author mcxiaoke
 * @version 1.0 2012.02.22
 */
public interface FriendshipsMethods {

    UserModel follow(String id) throws ApiException;

    UserModel unfollow(String id) throws ApiException;

    List<String> friendshipsRequests(Paging paging) throws ApiException;

    UserModel acceptFriendshipsRequest(String id) throws ApiException;

    UserModel denyFriendshipsRequest(String id) throws ApiException;

    boolean isFriends(String userA, String userB) throws ApiException;

    BitSet friendshipsShow(String source, String target) throws ApiException;

}
