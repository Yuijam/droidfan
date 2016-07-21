/**
 *
 */
package com.arenas.droidfan.api.rest;

import com.arenas.droidfan.api.ApiException;
import com.arenas.droidfan.data.model.Search;
import java.util.List;

public interface TrendsMethods {

    List<Search> getTrends() throws ApiException;

}
