/**
 *
 */
package com.arenas.droidfan.api.rest;

import com.arenas.droidfan.api.ApiException;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.data.model.StatusModel;

import java.io.File;
import java.util.List;


/**
 * @author mcxiaoke
 * @version 1.0 2012-2-23 上午10:13:38
 */
public interface PhotosMethods {

    List<StatusModel> getPhotosTimeline(String id, Paging paging) throws ApiException;

    StatusModel uploadPhoto(File photo, String status, String location) throws ApiException;

}
