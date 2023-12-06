package com.example.happyapp.model;

public class History {
    private String behavior;
    private String createAt;
    private int titleImage;

    public History(String behavior, String createAt, int titleImage) {
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

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public int getTitleImage() {
        return titleImage;
    }

    public void setTitleImage(int titleImage) {
        this.titleImage = titleImage;
    }
}
