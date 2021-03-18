package com.example.twisterpm;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.twisterpm.model.Message;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FirstFragment extends Fragment {
    TextView verifyEmailTextView;
    Button verifyEmailButton, postNewMessageButton;
    ImageView messageDeleteIconImage;
    FirebaseAuth fAuth;
    private SwipeRefreshLayout swipeRefreshLayout;
    RecyclerViewMessageAdapter adapter;

    public FirstFragment(){

    }
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
//    }
//
//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_main, menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_allMessages:  {
//                                NavHostFragment.findNavController(FirstFragment.this)
//                       .navigate(R.id.action_FirstFragment_to_SecondFragment);
//                return true;
//            }
//            case R.id.action_myMessages: {
//                return true;
//            }
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//
//    }




    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);

    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("KIMON", "In First Fragment");
        SwipeRefresh();
        CheckMailVerification();
        GetMessages();

        NestedScrollView scrollView = getActivity().findViewById (R.id.firstFragmentScrollView);
        scrollView.setFillViewport (true);
        TextView welcomeTextView = getActivity().findViewById(R.id.welcomeTextView);
        welcomeTextView.setText("Hi, " + fAuth.getCurrentUser().getEmail()+"!");


        verifyEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send verification email
                fAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Verification e-mail sent", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
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

    public void CheckMailVerification() {

        fAuth = FirebaseAuth.getInstance();
        fAuth.getCurrentUser().reload();
        Log.d("KIMON", "CheckMailVerification start - " + fAuth.getCurrentUser().getEmail());
        verifyEmailTextView = getView().findViewById(R.id.verifyEmailTextView);
        verifyEmailButton = getView().findViewById(R.id.verifyEmailButton);
        postNewMessageButton = getView().findViewById(R.id.postNewMessageButton);
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
        swipeRefreshLayout = getView().findViewById(R.id.mainSwipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            //swipeRefreshLayout.setRefreshing(true); // show progress
            CheckMailVerification();
            GetMessages();
            TextView welcomeTextView = getActivity().findViewById(R.id.welcomeTextView);
            welcomeTextView.setText("Hi, " + fAuth.getCurrentUser().getEmail()+"!");
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
                    List<Message> messages = response.body();
                    //Log.d("KIMON", messages.get(1).getUser());
                    populateRecyclerView(messages);
                } else {
                Toast.makeText(getActivity(), response.code(), Toast.LENGTH_LONG).show();
                String errorMessage = "Problem " + response.code() + " " + response.message();
                Log.d("KIMON", errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void PostMessage(){
        EditText newMessageEditText = getActivity().findViewById(R.id.newMessageEditText);

        if (newMessageEditText.getText().toString().trim().length() != 0){
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
                        Toast.makeText(getActivity(), "Message successfully posted", Toast.LENGTH_LONG).show();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        GetMessages();

                    } else {
                        Toast.makeText(getActivity(), response.code(), Toast.LENGTH_LONG).show();
                        String errorMessage = "Problem " + response.code() + " " + response.message();
                        Log.d("KIMON", errorMessage);
                    }
                }

                @Override
                public void onFailure(Call<Message> call, Throwable t) {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }else{
            Toast.makeText(getActivity(), "Message must contain non-space characters", Toast.LENGTH_LONG).show();
        }



    }

    private void CheckIfPostButtonShouldBeEnabled(){
        EditText newMessageEditText = getActivity().findViewById(R.id.newMessageEditText);
        if(newMessageEditText.length() == 0) postNewMessageButton.setEnabled(false);
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
        RecyclerView recyclerView = getActivity().findViewById(R.id.messagesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RecyclerViewMessageAdapter(this.getContext(),messages);
        recyclerView.setAdapter(adapter);
        adapter.setClickListener((view, position, item) -> {
            Message message = (Message) item;
            Log.d("KIMON", item.toString());

           Intent intent = new Intent(getActivity(), SingleMessageActivity.class);
           intent.putExtra("SINGLEMESSAGE", message);
           Log.d("KIMON", "putExtra " + message.toString());
           startActivity(intent);


//            NavHostFragment.findNavController(FirstFragment.this)
//                .navigate(R.id.action_FirstFragment_to_SecondFragment);

        });
    }
}