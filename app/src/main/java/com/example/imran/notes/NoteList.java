package com.example.imran.notes;

/**
 * Created by imran on 15/1/18.
 */

public class NoteList {
    String title, note;
    String id,tag;

    public NoteList(String id,String title, String note,  String tag) {
        this.title = title;
        this.note = note;
        this.id = id;
        this.tag = tag;

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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
