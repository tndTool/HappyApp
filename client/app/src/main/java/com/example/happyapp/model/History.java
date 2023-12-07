package com.example.happyapp.model;

import java.util.Date;

public class History {
    private String id;
    private String behavior;
    private Date createAt;
    private String titleImage;

    public History(String id, String behavior, Date createAt, String titleImage) {
        this.id = id;
        this.behavior = behavior;
        this.createAt = createAt;
        this.titleImage = titleImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBehavior() {
        return behavior;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getTitleImage() {
        return titleImage;
    }

    public void setTitleImage(String titleImage) {
        this.titleImage = titleImage;
    }
}
