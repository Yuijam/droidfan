package com.arenas.droidfan.api;


import com.arenas.droidfan.api.rest.AccountMethods;
import com.arenas.droidfan.api.rest.FavoritesMethods;
import com.arenas.droidfan.api.rest.OAuthMethods;
import com.arenas.droidfan.api.rest.ParseMethods;
import com.arenas.droidfan.api.rest.PhotosMethods;
import com.arenas.droidfan.api.rest.StatusMethods;
import com.arenas.droidfan.api.rest.TimelineMethods;
import com.arenas.droidfan.api.rest.UsersMethods;


public interface Api extends OAuthMethods, ParseMethods, AccountMethods , TimelineMethods , StatusMethods
,PhotosMethods , FavoritesMethods , UsersMethods{

}
