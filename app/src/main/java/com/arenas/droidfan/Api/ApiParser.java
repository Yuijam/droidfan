/**
 *
 */
package com.arenas.droidfan.api;

import com.arenas.droidfan.data.model.DirectMessageModel;
import com.arenas.droidfan.data.model.Search;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.data.model.UserModel;

import java.util.List;


/**
 * @author mcxiaoke
 * @version 1.0 2012-2-23 上午11:54:58
 */
public interface ApiParser {

    void setAccount(String account);

    List<UserModel> users(String response, int type, String owner) throws ApiException;

    UserModel user(String response, int type, String owner) throws ApiException;

    List<StatusModel> timeline(String response, int type, String owner) throws ApiException;

    StatusModel status(String response, int type, String owner) throws ApiException;

    List<DirectMessageModel> directMessageConversation(String response,
                                                       String userId) throws ApiException;

    List<DirectMessageModel> directMessagesConversationList(String response) throws ApiException;

    List<DirectMessageModel> directMessagesInBox(String response) throws ApiException;

    List<DirectMessageModel> directMessagesOutBox(String response) throws ApiException;


    DirectMessageModel directMessage(String response, int type) throws ApiException;

    List<Search> trends(String response) throws ApiException;

    List<Search> savedSearches(String response) throws ApiException;

    Search savedSearch(String response) throws ApiException;

    List<String> strings(String response) throws ApiException;

}
