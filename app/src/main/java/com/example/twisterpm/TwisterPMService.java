package com.example.twisterpm;

import com.example.twisterpm.model.Comment;
import com.example.twisterpm.model.Message;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TwisterPMService {
    //public final String apiUrl = "https://anbo-restmessages.azurewebsites.net/api/";

    @GET("messages")
    Call<List<Message>> getMessages();

    @GET("messages")
    Call<List<Message>> getMessagesByUser(@Query("user") String user);

    @GET("messages/{messageId}/comments")
    Call<List<Comment>> getMessageComments(@Path("messageId") int messageId);

    @GET("messages/{messageId}/comments")
    Call<List<Comment>> getCommentsByUser(@Path("messageId") int messageId,@Query("user") String user);

    @POST("messages")
    Call<Message> postMessage(@Body Message message);

    @POST("messages/{messageId}/comments")
    Call<Comment> postComment(@Path("messageId") int messageId, @Body Comment comment);

    @DELETE("messages/{messageId}")
    Call<Message> deleteMessage(@Path("messageId") int id);

    @DELETE("messages/{messageId}/comments/{commentsId}")
    Call<Comment> deleteComment(@Path("messageId") int messageId, @Path("commentsId") int commentsId);
}
