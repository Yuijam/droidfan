package com.arenas.droidfan.Api;


import com.arenas.droidfan.Api.rest.AccountMethods;
import com.arenas.droidfan.Api.rest.BlockMethods;
import com.arenas.droidfan.Api.rest.DirectMessagesMethods;
import com.arenas.droidfan.Api.rest.FavoritesMethods;
import com.arenas.droidfan.Api.rest.FriendsFollowersMethods;
import com.arenas.droidfan.Api.rest.FriendshipsMethods;
import com.arenas.droidfan.Api.rest.OAuthMethods;
import com.arenas.droidfan.Api.rest.ParseMethods;
import com.arenas.droidfan.Api.rest.PhotosMethods;
import com.arenas.droidfan.Api.rest.SavedSearchMethods;
import com.arenas.droidfan.Api.rest.SearchMethods;
import com.arenas.droidfan.Api.rest.StatusMethods;
import com.arenas.droidfan.Api.rest.TimelineMethods;
import com.arenas.droidfan.Api.rest.TrendsMethods;
import com.arenas.droidfan.Api.rest.UsersMethods;


public interface Api extends OAuthMethods, ParseMethods, AccountMethods , TimelineMethods , StatusMethods
,PhotosMethods , FavoritesMethods{

}
