package com.example.traintrack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ShowVideoSubmission extends AppCompatActivity {

    //Class variables!

    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showvideo_submission);

        recyclerView = findViewById(R.id.recyclerview_ShowVideo_s);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("videosubmission");         //referring to child "videosubmission" in realtime database!

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

    //this below class is used to search video in our recycler View!

    private void firebaseSearch(String searchtext) {

        String query = searchtext.toLowerCase();
        Query firebaseQuery = databaseReference.orderByChild("search").startAt(query).endAt(query + "\uf8ff");

        FirebaseRecyclerOptions<Member> options =
                new FirebaseRecyclerOptions.Builder<Member>()
                        .setQuery(firebaseQuery, Member.class)
                        .build();

        FirebaseRecyclerAdapter<Member, SubmissionViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Member, SubmissionViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull SubmissionViewHolder holder, int position, @NonNull Member model) {

                        holder.setExoplayer(getApplication(), model.getName(), model.getVideourl());
                    }

                    @NonNull
                    @Override
                    public SubmissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        //this will set our layout to item.xml!
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item, parent, false);

                        return new SubmissionViewHolder(view);
                    }
                };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();

        //firebase recycler options
        FirebaseRecyclerOptions<Member> options =
                new FirebaseRecyclerOptions.Builder<Member>()
                        .setQuery(databaseReference, Member.class)
                        .build();

        FirebaseRecyclerAdapter<Member, SubmissionViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Member, SubmissionViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull SubmissionViewHolder holder, int position, @NonNull Member model) {

                        holder.setExoplayer(getApplication(), model.getName(), model.getVideourl());
                    }

                    @NonNull
                    @Override
                    public SubmissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        //this will set our layout to item.xml!
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item, parent, false);

                        return new SubmissionViewHolder(view);
                    }
                };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem item = menu.findItem(R.id.search_firebase);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}