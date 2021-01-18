package com.example.traintrack;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traintrack.ui.RosterFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TeacherClassListActivity extends AppCompatActivity {
    private FloatingActionButton createClassroomBtn;
    private ClassroomAdapter adapter2;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private FirebaseAuth fAuth2;
    private RecyclerView mRecyclerView;
    private ArrayList<Classroom> cArrayList;
    private ClassroomAdapter adap;
    private ImageButton refreshBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_classroomlist);
        fAuth = FirebaseAuth.getInstance();
        cArrayList = new ArrayList<>();
        setmRecyclerView();
        setFirebase();
        LoadDataFromFirebase();
        //Create Classroom Button
        createClassroomBtn = findViewById(R.id.add_classroomBtn);
        createClassroomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeacherClassListActivity.this, CreateClassroomActivity.class));
            }
        });


    }

    // Retrieving the data of Classrooms from Firebase
    private void LoadDataFromFirebase(){
        // clear the cArraylist when the size is more than 0
        if(cArrayList.size() >0){
            cArrayList.clear();
        }
        // Connect to the User Firebase, in order to check which classcode that the teacher has
        FirebaseUser user = fAuth.getCurrentUser();
        DocumentReference documents = fStore.collection("Users").document(user.getUid());
        documents.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot res = task.getResult();
                ArrayList<String> cNamesList = (ArrayList) res.get("classname");
                if (res.exists()) {
                    //check whether the teacher already has the classroom Name
                    if (res.get("classname") != null) {


                        // Connected to the firebase Classrooms Collection
                        fStore.collection("Classrooms").get().addOnCompleteListener(task1 -> {
                            for (DocumentSnapshot querySnapshot : task1.getResult()) {
                                if (cNamesList.contains(querySnapshot.getString("Classname"))) { //  // Compare each of the classnames in the arraylist to see which class does this teacher belongs to
                                    // Initialize the Classroom class with the classname,classcode and maxCapacity of classrooms
                                    Classroom c = new Classroom(querySnapshot.getString("Classname"), querySnapshot.getString("Classcode"), querySnapshot.getLong("MaxCapacity"));
                                    cArrayList.add(c); // add this classroom object to the cArrayList
                                } else {
                                    // Nothing happens
                                }

                            }
                            adap = new ClassroomAdapter(TeacherClassListActivity.this, cArrayList); // adapter
                            mRecyclerView.setAdapter(adap);   //Set the recycler view with the values in cArrayList

                            // for removing the classrooms when needed
                            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);

                        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Problem", Toast.LENGTH_SHORT).show());


                    }
                }
            }
        });






    }

    // Instantiating our Firebase
    private void setFirebase(){
        fStore = FirebaseFirestore.getInstance();

    }

    // Setting the Recycler View of the Classrooms
    private void setmRecyclerView(){
        mRecyclerView = findViewById(R.id.classroom_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }




    // When Removing the Classroom
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0,  ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        //Delete Classroom by swipe right
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            FirebaseAuth fAuth;

            String targetClassname = cArrayList.get(position).getClassname();
            String targetClasscode = cArrayList.get(position).getClasscode();

            DocumentReference documentClassroom = fStore.collection("Classrooms").document();

            fStore.collection("Classrooms").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    //Iterate through the classroom db to find the selected classroom db
                    for (DocumentSnapshot querySnapshot : task.getResult()) {
                        if (targetClassname.equals(querySnapshot.getString("Classname"))) {
                            String classroomKey = querySnapshot.getId();
                            fStore.collection("Classrooms").document(classroomKey).delete(); //deleting the classroom firebase db

                            //Deleting the classroom info in both teacher and student firebase db
                            fStore.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    //Iterate through the Users db to find the selected classroom db
                                    for (DocumentSnapshot querySnapshot2 : task.getResult()) {
                                        fAuth2 = FirebaseAuth.getInstance();
                                        FirebaseUser user2 = fAuth2.getCurrentUser();   //get current User
                                        if(querySnapshot2.get("classname")!= null){ // Dont look at the students without classname
                                            if((querySnapshot2.get("UserType").equals("Student"))&& (querySnapshot2.get("classname").equals(targetClassname))){  // If the corresponding user is a student
                                                System.out.println("AA"+querySnapshot2.get("classname") + "BBBBBBBBBBBBBBBBBBBBBB");

                                                Map<String,Object> userInfo = new HashMap<>();
                                                userInfo.put("classcode",FieldValue.delete());   // delete the classcode string from student, it will only contain ""
                                                userInfo.put("classname",FieldValue.delete());   // delete the classname string from student, it will only contain ""
                                                fStore.collection("Users").document(querySnapshot2.getId()).set(userInfo, SetOptions.merge());   // Update the information to the db

                                            }else if((querySnapshot2.get("UserType").equals("Teacher"))){
                                                ArrayList<String> classCodeList = (ArrayList) querySnapshot2.get("classcode"); //retrieving all classcodes
                                                ArrayList<String> classNameList = (ArrayList) querySnapshot2.get("classname"); //retrieving all classnames
                                                if(classCodeList.contains(targetClasscode)){

                                                   fStore.collection("Users").document(user2.getUid()).update("classcode", FieldValue.arrayRemove(targetClasscode));
                                                }
                                                if(classNameList.contains(targetClassname)){

                                                   fStore.collection("Users").document(user2.getUid()).update("classname", FieldValue.arrayRemove(targetClassname));
                                                }
                                            }
                                        }



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
                    Toast.makeText(TeacherClassListActivity.this, "Classroom Has Been Deleted!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Problem", Toast.LENGTH_SHORT).show();
                }
            });


//            cArrayList.remove(viewHolder.getAdapterPosition());
//            adap.notifyDataSetChanged();

        }
    };


    //Refresh button function
    public void refreshPage(View view){
        finish();
        startActivity(getIntent());

    }


}