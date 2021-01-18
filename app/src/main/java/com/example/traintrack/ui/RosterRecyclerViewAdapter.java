package com.example.traintrack.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;


import com.example.traintrack.Classroom;
import com.example.traintrack.ClassroomActivity;
import com.example.traintrack.ClassroomRecyclerViewHolder;
import com.example.traintrack.R;
import com.example.traintrack.TeacherClassListActivity;

import java.util.ArrayList;
import java.util.List;

public class RosterRecyclerViewAdapter extends RecyclerView.Adapter<RosterRecyclerViewAdapter.RosterRecyclerViewHolder>{

    private LayoutInflater layoutInflater;
    private ArrayList<String> rosterList;


    RosterRecyclerViewAdapter(ArrayList<String> _data){
        this.rosterList = _data;
    } // take input as String ArrayList



    @NonNull
    @Override
    public RosterRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        // This view is passed to the view holder
        View view = layoutInflater.inflate(R.layout.fragment_roster_recyclerview_list,parent,false);
        return new RosterRecyclerViewHolder(view);
    }

    //Getting the info of Roster
    @Override
    public void onBindViewHolder(@NonNull RosterRecyclerViewHolder holder, int position) {

        String sName = rosterList.get(position);
        holder.sName.setText(sName);   // set StudentName into the View Holder
    }

    @Override
    public int getItemCount() {

        if(rosterList.size() == 0){
            return 0;
        }else{
            return rosterList.size();
        }

    }

    public class RosterRecyclerViewHolder extends RecyclerView.ViewHolder{
        TextView sName;
        public RosterRecyclerViewHolder(View itemView){
            super(itemView);
            sName = itemView.findViewById(R.id.studentName);
        }
    }

}
