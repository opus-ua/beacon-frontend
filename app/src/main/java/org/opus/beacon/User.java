package org.opus.beacon;

/**
 * Created by connorhamblett on 11/5/15.
 */
public class User {
    public User(String username, int userID) {
        this.username = username;
        this.userID = userID;
    }

    private String username;
    private int userID;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public int getUserID() {
        return userID;
    }
    public void setUserID(int userID) { this.userID = userID; }
}
