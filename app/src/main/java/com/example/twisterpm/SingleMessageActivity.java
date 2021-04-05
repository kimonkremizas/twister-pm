package com.example.twisterpm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.twisterpm.model.Comment;
import com.example.twisterpm.model.Message;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingleMessageActivity extends AppCompatActivity {
    Message singleMessage;
    TextView messageUserTextView, messageContentTextView, messageCommentsNoTextView, toolbarTitle;
    ImageButton messageOverflowButton, postCommentButton, homeButton, backButton;
    ImageView messageUserImageView;
    ConstraintLayout postCommentLayout;
    NestedScrollView nestedScrollView;
    FloatingActionButton scrollToTopButton;
    SwipeRefreshLayout swipeRefreshLayout;
    SearchView searchView;
    RecyclerViewCommentAdapter adapter;
    LayoutInflater layoutInflater;
    MenuInflater menuInflater;
    AlertDialog.Builder postCommentAlert, deleteMessageAlert, deleteCommentAlert, longClickCommentAlert;
    FirebaseAuth fAuth;
    int singleMessageCommentsNo;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (fAuth.getCurrentUser() != null) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }
        Log.d("KIMON", "SingleMessage Activity: onCreateOptionsMenu");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.d("KIMON", "SingleMessage Activity: onOptionsItemSelected");

        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
                break;
//            case R.id.action_filter:
//                View filterView = layoutInflater.inflate(R.layout.filter_popup, null);
//                filterAlert = new AlertDialog.Builder(this);
//                filterAlert.setTitle("Select user")
//                        .setPositiveButton("Set User", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                EditText filterEditText = filterView.findViewById(R.id.filterEditText);
//                                //postCommentEditText.requestFocus();
//                                if (filterEditText.getText().toString().trim().equals("")) {
//                                    Log.d("KIMON", "Empty comment found!");
//                                    filterEditText.setError("Required field");
//                                } else {
//                                    Log.d("KIMON", "Empty comment not found!");
//                                    String selectedUser = filterEditText.getText().toString().trim().replaceAll(" +", " ");
//                                    GetCommentsByUser(selectedUser);
//                                }
//                            }
//                        })
//                        .setNeutralButton("Reset", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                GetComments();
//                            }
//                        })
//                        .setView(filterView)
//                        .create().show();
//                break;
        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_message);
        Intent intent = getIntent();
        singleMessage = (Message) intent.getSerializableExtra("SINGLEMESSAGE");
        Log.d("KIMON", "Intent: " + singleMessage.toString());
        Toolbar toolbar = findViewById(R.id.toolbar);
        //toolbar.setTitle(singleMessage.getUser());
        //toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        Log.d("KIMON", "SingleMessage Activity: onCreate");
        messageUserTextView = findViewById(R.id.messageUserTextView);
        messageUserImageView = findViewById(R.id.messageUserIconImage);
        messageContentTextView = findViewById(R.id.messageContentTextView);
        messageCommentsNoTextView = findViewById(R.id.messageCommentsNoTextView);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        messageOverflowButton = findViewById(R.id.messageOverflowButton);
        postCommentButton = findViewById(R.id.postCommentButton2);
        homeButton = findViewById(R.id.homeButton);
        postCommentLayout = findViewById(R.id.postCommentLayout);
        nestedScrollView = findViewById(R.id.singleMessageScrollView);
        scrollToTopButton = findViewById(R.id.scrollToTopCommentButton);
        searchView = findViewById(R.id.searchView);
        backButton = findViewById(R.id.backButton);
        layoutInflater = this.getLayoutInflater();
        menuInflater = this.getMenuInflater();
        postCommentAlert = new AlertDialog.Builder(this);
        deleteMessageAlert = new AlertDialog.Builder(this);
        deleteCommentAlert = new AlertDialog.Builder(this);
        longClickCommentAlert = new AlertDialog.Builder(this);


        toolbarTitle.setText(singleMessage.getUser());
        messageUserTextView.setText(singleMessage.getUser());
        homeButton.setVisibility(View.GONE);

        if (singleMessage.getUser().contains("kremizas") || singleMessage.getUser().contains("kimon")) {
            messageUserImageView.setImageResource(R.drawable.a005man);
        } else if (singleMessage.getUser().contains("dominik")) {
            messageUserImageView.setImageResource(R.drawable.a002man);
        } else if (singleMessage.getUser().contains("anbo") || singleMessage.getUser().contains("anders")) {
            messageUserImageView.setImageResource(R.drawable.a013man);
        } else if (singleMessage.getUser().contains("katerina")) {
            messageUserImageView.setImageResource(R.drawable.a003woman);
        } else if (singleMessage.getUser().contains("rania")) {
            messageUserImageView.setImageResource(R.drawable.a004woman);
        } else if (singleMessage.getUser().contains("ani")) {
            messageUserImageView.setImageResource(R.drawable.a001woman);
        } else if (singleMessage.getUser().contains("uks")) {
            messageUserImageView.setImageResource(R.drawable.a006woman);
        } else if (singleMessage.getUser().contains("nicolai")) {
            messageUserImageView.setImageResource(R.drawable.a011man);
        } else if (singleMessage.getUser().contains("o")) {
            messageUserImageView.setImageResource(R.drawable.a007woman);
        } else if (singleMessage.getUser().contains("y")) {
            messageUserImageView.setImageResource(R.drawable.a008woman);
        } else if (singleMessage.getUser().contains("t")) {
            messageUserImageView.setImageResource(R.drawable.a009woman);
        } else if (singleMessage.getUser().contains("s")) {
            messageUserImageView.setImageResource(R.drawable.a010woman);
        } else if (singleMessage.getUser().contains("z")) {
            messageUserImageView.setImageResource(R.drawable.a012woman);
        } else if (singleMessage.getUser().contains("m")) {
            messageUserImageView.setImageResource(R.drawable.a014man);
        } else {
            messageUserImageView.setImageResource(R.drawable.a015woman);
        }

        messageContentTextView.setText(singleMessage.getContent());
        if (singleMessage.getTotalComments() == 1) {
            messageCommentsNoTextView.setText(singleMessage.getTotalComments() + " comment");
        } else {
            messageCommentsNoTextView.setText(singleMessage.getTotalComments() + " comments");
        }

        fAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser() != null) {
            if (singleMessage.getUser().equals(fAuth.getCurrentUser().getEmail())) {
                messageOverflowButton.setVisibility(View.VISIBLE);
            }
            if (fAuth.getCurrentUser().isEmailVerified()) {
                postCommentLayout.setVisibility(View.VISIBLE);
            }
        }


        messageOverflowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("KIMON", "SingleMessage Activity: showOverflowPopup");
