package com.example.traintrack;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class StudentValidateClasscodeActivity extends AppCompatActivity{

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    private TextInputLayout inputCode;
    private String code;
    private Button enterCodeBtn;
    private boolean cCodeValidation = false; // turn true when the entered classcode exists in the firebase db
    private boolean ret;   // true when the student already has the classcode when just logged in
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_validate_classcode);

        inputCode = findViewById(R.id.validate_code);

        enterCodeBtn = (Button) findViewById(R.id.code_validateBtn);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
    }


    // if the student already has the classcode into his/her firebase db, automatically connects to the classroom
    @Override
    protected void onStart(){
        super.onStart();

        FirebaseUser user = fAuth.getCurrentUser();
        DocumentReference documents = fStore.collection("Users").document(user.getUid());
        documents.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot res = task.getResult();
                    if(res.exists()){
                        if((res.get("classcode") == null) || res.get("classcode") == ""){
                            ret = false;
                        }else{
                            //Sending the information of the classroom(classname,classcode) to the Classroom activity
                            Intent intent = new Intent(getApplicationContext(), ClassroomActivity.class);
                            intent.putExtra("Classname",res.get("classname").toString());
                            intent.putExtra("Classcode",res.get("classcode").toString());
                            startActivity(intent); // move to the classroom acitivity with that classroom's info
                            ret = true;
                        }
                    }
                }
            }
        });

    }
    // Button Click to validate the classcode(where the input classcode exists in the db)
    public void validateCode(View v){
        code = inputCode.getEditText().getText().toString().trim();
        FirebaseUser user = fAuth.getCurrentUser();
        DocumentReference documents = fStore.collection("Users").document(user.getUid());
        Map<String, Object> studentClasscodeInfo = new HashMap<>();
        fStore.collection("Classrooms").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //Iterate over each of the classroom information in collection to check whether the classcode input exists in the db
                for (DocumentSnapshot querySnapshot : task.getResult()) {
                    String temClassCode = querySnapshot.getString("Classcode");
                    String ClassName = querySnapshot.getString("Classname");
                    if (temClassCode.equals(code)) {  // if the input classcode exists in the firebase db
                        cCodeValidation = true;
                        //Adding the student in into classroom firebase db
                        DocumentReference documentsClassroom = fStore.collection("Classrooms").document(querySnapshot.getId());
                        documentsClassroom.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentClassroom = task.getResult();
                                    if (documentClassroom.exists()) {
                                        documentsClassroom.update("StudentList", FieldValue.arrayUnion(user.getUid())); // adding the student user id to classrooms studentlist
                                    } else {
                                        Log.d("Error", "No such document");  // Cause an error message when that classroom doesnt exists
                                    }
                                } else {
                                    Log.d("Error", "get failed with ", task.getException());
                                }
                            }
                        });
                        // Adding the classname and classcode info to student firebase db
                        documents.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot res = task.getResult();
                                    if (res.exists()) {
                                        studentClasscodeInfo.put("classname", ClassName);
                                        studentClasscodeInfo.put("classcode",code);
                                        documents.set(studentClasscodeInfo, SetOptions.merge());  //Updating the student db info by merging the data
                                    }
                                }
                            }

                        });
                        //passing the classroom information to the next activity
                        Intent intent = new Intent(getApplicationContext(),ClassroomActivity.class);  // intent for classroom activity
                        intent.putExtra("Classname",ClassName);
                        intent.putExtra("Classcode",code);
                        startActivity(intent); // move to the classroom acitivity with that classroom's info
                    }
                }

                // When the Classcode is not in the firebase db
                if (cCodeValidation == false) {
                    inputCode.setError("This Classroomcode Doesn't Exists. Try again!");  // cause an error message
                    Toast.makeText(getApplicationContext(), "Invalid Classroomcode!", Toast.LENGTH_SHORT).show();
                }
            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Problem", Toast.LENGTH_SHORT).show();
            }
        });



    }




}