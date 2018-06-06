package com.ariel.wizeup.model;

import java.util.Date;

public class CourseFile {

    private String url;
    private Date creation_date;
    private String originalName;

    public String getName() {
        return originalName;
    }

    public void setName(String name) {
        this.originalName = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(Date creation_date) {
        this.creation_date = creation_date;
    }
}
