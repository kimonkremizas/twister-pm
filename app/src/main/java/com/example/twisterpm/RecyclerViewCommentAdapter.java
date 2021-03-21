package com.example.twisterpm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.twisterpm.model.Comment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class RecyclerViewCommentAdapter extends RecyclerView.Adapter<RecyclerViewCommentAdapter.ViewHolder> {
    private final List<Comment> data;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private ItemLongClickListener mLongClickListener;
    FirebaseAuth fAuth;

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
        holder.commentUserTextView.setText(comment.getUser());

        switch (comment.getUser()) {
            case "kimon":
                holder.imageView.setImageResource(R.drawable.photo1);
                break;
            case "rania@hotmail.com":
                holder.imageView.setImageResource(R.drawable.rania);
                break;
            case "anbo":
                holder.imageView.setImageResource(R.drawable.anbo);
                break;
            default:
                holder.imageView.setImageResource(R.drawable.philip);
        }

        fAuth = FirebaseAuth.getInstance();
        if (comment.getUser().equals(fAuth.getCurrentUser().getEmail())) {
            holder.commentOverflowButton.setVisibility(View.VISIBLE);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return data.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        final TextView commentContentTextView, commentUserTextView;
        final ImageView imageView;
        final ImageButton commentOverflowButton;

        ViewHolder(View itemView) {
            super(itemView);
            commentContentTextView = itemView.findViewById(R.id.commentContentTextView);
            commentUserTextView = itemView.findViewById(R.id.commentUserTextView);
            imageView = itemView.findViewById(R.id.commentIconImage);
            commentOverflowButton = itemView.findViewById(R.id.commentOverflowButton);
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
    Comment getItem(int id) {
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
        void onItemClick(View view, int position, Comment comment);
    }

    public interface ItemLongClickListener {
        void onItemLongClick(View view, int position, Comment comment);
    }
}