package com.example.twisterpm;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Comment implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("messageId")
    @Expose
    private Integer messageId;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("user")
    @Expose
    private String user;
    private final static long serialVersionUID = 3729107473480134756L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUser() { return user; }

    public void setUser(String user) {
        this.user = user;
    }

    public String toString() { return "User: " + user + ", Comment: " + content + "\n"; }
}
