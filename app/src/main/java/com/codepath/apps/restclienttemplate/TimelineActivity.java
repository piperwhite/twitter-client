package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    TweetAdapter tweetAdapter;
    List<Tweet> tweets = new ArrayList<>();
    RecyclerView rvTweets;
    FloatingActionButton fabComposeTweet;
    private SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;
    private User userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        client= TwitterApp.getRestClient();

        // find the RecyclerView
        rvTweets = (RecyclerView) findViewById(R.id.rv_tweet);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(Long.MAX_VALUE-1);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        //find the Floating action button
        fabComposeTweet = (FloatingActionButton) findViewById(R.id.fab_compose_tweet);
        fabComposeTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog();

            }
        });

        // construct the adapter from this datasource
        tweetAdapter = new TweetAdapter(tweets);
        // RecyclerView setup (layout manager, user adapter)
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        //set the adapter
        rvTweets.setAdapter(tweetAdapter);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                loadNextDataFromApi();
            }
        };
        rvTweets.addOnScrollListener(scrollListener);
        getUserData();
        populateTimeline(Long.MAX_VALUE-1);
    }

    public void fetchTimelineAsync(long lastID) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Remember to CLEAR OUT old items before appending in the new ones
                tweets.clear();
                tweetAdapter.notifyDataSetChanged();
                // ...the data has come back, add new items to your adapter...
                addTweetsFromJSONResponse(response);

                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }
        }, lastID);
    }


    private void getUserData() {
        client.getUserData(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    userData = User.fromJson(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
            }
        });
    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetDialogFragment composeTweetDialogFragment = ComposeTweetDialogFragment.newInstance("Compose tweet");
        composeTweetDialogFragment.setTweetButtonClickListener(new ComposeTweetDialogFragment.OnTweetButtonClickListener() {
            @Override
            public void onTweetButtonClick(final String tweetString) {
                client.postTweet(new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d("tweet success", tweetString);
                        Tweet newTweet = new Tweet();
                        newTweet.body= tweetString;
                        newTweet.user= userData;
                        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
                        Date now = new Date();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(twitterFormat);
                        newTweet.createAt= simpleDateFormat.format(now);
                        tweets.add(0, newTweet);
                        tweetAdapter.notifyItemInserted(0);
                        rvTweets.scrollToPosition(0);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                }, tweetString);
            }
        });
        composeTweetDialogFragment.show(fm, "fragment_compose_tweet");

    }

    // Append the next page of data into the adapter
    private void loadNextDataFromApi() {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyDataSetChanged()`

        populateTimeline(tweets.get(tweets.size()-1).uid);
    }

    private void populateTimeline(long lastID) {
        Log.d("populate", "id: "+ lastID);
        client.getHomeTimeline(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
               // Log.d("TwitterClient", response.toString());
                // iterate through the JSON array
                // for each entry, deserialize the JSON object

                addTweetsFromJSONResponse(response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("TwitterClient", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }
        }, lastID);
    }

    private void addTweetsFromJSONResponse(JSONArray response) {
        for(int i= 0; i < response.length(); i++){
            // convert each object to a Tweet model
            // add that Tweet model to our data source
            // notify the adapter that we've added an item
            try{
                Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                tweets.add(tweet);
                tweetAdapter.notifyItemInserted(tweets.size() - 1);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
}
