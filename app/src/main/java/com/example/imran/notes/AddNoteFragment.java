package com.example.imran.notes;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.imran.notes.HomeActivity.databaseNote;
import static com.example.imran.notes.HomeActivity.fab;
import static com.example.imran.notes.LoginActivity.userName;
import static com.example.imran.notes.MyAdapter.editNote;
import static com.example.imran.notes.MyAdapter.editNoteId;
import static com.example.imran.notes.MyAdapter.editNoteTitle;

/**
 * Created by imran on 13/1/18.
 */

public class AddNoteFragment extends Fragment implements View.OnClickListener {

    EditText note, noteTitle,noteTag;
    Button addNote, cancel;
    String notes, title,tag;
    FloatingActionButton addColor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.addnotefragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        note = (EditText) getActivity().findViewById(R.id.note);
        noteTag = (EditText) getActivity().findViewById(R.id.noteTag);
        noteTitle = (EditText) getActivity().findViewById(R.id.noteTitle);
        addNote = (Button) getActivity().findViewById(R.id.addNote);
        cancel = (Button) getActivity().findViewById(R.id.cancel);
        addColor=(FloatingActionButton)getActivity().findViewById(R.id.addColor);
        fab.setVisibility(View.INVISIBLE);
        if (editNoteId != null) {
            note.setText(editNote);
            noteTitle.setText(editNoteTitle);
        }
        addNote.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    //oncClick method to perform action according to the button being clicked.
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.addNote: {
                notes = note.getText().toString();
                title = noteTitle.getText().toString();
                tag=noteTag.getText().toString();
                if (notes.length() == 0)
                    Toast.makeText(getActivity(), "Note Empty", Toast.LENGTH_SHORT).show();
                else if (editNoteId != null) {
                    editNote(editNoteId, title, notes,tag);
                    Intent intent;
                    intent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "Note added successfully", Toast.LENGTH_SHORT).show();
                    Intent intent;
                    addNote();
                    intent = new Intent(getActivity(), HomeActivity.class);
                    fab.setVisibility(View.VISIBLE);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("Addnote", notes);
//                    bundle.putString("Title", title);
//                    bundle.putInt("add", 1);
//                    intent.putExtras(bundle);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                }
                break;
            }
            case R.id.cancel: {
                editNoteId = null;
                Intent intent;
                fab.setVisibility(View.VISIBLE);
                intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
                break;

            }
            case R.id.addColor:{

                break;
            }
        }
    }


    //adds note in the Firebase realtime database.
    public void addNote() {
        String nId = databaseNote.push().getKey();
        NoteList noteList = new NoteList(nId, title, notes,tag);
        databaseNote.child(nId).setValue(noteList);
        Toast.makeText(getActivity(), "Realtime Database!!!", Toast.LENGTH_SHORT).show();

    }

    public static void editNote(String editId, String editNoteTitle, String editNote,String priority) {
        editNoteId = null;
//        DatabaseReference editreference;
        NoteList noteList = new NoteList(editId, editNoteTitle, editNote,priority);
//        editreference = FirebaseDatabase.getInstance().getReference("NoteList").child(editId);
        databaseNote.child(editId).setValue(noteList);
//        Toast.makeText(getActivity(), "Updated Realtime Database!!!", Toast.LENGTH_SHORT).show();

    }
    public static void removeNote(String deleteNoteId)
    {
        databaseNote.child(deleteNoteId).removeValue();

    }
}
//onnotfiydatachanged.