/**
 *
 */
package com.arenas.droidfan.Api.rest;


import com.arenas.droidfan.Api.ApiException;
import com.arenas.droidfan.data.model.Search;

import java.util.List;


/**
 * @author mcxiaoke
 * @version 1.0 2012-2-23 上午10:09:02
 */
public interface SavedSearchMethods {

    List<Search> getSavedSearches() throws ApiException;

    Search showSavedSearch(String id) throws ApiException;

    Search createSavedSearch(String query) throws ApiException;

    Search deleteSavedSearch(String id) throws ApiException;


}
