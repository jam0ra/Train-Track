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

import com.example.traintrack.R;
import com.example.traintrack.ShowVideo;
import com.example.traintrack.StudentAssignmentActivity;
import com.example.traintrack.SubmissionVideo;

public class StudentHomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


       View view = inflater.inflate(R.layout.fragment_student_assignments_home, container, false);

        Button btnOpen_s = (Button) view.findViewById(R.id.btnOpen_s);
        btnOpen_s.setOnClickListener(v -> {
            Intent in = new Intent(getActivity(), StudentAssignmentActivity.class);
            in.putExtra("some", "some data");
            startActivity(in);
        });

        Button btnOpen2_s = (Button) view.findViewById(R.id.btnOpen2_s);
        btnOpen2_s.setOnClickListener(v -> {
            Intent in = new Intent(getActivity(), ShowVideo.class);
            in.putExtra("some", "some data");
            startActivity(in);
        });

        Button btnOpen3_s = (Button) view.findViewById(R.id.btnOpen3_s);
        btnOpen3_s.setOnClickListener(v -> {
            Intent in = new Intent(getActivity(), SubmissionVideo.class);
            in.putExtra("some", "some data");
            startActivity(in);
        });

        if (getArguments() != null && !getArguments().isEmpty()){
            TextView textClassName = view.findViewById(R.id.classroom_name2);
            TextView textClassCode = view.findViewById(R.id.classroom_code2);
            String className = getArguments().getString("Classname");
            String classCode = getArguments().getString("Classcode");
            textClassName.setText(className);  //Receive the Classname info
            textClassCode.setText(classCode);  //Receive the Classcode information
        }

       return view;
    }
}
