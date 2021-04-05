package com.example.twisterpm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.twisterpm.model.Message;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
import static com.example.twisterpm.ApiUtils.MY_PREFS;

public class AllMessagesActivity extends AppCompatActivity {
    TextView verifyEmailTextView, welcomeTextView, toolbarTitle;
    Button verifyEmailButton, postNewMessageButton;
    ImageButton homeButton, backButton;
    FloatingActionButton scrollToTopButton;
    SearchView searchView;
    AlertDialog.Builder deleteMessageAlert, longClickCommentAlert;
    FirebaseAuth fAuth;
    private SwipeRefreshLayout swipeRefreshLayout;
    RecyclerViewMessageAdapter adapter;
    List<Message> messages;
    RelativeLayout postMessageLayout;
    LayoutInflater layoutInflater;
    NestedScrollView nestedScrollView;
    boolean haveAlreadySearched;
    String selectedUser;

    //ImageButton messageOverflowButton;
    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (fAuth.getCurrentUser() != null) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            MenuItem searchItem = menu.findItem(R.id.action_search);
            SearchView searchView = (SearchView) searchItem.getActionView();
            haveAlreadySearched = false;
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    selectedUser = query.trim().replaceAll(" +", " ");
                    GetMessagesByUser(selectedUser);
                    haveAlreadySearched = true;
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });

            searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    if (haveAlreadySearched){
                        GetMessages();
                        haveAlreadySearched = false;
                    }
                    return true;
                }
            });
        }

        Log.d("KIMON", "AllMessages Activity: onCreateOptionsMenu");
        //getMenuInflater().inflate(R.menu.menu_bottom, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.d("KIMON", "Main Activity: onOptionsItemSelected");

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
//                                    GetMessagesByUser(selectedUser);
//                                }
//                            }
//                        })
//                        .setNeutralButton("Reset", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                GetMessages();
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

        // SET THEME FROM PREFERENCES FILE
        SharedPreferences prefs = getSharedPreferences(MY_PREFS, MODE_PRIVATE);
        String theme = prefs.getString("Theme", "No theme defined");//"No name defined" is the default value.
        Log.d("KIMON", "Theme from preferences file: " + theme);

        if (theme.equals("Dark")) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
        } else if (theme.equals("Light")) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_all_messages);
        postMessageLayout = findViewById(R.id.postMessageLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        //toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        homeButton = findViewById(R.id.homeButton);
        nestedScrollView = findViewById(R.id.allMessagesScrollView);
        scrollToTopButton = findViewById(R.id.scrollToTopMessageButton);
        deleteMessageAlert = new AlertDialog.Builder(this);
        longClickCommentAlert = new AlertDialog.Builder(this);
        layoutInflater = this.getLayoutInflater();
        backButton = findViewById(R.id.backButton);
        toolbarTitle = findViewById(R.id.toolbarTitle);

        toolbarTitle.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);


        Log.d("KIMON", "AllMessages Activity: onCreate");

        SwipeRefresh();
        CheckMailVerification();
        NestedScrollView scrollView = findViewById(R.id.allMessagesScrollView);
        scrollView.setFillViewport(true);
        welcomeTextView = findViewById(R.id.welcomeTextView);

        if (fAuth.getCurrentUser() != null) {
            welcomeTextView.setText("Hi, " + fAuth.getCurrentUser().getEmail() + "!");
        } else {
            welcomeTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
            });
        }


        verifyEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send verification email
                fAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Verification e-mail sent", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        CheckIfPostButtonShouldBeEnabled();
        postNewMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostMessage();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AllMessagesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


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
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX,
                                       int oldScrollY) {
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
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("KIMON", "AllMessages Activity: onResume");
        GetMessages();
    }

