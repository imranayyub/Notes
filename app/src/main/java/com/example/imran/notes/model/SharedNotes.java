package com.example.imran.notes.model;

import com.example.imran.notes.database.Appdatabase;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by imran on 28/1/18.
 */
//Model for SharedNotes.
@Table(database = Appdatabase.class)

public class SharedNotes extends BaseModel {

    @Column
    @SerializedName("sender_email")
    @Expose
    String sender_email;

    @Column
    @SerializedName("recipient")
    @Expose
    String recipient;

    @Column
    @SerializedName("title")
    @Expose
    String title;

    @Column
    @SerializedName("note")
    @Expose
    String note;

    @Column
    @SerializedName("color")
    @Expose
    String color;

    @Column
    @SerializedName("tag")
    @Expose
    String tag;

    @Column
    @SerializedName("tag1")
    @Expose
    String tag1;

    @Column
    @SerializedName("tag2")
    @Expose
    String tag2;

    @PrimaryKey
    @Column
    @SerializedName("id")
    @Expose
    String id;

    @Column
    String status;

    public SharedNotes(String sender_email, String recipient, String title, String note, String color, String tag, String tag1, String tag2, String id) {
        this.sender_email = sender_email;
        this.recipient = recipient;
        this.title = title;
        this.note = note;
        this.color = color;
        this.tag = tag;
        this.id = id;
        this.tag1 = tag1;
        this.tag2 = tag2;
    }

    public SharedNotes() {
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

    public String getTag1() {
        return tag1;
    }

    public String getTag2() {
        return tag2;
    }

    public void setTag1(String tag1) {
        this.tag1 = tag1;
    }

    public void setTag2(String tag2) {
        this.tag2 = tag2;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
