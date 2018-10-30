package com.wizeup.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Course implements Parcelable {

    private String cid;
    private String name;
    private String department;
    private String teacher_fname;
    private String teacher_lname;
    private String teacher;
    private String teacher_email;
    private String students[];

    private String location;
    private Double points;
    private Date creation_date;
    private String files[];

    public Course(){}

    protected Course(Parcel in) {
        cid = in.readString();
        name = in.readString();
        department = in.readString();
        teacher_fname = in.readString();
        teacher_lname = in.readString();
        teacher = in.readString();
        teacher_email = in.readString();
        students = in.createStringArray();
        location = in.readString();
        if (in.readByte() == 0) {
            points = null;
        } else {
            points = in.readDouble();
        }
        files = in.createStringArray();
    }

    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

    public String[] getStudents() {
        return students;
    }

    public void setStudents(String[] students) {
        this.students = students;
    }

    public String getTeacher_email() {
        return teacher_email;
    }

    public void setTeacher_email(String teacher_email) {
        this.teacher_email = teacher_email;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getTeacher_lname() {
        return teacher_lname;
    }

    public void setTeacher_lname(String teacher_lname) {
        this.teacher_lname = teacher_lname;
    }

    public String[] getFiles() {
        return files;
    }

    public void setFiles(String[] files) {
        this.files = files;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTeacher_fname() {
        return teacher_fname;
    }

    public void setTeacher_fname(String teacher_fname) {
        this.teacher_fname = teacher_fname;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getPoints() {
        return points;
    }

    public void setPoints(Double points) {
        this.points = points;
    }

    public Date getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(Date creation_date) {
        this.creation_date = creation_date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(cid);
        parcel.writeString(name);
        parcel.writeString(department);
        parcel.writeString(teacher_fname);
        parcel.writeString(teacher_lname);
        parcel.writeString(teacher);
        parcel.writeString(teacher_email);
        parcel.writeStringArray(students);
        parcel.writeString(location);
        if (points == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(points);
        }
        parcel.writeStringArray(files);
    }
}
