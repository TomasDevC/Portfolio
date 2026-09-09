package com.starterkit.springboot.todo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class TodoRequest {
    private String title;
    private String description;
    private Boolean done; // opcional

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getDone() { return done; }
    public void setDone(Boolean done) { this.done = done; }
}