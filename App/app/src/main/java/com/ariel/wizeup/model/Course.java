package com.ariel.wizeup.model;

import java.util.Date;

public class Course {

    private String cid;
    private String name;
    private String department;
    private String teacher_fname;
    private String teacher_lname;

    private String location;
    private Double points;
    private Date creation_date;
    private String files[];

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
}
