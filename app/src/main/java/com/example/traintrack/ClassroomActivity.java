package com.example.traintrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.traintrack.ui.RosterFragment;
import com.example.traintrack.ui.SettingsFragment;
import com.example.traintrack.ui.StudentHomeFragment;
import com.example.traintrack.ui.TeacherHomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ClassroomActivity extends AppCompatActivity {
    TextView textClassName;
    TextView textClassCode;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    private ArrayList<String> retrieveNameList = new ArrayList<>();
    private ArrayList<String> studentNameList = new ArrayList<>();// Student arraylist for view
    private String tempName;
    private int index = 0;
    private String userType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classroom);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        FirebaseUser user = fAuth.getCurrentUser();
        DocumentReference documents = fStore.collection("Users").document(user.getUid());
        documents.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot doc = task.getResult();
                if (doc != null){
                    userType = doc.getString("UserType");
                }
            }
        });

        // Receiving the information(Classname and Classcode) of the Classroom
        Intent i = getIntent();
        String className = i.getStringExtra("Classname");
        String classCode = i.getStringExtra("Classcode");

        Bundle bundle = new Bundle();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()){
                case (R.id.nav_home):
                    // Sending Classcode to Home Fragment
                    bundle.putString("Classname", className);
                    bundle.putString("Classcode", classCode);
                    selectedFragment = userType.equals("Teacher") ? new TeacherHomeFragment() : new StudentHomeFragment();
                    selectedFragment.setArguments(bundle);
                    break;

                case (R.id.nav_roster):
                    // clear the studentNameList when the size is more than 0
                    if(studentNameList.size() >0) {
                        studentNameList.clear();
                    }

                    // Retrieving the studentlist from Firebase
                    fStore.collection("Classrooms").get().addOnCompleteListener(task1 -> {
                        for (DocumentSnapshot querySnapshot : task1.getResult()) {
                            if (classCode.equals(querySnapshot.getString("Classcode"))) { // if found the right Classroom code
                                retrieveNameList = (ArrayList) querySnapshot.get("StudentList");  // retrieve arraylist of studentlist with student ID

                                //Getting the student name by using the student ID from retrieveNameList
                                fStore.collection("Users").get().addOnCompleteListener(task2 -> {
                                    for (DocumentSnapshot querySnapshot2 : task2.getResult()) {
                                        if(task2.isSuccessful()){
                                            if(retrieveNameList.contains(querySnapshot2.getString("UserID"))){
                                                studentNameList.add(querySnapshot2.getString("FullName"));
                                            }else{
                                                //Nothing Happens, goes to next
                                            }
                                        }
                                    }

                                    // Sending Classcode to Roster Fragment
                                    Fragment rosterFragment = new RosterFragment();
                                    bundle.putStringArrayList("NamesList", studentNameList);
                                    rosterFragment.setArguments(bundle);
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.home_fragment_container, rosterFragment).commit();

                                });
                            } else {
                                // Nothing happens, goes to next classroom and check
                            }
                        }
                    });
                    break;

                case (R.id.nav_settings):
                    selectedFragment = new SettingsFragment();
                    break;
            }

            if (selectedFragment != null){
                getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment_container, selectedFragment).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment_container,userType.equals("Teacher") ? new TeacherHomeFragment() : new StudentHomeFragment()).commit();
            }

            return true;
        });

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment_container,userType.equals("Teacher") ? new TeacherHomeFragment() : new StudentHomeFragment()).commit();
        }
    }

    // logout function
    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
