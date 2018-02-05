package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by BLANCA on 04/02/2018.
 */

public class User {

    //list the attributes
    public String name;
    public long uid;
    public String screenName;
    public String profileImageUrl;

    //deserialize the JSON
    public static User fromJson(JSONObject jsonObject) throws JSONException{
        User user= new User();

        //extract and fill the values
        user.name = jsonObject.getString("name");
        user.uid = jsonObject.getLong("id");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url");

        return user;
    }
}
