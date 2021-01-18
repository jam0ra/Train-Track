package com.example.traintrack;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class DemoVideo extends AppCompatActivity {

    private static final int PICK_VIDEO = 1;
    VideoView videoView;
    Button button;
    ProgressBar progressBar;
    EditText editText;
    private Uri videoUri;
    MediaController mediaController;        //for playing and pause our video!
    StorageReference storageReference;      //firebase storage
    DatabaseReference databaseReference;     //realtime database
    Member member;
    UploadTask uploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_video);

        member = new Member();

        storageReference = FirebaseStorage.getInstance().getReference("Video");         //This will create a folder name "Video" in firebase storage, where the videos will be saved!
        databaseReference = FirebaseDatabase.getInstance().getReference("video");          //This will create a folder named "video" in our realtime database, where videos will be saved!

        videoView = findViewById(R.id.videoview_main);
        button = findViewById(R.id.button_upload_main);
        progressBar = findViewById(R.id.progressBar_main);
        editText = findViewById(R.id.et_video_name);
        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.start();

        button.setOnClickListener(new View.OnClickListener() {

            //This is for our Upload button, so when we click it this will take us to UploadVideo process!

            @Override
            public void onClick(View v) {
                UploadVideo();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if(bundle !=null)
        {
            if(bundle.getString("some")!= null){
                Toast.makeText(getApplicationContext(),
                        "data:" + bundle.getString("some"),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_VIDEO || resultCode == RESULT_OK ||
                data != null || data.getData() !=null){

            //if we select a video and its is non null, it will show as video view!

            videoUri = data.getData();

            videoView.setVideoURI(videoUri);
        }
    }

    public void ChooseVideo(View view) {

        //when we click on choose video text, it will take us to the video choosing process!

        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO);
    }

    private String getExt(Uri uri){

        //This will check the extension of our file

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void ShowVideo(View view) {

        //When we click on ShowVideo text, it will take us to ShowVideo class!

        Intent intent = new Intent(DemoVideo.this, ShowVideo.class);
        startActivity(intent);
    }

    private void UploadVideo() {

        String videoName = editText.getText().toString();
        String search = editText.getText().toString().toLowerCase();            //our video will be saved as lower case for search purpose only!

        if(videoUri != null || !TextUtils.isEmpty(videoName)){

            progressBar.setVisibility(View.VISIBLE);                            //here our progress bar is visible when uploading!
            final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getExt(videoUri));
            uploadTask = reference.putFile(videoUri);


            //This below class is useful to retrieve our video Uri!

            Task<Uri> urltask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()) {
                        //if the data is being saved, after its uploaded on firebase, the progress bar becomes invisible and toast message will show that "data saved"

                        Uri downloadUrl = task.getResult();
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(DemoVideo.this, "Data saved", Toast.LENGTH_SHORT).show();

                        //The below process is to save data onto our realtime database

                        member.setName(videoName);
                        member.setVideourl(downloadUrl.toString());
                        member.setSearch(search);
                        String i = databaseReference.push().getKey();
                        databaseReference.child(i).setValue(member);
                    } else{

                        //if our task was not successful we will raise a toast that it "Failed"
                        Toast.makeText(DemoVideo.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            //If our video name is empty it will raise a toast!
            Toast.makeText(this, "All Fields are required", Toast.LENGTH_SHORT).show();
        }
    }
}