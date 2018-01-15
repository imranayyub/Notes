package com.example.imran.notes;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by imran on 13/1/18.
 */

public class AddNoteFragment extends Fragment implements View.OnClickListener {

    EditText note, noteTitle;
    Button addNote, cancel;
    String notes, title;

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
        noteTitle = (EditText) getActivity().findViewById(R.id.noteTitle);
        addNote = (Button) getActivity().findViewById(R.id.addNote);
        cancel = (Button) getActivity().findViewById(R.id.cancel);
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
                if (notes.length() == 0)
                    Toast.makeText(getActivity(), "Note Empty", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(getActivity(), "Note added successfully", Toast.LENGTH_SHORT).show();
                    Intent intent;

                    intent = new Intent(getActivity(), HomeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Addnote", notes);
                    bundle.putString("Title", title);
                    bundle.putInt("add", 1);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            }
            case R.id.cancel: {
                Intent intent;
                intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
                break;

            }
        }
    }


}
