package com.example.traintrack;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

// Class for the RecyclerViewHolder
public class ClassroomRecyclerViewHolder extends RecyclerView.ViewHolder {
    public TextView cName,cCode,cMaxCap;


    public ClassroomRecyclerViewHolder(View itemView){
        super(itemView);
        cName = itemView.findViewById(R.id.cName);
        cCode = itemView.findViewById(R.id.cCode);
        cMaxCap = itemView.findViewById(R.id.cMaxCap);

    }


}
