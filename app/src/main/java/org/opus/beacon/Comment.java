package org.opus.beacon;


public class Comment {
    private String text;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    public void setUser(int user) {
        this.user = user;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setHearts(int hearts) {
        this.hearts = hearts;
    }

    public void setTime(long time) {
        this.time = time;
    }

    private int user;
    private int hearts;
    private long time;
    private boolean hearted;


    public boolean getHearted() { return hearted; }
    public void setHearted(boolean hearted) { this.hearted = hearted;}
    public void editComment(String newComment) {
        this.text = newComment;
    }

    public String getText() {
        return text;
    }
    public int user() {
        return user;
    }

    public double getHearts() {
        return hearts;
    }

    public long getTime() {
        return time;
    }

    public Comment(String newComment, int newUser, int hearts, long time) {
        this.text = newComment;
        this.user = newUser;
        this.hearts = hearts;
        this.time = time;

    }
}