//                PopupMenu popup = new PopupMenu(getApplicationContext(), v);
//                MenuInflater inflater = popup.getMenuInflater();
//                inflater.inflate(R.menu.menu_overflow, popup.getMenu());
                Context wrapper = new ContextThemeWrapper(v.getContext(), R.style.PopupMenuTheme);
                PopupMenu popup = new androidx.appcompat.widget.PopupMenu(wrapper, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_overflow, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_delete:
                                ShowDeleteMessageAlert();
                                break;
                            case R.id.action_edit:
                                Toast.makeText(getApplicationContext(), "You pressed Edit", Toast.LENGTH_LONG).show();
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AllMessagesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String selectedUser = query.trim().replaceAll(" +", " ");
                GetCommentsByUser(selectedUser);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                GetComments();
                return false;
            }
        });

        CheckIfPostButtonShouldBeEnabled();
        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText postCommentEditText = findViewById(R.id.postCommentEditText2);
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
        });

        scrollToTopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("KIMON", "Home Button: pressed");
//                NestedScrollView nestedScrollView = findViewById(R.id.allMessagesScrollView);
                nestedScrollView.smoothScrollTo(0, 0);
            }
        });


        nestedScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == 0) {
                    Log.i("KIMON", "TOP SCROLL");
                    scrollToTopButton.setVisibility(View.GONE);
                }
                if (scrollY != 0) {
                    Log.i("KIMON", "TOP SCROLL");
                    scrollToTopButton.setVisibility(View.VISIBLE);
                } else scrollToTopButton.setVisibility(View.GONE);
            }
        });


        SwipeRefresh();
        GetComments();
        CheckMailVerification();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void SwipeRefresh() {
        swipeRefreshLayout = findViewById(R.id.singleMessageSwipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            //swipeRefreshLayout.setRefreshing(true); // show progress
            GetComments();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void PopulateRecyclerView(List<Comment> comments) {
        RecyclerView recyclerView = findViewById(R.id.commentsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewCommentAdapter(this, comments);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
        adapter.setLongClickListener((view, position, item) -> {
                longClickCommentAlert.setTitle("Filter comments")
                        .setMessage("Show all comments from\n"+item.getUser()+"?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String selectedUser = item.getUser();
                                    GetCommentsByUser(selectedUser);
                                }
                            }).setNegativeButton("No", null)
                            // .setView(view)
                            .create().show();
//                if (item.getUser().equals(fAuth.getCurrentUser().getEmail()) & fAuth.getCurrentUser().isEmailVerified()) {
//                    Log.d("KIMON", "Long click with delete permission on comment: " + item.toString());
//                    deleteCommentAlert.setTitle("Delete Comment")
//                            .setMessage("Are you sure you want to delete this comment?")
//                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    DeleteComment(position);
//                                }
//                            }).setNegativeButton("No", null)
//                            // .setView(view)
//                            .create().show();
//                }
        });

        adapter.setRVButtonClickListener((view, position, item) -> {
            Log.d("KIMON", "Overflow button click on message: " + item.toString());
            //PopupMenu popup = new PopupMenu(getApplicationContext(), view);
            Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenuTheme);
            androidx.appcompat.widget.PopupMenu popup = new androidx.appcompat.widget.PopupMenu(wrapper, view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_overflow, popup.getMenu());
            popup.setOnMenuItemClickListener(new androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_delete:
                            ShowDeleteCommentAlert(position);
                            break;
                        case R.id.action_edit:
                            Toast.makeText(getApplicationContext(), "You pressed Edit", Toast.LENGTH_LONG).show();
                            break;
                    }
                    return true;
                }
            });
            popup.show();
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
                    PopulateRecyclerView(comments);
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

    public void GetCommentsByUser(String selectedUser) {
        swipeRefreshLayout.setRefreshing(true);

        TwisterPMService service = ApiUtils.getTwisterPMService();
        Call<List<Comment>> commentCall = service.getCommentsByUser(singleMessage.getId(),selectedUser);
        commentCall.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    List<Comment> comments = response.body();
                    comments = response.body();
                    //Log.d("KIMON", messages.get(1).getUser());
                    Log.d("KIMON",comments.toString());
                    PopulateRecyclerView(comments);
                    Toolbar toolbar = findViewById(R.id.toolbar);
                    //toolbar.setTitle("All messages (" + messages.size() + ")");
                } else {
                    Toast.makeText(getApplicationContext(), response.code(), Toast.LENGTH_LONG).show();
                    String errorMessage = "Problem " + response.code() + " " + response.message();
                    Log.d("KIMON", errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("KIMON", t.getMessage());
            }
        });
    }

    public void PostComment(int messsageId, Comment newComment) {

        swipeRefreshLayout.setRefreshing(true);
        TwisterPMService service = ApiUtils.getTwisterPMService();
        EditText postCommentEditText = findViewById(R.id.postCommentEditText2);
        Call<Comment> commentCall = service.postComment(messsageId, newComment);
        commentCall.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    postCommentEditText.setText("");
                    postCommentEditText.clearFocus();
                    Toast.makeText(getApplicationContext(), "Comment successfully posted", Toast.LENGTH_LONG).show();
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
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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

    public void DeleteComment(int position) {
        TwisterPMService service = ApiUtils.getTwisterPMService();

        Call<Comment> commentCall = service.deleteComment(singleMessage.getId(), adapter.getItem(position).getId());
        commentCall.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (response.isSuccessful()) {
                    Log.d("KIMON", "Message with id " + singleMessage.getId() + " deleted");
                    Toast.makeText(getApplicationContext(), "Message successfully deleted", Toast.LENGTH_LONG).show();
                    GetComments();
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

    final ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    final int position = viewHolder.getAdapterPosition();
                    if (position >= 0) {
                        String user = adapter.getItem(position).getUser();
                        if (fAuth.getCurrentUser() != null) {
                            if (Objects.equals(fAuth.getCurrentUser().getEmail(), user) & fAuth.getCurrentUser().isEmailVerified()) {
                                ShowDeleteCommentAlert(position);
                                Log.d("KIMON", "Comment in position " + position + " deleted with a swipe!");
                            } else {
                                GetComments();
                                Log.d("KIMON", "Comment in position " + position + " NOT deleted with a swipe: no permission");
                            }
                        } else {
                            GetComments();
                            Log.d("KIMON", "Comment in position " + position + " NOT deleted with a swipe: no permission");
                        }
                    }
                }
            };

    public void ShowDeleteMessageAlert() {
        deleteMessageAlert.setTitle("Delete Message")
                .setMessage("Are you sure you want to delete this message?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteMessage(singleMessage);
                    }
                }).setNegativeButton("No", null)
                //.setView(deleteMessageView)
                .create().show();
    }

    public void ShowDeleteCommentAlert(int position) {
        deleteMessageAlert.setTitle("Delete Message")
                .setMessage("Are you sure you want to delete this message?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteComment(position);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GetComments();
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                GetComments();
            }
        })
                .create().show();
    }

    private void CheckIfPostButtonShouldBeEnabled() {
        Log.d("KIMON", "CheckIfPostButtonShouldBeEnabled");
        EditText postCommentEditText = findViewById(R.id.postCommentEditText2);
        if (postCommentEditText.length() == 0) postCommentButton.setEnabled(false);
        postCommentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean isReady = postCommentEditText.getText().toString().trim().length() > 0;
                postCommentButton.setEnabled(isReady);
                Log.d("KIMON", "onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void CheckMailVerification() {
        fAuth = FirebaseAuth.getInstance();
        messageOverflowButton = findViewById(R.id.messageOverflowButton);
        postCommentLayout = findViewById(R.id.postCommentLayout);
        if (fAuth.getCurrentUser() != null) {
            Log.d("KIMON", "CheckMailVerification start - " + fAuth.getCurrentUser().getEmail());
            if (!fAuth.getCurrentUser().isEmailVerified()) {
                messageOverflowButton.setVisibility(View.GONE);
                postCommentLayout.setVisibility(View.GONE);
                Log.d("KIMON", "CheckMailVerification: not verified - " + fAuth.getCurrentUser().getEmail());
            } else {
                Log.d("KIMON", "CheckMailVerification: verified - " + fAuth.getCurrentUser().getEmail());
            }
            Log.d("KIMON", "CheckMailVerification end - " + fAuth.getCurrentUser().getEmail());
        }

    }

}