package org.opus.beacon;

import android.graphics.Bitmap;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Arrays;


public class Thread {

    private int id;
    private int userid;
    private String username;
    private int hearts;
    private String text;

    private String time;
    private double longitude;
    private double latitude;
    private Comment[] comments;
    private boolean hearted;

    @JsonIgnore
    private Bitmap image;

    public Thread() {
    }

    public void addComment(Comment c) {
        if (c.getUserid() == this.getUserid()) {
            c.setOp();
        }

        ArrayList<Comment> commentList = new ArrayList<Comment>(Arrays.asList(comments));
        commentList.add(c);
        comments = (Comment[]) commentList.toArray();
    }

    public void markOp() {
        for(int i = 0; i < comments.length; i++) {
           if (comments[i].getUserid() == this.getUserid()) {
               comments[i].setOp();
           }
        }
    }

    public int getUserid() { return userid; }
    public void setUserid(int userid) { this.userid = userid; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public int getHearts() {
        return hearts;
    }
    public void setHearts(int hearts) {
        this.hearts = hearts;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public Bitmap getImage() {
        return image;
    }
    public void setImage(Bitmap image) { this.image = image; }
    public Comment[] getComments() { return comments; }
    public void setComments(Comment[] comments) { this.comments = comments; }
    public boolean getHearted() { return hearted; }
    public void setHearted(boolean hearted) { this.hearted = hearted;}

    public static class Comment {
        private int id;
        private int userid;
        private int hearts;
        private String text;
        private String time;
        private String username;
        private boolean hearted;

        @JsonIgnore
        private boolean isOp = false;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public int getUserid() { return userid; }
        public void setUserid(int userid) { this.userid = userid; }
        public int getHearts() { return hearts; }
        public void setHearts(int hearts) { this.hearts = hearts; }
        public String getText() {
            return text;
        }
        public void setText(String text) {
            this.text = text;
        }
        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username;}
        public boolean getHearted() { return hearted; }
        public void setHearted(boolean hearted) { this.hearted = hearted;}

        public void setOp() { this.isOp = true; }
        public boolean isOp() { return this.isOp; }
    }
}
