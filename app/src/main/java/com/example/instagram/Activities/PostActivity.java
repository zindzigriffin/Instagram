package com.example.instagram.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.instagram.models.Post;
import com.example.instagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;
//The Post Activity screen is where the user can write a caption, post a picture, and post a picture
//The Post Activity then launches the login activity where the user can login into their instagram account
public class PostActivity extends AppCompatActivity {
    //Declare the variables
    Button logoutButton;
    public static final String TAG = "PostActivity";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private EditText meditTextDescription;
    private Button mbuttonCaptureImage;
    private ImageView mimageViewPostImage;
    private Button mbuttonSubmit;
    private File mphotoFile;
    public String mphotoFileName = "photo.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        //Initialize the variables
        logoutButton = findViewById(R.id.logoutButton);
        meditTextDescription = findViewById(R.id.editTextDescription);
        mbuttonCaptureImage = findViewById(R.id.buttonCaptureImage);
        mimageViewPostImage = findViewById(R.id.imageViewPostImage);
        mbuttonSubmit = findViewById(R.id.buttonSubmit);
        mbuttonCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });
        //when the logoutButton is clicked call the logoutbutton method
        logoutButton.setOnClickListener(v -> {onLogoutButton();});
        //Set an onClickListener for the submit button
        mbuttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Returning the text that editTextDescription is displaying as a string
                String description = meditTextDescription.getText().toString();
                //error handling to check and see if the description is empty. If it is then show the message "Description cannot be empty"
                if(description.isEmpty()){
                    Toast.makeText(PostActivity.this,"Description cannot be empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                //error handling to check and see if the image is empty. If it is then the show the message "There is no image to the viewer"
                if(mphotoFile == null || mimageViewPostImage.getDrawable() == null  ){
                    Toast.makeText(PostActivity.this,"There is no image!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //otherwise get the current user
                ParseUser currentUser = ParseUser.getCurrentUser();
                //call a method called savePost that will take in the description and currentUser
                savePost(description,currentUser, mphotoFile);
                //Call the finish method when the activity is done
                finish();
            }
        });
    }
    //Method to launch the camera
    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        mphotoFile = getPhotoFileUri(mphotoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(PostActivity.this, "com.codepath.fileprovider", mphotoFile);
        //File provider wraps the photo file
        //Serving as a place to put the photo that was taken
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }

    }
    //The Activity Result APIs decouple the result callback from the place in your code where you launch the other activity.
    //The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
    // The integer result code returned by the child activity through its setResult().
    // An Intent, which can return result data to the calle
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(mphotoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                mimageViewPostImage.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    //Unambiguously identifies a resource given which is the image we captured
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }
    //This method saves the post captured by the image and sends it to the parse backend server back4app
    private void savePost(String description, ParseUser currentUser, File mphotoFile) {
        //empty constructor to create a new post
        Post post = new Post();
        //gets the description of the post
        post.setDescription(description);
        //sets an image for the post
        post.setImage(new ParseFile(mphotoFile));
        //gets the current user
        post.setUser(currentUser);
        //saves the object to the server in the background
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG, "Error while saving",e);
                    Toast.makeText(PostActivity.this, "Error while saving",Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Post save was successful ");
                meditTextDescription.setText("");
                //Sets a drawable as the content of this ImageView.
                //This does Bitmap reading and decoding on the UI thread, which can cause a latency hiccup
                mimageViewPostImage.setImageResource(0);
            }
        });
    }

    //Method to log the user out of the app
    //If the logout fails then the user is redirected back to the LoginActivity
    private void onLogoutButton() {
        Log.i("logout failure", "onFailure");
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser();
        //once the logout button is clicked, take the user back to the sign in screen
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();

    }
    //Method to retrieve all posts from Instagram
    private void queryPosts(){
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e!=null){
                    Log.e(TAG, "Issue with getting posts",e);
                    return;
                }
                //iterate through all of the posts
                for(Post post: posts){
                    Log.i(TAG,"Post: "+post.getDescription() + ", username: "+post.getUser().getUsername());
                }
            }
        });
    }

}