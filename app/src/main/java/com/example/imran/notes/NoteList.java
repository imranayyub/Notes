package com.example.imran.notes;

/**
 * Created by imran on 15/1/18.
 */

public class NoteList {
    String title, note;
    String id,priority,date;

    public NoteList(String title, String note, String id, String priority, String date) {
        this.title = title;
        this.note = note;
        this.id = id;
        this.priority = priority;
        this.date = date;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
