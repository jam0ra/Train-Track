package com.example.traintrack;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page);
    }
    
    public void login(View view) {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    public void signup(View view) {
        startActivity(new Intent(MainActivity.this, SignUpActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
            DocumentReference documents = fStore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            documents.get().addOnSuccessListener(documentSnapshot -> {
                if (Objects.equals(documentSnapshot.getString("UserType"), "Teacher")){
                    Log.d("test", "main");
                    startActivity(new Intent(getApplicationContext(), TeacherClassListActivity.class));
                } else {
                    startActivity(new Intent(getApplicationContext(), StudentValidateClasscodeActivity.class));
                }
            });
        }
    }
}
