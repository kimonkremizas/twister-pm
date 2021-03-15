package com.example.twisterpm;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TwisterPMService {
    @GET("messages")
    Call<List<Message>> getMessages();

    @GET("messages/{messageId}/comments")
    Call<List<Comment>> getMessageComments(@Path("messageId") int messageId);

}
