package com.example.instagram.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.models.Post;
import com.example.instagram.Adapters.PostsAdapter;
import com.example.instagram.R;
import com.example.instagram.fragments.HomeFragment;
import com.example.instagram.fragments.PostFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
//This class allows the users to see their instagram feed 
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

    public static class PostDetailActivity extends AppCompatActivity {

        // Create variables (hint: TextView)
        private TextView tvUsername;
        private TextView tvDescription;
        TextView tvDate;
        private Post post;
        private ImageView image;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_post_detail);

            // Find views in layout
            tvDescription = findViewById(R.id.tvDescription);
            tvUsername = findViewById(R.id.tvUsername);
            image = findViewById(R.id.ivImage);
            tvDate = findViewById(R.id.tvDate);

            //Parcelable parcel[] = getIntent().getParcelableArrayExtra("Post item");
            post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getName()));

            Glide.with(this)
                    .load(post.getImage().getUrl())
                    .into(image);

            tvDescription.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());
            Date date = post.getCreatedAt();
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");
            String dateStr = format.format(date);
            tvDate.setText(dateStr);
        }
    }
}