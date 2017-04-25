package com.example.vadman_pc.githubclient.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.vadman_pc.githubclient.R;


/**
 * Created by Vadman_PC on 04.04.2017.
 */
public class DetailActivity extends AppCompatActivity {
    public static final String LOGIN_KEY = "login";
    public static final String AVATAR_URL_KEY = "avatar_url";
    public static final String HTML_URL_KEY = "html_url";



    private TextView tvLink;
    private TextView tvUserName;
    private Toolbar actionBarToolbar;
    private ImageView ivUserHeader;

    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        actionBarToolbar = (Toolbar) findViewById(R.id.my_toolBar);
        setSupportActionBar(actionBarToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivUserHeader = (ImageView) findViewById(R.id.user_image_header);
        tvUserName = (TextView) findViewById(R.id.user_name);
        tvLink = (TextView) findViewById(R.id.link);

        String userName = getIntent().getExtras().getString(LOGIN_KEY);
        String avatarUrl = getIntent().getExtras().getString(AVATAR_URL_KEY);
        String link = getIntent().getExtras().getString(HTML_URL_KEY);

        tvLink.setText(link);
        tvUserName.setText(userName);

        Linkify.addLinks(tvLink, Linkify.WEB_URLS);

        Glide.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.load)
                .into(ivUserHeader);

        getSupportActionBar().setTitle("Details Activity");
    }

    private Intent createSharecastIntent() {
        String userName = getIntent().getExtras().getString(LOGIN_KEY);
        String link = getIntent().getExtras().getString(HTML_URL_KEY);
        return ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText("Check out this awesome developer @" + userName + ", " + link)
                .getIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_sh);
        menuItem.setIntent(createSharecastIntent());

        return true;
    }
}