package com.arenas.droidfan.Api.rest;

import com.arenas.droidfan.Api.ApiException;
import com.arenas.droidfan.data.model.UserModel;

import java.util.List;


/**
 * @author mcxiaoke
 * @version 1.0 2012.02.23
 */
public interface BlockMethods {

    List<String> blockIDs() throws ApiException;

//    List<UserModel> blockUsers(Paging paging) throws ApiException;

    UserModel isBlocked(String id) throws ApiException;

    UserModel block(String id) throws ApiException;

    UserModel unblock(String id) throws ApiException;


}
