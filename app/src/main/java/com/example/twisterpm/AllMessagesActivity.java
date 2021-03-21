package com.example.twisterpm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.twisterpm.model.Message;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllMessagesActivity extends AppCompatActivity {
    TextView verifyEmailTextView;
    Button verifyEmailButton, postNewMessageButton;
    ImageView messageDeleteIconImage;
    AlertDialog.Builder deleteMessageAlert;
    FirebaseAuth fAuth;
    private SwipeRefreshLayout swipeRefreshLayout;
    RecyclerViewMessageAdapter adapter;
    List<Message> messages;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
                break;
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                break;
            case R.id.action_allMessages:
                startActivity(new Intent(getApplicationContext(), AllMessagesActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_messages);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("All messages");
        setSupportActionBar(toolbar);
        deleteMessageAlert = new AlertDialog.Builder(this);

        Log.d("KIMON", "AllMessages Activity: onCreate");
        SwipeRefresh();
        CheckMailVerification();

        NestedScrollView scrollView = findViewById(R.id.firstFragmentScrollView);
        scrollView.setFillViewport(true);
        TextView welcomeTextView = findViewById(R.id.welcomeTextView);
        welcomeTextView.setText("Hi, " + fAuth.getCurrentUser().getEmail() + "!");

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
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("KIMON", "AllMessages Activity: onResume");
        GetMessages();
    }

    public void CheckMailVerification() {

        fAuth = FirebaseAuth.getInstance();
        fAuth.getCurrentUser().reload();
        Log.d("KIMON", "CheckMailVerification start - " + fAuth.getCurrentUser().getEmail());
        verifyEmailTextView = findViewById(R.id.verifyEmailTextView);
        verifyEmailButton = findViewById(R.id.verifyEmailButton);
        postNewMessageButton = findViewById(R.id.postNewMessageButton);
        if (!fAuth.getCurrentUser().isEmailVerified()) {
            verifyEmailTextView.setVisibility(View.VISIBLE);
            verifyEmailButton.setVisibility(View.VISIBLE);
            Log.d("KIMON", "CheckMailVerification: not verified - " + fAuth.getCurrentUser().getEmail());
        }
        if (fAuth.getCurrentUser().isEmailVerified()) {
            verifyEmailTextView.setVisibility(View.GONE);
            verifyEmailButton.setVisibility(View.GONE);
            Log.d("KIMON", "CheckMailVerification: verified - " + fAuth.getCurrentUser().getEmail());
        }
        Log.d("KIMON", "CheckMailVerification end - " + fAuth.getCurrentUser().getEmail());
    }

    public void SwipeRefresh() {
        swipeRefreshLayout = findViewById(R.id.mainSwipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            //swipeRefreshLayout.setRefreshing(true); // show progress
            CheckMailVerification();
            GetMessages();
            TextView welcomeTextView = findViewById(R.id.welcomeTextView);
            welcomeTextView.setText("Hi, " + fAuth.getCurrentUser().getEmail() + "!");
            swipeRefreshLayout.setRefreshing(false);
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
                    String responseMessage = response.message();
                    messages = response.body();
                    //Log.d("KIMON", messages.get(1).getUser());
                    populateRecyclerView(messages);
                    Toolbar toolbar = findViewById(R.id.toolbar);
                    toolbar.setTitle("All messages ("+ messages.size()+")");
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
            }
        });
    }

    public void PostMessage() {
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
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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

    private void populateRecyclerView(List<Message> messages) {
        RecyclerView recyclerView = findViewById(R.id.messagesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewMessageAdapter(this, messages);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
        adapter.setClickListener((view, position, item) -> {
            Message message = (Message) item;
            Log.d("KIMON", item.toString());
            Intent intent = new Intent(this, SingleMessageActivity.class);
            intent.putExtra("SINGLEMESSAGE", message);
            Log.d("KIMON", "putExtra " + message.toString());
            startActivity(intent);

        });

        adapter.setLongClickListener((view, position, item) -> {
            //Comment comment = (Comment) item;
            if (item.getUser().equals(fAuth.getCurrentUser().getEmail())) {
                Log.d("KIMON", "Long click with delete permission on message: " + item.toString());
                deleteMessageAlert.setTitle("Delete Message")
                        .setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DeleteMessage(item);
                            }
                        }).setNegativeButton("No", null)
                        //.setView(deleteMessageView)
                        .create().show();
            } else {
                Log.d("KIMON", "Long click with no delete permission on message: " + item.toString());
            }
        });

    }




    ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    messages.remove(viewHolder.getAdapterPosition());
                    adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                    Log.d("KIMON", "Message with Id=" + " deleted with a swipe!");
                }
            };
}