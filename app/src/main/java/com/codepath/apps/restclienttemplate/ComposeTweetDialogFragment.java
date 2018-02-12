package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by BLANCA on 11/02/2018.
 */

public class ComposeTweetDialogFragment extends DialogFragment {

    private EditText etNewTweet;
    private Button bTweet;
    private TextView tvMaxCharacters;

    private OnTweetButtonClickListener tweetButtonClickListener;
    private int count;
    private int colorCount;

    public ComposeTweetDialogFragment(){

    }

    public static ComposeTweetDialogFragment newInstance(String title) {
        ComposeTweetDialogFragment frag = new ComposeTweetDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose_tweet, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etNewTweet = (EditText) view.findViewById(R.id.et_new_tweet);
        tvMaxCharacters = (TextView)  view.findViewById(R.id.tv_max_characters);
        bTweet = (Button) view.findViewById(R.id.b_tweet);
        colorCount =tvMaxCharacters.getCurrentTextColor();

        etNewTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                count = 280- charSequence.length();
                if(count<0){
                    tvMaxCharacters.setTextColor(getResources().getColor(R.color.negative_count_color));
                    bTweet.setBackgroundColor(getResources().getColor(R.color.disable_color));
                }else{
                    tvMaxCharacters.setTextColor(colorCount);
                    bTweet.setBackgroundColor(getResources().getColor(R.color.twitter_color));
                }

                tvMaxCharacters.setText(Integer.toString(count));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        bTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count >= 0){
                    tweetButtonClickListener.onTweetButtonClick(etNewTweet.getText().toString());
                    dismiss();
                }
            }
        });


        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Compose Tweet");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        etNewTweet.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public interface OnTweetButtonClickListener{
        public void onTweetButtonClick(String tweet);
    }

    public void setTweetButtonClickListener(OnTweetButtonClickListener tweetButtonClickListener) {
        this.tweetButtonClickListener = tweetButtonClickListener;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}
