package org.opus.beacon;

import java.util.ArrayList;


/**
 * Created by connorhamblett on 9/13/15.
 */
public class Thread {
    private Beacon post;
    private ArrayList<Comment> comments;
    private Thread nextThread;

    public void addComment (Comment newComment)
    {
        comments.add(newComment);
    }

    public Thread(Beacon newPost, Thread next)
    {
        this.post = newPost;
        this.nextThread = next;
        this.comments = new ArrayList<Comment>();
    }

}
