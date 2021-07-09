package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class PostDetailActivity extends AppCompatActivity {

    // Create variables (hint: TextView)
    private TextView textViewUsername;
    private TextView textViewDescription;
    private TextView textViewTimeStamp;
    //private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // Find views in layout
        textViewDescription = findViewById(R.id.tvDescription);
        textViewUsername = findViewById(R.id.tvUsername);
        //image = findViewById(R.id.ivImage);

        Bundle extras = getIntent().getExtras();
        String username = extras.getString("username");
        String description = extras.getString("description");

//        byte[] byteArray = extras.getByteArray("image");
//        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        // Set data for view items
        textViewUsername.setText(username);
        textViewDescription.setText(description);

        //image.setImageBitmap(bitmap);
    }
}