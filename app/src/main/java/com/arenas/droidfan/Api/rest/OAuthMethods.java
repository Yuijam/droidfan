package com.arenas.droidfan.api.rest;

import com.arenas.droidfan.api.ApiException;

import org.oauthsimple.model.OAuthToken;

import java.io.IOException;

/**
 * @author mcxiaoke
 * @version 1.1 2012.02.27
 */
public interface OAuthMethods {

    String getAccount();

    void setAccount(String account);

    OAuthToken getOAuthRequestToken() throws ApiException;

    OAuthToken getOAuthAccessToken(String username, String password)
            throws IOException, ApiException;

    void setAccessToken(OAuthToken token);

}
