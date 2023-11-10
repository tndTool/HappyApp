package com.example.happyapp.model;

public class FAQs {
    private String title, description;
    private boolean expandable;

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public FAQs(String title, String description) {
        this.title = title;
        this.description = description;
        this.expandable = false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "FAQs{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
