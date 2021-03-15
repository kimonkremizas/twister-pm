package com.example.twisterpm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SingleMessageActivity extends AppCompatActivity {
    private Message singleMessage;
    private TextView messageUserTextView, messageContentTextView, messageCommentsNoTextView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_message);
        messageUserTextView = findViewById(R.id.messageUserTextView);
        messageContentTextView = findViewById(R.id.messageContentTextView);
        messageCommentsNoTextView = findViewById(R.id.messageCommentsNoTextView3);

        Log.d("KIMON", "In Single Message Activity");
        Intent intent = getIntent();
        singleMessage = (Message) intent.getSerializableExtra("SINGLEMESSAGE");
        Log.d("KIMON", "Intent: "+ singleMessage.toString());
        messageUserTextView.setText(singleMessage.getUser());
        messageContentTextView.setText(singleMessage.getContent());
        if (singleMessage.getTotalComments()==1){
            messageCommentsNoTextView.setText(singleMessage.getTotalComments() +" comment");
        }else{
            messageCommentsNoTextView.setText(singleMessage.getTotalComments()+" comments");
        }
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
        RecyclerViewSimpleAdapter<Comment> adapter = new RecyclerViewSimpleAdapter<>(comments);
        recyclerView.setAdapter(adapter);
//        adapter.setOnItemClickListener((view, position, item) -> {
//            Message message = (Message) item;
//            Log.d("KIMON", item.toString());
//            Intent intent = new Intent(this, SingleMessageActivity.class);
//            intent.putExtra("SINGLEMESSAGE", message);
//            Log.d("KIMON", "putExtra " + message.toString());
//            startActivity(intent);
//        });
    }

    public void GetComments() {
        swipeRefreshLayout.setRefreshing(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://anbo-restmessages.azurewebsites.net/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TwisterPMService service = retrofit.create(TwisterPMService.class);

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


}