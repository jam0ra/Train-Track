package com.example.traintrack;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class CreateClassroomActivity<refresh> extends AppCompatActivity {
    private String classCode;
    private TextInputLayout classNameInput;
    private TextInputLayout maxCapInput;
    private String className;
    private int maxCap;
    private Button createClassroomBtn;
    private boolean ret = true;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_create_classroom);
        classNameInput = findViewById(R.id.new_classname);
        maxCapInput = findViewById(R.id.new_classsize);
        createClassroomBtn = findViewById(R.id.create_classroom);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

    }




    // Function to generate Classcode. Mixed with characters and numbers with Maximum 5 characters
    private String generateClassCode(){
        final int classCodeLength = 6;
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(classCodeLength);

        for (int i = 0; i < classCodeLength; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }

    // Function to validate the input Classroom maximum capacity.
    private boolean validateClassMaxCap(){
        String cMaxCap = maxCapInput.getEditText().getText().toString().trim();
        try{
            int cMaxCapInt = Integer.parseInt(cMaxCap);
            if (cMaxCapInt <= 0) { // There should at least 1 student in the classroom
                maxCapInput.setError("Classroom should have at least one student");
                Toast.makeText(getApplicationContext(), "Classroom Size Error", Toast.LENGTH_SHORT).show();
                return false;
            } else if (cMaxCapInt > 100) { // No more than 100 student can join in the classroom
                maxCapInput.setError("Classroom can not hold more than 100 students");
                Toast.makeText(getApplicationContext(), "Classroom Size Error", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        }
        catch (NumberFormatException e){
            maxCapInput.setError("Field cannot be empty");
        }

        return true;
    }

    // Function to validate input Classname
    private boolean validateClassname(FirebaseUser user){
        DocumentReference documents = fStore.collection("Users").document(user.getUid());
        String cName = classNameInput.getEditText().getText().toString().trim();
        if(cName.isEmpty()){  // Raise a message when the input Classname is empty
            classNameInput.setError("Can't be Empty!");
            Toast.makeText(getApplicationContext(),"Classroom Name Error",Toast.LENGTH_SHORT).show();
            ret = false;
        }
        else{
            documents.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot res = task.getResult();
                    ArrayList<String> classnames;

                    if (res.exists()) {
                        //check whether the teacher already has the classroom Name
                        if(res.get("classname") == null){
                            ret = true;
                        }
                        else{
                            // To check whether the teacher already has  5 classes, raise an error message if the teacher has 5 classnames in the db
                            classnames = (ArrayList) res.get("classname");
                            if(classnames.size() == 5){
                                classNameInput.requestFocus();
                                classNameInput.setError("Can't Add More Classrooms!");
                                Toast.makeText(getApplicationContext(),"Already Have 5 Classrooms!",Toast.LENGTH_SHORT).show();
                                ret = false;
                            }else{
                                // Check if the classname already exists in the teacher's db
                                if(classnames.contains(cName)){
                                    classNameInput.requestFocus();
                                    classNameInput.setError("This Classroom Name Already Exists");
                                    Toast.makeText(getApplicationContext(),"Classroom Already Exists",Toast.LENGTH_SHORT).show();
                                    ret = false;
                                }
                                else{
                                    ret = true;
                                }
                            }


                        }
                    }
                }
            });

        }
        return ret;

    }

    // Main Creating Classroom Function
    public void createNewClassroom(View button ) {
        FirebaseUser user = fAuth.getCurrentUser();
        DocumentReference documents = fStore.collection("Users").document(user.getUid());
        // Validation
        if (!validateClassMaxCap() || !validateClassname(user)) {
            Toast.makeText(this, "Cannot create classroom", Toast.LENGTH_SHORT).show();
            return;
        }


        className = classNameInput.getEditText().getText().toString().trim();
        maxCap = Integer.parseInt(maxCapInput.getEditText().getText().toString().trim());
        classCode = generateClassCode(); // Generating the Classcode and get a randomly generated Classcode
        Map<String, Object> classCodeInfo = new HashMap<>();
                documents.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot res = task.getResult();
                            ArrayList<String> classnames;
                            ArrayList<String> classcodes;

                            if (res.exists()) {
                                //check whether the teacher already has the classroom
                                if(res.get("classname") == null){ // if this is the first time for the teacher to create classroom
                                    classCodeInfo.put("classname", Arrays.asList(className));  //save the classname data in arraylist
                                    classCodeInfo.put("classcode",Arrays.asList(classCode));   // save the classcode data in arraylist
                                    documents.set(classCodeInfo, SetOptions.merge());   // Update the information to the teacher's db
                                }
                                else{  // if the teacher already has some classnames in the db
                                    classnames = (ArrayList) res.get("classname");  // retrieve all the classnames from the teacher's db
                                    classcodes = (ArrayList) res.get("classcode");  // retrieve all the classcodes from the teacher's db

                                    // Check if the classname already exists and cause an error message if it exists
                                    if(classnames.contains(className)){
                                        classNameInput.requestFocus();
                                        classNameInput.setError("This Classroom Name Already Exists");
                                        Toast.makeText(getApplicationContext(),"Classroom Already Exists",Toast.LENGTH_SHORT).show();
                                    }

                                    else{
                                        documents.update("classname", FieldValue.arrayUnion(className));  // If the classroom name is unique, then add the classname into the classname arraylist in teacher's db
                                        // If the randomly generated classcode exists in the teacher's db, repeatedly generate again until the unique classcode is received
                                        if(classcodes.contains(classCode) ){
                                            while(classcodes.contains(classCode)){
                                                classCode = generateClassCode();
                                            }
                                        }
                                        else{
                                            // If the generated code is unique, add the classcode into the firebase db classcode arraylist
                                            documents.update("classcode", FieldValue.arrayUnion(classCode));
                                        }

                                    }
                                }



                            }
                        }
                    }
                });


        //Saving Classroom Info to Firebase
        DocumentReference documents2 = fStore.collection("Classrooms").document();
        Map<String, Object> userInfo2 = new HashMap<>();
        documents.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userInfo2.put("Classname", className);
                        userInfo2.put("Classcode", classCode);
                        userInfo2.put("MaxCapacity",maxCap);
                        userInfo2.put("StudentList", Arrays.asList()); // Add Empty student list as no students are added yet.
                        userInfo2.put("Teacher", document.getData().get("FullName").toString());
                        userInfo2.put("TeacherEmail", document.getData().get("Email").toString());
                        documents2.set(userInfo2);
                    } else {
                        Log.d("Error", "No such document");
                    }
                } else {
                    Log.d("Error", "get failed with ", task.getException());
                }
            }
        });
        Toast.makeText(getApplicationContext(),className + " Classroom Has Been Created!",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), TeacherClassListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        CreateClassroomActivity.this.finish();
    }
}