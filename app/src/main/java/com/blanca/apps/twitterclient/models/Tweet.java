package com.blanca.apps.twitterclient.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.blanca.apps.twitterclient.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by BLANCA on 04/02/2018.
 */
@Table(database = MyDatabase.class)
public class Tweet extends BaseModel implements Parcelable {

    //List out the attributes
    @Column
    public String body;

    @Column
    @PrimaryKey
    public long uid;// database ID for the tweet

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    public User user;

    @Column
    public String createAt;

    @Column
    public long retweetCount;

    @Column
    public long favouritesCount;

    //deserialize the JSON
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException{
        Tweet tweet = new Tweet();

        //extract the values from JSON
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.retweetCount= jsonObject.getLong("retweet_count");
      //  tweet.favouritesCount= user.;
        return tweet;
    }


    public Tweet() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.body);
        dest.writeLong(this.uid);
        dest.writeParcelable(this.user, flags);
        dest.writeString(this.createAt);
        dest.writeLong(this.retweetCount);
        dest.writeLong(this.favouritesCount);
    }

    protected Tweet(Parcel in) {
        this.body = in.readString();
        this.uid = in.readLong();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.createAt = in.readString();
        this.retweetCount = in.readLong();
        this.favouritesCount = in.readLong();
    }

    public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
}
