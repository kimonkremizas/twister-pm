package com.example.twisterpm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.twisterpm.model.Comment;
import com.example.twisterpm.model.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecyclerViewMessageAdapter extends RecyclerView.Adapter<RecyclerViewMessageAdapter.ViewHolder> {
    private final List<Message> data;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private ItemLongClickListener mLongClickListener;

    FirebaseAuth fAuth;

    // data is passed into the constructor
    RecyclerViewMessageAdapter(Context context, List<Message> data) {
        this.mInflater = LayoutInflater.from(context);
        this.data = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.single_message_layout, parent, false);
        return new ViewHolder(view);
    }


    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Message message = data.get(position);
        holder.messageContentTextView.setText(message.getContent());
        holder.messageUserTextView.setText(message.getUser());
        if (message.getTotalComments() == 1) {
            holder.messageCommentsNoTextView.setText(message.getTotalComments().toString() + " comment");
        } else {
            holder.messageCommentsNoTextView.setText(message.getTotalComments().toString() + " comments");
        }

        switch (message.getUser()){
            case "kimon":
                holder.imageView.setImageResource(R.drawable.photo1);
                break;
            case "rania@hotmail.com":
                holder.imageView.setImageResource(R.drawable.rania);
                break;
            case "Philip":
                holder.imageView.setImageResource(R.drawable.philip);
                break;
            case "anbo":
                holder.imageView.setImageResource(R.drawable.anbo);
                break;
        }

        fAuth = FirebaseAuth.getInstance();
        if (message.getUser().equals(fAuth.getCurrentUser().getEmail())) {
            holder.messageDeleteButton.setVisibility(View.VISIBLE);
        }

   }

    // total number of rows
    @Override
    public int getItemCount() {
        return data.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        final TextView messageContentTextView, messageUserTextView, messageCommentsNoTextView;
        final ImageView imageView;
        final ImageButton messageDeleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            messageContentTextView = itemView.findViewById(R.id.messageContentTextView);
            messageUserTextView = itemView.findViewById(R.id.messageUserTextView);
            imageView = itemView.findViewById(R.id.messageUserIconImage);
            messageDeleteButton = itemView.findViewById(R.id.messageDeleteButton);
            messageCommentsNoTextView = itemView.findViewById(R.id.messageCommentsNoTextView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition(), data.get(getAdapterPosition()));
        }

        @Override
        public boolean onLongClick(View view) {
            if (mLongClickListener != null)
                mLongClickListener.onItemLongClick(view, getAdapterPosition(), data.get(getAdapterPosition()));
            return true;
        }
    }

    // convenience method for getting data at click position
    Message getItem(int id) {
        return data.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    void setLongClickListener(ItemLongClickListener itemLongClickListener) {
        this.mLongClickListener = itemLongClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position, Message message);
    }

    public interface ItemLongClickListener {
        void onItemLongClick(View view, int position, Message message);
    }
}