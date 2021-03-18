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

        if (message.getUser().equals("kimon")) {
            holder.messageUserIconImage.setImageResource(R.drawable.photo1);
        } else if (message.getUser().equals("rania@hotmail.com")) {
            holder.messageUserIconImage.setImageResource(R.drawable.rania);
        } else if (message.getUser().equals("Philip")) {
            holder.messageUserIconImage.setImageResource(R.drawable.philip);
        } else if (message.getUser().equals("anbo")) {
            holder.messageUserIconImage.setImageResource(R.drawable.anbo);
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
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView messageContentTextView, messageUserTextView, messageCommentsNoTextView;
        final ImageView messageUserIconImage;
        final ImageButton messageDeleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            messageContentTextView = itemView.findViewById(R.id.messageContentTextView);
            messageUserTextView = itemView.findViewById(R.id.messageUserTextView);
            messageUserIconImage = itemView.findViewById(R.id.messageUserIconImage);
            messageDeleteButton = itemView.findViewById(R.id.messageDeleteButton);
            messageCommentsNoTextView = itemView.findViewById(R.id.messageCommentsNoTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition(), data.get(getAdapterPosition()));
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

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position, Message message);
    }
}