package com.example.twisterpm;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.twisterpm.model.Comment;
import com.example.twisterpm.model.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingleMessageActivity extends AppCompatActivity {
    Message singleMessage;
    Comment selectedComment;
    TextView messageUserTextView, messageContentTextView, messageCommentsNoTextView;
    ImageButton messageDeleteButton, postCommentButton;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerViewCommentAdapter adapter;
    LayoutInflater inflater;
    AlertDialog.Builder resetAlert;
    FirebaseAuth fAuth;
    int singleMessageCommentsNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_message);
        Log.d("KIMON", "SingleMessage Activity: onCreate");
        messageUserTextView = findViewById(R.id.messageUserTextView);
        messageContentTextView = findViewById(R.id.messageContentTextView);
        messageCommentsNoTextView = findViewById(R.id.messageCommentsNoTextView);
        messageDeleteButton = findViewById(R.id.messageDeleteButton);
        postCommentButton = findViewById(R.id.postCommentButton);

        inflater = this.getLayoutInflater();
        resetAlert = new AlertDialog.Builder(this);

        Intent intent = getIntent();
        singleMessage = (Message) intent.getSerializableExtra("SINGLEMESSAGE");
        Log.d("KIMON", "Intent: " + singleMessage.toString());
        messageUserTextView.setText(singleMessage.getUser());
        messageContentTextView.setText(singleMessage.getContent());
        if (singleMessage.getTotalComments() == 1) {
            messageCommentsNoTextView.setText(singleMessage.getTotalComments() + " comment");
        } else {
            messageCommentsNoTextView.setText(singleMessage.getTotalComments() + " comments");
        }
        fAuth = FirebaseAuth.getInstance();
        if (singleMessage.getUser().equals(fAuth.getCurrentUser().getEmail())) {
            messageDeleteButton.setVisibility(View.VISIBLE);
        }

        messageDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //View view = inflater.inflate(R.layout.delete_message_popup, null);

                resetAlert.setTitle("Delete Message")
                        .setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DeleteMessage(singleMessage);
                            }
                        }).setNegativeButton("No", null)
                        // .setView(view)
                        .create().show();
            }
        });

        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = inflater.inflate(R.layout.post_comment_popup, null);

                resetAlert.setTitle("Post Comment")
                        //.setMessage("Enter your e-mail to receive password reset link")
                        .setPositiveButton("Post", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText postCommentEditText = view.findViewById(R.id.postCommentEditText);
                                //postCommentEditText.requestFocus();
                                if (postCommentEditText.getText().toString().trim().equals("")) {
                                    Log.d("KIMON", "Empty comment found!");
                                    postCommentEditText.setError("Required field");
                                } else {
                                    Log.d("KIMON", "Empty comment not found!");
                                    String newCommentContent = postCommentEditText.getText().toString().trim().replaceAll(" +", " ");
                                    String newCommentUser = fAuth.getCurrentUser().getEmail();
                                    Comment newComment = new Comment();
                                    newComment.setContent(newCommentContent);
                                    newComment.setUser(newCommentUser);
                                    newComment.setMessageId(singleMessage.getId());
                                    PostComment(singleMessage.getId(), newComment);
                                }
                            }
                        }).setNegativeButton("Cancel", null)
                        .setView(view)
                        .create().show();
            }
        });

        SwipeRefresh();
        GetComments();
    }


    public void SwipeRefresh() {
        swipeRefreshLayout = findViewById(R.id.singleMessageSwipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            //swipeRefreshLayout.setRefreshing(true); // show progress
            GetComments();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void populateRecyclerView(List<Comment> comments) {
        RecyclerView recyclerView = findViewById(R.id.commentsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewCommentAdapter(this, comments);
        recyclerView.setAdapter(adapter);
        adapter.setLongClickListener((view, position, item) -> {
            //Comment comment = (Comment) item;
            if (item.getUser().equals(fAuth.getCurrentUser().getEmail())) {
                Log.d("KIMON", "Long click with delete permission on comment: " + item.toString());
                resetAlert.setTitle("Delete Comment")
                        .setMessage("Are you sure you want to delete this comment?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                DeleteComment(item);
                                GetComments();
                            }
                        }).setNegativeButton("No", null)
                        // .setView(view)
                        .create().show();
            } else {
                Log.d("KIMON", "Long click with no delete permission on comment: " + item.toString());
            }
        });
    }

    public void GetComments() {
        swipeRefreshLayout.setRefreshing(true);

        TwisterPMService service = ApiUtils.getTwisterPMService();

        Call<List<Comment>> commentCall = service.getMessageComments(singleMessage.getId());
        commentCall.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    String responseMessage = response.message();
                    List<Comment> comments = response.body();
                    //Log.d("KIMON", comments.get(12).getContent());
                    populateRecyclerView(comments);
                    //messageCommentsNoTextView = findViewById(R.id.messageCommentsNoTextView);
                    singleMessageCommentsNo = comments.size();
                    if (singleMessageCommentsNo == 1) {
                        messageCommentsNoTextView.setText(singleMessageCommentsNo + " comment");
                    } else {
                        messageCommentsNoTextView.setText(singleMessageCommentsNo + " comments");
                    }
                } else {
                    //Toast.makeText(SingleMessageActivity,response.code(),Toast.LENGTH_LONG).show();
                    String message = "Problem " + response.code() + " " + response.message();
                    Log.d("KIMON", message);
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                //Toast.makeText(this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    public void PostComment(int messsageId, Comment newComment) {

        swipeRefreshLayout.setRefreshing(true);
        TwisterPMService service = ApiUtils.getTwisterPMService();

        Call<Comment> commentCall = service.postComment(messsageId, newComment);
        commentCall.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Comment successfully posted", Toast.LENGTH_LONG).show();
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    GetComments();
                } else {
                    Toast.makeText(getApplicationContext(), response.code(), Toast.LENGTH_LONG).show();
                    String errorMessage = "Problem " + response.code() + " " + response.message();
                    Log.d("KIMON", errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }


    public void DeleteMessage(Message singleMessage) {
        TwisterPMService service = ApiUtils.getTwisterPMService();

        Call<Message> messageCall = service.deleteMessage(singleMessage.getId());
        messageCall.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(getApplicationContext(), AllMessagesActivity.class);
                    intent.putExtra("SINGLEMESSAGE", "Message deleted");
                    Log.d("KIMON", "Message with id " + singleMessage.getId() + " deleted");
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Message successfully deleted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), response.code(), Toast.LENGTH_LONG).show();
                    String errorMessage = "Problem " + response.code() + " " + response.message();
                    Log.d("KIMON", errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void DeleteComment(Comment selectedComment) {
        TwisterPMService service = ApiUtils.getTwisterPMService();

        Call<Comment> commentCall = service.deleteComment(singleMessage.getId(), selectedComment.getId());
        commentCall.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (response.isSuccessful()) {
                    Log.d("KIMON", "Message with id " + singleMessage.getId() + " deleted");
                    Toast.makeText(getApplicationContext(), "Message successfully deleted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), response.code(), Toast.LENGTH_LONG).show();
                    String errorMessage = "Problem " + response.code() + " " + response.message();
                    Log.d("KIMON", errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}