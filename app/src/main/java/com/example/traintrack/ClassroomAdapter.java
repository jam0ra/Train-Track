package com.example.traintrack;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ClassroomAdapter extends RecyclerView.Adapter<ClassroomRecyclerViewHolder> {
    TeacherClassListActivity teacherClassListActivity;
    ArrayList<Classroom> cArrayList;
    private Context mContext;

    //Constructor
    public ClassroomAdapter(TeacherClassListActivity teacherClassListActivity, ArrayList<Classroom> cArrayList){
        this.teacherClassListActivity = teacherClassListActivity;
        this.cArrayList = cArrayList;
    }

    @NonNull
    @Override
    public ClassroomRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(teacherClassListActivity.getBaseContext());
        View view = layoutInflater.inflate(R.layout.classroom_recyclerview_list,parent,false);
        mContext = parent.getContext();
        return new ClassroomRecyclerViewHolder(view);
    }

    //Getting the info of Classroom and Going into different activities by click
    @Override
    public void onBindViewHolder(@NonNull ClassroomRecyclerViewHolder holder, int position) {
        // Setting the textview of the recycler view with classroom name, classroom code and classroom maximum capacacity
        holder.cName.setText(cArrayList.get(position).getClassname());
        holder.cCode.setText(cArrayList.get(position).getClasscode());
        holder.cMaxCap.setText(String.valueOf(cArrayList.get(position).getMaxCapacity()));
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Sending the information of Classroom and Classcode to the classroom activity
                Intent intent = new Intent(mContext,ClassroomActivity.class);
                intent.putExtra("Classname",cArrayList.get(position).getClassname());
                intent.putExtra("Classcode",cArrayList.get(position).getClasscode());
                mContext.startActivity(intent); // move to the corresponding acitivity with that classroom's info
            }

        });
    }

    // Getting the item size
    @Override
    public int getItemCount() {
        return cArrayList.size();
    }




}
