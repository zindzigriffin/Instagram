package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PostDetailActivity extends AppCompatActivity {

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