package com.example.instagram;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }
    //Whenever RecyclerView has to show an item to a user it will call onBindViewHolder with this item’s position and ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = mPosts.get(position);
        holder.bind(post);
    }
    //Passing the context and feed data into the viewHolder
    private Context mContext;
    private List<Post> mPosts;

    public PostsAdapter(Context context, List<Post> posts) {
        mContext = context;
        mPosts= posts;
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public void clear(){
        mPosts.clear();
        notifyDataSetChanged();
    }
    public void addAll(List<Post> list){
        mPosts.addAll(list);
        notifyDataSetChanged();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewUsername;
        private ImageView imageViewImage;
        private TextView textViewDescription;
        private TextView textViewTimeStamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
            imageViewImage = itemView.findViewById(R.id.imageViewImage);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewTimeStamp = itemView.findViewById(R.id.textViewTimeStamp);
        }

        public void bind(Post mPosts) {
            Log.d("PostAdapter:", "bind()" );
            // Bind the post data to the view elements
            textViewDescription.setText(mPosts.getDescription());
            textViewUsername.setText(mPosts.getUser().getUsername());
            Log.i("PostsAdapter", String.valueOf(mPosts.getCreatedAt()));
            //Log.i("PostsAdapter", String.valueOf(mPosts));
            //Sets the text for the time stamp that which they got the relative time ago method on the adapter inside the bind method
            textViewTimeStamp.setText(mPosts.getRelativeTimeAgo(mPosts.getCreatedAt().toString()));
            ParseFile image = mPosts.getImage();
            if (image != null) {
                Glide.with(mContext).load(image.getUrl()).into(imageViewImage);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Convert image file to Bitmap
                    // Create Bitmap variable
                    //Bitmap bitmap = BitmapFactory.decodeFile(image.getUrl());
                    // Create ByteArrayOutputStream and initialize
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);
//                    byte[] byteArray = baos.toByteArray();

//                    Intent intent = new Intent(mContext, PostDetailActivity.class);
//                    intent.putExtra("username", textViewUsername.getText());
//                    intent.putExtra("description", textViewDescription.getText());
                    // intent.putExtra("image", byteArray);
                    Intent intent = new Intent(mContext, PostDetailActivity.class);
                    intent.putExtra(Post.class.getName(), Parcels.wrap(mPosts));
//                    Bundle bundle = new Bundle();
//                    bundle.putParcelable("Post item", Parcels.wrap(mPosts));
//                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}