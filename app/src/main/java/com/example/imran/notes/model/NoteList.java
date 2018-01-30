package com.example.imran.notes.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by imran on 15/1/18.
 */
//Model for Normal notes.
public class NoteList {

    @SerializedName("emails")
    @Expose
    String email;
    @SerializedName("title")
    @Expose
    String title;
    @SerializedName("note")
    @Expose
    String note;
    @SerializedName("color")
    @Expose
    String color;
    @SerializedName("tag")
    @Expose
    String tag;

    @SerializedName("id")
    @Expose
    String id;

    public NoteList(String email, String title, String note, String color, String tag,String id) {
        this.email = email;
        this.title = title;
        this.note = note;
        this.color = color;
        this.tag = tag;
        this.id=id;
    }

    public NoteList() {
    }

    public String getTitle() {
        return title;
    }

    public String getNote() {
        return note;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
