package com.example.twisterpm;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.twisterpm.model.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class RecyclerViewMessageAdapter extends RecyclerView.Adapter<RecyclerViewMessageAdapter.ViewHolder> {
    private final List<Message> data;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private RVButtonClickListener rvButtonClickListener;
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

//        switch (message.getUser()) {
//            case "kimon":
//                holder.imageView.setImageResource(R.drawable.photo1);
//                break;
//            case "rania@hotmail.com":
//                holder.imageView.setImageResource(R.drawable.rania);
//                break;
//            case "anbo":
//                holder.imageView.setImageResource(R.drawable.anbo);
//                break;
//            default:
//                holder.imageView.setImageResource(R.drawable.philip);
//        }

        if (message.getUser().contains("kremizas") || message.getUser().contains("kimon")) {
            holder.imageView.setImageResource(R.drawable.a005man);
        } else if (message.getUser().contains("dominik")) {
            holder.imageView.setImageResource(R.drawable.a002man);
        } else if (message.getUser().contains("anbo") || message.getUser().contains("anders")) {
            holder.imageView.setImageResource(R.drawable.a013man);
        } else if (message.getUser().contains("katerina")) {
            holder.imageView.setImageResource(R.drawable.a003woman);
        } else if (message.getUser().contains("rania")) {
            holder.imageView.setImageResource(R.drawable.a004woman);
        } else if (message.getUser().contains("ani")) {
            holder.imageView.setImageResource(R.drawable.a001woman);
        } else if (message.getUser().contains("uks")) {
            holder.imageView.setImageResource(R.drawable.a006woman);
        } else if (message.getUser().contains("nicolai")) {
            holder.imageView.setImageResource(R.drawable.a011man);
        } else if (message.getUser().contains("o")) {
            holder.imageView.setImageResource(R.drawable.a007woman);
        } else if (message.getUser().contains("y")) {
            holder.imageView.setImageResource(R.drawable.a008woman);
        } else if (message.getUser().contains("t")) {
            holder.imageView.setImageResource(R.drawable.a009woman);
        } else if (message.getUser().contains("s")) {
            holder.imageView.setImageResource(R.drawable.a010woman);
        } else if (message.getUser().contains("z")) {
            holder.imageView.setImageResource(R.drawable.a012woman);
        } else if (message.getUser().contains("m")) {
            holder.imageView.setImageResource(R.drawable.a014man);
        } else {
            holder.imageView.setImageResource(R.drawable.a015woman);
        }

        fAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser() != null) {
            if (message.getUser().equals(fAuth.getCurrentUser().getEmail()) & fAuth.getCurrentUser().isEmailVerified()) {
                holder.messageOverflowButton.setVisibility(View.VISIBLE);
            }
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return data.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        final TextView messageContentTextView, messageUserTextView, messageCommentsNoTextView;
        final ImageView imageView;
        final ImageButton messageOverflowButton;

        ViewHolder(View itemView) {
            super(itemView);
            messageContentTextView = itemView.findViewById(R.id.messageContentTextView);
            messageUserTextView = itemView.findViewById(R.id.messageUserTextView);
            imageView = itemView.findViewById(R.id.messageUserIconImage);
            messageOverflowButton = itemView.findViewById(R.id.messageOverflowButton);
            messageCommentsNoTextView = itemView.findViewById(R.id.messageCommentsNoTextView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            messageOverflowButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (rvButtonClickListener != null & view == messageOverflowButton) {
                rvButtonClickListener.onRVButtonClick(view, getAdapterPosition(), data.get(getAdapterPosition()));
            } else {
                if (mClickListener != null)
                    mClickListener.onItemClick(view, getAdapterPosition(), data.get(getAdapterPosition()));
            }
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

    void setRVButtonClickListener(RVButtonClickListener rvButtonClickListener) {
        this.rvButtonClickListener = rvButtonClickListener;
    }


    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position, Message message);
    }

    public interface ItemLongClickListener {
        void onItemLongClick(View view, int position, Message message);
    }

    public interface RVButtonClickListener {
        void onRVButtonClick(View view, int position, Message message);
    }
}