package org.opus.beacon;

import java.util.ArrayList;


public class Thread {
    private Beacon post;
    private ArrayList<Comment> comments;
    private Thread nextThread;

    public Beacon getPost() {
        return post;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }


    public void addComment (Comment newComment) {
        comments.add(newComment);
    }

    public Thread(Beacon newPost, Thread next) {
        this.post = newPost;
        this.nextThread = next;
        this.comments = new ArrayList<Comment>();
    }

}
