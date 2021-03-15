package com.example.twisterpm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewPrettyAdapter extends RecyclerView.Adapter<RecyclerViewPrettyAdapter.ViewHolder> {
    private final List<Message> data;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    RecyclerViewPrettyAdapter(Context context, List<Message> data) {
        this.mInflater = LayoutInflater.from(context);
        this.data = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.single_message, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = data.get(position);
        holder.messageContentTextView.setText(message.getContent());
        holder.userTextView.setText(message.getUser());
        if (message.getTotalComments() == 1) {
            holder.messageCommentsNoTextView.setText(message.getTotalComments().toString() + " comment");
        } else {
            holder.messageCommentsNoTextView.setText(message.getTotalComments().toString() + " comments");
        }

        if (message.getUser().equals("kimon")) {
            holder.imageView.setImageResource(R.drawable.photo1);
        } else if (message.getUser().equals("rania@hotmail.com")) {
            holder.imageView.setImageResource(R.drawable.rania);
        } else if (message.getUser().equals("Philip")) {
            holder.imageView.setImageResource(R.drawable.philip);
        } else if (message.getUser().equals("anbo")) {
            holder.imageView.setImageResource(R.drawable.anbo);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return data.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView messageContentTextView, userTextView, messageCommentsNoTextView;
        final ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            messageContentTextView = itemView.findViewById(R.id.messageContentTextView);
            userTextView = itemView.findViewById(R.id.messageUserTextView);
            imageView = itemView.findViewById(R.id.messageIconImage);
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