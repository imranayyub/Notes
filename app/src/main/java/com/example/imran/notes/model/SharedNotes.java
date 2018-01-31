package com.example.imran.notes.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by imran on 28/1/18.
 */
//Model for SharedNotes.
public class SharedNotes {

    @SerializedName("sender_email")
    @Expose
    String sender_email;
    @SerializedName("recipient")
    @Expose
    String recipient;
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


    public SharedNotes(String sender_email, String recipient, String title, String note, String color, String tag,String id ) {
        this.sender_email = sender_email;
        this.recipient = recipient;
        this.title = title;
        this.note = note;
        this.color = color;
        this.tag = tag;
        this.id=id;
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

    public String getSender_Email() {
        return sender_email;
    }

    public void setSender_Email(String email) {
        this.sender_email = email;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
