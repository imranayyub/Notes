package com.example.imran.notes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Im on 21-11-2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {
    //this context we will use to inflate the layout
    Context context;

    //we are storing all the rides in a list
    private ArrayList<String> noteList;
    private ArrayList<String> titleList;

    //getting the context and ride list with constructor
    public MyAdapter(Context context,ArrayList<String> noteList,ArrayList<String> titleList) {
        this.context = context;
        this.noteList = noteList;
        this.titleList = titleList;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cardview, null);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        //getting the ride of the specified position
        String notes,title;
        notes = noteList.get(position);
        title=titleList.get(position);
        //binding the data with the viewholder views
        holder.note.setText("Note :" + notes);
        holder.title.setText("Title :"+title );

    }


    //Function to get the size of List noteList.
    @Override
    public int getItemCount() {
        return noteList.size();
    }

    //MyHolder class describes an item View and space with the recyclerView(Finds item within cardView Layout).
    public static class MyHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        TextView note, title;
        int p;

        public MyHolder(final View itemView) {
            super(itemView);
            note = (TextView) itemView.findViewById(R.id.adapterNote);
            title = (TextView) itemView.findViewById(R.id.title);
            itemView.setOnCreateContextMenuListener(this);
//            itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    p = getLayoutPosition();
//                    System.out.println("LongClick: " + p);
//                    Toast.makeText(itemView.getContext(), "Long click" + p, Toast.LENGTH_SHORT).show();
//                    return true;    // returning true instead of false, works for me
//                }
//            });

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//            super.onCreateContextMenu(menu, v, menuInfo);
            menu.setHeaderTitle("Options:");
            MenuItem remove = menu.add(0, v.getId(), 0, "Remove Note");//groupId, itemId, order, title
            MenuItem vImp = menu.add(0, v.getId(), 0, "Mark as Very Important");
            MenuItem imp = menu.add(0, v.getId(), 0, "Mark as Important");
            MenuItem cancel = menu.add(0, v.getId(), 0, "Cancel");
            remove.setOnMenuItemClickListener(onEditMenu);
            vImp.setOnMenuItemClickListener(onEditMenu);
            imp.setOnMenuItemClickListener(onEditMenu);
            cancel.setOnMenuItemClickListener(onEditMenu);
        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle() == "Remove Note") {
//                    p=itemView.getLayoutPosition;
                    p = getAdapterPosition();
                    HomeActivity.noteList.remove(p);
                    Toast.makeText(itemView.getContext(), "Removing Note", Toast.LENGTH_SHORT).show();

                } else if (item.getTitle() == "Mark as Very Important") {
                    Toast.makeText(itemView.getContext(), "Very Important Note", Toast.LENGTH_SHORT).show();
                } else if (item.getTitle() == "Mark as Important") {
                    Toast.makeText(itemView.getContext(), "Important Note", Toast.LENGTH_SHORT).show();
                } else if (item.getTitle() == "Cancel") {
                    Toast.makeText(itemView.getContext(), "Cancel", Toast.LENGTH_SHORT).show();
                } else {

                    return true;
                }
                return false;
            }

        };
    }
}
