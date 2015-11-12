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

    public void setTime(String time) {
        this.time = time;
    }

    private int user;
    private int hearts;
    private String time;


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

    public String getTime() {
        return time;
    }

    public Comment(String newComment, int newUser, int hearts, String time) {
        this.text = newComment;
        this.user = newUser;
        this.hearts = hearts;
        this.time = time;

    }
}
