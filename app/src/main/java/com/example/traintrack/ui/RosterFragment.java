package com.example.traintrack.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traintrack.Classroom;
import com.example.traintrack.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RosterFragment extends Fragment {
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser user = fAuth.getCurrentUser();
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    private RecyclerView rosterRecyclerView;
    private RecyclerView.Adapter rosterAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private View listItemsViews;
    private ArrayList<String> studentNameList = new ArrayList<>();



    public static RosterFragment newInstance() {
        return new RosterFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_roster, container, false);

        // Receiving the studentNamesList from ClassroomActivity
        studentNameList = this.getArguments().getStringArrayList("NamesList");
        // This Inflate the framgent_roster to the view group
        listItemsViews = inflater.inflate(R.layout.fragment_roster,container,false);
        rosterRecyclerView = listItemsViews.findViewById(R.id.classroom_recycler_view);

        rosterRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        rosterRecyclerView.setLayoutManager(layoutManager);

        //loads list items to the recycler view
        rosterAdapter = new RosterRecyclerViewAdapter(studentNameList);
        rosterRecyclerView.setAdapter(rosterAdapter);
        return listItemsViews;
    }
}
