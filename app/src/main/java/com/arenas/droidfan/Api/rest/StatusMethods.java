package com.arenas.droidfan.Api.rest;


import com.arenas.droidfan.Api.ApiException;
import com.arenas.droidfan.data.model.StatusModel;

public interface StatusMethods {

    StatusModel showStatus(String id) throws ApiException;

    StatusModel deleteStatus(String id) throws ApiException;

    StatusModel retweetStatus(String id) throws ApiException;

    StatusModel updateStatus(String status, String replyId,
                             String repostId, String location) throws ApiException;


}
