package com.codepath.apps.restclienttemplate.models;

import com.codepath.apps.restclienttemplate.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by BLANCA on 04/02/2018.
 */
@Table(database = MyDatabase.class)
public class User extends BaseModel {

    //list the attributes
    @Column
    public String name;

    @Column
    @PrimaryKey
    public long uid;
    @Column
    public String screenName;
    @Column
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