//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        outState.putSerializable("KIMON", new ArrayList(messages));
//        super.onSaveInstanceState(outState);
//        Log.d("KIMON", "AllMessages Activity: onSaveInstanceState");
//    }
//
//    @Override
//    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
//        messages = (List<Message>) savedInstanceState.getSerializable("KIMON");
//        PopulateRecyclerView(messages);
//        super.onRestoreInstanceState(savedInstanceState);
//        Log.d("KIMON", "AllMessages Activity: onRestoreInstanceState");
//    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void CheckMailVerification() {
        fAuth = FirebaseAuth.getInstance();
        verifyEmailTextView = findViewById(R.id.verifyEmailTextView);
        verifyEmailButton = findViewById(R.id.verifyEmailButton);
        postNewMessageButton = findViewById(R.id.postNewMessageButton);
        if (fAuth.getCurrentUser() != null) {
            fAuth.getCurrentUser().reload();
            //Log.d("KIMON", "CheckMailVerification start - " + fAuth.getCurrentUser().getEmail());

            if (!fAuth.getCurrentUser().isEmailVerified()) {
                verifyEmailTextView.setVisibility(View.VISIBLE);
                verifyEmailButton.setVisibility(View.VISIBLE);
                postMessageLayout.setVisibility(View.GONE);
                Log.d("KIMON", "CheckMailVerification: not verified - " + fAuth.getCurrentUser().getEmail());
            }
            if (fAuth.getCurrentUser().isEmailVerified()) {
                verifyEmailTextView.setVisibility(View.GONE);
                verifyEmailButton.setVisibility(View.GONE);
                postMessageLayout.setVisibility(View.VISIBLE);
                Log.d("KIMON", "CheckMailVerification: verified - " + fAuth.getCurrentUser().getEmail());
            }
            //Log.d("KIMON", "CheckMailVerification end - " + fAuth.getCurrentUser().getEmail());
        }
