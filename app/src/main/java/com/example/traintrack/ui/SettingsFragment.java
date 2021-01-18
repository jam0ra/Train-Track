package com.example.traintrack.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.traintrack.MainActivity;
import com.example.traintrack.R;
import com.example.traintrack.SettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

 public class SettingsFragment extends Fragment{
    private Button deleteBtn;  //delete account button
    private DocumentReference documents;
    private ArrayList classNamesList;
    private ArrayList studentNamesList;
    private String studentId;

    public SettingsFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        // Calling the deleteAccount onCLick Function from SettingsActivity
        FirebaseAuth fAuth = FirebaseAuth.getInstance();  // instantiate the User
        FirebaseFirestore fStore = FirebaseFirestore.getInstance(); // instantiate the Cloud Storage
        FirebaseUser fUser = fAuth.getCurrentUser();
        deleteBtn = view.findViewById(R.id.delete_account);
        deleteBtn.setOnClickListener(v -> {
            // When Delete account button is clicked
            AlertDialog.Builder dialog = new AlertDialog.Builder(requireActivity());
            dialog.setTitle("Are you sure you want to delete your account?"); // Alert dialog
            dialog.setMessage("Deleting the account will permanently delete all the information related to the account!");
            dialog.setPositiveButton("Delete", (dialog12, which) -> {
                assert fUser != null;
                studentId = fUser.getUid();  // get the currentUser id
                // Now delete the cloud storage data first
                documents = fStore.collection("Users").document(studentId); // get db of current user
                documents.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot res = task.getResult();
                        assert res != null;
                        if (res.exists()) {
                            if (Objects.equals(res.get("UserType"), "Teacher")) {

                                classNamesList = (ArrayList) res.get("classname");  // saving the classnames of the teacher
                                //Delete those classrooms db
                                fStore.collection("Classrooms").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        //Iterate through the classroom db to find the selected classroom db
                                        for (DocumentSnapshot querySnapshot : Objects.requireNonNull(task.getResult())) {
                                            if (classNamesList.contains(querySnapshot.getString("Classname"))) {

                                                String classroomKey = querySnapshot.getId();
                                                fStore.collection("Classrooms").document(classroomKey).delete(); // Delete the classroom db
                                            }

                                        }
                                    }
                                }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Problem", Toast.LENGTH_SHORT).show());
                            } else { //If current user is a student
                                //Delete the student from that classroom
                                fStore.collection("Classrooms").get().addOnCompleteListener(task1 -> {
                                    //Iterate through the classroom db to find the selected classroom db
                                    for (DocumentSnapshot querySnapshot : Objects.requireNonNull(task1.getResult())) {
                                        studentNamesList = (ArrayList) querySnapshot.get("StudentList");  // saving the classnames of the teacher
                                        assert studentNamesList != null;
                                        if (studentNamesList.contains(studentId)) { //if the classroom db contains that student's id
                                            fStore.collection("Classrooms").document(querySnapshot.getId()).update("StudentList", FieldValue.arrayRemove(studentId));// Delete the student from the studentlist
                                        } else {
                                            //Nothing
                                        }
                                    }
                                }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Problem", Toast.LENGTH_SHORT).show());

                            }
                        }
                    }
                });
                //Delete the fAuth and move to the main activity
                fUser.delete().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) { // the email and password is deleted fro fAuth
                        //Delete data from cloud storage
                        documents.delete(); // the current user db is deleted
                        Toast.makeText(getActivity(), "Account Deleted!", Toast.LENGTH_SHORT).show();
                        //logout
                        startActivity(new Intent(getActivity(), MainActivity.class));
                }else{
                    Toast.makeText(getActivity(), Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                }
                });
            });
            dialog.setNegativeButton("Cancel", (dialog1, which) -> dialog1.dismiss());
            AlertDialog alertDialog = dialog.create();
            alertDialog.show();
        });  // end of deleteBtn.setOnClickListener(v -> {

        return view;
    }
}
