package com.example.connorhamblett.opustemplate;

/**
 * Created by connorhamblett on 9/13/15.
 */
public class Comment {
    private String comment;
    private String user;


    public void editComment(String newComment)
    {
        this.comment = newComment;
    }

    public Comment(String newComment, String newUser)
    {
        this.comment = newComment;
        this.user = newUser;
    }
}
