package com.example.imran.notes;

/**
 * Created by imran on 15/1/18.
 */

public class NoteList {
    String title,note;

    public NoteList(String title, String note) {
        this.title = title;
        this.note = note;
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
}
