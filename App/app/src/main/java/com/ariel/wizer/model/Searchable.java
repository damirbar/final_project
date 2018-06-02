package com.ariel.wizer.model;

public class Searchable {

    private User users[];
    private Session sessions[];
    private CourseFile files[];
    private Course courses[];

    public Course[] getCourses() {
        return courses;
    }

    public void setCourses(Course[] courses) {
        this.courses = courses;
    }

    public User[] getUsers() {
        return users;
    }

    public void setUsers(User[] users) {
        this.users = users;
    }

    public Session[] getSessions() {
        return sessions;
    }

    public void setSessions(Session[] sessions) {
        this.sessions = sessions;
    }

    public CourseFile[] getFiles() {
        return files;
    }

    public void setFiles(CourseFile[] files) {
        this.files = files;
    }
}
