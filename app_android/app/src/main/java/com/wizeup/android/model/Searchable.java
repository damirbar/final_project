package com.wizeup.android.model;

import java.util.ArrayList;

public class Searchable {

    private ArrayList<User> users;
    private ArrayList<Session> sessions;
    private ArrayList<CourseFile> files;
    private ArrayList<Course> courses;


    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public ArrayList<Session> getSessions() {
        return sessions;
    }

    public void setSessions(ArrayList<Session> sessions) {
        this.sessions = sessions;
    }

    public ArrayList<CourseFile> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<CourseFile> files) {
        this.files = files;
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<Course> courses) {
        this.courses = courses;
    }
}
