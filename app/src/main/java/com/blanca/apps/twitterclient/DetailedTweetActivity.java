package com.blanca.apps.twitterclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.blanca.apps.twitterclient.models.Tweet;
import com.bumptech.glide.Glide;

public class DetailedTweetActivity extends AppCompatActivity {

    private Tweet tweet;
    private TextView tvUserName;
    private TextView tvBody;
    private TextView tvTimestamp;
    private ImageView ivProfileImage;
    private TextView tvRetweetCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_tweet);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tweet= getIntent().getParcelableExtra("tweet");

        tvUserName= (TextView) findViewById(R.id.tv_user_name);
        tvUserName.setText(tweet.user.name);

        tvBody= (TextView) findViewById(R.id.tv_body);
        tvBody.setText(tweet.body);

        tvTimestamp= (TextView) findViewById(R.id.tv_timestamp);
        tvTimestamp.setText(tweet.createAt);

        ivProfileImage= (ImageView) findViewById(R.id.iv_profile_image);
        Glide.with(this).load(tweet.user.profileImageUrl).into(ivProfileImage);

        tvRetweetCount= (TextView) findViewById(R.id.tv_retweet_count);
        tvRetweetCount.setText(Long.toString(tweet.retweetCount));
    }
}