//        else{
//            verifyEmailTextView.setVisibility(View.VISIBLE);
//            verifyEmailButton.setVisibility(View.VISIBLE);
//        }
    }

    public void SwipeRefresh() {
        swipeRefreshLayout = findViewById(R.id.mainSwipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            //swipeRefreshLayout.setRefreshing(true); // show progress
            CheckMailVerification();
            if (!haveAlreadySearched){
                GetMessages();
            } else GetMessagesByUser(selectedUser);
            if (fAuth.getCurrentUser() != null) {
                TextView welcomeTextView = findViewById(R.id.welcomeTextView);
                welcomeTextView.setText("Hi, " + fAuth.getCurrentUser().getEmail() + "!");
            }
            //swipeRefreshLayout.setRefreshing(false);
        });
    }

    public void GetMessages() {

        swipeRefreshLayout.setRefreshing(true);
        TwisterPMService service = ApiUtils.getTwisterPMService();
        Call<List<Message>> messageCall = service.getMessages();
        messageCall.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    //String responseMessage = response.message();
                    messages = response.body();
                    //Log.d("KIMON", messages.get(1).getUser());
                    PopulateRecyclerView(messages);
                    Toolbar toolbar = findViewById(R.id.toolbar);
                    //toolbar.setTitle("All messages (" + messages.size() + ")");
                } else {
                    Toast.makeText(getApplicationContext(), response.code(), Toast.LENGTH_LONG).show();
                    String errorMessage = "Problem " + response.code() + " " + response.message();
                    Log.d("KIMON", errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("KIMON", t.getMessage());
            }
        });
    }

    public void GetMessagesByUser(String selectedUser) {
        swipeRefreshLayout.setRefreshing(true);

        TwisterPMService service = ApiUtils.getTwisterPMService();
        Call<List<Message>> messageCall = service.getMessagesByUser(selectedUser);
        messageCall.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    //String responseMessage = response.message();
                    messages = response.body();
                    //Log.d("KIMON", messages.get(1).getUser());
                    Log.d("KIMON", messages.toString());
                    PopulateRecyclerView(messages);
                    Toolbar toolbar = findViewById(R.id.toolbar);
                    //toolbar.setTitle("All messages (" + messages.size() + ")");
                } else {
                    Toast.makeText(getApplicationContext(), response.code(), Toast.LENGTH_LONG).show();
                    String errorMessage = "Problem " + response.code() + " " + response.message();
                    Log.d("KIMON", errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("KIMON", t.getMessage());
            }
        });
    }

    public void PostMessage() {
        if (fAuth.getCurrentUser() != null) {
            EditText newMessageEditText = findViewById(R.id.newMessageEditText);
            if (newMessageEditText.getText().toString().trim().length() != 0) {
                swipeRefreshLayout.setRefreshing(true);

                String newMessageContent = newMessageEditText.getText().toString().trim().replaceAll(" +", " ");

                String newMessageUser = fAuth.getCurrentUser().getEmail();
                Message newMessage = new Message();
                newMessage.setContent(newMessageContent);
                newMessage.setUser(newMessageUser);

                TwisterPMService service = ApiUtils.getTwisterPMService();

                Call<Message> messageCall = service.postMessage(newMessage);
                messageCall.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        swipeRefreshLayout.setRefreshing(false);
                        if (response.isSuccessful()) {
                            newMessageEditText.setText("");
                            newMessageEditText.clearFocus();
                            Toast.makeText(getApplicationContext(), "Message successfully posted", Toast.LENGTH_LONG).show();
                            GetMessages();

                        } else {
                            Toast.makeText(getApplicationContext(), response.code(), Toast.LENGTH_LONG).show();
                            String errorMessage = "Problem " + response.code() + " " + response.message();
                            Log.d("KIMON", errorMessage);
                        }

                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Message must contain non-space characters", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void DeleteMessage(int position) {
        TwisterPMService service = ApiUtils.getTwisterPMService();

        Call<Message> messageCall = service.deleteMessage(adapter.getItem(position).getId());
        messageCall.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful()) {
                    //Intent intent = new Intent(getApplicationContext(), AllMessagesActivity.class);
                    //intent.putExtra("SINGLEMESSAGE", "Message deleted");
                    Log.d("KIMON", "Message with id " + adapter.getItem(position).getId() + " deleted");
                    //startActivity(intent);
                    GetMessages();
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

    public void ShowDeleteMessageAlert(int position) {
        //deleteMessageAlert = new AlertDialog.Builder(getApplicationContext());
        deleteMessageAlert.setTitle("Delete Message")
                .setMessage("Are you sure you want to delete this message?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteMessage(position);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GetMessages();
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                GetMessages();
            }
        })
                .create().show();
    }


    private void CheckIfPostButtonShouldBeEnabled() {
        EditText newMessageEditText = findViewById(R.id.newMessageEditText);
        if (newMessageEditText.length() == 0) postNewMessageButton.setEnabled(false);
        newMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean isReady = newMessageEditText.getText().toString().trim().length() > 0;
                postNewMessageButton.setEnabled(isReady);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void PopulateRecyclerView(List<Message> messages) {
        RecyclerView recyclerView = findViewById(R.id.messagesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewMessageAdapter(this, messages);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
        //recyclerView.scheduleLayoutAnimation();
        adapter.setClickListener((view, position, item) -> {
            //Message message = (Message) item;
            Log.d("KIMON", item.toString());
            Intent intent = new Intent(this, SingleMessageActivity.class);
            intent.putExtra("SINGLEMESSAGE", item);
            Log.d("KIMON", "putExtra " + item.toString());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        adapter.setLongClickListener((view, position, item) -> {
                longClickCommentAlert.setTitle("Filter messages")
                        .setMessage("Show all messages from\n"+item.getUser()+"?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String selectedUser = item.getUser();
                                GetMessagesByUser(selectedUser);
                            }
                        }).setNegativeButton("No", null)
                        //.setView(view)
                        .create().show();
        });

        adapter.setRVButtonClickListener((view, position, item) -> {
            Log.d("KIMON", "Overflow button click on message: " + item.toString());
            Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenuTheme);
            androidx.appcompat.widget.PopupMenu popup = new androidx.appcompat.widget.PopupMenu(wrapper, view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_overflow, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_delete:
                            ShowDeleteMessageAlert(position);
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
                            if (fAuth.getCurrentUser().getEmail().equals(user) & fAuth.getCurrentUser().isEmailVerified()) {
                                ShowDeleteMessageAlert(position);
                                Log.d("KIMON", "Message in position " + position + " deleted with a swipe!");
                            } else {
                                GetMessages();
                                Log.d("KIMON", "Message in position " + position + " NOT deleted with a swipe: no permission");
                            }
                        } else {
                            GetMessages();
                            Log.d("KIMON", "Message in position " + position + " NOT deleted with a swipe: no permission");
                        }
                    }
                }
            };
}




