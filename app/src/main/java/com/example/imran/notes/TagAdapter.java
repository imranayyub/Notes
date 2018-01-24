package com.example.imran.notes;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by imran on 24/1/18.
 */

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.MyHolder> {
    //this context we will use to inflate the layout
    Context context;
    public static ArrayList<String> tagList;
    public static String byTags = null;

    //getting the context and ride list with constructor
    public TagAdapter(Context context, ArrayList<String> tagList) {
        this.context = (Context) context;
        this.tagList = tagList;
    }

    @Override
    public TagAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tag_layout, null);
        return new TagAdapter.MyHolder(view);
    }


    @Override
    public void onBindViewHolder(TagAdapter.MyHolder holder, int position) {

        String tags = tagList.get(position);
        holder.tag.setText(tags);
    }


    @Override
    public int getItemCount() {
        return tagList.size();
    }


    //MyHolder class describes an item View and space with the recyclerView(Finds item within cardView Layout).
    public static class MyHolder extends RecyclerView.ViewHolder {
        TextView tag;
        Context context;
        int p;

        public MyHolder(final View itemView) {
            super(itemView);
            tag = (TextView) itemView.findViewById(R.id.tag);
            context = itemView.getContext();

            //in case any Note is clicked.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    p = getLayoutPosition();
                    byTags = tagList.get(p);
//                    Intent intent = new Intent(context, HomeActivity.class);
//                    context.startActivity(intent);

                }
            });

        }

    }
}