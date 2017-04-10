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
public class DetailActivity extends AppCompatActivity{
    TextView tvLink;
    TextView tvUserName;
    Toolbar actionBarToolbar;
    ImageView ivImageView;


    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        actionBarToolbar = (Toolbar) findViewById(R.id.my_toolBar);
        setSupportActionBar(actionBarToolbar);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivImageView = (ImageView) findViewById(R.id.user_image_header);
        tvUserName = (TextView) findViewById(R.id.user_name);

        tvLink = (TextView) findViewById(R.id.link);

        String userName = getIntent().getExtras().getString("login");
        String avatarUrl = getIntent().getExtras().getString("avatar_url");
        String link = getIntent().getExtras().getString("html_url");

        tvLink.setText(link);
        Linkify.addLinks(tvLink, Linkify.WEB_URLS);

        tvUserName.setText(userName);

        Glide.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.load)
                .into(ivImageView);

        getSupportActionBar().setTitle("Details Activity");
    }

    private Intent createSharecastIntent() {
        String userName = getIntent().getExtras().getString("login");
        String link = getIntent().getExtras().getString("html_url");
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText("Check out this awesome developer @" + userName + ", " + link)
                .getIntent();
        return shareIntent;
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