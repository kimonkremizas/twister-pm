package com.example.twisterpm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.twisterpm.model.Comment;
import com.example.twisterpm.model.Message;

import java.util.List;

public class RecyclerViewCommentAdapter extends RecyclerView.Adapter<RecyclerViewCommentAdapter.ViewHolder> {
    private final List<Comment> data;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    RecyclerViewCommentAdapter(Context context, List<Comment> data) {
        this.mInflater = LayoutInflater.from(context);
        this.data = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.single_comment_layout, parent, false);
        return new ViewHolder(view);
    }


    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Comment comment = data.get(position);
        holder.commentContentTextView.setText(comment.getContent());
        holder.commentuserTextView.setText(comment.getUser());
        if (comment.getUser().equals("kimon")) {
            holder.imageView.setImageResource(R.drawable.photo1);
        } else if (comment.getUser().equals("rania@hotmail.com")) {
            holder.imageView.setImageResource(R.drawable.rania);
        } else if (comment.getUser().equals("Philip")) {
            holder.imageView.setImageResource(R.drawable.philip);
        } else if (comment.getUser().equals("anbo")) {
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
        final TextView commentContentTextView, commentuserTextView;
        final ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            commentContentTextView = itemView.findViewById(R.id.commentContentTextView);
            commentuserTextView = itemView.findViewById(R.id.commentUserTextView);
            imageView = itemView.findViewById(R.id.commentIconImage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition(), data.get(getAdapterPosition()));
        }
    }

    // convenience method for getting data at click position
    Comment getItem(int id) {
        return data.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position, Comment comment);
    }
}