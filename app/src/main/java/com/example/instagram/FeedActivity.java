package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.instagram.fragments.HomeFragment;
import com.example.instagram.fragments.PostFragment;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {
    public static final String TAG = "FeedActivity";
    protected PostsAdapter adapter;
    protected List<Post> allPosts;
    private RecyclerView recyclerViewPosts;
    private SwipeRefreshLayout swipeContainer;
    Button btnLogout;

    FragmentManager fragmentManager;
    Fragment fragment;
    BottomNavigationView bottomNavigationMenu;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        btnLogout = findViewById(R.id.btnLogout);
        //Lookup the swipe container view
//        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
//        // Setup refresh listener which triggers new data loading
//        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                // Your code to refresh the list here.
//                // Make sure you call swipeContainer.setRefreshing(false)
//                // once the network request has completed successfully.
//                //fetchTimelineAsync(0);
//                queryPosts();
//            }
//        });
//        Button btnToPostAct = findViewById(R.id.btnAddPost);
//        btnToPostAct.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent toPost = new Intent(FeedActivity.this, PostActivity.class);
//                startActivity(toPost);
//            }
//        });
//        // Configure the refreshing colors
//        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);
//
//        recyclerViewPosts = findViewById(R.id.recyclerViewPosts);
//
//        // initialize the array that will hold posts and create a PostsAdapter
//        allPosts = new ArrayList<>();
//        adapter = new PostsAdapter(this, allPosts);
//        // set the adapter on the recycler view
//        recyclerViewPosts.setAdapter(adapter);
//        // set the layout manager on the recycler view
//        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
//        // query posts from Parstagram
//        queryPosts();
            context = this;
            fragmentManager = getSupportFragmentManager();
            fragment = HomeFragment.newInstance(context);
            bottomNavigationMenu = findViewById(R.id.bottom_navigation);
            bottomNavigationMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.action_home:
                            fragment = HomeFragment.newInstance(context);
                            break;
                        case R.id.action_compose:
                            fragment = PostFragment.newInstance(context);
                            break;
//                        case R.id.action_profile:
//                            break;
                        default:
                            break;
                    }
                    fragmentManager.beginTransaction().replace(R.id.flContainer,fragment).commit();
                    return true;
                }
            });
        fragmentManager.beginTransaction().replace(R.id.flContainer,fragment).commit();
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null){
                            Intent intent = new Intent(FeedActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }


    /*public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        client.getHomeTimeline(new JsonHttpResponseHandler() {
                                   public void onSuccess(JSONArray json) {
                                       // Remember to CLEAR OUT old items before appending in the new ones
                                       adapter.clear();
                                       // ...the data has come back, add new items to your adapter...
                                       //adapter.addAll(Post);
                                       // Now we call setRefreshing(false) to signal refresh has finished
                                       swipeContainer.setRefreshing(false);
                                   }
        });
    }*/

    //In the snippet above we form request and set options such as order, max number of posts and additional data to include.
    public void queryPosts() {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_USER);
        // limit query to latest 20 items
        query.setLimit(20);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                // for debugging purposes let's print every post description to logcat
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }
                adapter.clear();
                adapter.addAll(posts);
                // save received posts to list and notify adapter of new data
                //allPosts.addAll(posts);
                //adapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }
        });
    }
}