package com.example.instagram.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.instagram.models.Post;
import com.example.instagram.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostFragment extends Fragment {

    Context context;
    public static final String TAG = "PostActivity";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private EditText meditTextDescription;
    private Button mbuttonCaptureImage;
    private ImageView mimageViewPostImage;
    private Button mbuttonSubmit;
    //private Button mbuttonFeed;
    private File mphotoFile;
    public String mphotoFileName = "photo.jpg";

    public PostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostFragment newInstance(Context context) {
        PostFragment fragment = new PostFragment();
        fragment.context = context;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        meditTextDescription = view.findViewById(R.id.editTextDescription);
        mbuttonCaptureImage = view.findViewById(R.id.buttonCaptureImage);
        mimageViewPostImage = view.findViewById(R.id.imageViewPostImage);
        mbuttonSubmit = view.findViewById(R.id.buttonSubmit);
        //mbuttonFeed = findViewById(R.id.mbuttonFeed);
        //set an onclick listener to the button capture image
        mbuttonCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        mbuttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Returning the text that editTextDescription is displaying as a string
                String description = meditTextDescription.getText().toString();
                //error handling to check and see if the description is empty. If it is then show the message "Description cannot be empty"
                if (description.isEmpty()) {
                    Toast.makeText(context, "Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mphotoFile == null || mimageViewPostImage.getDrawable() == null) {
                    Toast.makeText(context, "There is no image!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //otherwise get the current user
                ParseUser currentUser = ParseUser.getCurrentUser();
                //call a method called savePost that will take in the description and currentUser
                savePost(description, currentUser, mphotoFile);
            }
        });
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        mphotoFile = getPhotoFileUri(mphotoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(context, "com.codepath.fileprovider", mphotoFile);
        //File provider wraps the photo file
        //Serving as a place to put the photo that was taken
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }

    }
    //The Activity Result APIs decouple the result callback from the place in your code where you launch the other activity.
    //The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
    // The integer result code returned by the child activity through its setResult().
    // An Intent, which can return result data to the calle
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(mphotoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                mimageViewPostImage.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(context, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    //Unambigiously identifies a resource given which is the image we captured
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

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
                    Toast.makeText(context, "Error while saving",Toast.LENGTH_SHORT).show();
                }
                meditTextDescription.setText("");
                //Sets a drawable as the content of this ImageView.
                //This does Bitmap reading and decoding on the UI thread, which can cause a latency hiccup
                mimageViewPostImage.setImageResource(0);
            }
        });
    }
}