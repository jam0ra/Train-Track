package com.example.traintrack;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class StudentAssignmentActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference notebookRef = db.collection("Notebook");

    private StudentNoteAdapter adapter_student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_student_assignments_list);

        setUpRecyclerView();

        Bundle bundle = getIntent().getExtras();
        if(bundle !=null)
        {
            if(bundle.getString("some")!= null){
                Toast.makeText(getApplicationContext(),
                        "data" + bundle.getString("some"),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setUpRecyclerView() {
        Query query = notebookRef.orderBy("priority", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<StudentNote> options = new FirestoreRecyclerOptions.Builder<StudentNote>()
                .setQuery(query, StudentNote.class)
                .build();

        adapter_student = new StudentNoteAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_s);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter_student);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter_student.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter_student.stopListening();
    }
}