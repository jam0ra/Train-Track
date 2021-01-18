package com.example.traintrack;

public class StudentNote {
    private String title;
    private String description;
    private int priority;

    public StudentNote() {
        //empty constructor needed!
    }

    public StudentNote(String title, String description, int priority){
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }
}
