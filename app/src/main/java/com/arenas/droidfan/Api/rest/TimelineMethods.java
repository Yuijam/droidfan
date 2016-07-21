package com.arenas.droidfan.api.rest;

import com.arenas.droidfan.api.ApiException;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.data.model.StatusModel;

import java.util.List;


/**
 * @author mcxiaoke
 * @version 1.0 2012.02.22
 */
public interface TimelineMethods {

    List<StatusModel> getHomeTimeline(Paging paging) throws ApiException;

    List<StatusModel> getMentions(Paging paging) throws ApiException;

    List<StatusModel> getPublicTimeline() throws ApiException;

    List<StatusModel> getUserTimeline(String userId, Paging paging) throws ApiException;

    List<StatusModel> getContextTimeline(String contextId) throws ApiException;

}
