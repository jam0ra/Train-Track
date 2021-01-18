package com.example.traintrack.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.traintrack.DemoVideo;
import com.example.traintrack.Notification;
import com.example.traintrack.R;
import com.example.traintrack.ShowVideoSubmission;
import com.example.traintrack.TeacherAssignmentActivity;

public class TeacherHomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_assignments_home, container,false);

       Button btnOpen = (Button) view.findViewById(R.id.btnOpen);
       btnOpen.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent in = new Intent(getActivity(), TeacherAssignmentActivity.class);
               in.putExtra("some", "some data");
               startActivity(in);
           }
       });

       Button btnOpen2 = (Button) view.findViewById(R.id.btnOpen2);
       btnOpen2.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent in = new Intent(getActivity(), DemoVideo.class);
               in.putExtra("some", "some data");
               startActivity(in);
           }
       });

        Button btnOpen3 = (Button) view.findViewById(R.id.btnOpen3);
        btnOpen3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), ShowVideoSubmission.class);
                in.putExtra("some", "some data");
                startActivity(in);
            }
        });

        Button btnOpen4 = (Button) view.findViewById(R.id.btnOpen4);
        btnOpen4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), Notification.class);
                in.putExtra("some", "some data");
                startActivity(in);
            }
        });

        if (getArguments() != null && !getArguments().isEmpty()){
            TextView textClassName = view.findViewById(R.id.classroom_name);
            TextView textClassCode = view.findViewById(R.id.classroom_code);
            String className = getArguments().getString("Classname");
            String classCode = getArguments().getString("Classcode");
            textClassName.setText(className);  //Receive the Classname info
            textClassCode.setText(classCode);  //Receive the Classcode information
        }

        return view;


    }

}
