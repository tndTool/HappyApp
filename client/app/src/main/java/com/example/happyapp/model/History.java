package com.example.happyapp.model;

import java.util.Date;

public class History {
    private String behavior;
    private Date createAt;
    private String titleImage;

    public History(String behavior, Date createAt, String titleImage) {
        this.behavior = behavior;
        this.createAt = createAt;
        this.titleImage = titleImage;
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
