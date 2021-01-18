package com.example.traintrack;

// Class for Classroom class
public class Classroom {
    private String classname;
    private String classcode;
    private long maxCapacity;


    public Classroom() {
        //empty constructor needed!
    }

    public Classroom(String classname, String classcode, long maxCapacity){
        this.classname = classname;
        this.classcode = classcode;
        this.maxCapacity = maxCapacity;
    }


    public String getClassname() {
        return this.classname;
    }

    public String getClasscode() {
        return this.classcode;
    }

    public long getMaxCapacity() {return this.maxCapacity; }
}
