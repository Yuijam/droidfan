/**
 *
 */
package com.arenas.droidfan.Api.rest;

import com.arenas.droidfan.Api.ApiException;
import com.arenas.droidfan.Api.Paging;
import com.arenas.droidfan.data.model.StatusModel;

import java.util.List;


/**
 * @author mcxiaoke
 * @version 1.0 2012-2-24 上午10:31:08
 */
public interface FavoritesMethods {
    List<StatusModel> getFavorites(String id, Paging paging) throws ApiException;

    StatusModel favorite(String id) throws ApiException;

    StatusModel unfavorite(String id) throws ApiException;
}
