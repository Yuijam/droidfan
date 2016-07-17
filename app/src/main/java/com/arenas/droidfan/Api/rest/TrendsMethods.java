/**
 *
 */
package com.arenas.droidfan.Api.rest;

import com.arenas.droidfan.Api.ApiException;
import com.arenas.droidfan.data.model.Search;
import java.util.List;

public interface TrendsMethods {

    List<Search> getTrends() throws ApiException;

}
